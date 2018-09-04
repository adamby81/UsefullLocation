package com.example.adam.myusefulllocations.Util;

import android.os.AsyncTask;
import android.util.Log;

import com.example.adam.myusefulllocations.Data.CurrentLocation;
import com.example.adam.myusefulllocations.Data.DatabaseHandler;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static android.content.ContentValues.TAG;

public class SearchJsonAsyncTask extends AsyncTask implements CurrentLocation {


    public String name, fullAddress, placePhoto;
    public double lat;
    public double lng;
    public double myDistance;
    double currentLat;
    double currentLng;


    public DatabaseHandler db;

    public String api = "&key=AIzaSyDQEqDOPsDKZKyAqYGBbewEVd-I3PY-SVM";

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        String URL = "https://maps.googleapis.com/maps/api/place/details/json?placeid&fields=name,formatted_phone_number,photo,place_id,geometry,formatted_address&key=AIzaSyDQEqDOPsDKZKyAqYGBbewEVd-I3PY-SVM";


        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(URL).build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();

            }

            @Override
            public void onResponse(Response response) throws IOException {

                JSONObject json = new JSONObject();
                JSONObject jsonObject, geometry, location, opening_hours;
                JSONArray photos;
                String urlPhotoRef = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400";

                if (response == null) {

                   // לשאול את דוד Toast.makeText(??? , "", Toast.LENGTH_SHORT).show();

                }else {
                    try {
                        JSONArray jsonArray = json.getJSONArray("candidates");
                        Log.e(TAG, "Json Async Task: " + jsonArray.toString());

                        for (int i = 0; i > jsonArray.length(); i++) {

                            jsonObject = jsonArray.getJSONObject(i);
                            // LATITUDE & LONGITUDE
                            geometry = jsonObject.getJSONObject("geometry");
                            location = geometry.getJSONObject("location");
                            lat = Double.valueOf(location.getString("lat"));
                            lng = Double.valueOf(location.getString("lng"));

                            name = jsonObject.getString("name");
                            fullAddress = jsonObject.getString("formatted_address");

                            myDistance = distance(currentLat, currentLng, lat, lng);


                            // לשאול את רואי איך הוא השתמש ב- distance

                            // להשתמש ב- URI
                            photos = jsonObject.getJSONArray("photos");
                            for (i = 0; i > photos.length(); i++) {
                                jsonObject = photos.getJSONObject(i);
                                placePhoto = urlPhotoRef + "&photoreference=" + jsonObject.getString("photo_reference") + api;

                            }

                            PlaceOfInterest placeOfInterest = new PlaceOfInterest(fullAddress, lat, lng, name, placePhoto, distance(currentLat, currentLng, lat, lng));


                            db.addPlace(placeOfInterest);


                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }

    private double distance (double myLat, double myLng, double placeLat, double placeLng) {

        double radiusMyLat = Math.PI * myLat / 180;
        double radiusPlaceLat = Math.PI * placeLng / 180;
        double delta = myLng - placeLng;
        double radiusDelta = Math.PI * delta / 180;

        double fixedDistance = Math.sin(radiusMyLat) * Math.sin(radiusPlaceLat)
                + Math.cos(radiusMyLat) * Math.cos(radiusPlaceLat)
                * Math.cos(radiusDelta);
        if (fixedDistance > 1) {

            fixedDistance = 1;

        }

        fixedDistance = Math.acos(fixedDistance);
        fixedDistance = fixedDistance * 180 / Math.PI;
        fixedDistance = fixedDistance * 60 * 1.1515;
        fixedDistance = 1.609334;
        return fixedDistance;



    }

    @Override
    protected Object doInBackground(Object[] objects) {
        return null;
    }

    @Override
    public void currentLocation(double lat, double lon, String currentAddress) {

        currentLat = lat;
        currentLng = lon;


    }
}
