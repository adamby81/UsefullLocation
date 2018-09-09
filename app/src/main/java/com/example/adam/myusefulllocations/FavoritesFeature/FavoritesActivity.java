package com.example.adam.myusefulllocations.FavoritesFeature;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.adam.myusefulllocations.FavoritesFeature.provider.PlaceContract;
import com.example.adam.myusefulllocations.Fragment.ItemSearchFragment;
import com.example.adam.myusefulllocations.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class FavoritesActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

     //Constants:

     public static final String TAG = FavoritesActivity.class.getSimpleName();
     public static final int PERMISSIONS_REQUEST_FINE_LOCATION = 111;
     public static final int PLACE_PICKER_REQUEST = 1;

     private PlaceListAdapter mAdapter;
     private RecyclerView mRecyclerView;
     private boolean mIsEnabled;
     private GoogleApiClient mClient;


    public String address;
    public static double latitude;
    public static double longitude;
    public static double altitude;

    static LocationManager locationManager;
    static LocationListener locationListener;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            startListening();

        }

    }

    public void startListening () {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        }

    }

    public void updateLocationInfo(Location location) {

        Log.i("PlaceOfInterest Info: ", location.toString());


        // קבלת המיקום בפועל
        TextView latView;
//        TextView longView = findViewById(R.id.longitudeView_ID);
//        TextView accView = findViewById(R.id.accView_ID);
//        TextView altView = findViewById(R.id.altitudeView_ID);
//
//        latView.setText("Latitude: " + location.getLatitude());
//        longView.setText("Longitude: " + location.getLongitude());
//        accView.setText("Accuracy: " + location.getAccuracy());
//        altView.setText("Altitude: " + location.getAltitude());


        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {

            address = "Could not find any address";
            List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1);

            if (listAddresses != null && listAddresses.size() >0) {
                Log.i("Address Info: ", listAddresses.get(0).toString());

                address = "Address: " + "\n";
                if (listAddresses.get(0).getSubThoroughfare() != null) {
                    // רחוב
                    address += listAddresses.get(0).getSubThoroughfare() + " ";
                }

                if (listAddresses.get(0).getThoroughfare() != null) {
                    // מספר בית
                    address += listAddresses.get(0).getThoroughfare() + "\n";

                }

                if (listAddresses.get(0).getLocality() != null) {
                    // עיר
                    address += listAddresses.get(0).getLocality() + ", ";
                }
                if (listAddresses.get(0).getPostalCode() != null) {
                    // תיבת דואר
                    address += listAddresses.get(0).getPostalCode() + "\n";

                }

                if (listAddresses.get(0).getCountryName() != null) {
                    // מדינה
                    address += listAddresses.get(0).getCountryName() + "\n";

                }

            }

            latitude = location.getLatitude();
            longitude =  location.getLongitude();
            altitude =  location.getAltitude();

            Log.e("location: ", "updateLocationInfo: " + latitude + " AND " + location.getLatitude());



//            currLocationEditor.putLong("Latitude", latitude );
//            currLocationEditor.putLong("Longitude", longitude );
//            currLocationEditor.putLong("Altitude", altitude );
//            currLocationEditor.putString("address", address);

            // מיקום בפועל
            // TextView addView = findViewById(R.id.addressView_ID);

            // addView.setText(address);

        } catch (IOException e) {

            e.printStackTrace();

        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // setting up location listener and manager:
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            //v-1 testing changes on git
            @Override
            public void onLocationChanged(Location location) {

                updateLocationInfo(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        //setting up permissions:


        if (Build.VERSION.SDK_INT > 23) {

            if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                    != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startListening();


        } else {

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(this, new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {

                locationManager.requestLocationUpdates(LocationManager
                        .GPS_PROVIDER, 0, 0, locationListener);
                Location location  = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {

                    updateLocationInfo(location);
                }

            }

        }

        // set up the recycler view:
        mRecyclerView = findViewById(R.id.place_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new PlaceListAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);

            mClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .enableAutoManage(this, this)
                    .build();



    }

     @Override
     public void onConnected(@Nullable Bundle connectionHint) {

        refreshPlacesData();
        Log.i(TAG, "API Client Connection Successful");
     }

    @Override
     public void onConnectionSuspended(int cause) {
         Log.i(TAG, "API Client Connection Suspended");


     }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        //TODO: SETUP A NOTIFICATION
        Log.i(TAG, "API Client Connection Failed");

    }

    public void refreshPlacesData() {

        Uri uri = PlaceContract.PlaceEntry.CONTENT_URI;
        Cursor data = getContentResolver().query(
                uri, null,null,null,null);

        if (data == null || data.getCount() == 0) return;
        List<String> guids = new ArrayList<>();

        while (data.moveToNext()) {

            guids.add(data.getString(data.getColumnIndex(PlaceContract.PlaceEntry.COLUMNS_PLACE_ID)));

        }
        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mClient,
                guids.toArray(new String[guids.size()]));
        placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(@NonNull PlaceBuffer places) {

                mAdapter.swapPlaces(places);


            }
        });

      }

      protected  void onActivityResult(int requestCode, int resultCode, Intent data){

        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {

            Place place = PlacePicker.getPlace(this, data);
            if (place == null){

                Log.i(TAG, "No Place Selected");
                return;

            }

            String placeID = place.getId();

            ContentValues contentValues = new ContentValues();
            contentValues.put(PlaceContract.PlaceEntry.COLUMNS_PLACE_ID, placeID);
            getContentResolver().insert(PlaceContract.PlaceEntry.CONTENT_URI, contentValues);

            refreshPlacesData();

        }

      }

    public void onAddPlaceButtonClicked(View view) {


        ItemSearchFragment fragment = new ItemSearchFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_main, fragment)
                .commit();

//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            Toast.makeText(this, "Enable Location Permission", Toast.LENGTH_LONG).show();
//            return;
//        }
//        try {
//
//            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//            Intent i = builder.build(this);
//            startActivityForResult(i, PLACE_PICKER_REQUEST );
//
//
//
//        } catch (GooglePlayServicesNotAvailableException e) {
//            Log.e(TAG, String.format("GooglePlayServices Not Available: [%s]", e.getMessage()));
//        } catch (GooglePlayServicesRepairableException e) {
//            Log.e(TAG, String.format("GooglePlayServicesRepairable Not Available: [%s]", e.getMessage()));
//        } catch (Exception e) {
//            Log.e(TAG, String.format("PlacePicker Exception: %s", e.getMessage()));
//        }

    }
}
