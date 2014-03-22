package info.guron.redminer;



import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

import java.util.Calendar;

public class MainActivity extends Activity {
    final String LOG_TAG = "RedminerMainActivity";
    private static String login = "";
    private static String password = "";
    private static String url = "";
    private static int login_status;

    public final static int sharedLoginStatus_notLogin = 0;
    public final static int sharedLoginStatus_itLogin = 1;

    SharedPreferences setting;
    RedmineServiceConnection sConn;
    Intent intentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate");
        setting = getSharedPreferences(getString(R.string.preference_key_file), Context.MODE_PRIVATE);
        login = setting.getString("login",null);
        password = setting.getString("password",null);
        url = setting.getString("url",null);
        login_status = setting.getInt("login_status", sharedLoginStatus_notLogin);

        //check setting,
        if(login_status==sharedLoginStatus_notLogin){
            Log.d(LOG_TAG, "onCreate->login_status = sharedLoginStatus_notLogin");
            intentActivity = new Intent(this,LoginActivity.class);
            startActivity(intentActivity);
        }
        else {
            Log.d(LOG_TAG, "onCreate->login_status = sharedLoginStatus_itLogin");
            startActivity(new Intent(this,TasksList.class));
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
