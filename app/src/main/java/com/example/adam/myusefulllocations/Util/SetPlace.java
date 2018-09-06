package com.example.adam.myusefulllocations.Util;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

public interface SetPlace {
    void getSearchResult(String myPlaces);
    void getPlaceID(String placeID);
    void getPlace(PlaceOfInterest myPlace);
    void setPlaceTablet(LatLng latLng, Context context, Activity activity, int cHour);
    void getNearbyPlaceDialog(String placeID, LatLng latLng);
}

