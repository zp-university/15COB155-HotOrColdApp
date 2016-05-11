package com.mad.team1.hotorcold.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author Zack Pollard
 */

public class ScoreDBHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;
    // db name as public as we use it in test later
    public static final String DB_NAME = "scores.db";

    public SQLiteDatabase myDB;

    public ScoreDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_NAME, null, DB_VERSION);
        myDB = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        myDB = db;

        String query = "CREATE TABLE " +
                ScoreDataModel.scoreEntry.TABLE_NAME +
                " ( " + ScoreDataModel.scoreEntry._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ScoreDataModel.scoreEntry.COLUMN_SCORE_TIME +
                " INTEGER NOT NULL, " +
                ScoreDataModel.scoreEntry.COLUMN_SCORE_VALUE +
                " INTEGER NOT NULL );";

        db.execSQL(query);
        Log.i("WeatherDBHelper","WeatherDBHelper onCreate()");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        /**
         * If we were to ever upgrade the database design, we would add in upgrade code specific to those versions.
         */
    }


    public void clearTable(String table_name){

        myDB.execSQL("DELETE FROM "+ table_name);
    }
}
