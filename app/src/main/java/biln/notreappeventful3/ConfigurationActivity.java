package biln.notreappeventful3;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Button;
import android.widget.EditText;

import android.graphics.Typeface;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Boris on 2015-03-14.
 */

public class ConfigurationActivity extends ActionBarActivity {





    TextView texte_firstlauchn_1st ;
    TextView texte_intro ;
    ImageView localisation ;
    Button bouton_validation ;
    EditText editText_localisation ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_layout);


        texte_firstlauchn_1st = (TextView) findViewById(R.id.text_first_screen1st) ;
        texte_intro = (TextView) findViewById(R.id.texte_intro) ;
        localisation = (ImageView) findViewById(R.id.localisation);
        bouton_validation = (Button)findViewById(R.id.bouton_validation);
        editText_localisation = (EditText)findViewById(R.id.edit_text_localisation) ;

        // repertoire de la police
        String fontPath = "assets/fonts/Raleway-Regular.ttf";
        String fontPath2 = "assets/fonts/Raleway-Bold.ttf";
        String fontPath3 = "assets/fonts/Raleway-LightItalic.ttf";

        //application de la police
        Typeface ma_police = Typeface.createFromAsset(getAssets(), fontPath);
        Typeface ma_police2 = Typeface.createFromAsset(getAssets(), fontPath2);
        Typeface ma_police3 = Typeface.createFromAsset(getAssets(), fontPath3);

        texte_firstlauchn_1st.setTypeface(ma_police);
        texte_intro.setTypeface(ma_police) ;
        editText_localisation.setTypeface(ma_police3);
        bouton_validation.setTypeface(ma_police2);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}