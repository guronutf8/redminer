package info.guron.redminer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by guron on 20.03.14.
 */
public class TaskListRefreshAsync extends AsyncTask<String,Integer, String>{
    final String LOG_TAG = "TaskListRefreshAsync";
    private PullToRefreshListView lvTask;
    private Context context;

    public TaskListRefreshAsync(PullToRefreshListView lvTask, Context context) {
        this.lvTask = lvTask;
        this.context = context;
    }

    @Override
    protected String doInBackground(String[] strings) {
        Log.d(LOG_TAG, "doInBackground");
        context.startService(new Intent(context,RedmineService.class).putExtra("action", RedmineService.ACTION_RESTART_SHEDULER)); // перезапуск щедулера
//        try {
//            Thread.sleep(3000);
//        }catch (java.lang.InterruptedException e){e.printStackTrace();}
        return null;
    }

    @Override
    protected void onPostExecute(String o) {
        Log.d(LOG_TAG, "onPostExecute");
        //lvTask.onRefreshComplete();
        super.onPostExecute("TaskListRefreshAsync -> onPostExecute");
    }
}
