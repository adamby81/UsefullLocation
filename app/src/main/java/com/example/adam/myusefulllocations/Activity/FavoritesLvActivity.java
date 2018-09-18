package com.example.adam.myusefulllocations.Activity;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.adam.myusefulllocations.Constant.Constants;
import com.example.adam.myusefulllocations.Data.DatabaseHandler;
import com.example.adam.myusefulllocations.R;
import com.example.adam.myusefulllocations.Util.LocationsCursorAdapter;
import com.google.android.gms.maps.MapFragment;

public class FavoritesLvActivity extends AppCompatActivity {

    ListView locationsListView;
    LocationsCursorAdapter locationsCursorAdapter;
    DatabaseHandler db;
    Cursor cursor;
    Intent intent;

    Button addNewPlaceBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_lv);


        addNewPlaceBtn = findViewById(R.id.FAV_add_Btn_lv_ID);
        addNewPlaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(FavoritesLvActivity.this, MainActivity.class);
                startActivity(intent);


            }
        });


        db = new DatabaseHandler(FavoritesLvActivity.this, Constants.FAVORITES_DB_NAME, null, Constants.FAVORITES_DB_VERSION);

        locationsListView = findViewById(R.id.FAV_list_view_ID);
        cursor = db.getAllLocationsFavorites(Constants.TABLE_NAME_FAV);
        cursor.moveToFirst();

        locationsCursorAdapter = new LocationsCursorAdapter(this, cursor);
        locationsListView.setAdapter(locationsCursorAdapter);
        registerForContextMenu(locationsListView);

        locationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Fragment fragment = new MapFragment();
//                getSupportFragmentManager().beginTransaction()
//                        .add(R.id.fragment_container_main, fragment, position, id).commit();


            }
        });




    }
}
