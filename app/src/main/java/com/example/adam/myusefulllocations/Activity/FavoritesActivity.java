package com.example.adam.myusefulllocations.Activity;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.adam.myusefulllocations.Data.DatabaseHandler;
import com.example.adam.myusefulllocations.R;
import com.example.adam.myusefulllocations.UI.RecyclerViewAdapterFav;
import com.example.adam.myusefulllocations.Util.GlogalValues;
import com.example.adam.myusefulllocations.Util.PlaceOfInterest;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity implements GlogalValues {

    private RecyclerView recyclerView;
    private RecyclerViewAdapterFav recyclerViewAdapterFav;
    private List<PlaceOfInterest> placeOfInterestList;
    private List<PlaceOfInterest> listPlaceOfInterests;
    private DatabaseHandler db;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText task;
    private EditText managerName;
    private EditText dueDate;
    private Button saveBtn;
    private EditText summeryNote;

    private TextView countView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);


        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(FavoritesActivity.this, new String[]{Manifest.permission.SEND_SMS}, 100);

        }





        db = new DatabaseHandler(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_ID);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        placeOfInterestList = new ArrayList<>();
        listPlaceOfInterests = new ArrayList<>();

       // countView = findViewById(R.id.count_View_ID);



        // Get tasks from the db

        placeOfInterestList = db.getAllLocations();

        for (PlaceOfInterest c: placeOfInterestList) {
            PlaceOfInterest placeOfInterest = new PlaceOfInterest();
            placeOfInterest.setAddress(c.getAddress());
            placeOfInterest.setLatitude(c.getLatitude());
            placeOfInterest.setLongitude(c.getLongitude());
            placeOfInterest.setName(c.getName());
            placeOfInterest.setPhotoUrl(c.getPhotoUrl());
            placeOfInterest.set_id(c.get_id());





            listPlaceOfInterests.add(placeOfInterest);
        }

        recyclerViewAdapterFav = new RecyclerViewAdapterFav(this, listPlaceOfInterests);
        recyclerView.setAdapter(recyclerViewAdapterFav);
        recyclerViewAdapterFav.notifyDataSetChanged();
       // tasksCounterUpdater (); //counts the opening tasks

        addNotification();
    }

    // date picker View


    private void addNotification (){

        Intent intent = new Intent (FavoritesActivity.this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(FavoritesActivity.this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder b = new NotificationCompat.Builder(FavoritesActivity.this, "default");

        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Task Manager ")
                .setContentText("A task needs to be sent for you Today !")
                .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND)
                .setContentIntent(contentIntent)
                .setContentInfo("Info");

        NotificationManager notificationManager = (NotificationManager)
                FavoritesActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= 26) {

            NotificationChannel channel = new NotificationChannel("defaul", "Task Manager Notes ", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("");
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(1, b.build());


    }
//
//    public void createPopupDialog() {
//
//        dialogBuilder = new AlertDialog.Builder(this);
//        View view = getLayoutInflater().inflate(R.layout.popup, null);
//
//        task = (EditText) view.findViewById(R.id.task_POP_ID);
//        managerName = (EditText) view.findViewById(R.id.manager_name_POP_ID);
//        dueDate = (EditText) view.findViewById(R.id.DueDate_POP_ID);
//        saveBtn = (Button) view.findViewById(R.id.save_POP_BTN_ID);
//        summeryNote = view.findViewById(R.id.summery_POP_ID);
//
//        dialogBuilder.setView(view);
//        dialog = dialogBuilder.create();
//        dialog.show();
//
//        dueDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                DialogFragment datePicker = new DatePickerFragment();
//                datePicker.show(getSupportFragmentManager(), "date picker");
//
//            }
//        });
//
//        saveBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                //TODO: save to db
//                //TODO: Go to next screen
//
//                if (!task.getText().toString().isEmpty()  //&& !dueDate.getText().toString().isEmpty()
//                        && !managerName.getText().toString().isEmpty()) {
//                    saveTasksToDB(v);
//                    tasksCounterUpdater ();
//                }
//
//            }
//
//            private void saveTasksToDB(View v) {
//
//                Tasks task = new Tasks();
//
//                String newTask = ListActivity.this.task.getText().toString();
//                String newManagerName = managerName.getText().toString();
//                //TODO: Convert the due date to a type that the actual time will recognize
//                String newDueDate = dueDate.getText().toString();
//                String newNote = summeryNote.getText().toString();
//
//                task.setTask(newTask);
//                task.setManagerName(newManagerName);
//                task.setDueDate(newDueDate);
//                task.setSummeryNotes(newNote);
//
//                //save to db
//
//                db.addTasks(task);
//
//                Snackbar.make(v, "Task Saved!", Snackbar.LENGTH_LONG).show();
//                //TODO: Create a Counter View on the settings menu !!!
//                Log.d("Task Added ID: ", String.valueOf(db.getTasksCounter()));
//
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        dialog.dismiss();
//
//                        //refresh activity
//
//                        startActivity(new Intent(ListActivity.this, ListActivity.class));
//
//                    }
//                }, 1000); // = 1 second
//
//            }
//        });
//
//    }
//    //TODO: FIND OUT HOW TO DECREES THE COUNTER WHEN TASK DELETED
//    @SuppressLint("SetTextI18n")
//    public void tasksCounterUpdater () {
//
//        String counterV = String.valueOf (db.getTasksCounter());
//        countView.setText("Open Tasks: " + counterV);
//
//
//    }

    @Override
    public void getLocationCounter(int i) {
        countView.setText("Listing Locations: " + i);

    }


}