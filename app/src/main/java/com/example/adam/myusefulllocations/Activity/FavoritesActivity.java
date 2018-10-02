package com.example.adam.myusefulllocations.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.adam.myusefulllocations.Constant.Constants;
import com.example.adam.myusefulllocations.Data.DatabaseHandler;
import com.example.adam.myusefulllocations.R;
import com.example.adam.myusefulllocations.Util.CursorAdapterFavorites;
import com.example.adam.myusefulllocations.Util.Global;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static com.example.adam.myusefulllocations.Activity.MainActivity.isKmSettings;
import static com.example.adam.myusefulllocations.Activity.MainActivity.isMilesSettings;
import static com.example.adam.myusefulllocations.Activity.MainActivity.radius1000;
import static com.example.adam.myusefulllocations.Activity.MainActivity.radius10000;
import static com.example.adam.myusefulllocations.Activity.MainActivity.radius2000;
import static com.example.adam.myusefulllocations.Activity.MainActivity.radius5000;
import static com.example.adam.myusefulllocations.Activity.MainActivity.radiusGroup;
import static com.example.adam.myusefulllocations.Fragment.SearchFragment.MY_PREFS;
import static com.example.adam.myusefulllocations.R.id.radius_GR_ID;

public class FavoritesActivity extends AppCompatActivity {

    ListView favoritesListView;
    CursorAdapterFavorites cursorAdapterFavorites;
    DatabaseHandler db;
    Cursor cursor;

    Button addNewPlaceBtn;

    private android.support.v7.app.AlertDialog.Builder dialogBuilder;
    private android.support.v7.app.AlertDialog dialog;
    public SharedPreferences mPrefs;

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if (item.getTitle() == getString(R.string.share)) {

            Cursor c = db.getPlaceSearch(Constants.TABLE_NAME_FAV, (int) info.id);
            c.moveToFirst();

            String name = c.getString(c.getColumnIndex(Constants.KEY_FAV_NAME));
            float latitude = c.getFloat(c.getColumnIndex(Constants.KEY_FAV_LATITUDE));
            float longitude = c.getFloat(c.getColumnIndex(Constants.KEY_FAV_LONGITUDE));

            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_SUBJECT, name);
            share.putExtra(Intent.EXTRA_TEXT,"https://www.google.com/maps/search/?api=1&query="+latitude+","+longitude);

