package info.guron.redminer;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import info.guron.redminer.Asynk.*;
import info.guron.socket_redmine.ControllerRedmine;
import info.guron.socket_redmine.Settings;
import info.guron.socket_redmine.Task;


/**
 * Created by Guron on 11.10.13.
 */
public class RedmineService extends Service {
    final String LOG_TAG = "RedmineService";

    public static final int ACTION_LOGINUP = 1;
    public static final int ACTION_GET_TASKS = 2;
    public static final int ACTION_SENT_TIME = 3;
    public static final int ACTION_RESTART_SHEDULER = 4;


    public static final int RETURN_LOGINUP = 1;
    public static final int RETURN_ERROR_LOGINUP = 0;

    public static final int RETURN_TASKS = 1;
    public static final int RETURN_NOT_TASKS = 0;

    public static final String BR_TOLD_TASKS = "BR_TOLD_TASKS";
    public final static String BROADCAST_ACTION_BR_TOLD_TASKS = "info.guron.redminer.task.action";

    private Handler handlerService = new Handler();

    PendingIntent pi;

    ControllerRedmine  redmine;
    Settings settings;
    SharedPreferences setting;
    Calendar dateLastLogin; //Дата и время последнего логина
    boolean sessionLogin = false; //т.е. было ли удачное содинение
    Calendar dateLastGetTasks; //Дата и время обновления тасков
    DBHelper dbHelper;

    static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    ExecutorService esLogin = Executors.newFixedThreadPool(1); //сервис котороый следит чтобы не получилось так что прога одновременно 2 раза логнится.
    ExecutorService esSendOperation = Executors.newFixedThreadPool(1); //сервис котороый следит чтобы не получилось так что прога одновременно 2 раза пытается провернуть операцию на сервере.

    int countTaskUpdate = 0;

    public static final int CONNECTION_TIOMEOUT = 30*60;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void restart_sheduler(){
        int refresh_time = setting.getInt("refresh_time", 30);
        scheduler.scheduleWithFixedDelay(new UpdateTasksTask(),3,refresh_time*60, java.util.concurrent.TimeUnit.SECONDS);
    }

    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreare");

        redmine = new ControllerRedmine();
        setting = getSharedPreferences(getString(R.string.preference_key_file), Context.MODE_PRIVATE);
        dbHelper = new DBHelper(this);

