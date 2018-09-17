package com.example.adam.myusefulllocations.Activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.example.adam.myusefulllocations.Data.SearchDatabaseHandler;
import com.example.adam.myusefulllocations.R;
import com.example.adam.myusefulllocations.Util.LocationsCursorAdapter;

public class FavoritesActivity extends AppCompatActivity {

    ListView locationsListView;
    LocationsCursorAdapter locationsCursorAdapter;
    SearchDatabaseHandler db;
    Cursor cursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_lv);






    }
}
