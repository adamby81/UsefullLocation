package com.example.adam.myusefulllocations.Util;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.example.adam.myusefulllocations.Constant.Constants;
import com.example.adam.myusefulllocations.Data.DatabaseHandler;
import com.example.adam.myusefulllocations.R;
import com.google.android.gms.location.FusedLocationProviderClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class SearchJsonAsyncTask extends AsyncTask<Void, Void, String> {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    BufferedReader bufferedReader;
    public float currentLat;
    public float currentLng;
    LocationsCursorAdapter locationsCursorAdapter;
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
        int titleProg = (R.string.searching);
        int messageProg = R.string.searchingMessage;
        db = new DatabaseHandler(this.context, Constants.TABLE_NAME_SEARCH,null, Constants.SEARCH_DB_VERSION);
        progressDialog = ProgressDialog.show(context, Integer.toString(titleProg), Integer.toString(messageProg), true);
    }

    @Override
    protected void onPostExecute(String response) {

            //locationsCursorAdapter.swapCursor(db.getAllLocations(Constants.TABLE_NAME_SEARCH));

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
                bufferedReader = new BufferedReader(new InputStreamReader(myConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder(); //Build the response
                String line;
                int count = 0;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                    count = count +1;
                    //Log.i("String Builder: ", "Count: " + count +  stringBuilder.append(line).append("\n"));



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
     Log.i( "IN THE JSON: ","onPostExecute: " + jArray.length());
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

                        distance = distance(currentLat, currentLng, lat, lon);

                        photos = jsonobject.getJSONArray("photos");
                        for (int j = 0; j < photos.length(); j++) {
                            jsonobject = photos.getJSONObject(j);
                            img = prefix + "&photoreference=" + jsonobject.getString("photo_reference");
                        }
                        img += "&key=" + API_KEY;
                        PlaceOfInterest place = new PlaceOfInterest(address,lat,lon,name,img, distance);
                        db.addPlaceSearch(context, place, Constants.TABLE_NAME_SEARCH); //Add to the downloaded list table

                        Log.i("Row Count - JSON: ", " Row Num: " + i);


                    }
                    Log.i("JSON LENGTH: ", Integer.toString(jsonLen));
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

    private float distance(float myLat, float myLng, float placeLat, float placeLng) {

        float radiusMyLat = (float) (Math.PI * myLat / 180);
        float radiusPlaceLat = (float) (Math.PI * placeLat / 180);
        float delta = myLng - placeLng;
        float radiusDelta = (float) (Math.PI * delta / 180);

        float fixedDistance = (float) (Math.sin(radiusMyLat) * Math.sin(radiusPlaceLat)
                        + Math.cos(radiusMyLat) * Math.cos(radiusPlaceLat)
                        * Math.cos(radiusDelta));
        if (fixedDistance > 1) {

            fixedDistance = 1;

        }

        fixedDistance = (float) Math.acos(fixedDistance);
        fixedDistance = (float) (fixedDistance * 180 / Math.PI);
        fixedDistance = (float) (fixedDistance * 60 * 1.1515);
        fixedDistance = (float) 1.609334;
        return fixedDistance;


    }

//    private void getCurrentLocation(Context context) {
//        try {
//            // Ask for permissions if they have not been given
//            if ((ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
//                Task location = fusedLocationProviderClient.getLastLocation();
//                location.addOnCompleteListener((Activity) context, new OnCompleteListener() {
//                    @Override
//                    public void onComplete(@NonNull Task task) {
//                        if (task.isSuccessful() && task.getResult() != null) {
//                            mLocation = (Location) task.getResult();
//                            lat = (float) mLocation.getLatitude();
//                            lon = (float) mLocation.getLongitude();
////                            Log.e(TAG, String.format("getCurrentLocation(%f, %f)", mLocation.getLatitude(), mLocation.getLongitude()));
//                        } else {
//                            Toast.makeText(getContext(), "Failed to get current location", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                });
//            } else {
//                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
//                        Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "getCurrentLocation: " + e.getMessage());
//        }
//    }




}
