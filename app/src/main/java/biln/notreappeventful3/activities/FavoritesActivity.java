package biln.notreappeventful3.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import biln.notreappeventful3.R;
import biln.notreappeventful3.menu.MyMenu;
import biln.notreappeventful3.utils.DBHelper;
import biln.notreappeventful3.utils.MyAdapter;
/**
 * Created by Fleur de Lotus on 20/03/2015.
 */
public class FavoritesActivity extends MyMenu implements View.OnClickListener, AdapterView.OnItemClickListener {
    ListView listv;
    SQLiteDatabase db;
    DBHelper dbh;
    MyAdapter adapter;
    String city;
    Boolean resumeHasRun = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_list_view);
        TextView title = (TextView)findViewById(R.id.title);
        title.setText("Mes Favoris:");

        //dbh = new DBHelper(this)
        dbh =  new DBHelper(this);
        db = dbh.getWritableDatabase();

        Cursor c = DBHelper.listFavoris(db);
        String s = "favoriteEvents";
        adapter = new MyAdapter(this, c, dbh, s);

        listv = (ListView)findViewById(R.id.activity_list);
        listv.setAdapter(adapter);
        listv.setOnItemClickListener(this);
    }

    protected void onResume(){
        super.onResume();
        if(!resumeHasRun) {
            resumeHasRun = true;
            return;
        }
        adapter.updateResults();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        dbh.close();
    }

    public void onClick(View v){
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        //si le clic se fait sur l'icône de l'activité des Favoris, ne rien faire
        if (id == R.id.menuShowFavorites) {
            return true;
        }
        //sinon, faire comme la classe parent le prévoit
        return super.onOptionsItemSelected(item);
    }





    /**
     * Clic sur l'événement pour voir les détails
     * @param parent
     * @param viewClicked
     * @param position
     * @param viewID
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long viewID) {

        Toast.makeText(getApplicationContext(), "Clic reçu", Toast.LENGTH_SHORT).show();

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



}