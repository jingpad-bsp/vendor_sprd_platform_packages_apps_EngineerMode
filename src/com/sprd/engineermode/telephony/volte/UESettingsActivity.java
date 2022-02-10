package com.sprd.engineermode.telephony.volte;

import android.os.PowerManager;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.TwoStatePreference;
import android.preference.EditTextPreference;

import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.exception.EmException;
import com.unisoc.engineermode.core.intf.ITelephonyApi;

import android.util.Log;
import android.widget.Toast;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.content.Context;
import android.os.Message;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.intf.ITelephonyApi.IVolteUeSettings;
import com.unisoc.engineermode.core.intf.ITelephonyApi.IVolteUeSettings.Bandwidth;
import com.unisoc.engineermode.core.intf.ITelephonyApi.IVolteUeSettings.Bitrate;
import com.unisoc.engineermode.core.intf.ITelephonyApi.IVolteUeSettings.SmsPdu;
import com.unisoc.engineermode.core.intf.ITelephonyApi.IVolteUeSettings.VideoCodec;
import com.unisoc.engineermode.core.intf.ITelephonyApi.IVolteUeSettings.VoiceCodec;

public class UESettingsActivity extends PreferenceActivity implements
    Preference.OnPreferenceChangeListener {

    private static final String TAG = "UESettingsActivity";

    private static final String KEY_SPRD_VOLTE = "sprd_volte";
    private static final String KEY_VOICE_CODE = "volte_voice_code";
    private static final String KEY_VIDEO_CODE = "volte_video_code";
    private static final String KEY_MAX_BANDWIDTH = "max_bandwidth";
    private static final String KEY_MAX_BITRATE = "max_bitrate";
    private static final String KEY_SMS_PDU = "key_sms_pdu";
    private static final String KEY_VIDEO_CALL_ENABLE = "video_call_enable";
    private static final String KEY_VIDEO_CONF_ENABLE = "video_conference_enable";


    private static final int MSG_GET_VOICE_CODE_TYPE = 27;
    private static final int MSG_SET_VOICE_CODE_TYPE = 28;
    private static final int MSG_GET_MAX_BANDWIDTH = 31;
    private static final int MSG_SET_MAX_BANDWIDTH = 32;
    private static final int MSG_GET_MAX_BITRATE = 33;
    private static final int MSG_SET_MAX_BITRATE = 34;
    private static final int MSG_SET_VIDEO_CODE_TYPE = 35;
    private static final int MSG_GET_VIDEO_CODE_TYPE = 36;
    private static final int MSG_SET_SPRD_VOLTE = 37;
    private static final int MSG_GET_SPRD_VOLTE = 38;
    private static final int MSG_SET_SMS_PDU = 39;
    private static final int MSG_GET_SMS_PDU = 40;
    private static final int MSG_SET_VIDEO_CALL_ENABLE = 41;
    private static final int MSG_GET_VIDEO_CALL_ENABLE = 42;
    private static final int MSG_SET_VIDEO_CONFERENCE_ENABLE = 43;
    private static final int MSG_GET_VIDEO_CONFERENCE_ENABLE = 44;

    private TwoStatePreference mVideoCallEnable;
    private TwoStatePreference mVideoConferenceEnable;
    private MultiSelectListPreference mVolteVoiceCode;
    private MultiSelectListPreference mVolteVideoCode;
    private ListPreference mMaxBandWidth;
    private ListPreference mMaxBitRate;
    private ListPreference mSmsPduformat;
    private EditTextPreference mSprdVolte;

    private Context mContext = null;
    //    private SharedPreferences mSharePref;
    private Handler mUiThread = new Handler();
    private VolteHandler mVolteHandler;

    private static ITelephonyApi teleApi = CoreApi.getTelephonyApi();
    private static IVolteUeSettings volteUeApi = CoreApi.getTelephonyApi().volteUeSettings();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_volte_ue_settings);
        mContext = this;
