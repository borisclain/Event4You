package biln.notreappeventful3.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import biln.notreappeventful3.R;

/**
 * Created by Boris on 2015-04-28.
 */

public class MyAdapter extends CursorAdapter {

    LayoutInflater inflater;
    DBHelper dbh;
    SQLiteDatabase db;
    String listIdentifier;

    public MyAdapter(Context context, Cursor c, DBHelper dbh, String listIdentifier) {
        super(context, c, true);
        this.dbh = dbh;
        db = dbh.getWritableDatabase();
        this.listIdentifier = listIdentifier;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateResults(){
        db = dbh.getWritableDatabase();
        if(listIdentifier.equals("suggestedEvents"))
            this.changeCursor(dbh.listEvents(db));
        else if(listIdentifier.equals("searchedEvents"))
            this.changeCursor(dbh.listSearchedEvents(db));
        else if(listIdentifier.equals("favoriteEvents"))
            this.changeCursor(dbh.listFavoris(db));
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if(v==null){
            v = inflater.inflate(R.layout.my_rows, parent, false);
        }

        TextView title = (TextView)v.findViewById(R.id.title);
        TextView startT = (TextView)v.findViewById(R.id.startT);
        TextView stopT = (TextView)v.findViewById(R.id.stopT);
        TextView location = (TextView)v.findViewById(R.id.location);

        CheckBox myBtn = (CheckBox)v.findViewById(R.id.starButton);

        Cursor c = getCursor();
        c.moveToPosition(position);

        int id= c.getInt(c.getColumnIndex(DBHelper.C_ID));

        // Format des dates
        DateFormat dateFormatFinal= new SimpleDateFormat("dd MMM yyyy HH:mm");
        DateFormat dateFormatIni= new SimpleDateFormat("yyyy-MM-dd HH:mm"); //M
        String dateDebut = c.getString(c.getColumnIndex(DBHelper.C_DATE_START));
        String dateFin = c.getString(c.getColumnIndex(DBHelper.C_DATE_STOP));
        try {
            Date dateOld1 = dateFormatIni.parse(dateDebut);
            String newDate1 = dateFormatFinal.format(dateOld1);
            dateDebut = newDate1;
            Log.d("dateFin en DB = ", "" + dateFin);
            if (!(dateFin.equals("2030-01-01 00:00:00"))){
                Date dateOld2 = dateFormatIni.parse(dateFin);
                String newDate2 = dateFormatFinal.format(dateOld2);
                dateFin = newDate2;
            }else{
                dateFin = "inconnue";
            }
        }catch(ParseException e){
            e.printStackTrace();
        }

        title.setText(c.getString(c.getColumnIndex(DBHelper.C_TITLE)));
        location.setText(c.getString(c.getColumnIndex(DBHelper.C_LOCATION)));
        startT.setText(dateDebut);
        stopT.setText(dateFin);

        int valFavorite = c.getInt(c.getColumnIndex(DBHelper.C_FAVORITE));
        //pour ne pas que le setChecked soit considérer dans le recyclage
        myBtn.setOnCheckedChangeListener(null);
        myBtn.setChecked(valFavorite==1);

        myBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                int id = ((Integer) buttonView.getTag()).intValue();
                Log.d("Listener", "Checked " + isChecked + " "  + " " + id );
                dbh.changeFavoriteStatus(db, id);//(int)getItemId(pos)


                if(listIdentifier.equals("suggestedEvents")) {
                    Cursor nc = dbh.listEvents(db);
                    MyAdapter.this.swapCursor(nc);
                }
                else if(listIdentifier.equals("searchedEvents")){
                    Cursor nc = dbh.listSearchedEvents(db);
                    MyAdapter.this.swapCursor(nc);
                }
                else if(listIdentifier.equals("favoriteEvents")){
                    Cursor nc = dbh.listFavoris(db);
                    MyAdapter.this.swapCursor(nc);

                }

                //voir listview: MyAdapter notifyDatasetchanged

                if(isChecked){
                    //Toast.makeText(getApplicationContext(), "Ajouté aux favoris ", Toast.LENGTH_SHORT).show();
                }else{
                    //Toast.makeText(getApplicationContext(), "Retiré des favoris ", Toast.LENGTH_SHORT).show();
                }
            }

        });

        myBtn.setTag(new Integer(id));

        return v;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }

}