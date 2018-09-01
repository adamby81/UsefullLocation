package com.example.adam.myusefulllocations.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.adam.myusefulllocations.Activity.FavoritesActivity;
import com.example.adam.myusefulllocations.Data.DatabaseHandler;
import com.example.adam.myusefulllocations.R;
import com.example.adam.myusefulllocations.Util.GlogalValues;
import com.example.adam.myusefulllocations.Util.PlaceOfInterest;

import java.util.List;

public class RecyclerViewAdapterFav extends RecyclerView.Adapter<RecyclerViewAdapterFav.ViewHolder> {

    private Context context;
    private List<PlaceOfInterest> placeOfInterestList;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog dialog;
    private LayoutInflater inflater;
    private GlogalValues counterVal;



    public RecyclerViewAdapterFav(Context context, List<PlaceOfInterest> placeOfInterestList) {
        this.context = context;
        this.placeOfInterestList = placeOfInterestList;
    }

    @Override
    public RecyclerViewAdapterFav.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_favorites, parent, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder( ViewHolder holder, int position) {


        PlaceOfInterest placeOfInterest = placeOfInterestList.get(position);

        holder.address.setText(placeOfInterest.getAddress());
        holder.latitude.setText((int) placeOfInterest.getLatitude());
        holder.longitude.setText((int) placeOfInterest.getLongitude());
//        holder.altitude.setText((int) placeOfInterest.getAltitude());



    }

    @Override
    public int getItemCount() {
        return placeOfInterestList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView address;
        public TextView latitude;
        public TextView longitude;
        public TextView altitude;

        public Button editBtn;
        public Button deleteBtn;

        public int id;

        public ViewHolder(View view, Context ctx) {
            super(view);

            context = ctx;

            address = (TextView) view.findViewById(R.id.address_Full_row_ID);
            latitude = (TextView) view.findViewById(R.id.latitude_row_ID);
            longitude = (TextView) view.findViewById(R.id.longitude_row_ID);
            altitude = (TextView) view.findViewById(R.id.altitude_row_ID);

            editBtn = (Button) view.findViewById(R.id.editBTN_row_ID);
            deleteBtn = (Button) view.findViewById(R.id.deleteBTN_row_ID);

            editBtn.setOnClickListener(this);
            deleteBtn.setOnClickListener(this);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: go to next screen

                    int position = getAdapterPosition();
                    PlaceOfInterest placeOfInterest = placeOfInterestList.get(position);


                    Intent intent = new Intent(context, FavoritesActivity.class);

                    intent.putExtra("address", placeOfInterest.getAddress());
                    intent.putExtra("latitude", placeOfInterest.getLatitude());
                    intent.putExtra("longitude", placeOfInterest.getLongitude());
                    intent.putExtra("name", placeOfInterest.getName());
                    intent.putExtra("id", placeOfInterest.get_id());

                    context.startActivity(intent);


                }
            });

        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.editBTN_row_ID:

                    int position = getAdapterPosition();
                    PlaceOfInterest placeOfInterest = placeOfInterestList.get(position);
                    Intent intent = new Intent(context, FavoritesActivity.class);


                    intent.putExtra("address", placeOfInterest.getAddress());
                    intent.putExtra("latitude", placeOfInterest.getLatitude());
                    intent.putExtra("longitude", placeOfInterest.getLongitude());
                    intent.putExtra("name", placeOfInterest.getName());
                    intent.putExtra("image", placeOfInterest.getPhotoUrl());
                    intent.putExtra("id", placeOfInterest.get_id());



                    context.startActivity(intent);
                    //editBtn(tasks);


                    break;
                case R.id.deleteBTN_row_ID:

                    position  = getAdapterPosition();
                    placeOfInterest = placeOfInterestList.get(position);
                    deleteTask(placeOfInterest.get_id());
                    DatabaseHandler db = new DatabaseHandler(context);


                    break;

            }

        }

        //TODO: SPECIFY THE RESONE FOR ENDING THE TASK
        //TODO: ADD A SCROLLING BAR FROM 0-100 TO RATE THE PERFORMANCE OF THE MANAGER
        public void deleteTask (final int id){

            // create an alert dialog
            alertDialogBuilder = new AlertDialog.Builder(context);

            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.confermation_dialog, null);

            Button cancelBtn = (Button) view.findViewById(R.id.cancelBtn_Dialog_ID);
            Button okBtn = view.findViewById(R.id.okBtn_Dialog_ID);

            alertDialogBuilder.setView(view);
            dialog = alertDialogBuilder.create();
            dialog.show();

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();

                }
            });

            okBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: DELETE FROM THE DB AND !!! WRITE TO A NEW TABLE OF CLOSED ASSIGNMENTS

                    DatabaseHandler db = new DatabaseHandler(context);
                    // delete the task
                    db.deleteLocation(id);
//                    db.getTasksCounter();
//                    String c = String.valueOf(db.getTasksCounter());

                    placeOfInterestList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());

                    // db.getTasksCounter()
                    dialog.dismiss();
                    int counter = db.getLocationsCounter();

                    counterVal = (GlogalValues)context;
                    counterVal.getLocationCounter(counter);
                }
            });

        }






    }
}
