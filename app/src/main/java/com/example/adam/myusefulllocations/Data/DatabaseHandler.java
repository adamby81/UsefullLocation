package com.example.adam.myusefulllocations.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.adam.myusefulllocations.Constant.Constants;
import com.example.adam.myusefulllocations.Util.PlaceOfInterest;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private String[] queryLocationList;
    private Context ctx;
    public DatabaseHandler(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION );

        this.ctx = context;

    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        // יצירת טבלת מועדפים

        String CREATE_LOCATION_TABLE = "CREATE TABLE " + Constants.TABLE_NAME_LOCATION + "("
                + Constants.KEY_LOCATION_ID + " INTEGER PRIMARY KEY, "
                + Constants.KEY_LOCATION_ADDRESS + " TEXT,"
                + Constants.KEY_LOCATION_NAME + " TEXT,"
                + Constants.KEY_LOCATION_IMAGE + " TEXT,"
                + Constants.KEY_LOCATION_LATITUDE + " LONG,"
                + Constants.KEY_LOCATION_LONGITUDE + " LONG);";

        db.execSQL(CREATE_LOCATION_TABLE);

        // יצירת טבלת חיפוש

        String CREATE_SEARCH_TABLE = "CREATE TABLE " + Constants.TABLE_NAME_LOCATION + "("
                + Constants.KEY_SEARCH_ID + " INTEGER PRIMARY KEY, "
                + Constants.KEY_SEARCH_LOCATION_ADDRESS + " TEXT,"
                + Constants.KEY_SEARCH_LOCATION_NAME + " TEXT,"
                + Constants.KEY_SEARCH_LOCATION_IMAGE + " TEXT,"
                + Constants.KEY_SEARCH_LOCATION_LATITUDE + " LONG,"
                + Constants.KEY_SEARCH_LOCATION_LONGITUDE + " LONG,"
                + Constants.KEY_SEARCH_LOCATION_DISTANCE + "LONG);";

        db.execSQL(CREATE_SEARCH_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME_LOCATION);

        onCreate(db);
    }

    /**
     *   CRUD OPERATIOND: Create, Read, Update, Delete Methods
     */

    public void addPlace(PlaceOfInterest placeOfInterest) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Constants.KEY_LOCATION_ADDRESS, placeOfInterest.getAddress());
        values.put(Constants.KEY_LOCATION_LATITUDE, placeOfInterest.getLatitude());
        values.put(Constants.KEY_LOCATION_LONGITUDE, placeOfInterest.getLongitude());
        values.put(Constants.KEY_LOCATION_NAME,placeOfInterest.getName());
        values.put(Constants.KEY_LOCATION_IMAGE,placeOfInterest.getPhotoUrl());





        // Insert the row

        db.insert(Constants.TABLE_NAME_LOCATION, null, values);

        Log.d("Saved", "New PlaceOfInterest was Added to DB");

    }
    //Get a PlaceOfInterest
    public PlaceOfInterest getLocation (int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(Constants.TABLE_NAME_LOCATION, new String[] {
                        Constants.KEY_LOCATION_ID, Constants.KEY_LOCATION_ADDRESS, Constants.KEY_LOCATION_LATITUDE,
                        Constants.KEY_LOCATION_LONGITUDE, Constants.KEY_LOCATION_NAME,
                        Constants.KEY_LOCATION_IMAGE},
                Constants.KEY_LOCATION_ID + "=?",
                new String [] {String.valueOf(id)}, null, null, null, null);

        if (cursor!=null)
            cursor.moveToFirst();

        PlaceOfInterest placeOfInterest = new PlaceOfInterest();
        placeOfInterest.set_id(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_LOCATION_ID))));
        placeOfInterest.setAddress(cursor.getString(cursor.getColumnIndex(Constants.KEY_LOCATION_ADDRESS)));
        placeOfInterest.setLatitude(Long.parseLong(cursor.getString(cursor.getColumnIndex(Constants.KEY_LOCATION_LATITUDE))));
        placeOfInterest.setLongitude(Long.parseLong(cursor.getString(cursor.getColumnIndex(Constants.KEY_LOCATION_LONGITUDE))));
        placeOfInterest.setName(cursor.getString(cursor.getColumnIndex(Constants.KEY_LOCATION_NAME)));
        placeOfInterest.setPhotoUrl(cursor.getString(cursor.getColumnIndex(Constants.KEY_LOCATION_IMAGE)));

        // Convert time stamp to something readable


        return placeOfInterest;

    }

    // Get All Locations

    public List<PlaceOfInterest> getAllLocations () {

        SQLiteDatabase db = this.getReadableDatabase();

        List<PlaceOfInterest> placeOfInterestList = new ArrayList<>();

        queryLocationList = new String[] {
                Constants.KEY_LOCATION_ID,
                Constants.KEY_LOCATION_ADDRESS, Constants.KEY_LOCATION_LATITUDE,
                Constants.KEY_LOCATION_LONGITUDE, Constants.KEY_LOCATION_NAME,
                Constants.KEY_LOCATION_IMAGE};

        Cursor cursor = db.query(Constants.TABLE_NAME_LOCATION, queryLocationList,
                null,null, null, null,
                Constants.KEY_LOCATION_ID + " DESC");

        if (cursor.moveToFirst()) {
            do {
                PlaceOfInterest placeOfInterest = new PlaceOfInterest();
                placeOfInterest.set_id(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_LOCATION_ID))));
                placeOfInterest.setAddress(cursor.getString(cursor.getColumnIndex(Constants.KEY_LOCATION_ADDRESS)));
                placeOfInterest.setLatitude(Long.parseLong(cursor.getString(cursor.getColumnIndex(Constants.KEY_LOCATION_LATITUDE))));
                placeOfInterest.setLongitude(Long.parseLong(cursor.getString(cursor.getColumnIndex(Constants.KEY_LOCATION_LONGITUDE))));
                placeOfInterest.setName(cursor.getString(cursor.getColumnIndex(Constants.KEY_LOCATION_NAME)));
                placeOfInterest.setPhotoUrl(cursor.getString(cursor.getColumnIndex(Constants.KEY_LOCATION_IMAGE)));




                //add to the tasks list

                placeOfInterestList.add(placeOfInterest);


            }while (cursor.moveToNext());
        }
        return placeOfInterestList;
    }

    // Update PlaceOfInterest

    public int updateLocation (PlaceOfInterest placeOfInterest) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.KEY_LOCATION_ADDRESS, placeOfInterest.getAddress());
        values.put(Constants.KEY_LOCATION_LATITUDE, placeOfInterest.getLatitude());
        values.put(Constants.KEY_LOCATION_LONGITUDE, placeOfInterest.getLongitude());
        values.put(Constants.KEY_LOCATION_NAME, placeOfInterest.getName());
        values.put(Constants.KEY_LOCATION_IMAGE, placeOfInterest.getPhotoUrl());
        //update row
        getLocationsCounter();

        return db.update(Constants.TABLE_NAME_LOCATION, values, Constants.KEY_LOCATION_ID +"=?",
                new String[]{String.valueOf(placeOfInterest.get_id())});
    }

    // Delete PlaceOfInterest
    public void deleteLocation (int id) {

        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(Constants.TABLE_NAME_LOCATION, Constants.KEY_LOCATION_ID + "=?",
                new String[]{String.valueOf(id)});
        getLocationsCounter();

        db.close();


    }

    //Get Count Tasks

    public int getLocationsCounter() {

        //TODO: ADD A TEXT VIEW FOR COUNTING OPEN TASKS

        String countQuery = "SELECT * FROM " + Constants.TABLE_NAME_LOCATION;
        SQLiteDatabase db = this.getReadableDatabase();


        Cursor cursor = db.rawQuery(countQuery, null);

        return cursor.getCount();
    }
}
