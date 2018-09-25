package com.example.adam.myusefulllocations.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.adam.myusefulllocations.Constant.Constants;
import com.example.adam.myusefulllocations.Data.DatabaseHandler;
import com.example.adam.myusefulllocations.Fragment.ItemSearchFragment;
import com.example.adam.myusefulllocations.Fragment.MapsFragment;
import com.example.adam.myusefulllocations.R;
import com.example.adam.myusefulllocations.Util.Global;
import com.example.adam.myusefulllocations.Util.PlaceOfInterest;
import com.example.adam.myusefulllocations.Util.PowerConnectionReceiver;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import static com.example.adam.myusefulllocations.Fragment.ItemSearchFragment.MY_PREFS;

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

    private android.support.v7.app.AlertDialog.Builder dialogBuilder;
    private android.support.v7.app.AlertDialog dialog;
    RadioButton isKm;
    RadioButton isMiles;
    public SharedPreferences mPrefs;



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

        Global global = new Global(MainActivity.this);

        if (!global.isNetworkConnected()) {
            AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
            alertDialogBuilder.setTitle("No Internet Connection Identified");
            alertDialogBuilder.setMessage("App will work in Offline mode");
            alertDialogBuilder.setPositiveButton("I Understand", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }
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

            dialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
            View view = getLayoutInflater().inflate(R.layout.settings_popup, null);

            Button save = view.findViewById(R.id.saveBtn_POP_ID);

            dialogBuilder.setView(view);
            dialog = dialogBuilder.create();
            dialog.show();

            isKm = view.findViewById(R.id.km_RB_ID);
            isMiles = view.findViewById(R.id.miles_RB_ID);

            mPrefs = getSharedPreferences(MY_PREFS, MODE_PRIVATE);

            boolean isKM = mPrefs.getBoolean("isKm", true);
            if (isKM) {
                isKm.isChecked();
            }else{

                isMiles.isChecked();
            }

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isKm.isChecked()){

                        mPrefs = getSharedPreferences(MY_PREFS,0);
                        SharedPreferences.Editor editor = mPrefs.edit();
                        editor.putBoolean("isKM", true);
                        editor.apply();
                        editor.commit();

                    }else{

                        if (isMiles.isChecked()){
                            mPrefs = getSharedPreferences(MY_PREFS,0);
                            SharedPreferences.Editor editor = mPrefs.edit();
                            editor.putBoolean("isKM", false);
                            editor.apply();
                            editor.commit();

                        }else{

                            Toast.makeText(MainActivity.this, R.string.settings_popup,
                                    Toast.LENGTH_LONG).show();
                        }
                    }



                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                        }
                    }, 1000); // = 1 second

                    dialog.dismiss();


                }
            });


        }

        if (id == R.id.action_delete){

            dialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
            View view = getLayoutInflater().inflate(R.layout.confermation_dialog_search, null);

            dialogBuilder.setView(view);
            dialog = dialogBuilder.create();
            dialog.show();

            Button delete = view.findViewById(R.id.deleteBtn_search_ID);
            Button cancel = view.findViewById(R.id.cancelBtn_search_ID);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DatabaseHandler databaseHandler = new DatabaseHandler(MainActivity.this, Constants.TABLE_NAME_SEARCH, null, Constants.SEARCH_DB_VERSION);
                    databaseHandler.deleteFavoriteshLocationTable(Constants.TABLE_NAME_SEARCH);

                    dialog.dismiss();

                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });


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

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
