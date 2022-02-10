package com.sprd.engineermode.gcf;

import android.os.Bundle;
import android.util.Log;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.telephony.TelephonyManager;

import com.sprd.engineermode.EMSwitchPreference;
import com.unisoc.engineermode.core.impl.nonpublic.TelephonyManagerProxy;
import com.unisoc.engineermode.core.utils.IATUtils;
import com.unisoc.engineermode.core.common.engconstents;
import android.os.PowerManager;
import android.content.Context;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.sprd.engineermode.R;

public class AutoTestBySIMActivity extends PreferenceActivity {

    private static final String TAG = "AutoTestBySIMActivity";

    private static final String KEY_APC = "apc_key";
    private EMSwitchPreference mAPCTest;
    private String mStrTmp;
    private int mPhoneCount;
    private int mPhoneId = 0;

    private TelephonyManager[] mTelephonyManager;
    private Handler mUiThread = new Handler();
    private APCHandler mTHHandler;

    private static final int MSG_GET_APC_STATUS=1;
    private static final int MSG_SET_APC_ENABLE=2;
    private static final int MSG_SET_APC_DISABLE=3;

    private String str = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_delta_nv_auto);

        mPhoneCount = TelephonyManagerProxy.getPhoneCount();
        mTelephonyManager = new TelephonyManager[mPhoneCount];

        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mTHHandler = new APCHandler(ht.getLooper());

        mAPCTest = (EMSwitchPreference) findPreference(KEY_APC);
        mAPCTest.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Log.d(TAG, "mAPCTest onPreferenceClick");
                if (!mAPCTest.isChecked()) {
                    Log.d(TAG, "mAPCTest onPreferenceClick MSG_SET_APC_DISABLE");
                    Message setAPCEnable = mTHHandler.obtainMessage(MSG_SET_APC_DISABLE);
                    mTHHandler.sendMessage(setAPCEnable);
                } else {
                    Log.d(TAG, "mAPCTest onPreferenceClick MSG_SET_APC_ENABLE");
                    Message setAPCEnable = mTHHandler.obtainMessage(MSG_SET_APC_ENABLE);
                    mTHHandler.sendMessage(setAPCEnable);
                }
                return false;
            }
        });
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        Message getAPCState = mTHHandler.obtainMessage(MSG_GET_APC_STATUS);
        mTHHandler.sendMessage(getAPCState);
        super.onStart();
    }

    class APCHandler extends Handler {
        public APCHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
               case MSG_GET_APC_STATUS:
                    str = "SIM1: ";
                    mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_GET_SPAPC, "atchannel0");
                    Log.d(TAG, "MSG_GET_APC_STATUS SIM1 return : " + mStrTmp);
                    if (mStrTmp != null && mStrTmp.contains(IATUtils.AT_OK) && mStrTmp.contains("1")) {
                        String[] res = mStrTmp.split("\\,");
                        for (int i = 0; i < res.length; i ++) {
                            Log.d(TAG, "MSG_GET_APC_STATUS SIM1 res " + i + " = " + res[i]);
                            if (res[i].contains("\"")) {
                                str += res[i] + ",";
                            }
                        }
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mAPCTest.setChecked(true);
                                if (str.length() > 1) {
                                    mAPCTest.setSummary(str.substring(0, str.length() - 1));
                                }
                            }
                        });
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mAPCTest.setChecked(false);
                                mAPCTest.setSummary("");
                            }
                        });
                    }
                    mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_GET_SPAPC, "atchannel1");
                    Log.d(TAG, "MSG_GET_APC_STATUS SIM2 return : " + mStrTmp);
                    if (mStrTmp != null && mStrTmp.contains(IATUtils.AT_OK) && mStrTmp.contains("1")) {
                        str += "\nSIM2: ";
                        String[] res = mStrTmp.split("\\,");
                        for (int i = 0; i < res.length; i ++) {
                            Log.d(TAG, "MSG_GET_APC_STATUS SIM2 res " + i + " = " + res[i]);
                            if (res[i].contains("\"")) {
                                str += res[i] + ",";
                            }
                        }
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mAPCTest.setChecked(true);
                                if (str.length() > 1) {
                                    mAPCTest.setSummary(str.substring(0, str.length() - 1));
                                }
                            }
                        });
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mAPCTest.setChecked(false);
                                mAPCTest.setSummary("");
                            }
                        });
                    }
                    break;
                case MSG_SET_APC_ENABLE:
                    mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SET_SPAPC_ENABLE, "atchannel0");
                    Log.d(TAG, "MSG_SET_APC_ENABLE return : " + mStrTmp);
                    if (mStrTmp != null && mStrTmp.contains(IATUtils.AT_OK)) {
                        AlertDialogShow("");
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mAPCTest.setChecked(true);
                            }
                        });
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mAPCTest.setChecked(false);
                            }
                        });
                    }
                    break;
                case MSG_SET_APC_DISABLE:
                    mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SET_SPAPC_DISABLE, "atchannel0");
                    Log.d(TAG, "ENG_AT_SET_SPAPC_DISABLE return : " + mStrTmp);
                    if (mStrTmp != null && mStrTmp.contains(IATUtils.AT_OK)) {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mAPCTest.setChecked(false);
                            }
                        });
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mAPCTest.setChecked(true);
                            }
                        });
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void AlertDialogShow(final String key) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage(getString(R.string.apc_enable_waring))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.alertdialog_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PowerManager pm = (PowerManager) AutoTestBySIMActivity.this.getSystemService(Context.POWER_SERVICE);
                                pm.reboot("setAPCSwitch");
                            }
                        })
                .setNegativeButton(R.string.alertdialog_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                mUiThread.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //mAPCTest.setChecked(false);
                                    }
                                });
                            }
                        }).create();
        alertDialog.show();
    }
}