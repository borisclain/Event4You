package biln.notreappeventful3.utils;

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

    public static final String DB_NAME = "eventful.db";
    public static final int DB_VERSION = 213;

    public static final String TABLE_EVENTS = "events";
    public static final String C_ID = "_id";
    public static final String C_ID_FROM_EVENTFUL = "_id_from_eventful";
    public static final String C_TITLE ="title";
    public static final String C_DATE_START ="date_start";
    public static final String C_DATE_STOP = "date_stop";
    public static final String C_LOCATION ="location";
    public static final String C_ADDRESS = "address";
    public static final String C_DESCRIPTION = "description";
    public static final String C_SUGGESTION = "isSuggestion";
    public static final String C_ADVSEARCH = "isAdvSearch";  // 1 si l'événement est nouveau, 0 sinon
    public static final String C_FAVORITE = "favorite";


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



    /**
     * Dans la base de données, la colonne C_DATE_STOP contient sa valeur sous la forme d'un String:
     * ISO 8601 comme ceci: "2005-03-01 19:00:00"
     * @param db
     */
    public void deletePassedEvents(SQLiteDatabase db){

        String sql = "DELETE from " + TABLE_EVENTS + " WHERE " + C_DATE_STOP + " <= date('now','-1 day')";
        db.execSQL(sql);
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
        Cursor c = db.rawQuery("select * from " + TABLE_EVENTS +" where "+C_SUGGESTION+" = "+1+ " order by " + C_DATE_START+ " asc", null);
        Log.d("DB","liste events nb = "+c.getCount());
        return c;
    }

    public static Cursor listFavoris(SQLiteDatabase db){
        Cursor c = db.rawQuery("select * from "+TABLE_EVENTS+" where "+C_FAVORITE+" = "+1+ " order by " + C_DATE_START + " asc", null);
        Log.d("DB NbFavoris:"," "+c.getCount());
        return c;
    }

    public static Cursor listSearchedEvents(SQLiteDatabase db){
        Cursor c = db.rawQuery("select * from " + TABLE_EVENTS +" where "+C_ADVSEARCH+" = "+1+ " order by " + C_DATE_START + " asc", null);
        Log.d("DB","liste events nb = "+c.getCount());
        return c;
    }



    public static void changeFavoriteStatus(SQLiteDatabase db, int id) {
        ContentValues val = new ContentValues();
        Cursor d = db.rawQuery("select * from "+TABLE_EVENTS+" where "+C_ID+" = "+id, null);
        d.moveToFirst();
        int valFavorite = d.getInt(d.getColumnIndex(C_FAVORITE));
        Log.d("DBQuery", "valeur Favorite Success" + valFavorite);
        if (valFavorite == 0 ) {
            Log.d("DBQUERY", "Mise en favori");
            val.put(C_FAVORITE, 1);
        }
        else {
            Log.d("DBQUERY", "Annulation du favori");
            val.put(C_FAVORITE, 0);
        }
        db.update(TABLE_EVENTS ,val, C_ID+" = "+id, null); //update l'élément dont on a récupéré le ID
    }




}