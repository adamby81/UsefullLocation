package com.example.adam.myusefulllocations.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adam.myusefulllocations.Activity.DataPassListener;
import com.example.adam.myusefulllocations.FavoritesFeature.provider.PlaceDBHelper;
import com.example.adam.myusefulllocations.R;
import com.example.adam.myusefulllocations.Util.PlaceOfInterest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, DataPassListener {
    MapView mMapView;
    private GoogleMap googleMap;

    public double latitude;
    public double longitude;
    public String address;
    String name;




    private PlaceDBHelper db;
    private String mFeatureName;
    private String mPhotoUrl;
    double latitudeMarker;
    double longitudeMarker;
    double mDistance;



    private Context mContext;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }
//
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
                mContext = getContext();

                if (ItemSearchFragment.fromSearchFrag) {

                    passDataMyLocation(latitude, longitude, address);

                } else {

                    // we will need to use ReceiveFragment.this in the permission requests!!
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

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
            }
        });

        return rootView;

    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);

        mContext = getActivity();



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
    public void onMapReady(GoogleMap googleMap) {

    }


    String addressMarker;

    @Override
    public void onMapLongClick(LatLng latLng) {

        Log.i("PlaceOfInterest Info: ", latLng.toString());

        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

        try {

            addressMarker = "Could not find any address";
            List<Address> listAddresses = geocoder.getFromLocation(latLng.latitude,
                    latLng.longitude, 1);

            if (listAddresses != null && listAddresses.size() >0) {
                Log.i("Address Info: ", listAddresses.get(0).toString());

                addressMarker = "Address: " + "\n";
                if (listAddresses.get(0).getSubThoroughfare() != null) {
                    // רחוב
                    addressMarker += listAddresses.get(0).getSubThoroughfare() + " ";
                }

                if (listAddresses.get(0).getThoroughfare() != null) {
                    // מספר בית
                    addressMarker += listAddresses.get(0).getThoroughfare() + "\n";

                }

                if (listAddresses.get(0).getLocality() != null) {
                    // עיר
                    addressMarker += listAddresses.get(0).getLocality() + ", ";
                }
                if (listAddresses.get(0).getPostalCode() != null) {
                    // תיבת דואר
                    addressMarker += listAddresses.get(0).getPostalCode() + "\n";

                }

                if (listAddresses.get(0).getCountryName() != null) {
                    // מדינה
                    addressMarker += listAddresses.get(0).getCountryName() + "\n";

                }
                if (listAddresses.get(0).getFeatureName() != null) {
                    // רחוב
                    mFeatureName = listAddresses.get(0).getFeatureName();
                }
                if (listAddresses.get(0).getUrl() != null) {
                    mPhotoUrl = listAddresses.get(0).getUrl();
                }

            }

            latitudeMarker = latLng.latitude;
            longitudeMarker =  latLng.longitude;
            mDistance = distance(latitudeMarker, longitudeMarker, latitude, longitude);



            Log.e("location: ", "updateLocationInfo: " + latitudeMarker + " AND " + latLng.latitude + " AND " + mFeatureName);

        } catch (IOException e) {
            e.printStackTrace();
        }


        googleMap.addMarker(new MarkerOptions().position(latLng).title(addressMarker));

                PlaceOfInterest location = new PlaceOfInterest();

                String mNewName = mFeatureName;
                String mNewFullAddress = addressMarker;
                double mNewLatitude = latitudeMarker;
                double mNewLongitude = longitudeMarker;
                String mNewPhoto = mPhotoUrl;
                double mNewdistance = mDistance;

                location.setName(mNewName);
                location.setAddress(mNewFullAddress);
                location.setLatitude(mNewLatitude);
                location.setLongitude(mNewLongitude);
                location.setPhotoUrl(mNewPhoto);
                location.setDistance(mNewdistance);

            db.addPlace(location);

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


    private double distance (double myLat, double myLng, double placeLat, double placeLng) {

        double radiusMyLat = Math.PI * myLat / 180;
        double radiusPlaceLat = Math.PI * placeLat / 180;
        double delta = myLng - placeLng;
        double radiusDelta = Math.PI * delta / 180;

        double fixedDistance = Math.sin(radiusMyLat) * Math.sin(radiusPlaceLat)
                + Math.cos(radiusMyLat) * Math.cos(radiusPlaceLat)
                * Math.cos(radiusDelta);
        if (fixedDistance > 1) {

            fixedDistance = 1;

        }

        fixedDistance = Math.acos(fixedDistance);
        fixedDistance = fixedDistance * 180 / Math.PI;
        fixedDistance = fixedDistance * 60 * 1.1515;
        fixedDistance = 1.609334;
        return fixedDistance;



    }


    @Override
    public void passDataMyLocation(double lat, double lng, String name) {

            this.latitude = lat;
            this.longitude = lng;
            this.address = name;


//        public void setLocation(LatLng location,GoogleMap map,String name){
//        GoogleMap mMap = map;
//        if(location==null)
//            location=new LatLng(0,0);
//        mMap.addMarker(new MarkerOptions().position(location).title(name));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,10));
//    }



    }
}
