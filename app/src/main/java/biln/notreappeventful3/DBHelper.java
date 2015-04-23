package biln.notreappeventful3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Boris on 2015-03-15.
 */

//static
public class DBHelper extends SQLiteOpenHelper {

    //private static DBHelper mInstance = null;

    static final String DB_NAME = "eventful.db";
    static final int DB_VERSION = 160;                   //TODO Important pour le développement

    static final String TABLE_EVENTS = "events";
    static final String C_ID = "_id";
    static final String C_ID_FROM_EVENTFUL = "_id_from_eventful";
    static final String C_TITLE ="title";
    static final String C_DATE_START ="date_start";
    static final String C_DATE_STOP = "date_stop";
    static final String C_LOCATION ="location";
    static final String C_ADDRESS = "address";
    static final String C_DESCRIPTION = "description";
    static final String C_SUGGESTION = "isSuggestion";
    static final String C_ADVSEARCH = "isAdvSearch";  // 1 si l'événement est nouveau, 0 sinon
    static final String C_FAVORITE = "favorite";

/*
    public static DBHelper getInstance(Context ctx){
        if(mInstance == null){
            mInstance = new DBHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }
*/

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table "+ TABLE_EVENTS +" ("
                +C_ID+" integer primary key autoincrement, "
                +C_ID_FROM_EVENTFUL+" text,"
                +C_TITLE+" text,"
                +C_DATE_START+" text,"
                +C_DATE_STOP+" text,"
                +C_LOCATION+" text,"
                +C_ADDRESS+" text,"
                +C_DESCRIPTION+" text,"
                +C_SUGGESTION+" integer default 0,"
                +C_ADVSEARCH+" integer default 0,"
                +C_FAVORITE+" integer default 0,"+
                "UNIQUE "+"("+C_ID_FROM_EVENTFUL+")"+" ON CONFLICT IGNORE)";
        db.execSQL(sql);
        Log.d("DB", "DB created");

        Log.d("DB","onCreate");
    }



    //TODO : Tester de fond en combe la méthode suivante et trouver le meilleur moment pour l'appeler
    /**
     * Dans la base de données, la colonne C_DATE_STOP contient sa valeur sous la forme d'un String:
     * ISO 8601 comme ceci: "2005-03-01 19:00:00"
     * @param db
     */
    public void deletePassedEvents(SQLiteDatabase db){

        Calendar c = Calendar.getInstance();
        System.out.println("Current time =&gt; "+c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String today = df.format(c.getTime());

        System.out.println("Aujourd'hui nous sommes le "+today);
        db.execSQL("delete from "+TABLE_EVENTS+" where date("+C_DATE_STOP+") <"+today);
    }



    public static Cursor getEventByID(SQLiteDatabase db, long idQueried){
        int id = (int)idQueried;
        Cursor c = db.rawQuery("select * from "+TABLE_EVENTS+" where "+C_ID+" = "+id, null);
        c.moveToFirst();
        return c;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+TABLE_EVENTS);
        onCreate(db);
    }



    public void cleanLastAdvancedSearch(SQLiteDatabase db){
        String whereClause = C_FAVORITE + " = " + 1 + " and " + C_ADVSEARCH + " = " + 1;
        ContentValues val = new ContentValues();
        val.put(C_ADVSEARCH, 0);
        db.update(TABLE_EVENTS ,val, whereClause, null);
        db.execSQL("delete from " + TABLE_EVENTS + " where " + C_ADVSEARCH + " = " + 1); //supprime ceux qui restent
    }


    public static Cursor listEvents(SQLiteDatabase db){
        //" where "+C_NEW+ " = "+1+
        Cursor c = db.rawQuery("select * from " + TABLE_EVENTS +" order by " + C_DATE_STOP + " asc", null);
        Log.d("DB","liste events nb = "+c.getCount());
        return c;
    }

