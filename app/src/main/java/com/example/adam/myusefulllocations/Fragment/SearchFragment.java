package com.example.adam.myusefulllocations.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.adam.myusefulllocations.Activity.DataPassListener;
import com.example.adam.myusefulllocations.Activity.MainActivity;
import com.example.adam.myusefulllocations.Constant.Constants;
import com.example.adam.myusefulllocations.Data.DatabaseHandler;
import com.example.adam.myusefulllocations.R;
import com.example.adam.myusefulllocations.Util.AsyncTaskNearby;
import com.example.adam.myusefulllocations.Util.AsyncTaskSearch;
import com.example.adam.myusefulllocations.Util.CursorAdapterSearch;
import com.example.adam.myusefulllocations.Util.Global;
import com.example.adam.myusefulllocations.Util.PlaceOfInterest;
import com.google.android.gms.location.FusedLocationProviderClient;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class SearchFragment extends Fragment implements LocationListener {

    public static boolean fromSearchFrag;

    private static final String ARG_COLUMN_COUNT = "column-count";
    public static final String MY_PREFS = "MyPrefsFile";
    public SharedPreferences mPrefs;
    public boolean firstTime;
    Activity activity;
    Location mLocation;

    public static String type;
    public String placeNameToast;

    LocationManager locationManager;

    public CursorAdapterSearch cursorAdapterSearch;
    private ListView listViewSearch;
    private DatabaseHandler db;

    private EditText search;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    private Cursor cursor;
    private FusedLocationProviderClient fusedLocationProviderClient;

    DataPassListener dataPassListener;

    private Button startUsingBtn;


    private AsyncTaskSearch asyncTaskSearch;
    private AsyncTaskNearby asyncTaskNearby;

    private Button searchByText;
    private Button searchNearby;

    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */


    public SearchFragment() {
    }


    public SearchFragment newInstance(int columnCount) {
        SearchFragment fragment = new SearchFragment();
        // get current Location from MainActivity:
        Bundle bundle = getArguments();
        float latitude = bundle.getFloat("lat");
        float longitude = bundle.getFloat("lng");
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fromSearchFrag = false;

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if (item.getTitle() == getString(R.string.share)) {

            Cursor c = db.getPlaceSearch(Constants.TABLE_NAME_SEARCH, (int) info.id);
            c.moveToFirst();
            String name = c.getString(c.getColumnIndex(Constants.KEY_SEARCH_LOCATION_NAME));
            float latitude = c.getFloat(c.getColumnIndex(Constants.KEY_SEARCH_LOCATION_LATITUDE));
            float longitude = c.getFloat(c.getColumnIndex(Constants.KEY_SEARCH_LOCATION_LONGITUDE));

            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_SUBJECT, name);
            share.putExtra(Intent.EXTRA_TEXT, "https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude);

            startActivity(Intent.createChooser(share, "Share Via"));

        } else if (item.getTitle() == getString(R.string.add_to_fav)) {
            Cursor c = db.getPlaceSearch(Constants.TABLE_NAME_SEARCH, (int) info.id);
            c.moveToFirst();

            String name = c.getString(c.getColumnIndex(Constants.KEY_SEARCH_LOCATION_NAME));
            String address = c.getString(c.getColumnIndex(Constants.KEY_SEARCH_LOCATION_ADDRESS));
            float distance = c.getFloat(c.getColumnIndex(Constants.KEY_SEARCH_LOCATION_DISTANCE));
            float latitude = c.getFloat(c.getColumnIndex(Constants.KEY_SEARCH_LOCATION_LATITUDE));
            float longitude = c.getFloat(c.getColumnIndex(Constants.KEY_SEARCH_LOCATION_LONGITUDE));
            String image = c.getString(c.getColumnIndex(Constants.KEY_SEARCH_LOCATION_IMAGE));

            PlaceOfInterest place = new PlaceOfInterest(address, latitude, longitude, name, image, distance);
            db.addPlaceFavorites(activity, place, Constants.TABLE_NAME_FAV);

            Toast.makeText(activity, getString(R.string.place_added_to_fav), Toast.LENGTH_SHORT).show();
        } else {
            return false;
        }
        return true;
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(getString(R.string.select_action));

        menu.add(0, v.getId(), 0, getString(R.string.share));

        menu.add(0, v.getId(), 0, getString(R.string.add_to_fav));

    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_search, container, false);
        activity = getActivity();
        dataPassListener = (DataPassListener) activity;
        db = new DatabaseHandler(getActivity(), Constants.DB_NAME, null, Constants.SEARCH_DB_VERSION);

        listViewSearch = view.findViewById(R.id.search_lv_holder_ID);
        searchByText = view.findViewById(R.id.search_lv_by_Text_ID);
        searchNearby = view.findViewById(R.id.search_lv_nearby_ID);
        search = view.findViewById(R.id.search_lv_Bar_ID);

        cursor = db.getAllLocations(Constants.TABLE_NAME_SEARCH);
        cursor.moveToFirst();

        cursorAdapterSearch = new CursorAdapterSearch(getContext(), cursor);
        listViewSearch.setAdapter(cursorAdapterSearch);

        cursorAdapterSearch.swapCursor(db.getAllLocations(Constants.TABLE_NAME_SEARCH));
        registerForContextMenu(listViewSearch);
        Global global = new Global(getActivity());

        if (!global.isNetworkConnected()) {
            Toast.makeText(activity, R.string.off_line, Toast.LENGTH_SHORT).show();

        } else {

            searchByText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String textToSearch = search.getText().toString().trim().replace(" ", "%20");

                    if (!(textToSearch.length() <= 0)) {

                        db.deleteSearchLocationTable(Constants.TABLE_NAME_SEARCH);
                        asyncTaskSearch = new AsyncTaskSearch();

                        try {

                            asyncTaskSearch.setContext(getActivity());
                            asyncTaskSearch.setSearchText(textToSearch);
                            asyncTaskSearch.currentLat = MainActivity.latitude;
                            asyncTaskSearch.currentLng = MainActivity.longitude;
                            asyncTaskSearch.cursorAdapterSearch = cursorAdapterSearch;

                            asyncTaskSearch.execute();
                        } catch (Exception e) {

                        }
                        cursorAdapterSearch.swapCursor(db.getAllLocations(Constants.TABLE_NAME_SEARCH));

                        MainActivity.hideKeyboard(getActivity());

                    }


                }
            });
        }
        searchNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity.hideKeyboard(getActivity());

                mPrefs = getActivity().getSharedPreferences(MY_PREFS, 0);

                MainActivity.nearbyRadius = mPrefs.getString("radius", "2000");

                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                MainActivity.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


                dialogBuilder = new AlertDialog.Builder(getContext());
                View view = getLayoutInflater().inflate(R.layout.popup_nearby, null);

                CardView vAtmOnClick;
                CardView vRestaurantOnClick;
                CardView vGymOnClick;
                CardView vHospitalOnClick;
                CardView vPharmacyOnClick;
                CardView vSupermarketOnClick;
                CardView vGasOnClick;
                CardView vMallOnClick;
                CardView vMovieOnClick;
                CardView vCafeOnClick;

                CardView vBankOnClick;
                CardView vBeautySalonOnClick;
                CardView vBusStationOnClick;
                CardView vBarOnClick;
                CardView vCampgroundOnClick;
                CardView vPoliceOnClick;
                CardView vParkingOnClick;
                CardView vZooOnClick;
                CardView vFireStationOnClick;
                CardView vSynagogueOnClick;


                vAtmOnClick = view.findViewById(R.id.pop_nearby_cv_atm_ID);
                vRestaurantOnClick = view.findViewById(R.id.pop_nearby_cv_restaurant_ID);
                vGymOnClick = view.findViewById(R.id.pop_nearby_cv_gym_ID);
                vHospitalOnClick = view.findViewById(R.id.pop_nearby_cv_hospital_ID);
                vPharmacyOnClick = view.findViewById(R.id.pop_nearby_cv_pharmacy_ID);
                vSupermarketOnClick = view.findViewById(R.id.pop_nearby_cv_supermarket_ID);
                vGasOnClick = view.findViewById(R.id.pop_nearby_cv_gas_ID);
                vMallOnClick = view.findViewById(R.id.pop_nearby_cv_mall_ID);
                vMovieOnClick = view.findViewById(R.id.pop_nearby_cv_movie_ID);
                vCafeOnClick = view.findViewById(R.id.pop_nearby_cv_cafe_ID);
                vBankOnClick = view.findViewById(R.id.pop_nearby_cv_bank_ID);
                vBeautySalonOnClick = view.findViewById(R.id.pop_nearby_cv_beauty_salon_ID);
                vBusStationOnClick = view.findViewById(R.id.pop_nearby_cv_bus_station_ID);
                vBarOnClick = view.findViewById(R.id.pop_nearby_cv_bar_ID);
                vCampgroundOnClick = view.findViewById(R.id.pop_nearby_cv_campground_ID);
                vPoliceOnClick = view.findViewById(R.id.pop_nearby_cv_police_ID);
                vParkingOnClick = view.findViewById(R.id.pop_nearby_cv_parking_ID);
                vZooOnClick = view.findViewById(R.id.pop_nearby_cv_zoo_ID);
                vFireStationOnClick = view.findViewById(R.id.pop_nearby_cv_fire_station_ID);
                vSynagogueOnClick = view.findViewById(R.id.pop_nearby_cv_synagogue_ID);




                dialogBuilder.setView(view);
                dialog = dialogBuilder.create();
                dialog.show();

                vAtmOnClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        type = "atm";
                        placeNameToast = getString(R.string.atm);
                        startSearch();
                    }
                });
                vRestaurantOnClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        type = "restaurant";
                        placeNameToast = getString(R.string.restaurant);
                        startSearch();
                    }
                });
                vGymOnClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        type = "gym";
                        placeNameToast = getString(R.string.gym);
                        startSearch();
                    }
                });
                vHospitalOnClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        type = "hospital";
                        placeNameToast = getString(R.string.hospital);
                        startSearch();
                    }
                });
                vPharmacyOnClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        type = "pharmacy";
                        placeNameToast = getString(R.string.pharmacy);
                        startSearch();
                    }
                });
                vSupermarketOnClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        type = "supermarket";
                        placeNameToast = getString(R.string.supermarket);
                        startSearch();
                    }
                });
                vGasOnClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        type = "gas_station";
                        placeNameToast = getString(R.string.gas);
                        startSearch();
                    }
                });
                vMallOnClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        type = "shopping_mall";
                        placeNameToast = getString(R.string.mall);
                        startSearch();
                    }
                });
                vMovieOnClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        type = "movie_theater";
                        placeNameToast = getString(R.string.movie);
                        startSearch();
                    }
                });
                vCafeOnClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        type = "cafe";
                        placeNameToast = getString(R.string.cafe);
                        startSearch();
                    }
                });

                vBankOnClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        type = "bank";
                        placeNameToast = getString(R.string.bank);
                        startSearch();
                    }
                });
                vBeautySalonOnClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        type = "beauty_salon";
                        placeNameToast = getString(R.string.beauty_salon);
                        startSearch();
                    }
                });
                vBusStationOnClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        type = "bus_station";
                        placeNameToast = getString(R.string.bus_station);
                        startSearch();
                    }
                });
                vBarOnClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        type = "bar";
                        placeNameToast = getString(R.string.bar);
                        startSearch();
                    }
                });
                vCampgroundOnClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        type = "campground";
                        placeNameToast = getString(R.string.campground);
                        startSearch();
                    }
                });
                vPoliceOnClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        type = "police";
                        placeNameToast = getString(R.string.police);
                        startSearch();
                    }
                });
                vParkingOnClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        type = "parking";
                        placeNameToast = getString(R.string.parking);
                        startSearch();
                    }
                });
                vZooOnClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        type = "zoo";
                        placeNameToast = getString(R.string.zoo);
                        startSearch();
                    }
                });
                vFireStationOnClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        type = "fire_station";
                        placeNameToast = getString(R.string.fire_station);
                        startSearch();
                    }
                });
                vSynagogueOnClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        type = "synagogue";
                        placeNameToast = getString(R.string.synagogue);
                        startSearch();
                    }
                });
            }
        });

        listViewSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                fromSearchFrag = true;
                cursor = (Cursor) parent.getAdapter().getItem(position);
                double lat = cursor.getFloat(cursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_LATITUDE));
                double lng = cursor.getFloat(cursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_LONGITUDE));
                String name = cursor.getString(cursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_NAME));

                dataPassListener.passDataLocationToMap(lat, lng, name);
            }
        });


        return view;
    }

    public void startSearch() {



        Toast.makeText(getContext(), getString(R.string.nearby_chsen_type) + placeNameToast, Toast.LENGTH_LONG).show();


        db.deleteSearchLocationTable(Constants.TABLE_NAME_SEARCH);
        asyncTaskNearby = new AsyncTaskNearby();

        try {
            asyncTaskNearby.setContext(getActivity());
            asyncTaskNearby.currentLat = MainActivity.latitude;
            asyncTaskNearby.currentLng = MainActivity.longitude;
            asyncTaskNearby.cursorAdapterSearch = cursorAdapterSearch;
            asyncTaskNearby.execute();

        } catch (Exception e) {
        }

        MainActivity.hideKeyboard(getActivity());
        dialog.dismiss();

        cursorAdapterSearch.swapCursor(db.getAllLocations(Constants.TABLE_NAME_SEARCH));

    }
    @Override
    public void onLocationChanged(Location location) {
        MainActivity.updateLocationInfo(location);
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

    public void welcomePopup() {

        dialogBuilder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.popup_welcome, null);

        startUsingBtn = view.findViewById(R.id.startUsingBtn_POP_ID);


        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();

        MainActivity.isKmSettings = view.findViewById(R.id.km_RB_ID);
        MainActivity.isMilesSettings = view.findViewById(R.id.miles_RB_ID);

        startUsingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPrefs = getActivity().getSharedPreferences(MY_PREFS, 0);
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putBoolean("ifFirstTime", false);
                editor.putString("radius", "2000");
                MainActivity.nearbyRadius = "2000";

                if (MainActivity.isKmSettings.isChecked()) {

                    editor.putBoolean("isKM", true);

                } else {

                    editor.putBoolean("isKM", false);

                }

                editor.apply();
                editor.commit();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    }
                }, 1000); // = 1 second

                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
                dialog.dismiss();


            }
        });
    }


    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            SharedPreferences prefs = getActivity().getSharedPreferences(MY_PREFS, 0);
            if (prefs.contains("ifFirstTime")) {

                firstTime = prefs.getBoolean("ifFirstTime", true);
            } else {
                welcomePopup();
            }
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(SearchFragment item);

        // TODO: Update argument type and name
        void onListFragmentInteraction(PlaceOfInterest item);
    }

}
