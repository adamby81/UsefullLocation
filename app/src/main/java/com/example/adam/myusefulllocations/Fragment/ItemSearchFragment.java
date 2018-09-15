package com.example.adam.myusefulllocations.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.adam.myusefulllocations.Activity.MainActivity;
import com.example.adam.myusefulllocations.Constant.Constants;
import com.example.adam.myusefulllocations.Data.SearchDatabaseHandler;
import com.example.adam.myusefulllocations.R;
import com.example.adam.myusefulllocations.UI.MyItemRecyclerViewAdapter;
import com.example.adam.myusefulllocations.Util.PlaceOfInterest;
import com.example.adam.myusefulllocations.Util.SearchJsonAsyncTask;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ItemSearchFragment extends Fragment implements LocationListener {

    // TODO: Customize parameter argument names

    private static final String ARG_COLUMN_COUNT = "column-count";
    public static final String MY_PREFS = "MyPrefsFile";
    public SharedPreferences mPrefs;
    public boolean firstTime;
    private String[] querySearchList;


    private List<PlaceOfInterest> placeOfInterestList;
    private List<PlaceOfInterest> listPlaceOfInterests;

    private SearchDatabaseHandler db;

    private EditText search;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;


    // popup

    private TextView aboutUs;
    private Button startUsingBtn;


    private SearchJsonAsyncTask searchJsonAsyncTask;

    private Button searchByText;
    private Button searchNearby;

    private MyItemRecyclerViewAdapter adapter;





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
            double latitude = bundle.getDouble("latitude");
            double longitude = bundle.getDouble("longitude");
        fragment.setArguments(bundle);
        return fragment;
    }
    public List<PlaceOfInterest> getAllLocations(Cursor cursor) {


        List<PlaceOfInterest> searchList = new ArrayList<>();

        querySearchList = new String[]{
                Constants.KEY_SEARCH_ID,
                Constants.KEY_SEARCH_LOCATION_ADDRESS, Constants.KEY_SEARCH_LOCATION_LATITUDE,
                Constants.KEY_SEARCH_LOCATION_LONGITUDE, Constants.KEY_SEARCH_LOCATION_NAME,
                Constants.KEY_SEARCH_LOCATION_DISTANCE,
                Constants.KEY_SEARCH_LOCATION_IMAGE};

        if (cursor.moveToFirst()) {
            do {
                PlaceOfInterest placeOfInterest = new PlaceOfInterest();
                placeOfInterest.set_id(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_SEARCH_ID))));
                placeOfInterest.setAddress(cursor.getString(cursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_ADDRESS)));
                placeOfInterest.setLatitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_LATITUDE))));
                placeOfInterest.setLongitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_LONGITUDE))));
                placeOfInterest.setLongitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_DISTANCE))));
                placeOfInterest.setName(cursor.getString(cursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_NAME)));
                placeOfInterest.setPhotoUrl(cursor.getString(cursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_IMAGE)));


                //add to the tasks list

                searchList.add(placeOfInterest);


            } while (cursor.moveToNext());
        }
        return searchList;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        db = new SearchDatabaseHandler(getActivity());

        placeOfInterestList = new ArrayList<>();
        placeOfInterestList=getAllLocations(db.getAllLocations(Constants.TABLE_NAME_SEARCH));
        listPlaceOfInterests = new ArrayList<>();

        adapter = new MyItemRecyclerViewAdapter(getActivity(), placeOfInterestList);


        searchByText = view.findViewById(R.id.search_by_Text_ID);
        searchNearby = view.findViewById(R.id.search_nearby_ID);

        search = view.findViewById(R.id.search_Bar_ID);

        Context context = view.getContext();
        RecyclerView recyclerView = view.findViewById(R.id.search_recyclerView_ID);
//        ((RecyclerView) view).setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

            for (PlaceOfInterest place : placeOfInterestList) {

                PlaceOfInterest placeOfInterest = new PlaceOfInterest();

                placeOfInterest.setName(place.getName());
                placeOfInterest.setAddress(place.getAddress());
                placeOfInterest.setDistance(place.getDistance());


                listPlaceOfInterests.add(placeOfInterest);
            }
            searchByText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get current location function placed here
                    String textToSearch = search.getText().toString().trim().replace(" ", "%20");

                    if (textToSearch.length() > 0) {

                        jsonParse(textToSearch);
                    }
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

    private void jsonParse(String textToSearch) {

        searchJsonAsyncTask = new SearchJsonAsyncTask();
        searchJsonAsyncTask.setContext(getActivity());
        searchJsonAsyncTask.setSearchText(textToSearch);
        searchJsonAsyncTask.currentLat=MainActivity.latitude;
        searchJsonAsyncTask.currentLng=MainActivity.longitude;
        searchJsonAsyncTask.execute();


    }

    private void welcomePopup() {


        dialogBuilder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.popup, null);

       aboutUs = view.findViewById(R.id.aboutTextView_POP_ID);
       startUsingBtn = view.findViewById(R.id.startUsingBtn_POP_ID);

        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();

        startUsingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPrefs = getActivity().getSharedPreferences(MY_PREFS,0);
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putBoolean("ifFirstTime", false);
                editor.putBoolean("isMiles", true);


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
