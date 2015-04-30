package biln.notreappeventful3.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.Normalizer;
import java.util.List;

import biln.notreappeventful3.R;

/**
 * Created by Boris on 2015-03-14.
 */

public class ConfigurationActivity extends Activity implements LocationListener, View.OnClickListener{

    TextView texte_firstlauchn_1st ;
    TextView texte_intro ;
    ImageView localisation ;
    Button bouton_validation ;
    EditText editText_localisation ;
    SharedPreferences settings;

    Geocoder geocoder;
    LocationManager locationManager;
    String cityByLocalisation;
    boolean aCityWasFound;

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

            aCityWasFound = false;
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if( locationManager!=null ) Log.d("Geolocalisation", "Manager ok!");

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            geocoder = new Geocoder(this);
            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(lastLocation!=null ) {
                Log.d("Geolocalisation", "Last location ok");
                onLocationChanged(lastLocation);
            }else{
                Log.d("Geolocalisation", "Last location NOT ok");
            }

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("Geolocalisation", "Location is "+location.getLatitude() + " " + location.getLongitude());
        cityByLocalisation = String.format("Latitude: %f\nLongitude:%f\nBearing:  \t%f\n",location.getLatitude(),location.getLongitude(),location.getBearing());

        // geocoder
        try{
            List<Address> ads = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 5); //C'était 10 dans l'exemple de Rania

            if(ads.size() > 1) {
                cityByLocalisation = "" + ads.get(0).getLocality(); //Important
                if (!cityByLocalisation.equals("") && !cityByLocalisation.isEmpty() && cityByLocalisation != null) {
                    aCityWasFound = true;
                    Log.d("cityByLocalisation ", "" + cityByLocalisation);
                    try {
                        Thread.sleep(1000);
                    } catch(InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                    cityByLocalisation = Normalizer.normalize(cityByLocalisation, Normalizer.Form.NFD);
                    cityByLocalisation = cityByLocalisation.replaceAll("[^\\p{ASCII}]", "");
                    Toast.makeText(this, "Nous avons détecté " + cityByLocalisation + ". Est-ce exact?", Toast.LENGTH_LONG).show();
                    editText_localisation.setHint(cityByLocalisation);
                }
            }
        }catch(IOException e) {
            cityByLocalisation+="Pas d'adresse disponible.";
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        if( locationManager!=null ) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10.0f, this);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }
    @Override
    public void onProviderDisabled(String provider) {
    }
    @Override
    public void onProviderEnabled(String provider) {
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }


    public void onClick(View view) {
        String myCity = editText_localisation.getText().toString();
        //Si l'usager n'a rien rentré et que la géolocalisation n'a pas trouvé de ville
        if(myCity.matches("") && !aCityWasFound){
            Toast.makeText(getApplicationContext(), "Entrez une ville", Toast.LENGTH_SHORT).show();
        //Si l'usager n'a rien rentré mais que la géolocalisation a bien trouvé une ville
        }else if(myCity.matches("") && aCityWasFound){
            settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor edit = settings.edit();
            edit.putString("myCity", cityByLocalisation);
            edit.putBoolean("First call in app run", true);
            edit.commit();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        //Si l'usager a rentré quelque chose
        }else{
            myCity = Normalizer.normalize(myCity, Normalizer.Form.NFD);
            myCity = myCity.replaceAll("[^\\p{ASCII}]", "");
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