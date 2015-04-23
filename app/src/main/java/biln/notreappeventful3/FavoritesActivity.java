package biln.notreappeventful3;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        adapter = new MyAdapter(this, c);
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
        b.putString("location", c.getString(c.getColumnIndex(DBHelper.C_LOCATION)) );
        b.putString("startT", c.getString(c.getColumnIndex(DBHelper.C_DATE_START)) );
        b.putString("description", c.getString(c.getColumnIndex(DBHelper.C_DESCRIPTION)) );
        b.putString("eventfulID", c.getString(c.getColumnIndex(DBHelper.C_ID_FROM_EVENTFUL)));
        intent.putExtras(b);
        startActivity(intent);

    }

    /**
     * Adapte la vue suivant les info de la BDD
     */
    public class MyAdapter extends CursorAdapter {

        LayoutInflater inflater;

        public MyAdapter(Context context, Cursor c) {
            super(context, c, true);
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null) {
                v = inflater.inflate(R.layout.my_rows, parent, false);

            }

            // si favoris
            Cursor c = getCursor();
            c.moveToPosition(position);
            final String id= c.getString(c.getColumnIndex(DBHelper.C_ID)); //TODO : voir pourquoi getInt fonctionne pas
            String favorite = c.getString(c.getColumnIndex(DBHelper.C_FAVORITE));
            CheckBox myBtn = (CheckBox)v.findViewById(R.id.starButton);
            int compteur_favoris = 0;

            Log.d("avant sel favorite ", favorite + " id : " + id);

            if(favorite.contains("1")){
                //ID dans favoris.xml
                TextView title = (TextView) v.findViewById(R.id.title);
                TextView startT = (TextView) v.findViewById(R.id.startT);
                TextView stopT = (TextView)v.findViewById(R.id.stopT);
                TextView location = (TextView) v.findViewById(R.id.location);
                //étoile sur évenement

                //Format des dates
                DateFormat dateFormatFinal= new SimpleDateFormat("dd MMM yyyy HH:mm");
                DateFormat dateFormatIni= new SimpleDateFormat("yyyy-MM-dd HH:mm");


                String dateDebut = c.getString(c.getColumnIndex(DBHelper.C_DATE_START));
                String dateFin = c.getString(c.getColumnIndex(DBHelper.C_DATE_STOP));
                try {
                    Date dateOld1 = dateFormatIni.parse(dateDebut);
                    String newDate1 = dateFormatFinal.format(dateOld1);
                    dateDebut = newDate1;

                    if (!(dateFin == "2030-01-01")){
                        Date dateOld2 = dateFormatIni.parse(dateFin);
                        String newDate2 = dateFormatFinal.format(dateOld2);
                        dateFin = newDate2;
                    }else{
                        dateFin = "inconnue";
                    }
                }catch(ParseException e){
                    e.printStackTrace();
                }

                //selection des favoris dans la bdd
                Log.d("selection favorite ", favorite + " id : " + id);
                title.setText(c.getString(c.getColumnIndex(DBHelper.C_TITLE)) + " id supposé : " + id);
                location.setText(c.getString(c.getColumnIndex(DBHelper.C_LOCATION)));
                startT.setText(dateDebut);
                stopT.setText(dateFin);
            }

            // Gestion de l'étoile de l'événement

            myBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    int id = ((Integer)buttonView.getTag()).intValue();
                    Toast.makeText(getApplicationContext(), "Retiré des favoris ", Toast.LENGTH_SHORT).show();
                    Log.d("Listener", "Checked "+isChecked+" " + " "+ id  );
                    dbh.changeFavoriteStatus(db, id);//(int)getItemId(pos)

                    //Rafraîchir l'activité pour mettre à jour la listView
                    //finish();
                    //startActivity(getIntent());

                    adapter.updateResults();

                }
            });


            myBtn.setTag(new Integer(Integer.parseInt(id)));

            return v;
        }

        public void updateResults(){
            this.changeCursor(dbh.listFavoris(db));
            notifyDataSetChanged();
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return null;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

        }

    }
}