        restart_sheduler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags,int startId) {
        Log.d(LOG_TAG, "onStartCommand");
        //Смотрим что от нас хотят, и выполняем)
        int actionComannd = intent.getExtras().getInt("action");
        switch (actionComannd){
            case ACTION_LOGINUP:
                Log.d(LOG_TAG, "onStartCommand -> ACTION_LOGINUP");
                Log.d(LOG_TAG, intent.getStringExtra("login")+" "+intent.getStringExtra("password")+" "+intent.getStringExtra("url"));
                pi = intent.getParcelableExtra("pi");
                settings = new Settings(intent.getStringExtra("url"), intent.getStringExtra("login"), intent.getStringExtra("password"),RedmineService.CONNECTION_TIOMEOUT);
                login();
                break;
            case ACTION_GET_TASKS:
                Log.d(LOG_TAG, "onStartCommand -> ACTION_GET_TASKS");
                pi = intent.getParcelableExtra("pi");
                getTasks();
                break;
            case ACTION_SENT_TIME:
                Log.d(LOG_TAG, "onStartCommand -> ACTION_SENT_TIME");
                sentTimeShort(intent.getIntExtra("id", 0), intent.getFloatExtra("time", 0),intent.getStringExtra("topic_issue"));
                //pi = intent.getParcelableExtra("pi");
                break;
            case ACTION_RESTART_SHEDULER:
                Log.d(LOG_TAG, "onStartCommand -> ACTION_RESTART_SHEDULER");
                restart_sheduler();
                break;
            default:
                Log.d(LOG_TAG, "onStartCommand -> empty command");
                break;
        };
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        super.onDestroy();
    }

    //метод логина, который вытягивает Setting и сервиса, по нему подключается и естли получается сообщяется что все хорошо(RETURN_LOGINUP)
    private void login(){
        Log.d(LOG_TAG, "login()");
        new Thread(new Runnable(){
            public void run(){
                try{
                    Future<Boolean> fLogin = esLogin.submit(new LoginTask());
                    sessionLogin = fLogin.get();
                    Log.d(LOG_TAG, "login() return " +String.valueOf(sessionLogin));
                }catch (InterruptedException e){e.printStackTrace();}
                catch (ExecutionException e){e.printStackTrace();}
            }
        }).start();
    }

    //медод получения задач с внутненей базы, просто отдаем список задач те что есть в базе=)
    private void getTasks(){
        class GetTasksTask extends AsyncTask<Integer,Integer,Integer>{
            protected Integer doInBackground(Integer... params){
                Log.d(LOG_TAG,"creating GetTasksTask");
                Bundle bTasks = getTasksBoundle();
                if(bTasks!=null){
                    Intent intent = new Intent(RedmineService.BROADCAST_ACTION_BR_TOLD_TASKS).putExtra(RedmineService.BR_TOLD_TASKS,getTasksBoundle());
                    sendBroadcast(intent);
                    //try{
                        //pi.send(RedmineService.this, RedmineService.RETURN_TASKS, new Intent("TaskVASHEPOHUIROLINEIGRAET").putExtra("Task",bTasks)); // возваращает задачи =)
                    //}catch (PendingIntent.CanceledException e){e.printStackTrace();}
                } else{
                    Log.d(LOG_TAG, "0 rows");
                    Intent intent = new Intent(RedmineService.BROADCAST_ACTION_BR_TOLD_TASKS).putExtra(RedmineService.BR_TOLD_TASKS,getTasksBoundle());
                    sendBroadcast(intent);
//                    try{
//                        pi.send(RedmineService.this, RedmineService.RETURN_NOT_TASKS,new Intent()); //нет задач
//                    }catch (PendingIntent.CanceledException e){e.printStackTrace();}
                }



                return 1;// да просто так, т.к. нам не нужен ретурн
            };
        }
        GetTasksTask task = new GetTasksTask();
        task.execute();
    }
    /*
    Отдайтет интент "Task", с связкой в которой StringArray "Topic" c со списком топиков задач
    Отдайтет интент связку в которой StringArray "Topic" списк топиков задач
     */
    private Bundle getTasksBoundle(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Bundle bTasks = new Bundle();
        Intent iTasks;

        LinkedList tasks = new LinkedList();



        Log.d(LOG_TAG, "--- Rows in tasks: ---");
        // делаем запрос всех данных из таблицы mytable, получаем Cursor
        Cursor c = db.query("tasks", null, null, null, null, null, null);

        if (c.moveToFirst()) {
            ArrayList<String> taskTopics = new ArrayList<String>();
            ArrayList<Integer> taskIds = new ArrayList<Integer>();
            int idColIndex = c.getColumnIndex("id");
            int topicColIndex = c.getColumnIndex("topic");
            int projectColIndex = c.getColumnIndex("project");

            do {
                Log.d(LOG_TAG,
                        "ID = " + c.getInt(idColIndex) +
                                ", topic = " + c.getString(topicColIndex));
                taskTopics.add(c.getString(topicColIndex));
                taskIds.add(c.getInt(idColIndex));
                tasks.add(new Task(c.getInt(idColIndex),c.getString(topicColIndex),c.getString(projectColIndex)));
            } while (c.moveToNext());

            bTasks.putSerializable("tasks",tasks);

            c.close();
            return bTasks;

        }else return null;

    }

    /*
    1.Выкачивается список задач, и сохраняется в БД
    2.Отпраляется intent в broadcast со связкой задач
     */
    class UpdateTasksTask implements Runnable{
        final String LOG_TAG = "RedmineService -> UpdateTasksTask";
        public void run(){
            Log.d(LOG_TAG,"creating UpdateTasksTask");

            try{
                Log.d(LOG_TAG,"logining.");
                if(!sessionLogin)
                    sessionLogin = redmine.login(new Settings(setting.getString("url",null),
                            setting.getString("login",null),
                            setting.getString("password",null),
                            RedmineService.CONNECTION_TIOMEOUT));
                //System.out.println("xxxxxxxxxxxxx");
            }catch (IOException e){
                Log.d(LOG_TAG,"Logining exception.");
                e.printStackTrace();
                return;
            }



            List<Task> tasks = redmine.getTasks();
            if (tasks == null) {
                Log.d(LOG_TAG, "redmine.getTasks() returned null");
                return;
            }


            Log.d(LOG_TAG,"Geted "+tasks.size()+" tasks.");

            ContentValues cv = new ContentValues();
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            Log.d(LOG_TAG, "--- Clear tasks table: ---");
            int clearCount = db.delete("tasks", null, null);
            Log.d(LOG_TAG, "deleted rows count = " + clearCount);

            for(Task task:tasks){
                cv.put("topic",task.getTopic());
                cv.put("id",task.getId());
                cv.put("project", task.getProject());
                long rowID = db.insert("tasks", null, cv);
                Log.d(LOG_TAG, "row inserted, ID = " + rowID + ", "+cv.getAsString("topic")+ ", "+cv.getAsString("project"));
            }

            //cv.put("topic","Count update: "+String.valueOf(countTaskUpdate));
            //long rowID = db.insert("tasks", null, cv);
            //Log.d(LOG_TAG, "row inserted, ID = " + String.valueOf(countTaskUpdate) + ", "+cv.getAsString("topic"));


            Log.d(LOG_TAG, "complite");

//            чисто потестить
            Intent intent = new Intent(RedmineService.BROADCAST_ACTION_BR_TOLD_TASKS).putExtra(RedmineService.BR_TOLD_TASKS,getTasksBoundle());
            sendBroadcast(intent);

//            чисто потестить
        }
    }

    /**
     * Запускает асинхроное отправление затарченого времени
     * @param id
     * @param time
     */

    private void sentTimeShort(int id, Float time, String topic) {
        SendTimeTask task = new SendTimeTask(id, time, topic,sessionLogin, handlerService, setting, redmine, this);
        esSendOperation.submit(task);
    }
