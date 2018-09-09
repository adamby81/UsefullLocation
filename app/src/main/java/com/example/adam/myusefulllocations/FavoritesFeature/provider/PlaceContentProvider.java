package com.example.adam.myusefulllocations.FavoritesFeature.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class PlaceContentProvider extends ContentProvider {

    public static final int PLACES = 100;
    public static final int PLACE_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final String TAG = PlaceContentProvider.class.getName();

    public static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PlaceContract.AUTHORITY, PlaceContract.PATH_PLACE, PLACES);
        uriMatcher.addURI(PlaceContract.AUTHORITY, PlaceContract.PATH_PLACE +
        "/#", PLACE_WITH_ID);
        return uriMatcher;
    }

    private PlaceDBHelper mPlaceDBHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mPlaceDBHelper = new PlaceDBHelper(context);
        return true;
    }
    @Nullable
    @Override
    public Uri insert (@NonNull Uri uri, @NonNull ContentValues values){
        final SQLiteDatabase db = mPlaceDBHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case PLACES:
                long id = db.insert(PlaceContract.PlaceEntry.TABLE_NAME, null, values);
                if (id > 0){

                    returnUri = ContentUris.withAppendedId(PlaceContract.PlaceEntry.CONTENT_URI, id);

                }else {

                    throw new android.database.SQLException("Failed to insert row into " + uri );

                }
                break;
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;

    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        final SQLiteDatabase db = mPlaceDBHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);

        Cursor retCursor;

        switch (match) {
            case PLACES:
                retCursor = db.query(PlaceContract.PlaceEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null
                , sortOrder);
                break;

                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;



    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet Implemented");
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mPlaceDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int placesDleted;

        switch (match){
            case PLACE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                placesDleted = db.delete(PlaceContract.PlaceEntry.TABLE_NAME,
                        "_id=?", new String []{id});
                break;
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        if (placesDleted != 0) {

            getContext().getContentResolver().notifyChange(uri,null);

        }

        return placesDleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mPlaceDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int placesUpdated;

        switch (match){
            case PLACE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                placesUpdated = db.update(PlaceContract.PlaceEntry.TABLE_NAME, values,
                        "_id=?", new String []{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        if (placesUpdated != 0) {

            getContext().getContentResolver().notifyChange(uri,null);

        }

        return placesUpdated;
    }
}
