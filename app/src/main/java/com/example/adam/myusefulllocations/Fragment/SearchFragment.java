package com.example.adam.myusefulllocations.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
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
import com.example.adam.myusefulllocations.Util.ImageNearbyAdapter;
import com.example.adam.myusefulllocations.Util.PlaceOfInterest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class SearchFragment extends Fragment implements LocationListener {

    // TODO: Customize parameter argument names
    private static final String TAG = "SearchFragment";
    public static boolean fromSearchFrag;

    private static final String ARG_COLUMN_COUNT = "column-count";
    public static final String MY_PREFS = "MyPrefsFile";
    public SharedPreferences mPrefs;
    public boolean firstTime;
    Activity activity;

    GridView gridView;
    public static String type;
    public String placeNameToast;


    public CursorAdapterSearch cursorAdapterSearch;
    //    private String[] querySearchList;
    private ListView listViewSearch;
    RadioButton isKm;
    RadioButton isMiles;

    private List<PlaceOfInterest> placeOfInterestList;
//    private List<PlaceOfInterest> listPlaceOfInterests;

    private DatabaseHandler db;

    private EditText search;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    private Cursor cursor;
    private FusedLocationProviderClient fusedLocationProviderClient;

    DataPassListener dataPassListener;
    // popup_welcome

    private TextView aboutUs;
    private Button startUsingBtn;


    private AsyncTaskSearch asyncTaskSearch;
    private AsyncTaskNearby asyncTaskNearby;

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


    public SearchFragment() {
    }


    public SearchFragment newInstance(int columnCount) {
        SearchFragment fragment = new SearchFragment();
        // get current Location from MainActivity:
        Bundle bundle = getArguments();
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
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        // Share location details through google maps
        if (item.getTitle() == "Share") {

            Cursor c = db.getPlaceSearch(Constants.TABLE_NAME_SEARCH, (int) info.id);
            c.moveToFirst();
            String name = c.getString(c.getColumnIndex(Constants.KEY_SEARCH_LOCATION_NAME));
            float latitude = c.getFloat(c.getColumnIndex(Constants.KEY_SEARCH_LOCATION_LATITUDE));
            float longitude = c.getFloat(c.getColumnIndex(Constants.KEY_SEARCH_LOCATION_LONGITUDE));

            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_SUBJECT, name);
            share.putExtra(Intent.EXTRA_TEXT,"https://www.google.com/maps/search/?api=1&query="+latitude+","+longitude);

            startActivity(Intent.createChooser(share,"Share Via"));

        } else if (item.getTitle() == "Add To Favorites") {
            Cursor c = db.getPlaceSearch(Constants.TABLE_NAME_SEARCH, (int) info.id);
            c.moveToFirst();

            String name = c.getString(c.getColumnIndex(Constants.KEY_SEARCH_LOCATION_NAME));
            String address = c.getString(c.getColumnIndex(Constants.KEY_SEARCH_LOCATION_ADDRESS));
            float distance = c.getFloat(c.getColumnIndex(Constants.KEY_SEARCH_LOCATION_DISTANCE));
            float latitude = c.getFloat(c.getColumnIndex(Constants.KEY_SEARCH_LOCATION_LATITUDE));
            float longitude = c.getFloat(c.getColumnIndex(Constants.KEY_SEARCH_LOCATION_LONGITUDE));
            String image = c.getString(c.getColumnIndex(Constants.KEY_SEARCH_LOCATION_IMAGE));

            PlaceOfInterest place = new PlaceOfInterest(address,latitude,longitude,name,image, distance);
            db.addPlaceFavorites(activity, place, Constants.TABLE_NAME_FAV); //Add to the downloaded list table

            Toast.makeText(activity, "Place added to your favorites", Toast.LENGTH_SHORT).show();
        } else {
            return false;
        }
        return true;
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            super.onCreateContextMenu(menu, v, menuInfo);
            menu.setHeaderTitle("Select an Action");

            menu.add(0, v.getId(), 0, "Share");

            menu.add(0, v.getId(), 0, "Add To Favorites");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_lv_search, container, false);
        activity = getActivity();
        dataPassListener = (DataPassListener) activity;
        db = new DatabaseHandler(getActivity(), Constants.DB_NAME, null, Constants.SEARCH_DB_VERSION);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());


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

            // Online Mode
        } else {

            searchByText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get current location function placed here
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



                dialogBuilder = new AlertDialog.Builder(getContext());
                View view = getLayoutInflater().inflate(R.layout.popup_nearby_type, null);

                gridView = view.findViewById(R.id.grid_view_container_ID);
                gridView.setAdapter(new ImageNearbyAdapter(getContext()));

                dialogBuilder.setView(view);
                dialog = dialogBuilder.create();
                dialog.show();

                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        switch (position) {

                            case 0:
                                type = "gym";
                                placeNameToast = "GYMS";
                                break;
                            case 1:
                                type = "bank";
                                placeNameToast = "BANKS";
                                break;
                            case 2:
                                type = "gas_station";
                                placeNameToast = "GAS STATIONS";
                                break;
                            case 3:
                                type = "hospital";
                                placeNameToast = "HOSPITALS";
                                break;
                            case 4:
                                type = "pharmacy";
                                placeNameToast = "PHARMACY";
                                break;
                            case 5:
                                type = "supermarket";
                                placeNameToast = "SUPERMARKET";
                                break;
                            case 6:
                                type = "restaurant";
                                placeNameToast = "RESTAURANT";
                                break;
                            case 7:
                                type = "shopping_mall";
                                placeNameToast = "SHOPPING MALLS";
                                break;
                            case 8:
                                type = "movie_theater";
                                placeNameToast = "MOVIE THEATERS";
                                break;
                        }
                        Toast.makeText(getContext(), "You Chose to look for: " + placeNameToast, Toast.LENGTH_LONG).show();


                        db.deleteSearchLocationTable(Constants.TABLE_NAME_SEARCH);
                        asyncTaskNearby = new AsyncTaskNearby();
                        try {
                            asyncTaskNearby.setContext(getActivity());
                            asyncTaskNearby.currentLat = MainActivity.latitude;
                            asyncTaskNearby.currentLng = MainActivity.longitude;
                            asyncTaskNearby.cursorAdapterSearch = cursorAdapterSearch;
                            asyncTaskNearby.execute();

                        } catch (Exception e) {
                            Log.e(TAG, "onClick: " + e.getMessage());
                        }

                        MainActivity.hideKeyboard(getActivity());
                        dialog.dismiss();

                        cursorAdapterSearch.swapCursor(db.getAllLocations(Constants.TABLE_NAME_SEARCH));


                    }
                });

            }
        });

        listViewSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                fromSearchFrag = true;
                cursor = (Cursor) parent.getAdapter().getItem(position);
                float lat = cursor.getFloat(cursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_LATITUDE));
                float lng = cursor.getFloat(cursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_LONGITUDE));
                String name = cursor.getString(cursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_NAME));

                dataPassListener.passDataLocationToMap(lat, lng, name);
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
        View view = getLayoutInflater().inflate(R.layout.popup_welcome, null);

        aboutUs = view.findViewById(R.id.aboutTextView_POP_ID);
        startUsingBtn = view.findViewById(R.id.startUsingBtn_POP_ID);

        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();

        isKm = view.findViewById(R.id.km_RB_ID);
        isMiles = view.findViewById(R.id.miles_RB_ID);


        startUsingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPrefs = getActivity().getSharedPreferences(MY_PREFS, 0);
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putBoolean("ifFirstTime", false);

                if (isKm.isChecked()) {

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
