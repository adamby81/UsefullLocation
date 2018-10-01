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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.adam.myusefulllocations.Constant.Constants;
import com.example.adam.myusefulllocations.Data.DatabaseHandler;
import com.example.adam.myusefulllocations.Fragment.MapsFragment;
import com.example.adam.myusefulllocations.Fragment.SearchFragment;
import com.example.adam.myusefulllocations.R;
import com.example.adam.myusefulllocations.Util.CursorAdapterSearch;
import com.example.adam.myusefulllocations.Util.Global;
import com.example.adam.myusefulllocations.Util.PlaceOfInterest;
import com.example.adam.myusefulllocations.Util.PowerConnectionReceiver;
import com.google.android.gms.maps.model.LatLng;

import static com.example.adam.myusefulllocations.Fragment.SearchFragment.MY_PREFS;
import static com.example.adam.myusefulllocations.R.id.radius_GR_ID;
import static com.example.adam.myusefulllocations.R.layout.popup_settings;

public class MainActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener
        , DataPassListener, SearchFragment.OnListFragmentInteractionListener {

    FragmentTransaction fragmentTransaction;
    FrameLayout frameLayoutSearch, frameLayoutMap;
    MapsFragment myMapFragment;

    public static String address;
    public static float latitude;
    public static float longitude;

    private android.support.v7.app.AlertDialog.Builder dialogBuilder;
    private android.support.v7.app.AlertDialog dialog;
    public static RadioButton isKmSettings;
    public static RadioButton isMilesSettings;
    public static RadioButton radius1000;
    public static RadioButton radius2000;
    public static RadioButton radius5000;
    public static RadioButton radius10000;
    public static RadioGroup radiusGroup;
    public static RadioButton radiusChoice;


    public static String nearbyRadius;


    public static SharedPreferences mPrefs;

    DatabaseHandler db;
    CursorAdapterSearch cursorAdapterSearch;

    public static int popOnceChecker = -1;
    PowerConnectionReceiver receiver;

    public LatLng latLng;

    public static LocationManager locationManager;
    public static LocationListener locationListener;
    private String name;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            startListening();

        }

    }

    public void startListening() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        } else {

            AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
            alertDialogBuilder.setTitle(getString(R.string.no_gps_connection));
            alertDialogBuilder.setMessage(getString(R.string.no_gps_connection_worning));
            alertDialogBuilder.setPositiveButton(getString(R.string.understand), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }
    }

    public static void updateLocationInfo(Location location) {
        latitude = (float) location.getLatitude();
        longitude = (float) location.getLongitude();
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
            alertDialogBuilder.setTitle(getString(R.string.no_connection));
            alertDialogBuilder.setMessage(getString(R.string.no_connection_message_offline));
            alertDialogBuilder.setPositiveButton(getString(R.string.understand), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }

        SearchFragment searchFragment = new SearchFragment();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (!(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)) {

            startListening();
            fragmentTransaction.add(R.id.fragment_container_main, searchFragment);
            fragmentTransaction.commit();
            BottomNavigationView navigation = findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(this);

        } else {

            startListening();
            frameLayoutSearch = findViewById(R.id.fragment_container_search);
            frameLayoutMap = findViewById(R.id.fragment_container_map);
            frameLayoutMap.removeAllViews();
            frameLayoutSearch.removeAllViews();

            myMapFragment = new MapsFragment();
            fragmentTransaction.add(R.id.fragment_container_search, searchFragment);
            fragmentTransaction.add(R.id.fragment_container_map, myMapFragment);
            fragmentTransaction.commit();

            Bundle bundleMapsAndSearch = new Bundle();
            bundleMapsAndSearch.putFloat("lat", latitude);
            bundleMapsAndSearch.putFloat("lng", longitude);
            bundleMapsAndSearch.putString("name", name);

            myMapFragment.setArguments(bundleMapsAndSearch);
            searchFragment.setArguments(bundleMapsAndSearch);

        }

        if (!(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)) {

        } else {

        }

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

            if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startListening();


        } else {

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {

                locationManager.requestLocationUpdates(LocationManager
                        .GPS_PROVIDER, 0, 0, locationListener);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {

                    updateLocationInfo(location);
                }

            }

        }
    }

    private boolean loadFragment(Fragment fragment) {

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
                fragment = new SearchFragment();

                Bundle bundleSearch = new Bundle();
                bundleSearch.putFloat("latitude", latitude);
                bundleSearch.putFloat("longitude", longitude);

                fragment.setArguments(bundleSearch);

                break;

            case R.id.navigation_map_ID:
                fragment = new MapsFragment();

                Bundle bundleMaps = new Bundle();
                bundleMaps.putFloat("lat", latitude);
                bundleMaps.putFloat("lng", longitude);
                bundleMaps.putString("name", address);

                fragment.setArguments(bundleMaps);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @SuppressLint("CutPasteId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {


            dialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
            final View view = getLayoutInflater().inflate(popup_settings, null);

            Button save = view.findViewById(R.id.saveBtn_POP_settings_ID);

            dialogBuilder.setView(view);
            dialog = dialogBuilder.create();
            dialog.show();


            isKmSettings = view.findViewById(R.id.km_RB_settings_ID);
            isMilesSettings = view.findViewById(R.id.miles_RB_settings_ID);

            mPrefs = getSharedPreferences(MY_PREFS, MODE_PRIVATE);


            radius1000 = view.findViewById(R.id.radius1000_GR_ID);
            radius2000 = view.findViewById(R.id.radius2000_GR_ID);
            radius5000 = view.findViewById(R.id.radius5000_GR_ID);
            radius10000 = view.findViewById(R.id.radius10000_GR_ID);
            radiusGroup = view.findViewById(radius_GR_ID);


            boolean isKM = mPrefs.getBoolean("isKM", true);

            if (isKM) {
                isKmSettings.setChecked(true);
                isMilesSettings.setChecked(false);
                isKmSettings.isChecked();

            }else{
                isKmSettings.setChecked(false);
                isMilesSettings.setChecked(true);
                isMilesSettings.isChecked();
            }

            String radiusPrefs = mPrefs.getString("radius", "2000");

            switch (radiusPrefs) {
                case "1000":
                    radius1000.setChecked(true);
                    radius2000.setChecked(false);
                    radius5000.setChecked(false);
                    radius10000.setChecked(false);
                    break;
                case "2000":
                    radius1000.setChecked(false);
                    radius2000.setChecked(true);
                    radius5000.setChecked(false);
                    radius10000.setChecked(false);
                    break;
                case "5000":
                    radius1000.setChecked(false);
                    radius2000.setChecked(false);
                    radius5000.setChecked(true);
                    radius10000.setChecked(false);
                    break;
                case "10000":
                    radius1000.setChecked(false);
                    radius2000.setChecked(false);
                    radius5000.setChecked(false);
                    radius10000.setChecked(true);
                    break;

            }


            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isKmSettings.isChecked()){

                        SharedPreferences.Editor editor = mPrefs.edit();
                        editor.putBoolean("isKM", true);
                        editor.apply();
                        editor.commit();


                    }else{

                        if (isMilesSettings.isChecked()){
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

                    db = new DatabaseHandler(MainActivity.this, Constants.DB_NAME, null, Constants.SEARCH_DB_VERSION);
                    db.deleteSearchLocationTable(Constants.TABLE_NAME_SEARCH);

                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
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

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        View view = activity.getCurrentFocus();

        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    @Override
    public void onListFragmentInteraction(SearchFragment item) {

    }

    @Override
    public void passDataLocationToMap(float lat, float lng, String name) {

         myMapFragment = new MapsFragment();

        Bundle bundleMaps = new Bundle();

        bundleMaps.putFloat("lat", lat);
        bundleMaps.putFloat("lng", lng);
        bundleMaps.putString("name", name);

        myMapFragment.setArguments(bundleMaps);

        if (!(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)) {

            startListening();
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container_main, myMapFragment).addToBackStack(null);
            fragmentTransaction.commit();

        } else {

            startListening();
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container_map, myMapFragment).addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onListFragmentInteraction(PlaceOfInterest item) {

    }

    public void onRadiusChooseClick(View view) {

        int checked = radiusGroup.getCheckedRadioButtonId();

        radiusChoice = findViewById(checked);

        mPrefs = getSharedPreferences(MY_PREFS, 0);
        SharedPreferences.Editor editor = mPrefs.edit();

        switch(checked) {
            case R.id.radius1000_GR_ID:
                    nearbyRadius = "1000";
                    editor.putString("radius", "1000");
                editor.apply();
                editor.commit();
                break;
            case R.id.radius2000_GR_ID:
                    nearbyRadius = "2000";
                editor.putString("radius", "2000");
                editor.apply();
                editor.commit();
                    break;
            case R.id.radius5000_GR_ID:
                    nearbyRadius = "5000";
                editor.putString("radius", "5000");
                editor.apply();
                editor.commit();
                break;
            case R.id.radius10000_GR_ID:
                    nearbyRadius = "10000";
                editor.putString("radius", "10000");
                editor.apply();
                editor.commit();

                break;
        }
    }
}
