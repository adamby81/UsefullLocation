package com.example.adam.myusefulllocations.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import com.example.adam.myusefulllocations.Data.CurrentLocation;
import com.example.adam.myusefulllocations.Data.FavDatabaseHandler;
import com.example.adam.myusefulllocations.R;
import com.example.adam.myusefulllocations.UI.MyItemRecyclerViewAdapter;
import com.example.adam.myusefulllocations.Util.PlaceOfInterest;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ItemSearchFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private List<PlaceOfInterest> placeOfInterestList;
    private List<PlaceOfInterest> listPlaceOfInterests;

    private List<PlaceOfInterest> searchList;
    private FavDatabaseHandler db;
    CurrentLocation currentLocation = null;
    private AutoCompleteTextView search;


    private MyItemRecyclerViewAdapter myItemRecyclerViewAdapter;



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

        placeOfInterestList = new ArrayList<>();
        listPlaceOfInterests = new ArrayList<>();

        searchList = new ArrayList<>();

        search = view.findViewById(R.id.search_Bar_ID);

        if (search != null){

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            view = view.findViewById(R.id.search_recyclerView_ID);
//            ((RecyclerView) view).setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));


//            if (db.getAllLocations().size() > 0) {
//                placeOfInterestList = db.getAllLocations();
//
//                for (PlaceOfInterest p : placeOfInterestList) {
//
//                    PlaceOfInterest place = new PlaceOfInterest();
//
//                    place.set_id(p.get_id());
//                    place.setName(p.getName());
//                    place.setAddress(p.getAddress());
//                    place.setLatitude(p.getLatitude());
//                    place.setLongitude(p.getLongitude());
//                    place.setPhotoUrl(p.getPhotoUrl());
//
//                    listPlaceOfInterests.add(place);
//
//                }
//            } else{
//
//                Log.e("DB content", "NO CONTENT TO RETRIVE FROM DB");
//
//            }
        }else{

            //TODO: Needs to create a message that says "ENTER A KEY WORD FOR SEARCHING


        }

        }
        return view;
    }


    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
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
