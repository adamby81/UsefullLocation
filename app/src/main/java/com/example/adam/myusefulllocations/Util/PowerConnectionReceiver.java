package com.example.adam.myusefulllocations.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.Toast;

import com.example.adam.myusefulllocations.R;

import static com.example.adam.myusefulllocations.Activity.MainActivity.popOnceChecker;


public class PowerConnectionReceiver extends BroadcastReceiver {

    boolean acCharge;
    boolean usbCharge;

    int chargePlug;
    boolean isCharging;
    int status;


    @Override
    public void onReceive(Context context, Intent intent) {

        status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        isCharging = (status == BatteryManager.BATTERY_STATUS_CHARGING)
                || (status == BatteryManager.BATTERY_STATUS_FULL);

        chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        usbCharge = (chargePlug == BatteryManager.BATTERY_PLUGGED_USB);
        acCharge = (chargePlug == BatteryManager.BATTERY_PLUGGED_AC);

//
//        if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
//
//            popOnceChecker = true;
//
//        }else {
//
//            popOnceChecker = false;
//
//        }


        Log.e("onReceive", "start");




        if ((isCharging && usbCharge) && (popOnceChecker != 2)) {

            Toast.makeText(context, (R.string.usbCharge_Toast), Toast.LENGTH_LONG).show();
            popOnceChecker = 2;
        }

        if ((isCharging && acCharge) && (popOnceChecker != 3)) {

            Toast.makeText(context, (R.string.acCharge_Toast), Toast.LENGTH_LONG).show();
            popOnceChecker = 3;

        }



    }
}
