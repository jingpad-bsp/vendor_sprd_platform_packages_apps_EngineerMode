
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
import android.widget.Toast;

import com.sprd.engineermode.EMSwitchPreference;
import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;
import static com.unisoc.engineermode.core.impl.nonpublic.MiscProxyKt.ActivityManager_forceStopPackage;

public class LauncherDebugSettingActivity extends PreferenceActivity implements
    Preference.OnPreferenceChangeListener {

    private static final String TAG = "LauncherDebugSettingActivity";
    private static final String KEY_LAUNCHER_PREFERENCESCREEN = "launcher_debug_preferencescreen";
    private static final String KEY_LAUNCHER_CATEGORY = "category_launcher";
    private static final String KEY_LAUNCHER_ALL_LOG = "launcher_all_log";
    private static final String KEY_LAUNCHER_EXTERNAL_LOG = "launcher_external_log";
    private static final String KEY_LAUNCHER_LOADER_LOG = "launcher_loader_log";
    private static final String KEY_LAUNCHER_WIDGET_LOG = "launcher_widget_log";
    private static final String KEY_LAUNCHER_PERFORMANCE_LOG = "launcher_perform_log";
    private static final String KEY_LAUNCHER_UNRAD_LOG = "launcher_unread_log";
    private static final String KEY_LAUNCHER_DYNAMICION_LOG = "launcher_dynamicicon_log";
    private static final String KEY_WALLPAPERS_LOG = "wallpapers_log";
    private static final String KEY_LAUNCHER_FW_LOG = "launcherframeworks_log";

    private static final String PROP_DEBUG_ALL = "persist.sys.launcher.all";
    private static final String PROP_DEBUG_EXTERNAL_MSG = "persist.sys.launcher.external";
    private static final String PROP_DEBUG_LOADER = "persist.sys.launcher.loader";
    private static final String PROP_DEBUG_WIDGET = "persist.sys.launcher.widget";
    private static final String PROP_DEBUG_PERFORMANCE = "persist.sys.launcher.perform";
    private static final String PROP_DEBUG_UNREAD = "persist.sys.launcher.unread";
    private static final String PROP_DEBUG_DYNAMIC_ICON = "persist.sys.launcher.dyicon";
    private static final String PROP_DEBUG_WALLPAPERS = "persist.sys.wallpaper.debug";
    private static final String PROP_DEBUG_LAUNCHER_FW = "persist.sys.launcherfw.debug";

    private static final String SPRD_BADGE_SUPPORT = "ro.launcher.badge";
    private static final String SPRD_DYNAMIC_ICON_SUPPORT = "ro.launcher.dynamic";

    /** Marker action used to check has platform launcher */
    private static final String
            ACTION_PLATFORM_LAUNCHER = "com.android.launcher.action.UNISOC_LAUNCHER3";

    private String mPlatformLauncherPkgName;

    private PreferenceCategory mPreGroup;
    private EMSwitchPreference mPrefAllLog;
    private EMSwitchPreference mPrefReceiverLog;
    private EMSwitchPreference mPrefLoaderLog;
    private EMSwitchPreference mPrefPerformLog;
    private EMSwitchPreference mPrefWidgetLog;
    private EMSwitchPreference mPrefUnreadLog;
    private EMSwitchPreference mPrefDynamicLog;
    private EMSwitchPreference mPrefWallpapersLog;
    private EMSwitchPreference mPrefLauncherFwLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_launcher_debug_settings);
        initLauncherAppPreferences();
        initOthersPreferences();
    }

    private boolean hasPlatformLauncher(String action, PackageManager pm) {
        if (TextUtils.isEmpty(action) || pm == null) {
            return false;
        }
        final Intent intent = new Intent(action);
        for (ResolveInfo info : pm.queryBroadcastReceivers(intent, 0)) {
            if (info.activityInfo != null) {
                mPlatformLauncherPkgName = info.activityInfo.packageName;
                return true;
            }
        }
        return false;
    }

    private void initLauncherAppPreferences() {
        boolean hasPlatformLauncher = hasPlatformLauncher(ACTION_PLATFORM_LAUNCHER, getPackageManager());
        PreferenceScreen myScreen = (PreferenceScreen) findPreference(KEY_LAUNCHER_PREFERENCESCREEN);
        mPreGroup = (PreferenceCategory) findPreference(KEY_LAUNCHER_CATEGORY);

        if (!hasPlatformLauncher) {
            myScreen.removePreference(mPreGroup);
            return;
        }

        mPrefAllLog = (EMSwitchPreference) findPreference(KEY_LAUNCHER_ALL_LOG);
        mPrefAllLog.setChecked(SystemPropertiesProxy.getBoolean(PROP_DEBUG_ALL, false));
        mPrefAllLog.setOnPreferenceChangeListener(this);

        mPrefReceiverLog = (EMSwitchPreference) findPreference(KEY_LAUNCHER_EXTERNAL_LOG);
        mPrefReceiverLog.setChecked(SystemPropertiesProxy.getBoolean(PROP_DEBUG_EXTERNAL_MSG, true));
        mPrefReceiverLog.setOnPreferenceChangeListener(this);

        mPrefLoaderLog = (EMSwitchPreference) findPreference(KEY_LAUNCHER_LOADER_LOG);
        mPrefLoaderLog.setChecked(SystemPropertiesProxy.getBoolean(PROP_DEBUG_LOADER, !Build.TYPE.equals("user")));
        mPrefLoaderLog.setOnPreferenceChangeListener(this);

        mPrefPerformLog = (EMSwitchPreference) findPreference(KEY_LAUNCHER_PERFORMANCE_LOG);
        mPrefPerformLog.setChecked(SystemPropertiesProxy.getBoolean(PROP_DEBUG_PERFORMANCE, false));
        mPrefPerformLog.setOnPreferenceChangeListener(this);

        mPrefWidgetLog = (EMSwitchPreference) findPreference(KEY_LAUNCHER_WIDGET_LOG);
        mPrefWidgetLog.setChecked(SystemPropertiesProxy.getBoolean(PROP_DEBUG_WIDGET, false));
        mPrefWidgetLog.setOnPreferenceChangeListener(this);

        mPrefUnreadLog = (EMSwitchPreference) findPreference(KEY_LAUNCHER_UNRAD_LOG);
        if (SystemPropertiesProxy.getBoolean(SPRD_BADGE_SUPPORT, false)) {
            mPrefUnreadLog.setChecked(SystemPropertiesProxy.getBoolean(PROP_DEBUG_UNREAD, false));
            mPrefUnreadLog.setOnPreferenceChangeListener(this);
        } else {
            mPreGroup.removePreference(mPrefUnreadLog);
        }

        mPrefDynamicLog = (EMSwitchPreference) findPreference(KEY_LAUNCHER_DYNAMICION_LOG);
        if (SystemPropertiesProxy.getBoolean(SPRD_DYNAMIC_ICON_SUPPORT, false)) {
            mPrefDynamicLog.setChecked(SystemPropertiesProxy.getBoolean(PROP_DEBUG_DYNAMIC_ICON, false));
            mPrefDynamicLog.setOnPreferenceChangeListener(this);
        } else {
            mPreGroup.removePreference(mPrefDynamicLog);
        }
    }

    private void initOthersPreferences() {
        mPrefWallpapersLog = (EMSwitchPreference) findPreference(KEY_WALLPAPERS_LOG);
        mPrefWallpapersLog.setChecked(SystemPropertiesProxy.getBoolean(PROP_DEBUG_WALLPAPERS, false));
        mPrefWallpapersLog.setOnPreferenceChangeListener(this);

        mPrefLauncherFwLog = (EMSwitchPreference) findPreference(KEY_LAUNCHER_FW_LOG);
        mPrefLauncherFwLog.setChecked(SystemPropertiesProxy.getBoolean(PROP_DEBUG_LAUNCHER_FW, false));
        mPrefLauncherFwLog.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference pref, Object newValue) {
        if (pref == mPrefAllLog) {
            SystemPropertiesProxy.set(PROP_DEBUG_ALL, ((boolean) newValue) ? "true" : "false");
        } else if (pref == mPrefReceiverLog) {
            SystemPropertiesProxy.set(PROP_DEBUG_EXTERNAL_MSG, ((boolean) newValue) ? "true" : "false");
        } else if (pref == mPrefLoaderLog) {
            SystemPropertiesProxy.set(PROP_DEBUG_LOADER, ((boolean) newValue) ? "true" : "false");
        } else if (pref == mPrefPerformLog) {
            SystemPropertiesProxy.set(PROP_DEBUG_PERFORMANCE, ((boolean) newValue) ? "true" : "false");
        } else if (pref == mPrefWidgetLog) {
            SystemPropertiesProxy.set(PROP_DEBUG_WIDGET, ((boolean) newValue) ? "true" : "false");
        } else if (pref == mPrefUnreadLog) {
            SystemPropertiesProxy.set(PROP_DEBUG_UNREAD, ((boolean) newValue) ? "true" : "false");
        } else if (pref == mPrefDynamicLog) {
            SystemPropertiesProxy.set(PROP_DEBUG_DYNAMIC_ICON, ((boolean) newValue) ? "true" : "false");
        } else if (pref == mPrefWallpapersLog) {
            SystemPropertiesProxy.set(PROP_DEBUG_WALLPAPERS, ((boolean) newValue) ? "true" : "false");
        } else if (pref == mPrefLauncherFwLog) {
            SystemPropertiesProxy.set(PROP_DEBUG_LAUNCHER_FW, ((boolean) newValue) ? "true" : "false");
        }

        if (((boolean) newValue)) {
            if (pref == mPrefWallpapersLog || pref == mPrefLauncherFwLog) {
                new AlertDialog.Builder(pref.getContext())
                    .setTitle(getResources().getString(R.string.launcher_fw_debug_enable_hint_title))
                    .setMessage(getResources().getString(R.string.launcher_fw_debug_enable_hint_msg))
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                        if (pm != null) {
                            pm.reboot(null);
                        }
                    }).create().show();
            } else {
                if (!TextUtils.isEmpty(mPlatformLauncherPkgName)) {
                    ActivityManager_forceStopPackage(mPlatformLauncherPkgName);
                    Toast.makeText(LauncherDebugSettingActivity.this,
                            getResources().getString(R.string.launcher_debug_enable_hint), Toast.LENGTH_SHORT).show();
                }
            }
        }
        return true;
    }
}
