package com.sprd.engineermode.telephony;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;
import com.sprd.engineermode.R;

public class UeNwCapActivity extends PreferenceActivity {

    private static final String TAG = "UeNwCapActivity";
    private static final String KEY_SIM_INDEX = "simindex";
    private static final String KEY_NW_CAP = "nw_cap";
    private static final String KEY_UE_CAP = "ue_cap";

    static int mSimIndex;
    private Preference mPreNwCap, mPreUeCap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_ue_nw_cap);
        Intent intent = this.getIntent();
        mSimIndex = intent.getIntExtra(KEY_SIM_INDEX, -1);
        Log.d(TAG, "mSimIndex: " + mSimIndex);
        mPreNwCap = (Preference)findPreference(KEY_NW_CAP);
        mPreUeCap = (Preference)findPreference(KEY_UE_CAP);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    @Deprecated
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        String key = preference.getKey();
        Log.d(TAG, "key = " + key);
        if (key.equals(KEY_NW_CAP)) {
            Intent intent = new Intent(
                    "android.engineermode.action.NETINFOSIMNETWORKTYPE");
            intent.putExtra(KEY_SIM_INDEX, mSimIndex);
            startActivity(intent);
        } else if (key.equals(KEY_UE_CAP)) {
            Intent intent1 = new Intent(
                    "android.engineermode.action.UECAP");
            intent1.putExtra(KEY_SIM_INDEX, mSimIndex);
            startActivity(intent1);
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
