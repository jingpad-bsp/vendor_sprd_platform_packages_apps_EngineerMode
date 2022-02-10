package com.sprd.engineermode.debuglog;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;

import com.sprd.engineermode.R;
import com.sprd.engineermode.EMSwitchPreference;
import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.intf.IDebugLogApi;

public class AIIPSettingActivity extends PreferenceActivity implements
        OnPreferenceChangeListener {

    private static final String TAG = "AIIPSettingActivity";
    private static final String KEY_AI_IP = "ai_ip_key";
    private static final String KEY_MODEL_LOG = "model_log_key";
    private static final String KEY_COMPILATION_LOG = "compilation_log_key";
    private static final String KEY_EXECUTION_LOG = "execution_log_key";
    private static final String KEY_CPUEXEC_LOG = "cpuexec_log_key";
    private static final String KEY_MANAGER_LOG = "manager_log_key";
    private static final String KEY_DRIVER_LOG = "driver_log_key";

    private EMSwitchPreference mModelLog, mCompilationlLog, mExecutionlLog, mCpuexecLog, mManagerLog, mDriverLog;
    private ListPreference mAIIPLog;
    private static final String NN_LOG_MODEL = "model";
    private static final String NN_LOG_COMPILATION = "compilation";
    private static final String NN_LOG_EXECUTION = "execution";
    private static final String NN_LOG_CPUEXEC = "cpuexec";
    private static final String NN_LOG_MANAGER = "manager";
    private static final String NN_LOG_DRIVER = "driver";

    private IDebugLogApi debuglogApi = CoreApi.getDebugLogApi();

    private String NN_LOG = "model compilation execution cpuexec manager driver";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_ai_ip);

        mModelLog = (EMSwitchPreference) findPreference(KEY_MODEL_LOG);
        mModelLog.setOnPreferenceChangeListener(this);

        mCompilationlLog = (EMSwitchPreference) findPreference(KEY_COMPILATION_LOG);
        mCompilationlLog.setOnPreferenceChangeListener(this);

        mExecutionlLog = (EMSwitchPreference) findPreference(KEY_EXECUTION_LOG);
        mExecutionlLog.setOnPreferenceChangeListener(this);

        mCpuexecLog = (EMSwitchPreference) findPreference(KEY_CPUEXEC_LOG);
        mCpuexecLog.setOnPreferenceChangeListener(this);

        mManagerLog = (EMSwitchPreference) findPreference(KEY_MANAGER_LOG);
        mManagerLog.setOnPreferenceChangeListener(this);

        mDriverLog = (EMSwitchPreference) findPreference(KEY_DRIVER_LOG);
        mDriverLog.setOnPreferenceChangeListener(this);

        mAIIPLog = (ListPreference) findPreference(KEY_AI_IP);
        mAIIPLog.setOnPreferenceChangeListener(this);

        initUI();
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshUI();
    }

    private void initUI() {
        mModelLog.setChecked(false);
        mCompilationlLog.setChecked(false);
        mExecutionlLog.setChecked(false);
        mCpuexecLog.setChecked(false);
        mManagerLog.setChecked(false);
        mDriverLog.setChecked(false);
    }

    private void refreshUI() {
        String mAIStatus = debuglogApi.aiIpApi().getAiIpStatus();
        if (mAIStatus.equals("1")) {
            mAIIPLog.setValueIndex(1);
            mAIIPLog.setSummary(mAIIPLog.getEntry());
        } else if (mAIStatus.equals("2")) {
            mAIIPLog.setValueIndex(2);
            mAIIPLog.setSummary(mAIIPLog.getEntry());
        } else if (mAIStatus.equals("0")) {
            mAIIPLog.setValueIndex(0);
            mAIIPLog.setSummary(mAIIPLog.getEntry());
        } else {
            mAIIPLog.setValueIndex(0);
            mAIIPLog.setSummary(R.string.feature_not_support);
            mAIIPLog.setEnabled(false);
        }

        String mVlogStatus = debuglogApi.aiIpApi().getAiDebugVLog();
        if (mVlogStatus.trim().equals("1")) {
            mModelLog.setChecked(true);
            mCompilationlLog.setChecked(true);
            mExecutionlLog.setChecked(true);
            mCpuexecLog.setChecked(true);
            mManagerLog.setChecked(true);
            mDriverLog.setChecked(true);
        } else {
            if (mVlogStatus.contains(NN_LOG_MODEL)) {
                mModelLog.setChecked(true);
            }
            if (mVlogStatus.contains(NN_LOG_COMPILATION)) {
                mCompilationlLog.setChecked(true);
            }
            if (mVlogStatus.contains(NN_LOG_EXECUTION)) {
                mExecutionlLog.setChecked(true);
            }
            if (mVlogStatus.contains(NN_LOG_CPUEXEC)) {
                mCpuexecLog.setChecked(true);
            }
            if (mVlogStatus.contains(NN_LOG_MANAGER)) {
                mManagerLog.setChecked(true);
            }
            if (mVlogStatus.contains(NN_LOG_DRIVER)) {
                mDriverLog.setChecked(true);
            }
        }
    }

    public String deleteSubString(String oriStr, String dleStr) {
        StringBuffer sb = new StringBuffer(oriStr);
        int delCount = 0;
        String mNewStr = null;

        while (true) {
            int index = sb.indexOf(dleStr);
            if (index == -1) {
                break;
            }
            sb.delete(index, index + dleStr.length());
            delCount++;
        }
        if (delCount != 0) {
            mNewStr = sb.toString();
        }
        Log.d(TAG, "deleteSubString mNewStr: " + mNewStr + " oriStr: " + oriStr);
        return mNewStr;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String key = preference.getKey();
        Log.d(TAG, "onPreferenceChange key: " + key + " newValue: " + String.valueOf(newValue));
        if (KEY_AI_IP.equals(key)) {
            String mAIStatus = debuglogApi.aiIpApi().getAiIpStatus();
            if (String.valueOf(newValue).equals("1")) {
                debuglogApi.aiIpApi().setAIImaginationNNA();
            } else if (String.valueOf(newValue).equals("2")) {
                debuglogApi.aiIpApi().setAICambriconNPU();
            } else {
                debuglogApi.aiIpApi().setAINone();
            }
            mAIIPLog.setValueIndex(Integer.parseInt(String.valueOf(newValue)));
            mAIIPLog.setSummary(mAIIPLog.getEntry());
        } else if (KEY_CPUEXEC_LOG.equals(key)) {
            String mVlogStatus = debuglogApi.aiIpApi().getAiDebugVLog();
            if (mVlogStatus.contains(NN_LOG_CPUEXEC) || mVlogStatus.trim().equals("1")) {
                String delLogCpuExecStr = deleteSubString(mVlogStatus.equals("1") ? NN_LOG : mVlogStatus, NN_LOG_CPUEXEC);
                debuglogApi.aiIpApi().setAiDebugVLog(delLogCpuExecStr);
                mCpuexecLog.setChecked(false);
            } else {
                debuglogApi.aiIpApi().setAiDebugVLog(mVlogStatus + " " + NN_LOG_CPUEXEC);
                mCpuexecLog.setChecked(true);
            }
        } else if (KEY_MODEL_LOG.equals(key)) {
            String mVlogStatus = debuglogApi.aiIpApi().getAiDebugVLog();
            if (mVlogStatus.contains(NN_LOG_MODEL) || mVlogStatus.trim().equals("1")) {
                String delLogModelStr = deleteSubString(mVlogStatus.equals("1") ? NN_LOG : mVlogStatus, NN_LOG_MODEL);
                debuglogApi.aiIpApi().setAiDebugVLog(delLogModelStr);
                mModelLog.setChecked(false);
            } else {
                debuglogApi.aiIpApi().setAiDebugVLog(mVlogStatus + " " + NN_LOG_MODEL);
                mModelLog.setChecked(true);
            }
        } else if (KEY_COMPILATION_LOG.equals(key)) {
            String mVlogStatus = debuglogApi.aiIpApi().getAiDebugVLog();
            if (mVlogStatus.contains(NN_LOG_COMPILATION) || mVlogStatus.trim().equals("1")) {
                String delLogCompStr = deleteSubString(mVlogStatus.equals("1") ? NN_LOG : mVlogStatus, NN_LOG_COMPILATION);
                debuglogApi.aiIpApi().setAiDebugVLog(delLogCompStr);
                mCompilationlLog.setChecked(false);
            } else {
                debuglogApi.aiIpApi().setAiDebugVLog(mVlogStatus + " " + NN_LOG_COMPILATION);
                mCompilationlLog.setChecked(true);
            }
        } else if (KEY_EXECUTION_LOG.equals(key)) {
            String mVlogStatus = debuglogApi.aiIpApi().getAiDebugVLog();
            if (mVlogStatus.contains(NN_LOG_EXECUTION) || mVlogStatus.trim().equals("1")) {
                String delLogExecutionStr = deleteSubString(mVlogStatus.equals("1") ? NN_LOG : mVlogStatus, NN_LOG_EXECUTION);
                debuglogApi.aiIpApi().setAiDebugVLog(delLogExecutionStr);
                mExecutionlLog.setChecked(false);
            } else {
                debuglogApi.aiIpApi().setAiDebugVLog(mVlogStatus + " " + NN_LOG_EXECUTION);
                mExecutionlLog.setChecked(true);
            }
        } else if (KEY_MANAGER_LOG.equals(key)) {
            String mVlogStatus = debuglogApi.aiIpApi().getAiDebugVLog();
            if (mVlogStatus.contains(NN_LOG_MANAGER) || mVlogStatus.trim().equals("1")) {
                String delLogManagerStr = deleteSubString(mVlogStatus.equals("1") ? NN_LOG : mVlogStatus, NN_LOG_MANAGER);
                debuglogApi.aiIpApi().setAiDebugVLog(delLogManagerStr);
                mManagerLog.setChecked(false);
            } else {
                debuglogApi.aiIpApi().setAiDebugVLog(mVlogStatus + " " + NN_LOG_MANAGER);
                mManagerLog.setChecked(true);
            }
        } else if (KEY_DRIVER_LOG.equals(key)) {
            String mVlogStatus = debuglogApi.aiIpApi().getAiDebugVLog();
            if (mVlogStatus.contains(NN_LOG_DRIVER) || mVlogStatus.trim().equals("1")) {
                String delLogDriverStr = deleteSubString(mVlogStatus.equals("1") ? NN_LOG : mVlogStatus, NN_LOG_DRIVER);
                debuglogApi.aiIpApi().setAiDebugVLog(delLogDriverStr);
                mDriverLog.setChecked(false);
            } else {
                debuglogApi.aiIpApi().setAiDebugVLog(mVlogStatus + " " + NN_LOG_DRIVER);
                mDriverLog.setChecked(true);
            }
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
