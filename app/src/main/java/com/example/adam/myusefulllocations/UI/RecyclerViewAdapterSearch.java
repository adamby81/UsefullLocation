package com.example.adam.myusefulllocations.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.adam.myusefulllocations.Data.DatabaseHandler;
import com.example.adam.myusefulllocations.R;
import com.example.adam.myusefulllocations.Util.GlogalValues;
import com.example.adam.myusefulllocations.Util.PlaceOfInterest;

import java.util.List;

public class RecyclerViewAdapterSearch extends RecyclerView.Adapter<RecyclerViewAdapterSearch.ViewHolder> {

    private Context context;
    private List<PlaceOfInterest> placeOfInterestList;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog dialog;
    private LayoutInflater inflater;
    private GlogalValues counterVal;



    public RecyclerViewAdapterSearch(Context context, List<PlaceOfInterest> placeOfInterestList) {
        this.context = context;
        this.placeOfInterestList = placeOfInterestList;
    }

    @Override
    public RecyclerViewAdapterSearch.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_search, parent, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder( ViewHolder holder, int position) {


        PlaceOfInterest placeOfInterest = placeOfInterestList.get(position);

        holder.name.setText(placeOfInterest.getName());
        holder.address.setText(placeOfInterest.getAddress());
       // holder.imageRowSearch.setImageBitmap();




    }

    @Override
    public int getItemCount() {
        return placeOfInterestList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView name;
        public TextView address;
        public TextView latitude;
        public ImageView longitude;

        public ImageView imageRowSearch;

        public int id;

        public ViewHolder(View view, Context ctx) {
            super(view);

            context = ctx;

            name = (TextView) view.findViewById(R.id.name_row_search_ID);
            address = (TextView) view.findViewById(R.id.address_Full_row_search_ID);
            imageRowSearch = view.findViewById(R.id.imageView_row_search_ID);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: go to next screen
//
//                    int position = getAdapterPosition();
//                    PlaceOfInterest placeOfInterest = placeOfInterestList.get(position);
//
//
//                    Intent intent = new Intent(context, FavoritesActivity.class);
//
//                    intent.putExtra("address", placeOfInterest.getAddress());
//                    intent.putExtra("latitude", placeOfInterest.getLatitude());
//                    intent.putExtra("longitude", placeOfInterest.getLongitude());
//                    intent.putExtra("name", placeOfInterest.getName());
//                    intent.putExtra("image", placeOfInterest.getPhotoUrl());
//                    intent.putExtra("id", placeOfInterest.get_id());
//
//                    context.startActivity(intent);


                }
            });

        }

//        @Override
//        public void onClick(View v) {
//
//            switch (v.getId()) {
//                case R.id.editBTN_row_ID:
//
//                    int position = getAdapterPosition();
//                    PlaceOfInterest placeOfInterest = placeOfInterestList.get(position);
//                    Intent intent = new Intent(context, FavoritesActivity.class);
//
//
//                    intent.putExtra("address", placeOfInterest.getAddress());
//                    intent.putExtra("latitude", placeOfInterest.getLatitude());
//                    intent.putExtra("longitude", placeOfInterest.getLongitude());
//                    intent.putExtra("name", placeOfInterest.getName());
//                    intent.putExtra("image", placeOfInterest.getPhotoUrl());
//                    intent.putExtra("id", placeOfInterest.get_id());
//
//
//
//                    context.startActivity(intent);
//                    //editBtn(tasks);
//
//
//                    break;
//                case R.id.deleteBTN_row_ID:
//
//                    position  = getAdapterPosition();
//                    placeOfInterest = placeOfInterestList.get(position);
//                    deleteTask(placeOfInterest.get_id());
//                    DatabaseHandler db = new DatabaseHandler(context);
//
//
//                    break;
//
//            }
//
//        }

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


        @Override
        public void onClick(View v) {

        }
    }
}
