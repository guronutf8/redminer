package info.guron.redminer;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import java.util.logging.Logger;


public class LoginActivity extends Activity {
    final String LOG_TAG = "RedminerLoginActivity";
    private static String login = "";
    private static String password = "";
    private static String url = "";
    private static String login_status = "";

    TextView tvLogin;
    TextView tvPassword;
    TextView tvUrl;


    SharedPreferences setting;

    RedmineServiceConnection sConn;
    Intent intentServiceRedmine;
    Intent intentService;
    boolean mBound = false;

    @Override
    /*
    * ger from shared preference to form*/
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate");
        setContentView(R.layout.login);

        setting = getSharedPreferences(getString(R.string.preference_key_file), Context.MODE_PRIVATE);
        login = setting.getString("login",null);
        password = setting.getString("password",null);
        url = setting.getString("url",null);

        tvLogin = (TextView)findViewById(R.id.login);
        tvPassword = (TextView)findViewById(R.id.password);
        tvUrl = (TextView)findViewById(R.id.textURL);

        login = tvLogin.getText().toString();
        password = tvPassword.getText().toString();
        url = tvUrl.getText().toString();

        tvLogin.setText(login != null ? login : "");
        tvPassword.setText(password != null ? password : "");
        tvUrl.setText(url != null ? url : "");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void clickLogin(View view){
        Log.d(LOG_TAG, "onClick -> clickLogin");
        PendingIntent pi;

        LinearLayout layout = (LinearLayout) findViewById(R.id.login_layout);
        layout.setVisibility(View.GONE);

        ProgressBar progress = (ProgressBar)findViewById(R.id.login_progress);
        progress.setVisibility(View.VISIBLE);

        intentServiceRedmine = new Intent(this,info.guron.redminer.RedmineService.class).putExtra("action", RedmineService.ACTION_LOGINUP);
        intentServiceRedmine.putExtra("login", tvLogin.getText().toString());
        intentServiceRedmine.putExtra("password", tvPassword.getText().toString());
        intentServiceRedmine.putExtra("url", tvUrl.getText().toString());

        pi  = createPendingResult(RedmineService.ACTION_LOGINUP,new Intent(),0);
        intentServiceRedmine.putExtra("pi",pi);

        startService(intentServiceRedmine);
        Log.d(LOG_TAG, "clickLogin -> clickLogin -> RedmineService.ACTION_LOGINUP");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(LOG_TAG, "onActivityResult requestCode :"+requestCode+" resultCode :"+resultCode);

        if(requestCode==RedmineService.ACTION_LOGINUP){ //login in
            if(resultCode == RedmineService.RETURN_LOGINUP ){ //cool!
                Log.d(LOG_TAG, "onActivityResult -> ACTION_LOGINUP -> RETURN_LOGINUP");
                Toast.makeText(getApplicationContext(),R.string.toast_login_in_true,Toast.LENGTH_SHORT).show();
                startService(new Intent(this,RedmineService.class).putExtra("action", RedmineService.ACTION_RESTART_SHEDULER)); // перезапуск щедулера
                startActivity(new Intent(this,TasksList.class));
            }else{
                Log.d(LOG_TAG, "onActivityResult -> ACTION_LOGINUP -> RETURN_ERROR_LOGINUP");
                Toast.makeText(getApplicationContext(),R.string.toast_login_in_false,Toast.LENGTH_SHORT).show();
            }

            ProgressBar progress = (ProgressBar)findViewById(R.id.login_progress);
            progress.setVisibility(View.GONE);
            LinearLayout layout = (LinearLayout) findViewById(R.id.login_layout);
            layout.setVisibility(View.VISIBLE);
        }

    }

}