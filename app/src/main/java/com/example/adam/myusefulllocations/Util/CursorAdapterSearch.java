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

public class CursorAdapterSearch extends CursorAdapter {

    private TextView placeName;
    private TextView placeAddress;
    private TextView placeDistance;
    private ImageView placeImage;
    private String MyPREFERENCES = "MyPrefsFile";


    public CursorAdapterSearch(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_row, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        placeName = view.findViewById(R.id.name_row_search_ID);
        placeAddress = view.findViewById(R.id.address_Full_row_search_ID);
        placeDistance = view.findViewById(R.id.distance_row_search_ID);
        placeImage = view.findViewById(R.id.imageView_row_search_ID);


        placeName.setText(cursor.getString(cursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_NAME)));
        placeAddress.setText(cursor.getString(cursor.getColumnIndex((Constants.KEY_SEARCH_LOCATION_ADDRESS))));
        boolean isKm = sharedpreferences.getBoolean("isKM", true);

        if (isKm) {
            placeDistance.setText(cursor.getString(cursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_DISTANCE)).concat(" km"));
        }

        else {
            float distance = Float.valueOf(cursor.getString(cursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_DISTANCE)));
            distance *= 0.62137;
            placeDistance.setText(Float.toString(distance).concat(" ml"));
        }

        Picasso.get().load(cursor.getString(cursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_IMAGE))).into(placeImage);
        this.notifyDataSetChanged();


    }


}

