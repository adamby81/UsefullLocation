package com.example.adam.myusefulllocations.Util;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.adam.myusefulllocations.Activity.DataPassListener;
import com.example.adam.myusefulllocations.Data.SearchDatabaseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.ContentValues.TAG;

public class SearchJsonAsyncTask extends AsyncTask <Void, Void, String> {

    public double myDistance;
    double currentLat;
    double currentLng;
    String address;
    double lat;
    double lng;
    private DataPassListener dataPassListener;


    private String apiRequest = "";
    private Context context;

    public String getApiRequest() {
        return apiRequest;
    }

    public void setApiRequest(String apiRequest) {
        this.apiRequest = apiRequest;
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
    public RecyclerView adapter;
    private final String API_KEY = "AIzaSyDQEqDOPsDKZKyAqYGBbewEVd-I3PY-SVM";

    public String api = "&key=AIzaSyDQEqDOPsDKZKyAqYGBbewEVd-I3PY-SVM";

    @Override
    protected void onPreExecute() {

        db = new SearchDatabaseHandler(this.context);
        dataPassListener.passDataMyLocation(currentLat, currentLng, address );

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

    }

    StringBuilder stringBuilder;
    HttpURLConnection myConnection;



    @Override
    protected String doInBackground(Void... urls) {

        URL url = null;
        try {
            url = new URL("https://maps.googleapis.com/maps/api/place/textsearch/json?query =" +  this.getApiRequest() + "&key=" + API_KEY);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {

            myConnection = (HttpURLConnection) url.openConnection();
            myConnection.setRequestMethod("GET");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(myConnection.getInputStream()));

            stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {

                stringBuilder.append(line).append("\n");
            }
            bufferedReader.close();

        }catch (IOException e) {
            e.printStackTrace();
        }


            String name, fullAddress;
            String placePhoto = "";
            String urlPhotoRef = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400";


            JSONObject jsonObject, geometry, location;
            JSONArray photos;

            if (stringBuilder == null) {

            //no result was found

            }try {

                JSONObject json = new JSONObject(stringBuilder.toString());
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

                            if (photos.length() >0)
                             {
                                jsonObject = photos.getJSONObject(0);
                                placePhoto = urlPhotoRef + "&photoreference=" + jsonObject.getString("photo_reference") + api;
                                placePhoto +=  "&key="+API_KEY;
                            }

                            PlaceOfInterest placeOfInterest = new PlaceOfInterest(fullAddress, lat, lng, name, placePhoto, distance(currentLat, currentLng, lat, lng));


                            db.addPlace(placeOfInterest);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
        return stringBuilder.toString();
    }



    private double distance (double myLat, double myLng, double placeLat, double placeLng) {

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
