package com.example.adam.myusefulllocations.Fragment;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.adam.myusefulllocations.Constant.Constants;
import com.example.adam.myusefulllocations.R;
import com.example.adam.myusefulllocations.Util.PlaceOfInterest;

/**
 * Created by timbuchalka on 1/12/16.
 */

class CursorRecyclerViewAdapter extends RecyclerView.Adapter<CursorRecyclerViewAdapter.SearchViewHolder> {
    private static final String TAG = "CursorRecyclerViewAdapt";
    private Cursor mCursor;
    private OnPlaceClickListener mListener;

    interface OnPlaceClickListener {
        void onInfoDetailsClick(PlaceOfInterest place); //TODO: PASS DATA TO A DETAILS ACTIVITY FOR MORE FUNCTION LIKE WHETHER...
        void onSearchClick(PlaceOfInterest place); //TODO: PASS DATA TO NEARBY FRAGMENT
    }

    public CursorRecyclerViewAdapter(Cursor cursor, OnPlaceClickListener listener) {
        Log.d(TAG, "CursorRecyclerViewAdapter: Constructor called");
        mCursor = cursor;
        mListener = listener;
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_search, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchViewHolder holder, int position) {
//        Log.d(TAG, "onBindViewHolder: starts");

        if((mCursor == null) || (mCursor.getCount() == 0)) {
            Log.d(TAG, "onBindViewHolder: providing instructions");

            holder.name.setText(R.string.instructions_heading);
            holder.address.setText(R.string.instructions);

            holder.infoDetailsBtn.setVisibility(View.VISIBLE);  // TODO add onClick listener
            holder.searchBtn.setVisibility(View.VISIBLE);  // TODO add onClick listener

        } else {
            if(!mCursor.moveToPosition(position)) {
                throw new IllegalStateException("Couldn't move cursor to position " + position);
            }

            final PlaceOfInterest place = new PlaceOfInterest(mCursor.getInt(mCursor.getColumnIndex(Constants.KEY_SEARCH_ID)),
                    mCursor.getString(mCursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_ADDRESS)),
                    mCursor.getDouble(mCursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_LATITUDE)),
                    mCursor.getDouble(mCursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_LONGITUDE)),
                    mCursor.getString(mCursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_NAME)),

                    mCursor.getString(mCursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_IMAGE)),
                    mCursor.getDouble(mCursor.getColumnIndex(Constants.KEY_SEARCH_LOCATION_DISTANCE)));

            holder.name.setText(place.getName());
            holder.address.setText(place.getAddress());
            holder.infoDetailsBtn.setVisibility(View.VISIBLE);  // TODO add onClick listener
            holder.searchBtn.setVisibility(View.VISIBLE); // TODO add onClick listener

            View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {


        //TODO: DO SOMTHING... MAYBE A POPUP
                    Log.d(TAG, "onLongClick: starts");


                    return false;
                }
            };

            View.OnClickListener buttonListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Log.d(TAG, "onClick: starts");
                   //TODO: DO SOMETHING
                }
            };

        }

    }

    @Override
    public int getItemCount() {
//        Log.d(TAG, "getItemCount: starts");
        if((mCursor == null) || (mCursor.getCount() == 0)) {
            return 1; // fib, because we populate a single ViewHolder with instructions
        } else {
            return mCursor.getCount();
        }
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.
     * The returned old Cursor is <em>not</em> closed.
     *
     * @param newCursor The new cursor to be used
     * @return Returns the previously set Cursor, or null if there wasn't one.
     * If the given new Cursor is the same instance as the previously set
     * Cursor, null is also returned.
     */
   public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }

        final Cursor oldCursor = mCursor;
        mCursor = newCursor;
        if(newCursor != null) {
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            // notify the observers about the lack of a data set
            notifyItemRangeRemoved(0, getItemCount());
        }
        return oldCursor;

    }

    static class SearchViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "SearchViewHolder";

        TextView name = null;
        TextView address = null;
        TextView lat;
        TextView lng;
        TextView distance;
        ImageView placePhoto;

        ImageButton infoDetailsBtn = null;
        ImageButton searchBtn = null;

        public SearchViewHolder(View itemView) {
            super(itemView);
//            Log.d(TAG, "SearchViewHolder: starts");

            name = (TextView) itemView.findViewById(R.id.name_row_search_ID);
            address = (TextView) itemView.findViewById(R.id.address_Full_row_search_ID);
//            this.lat = itemView.findViewById(R.id.);
//            this.lng = itemView.findViewById(R.id.);
            distance = itemView.findViewById(R.id.distance_row_search_ID);

            placePhoto = itemView.findViewById(R.id.imageView_row_search_ID);



//            this.infoDetailsBtn = (ImageButton) itemView.findViewById(R.id.infoDetailsBtn_row_Fav_ID);
//            this.searchBtn = (ImageButton) itemView.findViewById(R.id.searchBtn_row_Fav_ID);
        }
    }
}
