package com.example.adam.myusefulllocations.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.adam.myusefulllocations.Fragment.ItemSearchFragment;
import com.example.adam.myusefulllocations.Fragment.MapsFragment;
import com.example.adam.myusefulllocations.R;
import com.example.adam.myusefulllocations.Util.PlaceOfInterest;
import com.example.adam.myusefulllocations.Util.PowerConnectionReceiver;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener
        , DataPassListener, ItemSearchFragment.OnListFragmentInteractionListener {

    FragmentTransaction fragmentTransaction;
    FrameLayout frameLayoutSearch, frameLayoutMap;
    GoogleMap mMap;
    MapsFragment myMapFragment;
    ItemSearchFragment itemSearchFragment;

    public static String address;
    public static float latitude;
    public static float longitude;

    public static int popOnceChecker = -1;
    PowerConnectionReceiver receiver;

    public LatLng latLng;

    public static LocationManager locationManager;
       public static LocationListener locationListener;
    private String name;

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

    public static void updateLocationInfo(Location location) {
            latitude = (float) location.getLatitude();
            longitude = (float) location.getLongitude();

            Log.e("location: ", "updateLocationInfo: " + latitude + " AND " + location.getLatitude());



//            currLocationEditor.putLong("Latitude", latitude );
//            currLocationEditor.putLong("Longitude", longitude );
//            currLocationEditor.putLong("Altitude", altitude );
//            currLocationEditor.putString("name", name);

            // מיקום בפועל
            // TextView addView = findViewById(R.id.addressView_ID);

            // addView.setText(name);
    }

    @Override
    protected void onDestroy() {


        unregisterReceiver(receiver);


        super.onDestroy();
    }


    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // fragments handeling - landScape

        ItemSearchFragment itemSearchFragment = new ItemSearchFragment();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (!(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)) {

            fragmentTransaction.add(R.id.fragment_container_main, itemSearchFragment);
            fragmentTransaction.commit();
            BottomNavigationView navigation = findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(this);

        }else {

            frameLayoutSearch = findViewById(R.id.fragment_container_search);
            frameLayoutMap = findViewById(R.id.fragment_container_map);
            frameLayoutMap.removeAllViews();
            frameLayoutSearch.removeAllViews();

            myMapFragment = new MapsFragment();
            fragmentTransaction.add(R.id.fragment_container_search, itemSearchFragment);
            fragmentTransaction.add(R.id.fragment_container_map, myMapFragment);
            fragmentTransaction.commit();

            Bundle bundleMapsAndSearch = new Bundle();
            bundleMapsAndSearch.putDouble("latitude", latitude);
            bundleMapsAndSearch.putDouble("longitude", longitude);
            bundleMapsAndSearch.putString("name", address);

            myMapFragment.setArguments(bundleMapsAndSearch);
            itemSearchFragment.setArguments(bundleMapsAndSearch);

        }

        if (!(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)) {



        }else {


        }

//        BottomNavigationView navigation = findViewById(R.id.navigation);
//        navigation.setOnNavigationItemSelectedListener(this);

     //   loadFragment(new ItemSearchFragment());

            // רכיב טעינה
        receiver = new PowerConnectionReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction((Intent.ACTION_BATTERY_CHANGED));
        registerReceiver(receiver, intentFilter);


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



    }

    private boolean loadFragment (Fragment fragment) {

        if (fragment != null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_main, fragment)
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
                fragment = new ItemSearchFragment();

                Bundle bundleSearch = new Bundle();
                bundleSearch.putDouble("latitude", latitude);
                bundleSearch.putDouble("longitude", longitude);

                fragment.setArguments(bundleSearch);

                break;

            case R.id.navigation_map_ID:
               fragment = new MapsFragment();

                Bundle bundleMaps = new Bundle();
                bundleMaps.putDouble("latitude", latitude);
                bundleMaps.putDouble("longitude", longitude);
                bundleMaps.putString("name", address);

                fragment.setArguments(bundleMaps);

                break;

            case R.id.navigation_favorites_ID:

                loadFavoritesActivity(item);
//                fragment = new FavoritesFragment();
                break;

        }

            return loadFragment(fragment);
    }

    public void loadFavoritesActivity(MenuItem item) {

        Intent intent = new Intent(MainActivity.this, FavoritesLvActivity.class);
        startActivity(intent);
    }

       // מיקום נוכחי





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
//                                name = jsonResults.getString("formatted_address");
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
    public void onListFragmentInteraction(ItemSearchFragment item) {

    }

    @Override
    public void passDataMyLocation(double lat, double lng, String name) {

        this.name=name;
        latLng = new LatLng(lat, lng);
         myMapFragment = new MapsFragment();
        //Device is in Portrait
        if (!(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container_main, myMapFragment).addToBackStack(null);
            fragmentTransaction.commit();
            // If device is in landscape no need to replace fragments
        } else {

           // myMapFragment.passDataMyLocation(latitude, longitude ,name);
            Fragment fragment = new MapsFragment();

            Bundle bundleMaps = new Bundle();
            bundleMaps.putDouble("latitude", lat);
            bundleMaps.putDouble("longitude", lng);
            bundleMaps.putString("name", name);



            fragment.setArguments(bundleMaps);
        }
    }

    @Override
    public void onListFragmentInteraction(PlaceOfInterest item) {

    }
}
