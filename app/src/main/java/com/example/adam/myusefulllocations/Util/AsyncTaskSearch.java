package com.example.adam.myusefulllocations.Util;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.example.adam.myusefulllocations.Constant.Constants;
import com.example.adam.myusefulllocations.Data.DatabaseHandler;
import com.google.android.gms.location.FusedLocationProviderClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class AsyncTaskSearch extends AsyncTask<Void, Void, String> {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    BufferedReader bufferedReader;
    public float currentLat;
    public float currentLng;
   public CursorAdapterSearch cursorAdapterSearch;
    FusedLocationProviderClient fusedLocationProviderClient;
    private String searchText = "";
    public Context context;
    Location mLocation;
    List<PlaceOfInterest> placesList;

    ProgressDialog progressDialog;

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

    private DatabaseHandler db;
    private final String API_KEY = "AIzaSyCmEYpUa4JvvgEefYJnzTtISDhJzpES84M";


    @Override
    protected void onPreExecute() {
        String titleProg = "Searching";
        String messageProg = "Searching... Please Wait...";

        db = new DatabaseHandler(this.context, Constants.DB_NAME,null, Constants.SEARCH_DB_VERSION);
        progressDialog = ProgressDialog.show(context, titleProg, messageProg, true);
    }

    @Override
    protected void onPostExecute(String response) {


        cursorAdapterSearch.swapCursor(db.getAllLocations(Constants.TABLE_NAME_SEARCH));

    }


    protected String doInBackground(Void... urls) {

        try {

            URL url = new URL("https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + this.getSearchText() + "&key=" + API_KEY);
            HttpsURLConnection myConnection
                    = (HttpsURLConnection) url.openConnection(); //Make the request
            myConnection.setRequestMethod("GET"); //Connection method for the HTTP request
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(myConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder(); //Build the response
                String line;
                int count = 0;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                    count = count +1;
                }
                Log.i("String Builder: ", "Count TOTAL: " + count);

                bufferedReader.close();

                String name, address, img = "", prefix = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400";
                float lon;
                float lat;
                int jsonLen = 0;
                float distance;
                JSONObject jsonobject, geometry, viewport, northeast;
                JSONArray photos;


                progressDialog.dismiss();

                try {
                    JSONObject json = new JSONObject(stringBuilder.toString()); // Make a JSON object out of the String response
                    JSONArray jArray = json.getJSONArray("results"); // Get the array of results inside the JSON ignore the rest of the information
                    for (int i = 0; i < jArray.length(); i++) { //Iterate through the array of results
                        jsonobject = jArray.getJSONObject(i);

                        jsonLen = jsonLen++;
                        geometry = jsonobject.getJSONObject("geometry");
                        viewport = geometry.getJSONObject("viewport");
                        northeast = viewport.getJSONObject("northeast");

                        name = jsonobject.getString("name");
                        address = jsonobject.getString("formatted_address");

                        lat = Float.parseFloat(northeast.getString("lat"));
                        lon = Float.parseFloat(northeast.getString("lng"));

                        distance = (float) distance(currentLat, currentLng, lat, lon);

                        photos = jsonobject.getJSONArray("photos");
                        for (int j = 0; j < photos.length(); j++) {
                            jsonobject = photos.getJSONObject(j);
                            img = prefix + "&photoreference=" + jsonobject.getString("photo_reference");
                        }
                        img += "&key=" + API_KEY;
                        PlaceOfInterest place = new PlaceOfInterest(address,lat,lon,name,img, distance);
                        db.addPlaceSearch(context, place, Constants.TABLE_NAME_SEARCH); //Add to the downloaded list table

                    }

                } catch (Exception e) {

                }
                return stringBuilder.toString();
            } finally {
                myConnection.disconnect(); //Close the HTTP connection

            }
        } catch (Exception e) {

            return null;
        }
    }

    private double distance(double myLat, double myLng, double placeLat, double placeLng) {

        double radlat1 = Math.PI * myLat / 180;
        double radlat2 = Math.PI * placeLat / 180;
        double theta = myLng - placeLng;
        double radtheta = Math.PI * theta / 180;
        double dist = Math.sin(radlat1) * Math.sin(radlat2) + Math.cos(radlat1) * Math.cos(radlat2) * Math.cos(radtheta);
        if (dist > 1) {
            dist = 1;
        }
        dist = Math.acos(dist);
        dist = dist * 180 / Math.PI;
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        return dist;

    }
}