    public static Cursor listFavoris(SQLiteDatabase db){
        Cursor c = db.rawQuery("select * from "+TABLE_EVENTS+" where "+C_FAVORITE+" = "+1+ " order by " + C_DATE_STOP + " asc", null);
        Log.d("DB","liste favoris nb = "+c.getCount());
        return c;
    }

    public static Cursor listSearchedEvents(SQLiteDatabase db){
        Cursor c = db.rawQuery("select * from " + TABLE_EVENTS +" where "+C_ADVSEARCH+" = "+1+ " order by " + C_DATE_STOP + " asc", null);
        Log.d("DB","liste events nb = "+c.getCount());
        return c;
    }



    /**
     * Permet de rechercher un mot dans le titre des événements
     *
     * @param db La database en entrée
     * @param word Le mot à rechercher dans les titres
     * @return c Le curseur permettant d'itérer sur ces mots
     */
    public static Cursor searchEventsByTitle(SQLiteDatabase db, String word){
        String args[] = new String[] { "%"+word+"%" };
        Cursor c = db.rawQuery("select * from "+TABLE_EVENTS+" where "+C_TITLE+" like ? order by datetime("+
                C_DATE_STOP+")"+" desc", args);
        Log.d("DB","cherche events nb = "+c.getCount());
        return c;
    }





    public static void changeFavoriteStatus(SQLiteDatabase db, int id) {
        ContentValues val = new ContentValues();
        Cursor d = db.rawQuery("select * from "+TABLE_EVENTS+" where "+C_ID+" = "+id, null);
        Log.d("DBQuery", " success");
        Log.d("DBQuery", "ColumnIndex de favorite: "+d.getColumnIndex(C_FAVORITE));
        d.moveToFirst();
        int valFavorite = d.getInt(d.getColumnIndex(C_FAVORITE));
        Log.d("DBQuery", "valeur Favorite Success" + valFavorite);

        if (valFavorite == 0 ) {
            Log.d("DBQUERY", "Success du if");
            val.put(C_FAVORITE, 1);
            //TODO le retirer de la listView
        }
        else {
            Log.d("DBQUERY", "Success du else");
            val.put(C_FAVORITE, 0);
        }
        db.update(TABLE_EVENTS ,val, C_ID+" = "+id, null); //update l'élément dont on a récupéré le ID
    }

    //TODO Revérifier que ces deux methodes sont bien la meme
    /*
        public static void changeFavoriteStatus(SQLiteDatabase db, int id) {
        ContentValues val = new ContentValues();
        Cursor d = db.rawQuery("select * from "+TABLE_EVENTS+" where "+C_ID+" = "+id, null);

        //Log.d("DBQuery", " success " + d.getString(d.getColumnIndex(DBHelper.C_FAVORITE)));
        Log.d("DBQuery", "ColumnIndex de favorite: "+d.getColumnIndex(C_FAVORITE));

        d.moveToFirst();
        int valFavorite = d.getInt(d.getColumnIndex(C_FAVORITE));
        Log.d("DBQuery", "valeur Favorite Success : " + valFavorite);

        if (valFavorite == 0 ) {
            Log.d("DBQUERY", "Ajouté dans les favoris");
            val.put(C_FAVORITE, 1);

            // TODO le retirer de la listview
        }
        else {

            Log.d("DBQUERY", "Retire des favoris");
            val.put(C_FAVORITE, 0);
            // TODO le retirer de la listview des favoris
        }
        db.update(TABLE_EVENTS ,val, C_ID+" = "+id, null); //update l'élément dont on a récupéré le ID
    }
     */

    /*

    public void setEventsToOld(SQLiteDatabase db){

        ContentValues val = new ContentValues();
        Cursor c = db.rawQuery("select * from "+TABLE_EVENTS+" where "+C_NEW+" = "+1, null);
        c.moveToFirst();
        while (c.isAfterLast() == false) {
            val.put(C_NEW, 0);
            int id =  c.getInt(c.getColumnIndex(C_ID));
            db.update(TABLE_EVENTS , val, C_ID+" = "+id, null);
            c.moveToNext();
        }
    }

    */


}