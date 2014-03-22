package info.guron.redminer.Asynk;

/**
 * Created by guron on 16.02.14.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import info.guron.redminer.RedmineService;
import info.guron.redminer.Setting;
import info.guron.redminer.util.UtilNotify;
import info.guron.socket_redmine.ControllerRedmine;
import info.guron.socket_redmine.Settings;

/**
 * ОТправляет на сервер редмайна затраченое время в асинхроном режиме
 * пока не позвращяет резельтата, т.е. мы не знает проставилось ли оно или случилоть говно
 */
public class SendTimeTask implements Runnable {
    final String LOG_TAG = "RedmineService -> SentTimeTask";
    private int id;
    private Float time;
    private String topic;
    private boolean sessionLogin;
    private Handler handler;
    SharedPreferences setting;
    private ControllerRedmine redmine;
    private Context context;

    public SendTimeTask(int id, Float time, String topic, boolean sessionLogin, Handler handler, SharedPreferences setting, ControllerRedmine redmine, Context context) {
        this.id = id;
        this.time = time;
        this.topic = topic;
        this.sessionLogin = sessionLogin;
        this.handler = handler;
        this.setting = setting;
        this.redmine = redmine;
        this.context = context;
    }

    public void run(){

        Log.d(LOG_TAG, "Run SendTimeTask");
        // стандартная хрень по логину
        try{
            Log.d(LOG_TAG,"logining.");
            if(!sessionLogin)
                sessionLogin = redmine.login(new Settings(setting.getString("url",null),
                        setting.getString("login",null),
                        setting.getString("password",null),
                        RedmineService.CONNECTION_TIOMEOUT));
        }catch (IOException e){
            e.printStackTrace();
            Log.d(LOG_TAG, "logining failure.");
            Runnable notifyErrorSendTime = new UtilNotify(context.getApplicationContext(), id, topic, time);
            handler.post(notifyErrorSendTime);
            return;
        }
        Log.d(LOG_TAG,"Logining successfully.");
        // конец стандартной хрени по логину

        Log.d(LOG_TAG,"SendTimeTask -> SendTime.");
        try{
            redmine.sendJobTimeShort(id,time);
        }catch (IOException e){
            e.printStackTrace();
            Log.d(LOG_TAG, "SendTimeTask -> SendTime failure.");
            Runnable notifyErrorSendTime = new UtilNotify(context.getApplicationContext(), id, topic, time);
            handler.post(notifyErrorSendTime);
            return;
        }
    }
}
