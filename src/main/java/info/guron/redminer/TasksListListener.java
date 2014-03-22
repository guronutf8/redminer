package info.guron.redminer;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by guron on 25.11.13.
 */
public class TasksListListener implements AdapterView.OnItemClickListener {
    final String LOG_TAG = "TasksListListener";

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        Log.d(LOG_TAG, "Click");
    }
}
