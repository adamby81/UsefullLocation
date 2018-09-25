package com.example.adam.myusefulllocations.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.adam.myusefulllocations.Constant.Constants;
import com.example.adam.myusefulllocations.R;
import com.squareup.picasso.Picasso;

public class LocationsCursorAdapter extends CursorAdapter {

    private TextView tvLocationName;
    private TextView tvLocationAddress;
    private TextView tvLocationDistance;
    private ImageView ivLocationPhoto;
    private String MyPREFERENCES = "MyPrefsFile";


    public LocationsCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_row_search, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        tvLocationName = view.findViewById(R.id.name_row_search_ID);
        tvLocationAddress = view.findViewById(R.id.address_Full_row_search_ID);
        tvLocationDistance = view.findViewById(R.id.distance_row_search_ID);
        ivLocationPhoto = view.findViewById(R.id.imageView_row_search_ID);


        tvLocationName.setText(cursor.getString(cursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_NAME)));
        tvLocationAddress.setText(cursor.getString(cursor.getColumnIndex((Constants.KEY_SEARCH_LOCATION_ADDRESS))));
        boolean isKm = sharedpreferences.getBoolean("isKM", true);
        // User selected Kilometers
        if (isKm) {
            tvLocationDistance.setText(cursor.getString(cursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_DISTANCE)).concat(" km"));
        }
        // User selected Miles (need to convert)
        else {
            float distance = Float.valueOf(cursor.getString(cursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_DISTANCE)));
            distance *= 0.62137;
            tvLocationDistance.setText(Float.toString(distance) .concat( "m"));
        }
        //Use Picasso to get picture into the image view
        Picasso.get().load(cursor.getString(cursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_IMAGE))).into(ivLocationPhoto);
        this.notifyDataSetChanged();


    }


}

