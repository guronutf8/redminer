package info.guron.redminer;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.util.logging.Logger;

/**
 * Created by Guron on 13.10.13.
 */
public class RedmineServiceConnection implements ServiceConnection {
    private static Logger log = Logger.getLogger(RedmineServiceConnection.class.getName());

    public void onServiceConnected(ComponentName name, IBinder service){
        log.info("Connected");
    };
    public void onServiceDisconnected(ComponentName name){
        log.info("Disconnected");
    };

}
