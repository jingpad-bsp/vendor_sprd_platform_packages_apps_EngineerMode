package com.sprd.engineermode.telephony;

import android.os.Bundle;
import android.util.Log;

import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;
import com.unisoc.engineermode.core.impl.nonpublic.TelephonyManagerProxy;

import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.telephony.TelephonyManager;
import android.content.Intent;

public class NetModeSIMActivity extends PreferenceActivity implements OnPreferenceChangeListener {

    private static final String TAG = "NetModeSIMActivity";
    private static final String KEY_SIM_INDEX = "simindex";
    private static final String PHONE_CHANNEL = "phonae_channel";
    private static final String INTENT_CONTENT = "intent_content";
    private static final String KEY_SIM0_SELECT = "sim0_select";
    private static final String KEY_SIM1_SELECT = "sim1_select";
    private static final String KEY_PHONEID = "key_phoneid";
    private Preference mSIM0Select;
    private Preference mSIM1Select;

    private int mPhoneCount;
    private int mPhoneId = 0;
    /* SPRD BUG 874410 - Frequency Point test @{ */
    private String intentContent;
    /* }@ */
    private TelephonyManager mTelephonyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* SPRD BUG 874410 - Frequency Point test @{ */
        Intent intent = getIntent();
        intentContent = intent.getStringExtra(INTENT_CONTENT);
        /* }@ */
        addPreferencesFromResource(R.xml.pref_netmode_sim);

        mPhoneCount = TelephonyManagerProxy.getPhoneCount();
//        mTelephonyManager = new TelephonyManager[mPhoneCount];

        mSIM0Select = (Preference) findPreference(KEY_SIM0_SELECT);
        /* SPRD BUG 874410 - Frequency Point test @{ */
        mSIM0Select.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Log.d(TAG, "mSIM0Select onPreferenceClick intentContent = " + intentContent);
                if (intentContent != null && intentContent.equals("netmode")) {
                    Intent intent = new Intent("android.engineermode.action.NetworkModeActivity");
                    intent.putExtra(KEY_SIM_INDEX, "0");
                    startActivity(intent);
                } else if (intentContent != null && intentContent.equals("bandselect")) {
                    if (SystemPropertiesProxy.get("persist.vendor.radio.engtest.enable").contains("true")) {
                        return false;
                    }
                    Intent intent = new Intent(
                            "com.sprd.engineermode.action.BANDMODESET");
                    intent.putExtra(KEY_PHONEID, 0);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent("android.engineermode.action.FrequencyPointActivity");
                    intent.putExtra(PHONE_CHANNEL, "atchannel0");
                    startActivity(intent);
                }
                return false;
            }
        });

        mSIM1Select = (Preference) findPreference(KEY_SIM1_SELECT);
        mSIM1Select.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Log.d(TAG, "mSIM1Select onPreferenceClick intentContent = " + intentContent);
                if (intentContent != null && intentContent.equals("netmode")) {
                    Intent intent = new Intent("android.engineermode.action.NetworkModeActivity");
                    intent.putExtra(KEY_SIM_INDEX, "1");
                    startActivity(intent);
                } else if (intentContent != null && intentContent.equals("bandselect")) {
                    if (SystemPropertiesProxy.get("persist.vendor.radio.engtest.enable").contains("true")) {
                        return false;
                    }
                    Intent intent = new Intent(
                            "com.sprd.engineermode.action.BANDMODESET");
                    intent.putExtra(KEY_PHONEID, 1);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent("android.engineermode.action.FrequencyPointActivity");
                    intent.putExtra(PHONE_CHANNEL, "atchannel1");
                    startActivity(intent);
                }
                return false;
            }
        });
        /* }@ */
        getCardExitState();
    }


    private void getCardExitState() {
        Log.d(TAG, "mPhoneCount: " + mPhoneCount);
        for (int i = 0; i < mPhoneCount; i++) {
            mTelephonyManager =  TelephonyManagerProxy.getService();
            if (mTelephonyManager != null
                    && mTelephonyManager.getSimState(i) == TelephonyManager.SIM_STATE_READY) {
                Log.d(TAG, "SIM " + i + " SIM_STATE_READY");
                if (i == 0) {
                    mSIM0Select.setEnabled(true);
                } else {
                    mSIM1Select.setEnabled(true);
                }
            } else {
                Log.d(TAG, "SIM " + i + " SIM_STATE_NOT_READY");
                if (i == 0) {
                    mSIM0Select.setEnabled(false);
                } else {
                    mSIM1Select.setEnabled(false);
                }
            }
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d(TAG, "onPreferenceChange");
        if (preference == mSIM0Select) {
            Log.d(TAG, "get mSIM0Select ");
        } else if (preference == mSIM1Select) {
            Log.d(TAG, "get mSIM1Select ");
        }
        return true;
    }
}