package com.example.adam.myusefulllocations.Fragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.example.adam.myusefulllocations.Activity.DataPassListener;
import com.example.adam.myusefulllocations.Activity.MainActivity;
import com.example.adam.myusefulllocations.Constant.Constants;
import com.example.adam.myusefulllocations.Data.DatabaseHandler;
import com.example.adam.myusefulllocations.R;
import com.example.adam.myusefulllocations.Util.Global;
import com.example.adam.myusefulllocations.Util.LocationsCursorAdapter;
import com.example.adam.myusefulllocations.Util.PlaceOfInterest;
import com.example.adam.myusefulllocations.Util.SearchJsonAsyncTask;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ItemSearchFragment extends Fragment implements LocationListener {

    // TODO: Customize parameter argument names
    private static final String TAG = "SearchFragment";
    public static boolean fromSearchFrag;

    private static final String ARG_COLUMN_COUNT = "column-count";
    public static final String MY_PREFS = "MyPrefsFile";
    public SharedPreferences mPrefs;
    public boolean firstTime;
    RequestQueue requestQueue;


    public LocationsCursorAdapter locationsCursorAdapter;
//    private String[] querySearchList;
    private ListView listViewSearch;
    RadioButton isKm;
    MapsFragment mapsFragment;


//    private List<PlaceOfInterest> placeOfInterestList;
//    private List<PlaceOfInterest> listPlaceOfInterests;

    private DatabaseHandler db;

    private SearchView search;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    private Cursor cursor;
    private FusedLocationProviderClient fusedLocationProviderClient;

    DataPassListener dataPassListener;
    // popup

    private TextView aboutUs;
    private Button startUsingBtn;


    private SearchJsonAsyncTask searchJsonAsyncTask;

    private Button searchByText;
    private Button searchNearby;

//    private CursorRecyclerViewAdapter adapter;





    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;



    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */


    public ItemSearchFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public ItemSearchFragment newInstance(int columnCount) {
        ItemSearchFragment fragment = new ItemSearchFragment();
        // get current Location from MainActivity:
            Bundle bundle =  getArguments();
            float latitude = bundle.getFloat("latitude");
            float longitude = bundle.getFloat("longitude");
        fragment.setArguments(bundle);
        return fragment;
    }
//
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fromSearchFrag = false;


        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_lv_search, container, false);

        db = new DatabaseHandler(getActivity(), Constants.SEARCH_DB_NAME, null, Constants.SEARCH_DB_VERSION);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());


        listViewSearch = view.findViewById(R.id.search_lv_holder_ID);

        searchByText = view.findViewById(R.id.search_lv_by_Text_ID);
        searchNearby = view.findViewById(R.id.search_lv_nearby_ID);

        search = view.findViewById(R.id.search_lv_Bar_ID);

        cursor = db.getAllLocations(Constants.TABLE_NAME_SEARCH);
        cursor.moveToFirst();

        locationsCursorAdapter = new LocationsCursorAdapter(getActivity(), cursor);
        listViewSearch.setAdapter(locationsCursorAdapter);

        locationsCursorAdapter.swapCursor(db.getAllLocations(Constants.TABLE_NAME_SEARCH));
        registerForContextMenu(listViewSearch);
        Global global = new Global(getActivity());


        search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                // responsible for the user focus on the search view, keyboard...

            }
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length() >= 2){

                    listViewSearch.setVisibility(View.VISIBLE);
                    searchJsonAsyncTask = new SearchJsonAsyncTask();

                    try {

                        searchJsonAsyncTask.setContext(getActivity());
                        searchJsonAsyncTask.setSearchText(newText);
                        searchJsonAsyncTask.currentLat=MainActivity.latitude;
                        searchJsonAsyncTask.currentLng=MainActivity.longitude;
                        searchJsonAsyncTask.execute();

                    }catch (Exception e) {
                        Log.e(TAG, "onClick: " + e.getMessage());
                    }
                }else{

                    listViewSearch.setVisibility(View.INVISIBLE);

                }


                return false;
            }
        });

        if (global.isNetworkConnected()){

            //TODO - ADD NOTIFICATION ABOUT THE CONNECTION

        }else {
        }

//
//            searchByText.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                        // get current location function placed here
//                        String textToSearch = search.toString().trim().replace(" ", "%20");
//
//                    if (!(search.toString().trim().length() <= 0)) {
//
//                        db.deleteSearchLocationTable(Constants.TABLE_NAME_SEARCH);
//                        searchJsonAsyncTask = new SearchJsonAsyncTask();
//
//                        try {
//
//                                searchJsonAsyncTask.setContext(getActivity());
//                                searchJsonAsyncTask.setSearchText(textToSearch);
//                                searchJsonAsyncTask.currentLat=MainActivity.latitude;
//                                searchJsonAsyncTask.currentLng=MainActivity.longitude;
//                                searchJsonAsyncTask.execute();
//                            }catch (Exception e) {
//                                Log.e(TAG, "onClick: " + e.getMessage());
//                            }
//
//                            locationsCursorAdapter.swapCursor(db.getAllLocations(Constants.TABLE_NAME_SEARCH));
//                        }
//                }
//            });


        listViewSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private FragmentManager supportFragmentManager;

            public FragmentManager getSupportFragmentManager() {
                return supportFragmentManager;
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                fromSearchFrag = true;
                cursor = (Cursor) parent.getAdapter().getItem(position);
                float lat = cursor.getFloat(cursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_LATITUDE));
                float lng = cursor.getFloat(cursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_LONGITUDE));
                String name = cursor.getString(cursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_NAME));
                //mapsFragment.passDataMyLocation(lat, lng, name);

                mPrefs = getActivity().getSharedPreferences(MY_PREFS,0);
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putFloat("place_lat", lat);
                editor.putFloat("place_lng", lng);
                editor.putString("place_name", name);


            }
        });

                return view;
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
        View view = getLayoutInflater().inflate(R.layout.popup, null);

       aboutUs = view.findViewById(R.id.aboutTextView_POP_ID);
       startUsingBtn = view.findViewById(R.id.startUsingBtn_POP_ID);

        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();

        isKm = view.findViewById(R.id.km_RB_ID);


        startUsingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPrefs = getActivity().getSharedPreferences(MY_PREFS,0);
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putBoolean("ifFirstTime", false);

                if (isKm.isChecked()) {

                    editor.putBoolean("isKM", true);

                }else {

                    editor.putBoolean("isKM", false);

                }
                editor.apply();
                editor.commit();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    }
                }, 1000); // = 1 second

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
            }else {
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
        void onListFragmentInteraction(ItemSearchFragment item);

        // TODO: Update argument type and name
        void onListFragmentInteraction(PlaceOfInterest item);
    }
}
