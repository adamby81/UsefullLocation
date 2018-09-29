package com.example.adam.myusefulllocations.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Global{
    Context mContext;


    public Global(Context context){
        this.mContext = context;
    }


    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null) {


            return false;
        } else
            return true;
    }
}
