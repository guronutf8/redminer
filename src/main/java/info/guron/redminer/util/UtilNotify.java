package info.guron.redminer.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import info.guron.redminer.IssueActivity;
import info.guron.redminer.R;

/**
 * Created by guron on 16.02.14.
 */
public class UtilNotify implements Runnable{
    final static public String ERROR_SEND_TIME = "ERROR_SEND_TIME";

    public UtilNotify(Context context, int id, String topic, Float time) {
        this.context = context;
        this.id = id;
        this.topic = topic;
        this.time = time;
    }

    @Override
    public void run() {
        //Toast.makeText(context, "Server connecting error (=", Toast.LENGTH_LONG).show();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.redmine_clear_big_white)
                .setContentTitle("Redminer")
                .setContentText("Connection error, can not send time.");
        Intent notifyIntentIn = new Intent(context, IssueActivity.class)
                .putExtra("id_issue",id)
                .putExtra("topic_issue",topic)
                .putExtra("time",time);
        PendingIntent notifyIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        notifyIntentIn,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        builder.setContentIntent(notifyIntent);
        Notification notification = builder.build();
        NotificationManager mNotificationManager =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(ERROR_SEND_TIME,id,notification);

    }

    private Context context;
    private int id;
    private String topic;
    private Float time;
}
