package biln.notreappeventful3.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import biln.notreappeventful3.R;
import biln.notreappeventful3.menu.MyMenu;
import biln.notreappeventful3.utils.DBHelper;
import biln.notreappeventful3.utils.MyAdapter;
import biln.notreappeventful3.utils.ServiceSearchAndPopulate;

/**
 *
 */
public class MainActivity extends MyMenu implements View.OnClickListener, AdapterView.OnItemClickListener {
    ListView listv;
    MyAdapter adapter;
    SQLiteDatabase db;
    DBHelper dbh; //static DBHelper
    BusyReceiver busyR;
    IntentFilter filter;
    LinearLayout linlaHeaderProgress;

    boolean resumeHasRun = false;
    boolean firstCallInAppRun = true;

    String myCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.my_list_view);


        dbh = new DBHelper(this);
        db = dbh.getWritableDatabase();

        Cursor c = DBHelper.listEvents(db);
        String s = "suggestedEvents";
        adapter = new MyAdapter(this, c, dbh, s);

        listv = (ListView)findViewById(R.id.activity_list);
        listv.setAdapter(adapter);
        listv.setOnItemClickListener(this);

        busyR = new BusyReceiver();
        filter = new IntentFilter("biln.notreappeventful3.BUSY");

        linlaHeaderProgress = (LinearLayout)findViewById(R.id.linlaHeaderProgress);
    }

    /*
        Si l'activité est lancée pour la première fois dans cette exécution de l'app, onStart() lancera le service
     */
    protected void onStart(){
        super.onStart();
        SharedPreferences mySettings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        firstCallInAppRun = mySettings.getBoolean("First call in app run", false);
        Log.d("FirstCall", " "+firstCallInAppRun);
        if (firstCallInAppRun){
            SharedPreferences.Editor edit = mySettings.edit();
            edit.putBoolean("First call in app run", false);
            edit.commit();
            myCity = mySettings.getString("myCity", "Montreal");
            registerReceiver(busyR, filter);
            Intent intent = new Intent(this, ServiceSearchAndPopulate.class);
            intent.putExtra("populateSuggestedList", true);
            intent.putExtra("myCity", myCity);
            startService(intent);
            return;
        }
        adapter.updateResults();
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
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    public void onClick(View v){

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        //si le clic se fait sur l'icône de l'activité des Suggestions, ne rien faire
        if (id == R.id.menuSuggestion) {
            return true;
        }
        //sinon, faire comme la classe parent le prévoit
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long viewID) {


        Log.d("db","Clic sur item en position= "+position+" et avec viewID = "+ viewID);
        Intent intent = new Intent(this, DetailsActivity.class);
        Bundle b = new Bundle();

        Cursor c = dbh.getEventByID(db, viewID);
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
                Cursor c = dbh.listEvents(db);
                adapter.changeCursor(c);
                linlaHeaderProgress.setVisibility(View.GONE);
                unregisterReceiver(busyR);
            }
        }
    }

}


