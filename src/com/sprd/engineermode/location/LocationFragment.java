package com.sprd.engineermode.location;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.content.ComponentName;

import com.sprd.engineermode.R;

public class LocationFragment extends PreferenceFragment implements
        Preference.OnPreferenceClickListener {

    private static final String TAG = "LocationFragment";
    private static final String KEY_Clock_Quality = "Clock_Quality";
    private static final String KEY_GNSS_Desense = "GNSS_Desense";
    private static final String KEY_GNSS_NoiseScan = "GNSS_NoiseScan";
    private static final String KEY_SGPS = "SGPS";
    private Preference mClockQuality;
    private Preference mGNSSDesense;
    private Preference mGNSSNoiseScan;
    private Preference mSGPS;

    private Context mContext;

    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_location);
        mContext = this.getActivity();
        mClockQuality = (Preference) findPreference(KEY_Clock_Quality);
        mClockQuality.setOnPreferenceClickListener(this);
        mGNSSDesense = (Preference) findPreference(KEY_GNSS_Desense);
        mGNSSDesense.setOnPreferenceClickListener(this);
        mGNSSNoiseScan = (Preference) findPreference(KEY_GNSS_NoiseScan);
        mGNSSNoiseScan.setOnPreferenceClickListener(this);
        mSGPS = (Preference) findPreference(KEY_SGPS);
        mSGPS.setOnPreferenceClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public boolean onPreferenceClick(Preference pref) {
        try {
            ComponentName cn = null;
            Intent intent = new Intent();
            intent.putExtra("friendProcessName",
                    mContext.getApplicationInfo().processName);
            intent.putExtra("friendPackageName", mContext.getPackageName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.d(TAG, pref.getKey());
            if (pref.getKey().equals(KEY_Clock_Quality)) {

                cn = new ComponentName("com.unisoc.gnsstool",
                        "com.unisoc.gnsstool.ClockQualityActivity");

            } else if (pref.getKey().equals(KEY_GNSS_Desense)) {

                cn = new ComponentName("com.unisoc.gnsstool",
                        "com.unisoc.gnsstool.GNSSDesenseActivity");

            } else if (pref.getKey().equals(KEY_GNSS_NoiseScan)) {

                cn = new ComponentName("com.unisoc.gnsstool",
                        "com.unisoc.gnsstool.NoiseScanActivity");

            } else if (pref.getKey().equals(KEY_SGPS)) {

                cn = new ComponentName("com.spreadtrum.sgps",
                        "com.spreadtrum.sgps.SgpsActivity");
            }
            intent.setComponent(cn);
            mContext.startActivity(intent);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            return false;
        }
        return true;
    }

}
