package com.sprd.engineermode.debuglog;


import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.utils.IATUtils;

import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.os.Looper;
import android.os.IBinder;
import android.widget.Toast;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;


public class ModemAssertTestService extends Service {

    private String TAG = "ModemAssertTestService";

    private Toast mToast;
    private String mTime;
    private String mAt;
    private String mResult;
    private Timer mTimer;

    private String mModemAt = "AT+SPTEST=45,1,";
    private int mDelayTime = 0;
    private int mNumber = 0;
    private int mRestartTime = 0;

    private Context mContext;
    private NotificationManager mNotificationManager;
    private Notification mNotify;
    private String mName;
    private String mChannelId = "4815";
    private int mStart;
    private WakeLock mWakeLock;


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mNotificationManager = mContext.getSystemService(NotificationManager.class);
        acquireWakeLock();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            mDelayTime = intent.getIntExtra("delaytime", 10);
            mNumber = intent.getIntExtra("number", 10);
            mRestartTime = intent.getIntExtra("restarttime", 20);
            cmdLoopExcute();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (mTimer != null) {
            mTimer.cancel();
            Log.d(TAG, " onDestroy :  Timer has canceled ");
        }
        if (mWakeLock != null)
        {
            mWakeLock.release();
            mWakeLock = null;
        }
        super.onDestroy();
    }

    private void acquireWakeLock()
    {
        if (mWakeLock == null)
        {
            PowerManager pm = (PowerManager)mContext.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE, TAG);
            if (mWakeLock != null)
            {
                mWakeLock.acquire();
            }
        }
    }

    private void updateNotification() {
        mName = String.valueOf(mNumber);
        String title = "Modem Assert Test";
        NotificationChannel channel = new NotificationChannel(mChannelId , mName,
                NotificationManager.IMPORTANCE_MIN);
        mNotificationManager.createNotificationChannel(channel);
        Notification mNotify = new Notification.Builder(mContext, mChannelId)
                .setContentText(mName + "time(s) left")
                .setContentTitle(title)
                .setSmallIcon(R.drawable.cg)
                .build();
        startForeground(1, mNotify);
    }

    public void cmdLoopExcute() {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, " Timer start ");
                if (mNumber <= 0 && mTimer != null) {
                    mTimer.cancel();
                    Log.d(TAG, " service stop " );
                    stopSelf();
                    return;
                }
                mTime = String.valueOf(mDelayTime * 1000);
                mAt = mModemAt + mTime;
                mNumber--;
                sendCmd();
                Log.d(TAG, " assert number = " + mNumber);
            }
        }, 0, (mDelayTime + mRestartTime) * 1000);
    }

    private void sendCmd() {
        mResult = IATUtils.sendATCmd(mAt, "atchannel0");
        Log.d(TAG, " modem: result = " + mResult + " modem: at  = " + mAt);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                String str;
                if (mResult.contains(IATUtils.AT_OK)) {
                    str = "modem assert succeed";
                } else {
                    str = "modem assert fail";
                }
                updateNotification();
                mToast = Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT);
                mToast.show();
            }
        });
    }
}
