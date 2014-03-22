package info.guron.redminer;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import info.guron.socket_redmine.Task;

/**
 * Created by guron on 28.02.14.
 */
public class TasksAdapter<T> extends ArrayAdapter {
    private List<T> objects;

    private int mResource;
    private LayoutInflater mInflater;
    private final Context context;
    public  TasksAdapter(android.content.Context context, int textViewResourceId, java.util.List<T> objects){
        super(context, textViewResourceId,objects);
        this.context = context;
        this.objects = objects;
        this.mResource = textViewResourceId;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return createViewFromResource(position, convertView, parent, mResource);
    }
    private View createViewFromResource(int position, View convertView, ViewGroup parent,
                                        int resource) {
        View view;
        TextView fistText;
        TextView secondText;
        if (convertView == null) {
            view = mInflater.inflate(resource, parent, false);
        } else {
            view = convertView;
        }

        try {
                fistText = (TextView) view.findViewById(R.id.textView);
                secondText = (TextView) view.findViewById(R.id.textView2);
        } catch (ClassCastException e) {
            Log.e("ArrayAdapter", "You must supply a resource ID for a TextView");
            throw new IllegalStateException(
                    "ArrayAdapter requires the resource ID to be a TextView", e);
        }
        Object item = getItem(position);
        if(item instanceof Task) {
            fistText.setText(((Task) item).getTopic());
            secondText.setText(((Task) item).getProject());
        }else {
            fistText.setText(item.toString());
        }

        return view;
    }
}
