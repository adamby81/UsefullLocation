package com.example.adam.myusefulllocations.Util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.adam.myusefulllocations.Activity.DataPassListener;
import com.example.adam.myusefulllocations.Constant.Constants;
import com.example.adam.myusefulllocations.Data.SearchDatabaseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class SearchJsonAsyncTask extends AsyncTask<Void, Void, String> {

    public double myDistance;
    public double currentLat;
    public double currentLng;
    String address;
    double lat;
    double lng;
    private DataPassListener dataPassListener;
    private final String SEARCH_TABLE_NAME = Constants.TABLE_NAME_SEARCH;
    private final String FAV_TABLE_NAME = Constants.TABLE_NAME_LOCATION;


    private String searchText = "";
    private Context context;

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public SearchDatabaseHandler getDb() {
        return db;
    }

    public void setDb(SearchDatabaseHandler db) {
        this.db = db;
    }


    private SearchDatabaseHandler db;
    private final String API_KEY = "AIzaSyCmEYpUa4JvvgEefYJnzTtISDhJzpES84M";


    @Override
    protected void onPreExecute() {

        db = new SearchDatabaseHandler(this.context);

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

    }


    protected String doInBackground(Void... urls) {
//    Log.e(TAG, "doInBackground: " + this.getApiRequestUrl());
        try {
//            URL url = new URL("https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=" + this.getApiRequestUrl() + "&inputtype=textquery&fields=geometry,photos,formatted_address,name&key=" + API_KEY);
            URL url = new URL("https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + this.getSearchText() + "&key=" + API_KEY);
            HttpsURLConnection myConnection
                    = (HttpsURLConnection) url.openConnection(); //Make the request
            myConnection.setRequestMethod("GET"); //Connection method for the HTTP request
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(myConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder(); //Build the response
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();


                String name, address, img = "", prefix = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400";
                double lon, lat, distance;
                JSONObject jsonobject, geometry, viewport, northeast;
                JSONArray photos;
//                Log.d(TAG, "onPostExecute: " + stringBuilder);
                try {
                    JSONObject json = new JSONObject(stringBuilder.toString()); // Make a JSON object out of the String response
                    JSONArray jArray = json.getJSONArray("results"); // Get the array of results inside the JSON ignore the rest of the information
//      Log.e(TAG, "onPostExecute: " + jArray.toString());
                    for (int i = 0; i < jArray.length(); i++) { //Iterate through the array of results
                        jsonobject = jArray.getJSONObject(i);
                        geometry = jsonobject.getJSONObject("geometry");
                        viewport = geometry.getJSONObject("viewport");
                        northeast = viewport.getJSONObject("northeast");
                        name = jsonobject.getString("name");
                        address = jsonobject.getString("formatted_address");
                        lat = Double.valueOf(northeast.getString("lat"));
                        lon = Double.valueOf(northeast.getString("lng"));
                        distance = distance(currentLat, currentLng, lat, lon);
                        photos = jsonobject.getJSONArray("photos");
                        for (int j = 0; j < photos.length(); j++) {
                            jsonobject = photos.getJSONObject(j);
                            img = prefix + "&photoreference=" + jsonobject.getString("photo_reference");
                        }
                        img += "&key=" + API_KEY;
                        PlaceOfInterest place = new PlaceOfInterest(address,lat,lon,name,img,distance);
                        db.addPlace(place,Constants.TABLE_NAME_SEARCH); //Add to the downloaded list table
                    }
//      update.updateTable();
                } catch (Exception e) {
                    Log.e("My App", "Could not parse malformed JSON: \"" + e.getMessage() + "\"");
                }



                return stringBuilder.toString();
            } finally {
                myConnection.disconnect(); //Close the HTTP connection
            }
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            return null;
        }
    }





    private double distance(double myLat, double myLng, double placeLat, double placeLng) {

        double radiusMyLat = Math.PI * myLat / 180;
        double radiusPlaceLat = Math.PI * placeLat / 180;
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

}
