package info.guron.socket_redmine;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Guron
 * Date: 04.09.13
 * Time: 19:24
 * To change this template use File | Settings | File Templates.
 */
public class Task implements Serializable{
    @Deprecated
    public Task(int id, String topic) {
        this.id =id;
        this.topic = topic;
        this.project= "Null";
    }

    public Task(int id, String topic, String project) {
        this.id = id;
        this.topic = topic;
        if(project==null)this.project = "Null";
            else this.project = project;
    }

    public int getId() {
        return id;
    }

    public String getTopic() {
        return topic;
    }

    private int id;
    private String topic;



    private String project = null;

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }
}
