package com.example.adam.myusefulllocations.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.adam.myusefulllocations.Constant.Constants;
import com.example.adam.myusefulllocations.Data.DatabaseHandler;
import com.example.adam.myusefulllocations.Fragment.MapsFragment;
import com.example.adam.myusefulllocations.R;
import com.example.adam.myusefulllocations.Util.CursorAdapterFavorites;

import static com.example.adam.myusefulllocations.Activity.MainActivity.isKmSettings;
import static com.example.adam.myusefulllocations.Activity.MainActivity.isMilesSettings;
import static com.example.adam.myusefulllocations.Fragment.SearchFragment.MY_PREFS;

public class FavoritesLvActivity extends AppCompatActivity {

    ListView favoritesListView;
    CursorAdapterFavorites cursorAdapterFavorites;
    DatabaseHandler db;
    Cursor cursor;
    DataPassListener dataPassListener;
    Activity activity;
    MapsFragment myMapFragment;



    Button addNewPlaceBtn;

    private android.support.v7.app.AlertDialog.Builder dialogBuilder;
    private android.support.v7.app.AlertDialog dialog;
    RadioButton isKm;
    RadioButton isMiles;
    public SharedPreferences mPrefs;


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if (item.getTitle() == "Share") {

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
        return true;
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select an Action");

        menu.add(0, v.getId(), 0, "Share");

    }

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



        db = new DatabaseHandler(FavoritesLvActivity.this, Constants.DB_NAME, null, Constants.FAVORITES_DB_VERSION);
//        activity = this;
//        dataPassListener = (DataPassListener) activity ;

        favoritesListView = findViewById(R.id.FAV_list_view_ID);
        cursor = db.getAllLocationsFavorites(Constants.TABLE_NAME_FAV);
        cursor.moveToFirst();

        cursorAdapterFavorites = new CursorAdapterFavorites(this, cursor);
        favoritesListView.setAdapter(cursorAdapterFavorites);
        registerForContextMenu(favoritesListView);

        favoritesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {
                dialogBuilder = new android.support.v7.app.AlertDialog.Builder(FavoritesLvActivity.this);
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
//                        Intent intent = new Intent(FavoritesLvActivity.this, FavoritesLvActivity.class);
//                        startActivity(intent);

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

            Button save = view.findViewById(R.id.saveBtn_POP_settings_ID);

            dialogBuilder.setView(view);
            dialog = dialogBuilder.create();
            dialog.show();

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
            View view = getLayoutInflater().inflate(R.layout.confermation_popup_delete_all, null);

            dialogBuilder.setView(view);
            dialog = dialogBuilder.create();
            dialog.show();

            Button delete = view.findViewById(R.id.deleteBtn_Dialog_DEL_ID);
            Button cancel = view.findViewById(R.id.cancelBtn_Dialog_DEL_ID);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    db = new DatabaseHandler(FavoritesLvActivity.this, Constants.DB_NAME, null, Constants.FAVORITES_DB_VERSION);
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
}
