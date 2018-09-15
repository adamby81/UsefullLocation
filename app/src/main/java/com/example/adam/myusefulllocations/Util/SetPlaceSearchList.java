package com.example.adam.myusefulllocations.Util;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

public interface SetPlaceSearchList {
    void getJSONaddress(String myPlaces);
    void getJASONphotoURL(String placeID);
    void getPlace(PlaceOfInterest myPlace);
    void setPlaceTablet(LatLng latLng, Context context, Activity activity, int cHour);
    void getNearbyPlaceDialog(String placeID, LatLng latLng);
}

