package info.guron.redminer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import info.guron.socket_redmine.Task;

/**
 * Created by guron on 18.12.13.
 * Содерджит методы для управления такслистом.
 */
abstract class TasksListManager {
    static final String LOG_TAG = "TasksListActivity";

    static void fillingTaskList(final android.content.Context context, PullToRefreshListView lvTask, Intent broadcastIntent){
        Bundle bTasks = broadcastIntent.getBundleExtra(RedmineService.BR_TOLD_TASKS);
        final ArrayList<Task> tasks;
        try {
            tasks = (ArrayList)bTasks.getSerializable("tasks");
        } catch (NullPointerException e) {
            e.printStackTrace();
            return;
        }
        TasksAdapter adapter = new TasksAdapter(context ,R.layout.simple_list_item_3,tasks){};
        lvTask.setAdapter(adapter);

        ProgressBar pbGetTasks = (ProgressBar)  ((TasksList)context).findViewById(R.id.pbGetTasks);
        pbGetTasks.setVisibility(View.GONE);

        lvTask.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(LOG_TAG, "Click-> " + i + " topic: " + tasks.get(i-1).getTopic());
                context.startActivity(new Intent(context,IssueActivity.class)
                        .putExtra("id_issue",((Task)tasks.get(i-1)).getId())
                        .putExtra("topic_issue", ((Task)tasks.get(i-1)).getTopic()));
            }
        });


        // убераем статус, т.е. приводим окно к стандартному виду
        lvTask.onRefreshComplete();
    }


}
