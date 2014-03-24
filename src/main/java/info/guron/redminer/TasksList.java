package info.guron.redminer;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

//import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by Guron on 23.09.13.
 */
public class TasksList extends Activity   {
    final String LOG_TAG = "TasksListActivity";
    final String DEBUG_TAG = "TasksListActivity";

    Intent intentServiceRedmineGetTasks;
    private GestureDetectorCompat mDetector;

    BroadcastReceiver brTasks; //получатор таксков
    PullToRefreshListView lvTask;
    /**
     * <p>При создании activity</p>
     * <p>- отправляется пендинг за списком задач. Ответ слушается в onActivityResult</p>
     * <p>- создается BroadcastReceiver. Он ловит широковещятельные сообщения от сервиса. И устанавливает OnClick</p>
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate");




        //Создание пендинга, который спрашивает у сервера задачи.
        PendingIntent pi;
        intentServiceRedmineGetTasks = new Intent(this,info.guron.redminer.RedmineService.class).putExtra("action", RedmineService.ACTION_GET_TASKS);
        pi  = createPendingResult(RedmineService.ACTION_GET_TASKS,new Intent(),0);
        intentServiceRedmineGetTasks.putExtra("pi", pi);
        startService(intentServiceRedmineGetTasks);




        //Ловит сообщения по своему фильтру, а конеретно когда система одновляет список задач с сервера, и почемуто я намудрил что только в этом случает устанавливает кликание....
        brTasks = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(LOG_TAG, "onCreate -> onReceive");
                TasksListManager.fillingTaskList(TasksList.this,lvTask,intent);
            }
        };
        IntentFilter intFilt = new IntentFilter(RedmineService.BROADCAST_ACTION_BR_TOLD_TASKS);
        registerReceiver(brTasks, intFilt);





        setContentView(R.layout.tasks);

        //событие рефрешь листа
        lvTask = (PullToRefreshListView)findViewById(R.id.lvTask);
        lvTask.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                Log.d(LOG_TAG, "setOnRefreshListener - > onRefresh");
                new TaskListRefreshAsync(lvTask, getApplicationContext()).execute();
            }
        });
    }









    @Override
    public void onStart(){
        Log.d(LOG_TAG, "onStart");
        // Законементировал, больно уж часто выполняется, в дебаге не надо
        //startService(intentServiceRedmineGetTasks);
        super.onResume();



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(LOG_TAG,"onActivityResult requestCode: "+requestCode+" resultCode :"+resultCode);
        if(resultCode==RedmineService.RETURN_TASKS){
//            ProgressBar pbGetTasks = (ProgressBar)findViewById(R.id.pbGetTasks);
//            pbGetTasks.setVisibility(View.GONE);
//
//            String[] topics =  data.getBundleExtra("Task").getStringArray("Topic");
//            ArrayAdapter adapter = new ArrayAdapter(this,R.layout.simple_list_item_1,topics);
//            ListView lvTask = (ListView)findViewById(R.id.lvTask);
//            lvTask.setAdapter(adapter);

        }else if(resultCode==RedmineService.RETURN_NOT_TASKS) {
            Log.d(LOG_TAG, "Tasks empty");
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setting, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, info.guron.redminer.Setting.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(brTasks);
    }
}