package info.guron.redminer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;


public class Setting extends Activity {
    final String LOG_TAG = "SettingActivity";
    SharedPreferences setting;

    public final static int sharedLoginStatus_notLogin = 0;
    public final static int sharedLoginStatus_itLogin = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

//        setting = getSharedPreferences(getString(R.string.preference_key_file), Context.MODE_PRIVATE);
//        String login = setting.getString("login",null);
//        String password = setting.getString("password",null);
//        String url = setting.getString("url",null);
//        int login_status = setting.getInt("login_status", sharedLoginStatus_notLogin);
//
//        TextView tvLogin = (TextView) findViewById(R.id.tvLogin);
//        TextView tvPassword = (TextView) findViewById(R.id.tvPassword);
//        TextView tvUrl = (TextView) findViewById(R.id.tvUrl);
//        TextView tvStatus = (TextView) findViewById(R.id.tvStatus);
//        tvLogin.setText(login);
//        tvPassword.setText(password);
//        tvUrl.setText(url);
//        tvStatus.setText(login_status);


    }

    public void onClick_common(View view){
        System.out.println("onClick_common");
        startActivity(new Intent(this, setting_common.class));
    }
    public void onClick_account(View view){
        System.out.println("onClick_account");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setting, menu);
        return true;
    }

    public void onClickClearSettings(View view){
        setting = getSharedPreferences(getString(R.string.preference_key_file), Context.MODE_PRIVATE);
        setting.edit().remove("login_status").commit();

    }


}
