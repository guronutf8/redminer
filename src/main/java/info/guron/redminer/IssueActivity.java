package info.guron.redminer;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import android.os.Handler;

import info.guron.redminer.util.UtilNotify;

/**
 * Created by guron on 25.11.13.
 */
public class IssueActivity extends Activity {
    final String LOG_TAG = "IssueActivity";

    String topic; //тема задачи, заполняется сразу после создания активити
    Integer id; //id задачи, заполняется сразу после создания активити
    Float time; // приходит из notify
    NumberPicker npHours, npMinutes;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //заполняются основные переменые для класса.
        id = getIntent().getIntExtra("id_issue", 0);
        topic = getIntent().getStringExtra("topic_issue");
        time = getIntent().getFloatExtra("time", 1.0f);

        setContentView(R.layout.issue);

        //выводит на актитити Тему задачи.
        TextView tvIssue  = (TextView)findViewById(R.id.tvIssue);
        tvIssue.setText(String.valueOf(id)+": "+topic);

        //заполняются numberPicker
        npHours  = (NumberPicker)findViewById(R.id.npHours);
        npHours.setMinValue(0);
        npHours.setMaxValue(24);
        npHours.setValue(time.intValue());

        npMinutes  = (NumberPicker)findViewById(R.id.npMinutes);
        String[] listMinutes = {"0", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60"};
        npMinutes.setDisplayedValues(listMinutes);
        npMinutes.setMinValue(0);
        npMinutes.setMaxValue(listMinutes.length - 1);
        npMinutes.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i2) {
                int time = i2 * 5 / 60;
            }
        });
        int minutes = Math.round(new Float((time - time.intValue()) / 5 * 6) * 10);
        npMinutes.setValue(minutes);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(UtilNotify.ERROR_SEND_TIME,id);

    }

    //событие нанажиние кнопки "отправить время"
    public void clickSendTime(View view) {
        Log.d(LOG_TAG, "clickSendTime");

        Float minutes = Float.parseFloat(Integer.toString(npMinutes.getValue()));
        Float hours = Float.parseFloat(Integer.toString(npHours.getValue()));
        minutes = new Float(minutes * 5 / 60);
        Float time = hours + minutes;
        //Создание пендинга, который отправлет сервису задачу что бы он отправил время на сервер.
        Intent intentServiceRedmine = new Intent(this, info.guron.redminer.RedmineService.class)
                .putExtra("action", RedmineService.ACTION_SENT_TIME)
                .putExtra("time", time)
                .putExtra("id", id)
                .putExtra("topic_issue", topic);
        PendingIntent pi;
        pi = createPendingResult(RedmineService.ACTION_SENT_TIME, new Intent(), 0);
        intentServiceRedmine.putExtra("pi", pi);
        startService(intentServiceRedmine);

        onBackPressed();
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

}