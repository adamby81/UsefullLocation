package com.example.adam.myusefulllocations.UI;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import com.example.adam.myusefulllocations.Fragment.ItemSearchFragment.OnListFragmentInteractionListener;
import com.example.adam.myusefulllocations.R;
import com.example.adam.myusefulllocations.Util.PlaceOfInterest;

import java.util.List;

/**
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {


    private List<PlaceOfInterest> placeOfInterestList;

    private Context context;



// here was a DummyItem list as content
    public MyItemRecyclerViewAdapter(Context mContext, List<PlaceOfInterest> placesList) {
        this.placeOfInterestList = placesList;
        this.context = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        PlaceOfInterest place = placeOfInterestList.get(position);

        holder.name.setText(place.getName());
        holder.address.setText(place.getAddress());
       // holder.distance.setText((int)place.getDistance());
        Picasso.get().load(place.getPhotoUrl()).into(holder.photo);

//        holder.mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (null != mListener) {
//                    // Notify the active callbacks interface (the activity, if the
//                    // fragment is attached to one) that an item has been selected.
//                   // mListener.onListFragmentInteraction(holder.m);
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return placeOfInterestList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView address;
        public TextView name;
        public TextView distance;
        public ImageView photo;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            name = (TextView) view.findViewById(R.id.name_row_search_ID);
            address = (TextView) view.findViewById(R.id.address_Full_row_search_ID);
            distance = (TextView) view.findViewById(R.id.distance_row_search_ID);
            photo = view.findViewById(R.id.imageView_row_search_ID);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // TODO: GO TO MAPS FRAGMENT AT THE SAME ITEM LOCATION

                }
            });

        }

//        @Override
//        public String toString() {
//            return super.toString() + " '" + mContentView.getText() + "'";
//        }
    }
}
