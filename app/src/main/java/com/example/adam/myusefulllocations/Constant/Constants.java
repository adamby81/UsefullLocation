package com.example.adam.myusefulllocations.Constant;

public class Constants {




    // טבלת מקומות - למועדפים

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "locationDB.db";
    public static final String TABLE_NAME_LOCATION = "locationTable";

    // טבלת חיפוש אחרון

    public static final int SEARCH_DB_VERSION = 1;
    //public static final String SEARCH_DB_NAME = "SearchDB.db";
    public static final String TABLE_NAME_SEARCH = "searchTable";

    // פריטים בטבלת מועדפים

    public static final String KEY_LOCATION_ID = "_id";
    public static final String KEY_LOCATION_ADDRESS = "address";
    public static final String KEY_LOCATION_NAME = "name";
    public static final String KEY_LOCATION_IMAGE = "image";

    public static final String KEY_LOCATION_LATITUDE = "latitude";
    public static final String KEY_LOCATION_LONGITUDE = "longitude";

    // פריטים בטבלת חיפוש אחרון

    public static final String KEY_SEARCH_ID = "_id";
    public static final String KEY_SEARCH_LOCATION_ADDRESS = "address";
    public static final String KEY_SEARCH_LOCATION_NAME = "name";
    public static final String KEY_SEARCH_LOCATION_DISTANCE = "distance";
    public static final String KEY_SEARCH_LOCATION_LATITUDE = "latitude";
    public static final String KEY_SEARCH_LOCATION_LONGITUDE = "longitude";
    public static final String KEY_SEARCH_LOCATION_IMAGE = "image";



}
