package com.example.adam.myusefulllocations.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.adam.myusefulllocations.Data.CurrentLocation;
import com.example.adam.myusefulllocations.Fragment.ItemSearchFragment;
import com.example.adam.myusefulllocations.Fragment.MapsActivity;
import com.example.adam.myusefulllocations.Fragment.SearchFragment;
import com.example.adam.myusefulllocations.Fragment.dummy.DummyContent;
import com.example.adam.myusefulllocations.R;
import com.example.adam.myusefulllocations.Util.PowerConnectionReceiver;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener
        , DataPassListene, CurrentLocation, ItemSearchFragment.OnListFragmentInteractionListener {



    public String address;
    public static double latitude;
    public static double longitude;
    public static double altitude;

    public static int popOnceChecker = -1;
    PowerConnectionReceiver receiver;


        LocationManager locationManager;
        LocationListener locationListener;

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
    protected void onDestroy() {


        unregisterReceiver(receiver);


        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        loadFragment(new SearchFragment());

            // רכיב טעינה
        receiver = new PowerConnectionReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction((Intent.ACTION_BATTERY_CHANGED));
        registerReceiver(receiver, intentFilter);


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
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

        if (Build.VERSION.SDK_INT > 23) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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



    }

    private boolean loadFragment (Fragment fragment) {

        if (fragment != null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();


            return true;
        }
        return false;
    }


    @SuppressLint("ResourceType")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;

        switch (item.getItemId()) {

            case R.id.navigation_search_ID:
                fragment = new SearchFragment();
                break;
            case R.id.navigation_map_ID:
               fragment = new MapsActivity();

                Bundle bundle = new Bundle();
                bundle.putDouble("latitude", latitude);
                bundle.putDouble("longitude", longitude);
                bundle.putString("address", address);



                fragment.setArguments(bundle);
                break;
            case R.id.navigation_favorites_ID:
                loadFavoritesActivity(item);
                break;

        }

            return loadFragment(fragment);
    }

    public void loadFavoritesActivity(MenuItem item) {

        Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
        startActivity(intent);
    }

       // מיקום נוכחי



    @Override
    public void currentLocation(long lat, long lon, String currentAddress) {


//        lat = latitude;
//        lng = longitude;
//        alt = altitude;
//        currentAddress = address;



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_share){
            return true;
        }

        if (id == R.id.action_where_I_am_ID){

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Json :

    public void getLocationInfoJson () {

        //String URL = "https://maps.googleapis.com/maps/api/place/details/json?placeid&fields=name,formatted_phone_number,photo,place_id,geometry,formatted_address&key=AIzaSyDQEqDOPsDKZKyAqYGBbewEVd-I3PY-SVM";

//        OkHttpClient okHttpClient = new OkHttpClient();
//        Request request = new Request.Builder().url(URL).build();
//        okHttpClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Request request, IOException e) {
//                e.printStackTrace();
//
//            }
//
//            @Override
//            public void onResponse(Response response) throws IOException {
//
//                if (response.isRedirect()) {
//
//                    final String myResponse = response.body().string();
//                    MainActivity.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            try {
//
//                                JSONObject jsonObject = new JSONObject(myResponse);
//                                JSONObject jsonResults = jsonObject.getJSONObject("results");
//
//                                name = jsonResults.getString("name");
//                                address = jsonResults.getString("formatted_address");
//                                place_id = jsonResults.getString("place_id");
//
//                                JSONObject geometry = jsonResults.getJSONObject("geometry");
//                                JSONObject location = geometry.getJSONObject("location");
//                                placelat = location.getDouble("lat");
//                                placelon =location.getDouble("lng");
//
//
//
//                            } catch (Exception e) {
//
//
//                            }
//
//
//                        }
//                    });
//
//                }
//            }
//        });



    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }
}
