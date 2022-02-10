package com.sprd.engineermode.debuglog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.sprd.engineermode.EMSwitchPreference;
import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;


public class CtaSecurityActivity extends PreferenceActivity implements
    Preference.OnPreferenceChangeListener {
    private static final String TAG = "CtaSecurityActivity";

    private static final String KEY_CTA_SECURITY_LOG = "cta_security_log";

    private static final String PROP_CTA_SECURITY_LOG = "persist.sys.cta.security.log";

    private static final String PROP_RO_CTA_SECURITY_FEATURE = "ro.cta.security.feature";

    private EMSwitchPreference mPrefCtaSecurityLog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_cta_security);
        initCtaSecurityPreferences();
    }

    private void initCtaSecurityPreferences() {
        mPrefCtaSecurityLog = (EMSwitchPreference) findPreference(KEY_CTA_SECURITY_LOG);
        boolean isLogEnable =
                SystemPropertiesProxy.getInt(PROP_CTA_SECURITY_LOG, 0) == 1;
        Log.d(TAG, "[initCtaSecurityPreferences]isLogEnable:" + isLogEnable);
        mPrefCtaSecurityLog.setChecked(isLogEnable);
        mPrefCtaSecurityLog.setOnPreferenceChangeListener(this);

        boolean isCtaSecurityFeatureSupported =
                SystemPropertiesProxy.getInt(PROP_RO_CTA_SECURITY_FEATURE, 0) == 1;
        Log.d(TAG, "[initCtaSecurityPreferences]isCtaSecurityFeatureSupported:" + isCtaSecurityFeatureSupported);
        mPrefCtaSecurityLog.setEnabled(isCtaSecurityFeatureSupported);
    }

    @Override
    public boolean onPreferenceChange(Preference pref, Object newValue) {
        boolean bValue = (boolean) newValue;
        if (pref == mPrefCtaSecurityLog) {
            Log.d(TAG, "[onPreferenceChange]set PROP_CTA_SECURITY_LOG " + bValue);
            if (bValue) {
                SystemPropertiesProxy.set(PROP_CTA_SECURITY_LOG, "1");
            } else {
                SystemPropertiesProxy.set(PROP_CTA_SECURITY_LOG, "0");
            }
        }
        return true;
    }
}