package biln.notreappeventful3.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import biln.notreappeventful3.R;

/**
 * Created by Boris on 2015-03-14.
 */

public class ConfigurationActivity extends Activity implements View.OnClickListener{

    TextView texte_firstlauchn_1st ;
    TextView texte_intro ;
    ImageView localisation ;
    Button bouton_validation ;
    EditText editText_localisation ;
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_layout);

        texte_firstlauchn_1st = (TextView) findViewById(R.id.text_first_screen1st) ;
        texte_intro = (TextView) findViewById(R.id.texte_intro) ;
        localisation = (ImageView) findViewById(R.id.localisation);
        bouton_validation = (Button)findViewById(R.id.bouton_validation);
        editText_localisation = (EditText)findViewById(R.id.edit_text_localisation) ;
        bouton_validation.setOnClickListener(this);

        String fontPath = "fonts/Raleway-Regular.ttf";
        String fontPath2 = "fonts/Raleway-Bold.ttf";

        Typeface ma_police = Typeface.createFromAsset(getResources().getAssets(), fontPath);
        Typeface ma_police2 = Typeface.createFromAsset(getResources().getAssets(), fontPath2);

        texte_firstlauchn_1st.setTypeface(ma_police);
        texte_intro.setTypeface(ma_police) ;

        bouton_validation.setTypeface(ma_police2);
    }

    public void onClick(View view) {
        String myCity = editText_localisation.getText().toString();
        if(myCity.matches("")){
            Toast.makeText(getApplicationContext(), "Entrez une ville", Toast.LENGTH_SHORT).show();
        }else{
            settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor edit = settings.edit();
            edit.putString("myCity", myCity);
            edit.putBoolean("First call in app run", true);
            edit.commit();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}