package com.sprd.engineermode.telephony;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;

import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.utils.IATUtils;

public class UeCapC2kActivity extends PreferenceActivity implements OnPreferenceChangeListener {

    private static final String TAG = "UeCapC2kActivity";

    private static final String KEY_SR1_SUPPORTED = "sr1_supported";
    private static final String KEY_SR3_SUPPORTED = "sr3_supported";
    private static final String KEY_CLASS1_SUPPORTED = "rcclass1_supported";
    private static final String KEY_CLASS2_SUPPORTED = "rcclass2_supported";
    private static final String KEY_CLASS3_SUPPORTED = "rcclass3_supported";
    private static final String KEY_QPCH_SUPPORTED = "qpch_supported";
    private static final String KEY_BAND_CLASS = "band_class";
    private static final String KEY_BAND_SUB_CLASS = "band_sub_class";
    private static final String KEY_RATE1M8_SUPPORTED = "rate1m8_supported";

    private static final int GET_C2K_UE_CAP = 0;

    private MyHandler mHandler;
    private Handler mUiThread = new Handler();

    private String mStrTmp;
    private int simIdx = UeNwCapActivity.mSimIndex;

    private SwitchPreference mSr1Supported;
    private SwitchPreference mSr3Supported;
    private SwitchPreference mRcclass1Supported;
    private SwitchPreference mRcclass2Supported;
    private SwitchPreference mRcclass3Supported;
    private SwitchPreference mQpchSupported;
    private SwitchPreference mBandClass;
    private SwitchPreference mBandSubClass;
    private SwitchPreference mRate1m8Supported;

    private ITelephonyApi teleApi = CoreApi.getTelephonyApi();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_uecap_cdma_2000);

        mSr1Supported = (SwitchPreference)findPreference(KEY_SR1_SUPPORTED);
        mSr1Supported.setOnPreferenceChangeListener(this);
        mSr3Supported = (SwitchPreference)findPreference(KEY_SR3_SUPPORTED);
        mSr3Supported.setOnPreferenceChangeListener(this);
        mRcclass1Supported = (SwitchPreference)findPreference(KEY_CLASS1_SUPPORTED);
        mRcclass1Supported.setOnPreferenceChangeListener(this);
        mRcclass2Supported = (SwitchPreference)findPreference(KEY_CLASS2_SUPPORTED);
        mRcclass2Supported.setOnPreferenceChangeListener(this);
        mRcclass3Supported = (SwitchPreference)findPreference(KEY_CLASS3_SUPPORTED);
        mRcclass3Supported.setOnPreferenceChangeListener(this);
        mQpchSupported = (SwitchPreference)findPreference(KEY_QPCH_SUPPORTED);
        mQpchSupported.setOnPreferenceChangeListener(this);
        mBandClass = (SwitchPreference)findPreference(KEY_BAND_CLASS);
        mBandClass.setOnPreferenceChangeListener(this);
        mBandSubClass = (SwitchPreference)findPreference(KEY_BAND_SUB_CLASS);
        mBandSubClass.setOnPreferenceChangeListener(this);
        mRate1m8Supported = (SwitchPreference)findPreference(KEY_RATE1M8_SUPPORTED);
        mRate1m8Supported.setOnPreferenceChangeListener(this);

        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mHandler = new MyHandler(ht.getLooper());
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onstart");
        Message mGetC2kUeCap = mHandler.obtainMessage(GET_C2K_UE_CAP);
        mHandler.sendMessage(mGetC2kUeCap);
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class MyHandler extends Handler {
        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_C2K_UE_CAP:
                    mStrTmp = teleApi.c2kUeCapApi().getUeCapInfo(simIdx);
                    Log.d(TAG, "GET_C2K_UE_CAP simIdx: " + simIdx + "  mStrTmp: " + mStrTmp);

                    if (mStrTmp != null && mStrTmp.contains(IATUtils.AT_OK)){
                        try {
                            String[] str = mStrTmp.split("\n")[0].trim().split("-");
                            mUiThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    mSr1Supported.setChecked(str[0].equals("1"));
                                    mSr3Supported.setChecked(str[1].equals("1"));
                                    mRcclass1Supported.setChecked(str[2].equals("1"));
                                    mRcclass2Supported.setChecked(str[3].equals("1"));
                                    mRcclass3Supported.setChecked(str[4].equals("1"));
                                    mQpchSupported.setChecked(str[9].equals("1"));
                                    mBandClass.setChecked(str[35].equals("1"));
                                    mBandSubClass.setChecked(str[36].equals("1"));
                                    mRate1m8Supported.setChecked(str[41].equals("1"));
                                }
                            });
                        } catch (Exception e) {
                            Log.d(TAG, "" + e);
                        }
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mSr1Supported.setEnabled(false);
                                mSr3Supported.setEnabled(false);
                                mRcclass1Supported.setEnabled(false);
                                mRcclass2Supported.setEnabled(false);
                                mRcclass3Supported.setEnabled(false);
                                mQpchSupported.setEnabled(false);
                                mBandClass.setEnabled(false);
                                mBandSubClass.setEnabled(false);
                                mRate1m8Supported.setEnabled(false);
                            }
                        });
                    }
                    break;
            }
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String key = preference.getKey();
        Log.d(TAG, "key = " + key);
        return false;
    }
}