//        mSharePref = PreferenceManager.getDefaultSharedPreferences(this);
//        mSharePref.registerOnSharedPreferenceChangeListener(this);
        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mVolteHandler = new VolteHandler(ht.getLooper());

        initVoiceCodecListPref();
        initVideoCodecListPref();
        initBandwidthListPref();
        initBitrateListPref();
        initSmsPduListPref();

        mVideoCallEnable = (TwoStatePreference) findPreference(KEY_VIDEO_CALL_ENABLE);
        mVideoCallEnable.setOnPreferenceChangeListener(this);
        mVideoConferenceEnable = (TwoStatePreference) findPreference(KEY_VIDEO_CONF_ENABLE);
        mVideoConferenceEnable.setOnPreferenceChangeListener(this);
        mSprdVolte = (EditTextPreference) findPreference(KEY_SPRD_VOLTE);
        mSprdVolte.setOnPreferenceChangeListener(this);
    }

    private void initVoiceCodecListPref() {
        mVolteVoiceCode = (MultiSelectListPreference) findPreference(KEY_VOICE_CODE);
        mVolteVoiceCode.setOnPreferenceChangeListener(this);
        mVolteVoiceCode.setEntries(R.array.volte_voice_code);
        mVolteVoiceCode.setEntryValues(
            Arrays.stream(VoiceCodec.values()).map(Enum::name).toArray(String[]::new));
    }

    private void initVideoCodecListPref() {
        mVolteVideoCode = (MultiSelectListPreference) findPreference(KEY_VIDEO_CODE);
        mVolteVideoCode.setOnPreferenceChangeListener(this);
        mVolteVideoCode.setEntries(R.array.video_codec_str);
        mVolteVideoCode.setEntryValues(
            Arrays.stream(VideoCodec.values()).map(Enum::name).toArray(String[]::new));
    }

    private void initBandwidthListPref() {
        mMaxBandWidth = (ListPreference) findPreference(KEY_MAX_BANDWIDTH);
        mMaxBandWidth.setOnPreferenceChangeListener(this);
        mMaxBandWidth.setEntries(R.array.evs_primary_bandwidth);
        mMaxBandWidth.setEntryValues(
            Arrays.stream(Bandwidth.values()).map(Enum::name).toArray(String[]::new));
    }

    private void initBitrateListPref() {
        mMaxBitRate = (ListPreference) findPreference(KEY_MAX_BITRATE);
        mMaxBitRate.setOnPreferenceChangeListener(this);
        mMaxBitRate.setEntries(R.array.evs_primary_bitrate);
        mMaxBitRate.setEntryValues(
            Arrays.stream(Bitrate.values()).map(Enum::name).toArray(String[]::new));
    }

    private void initSmsPduListPref() {
        mSmsPduformat = (ListPreference) findPreference(KEY_SMS_PDU);
        mSmsPduformat.setOnPreferenceChangeListener(this);
        mSmsPduformat.setEntries(R.array.sms_pdu_str);
        mSmsPduformat.setEntryValues(
            Arrays.stream(SmsPdu.values()).map(Enum::name).toArray(String[]::new));
    }

    @Override
    public void onStart() {
        if (mMaxBandWidth != null && mMaxBandWidth.isEnabled()) {
            Message getMaxBandWidth = mVolteHandler.obtainMessage(MSG_GET_MAX_BANDWIDTH);
            mVolteHandler.sendMessage(getMaxBandWidth);
        }
        if (mMaxBitRate != null && mMaxBitRate.isEnabled()) {
            Message getMaxBitRate = mVolteHandler.obtainMessage(MSG_GET_MAX_BITRATE);
            mVolteHandler.sendMessage(getMaxBitRate);
        }
        if (mSmsPduformat != null && mSmsPduformat.isEnabled()) {
            Message msg = mVolteHandler.obtainMessage(MSG_GET_SMS_PDU);
            mVolteHandler.sendMessage(msg);
        }
        if (mVideoCallEnable != null && mVideoCallEnable.isEnabled()) {
            Message getVideoCallEnable = mVolteHandler.obtainMessage(MSG_GET_VIDEO_CALL_ENABLE);
            mVolteHandler.sendMessage(getVideoCallEnable);
        }
        if (mVideoConferenceEnable != null && mVideoConferenceEnable.isEnabled()) {
            Message getVideoConference = mVolteHandler.obtainMessage(MSG_GET_VIDEO_CONFERENCE_ENABLE);
            mVolteHandler.sendMessage(getVideoConference);
        }
        Message getVoiceCodeType = mVolteHandler.obtainMessage(MSG_GET_VOICE_CODE_TYPE);
        mVolteHandler.sendMessage(getVoiceCodeType);
        Message getVideoCodeType = mVolteHandler.obtainMessage(MSG_GET_VIDEO_CODE_TYPE);
        mVolteHandler.sendMessage(getVideoCodeType);

        if ((mSprdVolte != null) && mSprdVolte.isEnabled()) {
            Message msg = mVolteHandler.obtainMessage(MSG_GET_SPRD_VOLTE);
            mVolteHandler.sendMessage(msg);
        }
        super.onStart();
    }

    @Override
    public void onDestroy() {
        if (mVolteHandler != null) {
            mVolteHandler.getLooper().quit();
            Log.d(TAG, "HandlerThread has quit");
        }
        super.onDestroy();
    }

    @Override
    public boolean onPreferenceChange(Preference pref, Object newValue) {
        String prefkey = pref.getKey();
        Log.d(TAG, "prefkey: " + prefkey);

        if (prefkey.equals(KEY_VOICE_CODE)) {
            mVolteHandler.sendMessage(
                mVolteHandler.obtainMessage(MSG_SET_VOICE_CODE_TYPE, newValue));
            return false;
        } else if (prefkey.equals(KEY_VIDEO_CODE)) {
            mVolteHandler.sendMessage(
                mVolteHandler.obtainMessage(MSG_SET_VIDEO_CODE_TYPE, newValue));
            return false;
        } else if (prefkey.equals(KEY_MAX_BANDWIDTH)) {
            mVolteHandler.sendMessage(
                mVolteHandler.obtainMessage(MSG_SET_MAX_BANDWIDTH, newValue));
            return false;
        } else if (prefkey.equals(KEY_MAX_BITRATE)) {
            mVolteHandler.sendMessage(mVolteHandler.obtainMessage(MSG_SET_MAX_BITRATE, newValue));
            return false;
        } else if (prefkey.equals(KEY_SMS_PDU)) {
            mVolteHandler.sendMessage(mVolteHandler.obtainMessage(MSG_SET_SMS_PDU, newValue));
            return false;
        } else if (prefkey.equals(KEY_SPRD_VOLTE)) {
            Bundle bundle = new Bundle();
            bundle.putString("old", mSprdVolte.getText());
            bundle.putString("new", (String) newValue);
            mVolteHandler.sendMessage(mVolteHandler.obtainMessage(MSG_SET_SPRD_VOLTE, bundle));
            return false;
        } else if (pref == mVideoCallEnable) {
            Message msg = mVolteHandler.obtainMessage(MSG_SET_VIDEO_CALL_ENABLE,
                !mVideoCallEnable.isChecked());
            mVolteHandler.sendMessage(msg);
        } else if (pref == mVideoConferenceEnable) {
            Message msg = mVolteHandler.obtainMessage(MSG_SET_VIDEO_CONFERENCE_ENABLE,
                !mVideoConferenceEnable.isChecked());
            mVolteHandler.sendMessage(msg);
        }
        return true;
    }

    class VolteHandler extends Handler {
        VolteHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_GET_VOICE_CODE_TYPE:
                    Log.d(TAG, "MSG_GET_VOICE_CODE_TYPE");
                    try {
                        VoiceCodec[] voiceCodes = volteUeApi.getVoiceCodecType();
                        mUiThread.post(() -> {
                            mVolteVoiceCode.setValues(
                                Arrays.stream(voiceCodes).map(Enum::name).collect(Collectors.toSet()));
                            mVolteVoiceCode.setSummary(
                                Arrays.stream(voiceCodes).map(Enum::name)
                                    .reduce((t, u) -> t + "/" + u).orElse(""));
                        });
                    } catch (EmException e) {
                        e.printStackTrace();
                        mUiThread.post(() -> {
                            mVolteVoiceCode.setEnabled(false);
                            mVolteVoiceCode.setSummary(R.string.feature_abnormal);
                        });
                    }
                    break;
                case MSG_SET_VOICE_CODE_TYPE:
                    Log.d(TAG, "MSG_SET_VOICE_CODE_TYPE");
                    @SuppressWarnings("unchecked")
                    Set<String> values = (Set<String>) msg.obj;
                    try {
                        volteUeApi.setVoiceCodecType(
                            values.stream().map(VoiceCodec::valueOf).toArray(VoiceCodec[]::new));
                        mUiThread.post(() -> {
                            mVolteVoiceCode.setValues(values);
                            mVolteVoiceCode.setSummary(values.stream().reduce((t, u) -> t + "/" + u).orElse(""));
                            //Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                            //UNISOC: add for bug1249803
                            PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
                            pm.reboot("Audio Codec");
                        });
                    } catch (EmException e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_VIDEO_CODE_TYPE:
                    Log.d(TAG, "MSG_GET_VIDEO_CODE_TYPE");
                    try {
                        VideoCodec[] videoCodes = volteUeApi.getVideoCodecType();
                        mUiThread.post(() -> {
                            mVolteVideoCode.setValues(
                                Arrays.stream(videoCodes).map(Enum::name).collect(Collectors.toSet()));
                            mVolteVideoCode.setSummary(
                                Arrays.stream(videoCodes).map(Enum::name)
                                    .reduce((t, u) -> t + "/" + u).orElse(""));
                        });
                    } catch (EmException e) {
                        e.printStackTrace();
                        mUiThread.post(() -> {
                            mVolteVideoCode.setEnabled(false);
                            mVolteVideoCode.setSummary(R.string.feature_abnormal);
                        });
                    }
                    break;
                case MSG_SET_VIDEO_CODE_TYPE:
                    Log.d(TAG, "MSG_SET_VIDEO_CODE_TYPE");
                    @SuppressWarnings("unchecked")
                    Set<String> videoValues = (Set<String>) msg.obj;
                    try {
                        volteUeApi.setVideoCodecType(
                            videoValues.stream().map(VideoCodec::valueOf).toArray(VideoCodec[]::new));
                        mUiThread.post(() -> {
                            mVolteVideoCode.setValues(videoValues);
                            mVolteVideoCode.setSummary(videoValues.stream().reduce((t, u) -> t + "/" + u).orElse(""));
                            Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                        });
                    } catch (EmException e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_MAX_BANDWIDTH:
                    try {
                        final Bandwidth bw = volteUeApi.getMaxBandWidth();
                        mUiThread.post(() -> {
                            mMaxBandWidth.setValue(bw.name());
                            mMaxBandWidth.setSummary(mMaxBandWidth.getEntry());
                        });
                    } catch (EmException e) {
                        e.printStackTrace();
                        mUiThread.post(() -> {
                            mMaxBandWidth.setEnabled(false);
                            mMaxBandWidth.setSummary(R.string.feature_abnormal);
                        });
                    }
                    break;
                case MSG_SET_MAX_BANDWIDTH:
                    try {
                        String bw = (String) msg.obj;
                        volteUeApi.setMaxBandWidth(Bandwidth.valueOf(bw));
                        mUiThread.post(() -> {
                            mMaxBandWidth.setValue(bw);
                            mMaxBandWidth.setSummary(mMaxBandWidth.getEntry());
                            Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                        });
                    } catch (EmException e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_MAX_BITRATE:
                    try {
                        final Bitrate br = volteUeApi.getMaxBitRate();
                        mUiThread.post(() -> {
                            mMaxBitRate.setValue(br.name());
                            mMaxBitRate.setSummary(mMaxBitRate.getEntry());
                        });
                    } catch (EmException e) {
                        e.printStackTrace();
                        mUiThread.post(() -> {
                            mMaxBitRate.setEnabled(false);
                            mMaxBitRate.setSummary(R.string.feature_abnormal);
                        });
                    }
                    break;
                case MSG_SET_MAX_BITRATE:
                    try {
                        String br = (String) msg.obj;
                        volteUeApi.setMaxBitRate(Bitrate.valueOf(br));
                        mUiThread.post(() -> {
                            mMaxBitRate.setValue(br);
                            mMaxBitRate.setSummary(mMaxBitRate.getEntry());
                            Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                        });
                    } catch (EmException e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_SMS_PDU:
                    try {
                        final SmsPdu sp = volteUeApi.getSmsPdu();
                        mUiThread.post(() -> {
                            mSmsPduformat.setValue(sp.name());
                            mSmsPduformat.setSummary(mSmsPduformat.getEntry());
                        });
                    } catch (EmException e) {
                        e.printStackTrace();
                        mUiThread.post(() -> {
                            mSmsPduformat.setEnabled(false);
                            mSmsPduformat.setSummary(R.string.feature_abnormal);
                        });
                    }
                    break;
                case MSG_SET_SMS_PDU:
                    try {
                        String sp = (String) msg.obj;
                        volteUeApi.setSmsPdu(SmsPdu.valueOf(sp));
                        mUiThread.post(() -> {
                            mSmsPduformat.setValue(sp);
                            mSmsPduformat.setSummary(mSmsPduformat.getEntry());
                            Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                        });
                    } catch (EmException e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_SPRD_VOLTE:
                    try {
                        String volte = teleApi.volteUeSettings().getSprdVolte();
                        Log.d(TAG, "get volte " + volte);
                        mUiThread.post(() -> {
                            if (!volte.equals("")) {
                                mSprdVolte.setText(volte);
                                mSprdVolte.setSummary(volte);
                            }
                        });
                    } catch (EmException e) {
                        e.printStackTrace();
                        mUiThread.post(() -> mSprdVolte.setSummary(""));
                    }
                    break;
                case MSG_SET_SPRD_VOLTE:
                    Bundle bundle = (Bundle) msg.obj;
                    final String currentValue = bundle.getString("old");
                    String newValue = bundle.getString("new");
                    Log.d(TAG, String.format("MSG_SET_SPRD_VOLTE current=%s, new=%s", currentValue, newValue));
                    try {
                        volteUeApi.setSprdVolte(newValue);
                        mUiThread.post(() -> {
                            mSprdVolte.setText(newValue);
                            mSprdVolte.setSummary(newValue);
                        });
                    } catch (EmException e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_VIDEO_CALL_ENABLE:
                    try {
                        boolean enabled = volteUeApi.getVideoCallState();
                        mUiThread.post(() -> mVideoCallEnable.setChecked(enabled));
                    } catch (EmException e) {
                        e.printStackTrace();
                        mUiThread.post(() -> {
                            mVideoCallEnable.setEnabled(false);
                            mVideoCallEnable.setSummary(R.string.feature_abnormal);
                        });
                    }
                    break;
                case MSG_SET_VIDEO_CALL_ENABLE:
                    try {
                        boolean state = (boolean) msg.obj;
                        volteUeApi.setVideoCallState(state);
                        mUiThread.post(() -> mVideoCallEnable.setChecked(state));
                    } catch (EmException e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_VIDEO_CONFERENCE_ENABLE:
                    try {
                        boolean enabled = volteUeApi.getVideoConferenceState();
                        mUiThread.post(() -> mVideoConferenceEnable.setChecked(enabled));
                    } catch (EmException e) {
                        e.printStackTrace();
                        mUiThread.post(() -> {
                            mVideoConferenceEnable.setEnabled(false);
                            mVideoConferenceEnable.setSummary(R.string.feature_abnormal);
                        });
                    }
                    break;
                case MSG_SET_VIDEO_CONFERENCE_ENABLE:
                    try {
                        boolean state = (boolean) msg.obj;
                        volteUeApi.setVideoConferenceState(state);
                        mUiThread.post(() -> mVideoConferenceEnable.setChecked(state));
                    } catch (EmException e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                default:
                    break;
            }
        }

        private void showFailToast() {
            mUiThread.post(() -> Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show());
        }
    }
}
