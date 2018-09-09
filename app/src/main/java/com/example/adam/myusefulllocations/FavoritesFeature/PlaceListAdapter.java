package com.example.adam.myusefulllocations.FavoritesFeature;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.adam.myusefulllocations.R;
import com.google.android.gms.location.places.PlaceBuffer;


public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.PlaceViewHolder> {

    private Context mContext;
    private PlaceBuffer mPlaces;

    public PlaceListAdapter(Context mContext, PlaceBuffer Places) {
        this.mContext = mContext;
        this.mPlaces = Places;
    }

//    @Override
//    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//
//        LayoutInflater inflater = LayoutInflater.from(mContext);
//        View view = inflater.inflate(R.layout.item_place_card, parent, false);
//        return new PlaceViewHolder(view);
//
//
//    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_place_card, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceListAdapter.PlaceViewHolder holder, int position) {

        String placeName = mPlaces.get(position).getName().toString();
        String placeAddress = mPlaces.get(position).getAddress().toString();

        holder.nameTextView.setText(placeName);
        holder.addressTextView.setText(placeAddress);


    }

    public void swapPlaces (PlaceBuffer newPlace) {

        mPlaces = newPlace;
        if (mPlaces != null) {

            this.notifyDataSetChanged();

        }

    }

    @Override
    public int getItemCount() {
        if(mPlaces==null)return 0;
        return mPlaces.getCount();
    }


    public class PlaceViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        TextView addressTextView;


        public PlaceViewHolder(View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.name_text_view);
            addressTextView = itemView.findViewById(R.id.address_text_view);

        }
    }
}
