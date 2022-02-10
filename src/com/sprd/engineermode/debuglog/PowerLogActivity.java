
package com.sprd.engineermode.debuglog;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.TwoStatePreference;
import android.util.Log;
import android.widget.Toast;

import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;

public class PowerLogActivity extends PreferenceActivity implements
        Preference.OnPreferenceClickListener {
    private static String TAG = "PowerLogActivity";
    private TwoStatePreference mBattery;
    private TwoStatePreference mDoze;
    private TwoStatePreference mDisplay;
    private TwoStatePreference mPowerGuru;
    private TwoStatePreference mPowerHint;
    private TwoStatePreference mWakeLock;
    private TwoStatePreference mPowerController;
    private TwoStatePreference mBatterySaver;
    private TwoStatePreference mAlarm;

    private TwoStatePreference mPowerControllerSwitch;
    private TwoStatePreference mPowerControllerGPSSwitch;
    private TwoStatePreference mPowerControllerNetworkSwitch;
    private TwoStatePreference mPowerControllerBgCleanSwitch;
    private TwoStatePreference mPowerControllerWakelockSwitch;
    private TwoStatePreference mPowerControllerOnlyInSaverSwitch;
    private TwoStatePreference mPowerControllerGPSOnlyInSaverSwitch;

    private TwoStatePreference mPowerHalSwitch;

    private static final String SWITCH_BATTERY_LOG = "sprd_battery_log";
    private static final String SWITCH_DOZE_LOG = "sprd_doze_log";
    private static final String SWITCH_DISPLAY_LOG = "sprd_display_log";
    private static final String SWITCH_POWER_GURU_LOG = "sprd_power_guru_log";
    private static final String SWITCH_POWER_HINT_LOG = "sprd_power_hint_log";
    private static final String SWITCH_WAKELOCK_LOG = "sprd_wakelock_log";
    private static final String SWITCH_POWER_CONTROLLER_LOG = "sprd_power_controller_log";
    private static final String SWITCH_BATTERY_SAVER_LOG = "sprd_battery_saver_log";
    private static final String SWITCH_ALARM_LOG = "sprd_alarm_log";

    private static final String SWITCH_POWER_CONTROLLER_SWITCH = "sprd_power_controller_switch";
    private static final String SWITCH_POWER_CONTROLLER_GPS_SWITCH = "sprd_power_controller_gps_switch";
    private static final String SWITCH_POWER_CONTROLLER_NETWORK_SWITCH = "sprd_power_controller_network_switch";
    private static final String SWITCH_POWER_CONTROLLER_BGCLEAN_SWITCH = "sprd_power_controller_bg_clean_switch";
    private static final String SWITCH_POWER_CONTROLLER_WAKELOCK_SWITCH = "sprd_power_controller_wakelock_switch";
    private static final String SWITCH_POWER_CONTROLLER_ONLY_IN_SAVER_SWITCH = "sprd_power_controller_only_saver_switch";
    private static final String SWITCH_POWER_CONTROLLER_GPS_ONLY_IN_SAVER_SWITCH = "sprd_power_controller_gps_only_saver_switch";
    private static final String SWITCH_POWERHAL_ENABLE = "sprd_power_hal_debug_switch";

    private static final String KEY_POWER_DEBUG_LOG = "persist.sys.power.fw.debug";
    private static final String KEY_ALARM_DEBUG_LOG = "persist.sys.alarm.debug";

    private static final String KEY_POWER_CONTROLLER_ENABLE = "persist.sys.pwctl.enable";
    private static final String KEY_POWER_CONTROLLER_GPS_ENABLE = "persist.sys.pwctl.gps";
    private static final String KEY_POWER_CONTROLLER_NETWORK_ENABLE = "persist.sys.pwctl.appidle";
    private static final String KEY_POWER_CONTROLLER_BGCLEAN_ENABLE = "persist.sys.pwctl.bgclean";
    private static final String KEY_POWER_CONTROLLER_WAKELOCK_ENABLE = "persist.sys.pwctl.wl";
    private static final String KEY_POWER_CONTROLLER_ONLY_IN_SAVER_ENABLE = "persist.sys.pwctl.onlysave";
    private static final String KEY_POWER_CONTROLLER_GPS_ONLY_IN_SAVER_ENABLE = "persist.sys.pwctl.gps.onlysave";
    private static final String KEY_POWERHAL_DEBUG_ENABLE = "persist.vendor.power.debug_d";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.power_log);

        mBattery = (TwoStatePreference) findPreference(SWITCH_BATTERY_LOG);
        mBattery.setOnPreferenceClickListener(this);

        mDoze = (TwoStatePreference) findPreference(SWITCH_DOZE_LOG);
        mDoze.setOnPreferenceClickListener(this);

        mDisplay = (TwoStatePreference) findPreference(SWITCH_DISPLAY_LOG);
        mDisplay.setOnPreferenceClickListener(this);

        mPowerGuru = (TwoStatePreference) findPreference(SWITCH_POWER_GURU_LOG);
        mPowerGuru.setOnPreferenceClickListener(this);

        mPowerHint = (TwoStatePreference) findPreference(SWITCH_POWER_HINT_LOG);
        mPowerHint.setOnPreferenceClickListener(this);

        mWakeLock = (TwoStatePreference) findPreference(SWITCH_WAKELOCK_LOG);
        mWakeLock.setOnPreferenceClickListener(this);

        mPowerController = (TwoStatePreference) findPreference(SWITCH_POWER_CONTROLLER_LOG);
        mPowerController.setOnPreferenceClickListener(this);

        mBatterySaver = (TwoStatePreference)  findPreference(SWITCH_BATTERY_SAVER_LOG);
        mBatterySaver.setOnPreferenceClickListener(this);

        mAlarm = (TwoStatePreference)  findPreference(SWITCH_ALARM_LOG);
        mAlarm.setOnPreferenceClickListener(this);

        mPowerControllerSwitch = (TwoStatePreference) findPreference(SWITCH_POWER_CONTROLLER_SWITCH);
        mPowerControllerSwitch.setOnPreferenceClickListener(this);

        mPowerControllerGPSSwitch = (TwoStatePreference) findPreference(SWITCH_POWER_CONTROLLER_GPS_SWITCH);
        mPowerControllerGPSSwitch.setOnPreferenceClickListener(this);

        mPowerControllerNetworkSwitch = (TwoStatePreference) findPreference(SWITCH_POWER_CONTROLLER_NETWORK_SWITCH);
        mPowerControllerNetworkSwitch.setOnPreferenceClickListener(this);

        mPowerControllerBgCleanSwitch = (TwoStatePreference) findPreference(SWITCH_POWER_CONTROLLER_BGCLEAN_SWITCH);
        mPowerControllerBgCleanSwitch.setOnPreferenceClickListener(this);

        mPowerControllerWakelockSwitch = (TwoStatePreference) findPreference(SWITCH_POWER_CONTROLLER_WAKELOCK_SWITCH);
        mPowerControllerWakelockSwitch.setOnPreferenceClickListener(this);

        mPowerControllerOnlyInSaverSwitch = (TwoStatePreference) findPreference(SWITCH_POWER_CONTROLLER_ONLY_IN_SAVER_SWITCH);
        mPowerControllerOnlyInSaverSwitch.setOnPreferenceClickListener(this);

        mPowerControllerGPSOnlyInSaverSwitch = (TwoStatePreference) findPreference(SWITCH_POWER_CONTROLLER_GPS_ONLY_IN_SAVER_SWITCH);
        mPowerControllerGPSOnlyInSaverSwitch.setOnPreferenceClickListener(this);

        mPowerHalSwitch = (TwoStatePreference) findPreference(SWITCH_POWERHAL_ENABLE);
        mPowerHalSwitch.setOnPreferenceClickListener(this);

        if (mPowerControllerSwitch.isChecked()) {
            mPowerControllerGPSSwitch.setEnabled(true);
            mPowerControllerNetworkSwitch.setEnabled(true);
            mPowerControllerBgCleanSwitch.setEnabled(true);
            mPowerControllerWakelockSwitch.setEnabled(true);
            mPowerControllerOnlyInSaverSwitch.setEnabled(true);
            mPowerControllerGPSOnlyInSaverSwitch.setEnabled(true);
        } else {
            mPowerControllerGPSSwitch.setEnabled(false);
            mPowerControllerNetworkSwitch.setEnabled(false);
            mPowerControllerBgCleanSwitch.setEnabled(false);
            mPowerControllerWakelockSwitch.setEnabled(false);
            mPowerControllerOnlyInSaverSwitch.setEnabled(false);
            mPowerControllerGPSOnlyInSaverSwitch.setEnabled(false);
        }

        Toast.makeText(PowerLogActivity.this, getResources().getString(R.string.sprd_log_prompt), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onPreferenceClick(Preference pref) {
        if (pref == mBattery) {
            if (mBattery.isChecked()) {
                Log.d(TAG, "open Battery log");
                setProperty(true, "battery");
            } else {
                Log.d(TAG, "off Battery log");
                setProperty(false, "battery");
            }
        } else if (pref == mDoze) {
            if (mDoze.isChecked()) {
                Log.d(TAG, "open doze log");
                setProperty(true, "doze");
            } else {
                Log.d(TAG, "off doze log");
                setProperty(false, "doze");
            }
        } else if (pref == mDisplay) {
            if (mDisplay.isChecked()) {
                Log.d(TAG, "open display log");
                setProperty(true, "display");
            } else {
                Log.d(TAG, "off display log");
                setProperty(false, "display");
            }
        } else if (pref == mPowerGuru) {
            if (mPowerGuru.isChecked()) {
                Log.d(TAG, "open powerguru log");
                setProperty(true, "guru");
            } else {
                Log.d(TAG, "off powerguru log");
                setProperty(false, "guru");
            }
        } else if (pref == mPowerHint) {
            if (mPowerHint.isChecked()) {
                Log.d(TAG, "open powerhint log");
                setProperty(true, "hint");
            } else {
                Log.d(TAG, "off powerhint log");
                setProperty(false, "hint");
            }
        } else if (pref == mWakeLock) {
            if (mWakeLock.isChecked()) {
                Log.d(TAG, "open wakelock log");
                setProperty(true, "wakelock");
            } else {
                Log.d(TAG, "off wakelock log");
                setProperty(false, "wakelock");
            }
        } else if (pref == mPowerController) {
            if (mPowerController.isChecked()) {
                Log.d(TAG, "open power controller log");
                setProperty(true, "controller");
            } else {
                Log.d(TAG, "off power controller log");
                setProperty(false, "controller");
            }
        } else if (pref == mBatterySaver) {
            if (mBatterySaver.isChecked()) {
                Log.d(TAG, "open battery saver log");
                setProperty(true, "saver");
            } else {
                Log.d(TAG, "off battery saver log");
                setProperty(false, "saver");
            }
        } else if (pref == mPowerHalSwitch){
            if (mPowerHalSwitch.isChecked()) {
                Log.d(TAG, "open power hal log");
                setSwitchValue(KEY_POWERHAL_DEBUG_ENABLE, true);
            } else {
                Log.d(TAG, "off power hal log");
                setSwitchValue(KEY_POWERHAL_DEBUG_ENABLE, false);
            }

        } else if (pref == mAlarm) {
            if (mAlarm.isChecked()) {
                Log.d(TAG, "open alarm log");
                setAlarmDebugLogEnable("1");
            } else {
                Log.d(TAG, "off alarm log");
                setAlarmDebugLogEnable("0");
            }
        } else {
            if (pref == mPowerControllerSwitch) {
                if (mPowerControllerSwitch.isChecked()) {
                    Log.d(TAG, "open power controller switch");
                    setSwitchValue(KEY_POWER_CONTROLLER_ENABLE, true);
                    mPowerControllerGPSSwitch.setEnabled(true);
                    mPowerControllerNetworkSwitch.setEnabled(true);
                    mPowerControllerBgCleanSwitch.setEnabled(true);
                    mPowerControllerWakelockSwitch.setEnabled(true);
                    mPowerControllerOnlyInSaverSwitch.setEnabled(true);
                    mPowerControllerGPSOnlyInSaverSwitch.setEnabled(true);
                } else {
                    Log.d(TAG, "close power controller switch");
                    setSwitchValue(KEY_POWER_CONTROLLER_ENABLE,false);
                    mPowerControllerGPSSwitch.setEnabled(false);
                    mPowerControllerNetworkSwitch.setEnabled(false);
                    mPowerControllerBgCleanSwitch.setEnabled(false);
                    mPowerControllerWakelockSwitch.setEnabled(false);
                    mPowerControllerOnlyInSaverSwitch.setEnabled(false);
                    mPowerControllerGPSOnlyInSaverSwitch.setEnabled(false);
                }
            } else if (pref == mPowerControllerGPSSwitch) {
                if (mPowerControllerGPSSwitch.isChecked()) {
                    setSwitchValue(KEY_POWER_CONTROLLER_GPS_ENABLE,true);
                    Log.d(TAG, "open power controller GPS switch");
                } else {
                    setSwitchValue(KEY_POWER_CONTROLLER_GPS_ENABLE, false);
                    Log.d(TAG, "close power controller GPS switch");
                }
            } else if (pref == mPowerControllerNetworkSwitch) {
                if (mPowerControllerNetworkSwitch.isChecked()) {
                    setSwitchValue(KEY_POWER_CONTROLLER_NETWORK_ENABLE, true);
                    Log.d(TAG, "open power controller network switch");
                } else {
                    setSwitchValue(KEY_POWER_CONTROLLER_NETWORK_ENABLE, false);
                    Log.d(TAG, "close power controller network switch");
                }
            } else if (pref == mPowerControllerBgCleanSwitch) {
                if (mPowerControllerBgCleanSwitch.isChecked()) {
                    setSwitchValue(KEY_POWER_CONTROLLER_BGCLEAN_ENABLE, true);
                    Log.d(TAG, "open power controller bgClean switch");
                } else {
                    setSwitchValue(KEY_POWER_CONTROLLER_BGCLEAN_ENABLE, false);
                    Log.d(TAG, "close power controller bgClean switch");
                }
            } else if (pref == mPowerControllerWakelockSwitch) {
                if (mPowerControllerWakelockSwitch.isChecked()) {
                    setSwitchValue(KEY_POWER_CONTROLLER_WAKELOCK_ENABLE, true);
                    Log.d(TAG, "open power controller wakelock switch");
                } else {
                    setSwitchValue(KEY_POWER_CONTROLLER_WAKELOCK_ENABLE, false);
                    Log.d(TAG, "close power controller wakelock switch");
                }
            } else if (pref == mPowerControllerOnlyInSaverSwitch) {
                if (mPowerControllerOnlyInSaverSwitch.isChecked()) {
                    setSwitchValue(KEY_POWER_CONTROLLER_ONLY_IN_SAVER_ENABLE, true);
                    Log.d(TAG, "open power controller only in saver switch");
                } else {
                    setSwitchValue(KEY_POWER_CONTROLLER_ONLY_IN_SAVER_ENABLE, false);
                    Log.d(TAG, "close power controller only in saver switch");
                }
            } else if (pref == mPowerControllerGPSOnlyInSaverSwitch) {
                if (mPowerControllerGPSOnlyInSaverSwitch.isChecked()) {
                    setSwitchValue(KEY_POWER_CONTROLLER_GPS_ONLY_IN_SAVER_ENABLE, true);
                    Log.d(TAG, "open power controller GPS only in saver switch");
                } else {
                    setSwitchValue(KEY_POWER_CONTROLLER_GPS_ONLY_IN_SAVER_ENABLE, false);
                    Log.d(TAG, "close power controller GPS only in saver switch");
                }
            }
        }

        return true;
    }

    public void setProperty(boolean enabled, String debugtype) {
        if (enabled) {
            if (getPowerDebugLog().equals("null")) {
                setPersistValue(setfirstPersistValue() + debugtype);
            } else {
                setPersistValue(getPowerDebugLog() + "," + debugtype);
            }
        } else {
            if (getPowerDebugLog().equals(debugtype)) {
                setPersistValue("null");
            } else {
                setPersistValue(deletePowerDebugLog(debugtype));
            }
        }
    }

    private void setSwitchValue(String value, boolean enabled) {
        if (enabled) {
            SystemPropertiesProxy.set(value, "1");
        } else {
            SystemPropertiesProxy.set(value, "0");
        }
    }

    public String getPowerDebugLog() {
        return SystemPropertiesProxy.get(KEY_POWER_DEBUG_LOG,"null");
    }

    public String setfirstPersistValue() {
        String valueNew = null;
        String value = getPowerDebugLog();
        StringBuilder stringBuilder = new StringBuilder(value);
        String stringBuilderNew = stringBuilder.toString();
        if (stringBuilderNew.equals("null")) {
            valueNew = stringBuilderNew.replace("null", "");
        }
        return valueNew;
    }

    public String deletePowerDebugLog(String debugtpye) {
        String valueNew = null;
        String value = getPowerDebugLog();
        StringBuilder stringBuilder = new StringBuilder(value);
        String stringBuilderNew = stringBuilder.toString();

        if (stringBuilderNew.contains("," + debugtpye + ",") || stringBuilderNew.contains("," + debugtpye)) {
            valueNew = stringBuilderNew.replace("," + debugtpye, "");
        } else if (stringBuilderNew.contains(debugtpye + ",")) {
            valueNew = stringBuilderNew.replace(debugtpye + ",", "");
        }
        return valueNew;
    }

    public void setPersistValue(String debugtype) {
        SystemPropertiesProxy.set(KEY_POWER_DEBUG_LOG, debugtype);
    }

    public void setAlarmDebugLogEnable(String value) {
        SystemPropertiesProxy.set(KEY_ALARM_DEBUG_LOG, value);
    }

}
