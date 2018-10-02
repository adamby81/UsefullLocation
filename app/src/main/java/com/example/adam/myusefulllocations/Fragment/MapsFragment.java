package com.example.adam.myusefulllocations.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adam.myusefulllocations.Activity.MainActivity;
import com.example.adam.myusefulllocations.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragment extends Fragment implements OnMapReadyCallback {
    MapView mMapView;
    private GoogleMap googleMap;

    public double latitudeMap;
    public double longitudeMap;
    public String name;

    private Context mContext;

    public LatLng myCurrentLocation;
    public String titleName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_maps, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            }
           // MainActivity.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
//            double lastKnownLat = MainActivity.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
//
//            double lastKnownLng = MainActivity.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();


            latitudeMap = bundle.getDouble("lat",  0);
            longitudeMap = bundle.getDouble("lng",  0);
            name = bundle.getString("name", null);

        }else {

            latitudeMap = MainActivity.latitude;
            longitudeMap = MainActivity.longitude;

        }
        MainActivity.hideKeyboard(getActivity());


        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                mContext = getContext();
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                }
                googleMap.setMyLocationEnabled(true);


                myCurrentLocation = new LatLng(latitudeMap, longitudeMap);
                titleName = name;
                googleMap.addMarker(new MarkerOptions().position(myCurrentLocation).title(titleName));

                CameraPosition cameraPosition = new CameraPosition.Builder().target(myCurrentLocation).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }
        });

        mMapView.onResume();


        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
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
    public void onMapReady(GoogleMap mMap) {
        googleMap = mMap;

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            }
            // MainActivity.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
//            double lastKnownLat = MainActivity.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
//
//            double lastKnownLng = MainActivity.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();


            latitudeMap = bundle.getDouble("lat", 0);
            longitudeMap = bundle.getDouble("lng", 0);
            name = bundle.getString("name", null);

        } else {

            latitudeMap = MainActivity.latitude;
            longitudeMap = MainActivity.longitude;

        }
    }
}
