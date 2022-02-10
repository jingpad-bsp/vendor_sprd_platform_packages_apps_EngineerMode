
package com.sprd.engineermode.debuglog;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;
import com.unisoc.engineermode.core.intf.IDebugLogApi;
import com.unisoc.engineermode.core.utils.ShellUtils;
import java.io.File;
import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.impl.hardware.PhaseCheckParse;
import com.unisoc.engineermode.core.common.Const;

public class MIPILogSettingActivity extends PreferenceActivity implements
        OnPreferenceChangeListener, OnPreferenceClickListener {

    private static final String TAG = "MIPILogSettingActivity";
    private static final String MIPI_LOG_FILE_PATH = "/sys/class/modem/debug-log/channel";
    private static final String KEY_MIPI = "mipi_log";
    private static final String KEY_MIPI_SERDES = "mipi_log_serdes";
    private static final String KEY_MIPI_SERDES0 = "mipi_log_serdes0";
    private static final String KEY_MIPI_SERDES1 = "mipi_log_serdes1";
    private static final String KEY_MIPI_SERDES2 = "mipi_log_serdes2";

    private ListPreference mListMIPILog;
    private SharedPreferences mSharePref;
    private Handler mUiThread = new Handler();
    private Preference mMIPILogSerdes0;
    private Preference mMIPILogSerdes1;
    private Preference mMIPILogSerdes2;
    private IDebugLogApi debuglogApi = CoreApi.getDebugLogApi();
    private boolean mIsSupportNr = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_mipi_log);
        mListMIPILog = (ListPreference) findPreference(KEY_MIPI);
        mSharePref = PreferenceManager.getDefaultSharedPreferences(this);
        mListMIPILog.setOnPreferenceChangeListener(this);

        mMIPILogSerdes0 = (Preference) findPreference(KEY_MIPI_SERDES0);
        mMIPILogSerdes0.setOnPreferenceClickListener(this);
        mMIPILogSerdes1 = (Preference) findPreference(KEY_MIPI_SERDES1);
        mMIPILogSerdes1.setOnPreferenceClickListener(this);
        mMIPILogSerdes2 = (Preference) findPreference(KEY_MIPI_SERDES2);
        mMIPILogSerdes2.setOnPreferenceClickListener(this);

        mIsSupportNr = debuglogApi.mipiLogApi().isSupportNr();
        if (mIsSupportNr) {
            getPreferenceScreen().removePreference(mListMIPILog);
        } else {
            getPreferenceScreen().removePreference(mMIPILogSerdes0);
            getPreferenceScreen().removePreference(mMIPILogSerdes1);
            getPreferenceScreen().removePreference(mMIPILogSerdes2);
        }
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart...");
        super.onStart();
        if (mListMIPILog != null && !mIsSupportNr) {
            if (isSupportMipi()) {
                String value = debuglogApi.mipiLogApi().getMIPILogChannel();
                mListMIPILog.setValueIndex(Integer.parseInt(value.trim()));
                mListMIPILog.setSummary(mListMIPILog.getEntry());
            } else {
                mListMIPILog.setEnabled(false);
                mListMIPILog.setSummary(R.string.feature_not_support);
            }
        }
    }

    private boolean isSupportMipi() {
        boolean isSupport = false;
        boolean isExist = isFileExist();
        if (!Const.isUser() && isExist) {
            isSupport = true;
        }
        Log.d(TAG, "isExist: " + isExist + "isSupport: " + isSupport);
        return isSupport;
    }

    private boolean isFileExist() {
        boolean isExist = false;
        File file = new File(MIPI_LOG_FILE_PATH);
        if (file.exists()) {
            isExist = true;
        }
        return isExist;
    }

    private boolean writeToFile(String str) {
        Log.d(TAG, "writeToFile: " + str);
        if (ShellUtils.writeToFile(MIPI_LOG_FILE_PATH, str)) {
            Log.d(TAG, "write success");
            return true;
        } else {
            Log.d(TAG, "write fail");
            return false;
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String setValue = newValue.toString();
        Log.d(TAG, "setValueIndex: " + setValue);
        new Thread(new Runnable() {
            public void run() {
                if (new PhaseCheckParse().writeMIPISwitch(Integer.parseInt(setValue))/*writeToFile(setValue)*/) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            debuglogApi.mipiLogApi().setMIPILogChannel(setValue);
                            mListMIPILog.setValueIndex(Integer.parseInt(setValue.trim()));
                            mListMIPILog.setSummary(mListMIPILog.getEntry());
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mListMIPILog.setValueIndex(Integer.parseInt(debuglogApi.mipiLogApi().getMIPILogChannel()));
                            mListMIPILog.setSummary(mListMIPILog.getEntry());
                            Toast.makeText(MIPILogSettingActivity.this, "set fail!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        Log.d(TAG, "onPreferenceClick key = " + key);
        if (key == null) {
            return true;
        }
        if (key.contains(KEY_MIPI_SERDES)) {
            Intent intent = new Intent(this, MIPILogSerdesActivity.class);
            intent.putExtra(KEY_MIPI_SERDES, key);
            startActivity(intent);
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
