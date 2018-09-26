package com.example.adam.myusefulllocations.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.adam.myusefulllocations.Constant.Constants;
import com.example.adam.myusefulllocations.Util.PlaceOfInterest;

public class DatabaseHandler extends SQLiteOpenHelper {

    private Context ctx;

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {


        String CREATE_SEARCH_TABLE = "CREATE TABLE " + Constants.TABLE_NAME_SEARCH + "("
                + Constants.KEY_SEARCH_ID + " INTEGER PRIMARY KEY, "
                + Constants.KEY_SEARCH_LOCATION_ADDRESS + " TEXT,"
                + Constants.KEY_SEARCH_LOCATION_NAME + " TEXT,"
                + Constants.KEY_SEARCH_LOCATION_IMAGE + " TEXT,"
                + Constants.KEY_SEARCH_LOCATION_LATITUDE + " LONG,"
                + Constants.KEY_SEARCH_LOCATION_LONGITUDE + " LONG,"
                + Constants.KEY_SEARCH_LOCATION_DISTANCE + " LONG);";

        db.execSQL(CREATE_SEARCH_TABLE);

        final String SQL_CREATE_FAV_TABLE = "CREATE TABLE " + Constants.TABLE_NAME_FAV
                + " (" + Constants.KEY_FAV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Constants.KEY_FAV_NAME + " TEXT, "
                + Constants.KEY_FAV_ADDRESS + " TEXT, "
                + Constants.KEY_FAV_IMAGE + " TEXT, "
                + Constants.KEY_FAV_LATITUDE + " LONG, "
                + Constants.KEY_FAV_LONGITUDE + " LONG, "
                + Constants.KEY_FAV_DISTANCE+ " LONG);";

        db.execSQL(SQL_CREATE_FAV_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME_SEARCH);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME_FAV);


        onCreate(db);
    }



    /**
     * CRUD OPERATIOND: Create, Read, Update, Delete Methods
     */

    public void addPlaceSearch(Context mContext, PlaceOfInterest placeOfInterest, String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        this.ctx = mContext;
        ContentValues values = new ContentValues();
        values.put(Constants.KEY_SEARCH_LOCATION_NAME, placeOfInterest.getName());
        values.put(Constants.KEY_SEARCH_LOCATION_ADDRESS, placeOfInterest.getAddress());
        values.put(Constants.KEY_SEARCH_LOCATION_LATITUDE, placeOfInterest.getLatitude());
        values.put(Constants.KEY_SEARCH_LOCATION_LONGITUDE, placeOfInterest.getLongitude());
        values.put(Constants.KEY_SEARCH_LOCATION_IMAGE, placeOfInterest.getPhotoUrl());
        values.put(Constants.KEY_SEARCH_LOCATION_DISTANCE, placeOfInterest.getDistance());


        // Insert the row

        db.insert(tableName, null, values);

        Log.d("Saved", "New PlaceOfInterest was Added to DB");

    }

    public void addPlaceFavorites(Context mContext, PlaceOfInterest placeOfInterest, String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        this.ctx = mContext;
        ContentValues values = new ContentValues();
        values.put(Constants.KEY_FAV_NAME, placeOfInterest.getName());
        values.put(Constants.KEY_FAV_ADDRESS, placeOfInterest.getAddress());
        values.put(Constants.KEY_FAV_LATITUDE, placeOfInterest.getLatitude());
        values.put(Constants.KEY_FAV_LONGITUDE, placeOfInterest.getLongitude());
        values.put(Constants.KEY_FAV_IMAGE, placeOfInterest.getPhotoUrl());
        values.put(Constants.KEY_FAV_DISTANCE, placeOfInterest.getDistance());


        // Insert the row

        db.insert(tableName, null, values);

        Log.d("Saved", "New PlaceOfInterest was Added to DB");

    }

    //Get a PlaceOfInterest
    public Cursor getPlaceSearch(String tableName, int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        return db.rawQuery("SELECT * FROM " + tableName + " WHERE _id=" + id, null);


    }

    public void deletePlaceFromFav (long id) {
        SQLiteDatabase db = this.getWritableDatabase();

         db.delete(Constants.TABLE_NAME_FAV, "_id=?", new String []{Integer.toString((int) id)});
        db.close();

    }

    public long deleteSearchLocationTable(String tableName) {

        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(tableName, null, null);

    }

    public long deleteFavoritesLocationTable(String tableName) {

        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(tableName, null, null);

    }


    //Get Count Tasks

    public int getLocationsCounter(String tableName) {

        //TODO: ADD A TEXT VIEW FOR COUNTING OPEN TASKS

        String countQuery = "SELECT * FROM " + Constants.TABLE_NAME_SEARCH;
        SQLiteDatabase db = this.getReadableDatabase();


        Cursor cursor = db.rawQuery(countQuery, null);

        return cursor.getCount();
    }

    public int getLocationsCounterFavorites(String tableName) {

        //TODO: ADD A TEXT VIEW FOR COUNTING OPEN TASKS

        String countQuery = "SELECT * FROM " + Constants.TABLE_NAME_SEARCH;
        SQLiteDatabase db = this.getReadableDatabase();


        Cursor cursor = db.rawQuery(countQuery, null);

        return cursor.getCount();
    }

    public Cursor getAllLocations(String tableName) {

        //TODO: ADD A TEXT VIEW FOR COUNTING OPEN TASKS

        SQLiteDatabase db = this.getWritableDatabase();



        return db.rawQuery("SELECT * FROM " + tableName, null);
    }

    public Cursor getAllLocationsFavorites(String tableName) {

        //TODO: ADD A TEXT VIEW FOR COUNTING OPEN TASKS

        SQLiteDatabase db = this.getWritableDatabase();



        return db.rawQuery("SELECT * FROM " + tableName, null);
    }
}
