package com.sprd.engineermode.debuglog;


import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.TwoStatePreference;
import android.util.Log;

import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;

public class BluetoothActivity extends PreferenceActivity implements
Preference.OnPreferenceChangeListener{

    private static final String TAG = "BluetoothActivity";

    private static final String KEY_SPEED_LIMIT = "speed_limit";

    private TwoStatePreference mSpeed_limit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_cmcc_bluetooth);

        mSpeed_limit = (TwoStatePreference) findPreference(KEY_SPEED_LIMIT);
        int lowSpeed = SystemPropertiesProxy.getInt("persist.sys.bt.lowspeed", 110);
        Log.d(TAG,"lowSpeed="+lowSpeed);
        if (lowSpeed >0) {
            mSpeed_limit.setChecked(true);
        } else {
            mSpeed_limit.setChecked(false);
        }
        mSpeed_limit.setOnPreferenceChangeListener(this);

    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    public boolean onPreferenceChange(Preference pref,Object objValue){
        if (pref == mSpeed_limit) {
            Log.d(TAG,"mSpeed_limit change ");
            if(mSpeed_limit.isChecked()){
                SystemPropertiesProxy.set("persist.sys.bt.lowspeed","0");
                Log.d(TAG,"SET_SPEED_LIMIT: set prop is " + SystemPropertiesProxy.get("persist.sys.bt.lowspeed"));
            }else{
                SystemPropertiesProxy.set("persist.sys.bt.lowspeed","110");
                Log.d(TAG,"SET_SPEED_LIMIT: set prop is " + SystemPropertiesProxy.get("persist.sys.bt.lowspeed"));
            }
            return true;
        }
        return false;
    }
}