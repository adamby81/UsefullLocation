package com.example.adam.myusefulllocations.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.adam.myusefulllocations.Constant.Constants;
import com.example.adam.myusefulllocations.Data.DatabaseHandler;
import com.example.adam.myusefulllocations.R;
import com.example.adam.myusefulllocations.Util.LocationsCursorAdapter;

import static com.example.adam.myusefulllocations.Fragment.ItemSearchFragment.MY_PREFS;

public class FavoritesLvActivity extends AppCompatActivity {

    ListView locationsListView;
    LocationsCursorAdapter locationsCursorAdapter;
    DatabaseHandler db;
    Cursor cursor;
    Intent intent;

    Button addNewPlaceBtn;

    private android.support.v7.app.AlertDialog.Builder dialogBuilder;
    private android.support.v7.app.AlertDialog dialog;
    RadioButton isKm;
    RadioButton isMiles;
    public SharedPreferences mPrefs;


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


            }
        });




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {


            dialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
            View view = getLayoutInflater().inflate(R.layout.settings_popup, null);

            Button save = view.findViewById(R.id.saveBtn_POP_ID);

            dialogBuilder.setView(view);
            dialog = dialogBuilder.create();
            dialog.show();

            isKm = view.findViewById(R.id.km_RB_ID);
            isMiles = view.findViewById(R.id.miles_RB_ID);

            mPrefs = getSharedPreferences(MY_PREFS, MODE_PRIVATE);

            boolean isKM = mPrefs.getBoolean("isKm", true);
            if (isKM) {
                isKm.isChecked();
            }else{

                isMiles.isChecked();
            }

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isKm.isChecked()){

                        mPrefs = getSharedPreferences(MY_PREFS,0);
                        SharedPreferences.Editor editor = mPrefs.edit();
                        editor.putBoolean("isKM", true);
                        editor.apply();
                        editor.commit();

                    }else{

                        if (isMiles.isChecked()){
                            mPrefs = getSharedPreferences(MY_PREFS,0);
                            SharedPreferences.Editor editor = mPrefs.edit();
                            editor.putBoolean("isKM", false);
                            editor.apply();
                            editor.commit();

                        }else{

                            Toast.makeText(FavoritesLvActivity.this, R.string.settings_popup,
                                    Toast.LENGTH_LONG).show();
                        }
                    }



                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                        }
                    }, 1000); // = 1 second

                    dialog.dismiss();


                }
            });
        }

        if (id == R.id.action_delete){

            dialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
            View view = getLayoutInflater().inflate(R.layout.confermation_popup_delete, null);

            dialogBuilder.setView(view);
            dialog = dialogBuilder.create();
            dialog.show();

            Button delete = view.findViewById(R.id.deleteBtn_Dialog_DEL_ID);
            Button cancel = view.findViewById(R.id.cancelBtn_Dialog_DEL_ID);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DatabaseHandler databaseHandler = new DatabaseHandler(FavoritesLvActivity.this, Constants.TABLE_NAME_FAV, null, Constants.FAVORITES_DB_VERSION);
                    databaseHandler.deleteFavoriteshLocationTable(Constants.TABLE_NAME_FAV);

                    dialog.dismiss();

                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });


        }


        return super.onOptionsItemSelected(item);
    }
}
