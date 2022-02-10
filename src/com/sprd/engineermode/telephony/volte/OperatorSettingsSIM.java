
package com.sprd.engineermode.telephony.volte;

import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.TwoStatePreference;
import android.preference.ListPreference;
import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import android.util.Log;
import android.widget.Toast;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Message;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.CheckBox;
import android.view.View;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.preference.EditTextPreference;
import android.widget.CompoundButton;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import com.sprd.engineermode.EMClickEditTextPreference;
import com.sprd.engineermode.R;

public class OperatorSettingsSIM extends PreferenceActivity implements
        Preference.OnPreferenceChangeListener,
        Preference.OnPreferenceClickListener,
        OnSharedPreferenceChangeListener {
    private static final String TAG = "OperatorSettingsSIM";
    private static final String KEY_IPSEC_SWITCH = "ipsec_enable";
    private static final String KEY_REG_SUBSCRIBE = "reg_subscribe_enable";
    private static final String KEY_MWI_ENABLE = "mwi_enable";
    private static final String KEY_SIP_HEADER_COMPACT = "sip_header_compact";
    private static final String KEY_SMS_OVER_IP = "sms_over_ip_enable";
    private static final String KEY_USSI_ENABLE = "ussi_enable";
    private static final String KEY_INITIAL_REGISTER_PANI = "initial_register_pani";
    private static final String KEY_INTEGRITY_ALGORITHM = "Integrity_algorithm";
    private static final String KEY_CIPHER_ALGORITHM = "cipher_algorithm";
    private static final String KEY_REG_SUBSCRIB_EXPIRE = "reg_subscrib_expire";
    private static final String KEY_MWI_SUBSCRIBE_EXPIRE = "mwi_subscribe_expire";
    private static final String KEY_MTU = "mtu";
    private static final String KEY_PCSCF_PORT = "pcscf_port";
    private static final String KEY_REG_EXPIRE = "register_expire";
    private static final String KEY_TREG_TIMER = "treg_timer";
    private static final String KEY_TEMERGREG_TIMER = "temergreg_timer";
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
    private static final String KEY_MO_AMR_OA_MODE = "mo_amr_oa_mode";
    private static final String KEY_NAMED_TEL_EVENT_ENABLE = "named_tel_event_enable";
    private static final String KEY_AUDIO_RTP_TIMEOUT = "audio_rtp_timeout_timer";
    private static final String KEY_AUDIO_RTCP_TIME_INTERNAL = "audio_rtcp_time_internal";
    private static final String KEY_AUDIO_RS = "audio_rs";
    private static final String KEY_AUDIO_RR = "audio_rr";
    private static final String KEY_VIDEO_CODEC_TYPE = "video_codec_type";
    private static final String KEY_VIDEO_RS = "video_rs";
    private static final String KEY_VIDEO_RR = "video_rr";
    private static final String KEY_AUDIO_DYNAMIC_ENABLE = "audio_dynamic_resolution";
    private static final String KEY_VIDEO_DYNAMIC_ENABLE = "video_dynamic_resolution";
    private static final String KEY_RELIABLE_180_ENABLE = "reliable_180_enable";
    private static final String KEY_DIRECT_ALERTING_ENABLE = "direct_alerting_enable";
    private static final String KEY_RESOURCE_ALWAYS_READY = "resource_always_ready";
    private static final String KEY_PRECONDITION_ENABLE = "precondition_enable";
    private static final String KEY_VIDEO_UPGRADE_PRECONDITION = "video_upgrade_precondition";
    private static final String KEY_VIDEO_EARLY_MEDIA = "video_early_media";
    private static final String KEY_ALERTING = "alerting";
    private static final String KEY_MID_CALL = "mid_call";
    private static final String KEY_PER_ALERTING = "pre_alerting";
    private static final String KEY_RSRVCC_SUPPORT = "rsrvcc_support";
    private static final String KEY_RSRVCC_ALERTING = "rsrvcc_alerting";
    private static final String KEY_RSRVCC_MID_CALL = "rsrvcc_mid_call";
    private static final String KEY_TCALL_TIMER = "tcall_timer";
    private static final String KEY_RINGRING_TIMER = "ringing_timer";
    private static final String KEY_RINGBACK_TIMER = "ringback_timer";
    private static final String KEY_NO_ANSWER_TIMER = "no_answer_timer";
    private static final String KEY_SESSION_TIMER = "session_timer";
    private static final String KEY_MT_SESSION_TIMER = "mt_session_timer";
    private static final String KEY_MIN_SE_TIMER = "min_se_timer";
    private static final String KEY_CONFERENCE_URI = "conference_urI";
    private static final String KEY_NATIONAL_URL_FORMAT = "national_url_format";
    private static final String KEY_INTERNATIONAL_URL_FORMAT = "international_url_format";
    private static final String KEY_SESSION_TIMER_REFRESHER = "session_timer_refresher";
    private static final String KEY_MT_SESSION_TIMER_REFRESHER = "mt_session_timer_refresher";
    private static final String KEY_VIDEO_AUDIO_MERGE = "video_audio_merge";
    private static final String KEY_BSF_URI = "bsf_uri";
    private static final String KEY_BSF_PORT = "bsf_port";
    private static final String KEY_XCAP_URI = "xcap_uri";
    private static final String KEY_XCAP_PORT = "xcap_port";
    private static final String KEY_XCAP_AUID = "xcap_auid";
    private static final String KEY_LOCAL_CALL_WAITING = "local_call_waiting";
    private static final String KEY_DNS_SRV_ENABLE = "dns_srv_enable";
    private static final String KEY_HTTPS_ENABLE = "https_enable";
    private static final String KEY_ACTIVATE_CFNL_ENABLE = "activate_cfnl_enable";
    private static final String KEY_HTTP_PUT_MEDIA = "http_put_media";
    private static final String KEY_IP_PRIORITY = "ip_priority";
    private static final String KEY_VIDEO_MAX_RESOLUTION = "video_max_resolution";
    public static final String KEY_CHANNEL = "key_channel";

    private static final int MSG_GET_IPSEC_STATE = 0;
    private static final int MSG_SET_IPSEC_STATE = 1;
    private static final int MSG_GET_INTEGRITY_ALGORITHM = 7;
    private static final int MSG_SET_INTEGRITY_ALGORITHM = 8;
    private static final int MSG_GET_CIPHER_ALGORITHM = 9;
    private static final int MSG_SET_CIPHER_ALGORITHM = 10;
    private static final int MSG_GET_REG_SUBSCRIBE_ENABLE = 11;
    private static final int MSG_SET_REG_SUBSCRIBE_ENABLE = 12;
    private static final int MSG_GET_MWI_ENABLE = 13;
    private static final int MSG_SET_MWI_ENABLE = 14;
    private static final int MSG_GET_SIP_HEADER_COMPACT = 17;
    private static final int MSG_SET_SIP_HEADER_COMPACT = 18;
    private static final int MSG_GET_SMS_OVER_IP_ENABLE = 19;
    private static final int MSG_SET_SMS_OVER_IP_ENABLE = 20;
    private static final int MSG_GET_USSI_ENABLE = 21;
    private static final int MSG_SET_USSI_ENABLE = 22;
    private static final int MSG_GET_INITIAL_REGISTER_PANI = 23;
    private static final int MSG_SET_INITIAL_REGISTER_PANI = 24;
    private static final int MSG_GET_REG_SUBSCRIBE_EXPIRE = 25;
    private static final int MSG_SET_REG_SUBSCRIBE_EXPIRE = 26;
    private static final int MSG_GET_MWI_SUBSCRIBE_EXPIRE = 27;
    private static final int MSG_SET_MWI_SUBSCRIBE_EXPIRE = 28;
    private static final int MSG_GET_MTU = 29;
    private static final int MSG_SET_MTU = 30;
    private static final int MSG_GET_PCSCFPORT = 31;
    private static final int MSG_SET_PCSCFPORT = 32;
    private static final int MSG_GET_REG_EXPIRE = 33;
    private static final int MSG_SET_REG_EXPIRE = 34;
    private static final int MSG_GET_TREG_TIMER = 35;
    private static final int MSG_SET_TREG_TIMER = 36;
    private static final int MSG_GET_TEMERGREG_TIMER = 37;
    private static final int MSG_SET_TEMERGREG_TIMER = 38;
    private static final int MSG_GET_VOICE_CODE_TYPE = 45;
    private static final int MSG_SET_VOICE_CODE_TYPE = 46;
    private static final int MSG_GET_MIN_BANDWIDTH = 47;
    private static final int MSG_SET_MIN_BANDWIDTH = 48;
    private static final int MSG_GET_MAX_BANDWIDTH = 49;
    private static final int MSG_SET_MAX_BANDWIDTH = 50;
    private static final int MSG_GET_MIN_BITRATE = 51;
    private static final int MSG_SET_MIN_BITRATE = 52;
    private static final int MSG_GET_MAX_BITRATE = 53;
    private static final int MSG_SET_MAX_BITRATE = 54;
    private static final int MSG_GET_DEFAULT_BITRATE = 55;
    private static final int MSG_SET_DEFAULT_BITRATE = 56;
    private static final int MSG_GET_EVS_DEFAULT_BITRATE = 57;
    private static final int MSG_SET_EVS_DEFAULT_BITRATE = 58;
    private static final int MSG_GET_WB_DEFAULT_BITRATE = 59;
    private static final int MSG_SET_WB_DEFAULT_BITRATE = 60;
    private static final int MSG_GET_NB_DEFAULT_BITRATE = 61;
    private static final int MSG_SET_NB_DEFAULT_BITRATE = 62;
    private static final int MSG_GET_CHANNEL_AWARE_MODE = 63;
    private static final int MSG_SET_CHANNEL_AWARE_MODE = 64;
    private static final int MSG_GET_WB_RATE_SET = 65;
    private static final int MSG_SET_WB_RATE_SET = 66;
    private static final int MSG_GET_NB_RATE_SET = 67;
    private static final int MSG_SET_NB_RATE_SET = 68;
    private static final int MSG_GET_EVS_RATE_SET = 69;
    private static final int MSG_SET_EVS_RATE_SET = 70;
    private static final int MSG_GET_MO_AMR_OA = 71;
    private static final int MSG_SET_MO_AMR_OA = 72;
    private static final int MSG_GET_NAMED_TEL_EVENT = 73;
    private static final int MSG_SET_NAMED_TEL_EVENT = 74;
    private static final int MSG_GET_AUDIO_RTP_TIMEOUT = 75;
    private static final int MSG_SET_AUDIO_RTP_TIMEOUT = 76;
    private static final int MSG_GET_AUDIO_RTCP_TIME = 77;
    private static final int MSG_SET_AUDIO_RTCP_TIME = 78;
    private static final int MSG_GET_AUDIO_RS = 79;
    private static final int MSG_SET_AUDIO_RS = 80;
    private static final int MSG_GET_AUDIO_RR = 81;
    private static final int MSG_SET_AUDIO_RR = 82;
    private static final int MSG_GET_VIDEO_RS = 83;
    private static final int MSG_SET_VIDEO_RS = 84;
    private static final int MSG_GET_VIDEO_RR = 85;
    private static final int MSG_SET_VIDEO_RR = 86;
    private static final int MSG_GET_TCALL_TIMER = 87;
    private static final int MSG_SET_TCALL_TIMER = 88;
    private static final int MSG_GET_RINGRING_TIMER = 89;
    private static final int MSG_SET_RINGRING_TIMER = 90;
    private static final int MSG_GET_RINGBACK_TIMER = 91;
    private static final int MSG_SET_RINGBACK_TIMER = 92;
    private static final int MSG_GET_NO_ANSWER_TIMER = 93;
    private static final int MSG_SET_NO_ANSWER_TIMER = 94;
    private static final int MSG_GET_SESSION_TIMER = 95;
    private static final int MSG_SET_SESSION_TIMER = 96;
    private static final int MSG_GET_MT_SESSION_TIMER = 97;
    private static final int MSG_SET_MT_SESSION_TIMER = 98;
    private static final int MSG_GET_CONFERENCE_URI = 99;
    private static final int MSG_SET_CONFERENCE_URI = 100;
    private static final int MSG_GET_VIDEO_CODE_TYPE = 101;
    private static final int MSG_SET_VIDEO_CODE_TYPE = 102;
    private static final int MSG_GET_RELIABLE_180 = 103;
    private static final int MSG_SET_RELIABLE_180 = 104;
    private static final int MSG_GET_DIRECT_ALERTING = 105;
    private static final int MSG_SET_DIRECT_ALERTING = 106;
    private static final int MSG_GET_RESOURCE_ALWAYS = 107;
    private static final int MSG_SET_RESOURCE_ALWAYS = 108;
    private static final int MSG_GET_PRECONDITION = 109;
    private static final int MSG_SET_PRECONDITION = 110;
    private static final int MSG_GET_VIDEO_UPGRADE = 111;
    private static final int MSG_SET_VIDEO_UPGRADE = 112;
    private static final int MSG_GET_VIDEO_EARLY_MEDIA = 113;
    private static final int MSG_SET_VIDEO_EARLY_MEDIA = 114;
    private static final int MSG_GET_ALERTING = 115;
    private static final int MSG_SET_ALERTING = 116;
    private static final int MSG_GET_MID_CALL = 117;
    private static final int MSG_SET_MID_CALL = 118;
    private static final int MSG_GET_PER_ALERTING = 119;
    private static final int MSG_SET_PER_ALERTING = 120;
    private static final int MSG_GET_RSRVCC_SUPPORT = 121;
    private static final int MSG_SET_RSRVCC_SUPPORT = 122;
    private static final int MSG_GET_RSRVCC_ALERTING = 123;
    private static final int MSG_SET_RSRVCC_ALERTING = 124;
    private static final int MSG_GET_RSRVCC_MID_CALL = 125;
    private static final int MSG_SET_RSRVCC_MID_CALL = 126;
    private static final int MSG_GET_NATIONAL_URL_FORMAT = 127;
    private static final int MSG_SET_NATIONAL_URL_FORMAT = 128;
    private static final int MSG_GET_INTERNATIONAL_URL = 129;
    private static final int MSG_SET_INTERNATIONAL_URL = 130;
    private static final int MSG_GET_SESSION_TIMER_REFRESHER = 131;
    private static final int MSG_SET_SESSION_TIMER_REFRESHER = 132;
    private static final int MSG_GET_MT_SESSION_REFRESHER = 133;
    private static final int MSG_SET_MT_SESSION_REFRESHER = 134;
    private static final int MSG_GET_VIDEO_AUDIO_MERGE = 135;
    private static final int MSG_SET_VIDEO_AUDIO_MERGE = 136;
    private static final int MSG_GET_AUDIO_DYNAMIC_ENABLE = 137;
    private static final int MSG_SET_AUDIO_DYNAMIC_ENABLE = 138;
    private static final int MSG_GET_VIDEO_DYNAMIC_ENABLE = 139;
    private static final int MSG_SET_VIDEO_DYNAMIC_ENABLE = 140;
    private static final int MSG_GET_BSF_URI = 141;
    private static final int MSG_SET_BSF_URI = 142;
    private static final int MSG_GET_BSF_PORT = 143;
    private static final int MSG_SET_BSF_PORT = 144;
    private static final int MSG_GET_XCAP_URI = 145;
    private static final int MSG_SET_XCAP_URI = 146;
    private static final int MSG_GET_XCAP_PORT = 147;
    private static final int MSG_SET_XCAP_PORT = 148;
    private static final int MSG_GET_XCAP_AUID = 149;
    private static final int MSG_SET_XCAP_AUID = 150;
    private static final int MSG_GET_LOCAL_CALL = 151;
    private static final int MSG_SET_LOCAL_CALL = 152;
    private static final int MSG_GET_DNS_SRV_ENABLE = 153;
    private static final int MSG_SET_DNS_SRV_ENABLE = 154;
    private static final int MSG_GET_HTTPS_ENABLE = 155;
    private static final int MSG_SET_HTTPS_ENABLE = 156;
    private static final int MSG_GET_ACTIVATE_CFNL = 157;
    private static final int MSG_SET_ACTIVATE_CFNL = 158;
    private static final int MSG_GET_HTTP_PUT_MEDIA = 159;
    private static final int MSG_SET_HTTP_PUT_MEDIA = 160;
    private static final int MSG_GET_IP_PRIORITY = 161;
    private static final int MSG_SET_IP_PRIORITY = 162;
    private static final int MSG_GET_VIDEO_MAX_RESOLUTION = 163;
    private static final int MSG_SET_VIDEO_MAX_RESOLUTION = 164;
    private static final int MSG_GET_MIN_SE_TIMER = 165;
    private static final int MSG_SET_MIN_SE_TIMER = 166;

    private Handler mUiThread = new Handler();
    private IpsecHandler mIpsecHandler;
    private String mResp = null;
    private SharedPreferences mSharePref;
    private Context mContext = null;
    private ArrayList<CheckBox> mVolteViewList;
    private TwoStatePreference mIPsecSwitch, mRegSubscribeEnable, mMWIEnable,
            mSIPHeaderCompactForm, mSMSoverIPEnable, mUSSIEnable, mInitialRegisterPANI, mNamedTelEvent,
            mReliable180Switch, mDirectAlertingSwitch, mResourceAlwaysSwitch, mPreconditionSwitch,
            mVideoUpgradeSwitch,mVideoEarlySwitch, mAlertingSwitch, mMidCallSwitch, mPreAlertingSwitch,
            mRSRVCCSupportSwitch, mRSRVCCAlertSwitch, mRSRVCCMidSwitch, mVideoDynamicEnable, mAudioDynamicEnable,
            mLocalCallSwitch, mDnsSrvEnableSwitch, mHttpsEnableSwitch, mActivateCfnlSwitch, mHttpPutMediaSwitch;
    private Preference mIntegrityAlgorithm, mCipherAlgorithm;
    private EditTextPreference mRegSubscribExpire, mMwiSubscribExpire, mMTU, mPcscfPort, mRegExpire, mTregTimer, mTemergregTimer;
    private EditTextPreference mAudioRTPTimeout, mAudioRTCPTimeInterval, mAudioRS, mAudioRR, mVideoRs, mVideoRR,
            mTcallTimer, mRingingTimer, mRingbackTimer, mNoAnswerTimer, mSessionTimer, mMTSessionTimer, mMinSeTimer,
            mBsfPort, mXcapPort, mXcapAuid;
    private AlertDialog mAlertDialog;
    private EMClickEditTextPreference mConferenceURI, mBsfURI, mXcapURI;
    private Preference mVolteVoiceCode, mVolteVideoCode, mEvsRateSet, mWbRateSet, mNbRateSet;
    private ListPreference mMinBandWidth, mMaxBandWidth, mMinBitRate, mMaxBitRate, mDefaultBitRate, mMoAmrOaMode;
    private ListPreference mEvsDefaultBR, mWbDefaultBR, mNbDefaultBR, mChannelAwareMode;
    private ListPreference mNationalURL, mVideoMaxResolution, mInternationalURL, mSessionTimerRefresher, mMTSessionTimerRefresher, mVideoAudioMerge;
    private ListPreference mIPPriority;

    private int mSim;

    private static ITelephonyApi teleApi = CoreApi.getTelephonyApi();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSim = getIntent().getIntExtra(KEY_CHANNEL, 0);
        if (mSim == 0) {
            addPreferencesFromResource(R.xml.pref_volte_operator_settings0);
        } else {
            addPreferencesFromResource(R.xml.pref_volte_operator_settings1);
        }
        mContext = this;

        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mIpsecHandler = new IpsecHandler(ht.getLooper());
        mSharePref = PreferenceManager.getDefaultSharedPreferences(this);
        mSharePref.registerOnSharedPreferenceChangeListener(this);

        mIPsecSwitch = (TwoStatePreference) findPreference(KEY_IPSEC_SWITCH + mSim);
        mIPsecSwitch.setOnPreferenceChangeListener(this);
        mRegSubscribeEnable = (TwoStatePreference) findPreference(KEY_REG_SUBSCRIBE + mSim);
        mRegSubscribeEnable.setOnPreferenceChangeListener(this);
        mMWIEnable = (TwoStatePreference) findPreference(KEY_MWI_ENABLE + mSim);
        mMWIEnable.setOnPreferenceChangeListener(this);
        mSIPHeaderCompactForm = (TwoStatePreference) findPreference(KEY_SIP_HEADER_COMPACT + mSim);
        mSIPHeaderCompactForm.setOnPreferenceChangeListener(this);
        mSMSoverIPEnable = (TwoStatePreference) findPreference(KEY_SMS_OVER_IP + mSim);
        mSMSoverIPEnable.setOnPreferenceChangeListener(this);
        mUSSIEnable = (TwoStatePreference) findPreference(KEY_USSI_ENABLE + mSim);
        mUSSIEnable.setOnPreferenceChangeListener(this);
        mInitialRegisterPANI = (TwoStatePreference) findPreference(KEY_INITIAL_REGISTER_PANI + mSim);
        mInitialRegisterPANI.setOnPreferenceChangeListener(this);
        mNamedTelEvent = (TwoStatePreference) findPreference(KEY_NAMED_TEL_EVENT_ENABLE + mSim);
        mNamedTelEvent.setOnPreferenceChangeListener(this);

        mIntegrityAlgorithm = (Preference) findPreference(KEY_INTEGRITY_ALGORITHM + mSim);
        mIntegrityAlgorithm.setOnPreferenceClickListener(this);
        mCipherAlgorithm = (Preference) findPreference(KEY_CIPHER_ALGORITHM + mSim);
        mCipherAlgorithm.setOnPreferenceClickListener(this);

        mRegSubscribExpire = (EditTextPreference) findPreference(KEY_REG_SUBSCRIB_EXPIRE + mSim);
        mRegSubscribExpire.setOnPreferenceChangeListener(this);
        mMwiSubscribExpire = (EditTextPreference) findPreference(KEY_MWI_SUBSCRIBE_EXPIRE + mSim);
        mMwiSubscribExpire.setOnPreferenceChangeListener(this);
        mMTU = (EditTextPreference) findPreference(KEY_MTU + mSim);
        mMTU.setOnPreferenceChangeListener(this);
        mPcscfPort = (EditTextPreference) findPreference(KEY_PCSCF_PORT + mSim);
        mPcscfPort.setOnPreferenceChangeListener(this);
        mRegExpire = (EditTextPreference) findPreference(KEY_REG_EXPIRE + mSim);
        mRegExpire.setOnPreferenceChangeListener(this);
        mTregTimer = (EditTextPreference) findPreference(KEY_TREG_TIMER + mSim);
        mTregTimer.setOnPreferenceChangeListener(this);
        mTemergregTimer = (EditTextPreference) findPreference(KEY_TEMERGREG_TIMER + mSim);
        mTemergregTimer.setOnPreferenceChangeListener(this);

        //Call Settings (SIM 0/SIM 1) -> Audio Codec Settings
        mVolteVoiceCode = (Preference) findPreference(KEY_VOICE_CODE + mSim);
        mVolteVoiceCode.setOnPreferenceClickListener(this);
        mMinBandWidth = (ListPreference) findPreference(KEY_MIN_BANDWIDTH + mSim);
        mMinBandWidth.setOnPreferenceChangeListener(this);
        mMaxBandWidth = (ListPreference) findPreference(KEY_MAX_BANDWIDTH + mSim);
        mMaxBandWidth.setOnPreferenceChangeListener(this);
        mMinBitRate = (ListPreference) findPreference(KEY_MIN_BITRATE + mSim);
        mMinBitRate.setOnPreferenceChangeListener(this);
        mMaxBitRate = (ListPreference) findPreference(KEY_MAX_BITRATE + mSim);
        mMaxBitRate.setOnPreferenceChangeListener(this);
        mDefaultBitRate = (ListPreference) findPreference(KEY_DEFAULT_BITRATE + mSim);
        mDefaultBitRate.setOnPreferenceChangeListener(this);
        mEvsDefaultBR = (ListPreference) findPreference(KEY_EVS_DEFAULT_BITRATE + mSim);
        mEvsDefaultBR.setOnPreferenceChangeListener(this);
        mWbDefaultBR = (ListPreference) findPreference(KEY_WB_DEFAULT_BITRATE + mSim);
        mWbDefaultBR.setOnPreferenceChangeListener(this);
        mNbDefaultBR = (ListPreference) findPreference(KEY_NB_DEFAULT_BITRATE + mSim);
        mNbDefaultBR.setOnPreferenceChangeListener(this);
        mChannelAwareMode = (ListPreference) findPreference(KEY_CHANNEL_AWARE_MODE + mSim);
        mChannelAwareMode.setOnPreferenceChangeListener(this);
        mEvsRateSet = (Preference) findPreference(KEY_EVS_RATE_SET + mSim);
        mEvsRateSet.setOnPreferenceClickListener(this);
        mWbRateSet = (Preference) findPreference(KEY_WB_RATE_SET + mSim);
        mWbRateSet.setOnPreferenceClickListener(this);
        mNbRateSet = (Preference) findPreference(KEY_NB_RATE_SET + mSim);
        mNbRateSet.setOnPreferenceClickListener(this);
        mMoAmrOaMode = (ListPreference) findPreference(KEY_MO_AMR_OA_MODE + mSim);
        mMoAmrOaMode.setOnPreferenceChangeListener(this);

        //Call Settings (SIM 0/SIM 1)
        mAudioRTPTimeout = (EditTextPreference) findPreference(KEY_AUDIO_RTP_TIMEOUT + mSim);
        mAudioRTPTimeout.setOnPreferenceChangeListener(this);
        mAudioRTCPTimeInterval = (EditTextPreference) findPreference(KEY_AUDIO_RTCP_TIME_INTERNAL + mSim);
        mAudioRTCPTimeInterval.setOnPreferenceChangeListener(this);
        mAudioRS = (EditTextPreference) findPreference(KEY_AUDIO_RS + mSim);
        mAudioRS.setOnPreferenceChangeListener(this);
        mAudioRR = (EditTextPreference) findPreference(KEY_AUDIO_RR + mSim);
        mAudioRR.setOnPreferenceChangeListener(this);
        mVideoRs = (EditTextPreference) findPreference(KEY_VIDEO_RS + mSim);
        mVideoRs.setOnPreferenceChangeListener(this);
        mVideoRR = (EditTextPreference) findPreference(KEY_VIDEO_RR + mSim);
        mVideoRR.setOnPreferenceChangeListener(this);
        mVideoDynamicEnable = (TwoStatePreference) findPreference(KEY_VIDEO_DYNAMIC_ENABLE + mSim);
        mVideoDynamicEnable.setOnPreferenceChangeListener(this);
        mAudioDynamicEnable = (TwoStatePreference) findPreference(KEY_AUDIO_DYNAMIC_ENABLE + mSim);
        mAudioDynamicEnable.setOnPreferenceChangeListener(this);
        mTcallTimer = (EditTextPreference) findPreference(KEY_TCALL_TIMER + mSim);
        mTcallTimer.setOnPreferenceChangeListener(this);
        mRingingTimer = (EditTextPreference) findPreference(KEY_RINGRING_TIMER + mSim);
        mRingingTimer.setOnPreferenceChangeListener(this);
        mRingbackTimer = (EditTextPreference) findPreference(KEY_RINGBACK_TIMER + mSim);
        mRingbackTimer.setOnPreferenceChangeListener(this);
        mNoAnswerTimer = (EditTextPreference) findPreference(KEY_NO_ANSWER_TIMER + mSim);
        mNoAnswerTimer.setOnPreferenceChangeListener(this);
        mSessionTimer = (EditTextPreference) findPreference(KEY_SESSION_TIMER + mSim);
        mSessionTimer.setOnPreferenceChangeListener(this);
        //UNISOC: add for bug1382482
        mMinSeTimer = (EditTextPreference) findPreference(KEY_MIN_SE_TIMER + mSim);
        mMinSeTimer.setOnPreferenceChangeListener(this);
        mMTSessionTimer = (EditTextPreference) findPreference(KEY_MT_SESSION_TIMER + mSim);
        mMTSessionTimer.setOnPreferenceChangeListener(this);
        mConferenceURI = (EMClickEditTextPreference) findPreference(KEY_CONFERENCE_URI + mSim);
        mConferenceURI.setOnPreferenceChangeListener(this);
        mVolteVideoCode = (Preference) findPreference(KEY_VIDEO_CODEC_TYPE + mSim);
        mVolteVideoCode.setOnPreferenceClickListener(this);
        mVideoMaxResolution = (ListPreference) findPreference(KEY_VIDEO_MAX_RESOLUTION + mSim);
        mVideoMaxResolution.setOnPreferenceChangeListener(this);
        mReliable180Switch = (TwoStatePreference) findPreference(KEY_RELIABLE_180_ENABLE + mSim);
        mReliable180Switch.setOnPreferenceChangeListener(this);
        mDirectAlertingSwitch = (TwoStatePreference) findPreference(KEY_DIRECT_ALERTING_ENABLE + mSim);
        mDirectAlertingSwitch.setOnPreferenceChangeListener(this);
        mResourceAlwaysSwitch = (TwoStatePreference) findPreference(KEY_RESOURCE_ALWAYS_READY + mSim);
        mResourceAlwaysSwitch.setOnPreferenceChangeListener(this);
        mPreconditionSwitch = (TwoStatePreference) findPreference(KEY_PRECONDITION_ENABLE + mSim);
        mPreconditionSwitch.setOnPreferenceChangeListener(this);
        mVideoUpgradeSwitch = (TwoStatePreference) findPreference(KEY_VIDEO_UPGRADE_PRECONDITION + mSim);
        mVideoUpgradeSwitch.setOnPreferenceChangeListener(this);
        mVideoEarlySwitch = (TwoStatePreference) findPreference(KEY_VIDEO_EARLY_MEDIA + mSim);
        mVideoEarlySwitch.setOnPreferenceChangeListener(this);
        mAlertingSwitch = (TwoStatePreference) findPreference(KEY_ALERTING + mSim);
        mAlertingSwitch.setOnPreferenceChangeListener(this);
        mMidCallSwitch = (TwoStatePreference) findPreference(KEY_MID_CALL + mSim);
        mMidCallSwitch.setOnPreferenceChangeListener(this);
        mPreAlertingSwitch = (TwoStatePreference) findPreference(KEY_PER_ALERTING + mSim);
        mPreAlertingSwitch.setOnPreferenceChangeListener(this);
        mRSRVCCSupportSwitch = (TwoStatePreference) findPreference(KEY_RSRVCC_SUPPORT + mSim);
        mRSRVCCSupportSwitch.setOnPreferenceChangeListener(this);
        mRSRVCCAlertSwitch = (TwoStatePreference) findPreference(KEY_RSRVCC_ALERTING + mSim);
        mRSRVCCAlertSwitch.setOnPreferenceChangeListener(this);
        mRSRVCCMidSwitch = (TwoStatePreference) findPreference(KEY_RSRVCC_MID_CALL + mSim);
        mRSRVCCMidSwitch.setOnPreferenceChangeListener(this);

        mNationalURL = (ListPreference) findPreference(KEY_NATIONAL_URL_FORMAT + mSim);
        mNationalURL.setOnPreferenceChangeListener(this);
        mInternationalURL = (ListPreference) findPreference(KEY_INTERNATIONAL_URL_FORMAT + mSim);
        mInternationalURL.setOnPreferenceChangeListener(this);
        mSessionTimerRefresher = (ListPreference) findPreference(KEY_SESSION_TIMER_REFRESHER + mSim);
        mSessionTimerRefresher.setOnPreferenceChangeListener(this);
        mMTSessionTimerRefresher = (ListPreference) findPreference(KEY_MT_SESSION_TIMER_REFRESHER + mSim);
        mMTSessionTimerRefresher.setOnPreferenceChangeListener(this);
        mVideoAudioMerge = (ListPreference) findPreference(KEY_VIDEO_AUDIO_MERGE + mSim);
        mVideoAudioMerge.setOnPreferenceChangeListener(this);

        //Call Settings (SIM 0/SIM 1) : Supplementary Service Settings
        mBsfURI = (EMClickEditTextPreference) findPreference(KEY_BSF_URI + mSim);
        mBsfURI.setOnPreferenceChangeListener(this);
        mBsfPort = (EditTextPreference) findPreference(KEY_BSF_PORT + mSim);
        mBsfPort.setOnPreferenceChangeListener(this);
        mXcapURI = (EMClickEditTextPreference) findPreference(KEY_XCAP_URI + mSim);
        mXcapURI.setOnPreferenceChangeListener(this);
        mXcapPort = (EditTextPreference) findPreference(KEY_XCAP_PORT + mSim);
        mXcapPort.setOnPreferenceChangeListener(this);
        mXcapAuid = (EditTextPreference) findPreference(KEY_XCAP_AUID + mSim);
        mXcapAuid.setOnPreferenceChangeListener(this);
        mLocalCallSwitch = (TwoStatePreference) findPreference(KEY_LOCAL_CALL_WAITING + mSim);
        mLocalCallSwitch.setOnPreferenceChangeListener(this);
        mDnsSrvEnableSwitch = (TwoStatePreference) findPreference(KEY_DNS_SRV_ENABLE + mSim);
        mDnsSrvEnableSwitch.setOnPreferenceChangeListener(this);
        mHttpsEnableSwitch = (TwoStatePreference) findPreference(KEY_HTTPS_ENABLE + mSim);
        mHttpsEnableSwitch.setOnPreferenceChangeListener(this);
        mActivateCfnlSwitch = (TwoStatePreference) findPreference(KEY_ACTIVATE_CFNL_ENABLE + mSim);
        mActivateCfnlSwitch.setOnPreferenceChangeListener(this);
        mHttpPutMediaSwitch = (TwoStatePreference) findPreference(KEY_HTTP_PUT_MEDIA + mSim);
        mHttpPutMediaSwitch.setOnPreferenceChangeListener(this);

        mIPPriority = (ListPreference) findPreference(KEY_IP_PRIORITY + mSim);
        mIPPriority.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
    }

    @Override
    public void onStart() {
        if (mIPsecSwitch != null && mIPsecSwitch.isEnabled()) {
            Message getIpsec = mIpsecHandler.obtainMessage(MSG_GET_IPSEC_STATE);
            mIpsecHandler.sendMessage(getIpsec);
        }
        if (mRegSubscribeEnable != null && mRegSubscribeEnable.isEnabled()) {
            Message getRegSubscribe = mIpsecHandler.obtainMessage(MSG_GET_REG_SUBSCRIBE_ENABLE);
            mIpsecHandler.sendMessage(getRegSubscribe);
        }
        if (mMWIEnable != null && mMWIEnable.isEnabled()) {
            Message getMWI = mIpsecHandler.obtainMessage(MSG_GET_MWI_ENABLE);
            mIpsecHandler.sendMessage(getMWI);
        }
        if (mSIPHeaderCompactForm != null && mSIPHeaderCompactForm.isEnabled()) {
            Message getSIPHeaderCompact = mIpsecHandler.obtainMessage(MSG_GET_SIP_HEADER_COMPACT);
            mIpsecHandler.sendMessage(getSIPHeaderCompact);
        }
        if (mSMSoverIPEnable != null && mSMSoverIPEnable.isEnabled()) {
            Message getSMSoverIP = mIpsecHandler.obtainMessage(MSG_GET_SMS_OVER_IP_ENABLE);
            mIpsecHandler.sendMessage(getSMSoverIP);
        }
        if (mUSSIEnable != null && mUSSIEnable.isEnabled()) {
            Message getUSSI = mIpsecHandler.obtainMessage(MSG_GET_USSI_ENABLE);
            mIpsecHandler.sendMessage(getUSSI);
        }
        if (mInitialRegisterPANI != null && mInitialRegisterPANI.isEnabled()) {
            Message getInitialRegister = mIpsecHandler.obtainMessage(MSG_GET_INITIAL_REGISTER_PANI);
            mIpsecHandler.sendMessage(getInitialRegister);
        }
        if (mRegSubscribExpire != null && mRegSubscribExpire.isEnabled()) {
            Message getRegSubscribExpire = mIpsecHandler
                    .obtainMessage(MSG_GET_REG_SUBSCRIBE_EXPIRE);
            mIpsecHandler.sendMessage(getRegSubscribExpire);
        }
        if (mMwiSubscribExpire != null && mMwiSubscribExpire.isEnabled()) {
            Message getMwiSubscribExpire = mIpsecHandler
                    .obtainMessage(MSG_GET_MWI_SUBSCRIBE_EXPIRE);
            mIpsecHandler.sendMessage(getMwiSubscribExpire);
        }
        if (mMTU != null && mMTU.isEnabled()) {
            Message getMTU = mIpsecHandler
                    .obtainMessage(MSG_GET_MTU);
            mIpsecHandler.sendMessage(getMTU);
        }
        if (mPcscfPort != null && mPcscfPort.isEnabled()) {
            Message getPcscfPort = mIpsecHandler
                    .obtainMessage(MSG_GET_PCSCFPORT);
            mIpsecHandler.sendMessage(getPcscfPort);
        }
        if (mRegExpire != null && mRegExpire.isEnabled()) {
            Message getRegExpire = mIpsecHandler
                    .obtainMessage(MSG_GET_REG_EXPIRE);
            mIpsecHandler.sendMessage(getRegExpire);
        }
        if (mTregTimer != null && mTregTimer.isEnabled()) {
            Message getTregTimer = mIpsecHandler
                    .obtainMessage(MSG_GET_TREG_TIMER);
            mIpsecHandler.sendMessage(getTregTimer);
        }
        if (mTemergregTimer != null && mTemergregTimer.isEnabled()) {
            Message getTemergregTimer = mIpsecHandler
                    .obtainMessage(MSG_GET_TEMERGREG_TIMER);
            mIpsecHandler.sendMessage(getTemergregTimer);
        }
        if (mMinBandWidth != null && mMinBandWidth.isEnabled()) {
            Message getMinBandWidth = mIpsecHandler
                    .obtainMessage(MSG_GET_MIN_BANDWIDTH);
            mIpsecHandler.sendMessage(getMinBandWidth);
        }
        if (mMaxBandWidth != null && mMaxBandWidth.isEnabled()) {
            Message getMaxBandWidth = mIpsecHandler
                    .obtainMessage(MSG_GET_MAX_BANDWIDTH);
            mIpsecHandler.sendMessage(getMaxBandWidth);
        }
        if (mMinBitRate != null && mMinBitRate.isEnabled()) {
            Message getMinBitRate = mIpsecHandler
                    .obtainMessage(MSG_GET_MIN_BITRATE);
            mIpsecHandler.sendMessage(getMinBitRate);
        }
        if (mMaxBitRate != null && mMaxBitRate.isEnabled()) {
            Message getMaxBitRate = mIpsecHandler
                    .obtainMessage(MSG_GET_MAX_BITRATE);
            mIpsecHandler.sendMessage(getMaxBitRate);
        }
        if (mDefaultBitRate != null && mDefaultBitRate.isEnabled()) {
            Message getDefaultBitRate = mIpsecHandler
                    .obtainMessage(MSG_GET_DEFAULT_BITRATE);
            mIpsecHandler.sendMessage(getDefaultBitRate);
        }
        if (mEvsDefaultBR != null && mEvsDefaultBR.isEnabled()) {
            Message getEvsDefaultBr = mIpsecHandler
                    .obtainMessage(MSG_GET_EVS_DEFAULT_BITRATE);
            mIpsecHandler.sendMessage(getEvsDefaultBr);
        }
        if (mWbDefaultBR != null && mWbDefaultBR.isEnabled()) {
            Message getWbDefaultBr = mIpsecHandler
                    .obtainMessage(MSG_GET_WB_DEFAULT_BITRATE);
            mIpsecHandler.sendMessage(getWbDefaultBr);
        }
        if (mNbDefaultBR != null && mNbDefaultBR.isEnabled()) {
            Message getNbDefaultBr = mIpsecHandler
                    .obtainMessage(MSG_GET_NB_DEFAULT_BITRATE);
            mIpsecHandler.sendMessage(getNbDefaultBr);
        }
        if (mChannelAwareMode != null && mChannelAwareMode.isEnabled()) {
            Message getAwareMode = mIpsecHandler
                    .obtainMessage(MSG_GET_CHANNEL_AWARE_MODE);
            mIpsecHandler.sendMessage(getAwareMode);
        }
        if (mMoAmrOaMode != null && mMoAmrOaMode.isEnabled()) {
            Message getMoAmrOaMode = mIpsecHandler
                    .obtainMessage(MSG_GET_MO_AMR_OA);
            mIpsecHandler.sendMessage(getMoAmrOaMode);
        }
        if (mNamedTelEvent != null && mNamedTelEvent.isEnabled()) {
            Message getNamedTelEvent = mIpsecHandler.obtainMessage(MSG_GET_NAMED_TEL_EVENT);
            mIpsecHandler.sendMessage(getNamedTelEvent);
        }
        //Call Settings (SIM 0/SIM 1)
        if (mAudioRTPTimeout != null && mAudioRTPTimeout.isEnabled()) {
            Message getudioRTPTimeout = mIpsecHandler
                    .obtainMessage(MSG_GET_AUDIO_RTP_TIMEOUT);
            mIpsecHandler.sendMessage(getudioRTPTimeout);
        }
        if (mAudioRTCPTimeInterval != null && mAudioRTCPTimeInterval.isEnabled()) {
            Message getAudioRTCPTimeInterval = mIpsecHandler
                    .obtainMessage(MSG_GET_AUDIO_RTCP_TIME);
            mIpsecHandler.sendMessage(getAudioRTCPTimeInterval);
        }
        if (mAudioRS != null && mAudioRS.isEnabled()) {
            Message getAudioRS = mIpsecHandler
                    .obtainMessage(MSG_GET_AUDIO_RS);
            mIpsecHandler.sendMessage(getAudioRS);
        }
        if (mAudioRR != null && mAudioRR.isEnabled()) {
            Message getAudioRR = mIpsecHandler
                    .obtainMessage(MSG_GET_AUDIO_RR);
            mIpsecHandler.sendMessage(getAudioRR);
        }
        if (mVideoRs != null && mVideoRs.isEnabled()) {
            Message getVideoRs = mIpsecHandler
                    .obtainMessage(MSG_GET_VIDEO_RS);
            mIpsecHandler.sendMessage(getVideoRs);
        }
        if (mVideoRR != null && mVideoRR.isEnabled()) {
            Message getVideoRR = mIpsecHandler
                    .obtainMessage(MSG_GET_VIDEO_RR);
            mIpsecHandler.sendMessage(getVideoRR);
        }
        if (mTcallTimer != null && mTcallTimer.isEnabled()) {
            Message getTcallTimer = mIpsecHandler
                    .obtainMessage(MSG_GET_TCALL_TIMER);
            mIpsecHandler.sendMessage(getTcallTimer);
        }
        if (mRingingTimer != null && mRingingTimer.isEnabled()) {
            Message getRingingTimer = mIpsecHandler
                    .obtainMessage(MSG_GET_RINGRING_TIMER);
            mIpsecHandler.sendMessage(getRingingTimer);
        }
        if (mRingbackTimer != null && mRingbackTimer.isEnabled()) {
            Message getRingbackTimer = mIpsecHandler
                    .obtainMessage(MSG_GET_RINGBACK_TIMER);
            mIpsecHandler.sendMessage(getRingbackTimer);
        }
        if (mNoAnswerTimer != null && mNoAnswerTimer.isEnabled()) {
            Message getNoAnswerTimer = mIpsecHandler
                    .obtainMessage(MSG_GET_NO_ANSWER_TIMER);
            mIpsecHandler.sendMessage(getNoAnswerTimer);
        }
        if (mSessionTimer != null && mSessionTimer.isEnabled()) {
            Message getSessionTimer = mIpsecHandler
                    .obtainMessage(MSG_GET_SESSION_TIMER);
            mIpsecHandler.sendMessage(getSessionTimer);
        }
        //UNISOC: add for bug1382482
        if (mMinSeTimer != null && mMinSeTimer.isEnabled()) {
            Message getMinSeTimer = mIpsecHandler
                    .obtainMessage(MSG_GET_MIN_SE_TIMER);
            mIpsecHandler.sendMessage(getMinSeTimer);
        }
        if (mMTSessionTimer != null && mMTSessionTimer.isEnabled()) {
            Message getMTSessionTimer = mIpsecHandler
                    .obtainMessage(MSG_GET_MT_SESSION_TIMER);
            mIpsecHandler.sendMessage(getMTSessionTimer);
        }
        if (mConferenceURI != null && mConferenceURI.isEnabled()) {
            Message getConferenceURI = mIpsecHandler
                    .obtainMessage(MSG_GET_CONFERENCE_URI);
            mIpsecHandler.sendMessage(getConferenceURI);
        }
        if (mReliable180Switch != null && mReliable180Switch.isEnabled()) {
            Message getReliable180 = mIpsecHandler.obtainMessage(MSG_GET_RELIABLE_180);
            mIpsecHandler.sendMessage(getReliable180);
        }
        if (mDirectAlertingSwitch != null && mDirectAlertingSwitch.isEnabled()) {
            Message getDirectAlerting = mIpsecHandler.obtainMessage(MSG_GET_DIRECT_ALERTING);
            mIpsecHandler.sendMessage(getDirectAlerting);
        }
        if (mResourceAlwaysSwitch != null && mResourceAlwaysSwitch.isEnabled()) {
            Message getResourceAlways = mIpsecHandler.obtainMessage(MSG_GET_RESOURCE_ALWAYS);
            mIpsecHandler.sendMessage(getResourceAlways);
        }
        if (mPreconditionSwitch != null && mPreconditionSwitch.isEnabled()) {
            Message getPrecondition = mIpsecHandler.obtainMessage(MSG_GET_PRECONDITION);
            mIpsecHandler.sendMessage(getPrecondition);
        }
        if (mVideoUpgradeSwitch != null && mVideoUpgradeSwitch.isEnabled()) {
            Message getVideoUpgrad = mIpsecHandler.obtainMessage(MSG_GET_VIDEO_UPGRADE);
            mIpsecHandler.sendMessage(getVideoUpgrad);
        }
        if (mVideoEarlySwitch != null && mVideoEarlySwitch.isEnabled()) {
            Message getVideoEarly = mIpsecHandler.obtainMessage(MSG_GET_VIDEO_EARLY_MEDIA);
            mIpsecHandler.sendMessage(getVideoEarly);
        }
        if (mAlertingSwitch != null && mAlertingSwitch.isEnabled()) {
            Message getAlerting = mIpsecHandler.obtainMessage(MSG_GET_ALERTING);
            mIpsecHandler.sendMessage(getAlerting);
        }
        if (mMidCallSwitch != null && mMidCallSwitch.isEnabled()) {
            Message getMidCall = mIpsecHandler.obtainMessage(MSG_GET_MID_CALL);
            mIpsecHandler.sendMessage(getMidCall);
        }
        if (mPreAlertingSwitch != null && mPreAlertingSwitch.isEnabled()) {
            Message getPreAlerting = mIpsecHandler.obtainMessage(MSG_GET_PER_ALERTING);
            mIpsecHandler.sendMessage(getPreAlerting);
        }
        if (mRSRVCCSupportSwitch != null && mRSRVCCSupportSwitch.isEnabled()) {
            Message getRSRVCCSupport = mIpsecHandler.obtainMessage(MSG_GET_RSRVCC_SUPPORT);
            mIpsecHandler.sendMessage(getRSRVCCSupport);
        }
        if (mRSRVCCAlertSwitch != null && mRSRVCCAlertSwitch.isEnabled()) {
            Message getRSRVCCAlert = mIpsecHandler.obtainMessage(MSG_GET_RSRVCC_ALERTING);
            mIpsecHandler.sendMessage(getRSRVCCAlert);
        }
        if (mRSRVCCMidSwitch != null && mRSRVCCMidSwitch.isEnabled()) {
            Message getRSRVCCMid = mIpsecHandler.obtainMessage(MSG_GET_RSRVCC_MID_CALL);
            mIpsecHandler.sendMessage(getRSRVCCMid);
        }
        if (mVideoMaxResolution != null && mVideoMaxResolution.isEnabled()) {
            Message getVideoMaxResolution = mIpsecHandler
                    .obtainMessage(MSG_GET_VIDEO_MAX_RESOLUTION);
            mIpsecHandler.sendMessage(getVideoMaxResolution);
        }
        if (mNationalURL != null && mNationalURL.isEnabled()) {
            Message getNationalURL = mIpsecHandler
                    .obtainMessage(MSG_GET_NATIONAL_URL_FORMAT);
            mIpsecHandler.sendMessage(getNationalURL);
        }
        if (mInternationalURL != null && mInternationalURL.isEnabled()) {
            Message getInternationalURL = mIpsecHandler
                    .obtainMessage(MSG_GET_INTERNATIONAL_URL);
            mIpsecHandler.sendMessage(getInternationalURL);
        }
        if (mSessionTimerRefresher != null && mSessionTimerRefresher.isEnabled()) {
            Message getSessionTimerRefresher = mIpsecHandler
                    .obtainMessage(MSG_GET_SESSION_TIMER_REFRESHER);
            mIpsecHandler.sendMessage(getSessionTimerRefresher);
        }
        if (mMTSessionTimerRefresher != null && mMTSessionTimerRefresher.isEnabled()) {
            Message getTimerRefresher = mIpsecHandler
                    .obtainMessage(MSG_GET_MT_SESSION_REFRESHER);
            mIpsecHandler.sendMessage(getTimerRefresher);
        }
        if (mVideoAudioMerge != null && mVideoAudioMerge.isEnabled()) {
            Message getVideoAudioMerge = mIpsecHandler
                    .obtainMessage(MSG_GET_VIDEO_AUDIO_MERGE);
            mIpsecHandler.sendMessage(getVideoAudioMerge);
        }
        if (mAudioDynamicEnable != null && mAudioDynamicEnable.isEnabled()) {
            Message getAudioDynamic = mIpsecHandler.obtainMessage(MSG_GET_AUDIO_DYNAMIC_ENABLE);
            mIpsecHandler.sendMessage(getAudioDynamic);
        }
        if (mVideoDynamicEnable != null && mVideoDynamicEnable.isEnabled()) {
            Message getVideoDynamic = mIpsecHandler.obtainMessage(MSG_GET_VIDEO_DYNAMIC_ENABLE);
            mIpsecHandler.sendMessage(getVideoDynamic);
        }
        if (mBsfURI != null && mBsfURI.isEnabled()) {
            Message getBsfURI = mIpsecHandler
                    .obtainMessage(MSG_GET_BSF_URI);
            mIpsecHandler.sendMessage(getBsfURI);
        }
        if (mBsfPort != null && mBsfPort.isEnabled()) {
            Message getBsfPort = mIpsecHandler
                    .obtainMessage(MSG_GET_BSF_PORT);
            mIpsecHandler.sendMessage(getBsfPort);
        }
        if (mXcapURI != null && mXcapURI.isEnabled()) {
            Message getXcapURI = mIpsecHandler
                    .obtainMessage(MSG_GET_XCAP_URI);
            mIpsecHandler.sendMessage(getXcapURI);
        }
        if (mXcapPort != null && mXcapPort.isEnabled()) {
            Message getXcapPort = mIpsecHandler
                    .obtainMessage(MSG_GET_XCAP_PORT);
            mIpsecHandler.sendMessage(getXcapPort);
        }
        if (mXcapAuid != null && mXcapAuid.isEnabled()) {
            Message getXcapAuid = mIpsecHandler
                    .obtainMessage(MSG_GET_XCAP_AUID);
            mIpsecHandler.sendMessage(getXcapAuid);
        }
        if (mLocalCallSwitch != null && mLocalCallSwitch.isEnabled()) {
            Message getLocalCall = mIpsecHandler.obtainMessage(MSG_GET_LOCAL_CALL);
            mIpsecHandler.sendMessage(getLocalCall);
        }
        if (mDnsSrvEnableSwitch != null && mDnsSrvEnableSwitch.isEnabled()) {
            Message getDnsSrvEnable = mIpsecHandler.obtainMessage(MSG_GET_DNS_SRV_ENABLE);
            mIpsecHandler.sendMessage(getDnsSrvEnable);
        }
        if (mHttpsEnableSwitch != null && mHttpsEnableSwitch.isEnabled()) {
            Message getHttpsEnable = mIpsecHandler.obtainMessage(MSG_GET_HTTPS_ENABLE);
            mIpsecHandler.sendMessage(getHttpsEnable);
        }
        if (mActivateCfnlSwitch != null && mActivateCfnlSwitch.isEnabled()) {
            Message getActivateCfnl = mIpsecHandler.obtainMessage(MSG_GET_ACTIVATE_CFNL);
            mIpsecHandler.sendMessage(getActivateCfnl);
        }
        if (mHttpPutMediaSwitch != null && mHttpPutMediaSwitch.isEnabled()) {
            Message getHttpPutMedia = mIpsecHandler.obtainMessage(MSG_GET_HTTP_PUT_MEDIA);
            mIpsecHandler.sendMessage(getHttpPutMedia);
        }
        if (mIPPriority != null && mIPPriority.isEnabled()) {
            Message getIPPriority = mIpsecHandler
                    .obtainMessage(MSG_GET_IP_PRIORITY);
            mIpsecHandler.sendMessage(getIPPriority);
        }
        Message getVoiceCode = mIpsecHandler.obtainMessage(MSG_GET_VOICE_CODE_TYPE);
        mIpsecHandler.sendMessage(getVoiceCode);
        Message getIntegrityAlgorithm = mIpsecHandler.obtainMessage(MSG_GET_INTEGRITY_ALGORITHM);
        mIpsecHandler.sendMessage(getIntegrityAlgorithm);
        Message getCipherAlgorithm = mIpsecHandler.obtainMessage(MSG_GET_CIPHER_ALGORITHM);
        mIpsecHandler.sendMessage(getCipherAlgorithm);
        Message getVideoCodeType = mIpsecHandler.obtainMessage(MSG_GET_VIDEO_CODE_TYPE);
        mIpsecHandler.sendMessage(getVideoCodeType);
        super.onStart();
    }

    @Override
    public void onDestroy() {
        if (mIpsecHandler != null) {
            mIpsecHandler.getLooper().quit();
            Log.d(TAG, "HandlerThread has quit");
        }
        super.onDestroy();
    }

    @Override
    public boolean onPreferenceChange(Preference pref, Object newValue) {
        Log.d(TAG, "onPreferenceChange newValue: " + newValue);
        if (pref == mIPsecSwitch) {
            if (mIPsecSwitch.isChecked()) {
                Message setIpsecClose = mIpsecHandler.obtainMessage(MSG_SET_IPSEC_STATE, "0");
                mIpsecHandler.sendMessage(setIpsecClose);
            } else {
                Message setIpsecOpen = mIpsecHandler.obtainMessage(MSG_SET_IPSEC_STATE, "1");
                mIpsecHandler.sendMessage(setIpsecOpen);
            }
        } else if (pref == mRegSubscribeEnable) {
            if (mRegSubscribeEnable.isChecked()) {
                Message setRegSubscribeClose = mIpsecHandler.obtainMessage(MSG_SET_REG_SUBSCRIBE_ENABLE, "0");
                mIpsecHandler.sendMessage(setRegSubscribeClose);
            } else {
                Message setRegSubscribeOpen = mIpsecHandler.obtainMessage(MSG_SET_REG_SUBSCRIBE_ENABLE, "1");
                mIpsecHandler.sendMessage(setRegSubscribeOpen);
            }
        } else if (pref == mMWIEnable) {
            if (mMWIEnable.isChecked()) {
                Message setMWIClose = mIpsecHandler.obtainMessage(MSG_SET_MWI_ENABLE, "0");
                mIpsecHandler.sendMessage(setMWIClose);
            } else {
                Message setMWIOpen = mIpsecHandler.obtainMessage(MSG_SET_MWI_ENABLE, "1");
                mIpsecHandler.sendMessage(setMWIOpen);
            }
        } else if (pref == mSIPHeaderCompactForm) {
            if (mSIPHeaderCompactForm.isChecked()) {
                Message setSIPHeaderCompactClose = mIpsecHandler.obtainMessage(MSG_SET_SIP_HEADER_COMPACT, "0");
                mIpsecHandler.sendMessage(setSIPHeaderCompactClose);
            } else {
                Message setSIPHeaderCompactOpen = mIpsecHandler.obtainMessage(MSG_SET_SIP_HEADER_COMPACT, "1");
                mIpsecHandler.sendMessage(setSIPHeaderCompactOpen);
            }
        } else if (pref == mSMSoverIPEnable) {
            if (mSMSoverIPEnable.isChecked()) {
                Message setSMSoverIPClose = mIpsecHandler.obtainMessage(MSG_SET_SMS_OVER_IP_ENABLE, "0");
                mIpsecHandler.sendMessage(setSMSoverIPClose);
            } else {
                Message setSMSoverIPOpen = mIpsecHandler.obtainMessage(MSG_SET_SMS_OVER_IP_ENABLE, "1");
                mIpsecHandler.sendMessage(setSMSoverIPOpen);
            }
        } else if (pref == mUSSIEnable) {
            if (mUSSIEnable.isChecked()) {
                Message setUSSIClose = mIpsecHandler.obtainMessage(MSG_SET_USSI_ENABLE, "0");
                mIpsecHandler.sendMessage(setUSSIClose);
            } else {
                Message setUSSIOpen = mIpsecHandler.obtainMessage(MSG_SET_USSI_ENABLE, "1");
                mIpsecHandler.sendMessage(setUSSIOpen);
            }
        } else if (pref == mInitialRegisterPANI) {
            if (mInitialRegisterPANI.isChecked()) {
                Message setInitialRegisterClose = mIpsecHandler.obtainMessage(MSG_SET_INITIAL_REGISTER_PANI, "0");
                mIpsecHandler.sendMessage(setInitialRegisterClose);
            } else {
                Message setInitialRegisterOpen = mIpsecHandler.obtainMessage(MSG_SET_INITIAL_REGISTER_PANI, "1");
                mIpsecHandler.sendMessage(setInitialRegisterOpen);
            }
        } else if (pref == mNamedTelEvent) {
            if (mNamedTelEvent.isChecked()) {
                Message setNamedTelEventClose = mIpsecHandler.obtainMessage(MSG_SET_NAMED_TEL_EVENT, "0");
                mIpsecHandler.sendMessage(setNamedTelEventClose);
            } else {
                Message setNamedTelEventOpen = mIpsecHandler.obtainMessage(MSG_SET_NAMED_TEL_EVENT, "1");
                mIpsecHandler.sendMessage(setNamedTelEventOpen);
            }
        } else if (pref == mIntegrityAlgorithm) {
            Log.d(TAG, "mIntegrityAlgorithm");
            Message setIntegrityAlgorithm = mIpsecHandler
                    .obtainMessage(MSG_SET_INTEGRITY_ALGORITHM);
            mIpsecHandler.sendMessage(setIntegrityAlgorithm);
        } else if (pref == mCipherAlgorithm) {
            Log.d(TAG, "mCipherAlgorithm");
            Message setCipherAlgorithm = mIpsecHandler
                    .obtainMessage(MSG_SET_CIPHER_ALGORITHM);
            mIpsecHandler.sendMessage(setCipherAlgorithm);
        } else if (pref == mRegSubscribExpire) {
            Log.d(TAG, "mRegSubscribExpire");
            Message setRegSubscribExpire = mIpsecHandler
                    .obtainMessage(MSG_SET_REG_SUBSCRIBE_EXPIRE, newValue);
            mIpsecHandler.sendMessage(setRegSubscribExpire);
        } else if (pref == mMwiSubscribExpire) {
            Log.d(TAG, "mMwiSubscribExpire");
            Message setMwiSubscribExpire = mIpsecHandler
                    .obtainMessage(MSG_SET_MWI_SUBSCRIBE_EXPIRE, newValue);
            mIpsecHandler.sendMessage(setMwiSubscribExpire);
        } else if (pref == mMTU) {
            Log.d(TAG, "mMTU");
            Message setMTU = mIpsecHandler
                    .obtainMessage(MSG_SET_MTU, newValue);
            mIpsecHandler.sendMessage(setMTU);
        } else if (pref == mPcscfPort) {
            Log.d(TAG, "mPcscfPort");
            Message setPcscfPort = mIpsecHandler
                    .obtainMessage(MSG_SET_PCSCFPORT, newValue);
            mIpsecHandler.sendMessage(setPcscfPort);
        } else if (pref == mRegExpire) {
            Log.d(TAG, "mRegExpire");
            Message setRegExpire = mIpsecHandler
                    .obtainMessage(MSG_SET_REG_EXPIRE, newValue);
            mIpsecHandler.sendMessage(setRegExpire);
        } else if (pref == mTregTimer) {
            Log.d(TAG, "mTregTimer");
            Message setTregTimer = mIpsecHandler
                    .obtainMessage(MSG_SET_TREG_TIMER, newValue);
            mIpsecHandler.sendMessage(setTregTimer);
        } else if (pref == mTemergregTimer) {
            Log.d(TAG, "mTemergregTimer");
            Message setTemergregTimer = mIpsecHandler
                    .obtainMessage(MSG_SET_TEMERGREG_TIMER, newValue);
            mIpsecHandler.sendMessage(setTemergregTimer);
        } else if (pref == mMinBandWidth) {
            Message setMinBandWidth = mIpsecHandler.obtainMessage(
                    MSG_SET_MIN_BANDWIDTH, newValue);
            mIpsecHandler.sendMessage(setMinBandWidth);
            return false;
        } else if (pref == mMaxBandWidth) {
            Message setMaxBandWidth = mIpsecHandler.obtainMessage(
                    MSG_SET_MAX_BANDWIDTH, newValue);
            mIpsecHandler.sendMessage(setMaxBandWidth);
            return false;
        } else if (pref == mMinBitRate) {
            Message setMinBitRate = mIpsecHandler.obtainMessage(
                    MSG_SET_MIN_BITRATE, newValue);
            mIpsecHandler.sendMessage(setMinBitRate);
            return false;
        } else if (pref == mMaxBitRate) {
            Message setMaxBitRate = mIpsecHandler.obtainMessage(
                    MSG_SET_MAX_BITRATE, newValue);
            mIpsecHandler.sendMessage(setMaxBitRate);
            return false;
        } else if (pref == mDefaultBitRate) {
            Message setDefaultBitRate = mIpsecHandler.obtainMessage(
                    MSG_SET_DEFAULT_BITRATE, newValue);
            mIpsecHandler.sendMessage(setDefaultBitRate);
            return false;
        } else if (pref == mEvsDefaultBR) {
            Message setEvsDefaultBr = mIpsecHandler.obtainMessage(
                    MSG_SET_EVS_DEFAULT_BITRATE, newValue);
            mIpsecHandler.sendMessage(setEvsDefaultBr);
            return false;
        } else if (pref == mWbDefaultBR) {
            Message setWbDefaultBr = mIpsecHandler.obtainMessage(
                    MSG_SET_WB_DEFAULT_BITRATE, newValue);
            mIpsecHandler.sendMessage(setWbDefaultBr);
            return false;
        } else if (pref == mNbDefaultBR) {
            Message setNbDefaultBr = mIpsecHandler.obtainMessage(
                    MSG_SET_NB_DEFAULT_BITRATE, newValue);
            mIpsecHandler.sendMessage(setNbDefaultBr);
            return false;
        } else if (pref == mChannelAwareMode) {
            Message setAwreMode = mIpsecHandler.obtainMessage(
                    MSG_SET_CHANNEL_AWARE_MODE, newValue);
            mIpsecHandler.sendMessage(setAwreMode);
            return false;
        } else if (pref == mMoAmrOaMode) {
            Message setMoAmrOaMode = mIpsecHandler.obtainMessage(
                    MSG_SET_MO_AMR_OA, newValue);
            mIpsecHandler.sendMessage(setMoAmrOaMode);
            return false;
        } else if (pref == mAudioRTPTimeout) {
            Log.d(TAG, "mAudioRTPTimeout");
            Message setAudioRTPTimeout = mIpsecHandler
                    .obtainMessage(MSG_SET_AUDIO_RTP_TIMEOUT, newValue);
            mIpsecHandler.sendMessage(setAudioRTPTimeout);
        } else if (pref == mAudioRTCPTimeInterval) {
            Log.d(TAG, "mAudioRTCPTimeInterval");
            Message setAudioRTCPTime = mIpsecHandler
                    .obtainMessage(MSG_SET_AUDIO_RTCP_TIME, newValue);
            mIpsecHandler.sendMessage(setAudioRTCPTime);
        } else if (pref == mAudioRS) {
            Log.d(TAG, "mAudioRS");
            Message setAudioRS = mIpsecHandler
                    .obtainMessage(MSG_SET_AUDIO_RS, newValue);
            mIpsecHandler.sendMessage(setAudioRS);
        } else if (pref == mAudioRR) {
            Log.d(TAG, "mAudioRR");
            Message setAudioRR = mIpsecHandler
                    .obtainMessage(MSG_SET_AUDIO_RR, newValue);
            mIpsecHandler.sendMessage(setAudioRR);
        } else if (pref == mVideoRs) {
            Log.d(TAG, "mVideoRs");
            Message setVideoRs = mIpsecHandler
                    .obtainMessage(MSG_SET_VIDEO_RS, newValue);
            mIpsecHandler.sendMessage(setVideoRs);
        } else if (pref == mVideoRR) {
            Log.d(TAG, "mVideoRR");
            Message setVideoRR = mIpsecHandler
                    .obtainMessage(MSG_SET_VIDEO_RR, newValue);
            mIpsecHandler.sendMessage(setVideoRR);
        } else if (pref == mTcallTimer) {
            Log.d(TAG, "mTcallTimer");
            Message setTcallTimer = mIpsecHandler
                    .obtainMessage(MSG_SET_TCALL_TIMER, newValue);
            mIpsecHandler.sendMessage(setTcallTimer);
        } else if (pref == mRingingTimer) {
            Log.d(TAG, "mRingingTimer");
            Message setRingingTimer = mIpsecHandler
                    .obtainMessage(MSG_SET_RINGRING_TIMER, newValue);
            mIpsecHandler.sendMessage(setRingingTimer);
        } else if (pref == mRingbackTimer) {
            Log.d(TAG, "mRingbackTimer");
            Message setRingbackTimer = mIpsecHandler
                    .obtainMessage(MSG_SET_RINGBACK_TIMER, newValue);
            mIpsecHandler.sendMessage(setRingbackTimer);
        } else if (pref == mNoAnswerTimer) {
            Log.d(TAG, "mNoAnswerTimer");
            Message setNoAnswerTimer = mIpsecHandler
                    .obtainMessage(MSG_SET_NO_ANSWER_TIMER, newValue);
            mIpsecHandler.sendMessage(setNoAnswerTimer);
        } else if (pref == mSessionTimer) {
            Log.d(TAG, "mSessionTimer");
            Message setSessionTimer = mIpsecHandler
                    .obtainMessage(MSG_SET_SESSION_TIMER, newValue);
            mIpsecHandler.sendMessage(setSessionTimer);
        //UNISOC: add for bug1382482
        } else if (pref == mMinSeTimer) {
            Log.d(TAG, "MinSeTimer");
            Message setMinSeTimer = mIpsecHandler
                    .obtainMessage(MSG_SET_MIN_SE_TIMER, newValue);
            mIpsecHandler.sendMessage(setMinSeTimer);
        } else if (pref == mMTSessionTimer) {
            Log.d(TAG, "mMTSessionTimer");
            Message setMTSessionTimer = mIpsecHandler
                    .obtainMessage(MSG_SET_MT_SESSION_TIMER, newValue);
            mIpsecHandler.sendMessage(setMTSessionTimer);
        } else if (pref == mConferenceURI) {
            Log.d(TAG, "mConferenceURI");
            Message setConferenceURI = mIpsecHandler
                    .obtainMessage(MSG_SET_CONFERENCE_URI, newValue);
            mIpsecHandler.sendMessage(setConferenceURI);
        } else if (pref == mReliable180Switch) {
            if (mReliable180Switch.isChecked()) {
                Message setReliable180Close = mIpsecHandler.obtainMessage(MSG_SET_RELIABLE_180, "0");
                mIpsecHandler.sendMessage(setReliable180Close);
            } else {
                Message setReliable180Open = mIpsecHandler.obtainMessage(MSG_SET_RELIABLE_180, "1");
                mIpsecHandler.sendMessage(setReliable180Open);
            }
        } else if (pref == mDirectAlertingSwitch) {
            if (mDirectAlertingSwitch.isChecked()) {
                Message setDirectAlertClose = mIpsecHandler.obtainMessage(MSG_SET_DIRECT_ALERTING, "0");
                mIpsecHandler.sendMessage(setDirectAlertClose);
            } else {
                Message setDirectAlertOpen = mIpsecHandler.obtainMessage(MSG_SET_DIRECT_ALERTING, "1");
                mIpsecHandler.sendMessage(setDirectAlertOpen);
            }
        } else if (pref == mResourceAlwaysSwitch) {
            if (mResourceAlwaysSwitch.isChecked()) {
                Message setResourceAlwaysClose = mIpsecHandler.obtainMessage(MSG_SET_RESOURCE_ALWAYS, "0");
                mIpsecHandler.sendMessage(setResourceAlwaysClose);
            } else {
                Message setResourceAlwaysOpen = mIpsecHandler.obtainMessage(MSG_SET_RESOURCE_ALWAYS, "1");
                mIpsecHandler.sendMessage(setResourceAlwaysOpen);
            }
        } else if (pref == mPreconditionSwitch) {
            if (mPreconditionSwitch.isChecked()) {
                Message setPreconditionClose = mIpsecHandler.obtainMessage(MSG_SET_PRECONDITION, "0");
                mIpsecHandler.sendMessage(setPreconditionClose);
            } else {
                Message setPreconditionOpen = mIpsecHandler.obtainMessage(MSG_SET_PRECONDITION, "1");
                mIpsecHandler.sendMessage(setPreconditionOpen);
            }
        } else if (pref == mVideoUpgradeSwitch) {
            if (mVideoUpgradeSwitch.isChecked()) {
                Message setVideoUpgradeClose = mIpsecHandler.obtainMessage(MSG_SET_VIDEO_UPGRADE, "0");
                mIpsecHandler.sendMessage(setVideoUpgradeClose);
            } else {
                Message setVideoUpgradeOpen = mIpsecHandler.obtainMessage(MSG_SET_VIDEO_UPGRADE, "1");
                mIpsecHandler.sendMessage(setVideoUpgradeOpen);
            }
        } else if (pref == mVideoEarlySwitch) {
            if (mVideoEarlySwitch.isChecked()) {
                Message setVideoEarlyClose = mIpsecHandler.obtainMessage(MSG_SET_VIDEO_EARLY_MEDIA, "0");
                mIpsecHandler.sendMessage(setVideoEarlyClose);
            } else {
                Message setVideoEarlyOpen = mIpsecHandler.obtainMessage(MSG_SET_VIDEO_EARLY_MEDIA, "1");
                mIpsecHandler.sendMessage(setVideoEarlyOpen);
            }
        } else if (pref == mAlertingSwitch) {
            if (mAlertingSwitch.isChecked()) {
                Message setAlertingClose = mIpsecHandler.obtainMessage(MSG_SET_ALERTING, "0");
                mIpsecHandler.sendMessage(setAlertingClose);
            } else {
                Message setAlertingOpen = mIpsecHandler.obtainMessage(MSG_SET_ALERTING, "1");
                mIpsecHandler.sendMessage(setAlertingOpen);
            }
        } else if (pref == mMidCallSwitch) {
            if (mMidCallSwitch.isChecked()) {
                Message setMidCallClose = mIpsecHandler.obtainMessage(MSG_SET_MID_CALL, "0");
                mIpsecHandler.sendMessage(setMidCallClose);
            } else {
                Message setMidCallOpen = mIpsecHandler.obtainMessage(MSG_SET_MID_CALL, "1");
                mIpsecHandler.sendMessage(setMidCallOpen);
            }
        } else if (pref == mPreAlertingSwitch) {
            if (mPreAlertingSwitch.isChecked()) {
                Message setPreAlertClose = mIpsecHandler.obtainMessage(MSG_SET_PER_ALERTING, "0");
                mIpsecHandler.sendMessage(setPreAlertClose);
            } else {
                Message setPreAlertOpen = mIpsecHandler.obtainMessage(MSG_SET_PER_ALERTING, "1");
                mIpsecHandler.sendMessage(setPreAlertOpen);
            }
        } else if (pref == mRSRVCCSupportSwitch) {
            if (mRSRVCCSupportSwitch.isChecked()) {
                Message setRSRVCCSupportClose = mIpsecHandler.obtainMessage(MSG_SET_RSRVCC_SUPPORT, "0");
                mIpsecHandler.sendMessage(setRSRVCCSupportClose);
            } else {
                Message setRSRVCCSupportOpen = mIpsecHandler.obtainMessage(MSG_SET_RSRVCC_SUPPORT, "1");
                mIpsecHandler.sendMessage(setRSRVCCSupportOpen);
            }
        } else if (pref == mRSRVCCAlertSwitch) {
            if (mRSRVCCAlertSwitch.isChecked()) {
                Message setRSRVCCAlertClose = mIpsecHandler.obtainMessage(MSG_SET_RSRVCC_ALERTING, "0");
                mIpsecHandler.sendMessage(setRSRVCCAlertClose);
            } else {
                Message setRSRVCCAlertOpen = mIpsecHandler.obtainMessage(MSG_SET_RSRVCC_ALERTING, "1");
                mIpsecHandler.sendMessage(setRSRVCCAlertOpen);
            }
        } else if (pref == mRSRVCCMidSwitch) {
            if (mRSRVCCMidSwitch.isChecked()) {
                Message setRSRVCCMidClose = mIpsecHandler.obtainMessage(MSG_SET_RSRVCC_MID_CALL, "0");
                mIpsecHandler.sendMessage(setRSRVCCMidClose);
            } else {
                Message setRSRVCCMidOpen = mIpsecHandler.obtainMessage(MSG_SET_RSRVCC_MID_CALL, "1");
                mIpsecHandler.sendMessage(setRSRVCCMidOpen);
            }
        } else if (pref == mVideoMaxResolution) {
            Message setVideoMaxResolution = mIpsecHandler.obtainMessage(
                    MSG_SET_VIDEO_MAX_RESOLUTION, newValue);
            mIpsecHandler.sendMessage(setVideoMaxResolution);
            return false;
        } else if (pref == mNationalURL) {
            Message setNationalURL = mIpsecHandler.obtainMessage(
                    MSG_SET_NATIONAL_URL_FORMAT, newValue);
            mIpsecHandler.sendMessage(setNationalURL);
            return false;
        } else if (pref == mInternationalURL) {
            Message setInternationalURL = mIpsecHandler.obtainMessage(
                    MSG_SET_INTERNATIONAL_URL, newValue);
            mIpsecHandler.sendMessage(setInternationalURL);
            return false;
        } else if (pref == mSessionTimerRefresher) {
            Message setTimerRefresher = mIpsecHandler.obtainMessage(
                    MSG_SET_SESSION_TIMER_REFRESHER, newValue);
            mIpsecHandler.sendMessage(setTimerRefresher);
            return false;
        } else if (pref == mMTSessionTimerRefresher) {
            Message setMTSession = mIpsecHandler.obtainMessage(
                    MSG_SET_MT_SESSION_REFRESHER, newValue);
            mIpsecHandler.sendMessage(setMTSession);
            return false;
        } else if (pref == mVideoAudioMerge) {
            Message setVideoAudioMerge = mIpsecHandler.obtainMessage(
                    MSG_SET_VIDEO_AUDIO_MERGE, newValue);
            mIpsecHandler.sendMessage(setVideoAudioMerge);
            return false;
        } else if (pref == mAudioDynamicEnable) {
            if (mAudioDynamicEnable.isChecked()) {
                Message setsetAudioDynamicClose = mIpsecHandler.obtainMessage(MSG_SET_AUDIO_DYNAMIC_ENABLE, "0");
                mIpsecHandler.sendMessage(setsetAudioDynamicClose);
            } else {
                Message setAudioDynamicOpen = mIpsecHandler.obtainMessage(MSG_SET_AUDIO_DYNAMIC_ENABLE, "1");
                mIpsecHandler.sendMessage(setAudioDynamicOpen);
            }
        } else if (pref == mVideoDynamicEnable) {
            if (mVideoDynamicEnable.isChecked()) {
                Message setVideoDynamicClose = mIpsecHandler.obtainMessage(MSG_SET_VIDEO_DYNAMIC_ENABLE, "0");
                mIpsecHandler.sendMessage(setVideoDynamicClose);
            } else {
                Message setVideoDynamicOpen = mIpsecHandler.obtainMessage(MSG_SET_VIDEO_DYNAMIC_ENABLE, "1");
                mIpsecHandler.sendMessage(setVideoDynamicOpen);
            }
        } else if (pref == mBsfURI) {
            Log.d(TAG, "mBsfURI");
            Message setBsfURI = mIpsecHandler
                    .obtainMessage(MSG_SET_BSF_URI, newValue);
            mIpsecHandler.sendMessage(setBsfURI);
        } else if (pref == mBsfPort) {
            Log.d(TAG, "mBsfPort");
            Message setBsfPort = mIpsecHandler
                    .obtainMessage(MSG_SET_BSF_PORT, newValue);
            mIpsecHandler.sendMessage(setBsfPort);
        } else if (pref == mXcapURI) {
            Log.d(TAG, "mXcapURI");
            Message setXcapURI = mIpsecHandler
                    .obtainMessage(MSG_SET_XCAP_URI, newValue);
            mIpsecHandler.sendMessage(setXcapURI);
        } else if (pref == mXcapPort) {
            Log.d(TAG, "mXcapPort");
            Message setXcapPort = mIpsecHandler
                    .obtainMessage(MSG_SET_XCAP_PORT, newValue);
            mIpsecHandler.sendMessage(setXcapPort);
        } else if (pref == mXcapAuid) {
            Log.d(TAG, "mXcapAuid");
            Message setXcapAuid = mIpsecHandler
                    .obtainMessage(MSG_SET_XCAP_AUID, newValue);
            mIpsecHandler.sendMessage(setXcapAuid);
        } else if (pref == mLocalCallSwitch) {
            if (mLocalCallSwitch.isChecked()) {
                Message setLocalCallClose = mIpsecHandler.obtainMessage(MSG_SET_LOCAL_CALL, "0");
                mIpsecHandler.sendMessage(setLocalCallClose);
            } else {
                Message setLocalCallOpen = mIpsecHandler.obtainMessage(MSG_SET_LOCAL_CALL, "1");
                mIpsecHandler.sendMessage(setLocalCallOpen);
            }
        } else if (pref == mDnsSrvEnableSwitch) {
            if (mDnsSrvEnableSwitch.isChecked()) {
                Message setDnsSrvEnableClose = mIpsecHandler.obtainMessage(MSG_SET_DNS_SRV_ENABLE, "0");
                mIpsecHandler.sendMessage(setDnsSrvEnableClose);
            } else {
                Message setDnsSrvEnableOpen = mIpsecHandler.obtainMessage(MSG_SET_DNS_SRV_ENABLE, "1");
                mIpsecHandler.sendMessage(setDnsSrvEnableOpen);
            }
        } else if (pref == mHttpsEnableSwitch) {
            if (mHttpsEnableSwitch.isChecked()) {
                Message setHttpsEnableClose = mIpsecHandler.obtainMessage(MSG_SET_HTTPS_ENABLE, "0");
                mIpsecHandler.sendMessage(setHttpsEnableClose);
            } else {
                Message setHttpsEnableOpen = mIpsecHandler.obtainMessage(MSG_SET_HTTPS_ENABLE, "1");
                mIpsecHandler.sendMessage(setHttpsEnableOpen);
            }
        } else if (pref == mActivateCfnlSwitch) {
            if (mActivateCfnlSwitch.isChecked()) {
                Message setActivateCfnlClose = mIpsecHandler.obtainMessage(MSG_SET_ACTIVATE_CFNL, "0");
                mIpsecHandler.sendMessage(setActivateCfnlClose);
            } else {
                Message setActivateCfnlOpen = mIpsecHandler.obtainMessage(MSG_SET_ACTIVATE_CFNL, "1");
                mIpsecHandler.sendMessage(setActivateCfnlOpen);
            }
        } else if (pref == mHttpPutMediaSwitch) {
            if (mHttpPutMediaSwitch.isChecked()) {
                Message setHttpPutMediaClose = mIpsecHandler.obtainMessage(MSG_SET_HTTP_PUT_MEDIA, "0");
                mIpsecHandler.sendMessage(setHttpPutMediaClose);
            } else {
                Message setHttpPutMediaOpen = mIpsecHandler.obtainMessage(MSG_SET_HTTP_PUT_MEDIA, "1");
                mIpsecHandler.sendMessage(setHttpPutMediaOpen);
            }
        } else if (pref == mIPPriority) {
            Message setIPPriority = mIpsecHandler.obtainMessage(
                    MSG_SET_IP_PRIORITY, newValue);
            mIpsecHandler.sendMessage(setIPPriority);
            return false;
        }

        return false;
    }

    @Override
    public boolean onPreferenceClick(Preference pref) {
        if (pref == mIntegrityAlgorithm) {
            Log.d(TAG, "mIntegrityAlgorithm");
            Message setIntegrityAlgorithm = mIpsecHandler
                    .obtainMessage(MSG_SET_INTEGRITY_ALGORITHM);
            mIpsecHandler.sendMessage(setIntegrityAlgorithm);
        } else if (pref == mCipherAlgorithm) {
            Log.d(TAG, "mCipherAlgorithm");
            Message setCipherAlgorithm = mIpsecHandler
                    .obtainMessage(MSG_SET_CIPHER_ALGORITHM);
            mIpsecHandler.sendMessage(setCipherAlgorithm);
        } else if (pref == mVolteVoiceCode) {
            Message setVoiceCode = mIpsecHandler
                    .obtainMessage(MSG_SET_VOICE_CODE_TYPE);
            mIpsecHandler.sendMessage(setVoiceCode);
            return false;
        } else if (pref == mEvsRateSet) {
            Message setEvsRateSet = mIpsecHandler
                    .obtainMessage(MSG_SET_EVS_RATE_SET);
            mIpsecHandler.sendMessage(setEvsRateSet);
            return false;
        } else if (pref == mWbRateSet) {
            Message setWbRateSet = mIpsecHandler
                    .obtainMessage(MSG_SET_WB_RATE_SET);
            mIpsecHandler.sendMessage(setWbRateSet);
            return false;
        } else if (pref == mNbRateSet) {
            Message setNbRateSet = mIpsecHandler
                    .obtainMessage(MSG_SET_NB_RATE_SET);
            mIpsecHandler.sendMessage(setNbRateSet);
            return false;
        } else if (pref == mVolteVideoCode) {
            Message setVideoCode = mIpsecHandler
                    .obtainMessage(MSG_SET_VIDEO_CODE_TYPE);
            mIpsecHandler.sendMessage(setVideoCode);
            return false;
        }
        return true;
    }

    class IpsecHandler extends Handler {
        IpsecHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_GET_IPSEC_STATE:
                    try {
                        String mGetIPsec = teleApi.volteOperatorSettings().getIpsecStatus(mSim);
                        mUiThread.post(() -> {
                            boolean open = mGetIPsec.contains("1");
                            mIPsecSwitch.setChecked(open);
                            mIPsecSwitch.setEnabled(true);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mIPsecSwitch.setEnabled(false);
                                mIPsecSwitch.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_IPSEC_STATE:
                    String mSetIPsecValue = (String) msg.obj;
                    Log.d(TAG, "MSG_SET_IPSEC_STATE mSetIPsecValue: " + mSetIPsecValue);
                    String mSetIPsec = engconstents.ENG_AT_SPVOLTEENG + "95,1," + "\"" + mSetIPsecValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setIpsecStatus(mSetIPsec, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mIPsecSwitch.setChecked(mSetIPsecValue.contains("1"));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_REG_SUBSCRIBE_ENABLE:
                    try {
                        String mGetRegSubscribe = teleApi.volteOperatorSettings().getRegSubscribe(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                boolean open = mGetRegSubscribe.contains("1");
                                mRegSubscribeEnable.setChecked(open);
                                mRegSubscribeEnable.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mRegSubscribeEnable.setEnabled(false);
                                mRegSubscribeEnable.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_REG_SUBSCRIBE_ENABLE:
                    String mSetRegSubscribeValue = (String) msg.obj;
                    Log.d(TAG, "MSG_SET_REG_SUBSCRIBE_ENABLE mSetRegSubscribeValue: " + mSetRegSubscribeValue);
                    String mSetRegSubscribe = engconstents.ENG_AT_SPVOLTEENG + "30,1," + "\"" + mSetRegSubscribeValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setRegSubscribe(mSetRegSubscribe, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mRegSubscribeEnable.setChecked(mSetRegSubscribeValue.contains("1"));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_MWI_ENABLE:
                    try {
                        String mGetMWIEnable = teleApi.volteOperatorSettings().getMwiEnable(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                boolean open = mGetMWIEnable.contains("1");
                                mMWIEnable.setChecked(open);
                                mMWIEnable.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mMWIEnable.setEnabled(false);
                                mMWIEnable.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_MWI_ENABLE:
                    String mSetMWIEnableValue = (String) msg.obj;
                    Log.d(TAG, "MSG_SET_MWI_ENABLE mSetMWIEnableValue: " + mSetMWIEnableValue);
                    String mSetMWIEnable = engconstents.ENG_AT_SPVOLTEENG + "33,1," + "\"" + mSetMWIEnableValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setMwiEnable(mSetMWIEnable, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mMWIEnable.setChecked(mSetMWIEnableValue.contains("1"));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_SIP_HEADER_COMPACT:
                    try {
                        String mGetSIPHeaderCompact = teleApi.volteOperatorSettings().getSIPHeaderCompact(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                boolean open = mGetSIPHeaderCompact.contains("1");
                                mSIPHeaderCompactForm.setChecked(open);
                                mSIPHeaderCompactForm.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mSIPHeaderCompactForm.setEnabled(false);
                                mSIPHeaderCompactForm.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_SIP_HEADER_COMPACT:
                    String mSetSIPHeaderCompactValue = (String) msg.obj;
                    Log.d(TAG, "MSG_SET_SIP_HEADER_COMPACT mSetSIPHeaderCompactValue: " + mSetSIPHeaderCompactValue);
                    String mSetSIPHeaderCompact = engconstents.ENG_AT_SPVOLTEENG + "44,1," + "\"" + mSetSIPHeaderCompactValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setSIPHeaderCompact(mSetSIPHeaderCompact, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mSIPHeaderCompactForm.setChecked(mSetSIPHeaderCompactValue.contains("1"));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_SMS_OVER_IP_ENABLE:
                    try {
                        String mGetSMSoverIPEnable = teleApi.volteOperatorSettings().getSMSoverIPEnable(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                boolean open = mGetSMSoverIPEnable.contains("1");
                                mSMSoverIPEnable.setChecked(open);
                                mSMSoverIPEnable.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mSMSoverIPEnable.setEnabled(false);
                                mSMSoverIPEnable.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_SMS_OVER_IP_ENABLE:
                    String mSetSMSoverIPEnableValue = (String) msg.obj;
                    Log.d(TAG, "MSG_SET_SMS_OVER_IP_ENABLE mSetSMSoverIPEnableValue: " + mSetSMSoverIPEnableValue);
                    String mSetSMSoverIPEnable = engconstents.ENG_AT_SET_SMS_IP + mSetSMSoverIPEnableValue;
                    try {
                        teleApi.volteOperatorSettings().setSMSoverIPEnable(mSetSMSoverIPEnable, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mSMSoverIPEnable.setChecked(mSetSMSoverIPEnableValue.contains("1"));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_USSI_ENABLE:
                    try {
                        String mGetUSSIEnable = teleApi.volteOperatorSettings().getUssiEnable(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                boolean open = mGetUSSIEnable.contains("1");
                                mUSSIEnable.setChecked(open);
                                mUSSIEnable.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mUSSIEnable.setEnabled(false);
                                mUSSIEnable.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_USSI_ENABLE:
                    String mSetUSSIEnableValue = (String) msg.obj;
                    Log.d(TAG, "MSG_SET_USSI_ENABLE mSetUSSIEnableValue: " + mSetUSSIEnableValue);
                    String mSetUSSIEnable = engconstents.ENG_AT_SPVOLTEENG + "41,1," + "\"" + mSetUSSIEnableValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setUssiEnable(mSetUSSIEnable, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mUSSIEnable.setChecked(mSetUSSIEnableValue.contains("1"));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_INITIAL_REGISTER_PANI:
                    try {
                        String mGetInitialRegisterPANI = teleApi.volteOperatorSettings().getInitialRegister(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                boolean open = mGetInitialRegisterPANI.contains("1");
                                mInitialRegisterPANI.setChecked(open);
                                mInitialRegisterPANI.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mInitialRegisterPANI.setEnabled(false);
                                mInitialRegisterPANI.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_INITIAL_REGISTER_PANI:
                    String mSetInitialRegisterPANIValue = (String) msg.obj;
                    Log.d(TAG, "MSG_SET_INITIAL_REGISTER_PANI mSetInitialRegisterPANIValue: " + mSetInitialRegisterPANIValue);
                    String mSetInitialRegisterPANI = engconstents.ENG_AT_SPVOLTEENG + "32,1," + "\"" + mSetInitialRegisterPANIValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setInitialRegister(mSetInitialRegisterPANI, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mInitialRegisterPANI.setChecked(mSetInitialRegisterPANIValue.contains("1"));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_INTEGRITY_ALGORITHM:
                    Log.d(TAG, "MSG_GET_INTEGRITY_ALGORITHM");
                    try {
                        String getIntegrityAlgorithm = teleApi.volteOperatorSettings().getIntegrityAlgorithm(mSim);
                        int value = Integer.parseInt(getIntegrityAlgorithm);
                        String[] arr = mContext.getResources().getStringArray(R.array.integrity_algorithm_str);
                        String values = IntStream.range(0, arr.length).filter( i -> (value & (1 << i)) != 0 )
                            .mapToObj( i -> arr[i]).collect(Collectors.joining("/"));
                        mUiThread.post(() -> {
                            Log.d(TAG, "MSG_GET_INTEGRITY_ALGORITHM : " + values);
                            mIntegrityAlgorithm.setSummary(values);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(() -> {
                            mIntegrityAlgorithm.setEnabled(false);
                            mIntegrityAlgorithm.setSummary(R.string.feature_abnormal);
                        });
                    }
                    break;
                case MSG_SET_INTEGRITY_ALGORITHM:
                    Log.d(TAG, "MSG_SET_INTEGRITY_ALGORITHM");
                    View view = LayoutInflater.from(mContext).inflate(
                            R.layout.volte_voice_code, null);
                    LinearLayout line = (LinearLayout) view
                            .findViewById(R.id.linearlayout_main);
                    if (mVolteViewList == null) {
                        mVolteViewList = new ArrayList<CheckBox>();
                    } else {
                        mVolteViewList.clear();
                    }
                    for (int i = 0; i < 2; i++) {
                        CheckBox checkbox = new CheckBox(mContext);
                        checkbox.setText(mContext.getResources().getStringArray(
                                R.array.integrity_algorithm_str)[i]);
                        line.addView(checkbox);
                        mVolteViewList.add(checkbox);
                    }

                    try {
                        String getIntegrityAlgorithm = teleApi.volteOperatorSettings().getIntegrityAlgorithm(mSim);
                        final int voiceCodeTypeValue = Integer
                                    .valueOf(getIntegrityAlgorithm);
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    popDialog(view, MSG_SET_INTEGRITY_ALGORITHM);
                    break;
                case MSG_GET_CIPHER_ALGORITHM:
                    Log.d(TAG, "MSG_GET_CIPHER_ALGORITHM");
                    try {
                        String getCipherAlgorithm = teleApi.volteOperatorSettings().getCipherAlgorithm(mSim);
                        int value = Integer.valueOf(getCipherAlgorithm);
                        StringBuffer sb = new StringBuffer();
                        for (int i = 0; i < mContext.getResources().getStringArray(
                                R.array.cipher_algorithm_str).length; i++) {
                            if ((value & (1 << i)) != 0) {
                                if (!("".equals(sb.toString()))) {
                                    sb.append("/");
                                }
                                sb.append(mContext.getResources()
                                        .getStringArray(R.array.cipher_algorithm_str)[i]);
                            }
                        }
                        final String summary = sb.toString();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "MSG_GET_CIPHER_ALGORITHM : " + summary);
                                mCipherAlgorithm.setSummary(summary);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mCipherAlgorithm.setEnabled(false);
                                mCipherAlgorithm
                                        .setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_CIPHER_ALGORITHM:
                    Log.d(TAG, "MSG_SET_CIPHER_ALGORITHM");
                    View view1 = LayoutInflater.from(mContext).inflate(
                            R.layout.volte_voice_code, null);
                    LinearLayout line1 = (LinearLayout) view1
                            .findViewById(R.id.linearlayout_main);
                    if (mVolteViewList == null) {
                        mVolteViewList = new ArrayList<CheckBox>();
                    } else {
                        mVolteViewList.clear();
                    }
                    for (int i = 0; i < 3; i++) {
                        CheckBox checkbox = new CheckBox(mContext);
                        checkbox.setText(mContext.getResources().getStringArray(
                                R.array.cipher_algorithm_str)[i]);
                        line1.addView(checkbox);
                        mVolteViewList.add(checkbox);
                    }
                    try {
                        String getCipherAlgorithm = teleApi.volteOperatorSettings().getCipherAlgorithm(mSim);
                        final int cipherAlgorithmValue = Integer
                                .valueOf(getCipherAlgorithm);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < mVolteViewList.size(); i++) {
                                    if ((cipherAlgorithmValue & (1 << i)) != 0) {
                                        mVolteViewList.get(i).setChecked(true);
                                    } else {
                                        mVolteViewList.get(i).setChecked(false);
                                    }
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    popDialog(view1, MSG_SET_CIPHER_ALGORITHM);
                    break;
                case MSG_GET_REG_SUBSCRIBE_EXPIRE:
                    try {
                        String RegRegSubscribValue = teleApi.volteOperatorSettings().getRegSubscrib(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mRegSubscribExpire.setEnabled(true);
                                mRegSubscribExpire.setSummary(RegRegSubscribValue);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mRegSubscribExpire.setEnabled(false);
                                mRegSubscribExpire.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_REG_SUBSCRIBE_EXPIRE:
                    final String setRegSubscribe = (String) msg.obj;
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "31,1," + "\"" + setRegSubscribe + "\"";
                    try {
                        teleApi.volteOperatorSettings().setRegSubscrib(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Editor editor = mSharePref.edit();
                                editor.putString(KEY_REG_SUBSCRIB_EXPIRE + mSim, setRegSubscribe);
                                editor.commit();
                                mRegSubscribExpire.setSummary(setRegSubscribe);
                                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_MWI_SUBSCRIBE_EXPIRE:
                    try {
                        String MwiSubscribValue = teleApi.volteOperatorSettings().getMwiSubscrib(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mMwiSubscribExpire.setSummary(MwiSubscribValue);
                                mMwiSubscribExpire.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mMwiSubscribExpire.setEnabled(false);
                                mMwiSubscribExpire.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_MWI_SUBSCRIBE_EXPIRE:
                    final String setMWISubscribe = (String) msg.obj;
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "35,1," + "\"" + setMWISubscribe + "\"";
                    try {
                        teleApi.volteOperatorSettings().setMwiSubscrib(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Editor editor = mSharePref.edit();
                                editor.putString(KEY_MWI_SUBSCRIBE_EXPIRE + mSim, setMWISubscribe);
                                editor.commit();
                                mMwiSubscribExpire.setSummary(setMWISubscribe);
                                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_MTU:
                    try {
                        String MTUValue = teleApi.volteOperatorSettings().getMtuValue(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mMTU.setSummary(MTUValue);
                                mMTU.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mMTU.setEnabled(false);
                                mMTU.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_MTU:
                    final String setMTU = (String) msg.obj;
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "86,1," + "\"" + setMTU + "\"";
                    try {
                        teleApi.volteOperatorSettings().setMtuValue(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Editor editor = mSharePref.edit();
                                editor.putString(KEY_MTU + mSim, setMTU);
                                editor.commit();
                                mMTU.setSummary(setMTU);
                                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_PCSCFPORT:
                    try {
                        String PcscfPortValue = teleApi.volteOperatorSettings().getPcscfPort(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mPcscfPort.setSummary(PcscfPortValue);
                                mPcscfPort.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mPcscfPort.setEnabled(false);
                                mPcscfPort.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_PCSCFPORT:
                    final String setPcscfPort = (String) msg.obj;
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "87,1," + "\"" + setPcscfPort + "\"";
                    try {
                        teleApi.volteOperatorSettings().setPcscfPort(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Editor editor = mSharePref.edit();
                                editor.putString(KEY_PCSCF_PORT + mSim, setPcscfPort);
                                editor.commit();
                                mPcscfPort.setSummary(setPcscfPort);
                                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_REG_EXPIRE:
                    try {
                        String RegExpireValue = teleApi.volteOperatorSettings().getRegExpireValue(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mRegExpire.setSummary(RegExpireValue);
                                mRegExpire.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mRegExpire.setEnabled(false);
                                mRegExpire.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_REG_EXPIRE:
                    final String setRegExpire = (String) msg.obj;
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "29,1," + "\"" + setRegExpire + "\"";
                    try {
                        teleApi.volteOperatorSettings().setRegExpireValue(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Editor editor = mSharePref.edit();
                                editor.putString(KEY_REG_EXPIRE + mSim, setRegExpire);
                                editor.commit();
                                mRegExpire.setSummary(setRegExpire);
                                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_TREG_TIMER:
                    try {
                        String TregTimerValue = teleApi.volteOperatorSettings().getTregTimerValue(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mTregTimer.setSummary(TregTimerValue);
                                mTregTimer.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mTregTimer.setEnabled(false);
                                mTregTimer.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_TREG_TIMER:
                    final String setTregTimer = (String) msg.obj;
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "25,1," + "\"" + setTregTimer + "\"";
                    try {
                        teleApi.volteOperatorSettings().setTregTimerValue(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Editor editor = mSharePref.edit();
                                editor.putString(KEY_TREG_TIMER + mSim, setTregTimer);
                                editor.commit();
                                mTregTimer.setSummary(setTregTimer);
                                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_TEMERGREG_TIMER:
                    try {
                        String TemergregTimerValue = teleApi.volteOperatorSettings().getTemergregValue(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mTemergregTimer.setSummary(TemergregTimerValue);
                                mTemergregTimer.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mTemergregTimer.setEnabled(false);
                                mTemergregTimer.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_TEMERGREG_TIMER:
                    final String setTemergregTimer = (String) msg.obj;
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "26,1," + "\"" + setTemergregTimer + "\"";
                    try {
                        teleApi.volteOperatorSettings().setTemergregValue(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Editor editor = mSharePref.edit();
                                editor.putString(KEY_TEMERGREG_TIMER + mSim, setTemergregTimer);
                                editor.commit();
                                mTemergregTimer.setSummary(setTemergregTimer);
                                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_VOICE_CODE_TYPE:
                    try {
                        String getVoiceCodeType = teleApi.volteOperatorSettings().getVoiceCodeType(mSim);
                        int value = Integer.valueOf(getVoiceCodeType);
                        String TypeValue = "";
                        for (int i = 0; i < mContext.getResources().getStringArray(
                                R.array.volte_voice_code).length; i++) {
                            if ((value & (1 << i)) != 0) {
                                if (!("".equals(TypeValue))) {
                                    TypeValue += "/";
                                }
                                TypeValue += mContext.getResources()
                                        .getStringArray(R.array.volte_voice_code)[i];
                            }
                        }
                        final String summary = TypeValue;
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mVolteVoiceCode.setSummary(summary);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mVolteVoiceCode.setEnabled(false);
                                mVolteVoiceCode
                                        .setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    Message msgEvsRateSet = mIpsecHandler
                            .obtainMessage(MSG_GET_EVS_RATE_SET);
                    mIpsecHandler.sendMessage(msgEvsRateSet);
                    break;
                case MSG_SET_VOICE_CODE_TYPE:
                    View view11 = LayoutInflater.from(mContext).inflate(
                            R.layout.volte_voice_code, null);
                    LinearLayout line11 = (LinearLayout) view11
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
                        line11.addView(checkbox);
                        mVolteViewList.add(checkbox);
                    }

                    try {
                        String mVoiceCodeType = teleApi.volteOperatorSettings().getVoiceCodeType(mSim);
                        final int voiceCodeTypeValue = Integer
                                .valueOf(mVoiceCodeType);
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    popDialog(view11, MSG_SET_VOICE_CODE_TYPE);
                    break;
                case MSG_GET_MIN_BANDWIDTH:
                    try {
                        String minBandWidthValue = teleApi.volteOperatorSettings().getMinBandWidth(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mMinBandWidth.setValueIndex(Integer.valueOf(
                                        minBandWidthValue));
                                mMinBandWidth.setSummary(mMinBandWidth.getEntry());
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mMinBandWidth.setEnabled(false);
                                mMinBandWidth.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_MIN_BANDWIDTH:
                    String setMinBandWidth = (String) msg.obj;
                    final int setMinBandWidthValue = Integer.valueOf(
                            setMinBandWidth);
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "69,1," + "\"" + setMinBandWidthValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setMinBandWidth(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mMinBandWidth.setValueIndex(setMinBandWidthValue);
                                mMinBandWidth.setSummary(mMinBandWidth.getEntry());
                                Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_MAX_BANDWIDTH:
                    try {
                        String maxBandWidthValue = teleApi.volteOperatorSettings().getMaxBandWidth(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mMaxBandWidth.setValueIndex(Integer.valueOf(
                                        maxBandWidthValue));
                                mMaxBandWidth.setSummary(mMaxBandWidth.getEntry());
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mMaxBandWidth.setEnabled(false);
                                mMaxBandWidth.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_MAX_BANDWIDTH:
                    String setMaxBandWidth = (String) msg.obj;
                    final int setMaxBandWidthValue = Integer.valueOf(
                            setMaxBandWidth);
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "70,1," + "\"" + setMaxBandWidthValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setMaxBandWidth(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mMaxBandWidth.setValueIndex(setMaxBandWidthValue);
                                mMaxBandWidth.setSummary(mMaxBandWidth.getEntry());
                                Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_MIN_BITRATE:
                    try {
                        String minBitRateValue = teleApi.volteOperatorSettings().getMinBitRate(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mMinBitRate.setValueIndex(Integer.valueOf(
                                        minBitRateValue));
                                mMinBitRate.setSummary(mMinBitRate.getEntry());
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mMinBitRate.setEnabled(false);
                                mMinBitRate.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_MIN_BITRATE:
                    String setMinBitRate = (String) msg.obj;
                    final int setMinBitRateValue = Integer.valueOf(setMinBitRate)
                            ;
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "71,1," + "\"" + setMinBitRateValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setMinBitRate(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mMinBitRate.setValueIndex(setMinBitRateValue);
                                mMinBitRate.setSummary(mMinBitRate.getEntry());
                                Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_MAX_BITRATE:
                    try {
                        String maxBitRateValue = teleApi.volteOperatorSettings().getMaxBitRate(mSim);
                                                mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mMaxBitRate.setValueIndex(Integer.valueOf(
                                        maxBitRateValue));
                                mMaxBitRate.setSummary(mMaxBitRate.getEntry());
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mMaxBitRate.setEnabled(false);
                                mMaxBitRate.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_MAX_BITRATE:
                    String setMaxBitRate = (String) msg.obj;
                    final int setMaxBitRateValue = Integer.valueOf(setMaxBitRate)
                            ;
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "72,1," + "\"" + setMaxBitRateValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setMaxBitRate(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mMaxBitRate.setValueIndex(setMaxBitRateValue);
                                mMaxBitRate.setSummary(mMaxBitRate.getEntry());
                                Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_DEFAULT_BITRATE:
                    try {
                        String defaultBitRateValue = teleApi.volteOperatorSettings().getDefaultBitRate(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mDefaultBitRate.setValueIndex(Integer.valueOf(
                                        defaultBitRateValue));
                                mDefaultBitRate.setSummary(mDefaultBitRate
                                        .getEntry());
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mDefaultBitRate.setEnabled(false);
                                mDefaultBitRate
                                        .setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_DEFAULT_BITRATE:
                    String setDefaultBitRate = (String) msg.obj;
                    final int setDefaultBitRateValue = Integer.valueOf(
                            setDefaultBitRate);
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "77,1," + "\"" + setDefaultBitRateValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setDefaultBitRate(mResp, mSim);
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
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_EVS_RATE_SET:
                    try {
                        String getEvsRateSet = teleApi.volteOperatorSettings().getEvsRate(mSim);
                        int evs_rs_value = Integer.valueOf(getEvsRateSet);
                        String TypeValue = "";
                        for (int i = 0; i < mContext.getResources().getStringArray(
                                R.array.evs_wb_bitrate_set).length; i++) {
                            if ((evs_rs_value & (1 << i)) != 0) {
                                if (!("".equals(TypeValue))) {
                                    TypeValue += "/";
                                }
                                TypeValue += mContext.getResources()
                                        .getStringArray(R.array.evs_wb_bitrate_set)[i];
                            }
                        }
                        final String summary = TypeValue;
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mEvsRateSet.setSummary(summary);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mEvsRateSet.setEnabled(false);
                                mEvsRateSet.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    Message msgWbRateSet = mIpsecHandler
                            .obtainMessage(MSG_GET_WB_RATE_SET);
                    mIpsecHandler.sendMessage(msgWbRateSet);
                    break;
                case MSG_SET_EVS_RATE_SET:  //EVS AMR-WB IO :  Mode Set
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
                    try {
                        String mEvsRateSet = teleApi.volteOperatorSettings().getEvsRate(mSim);
                        final int EvsRateSetValue = Integer.valueOf(mEvsRateSet);
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    popDialog(view, MSG_SET_EVS_RATE_SET);
                    break;
                case MSG_GET_EVS_DEFAULT_BITRATE:  //EVS AMR-WB IO :  Default Encoding Bitrate
                    try {
                        String evsDefBrValue = teleApi.volteOperatorSettings().getEvsDefaultBitRate(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mEvsDefaultBR.setValueIndex(Integer.valueOf(
                                        evsDefBrValue));
                                mEvsDefaultBR.setSummary(mEvsDefaultBR.getEntry());
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mEvsDefaultBR.setEnabled(false);
                                mEvsDefaultBR.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_EVS_DEFAULT_BITRATE:
                    String setEvsDefaultBr = (String) msg.obj;
                    final int setEvsDefaultBrValue = Integer.valueOf(
                            setEvsDefaultBr);
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "76,1," + "\"" + setEvsDefaultBrValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setEvsDefaultBitRate(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mEvsDefaultBR.setValueIndex(setEvsDefaultBrValue);
                                mEvsDefaultBR.setSummary(mEvsDefaultBR.getEntry());
                                Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_WB_RATE_SET:
                    try {
                        String getWbRateSet = teleApi.volteOperatorSettings().getWbRate(mSim);
                        int wb_rs_value = Integer.valueOf(getWbRateSet);
                        String TypeValue = "";
                        for (int i = 0; i < mContext.getResources().getStringArray(
                                R.array.evs_wb_bitrate_set).length; i++) {
                            if ((wb_rs_value & (1 << i)) != 0) {
                                if (!("".equals(TypeValue))) {
                                    TypeValue += "/";
                                }
                                TypeValue += mContext.getResources()
                                        .getStringArray(R.array.evs_wb_bitrate_set)[i];
                            }
                        }
                        final String summary = TypeValue;
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mWbRateSet.setSummary(summary);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mWbRateSet.setEnabled(false);
                                mWbRateSet.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    Message msgNbRateSet = mIpsecHandler
                            .obtainMessage(MSG_GET_NB_RATE_SET);
                    mIpsecHandler.sendMessage(msgNbRateSet);
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
                                                for (int i = 0; i < mVolteViewList.size() - 1; i++) {
                                                    mVolteViewList.get(i)
                                                            .setChecked(true);
                                                }
                                            } else {
                                                for (int i = 0; i < mVolteViewList.size() - 1; i++) {
                                                    mVolteViewList.get(i)
                                                            .setChecked(false);
                                                }
                                            }
                                        }
                                    });
                    try {
                        String mWbRateSet = teleApi.volteOperatorSettings().getWbRate(mSim);
                        final int WbRateSetValue = Integer.valueOf(mWbRateSet);
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    popDialog(view, MSG_SET_WB_RATE_SET);
                    break;
                case MSG_GET_WB_DEFAULT_BITRATE:
                    try {
                        String wbDefBrValue = teleApi.volteOperatorSettings().getWbDefaultRate(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mWbDefaultBR.setValueIndex(Integer.valueOf(
                                        wbDefBrValue));
                                mWbDefaultBR.setSummary(mWbDefaultBR.getEntry());
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mWbDefaultBR.setEnabled(false);
                                mWbDefaultBR.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_WB_DEFAULT_BITRATE:
                    String setWbDefaultBr = (String) msg.obj;
                    final int setWbDefaultBrValue = Integer.valueOf(setWbDefaultBr)
                            ;
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "75,1," + "\"" + setWbDefaultBrValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setWbDefaultRate(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mWbDefaultBR.setValueIndex(setWbDefaultBrValue);
                                mWbDefaultBR.setSummary(mWbDefaultBR.getEntry());
                                Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_NB_RATE_SET:
                    try {
                        String getNbRateSet = teleApi.volteOperatorSettings().getNbRate(mSim);
                        int nb_rs_value = Integer.valueOf(getNbRateSet);
                        String TypeValue = "";
                        for (int i = 0; i < mContext.getResources().getStringArray(
                                R.array.nb_bitrate_set).length; i++) {
                            if ((nb_rs_value & (1 << i)) != 0) {
                                if (!("".equals(TypeValue))) {
                                    TypeValue += "/";
                                }
                                TypeValue += mContext.getResources()
                                        .getStringArray(R.array.nb_bitrate_set)[i];
                            }
                        }
                        final String summary = TypeValue;
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mNbRateSet.setSummary(summary);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
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
                    try {
                        String mNbRateSet = teleApi.volteOperatorSettings().getNbRate(mSim);
                        final int NbRateSetValue = Integer.valueOf(mNbRateSet);
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    popDialog(view, MSG_SET_NB_RATE_SET);
                    break;
                case MSG_GET_NB_DEFAULT_BITRATE:
                    try {
                        String nbDefBrValue = teleApi.volteOperatorSettings().getNbDefaultRate(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mNbDefaultBR.setValueIndex(Integer.valueOf(
                                        nbDefBrValue));
                                mNbDefaultBR.setSummary(mNbDefaultBR.getEntry());
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mNbDefaultBR.setEnabled(false);
                                mNbDefaultBR.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_NB_DEFAULT_BITRATE:
                    String setNbDefaultBr = (String) msg.obj;
                    final int setNbDefaultBrValue = Integer.valueOf(setNbDefaultBr)
                            ;
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "74,1," + "\"" + setNbDefaultBrValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setNbDefaultRate(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mNbDefaultBR.setValueIndex(setNbDefaultBrValue);
                                mNbDefaultBR.setSummary(mNbDefaultBR.getEntry());
                                Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_CHANNEL_AWARE_MODE:
                    try {
                        String channelAwareMode = teleApi.volteOperatorSettings().getChannelAwarkMode(mSim);
                        final int tempValue = changeValueToIndex(channelAwareMode);
                        Log.d(TAG, "tempValue is " + tempValue);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mChannelAwareMode.setValueIndex(tempValue);
                                mChannelAwareMode.setSummary(mChannelAwareMode
                                        .getEntry());
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
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
                            ;
                    String tempChannelAware = changeIndexToValue(Integer.valueOf(
                            setAwreMde));
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "73,1," + "\"" + tempChannelAware + "\"";
                    try {
                        teleApi.volteOperatorSettings().setChannelAwarkMode(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mChannelAwareMode.setValueIndex(awareModevalue);
                                mChannelAwareMode.setSummary(mChannelAwareMode
                                        .getEntry());
                                Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_MO_AMR_OA:
                    try {
                        String moAmrOaValue = teleApi.volteOperatorSettings().getMoAmrOa(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mMoAmrOaMode.setValueIndex(Integer.valueOf(
                                        moAmrOaValue));
                                mMoAmrOaMode.setSummary(mMoAmrOaMode
                                        .getEntry());
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mMoAmrOaMode.setEnabled(false);
                                mMoAmrOaMode
                                        .setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_MO_AMR_OA:
                    String setMoAmrOaMode = (String) msg.obj;
                    final int setMoAmrOaModeValue = Integer.valueOf(
                            setMoAmrOaMode);
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "78,1," + "\"" + setMoAmrOaModeValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setMoAmrOa(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mMoAmrOaMode
                                        .setValueIndex(setMoAmrOaModeValue);
                                mMoAmrOaMode.setSummary(mMoAmrOaMode
                                        .getEntry());
                                Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_NAMED_TEL_EVENT:
                    try {
                        String mGetNamedTelEventValue = teleApi.volteOperatorSettings().getNamedTelEvent(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                boolean open = mGetNamedTelEventValue.contains("1");
                                mNamedTelEvent.setChecked(open);
                                mNamedTelEvent.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mNamedTelEvent.setEnabled(false);
                                mNamedTelEvent.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_NAMED_TEL_EVENT:
                    String mSetNamedTelEventValue = (String) msg.obj;
                    Log.d(TAG, "MSG_SET_NAMED_TEL_EVENT mSetNamedTelEventValue: " + mSetNamedTelEventValue);
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "79,1," + "\"" + mSetNamedTelEventValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setNamedTelEvent(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mNamedTelEvent.setChecked(mSetNamedTelEventValue.contains("1"));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_AUDIO_RTP_TIMEOUT:
                    try {
                        String mAudioRTPTimeoutValue = teleApi.volteOperatorSettings().getAudioRTPTimeout(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mAudioRTPTimeout.setSummary(mAudioRTPTimeoutValue);
                                mAudioRTPTimeout.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mAudioRTPTimeout.setEnabled(false);
                                mAudioRTPTimeout.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_AUDIO_RTP_TIMEOUT:
                    final String setAudioRTPTimeout = (String) msg.obj;
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "45,1," + "\"" + setAudioRTPTimeout + "\"";
                    try {
                        teleApi.volteOperatorSettings().setAudioRTPTimeout(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Editor editor = mSharePref.edit();
                                editor.putString(KEY_AUDIO_RTP_TIMEOUT + mSim, setAudioRTPTimeout);
                                editor.commit();
                                mAudioRTPTimeout.setSummary(setAudioRTPTimeout);
                                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_AUDIO_RTCP_TIME:
                    try {
                        String mAudioRTCPTimeValue = teleApi.volteOperatorSettings().getAudioRTCPTime(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mAudioRTCPTimeInterval.setSummary(mAudioRTCPTimeValue);
                                mAudioRTCPTimeInterval.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mAudioRTCPTimeInterval.setEnabled(false);
                                mAudioRTCPTimeInterval.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_AUDIO_RTCP_TIME:
                    final String setAudioRTCPTimeInterval = (String) msg.obj;
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "47,1," + "\"" + setAudioRTCPTimeInterval + "\"";
                    try {
                        teleApi.volteOperatorSettings().setAudioRTCPTime(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Editor editor = mSharePref.edit();
                                editor.putString(KEY_AUDIO_RTCP_TIME_INTERNAL + mSim, setAudioRTCPTimeInterval);
                                editor.commit();
                                mAudioRTCPTimeInterval.setSummary(setAudioRTCPTimeInterval);
                                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_AUDIO_RS:
                    try {
                        String mAudioRSValue = teleApi.volteOperatorSettings().getAudioRSValue(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mAudioRS.setSummary(mAudioRSValue);
                                mAudioRS.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mAudioRS.setEnabled(false);
                                mAudioRS.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_AUDIO_RS:
                    final String setAudioRS = (String) msg.obj;
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "89,1," + "\"" + setAudioRS + "\"";
                    try {
                        teleApi.volteOperatorSettings().setAudioRSValue(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Editor editor = mSharePref.edit();
                                editor.putString(KEY_AUDIO_RS + mSim, setAudioRS);
                                editor.commit();
                                mAudioRS.setSummary(setAudioRS);
                                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_AUDIO_RR:
                    try {
                        String mAudioRRValue = teleApi.volteOperatorSettings().getAudioRRValue(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mAudioRR.setSummary(mAudioRRValue);
                                mAudioRR.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mAudioRR.setEnabled(false);
                                mAudioRR.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_AUDIO_RR:
                    final String setAudioRR = (String) msg.obj;
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "90,1," + "\"" + setAudioRR + "\"";
                    try {
                        teleApi.volteOperatorSettings().setAudioRRValue(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Editor editor = mSharePref.edit();
                                editor.putString(KEY_AUDIO_RR + mSim, setAudioRR);
                                editor.commit();
                                mAudioRR.setSummary(setAudioRR);
                                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_VIDEO_RS:
                    try {
                        String setVideoRsValue = teleApi.volteOperatorSettings().getVideoRsValue(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mVideoRs.setSummary(setVideoRsValue);
                                mVideoRs.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mVideoRs.setEnabled(false);
                                mVideoRs.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_VIDEO_RS:
                    final String setVideoRs = (String) msg.obj;
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "91,1," + "\"" + setVideoRs + "\"";
                    try {
                        teleApi.volteOperatorSettings().setVideoRsValue(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Editor editor = mSharePref.edit();
                                editor.putString(KEY_VIDEO_RS + mSim, setVideoRs);
                                editor.commit();
                                mVideoRs.setSummary(setVideoRs);
                                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_VIDEO_RR:
                    try {
                        String mVideoRRValue = teleApi.volteOperatorSettings().getVideoRRValue(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mVideoRR.setSummary(mVideoRRValue);
                                mVideoRR.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mVideoRR.setEnabled(false);
                                mVideoRR.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_VIDEO_RR:
                    final String setVideoRR = (String) msg.obj;
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "92,1," + "\"" + setVideoRR + "\"";
                    try {
                        teleApi.volteOperatorSettings().setVideoRRValue(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Editor editor = mSharePref.edit();
                                editor.putString(KEY_VIDEO_RR + mSim, setVideoRR);
                                editor.commit();
                                mVideoRR.setSummary(setVideoRR);
                                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_TCALL_TIMER:
                    try {
                        String mTcallTimerValue = teleApi.volteOperatorSettings().getTcallTimerValue(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mTcallTimer.setSummary(mTcallTimerValue);
                                mTcallTimer.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mTcallTimer.setEnabled(false);
                                mTcallTimer.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_TCALL_TIMER:
                    final String setTcallTimer = (String) msg.obj;
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "23,1," + "\"" + setTcallTimer + "\"";
                    try {
                        teleApi.volteOperatorSettings().setTcallTimerValue(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Editor editor = mSharePref.edit();
                                editor.putString(KEY_TCALL_TIMER + mSim, setTcallTimer);
                                editor.commit();
                                mTcallTimer.setSummary(setTcallTimer);
                                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_RINGRING_TIMER:
                    try {
                        String mRingingTimerValue = teleApi.volteOperatorSettings().getRingingTimerValue(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mRingingTimer.setSummary(mRingingTimerValue);
                                mRingingTimer.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mRingingTimer.setEnabled(false);
                                mRingingTimer.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_RINGRING_TIMER:
                    final String setRingingTimer = (String) msg.obj;
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "48,1," + "\"" + setRingingTimer + "\"";
                    try {
                        teleApi.volteOperatorSettings().setRingingTimerValue(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Editor editor = mSharePref.edit();
                                editor.putString(KEY_RINGRING_TIMER + mSim, setRingingTimer);
                                editor.commit();
                                mRingingTimer.setSummary(setRingingTimer);
                                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_RINGBACK_TIMER:
                    try {
                        String mRingbackTimerValue = teleApi.volteOperatorSettings().getRingbackTimer(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mRingbackTimer.setSummary(mRingbackTimerValue);
                                mRingbackTimer.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mRingbackTimer.setEnabled(false);
                                mRingbackTimer.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_RINGBACK_TIMER:
                    final String setRingbackTimer = (String) msg.obj;
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "49,1," + "\"" + setRingbackTimer + "\"";
                    try {
                        teleApi.volteOperatorSettings().setRingbackTimer(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Editor editor = mSharePref.edit();
                                editor.putString(KEY_RINGBACK_TIMER + mSim, setRingbackTimer);
                                editor.commit();
                                mRingbackTimer.setSummary(setRingbackTimer);
                                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_NO_ANSWER_TIMER:
                    try {
                        String mNoAnswerTimerValue = teleApi.volteOperatorSettings().getNoAnswerTimer(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mNoAnswerTimer.setSummary(mNoAnswerTimerValue);
                                mNoAnswerTimer.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mNoAnswerTimer.setEnabled(false);
                                mNoAnswerTimer.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_NO_ANSWER_TIMER:
                    final String setNoAnswerTimer = (String) msg.obj;
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "50,1," + "\"" + setNoAnswerTimer + "\"";
                    try {
                        teleApi.volteOperatorSettings().setNoAnswerTimer(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Editor editor = mSharePref.edit();
                                editor.putString(KEY_NO_ANSWER_TIMER + mSim, setNoAnswerTimer);
                                editor.commit();
                                mNoAnswerTimer.setSummary(setNoAnswerTimer);
                                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_SESSION_TIMER:
                    try {
                        String mSessionTimerValue = teleApi.volteOperatorSettings().getSessionTimer(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mSessionTimer.setSummary(mSessionTimerValue);
                                mSessionTimer.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mSessionTimer.setEnabled(false);
                                mSessionTimer.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_SESSION_TIMER:
                    final String setSessionTimer = (String) msg.obj;
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "37,1," + "\"" + setSessionTimer + "\"";
                    try {
                        teleApi.volteOperatorSettings().setSessionTimer(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Editor editor = mSharePref.edit();
                                editor.putString(KEY_SESSION_TIMER + mSim, setSessionTimer);
                                editor.commit();
                                mSessionTimer.setSummary(setSessionTimer);
                                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_MIN_SE_TIMER:
                    try {
                        String minSeTimerValue = teleApi.volteOperatorSettings().getMinSeTimer(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mMinSeTimer.setSummary(minSeTimerValue);
                                mMinSeTimer.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mMinSeTimer.setEnabled(false);
                                mMinSeTimer.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_MIN_SE_TIMER:
                    final String setMinSeTimer = (String) msg.obj;
                    try {
                        teleApi.volteOperatorSettings().setMinSeTimer(setMinSeTimer, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mMinSeTimer.setSummary(setMinSeTimer);
                                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_MT_SESSION_TIMER:
                    try {
                        String mMTSessionTimerValue = teleApi.volteOperatorSettings().getMTSessionTimer(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mMTSessionTimer.setSummary(mMTSessionTimerValue);
                                mMTSessionTimer.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mMTSessionTimer.setEnabled(false);
                                mMTSessionTimer.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_MT_SESSION_TIMER:
                    final String setMTSessionTimer = (String) msg.obj;
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "39,1," + "\"" + setMTSessionTimer + "\"";
                    try {
                        teleApi.volteOperatorSettings().setMTSessionTimer(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Editor editor = mSharePref.edit();
                                editor.putString(KEY_MT_SESSION_TIMER + mSim, setMTSessionTimer);
                                editor.commit();
                                mMTSessionTimer.setSummary(setMTSessionTimer);
                                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_CONFERENCE_URI:
                    try {
                        String mConferenceURIValue = teleApi.volteOperatorSettings().getConferenceURI(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mConferenceURI.setSummary(mConferenceURIValue);
                                mConferenceURI.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mConferenceURI.setEnabled(false);
                                mConferenceURI.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_CONFERENCE_URI:
                    final String setConferenceURI = (String) msg.obj;
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "53,1," + "\"" + setConferenceURI + "\"";
                    try {
                        teleApi.volteOperatorSettings().setConferenceURI(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Editor editor = mSharePref.edit();
                                editor.putString(KEY_CONFERENCE_URI + mSim, setConferenceURI);
                                editor.commit();
                                mConferenceURI.setSummary(setConferenceURI);
                                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_VIDEO_CODE_TYPE:
                    Log.d(TAG, "MSG_GET_VIDEO_CODE_TYPE");
                    try {
                        String getVIDEOCodeType = teleApi.volteOperatorSettings().getVideoCodeType(mSim);
                        int value = Integer.valueOf(getVIDEOCodeType);
                        String TypeValue = "";
                        for (int i = 0; i < mContext.getResources().getStringArray(
                                R.array.video_codec_str).length; i++) {
                            if ((value & (1 << i)) != 0) {
                                if (!("".equals(TypeValue))) {
                                    TypeValue += "/";
                                }
                                TypeValue += mContext.getResources()
                                        .getStringArray(R.array.video_codec_str)[i];
                            }
                        }
                        final String summary = TypeValue;
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "MSG_GET_VIDEO_CODE_TYPE : " + summary);
                                mVolteVideoCode.setSummary(summary);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mVolteVideoCode.setEnabled(false);
                                mVolteVideoCode
                                        .setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_VIDEO_CODE_TYPE:
                    Log.d(TAG, "MSG_SET_VIDEO_CODE_TYPE");
                    View viewVideo = LayoutInflater.from(mContext).inflate(
                            R.layout.volte_voice_code, null);
                    LinearLayout lineVideo = (LinearLayout) viewVideo
                            .findViewById(R.id.linearlayout_main);
                    if (mVolteViewList == null) {
                        mVolteViewList = new ArrayList<CheckBox>();
                    } else {
                        mVolteViewList.clear();
                    }
                    for (int i = 0; i < 2; i++) {
                        CheckBox checkbox = new CheckBox(mContext);
                        checkbox.setText(mContext.getResources().getStringArray(
                                R.array.video_codec_str)[i]);
                        lineVideo.addView(checkbox);
                        mVolteViewList.add(checkbox);
                    }

                    try {
                        String mVoiceCodeType = teleApi.volteOperatorSettings().getVideoCodeType(mSim);
                        final int voiceCodeTypeValue = Integer
                                .valueOf(mVoiceCodeType);
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    popDialog(viewVideo, MSG_SET_VIDEO_CODE_TYPE);
                    break;
                case MSG_GET_RELIABLE_180:
                    try {
                        String mGetReliable180Value = teleApi.volteOperatorSettings().getReliable180(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                boolean open = mGetReliable180Value.contains("1");
                                mReliable180Switch.setChecked(open);
                                mReliable180Switch.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mReliable180Switch.setEnabled(false);
                                mReliable180Switch.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_RELIABLE_180:
                    String mSetReliable180Value = (String) msg.obj;
                    Log.d(TAG, "MSG_SET_RELIABLE_180 mSetReliable180Value: " + mSetReliable180Value);
                    String mResp = engconstents.ENG_AT_SPVOLTEENG + "100,1," + "\"" + mSetReliable180Value + "\"";
                    try {
                        teleApi.volteOperatorSettings().setReliable180(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mReliable180Switch.setChecked(mSetReliable180Value.contains("1"));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_DIRECT_ALERTING:
                    try {
                        String mGetDirectAlertingValue = teleApi.volteOperatorSettings().getDirectAlerting(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                boolean open = mGetDirectAlertingValue.contains("1");
                                mDirectAlertingSwitch.setChecked(open);
                                mDirectAlertingSwitch.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mDirectAlertingSwitch.setEnabled(false);
                                mDirectAlertingSwitch.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_DIRECT_ALERTING:
                    String mSetDirectAlertingValue = (String) msg.obj;
                    Log.d(TAG, "MSG_SET_DIRECT_ALERTING mSetDirectAlertingValue: " + mSetDirectAlertingValue);
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "99,1," + "\"" + mSetDirectAlertingValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setDirectAlerting(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mDirectAlertingSwitch.setChecked(mSetDirectAlertingValue.contains("1"));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_RESOURCE_ALWAYS:
                    try {
                        String mGetResourceAlwaysValue = teleApi.volteOperatorSettings().getResourceAlways(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                boolean open = mGetResourceAlwaysValue.contains("1");
                                mResourceAlwaysSwitch.setChecked(open);
                                mResourceAlwaysSwitch.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mResourceAlwaysSwitch.setEnabled(false);
                                mResourceAlwaysSwitch.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_RESOURCE_ALWAYS:
                    String mSetResourceAlwaysValue = (String) msg.obj;
                    Log.d(TAG, "MSG_SET_RESOURCE_ALWAYS mSetResourceAlwaysValue: " + mSetResourceAlwaysValue);
                    String mSetResourceAlways = engconstents.ENG_AT_SPVOLTEENG + "102,1," + "\"" + mSetResourceAlwaysValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setResourceAlways(mSetResourceAlways, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mResourceAlwaysSwitch.setChecked(mSetResourceAlwaysValue.contains("1"));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_PRECONDITION:
                    try {
                        String mGetPreconditionValue = teleApi.volteOperatorSettings().getPrecondition(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                boolean open = mGetPreconditionValue.contains("1");
                                mPreconditionSwitch.setChecked(open);
                                mPreconditionSwitch.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mPreconditionSwitch.setEnabled(false);
                                mPreconditionSwitch.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_PRECONDITION:
                    String mSetPreconditionValue = (String) msg.obj;
                    Log.d(TAG, "MSG_SET_PRECONDITION mSetPreconditionValue: " + mSetPreconditionValue);
                    String mSetPrecondition = engconstents.ENG_AT_SPVOLTEENG + "93,1," + "\"" + mSetPreconditionValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setPrecondition(mSetPrecondition, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mPreconditionSwitch.setChecked(mSetPreconditionValue.contains("1"));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_VIDEO_UPGRADE:
                    try {
                        String mGetVideoUpgradeValue = teleApi.volteOperatorSettings().getVideoUpgrade(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                boolean open = mGetVideoUpgradeValue.contains("1");
                                mVideoUpgradeSwitch.setChecked(open);
                                mVideoUpgradeSwitch.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mVideoUpgradeSwitch.setEnabled(false);
                                mVideoUpgradeSwitch.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_VIDEO_UPGRADE:
                    String mSetVideoUpgradeValue = (String) msg.obj;
                    Log.d(TAG, "MSG_SET_VIDEO_UPGRADE mSetVideoUpgradeValue: " + mSetVideoUpgradeValue);
                    String mSetVideoUpgrade = engconstents.ENG_AT_SPVOLTEENG + "94,1," + "\"" + mSetVideoUpgradeValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setVideoUpgrade(mSetVideoUpgrade, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mVideoUpgradeSwitch.setChecked(mSetVideoUpgradeValue.contains("1"));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_VIDEO_EARLY_MEDIA:
                    try {
                        String mGetVideoEarlyValue = teleApi.volteOperatorSettings().getVideoEarly(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                boolean open = mGetVideoEarlyValue.contains("1");
                                mVideoEarlySwitch.setChecked(open);
                                mVideoEarlySwitch.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mVideoEarlySwitch.setEnabled(false);
                                mVideoEarlySwitch.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_VIDEO_EARLY_MEDIA:
                    String mSetVideoEarlyValue = (String) msg.obj;
                    Log.d(TAG, "MSG_SET_VIDEO_EARLY_MEDIA mSetVideoEarlyValue: " + mSetVideoEarlyValue);
                    String mSetVideoEarly = engconstents.ENG_AT_SPVOLTEENG + "51,1," + "\"" + mSetVideoEarlyValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setVideoEarly(mSetVideoEarly, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mVideoEarlySwitch.setChecked(mSetVideoEarlyValue.contains("1"));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_ALERTING:
                    try {
                        String mGetAlertingValue = teleApi.volteOperatorSettings().getAlertingValue(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                boolean open = mGetAlertingValue.contains("1");
                                mAlertingSwitch.setChecked(open);
                                mAlertingSwitch.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mAlertingSwitch.setEnabled(false);
                                mAlertingSwitch.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_ALERTING:
                    String mSetAlertingValue = (String) msg.obj;
                    Log.d(TAG, "MSG_SET_ALERTING mSetAlertingValue: " + mSetAlertingValue);
                    String mSetAlerting = engconstents.ENG_AT_SPVOLTEENG + "80,1," + "\"" + mSetAlertingValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setAlertingValue(mSetAlerting, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mAlertingSwitch.setChecked(mSetAlertingValue.contains("1"));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_MID_CALL:
                    try {
                        String mGetMidCallSwitchValue = teleApi.volteOperatorSettings().getMidCallSwitch(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                boolean open = mGetMidCallSwitchValue.contains("1");
                                mMidCallSwitch.setChecked(open);
                                mMidCallSwitch.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mMidCallSwitch.setEnabled(false);
                                mMidCallSwitch.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_MID_CALL:
                    String mSetMidCallSwitchValue = (String) msg.obj;
                    Log.d(TAG, "MSG_SET_MID_CALL mSetMidCallSwitchValue: " + mSetMidCallSwitchValue);
                    String mSetMidCallSwitch = engconstents.ENG_AT_SPVOLTEENG + "81,1," + "\"" + mSetMidCallSwitchValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setMidCallSwitch(mSetMidCallSwitch, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mMidCallSwitch.setChecked(mSetMidCallSwitchValue.contains("1"));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_PER_ALERTING:
                    try {
                        String mGetPreAlertingSwitchValue = teleApi.volteOperatorSettings().getPreAlerting(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                boolean open = mGetPreAlertingSwitchValue.contains("1");
                                mPreAlertingSwitch.setChecked(open);
                                mPreAlertingSwitch.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mPreAlertingSwitch.setEnabled(false);
                                mPreAlertingSwitch.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_PER_ALERTING:
                    String mSetPreAlertingSwitchValue = (String) msg.obj;
                    Log.d(TAG, "MSG_SET_PER_ALERTING mSetPreAlertingSwitchValue: " + mSetPreAlertingSwitchValue);
                    String mSetPreAlertingSwitch = engconstents.ENG_AT_SPVOLTEENG + "82,1," + "\"" + mSetPreAlertingSwitchValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setPreAlerting(mSetPreAlertingSwitch, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mPreAlertingSwitch.setChecked(mSetPreAlertingSwitchValue.contains("1"));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }

                    break;
                case MSG_GET_RSRVCC_SUPPORT:
                    try {
                        String mGetRSRVCCSupportValue = teleApi.volteOperatorSettings().getRSRVCCSupport(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                boolean open = mGetRSRVCCSupportValue.contains("1");
                                mRSRVCCSupportSwitch.setChecked(open);
                                mRSRVCCSupportSwitch.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mRSRVCCSupportSwitch.setEnabled(false);
                                mRSRVCCSupportSwitch.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_RSRVCC_SUPPORT:
                    String mSetRSRVCCSupportValue = (String) msg.obj;
                    Log.d(TAG, "MSG_SET_RSRVCC_SUPPORT mSetRSRVCCSupportValue: " + mSetRSRVCCSupportValue);
                    String mSetRSRVCCSupport = engconstents.ENG_AT_SPVOLTEENG + "83,1," + "\"" + mSetRSRVCCSupportValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setRSRVCCSupport(mSetRSRVCCSupport, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mRSRVCCSupportSwitch.setChecked(mSetRSRVCCSupportValue.contains("1"));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_RSRVCC_ALERTING:
                    try {
                        String mGetRSRVCCAlertValue = teleApi.volteOperatorSettings().getRSRVCCAlert(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                boolean open = mGetRSRVCCAlertValue.contains("1");
                                mRSRVCCAlertSwitch.setChecked(open);
                                mRSRVCCAlertSwitch.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mRSRVCCAlertSwitch.setEnabled(false);
                                mRSRVCCAlertSwitch.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_RSRVCC_ALERTING:
                    String mSetRSRVCCAlertValue = (String) msg.obj;
                    Log.d(TAG, "MSG_SET_RSRVCC_ALERTING mSetRSRVCCAlertValue: " + mSetRSRVCCAlertValue);
                    String mSetRSRVCCAlert = engconstents.ENG_AT_SPVOLTEENG + "84,1," + "\"" + mSetRSRVCCAlertValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setRSRVCCAlert(mSetRSRVCCAlert, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mRSRVCCAlertSwitch.setChecked(mSetRSRVCCAlertValue.contains("1"));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_RSRVCC_MID_CALL:
                    try {
                        String mGetRSRVCCMidValue = teleApi.volteOperatorSettings().getRSRVCCMid(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                boolean open = mGetRSRVCCMidValue.contains("1");
                                mRSRVCCMidSwitch.setChecked(open);
                                mRSRVCCMidSwitch.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mRSRVCCMidSwitch.setEnabled(false);
                                mRSRVCCMidSwitch.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_RSRVCC_MID_CALL:
                    String mSetRSRVCCMidValue = (String) msg.obj;
                    Log.d(TAG, "MSG_SET_RSRVCC_MID_CALL mSetRSRVCCMidValue: " + mSetRSRVCCMidValue);
                    String mSetRSRVCCMid = engconstents.ENG_AT_SPVOLTEENG + "85,1," + "\"" + mSetRSRVCCMidValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setRSRVCCMid(mSetRSRVCCMid, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mRSRVCCMidSwitch.setChecked(mSetRSRVCCMidValue.contains("1"));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_VIDEO_MAX_RESOLUTION:
                    try {
                        String mVideoMaxValue = teleApi.volteOperatorSettings().getVideoMaxResolution(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mVideoMaxResolution.setValueIndex(Integer.valueOf(
                                        mVideoMaxValue));
                                mVideoMaxResolution.setSummary(mVideoMaxResolution.getEntry());
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mVideoMaxResolution.setEnabled(false);
                                mVideoMaxResolution.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_VIDEO_MAX_RESOLUTION:
                    String setVideoMaxResolution = (String) msg.obj;
                    final int setVideoMaxValue = Integer.valueOf(
                            setVideoMaxResolution);
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "106,1," + "\"" + setVideoMaxValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setVideoMaxResolution(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mVideoMaxResolution.setValueIndex(setVideoMaxValue);
                                mVideoMaxResolution.setSummary(mVideoMaxResolution.getEntry());
                                Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_NATIONAL_URL_FORMAT:
                    try {
                        String mNationalURLValue = teleApi.volteOperatorSettings().getNationalURL(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mNationalURL.setValueIndex(Integer.valueOf(
                                        mNationalURLValue));
                                mNationalURL.setSummary(mNationalURL.getEntry());
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mNationalURL.setEnabled(false);
                                mNationalURL.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_NATIONAL_URL_FORMAT:
                    String setNationalURL = (String) msg.obj;
                    final int setNationalURLValue = Integer.valueOf(
                            setNationalURL);
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "42,1," + "\"" + setNationalURLValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setNationalURL(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mNationalURL.setValueIndex(setNationalURLValue);
                                mNationalURL.setSummary(mNationalURL.getEntry());
                                Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_INTERNATIONAL_URL:
                    try {
                        String mInternationalURLValue = teleApi.volteOperatorSettings().getInternationalURL(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mInternationalURL.setValueIndex(Integer.valueOf(
                                        mInternationalURLValue));
                                mInternationalURL.setSummary(mInternationalURL.getEntry());
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mInternationalURL.setEnabled(false);
                                mInternationalURL.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_INTERNATIONAL_URL:
                    String setInternationalURL = (String) msg.obj;
                    final int setInternationalURLValue = Integer.valueOf(
                            setInternationalURL);
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "43,1," + "\"" + setInternationalURLValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setInternationalURL(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mInternationalURL.setValueIndex(setInternationalURLValue);
                                mInternationalURL.setSummary(mInternationalURL.getEntry());
                                Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_SESSION_TIMER_REFRESHER:
                    try {
                        String mTimerRefresherValue = teleApi.volteOperatorSettings().getTimerRefresher(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mSessionTimerRefresher.setValueIndex(Integer.valueOf(
                                        mTimerRefresherValue));
                                mSessionTimerRefresher.setSummary(mSessionTimerRefresher.getEntry());
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mSessionTimerRefresher.setEnabled(false);
                                mSessionTimerRefresher.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }

                    break;
                case MSG_SET_SESSION_TIMER_REFRESHER:
                    String setTimerRefresher = (String) msg.obj;
                    final int setTimerRefresherValue = Integer.valueOf(
                            setTimerRefresher);
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "38,1," + "\"" + setTimerRefresherValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setTimerRefresher(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mSessionTimerRefresher.setValueIndex(setTimerRefresherValue);
                                mSessionTimerRefresher.setSummary(mSessionTimerRefresher.getEntry());
                                Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_MT_SESSION_REFRESHER:
                    try {
                        String mMTTimerRefresherValue = teleApi.volteOperatorSettings().getMTTimerRefresher(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mMTSessionTimerRefresher.setValueIndex(Integer.valueOf(
                                        mMTTimerRefresherValue));
                                mMTSessionTimerRefresher.setSummary(mMTSessionTimerRefresher.getEntry());
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mMTSessionTimerRefresher.setEnabled(false);
                                mMTSessionTimerRefresher.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_MT_SESSION_REFRESHER:
                    String setMTTimerRefresher = (String) msg.obj;
                    final int setMTTimerRefresherValue = Integer.valueOf(
                            setMTTimerRefresher);
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "40,1," + "\"" + setMTTimerRefresherValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setMTTimerRefresher(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mMTSessionTimerRefresher.setValueIndex(setMTTimerRefresherValue);
                                mMTSessionTimerRefresher.setSummary(mMTSessionTimerRefresher.getEntry());
                                Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_VIDEO_AUDIO_MERGE:
                    try {
                        String mVideoAudioMergeValue = teleApi.volteOperatorSettings().getVideoAudioMerge(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mVideoAudioMerge.setValueIndex(Integer.valueOf(
                                        mVideoAudioMergeValue));
                                mVideoAudioMerge.setSummary(mVideoAudioMerge.getEntry());
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mVideoAudioMerge.setEnabled(false);
                                mVideoAudioMerge.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_VIDEO_AUDIO_MERGE:
                    String setVideoAudioMerge = (String) msg.obj;
                    final int setVideoAudioMergeValue = Integer.valueOf(
                            setVideoAudioMerge);
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "54,1," + "\"" + setVideoAudioMergeValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setVideoAudioMerge(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mVideoAudioMerge.setValueIndex(setVideoAudioMergeValue);
                                mVideoAudioMerge.setSummary(mVideoAudioMerge.getEntry());
                                Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_AUDIO_DYNAMIC_ENABLE:
                    try {
                        String mGetAudioDynamicValue = teleApi.volteOperatorSettings().getAudioDynamic(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                boolean open = mGetAudioDynamicValue.contains("1");
                                mAudioDynamicEnable.setChecked(open);
                                mAudioDynamicEnable.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mAudioDynamicEnable.setEnabled(false);
                                mAudioDynamicEnable.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_AUDIO_DYNAMIC_ENABLE:
                    String mSetAudioDynamicValue = (String) msg.obj;
                    Log.d(TAG, "MSG_SET_AUDIO_DYNAMIC_ENABLE mSetAudioDynamicValue: " + mSetAudioDynamicValue);
                    String mSetAudioDynamic = engconstents.ENG_AT_SPVOLTEENG + "104,1," + "\"" + mSetAudioDynamicValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setAudioDynamic(mSetAudioDynamic, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mAudioDynamicEnable.setChecked(mSetAudioDynamicValue.contains("1"));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_VIDEO_DYNAMIC_ENABLE:
                    try {
                        String mGetVideoDynamicValue = teleApi.volteOperatorSettings().getVideoDynamic(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                boolean open = mGetVideoDynamicValue.contains("1");
                                mVideoDynamicEnable.setChecked(open);
                                mVideoDynamicEnable.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mVideoDynamicEnable.setEnabled(false);
                                mVideoDynamicEnable.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_VIDEO_DYNAMIC_ENABLE:
                    String mSetVideoDynamicValue = (String) msg.obj;
                    Log.d(TAG, "MSG_SET_VIDEO_DYNAMIC_ENABLE mSetVideoDynamicValue: " + mSetVideoDynamicValue);
                    String mSetVideoDynamic = engconstents.ENG_AT_SPVOLTEENG + "20,1," + "\"" + mSetVideoDynamicValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setVideoDynamic(mSetVideoDynamic, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mVideoDynamicEnable.setChecked(mSetVideoDynamicValue.contains("1"));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_BSF_URI:
                    try {
                        String mBsfURIValue = teleApi.volteOperatorSettings().getBsfURIValue(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mBsfURI.setSummary(mBsfURIValue);
                                mBsfURI.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mBsfURI.setEnabled(false);
                                mBsfURI.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_BSF_URI:
                    final String setBsfURI = (String) msg.obj;
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "60,1," + "\"" + setBsfURI + "\"";
                    try {
                        teleApi.volteOperatorSettings().setBsfURIValue(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Editor editor = mSharePref.edit();
                                editor.putString(KEY_BSF_URI + mSim, setBsfURI);
                                editor.commit();
                                mBsfURI.setSummary(setBsfURI);
                                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_BSF_PORT:
                    try {
                        String mBsfPORTValue = teleApi.volteOperatorSettings().getBsfPORTValue(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mBsfPort.setSummary(mBsfPORTValue);
                                mBsfPort.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mBsfPort.setEnabled(false);
                                mBsfPort.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_BSF_PORT:
                    final String setBsfPort = (String) msg.obj;
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "61,1," + "\"" + setBsfPort + "\"";
                    try {
                        teleApi.volteOperatorSettings().setBsfPORTValue(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Editor editor = mSharePref.edit();
                                editor.putString(KEY_BSF_PORT + mSim, setBsfPort);
                                editor.commit();
                                mBsfPort.setSummary(setBsfPort);
                                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_XCAP_URI:
                    try {
                        String mXcapURIValue = teleApi.volteOperatorSettings().getXcapURIValue(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mXcapURI.setSummary(mXcapURIValue);
                                mXcapURI.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mXcapURI.setEnabled(false);
                                mXcapURI.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_XCAP_URI:
                    final String setXcapURI = (String) msg.obj;
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "62,1," + "\"" + setXcapURI + "\"";
                    try {
                        teleApi.volteOperatorSettings().setXcapURIValue(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Editor editor = mSharePref.edit();
                                editor.putString(KEY_XCAP_URI + mSim, setXcapURI);
                                editor.commit();
                                mXcapURI.setSummary(setXcapURI);
                                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_XCAP_PORT:
                    try {
                        String mXcapPortValue = teleApi.volteOperatorSettings().getXcapPortValue(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mXcapPort.setSummary(mXcapPortValue);
                                mXcapPort.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mXcapPort.setEnabled(false);
                                mXcapPort.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_XCAP_PORT:
                    final String setXcapPort = (String) msg.obj;
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "63,1," + "\"" + setXcapPort + "\"";
                    try {
                        teleApi.volteOperatorSettings().setXcapPortValue(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Editor editor = mSharePref.edit();
                                editor.putString(KEY_XCAP_PORT + mSim, setXcapPort);
                                editor.commit();
                                mXcapPort.setSummary(setXcapPort);
                                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_XCAP_AUID:
                    try {
                        String mXcapAuidValue = teleApi.volteOperatorSettings().getXcapAuidValue(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mXcapAuid.setSummary(mXcapAuidValue);
                                mXcapAuid.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mXcapAuid.setEnabled(false);
                                mXcapAuid.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_XCAP_AUID:
                    final String setXcapAuid = (String) msg.obj;
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "64,1," + "\"" + setXcapAuid + "\"";
                    try {
                        teleApi.volteOperatorSettings().setXcapAuidValue(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Editor editor = mSharePref.edit();
                                editor.putString(KEY_XCAP_AUID + mSim, setXcapAuid);
                                editor.commit();
                                mXcapAuid.setSummary(setXcapAuid);
                                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_LOCAL_CALL:
                    try {
                        String mGetLocalCallSwitchValue = teleApi.volteOperatorSettings().getLocalCall(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                boolean open = mGetLocalCallSwitchValue.contains("1");
                                mLocalCallSwitch.setChecked(open);
                                mLocalCallSwitch.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mLocalCallSwitch.setEnabled(false);
                                mLocalCallSwitch.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_LOCAL_CALL:
                    String mSetLocalCallSwitchValue = (String) msg.obj;
                    Log.d(TAG, "MSG_SET_LOCAL_CALL mSetLocalCallSwitchValue: " + mSetLocalCallSwitchValue);
                    String mSetLocalCallSwitch = engconstents.ENG_AT_SPVOLTEENG + "55,1," + "\"" + mSetLocalCallSwitchValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setLocalCall(mSetLocalCallSwitch, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mLocalCallSwitch.setChecked(mSetLocalCallSwitchValue.contains("1"));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_DNS_SRV_ENABLE:
                    try {
                        String mGetDnsSrvEnableValue = teleApi.volteOperatorSettings().getDnsSrvEnable(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                boolean open = mGetDnsSrvEnableValue.contains("1");
                                mDnsSrvEnableSwitch.setChecked(open);
                                mDnsSrvEnableSwitch.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mDnsSrvEnableSwitch.setEnabled(false);
                                mDnsSrvEnableSwitch.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_DNS_SRV_ENABLE:
                    String mSetDnsSrvEnableValue = (String) msg.obj;
                    Log.d(TAG, "MSG_SET_DNS_SRV_ENABLE mSetDnsSrvEnableValue: " + mSetDnsSrvEnableValue);
                    String mSetDnsSrvEnable = engconstents.ENG_AT_SPVOLTEENG + "56,1," + "\"" + mSetDnsSrvEnableValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setDnsSrvEnable(mSetDnsSrvEnable, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mDnsSrvEnableSwitch.setChecked(mSetDnsSrvEnableValue.contains("1"));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_HTTPS_ENABLE:
                    try {
                        String mGetHttpsEnableValue = teleApi.volteOperatorSettings().getHttpsEnable(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                boolean open = mGetHttpsEnableValue.contains("1");
                                mHttpsEnableSwitch.setChecked(open);
                                mHttpsEnableSwitch.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mHttpsEnableSwitch.setEnabled(false);
                                mHttpsEnableSwitch.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_HTTPS_ENABLE:
                    String mSetHttpsEnableValue = (String) msg.obj;
                    Log.d(TAG, "MSG_SET_HTTPS_ENABLE mSetHttpsEnableValue: " + mSetHttpsEnableValue);
                    String mSetHttpsEnable = engconstents.ENG_AT_SPVOLTEENG + "58,1," + "\"" + mSetHttpsEnableValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setHttpsEnable(mSetHttpsEnable, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mHttpsEnableSwitch.setChecked(mSetHttpsEnableValue.contains("1"));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_ACTIVATE_CFNL:
                    try {
                        String mGetActivateCfnlValue = teleApi.volteOperatorSettings().getActivateCfnl(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                boolean open = mGetActivateCfnlValue.contains("1");
                                mActivateCfnlSwitch.setChecked(open);
                                mActivateCfnlSwitch.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mActivateCfnlSwitch.setEnabled(false);
                                mActivateCfnlSwitch.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_ACTIVATE_CFNL:
                    String mSetActivateCfnlValue = (String) msg.obj;
                    Log.d(TAG, "MSG_SET_ACTIVATE_CFNL mSetActivateCfnlValue: " + mSetActivateCfnlValue);
                    String mSetActivateCfnl = engconstents.ENG_AT_SPVOLTEENG + "103,1," + "\"" + mSetActivateCfnlValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setActivateCfnl(mSetActivateCfnl, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mActivateCfnlSwitch.setChecked(mSetActivateCfnlValue.contains("1"));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_HTTP_PUT_MEDIA:
                    try {
                        String mGetHttpPutMediaValue = teleApi.volteOperatorSettings().getHttpPutMedia(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                boolean open = mGetHttpPutMediaValue.contains("1");
                                mHttpPutMediaSwitch.setChecked(open);
                                mHttpPutMediaSwitch.setEnabled(true);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mHttpPutMediaSwitch.setEnabled(false);
                                mHttpPutMediaSwitch.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_HTTP_PUT_MEDIA:
                    String mSetHttpPutMediaValue = (String) msg.obj;
                    Log.d(TAG, "MSG_SET_HTTP_PUT_MEDIA mSetHttpPutMediaValue: " + mSetHttpPutMediaValue);
                    String mSetHttpPutMedia = engconstents.ENG_AT_SPVOLTEENG + "59,1," + "\"" + mSetHttpPutMediaValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setHttpPutMedia(mSetHttpPutMedia, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mHttpPutMediaSwitch.setChecked(mSetHttpPutMediaValue.contains("1"));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
                    }
                    break;
                case MSG_GET_IP_PRIORITY:
                    try {
                        String mIPPriorityValue = teleApi.volteOperatorSettings().getIPPriority(mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mIPPriority.setValueIndex(Integer.valueOf(
                                        mIPPriorityValue));
                                mIPPriority.setSummary(mIPPriority.getEntry());
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mIPPriority.setEnabled(false);
                                mIPPriority.setSummary(R.string.feature_abnormal);
                            }
                        });
                    }
                    break;
                case MSG_SET_IP_PRIORITY:
                    String setIPPriority = (String) msg.obj;
                    final int setIPPriorityValue = Integer.valueOf(
                            setIPPriority);
                    mResp = engconstents.ENG_AT_SPVOLTEENG + "57,1," + "\"" + setIPPriorityValue + "\"";
                    try {
                        teleApi.volteOperatorSettings().setIPPriority(mResp, mSim);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mIPPriority.setValueIndex(setIPPriorityValue);
                                mIPPriority.setSummary(mIPPriority.getEntry());
                                Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFailToast();
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

    private void showFailToast() {
        mUiThread.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void popDialog(View view, final int dialogType) {
        int arrayId_tmp = 0;
        int titleId_tmp = 0;
        String para_tmp = "";
        Preference pref_tmp = null;
        if (dialogType == MSG_SET_INTEGRITY_ALGORITHM) {
            Log.d(TAG, "popDialog MSG_SET_INTEGRITY_ALGORITHM");
            titleId_tmp = R.string.Integrity_algorithm;
            arrayId_tmp = R.array.integrity_algorithm_str;
            para_tmp = "1";
            pref_tmp = mIntegrityAlgorithm;
        } else if (dialogType == MSG_SET_CIPHER_ALGORITHM) {
            Log.d(TAG, "popDialog MSG_SET_CIPHER_ALGORITHM");
            titleId_tmp = R.string.cipher_algorithm;
            arrayId_tmp = R.array.cipher_algorithm_str;
            para_tmp = "1";
            pref_tmp = mCipherAlgorithm;
        } else if (dialogType == MSG_SET_VOICE_CODE_TYPE) {
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
        } else if (dialogType == MSG_SET_VIDEO_CODE_TYPE) {
            titleId_tmp = R.string.video_codec_type;
            arrayId_tmp = R.array.video_codec_str;
            para_tmp = "1";
            pref_tmp = mVolteVideoCode;
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
                        if (dialogType == MSG_SET_INTEGRITY_ALGORITHM
                            || dialogType == MSG_SET_CIPHER_ALGORITHM
                            || dialogType == MSG_SET_VOICE_CODE_TYPE
                            || dialogType == MSG_SET_VIDEO_CODE_TYPE) {
                            listSize = mVolteViewList.size();
                        } else {
                            listSize = mVolteViewList.size() - 1;
                        }
                        String[] arr = mContext.getResources().getStringArray(stringArrayId);
                        int[] tempInt = {0};
                        String typeValue = IntStream.range(0, listSize).filter( i -> mVolteViewList.get(i).isChecked())
                            .peek( i -> tempInt[0] += (1 << i))
                            .mapToObj( i -> arr[i]).collect(Collectors.joining("/"));
                        int value = tempInt[0];
                        Log.d(TAG, "value=" + value + ",summary=" + typeValue);
                        if (value == 0) {
                            mUiThread.post(() -> Toast.makeText(mContext,
                                    R.string.volte_set_error,
                                    Toast.LENGTH_SHORT).show());
                            return;
                        }

                        if (dialogType == MSG_SET_INTEGRITY_ALGORITHM) {
                            final String cmd = engconstents.ENG_AT_SPVOLTEENG
                                    + "96,1," + "\"" + value + "\"";
                            Log.d(TAG, "MSG_SET_INTEGRITY_ALGORITHM cmd: " + cmd);
                            try {
                                teleApi.volteOperatorSettings().setIntegrityAlgorithm(cmd, mSim);
                                final String summary = typeValue;
                                mUiThread.post(() -> {
                                    preference.setSummary(summary);
                                    Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (dialogType == MSG_SET_CIPHER_ALGORITHM) {
                            final String cmd = engconstents.ENG_AT_SPVOLTEENG
                                    + "97,1," + "\"" + value + "\"";
                            try {
                                teleApi.volteOperatorSettings().setCipherAlgorithm(cmd, mSim);
                                final String summary = typeValue;
                                mUiThread.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        preference.setSummary(summary);
                                        Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (dialogType == MSG_SET_VOICE_CODE_TYPE) {
                            final String cmd = engconstents.ENG_AT_SPVOLTEENG
                                    + "65,1," + "\"" + value + "\"";
                            try {
                                teleApi.volteOperatorSettings().setVoiceCodeType(cmd, mSim);
                                final String summary = typeValue;
                                mUiThread.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        preference.setSummary(summary);
                                        Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (dialogType == MSG_SET_EVS_RATE_SET) {
                            final String cmd = engconstents.ENG_AT_SPVOLTEENG
                                    + "68,1," + "\"" + value + "\"";
                            try {
                                teleApi.volteOperatorSettings().setEvsRate(cmd, mSim);
                                final String summary = typeValue;
                                mUiThread.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        preference.setSummary(summary);
                                        Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (dialogType == MSG_SET_WB_RATE_SET) {
                            final String cmd = engconstents.ENG_AT_SPVOLTEENG
                                    + "67,1," + "\"" + value + "\"";
                            try {
                                teleApi.volteOperatorSettings().setWbRate(cmd, mSim);
                                final String summary = typeValue;
                                mUiThread.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        preference.setSummary(summary);
                                        Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (dialogType == MSG_SET_NB_RATE_SET) {
                            final String cmd = engconstents.ENG_AT_SPVOLTEENG
                                    + "66,1," + "\"" + value + "\"";
                            try {
                                teleApi.volteOperatorSettings().setNbRate(cmd, mSim);
                                final String summary = typeValue;
                                mUiThread.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        preference.setSummary(summary);
                                        Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (dialogType == MSG_SET_VIDEO_CODE_TYPE) {
                            //UNISOC: add for bug1615458, change AT for VIDOE CODEC
                            final String cmd = engconstents.ENG_AT_SPVOLTEENG
                                    + "101,1," + "\"" + value + "\"";
                            try {
                                teleApi.volteOperatorSettings().setVideoCodeType(cmd, mSim);
                                final String summary = typeValue;
                                mUiThread.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        preference.setSummary(summary);
                                        Toast.makeText(mContext, R.string.audio_codec_setting_hint, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        mAlertDialog = alertBuilder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.show();
    }
}
