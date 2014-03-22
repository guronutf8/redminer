package info.guron.redminer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by guron on 27.10.13.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static String LOG_TAG = "DBHelper";
    public DBHelper(Context context) {
        // конструктор суперкласса
        super(context, "myDB", null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "--- onCreate database ---");
        // создаем таблицу с полями
        db.execSQL("create table tasks ("
                + "id integer primary key,"
                + "topic text,"
                + "project text"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(LOG_TAG, "--- onUpgrade database ---");
        // Logs that the database is being upgraded
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");

        // Kills the table and existing data
        db.execSQL("DROP TABLE IF EXISTS tasks");

        // Recreates the database with a new version
        onCreate(db);
    }
}

