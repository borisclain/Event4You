package biln.notreappeventful3.utils;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by boris on 3/26/15.
 */
public class ServiceSearchAndPopulate extends IntentService{

    static final String NAME = "download";

    public ServiceSearchAndPopulate(){
        super(NAME);
    }

    /*
        Selon si l'intention est de peupler la base de données avec des événements suggérés ou avec
        des événements recherchés, cette méthode appelle la méthode de peuplement de la base de données
        appropriée, étiquetant ainsi l'événement comme étant un événement à afficher dans la listView
        d'événements suggérés ou dans la listView d'événements recherchés.
     */
    protected void onHandleIntent(Intent intent){
        EventfulAPI web = new EventfulAPI();
        DBHelper dbh = new DBHelper(this); //getApplicationContext si ça ne fonctionne pas
        SQLiteDatabase db = dbh.getWritableDatabase();
        //message de debut
        Intent in = new Intent("biln.notreappeventful3.BUSY");
        in.putExtra("begin", true);
        this.sendBroadcast(in);

        if(intent.getBooleanExtra("populateSuggestedList", false)) {
            Log.d("Service", "ON FAIT UNE RECHERCHE WEB DE SUGGESTIONS");
            String myCity = intent.getStringExtra("myCity");
            boolean connectionSuccess = populateSuggestedList(web, db, myCity);
            in = new Intent("biln.notreappeventful3.BUSY");
            in.putExtra("end", true);
            in.putExtra("connection status", connectionSuccess);
            this.sendBroadcast(in);
        }
        else if (intent.getBooleanExtra("populateAdvancedSearchList", false)){
            Log.d("Service", "ON FAIT UNE RECHERCHE WEB AVANCÉE");
            Bundle b = intent.getExtras();
            String searchCity = b.getString("city");
            String dateStart = b.getString("dateS");
            String dateStop = b.getString("dateT");
            ArrayList<String> categories = b.getStringArrayList("categories");
            boolean connectionSuccess = populateAdvancedSearchList(web, db, searchCity, dateStart, dateStop, categories);
            in = new Intent("biln.notreappeventful3.BUSY");
            in.putExtra("end", true);
            in.putExtra("connection status", connectionSuccess);
            this.sendBroadcast(in);
        }
        else
            Toast.makeText(getApplicationContext(), "Rien n'est demande", Toast.LENGTH_SHORT).show();


    }

    private boolean populateSuggestedList(EventfulAPI web, SQLiteDatabase db, String myCity) {
        web.getNextEvents(myCity);
        boolean connectionSuccess = web.connectionSuccess;
        ContentValues val = new ContentValues();
        for (int i = 0; i < web.eventsFound.size(); i++) {
            val.put(DBHelper.C_ID_FROM_EVENTFUL, web.eventsFound.get(i).idFromEventful);
            val.put(DBHelper.C_TITLE, web.eventsFound.get(i).title);
            val.put(DBHelper.C_DATE_START, web.eventsFound.get(i).date_start);
            val.put(DBHelper.C_DATE_STOP, web.eventsFound.get(i).date_stop);
            val.put(DBHelper.C_LOCATION, web.eventsFound.get(i).location);
            val.put(DBHelper.C_ADDRESS, web.eventsFound.get(i).address);
            val.put(DBHelper.C_DESCRIPTION, web.eventsFound.get(i).description);
            val.put(DBHelper.C_SUGGESTION, 1);

            Log.d("ServiceSearchAndPop ", " address récupérée de EventFulApi " + web.eventsFound.get(i).address);

            db.update(DBHelper.TABLE_EVENTS, val, "_id_from_eventful = \""+web.eventsFound.get(i).idFromEventful+"\"", null);
            db.insertWithOnConflict(DBHelper.TABLE_EVENTS, null, val, SQLiteDatabase.CONFLICT_IGNORE);

        }
        return connectionSuccess;
    }

    private boolean populateAdvancedSearchList(EventfulAPI web, SQLiteDatabase db, String searchCity, String dateStart, String dateStop, ArrayList<String> categories) {

        web.getDesiredResults(searchCity, dateStart, dateStop, categories, 1);
        boolean connectionSuccess = web.connectionSuccess;
        ContentValues val = new ContentValues();
        for (int i = 0; i < web.eventsFound.size(); i++) {
            val.put(DBHelper.C_ID_FROM_EVENTFUL, web.eventsFound.get(i).idFromEventful);
            val.put(DBHelper.C_TITLE, web.eventsFound.get(i).title);
            val.put(DBHelper.C_DATE_START, web.eventsFound.get(i).date_start);
            val.put(DBHelper.C_DATE_STOP, web.eventsFound.get(i).date_stop);
            val.put(DBHelper.C_LOCATION, web.eventsFound.get(i).location);
            val.put(DBHelper.C_ADDRESS, web.eventsFound.get(i).address);
            val.put(DBHelper.C_DESCRIPTION, web.eventsFound.get(i).description);
            val.put(DBHelper.C_ADVSEARCH, 1);

            db.update(DBHelper.TABLE_EVENTS, val, "_id_from_eventful = \""+web.eventsFound.get(i).idFromEventful+"\"", null);
            db.insertWithOnConflict(DBHelper.TABLE_EVENTS, null, val, SQLiteDatabase.CONFLICT_IGNORE);
        }
        return connectionSuccess;
    }
}