package com.sprd.engineermode.hardware;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Timer;

import android.os.Handler;

import java.text.SimpleDateFormat;
import android.content.SharedPreferences;
import android.os.PowerManager;
import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.intf.IHardwareApi;
import com.sprd.engineermode.utils.EMFileUtils;

public class CoulometerPowerReceiver extends BroadcastReceiver  {

    private static final String TAG = "CoulometerPowerReceiver";

    private Context mContext;
    private static final String CC_TEST_CMD_PATH = "/sys/class/power_supply/sprdfgu/";
    private static final String CC_TEST_RESULT_PATH = "/sys/class/power_supply/sprdfgu/cc_test_result";

    private int period;
    private static final int UPDATE_UI_END = 1;

    private Timer[] timer = new Timer[3];
    private Handler mHandler = new Handler();

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private PowerManager.WakeLock wakeLock = null;
    private IHardwareApi hwApi = CoreApi.getHardwareApi();

    private String dur;
    private String time;
    private float mAveEnergy;
    private float mAveVoltage;
    private boolean isCCTestCmdExist = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        mContext = context;

        dur = intent.getStringExtra("duration");
        time = intent.getStringExtra("time");
        Log.d(TAG, "CoulometerPowerReceiver dur: " + dur + " time: " + time);
        isCCTestCmdExist = EMFileUtils.isFileDirExits(CC_TEST_CMD_PATH);
        Log.d(TAG, "isFileDirExits /sys/class/power_supply/sprdfgu: " + isCCTestCmdExist);

        CoulometerPowerTimer(time);

        Intent service = new Intent(context, CoulometerPowerService.class);
        service.putExtra("duration", dur);
        service.putExtra("time", time);
        //startWakefulService(context, service);
        mContext.startService(service);
    }

    private void CoulometerPowerTimer(String timePeriod) {
        int index = 0;
        try {
            if (timePeriod.equals("time1")) {
                index = 0;
            } else if (timePeriod.equals("time2")) {
                index = 1;
            } else if (timePeriod.equals("time3")) {
                index = 2;
            }
            SimpleDateFormat sDateFormat = new SimpleDateFormat("HH:mm");
            if (isCCTestCmdExist) {
                hwApi.coulometerPowerApi().writeZeroToCCTestCmd();
                CoulometerPowerTestActivity.mCCValue[index] = hwApi.coulometerPowerApi().getCcTestResult().split("\\s+")[0].replaceAll("AVG_CUR", "power");
                CoulometerPowerTestActivity.mCCValueVol[index] = "   Voltage:" + hwApi.coulometerPowerApi().getCcTestVoltages() + "mV";
            } else {
                String mCurEnergy = hwApi.coulometerPowerApi().getCcResultNewKernel();
                Log.d(TAG, "mCurEnergy: " + mCurEnergy + " startEnergy: " + CoulometerPowerTestActivity.mCCValue[index] + " durTime: " + dur);
                mAveEnergy = ((Float.valueOf(mCurEnergy) - Float.valueOf(CoulometerPowerTestActivity.mCCValue[index])) * 72 / 10 ) / (Integer.valueOf(dur) * 60 * 2);
                CoulometerPowerTestActivity.mCCValue[index] = "    Time: " + dur + "Min\n" + "   Energy: " + String.valueOf(mAveEnergy).replaceAll("-", "") + "mA";
                String mVol = hwApi.coulometerPowerApi().getCcVoltNewKernel().replaceAll("\r|\n", "");
                mAveVoltage = Float.valueOf(mVol) / 1000;
                CoulometerPowerTestActivity.mCCValueVol[index] = "   Voltage: " + String.valueOf(mAveVoltage) + "mV";
            }
            Log.d(TAG, "hwApi CoulometerPowerTimer mCCValue: "
                        + CoulometerPowerTestActivity.mCCValue[index]
                        + " mCCValueVol: "
                        + CoulometerPowerTestActivity.mCCValueVol[index]);
            if (timePeriod.equals("time1")) {
                if (isCCTestCmdExist) {
                    CoulometerPowerTestActivity.testResults1.add(getCCResult(CoulometerPowerTestActivity.mCCValue[index]));
                } else {
                    CoulometerPowerTestActivity.testResults1.add(String.valueOf(mAveEnergy));
                }
                CoulometerPowerTestActivity.testTimes1.add(sDateFormat.format(new java.util.Date()));
            } else if (timePeriod.equals("time2")) {
                if (isCCTestCmdExist) {
                    CoulometerPowerTestActivity.testResults2.add(getCCResult(CoulometerPowerTestActivity.mCCValue[index]));
                } else {
                    CoulometerPowerTestActivity.testResults2.add(String.valueOf(mAveEnergy));
                }
                CoulometerPowerTestActivity.testTimes2.add(sDateFormat.format(new java.util.Date()));
            } else if (timePeriod.equals("time3")) {
                if (isCCTestCmdExist) {
                    CoulometerPowerTestActivity.testResults3.add(getCCResult(CoulometerPowerTestActivity.mCCValue[index]));
                } else {
                    CoulometerPowerTestActivity.testResults3.add(String.valueOf(mAveEnergy));
                }
                CoulometerPowerTestActivity.testTimes3.add(sDateFormat.format(new java.util.Date()));
            } else {
                if (isCCTestCmdExist) {
                    CoulometerPowerTestActivity.testResults1.add(getCCResult(CoulometerPowerTestActivity.mCCValue[index]));
                } else {
                    CoulometerPowerTestActivity.testResults1.add(String.valueOf(mAveEnergy));
                }
                CoulometerPowerTestActivity.testTimes1.add(sDateFormat.format(new java.util.Date()));
            }
            if (isCCTestCmdExist) {
                hwApi.coulometerPowerApi().writeOneToCCTestCmd();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getCCResult(String value) {
        String [] strings = value.split(":");
        if (strings.length > 1) {
            String str = strings[strings.length - 1].split("m")[0];
            Log.d(TAG, "getCCResult: " + str);
            return str;
        } else {
            return "";
        }
    }
}