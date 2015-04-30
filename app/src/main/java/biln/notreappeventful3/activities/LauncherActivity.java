package biln.notreappeventful3.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;

import biln.notreappeventful3.utils.DBHelper;

/**
 * Created by Boris on 2015-03-18.
 */
public class LauncherActivity extends Activity{

    SharedPreferences settings;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);
        DBHelper dbh = new DBHelper(this);
        SQLiteDatabase db = dbh.getWritableDatabase();
        dbh.deletePassedEvents(db);
        dbh.close();

        hasLoggedIn = false; // TODO Enlever

        if(hasLoggedIn)
        {
            Intent intent = new Intent(this, MainActivity.class);
            settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor edit = settings.edit();
            edit.putBoolean("First call in app run", true);
            edit.commit();
            startActivity(intent);
            finish();
        }
        else{
            SharedPreferences.Editor edit = settings.edit();
            edit.putBoolean("hasLoggedIn", Boolean.TRUE);
            edit.commit();
            Intent i = new Intent(this, ConfigurationActivity.class);
            startActivity(i);
            finish();
        }
    }
}
