package com.example.adam.myusefulllocations.FavoritesFeature.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.adam.myusefulllocations.Util.PlaceOfInterest;

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
                + PlaceContract.PlaceEntry.KEY_FAV_PLACE_NAME + " TEXT, "
                + PlaceContract.PlaceEntry.KEY_FAV_PLACE_ADDRESS + " TEXT, "
                + PlaceContract.PlaceEntry.KEY_FAV_PLACE_LATITUDE + " TEXT, "
                + PlaceContract.PlaceEntry.KEY_FAV_PLACE_LONGITUDE + " TEXT, "
                + PlaceContract.PlaceEntry.KEY_FAV_PLACE_PHOTOURL + " TEXT, "
                + PlaceContract.PlaceEntry.KEY_FAV_PLACE_DISTANCE + " TEXT, "

                + "UNIQUE (" + PlaceContract.PlaceEntry.COLUMNS_PLACE_ID + ") ON CONFLICT REPLACE"
                 + ");";
        db.execSQL(SQL_CREATE_PLACES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP TABLE IF EXISTS " + PlaceContract.PlaceEntry.TABLE_NAME);
        onCreate(db);


    }
    public void addPlace(PlaceOfInterest placeOfInterest) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(PlaceContract.PlaceEntry.KEY_FAV_PLACE_NAME,placeOfInterest.getName());
        values.put(PlaceContract.PlaceEntry.KEY_FAV_PLACE_ADDRESS, placeOfInterest.getAddress());
        values.put(PlaceContract.PlaceEntry.KEY_FAV_PLACE_LATITUDE, placeOfInterest.getLatitude());
        values.put(PlaceContract.PlaceEntry.KEY_FAV_PLACE_LONGITUDE, placeOfInterest.getLongitude());
        values.put(PlaceContract.PlaceEntry.KEY_FAV_PLACE_DISTANCE,placeOfInterest.getDistance());
        values.put(PlaceContract.PlaceEntry.KEY_FAV_PLACE_PHOTOURL,placeOfInterest.getPhotoUrl());





        // Insert the row

        db.insert(PlaceContract.PlaceEntry.TABLE_NAME, null, values);

        Log.d("Saved", "New PlaceOfInterest was Added to DB");

    }
}
