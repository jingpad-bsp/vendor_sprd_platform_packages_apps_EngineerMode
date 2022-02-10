package com.sprd.engineermode.debuglog;

import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.impl.nonpublic.TelephonyManagerProxy;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.telephony.TelephonyManager;
import android.content.Intent;
import android.preference.PreferenceActivity;
import android.content.ComponentName;

public class BinEnableSimSeleActivity extends PreferenceActivity {

    private static final String TAG = "BinEnableSimSeleActivity";
    private static final String SIM = "SIM";
    private static final String KEY_SIM_INDEX = "simindex";

    private int mPhoneCount;
    private Preference[] mSIMPref;
    private TelephonyManager mTelephonyManager;
    PreferenceGroup mPreGroup = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPreferenceScreen(getPreferenceManager().createPreferenceScreen(this));
        mPreGroup = getPreferenceScreen();
        mPhoneCount = TelephonyManagerProxy.getPhoneCount();
//        mTelephonyManager = new TelephonyManager[mPhoneCount];
        mSIMPref = new Preference[mPhoneCount];

        mTelephonyManager = TelephonyManagerProxy.getService();
        for (int i = 0; i < mPhoneCount; i++) {
            mSIMPref[i] = new Preference(this);
            mSIMPref[i].setTitle(SIM + i);
            mSIMPref[i].setKey(SIM + i);
            mPreGroup.addPreference(mSIMPref[i]);
            if (mTelephonyManager != null&& mTelephonyManager.getSimState(i) == TelephonyManager.SIM_STATE_READY) {
                mSIMPref[i].setEnabled(true);
            } else {
                mSIMPref[i].setEnabled(false);
                mSIMPref[i].setSummary(R.string.input_card_to_test);
            }
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        String key = preference.getKey();
        if (key.startsWith(SIM)) {
            int simIndex = Integer.valueOf(key.substring(3, 4));
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setComponent(new ComponentName("com.android.phone","com.sprd.phone.BinEnableActivity"));
            intent.putExtra(KEY_SIM_INDEX, simIndex);
            startActivity(intent);
        }
        return false;
        }
}
