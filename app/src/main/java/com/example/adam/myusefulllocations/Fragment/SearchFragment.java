package com.example.adam.myusefulllocations.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adam.myusefulllocations.Data.CurrentLocation;
import com.example.adam.myusefulllocations.Data.DatabaseHandler;
import com.example.adam.myusefulllocations.R;
import com.example.adam.myusefulllocations.UI.RecyclerViewAdapterSearch;
import com.example.adam.myusefulllocations.Util.PlaceOfInterest;

import java.util.List;

public class SearchFragment extends Fragment {

    CurrentLocation currentLocation = null;

    private List<PlaceOfInterest> placeOfInterestList;
    private List<PlaceOfInterest> ListPlaceOfInterests;
    private RecyclerView recyclerView;
    private RecyclerViewAdapterSearch recyclerViewAdapterSearch;
    private DatabaseHandler db;


//    TabItem searchByText = (TabItem) getView().findViewById(R.id.by_Text_ID);
//    TabItem searchNearby = (TabItem) getView().findViewById(R.id.by_Location_ID);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        searchByText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                db = new DatabaseHandler(getContext());
//               // recyclerView.setLayoutManager(recyclerView.);
//            }
//        });


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, null);

    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);

        currentLocation = (CurrentLocation) context;

        //currentLocation.currentLocation();


    }

}