            startActivity(Intent.createChooser(share,"Share Via"));

        }  else {
            return false;
        }
        if (item.getTitle() == getString(R.string.navigate)) {
            Cursor c = db.getPlaceSearch(Constants.TABLE_NAME_FAV, (int) info.id);
            c.moveToFirst();

            float latitude = c.getFloat(c.getColumnIndex(Constants.KEY_FAV_LATITUDE));
            float longitude = c.getFloat(c.getColumnIndex(Constants.KEY_FAV_LONGITUDE));

            Global global = new Global(FavoritesActivity.this);

            if (!global.isNetworkConnected()) {
                //String url = "//www.waze.com/ul?ll="+latitudeMap+"%2C"+longitudeMap+"&navigate=yes&zoom=17";
                //Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) );
                URL url = null;
                try {
                    url = new URL("//www.waze.com/ul?ll="+latitude+"%2C"+longitude+"&navigate=yes&zoom=17");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                try {
                    HttpsURLConnection myConnection
                            = (HttpsURLConnection) url.openConnection();
                    myConnection.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                startActivity( url );
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.stackoverflow.com")));



            }

        }

        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(getString(R.string.select_action));

        menu.add(0, v.getId(), 0, getString(R.string.share));
        menu.add(0, v.getId(), 0, getString(R.string.navigate));


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        addNewPlaceBtn = findViewById(R.id.FAV_add_Btn_lv_ID);
        addNewPlaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(FavoritesActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });

        db = new DatabaseHandler(FavoritesActivity.this, Constants.DB_NAME, null, Constants.FAVORITES_DB_VERSION);

        favoritesListView = findViewById(R.id.FAV_list_view_ID);
        cursor = db.getAllLocationsFavorites(Constants.TABLE_NAME_FAV);
        cursor.moveToFirst();

        cursorAdapterFavorites = new CursorAdapterFavorites(this, cursor);
        favoritesListView.setAdapter(cursorAdapterFavorites);
        registerForContextMenu(favoritesListView);

        favoritesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {
                dialogBuilder = new android.support.v7.app.AlertDialog.Builder(FavoritesActivity.this);
                 view = getLayoutInflater().inflate(R.layout.confermation_popup_delete_one_fav, null);

                Button delete = view.findViewById(R.id.deleteBtn_Dialog_DEL_One_Fav_ID);
                Button cancel = view.findViewById(R.id.cancelBtn_Dialog_DEL_One_Fav_ID);

                dialogBuilder.setView(view);
                dialog = dialogBuilder.create();
                dialog.show();

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        db.deletePlaceFromFav(id);
                        cursorAdapterFavorites.swapCursor(db.getAllLocationsFavorites(Constants.TABLE_NAME_FAV));

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
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {

            dialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
            View view = getLayoutInflater().inflate(R.layout.popup_settings, null);

            Button save = view.findViewById(R.id.saveBtn_POP_settings_ID);

            dialogBuilder.setView(view);
            dialog = dialogBuilder.create();
            dialog.show();

            radius1000 = view.findViewById(R.id.radius1000_GR_ID);
            radius2000 = view.findViewById(R.id.radius2000_GR_ID);
            radius5000 = view.findViewById(R.id.radius5000_GR_ID);
            radius10000 = view.findViewById(R.id.radius10000_GR_ID);
            radiusGroup = view.findViewById(radius_GR_ID);

            isKmSettings = view.findViewById(R.id.km_RB_settings_ID);
            isMilesSettings = view.findViewById(R.id.miles_RB_settings_ID);

            mPrefs = getSharedPreferences(MY_PREFS, MODE_PRIVATE);

            boolean isKM = mPrefs.getBoolean("isKM", true);
            if (isKM) {
                isKmSettings.setChecked(true);
                isMilesSettings.setChecked(false);
                isKmSettings.isChecked();

            }else{
                isKmSettings.setChecked(false);
                isMilesSettings.setChecked(true);
                isMilesSettings.isChecked();
            }

            String radiusPrefs = mPrefs.getString("radius", "2000");

            switch (radiusPrefs) {
                case "1000":
                    radius1000.setChecked(true);
                    radius2000.setChecked(false);
                    radius5000.setChecked(false);
                    radius10000.setChecked(false);
                    break;
                case "2000":
                    radius1000.setChecked(false);
                    radius2000.setChecked(true);
                    radius5000.setChecked(false);
                    radius10000.setChecked(false);
                    break;
                case "5000":
                    radius1000.setChecked(false);
                    radius2000.setChecked(false);
                    radius5000.setChecked(true);
                    radius10000.setChecked(false);
                    break;
                case "10000":
                    radius1000.setChecked(false);
                    radius2000.setChecked(false);
                    radius5000.setChecked(false);
                    radius10000.setChecked(true);
                    break;

            }

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isKmSettings.isChecked()){

                        mPrefs = getSharedPreferences(MY_PREFS,0);
                        SharedPreferences.Editor editor = mPrefs.edit();
                        editor.putBoolean("isKM", true);
                        editor.apply();
                        editor.commit();
                        cursorAdapterFavorites.swapCursor(db.getAllLocationsFavorites(Constants.TABLE_NAME_FAV));


                    }else{

                        if (isMilesSettings.isChecked()){
                            mPrefs = getSharedPreferences(MY_PREFS,0);
                            SharedPreferences.Editor editor = mPrefs.edit();
                            editor.putBoolean("isKM", false);
                            editor.apply();
                            editor.commit();
                            cursorAdapterFavorites.swapCursor(db.getAllLocationsFavorites(Constants.TABLE_NAME_FAV));


                        }else{

                            Toast.makeText(FavoritesActivity.this, R.string.settings_popup,
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
            View view = getLayoutInflater().inflate(R.layout.confermation_popup_delete_all, null);

            dialogBuilder.setView(view);
            dialog = dialogBuilder.create();
            dialog.show();

            Button delete = view.findViewById(R.id.deleteBtn_Dialog_DEL_ID);
            Button cancel = view.findViewById(R.id.cancelBtn_Dialog_DEL_ID);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    db = new DatabaseHandler(FavoritesActivity.this, Constants.DB_NAME, null, Constants.FAVORITES_DB_VERSION);
                    db.deleteFavoritesLocationTable(Constants.TABLE_NAME_FAV);
                    cursorAdapterFavorites.swapCursor(db.getAllLocationsFavorites(Constants.TABLE_NAME_FAV));


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

    public void onRadiusChooseClick(View view) {

        int checked = radiusGroup.getCheckedRadioButtonId();

        MainActivity.radiusChoice = findViewById(checked);

        mPrefs = getSharedPreferences(MY_PREFS, 0);
        SharedPreferences.Editor editor = mPrefs.edit();

        switch(checked) {
            case R.id.radius1000_GR_ID:
                MainActivity.nearbyRadius = "1000";
                editor.putString("radius", "1000");
                editor.apply();
                editor.commit();
                break;
            case R.id.radius2000_GR_ID:
                MainActivity.nearbyRadius = "2000";
                editor.putString("radius", "2000");
                editor.apply();
                editor.commit();
                break;
            case R.id.radius5000_GR_ID:
                MainActivity.nearbyRadius = "5000";
                editor.putString("radius", "5000");
                editor.apply();
                editor.commit();
                break;
            case R.id.radius10000_GR_ID:
                MainActivity.nearbyRadius = "10000";
                editor.putString("radius", "10000");
                editor.apply();
                editor.commit();

                break;
        }

    }
}
