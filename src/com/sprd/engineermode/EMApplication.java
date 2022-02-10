package com.sprd.engineermode;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.unisoc.engineermode.core.CoreApi;

import android.app.NotificationManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.widget.RemoteViews;
import android.content.Intent;
import com.sprd.engineermode.telephony.NotificationBroadcastReceiver;
import android.content.SharedPreferences;

import com.unisoc.engineermode.core.intf.ITelephonyApi;

public class EMApplication extends Application {
    private static final String TAG = "EMApplication";
    private static Context mContext;
    private ITelephonyApi teleApi = CoreApi.getTelephonyApi();
    //private static NotificationManager mNotifiCation;
    private static final String NEW_MESSAGE = "Notice From EngineerMode!";
    private static final String CLOSE_NOTICE ="After test,please close this notificaiton";
    private static final String NOTIFICATION_CHANNEL_ID = "mobileServiceMessages";
    private static final int NOTIFICATION_ID = 0x1123;

    /* SPRD 940291 - [true demo version] Control charge region @{ */
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor, mEditor;
    /* }@ */

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        CoreApi.init(mContext);
        Log.d(TAG, "get engtest is " + teleApi.EngTestStatus().isEngTest());
        if (teleApi.EngTestStatus().isEngTest()) {
            initNotifiCation(mContext);
        }

        /* SPRD 940291 - [true demo version] Control charge region @{ */
        preferences = this.getSharedPreferences("charge_control", MODE_PRIVATE);
        editor = preferences.edit();
        editor.putBoolean("charge_func", false);
        editor.commit();
        /* }@ */

        preferences = this.getSharedPreferences("asdiv_control", this.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putBoolean("lte_dpdt_switch1", false);
        editor.putBoolean("lte_dpdt_switch2", false);
        editor.putBoolean("gsm_dpdt_switch1", false);
        editor.putBoolean("gsm_dpdt_switch2", false);
        editor.putBoolean("lte_pri_tr_div_close", false);
        editor.putBoolean("lte_pri_tx_div_rx", false);
        editor.putBoolean("gsm_pri_tr_div_close", false);
        editor.putBoolean("gsm_pri_tx_div_rx", false);
        editor.commit();
    }

    private static void initNotifiCation(Context context) {
        NotificationManager mNotifiCation = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        mNotifiCation.cancel(NOTIFICATION_ID);
        mNotifiCation.createNotificationChannel(new NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                context.getResources().getString(R.string.app_name),
                NotificationManager.IMPORTANCE_LOW));
        RemoteViews mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.notification);
        mRemoteViews.setTextViewText(R.id.mt_notification, NEW_MESSAGE+"\n"+"    "+CLOSE_NOTICE);
        Intent clickIntent = new Intent(context, NotificationBroadcastReceiver.class);
        clickIntent.putExtra("notificationId", NOTIFICATION_ID);
        PendingIntent pendingIntent= PendingIntent.getBroadcast(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.bt_notification, pendingIntent);
        Notification notify = new Notification.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContent(mRemoteViews)
            .setAutoCancel(true)
            .setTicker(NEW_MESSAGE)
            .setContentTitle(NEW_MESSAGE)
            .setPriority(Notification.PRIORITY_DEFAULT)
            .setOngoing(true)
            .setSmallIcon(R.drawable.cg)
            .build();
        mNotifiCation.notify(NOTIFICATION_ID, notify);
    }

    public static Context getContext(){
        return mContext;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

}
