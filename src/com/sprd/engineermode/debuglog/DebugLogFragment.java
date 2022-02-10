
package com.sprd.engineermode.debuglog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.LocalSocketAddress;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.preference.TwoStatePreference;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.common.Const;
import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.impl.hardware.PhaseCheckParse;
import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;
import com.unisoc.engineermode.core.intf.IDebugLogApi;
import com.unisoc.engineermode.core.utils.IATUtils;
import com.unisoc.engineermode.core.utils.ShellUtils;
import com.unisoc.engineermode.core.utils.SocketUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.List;

public class DebugLogFragment extends PreferenceFragment implements
        Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
    private static final String TAG = "DebugLogFragment";
    private static final String KEY_GPS_CONFIG = "gps_config";
    private static final String KEY_AGPS_LOG = "agps_log";
    private static final String KEY_SYSTEM_SETTINGS = "system_settings";
    // Browser debug
    private static final String KEY_AVS_LOG = "avs_log";
    private static final String KEY_USEER_MODE = "switch_user_mode";
    private static final String KEY_SCREEN_OFF = "switch_screen_off_never";
    private static final String KEY_WATCH_DOG = "switch_watch_dog";
    private static final String WATCH_DOG_PATH = "/dev/sctl_pm";
    private static final String KEY_SYSTEM_UPDATE = "system_update";
    private static final String KEY_KERNEL_LOG_LEVEL_SWITCH = "log_level_switch";

    // VersionInfo
    private static final String KEY_HARDWARE_VERSION = "hardware_version";
    private static final String KEY_HARDWARE_VERSION2 = "hardware_version2";
    private static final String KEY_AP_VERSION = "ap_version";
    private static final String KEY_MODEM_VERSION = "modem_version";
    private static final String KEY_PS_VERSION = "ps_version";
    private static final String KEY_DSP_VERSION = "dsp_version";
    private static final String KEY_CP2_VERSION = "cp2_version";
    private static final String KEY_GPS_VERSION = "gps_version";
    private static final String KEY_TP_VERSION = "tp_version";
    private static final String KEY_CMD_TYPE = "cmd_type";
    private static final String KEY_PRECHECKOUT_TEST="precheck_test";
    private static final String KEY_USB_DEBUG_SWITCH = "switch_usbdebug";
    /* SPRD: 922313 - lvds dump @{ */
    private static final String KEY_LVDS_DUMP = "lvds_dump";
    private static final String KEY_LVDS_DUMP_GROUP = "persist.sys.lvds.dump";
    /* }@ */
    private static final String KEY_JEITA_CONTROL = "jeita_control";
    /* SPRD: 1192071 - Diagnosis and modem port lock */
    private static final String KEY_AT_DIAG = "at_diag";
    /* }@ */
    private static final String KEY_CABC_TEST = "cabc_test";

    //UNISOC: add for bug1310305
    private static final String KEY_BIND_BIG_CORE_SWITCH = "key_bind_big_core_switch";

    private static final String KEY_SFP_TEST = "sfp_key";
    private static final String KEY_QRM = "key_qrm";

    private static final String KEY_CPU_DEBUG = "cpu_debug";

    private static final String HARDWARE_VERSION_KEYWORD = "vendor.sys.hardware.version";

    private static final int GET_AVS_LOG = 0;
    private static final int OPEN_AVS_LOG = 1;
    private static final int CLOSE_AVS_LOG = 2;
    private static final int GET_USER_MODE = 3;
    private static final int OPEN_USER_MODE = 4;
    private static final int CLOSE_USER_MODE = 5;
    /* SPRD: 922313 - lvds dump @{ */
    private static final int SET_LVDS_DUMP_TYPE = 14;
    private static final int GET_LVDS_DUMP_TYPE = 15;
    /* }@ */
    private static final int GET_JEITA_TYPE = 16;
    private static final int SET_JEITA_TYPE = 17;
    private static final int GET_QRM_STATUS = 18;
    private static final int SET_QRM_STATUS = 19;

    private TwoStatePreference mUserMode;
    private TwoStatePreference mAVSLogSwitch;

    private TwoStatePreference mScreenOff;
    private TwoStatePreference mWatchDog;
    private Preference mGpsConfig;
    private Preference mAGpsLog;
    private Preference mSystemSet;
    private Preference mPreCheckTest;
    private Preference mSystemUpdate;
    private TwoStatePreference mKernelLogLevel;
    private SwitchPreference mSfpSwitch;
    private TwoStatePreference mUsbDebug;
    private TwoStatePreference mJeitaControl;
    private SwitchPreference mQrmSwitch;

    // VersionInfo
    private Preference mHardwareVersion;
    private Preference mHardwareVersion2;
    private Preference mAPVersion;

    private SwitchPreference mCabcTestSwitch;
    private SwitchPreference mCpuDebugSwitch;
    private Context mContext;
    private DEGHandler mDEGHandler;
    private Handler mUiThread = new Handler();

    private static final int SCREEN_OFF_NEVER_TIME = 360000000;
    private static final int FALLBACK_SCREEN_TIMEOUT_VALUE = 30000;
    private long mSettingsTimeout;
    public final static String SCREEN_OFF_TIME = "screen_off_time";
    private SharedPreferences mPrefs;

    private int mGroupIndex;
    private String mATResponse;
    private String mATCmd;

    private static final String SOCKET_NAME = "hidl_common_socket";
    private static final String PROCESS_NAME = "wcnd ";
    /* SPRD: 922313 - lvds dump @{ */
    private int mLVDSDumpIndex;
    private Preference mLVDSDump;
    private String[] mDumpGroup = new String[] {
            "Close LVDS DUMP", "Open LVDS DUMP upstream", "Open LVDS DUMP downstream", "Open LVDS DUMP upstream and downstream"
    };
    /* }@ */
    /* SPRD: 1314195 modem_assert @{ */
    private Toast mToast;
    private static final String KEY_AUTO_MODEM_ASSERT = "auto_modemassert_test";
    private SwitchPreference mModemAssertTest;
    private String mModemAt = "AT+SPTEST=45";
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    /* }@ */

    private static final String KEY_RPSSWITCH_ENABLED = "rps_switch";
    private int mRpsSwitchChipIndex = 0;
    public static SharedPreferences mDataTestPref;
    private TwoStatePreference mRpsSwitch;
    private static final String KEY_RPSCPU_HOTPLUG_PATH = "/sys/devices/system/cpu/cpuhotplug/dynamic_load_disable";
    private static final String KEY_RPSCPU_CHECK_X86_SOCK_FLOWS = "/proc/sys/net/core/rps_sock_flow_entries";
    private static final String CABC_PATH = "/sys/module/dpu_r4p0/parameters/cabc_disable";
    private static final String CPU_DEBUG_PATH = "/proc/sprd_hang_debug/wdt_disable";
    /* SPRD: 1192071 - Diagnosis and modem port lock */
    private TwoStatePreference mAtDiag;
    /* }@ */
    private IDebugLogApi debugLogApi = CoreApi.getDebugLogApi();
    //UNISOC: add for bug1310305
    private TwoStatePreference mBindBigCoreSwitch;
    /* SPRD: 1170609 - sfp function @{ */
    private static final String SOFT_FAST_PATH = "/proc/net/sfp/enable";

    @SuppressLint("HandlerLeak")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_debuglogtab);
        mContext = getActivity();
        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mDEGHandler = new DEGHandler(ht.getLooper());

        mGpsConfig = (Preference) findPreference(KEY_GPS_CONFIG);
        mGpsConfig.setOnPreferenceClickListener(this);
        mAGpsLog = (Preference) findPreference(KEY_AGPS_LOG);
        mAGpsLog.setOnPreferenceClickListener(this);
        mGpsConfig.setEnabled(false);
        mAGpsLog.setEnabled(false);

        mSystemSet = (Preference) findPreference(KEY_SYSTEM_SETTINGS);

        mPreCheckTest = (Preference) findPreference(KEY_PRECHECKOUT_TEST);
        mPreCheckTest.setOnPreferenceClickListener(this);

        mAVSLogSwitch = (TwoStatePreference) findPreference(KEY_AVS_LOG);
        mAVSLogSwitch.setOnPreferenceChangeListener(this);

        mUsbDebug = (TwoStatePreference) findPreference(KEY_USB_DEBUG_SWITCH);
        mUsbDebug.setOnPreferenceChangeListener(this);
        mUserMode = (TwoStatePreference) findPreference(KEY_USEER_MODE);

        mUserMode.setOnPreferenceChangeListener(this);
        mUserMode.setEnabled(false);
        mUserMode.setSummary(R.string.feature_not_support);

        mScreenOff = (TwoStatePreference) findPreference(KEY_SCREEN_OFF);
        mScreenOff.setOnPreferenceChangeListener(this);

        mWatchDog = (TwoStatePreference) findPreference(KEY_WATCH_DOG);
        mWatchDog.setOnPreferenceChangeListener(this);

        mSystemUpdate = (Preference) findPreference(KEY_SYSTEM_UPDATE);

        /* SPRD Bug 793108: Add kernel log level switch. @{ */
        mKernelLogLevel = (TwoStatePreference) findPreference(KEY_KERNEL_LOG_LEVEL_SWITCH);
        mKernelLogLevel.setOnPreferenceChangeListener(this);
        /* @} */
        /* SPRD: 1192071 - Diagnosis and modem port lock */
        mAtDiag = (TwoStatePreference) findPreference(KEY_AT_DIAG);
        mAtDiag.setOnPreferenceChangeListener(this);
        /* @} */
        mHardwareVersion = (Preference) findPreference(KEY_HARDWARE_VERSION);
        mHardwareVersion2 = (Preference) findPreference(KEY_HARDWARE_VERSION2);
        mAPVersion = (Preference) findPreference(KEY_AP_VERSION);
        /* UNISOC： 1314195: Add modem auto-assert  switch. @{ */
        mModemAssertTest = (SwitchPreference) findPreference(KEY_AUTO_MODEM_ASSERT);
        mModemAssertTest.setOnPreferenceChangeListener(this);
        mPref = getContext().getSharedPreferences("modem_assert", Context.MODE_PRIVATE);
        mEditor = mPref.edit();
        /* @} */
        //mAPVersion.setSummary(Build.DISPLAY);
        if (Const.isUser()) {
            mAPVersion.setSummary(SystemPropertiesProxy.get("ro.build.description"));
        } else {
            mAPVersion.setSummary(Build.DISPLAY);
        }
        Preference psVersion = (Preference) findPreference(KEY_PS_VERSION);
        psVersion.setOnPreferenceClickListener(this);

        Preference dspVersion = (Preference) findPreference(KEY_DSP_VERSION);
        dspVersion.setOnPreferenceClickListener(this);

        Preference cp2Version = (Preference) findPreference(KEY_CP2_VERSION);
        cp2Version.setOnPreferenceClickListener(this);

        Preference tpVersion = (Preference) findPreference(KEY_TP_VERSION);
        tpVersion.setOnPreferenceClickListener(this);
        Preference modemVersion = (Preference) findPreference(KEY_MODEM_VERSION);
        modemVersion.setOnPreferenceClickListener(this);

        mRpsSwitch = (TwoStatePreference) findPreference(KEY_RPSSWITCH_ENABLED);
        mRpsSwitch.setOnPreferenceChangeListener(this);

        mCabcTestSwitch = (SwitchPreference) findPreference(KEY_CABC_TEST);
        mCabcTestSwitch.setOnPreferenceChangeListener(this);

        mQrmSwitch = (SwitchPreference) findPreference(KEY_QRM);
        mQrmSwitch.setOnPreferenceChangeListener(this);

        mCpuDebugSwitch = (SwitchPreference) findPreference(KEY_CPU_DEBUG);
        mCpuDebugSwitch.setOnPreferenceChangeListener(this);
        if (!new File(CPU_DEBUG_PATH).isFile()){
            mCpuDebugSwitch.setEnabled(false);
        }

        if (mRpsSwitch != null) {
            if (allCpuOnline() == true) {
                mRpsSwitch.setChecked(true);
                mRpsSwitch.setSummary("On");
            } else {
                mRpsSwitch.setChecked(false);
                mRpsSwitch.setSummary("Off");
            }
        }
        if (mRpsSwitch != null) {
            mDataTestPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            int mDataTestState = mDataTestPref.getInt("test_mode_state", 0);
            if (mDataTestState ==1) {
               mRpsSwitch.setEnabled(false);
            }else {
                mRpsSwitch.setEnabled(true);
            }
        }

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        long screenTime = mPrefs.getLong(SCREEN_OFF_TIME, -1);
        mSettingsTimeout = Settings.System.getLong(getActivity().getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT, FALLBACK_SCREEN_TIMEOUT_VALUE);
        if (screenTime == -1 || (screenTime != -1 && mSettingsTimeout != screenTime)) {
            saveScreenTime();
        }
        /* SPRD: 922313 - lvds dump @{ */
        mLVDSDump = (Preference) findPreference(KEY_LVDS_DUMP);
        String mLVDSDumpStr = SystemPropertiesProxy.get(KEY_LVDS_DUMP_GROUP, "");
        if (!mLVDSDumpStr.equals("")) {
            mLVDSDump.setSummary(mLVDSDumpStr);
        } else {
            mLVDSDump.setSummary("Close LVDS DUMP");
        }
        mLVDSDump.setOnPreferenceClickListener(this);
        /* }@ */

        mJeitaControl = (TwoStatePreference) findPreference(KEY_JEITA_CONTROL);
        mJeitaControl.setOnPreferenceChangeListener(this);

        //UNISOC: add for bug1310305
        mBindBigCoreSwitch = (TwoStatePreference) findPreference(KEY_BIND_BIG_CORE_SWITCH);
        mBindBigCoreSwitch.setOnPreferenceChangeListener(this);
        /* SPRD: 1170609 - sfp function @{ */
        mSfpSwitch = (SwitchPreference) findPreference(KEY_SFP_TEST);
        mSfpSwitch.setOnPreferenceChangeListener(this);
        /* @} */

        getPreferenceScreen().removePreference(findPreference("switch_user_mode"));
        getPreferenceScreen().removePreference(findPreference("avs_log"));
        getPreferenceScreen().removePreference(findPreference("gps_config"));
        getPreferenceScreen().removePreference(findPreference("agps_log"));
        getPreferenceScreen().removePreference(findPreference("log_level_switch"));
    }

    public void saveScreenTime() {
        if ((int) mSettingsTimeout == SCREEN_OFF_NEVER_TIME) {
            return;
        }
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putLong(SCREEN_OFF_TIME, mSettingsTimeout);
        editor.apply();
    }

    /* SPRD Bug 793108: Add kernel log level switch. @{ */
    public int getKernelLogLevelState() {
        int logLevelState = new PhaseCheckParse().getKernelLogLevelState();
        if (logLevelState == 1) {
            return 1;
        }
        return 0;
    }

    private boolean allCpuOnline() {
        String strValue;

        strValue = readFile(KEY_RPSCPU_CHECK_X86_SOCK_FLOWS);
        if ("0".equals(strValue) || "readError".equals(strValue)) {
            return false;
        } else {
            return true;
        }
    }

    public boolean setKernelLogLevelState(int state) {
        return new PhaseCheckParse().setKernelLogLevelState(state);
    }
    /* @} */

    @Override
    public void onStart() {
        /* SPRD Bug 793108: Add kernel log level switch. @{ */
        int state = getKernelLogLevelState();  //state: 1--on; 0--off
        Log.d(TAG, "kernel log level state value is: " + state);
        if (state == 1) {
            mKernelLogLevel.setChecked(true);
        } else {
            mKernelLogLevel.setChecked(false);
        }
        /* @} */

        if(!checkApkExist("com.sprd.systemupdate")) {
            mSystemUpdate.setEnabled(false);
            mSystemUpdate.setSummary(R.string.apk_not_exist);
        }

        if (SystemPropertiesProxy.get("ro.board.board.customer", "none").equalsIgnoreCase("cgmobile")) {
            mHardwareVersion.setSummary(SystemPropertiesProxy.get("ro.product.hardware", "P1"));
        } else {
            mHardwareVersion.setSummary(SystemPropertiesProxy.get("ro.product.hardware", "UNISOC"));
        }
        if (mHardwareVersion2 != null) {
            mHardwareVersion2.setSummary(SystemPropertiesProxy.get(HARDWARE_VERSION_KEYWORD, "unsupported"));
        }
        if (mAVSLogSwitch != null && mAVSLogSwitch.isEnabled()) {
            Message getAvslog = mDEGHandler.obtainMessage(GET_AVS_LOG);
            mDEGHandler.sendMessage(getAvslog);
        }
        if (mJeitaControl != null && mJeitaControl.isEnabled()) {
            Message getJeitaStatus = mDEGHandler.obtainMessage(GET_JEITA_TYPE);
            mDEGHandler.sendMessage(getJeitaStatus);
        }

        if (mUserMode != null && mUserMode.isEnabled()) {
            Message getUserMode = mDEGHandler.obtainMessage(GET_USER_MODE);
            mDEGHandler.sendMessage(getUserMode);
        }

        if (mQrmSwitch != null && mQrmSwitch.isEnabled()) {
            Message getQrmStatus = mDEGHandler.obtainMessage(GET_QRM_STATUS);
            mDEGHandler.sendMessage(getQrmStatus);
        }

        if (!mScreenOff.isChecked()) {
            mScreenOff.setSummary(R.string.close_screen_off_never);
        } else {
            mScreenOff.setSummary(null);
        }

        /* SPRD: 922313 - lvds dump @{ */
        if (mLVDSDump != null && mLVDSDump.isEnabled()) {
            Message mLVDSDumpType = mDEGHandler.obtainMessage(GET_LVDS_DUMP_TYPE);
            mDEGHandler.sendMessage(mLVDSDumpType);
        }
        /* }@ */
        /* SPRD: 1192071 - Diagnosis and modem port lock */
        if (mAtDiag != null && mAtDiag.isEnabled()) {
            mAtDiag.setChecked(debugLogApi.atDiagApi().isAtDiagEnabled());
        }
        /* }@ */
        String cabcState = ShellUtils.execShellCmd("cat " + CABC_PATH);
        mCabcTestSwitch.setChecked(("0").equals(cabcState.trim()) ? true : false);

        /* SPRD: 1170609 - sfp function @{ */
        if (mSfpSwitch != null && mSfpSwitch.isEnabled()) {
            String sfpStatus = ShellUtils.execShellStr("cat " + SOFT_FAST_PATH).trim();
            Log.d(TAG, "sfpStatus: " + sfpStatus);
            mSfpSwitch.setChecked(sfpStatus.equals("1"));
        }
        /* @} */

        mCpuDebugSwitch.setChecked(debugLogApi.cpuDebugApi().get());

        mUsbDebug.setChecked(Settings.Global.getInt(getActivity().getContentResolver(), Settings.Global.ADB_ENABLED, 0) != 0);
        //UNISOC: add for bug1310305
        if (mBindBigCoreSwitch != null) {
            boolean bindbigcoreswitch = SystemPropertiesProxy.getBoolean("persist.sys.rps.udp", false);
            mBindBigCoreSwitch.setChecked(bindbigcoreswitch);
            if (bindbigcoreswitch) {
                Intent intent = new Intent();
                intent.setAction("com.unisoc.intent.action.UDP_TEST");
                mContext.sendBroadcast(intent);
            }
        }
        super.onStart();
    }

    @Override
    public void onResume() {
        /* Modify sprd_res_monitor service whether to restart for CTS */
        long mCurrentTime = Settings.System.getLong(getActivity()
                .getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT,
                FALLBACK_SCREEN_TIMEOUT_VALUE);
        if ((int) mCurrentTime != SCREEN_OFF_NEVER_TIME) {
            mScreenOff.setChecked(false);
            mScreenOff.setSummary(R.string.close_screen_off_never);
        }

            mSystemSet.setEnabled(true);
            mSystemSet.setSummary("");
        /* SPRD 917039: watch dog related to sysdump. {@ */
        Log.d(TAG, "sysdump:" + SystemPropertiesProxy.get("persist.vendor.sysdump", "off"));
        if (!Const.isUser()) {
            mWatchDog.setChecked(false);
            mWatchDog.setEnabled(false);
        } else {
            mWatchDog.setEnabled(true);
            mWatchDog.setChecked("on".equals(SystemPropertiesProxy.get("persist.vendor.modem.spwdt","on")));
        }
        /* @} */
        /* UNISOC： 1314195: Add modem auto-assert  switch. @{ */
        boolean modemStatus = SystemProperties.get("persist.vendor.sys.modem.autoassert", "false").equals("true");
        mModemAssertTest.setChecked(modemStatus);
        /* @} */
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if (mDEGHandler != null) {
            mDEGHandler.getLooper().quit();
            Log.d(TAG, "HandlerThread has quit");
        }
        super.onDestroy();
    }

    @Override
    public boolean onPreferenceClick(Preference pref) {
        if(pref == mPreCheckTest){
            Intent intent = new Intent(getActivity(), com.sprd.engineermode.debuglog.PreCheckApkActivity.class);
            startActivity(intent);
        } else if (pref == mGpsConfig) {
            Intent intent = new Intent();
            intent.setAction("android.settings.GPS_CONFIG");
            startActivity(intent);
        } else if (pref == mAGpsLog) {
            Intent intent = new Intent();
            intent.setAction("android.settings.AGPS_LOG_SHOW");
            startActivity(intent);
        } else if (pref.getKey().equals(KEY_MODEM_VERSION)) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString(KEY_CMD_TYPE, KEY_MODEM_VERSION);
            intent.putExtras(bundle);
            intent.setClass(mContext, VersionInfoActivity.class);
            startActivity(intent);
        } else if (pref.getKey().equals(KEY_PS_VERSION)) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString(KEY_CMD_TYPE, KEY_PS_VERSION);
            intent.putExtras(bundle);
            intent.setClass(mContext, VersionInfoActivity.class);
            startActivity(intent);
        } else if (pref.getKey().equals(KEY_DSP_VERSION)) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString(KEY_CMD_TYPE, KEY_DSP_VERSION);
            intent.putExtras(bundle);
            intent.setClass(mContext, VersionInfoActivity.class);
            startActivity(intent);
        } else if (pref.getKey().equals(KEY_CP2_VERSION)) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString(KEY_CMD_TYPE, KEY_CP2_VERSION);
            intent.putExtras(bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
            intent.setClass(mContext, VersionInfoActivity.class);
            startActivity(intent);
        } else if (pref.getKey().equals(KEY_GPS_VERSION)) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString(KEY_CMD_TYPE, KEY_GPS_VERSION);
            intent.putExtras(bundle);
            intent.setClass(mContext, VersionInfoActivity.class);
            startActivity(intent);
        } else if (pref.getKey().equals(KEY_TP_VERSION)){
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString(KEY_CMD_TYPE, KEY_TP_VERSION);
            intent.putExtras(bundle);
            intent.setClass(mContext, VersionInfoActivity.class);
            startActivity(intent);
        /* SPRD: 922313 - lvds dump @{ */
        } else if (pref == mLVDSDump) {
            popDialog();
        }
        return true;
    }

    /* SPRD: 922313 - lvds dump @{ */
    private String sendAt(String cmd, String servername) {
        String res = IATUtils.sendATCmd(cmd, servername);
        Log.d(TAG, "ATCmd is " + cmd + ", result is " + res);
        if (res != null) {
            return res;
        } else {
            return "FAILED";
        }
    }

    private void popDialog() {
        Builder alertBuilder = new AlertDialog.Builder(mContext);
        mGroupIndex = getLVDSSingleChoiceIndex(SystemPropertiesProxy.get(KEY_LVDS_DUMP_GROUP, ""));
        alertBuilder.setTitle(getString(R.string.lvds_dump))
            .setSingleChoiceItems(mDumpGroup, mGroupIndex,
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which >= 0) {
                        mGroupIndex = which;
                        Log.d(TAG, "mGroupIndex: " + mGroupIndex);
                        Message mLVDSDumpType = mDEGHandler.obtainMessage(SET_LVDS_DUMP_TYPE, mGroupIndex);
                        mDEGHandler.sendMessage(mLVDSDumpType);
                        mLVDSDump.setSummary(mDumpGroup[mGroupIndex]);
                        SystemPropertiesProxy.set(KEY_LVDS_DUMP_GROUP, mDumpGroup[mGroupIndex]);
                    }
                    dialog.dismiss();
                }
            });

        final AlertDialog mAlertDialog = alertBuilder
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null).create();
        mAlertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                    KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK)
                        && (event.getAction() == KeyEvent.ACTION_UP)) {
                    mAlertDialog.dismiss();
                }
                return false;
            }
        });
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.show();
    }

    @Override
    public boolean onPreferenceChange(Preference pref, Object objValue) {
        /* UNISOC: 1314195: Add modem auto-assert  switch. @{ */
        if (pref == mModemAssertTest) {
            Log.d(TAG,"AutoModemAssertTest");
            if (!mModemAssertTest.isChecked()) {
                SystemProperties.set("persist.vendor.sys.modem.autoassert", "true");
                mModemAssertTest.setChecked(true);
                Intent intent = new Intent(mContext, ModemAssertTestActivity.class);
                startActivity(intent);
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setMessage(R.string.modem_assert_ask).create();
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getActivity().getString(R.string.modem_assert_back), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SystemProperties.set("persist.vendor.sys.modem.autoassert", "true");
                        mModemAssertTest.setChecked(true);
                        Intent intent= new Intent(getActivity(),ModemAssertTestActivity.class);
                        startActivity(intent);
                        Log.d(TAG,"AutoModemAssertTest start");
                    }
                });
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, mContext.getString(R.string.modem_assert_close),new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SystemProperties.set("persist.vendor.sys.modem.autoassert", "false");
                        mModemAssertTest.setChecked(false);
                        String at =  mModemAt  + ",0,0";
                        String result = IATUtils.sendATCmd(at, "atchannel0");
                        Intent intent= new Intent(getActivity(),ModemAssertTestService.class);
                        mContext.stopService(intent);
                        try {
                            if (result.contains(IATUtils.AT_OK)) {
                                mToast = Toast.makeText(getActivity(), "modem close success", Toast.LENGTH_SHORT);
                                mToast.show();
                            } else {
                                mToast = Toast.makeText(getActivity(), "modem close failed", Toast.LENGTH_SHORT);
                                mToast.show();
                            }
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG,"AutoModemAssertTest close");
                    }
                });
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
            return true;
        }
        /**  }@ **/
        /* SPRD Bug 793108: Add kernel log level switch. @{ */
        if (pref == mKernelLogLevel) {
            if (mKernelLogLevel.isChecked()) {
                boolean retValueOn = setKernelLogLevelState(0);
                Log.d(TAG, "retValueOn =: " + retValueOn);
            } else {
                boolean retValueOff = setKernelLogLevelState(1);
                Log.d(TAG, "retValueOff =: " + retValueOff);
            }
            return true;
        /* @} */
        } else if (pref == mAVSLogSwitch) {
            if (!mAVSLogSwitch.isChecked()) {
                Message openAvslog = mDEGHandler.obtainMessage(OPEN_AVS_LOG);
                mDEGHandler.sendMessage(openAvslog);
            } else {
                Message closeAvslog = mDEGHandler.obtainMessage(CLOSE_AVS_LOG);
                mDEGHandler.sendMessage(closeAvslog);
            }
        } else if (pref == mUserMode) {
            if (!mUserMode.isChecked()) {
                Message openUserMode = mDEGHandler.obtainMessage(OPEN_USER_MODE);
                mDEGHandler.sendMessage(openUserMode);
            } else {
                Message closeUserMode = mDEGHandler.obtainMessage(CLOSE_USER_MODE);
                mDEGHandler.sendMessage(closeUserMode);
            }
        } else if (pref == mScreenOff) {
            if (!mScreenOff.isChecked()) {
                mSettingsTimeout = Settings.System.getLong(getActivity().getContentResolver(),
                        Settings.System.SCREEN_OFF_TIMEOUT, FALLBACK_SCREEN_TIMEOUT_VALUE);

                saveScreenTime();
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.SCREEN_OFF_TIMEOUT, SCREEN_OFF_NEVER_TIME);
                mScreenOff.setSummary(null);
            } else {
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.SCREEN_OFF_TIMEOUT, (int) mSettingsTimeout);
                mScreenOff.setSummary(R.string.close_screen_off_never);
            }

        } else if (pref == mWatchDog) {
            if(mWatchDog.isChecked()) {
                SystemPropertiesProxy.set("persist.vendor.modem.spwdt","off");
                ShellUtils.writeToFile(WATCH_DOG_PATH, "watchdog off");
            } else {
                SystemPropertiesProxy.set("persist.vendor.modem.spwdt","on");
                ShellUtils.writeToFile(WATCH_DOG_PATH, "watchdog on");
            }
        } else if (pref == mRpsSwitch) {
            if (mRpsSwitch.isChecked()) {
                SystemPropertiesProxy.set("ctl.start","vendor.rps_off");
                mRpsSwitch.setSummary("Off");
                Log.d(TAG, "close rps now.");
            } else {
                SystemPropertiesProxy.set("ctl.start","vendor.rps_on");
                mRpsSwitch.setSummary("On");
                Log.d(TAG, "open rps now.");
            }
        } else if (pref == mUsbDebug) {
            if (!mUsbDebug.isChecked()) {
                openDebugMode();
            } else {
                closeDebugMode();
            }
            return true;
        } else if (pref == mJeitaControl) {
            Message setJeitaType = mDEGHandler.obtainMessage(SET_JEITA_TYPE, mJeitaControl.isChecked() ? "0" : "1");
            mDEGHandler.sendMessage(setJeitaType);
            return true;
        } else if (pref == mAtDiag) {
            if (mAtDiag.isChecked()) {
                try {
                    debugLogApi.atDiagApi().closeAtDiag();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    debugLogApi.atDiagApi().openAtDiag();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (pref == mCabcTestSwitch) {
            boolean enableCabc = !mCabcTestSwitch.isChecked();
            try {
                debugLogApi.cabcApi().set(enableCabc);
                mCabcTestSwitch.setChecked(enableCabc);
            } catch (Exception e) {
                e.printStackTrace();
            }
            /* @} */
            return true;
            //UNISOC: add for bug1310305
        }  else if (pref == mBindBigCoreSwitch) {
            if (!mBindBigCoreSwitch.isChecked()) {
                SystemPropertiesProxy.set("persist.sys.rps.udp", "true");
                Intent intent = new Intent();
                intent.setAction("com.unisoc.intent.action.UDP_TEST");
                mContext.sendBroadcast(intent);
            } else {
                SystemPropertiesProxy.set("persist.sys.rps.udp", "false");
            }
            return true;
        }
        /* @} */
        else if (pref == mSfpSwitch) {
            if (mSfpSwitch.isChecked()) {
                try {
                    debugLogApi.sfpTestApi().setSfp(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    debugLogApi.sfpTestApi().setSfp(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            /* @} */
            return true;
        } else if (pref == mQrmSwitch) {
                qrmAlertDialogShow();
            /**Unisoc:Bug1522715 add cpu debug switch @{ **/
        } else if (pref == mCpuDebugSwitch) {
            boolean enableCpuDebug = !mCpuDebugSwitch.isChecked();
            try {
                debugLogApi.cpuDebugApi().set(enableCpuDebug);
                mCpuDebugSwitch.setChecked(enableCpuDebug);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
           /**  @}  **/
        return true;
    }

    private void openDebugMode() {
        Settings.Global.putInt(getActivity().getContentResolver(),
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 1);
        Settings.Global.putInt(getActivity().getContentResolver(),
                Settings.Global.ADB_ENABLED, 1);
    }

    private void closeDebugMode() {
        Settings.Global.putInt(getActivity().getContentResolver(),
                Settings.Global.ADB_ENABLED, 0);
        Settings.Global.putInt(getActivity().getContentResolver(),
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0);
    }

    class DEGHandler extends Handler {

        public DEGHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case GET_AVS_LOG:
                    // /sys/power/avs_log does not exist on 5.0
                    String avsStatus = readFile("/sys/power/avs_log");
                    Log.d(TAG, "AVS Log status is " + avsStatus);
                    if (!"readError".equals(avsStatus) && avsStatus != null) {
                        if (avsStatus.contains("1")) {
                            mUiThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    mAVSLogSwitch.setChecked(true);
                                }
                            });
                        } else if (avsStatus.contains("0")) {
                            mUiThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    mAVSLogSwitch.setChecked(false);
                                }
                            });
                        } else {
                            mUiThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    mAVSLogSwitch.setEnabled(false);
                                    mAVSLogSwitch.setSummary(R.string.feature_abnormal);
                                }
                            });
                        }
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mAVSLogSwitch.setEnabled(false);
                                mAVSLogSwitch.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case OPEN_AVS_LOG:
                    String res = execShellStr("echo 1 > /sys/power/avs_log");
                    String curstatus = readFile("/sys/power/avs_log");
                    Log.d(TAG, "openavslog result is " + res + ", curstatus is " + curstatus);
                    if (curstatus.contains("1")) {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mAVSLogSwitch.setChecked(true);
                            }
                        });
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mAVSLogSwitch.setChecked(false);
                            }
                        });
                    }

                    break;
                case CLOSE_AVS_LOG:
                    String clores = execShellStr("echo 0 > /sys/power/avs_log");
                    String curstatus1 = readFile("/sys/power/avs_log");
                    Log.d(TAG, "closeavslog result is " + clores + ", curstatus is " + curstatus1);
                    if (curstatus1.contains("0")) {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mAVSLogSwitch.setChecked(false);
                            }
                        });
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mAVSLogSwitch.setChecked(true);
                            }
                        });
                    }
                    break;
                case GET_USER_MODE:
                    res = SocketUtils.sendCmdAndRecResult(SOCKET_NAME,
                            LocalSocketAddress.Namespace.ABSTRACT, PROCESS_NAME + "wcn at+cp2_enter_user?");
                    Log.d(TAG, "UserMode status is " + res);
                    if (res != null
                            && (res.contains("OK") || res.contains("Ok") || res.contains("oK") || res
                                    .contains("ok"))) {
                        if (res.contains("0")) {
                            mUiThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    mUserMode.setChecked(false);
                                }
                            });
                        } else if (res.contains("1")) {
                            mUiThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    mUserMode.setChecked(true);
                                }
                            });
                        }
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mUserMode.setEnabled(false);
                                mUserMode.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case OPEN_USER_MODE:
                    res = SocketUtils.sendCmdAndRecResult(SOCKET_NAME,
                            LocalSocketAddress.Namespace.ABSTRACT, PROCESS_NAME + "wcn at+cp2_enter_user=1");
                    Log.d(TAG, "open UserMode Res is " + res);
                    if (res != null
                            && (res.contains("OK") || res.contains("Ok") || res.contains("oK") || res
                                    .contains("ok"))) {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mUserMode.setChecked(true);
                            }
                        });
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mUserMode.setChecked(false);
                            }
                        });
                    }
                    break;
                case CLOSE_USER_MODE:
                    res = SocketUtils.sendCmdAndRecResult(SOCKET_NAME,
                            LocalSocketAddress.Namespace.ABSTRACT, PROCESS_NAME + "wcn at+cp2_enter_user=0");
                    Log.d(TAG, "close UserMode Res is " + res);
                    if (res != null
                            && (res.contains("OK") || res.contains("Ok") || res.contains("oK") || res
                                    .contains("ok"))) {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mUserMode.setChecked(false);
                            }
                        });
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mUserMode.setChecked(true);
                            }
                        });
                    }
                    break;
                case GET_QRM_STATUS:
                    mATCmd = engconstents.ENG_AT_GET_QRM;
                    mATResponse = IATUtils.sendATCmd(mATCmd, "atchannel0");
                    if (mATResponse != null && mATResponse.contains(IATUtils.AT_OK)) {
                        String str[] = mATResponse.split("\n");
                        String str1[] = str[0].split(",");
                        if (str1.length >= 3 && "0".equals(str1[2].trim())){
                            mUiThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    mQrmSwitch.setChecked(false);
                                }
                            });
                        } else {
                            mUiThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    mQrmSwitch.setChecked(true);
                                }
                            });
                        }
                    }
                    break;
                case SET_QRM_STATUS:
                    mATCmd = engconstents.ENG_AT_SET_QRM;
                    if ((boolean) msg.obj) {
                        mATCmd = mATCmd + "1";
                    } else {
                        mATCmd = mATCmd + "0";
                    }
                    mATResponse = IATUtils.sendATCmd(mATCmd , "atchannel0");
                    if (mATResponse != null && mATResponse.contains(IATUtils.AT_OK)) {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                rebootDevice("qrm_switch");
                            }
                        });
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mQrmSwitch.setChecked(!mQrmSwitch.isChecked());
                                mToast = Toast.makeText(getActivity(), "QRM set failed", Toast.LENGTH_SHORT);
                                mToast.show();
                            }
                        });
                    }
                    break;
                /* SPRD: 922313 - lvds dump @{ */
                case SET_LVDS_DUMP_TYPE:
                    mLVDSDumpIndex = (Integer) msg.obj;
                    String mRespSetLVDS = sendAt(engconstents.ENG_SET_AT_LVDS_DUMP + mLVDSDumpIndex, "atchannel0");
                    Log.d(TAG, "SET_LVDS_DUMP_TYPE mRespSetLVDS = " + mRespSetLVDS);
                    if (mRespSetLVDS != null && mRespSetLVDS.contains(IATUtils.AT_OK)) {
                        AlertDialogShow();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mLVDSDump.setSummary(mDumpGroup[mLVDSDumpIndex]);
                                SystemPropertiesProxy.set(KEY_LVDS_DUMP_GROUP, mDumpGroup[mLVDSDumpIndex]);
                            }
                        });
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "Set LVDS Dump type failed!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
                case GET_LVDS_DUMP_TYPE:
                    String mRespGetLVDS = sendAt(engconstents.ENG_GET_AT_LVDS_DUMP, "atchannel0");
                    Log.d(TAG, "GET_LVDS_DUMP_TYPE mRespGetLVDS = " + mRespGetLVDS);
                    if (mRespGetLVDS != null && mRespGetLVDS.contains("LVDSDUMP")) {
                        try {
                            if (mRespGetLVDS.contains("OK")) {
                                mLVDSDumpIndex = Integer.parseInt(mRespGetLVDS.split("\n")[0].split(":")[1]);
                            } else {
                                mLVDSDumpIndex = Integer.parseInt(mRespGetLVDS.split(":")[1]);
                            }
                            Log.d(TAG, "GET_LVDS_DUMP_TYPE mLVDSDumpIndex = " + mLVDSDumpIndex);
                            mGroupIndex = mLVDSDumpIndex;
                            mUiThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    mLVDSDump.setEnabled(true);
                                    mLVDSDump.setSummary(mDumpGroup[mLVDSDumpIndex]);
                                    SystemPropertiesProxy.set(KEY_LVDS_DUMP_GROUP, mDumpGroup[mLVDSDumpIndex]);
                                }
                            });
                        } catch (ArrayIndexOutOfBoundsException e) {
                            Log.d(TAG, "ArrayIndexOutOfBoundsException:" + e);
                        } catch (NullPointerException e) {
                            Log.d(TAG, "NullPointerException:" + e);
                        } catch (NumberFormatException e) {
                            Log.d(TAG, "NumberFormatException: " + e);
                        }
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mLVDSDump.setEnabled(false);
                            }
                        });
                    }
                    break;
                    /* }@ */
                case GET_JEITA_TYPE:
                    String jeitaStatus = debugLogApi.jeitaControlApi().getJeita();
                    mUiThread.post(() -> mJeitaControl.setChecked("0".equals(jeitaStatus) ? false : true));
                    break;
                case SET_JEITA_TYPE:
                    final String setJeitaValue = (String) msg.obj;
                    debugLogApi.jeitaControlApi().setJeita(setJeitaValue);
                    mUiThread.post(() -> mJeitaControl.setChecked("0".equals(setJeitaValue) ? false : true));
                    break;
                default:
                    break;
            }
        }
    }
    public void qrmAlertDialogShow() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setMessage(getString(R.string.qrm_switch_waring))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.alertdialog_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                    Message msg = mDEGHandler.obtainMessage(SET_QRM_STATUS,mQrmSwitch.isChecked());
                                    mDEGHandler.sendMessage(msg);
                            }
                        })
                .setNegativeButton(R.string.alertdialog_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mUiThread.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mQrmSwitch.setChecked(!mQrmSwitch.isChecked());
                                    }
                                });
                            }
                        }).create();
        alertDialog.show();
    }

    /* SPRD: 922313 - lvds dump @{ */
    public void AlertDialogShow() {
        AlertDialog alertDialog = new AlertDialog.Builder(this.getActivity())
                .setMessage(getString(R.string.reboot_setting_prompt))
                .setCancelable(false)
                .setPositiveButton(R.string.alertdialog_ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
                                pm.reboot("LVDS dump");
                            }
                        })
                .setNegativeButton(R.string.alertdialog_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                mUiThread.post(new Runnable() {
                                    @Override
                                    public void run() {
                                    }
                                });
                            }
                        }).create();
        alertDialog.show();
    }

    private int getLVDSSingleChoiceIndex(String itemName){
        if(itemName.equals("Close LVDS DUMP")){
            return 0;
        } else if (itemName.equals("Open LVDS DUMP upstream")){
            return 1;
        } else if (itemName.equals("Open LVDS DUMP downstream")){
            return 2;
        } else if (itemName.equals("Open LVDS DUMP upstream and downstream")){
            return 3;
        } else {
            return 0;
        }

    }
    /* }@ */

    private boolean isConnected() {
        boolean isConnected = false;
        Activity act = this.getActivity();
        if ( act == null ) {
            return isConnected;
        }
        ConnectivityManager connectManager = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = connectManager.getActiveNetworkInfo();
        if (netinfo != null) {
            if (netinfo.getType() == ConnectivityManager.TYPE_MOBILE
                    || netinfo.getType() == ConnectivityManager.TYPE_WIFI) {
                isConnected = true;
            }
        }
        return isConnected;
    }

    public String readFile(String path) {
        File file = new File(path);
        String str = new String("");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                str = str + line;
            }
        } catch (Exception e) {
            Log.d(TAG, "Read file error!!!");
            str = "readError";
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
        Log.d(TAG, "read " + path + " value is " + str.trim());
        return str.trim();
    }

    public String execShellStr(String cmd) {
        String[] cmdStrings = new String[] {
                "sh", "-c", cmd
        };
        StringBuffer retString = new StringBuffer("");

        try {
            Process process = Runtime.getRuntime().exec(cmdStrings);
            BufferedReader stdout = new BufferedReader(new InputStreamReader(
                    process.getInputStream(), "UTF-8"), 7777);
            BufferedReader stderr = new BufferedReader(new InputStreamReader(
                    process.getErrorStream(), "UTF-8"), 7777);

            String line = null;

            while ((null != (line = stdout.readLine())) || (null != (line = stderr.readLine()))) {
                if ("" != line) {
                    retString = retString.append(line).append("\n");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, cmd + ":" + retString.toString() + "");
        return retString.toString();
    }

    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(Integer.MAX_VALUE);

        if (!(serviceList.size() > 0)) {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className)) {
                isRunning = true;
                break;
            }
        }

        return isRunning;
    }

    private int getSingleChoiceIndex(String itemName){

        if(itemName.equals("CSSLAB")){
            return 0;
        }else if(itemName.equals("CSFLAB")){
            return 1;
        }else if(itemName.equals("CSFT")){
            return 2;
        }else if(itemName.equals("Beta")){
            return 3;
        }else{
            return 0;
        }

    }

    public boolean checkApkExist(String packageName) {
        if (packageName == null || "".equals(packageName)) {
            return false;
        }
        try {
           ApplicationInfo info = DebugLogFragment.this.getActivity().getPackageManager()
             .getApplicationInfo(packageName,
           PackageManager.GET_UNINSTALLED_PACKAGES);
           return true;
        } catch (Exception e) {
           return false;
        }
    }

    private void rebootDevice(String reasonStr) {
        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        pm.reboot(reasonStr);
    }
}