//    /**
//     * ОТправляет на сервер редмайна затраченое время в асинхроном режиме
//     * пока не позвращяет резельтата, т.е. мы не знает проставилось ли оно или случилоть говно
//     */
//    class SendTimeTask implements Runnable {
//        final String LOG_TAG = "RedmineService -> SentTimeTask";
//        private int id;
//        private Float time;
//        private String topic;
//
//        public SendTimeTask(int id, Float time, String topic){
//            this.id = id;
//            this.time = time;
//            this.topic = topic;
//        }
//
//        public void run(){
//            Log.d(LOG_TAG,"Run SendTimeTask");
//            // стандартная хрень по логину
//            try{
//                Log.d(LOG_TAG,"logining.");
//                if(!sessionLogin)
//                    sessionLogin = redmine.login(new Settings(setting.getString("url",null),
//                            setting.getString("login",null),
//                            setting.getString("password",null),
//                            RedmineService.CONNECTION_TIOMEOUT));
//            }catch (IOException e){
//                e.printStackTrace();
//                Log.d(LOG_TAG, "logining failure.");
//                Runnable notifyErrorSendTime = new UtilNotify(getApplicationContext(), id, topic, time);
//                handlerService.post(notifyErrorSendTime);
//                return;
//            }
//            Log.d(LOG_TAG,"Logining successfully.");
//            // конец стандартной хрени по логину
//
//            Log.d(LOG_TAG,"SendTimeTask -> SendTime.");
//            try{
//                redmine.sendJobTimeShort(id,time);
//            }catch (IOException e){
//                e.printStackTrace();
//                Log.d(LOG_TAG, "SendTimeTask -> SendTime failure.");
//                Runnable notifyErrorSendTime = new UtilNotify(getApplicationContext(), id, topic, time);
//                handlerService.post(notifyErrorSendTime);
//                return;
//            }
//        }
//    }





    // вызвращает резульата логина
    class LoginTask implements Callable<Boolean>{
        public Boolean call(){
            try{
                sessionLogin = redmine.login(settings);
                if (sessionLogin) {
                    setting.edit().putInt("login_status", MainActivity.sharedLoginStatus_itLogin)
                            .putString("url", settings.getURLRedmine())
                            .putString("login", settings.getUsername())
                            .putString("password", settings.getPassword())
                            .commit();

                    dateLastLogin = new GregorianCalendar();
                    pi.send(RETURN_LOGINUP);
                } else {
                    setting.edit().putInt("login_status", MainActivity.sharedLoginStatus_notLogin);
                    pi.send(RETURN_ERROR_LOGINUP);// по сути ретурн тут
                }
            }catch (PendingIntent.CanceledException e){e.printStackTrace();}
            catch (IOException e){e.printStackTrace();}
            return sessionLogin;
        }

    }
}
