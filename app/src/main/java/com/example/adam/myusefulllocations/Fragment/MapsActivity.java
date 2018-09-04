package com.example.adam.myusefulllocations.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adam.myusefulllocations.Activity.DataPassListener;
import com.example.adam.myusefulllocations.Data.DataPassListen;
import com.example.adam.myusefulllocations.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends Fragment implements DataPassListener,DataPassListen, OnMapReadyCallback {
    MapView mMapView;
    private GoogleMap googleMap;

//    public  long latitude;
//    public  long longitude;
//    public  long altitude;
//    public  String address;


    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_maps, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately


        Bundle bundle =  getArguments();


            final double latitude = bundle.getDouble("latitude");
            final double longitude = bundle.getDouble("longitude");

            final String address = bundle.getString("address");



        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                Context context = getContext();

                // we will need to use ReceiveFragment.this in the permission requests!!
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

                    // googleMap.setMyLocationEnabled(true);
                }
                googleMap.setMyLocationEnabled(true);
                // For dropping a marker at a point on the Map
                LatLng mylocation = new LatLng(latitude, longitude);
                googleMap.addMarker(new MarkerOptions().position(mylocation).title("You Are Here!").snippet(address));
//
//                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(mylocation).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);



    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void positionRowPass(int position) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public void passDataMyLocation(double lat, double lng, String address) {

    }


//    private void saveTasksToDB(View v) {
//
//        PlaceOfInterest location = new PlaceOfInterest();
//
//        String newTask = MapsActivity.this.location.getText().toString();
//        String newManagerName = managerName.getText().toString();
//        //TODO: Convert the due date to a type that the actual time will recognize
//        String newDueDate = dueDate.getText().toString();
//        String newNote = summeryNote.getText().toString();
//
//        location.setTask(newTask);
//        location.setManagerName(newManagerName);
//        location.setDueDate(newDueDate);
//        location.setSummeryNotes(newNote);
//
//        //save to db
//
//        db.addTasks(location);
//
//        Snackbar.make(v, "Task Saved!", Snackbar.LENGTH_LONG).show();
//    }
}
