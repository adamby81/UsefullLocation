package com.example.adam.myusefulllocations.Util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.adam.myusefulllocations.Constant.Constants;
import com.example.adam.myusefulllocations.Data.DatabaseHandler;
import com.example.adam.myusefulllocations.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class NearbyAsyncTask extends AsyncTask<Void, Void, String> {

    private static final String TAG = "NearbyAsyncTask";
    private ProgressDialog progressDialog;
    public float lon;
    public float lat;
    private Context context;
    private DatabaseHandler db;
    public float currentLng, currentLat;
    public CursorAdapterSearch cursorAdapterSearch;
    private final String API_KEY = "AIzaSyCmEYpUa4JvvgEefYJnzTtISDhJzpES84M";

    public Context getContext() {
        return context;
    }

    public void setLat(float lat) {
        this.currentLat = lat;
    }

    public void setLon(float lon) {
        this.currentLng = lon;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    protected void onPreExecute() {

        int titleProg = (R.string.searching);
        int messageProg = R.string.searchingMessage;
        progressDialog = ProgressDialog.show(context, Integer.toString(titleProg), Integer.toString(messageProg), true);

        db = new DatabaseHandler(this.context, Constants.DB_NAME,null, Constants.SEARCH_DB_VERSION);

    }

    protected String doInBackground(Void... urls) {
//        Log.e(TAG, "doInBackground: " + this.currentLat + "," + this.currentLng);
        try {
            URL url = new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + this.currentLat + "," + this.currentLng +
                    "&radius=5000&key=" + API_KEY); //API request url
//            Log.e(TAG, "doInBackground: request is=> " + url);
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
                Float lon, lat, distance;
                JSONObject jsonobject, geometry, viewport, northeast;
                JSONArray photos;
                if (stringBuilder == null) {
//                    Log.e(TAG, "doInBackground: no results!");
                } else {
                    progressDialog.dismiss();
//                    Log.d(TAG, "onPostExecute: " + stringBuilder);
                    try {
                        JSONObject json = new JSONObject(stringBuilder.toString()); // Make a JSON object out of the String response
                        JSONArray jArray = json.getJSONArray("candidates");
                        if (jArray.length() >= 0) {
                            return "No Places Found";
                        } else {
                            // Get the array of results inside the JSON ignore the rest of the information
//                            Log.e(TAG, "onPostExecute: " + jArray.toString());
                            for (int i = 0; i < jArray.length(); ) { //Iterate through the array of results
                                jsonobject = jArray.getJSONObject(i);
                                geometry = jsonobject.getJSONObject("geometry");
                                viewport = geometry.getJSONObject("viewport");
                                northeast = viewport.getJSONObject("northeast");
                                name = jsonobject.getString("name");
                                address = jsonobject.getString("vicinity");
                                lat = Float.valueOf(northeast.getString("lat"));
                                lon = Float.valueOf(northeast.getString("lng"));

                                distance = (float) distance(currentLat, currentLng, lat, lon);
                                photos = jsonobject.getJSONArray("photos");
                                for (int j = 0; j < photos.length(); j++) {
                                    jsonobject = photos.getJSONObject(j);
                                    img = prefix + "&photoreference=" + jsonobject.getString("photo_reference");
                                }
                                img += "&type=restaurant&key=" + API_KEY;
                                PlaceOfInterest place = new PlaceOfInterest(address,lat,lon,name,img, distance);
                                db.addPlaceSearch(context, place, Constants.TABLE_NAME_SEARCH); //Add to the downloaded list table
                                i++;
                                //Add to the downloaded list table
                            }
                        }
                    } catch (Exception e) {
                        Log.e("My App", "Could not parse malformed JSON: \"" + e.getMessage() + "\"");
                    }
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

    protected void onPostExecute(String response) {
            cursorAdapterSearch.swapCursor(db.getAllLocations(Constants.TABLE_NAME_SEARCH));

    }

    private double distance(float lat1, float lon1, float lat2, float lon2) {
        double radlat1 = Math.PI * lat1 / 180;
        double radlat2 = Math.PI * lat2 / 180;
        double theta = lon1 - lon2;
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

