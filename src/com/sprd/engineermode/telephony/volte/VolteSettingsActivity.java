package com.sprd.engineermode.telephony.volte;

import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceCategory;
import android.preference.TwoStatePreference;
import android.preference.EditTextPreference;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.SharedPreferences;

import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;
import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.impl.nonpublic.TelephonyManagerProxy;
import com.unisoc.engineermode.core.utils.IATUtils;
import android.util.Log;
import android.widget.Toast;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.content.Context;
import android.os.Message;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.CheckBox;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import android.app.AlertDialog;
import android.view.View;
import android.os.PowerManager;
import android.widget.CompoundButton;

import com.unisoc.engineermode.core.common.Const;
import com.sprd.engineermode.R;

public class VolteSettingsActivity extends PreferenceActivity implements
        Preference.OnPreferenceChangeListener,
        Preference.OnPreferenceClickListener, OnSharedPreferenceChangeListener {

    private static final String TAG = "VolteSettingsActivity";
    private static final String KEY_CUSTOM_IMPI_SETTING = "volte_impi_impu_sub_setting";
    private static final String KEY_IP_SETTING = "volte_ip_setting";
    private static final String KEY_MUT_LENGTH = "volte_mut_length";
    private static final String KEY_SUBSCRIBE_SETTING = "volte_subscribe_setting_switch";
    private static final String KEY_SIGCOMP_SETTING = "volte_sigcomp_setting_switch";
    private static final String KEY_SIGCOMP_ALGORITHM = "volte_sigcomp_algorithm";
    private static final String KEY_EXPIRES_TIMER_SETTING = "volte_expires_time_setting";
    private static final String KEY_VOICE_CODE_WAY = "volte_voice_code_way";
    private static final String KEY_VOICE_CODE_SPEED = "volte_voice_code_speed";
    private static final String KEY_VOLTE_CODE_WAY = "volte_code_way";
    private static final String KEY_VOICE_CODE = "volte_voice_code";
    private static final String KEY_MIN_BANDWIDTH = "min_bandwidth";
    private static final String KEY_MAX_BANDWIDTH = "max_bandwidth";
    private static final String KEY_MIN_BITRATE = "min_bitrate";
    private static final String KEY_MAX_BITRATE = "max_bitrate";
    private static final String KEY_DEFAULT_BITRATE = "default_encode_bitrate";
    private static final String KEY_EVS_RATE_SET = "evs_rate_set";
    private static final String KEY_EVS_DEFAULT_BITRATE = "evs_io_default_bitrate";
    private static final String KEY_WB_RATE_SET = "wb_rate_set";
    private static final String KEY_WB_DEFAULT_BITRATE = "wb_default_bitrate";
    private static final String KEY_NB_RATE_SET = "nb_rate_set";
    private static final String KEY_NB_DEFAULT_BITRATE = "nb_default_bitrate";
    private static final String KEY_CHANNEL_AWARE_MODE = "channel_aware_mode";
    private static final String KEY_CHANNEL_AWARE_BANDWIDTH = "channel_aware_bandwidth";
    /* sharepref save key AMR_WB Speed and AMR_NB Speed @{ */
    private static final String KEY_VOICE_NB_CODE_SPEED = "volte_voice_nb_code_speed";
    private static final String KEY_VOICE_WB_CODE_SPEED = "volte_voice_nb_code_speed";
    /* }@ */
    private static final String KEY_PRECONDITION_SETTING = "volte_precondition_setting_switch";
    private static final String KEY_TQOS_TIMER_SETTING = "volte_tqos_time_setting";
    private static final String KEY_TCALL_TIMER_SETTING = "volte_tcall_time_setting";
    private static final String KEY_TREG_TIMER_SETTING = "volte_treg_time_setting";
    private static final String KEY_PQOS_TIMER_SETTING = "volte_pqos_time_setting";

    private static final String KEY_IMPI_SETTING = "impi_setting";
    private static final String KEY_IMPU_SETTING = "impu_setting";
    private static final String KEY_DOMAIN_SETTING = "domain_setting";

    private static final String KEY_PCSCF_SET = "pcscf_set";
    private static final String KEY_SESSION_TIMER = "session_timer";
    private static final String KEY_FORCE_MT_SESSION_TIMER = "force_mt_session_timer";

    private static final int MSG_SET_IMPI = 0;
    private static final int MSG_SET_IMPU = 1;
    private static final int MSG_SET_DOMAIN = 2;
    private static final int MSG_GET_IP_STATE = 3;
    private static final int MSG_SET_IP_STATE = 4;
    private static final int MSG_GET_MUT_LENGTH = 5;
    private static final int MSG_SET_MUT_LENGTH = 6;
    private static final int MSG_GET_SUBSCRIB_STATE = 7;
    private static final int MSG_SET_SUBSCRIB_STATE = 8;
    private static final int MSG_GET_SIGCOMP_STATE = 9;
    private static final int MSG_SET_SIGCOMP_STATE = 10;
    private static final int MSG_SET_SIGCOMP_INDEX = 11;
    private static final int MSG_GET_EXPIRES_TIME = 12;
    private static final int MSG_SET_EXPIRES_TIME = 13;
    private static final int MSG_GET_VOICE_CODE_WAY = 14;
    private static final int MSG_SET_VOICE_CODE_WAY = 15;
    private static final int MSG_SET_VOICE_CODE_SPEED = 16;
    private static final int MSG_GET_PRECONDITION_STATE = 17;
    private static final int MSG_SET_PRECONDITION_STATE = 18;
    private static final int MSG_GET_TQOS_TIMER = 19;
    private static final int MSG_SET_TQOS_TIMER = 20;
    private static final int MSG_GET_TCALL_TIMER = 21;
    private static final int MSG_SET_TCALL_TIMER = 22;
    private static final int MSG_GET_TREG_TIMER = 23;
    private static final int MSG_SET_TREG_TIMER = 24;
    private static final int MSG_GET_VOICE_CODE_TYPE = 27;
    private static final int MSG_SET_VOICE_CODE_TYPE = 28;
    private static final int MSG_GET_MIN_BANDWIDTH = 29;
    private static final int MSG_SET_MIN_BANDWIDTH = 30;
    private static final int MSG_GET_MAX_BANDWIDTH = 31;
    private static final int MSG_SET_MAX_BANDWIDTH = 32;
    private static final int MSG_GET_MIN_BITRATE = 33;
    private static final int MSG_SET_MIN_BITRATE = 34;
    private static final int MSG_GET_MAX_BITRATE = 35;
    private static final int MSG_SET_MAX_BITRATE = 36;
    private static final int MSG_GET_DEFAULT_BITRATE = 37;
    private static final int MSG_SET_DEFAULT_BITRATE = 38;
    private static final int MSG_GET_EVS_DEFAULT_BITRATE = 39;
    private static final int MSG_SET_EVS_DEFAULT_BITRATE = 40;
    private static final int MSG_GET_WB_DEFAULT_BITRATE = 41;
    private static final int MSG_SET_WB_DEFAULT_BITRATE = 42;
    private static final int MSG_GET_NB_DEFAULT_BITRATE = 43;
    private static final int MSG_SET_NB_DEFAULT_BITRATE = 44;
    private static final int MSG_GET_EVS_RATE_SET = 45;
    private static final int MSG_SET_EVS_RATE_SET = 46;
    private static final int MSG_GET_WB_RATE_SET = 47;
    private static final int MSG_SET_WB_RATE_SET = 48;
    private static final int MSG_GET_NB_RATE_SET = 49;
    private static final int MSG_SET_NB_RATE_SET = 50;
    private static final int MSG_GET_CHANNEL_AWARE_MODE = 51;
    private static final int MSG_SET_CHANNEL_AWARE_MODE = 52;
    private static final int MSG_GET_CHANNEL_AWARE_BANDWIDTH = 53;
    private static final int MSG_SET_CHANNEL_AWARE_BANDWIDTH = 54;
    private static final int GET_SESSION_TIMER = 55;
    private static final int SET_SESSION_TIMER = 56;
    private static final int GET_FORCE_MT_SESSION_TIMER = 57;
    private static final int SET_FORCE_MT_SESSION_TIMER = 58;
    private static final int EDITADRESS_LENGTH_MAX = 91;
    private static final int MSG_GET_PQOS_TIMER = 92;
    private static final int MSG_SET_PQOS_TIMER = 93;

    private PreferenceGroup mIMPISettingScreen;
    private ListPreference mIPSetting;
    private EditTextPreference mMUTLength;
    private TwoStatePreference mSubscribeSwitch;
    private TwoStatePreference mSigcompSwitch;
    private ListPreference mSigcompSetting;
    private EditTextPreference mExpiresTimer;
    private ListPreference mVoiceCodeWay;
    private ListPreference mVoiceCodeSpeed;
    private TwoStatePreference mPreconditionSwitch;
    private EditTextPreference mTqosTimer;
    private EditTextPreference mTcallTimer;
    private EditTextPreference mTregTimer;
    private EditTextPreference mPqosTimer;
    private EditTextPreference mSessionTimer, mForceMtSessionTimer;
    private TwoStatePreference mPcscfSwitch;
    private Preference mVolteVoiceCode, mEvsRateSet, mWbRateSet, mNbRateSet;
    private PreferenceScreen mVolteCode;
    private ListPreference mMinBandWidth, mMaxBandWidth, mMinBitRate,
            mMaxBitRate, mDefaultBitRate;
    private ListPreference mEvsDefaultBR, mWbDefaultBR, mNbDefaultBR,
            mChannelAwareMode, mChannelAwareBD;

    private AlertDialog mAlertDialog;
    private ArrayList<CheckBox> mVolteViewList;

    private int mPhoneCount = 0;
    private String mResp = null;
    private int mPhoneId = 0;
    private Context mContext = null;
    private SharedPreferences mSharePref;
    private String mVoiceWayIndex = null;
    private Handler mUiThread = new Handler();
    private VolteHandler mVolteHandler;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Const.isSupportEVS()) {
            addPreferencesFromResource(R.xml.pref_volte_setting_evs);
        } else {
            addPreferencesFromResource(R.xml.pref_volte_setting);
        }
        mPhoneCount = TelephonyManagerProxy.getPhoneCount();
        mContext = this;
        mSharePref = PreferenceManager.getDefaultSharedPreferences(this);
        mSharePref.registerOnSharedPreferenceChangeListener(this);
        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mVolteHandler = new VolteHandler(ht.getLooper());

        mIPSetting = (ListPreference) findPreference(KEY_IP_SETTING);
        mIPSetting.setOnPreferenceChangeListener(this);
        mMUTLength = (EditTextPreference) findPreference(KEY_MUT_LENGTH);
        mMUTLength.setOnPreferenceChangeListener(this);
        mSubscribeSwitch = (TwoStatePreference) findPreference(KEY_SUBSCRIBE_SETTING);
        mSubscribeSwitch.setOnPreferenceChangeListener(this);
        mSigcompSwitch = (TwoStatePreference) findPreference(KEY_SIGCOMP_SETTING);
        mSigcompSwitch.setOnPreferenceChangeListener(this);
        mSigcompSetting = (ListPreference) findPreference(KEY_SIGCOMP_ALGORITHM);
        mSigcompSetting.setOnPreferenceChangeListener(this);
        mSigcompSetting.setEnabled(false);
        mExpiresTimer = (EditTextPreference) findPreference(KEY_EXPIRES_TIMER_SETTING);
        mExpiresTimer.setOnPreferenceChangeListener(this);
        mPreconditionSwitch = (TwoStatePreference) findPreference(KEY_PRECONDITION_SETTING);
        mPreconditionSwitch.setOnPreferenceChangeListener(this);
        mTqosTimer = (EditTextPreference) findPreference(KEY_TQOS_TIMER_SETTING);
        mTqosTimer.setOnPreferenceChangeListener(this);
        mTcallTimer = (EditTextPreference) findPreference(KEY_TCALL_TIMER_SETTING);
        mTcallTimer.setOnPreferenceChangeListener(this);
        mTregTimer = (EditTextPreference) findPreference(KEY_TREG_TIMER_SETTING);
        mTregTimer.setOnPreferenceChangeListener(this);
        mPqosTimer = (EditTextPreference) findPreference(KEY_PQOS_TIMER_SETTING);
        mPqosTimer.setOnPreferenceChangeListener(this);
        mPcscfSwitch = (TwoStatePreference) findPreference(KEY_PCSCF_SET);
        mPcscfSwitch.setOnPreferenceChangeListener(this);
        mSessionTimer = (EditTextPreference) findPreference(KEY_SESSION_TIMER);
        mSessionTimer.setOnPreferenceChangeListener(this);
        mForceMtSessionTimer = (EditTextPreference) findPreference(KEY_FORCE_MT_SESSION_TIMER);
        mForceMtSessionTimer.setOnPreferenceChangeListener(this);

        if (Const.isSupportEVS()) {
            mVolteCode = (PreferenceScreen) findPreference(KEY_VOLTE_CODE_WAY);
            mVolteCode.setOnPreferenceClickListener(this);
            mVolteVoiceCode = (Preference) findPreference(KEY_VOICE_CODE);
            mVolteVoiceCode.setOnPreferenceClickListener(this);
            mMinBandWidth = (ListPreference) findPreference(KEY_MIN_BANDWIDTH);
            mMinBandWidth.setOnPreferenceChangeListener(this);
            mMaxBandWidth = (ListPreference) findPreference(KEY_MAX_BANDWIDTH);
            mMaxBandWidth.setOnPreferenceChangeListener(this);
            mMinBitRate = (ListPreference) findPreference(KEY_MIN_BITRATE);
            mMinBitRate.setOnPreferenceChangeListener(this);
            mMaxBitRate = (ListPreference) findPreference(KEY_MAX_BITRATE);
            mMaxBitRate.setOnPreferenceChangeListener(this);
            mDefaultBitRate = (ListPreference) findPreference(KEY_DEFAULT_BITRATE);
            mDefaultBitRate.setOnPreferenceChangeListener(this);
            mEvsDefaultBR = (ListPreference) findPreference(KEY_EVS_DEFAULT_BITRATE);
            mEvsDefaultBR.setOnPreferenceChangeListener(this);
            mWbDefaultBR = (ListPreference) findPreference(KEY_WB_DEFAULT_BITRATE);
            mWbDefaultBR.setOnPreferenceChangeListener(this);
            mNbDefaultBR = (ListPreference) findPreference(KEY_NB_DEFAULT_BITRATE);
            mNbDefaultBR.setOnPreferenceChangeListener(this);
            mEvsRateSet = (Preference) findPreference(KEY_EVS_RATE_SET);
            mEvsRateSet.setOnPreferenceClickListener(this);
            mWbRateSet = (Preference) findPreference(KEY_WB_RATE_SET);
            mWbRateSet.setOnPreferenceClickListener(this);
            mNbRateSet = (Preference) findPreference(KEY_NB_RATE_SET);
            mNbRateSet.setOnPreferenceClickListener(this);
            mChannelAwareMode = (ListPreference) findPreference(KEY_CHANNEL_AWARE_MODE);
            mChannelAwareMode.setOnPreferenceChangeListener(this);
            mChannelAwareBD = (ListPreference) findPreference(KEY_CHANNEL_AWARE_BANDWIDTH);
            mChannelAwareBD.setOnPreferenceChangeListener(this);
        } else {
            mVoiceCodeWay = (ListPreference) findPreference(KEY_VOICE_CODE_WAY);
            mVoiceCodeWay.setOnPreferenceChangeListener(this);
            mVoiceCodeSpeed = (ListPreference) findPreference(KEY_VOICE_CODE_SPEED);
            mVoiceCodeSpeed.setOnPreferenceChangeListener(this);
        }

        mIMPISettingScreen = (PreferenceGroup) findPreference(KEY_CUSTOM_IMPI_SETTING);
        for (int i = 0; i < mPhoneCount; i++) {
            PreferenceCategory prefCategory = new PreferenceCategory(this);
            prefCategory.setTitle("SIM" + i);
            mIMPISettingScreen.addPreference(prefCategory);
            EditTextPreference impisetting = new EditTextPreference(this);
            impisetting.setKey(KEY_IMPI_SETTING + i);
            impisetting.setTitle("IMPI Setting");
            impisetting.setDialogTitle("IMPI Setting");
            impisetting.setOnPreferenceChangeListener(this);
            if (mSharePref.getString(KEY_IMPI_SETTING + i, null) == null) {
                impisetting.setSummary(R.string.input);
            } else {
                impisetting.setSummary(mSharePref.getString(KEY_IMPI_SETTING
                        + i, null));
            }
            mIMPISettingScreen.addPreference(impisetting);

            EditTextPreference impusetting = new EditTextPreference(this);
            impusetting.setKey(KEY_IMPU_SETTING + i);
            impusetting.setTitle("IMPU Setting");
            impusetting.setOnPreferenceChangeListener(this);
            if (mSharePref.getString(KEY_IMPU_SETTING + i, null) == null) {
                impusetting.setSummary(R.string.input);
            } else {
                impusetting.setSummary(mSharePref.getString(KEY_IMPU_SETTING
                        + i, null));
            }
            mIMPISettingScreen.addPreference(impusetting);

            EditTextPreference domain = new EditTextPreference(this);
            domain.setKey(KEY_DOMAIN_SETTING + i);
            domain.setTitle("Domain Setting");
            domain.setOnPreferenceChangeListener(this);
            if (mSharePref.getString(KEY_DOMAIN_SETTING + i, null) == null) {
                domain.setSummary(R.string.input);
            } else {
                domain.setSummary(mSharePref.getString(KEY_DOMAIN_SETTING + i,
                        null));
            }
            mIMPISettingScreen.addPreference(domain);
        }

    }

    @Override
    public void onStart() {
        if (mIPSetting != null && mIPSetting.isEnabled()) {
            Message getIPState = mVolteHandler.obtainMessage(MSG_GET_IP_STATE);
            mVolteHandler.sendMessage(getIPState);
        }

        if (mMUTLength != null && mMUTLength.isEnabled()) {
            Message getMutLength = mVolteHandler
                    .obtainMessage(MSG_GET_MUT_LENGTH);
            mVolteHandler.sendMessage(getMutLength);
        }

        if (mSubscribeSwitch != null && mSubscribeSwitch.isEnabled()) {
            Message getSubscribeState = mVolteHandler
                    .obtainMessage(MSG_GET_SUBSCRIB_STATE);
            mVolteHandler.sendMessage(getSubscribeState);
        }

        if (mSigcompSwitch != null && mSigcompSwitch.isEnabled()) {
            Message getSigcompSwitch = mVolteHandler
                    .obtainMessage(MSG_GET_SIGCOMP_STATE);
            mVolteHandler.sendMessage(getSigcompSwitch);
        }

        if (mExpiresTimer != null && mExpiresTimer.isEnabled()) {
            Message getExpirestime = mVolteHandler
                    .obtainMessage(MSG_GET_EXPIRES_TIME);
            mVolteHandler.sendMessage(getExpirestime);
        }

        if (mVoiceCodeWay != null && mVoiceCodeWay.isEnabled()) {
            Message getVoiceCodeWay = mVolteHandler
                    .obtainMessage(MSG_GET_VOICE_CODE_WAY);
            mVolteHandler.sendMessage(getVoiceCodeWay);
        }

        if (mPreconditionSwitch != null && mPreconditionSwitch.isEnabled()) {
            Message getPrecondition = mVolteHandler
                    .obtainMessage(MSG_GET_PRECONDITION_STATE);
            mVolteHandler.sendMessage(getPrecondition);
        }

        if (mTqosTimer != null && mTqosTimer.isEnabled()) {
            Message getTqosTimer = mVolteHandler
                    .obtainMessage(MSG_GET_TQOS_TIMER);
            mVolteHandler.sendMessage(getTqosTimer);
        }

        if (mTcallTimer != null && mTcallTimer.isEnabled()) {
            Message getTcallTimer = mVolteHandler
                    .obtainMessage(MSG_GET_TCALL_TIMER);
            mVolteHandler.sendMessage(getTcallTimer);
        }

        if (mTregTimer != null && mTregTimer.isEnabled()) {
            Message getTregTimer = mVolteHandler
                    .obtainMessage(MSG_GET_TREG_TIMER);
            mVolteHandler.sendMessage(getTregTimer);
        }
        if (mPqosTimer != null && mPqosTimer.isEnabled()) {
            Message getPqosTimer = mVolteHandler
                    .obtainMessage(MSG_GET_PQOS_TIMER);
            mVolteHandler.sendMessage(getPqosTimer);
        }
        if (mPcscfSwitch != null) {
            String pcscfAddress = SystemPropertiesProxy.get("persist.vendor.sys.volte.pcscf");
            Log.d(TAG, "onStart pcscfAddress is: " + pcscfAddress);
            if ("".equals(pcscfAddress)) {
                mPcscfSwitch.setChecked(false);
                mPcscfSwitch.setSummary(getString(R.string.pcscf_dynamic));
            } else {
                mPcscfSwitch.setChecked(true);
                mPcscfSwitch.setSummary(getString(R.string.pcscf_static) + ": "
                        + pcscfAddress.trim());
            }
        }
        if (mMinBandWidth != null && mMinBandWidth.isEnabled()) {
            Message getMinBandWidth = mVolteHandler
                    .obtainMessage(MSG_GET_MIN_BANDWIDTH);
            mVolteHandler.sendMessage(getMinBandWidth);
        }
        if (mMaxBandWidth != null && mMaxBandWidth.isEnabled()) {
            Message getMaxBandWidth = mVolteHandler
                    .obtainMessage(MSG_GET_MAX_BANDWIDTH);
            mVolteHandler.sendMessage(getMaxBandWidth);
        }
        if (mMinBitRate != null && mMinBitRate.isEnabled()) {
            Message getMinBitRate = mVolteHandler
                    .obtainMessage(MSG_GET_MIN_BITRATE);
            mVolteHandler.sendMessage(getMinBitRate);
        }
        if (mMaxBitRate != null && mMaxBitRate.isEnabled()) {
            Message getMaxBitRate = mVolteHandler
                    .obtainMessage(MSG_GET_MAX_BITRATE);
            mVolteHandler.sendMessage(getMaxBitRate);
        }
        if (mDefaultBitRate != null && mDefaultBitRate.isEnabled()) {
            Message getDefaultBitRate = mVolteHandler
                    .obtainMessage(MSG_GET_DEFAULT_BITRATE);
            mVolteHandler.sendMessage(getDefaultBitRate);
        }
        if (mEvsDefaultBR != null && mEvsDefaultBR.isEnabled()) {
            Message getEvsDefaultBr = mVolteHandler
                    .obtainMessage(MSG_GET_EVS_DEFAULT_BITRATE);
            mVolteHandler.sendMessage(getEvsDefaultBr);
        }
        if (mWbDefaultBR != null && mWbDefaultBR.isEnabled()) {
            Message getWbDefaultBr = mVolteHandler
                    .obtainMessage(MSG_GET_WB_DEFAULT_BITRATE);
            mVolteHandler.sendMessage(getWbDefaultBr);
        }
        if (mNbDefaultBR != null && mNbDefaultBR.isEnabled()) {
            Message getNbDefaultBr = mVolteHandler
                    .obtainMessage(MSG_GET_NB_DEFAULT_BITRATE);
            mVolteHandler.sendMessage(getNbDefaultBr);
        }
        if (mChannelAwareMode != null && mChannelAwareMode.isEnabled()) {
            Message getAwareMode = mVolteHandler
                    .obtainMessage(MSG_GET_CHANNEL_AWARE_MODE);
            mVolteHandler.sendMessage(getAwareMode);
        }
        if (mChannelAwareBD != null && mChannelAwareBD.isEnabled()) {
            Message getAwareBd = mVolteHandler
                    .obtainMessage(MSG_GET_CHANNEL_AWARE_BANDWIDTH);
            mVolteHandler.sendMessage(getAwareBd);
        }
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSessionTimer != null) {
            Message getTimer = mVolteHandler.obtainMessage(GET_SESSION_TIMER);
            mVolteHandler.sendMessage(getTimer);
        }

        if (mForceMtSessionTimer != null) {
            Message getTimer = mVolteHandler
                    .obtainMessage(GET_FORCE_MT_SESSION_TIMER);
            mVolteHandler.sendMessage(getTimer);
        }
    }

    @Override
    public void onDestroy() {
        if (mVolteHandler != null) {
            mVolteHandler.getLooper().quit();
            Log.d(TAG, "HandlerThread has quit");
        }
        super.onDestroy();
    }

    private void editDialog(Context context) {
        final EditText inputServer = new EditText(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.pcscf_static_input));
        builder.setView(inputServer);
        builder.setCancelable(false);
        builder.setNegativeButton(getString(R.string.alertdialog_cancel),
            (dialog, which) -> mPcscfSwitch.setChecked(false));
        builder.setPositiveButton(getString(R.string.alertdialog_ok),
            (dialog, which) -> {
                String editAdress = inputServer.getText().toString();
                Log.d(TAG, "Edit input: " + editAdress);
                if (editAdress.length() != 0) {
                    if (editAdress.getBytes(StandardCharsets.UTF_8).length > EDITADRESS_LENGTH_MAX) {
                        Toast.makeText(
                                mContext,
                                "The length of the input value can not exceed 91",
                                Toast.LENGTH_SHORT).show();
                        mPcscfSwitch.setChecked(false);
                        return;
                    }
                    SystemPropertiesProxy.set("persist.vendor.sys.volte.pcscf",
                            editAdress);
                    PowerManager pm = (PowerManager) VolteSettingsActivity.this
                            .getSystemService(Context.POWER_SERVICE);
                    pm.reboot("pcscfset");
                } else {
                    mPcscfSwitch.setChecked(false);
                }
            });
        builder.show();
    }

    @Override
    public boolean onPreferenceChange(Preference pref, Object newValue) {
        String prefkey = pref.getKey();
        if (prefkey.equals(KEY_IP_SETTING)) {
            Message setIPState = mVolteHandler.obtainMessage(MSG_SET_IP_STATE,
                    newValue);
            mVolteHandler.sendMessage(setIPState);
            return false;
        }
        if (prefkey.equals(KEY_MUT_LENGTH)) {
            Message setMutLength = mVolteHandler.obtainMessage(
                    MSG_SET_MUT_LENGTH, newValue);
            mVolteHandler.sendMessage(setMutLength);
            return false;
        }
        if (prefkey.equals(KEY_SUBSCRIBE_SETTING)) {
            if (mSubscribeSwitch.isChecked()) {
                Message setSubscribeClose = mVolteHandler.obtainMessage(
                        MSG_SET_SUBSCRIB_STATE, "0");
                mVolteHandler.sendMessage(setSubscribeClose);
            } else {
                Message setSubscribeOpen = mVolteHandler.obtainMessage(
                        MSG_SET_SUBSCRIB_STATE, "1");
                mVolteHandler.sendMessage(setSubscribeOpen);
            }
            return false;
        }
        if (prefkey.equals(KEY_SIGCOMP_SETTING)) {
            if (mSigcompSwitch.isChecked()) {
                Message setSigcompClose = mVolteHandler.obtainMessage(
                        MSG_SET_SIGCOMP_STATE, "0");
                mVolteHandler.sendMessage(setSigcompClose);
            } else {
                Message setSigcompOpen = mVolteHandler.obtainMessage(
                        MSG_SET_SIGCOMP_STATE, "1");
                mVolteHandler.sendMessage(setSigcompOpen);
            }
            return false;
        }
        if (prefkey.equals(KEY_SIGCOMP_ALGORITHM)) {
            Message setSigcomp = mVolteHandler.obtainMessage(
                    MSG_SET_SIGCOMP_INDEX, newValue);
            mVolteHandler.sendMessage(setSigcomp);
            return false;
        }
        if (prefkey.equals(KEY_EXPIRES_TIMER_SETTING)) {
            Message setExpirestime = mVolteHandler.obtainMessage(
                    MSG_SET_EXPIRES_TIME, newValue);
            mVolteHandler.sendMessage(setExpirestime);
            return false;
        }
        if (prefkey.equals(KEY_VOICE_CODE_WAY)) {
            Message setCodeWay = mVolteHandler.obtainMessage(
                    MSG_SET_VOICE_CODE_WAY, newValue);
            mVolteHandler.sendMessage(setCodeWay);
            return false;
        }
        if (prefkey.equals(KEY_VOICE_CODE_SPEED)) {
            Message setCodeSpeed = mVolteHandler.obtainMessage(
                    MSG_SET_VOICE_CODE_SPEED, newValue);
            mVolteHandler.sendMessage(setCodeSpeed);
            return false;
        }
        if (prefkey.equals(KEY_PRECONDITION_SETTING)) {
            if (mPreconditionSwitch.isChecked()) {
                Message setPreClose = mVolteHandler.obtainMessage(
                        MSG_SET_PRECONDITION_STATE, "0");
                mVolteHandler.sendMessage(setPreClose);
            } else {
                Message setPreOpen = mVolteHandler.obtainMessage(
                        MSG_SET_PRECONDITION_STATE, "1");
                mVolteHandler.sendMessage(setPreOpen);
            }
            return false;
        }
        if (prefkey.equals(KEY_TQOS_TIMER_SETTING)) {
            Message setTqos = mVolteHandler.obtainMessage(MSG_SET_TQOS_TIMER,
                    newValue);
            mVolteHandler.sendMessage(setTqos);
            return false;
        }
        if (prefkey.equals(KEY_TCALL_TIMER_SETTING)) {
            Message setTcall = mVolteHandler.obtainMessage(MSG_SET_TCALL_TIMER,
                    newValue);
            mVolteHandler.sendMessage(setTcall);
            return false;
        }
        if (prefkey.equals(KEY_TREG_TIMER_SETTING)) {
            Message setTreg = mVolteHandler.obtainMessage(MSG_SET_TREG_TIMER,
                    newValue);
            mVolteHandler.sendMessage(setTreg);
            return false;
        }
        if (prefkey.equals(KEY_PQOS_TIMER_SETTING)) {
            Message setPqos = mVolteHandler.obtainMessage(MSG_SET_PQOS_TIMER,
                    newValue);
            mVolteHandler.sendMessage(setPqos);
            return false;
        }
        if (pref == mSessionTimer) {
            if ("".equals(newValue.toString())) {
                Toast.makeText(mContext, "warning:empty value",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
            Message m = mVolteHandler
                    .obtainMessage(SET_SESSION_TIMER, newValue);
            mVolteHandler.sendMessage(m);
        }
        if (pref == mForceMtSessionTimer) {
            if ("".equals(newValue.toString())) {
                Toast.makeText(mContext, "warning:empty value",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
            Message m = mVolteHandler.obtainMessage(SET_FORCE_MT_SESSION_TIMER,
                    newValue);
            mVolteHandler.sendMessage(m);
        }
        for (int i = 0; i < mPhoneCount; i++) {
            if (prefkey.equals(KEY_IMPI_SETTING + i)) {
                Message setIMPI = mVolteHandler.obtainMessage(MSG_SET_IMPI, i,
                        0, newValue);
                mVolteHandler.sendMessage(setIMPI);
                return false;
            } else if (prefkey.equals(KEY_IMPU_SETTING + i)) {
                Message setIMPU = mVolteHandler.obtainMessage(MSG_SET_IMPU, i,
                        0, newValue);
                mVolteHandler.sendMessage(setIMPU);
                return false;
            } else if (prefkey.equals(KEY_DOMAIN_SETTING + i)) {
                Message setDomain = mVolteHandler.obtainMessage(MSG_SET_DOMAIN,
                        i, 0, newValue);
                mVolteHandler.sendMessage(setDomain);
                return false;
            }
        }

        if (prefkey.equals(KEY_PCSCF_SET)) {
            AlertDialog alertDialog = new AlertDialog.Builder(
                    VolteSettingsActivity.this)
                    .setTitle("P-CSCF")
                    .setCancelable(false)
                    .setMessage(getString(R.string.mode_switch_waring))
                    .setPositiveButton(getString(R.string.alertdialog_ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    String currentSatus = SystemPropertiesProxy.get("persist.vendor.sys.volte.pcscf");
                                    if (!"".equals(currentSatus)) {
                                        SystemPropertiesProxy.set(
                                                "persist.vendor.sys.volte.pcscf", "");
                                        PowerManager pm = (PowerManager) VolteSettingsActivity.this
                                                .getSystemService(Context.POWER_SERVICE);
                                        pm.reboot("pcscfset");
                                    } else {
                                        editDialog(VolteSettingsActivity.this);
                                    }
                                }
                            })
                    .setNegativeButton(R.string.alertdialog_cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    mPcscfSwitch.setChecked(!("".equals(SystemPropertiesProxy.get("persist.vendor.sys.volte.pcscf"))));
                                }
                            }).create();
            alertDialog.show();
            return true;
        }
        if (prefkey.equals(KEY_MIN_BANDWIDTH)) {
            Message setMinBandWidth = mVolteHandler.obtainMessage(
                    MSG_SET_MIN_BANDWIDTH, newValue);
            mVolteHandler.sendMessage(setMinBandWidth);
            return false;
        }
        if (prefkey.equals(KEY_MAX_BANDWIDTH)) {
            Message setMaxBandWidth = mVolteHandler.obtainMessage(
                    MSG_SET_MAX_BANDWIDTH, newValue);
            mVolteHandler.sendMessage(setMaxBandWidth);
            return false;
        }
        if (prefkey.equals(KEY_MIN_BITRATE)) {
            Message setMinBitRate = mVolteHandler.obtainMessage(
                    MSG_SET_MIN_BITRATE, newValue);
            mVolteHandler.sendMessage(setMinBitRate);
            return false;
        }
        if (prefkey.equals(KEY_MAX_BITRATE)) {
            Message setMaxBitRate = mVolteHandler.obtainMessage(
                    MSG_SET_MAX_BITRATE, newValue);
            mVolteHandler.sendMessage(setMaxBitRate);
            return false;
        }
        if (prefkey.equals(KEY_DEFAULT_BITRATE)) {
            Message setDefaultBitRate = mVolteHandler.obtainMessage(
                    MSG_SET_DEFAULT_BITRATE, newValue);
            mVolteHandler.sendMessage(setDefaultBitRate);
            return false;
        }
        if (prefkey.equals(KEY_EVS_DEFAULT_BITRATE)) {
            Message setEvsDefaultBr = mVolteHandler.obtainMessage(
                    MSG_SET_EVS_DEFAULT_BITRATE, newValue);
            mVolteHandler.sendMessage(setEvsDefaultBr);
            return false;
        }
        if (prefkey.equals(KEY_WB_DEFAULT_BITRATE)) {
            Message setWbDefaultBr = mVolteHandler.obtainMessage(
                    MSG_SET_WB_DEFAULT_BITRATE, newValue);
            mVolteHandler.sendMessage(setWbDefaultBr);
            return false;
        }
        if (prefkey.equals(KEY_NB_DEFAULT_BITRATE)) {
            Message setNbDefaultBr = mVolteHandler.obtainMessage(
                    MSG_SET_NB_DEFAULT_BITRATE, newValue);
            mVolteHandler.sendMessage(setNbDefaultBr);
            return false;
        }
        if (prefkey.equals(KEY_CHANNEL_AWARE_MODE)) {
            Message setAwreMode = mVolteHandler.obtainMessage(
                    MSG_SET_CHANNEL_AWARE_MODE, newValue);
            mVolteHandler.sendMessage(setAwreMode);
            return false;
        }
        if (prefkey.equals(KEY_CHANNEL_AWARE_BANDWIDTH)) {
            Message setAwreBandWidth = mVolteHandler.obtainMessage(
                    MSG_SET_CHANNEL_AWARE_BANDWIDTH, newValue);
            mVolteHandler.sendMessage(setAwreBandWidth);
            return false;
        }
        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference pref) {
        if (pref == mVolteCode) {
            Message getVoiceCode = mVolteHandler
                    .obtainMessage(MSG_GET_VOICE_CODE_TYPE);
            mVolteHandler.sendMessage(getVoiceCode);
            return false;
        }
        if (pref == mVolteVoiceCode) {
            Message setVoiceCode = mVolteHandler
                    .obtainMessage(MSG_SET_VOICE_CODE_TYPE);
            mVolteHandler.sendMessage(setVoiceCode);
            return false;
        }
        if (pref == mEvsRateSet) {
            Message setEvsRateSet = mVolteHandler
                    .obtainMessage(MSG_SET_EVS_RATE_SET);
            mVolteHandler.sendMessage(setEvsRateSet);
            return false;
        }
        if (pref == mWbRateSet) {
            Message setWbRateSet = mVolteHandler
                    .obtainMessage(MSG_SET_WB_RATE_SET);
            mVolteHandler.sendMessage(setWbRateSet);
            return false;
        }
        if (pref == mNbRateSet) {
            Message setNbRateSet = mVolteHandler
                    .obtainMessage(MSG_SET_NB_RATE_SET);
            mVolteHandler.sendMessage(setNbRateSet);
            return false;
        }
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
    }

    class VolteHandler extends Handler {

        public VolteHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_SET_IMPI:
                mPhoneId = msg.arg1;
                final String impiValue = (String) msg.obj;
                mResp = sendAt(engconstents.ENG_VOLTE_IMPI + impiValue,
                        "atchannel" + mPhoneId);
                if (mResp.contains("OK")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mIMPISettingScreen.findPreference(
                                    KEY_IMPI_SETTING + mPhoneId).setSummary(
                                    impiValue);
                            Editor editor = mSharePref.edit();
                            editor.putString(KEY_IMPI_SETTING + mPhoneId,
                                    impiValue);
                            editor.commit();
                            Toast.makeText(mContext, "Success",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                }
                break;
            case MSG_SET_IMPU:
                mPhoneId = msg.arg1;
                final String impuValue = (String) msg.obj;
                mResp = sendAt(engconstents.ENG_VOLTE_IMPU + impuValue,
                        "atchannel" + mPhoneId);
                if (mResp.contains("OK")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mIMPISettingScreen.findPreference(
                                    KEY_IMPU_SETTING + mPhoneId).setSummary(
                                    impuValue);
                            Editor editor = mSharePref.edit();
                            editor.putString(KEY_IMPU_SETTING + mPhoneId,
                                    impuValue);
                            editor.commit();
                            Toast.makeText(mContext, "Success",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                }
                break;
            case MSG_SET_DOMAIN:
                mPhoneId = msg.arg1;
                final String domainValue = (String) msg.obj;
                mResp = sendAt(engconstents.ENG_VOLTE_DOMAIN + domainValue,
                        "atchannel" + mPhoneId);
                if (mResp.contains("OK")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mIMPISettingScreen.findPreference(
                                    KEY_DOMAIN_SETTING + mPhoneId).setSummary(
                                    domainValue);
                            Editor editor = mSharePref.edit();
                            editor.putString(KEY_DOMAIN_SETTING + mPhoneId,
                                    domainValue);
                            editor.commit();
                            Toast.makeText(mContext, "Success",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                }
                break;
            case MSG_GET_IP_STATE:
                mResp = sendAt(engconstents.ENG_VOLTE_IP_SETTING + "0",
                        "atchannel0");
                final String stateValue = anayResult(MSG_GET_IP_STATE, mResp);
                if (stateValue.contains("FAILED")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mIPSetting.setEnabled(false);
                            mIPSetting.setSummary(R.string.feature_abnormal);
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            int val = Integer.valueOf(stateValue).intValue();
                            if (val > 0) {
                                val --;
                            }
                            mIPSetting.setValueIndex(val);
                            mIPSetting.setSummary(mIPSetting.getEntry());
                        }
                    });
                }
                break;
            case MSG_SET_IP_STATE:
                String setIpState = (String) msg.obj;
                final int setIpStateValue = Integer.valueOf(setIpState)
                        .intValue() + 1;
                mResp = sendAt(engconstents.ENG_VOLTE_IP_SETTING + "1,"
                        + setIpStateValue, "atchannel0");
                if (mResp.contains("OK")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mIPSetting.setValueIndex(setIpStateValue - 1);
                            mIPSetting.setSummary(mIPSetting.getEntry());
                        }
                    });
                } else {
                    Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                }
                break;
            case MSG_GET_MUT_LENGTH:
                mResp = sendAt(engconstents.ENG_VOLTE_MUT_SETTING + "0",
                        "atchannel0");
                final String lengthValue = anayResult(MSG_GET_MUT_LENGTH, mResp);
                if (lengthValue.contains("FAILED")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mMUTLength.setEnabled(false);
                            mMUTLength.setSummary(R.string.feature_abnormal);
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mMUTLength.setSummary(lengthValue);
                            Editor editor = mSharePref.edit();
                            editor.putString(KEY_MUT_LENGTH, lengthValue);
                            editor.commit();
                        }
                    });
                }
                break;
            case MSG_SET_MUT_LENGTH:
                final String setMutLength = (String) msg.obj;
                mResp = sendAt(engconstents.ENG_VOLTE_MUT_SETTING + "1,"
                        + setMutLength, "atchannel0");
                if (mResp.contains("OK")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            Editor editor = mSharePref.edit();
                            editor.putString(KEY_MUT_LENGTH, setMutLength);
                            editor.commit();
                            mMUTLength.setSummary(setMutLength);
                        }
                    });
                } else {
                    Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                }
                break;
            case MSG_GET_SUBSCRIB_STATE:
                mResp = sendAt(engconstents.ENG_VOLTE_SUBSCRIB_SETTING + "0",
                        "atchannel0");
                final String getSubscrib = anayResult(MSG_GET_SUBSCRIB_STATE,
                        mResp);
                if (getSubscrib.contains("FAILED")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mSubscribeSwitch.setEnabled(false);
                            mSubscribeSwitch
                                    .setSummary(R.string.feature_abnormal);
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mSubscribeSwitch.setChecked(getSubscrib
                                    .contains("1"));
                        }
                    });
                }
                break;
            case MSG_SET_SUBSCRIB_STATE:
                final String setSubscrib = (String) msg.obj;
                mResp = sendAt(engconstents.ENG_VOLTE_SUBSCRIB_SETTING + "1,"
                        + setSubscrib, "atchannel0");
                if (mResp.contains("OK")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mSubscribeSwitch.setChecked(setSubscrib
                                    .contains("1"));
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mSubscribeSwitch.setChecked(!setSubscrib
                                    .contains("1"));
                        }
                    });
                    Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                }
                break;
            case MSG_GET_SIGCOMP_STATE:
                mResp = sendAt(engconstents.ENG_VOLTE_SIGCOMP_SETTING + "0",
                        "atchannel0");
                final String getSigcomp = anayResult(MSG_GET_SIGCOMP_STATE,
                        mResp);
                if (getSigcomp.contains("FAILED")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mSigcompSwitch.setEnabled(false);
                            mSigcompSwitch
                                    .setSummary(R.string.feature_abnormal);
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            boolean open = getSigcomp.split("\\,")[0]
                                    .contains("1");
                            mSigcompSwitch.setChecked(open);
                            if (open) {
                                mSigcompSetting.setEnabled(true);
                                Editor editor = mSharePref.edit();
                                editor.putString(KEY_SIGCOMP_ALGORITHM,
                                        getSigcomp.split("\\,")[1]);
                                editor.commit();
                                mSigcompSetting.setValueIndex(Integer.valueOf(
                                        getSigcomp.split("\\,")[1].trim())
                                        .intValue());
                                mSigcompSetting.setSummary(mSigcompSetting
                                        .getEntry());
                            } else {
                                mSigcompSetting.setEnabled(false);
                            }
                        }
                    });
                }
                break;
            case MSG_SET_SIGCOMP_STATE:
                final String setSigcomp = (String) msg.obj;
                mResp = sendAt(engconstents.ENG_VOLTE_SIGCOMP_SETTING + "1,"
                        + setSigcomp, "atchannel0");
                if (mResp.contains("OK")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mSigcompSwitch.setChecked(setSigcomp.contains("1"));
                            mSigcompSetting.setEnabled(true);
                            if (setSigcomp.contains("0")) {
                                mSigcompSetting.setEnabled(false);
                            }
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mSigcompSwitch.setChecked(!setSigcomp.contains("1"));
                        }
                    });
                    Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                }
                break;
            case MSG_SET_SIGCOMP_INDEX:
                final String setSigcompIndex = (String) msg.obj;
                mResp = sendAt(engconstents.ENG_VOLTE_SIGCOMP_SETTING + "1,1,"
                        + setSigcompIndex, "atchannel0");
                if (mResp.contains("OK")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            Editor editor = mSharePref.edit();
                            editor.putString(KEY_SIGCOMP_ALGORITHM,
                                    setSigcompIndex);
                            editor.commit();
                            mSigcompSetting.setValueIndex(Integer.valueOf(
                                    setSigcompIndex).intValue());
                            mSigcompSetting.setSummary(mSigcompSetting
                                    .getEntry());
                        }
                    });
                } else {
                    Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                }
                break;
            case MSG_GET_EXPIRES_TIME:
                mResp = sendAt(engconstents.ENG_EXPIERSTIMER_SETTING + "0",
                        "atchannel0");
                final String getExpiresTime = anayResult(MSG_GET_EXPIRES_TIME,
                        mResp);
                if (getExpiresTime.contains("FAILED")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mExpiresTimer.setEnabled(false);
                            mExpiresTimer.setSummary(R.string.feature_abnormal);
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mExpiresTimer.setEnabled(true);
                            mExpiresTimer.setSummary(getExpiresTime);
                        }
                    });
                }
                break;
            case MSG_SET_EXPIRES_TIME:
                final String setExpires = (String) msg.obj;
                mResp = sendAt(engconstents.ENG_EXPIERSTIMER_SETTING + "1,"
                        + setExpires, "atchannel0");
                if (mResp.contains("OK")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            Editor editor = mSharePref.edit();
                            editor.putString(KEY_EXPIRES_TIMER_SETTING,
                                    setExpires);
                            editor.commit();
                            mExpiresTimer.setSummary(setExpires);
                        }
                    });
                } else {
                    Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                }
                break;

            case MSG_GET_VOICE_CODE_WAY:
                mResp = sendAt(engconstents.ENG_VOICE_CODE_SETTING + "0",
                        "atchannel0");
                final String getVoiceCodeWay = anayResult(
                        MSG_GET_VOICE_CODE_WAY, mResp);
                if (mResp != null && mResp.contains(IATUtils.AT_OK)) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            Editor editor = mSharePref.edit();
                            editor.putString(KEY_VOICE_CODE_WAY,
                                    getVoiceCodeWay.split("\\,")[1]);
                            editor.commit();
                            if (getVoiceCodeWay.split("\\,")[1].contains("1")) {
                                mVoiceCodeWay.setValueIndex(1);
                                mVoiceCodeWay.setSummary(mVoiceCodeWay
                                        .getEntry());
                                mVoiceCodeSpeed
                                        .setEntries(R.array.volte_voice_wb_code_speed);
                                mVoiceCodeSpeed
                                        .setEntryValues(R.array.volte_voice_wb_code_speed_value);
                                mVoiceCodeSpeed.setEnabled(true);
                                int setWbValueIndex = Integer.valueOf(
                                        getVoiceCodeWay.split("\\,")[2].trim())
                                        .intValue();
                                if (setWbValueIndex == 254) {
                                    mVoiceCodeSpeed.setValueIndex(9);
                                } else {
                                    mVoiceCodeSpeed
                                            .setValueIndex(setWbValueIndex);
                                }
                                mVoiceCodeSpeed.setSummary(mVoiceCodeSpeed
                                        .getEntry());
                                editor.putString(KEY_VOICE_WB_CODE_SPEED,
                                        getVoiceCodeWay.split("\\,")[2]);
                                editor.commit();

                            } else if (getVoiceCodeWay.split("\\,")[1]
                                    .contains("0")) {
                                mVoiceCodeWay.setValueIndex(0);
                                mVoiceCodeWay.setSummary(mVoiceCodeWay
                                        .getEntry());
                                mVoiceCodeSpeed
                                        .setEntries(R.array.volte_voice_nb_code_speed);
                                mVoiceCodeSpeed
                                        .setEntryValues(R.array.volte_voice_nb_code_speed_value);
                                mVoiceCodeSpeed.setEnabled(true);
                                int setNbValueIndex = Integer.valueOf(
                                        getVoiceCodeWay.split("\\,")[2].trim())
                                        .intValue();
                                if (setNbValueIndex == 254) {
                                    mVoiceCodeSpeed.setValueIndex(8);
                                } else {
                                    mVoiceCodeSpeed
                                            .setValueIndex(setNbValueIndex);
                                }
                                mVoiceCodeSpeed.setSummary(mVoiceCodeSpeed
                                        .getEntry());
                                editor.putString(KEY_VOICE_NB_CODE_SPEED,
                                        getVoiceCodeWay.split("\\,")[2]);
                                editor.commit();
                            } else if (getVoiceCodeWay.split("\\,")[1]
                                    .contains("3")) {
                                mVoiceCodeWay.setValueIndex(2);
                                mVoiceCodeWay.setSummary(mVoiceCodeWay
                                        .getEntry());
                                mVoiceCodeSpeed.setSummary(mVoiceCodeSpeed
                                        .getEntry());
                                mVoiceCodeSpeed.setEnabled(false);
                            }
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mVoiceCodeWay.setEnabled(false);
                            mVoiceCodeWay.setSummary(R.string.feature_abnormal);
                            mVoiceCodeSpeed.setEnabled(false);
                            mVoiceCodeSpeed
                                    .setSummary(R.string.feature_abnormal);
                        }
                    });
                }
                break;
            case MSG_SET_VOICE_CODE_WAY:
                final String setVoiceCodeWay = (String) msg.obj;
                if (setVoiceCodeWay.equals("0")) {
                    mResp = sendAt(engconstents.ENG_VOICE_CODE_SETTING + "1,"
                            + 0 + "," + 254, "atchannel0");
                } else if (setVoiceCodeWay.equals("1")) {
                    mResp = sendAt(engconstents.ENG_VOICE_CODE_SETTING + "1,"
                            + 1 + "," + 254, "atchannel0");
                } else if (setVoiceCodeWay.equals("2")) {
                    mResp = sendAt(engconstents.ENG_VOICE_CODE_SETTING + "1,"
                            + 3, "atchannel0");
                }
                if (!mResp.contains("OK")) {
                    Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                }
                Message getVoiceCodeWays = mVolteHandler
                        .obtainMessage(MSG_GET_VOICE_CODE_WAY);
                mVolteHandler.sendMessage(getVoiceCodeWays);
                break;
            case MSG_SET_VOICE_CODE_SPEED:
                final String setVoiceCodeSpeed = (String) msg.obj;
                String setVoiceSpeed = null;
                Log.d(TAG, "set Voice CodeSpeed: " + setVoiceCodeSpeed);
                mVoiceWayIndex = mSharePref.getString(KEY_VOICE_CODE_WAY, "0");
                if (("0".equals(mVoiceWayIndex) && "8"
                        .equals(setVoiceCodeSpeed))
                        || ("1".equals(mVoiceWayIndex) && "9"
                                .equals(setVoiceCodeSpeed))) {
                    setVoiceSpeed = "254";

                } else {
                    setVoiceSpeed = setVoiceCodeSpeed;
                }
                mResp = sendAt(engconstents.ENG_VOICE_CODE_SETTING + "1,"
                        + mVoiceWayIndex + "," + setVoiceSpeed, "atchannel0");
                if (mResp.contains("OK")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mVoiceCodeSpeed.setValueIndex(Integer.valueOf(
                                    setVoiceCodeSpeed).intValue());
                            mVoiceCodeSpeed.setSummary(mVoiceCodeSpeed
                                    .getEntry());
                        }
                    });
                    Editor editor = mSharePref.edit();
                    if (mVoiceWayIndex.contains("1")) {
                        editor.putString(KEY_VOICE_WB_CODE_SPEED, setVoiceSpeed);
                    } else {
                        editor.putString(KEY_VOICE_NB_CODE_SPEED, setVoiceSpeed);
                    }
                    editor.commit();
                } else {
                    Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                }
                break;
            case MSG_GET_VOICE_CODE_TYPE:
                mResp = sendAt(engconstents.ENG_VOICE_CODE_SETTING + "0,1",
                        "atchannel0");
                String getVoiceCodeType = anayResult(MSG_GET_VOICE_CODE_TYPE,
                        mResp);
                if (mResp != null && mResp.contains(IATUtils.AT_OK)) {
                    int value = Integer.parseInt(getVoiceCodeType);

                    String[] arr = mContext.getResources().getStringArray(R.array.volte_voice_code);

                    final String summary = IntStream.range(0, arr.length)
                        .filter(i -> (value & (1 << i)) != 0 )
                        .mapToObj( i -> arr[i]).collect(Collectors.joining("/"));
                    mUiThread.post(() -> mVolteVoiceCode.setSummary(summary));
                } else {
                    mUiThread.post(() -> {
                        mVolteVoiceCode.setEnabled(false);
                        mVolteVoiceCode.setSummary(R.string.feature_abnormal);
                    });
                }
                Message msgEvsRateSet = mVolteHandler
                        .obtainMessage(MSG_GET_EVS_RATE_SET);
                mVolteHandler.sendMessage(msgEvsRateSet);
                break;
            case MSG_SET_VOICE_CODE_TYPE:
                View view = LayoutInflater.from(mContext).inflate(
                        R.layout.volte_voice_code, null);
                LinearLayout line = (LinearLayout) view
                        .findViewById(R.id.linearlayout_main);
                if (mVolteViewList == null) {
                    mVolteViewList = new ArrayList<CheckBox>();
                } else {
                    mVolteViewList.clear();
                }
                for (int i = 0; i < 3; i++) {
                    CheckBox checkbox = new CheckBox(mContext);
                    checkbox.setText(mContext.getResources().getStringArray(
                            R.array.volte_voice_code)[i]);
                    line.addView(checkbox);
                    mVolteViewList.add(checkbox);
                }

                mResp = sendAt(engconstents.ENG_VOICE_CODE_SETTING + "0,1",
                        "atchannel0");
                getVoiceCodeType = anayResult(MSG_GET_VOICE_CODE_TYPE, mResp);
                if (mResp != null && mResp.contains(IATUtils.AT_OK)) {
                    final int voiceCodeTypeValue = Integer
                            .valueOf(getVoiceCodeType);
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < mVolteViewList.size(); i++) {
                                if ((voiceCodeTypeValue & (1 << i)) != 0) {
                                    mVolteViewList.get(i).setChecked(true);
                                } else {
                                    mVolteViewList.get(i).setChecked(false);
                                }
                            }
                        }
                    });
                }
                popDialog(view, MSG_SET_VOICE_CODE_TYPE);
                break;
            case MSG_GET_MIN_BANDWIDTH:
                String mResult_min = sendAt(engconstents.ENG_VOICE_CODE_SETTING
                        + "0,2", "atchannel0");
                final String minBandWidthValue = anayResult(
                        MSG_GET_MIN_BANDWIDTH, mResult_min);
                if (minBandWidthValue.contains("FAILED")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mMinBandWidth.setEnabled(false);
                            mMinBandWidth.setSummary(R.string.feature_abnormal);
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mMinBandWidth.setValueIndex(Integer.valueOf(
                                    minBandWidthValue).intValue());
                            mMinBandWidth.setSummary(mMinBandWidth.getEntry());
                        }
                    });
                }
                break;
            case MSG_SET_MIN_BANDWIDTH:
                String setMinBandWidth = (String) msg.obj;
                final int setMinBandWidthValue = Integer.valueOf(
                        setMinBandWidth).intValue();
                mResp = sendAt(engconstents.ENG_VOICE_CODE_SETTING + "1,2,"
                        + setMinBandWidthValue, "atchannel0");
                if (mResp.contains("OK")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mMinBandWidth.setValueIndex(setMinBandWidthValue);
                            mMinBandWidth.setSummary(mMinBandWidth.getEntry());
                            Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                }
                break;
            case MSG_GET_MAX_BANDWIDTH:
                String mResult_max = sendAt(engconstents.ENG_VOICE_CODE_SETTING
                        + "0,3", "atchannel0");
                final String maxBandWidthValue = anayResult(
                        MSG_GET_MAX_BANDWIDTH, mResult_max);
                if (maxBandWidthValue.contains("FAILED")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mMaxBandWidth.setEnabled(false);
                            mMaxBandWidth.setSummary(R.string.feature_abnormal);
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mMaxBandWidth.setValueIndex(Integer.valueOf(
                                    maxBandWidthValue).intValue());
                            mMaxBandWidth.setSummary(mMaxBandWidth.getEntry());
                        }
                    });
                }
                break;
            case MSG_SET_MAX_BANDWIDTH:
                String setMaxBandWidth = (String) msg.obj;
                final int setMaxBandWidthValue = Integer.valueOf(
                        setMaxBandWidth).intValue();
                mResp = sendAt(engconstents.ENG_VOICE_CODE_SETTING + "1,3,"
                        + setMaxBandWidthValue, "atchannel0");
                if (mResp.contains("OK")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mMaxBandWidth.setValueIndex(setMaxBandWidthValue);
                            mMaxBandWidth.setSummary(mMaxBandWidth.getEntry());
                            Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                }
                break;
            case MSG_GET_MIN_BITRATE:
                mResp = sendAt(engconstents.ENG_VOICE_CODE_SETTING + "0,4",
                        "atchannel0");
                final String minBitRateValue = anayResult(MSG_GET_MIN_BITRATE,
                        mResp);
                if (minBitRateValue.contains("FAILED")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mMinBitRate.setEnabled(false);
                            mMinBitRate.setSummary(R.string.feature_abnormal);
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mMinBitRate.setValueIndex(Integer.valueOf(
                                    minBitRateValue).intValue());
                            mMinBitRate.setSummary(mMinBitRate.getEntry());
                        }
                    });
                }
                break;
            case MSG_SET_MIN_BITRATE:
                String setMinBitRate = (String) msg.obj;
                final int setMinBitRateValue = Integer.valueOf(setMinBitRate)
                        .intValue();
                mResp = sendAt(engconstents.ENG_VOICE_CODE_SETTING + "1,4,"
                        + setMinBitRateValue, "atchannel0");
                if (mResp.contains("OK")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mMinBitRate.setValueIndex(setMinBitRateValue);
                            mMinBitRate.setSummary(mMinBitRate.getEntry());
                            Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                }
                break;
            case MSG_GET_MAX_BITRATE:
                mResp = sendAt(engconstents.ENG_VOICE_CODE_SETTING + "0,5",
                        "atchannel0");
                final String maxBitRateValue = anayResult(MSG_GET_MAX_BITRATE,
                        mResp);
                if (maxBitRateValue.contains("FAILED")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mMaxBitRate.setEnabled(false);
                            mMaxBitRate.setSummary(R.string.feature_abnormal);
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mMaxBitRate.setValueIndex(Integer.valueOf(
                                    maxBitRateValue).intValue());
                            mMaxBitRate.setSummary(mMaxBitRate.getEntry());
                        }
                    });
                }
                break;
            case MSG_SET_MAX_BITRATE:
                String setMaxBitRate = (String) msg.obj;
                final int setMaxBitRateValue = Integer.valueOf(setMaxBitRate)
                        .intValue();
                mResp = sendAt(engconstents.ENG_VOICE_CODE_SETTING + "1,5,"
                        + setMaxBitRateValue, "atchannel0");
                if (mResp.contains("OK")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mMaxBitRate.setValueIndex(setMaxBitRateValue);
                            mMaxBitRate.setSummary(mMaxBitRate.getEntry());
                            Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                }
                break;
            case MSG_GET_DEFAULT_BITRATE:
                mResp = sendAt(engconstents.ENG_VOICE_CODE_SETTING + "0,6",
                        "atchannel0");
                final String defaultBitRateValue = anayResult(
                        MSG_GET_DEFAULT_BITRATE, mResp);
                if (defaultBitRateValue.contains("FAILED")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mDefaultBitRate.setEnabled(false);
                            mDefaultBitRate
                                    .setSummary(R.string.feature_abnormal);
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mDefaultBitRate.setValueIndex(Integer.valueOf(
                                    defaultBitRateValue).intValue());
                            mDefaultBitRate.setSummary(mDefaultBitRate
                                    .getEntry());
                        }
                    });
                }
                break;
            case MSG_SET_DEFAULT_BITRATE:
                String setDefaultBitRate = (String) msg.obj;
                final int setDefaultBitRateValue = Integer.valueOf(
                        setDefaultBitRate).intValue();
                mResp = sendAt(engconstents.ENG_VOICE_CODE_SETTING + "1,6,"
                        + setDefaultBitRateValue, "atchannel0");
                if (mResp.contains("OK")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mDefaultBitRate
                                    .setValueIndex(setDefaultBitRateValue);
                            mDefaultBitRate.setSummary(mDefaultBitRate
                                    .getEntry());
                            Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                }
                break;
            case MSG_GET_EVS_RATE_SET:
                mResp = sendAt(engconstents.ENG_VOICE_CODE_SETTING + "0,7",
                        "atchannel0");
                String getEvsRateSet = anayResult(MSG_GET_EVS_RATE_SET, mResp);
                if (mResp != null && mResp.contains(IATUtils.AT_OK)) {
                    int evs_rs_value = Integer.valueOf(getEvsRateSet);
                    StringBuffer typeValue = new StringBuffer();
                    for (int i = 0; i < mContext.getResources().getStringArray(
                            R.array.evs_wb_bitrate_set).length; i++) {
                        if ((evs_rs_value & (1 << i)) != 0) {
                            if (!("".equals(typeValue.toString()))) {
                                typeValue.append("/");
                            }
                            typeValue.append(mContext.getResources().getStringArray(R.array.evs_wb_bitrate_set)[i]);
                        }
                    }
                    final String summary = typeValue.toString();
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mEvsRateSet.setSummary(summary);
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mEvsRateSet.setEnabled(false);
                            mEvsRateSet.setSummary(R.string.feature_abnormal);
                        }
                    });
                }
                Message msgWbRateSet = mVolteHandler
                        .obtainMessage(MSG_GET_WB_RATE_SET);
                mVolteHandler.sendMessage(msgWbRateSet);
                break;
            case MSG_SET_EVS_RATE_SET:
                view = LayoutInflater.from(mContext).inflate(
                        R.layout.volte_voice_code, null);
                line = (LinearLayout) view.findViewById(R.id.linearlayout_main);
                if (mVolteViewList == null) {
                    mVolteViewList = new ArrayList<CheckBox>();
                } else {
                    mVolteViewList.clear();
                }
                for (int i = 0; i < mContext.getResources().getStringArray(
                        R.array.evs_wb_bitrate_set).length; i++) {
                    CheckBox checkbox = new CheckBox(mContext);
                    checkbox.setText(mContext.getResources().getStringArray(
                            R.array.evs_wb_bitrate_set)[i]);
                    line.addView(checkbox);
                    mVolteViewList.add(checkbox);
                }
                mVolteViewList.get(mVolteViewList.size() - 1)
                        .setOnCheckedChangeListener(
                                new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(
                                            CompoundButton buttonView,
                                            boolean isChecked) {
                                        // TODO Auto-generated method stub
                                        if (isChecked) {
                                            for (int i = 0; i < mVolteViewList
                                                    .size() - 1; i++) {
                                                mVolteViewList.get(i)
                                                        .setChecked(true);
                                            }
                                        } else {
                                            for (int i = 0; i < mVolteViewList
                                                    .size() - 1; i++) {
                                                mVolteViewList.get(i)
                                                        .setChecked(false);
                                            }
                                        }
                                    }
                                });
                mResp = sendAt(engconstents.ENG_VOICE_CODE_SETTING + "0,7",
                        "atchannel0");
                getEvsRateSet = anayResult(MSG_GET_EVS_RATE_SET, mResp);
                if (mResp != null && mResp.contains(IATUtils.AT_OK)) {
                    final int EvsRateSetValue = Integer.valueOf(getEvsRateSet);
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < mVolteViewList.size(); i++) {
                                if ((EvsRateSetValue & (1 << i)) != 0) {
                                    mVolteViewList.get(i).setChecked(true);
                                } else {
                                    mVolteViewList.get(i).setChecked(false);
                                }
                            }
                        }
                    });
                }
                popDialog(view, MSG_SET_EVS_RATE_SET);
                break;
            case MSG_GET_EVS_DEFAULT_BITRATE:
                mResp = sendAt(engconstents.ENG_VOICE_CODE_SETTING + "0,8",
                        "atchannel0");
                final String evsDefBrValue = anayResult(
                        MSG_GET_EVS_DEFAULT_BITRATE, mResp);
                if (evsDefBrValue.contains("FAILED")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mEvsDefaultBR.setEnabled(false);
                            mEvsDefaultBR.setSummary(R.string.feature_abnormal);
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mEvsDefaultBR.setValueIndex(Integer.valueOf(
                                    evsDefBrValue).intValue());
                            mEvsDefaultBR.setSummary(mEvsDefaultBR.getEntry());
                        }
                    });
                }
                break;
            case MSG_SET_EVS_DEFAULT_BITRATE:
                String setEvsDefaultBr = (String) msg.obj;
                final int setEvsDefaultBrValue = Integer.valueOf(
                        setEvsDefaultBr).intValue();
                mResp = sendAt(engconstents.ENG_VOICE_CODE_SETTING + "1,8,"
                        + setEvsDefaultBrValue, "atchannel0");
                if (mResp.contains("OK")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mEvsDefaultBR.setValueIndex(setEvsDefaultBrValue);
                            mEvsDefaultBR.setSummary(mEvsDefaultBR.getEntry());
                            Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                }
                break;
            case MSG_GET_WB_RATE_SET:
                mResp = sendAt(engconstents.ENG_VOICE_CODE_SETTING + "0,9",
                        "atchannel0");
                String getWbRateSet = anayResult(MSG_GET_WB_RATE_SET, mResp);
                if (mResp != null && mResp.contains(IATUtils.AT_OK)) {
                    int wb_rs_value = Integer.valueOf(getWbRateSet);
                    StringBuffer typeValue = new StringBuffer();
                    for (int i = 0; i < mContext.getResources().getStringArray(
                            R.array.evs_wb_bitrate_set).length; i++) {
                        if ((wb_rs_value & (1 << i)) != 0) {
                            if (!("".equals(typeValue.toString()))) {
                                typeValue.append("/");
                            }
                            typeValue.append(mContext.getResources().getStringArray(R.array.evs_wb_bitrate_set)[i]);
                        }
                    }
                    final String summary = typeValue.toString();
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mWbRateSet.setSummary(summary);
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mWbRateSet.setEnabled(false);
                            mWbRateSet.setSummary(R.string.feature_abnormal);
                        }
                    });
                }
                Message msgNbRateSet = mVolteHandler
                        .obtainMessage(MSG_GET_NB_RATE_SET);
                mVolteHandler.sendMessage(msgNbRateSet);
                break;
            case MSG_SET_WB_RATE_SET:
                view = LayoutInflater.from(mContext).inflate(
                        R.layout.volte_voice_code, null);
                line = (LinearLayout) view.findViewById(R.id.linearlayout_main);
                if (mVolteViewList == null) {
                    mVolteViewList = new ArrayList<CheckBox>();
                } else {
                    mVolteViewList.clear();
                }
                for (int i = 0; i < mContext.getResources().getStringArray(
                        R.array.evs_wb_bitrate_set).length; i++) {
                    CheckBox checkbox = new CheckBox(mContext);
                    checkbox.setText(mContext.getResources().getStringArray(
                            R.array.evs_wb_bitrate_set)[i]);
                    line.addView(checkbox);
                    mVolteViewList.add(checkbox);
                }
                mVolteViewList.get(mVolteViewList.size() - 1)
                        .setOnCheckedChangeListener(
                                new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(
                                            CompoundButton buttonView,
                                            boolean isChecked) {
                                        // TODO Auto-generated method stub
                                        if (isChecked) {
                                            for (int i = 0; i < mVolteViewList
                                                    .size() - 1; i++) {
                                                mVolteViewList.get(i)
                                                        .setChecked(true);
                                            }
                                        } else {
                                            for (int i = 0; i < mVolteViewList
                                                    .size() - 1; i++) {
                                                mVolteViewList.get(i)
                                                        .setChecked(false);
                                            }
                                        }
                                    }
                                });
                mResp = sendAt(engconstents.ENG_VOICE_CODE_SETTING + "0,9",
                        "atchannel0");
                getWbRateSet = anayResult(MSG_GET_WB_RATE_SET, mResp);
                if (mResp != null && mResp.contains(IATUtils.AT_OK)) {
                    final int WbRateSetValue = Integer.valueOf(getWbRateSet);
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < mVolteViewList.size(); i++) {
                                if ((WbRateSetValue & (1 << i)) != 0) {
                                    mVolteViewList.get(i).setChecked(true);
                                } else {
                                    mVolteViewList.get(i).setChecked(false);
                                }
                            }
                        }
                    });
                }
                popDialog(view, MSG_SET_WB_RATE_SET);
                break;
            case MSG_GET_WB_DEFAULT_BITRATE:
                mResp = sendAt(engconstents.ENG_VOICE_CODE_SETTING + "0,10",
                        "atchannel0");
                final String wbDefBrValue = anayResult(
                        MSG_GET_WB_DEFAULT_BITRATE, mResp);
                if (wbDefBrValue.contains("FAILED")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mWbDefaultBR.setEnabled(false);
                            mWbDefaultBR.setSummary(R.string.feature_abnormal);
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mWbDefaultBR.setValueIndex(Integer.valueOf(
                                    wbDefBrValue).intValue());
                            mWbDefaultBR.setSummary(mWbDefaultBR.getEntry());
                        }
                    });
                }
                break;
            case MSG_SET_WB_DEFAULT_BITRATE:
                String setWbDefaultBr = (String) msg.obj;
                final int setWbDefaultBrValue = Integer.valueOf(setWbDefaultBr)
                        .intValue();
                mResp = sendAt(engconstents.ENG_VOICE_CODE_SETTING + "1,10,"
                        + setWbDefaultBrValue, "atchannel0");
                if (mResp.contains("OK")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mWbDefaultBR.setValueIndex(setWbDefaultBrValue);
                            mWbDefaultBR.setSummary(mWbDefaultBR.getEntry());
                            Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                }
                break;
            case MSG_GET_NB_RATE_SET:
                mResp = sendAt(engconstents.ENG_VOICE_CODE_SETTING + "0,11",
                        "atchannel0");
                String getNbRateSet = anayResult(MSG_GET_NB_RATE_SET, mResp);
                if (mResp != null && mResp.contains(IATUtils.AT_OK)) {
                    int nb_rs_value = Integer.valueOf(getNbRateSet);
                    StringBuffer typeValue = new StringBuffer();
                    for (int i = 0; i < mContext.getResources().getStringArray(
                            R.array.nb_bitrate_set).length; i++) {
                        if ((nb_rs_value & (1 << i)) != 0) {
                            if (!("".equals(typeValue.toString()))) {
                                typeValue.append("/");
                            }
                            typeValue.append(mContext.getResources().getStringArray(R.array.nb_bitrate_set)[i]);
                        }
                    }
                    final String summary = typeValue.toString();
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mNbRateSet.setSummary(summary);
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mNbRateSet.setEnabled(false);
                            mNbRateSet.setSummary(R.string.feature_abnormal);
                        }
                    });
                }
                break;
            case MSG_SET_NB_RATE_SET:
                view = LayoutInflater.from(mContext).inflate(
                        R.layout.volte_voice_code, null);
                line = (LinearLayout) view.findViewById(R.id.linearlayout_main);
                if (mVolteViewList == null) {
                    mVolteViewList = new ArrayList<CheckBox>();
                } else {
                    mVolteViewList.clear();
                }
                for (int i = 0; i < mContext.getResources().getStringArray(
                        R.array.nb_bitrate_set).length; i++) {
                    CheckBox checkbox = new CheckBox(mContext);
                    checkbox.setText(mContext.getResources().getStringArray(
                            R.array.nb_bitrate_set)[i]);
                    line.addView(checkbox);
                    mVolteViewList.add(checkbox);
                }
                mVolteViewList.get(mVolteViewList.size() - 1)
                        .setOnCheckedChangeListener(
                                new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(
                                            CompoundButton buttonView,
                                            boolean isChecked) {
                                        // TODO Auto-generated method stub
                                        if (isChecked) {
                                            for (int i = 0; i < mVolteViewList
                                                    .size() - 1; i++) {
                                                mVolteViewList.get(i)
                                                        .setChecked(true);
                                            }
                                        } else {
                                            for (int i = 0; i < mVolteViewList
                                                    .size() - 1; i++) {
                                                mVolteViewList.get(i)
                                                        .setChecked(false);
                                            }
                                        }
                                    }
                                });
                mResp = sendAt(engconstents.ENG_VOICE_CODE_SETTING + "0,11",
                        "atchannel0");
                getNbRateSet = anayResult(MSG_GET_NB_RATE_SET, mResp);
                if (mResp != null && mResp.contains(IATUtils.AT_OK)) {
                    final int NbRateSetValue = Integer.valueOf(getNbRateSet);
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < mVolteViewList.size(); i++) {
                                if ((NbRateSetValue & (1 << i)) != 0) {
                                    mVolteViewList.get(i).setChecked(true);
                                } else {
                                    mVolteViewList.get(i).setChecked(false);
                                }
                            }
                        }
                    });
                }
                popDialog(view, MSG_SET_NB_RATE_SET);
                break;
            case MSG_GET_NB_DEFAULT_BITRATE:
                mResp = sendAt(engconstents.ENG_VOICE_CODE_SETTING + "0,12",
                        "atchannel0");
                final String nbDefBrValue = anayResult(
                        MSG_GET_NB_DEFAULT_BITRATE, mResp);
                if (nbDefBrValue.contains("FAILED")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mNbDefaultBR.setEnabled(false);
                            mNbDefaultBR.setSummary(R.string.feature_abnormal);
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mNbDefaultBR.setValueIndex(Integer.valueOf(
                                    nbDefBrValue).intValue());
                            mNbDefaultBR.setSummary(mNbDefaultBR.getEntry());
                        }
                    });
                }
                break;
            case MSG_SET_NB_DEFAULT_BITRATE:
                String setNbDefaultBr = (String) msg.obj;
                final int setNbDefaultBrValue = Integer.valueOf(setNbDefaultBr)
                        .intValue();
                mResp = sendAt(engconstents.ENG_VOICE_CODE_SETTING + "1,12,"
                        + setNbDefaultBrValue, "atchannel0");
                if (mResp.contains("OK")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mNbDefaultBR.setValueIndex(setNbDefaultBrValue);
                            mNbDefaultBR.setSummary(mNbDefaultBR.getEntry());
                            Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
                }
                break;
            case MSG_GET_CHANNEL_AWARE_MODE:
                mResp = sendAt(engconstents.ENG_VOICE_CODE_SETTING + "0,13",
                        "atchannel0");
                final String channelAwareMode = anayResult(
                        MSG_GET_CHANNEL_AWARE_MODE, mResp);
                final int tempValue = changeValueToIndex(channelAwareMode);
                Log.d(TAG, "tempValue is " + tempValue);
                if (mResp != null && mResp.contains(IATUtils.AT_OK)) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mChannelAwareMode.setValueIndex(tempValue);
                            mChannelAwareMode.setSummary(mChannelAwareMode
                                    .getEntry());
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mChannelAwareMode.setEnabled(false);
                            mChannelAwareMode
                                    .setSummary(R.string.feature_abnormal);
                        }
                    });
                }
                break;
            case MSG_SET_CHANNEL_AWARE_MODE:
                String setAwreMde = (String) msg.obj;
                final int awareModevalue = Integer.valueOf(setAwreMde)
                        .intValue();
                String tempChannelAware = changeIndexToValue(Integer.valueOf(
                        setAwreMde).intValue());
                mResp = sendAt(engconstents.ENG_VOICE_CODE_SETTING + "1,13,"
                        + tempChannelAware, "atchannel0");
                if (mResp != null && mResp.contains(IATUtils.AT_OK)) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mChannelAwareMode.setValueIndex(awareModevalue);
                            mChannelAwareMode.setSummary(mChannelAwareMode
                                    .getEntry());
                            Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
                }
                break;
            case MSG_GET_CHANNEL_AWARE_BANDWIDTH:
                mResp = sendAt(engconstents.ENG_VOICE_CODE_SETTING + "0,14",
                        "atchannel0");
                final String channelAwareBandwidthValue = anayResult(
                        MSG_GET_CHANNEL_AWARE_BANDWIDTH, mResp);
                try {
                    final int tempBandwidthValue = Integer.valueOf(
                            channelAwareBandwidthValue).intValue();
                    if (mResp != null && mResp.contains(IATUtils.AT_OK)) {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mChannelAwareBD.setValueIndex((tempBandwidthValue > 0) ? (tempBandwidthValue - 1) : tempBandwidthValue);
                                mChannelAwareBD.setSummary(mChannelAwareBD
                                        .getEntry());
                            }
                        });
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mChannelAwareBD.setEnabled(false);
                                mChannelAwareBD
                                        .setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                } catch (NumberFormatException ex) {
                    Log.d(TAG, "NumberFormatException: " + ex);
                }
                break;
            case MSG_SET_CHANNEL_AWARE_BANDWIDTH:
                String setAwreBandwidth = (String) msg.obj;
                final int setNAwreBandwidthValue = Integer.valueOf(
                        setAwreBandwidth).intValue() + 1;
                mResp = sendAt(engconstents.ENG_VOICE_CODE_SETTING + "1,14,"
                        + setNAwreBandwidthValue, "atchannel0");
                if (mResp != null && mResp.contains(IATUtils.AT_OK)) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mChannelAwareBD
                                    .setValueIndex(setNAwreBandwidthValue - 1);
                            mChannelAwareBD.setSummary(mChannelAwareBD
                                    .getEntry());
                            Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
                }
                break;
            case MSG_GET_PRECONDITION_STATE:
                mResp = sendAt(engconstents.ENG_PRECONDITION_SETTING + "0",
                        "atchannel0");
                final String getPreconditionStatue = anayResult(
                        MSG_GET_PRECONDITION_STATE, mResp);
                if (getPreconditionStatue.contains("FAILED")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mPreconditionSwitch.setEnabled(false);
                            mPreconditionSwitch
                                    .setSummary(R.string.feature_abnormal);
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mPreconditionSwitch
                                    .setChecked(getPreconditionStatue
                                            .contains("1"));
                        }
                    });
                }
                break;
            case MSG_SET_PRECONDITION_STATE:
                final String setPreconditionStatue = (String) msg.obj;
                mResp = sendAt(engconstents.ENG_PRECONDITION_SETTING + "1,"
                        + setPreconditionStatue, "atchannel0");
                if (mResp.contains("OK")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mPreconditionSwitch
                                    .setChecked(setPreconditionStatue
                                            .contains("1"));
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mPreconditionSwitch
                                    .setChecked(!setPreconditionStatue
                                            .contains("1"));
                        }
                    });
                    Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                }
                break;
            case MSG_GET_TQOS_TIMER:
                mResp = sendAt(engconstents.ENG_TQOS_TIMER_SETTING + "0",
                        "atchannel0");
                final String getTqosTimer = anayResult(MSG_GET_TQOS_TIMER,
                        mResp);
                if (getTqosTimer.contains("FAILED")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mTqosTimer.setEnabled(false);
                            mTqosTimer.setSummary(R.string.feature_abnormal);
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mTqosTimer.setEnabled(true);
                            mTqosTimer.setSummary(getTqosTimer);
                        }
                    });
                }
                break;
            case MSG_SET_TQOS_TIMER:
                final String setTqosTimer = (String) msg.obj;
                mResp = sendAt(engconstents.ENG_TQOS_TIMER_SETTING + "1,"
                        + setTqosTimer, "atchannel0");
                if (mResp.contains("OK")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            Editor editor = mSharePref.edit();
                            editor.putString(KEY_TQOS_TIMER_SETTING,
                                    setTqosTimer);
                            editor.commit();
                            mTqosTimer.setSummary(setTqosTimer);
                        }
                    });
                } else {
                    Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                }
                break;
            case MSG_GET_TCALL_TIMER:
                mResp = sendAt(engconstents.ENG_TCALL_TIMER_SETTING + "0",
                        "atchannel0");
                final String getTcallTimer = anayResult(MSG_GET_TCALL_TIMER,
                        mResp);
                if (getTcallTimer.contains("FAILED")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mTcallTimer.setEnabled(false);
                            mTcallTimer.setSummary(R.string.feature_abnormal);
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mTcallTimer.setEnabled(true);
                            mTcallTimer.setSummary(getTcallTimer);
                        }
                    });
                }
                break;
            case MSG_SET_TCALL_TIMER:
                final String setTcallTimer = (String) msg.obj;
                mResp = sendAt(engconstents.ENG_TCALL_TIMER_SETTING + "1,"
                        + setTcallTimer, "atchannel0");
                if (mResp.contains("OK")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            Editor editor = mSharePref.edit();
                            editor.putString(KEY_TCALL_TIMER_SETTING,
                                    setTcallTimer);
                            editor.commit();
                            mTcallTimer.setSummary(setTcallTimer);
                        }
                    });
                } else {
                    Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                }
                break;
            case MSG_GET_TREG_TIMER:
                mResp = sendAt(engconstents.ENG_TREG_TIMER_SETTING + "0",
                        "atchannel0");
                final String getTrgeTimer = anayResult(MSG_GET_TREG_TIMER,
                        mResp);
                if (getTrgeTimer.contains("FAILED")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mTregTimer.setEnabled(false);
                            mTregTimer.setSummary(R.string.feature_abnormal);
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mTregTimer.setEnabled(true);
                            mTregTimer.setSummary(getTrgeTimer);
                        }
                    });
                }
                break;
            case MSG_SET_TREG_TIMER:
                final String setTrgeTimer = (String) msg.obj;
                mResp = sendAt(engconstents.ENG_TREG_TIMER_SETTING + "1,"
                        + setTrgeTimer, "atchannel0");
                if (mResp.contains("OK")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            Editor editor = mSharePref.edit();
                            editor.putString(KEY_TCALL_TIMER_SETTING,
                                    setTrgeTimer);
                            editor.commit();
                            mTregTimer.setSummary(setTrgeTimer);
                        }
                    });
                } else {
                    Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                }
                break;
            case MSG_GET_PQOS_TIMER:
                mResp = sendAt(engconstents.ENG_AT_GET_PQOS, "atchannel0");
                if (mResp.contains(IATUtils.AT_OK)) {
                    int index = mResp.indexOf("25,0,");
                    if (index > 0) {
                        final String time = mResp.substring(index + 5,
                                index + 6);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mPqosTimer.setSummary(time + "s");
                            }
                        });
                    }
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "GET_SESSION_TIMER Failed");
                            mPqosTimer.setSummary(mContext
                                    .getString(R.string.feature_not_support));
                            mPqosTimer.setEnabled(false);
                        }
                    });
                }
                break;
            case MSG_SET_PQOS_TIMER:
                final String setPqosTimer = (String) msg.obj;
                mResp = sendAt(engconstents.ENG_AT_SET_PQOS + setPqosTimer,
                        "atchannel0");
                if (mResp.contains("OK")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mPqosTimer.setSummary(setPqosTimer + "s");
                        }
                    });
                } else {
                    Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                }
                break;
            case GET_SESSION_TIMER:
                mResp = sendAt(engconstents.ENG_GET_SESSION_TIMER, "atchannel0");
                Log.d(TAG, "GET_SESSION_TIMER" + "  " + "mATResponse: " + mResp);
                if (mResp.contains(IATUtils.AT_OK)) {
                    String[] str = mResp.split("\"");
                    if (str.length > 1) {
                        final String time = str[1].equals("") ? "0" : str[1];
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mSessionTimer.setSummary(time + "s");
                            }
                        });
                    }
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "GET_SESSION_TIMER Failed");
                            mSessionTimer.setSummary(mContext
                                    .getString(R.string.feature_not_support));
                            mSessionTimer.setEnabled(false);
                        }
                    });
                }
                break;
            case SET_SESSION_TIMER:
                final int time = Integer.valueOf(msg.obj.toString());
                mResp = sendAt(engconstents.ENG_SET_SESSION_TIMER + "\"" + time
                        + "\"", "atchannel0");
                Log.d(TAG, "SET_SESSION_TIMER" + "  " + "mATResponse: " + mResp);
                if (mResp.contains(IATUtils.AT_OK)) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mSessionTimer.setSummary(time + "s");
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext,
                                    "SET_SESSION_TIMER Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            // begin bug572397 add by suyan.yang 20160620
            case GET_FORCE_MT_SESSION_TIMER:
                mResp = sendAt(engconstents.ENG_GET_FORCE_MT_SESSION_TIMER,
                        "atchannel0");
                Log.d(TAG, "GET_FORCE_MT_SESSION_TIMER" + "  "
                        + "mATResponse: " + mResp);
                if (mResp.contains(IATUtils.AT_OK)) {
                    String[] str = mResp.split("\"");
                    if (str.length > 1) {
                        final String[] strr = str[1].split(";");
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mForceMtSessionTimer.setSummary(strr[0] + "s");
                            }
                        });
                    }
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "GET_FORCE_MT_SESSION_TIMER Failed");
                            mForceMtSessionTimer.setSummary(mContext
                                    .getString(R.string.feature_not_support));
                            mForceMtSessionTimer.setEnabled(false);
                        }
                    });
                }
                break;
            case SET_FORCE_MT_SESSION_TIMER:
                final int force_mt_time = Integer.valueOf(msg.obj.toString());
                mResp = sendAt(engconstents.ENG_SET_FORCE_MT_SESSION_TIMER
                        + "\"" + force_mt_time + "\"", "atchannel0");
                Log.d(TAG, "SET_FORCE_MT_SESSION_TIMER" + "  "
                        + "mATResponse: " + mResp);
                if (mResp.contains(IATUtils.AT_OK)) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mForceMtSessionTimer
                                    .setSummary(force_mt_time + "s");
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext,
                                    "SET_FORCE_MT_SESSION_TIMER Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            default:
                break;
            }
        }
    }

    private int changeValueToIndex(String stringValue) {
        int valueIndex = 0;
        if (stringValue.contains("255")) {
            valueIndex = 0;
        } else if (stringValue.contains("0")) {
            valueIndex = 1;
        } else if (stringValue.contains("2")) {
            valueIndex = 2;
        } else if (stringValue.contains("3")) {
            valueIndex = 3;
        } else if (stringValue.contains("5")) {
            valueIndex = 4;
        } else if (stringValue.contains("7")) {
            valueIndex = 5;
        }
        return valueIndex;
    }

    private String changeIndexToValue(int setValueIndex) {
        String stringValue = null;
        if (setValueIndex == 0) {
            // stringValue = "0xFF";
            stringValue = "255";
        } else if (setValueIndex == 1) {
            stringValue = "0";
        } else if (setValueIndex == 2) {
            stringValue = "2";
        } else if (setValueIndex == 3) {
            stringValue = "3";
        } else if (setValueIndex == 4) {
            stringValue = "5";
        } else if (setValueIndex == 5) {
            stringValue = "7";
        }
        return stringValue;
    }

    private void popDialog(View view, final int dialogType) {
        int arrayId_tmp = 0;
        int titleId_tmp = 0;
        String para_tmp = "";
        Preference pref_tmp = null;
        if (dialogType == MSG_SET_VOICE_CODE_TYPE) {
            titleId_tmp = R.string.volte_voice_code_type;
            arrayId_tmp = R.array.volte_voice_code;
            para_tmp = "1";
            pref_tmp = mVolteVoiceCode;
        } else if (dialogType == MSG_SET_EVS_RATE_SET) {
            titleId_tmp = R.string.rate_set;
            arrayId_tmp = R.array.evs_wb_bitrate_set;
            para_tmp = "7";
            pref_tmp = mEvsRateSet;
        } else if (dialogType == MSG_SET_WB_RATE_SET) {
            titleId_tmp = R.string.rate_set;
            arrayId_tmp = R.array.evs_wb_bitrate_set;
            para_tmp = "9";
            pref_tmp = mWbRateSet;
        } else if (dialogType == MSG_SET_NB_RATE_SET) {
            titleId_tmp = R.string.rate_set;
            arrayId_tmp = R.array.nb_bitrate_set;
            para_tmp = "11";
            pref_tmp = mNbRateSet;
        } else {
            return;
        }
        final int stringArrayId = arrayId_tmp;
        final int diaTitleId = titleId_tmp;
        final String type = para_tmp;
        final Preference preference = pref_tmp;
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext)
                .setView(view).setNegativeButton(
                        getString(R.string.alertdialog_cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                // destroyDialog(dialog);
                            }
                        });

        alertBuilder.setTitle(diaTitleId).setPositiveButton(
                getString(R.string.alertdialog_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int listSize;
                        if (dialogType == MSG_SET_VOICE_CODE_TYPE) {
                            listSize = mVolteViewList.size();
                        } else {
                            listSize = mVolteViewList.size() - 1;
                        }
                        String[] arr = mContext.getResources().getStringArray(stringArrayId);
                        int[] tempInt = {0};
                        String typeValue = IntStream.range(0, listSize).filter(i -> mVolteViewList.get(i).isChecked())
                            .peek( i -> tempInt[0] += (1 << i))
                            .mapToObj( i -> arr[i]).collect(Collectors.joining("/"));
                        int value = tempInt[0];
                        if (value == 0) {
                            mUiThread.post(() -> Toast.makeText(mContext,
                                    R.string.volte_set_error,
                                    Toast.LENGTH_SHORT).show());
                            return;
                        }
                        final String cmd = engconstents.ENG_VOICE_CODE_SETTING
                                + "1," + type + "," + value;
                        final String atRsp = sendAt(cmd, "atchannel0");
                        final String summary = typeValue;
                        if (atRsp.contains(IATUtils.AT_OK)) {
                            mUiThread.post(() -> {
                                preference.setSummary(summary);
                                Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                });
        mAlertDialog = alertBuilder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.show();
    }

    /*
     * when at response is null,change response to "FAILED"
     */
    private String sendAt(String cmd, String servername) {
        String res = IATUtils.sendATCmd(cmd, servername);
        Log.d(TAG, "ATCmd is " + cmd + ", result is " + res);
        if (res != null) {
            return res;
        } else {
            return "FAILED";
        }
    }

    /*
     *
     */
    private String anayResult(int mesg, String result) {
        String res = null;
        if (!result.contains("OK")) {
            return res = "FAILED";
        }
        switch (mesg) {
        case MSG_GET_IP_STATE:
            // res only contains IP state;ex:res ="1";
            res = result.split("\\:")[1].split("\\,")[2].split("\n")[0].trim();
            Log.d(TAG, "MSG_GET_IP_STATE anayResult is " + res);
            break;
        case MSG_GET_MUT_LENGTH:
            // res only contains length;ex:res ="33";
            res = result.split("\\:")[1].split("\\,")[2].split("\n")[0].trim();
            Log.d(TAG, "MSG_GET_MUT_LENGTH anayResult is " + res);
            break;
        case MSG_GET_SUBSCRIB_STATE:
            // res only contains subscribe switch state;ex:res="1" means
            // switch open
            res = result.split("\\:")[1].split("\\,")[2].split("\n")[0].trim();
            Log.d(TAG, "MSG_GET_SUBSCRIB_STATE anayResult is " + res);
            break;
        case MSG_GET_SIGCOMP_STATE:
            // res contains switch state and algorithm;ex:res= "1,0" means
            // switch open and algorithm is default
            // <-+SPENGMDVOLTE锛�,0,1,2
            // <-OK
            res = result.split("\\:")[1].trim().split("\n")[0].substring(4);
            Log.d(TAG, "MSG_GET_SIGCOMP_STATE anayResult is " + res);
            break;
        case MSG_GET_EXPIRES_TIME:
            // res only contains time;ex:res="3600"
            // <-+SPENGMDVOLTE锛�1,0,3600
            // <-OK
            res = result.split("\\:")[1].split("\\,")[2].split("\n")[0].trim();
            Log.d(TAG, "MSG_GET_EXPIRES_TIME anayResult is " + res);
            break;
        case MSG_GET_VOICE_CODE_WAY:
            // res contains code_way and code_speed;ex:res="0,6" means
            // code_way is AMR-WB,code_speed is 19.85 kbps
            // -> AT+SPENGMDVOLTE:12,0,0锛�
            // <-OK
            res = result.split("\\:")[1].split("\n")[0].substring(4);
            Log.d(TAG, "MSG_GET_VOICE_CODE_WAY anayResult is " + res);
            break;
        case MSG_GET_VOICE_CODE_TYPE:
        case MSG_GET_MIN_BANDWIDTH:
        case MSG_GET_MAX_BANDWIDTH:
        case MSG_GET_MIN_BITRATE:
        case MSG_GET_MAX_BITRATE:
        case MSG_GET_DEFAULT_BITRATE:
        case MSG_GET_EVS_DEFAULT_BITRATE:
        case MSG_GET_WB_DEFAULT_BITRATE:
        case MSG_GET_NB_DEFAULT_BITRATE:
        case MSG_GET_EVS_RATE_SET:
        case MSG_GET_WB_RATE_SET:
        case MSG_GET_NB_RATE_SET:
            res = result.split("\\:")[1].split("\n")[0].split(",")[3].trim();
            Log.d(TAG, "MSG_GET anayResult is " + res);
            break;
        case MSG_GET_CHANNEL_AWARE_MODE:
        case MSG_GET_CHANNEL_AWARE_BANDWIDTH:
            res = result.split("\\:")[1].split("\n")[0].split(",")[3].trim();
            Log.d(TAG, "MSG_GET _CHANNEL_AWARE anayResult is " + res);
            break;
        case MSG_GET_PRECONDITION_STATE:
            // res only contains precondition state;ex:res="1" means
            // precondition open
            // <-+SPENGMDVOLTE锛�3,0,1
            // <-OK
            res = result.split("\\:")[1].split("\\,")[2].split("\n")[0].trim();
            Log.d(TAG, "MSG_GET_PRECONDITION_STATE anayResult is " + res);
            break;
        case MSG_GET_TQOS_TIMER:
            // res only contains tqos time; ex:res="6" means tqos time is 6 sec
            // <-+SPENGMDVOLTE锛�4,0,6
            // <-OK
            res = result.split("\\:")[1].split("\\,")[2].split("\n")[0].trim();
            Log.d(TAG, "MSG_GET_TQOS_TIMER anayResult is " + res);
            break;
        case MSG_GET_TCALL_TIMER:
            // res only contains tcall time; ex:res="10" means tqos time is 10
            // sec
            // <-+SPENGMDVOLTE锛�5,0,10
            // <-OK
            res = result.split("\\:")[1].split("\\,")[2].split("\n")[0].trim();
            Log.d(TAG, "MSG_GET_TCALL_TIMER anayResult is " + res);
            break;
        case MSG_GET_TREG_TIMER:
            // res only contains treg time; ex:res="15" means tqos time is 15
            // sec
            // <-+SPENGMDVOLTE锛�6,0,15
            // <-OK
            res = result.split("\\:")[1].split("\\,")[2].split("\n")[0].trim();
            Log.d(TAG, "MSG_GET_TREG_TIMER anayResult is " + res);
            break;
        // res only contains auth type; ex:res="auto" means auth type is auto
        default:
            break;
        }
        return res;
    }
}
