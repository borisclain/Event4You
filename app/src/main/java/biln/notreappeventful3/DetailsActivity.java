package biln.notreappeventful3;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created by Boris on 2015-03-25.
 */
public class DetailsActivity extends MyMenu {

    TextView title;
    TextView location;
    TextView startT;
    TextView stopT;
    TextView description;
    TextView categories;
    TextView imageUrl;
    ImageView image;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.event_description);


        title = (TextView) findViewById(R.id.event_title);
        location = (TextView) findViewById(R.id.event_location);
        startT = (TextView) findViewById(R.id.event_datestart);
        stopT = (TextView) findViewById(R.id.event_datestop);
        description = (TextView) findViewById(R.id.event_description);
        categories = (TextView) findViewById(R.id.event_categories);
        image = (ImageView) findViewById(R.id.event_image);
        imageUrl = (TextView) findViewById(R.id.imageUrl);


        Bundle b = getIntent().getExtras();

        title.setText(b.getString("title"));
        startT.setText(b.getString("startT"));
        stopT.setText(b.getString("stopT"));
        location.setText(b.getString("location"));

        Log.d("DetailsActivity", "On a fait les setText");


        Document doc = Jsoup.parse(b.getString("description"));
        description.setText(doc.body().text());

        //Drawable im = LoadImageFromWebOperations("http://s4.evcdn.com/images/medium/I0-001/004/564/091-4.jpeg_/john-mellencamp-91.jpeg");
        //image.setImageDrawable(im);


        /*ArrayList event_details = new ArrayList();
        EventfulAPI web = new EventfulAPI();
        event_details = web.getEventDetails("E0-001-081672548-8");
        categories.setText("test title : " + event_details.get(0));*/


        String param = b.getString("eventfulID");
        Log.d("DetailsActivity", "Le ID =" + param);
        new MyAsyncTask().execute(param);

    }

    // chargement d'une image à partir d'une url
    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }


    /*protected Cursor doInBackground(String... params) {
        ArrayList event_details = new ArrayList();
        EventfulAPI web = new EventfulAPI();
        event_details = web.getEventDetails("E0-001-081672548-8");
    }*/

    private class MyAsyncTask extends AsyncTask<String, String, ArrayList<String>> {


        @Override
        protected ArrayList<String> doInBackground(String... params) {
            ArrayList<String> event_details = new ArrayList<String>();
            EventfulAPI web = new EventfulAPI();
            String id = params[0];
            event_details = web.getEventDetails(id);
            return event_details;
        }

        @Override
        protected void onPostExecute(ArrayList<String> list_details) {
            for(String element : list_details) { //TODO A verifier
                categories.setText(list_details.get(0));
                imageUrl.setText(list_details.get(1));
            }
        }
    }

}

