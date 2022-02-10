/* This code functions as follows:
 * 1、there are several mobile phone SIM card slot detection
 * 2、detect whether the card slot to insert a SIM card, if there is no SIM card will prompt testers
 */

package com.sprd.engineermode.telephony;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.telephony.TelephonyManager;
import android.content.Intent;
import android.preference.PreferenceActivity;
import android.util.Log;
import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.impl.nonpublic.TelephonyManagerProxy;

public class NetInfoSimSelectActivity extends PreferenceActivity {
    private static final String TAG = "NetInfoSimSelect";
    private static final String SIM = "SIM";
    private static final String KEY_SIM_INDEX = "simindex";

    private int mPhoneCount;
    private Preference[] mSIMPref;
    private TelephonyManager mTelephonyManager;
    private PreferenceGroup mPreGroup = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPreferenceScreen(getPreferenceManager().createPreferenceScreen(this));
        mPreGroup = getPreferenceScreen();
        mPhoneCount = TelephonyManagerProxy.getPhoneCount();
        Log.d(TAG, "SIM counts is :" + mPhoneCount);
//        mTelephonyManager = new TelephonyManager[mPhoneCount];
        mSIMPref = new Preference[mPhoneCount];
        for (int i = 0; i < mPhoneCount; i++) {
            mSIMPref[i] = new Preference(this);
            mSIMPref[i].setTitle(SIM + i);
            mSIMPref[i].setKey(SIM + i);
            mPreGroup.addPreference(mSIMPref[i]);
            // Detect whether there is a card
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (int i = 0; i < mPhoneCount; i++) {
            if (TelephonyManagerProxy.getSimState(i) != TelephonyManager.SIM_STATE_READY) {
                mSIMPref[i].setEnabled(false);
                mSIMPref[i].setSummary(R.string.input_sim__warn);
            } else {
                mSIMPref[i].setEnabled(true);
            }
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        String key = preference.getKey();
        int simIndex = Integer.valueOf(key.substring(3, 4));
//        Intent intent = new Intent(
//                "android.engineermode.action.NETINFOSIMNETWORKTYPE");
        Intent intent = new Intent(
                "android.engineermode.action.UE_NW");
        intent.putExtra(KEY_SIM_INDEX, simIndex);
        startActivity(intent);
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

}
