package com.sprd.engineermode.telephony;

import com.sprd.engineermode.EMApplication;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.WindowManager;

import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.intf.ITelephonyApi;

public class NotificationBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationBroadcastReceiver";
    private static final String message = "please restart the phone or set airplane mode to "
        + "sync the network in settings";
    private int NOTIFICATION_ID = 0x1123;
    private Context mAppContext;
    private ITelephonyApi teleApi = CoreApi.getTelephonyApi();

    @Override
    public void onReceive(Context context, Intent intent) {
        int type = intent.getIntExtra("notificationId", -1);
        mAppContext = EMApplication.getContext();
        Log.d(TAG, "type is"+type);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(type == NOTIFICATION_ID){
            Log.d(TAG, "run here");
            teleApi.EngTestStatus().set(0);
            Log.d(TAG, "get vSystemProperties is " + teleApi.EngTestStatus().isEngTest());
            notificationManager.cancel(NOTIFICATION_ID);
            AlertDialog.Builder builder = new AlertDialog.Builder(mAppContext);
            builder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    mAppContext = null;
                }
            });
            AlertDialog alert = builder.create();
            alert.setCancelable(false);
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            alert.show();

        }
    }

}
