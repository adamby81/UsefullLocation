package com.example.adam.myusefulllocations.Util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.adam.myusefulllocations.R;

public class ImageNearbyAdapter extends BaseAdapter {


    private Context ctx;

    private Integer imageId [] = {
            R.drawable.gym,
            R.drawable.atm,
            R.drawable.gas,
            R.drawable.hospital,
            R.drawable.pharmacy,
            R.drawable.supermarket,
            R.drawable.restaurant,
            R.drawable.mall,
            R.drawable.movie};

    public ImageNearbyAdapter(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public int getCount() {
        return imageId.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        imageView = new ImageView(ctx);

        if (convertView == null) {

            imageView.setLayoutParams(new GridView.LayoutParams(160,160));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8,8,8,8);
        }else {

            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(imageId[position]);





        return imageView;
    }
}
