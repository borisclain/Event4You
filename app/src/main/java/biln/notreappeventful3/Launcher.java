package biln.notreappeventful3;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

/**
 * Created by Boris on 2015-03-18.
 */
public class Launcher extends Activity{

    public static SharedPreferences settings;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);

        if(hasLoggedIn)
        {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }
        else
        {
            SharedPreferences.Editor edit = Launcher.settings.edit();
            edit.putBoolean("hasLoggedIn", Boolean.TRUE);
            edit.commit();
            Intent i = new Intent(this, ConfigurationActivity.class);
            startActivity(i);
            finish();
        }

    }


}


