package biln.notreappeventful3;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created by Boris on 2015-03-25.
 */
public class DetailsActivity extends MyMenu implements View.OnClickListener{

    TextView title;
    TextView location;
    TextView startT;
    TextView stopT;
    TextView description;
    TextView categories;
    TextView imageUrl;
    ImageView image;
    Button map;
    CheckBox favoris;
    DBHelper dbh;
    SQLiteDatabase db;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.event_description);

        // Communication avec les éléments de event_descriptions.xml
        title = (TextView) findViewById(R.id.event_title);
        location = (TextView) findViewById(R.id.event_location);
        startT = (TextView) findViewById(R.id.event_datestart);
        stopT = (TextView) findViewById(R.id.event_datestop);
        description = (TextView) findViewById(R.id.event_description);
        categories = (TextView) findViewById(R.id.event_categories);
        image = (ImageView) findViewById(R.id.event_image);
        favoris = (CheckBox) findViewById(R.id.button_favoris);
        map = (Button) findViewById(R.id.button_map);

        // Initialisation des éléments
        Bundle b = getIntent().getExtras();  //favoris
        Boolean fav = false;
        if (b.getInt("favori")==1)
            fav = true;
        favoris.setChecked(fav);
        title.setText(b.getString("title"));
        startT.setText(b.getString("startT"));
        stopT.setText(b.getString("stopT"));
        location.setText(b.getString("address"));

        if (b.getString("description")==null)  // description
            description.setText("Pas de description disponible");
        else {
            Document doc = Jsoup.parse(b.getString("description"));
            description.setText(doc.body().text());
        }

        // Affichage de l'image
        String param = b.getString("eventfulID");
        Log.d("DetailsActivityLena", "Le ID =" + param);
        new MyAsyncTask().execute(param);

        map.setOnClickListener(this);

        // changement statut favori
        String id = b.getString("ID");
        Log.d("favoris","id : "+id);

        dbh = new DBHelper(this); // dbh = new DBHelper(this);
        db = dbh.getWritableDatabase();

        favoris.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                int id = ((Integer) buttonView.getTag()).intValue();
                //Toast.makeText(getApplicationContext(), "Retiré des favoris ", Toast.LENGTH_SHORT).show();
                Log.d("Listener", "Checked " + isChecked + " " + " " + id);
                dbh.changeFavoriteStatus(db, id);//(int)getItemId(pos)
            }
        });

        favoris.setTag(new Integer((Integer.parseInt(id))));

    }



    public void onClick(View v) {
        if (v.getId() == R.id.button_map) {
            Toast.makeText(this, "Accès à la carte.. ", Toast.LENGTH_SHORT).show();
            String text = location.getText().toString().replace(' ', '+');
            Log.d("MAP", text);
            Uri address = Uri.parse("geo:0,0?q=" + text);
            Intent address_i = new Intent(Intent.ACTION_VIEW, address);
            try {
                startActivity(address_i);
            } catch (Exception e) {
                Log.e("MAP", e.getMessage());
            }
        }
    }

    /*
 * Méthode utilitaire qui permet de rapidement
 * charger et obtenir une page web depuis
 * l'internet.
 *
 */
    private HttpEntity getHttp(String url) throws ClientProtocolException, IOException {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet http = new HttpGet(url);
        HttpResponse response = httpClient.execute(http);
        return response.getEntity();
    }

    /*
     * Méthode utilitaire qui permet
     * d'obtenir une image depuis une URL.
     *
     */
    public Drawable loadHttpImage(String url) {
        try {
            InputStream is = getHttp(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src");
            return d;
        }
        catch(ClientProtocolException e) {
            return null;
        }
        catch(IOException e) {
            return null;
        }

    }

    /*protected Cursor doInBackground(String... params) {
        ArrayList event_details = new ArrayList();
        EventfulAPI web = new EventfulAPI();
        event_details = web.getEventDetails("E0-001-081672548-8");
    }*/

    private class MyAsyncTask extends AsyncTask<String, String, ArrayList<String>> {

        Drawable draw;

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            ArrayList<String> event_details = new ArrayList<String>();
            EventfulAPI web = new EventfulAPI();
            String id = params[0];
            event_details = web.getEventDetails(id);

            draw = null;
            if (event_details.size()==2 && !(event_details.get(1)==null)) {
                draw = loadHttpImage(event_details.get(1));
                Log.d("doInBackground"," load img from api");
            }

            return event_details;
        }

        @Override
        protected void onPostExecute(ArrayList<String> list_details) {
            for(String element : list_details) { //TODO A verifier
                categories.setText("Categories : "+list_details.get(0));

                // Affichage de l'image
                Log.d("onPostExecute", "avant l'affichage de l'image");
                if (!(draw==null))
                {
                    Log.d("onPostExecute","affichage du drawable");
                    image.setImageDrawable(draw);
                }
                else {
                    Log.d("onPostExecute","drawable null");
                }

                ;
            }
        }
    }

}