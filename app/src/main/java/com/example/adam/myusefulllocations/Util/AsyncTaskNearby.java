package com.example.adam.myusefulllocations.Util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.adam.myusefulllocations.Constant.Constants;
import com.example.adam.myusefulllocations.Data.DatabaseHandler;
import com.example.adam.myusefulllocations.Fragment.SearchFragment;
import com.example.adam.myusefulllocations.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class AsyncTaskNearby extends AsyncTask<Void, Void, String> {

    private static final String TAG = "AsyncTaskNearby";
    private ProgressDialog progressDialog;

    private Context context;
    private DatabaseHandler db;
    public float currentLng, currentLat;
    public CursorAdapterSearch cursorAdapterSearch;

    private final String API_KEY = "AIzaSyCmEYpUa4JvvgEefYJnzTtISDhJzpES84M";

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    protected void onPreExecute() {

        String titleProg = getContext().getString(R.string.searching);
        String messageProg = getContext().getString(R.string.searchingMessage);

        progressDialog = ProgressDialog.show(context, titleProg, messageProg, true);

        db = new DatabaseHandler(this.context, Constants.DB_NAME,null, Constants.SEARCH_DB_VERSION);

    }
            //TODO - FINISH THE RADIUS CHOOSER!!
    protected String doInBackground(Void... urls) {

        try {
            URL url = new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + this.currentLat + "," + this.currentLng +
                    "&radius=3000&type=" + SearchFragment.type + "&key=" + API_KEY);
            HttpsURLConnection myConnection
                    = (HttpsURLConnection) url.openConnection();

            myConnection.setRequestMethod("GET");
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(myConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                String name;
                String address;
                String img = "";
                String prefix = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400";

                float lng;
                float lat;
                float distance;
                JSONObject jsonobject;
                JSONObject geometry;
                JSONObject viewport;
                JSONObject northeast;
                JSONArray photos;

                if (stringBuilder == null) {


                } else {

                    progressDialog.dismiss();

                    try {
                        JSONObject json = new JSONObject(stringBuilder.toString());
                        JSONArray jArray = json.getJSONArray("results");

                        if (jArray.length() <= 0) {
                            return String.valueOf(R.string.no_places_found_json);
                        } else {

                            for (int i = 0; i < jArray.length(); ) {

                                jsonobject = jArray.getJSONObject(i);
                                geometry = jsonobject.getJSONObject("geometry");
                                viewport = geometry.getJSONObject("viewport");
                                northeast = viewport.getJSONObject("northeast");

                                name = jsonobject.getString("name");
                                address = jsonobject.getString("vicinity");

                                lat = Float.valueOf(northeast.getString("lat"));
                                lng = Float.valueOf(northeast.getString("lng"));

                                distance = (float) distance(currentLat, currentLng, lat, lng);
                                photos = jsonobject.getJSONArray("photos");

                                for (int j = 0; j < photos.length(); j++) {
                                    jsonobject = photos.getJSONObject(j);
                                    img = prefix + "&photoreference=" + jsonobject.getString("photo_reference");
                                }
                                img += "&type=" + SearchFragment.type + "&key=" + API_KEY;
                                PlaceOfInterest place = new PlaceOfInterest(address,lat,lng,name,img, distance);
                                db.addPlaceSearch(context, place, Constants.TABLE_NAME_SEARCH);
                                i++;
                            }
                        }
                    } catch (Exception e) {

                    }
                }
                return stringBuilder.toString();
            } finally {
                myConnection.disconnect();
            }

        } catch (Exception e) {

            return null;
        }
    }

    protected void onPostExecute(String response) {
            cursorAdapterSearch.swapCursor(db.getAllLocations(Constants.TABLE_NAME_SEARCH));

    }

    private double distance(double myLat, double myLng, double placeLat, double placeLng) {

        double radiusLat1 = Math.PI * myLat / 180;
        double radiusLat2 = Math.PI * placeLat / 180;

        double delta = myLng - placeLng;

        double radiusDelta = Math.PI * delta / 180;
        double distance = Math.sin(radiusLat1) * Math.sin(radiusLat2) + Math.cos(radiusLat1) * Math.cos(radiusLat2) * Math.cos(radiusDelta);

        if (distance > 1) {
            distance = 1;
        }

        distance = Math.acos(distance);

        distance = distance * 180 / Math.PI;
        distance = distance * 60 * 1.1515;
        distance = distance * 1.609344;

        return distance;

    }
}
