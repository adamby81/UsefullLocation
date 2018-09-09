package com.example.adam.myusefulllocations.FavoritesFeature.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PlaceDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_FAV_NAME = "location_fav.db";
    public static final int DATABASE_VERSION = 1;



    public PlaceDBHelper(Context context) {
        super(context, DATABASE_FAV_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_PLACES_TABLE = "CREATE TABLE " + PlaceContract.PlaceEntry.TABLE_NAME
                 + " (" + PlaceContract.PlaceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                 + PlaceContract.PlaceEntry.COLUMNS_PLACE_ID + " TEXT NOT NULL, "
                 + "UNIQUE (" + PlaceContract.PlaceEntry.COLUMNS_PLACE_ID + ") ON CONFLICT REPLACE"
                 + ");";
        db.execSQL(SQL_CREATE_PLACES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP TABLE IF EXISTS " + PlaceContract.PlaceEntry.TABLE_NAME);
        onCreate(db);


    }
}
