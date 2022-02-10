
package com.sprd.engineermode.connectivity.wifi;

import com.sprd.engineermode.R;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import com.sprd.engineermode.EMSwitchPreference;
import android.util.Log;
import android.widget.Toast;
import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.exception.EmException;
import com.unisoc.engineermode.core.exception.OperationFailedException;
import com.unisoc.engineermode.core.intf.IConnectivityApi;
import com.unisoc.engineermode.core.utils.SocketUtils;
import com.unisoc.engineermode.core.common.Const;

public class WifiEUTActivity extends PreferenceActivity implements
        Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private static final String TAG = "WifiTestActivity";
    private static final String KEY_POWER_SAVE_MODE = "wifi_power_save_mode";
    private static final String KEY_WIFI_TX = "wifi_tx";
    private static final String KEY_WIFI_RX = "wifi_rx";
    private static final String KEY_WIFI_REG = "wifi_reg_wr";

    // socket communication message
    private static final int DISABLED_POWER_SAVE = 0;
    private static final int ENABLED_POWER_SAVE = 1;
    private static final int INIT_TEST_STATUS = 2;
    private static final int DEINIT_TEST_STATUS = 3;

    private static final String INSMODE_RES = "insmode_result";

    private EMSwitchPreference mDisPowerSaveMode;
    private Preference mWifi_TX;
    private Preference mWifi_RX;
    private Preference mWifi_REG;

    private ProgressDialog mProgress;

    private Handler mUiThread = new Handler();
    private WTHandler mWTHandler;

    private boolean mInsmodeSuccess = false;
    private IConnectivityApi.IWcndEngControl wcndEngCtl = CoreApi.getConnectivityApi().wcndEngControl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_wifi_test);
        mDisPowerSaveMode = (EMSwitchPreference) findPreference(KEY_POWER_SAVE_MODE);
        mDisPowerSaveMode.setOnPreferenceChangeListener(this);
        mDisPowerSaveMode.setEnabled(false);

        mWifi_TX = (Preference) findPreference(KEY_WIFI_TX);
        mWifi_TX.setOnPreferenceClickListener(this);
        mWifi_TX.setEnabled(false);

        mWifi_RX = (Preference) findPreference(KEY_WIFI_RX);
        mWifi_RX.setOnPreferenceClickListener(this);
        mWifi_RX.setEnabled(false);

        mWifi_REG = (Preference) findPreference(KEY_WIFI_REG);
        mWifi_REG.setOnPreferenceClickListener(this);
        mWifi_REG.setEnabled(false);

        mProgress = ProgressDialog.show(this, "Wifi Initing...", "Please wait...", true, true);

        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mWTHandler = new WTHandler(ht.getLooper());
        try {
            if (!wcndEngCtl.isWcndEngRunning()) {
                Log.d(TAG, "startWcndEng");
                wcndEngCtl.startWcndEng();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mWTHandler.postDelayed(runnable, 500);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (wcndEngCtl.isWcndEngRunning()) {
                    Log.d(TAG, "isWcndEngRunning INIT_TEST_STATUS");
                    Message initStatus = mWTHandler.obtainMessage(INIT_TEST_STATUS);
                    mWTHandler.sendMessage(initStatus);
                } else {
                    mWTHandler.postDelayed(this, 500);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onBackPressed() {
        Log.d(TAG,"onBackPressed() mProgress="+mProgress);
        if (mProgress != null) {
            mProgress.dismiss();
            finish();
        } else {
            Message deInitStatus = mWTHandler.obtainMessage(DEINIT_TEST_STATUS);
            mWTHandler.sendMessage(deInitStatus);
        }
    }

    @Override
    protected void onDestroy() {
        mWTHandler.removeCallbacks(runnable);
        try {
            if (wcndEngCtl.isWcndEngRunning()) {
                wcndEngCtl.stopWcndEng();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public boolean onPreferenceChange(Preference pref, Object objValue) {
        Message switchMsg = null;
        if (pref.getKey().equals(KEY_POWER_SAVE_MODE)) {
            if (mDisPowerSaveMode.isChecked()) {
                switchMsg = mWTHandler.obtainMessage(DISABLED_POWER_SAVE);
            } else {
                switchMsg = mWTHandler.obtainMessage(ENABLED_POWER_SAVE);
            }
            mWTHandler.sendMessage(switchMsg);
        }
        return false;
    }

    @Override
    public boolean onPreferenceClick(Preference pref) {
        Intent intent = new Intent();
        intent.putExtra(INSMODE_RES, mInsmodeSuccess);
        Log.d(TAG, "WifiEUTActivity insmode result is " + mInsmodeSuccess);
        if (pref.getKey().equals(KEY_WIFI_TX)) {
            intent.setClassName("com.sprd.engineermode",
                    "com.sprd.engineermode.connectivity.wifi.WifiTXActivity");
        } else if (pref.getKey().equals(KEY_WIFI_RX)) {
            intent.setClassName("com.sprd.engineermode",
                    "com.sprd.engineermode.connectivity.wifi.WifiRXActivity");
        } else if (pref.getKey().equals(KEY_WIFI_REG)) {
            intent.setClassName("com.sprd.engineermode",
                    "com.sprd.engineermode.connectivity.wifi.WifiREGWRActivity");
        }
        startActivity(intent);
        return false;
    }

    class WTHandler extends Handler {

        public WTHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INIT_TEST_STATUS:
                    if (WifiEUTHelper.getHelper().insMod()
                            && WifiEUTHelper.getHelper().start()) {
                        mInsmodeSuccess = true;
                        // enable other view
                        mUiThread.post(() -> {
                            mProgress.dismiss();
                            mProgress = null;
                            mWifi_TX.setEnabled(true);
                            mWifi_RX.setEnabled(true);
                            mWifi_REG.setEnabled(true);
                            if (Const.isMarlin()) {
                                mDisPowerSaveMode.setEnabled(true);
                            } else {
                                mDisPowerSaveMode.setEnabled(false);
                            }
                        });
                        if (!Const.isMarlin()) {
                            break;
                        }
                        boolean status;
                        try {
                            status = WifiEUTHelper.getHelper().getPowerSaveState();
                        } catch (EmException e) {
                            mUiThread.post(() -> {
                                mDisPowerSaveMode.setEnabled(false);
                                Toast.makeText(WifiEUTActivity.this,
                                    "GET_Power_Save_Mode Fail", Toast.LENGTH_SHORT)
                                    .show();
                            });
                            break;
                        }

                        if (status) {
                            mUiThread.post(() -> mDisPowerSaveMode.setChecked(true));
                        } else  {
                            mUiThread.post(() -> mDisPowerSaveMode.setChecked(false));
                        }
                    } else {
                        Toast.makeText(WifiEUTActivity.this,
                                "Insmod /system/lib/modules/sprdwl.ko or Start Wifi Fail",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    break;
                case DISABLED_POWER_SAVE:
                    try {
                        WifiEUTHelper.getHelper().disablePowerSave();
                    } catch (EmException e) {
                        mUiThread.post(() -> {
                            mDisPowerSaveMode.setChecked(false);
                            Toast.makeText(WifiEUTActivity.this, "DISABLED_POWER_SAVE Fail",
                                Toast.LENGTH_SHORT).show();
                        });
                        break;
                    }

                    mUiThread.post(() -> mDisPowerSaveMode.setChecked(false));
                    break;
                case ENABLED_POWER_SAVE:
                    try {
                        WifiEUTHelper.getHelper().enablePowerSave();
                    } catch (EmException e){
                        mUiThread.post(() -> {
                            mDisPowerSaveMode.setChecked(true);
                            Toast.makeText(WifiEUTActivity.this, "ENABLED_POWER_SAVE Fail",
                                Toast.LENGTH_SHORT).show();
                        });
                        break;
                    }

                    mUiThread.post(() -> mDisPowerSaveMode.setChecked(true));
                    break;
                case DEINIT_TEST_STATUS:
                    if (!WifiEUTHelper.getHelper().stop()
                            || !WifiEUTHelper.getHelper().removeMod()) {
                        Toast.makeText(WifiEUTActivity.this,
                                "Rmmod /system/lib/modules/sprdwl.ko or Stop Wifi Fail",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        mInsmodeSuccess = false;
                    }
                    finish();
                    break;
                default:
                    break;
            }
        }
    }
}
