package com.sprd.engineermode.hardware;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.content.SharedPreferences;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.os.SystemClock;
import android.app.Service;
import android.os.IBinder;

import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.intf.IHardwareApi;
import com.sprd.engineermode.utils.EMFileUtils;

public class CoulometerPowerService extends Service {

    public static final String TAG = "CoulometerPowerService";
    private AlarmManager alarmManager;
    private Intent mIntent;
    private PendingIntent pi1, pi2, pi3, pi1_end, pi2_end, pi3_end;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private static final int TWINTY_MINUTES = 20;//1 minute
    private static final int TEN_MINUTES = 10;//10 minute
    private static final int TEN_HOURS = 60 * 10;//60 minute
    private IHardwareApi hwApi = CoreApi.getHardwareApi();

    private static final String CC_TEST_CMD_PATH = "/sys/class/power_supply/sprdfgu/";

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "CoulometerPowerService onBind");
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "CoulometerPowerService onCreate");
        alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        mIntent = new Intent(this, CoulometerPowerReceiver.class);
        preferences = this.getSharedPreferences("cc_status", MODE_PRIVATE);
        editor = preferences.edit();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d(TAG, "CoulometerPowerService onStart");

        try {
            if (EMFileUtils.isFileDirExits(CC_TEST_CMD_PATH)) {
                hwApi.coulometerPowerApi().writeOneToCCTestCmd();
            }
            long timeDuring = 0;
            String dur = intent.getStringExtra("duration");
            String time = intent.getStringExtra("time");

            mIntent.putExtra("duration", dur);
            mIntent.putExtra("time", time);
            int t = Integer.parseInt(dur);
            if (t > TWINTY_MINUTES && t <= TEN_HOURS) {
                timeDuring = SystemClock.elapsedRealtime() + 60 * 10 * 1000;
            } else if (t <= TWINTY_MINUTES) {
                timeDuring = SystemClock.elapsedRealtime() + t * 60 * 1000;
            } else {
                timeDuring = SystemClock.elapsedRealtime() + 60 * 60 * 1000;
            }
            //timeDuring = SystemClock.elapsedRealtime() + 3 * 60 * 1000;
            Log.d(TAG, "new >>> CoulometerPowerService dur: " + dur + " time: " + time + " timeDuring: " + timeDuring);
            if (time.equals("time1") && !CoulometerPowerTestActivity.timerStop[0]) {
                pi1 = PendingIntent.getBroadcast(this, 10, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, timeDuring, pi1);
            } else if (time.equals("time2") && !CoulometerPowerTestActivity.timerStop[1]) {
                pi2 = PendingIntent.getBroadcast(this, 11, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, timeDuring, pi2);
            } else if (time.equals("time3") && !CoulometerPowerTestActivity.timerStop[2]) {
                pi3 = PendingIntent.getBroadcast(this, 12, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, timeDuring, pi3);
            } else if (time.equals("time1_end")) {
                alarmManager.cancel(pi1);
                CoulometerPowerTestActivity.timerStop[0] = true;
                editor.putBoolean("time1_stop", true);
                editor.commit();
            } else if (time.equals("time2_end")) {
                alarmManager.cancel(pi2);
                CoulometerPowerTestActivity.timerStop[1] = true;
                editor.putBoolean("time2_stop", true);
                editor.commit();
            } else if (time.equals("time3_end")) {
                alarmManager.cancel(pi3);
                CoulometerPowerTestActivity.timerStop[2] = true;
                editor.putBoolean("time3_stop", true);
                editor.commit();
            }
        } catch (Exception ex) {
            Log.d(TAG, "onStart Exception: " + ex);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "CoulometerPowerService onDestroy");
    }
}