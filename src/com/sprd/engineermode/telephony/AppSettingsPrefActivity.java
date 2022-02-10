
package com.sprd.engineermode.telephony;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;
import android.preference.Preference.OnPreferenceClickListener;

import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;
import com.unisoc.engineermode.core.utils.CommonUtils;

import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;

public class AppSettingsPrefActivity extends PreferenceActivity {

    private static final String TAG = "AppSettingsPrefActivity";

    public static final int REQUEST_UA_SETTINGS = 1;
    private static final String UA_SETTING = "ua_setting";

    private Preference mUAsetting;

    public static final String USER_AGENT_CHOICE = "user_agent_choice";
    public static final String CUSTOM_USER_AGENT_STRING = "custom_user_agent_string";
    public static final String OTHER_USER_AGENT_STRING = "other_user_agent_string";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_appsetting);
        mUAsetting = (Preference) findPreference(UA_SETTING);
        if(!checkApkExist("com.sprd.uasetting")) {
            mUAsetting.setEnabled(false);
            mUAsetting.setSummary(R.string.apk_not_exist);
        }
        mUAsetting.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (!CommonUtils.isCurrentUserDeviceOwner()) {

                    Toast.makeText(AppSettingsPrefActivity.this,
                            R.string.not_support_visitor_or_user_mode, Toast.LENGTH_SHORT).show();
                    return false;
                }

                Intent intent = new Intent();
                intent.setClassName("com.sprd.uasetting", "com.sprd.uasetting.UASettingActivity");
                startActivityForResult(intent, REQUEST_UA_SETTINGS);
                return false;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(TAG, "onActivityResult");
        Log.d(TAG, "resultCode: " + resultCode + "requestCode: " + requestCode + "RESULT_OK:"
                + RESULT_OK);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_UA_SETTINGS:
                    if (intent == null) {
                        return;
                    }
                    String customUaString = intent.getStringExtra(CUSTOM_USER_AGENT_STRING);
                    if (!TextUtils.isEmpty(customUaString)) {
                        Settings.System.putString(getContentResolver(), CUSTOM_USER_AGENT_STRING,
                                customUaString);
                    }
                    int uaChoice = intent.getIntExtra(USER_AGENT_CHOICE, -1);
                    if (uaChoice != -1) {
                        Settings.System.putInt(getContentResolver(),
                                USER_AGENT_CHOICE, uaChoice);
                    }
                    String otherUaString = intent
                            .getStringExtra(OTHER_USER_AGENT_STRING);
                    if (!TextUtils.isEmpty(otherUaString)) {
                        Settings.System.putString(getContentResolver(),
                                OTHER_USER_AGENT_STRING, otherUaString);
                    }
                    break;
            }
        } else {
            Log.d(TAG, "onActivityResult,resultCode: " + resultCode);
            return;
        }

    }

    public boolean checkApkExist(String packageName) {
        if (packageName == null || "".equals(packageName))
           return false;
        try {
           ApplicationInfo info = AppSettingsPrefActivity.this.getPackageManager()
             .getApplicationInfo(packageName,
           PackageManager.GET_UNINSTALLED_PACKAGES);
           return true;
        } catch (Exception e) {
           return false;
        }
    }
}
