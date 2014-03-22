package info.guron.redminer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * Created by guron on 02.03.14.
 */
public class setting_common extends Activity {
    SharedPreferences setting;
    int refresh_time;
    EditText edRefreshTime;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_common);

        setting = getSharedPreferences(getString(R.string.preference_key_file), Context.MODE_PRIVATE);
        refresh_time = setting.getInt("refresh_time", 30);

        edRefreshTime = (EditText)findViewById(R.id.setting_refresh_time_ed);
        edRefreshTime.setText(String.valueOf(refresh_time));
    }

    public void onClickSave(View view) {
        setting.edit().putInt("refresh_time", Integer.parseInt(edRefreshTime.getText().toString())).commit();
        startService(new Intent(this,RedmineService.class).putExtra("action", RedmineService.ACTION_RESTART_SHEDULER)); // перезапуск щедулера
        onBackPressed();
    }
}