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

public class SearchDatabaseHandler extends SQLiteOpenHelper {

    private String[] querySearchList;
    private Context ctx;
    public SearchDatabaseHandler(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION );

        this.ctx = context;

    }
    @Override
    public void onCreate(SQLiteDatabase db) {

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

        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME_SEARCH);

        onCreate(db);
    }

    /**
     *   CRUD OPERATIOND: Create, Read, Update, Delete Methods
     */

    public void addPlace(PlaceOfInterest placeOfInterest) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.KEY_SEARCH_LOCATION_NAME,placeOfInterest.getName());
        values.put(Constants.KEY_SEARCH_LOCATION_ADDRESS, placeOfInterest.getAddress());
        values.put(Constants.KEY_SEARCH_LOCATION_LATITUDE, placeOfInterest.getLatitude());
        values.put(Constants.KEY_SEARCH_LOCATION_LONGITUDE, placeOfInterest.getLongitude());
        values.put(Constants.KEY_SEARCH_LOCATION_DISTANCE,placeOfInterest.getDistance());
        values.put(Constants.KEY_SEARCH_LOCATION_IMAGE,placeOfInterest.getPhotoUrl());





        // Insert the row

        db.insert(Constants.TABLE_NAME_SEARCH, null, values);

        Log.d("Saved", "New PlaceOfInterest was Added to DB");

    }
    //Get a PlaceOfInterest
    public PlaceOfInterest getLocation (int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(Constants.TABLE_NAME_SEARCH, new String[] {
                        Constants.KEY_SEARCH_ID, Constants.KEY_SEARCH_LOCATION_ADDRESS, Constants.KEY_LOCATION_LATITUDE,
                        Constants.KEY_SEARCH_LOCATION_LONGITUDE, Constants.KEY_SEARCH_LOCATION_NAME,
                        Constants.KEY_SEARCH_LOCATION_DISTANCE,
                        Constants.KEY_SEARCH_LOCATION_IMAGE},
                Constants.KEY_SEARCH_ID + "=?",
                new String [] {String.valueOf(id)}, null, null, null, null);

        if (cursor!=null)
            cursor.moveToFirst();

        PlaceOfInterest placeOfInterest = new PlaceOfInterest();
        placeOfInterest.set_id(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_SEARCH_ID))));
        placeOfInterest.setAddress(cursor.getString(cursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_ADDRESS)));
        placeOfInterest.setLatitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_LATITUDE))));
        placeOfInterest.setLongitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_LONGITUDE))));
        placeOfInterest.setLongitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_DISTANCE))));
        placeOfInterest.setName(cursor.getString(cursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_NAME)));
        placeOfInterest.setPhotoUrl(cursor.getString(cursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_IMAGE)));

        // Convert time stamp to something readable


        return placeOfInterest;

    }

    // Get All Locations

    public List<PlaceOfInterest> getAllLocations () {

        SQLiteDatabase db = this.getReadableDatabase();

        List<PlaceOfInterest> searchList = new ArrayList<>();

        querySearchList = new String[] {
                Constants.KEY_SEARCH_ID,
                Constants.KEY_SEARCH_LOCATION_ADDRESS, Constants.KEY_SEARCH_LOCATION_LATITUDE,
                Constants.KEY_SEARCH_LOCATION_LONGITUDE, Constants.KEY_SEARCH_LOCATION_NAME,
                Constants.KEY_SEARCH_LOCATION_DISTANCE,
                Constants.KEY_SEARCH_LOCATION_IMAGE};

        Cursor cursor = db.query(Constants.TABLE_NAME_SEARCH, querySearchList,
                null,null, null, null,
                Constants.KEY_SEARCH_ID + " DESC");

        if (cursor.moveToFirst()) {
            do {
                PlaceOfInterest placeOfInterest = new PlaceOfInterest();
                placeOfInterest.set_id(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_SEARCH_ID))));
                placeOfInterest.setAddress(cursor.getString(cursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_ADDRESS)));
                placeOfInterest.setLatitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_LATITUDE))));
                placeOfInterest.setLongitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_LONGITUDE))));
                placeOfInterest.setLongitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_DISTANCE))));
                placeOfInterest.setName(cursor.getString(cursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_NAME)));
                placeOfInterest.setPhotoUrl(cursor.getString(cursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_IMAGE)));




                //add to the tasks list

                searchList.add(placeOfInterest);


            }while (cursor.moveToNext());
        }
        return searchList;
    }

    // Update PlaceOfInterest

    public int updateLocation (PlaceOfInterest placeOfInterest) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.KEY_SEARCH_LOCATION_NAME,placeOfInterest.getName());
        values.put(Constants.KEY_SEARCH_LOCATION_ADDRESS, placeOfInterest.getAddress());
        values.put(Constants.KEY_SEARCH_LOCATION_LATITUDE, placeOfInterest.getLatitude());
        values.put(Constants.KEY_SEARCH_LOCATION_LONGITUDE, placeOfInterest.getLongitude());
        values.put(Constants.KEY_SEARCH_LOCATION_DISTANCE,placeOfInterest.getDistance());
        values.put(Constants.KEY_SEARCH_LOCATION_IMAGE,placeOfInterest.getPhotoUrl());

        //update row
        getLocationsCounter();

        return db.update(Constants.TABLE_NAME_SEARCH, values, Constants.KEY_SEARCH_ID +"=?",
                new String[]{String.valueOf(placeOfInterest.get_id())});
    }

    // Delete PlaceOfInterest
    public void deleteSearchLocationTable () {

        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(Constants.TABLE_NAME_SEARCH, null, null);

        db.close();


    }

    //Get Count Tasks

    public int getLocationsCounter() {

        //TODO: ADD A TEXT VIEW FOR COUNTING OPEN TASKS

        String countQuery = "SELECT * FROM " + Constants.TABLE_NAME_SEARCH;
        SQLiteDatabase db = this.getReadableDatabase();


        Cursor cursor = db.rawQuery(countQuery, null);

        return cursor.getCount();
    }
}
