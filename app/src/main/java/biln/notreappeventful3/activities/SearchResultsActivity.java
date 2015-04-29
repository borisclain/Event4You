package biln.notreappeventful3.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import biln.notreappeventful3.R;
import biln.notreappeventful3.menu.MyMenu;
import biln.notreappeventful3.utils.DBHelper;
import biln.notreappeventful3.utils.MyAdapter;
import biln.notreappeventful3.utils.ServiceSearchAndPopulate;

/**
 * Created by boris on 4/14/15.
 */
public class SearchResultsActivity extends MyMenu implements View.OnClickListener, AdapterView.OnItemClickListener{


    String city;
    ListView listv;
    MyAdapter adapter;
    SQLiteDatabase db;
    DBHelper dbh;
    BusyReceiver busyR;
    IntentFilter filter;

    boolean resumeHasRun = false;
    boolean justCreated = false;

    String dateStart;
    String dateStop;
    ArrayList<String> categories;
    LinearLayout linlaHeaderProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_list_view);
        TextView title = (TextView)findViewById(R.id.title);
        title.setText("Résultats de la recherche");

        //SharedPreferences mySettings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        //city = mySettings.getString("myCity", "Montreal"); //valeur par défaut : Montreal

        dbh = new DBHelper(this);
        db = dbh.getWritableDatabase();

        Cursor c = DBHelper.listSearchedEvents(db);
        String s = "searchedEvents";
        adapter = new MyAdapter(this, c, dbh, s);

        listv = (ListView)findViewById(R.id.activity_list);
        listv.setAdapter(adapter);
        listv.setOnItemClickListener(this);
        linlaHeaderProgress = (LinearLayout)findViewById(R.id.linlaHeaderProgress);
        busyR = new BusyReceiver();
        filter = new IntentFilter("biln.notreappeventful3.BUSY");

        justCreated = true;
    }

    //Lancée soit par le bouton de recherche, soit par un "back"
    protected void onStart(){
        super.onStart();
        Intent in = getIntent();
        int calledFromSearchActivity = in.getIntExtra("Called from Search Activity", 0);
        if (justCreated) {
            justCreated = false;
            city = in.getStringExtra("city");
            dateStart = in.getStringExtra("dateS");
            dateStop = in.getStringExtra("dateT");
            categories = in.getStringArrayListExtra("categories");

            registerReceiver(busyR, filter);
            Intent intent = new Intent(this, ServiceSearchAndPopulate.class);
            intent.putExtra("populateAdvancedSearchList", true);
            Bundle b = new Bundle();
            b.putString("city", city);
            b.putString("dateS", dateStart);
            b.putString("dateT", dateStop);
            b.putStringArrayList("categories", categories);
            intent.putExtras(b);
            startService(intent);
        }
    }


    protected void onResume(){
        super.onResume();
        if(!resumeHasRun) {
            resumeHasRun = true;
            //registerReceiver(busyR, filter);
            return;
        }
        adapter.updateResults();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        dbh.close();
    }


    @Override
    public void onPause(){
        super.onPause();
    }

    public void onClick(View v){
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long viewID) {


        Log.d("db","Clic sur item en position= "+position+" et avec viewID = "+ viewID);
        Intent intent = new Intent(this, DetailsActivity.class);
        Bundle b = new Bundle();

        Cursor c = DBHelper.getEventByID(db, viewID);
        b.putString("title", c.getString(c.getColumnIndex(DBHelper.C_TITLE)) );
        b.putString("address", c.getString(c.getColumnIndex(DBHelper.C_ADDRESS)) );
        b.putString("startT", c.getString(c.getColumnIndex(DBHelper.C_DATE_START)) );
        b.putString("stopT", c.getString(c.getColumnIndex(DBHelper.C_DATE_STOP)) );
        b.putString("description", c.getString(c.getColumnIndex(DBHelper.C_DESCRIPTION)) );
        b.putString("eventfulID", c.getString(c.getColumnIndex(DBHelper.C_ID_FROM_EVENTFUL)));
        b.putString("ID", c.getString(c.getColumnIndex(DBHelper.C_ID)));
        b.putInt("favori", c.getInt(c.getColumnIndex(DBHelper.C_FAVORITE)));

        intent.putExtras(b);
        startActivity(intent);
    }


    public class BusyReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent){
            if(intent.getBooleanExtra("begin", false)){
                linlaHeaderProgress.setVisibility(View.VISIBLE);
            }
            if(intent.getBooleanExtra("end", false)){
                Cursor c = dbh.listSearchedEvents(db);
                adapter.changeCursor(c);
                linlaHeaderProgress.setVisibility(View.GONE);
                unregisterReceiver(busyR);
            }
        }
    }



}
