
package com.sprd.engineermode.hardware;

import java.io.File;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.Context;
import android.util.Log;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;

import android.preference.PreferenceFragment;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.TwoStatePreference;
import android.preference.SwitchPreference;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import android.content.SharedPreferences.Editor;
import android.os.BatteryManager;

import android.widget.Toast;
import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.common.Const;
import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;
import com.unisoc.engineermode.core.intf.IHardwareApi;
import com.sprd.engineermode.EMLongClickPreference;
import android.media.AudioManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.utils.CommonUtils;

public class HardWareFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, OnSharedPreferenceChangeListener {

    private static final String TAG = "HardWareFragment";
    private static final String KEY_CAMERA_API = "camera_api";
    private static final String CAMERA_API = "camera";
    private static final String CAMERA_API_SWITCH = "api_switch";

    private static final String KEY_USB_CHARGE = "usb_charge";
    /* SPRD 963152 : sharkl5 FM play through dsp @{ */
    private static final String KEY_FM_DSP = "fm_dsp";
    /* }@ */

    private static final String KEY_CAMERA_IDLE_SLEEP = "camera_idle_sleep";
    private static final String KEY_CAMERA_TEST_MODE = "camera_test_mode";
    /* SPRD 998936 RF send power @{ */
    private static final String SEND_POWER = "key_sendpower";
    /* }@ */
    /* SPRD 1033959: ASDIV feature @{ */
    private static final String KEY_ASDIV = "asdiv_key";
    /* }@ */
    private static final int MSG_LTE_DPDT_SWITCH_1 = 0;
    private static final int MSG_LTE_DPDT_SWITCH_2 = 1;
    private static final int MSG_GSM_DPDT_SWITCH_1 = 2;
    private static final int MSG_GSM_DPDT_SWITCH_2 = 3;
    private static final int MSG_WCDMA_DPDT_SWITCH_1 = 4;
    private static final int MSG_WCDMA_DPDT_SWITCH_2 = 5;
    private static final int MSG_C2K_DPDT_SWITCH_1 = 6;
    private static final int MSG_C2K_DPDT_SWITCH_2 = 7;
    private static final int MSG_GET_USB_CHARGE = 12;
    private static final int MSG_OPEN_USB_CHARGE = 13;
    private static final int MSG_CLOSE_USB_CHARGE = 14;
    private static final int MSG_GET_RESET_SETTING = 15;
    private static final int MSG_SET_RESET_SETTING = 16;
    private static final int MSG_SET_ASDIV_SETTING = 17;

    private TwoStatePreference mUsbCharge;
    /* Bug 803253 @ { */
    //private Preference mHashValue;
    /* }@ */
    private Handler mUiThread = new Handler();
    private HardwareHandler mHWHandler;
    private SharedPreferences mSharePref;
    private SwitchPreference mCameraIdleSleep;
    /* SPRD Bug 792363:Add Camera Power Test Mode switch. @{ */
    private SwitchPreference mCameraTestMode;
    /* @} */

    private ListPreference mResetSetting;

    /* SPRD 1033959: ASDIV feature @{ */
    private ListPreference mAsdivSetting;
    /* @} */

    private Context mContext;
    private IHardwareApi hwApi = CoreApi.getHardwareApi();

    /* SPRD 940291 - [true demo version] Control charge region @{ */
    private BatteryReceiver mReceiver;
    private int mOpenLevel = 0;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static final int BATTERY_LOWER_LIMIT = 30;
    private static final int BATTERY_UPPER_LIMIT = 90;
    private boolean mBelowThirtyToCharge = true;
    /* }@ */
    /* SPRD 998936 RF send power @{ */
    private Preference mSendPower;
    /* }@ */
    /* SPRD 963152 : sharkl5 FM play through dsp @{ */
    private AudioManager mAudioManager;
    private SwitchPreference mFMThroughDSP;
    /* }@ */
    /* SPRD 1033959: ASDIV feature @{ */
    private boolean mIsSuccess = true;
    private int mCurrumentAntenna;
    /* }@ */

