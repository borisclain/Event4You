package biln.notreappeventful3;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by boris on 3/26/15.
 */
public class ServiceSearchAndPopulate extends IntentService{

    String city = "Montreal";
    static final String NAME = "download";

    public ServiceSearchAndPopulate(){
        super(NAME);
        Log.d("Service", "Constructeur appelé");
    }

    /*
        Selon si l'intention est de peupler la base de données avec des événements suggérés ou avec
        des événements recherchés, cette méthode appelle la méthode de peuplement de la base de données
        appropriée, étiquetant ainsi l'événement comme étant un événement à afficher dans la listView
        d'événements suggérés ou dans la listView d'événements recherchés.
     */
    protected void onHandleIntent(Intent intent){
        Log.d("Service", "onHandleIntent appelé");

        EventfulAPI web = new EventfulAPI();
        DBHelper dbh = new DBHelper(this); //getApplicationContext si ça ne fonctionne pas
        SQLiteDatabase db = dbh.getWritableDatabase();

        //message de debut
        Intent in = new Intent("biln.notreappeventful3.BUSY");
        in.putExtra("begin", true);
        this.sendBroadcast(in);

        if(intent.getBooleanExtra("populateSuggestedList", false)) {
            populateSuggestedList(web, db);
            in = new Intent("biln.notreappeventful3.BUSY");
            in.putExtra("end", true);
            this.sendBroadcast(in);
        }
        else if (intent.getBooleanExtra("populateSearchedList", false)){
            populateSuggestedList(web, db);
            in = new Intent("biln.notreappeventful3.BUSY");
            in.putExtra("end", true);
            this.sendBroadcast(in);
        }
        else
            Toast.makeText(getApplicationContext(), "Rien n'est demande", Toast.LENGTH_SHORT).show();


    }

    private void populateSuggestedList(EventfulAPI web, SQLiteDatabase db) {
        web.getNextEvents(city);
        ContentValues val = new ContentValues();
        for (int i = 0; i < web.eventsFound.size(); i++) {
            val.put(DBHelper.C_ID_FROM_EVENTFUL, web.eventsFound.get(i).idFromEventful);
            val.put(DBHelper.C_TITLE, web.eventsFound.get(i).title);
            val.put(DBHelper.C_DATE_START, web.eventsFound.get(i).date_start);
            val.put(DBHelper.C_DATE_STOP, web.eventsFound.get(i).date_stop);
            val.put(DBHelper.C_LOCATION, web.eventsFound.get(i).location);
            val.put(DBHelper.C_DESCRIPTION, web.eventsFound.get(i).description);
            val.put(DBHelper.C_SUGGESTION, 1);

            db.update(DBHelper.TABLE_EVENTS, val, "_id_from_eventful = \""+web.eventsFound.get(i).idFromEventful+"\"", null);
            db.insertWithOnConflict(DBHelper.TABLE_EVENTS, null, val, SQLiteDatabase.CONFLICT_IGNORE);
        }
    }

    private void populateSearchedList(EventfulAPI web, SQLiteDatabase db) {
        web.getNextEvents(city);
        ContentValues val = new ContentValues();
        for (int i = 0; i < web.eventsFound.size(); i++) {
            val.put(DBHelper.C_ID_FROM_EVENTFUL, web.eventsFound.get(i).idFromEventful);
            val.put(DBHelper.C_TITLE, web.eventsFound.get(i).title);
            val.put(DBHelper.C_DATE_START, web.eventsFound.get(i).date_start);
            val.put(DBHelper.C_DATE_STOP, web.eventsFound.get(i).date_stop);
            val.put(DBHelper.C_LOCATION, web.eventsFound.get(i).location);
            val.put(DBHelper.C_DESCRIPTION, web.eventsFound.get(i).description);
            val.put(DBHelper.C_ADVSEARCH, 1);

            db.update(DBHelper.TABLE_EVENTS, val, "_id_from_eventful = \""+web.eventsFound.get(i).idFromEventful+"\"", null);
            db.insertWithOnConflict(DBHelper.TABLE_EVENTS, null, val, SQLiteDatabase.CONFLICT_IGNORE);
        }
            /*
            String sqlPhrase = "INSERT OR REPLACE INTO events (_id_from_eventful, title, " +
                    "date_start, date_stop, location, description, isSuggestion, " +
                    "isAdvSearch, favorite) VALUES (" + web.eventsFound.get(i).idFromEventful + ", " +
                    web.eventsFound.get(i).title + ", " + web.eventsFound.get(i).date_start + ", " +
                    web.eventsFound.get(i).date_stop + ", " + web.eventsFound.get(i).location + ", " +
                    web.eventsFound.get(i).description + ", COALESCE((SELECT isSuggestion FROM events "+
                    "WHERE _id_from_eventful = "+web.eventsFound.get(i).idFromEventful+"), 0), 1, "+
                    "COALESCE((SELECT favorite FROM events WHERE _id_from_eventful = "+
                    web.eventsFound.get(i).idFromEventful + "), 0));";
            db.execSQL(sqlPhrase);
            */
    }
}
