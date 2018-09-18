package com.example.adam.myusefulllocations.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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

import com.example.adam.myusefulllocations.Activity.MainActivity;
import com.example.adam.myusefulllocations.Data.DatabaseHandler;
import com.example.adam.myusefulllocations.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    MapView mMapView;
    private GoogleMap googleMap;

    public float latitude;
    public float longitude;
    public String name;

    private SharedPreferences prefs;
    private DatabaseHandler db;
    private String mFeatureName;
    private String mPhotoUrl;
    float latitudeMarker;
    float longitudeMarker;
    float mDistance;



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

        if (!ItemSearchFragment.fromSearchFrag) {

            final LatLng myCurrentLocation = new LatLng(MainActivity.latitude, MainActivity.longitude );

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


                    // we will need to use ReceiveFragment.this in the permission requests!!
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    }
                    googleMap.setMyLocationEnabled(true);
                    // For dropping a marker at a point on the Map

                    googleMap.addMarker(new MarkerOptions().position(myCurrentLocation).title("You Are Here!").snippet(name));
//
//                // For zooming automatically to the location of the marker
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(myCurrentLocation).zoom(12).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                }

            });


        }else {

            prefs = getActivity().getSharedPreferences(ItemSearchFragment.MY_PREFS, Context.MODE_PRIVATE);
            float lat = prefs.getFloat("place_lat", 0);
            float lng = prefs.getFloat("place_lng", 0);
            final LatLng placeLocation = new LatLng(lat, lng);


            name = prefs.getString("place_name", null);


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


                    // we will need to use ReceiveFragment.this in the permission requests!!
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    }
                    googleMap.setMyLocationEnabled(true);
                    // For dropping a marker at a point on the Map

                    googleMap.addMarker(new MarkerOptions().position(placeLocation).title("You Are Here!").snippet(name));
//
//                // For zooming automatically to the location of the marker
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(placeLocation).zoom(12).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                }

            });
        }
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
//
//        Log.i("PlaceOfInterest Info: ", latLng.toString());
//
//
//            latitudeMarker = (float) latLng.latitude;
//            longitudeMarker = (float) latLng.longitude;
//            mDistance = distance(latitudeMarker, longitudeMarker, latitude, longitude);
//
//
//
//            Log.e("location: ", "updateLocationInfo: " + latitudeMarker + " AND " + latLng.latitude + " AND " + mFeatureName);
//
//
//
//
//        googleMap.addMarker(new MarkerOptions().position(latLng).title(addressMarker));
//
////                PlaceOfInterest location = new PlaceOfInterest(address, lat, lon, name, img, distance);
//
//                String mNewName = mFeatureName;
//                String mNewFullAddress = addressMarker;
//                float mNewLatitude = latitudeMarker;
//                float mNewLongitude = longitudeMarker;
//                String mNewPhoto = mPhotoUrl;
//                float mNewdistance = mDistance;
//
//                location.setName(mNewName);
//                location.setAddress(mNewFullAddress);
//                location.setLatitude(mNewLatitude);
//                location.setLongitude(mNewLongitude);
//                location.setPhotoUrl(mNewPhoto);
//                location.setDistance(mNewdistance);
//
//            db.addPlaceFavorites(getContext(), location, Constants.TABLE_NAME_FAV);
//
//    }
//
//
////    private void saveTasksToDB(View v) {
////
////        PlaceOfInterest location = new PlaceOfInterest();
////
////        String newTask = MapsFragment.this.location.getText().toString();
////        String newManagerName = managerName.getText().toString();
////        //TODO: Convert the due date to a type that the actual time will recognize
////        String newDueDate = dueDate.getText().toString();
////        String newNote = summeryNote.getText().toString();
////
////        location.setTask(newTask);
////        location.setManagerName(newManagerName);
////        location.setDueDate(newDueDate);
////        location.setSummeryNotes(newNote);
////
////        //save to db
////
////        db.addTasks(location);
////
////        Snackbar.make(v, "Task Saved!", Snackbar.LENGTH_LONG).show();
////    }
//
//
//    private float distance (float myLat, float myLng, float placeLat, float placeLng) {
//
//        float radiusMyLat = (float) (Math.PI * myLat / 180);
//        float radiusPlaceLat = (float) (Math.PI * placeLat / 180);
//        float delta = (myLng - placeLng);
//        float radiusDelta = (float) (Math.PI * delta / 180);
//
//        float fixedDistance = (float) (Math.sin(radiusMyLat) * Math.sin(radiusPlaceLat)
//                        + Math.cos(radiusMyLat) * Math.cos(radiusPlaceLat)
//                        * Math.cos(radiusDelta));
//        if (fixedDistance > 1) {
//
//            fixedDistance = 1;
//
//        }
//
//        fixedDistance = (float) Math.acos(fixedDistance);
//        fixedDistance = (float) (fixedDistance * 180 / Math.PI);
//        fixedDistance = (float) (fixedDistance * 60 * 1.1515);
//        fixedDistance = (float) 1.609334;
//        return fixedDistance;
//
//

    }



}