    private ITelephonyApi teleApi = CoreApi.getTelephonyApi();
    /** Used to detect when the USB cable is unplugged, so we can call UsbCharge */
    private BroadcastReceiver mUsbStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(CommonUtils.USBMANAGER_ACTION_USB_STATE)) {
                handleUsbStateChanged(intent.getExtras().getBoolean("connected"));
            }
        }
    };

    /* SPRD 940291 - [true demo version] Control charge region @{ */
    private class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.i(TAG, "onReceive  action = " + action);
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                final int extra_level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                mOpenLevel = extra_level < 0 ? 0 : extra_level > 100 ? 100 : extra_level;
                boolean pluged = (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) == 0) ? false : true;
                Log.i(TAG, "battery level = " + mOpenLevel + " pluged = " + pluged);
                handleUsbStateChanged(pluged);
            }
        }
    }

    private void handleUsbStateChanged(boolean connecting) {
        if (!connecting) {
            //mUsbCharge.setChecked(true);
            mUsbCharge.setEnabled(false);
            mBelowThirtyToCharge = false;
        } else {
            mUsbCharge.setEnabled(true);
            int message;
            try {
                Log.d(TAG, "handleUsbStateChanged  getChargeLevel(): " + hwApi.chargeApi().getChargeLevel().trim());
                if (preferences.getBoolean("charge_func", true)) {
                    if (Integer.parseInt(hwApi.chargeApi().getChargeLevel().trim()) < BATTERY_LOWER_LIMIT) {
                        message = MSG_OPEN_USB_CHARGE;
                        mBelowThirtyToCharge = true;
                    } else if (Integer.parseInt(hwApi.chargeApi().getChargeLevel().trim()) > BATTERY_UPPER_LIMIT) {
                        mBelowThirtyToCharge = false;
                        message = MSG_CLOSE_USB_CHARGE;
                    } else {
                        if (!mBelowThirtyToCharge) {
                            message = MSG_CLOSE_USB_CHARGE;
                        } else {
                            message = MSG_OPEN_USB_CHARGE;
                        }
                    }
                } else {
                    message = MSG_OPEN_USB_CHARGE;
                }
                Message setUsbChargeState = mHWHandler.obtainMessage(message);
                mHWHandler.sendMessage(setUsbChargeState);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /* }@ */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_hardwaretab);
        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mHWHandler = new HardwareHandler(ht.getLooper());
        mSharePref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mContext=getActivity();
        /* SPRD 963152 : sharkl5 FM play through dsp @{ */
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        /* }@ */

        EMLongClickPreference deSensePll = (EMLongClickPreference) findPreference("de_sense_pll");
        /* Bug 803253 @ { */
        //mHashValue = (Preference) findPreference("hash_value");
        //mHashValue.setSummary(R.string.feature_not_support);
        /* }@ */
        mUsbCharge = (TwoStatePreference) findPreference(KEY_USB_CHARGE);
        mUsbCharge.setOnPreferenceChangeListener(this);
        /* SPRD 1033959: ASDIV feature @{ */
        mAsdivSetting = (ListPreference) findPreference(KEY_ASDIV);
        /* }@ */
        /* SPRD 963152 : sharkl5 FM play through dsp @{ */
        mFMThroughDSP = (SwitchPreference) findPreference(KEY_FM_DSP);
        mFMThroughDSP.setOnPreferenceChangeListener(this);
        String mAudioDspExit = mAudioManager.getParameters("isAudioDspExist");
        Log.d(TAG, "FM_WITH_DSP isAudioDspExist: " + mAudioDspExit);
        if (mAudioDspExit == null || !mAudioDspExit.trim().equalsIgnoreCase("isAudioDspExist=1")) {
            mFMThroughDSP.setEnabled(false);
            mFMThroughDSP.setSummary(R.string.feature_not_support);
        }
        /* }@ */
        boolean isUser = SystemPropertiesProxy.get("ro.build.type").equalsIgnoreCase("user");
        Log.d(TAG, "isUser = " + isUser);
        if (isUser) {
            deSensePll.setEnabled(false);
        }

        mCameraIdleSleep = (SwitchPreference) findPreference(KEY_CAMERA_IDLE_SLEEP);
        mCameraIdleSleep.setOnPreferenceChangeListener(this);

        /* SPRD Bug 792363:Add Camera Power Test Mode switch. @{ */
        mCameraTestMode = (SwitchPreference) findPreference(KEY_CAMERA_TEST_MODE);
        mCameraTestMode.setOnPreferenceChangeListener(this);
        /* @} */

        SharedPreferences mSharePref = PreferenceManager.getDefaultSharedPreferences(mContext);
        mSharePref.registerOnSharedPreferenceChangeListener(this);
        mResetSetting=(ListPreference)findPreference("reset_setting");
        mResetSetting.setSummary(mResetSetting.getEntry());
        mAsdivSetting = (ListPreference)findPreference(KEY_ASDIV);
        mAsdivSetting.setSummary(mAsdivSetting.getEntry());
        /* SPRD 998936 RF send power @{ */
        mSendPower = (Preference) findPreference(SEND_POWER);
        mSendPower.setOnPreferenceChangeListener(this);
        if (!teleApi.telephonyInfo().isSupportLte()) {
            mSendPower.setEnabled(false);
            mSendPower.setSummary(R.string.feature_not_support);
        }
        Log.d(TAG, "mSendPower setOnPreferenceChangeListener");

        mContext.registerReceiver(mUsbStateReceiver, new IntentFilter(CommonUtils.USBMANAGER_ACTION_USB_STATE));

        /* SPRD 940291 - [true demo version] Control charge region @{ */
        Log.d(TAG, "register BatteryReceiver");
        mReceiver = new BatteryReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        mContext.registerReceiver(mReceiver, filter);
        preferences = mContext.getSharedPreferences("charge_control", Context.MODE_PRIVATE);
        /* }@ */
        /* SPRD 1116096 - Hide ASDIV function in androidq @{ */
        //getPreferenceScreen().removePreference(mAsdivSetting);
        /* }@ */
    }

    @Override
    public void onStart() {
        /* Bug 803253 @ { */
        /*if (mHashValue != null && mHashValue.isEnabled()) {
            if (EngineerModeNative.native_hashValueWrited()) {
                mHashValue.setSummary(R.string.hash_value_writed);
            } else {
                mHashValue.setSummary(R.string.hash_value_not_writed);
            }
        }*/
        /* }@ */
        /* SPRD 940291 - [true demo version] Control charge region @{ */
        if (mUsbCharge != null && mUsbCharge.isEnabled()) {
            boolean usbCharge = mSharePref.getBoolean("usb_charge",true);
            int message;
            try {
                Log.d(TAG, "onStart getChargeLevel: " + hwApi.chargeApi().getChargeLevel());
                if (preferences.getBoolean("charge_func", true)) {
                    mUsbCharge.setChecked(true);
                    if (Integer.parseInt(hwApi.chargeApi().getChargeLevel().trim()) < BATTERY_LOWER_LIMIT) {
                        mBelowThirtyToCharge = true;
                        message = MSG_OPEN_USB_CHARGE;
                        Message setUsbChargeState = mHWHandler.obtainMessage(message);
                        mHWHandler.sendMessage(setUsbChargeState);
                    } else if (Integer.parseInt(hwApi.chargeApi().getChargeLevel().trim()) > BATTERY_UPPER_LIMIT) {
                        message = MSG_CLOSE_USB_CHARGE;
                        Message setUsbChargeState = mHWHandler.obtainMessage(message);
                        mHWHandler.sendMessage(setUsbChargeState);
                    }
                } else {
                    mUsbCharge.setChecked(false);
                    message = MSG_OPEN_USB_CHARGE;
                    Message setUsbChargeState = mHWHandler.obtainMessage(message);
                    mHWHandler.sendMessage(setUsbChargeState);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /* }@ */

        if (mCameraIdleSleep != null) {
            mCameraIdleSleep.setChecked(SystemPropertiesProxy.getBoolean("persist.sys.camera.idlesleep",true));
        }

        /* SPRD Bug 792363:Add Camera Power Test Mode switch. @{ */
        if (mCameraTestMode != null) {
            boolean flag = SystemPropertiesProxy.get("persist.vendor.cam.isp.af.bypass", "0").equals("1");
            mCameraTestMode.setChecked(flag);
        }
        /* @} */
        if(mResetSetting!=null){
            Message getUsbChargeState = mHWHandler.obtainMessage(MSG_GET_RESET_SETTING);
            mHWHandler.sendMessage(getUsbChargeState);
        }
        /* SPRD 963152 : sharkl5 FM play through dsp @{ */
        if (mFMThroughDSP != null) {
            String mDspStatus = mAudioManager.getParameters("FM_WITH_DSP");
            Log.d(TAG, "FM_WITH_DSP status: " + mDspStatus);
            mFMThroughDSP.setChecked(mDspStatus.trim().equalsIgnoreCase("FM_WITH_DSP=1"));
        }
        /* @} */
        super.onStart();
    }

    boolean checkFileExists() {
        return new File("/system/bin/su").exists() || new File("/system/app/Superuser.apk").exists();
    }

    @Override
    public boolean onPreferenceChange(Preference pref, Object objValue) {
        Log.d(TAG, "onPreferenceChange");
        /* SPRD 940291 - [true demo version] Control charge region @{ */
        if (pref == mUsbCharge) {
            if (mUsbCharge.isChecked()) {
                Message openUsbChargeState = mHWHandler.obtainMessage(MSG_OPEN_USB_CHARGE);
                mHWHandler.sendMessage(openUsbChargeState);
                editor = preferences.edit();
                editor.putBoolean("charge_func", false);
                editor.commit();
                mUsbCharge.setChecked(false);
            } else {
                try {
                    int message;
                    Log.d(TAG, "onPreferenceChange hwApi.chargeApi().getChargeLevel(): " + hwApi.chargeApi().getChargeLevel());
                    if (Integer.parseInt(hwApi.chargeApi().getChargeLevel().trim()) < BATTERY_LOWER_LIMIT) {
                        mBelowThirtyToCharge = true;
                        message = MSG_OPEN_USB_CHARGE;
                    } else {
                        mBelowThirtyToCharge = false;
                        message = MSG_CLOSE_USB_CHARGE;
                    }
                    Message setUsbChargeState = mHWHandler.obtainMessage(message);
                    mHWHandler.sendMessage(setUsbChargeState);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                editor = preferences.edit();
                editor.putBoolean("charge_func", true);
                editor.commit();
                mUsbCharge.setChecked(true);
            }
            /* @} */
        } else if (pref == mCameraIdleSleep) {
            if (mCameraIdleSleep.isChecked()) {
                SystemPropertiesProxy.set("persist.sys.camera.idlesleep","0");
                mCameraIdleSleep.setChecked(false);
            } else {
                SystemPropertiesProxy.set("persist.sys.camera.idlesleep","1");
                mCameraIdleSleep.setChecked(true);
            }
            Log.d(TAG,"idlesleep="+(SystemPropertiesProxy.getBoolean("persist.sys.camera.idlesleep",true)));
        /* SPRD Bug 792363:Add Camera Power Test Mode switch. @{ */
        } else if (pref == mCameraTestMode) {
            if (mCameraTestMode.isChecked()) {
                SystemPropertiesProxy.set("persist.vendor.cam.isp.af.bypass", "0");
                mCameraTestMode.setChecked(false);
            } else {
                SystemPropertiesProxy.set("persist.vendor.cam.isp.af.bypass", "1");
                mCameraTestMode.setChecked(true);
            }/* @} */
            /* SPRD 963152 : sharkl5 FM play through dsp @{ */
        } else if (pref == mFMThroughDSP) {
            if (mFMThroughDSP.isChecked()) {
                mAudioManager.setParameters("FM_WITH_DSP=0");
                mFMThroughDSP.setChecked(false);
            } else {
                mAudioManager.setParameters("FM_WITH_DSP=1");
                mFMThroughDSP.setChecked(true);
            }/* @} */
        /* SPRD 998936 RF send power @{ */
        }
        return true;
    }

    /* SPRD 998936 RF send power @{ */
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference.getKey().endsWith(SEND_POWER)) {
            AlertDialog alertDialog = new AlertDialog.Builder(
                    this.getActivity())
                    .setTitle(getString(R.string.send_power))
                    .setMessage(getString(R.string.send_power_dailog))
                    .setPositiveButton(getString(R.string.alertdialog_ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    Intent intent = new Intent(
                                            mContext,
                                            com.sprd.engineermode.hardware.SendPowerPrefActivity.class);
                                    startActivity(intent);
                                }
                            })
                    .setNegativeButton(R.string.alertdialog_cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                }
                            }).create();
            alertDialog.show();
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
    /* @} */

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
        if(key.equals("reset_setting")){
            Message openUsbChargeState = mHWHandler.obtainMessage(MSG_SET_RESET_SETTING,Integer.parseInt(mResetSetting.getValue()),0);
            mHWHandler.sendMessage(openUsbChargeState);
        } else if (key.equals(KEY_ASDIV)) {
            try {
                Message openAsdivSetting = mHWHandler.obtainMessage(MSG_SET_ASDIV_SETTING, Integer.parseInt(mAsdivSetting.getValue()), 0);
                mHWHandler.sendMessage(openAsdivSetting);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    class HardwareHandler extends Handler {
        public HardwareHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_GET_USB_CHARGE:
                    String chargeStatus = readFile("/sys/class/power_supply/usb/online");
                    Log.d(TAG,"usb charge status is "+chargeStatus);
                    if (!"readError".equals(chargeStatus) && chargeStatus != null) {
                        if (chargeStatus.contains("0")) {
                            mUiThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    //mUsbCharge.setChecked(false);
                                }
                            });
                        } else if (chargeStatus.contains("1")) {
                            mUiThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    //mUsbCharge.setChecked(true);
                                }
                            });
                        } else {
                            mUiThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    mUsbCharge.setEnabled(false);
                                    mUsbCharge.setSummary(R.string.feature_abnormal);
                                }
                            });
                        }
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mUsbCharge.setEnabled(false);
                                mUsbCharge.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_OPEN_USB_CHARGE:
                    try {
                        hwApi.chargeApi().openUsbCharge();
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }

                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            //mUsbCharge.setChecked(true);
                            Editor editor = mSharePref.edit();
                            editor.putBoolean("usb_charge",true);
                            editor.commit();
                        }
                    });
                    break;
                case MSG_CLOSE_USB_CHARGE:
                    /*try {
                        if (!hwApi.chargeApi().getChargeState()) {
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }*/
                    try {
                        hwApi.chargeApi().closeUsbCharge();
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
                     //chargeStatus = readFile("/sys/class/power_supply/usb/online");
                            mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                //mUsbCharge.setChecked(false);
                                Editor editor = mSharePref.edit();
                                editor.putBoolean("usb_charge",false);
                                editor.commit();
                            }
                        });
                    break;
                case MSG_GET_RESET_SETTING:
                    String reset;
                    if (SystemPropertiesProxy.getInt("ro.debuggable", 0) == 1) {
                        reset = SystemPropertiesProxy.get("persist.vendor.eng.reset", "0"); //bug 1215217 default 1: Soft Reset
                    } else {
                        reset = SystemPropertiesProxy.get("persist.vendor.eng.reset", "1"); //bug 1188919 default 1: Hard Reset
                    }
                    Log.d(TAG, "get reset state: " + reset);
                    mUiThread.post(() -> {
                        mResetSetting.setValue(reset);
                        mResetSetting.setSummary(mResetSetting.getEntry());
                    });
                    break;
                case MSG_SET_RESET_SETTING:
                    int type = (int)msg.arg1;
                    mUiThread.post(() -> {
                        mResetSetting.setSummary(mResetSetting.getEntry());
                        SystemPropertiesProxy.set("persist.vendor.eng.reset", String.valueOf(type));
                    });

                    break;
                /* SPRD 1033959: ASDIV feature @{ */
                case MSG_SET_ASDIV_SETTING:
                    final int asdivType = (int)msg.arg1;
                    Log.d(TAG, "MSG_SET_ASDIV_SETTING  asdivType: " + asdivType);
                    setAsdiv(asdivType);
                    break;
                    /* }@ */
                default:
                    break;
            }
        }
    }

    /* SPRD 1033959: ASDIV feature @{ */
    private void setAsdiv(int mChooseAntenna) {
        mIsSuccess = true;
        try {
            hwApi.asdivApi().closeAllAntennas();
        } catch (Exception e) {
            e.printStackTrace();
            mIsSuccess = false;
        }
        switch (mChooseAntenna) {
            case MSG_LTE_DPDT_SWITCH_1:
                try {
                    hwApi.asdivApi().setLteToAntenna1();
                } catch (Exception e) {
                    e.printStackTrace();
                    mIsSuccess = false;
                }
                break;
            case MSG_LTE_DPDT_SWITCH_2:
                try {
                    hwApi.asdivApi().setLteToAntenna2();
                } catch (Exception e) {
                    e.printStackTrace();
                    mIsSuccess = false;
                }
                break;
            case MSG_GSM_DPDT_SWITCH_1:
                try {
                    hwApi.asdivApi().setGsmToAntenna1();
                } catch (Exception e) {
                    e.printStackTrace();
                    mIsSuccess = false;
                }
                break;
            case MSG_GSM_DPDT_SWITCH_2:
                try {
                    hwApi.asdivApi().setGsmToAntenna2();
                } catch (Exception e) {
                    e.printStackTrace();
                    mIsSuccess = false;
                }
                break;
            case MSG_WCDMA_DPDT_SWITCH_1:
                try {
                    hwApi.asdivApi().setWcdmaToAntenna1();
                } catch (Exception e) {
                    e.printStackTrace();
                    mIsSuccess = false;
                }
                break;
            case MSG_WCDMA_DPDT_SWITCH_2:
                try {
                    hwApi.asdivApi().setWcdmaToAntenna2();
                } catch (Exception e) {
                    e.printStackTrace();
                    mIsSuccess = false;
                }
                break;
            case MSG_C2K_DPDT_SWITCH_1:
                try {
                    hwApi.asdivApi().setC2kToAntenna1();
                } catch (Exception e) {
                    e.printStackTrace();
                    mIsSuccess = false;
                }
                break;
            case MSG_C2K_DPDT_SWITCH_2:
                try {
                    hwApi.asdivApi().setC2kToAntenna2();
                } catch (Exception e) {
                    e.printStackTrace();
                    mIsSuccess = false;
                }
                break;
            default:
                break;
        }
        mUiThread.post(new Runnable() {
            @Override
            public void run() {
                if (mIsSuccess) {
                    mAsdivSetting.setValueIndex(mChooseAntenna);
                    mAsdivSetting.setSummary(mAsdivSetting.getEntry());
                    mCurrumentAntenna = mChooseAntenna;
                } else {
                    mAsdivSetting.setValueIndex(mCurrumentAntenna);
                    Toast.makeText(mContext, "Unsupported", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    /* }@ */

    public String readFile(String path) {
        File file = new File(path);
        StringBuffer sb = new StringBuffer();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            Log.d(TAG, "Read file error!!!");
            sb.append("readError");
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
        Log.d(TAG, "read " + path + " value is " + sb.toString().trim());
        return sb.toString().trim();
    }

    @Override
    public void onDestroy() {
        mContext.unregisterReceiver(mUsbStateReceiver);
        /* SPRD Bug 792363:Add Camera Power Test Mode switch. @{ */
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        /* }@ */
        super.onDestroy();
    }
}
