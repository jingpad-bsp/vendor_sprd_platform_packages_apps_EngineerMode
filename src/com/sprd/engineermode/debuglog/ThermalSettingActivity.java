
package com.sprd.engineermode.debuglog;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;

import java.io.File;

import com.sprd.engineermode.EMSwitchPreference;
import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.exception.EmException;
import com.unisoc.engineermode.core.intf.IThermal;

public class ThermalSettingActivity extends PreferenceActivity implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "ThermalSettingActivity";
    private static final String KEY_THERMAL_SWITCH = "thermal_switch";
    private static final String KEY_THERMAL_PA_SWITCH = "thermal_pa_switch";
    public  static final String KEY_THERMAL_CHARGE_SWITCH = "thermal_charge_switch";
    private static final String KEY_IPA = "ipa";
    private static final String IPA_PATH="/sys/class/thermal/thermal_zone0/thm_enable";

    private EMSwitchPreference mThermal,mThermalPa,mThermalCharge;
    private EMSwitchPreference mIpa;


    private IThermal thermal = CoreApi.getHardwareApi().thermalApi();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_thermal_settings);

        mThermal = (EMSwitchPreference) findPreference(KEY_THERMAL_SWITCH);
        mThermal.setOnPreferenceChangeListener(this);

        mThermalPa = (EMSwitchPreference) findPreference(KEY_THERMAL_PA_SWITCH);
        mThermalPa.setOnPreferenceChangeListener(this);
        mThermalCharge= (EMSwitchPreference) findPreference(KEY_THERMAL_CHARGE_SWITCH);
        mThermalCharge.setOnPreferenceChangeListener(this);
        mIpa = (EMSwitchPreference) findPreference(KEY_IPA);
        mIpa.setOnPreferenceChangeListener(this);

        File file=new File(IPA_PATH);
        if(!file.exists()) {
            mIpa.setEnabled(false);
            mIpa.setSummary(R.string.feature_not_support);
        }
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        updateThermalStatus();
        updateThermalPaStatus();
        updateThermalChargeStatus();
        updateThermalIpaStatus();
    }


    public void updateThermalStatus() {
        boolean state;
        try {
            state = thermal.getThermalState();
        } catch (EmException e) {
            e.printStackTrace();
            return;
        }
        Log.d(TAG," thermal state:  "+ state);
        if (mThermal.isChecked() != state) {
            mThermal.setChecked(state);
        } else {
            Log.w(TAG, "isCheck is same as state, maybe set failed");
        }
    }

    public void updateThermalPaStatus() {
        Log.d(TAG, "updateThermalPaStatus" );
        boolean state;
        try {
            state = thermal.getThermalPaState();
        } catch (EmException e) {
            e.printStackTrace();
            return;
        }
        Log.d(TAG,"thermal pa state:  "+state);
        if (mThermalPa.isChecked() != state) {
            mThermalPa.setChecked(state);
        } else {
            Log.w(TAG, "pa isCheck is same as state, maybe set failed");
        }
    }

     private void updateThermalChargeStatus() {
        Log.d(TAG, "updateThermalChargeStatus" );
        boolean state;
        try {
            state = thermal.getThermalChargeState();
        } catch (EmException e) {
            e.printStackTrace();
            return;
        }
        Log.d(TAG,"thermal charge state: "+state);
         if (mThermalCharge.isChecked() != state) {
             mThermalCharge.setChecked(state);
         } else {
             Log.w(TAG, "charge isCheck is same as state, maybe set failed");
         }
    }

    private void updateThermalIpaStatus() {
        Log.d(TAG, "updateThermalIpaStatus" );
        if (!mIpa.isEnabled()) {
            return;
        }

        boolean state;
        try {
            state = thermal.getThermalIpaState();
        } catch (EmException e) {
            e.printStackTrace();
            return;
        }
        Log.d(TAG,"thermal ipa state: "+state);
        mIpa.setChecked(state);
    }

    private void onThermalPreferenceChange() {
        boolean checked = mThermal.isChecked();
        try {
            if (checked) {
                thermal.switchThermalOff();
            } else {
                thermal.switchThermalOn();
            }
        } catch (EmException e) {
            e.printStackTrace();
            return;
        }

        updateThermalStatus();
        updateThermalPaStatus();
        updateThermalChargeStatus();
    }

    private void onThermalPaPreferenceChange() {
        try {
            if (mThermalPa.isChecked()) {
                thermal.switchThermalPaOff();
            } else {
                thermal.switchThermalPaOn();
            }
        } catch (EmException e) {
            e.printStackTrace();
            return;
        }
        updateThermalPaStatus();
    }

    private void onThermalChargePreferenceChange() {
        try {
            if (mThermalCharge.isChecked()) {
                thermal.switchThermalChargeOff();
            } else {
                thermal.switchThermalChargeOn();
            }
        } catch (EmException e) {
            e.printStackTrace();
            return;
        }
        updateThermalChargeStatus();
    }

    private void onThermalIpaPreferenceChange() {
        try {
            if (mIpa.isChecked()) {
                thermal.switchThermalIpaOff();
            } else {
                thermal.switchThermalIpaOn();
            }
        } catch (EmException e) {
            e.printStackTrace();
        }
        updateThermalIpaStatus();
    }

    @Override
    public boolean onPreferenceChange(Preference pref, Object newValue) {
        if(pref == mThermal){
            onThermalPreferenceChange();
        }else if(pref == mThermalPa){
            onThermalPaPreferenceChange();
        }else if (pref == mThermalCharge) {
            onThermalChargePreferenceChange();
         }else if(pref == mIpa){
            onThermalIpaPreferenceChange();
        }
        return false;
    }
}
