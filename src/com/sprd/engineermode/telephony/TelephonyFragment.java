package com.sprd.engineermode.telephony;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.preference.TwoStatePreference;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.sprd.engineermode.EMLongClickListPreference;
import com.sprd.engineermode.EMLongClickPreference;
import com.sprd.engineermode.EMSwitchPreference;
import com.sprd.engineermode.R;
import com.sprd.engineermode.debuglog.BandModeSetActivity;
import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.impl.nonpublic.SubscriptionManagerProxy;
import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;
import com.unisoc.engineermode.core.impl.nonpublic.TelephonyManagerProxy;
import com.unisoc.engineermode.core.impl.telephony.TelephonyManagerSprd;
import com.unisoc.engineermode.core.impl.telephony.TelephonyManagerSprd.RadioCapbility;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.utils.IATUtils;

public class TelephonyFragment extends PreferenceFragment implements
        OnPreferenceChangeListener, OnSharedPreferenceChangeListener {

    private static final String TAG = "TelephonyFragment";
    private static final String KEY_SUPPLSERVICEQUERY = "supplementary_service_query";
    private static final String KEY_VIDEOTYPE = "video_type";
    private static final String KEY_QOSSWITCH = "qos_switch";
    private static final String KEY_AUTO_ANSWER = "auto_answer";
    private static final String KEY_NETMODE_SELECT = "network_mode";
    private static final String KEY_DSDS_MODE = "dsds_mode";
    private static final String KEY_Image_SWITCH = "imageswitch";
    private static final String KEY_GPRS_SET = "gprs_set";
    private static final String KEY_SPRD_ILOG = "sprd_ilog";
    private static final String KEY_C2K_CONFIG = "c2k_configuration";
    private static final String KEY_NR_CAP = "nr_cap";
    private static final String KEY_VOLTE_ENABLE = "volte_enable";
    /* SPRD Bug 1048637: C2K Configuration. @{ */
    private static final String C2K_CONFIG_DEFAULT = "141";
    private static final String C2K_CONFIG_CTCC = "143";
    private static final String C2K_CONFIG_ROAMING = "145";
    private static final String C2K_CONFIG_CHAP = "147";
    private static final String C2K_CONFIG_EXTENDED2 = "149";
    /* @} */
    // add-
    private static final String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";
    // -add
    /*
     * SPRD: modify 20140621 Spreadtrum of 325713 EngineerMode, telephony-LTE
     * modem add Clear Prior Information@{
     */
    private static final String KEY_CLEAR_PRIOR_INFORMATION = "clear_prior_information";
    private static final String KEY_SIM_INDEX = "simindex";
    private static final String KEY_USAGE_SETTING = "usage_setting";

    private static final String W_PREFER_PLUS = "w_prefer_plus";
    private static final String NET_MODE = "net_mode";

    private static final String KEY_NETINFO_STATI = "netinfo_statistics";
    private static final String KEY_FAST_DORMANCY = "fastdormancy";
    private static final String KEY_FAST_DORMANCY_VALUE = "fastdormancy_value";
    private static final String KEY_DATA_SERVICE_PREFERRED = "dataservicepreferred";
    private static final String KEY_VT_OPTION_PREFERRED = "VT_option";
    private static final String INTENT_CONTENT = "intent_content";
    private static final String KEY_LTE_MODE_SET = "lte_set";
    private static final String KEY_LTE_MODEM_SET = "lte_modem_set";
    private static final String KEY_LTE_IMPEDE_DATA = "lte_impede_data";
    private static final String KEY_SIM_TRACE = "sim_trace";
    private static final String KEY_USB_ACTIVE = "switch_for_usb_active";
    private static final String KEY_DNS_FILTER = "dns_filter";
    private static final String KEY_VOLTE_SETTING = "volte_setting";
    private static final String KEY_VOLTE_SETTING_APC = "volte_setting_apc";
    private static final String KEY_AUTO_AUTH = "auto_auth";
    private static final String KEY_MODE_SWITCH = "mode_switch";
    public static final String KEY_PHONEID = "key_phoneid";
    public static final String KEY_DATA_SWITCH = "data_switch";
    public static final String KEY_DUALVOLTE_SWITCH = "dualVoLTE_switch";
    public static final String KEY_VOWIFI_LAB_SIM_ALLOWED_SWITCH = "vowifi_lab_sim_allowed_switch";
    public static final String KEY_VOWIFI_WHITE_LIST_ALLOWED_SWITCH = "vowifi_white_list_allowed_switch";
    public static final String KEY_CSFB_GSM = "csfb2gsm_delay";
    public static final String KEY_IMS_SWITCH = "ims_switch";
    // public static final String KEY_SMS_SWITCH = "sms_over_ip_switch";
    private static final String KEY_LOAD_APN = "load_apn";
    private static final String KEY_SINGLE_DUAL_SIM_MODE_SWITCH = "single_dual_sim_mode_switch";
    private static final String KEY_MOS_SWITCH = "mos_switch";
    //modify store meid when init sim in EngineerMode
    private static final String KEY_MEID_SWITCH = "meid_switch";
    private static final String KEY_DM_SWITCH = "dm_switch";
    private static final String KEY_NET_INFO_RECORD = "net_info_record";
    private static final String KEY_DUALVOLTE_ENHANCED_SWITCH = "volte_dsda_enhanced";
    private static final String KEY_WHITELIST_INGORE_SWITCH = "whitelist_ignore_switch";
    /* SPRD 926297: Disable w+G/W+W UE query function @{ */
    private static final String KEY_UE_CAT_SUPPORT = "cat3_4";
    /* }@ */
    /* SPRD Bug 898844: Volte Settings @{ */
    private static final String KEY_OPERATOR_SETTING_0 = "operater_setting_0";
    private static final String KEY_OPERATOR_SETTING_1 = "operater_setting_1";
    public static final String KEY_CHANNEL = "key_channel";
    /* }@ */
    /* SPRD 998834 UPLMN Control @{ */
    private static final String KEY_UPLMNCON = "uplmn_control";
    /* }@ */
    /* SPRD 1034017 disable EVDO @{ */
    private static final String KEY_DISABLE_EVDO = "disable_evdo";
    /* }@ */
    private static final String KEY_SMART_5G = "smart_5g";

    private static final int SET_CFU = 1;
    private static final int OPEN_AUTO_ANSWER = 2;
    private static final int CLOSE_AUTO_ANSWER = 3;
    private static final int SET_FAST_DORMANCY = 8;
    private static final int SET_VIDEOTYPE = 9;
    private static final int GET_VIDEOTYPE = 10;
    private static final int OPEN_DATA_SERVICES_PRE = 11;
    private static final int CLOSE_DATA_SERVICES_PRE = 12;

    private static final int GET_WPREFER_PLUS_STATUS = 16;
    private static final int SET_WPREFER_PLUS_STATUS = 17;
    private static final int LTE_BAND_QUERY = 19;
    private static final int LTE_GET_DATA_IMPEDE = 20;
    private static final int LTE_SET_DATA_IMPEDE = 21;
    private static final int GET_SIM_TRACE_STATUS = 22;
    private static final int OPEN_SIM_TRACE = 23;
    private static final int CLOSE_SIM_TRACE = 24;
    private static final int SET_MODE_SWITCH = 25;
    private static final int GET_AUTO_ANSWER = 26;
    private static final int SET_SINGLE_DUAL_MODE_SWITCH = 27;

    private static final int GET_NXP_STATUS = 34;
    private static final int GET_MOS_STATUS = 35;
    private static final int SET_MOS_STATUS = 36;
    private static final int SET_NXP_STATUS = 37;
    private static final int SET_DSDS_MODE = 38;
    /* @} */
    /* SPRD 1034017 disable EVDO @{ */
    private static final int GET_EVDO_STATUS = 39;
    private static final int SET_EVDO_STATUS = 40;
    /* @} */
    /* SPRD Bug 1048637: C2K Configuration. @{ */
    private static final int GET_C2K_CONFIG = 41;
    private static final int SET_C2K_CONFIG = 42;
    /* meid init configuration */
    private static final int GET_MEID_SWITCH = 43;
    private static final int SET_MEID_SWITCH = 44;
    /* @} */
    private static final int GET_NR_CAP = 45;
    private static final int SET_NR_CAP = 46;
    private static final int KEY_SET_REBOOTBUTTON_TEXT = 47;

    private static final int GET_USAGE_SETTING = 50;
    private static final int SET_USAGE_SETTING = 51;

    private static final int FAST_DORMANCY_OPEN = 1;
    private static final int FAST_DORMANCY_CLOSE = 0;
    private static final int FAST_DORMANCY_DEFAULT_VALUE = 8;

    /* dm switch @} */
    private static final String SWITCH_FOR_DM = "dm_switch";
    //private static final String CFU_CONTROL = "persist.sys.callforwarding";
    private static final String DDR_SWITCH_STATUS = "persist.vendor.sys.ddr.status";
    private static final String SWITCH_FOR_USB_ACTIVE = "switch_for_usb_active";

    // SPRD: modify 20170208 spreadtrum of 642299 EngineerMode
    private static final String AUTO_ANSWER = "persist.radio.call.autoanswer";
    private static final String DSDA_CONTROL = "persist.radio.dsda.wl.ignore";

    private ITelephonyApi teleApi = CoreApi.getTelephonyApi();

    private SwitchPreference mAutoAnswer;
    private SwitchPreference mQosSwitch;
    private SwitchPreference mSprdIlogSwitch;
    private EMLongClickListPreference mSupplementaryServiceQuery;
    private ListPreference mVideoType;
    private EMSwitchPreference mFastdormancy;
    private Preference mNetModeSelect;
    private Preference mImageSwitchPreference;
    private EMSwitchPreference mWpreferplus;
    private EMSwitchPreference mDataServicePreferred;
    private Preference mBandSelect;
    private Preference mBandSelectSim[];
    private EMLongClickPreference mGPRSSet;
    private EMSwitchPreference mVTOptionPreference;
    private EMSwitchPreference mLTEDateImpede;
    private TwoStatePreference mSimTrace;
    private EMLongClickPreference mNetinfoStatistics;
    private TwoStatePreference mUsbActive;
    private TwoStatePreference mDNSFilter;
    private TwoStatePreference mAutoAuth;
    private Preference mVolteSetting;
    private Preference mVolteSettingApc;
    private Preference mNVItemList;
    private ListPreference mModeSwitch;
    private ListPreference mDsdsMode;
    private ListPreference mSingleDualSIMModeSwitch;
    private EMSwitchPreference mDataSwitch;
    private EMSwitchPreference mVolteSwitch;
    private EMSwitchPreference mDualVolteSwitch;
    // for vowifi: add Vowifi lab sim and white list allowed permit
    private EMSwitchPreference mVowifiLabSimAllowedSwitch;
    private EMSwitchPreference mVowifiWhiteListAllowedSwitch;

    private EMSwitchPreference mImsSwitch;
    // private EMSwitchPreference mSmsOverIpSwitch;
    private EMSwitchPreference mLoadAPNSwitch;
    private EMSwitchPreference mMosSwitch;
    private EMSwitchPreference mWhiteListIgnoreSwitch;
    private EMSwitchPreference mMeidSwitch;
    private EMSwitchPreference mDmSwitch;
    /* SPRD 926297: Disable W+G/W+W UE query function @{ */
    private Preference mUEQuery;
    /* @} */
    /* SPRD 998834 UPLMN Control @{ */
    private TwoStatePreference mUplmnCon;
    /* @} */
    private PreferenceScreen mNxp;
    private ListPreference mNrCap;
    private SwitchPreference mVolteEnable;
    private String[] status;
    private int nxp_index;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Preference mCsfb2gsm;
    // private Preference mNetInfoRecord;
    /* SPRD Bug 898844: Volte Settings @{ */
    private Preference mOperatorSetting0;
    private Preference mOperatorSetting1;
    /* @} */
    /* SPRD 1034017 disable EVDO @{ */
    private EMSwitchPreference mDisableEvdo;
    /* }@ */
    private ListPreference mUsageSetting;

    private Handler mUiThread = new Handler();
    private TELHandler mTELHandler;
    private Context mContext;
    private int mPhoneCount;
    private int[] mModemType;
    private int mSIM;
    private ProgressDialog mProgressDialog;
    private TelephonyManagerSprd mTelephonyManagerSprd;

    private boolean isSupportLTE = teleApi.telephonyInfo().isSupportLte();
    private boolean isSupportCSFB = teleApi.telephonyInfo().isSupportCsfb();
    private boolean isSupportC2K = teleApi.telephonyInfo().isSupportC2k();
    private TelephonyManagerSprd.RadioCapbility nowModeName = TelephonyManagerSprd.getRadioCapbility();

    private String nowDsdsModeName = SystemPropertiesProxy.get(
            "persist.vendor.radio.modem.config", "");
    private String originalDsdsMode = SystemPropertiesProxy.get("persist.vendor.radio.modem.capability", "");
    private String nowSingelDualSIMModeName = "1";

    private boolean isLplusG = teleApi.telephonyInfo().isSupportLplusG();
    private boolean isLplusW = teleApi.telephonyInfo().isSupportLplusW();
    private boolean disableNetmode = teleApi.telephonyInfo().isSupportNetwork();

    private boolean autoAnswerOpen = SystemPropertiesProxy.get(AUTO_ANSWER).equals("1");
    private String mStrTmp;
    private int mMesarg;
    private String mATCmd;
    private static final String DORM_TIME = "Dormancy time";
    // private static String mDromTime;

    private SharedPreferences mSharePref;
    private String mATResponse;
    private String mFirstDormancySet = null;
    private int mPhoneId = 0;
    private int mCardCount = 0;
    private boolean[] mIsCardExist;
    private RadioCapbility mCurrentRadioCapbility;
    private boolean mVolteChecked = false;
    private boolean mVowifiLabSimAllowedChecked = false;
    private boolean mVowifiWhiteListAllowedChecked = false;
    private boolean mSmsChecked = false;
    private boolean getNxpStatus = false;
    private static final int NXP_COUNT = 27;
    private CheckBoxPreference[] mNxpList;
    private boolean mDualVolteChecked = false;

    /* SPRD Bug 1048637: C2K Configuration. @{ */
    private ListPreference mC2kConfig;
    private boolean mSupportDM;
    /* @} */
    private boolean mSupportVT = SystemPropertiesProxy.getBoolean("persist.vendor.sys.volte.enable", false);

    private static final String NETWORK_NR_ENABLE = "persist.sys.eng.nr.enable";
    private static final String NSA_ONLY = "2";
    private int mNrCapPreviousValue = 0;
    private static final String NSA_ONLY_MODE = "0";
    private static final String NR_ONLY_MODE = "1";
    private static final String NORMAL_MODE = "2";
    private Preference mSmart5g;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mTELHandler = new TELHandler(ht.getLooper());

        addPreferencesFromResource(R.xml.pref_telephonytab);

        PreferenceScreen prefSet = getPreferenceScreen();

        mContext = getActivity();
        preferences = mContext
                .getSharedPreferences("fd", Context.MODE_PRIVATE);
        editor = preferences.edit();
        mAutoAnswer = (SwitchPreference) prefSet
                .findPreference(KEY_AUTO_ANSWER);
        mAutoAnswer.setOnPreferenceChangeListener(this);
        mAutoAnswer.setChecked(autoAnswerOpen);
        mQosSwitch = (SwitchPreference) prefSet.findPreference(KEY_QOSSWITCH);
        mQosSwitch.setOnPreferenceChangeListener(this);
        mBandSelect = (PreferenceScreen) findPreference("band_select");
        mGPRSSet = (EMLongClickPreference) findPreference(KEY_GPRS_SET);
        mGPRSSet.setEnabled(true);

        mSimTrace = (TwoStatePreference) findPreference(KEY_SIM_TRACE);
        mSimTrace.setOnPreferenceChangeListener(this);
        mUsbActive = (TwoStatePreference) findPreference(KEY_USB_ACTIVE);
        mUsbActive.setOnPreferenceChangeListener(this);

        mSprdIlogSwitch = (SwitchPreference) findPreference(KEY_SPRD_ILOG);
        mSprdIlogSwitch.setEnabled(false);
        mSprdIlogSwitch.setOnPreferenceChangeListener(this);
        mFastdormancy = (EMSwitchPreference) findPreference(KEY_FAST_DORMANCY);
        mFastdormancy.setOnPreferenceChangeListener(this);
        mSupplementaryServiceQuery = (EMLongClickListPreference) findPreference(KEY_SUPPLSERVICEQUERY);
        mNrCap = (ListPreference) findPreference(KEY_NR_CAP);
        mNrCap.setOnPreferenceChangeListener(this);
        mNetModeSelect = (Preference) findPreference(KEY_NETMODE_SELECT);
        mImageSwitchPreference = (Preference) findPreference(KEY_Image_SWITCH);

        mDataServicePreferred = (EMSwitchPreference) findPreference(KEY_DATA_SERVICE_PREFERRED);
        mDataServicePreferred.setOnPreferenceChangeListener(this);

        mVTOptionPreference = (EMSwitchPreference) findPreference(KEY_VT_OPTION_PREFERRED);
        mVTOptionPreference.setOnPreferenceChangeListener(this);
        mLTEDateImpede = (EMSwitchPreference) findPreference(KEY_LTE_IMPEDE_DATA);
        mLTEDateImpede.setOnPreferenceChangeListener(this);
        mNetinfoStatistics = (EMLongClickPreference) findPreference(KEY_NETINFO_STATI);
        mDNSFilter = (TwoStatePreference) findPreference(KEY_DNS_FILTER);
        mDNSFilter.setOnPreferenceChangeListener(this);

        /* SPRD 1107765: delete mode switch @{ */
        // disable mode-switch
        // this item is always disabled on android8,9
        // if needed in the future, we can enable it then
        mModeSwitch = (ListPreference) findPreference(KEY_MODE_SWITCH);

        getPreferenceScreen().removePreference(mModeSwitch);
        /* }@ */
        mDsdsMode = (ListPreference) findPreference(KEY_DSDS_MODE);
        if (originalDsdsMode.equals("TL_LF_TD_W_G,TL_LF_TD_W_G")
            || originalDsdsMode.equals("TL_LF_W_G,TL_LF_W_G")) {
            mDsdsMode.setEntries(R.array.dsds_mode_ll);
            mDsdsMode.setEntryValues(R.array.dsds_mode_value_ll);
        }
        /* SPRD Bug 831799 :Only L+L version can use DSDS mode @{ */
        if (originalDsdsMode.equals("TL_LF_TD_W_G,TL_LF_TD_W_G") || originalDsdsMode.equals("TL_LF_W_G,TL_LF_W_G")) {
            mDsdsMode.setEnabled(true);
        } else {
            mDsdsMode.setEnabled(false);
            mDsdsMode.setSummary(R.string.feature_not_support);
        }
        /* @} */
        mSmart5g = (Preference) findPreference(KEY_SMART_5G);
        mSingleDualSIMModeSwitch = (ListPreference) findPreference(KEY_SINGLE_DUAL_SIM_MODE_SWITCH);
        mNVItemList = (Preference) findPreference("nv_item_list");
        mNVItemList.setEnabled(true);
        mVolteSetting = (Preference) findPreference(KEY_VOLTE_SETTING);
        mVolteSettingApc = (Preference) findPreference(KEY_VOLTE_SETTING_APC);
        if (SystemPropertiesProxy.get("persist.vendor.sys.volte.enable").equals("true") || isSupportLTE) {
            mVolteSetting.setEnabled(true);
            mVolteSettingApc.setEnabled(true);
        } else {
            mVolteSetting.setEnabled(false);
            mVolteSettingApc.setEnabled(false);
            mVolteSetting.setSummary(R.string.feature_not_support);
            mVolteSettingApc.setSummary(R.string.feature_not_support);
        }
        updateVolteSettings();

        mWhiteListIgnoreSwitch = (EMSwitchPreference) findPreference(KEY_WHITELIST_INGORE_SWITCH);
        mWhiteListIgnoreSwitch.setOnPreferenceChangeListener(this);
        // vowifi lab sim allowed
        mVowifiLabSimAllowedSwitch = (EMSwitchPreference) findPreference(KEY_VOWIFI_LAB_SIM_ALLOWED_SWITCH);
        mVowifiLabSimAllowedSwitch.setOnPreferenceChangeListener(this);

        // vowifi white list allowed
        mVowifiWhiteListAllowedSwitch = (EMSwitchPreference) findPreference(KEY_VOWIFI_WHITE_LIST_ALLOWED_SWITCH);
        mVowifiWhiteListAllowedSwitch.setOnPreferenceChangeListener(this);

        /* SPRD 1034017 disable EVDO @{ */
        mDisableEvdo = (EMSwitchPreference) findPreference(KEY_DISABLE_EVDO);
        mDisableEvdo.setOnPreferenceChangeListener(this);
        if (!isSupportC2K) {
            mDisableEvdo.setEnabled(false);
        }
        /* @} */
        mImsSwitch = (EMSwitchPreference) findPreference(KEY_IMS_SWITCH);
        mImsSwitch.setOnPreferenceChangeListener(this);

        if (!isSupportLTE) {
            mImsSwitch.setEnabled(false);
            mImsSwitch.setSummary(R.string.feature_not_support);
        } else {
            mImsSwitch.setEnabled(true);
        }
        if (SystemPropertiesProxy.get("persist.sys.support.vt").isEmpty()) {
            mVTOptionPreference.setChecked(true);
        } else {
            mVTOptionPreference.setChecked(SystemPropertiesProxy.get(
                    "persist.sys.support.vt").equals("true"));
        }
        mUsageSetting = (ListPreference) findPreference(KEY_USAGE_SETTING);

        mPhoneCount = TelephonyManagerProxy.getPhoneCount();
        mModemType = new int[mPhoneCount];
        mBandSelectSim = new Preference[mPhoneCount];
        mIsCardExist = new boolean[mPhoneCount];

        mTelephonyManagerSprd = TelephonyManagerSprd.getInstance();
        getCardExitState();
        for (int i = 0; i < mPhoneCount; i++) {
            mModemType[i] = TelephonyManagerSprd.getModemType();
            mBandSelectSim[i] = new Preference(mContext);
            if (mModemType[i] == TelephonyManagerSprd.MODEM_TYPE_TDSCDMA) {
                mNetModeSelect.setSummary(R.string.input_cmcc_card);
                Log.d(TAG, "modem type is TDSCDMA");
            } else if (mModemType[i] == TelephonyManagerSprd.MODEM_TYPE_WCDMA) {
                mNetModeSelect.setSummary(R.string.input_cucc_card);
                Log.d(TAG, "modem type is WCDMA");
            }
            mBandSelectSim[i].setTitle("SIM" + i);
            mBandSelectSim[i].setKey("SIM" + i);
        }

        if (TelephonyManagerSprd.getModemType() == TelephonyManagerSprd.MODEM_TYPE_GSM) {
            Log.d(TAG, "Modem Type is GSM, remove net mode select and vt option");
            mNetModeSelect.setEnabled(false);
            mNetModeSelect.setSummary(R.string.feature_not_support);
            mNetinfoStatistics.setEnabled(false);
            mNetinfoStatistics.setSummary(R.string.feature_not_support);
        }
        /* SPRD: bug 810559 W+W/W+G set disable NetModeSelect @{ */
        if (disableNetmode) {
            mNetModeSelect.setEnabled(false);
            mNetModeSelect.setSummary(R.string.feature_not_support);
        }
        /* }@ */
        // If RegionConfig is exists, show mImageSwitchPreference;else do not
        // show
        if (!isAppInstalled(mContext, "com.android.regionconfig")) {
            getPreferenceScreen().removePreference(mImageSwitchPreference);
        }

        // sharkl does not support VT
        mCurrentRadioCapbility = TelephonyManagerSprd.getRadioCapbility();
        if (mCurrentRadioCapbility == TelephonyManagerSprd.RadioCapbility.TDD_CSFB
                || isSupportCSFB) {
            mVTOptionPreference.setEnabled(false);
            prefSet.removePreference(mVTOptionPreference);
        }

        registerReceiver();

        if (!mGPRSSet.isEnabled()) {
            //mGPRSSet.setSummary(R.string.input_card_to_test);
        }

        mWpreferplus = (EMSwitchPreference) findPreference(W_PREFER_PLUS);
        mWpreferplus.setOnPreferenceChangeListener(this);
        if (!teleApi.telephonyInfo().isSupportWcdma()) {
            mWpreferplus.setEnabled(false);
            mWpreferplus.setSummary(R.string.feature_not_support);
            mWpreferplus = null;
        }

        mVideoType = (ListPreference) findPreference(KEY_VIDEOTYPE);

        mSharePref = PreferenceManager.getDefaultSharedPreferences(mContext);
        mSharePref.registerOnSharedPreferenceChangeListener(this);

        if (!isSupportLTE) {
            mLTEDateImpede.setEnabled(false);
            mLTEDateImpede.setSummary(R.string.feature_not_support);
        }
        mCsfb2gsm = (Preference) findPreference(KEY_CSFB_GSM);
        if (!isSupportLTE) {
            mCsfb2gsm.setEnabled(false);
            mCsfb2gsm.setSummary(R.string.feature_not_support);
        }
        mLoadAPNSwitch = (EMSwitchPreference) findPreference(KEY_LOAD_APN);
        mLoadAPNSwitch.setOnPreferenceChangeListener(this);

        mNxp = (PreferenceScreen) findPreference("nxp_list");
        mNxpList = new CheckBoxPreference[NXP_COUNT];
        for (int i = 0; i < NXP_COUNT; i++) {
            mNxpList[i] = new CheckBoxPreference(mContext);
            mNxpList[i].setTitle(this.getResources().getStringArray(
                    R.array.nxp_list)[i]);
            mNxp.addPreference(mNxpList[i]);
            mNxp.setKey("nxp" + i);
            mNxpList[i].setOnPreferenceClickListener(clickListenser);
        }
        mNxp.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (mNxp != null) {
                    getNxpStatus = true;
                    Message simTraceSta = mTELHandler
                            .obtainMessage(GET_NXP_STATUS);
                    mTELHandler.sendMessage(simTraceSta);
                }
                return true;
            }
        });

        mMosSwitch = (EMSwitchPreference) findPreference(KEY_MOS_SWITCH);
        mMosSwitch.setOnPreferenceChangeListener(this);
        if (isSupportLTE) {
            mMosSwitch.setEnabled(true);
        } else {
            mMosSwitch.setEnabled(false);
            mMosSwitch.setSummary(R.string.feature_not_support);
            mNxp.setSummary(mContext.getString(R.string.feature_not_support));
            mNxp.setEnabled(false);
        }
        /*
        * and store Meid switch @{
        */
        mMeidSwitch = (EMSwitchPreference) findPreference(KEY_MEID_SWITCH);
        mMeidSwitch.setOnPreferenceChangeListener(this);

        /*
        * and dm switch @{
        */
        mDmSwitch = (EMSwitchPreference) findPreference(KEY_DM_SWITCH);
        mDmSwitch.setOnPreferenceChangeListener(this);
        mSupportDM = getResources().getBoolean(R.bool.is_support_dm_switch);
        Log.d(TAG,"mSupportDM = " + mSupportDM);
        if (!mSupportDM) {
            mDmSwitch.setEnabled(false);
            mDmSwitch.setSummary(mContext.getString(R.string.feature_not_support));
        }

        /* SPRD 926297: Disable w+G/W+W UE query function @{ */
        mUEQuery = (Preference) findPreference(KEY_UE_CAT_SUPPORT);
        if (originalDsdsMode.equals("W_G,G") || originalDsdsMode.equals("W_G,W_G")) {
            mUEQuery.setEnabled(false);
            mUEQuery.setSummary(mContext.getString(R.string.feature_not_support));
        }
        /* @} */
        /* SPRD Bug 898844: Volte Settings @{ */
        try {
            mOperatorSetting0 = (Preference) findPreference(KEY_OPERATOR_SETTING_0);
            mOperatorSetting0.setOnPreferenceChangeListener(this);
            mOperatorSetting1 = (Preference) findPreference(KEY_OPERATOR_SETTING_1);
            mOperatorSetting1.setOnPreferenceChangeListener(this);
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException" + e);
        }
        /* @} */
        /* SPRD 998834 UPLMN Control @{ */
        mUplmnCon = (TwoStatePreference) findPreference(KEY_UPLMNCON);
        mUplmnCon.setOnPreferenceChangeListener(this);
        /* @} */
        /* SPRD Bug 1048637: C2K Configuration. @{ */
        mC2kConfig = (ListPreference) findPreference(KEY_C2K_CONFIG);
        mC2kConfig.setOnPreferenceChangeListener(this);
        if (!isSupportC2K) {
            mC2kConfig.setEnabled(false);
        }
        /* @} */

        mVolteEnable = (SwitchPreference) findPreference(KEY_VOLTE_ENABLE);
        mVolteEnable.setOnPreferenceChangeListener(this);

        getPreferenceScreen().removePreference(
                findPreference("lte_impede_data"));
        getPreferenceScreen().removePreference(findPreference("sprd_ilog"));
    }

    private void updateVolteSettings() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean mApcFuncSupport = teleApi.telephonyInfo().isSupportAPCFunc();
                mUiThread.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mApcFuncSupport) {
                            getPreferenceScreen().removePreference(mVolteSetting);
                        } else {
                            getPreferenceScreen().removePreference(mVolteSettingApc);
                        }
                    }
                });
            }
        }).start();
    }

    private void getCardExitState() {
        for (int i = 0; i < mPhoneCount; i++) {
            if (TelephonyManagerProxy.getSimState(i) == TelephonyManager.SIM_STATE_READY) {
                mIsCardExist[i] = true;
            } else {
                mIsCardExist[i] = false;
            }
            if (mIsCardExist[i]) {
                mGPRSSet.setEnabled(true);
                mPhoneId = i;
                mCardCount++;
            }
            Log.d(TAG, "mIsCardExist[" + i + "] = " + mIsCardExist[i]
                    + " mCardCount = " + mCardCount);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private BroadcastReceiver mMobileReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "action = " + action);
            if (action.startsWith(Intent.ACTION_AIRPLANE_MODE_CHANGED)) {
                if (!disableNetmode) {
                    updateModeType();
                }
            } else if (action.startsWith(ACTION_SIM_STATE_CHANGED)) {
                getCardExitState();
                setCardExitState();
            }
        }
    };

    private void setCardExitState() {
        boolean isAirplane = Settings.Global.getInt(
                mContext.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0) == 1;
        boolean[] simCardAvailable = new boolean[mPhoneCount];
        for (int i = 0; i < mPhoneCount; i++) {
            if (mIsCardExist[i]) {
                if (isAirplane || !TelephonyManagerSprd.isSimStandby(i, mContext)) {
                    mBandSelectSim[i].setSummary(mContext.getString(R.string.open_card_warn));
                    mBandSelectSim[i].setEnabled(false);
                    simCardAvailable[i] = false;
                    if (BandModeSetActivity.BandModeSetActivityInstance != null) {
                        BandModeSetActivity.BandModeSetActivityInstance.finish();
                    }
                } else {
                    mBandSelectSim[i].setSummary(null);
                    mBandSelectSim[i].setEnabled(true);
                    simCardAvailable[i] = true;
                }
            } else {
                mBandSelectSim[i].setSummary(mContext.getString(R.string.input_sim__warn));
                mBandSelectSim[i].setEnabled(false);
                simCardAvailable[i] = false;
                if (BandModeSetActivity.BandModeSetActivityInstance != null) {
                    BandModeSetActivity.BandModeSetActivityInstance.finish();
                }
            }
        }
        boolean allSimCardAvailable = false;
        for (int i = 0; i < mPhoneCount; i++) {
            allSimCardAvailable |= simCardAvailable[i];
        }
        if (!allSimCardAvailable) {
            mNetModeSelect.setEnabled(false);
            mSmart5g.setEnabled(false);
        } else {
            mNetModeSelect.setEnabled(true);
            mSmart5g.setEnabled(true);
        }
    }

    private void updateModeType() {
        int primaryCard = mTelephonyManagerSprd.getPrimaryCard();
        boolean isStandby = TelephonyManagerSprd.isSimStandby(primaryCard,
                mContext);
        boolean isAirplane = Settings.Global.getInt(
                mContext.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0) == 1;
        Log.d(TAG, "isSupportLTE = " + isSupportLTE + " isStandby = "
                + isStandby + " isAirplane = " + isAirplane);
        boolean isEnable = false;
        CharSequence summary = null;
        if (isSupportLTE) {
            if (TelephonyManagerProxy.getPhoneCount() == 1) {
                if (mCardCount != 0) {
                    if (!isStandby || isAirplane) {
                        isEnable = false;
                        summary = mContext.getString(R.string.open_card_warn);
                    } else {
                        isEnable = true;
                        summary = null;
                    }
                } else {
                    isEnable = false;
                    summary = mContext.getString(R.string.input_sim__warn);
                }
            } else {
                if (mCardCount == 0) {
                    isEnable = false;
                    summary = mContext.getString(R.string.input_sim__warn);
                } else {
                    if (!isStandby || isAirplane) {
                        isEnable = false;
                        summary = mContext.getString(R.string.open_card_warn);
                    } else {
                        isEnable = true;
                        summary = null;
                    }
                }
            }
            /* SPRD: bug 810559 W+W/W+G set disable NetModeSelect @{ */
            if (!(originalDsdsMode.equals("W_G,G") || originalDsdsMode.equals("W_G,W_G"))) {
                mNetModeSelect.setEnabled(isEnable);
                mNetModeSelect.setSummary(summary);
            }
            /* }@ */
        }
        setCardExitState();
    }

    public void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        filter.addAction(ACTION_SIM_STATE_CHANGED);
        mContext.registerReceiver(mMobileReceiver, filter);
    }

    public void unregisterReceiver() {
        try {
            mContext.unregisterReceiver(mMobileReceiver);
        } catch (IllegalArgumentException iea) {
            // Ignored.
        }
    }

    private int changeValueToIndex(TelephonyManagerSprd.RadioCapbility PrefKey) {
        int valueIndex = 0;
        if ((TelephonyManagerSprd.RadioCapbility.TDD_CSFB).equals(PrefKey)) {
            valueIndex = 0;
        } else if ((TelephonyManagerSprd.RadioCapbility.FDD_CSFB)
                .equals(PrefKey)) {
            valueIndex = 1;
        } else if ((TelephonyManagerSprd.RadioCapbility.CSFB).equals(PrefKey)) {
            valueIndex = 2;
        }
        return valueIndex;
    }

    private int changeSingleDualSIMModeValueToIndex(String PrefKey) {
        int valueIndex = 0;
        if (("1").equals(PrefKey)) {
            valueIndex = 0;
        } else if (("2").equals(PrefKey)) {
            valueIndex = 1;
        }
        return valueIndex;
    }

    private int changeDsdsModeValueToIndex(String dsdsMode) {
        int valueIndex = 0;
        if (("TL_LF_TD_W_G,G").equals(dsdsMode) || ("TL_LF_W_G,G").equals(dsdsMode)) {
            valueIndex = 0;
        } else if (("TL_LF_TD_W_G,W_G").equals(dsdsMode) || ("TL_LF_W_G,W_G").equals(dsdsMode)) {
            valueIndex = 1;
        } else if (("TL_LF_TD_W_G,TL_LF_TD_W_G").equals(dsdsMode) || ("TL_LF_W_G,TL_LF_W_G").equals(dsdsMode)) {
            valueIndex = 2;
        }
        Log.d(TAG, "changeDsdsModeValueToIndex valueIndex: " + valueIndex);
        return valueIndex;
    }

    private void setDsdsMode(int value) {
        switch (value) {
        case 0:
            if (originalDsdsMode.contains("TL_LF_W_G")) {
                SystemPropertiesProxy.set("persist.vendor.radio.modem.config", "TL_LF_W_G,G");
                SystemPropertiesProxy.set("persist.vendor.radio.modem.workmode", "6,10");
            } else if (originalDsdsMode.contains("TL_TD_G")) {
                SystemPropertiesProxy.set("persist.vendor.radio.modem.config", "TL_TD_G,G");
                SystemPropertiesProxy.set("persist.vendor.radio.modem.workmode", "7,10");
            } else {
                SystemPropertiesProxy.set("persist.vendor.radio.modem.config", "TL_LF_TD_W_G,G");
                SystemPropertiesProxy.set("persist.vendor.radio.modem.workmode", "9,10");
            }
            break;
        case 1:
            if (originalDsdsMode.contains("TL_LF_TD_W_G")) {
                SystemPropertiesProxy.set("persist.vendor.radio.modem.config", "TL_LF_TD_W_G,W_G");
                SystemPropertiesProxy.set("persist.vendor.radio.modem.workmode", "9,255");
            } else if (originalDsdsMode.contains("TL_LF_W_G")) {
                SystemPropertiesProxy.set("persist.vendor.radio.modem.config", "TL_LF_W_G,W_G");
                SystemPropertiesProxy.set("persist.vendor.radio.modem.workmode", "6,255");
            } else {
                SystemPropertiesProxy.set("persist.vendor.radio.modem.config", "TL_LF_TD_W_G,W_G");
                SystemPropertiesProxy.set("persist.vendor.radio.modem.workmode", "9,255");
            }
            break;
        case 2:
            if (originalDsdsMode.contains("TL_LF_TD_W_G,TL_LF_TD_W_G")) {
                SystemPropertiesProxy.set("persist.vendor.radio.modem.config", "TL_LF_TD_W_G,TL_LF_TD_W_G");
                SystemPropertiesProxy.set("persist.vendor.radio.modem.workmode", "9,9");
            } else if (originalDsdsMode.contains("TL_LF_W_G,TL_LF_W_G")) {
                SystemPropertiesProxy.set("persist.vendor.radio.modem.config", "TL_LF_W_G,TL_LF_W_G");
                SystemPropertiesProxy.set("persist.vendor.radio.modem.workmode", "6,6");
            }
            break;
        default:
            break;
        }
    }

    private void setSingeDualSIMModeSwitch(int value) {
        switch (value) {
        case 0:
            SystemPropertiesProxy.set("persist.radio.multisim.config", "ssss"); // set
                                                                            // multisim
                                                                            // config
                                                                            // to
                                                                            // single
                                                                            // sim
                                                                            // single
                                                                            // standby
            break;
        case 1:
            SystemPropertiesProxy.set("persist.radio.multisim.config", "dsds"); // set
                                                                            // multisim
                                                                            // config
                                                                            // to
                                                                            // dual
                                                                            // sim
                                                                            // dual
                                                                            // standby
            break;
        default:
            break;
        }
    }

    @Override
    public void onStart() {
        getNxpStatus = false;
        /*
         * Modify 355060 sync CFU and supplementary_service_query which in
         * TTCNActivity etc
         */
        if (SystemPropertiesProxy.get("persist.vendor.radio.fd.disable", "").equals("0")) {
            mFastdormancy.setSummary(mContext.getString(R.string.Default_value));
            mFastdormancy.setChecked(true);
            editor.putString("fd_open", "true");
            editor.commit();
        } else if (SystemPropertiesProxy.get("persist.vendor.radio.fd.disable", "").equals("1")) {
            if (preferences.getString("fd_open", "").equals("true")) {
                mFastdormancy.setSummary(preferences.getString(DORM_TIME,
                        "null") + "s");
                mFastdormancy.setChecked(true);
            }
        }

        String cfuValue = mSharePref.getString("supplementary_service_query", teleApi.callFunc().getCfu());

        if (cfuValue.equals("0")) {
            mSupplementaryServiceQuery.setValue("0");
        } else if (cfuValue.equals("1")) {
            mSupplementaryServiceQuery.setValue("1");
        } else if (cfuValue.equals("2")) {
            mSupplementaryServiceQuery.setValue("2");
        }
        /*
         * Modify 355060 sync CFU and supplementary_service_query which in
         * TTCNActivity etc
         */
        if (mSupportVT) {
            mVideoType.setEnabled(true);
            mVideoType.setSummary(mVideoType.getEntry());
        } else {
            mVideoType.setEnabled(false);
        }

        if (mWpreferplus != null) {
            mTELHandler.sendEmptyMessage(GET_WPREFER_PLUS_STATUS);
        }

        if (mSimTrace != null) {
            Message simTraceSta = mTELHandler
                    .obtainMessage(GET_SIM_TRACE_STATUS);
            mTELHandler.sendMessage(simTraceSta);
        }
        if (mUsbActive != null) {
            int usbActiveSta = Settings.Global.getInt(
                    mContext.getContentResolver(), SWITCH_FOR_USB_ACTIVE, 0);
            Log.d(TAG, "Usb active status is: " + usbActiveSta);
            if (usbActiveSta == 1) {
                mUsbActive.setChecked(true);
            } else {
                mUsbActive.setChecked(false);
            }
        }
//        UNISOC: Bug 1417710  Remove send LTE_GET_DATA_IMPEDE message to handlerMessage
//        if (isSupportLTE) {
//            mTELHandler.sendEmptyMessage(LTE_GET_DATA_IMPEDE);
//        }
        if (mModeSwitch != null && mModeSwitch.isEnabled()) {
            mModeSwitch.setValueIndex(changeValueToIndex(nowModeName));
            mModeSwitch.setSummary(mModeSwitch.getEntry());
        }
        if (mDsdsMode != null && mDsdsMode.isEnabled()) {
            mDsdsMode
                    .setValueIndex(changeDsdsModeValueToIndex(nowDsdsModeName));
            mDsdsMode.setSummary(mDsdsMode.getEntry());
        }
        if (mSingleDualSIMModeSwitch != null) {
            mSingleDualSIMModeSwitch
                    .setValueIndex(changeSingleDualSIMModeValueToIndex(nowSingelDualSIMModeName));
            mSingleDualSIMModeSwitch.setSummary(mSingleDualSIMModeSwitch
                    .getEntry());
        }
        if (mDNSFilter != null) {
            if (SystemPropertiesProxy.getBoolean("sys.engineermode.dns", false)) {
                mDNSFilter.setChecked(true);
            } else {
                mDNSFilter.setChecked(false);
            }
        }
        if (mNxp != null) {
            Message simTraceSta = mTELHandler.obtainMessage(GET_NXP_STATUS);
            mTELHandler.sendMessage(simTraceSta);
        }
        if (SystemPropertiesProxy.get(DSDA_CONTROL).equals("true")) {
            mWhiteListIgnoreSwitch.setChecked(true);
        } else {
            mWhiteListIgnoreSwitch.setChecked(false);
        }

        if (!disableNetmode) {
            updateModeType();
        }
        /*
         * SPRD: modify 20160707 Spreadtrum of 578851 EngineerMode, add NXP list
         * and MOS switch @{
         */
        if (isSupportLTE && mMosSwitch != null) {
            Message simTraceSta = mTELHandler.obtainMessage(GET_MOS_STATUS);
            mTELHandler.sendMessage(simTraceSta);
        }
        /*
        * and store meid switch @{
        */
        if (mMeidSwitch != null) {
            Message simTraceSta = mTELHandler.obtainMessage(GET_MEID_SWITCH);
            mTELHandler.sendMessage(simTraceSta);
        }
        /*
        * and dm switch @{
        */
        if (mSupportDM && mDmSwitch != null) {

            int dmSwitch = Settings.Global.getInt(
                    mContext.getContentResolver(), SWITCH_FOR_DM, 1);
            Log.d(TAG, "ctcc dm status is: " + dmSwitch);
            if (1 == dmSwitch) {
                mDmSwitch.setChecked(true);
                mDmSwitch.setSummary("Open");
            } else {
                mDmSwitch.setChecked(false);
                mDmSwitch.setSummary("Close");
            }
        }

        /* SPRD 998834 UPLMN Control @{ */
        if (mUplmnCon != null && mUplmnCon.isEnabled()) {
            mUplmnCon.setChecked(SystemPropertiesProxy.getBoolean("persist.sys.uplmn", false));
        }
        /* @} */
        /* SPRD 1034017 disable EVDO @{ */
        if (mDisableEvdo != null) {
            Message mEvdoStatus = mTELHandler.obtainMessage(GET_EVDO_STATUS);
            mTELHandler.sendMessage(mEvdoStatus);
        }
        /* @} */
        /* SPRD Bug 1048637: C2K Configuration. @{ */
        if (mC2kConfig != null && mC2kConfig.isEnabled()) {
            Message getC2kConfig = mTELHandler.obtainMessage(GET_C2K_CONFIG);
            mTELHandler.sendMessage(getC2kConfig);
        }
        /* @} */
        if (mNrCap != null && mNrCap.isEnabled()) {
            Message getNrCapState = mTELHandler.obtainMessage(GET_NR_CAP);
            mTELHandler.sendMessage(getNrCapState);
        }

        if (mVolteEnable != null && mVolteEnable.isEnabled()) {
            mVolteEnable.setChecked(teleApi.volteEnable().get());
        }
        if (mUsageSetting != null && mUsageSetting.isEnabled()) {
            Message getUsage = mTELHandler.obtainMessage(GET_USAGE_SETTING);
            mTELHandler.sendMessage(getUsage);
        }
        super.onStart();
    }

    @Override
    public void onDestroy() {
        if (mTELHandler != null) {
            mTELHandler.getLooper().quit();
            Log.d(TAG, "HandlerThread has quit");
        }
        unregisterReceiver();
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
        if (key.equals(KEY_SUPPLSERVICEQUERY)) {
            String re = sharedPreferences.getString(key, "");
            Message mSupplService = mTELHandler.obtainMessage(SET_CFU, re);
            mTELHandler.sendMessage(mSupplService);
        } else if (key.equals(KEY_VIDEOTYPE)) {
            mVideoType.setSummary(mVideoType.getEntry());
            String re = sharedPreferences.getString(key, "");
            Message mSupplService = mTELHandler
                    .obtainMessage(SET_VIDEOTYPE, re);
            mTELHandler.sendMessage(mSupplService);
        } else if (key.equals(KEY_DSDS_MODE)) {
            final String re = sharedPreferences.getString(key, "");
            if (Integer.parseInt(re) != changeDsdsModeValueToIndex(nowDsdsModeName)) {
                if (isAdded()) {
                    AlertDialog alertDialog = new AlertDialog.Builder(
                            getActivity())
                            .setTitle(getString(R.string.dsds_mode))
                            .setMessage(getString(R.string.mode_switch_waring))
                            .setPositiveButton(
                                    getString(R.string.alertdialog_ok),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            Message mSetMode = mTELHandler
                                                    .obtainMessage(
                                                            SET_DSDS_MODE, re);
                                            mTELHandler.sendMessage(mSetMode);
                                        }
                                    })
                            .setNegativeButton(R.string.alertdialog_cancel,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            mUiThread.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mDsdsMode
                                                            .setValueIndex(changeDsdsModeValueToIndex(nowDsdsModeName));
                                                }
                                            });
                                        }
                                    }).create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                }
            }
        } else if (key.equals(KEY_SINGLE_DUAL_SIM_MODE_SWITCH)) {
            final String re = sharedPreferences.getString(key, "");
            if (Integer.parseInt(re) != changeSingleDualSIMModeValueToIndex(nowSingelDualSIMModeName)) {
                if (isAdded()) {
                    AlertDialog alertDialog = new AlertDialog.Builder(
                            getActivity())
                            .setTitle(
                                    getString(R.string.single_dual_sim_mode_switch))
                            .setMessage(getString(R.string.mode_switch_waring))
                            .setPositiveButton(
                                    getString(R.string.alertdialog_ok),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            Message mSetMode = mTELHandler
                                                    .obtainMessage(
                                                            SET_SINGLE_DUAL_MODE_SWITCH,
                                                            re);
                                            mTELHandler.sendMessage(mSetMode);
                                        }
                                    })
                            .setNegativeButton(R.string.alertdialog_cancel,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            mUiThread.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mSingleDualSIMModeSwitch
                                                            .setValueIndex(changeSingleDualSIMModeValueToIndex(nowSingelDualSIMModeName));
                                                }
                                            });
                                        }
                                    }).create();
                    // Make sure the dialog is always dismissed by pressing
                    // buttons.
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                }
            }
        } else if (key.equals(KEY_USAGE_SETTING)) {
            Message setUsage = mTELHandler.obtainMessage(SET_USAGE_SETTING, sharedPreferences.getString(key, ""));
            mTELHandler.sendMessage(setUsage);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        /* SPRD Bug 849570:Fast Dormancy has problem for setting default value. @{ */
        mFirstDormancySet = preferences.getString("first_dormancy", "true");

        if (mVowifiLabSimAllowedSwitch != null
                && mVowifiLabSimAllowedSwitch.isEnabled()) {
            if (SystemPropertiesProxy.get("persist.sys.vowifi.lab.sim", "false")
                    .equals("true")) {
                mVowifiLabSimAllowedSwitch.setChecked(true);
                mVowifiLabSimAllowedSwitch.setSummary("Open");
            } else {
                mVowifiLabSimAllowedSwitch.setChecked(false);
                mVowifiLabSimAllowedSwitch.setSummary("Close");
            }
        }

        if (mVowifiWhiteListAllowedSwitch != null
                && mVowifiWhiteListAllowedSwitch.isEnabled()) {
            if (SystemPropertiesProxy.get("persist.sys.vowifi.whitelist", "false")
                    .equals("true")) {
                mVowifiWhiteListAllowedSwitch.setChecked(true);
                mVowifiWhiteListAllowedSwitch.setSummary("Open");
            } else {
                mVowifiWhiteListAllowedSwitch.setChecked(false);
                mVowifiWhiteListAllowedSwitch.setSummary("Close");
            }
        }

        if (mImsSwitch != null && mImsSwitch.isEnabled()) {
            if (SystemPropertiesProxy.get("persist.sys.ims.visibility", "false")
                    .equals("true")) {
                mImsSwitch.setChecked(true);
                mImsSwitch.setSummary("Open");
            } else {
                mImsSwitch.setChecked(false);
                mImsSwitch.setSummary("Close");
            }
        }

        String sKeyBack = preferences.getString("key_back", "false");
        // determine whether the back key is pressed
        if (sKeyBack.equals("true")) {
            mFastdormancy.setChecked(false);
            editor.putString("key_back", "false");
            editor.commit();
        } else {
            String sDefault = preferences.getString("default", "");
            // determine whether choose the default value,"true" means choose
            // the default value,"false" means choose not
            if (sDefault.equals("true")) {
                SystemPropertiesProxy.set("persist.vendor.radio.fd.disable", "0");
                /* SPRD Bug 849570:Fast Dormancy has problem for setting default value. @{ */
                Message defaultValueMessage = mTELHandler.obtainMessage(SET_FAST_DORMANCY, FAST_DORMANCY_OPEN,
                        0, FAST_DORMANCY_DEFAULT_VALUE + "");
                mTELHandler.sendMessage(defaultValueMessage);

                editor.putString("fd_open", "true");
                editor.commit();
                mFastdormancy.setSummary(mContext.getString(R.string.Default_value));
                /* @} */
            } else if (sDefault.equals("false")) {
                editor.putString("default", "");
                editor.commit();
                int value;
                // get the self definite value
                String definite_value = preferences.getString("definite_value", "");
                if (definite_value != null && definite_value.length() != 0) {
                    value = Integer.valueOf(definite_value);

                    if (value > 65535 || value <= 0) {
                        Toast.makeText(mContext,
                                "Please input between 1~65535",
                                Toast.LENGTH_SHORT).show();
                        mFastdormancy.setChecked(false);
                        return;
                    }
                    SystemPropertiesProxy.set("persist.vendor.radio.fd.disable", "1");
                    Message fastDormancy = mTELHandler.obtainMessage(
                            SET_FAST_DORMANCY, FAST_DORMANCY_OPEN, 0,
                            definite_value);
                    mTELHandler.sendMessage(fastDormancy);
                    // editor.putString("DORM_TIME",definite_value);
                    editor.putString("fd_open", "true");
                    editor.commit();
                } else {
                    Toast.makeText(mContext, "Please input between 1~65535", Toast.LENGTH_SHORT).show();
                    mFastdormancy.setChecked(false);
                    return;
                }
            }
            /* SPRD Bug 849570:Fast Dormancy has problem for setting default value. @{ */
            if (mFirstDormancySet.equals("true")) {
                Message defaultValueMessage = mTELHandler.obtainMessage(SET_FAST_DORMANCY, FAST_DORMANCY_OPEN, 0, FAST_DORMANCY_DEFAULT_VALUE + "");
                mTELHandler.sendMessage(defaultValueMessage);
            }
            /* @} */
        }
        if (mLoadAPNSwitch != null) {
            mLoadAPNSwitch.setChecked(SystemPropertiesProxy.getBoolean(
                    "persist.sys.loaded.apn", false));
        }
    }

    @Override
    public boolean onPreferenceChange(Preference pref, Object newValue) {
        if (pref == mAutoAnswer) {
            if (!mAutoAnswer.isChecked()) {
                Message openAutoAnswer = mTELHandler
                        .obtainMessage(OPEN_AUTO_ANSWER);
                mTELHandler.sendMessage(openAutoAnswer);
            } else {
                Message closeAutoAnswer = mTELHandler
                        .obtainMessage(CLOSE_AUTO_ANSWER);
                mTELHandler.sendMessage(closeAutoAnswer);
            }
        } else if (pref == mSimTrace) {
            if (!mSimTrace.isChecked()) {
                Message openSimTrace = mTELHandler
                        .obtainMessage(OPEN_SIM_TRACE);
                mTELHandler.sendMessage(openSimTrace);
            } else {
                Message closeSimTrace = mTELHandler
                        .obtainMessage(CLOSE_SIM_TRACE);
                mTELHandler.sendMessage(closeSimTrace);
            }
        } else if (pref == mWpreferplus) {
            if (mWpreferplus.isChecked()) {
                Message msg = mTELHandler.obtainMessage(
                        SET_WPREFER_PLUS_STATUS, 0, 0, null);
                mTELHandler.sendMessage(msg);
            } else {
                Message msg = mTELHandler.obtainMessage(
                        SET_WPREFER_PLUS_STATUS, 1, 0, null);
                mTELHandler.sendMessage(msg);
            }
        } else if (pref == mDNSFilter) {
            if (mDNSFilter.isChecked()) {
                teleApi.dnsFilter().set(0);
                SystemPropertiesProxy.set("sys.engineermode.dns", "0");
            } else {
                teleApi.dnsFilter().set(1);
                SystemPropertiesProxy.set("sys.engineermode.dns", "1");
            }
        } else if (pref == mQosSwitch) {
            if (mQosSwitch.isChecked()) {
                SystemPropertiesProxy.set("persist.vendor.sys.qosstate", "0");
                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
            } else {
                SystemPropertiesProxy.set("persist.vendor.sys.qosstate", "1");
                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
            }
        } else if (pref == mUsbActive) {
            if (mUsbActive.isChecked()) {
                Settings.Global.putInt(mContext.getContentResolver(),
                        SWITCH_FOR_USB_ACTIVE, 0);
                Toast.makeText(mContext, "Close Success", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Settings.Global.putInt(mContext.getContentResolver(),
                        SWITCH_FOR_USB_ACTIVE, 1);
                Toast.makeText(mContext, "Open Success", Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (pref == mSprdIlogSwitch) {
            // Log.setIlogEnable(!mSprdIlogSwitch.isChecked());
            Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
        } else if (pref == mFastdormancy) {
            if (mFastdormancy.isChecked()) {
                SystemPropertiesProxy.set("persist.vendor.radio.fd.disable", "1");
                Message fastDormancy = mTELHandler.obtainMessage(
                        SET_FAST_DORMANCY, FAST_DORMANCY_CLOSE, 0, null);
                mTELHandler.sendMessage(fastDormancy);
                editor.putString("fd_open", "false");
                editor.commit();
            } else {
                String value;
                if (mFastdormancy.getSummary() == null
                        || 0 == mFastdormancy.getSummary().toString()
                                .compareTo("65535")) {
                    value = "1~65535(s)";
                } else {
                    value = mFastdormancy.getSummary().toString();

                }
                // save the summary value
                editor.putString("summary", value);
                editor.commit();
                Intent intent = new Intent("android.engineermode.action.FDActivity");
                mContext.startActivity(intent);

            }
        } else if (pref == mDataServicePreferred) {
            if (!mDataServicePreferred.isChecked()) {
                mTELHandler.sendEmptyMessage(OPEN_DATA_SERVICES_PRE);
            } else {
                mTELHandler.sendEmptyMessage(CLOSE_DATA_SERVICES_PRE);
            }
        } else if (pref == mVTOptionPreference) {
            String message = this.getResources().getString(
                    R.string.vtoption_on_message);
            if (mVTOptionPreference.isChecked()) {
                message = this.getResources().getString(
                        R.string.vtoption_off_message);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(this.getResources().getString(R.string.confirm));
            builder.setMessage(message);
            builder.setNegativeButton(
                    this.getResources().getString(R.string.vt_dialog_cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mVTOptionPreference.setChecked(!mVTOptionPreference
                                    .isChecked());
                            Log.d(TAG,
                                    "setNegativeButton, mVTOptionPreference: "
                                            + mVTOptionPreference.isChecked());
                        }
                    });
            builder.setPositiveButton(
                    this.getResources().getString(R.string.vt_dialog_ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SystemPropertiesProxy.set("persist.sys.support.vt",
                                    mVTOptionPreference.isChecked() ? "true"
                                            : "false");
                            Log.d(TAG, "setPositiveButton,VT open: "
                                    + mVTOptionPreference.isChecked());
                            rebootDevice("VTOption");
                        }
                    });
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface arg0) {
                    // TODO Auto-generated method stub
                    mVTOptionPreference.setChecked(!mVTOptionPreference
                            .isChecked());
                    Log.d(TAG, "Cancel Button, mVTOptionPreference: "
                            + mVTOptionPreference.isChecked());
                }
            });
            builder.show();
        } else if (pref == mLTEDateImpede) {
            int set_flag = mLTEDateImpede.isChecked() ? 0 : 1;
            Message msg = mTELHandler.obtainMessage(LTE_SET_DATA_IMPEDE,
                    set_flag, 0);
            mTELHandler.sendMessage(msg);
        } else if (pref == mWhiteListIgnoreSwitch) {
            if (mWhiteListIgnoreSwitch.isChecked()) {
                mWhiteListIgnoreSwitch.setChecked(false);
                mDualVolteChecked = false;
                mWhiteListIgnoreSwitch.setSummary("Close");
                AlertDialogShow(KEY_WHITELIST_INGORE_SWITCH);
            } else {
                mWhiteListIgnoreSwitch.setChecked(true);
                mDualVolteChecked = true;
                mWhiteListIgnoreSwitch.setSummary("Open");
                AlertDialogShow(KEY_WHITELIST_INGORE_SWITCH);
            }
        } else if (pref == mVowifiLabSimAllowedSwitch) {
            if (mVowifiLabSimAllowedSwitch.isChecked()) {
                mVowifiLabSimAllowedSwitch.setChecked(false);
                mVowifiLabSimAllowedChecked = false;
                mVowifiLabSimAllowedSwitch.setSummary("Close");
                AlertDialogShow(KEY_VOWIFI_LAB_SIM_ALLOWED_SWITCH);
            } else {
                mVowifiLabSimAllowedSwitch.setChecked(true);
                mVowifiLabSimAllowedChecked = true;
                mVowifiLabSimAllowedSwitch.setSummary("Open");
                AlertDialogShow(KEY_VOWIFI_LAB_SIM_ALLOWED_SWITCH);
            }
        } else if (pref == mVowifiWhiteListAllowedSwitch) {
            if (mVowifiWhiteListAllowedSwitch.isChecked()) {
                mVowifiWhiteListAllowedSwitch.setChecked(false);
                mVowifiWhiteListAllowedChecked = false;
                mVowifiWhiteListAllowedSwitch.setSummary("Close");
                AlertDialogShow(KEY_VOWIFI_WHITE_LIST_ALLOWED_SWITCH);
            } else {
                mVowifiWhiteListAllowedSwitch.setChecked(true);
                mVowifiWhiteListAllowedChecked = true;
                mVowifiWhiteListAllowedSwitch.setSummary("Open");
                AlertDialogShow(KEY_VOWIFI_WHITE_LIST_ALLOWED_SWITCH);
            }
        /* SPRD 1034017 disable EVDO @{ */
        } else if (pref == mDisableEvdo) {
            if (mDisableEvdo.isChecked()) {
                Message msg = mTELHandler.obtainMessage(SET_EVDO_STATUS, 0);
                mTELHandler.sendMessage(msg);
            } else {
                Message msg = mTELHandler.obtainMessage(SET_EVDO_STATUS, 1);
                mTELHandler.sendMessage(msg);
            }
        /* @} */
        } else if (pref == mImsSwitch) {
            if (mImsSwitch.isChecked()) {
                mImsSwitch.setChecked(false);
                SystemPropertiesProxy.set("persist.sys.ims.visibility", "false");
                mImsSwitch.setSummary("Close");
            } else {
                mImsSwitch.setChecked(true);
                SystemPropertiesProxy.set("persist.sys.ims.visibility", "true");
                mImsSwitch.setSummary("Open");
            }
        } else if (pref == mLoadAPNSwitch) {
            if (mLoadAPNSwitch.isChecked()) {
                SystemPropertiesProxy.set("persist.sys.loaded.apn", "false");
            } else {
                SystemPropertiesProxy.set("persist.sys.loaded.apn", "true");
            }
        } else if (pref == mMosSwitch) {
            /*
             * SPRD: modify 20160707 Spreadtrum of 578851 EngineerMode, add NXP
             * list and MOS switch @{
             */
            int set_flag = mMosSwitch.isChecked() ? 0 : 1;
            Log.d(TAG, "set_flag is " + set_flag);
            Message msg = mTELHandler
                    .obtainMessage(SET_MOS_STATUS, set_flag, 0);
            mTELHandler.sendMessage(msg);
            /* @} */
        /* SPRD 998834 UPLMN Control @{ */
        } else if (pref == mUplmnCon) {
            if (!mUplmnCon.isChecked()) {
                SystemPropertiesProxy.set("persist.sys.uplmn", "1");
            } else {
                SystemPropertiesProxy.set("persist.sys.uplmn", "0");
            }
        /* SPRD Bug 1048637: C2K Configuration. @{ */
        } else if (pref == mC2kConfig) {
            Message mSetMode = mTELHandler.obtainMessage(SET_C2K_CONFIG, newValue);
            mTELHandler.sendMessage(mSetMode);
        } else if (pref == mMeidSwitch) {
            int meid_flag = mMeidSwitch.isChecked() ? 0 : 1;
            if ( mMeidSwitch.isChecked()) {
                Log.d(TAG, "set close meid_flag is " + meid_flag);
                Message msg = mTELHandler.obtainMessage(SET_MEID_SWITCH, 0);
                mTELHandler.sendMessage(msg);
            } else {
                Log.d(TAG, "set open meid_flag is " + meid_flag);
                Message msg = mTELHandler.obtainMessage(SET_MEID_SWITCH, 1);
                mTELHandler.sendMessage(msg);
            }

        } else if (pref == mDmSwitch) {

            if (mDmSwitch.isChecked()) {
                Settings.Global.putInt(mContext.getContentResolver(),
                        SWITCH_FOR_DM, 0);
                mDmSwitch.setChecked(false);
                mDmSwitch.setSummary("Close");
                Toast.makeText(mContext, "Close Success", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Settings.Global.putInt(mContext.getContentResolver(),
                        SWITCH_FOR_DM, 1);
                mDmSwitch.setChecked(true);
                mDmSwitch.setSummary("Open");
                Toast.makeText(mContext, "Open Success", Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (pref == mVolteEnable) {
            boolean enhanced4gMode = !mVolteEnable.isChecked();
            mVolteEnable.setChecked(enhanced4gMode);
            teleApi.volteEnable().set(enhanced4gMode);
            //UNISOC: add for bug1412063, send AT Command when close volte
            rebootDevice("EngineerMode VolteSetting");
        } else if (pref == mNrCap) {
            mNrCapPreviousValue = Integer.parseInt(mNrCap.getValue());
            Message mNrCapState = mTELHandler.obtainMessage(SET_NR_CAP, newValue.toString());
            mTELHandler.sendMessage(mNrCapState);
        } else {
            return false;
        }
        return true;
    }

    private void ShowVolteEnhancedAlertDialog(boolean isSwitchChecked) {
        String warningMessage;
        if (isSwitchChecked) {
            warningMessage = getString(R.string.dsda_enhanced_open);
        } else {
            warningMessage = getString(R.string.dsda_enhanced_closed);
        }
        AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                .setTitle(getString(R.string.volte_dsda_enhanced))
                .setMessage(warningMessage)
                .setPositiveButton(getString(R.string.alertdialog_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                            }
                        }).create();
        alertDialog.show();
        Log.d(TAG, "alertDialog.show");
    }

    private void checkEngTest() {
        if (teleApi.EngTestStatus().isEngTest()) {
            AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                    .setTitle(getString(R.string.bandselect_warning))
                    .setMessage(getString(R.string.bandselect_warning_message))
                    .setPositiveButton(getString(R.string.alertdialog_ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    // SystemPropertiesProxy.set("persist.radio.engtest.enable",
                                    // "false");
                                }
                            })
                    .setNegativeButton(R.string.alertdialog_cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                }
                            }).create();
            alertDialog.show();
        }

    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        // public boolean onPreferenceClick(Preference pref){
        String key = preference.getKey();
        Log.d(TAG, "onPreferenceTreeClick() preference.getKey():" + preference.getKey() + " preferenceScreen.getKey():" + preferenceScreen.getKey());
        int sim = 0;
        int messType = 0;
        if (key == null) {
            return false;
        /* SPRD Bug 921611: Not show SIM0 and SIM1 interface.@{ */
        } else if (key.equals("band_select")) {
            Log.d(TAG, "mCardCount: " + mCardCount);
            if (mCardCount == 0) {
                Toast.makeText(mContext, getString(R.string.insert_sim_card_prompt), Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent("com.sprd.engineermode.action.BANDMODESET");
                int primaryCardId = mTelephonyManagerSprd.getPrimaryCard();
                Log.d(TAG, "set KEY_PHONEID, primaryCardId=:" + primaryCardId);
                intent.putExtra(KEY_PHONEID, primaryCardId);
                startActivity(intent);
            }
        /* @} */
        }/* SPRD: fix bug346409 Engnieer mode crash @{ */
        else if (key.equals("net_info")) {
            Intent intent = new Intent("android.engineermode.action.SIMSELECT_NETINFO");
            mContext.startActivity(intent);
        } else if (key.equals("nv_item_list")) {
            String ATresult = IATUtils.sendAt("AT", "atchannel0");
            if (ATresult.contains(IATUtils.AT_OK)) {
                Intent intent = new Intent(mContext, NVItemListActivity.class);
                mContext.startActivity(intent);
            } else if (ATresult.contains(IATUtils.AT_FAIL)) {
                Toast.makeText(mContext, "sorry, the modem is not alive!", Toast.LENGTH_LONG).show();
            }
        }/* @} */
        else if (key.equals(KEY_GPRS_SET)) {
            boolean bContinue = false;
            for (int i = 0; i < mPhoneCount; i++) {
                if (TelephonyManagerProxy.getSimState(i) == TelephonyManager.SIM_STATE_READY) {
                    bContinue = true;
                    break;
                }
            }

            if (bContinue) {
                Intent intent = new Intent("android.engineermode.action.GPRSSET");
                mContext.startActivity(intent);
            } else {
                mUiThread.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext,
                                getString(R.string.sim_invalid),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        /*
         * SPRD: modify 20140701 Spreadtrum of 328867 EngineerMode,there is no
         * "NetworkMode" options in Telephony @{
         */
        else if (key.equals(KEY_NETMODE_SELECT)) {
            if (isSupportLTE && !isLplusW) {
                AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                        .setTitle(getString(R.string.network_mode))
                        .setMessage(
                                getString(R.string.switch_mode_warn_message))
                        .setPositiveButton(getString(R.string.alertdialog_ok),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (isLplusG) {
                                            Intent intent = new Intent("android.engineermode.action.NetworkModeActivity");
                                            intent.putExtra(KEY_SIM_INDEX, mPhoneId);
                                            mContext.startActivity(intent);
                                        } else {
                                            Intent intent = new Intent("android.engineermode.action.NetModeSIMActivity");
                                            intent.putExtra(INTENT_CONTENT, "netmode");
                                            mContext.startActivity(intent);
                                        }
                                    }
                                })
                        .setNegativeButton(R.string.alertdialog_cancel,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                    }
                                }).create();
                alertDialog.show();
            } else if (isSupportC2K) {
                Intent intent = new Intent("android.engineermode.action.NetModeSIMActivity");
                intent.putExtra(INTENT_CONTENT, "netmode");
                mContext.startActivity(intent);
            } else {
                if (isLplusW) {
                    Intent intent = new Intent(
                            "android.engineermode.action.NetModeSelection1");
                    intent.putExtra(KEY_SIM_INDEX, 0);
                    mContext.startActivity(intent);
                } else {
                    Intent intent = new Intent(
                            "android.engineermode.action.NetModeSelection");
                    intent.putExtra(KEY_SIM_INDEX, 0);
                    mContext.startActivity(intent);
                }
            }
        }/* modify 20140701 Spreadtrum of 328867@} */
        else if (key.equals(KEY_NETINFO_STATI)) {
            if (mPhoneCount > 1) {
                Intent intent = new Intent(
                        "android.engineermode.action.SIMSELECT_NETSTATI");
                mContext.startActivity(intent);
            } else if (mPhoneCount == 1) {
                Intent intent = new Intent("android.engineermode.action.NETINFOSTATI");
                intent.putExtra(KEY_SIM_INDEX, 0);
                mContext.startActivity(intent);
            }
        }/* SPRD Bug 898844: Volte Settings @{ */
        else if (key.equals(KEY_OPERATOR_SETTING_0)) {
            Log.d(TAG, "mOperatorSetting0");
            Intent intent0 = new Intent("com.sprd.engineermode.action.OPERATORSETTINGSSIM");
            intent0.putExtra(KEY_CHANNEL, 0);
            startActivity(intent0);
        } else if (key.equals(KEY_OPERATOR_SETTING_1)) {
            Log.d(TAG, "mOperatorSetting1");
            Intent intent1 = new Intent("com.sprd.engineermode.action.OPERATORSETTINGSSIM");
            intent1.putExtra(KEY_CHANNEL, 1);
            startActivity(intent1);
        }
        /* @} */
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    class TELHandler extends Handler {
        private int rebootDelay;
        private Button positiveBtn;

        public TELHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            boolean isSuccess = true;
            switch (msg.what) {
                case KEY_SET_REBOOTBUTTON_TEXT:
                    positiveBtn.setText(mContext.getResources().getString(R.string.alertdialog_reboot) + " (" + rebootDelay + ")");
                    if (rebootDelay == 0) {
                        positiveBtn.setEnabled(false);
                    } else if (rebootDelay == -1) {
                        rebootDevice("auto reboot after setting NR Cap");
                    }
                    break;
                case SET_WPREFER_PLUS_STATUS:
                    mMesarg = msg.arg1;
                    try {
                        if (mMesarg == 1) {
                            teleApi.wcdmaPrefer().openWcdmaPrefer();
                        } else {
                            teleApi.wcdmaPrefer().closeWcdmaPrefer();
                        }
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mMesarg == 1) {
                                    mWpreferplus.setChecked(true);
                                } else {
                                    mWpreferplus.setChecked(false);
                                }
                                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mMesarg == 1) {
                                    mWpreferplus.setChecked(false);
                                } else {
                                    mWpreferplus.setChecked(true);
                                }
                                Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
                case GET_SIM_TRACE_STATUS:
                    boolean simTraceState = false;
                    try {
                        simTraceState = teleApi.simTrace().get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (simTraceState) {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mSimTrace.setChecked(true);
                            }
                        });
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mSimTrace.setChecked(false);
                            }
                        });
                    }

                    break;
                case OPEN_SIM_TRACE:
                    try {
                        teleApi.simTrace().open();
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mSimTrace.setChecked(false);
                                Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });
                        break;
                    }
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mSimTrace.setChecked(true);
                        }
                    });

                    break;
                case CLOSE_SIM_TRACE:
                    try {
                        teleApi.simTrace().close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mSimTrace.setChecked(true);
                                Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });
                        break;
                    }
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mSimTrace.setChecked(false);
                        }
                    });
                    break;
                case SET_DSDS_MODE:
                    String dsdsModeStr = (String) msg.obj;
                    setDsdsMode(Integer.parseInt(dsdsModeStr));
                    nowDsdsModeName = SystemPropertiesProxy.get(
                            "persist.vendor.radio.modem.config", "");
                    rebootDevice("setDsdsMode");
                    break;
                case SET_SINGLE_DUAL_MODE_SWITCH:
                    String singleDualSIMModeStr = (String) msg.obj;

                    /*
                     * Reset phoneId values of all SIM information when switch
                     * single-dual SIM mode to make sure applications can get
                     * correct amount of active SIM information after reboot.
                     */
                    ContentValues phoneIdValue = new ContentValues(1);
                    phoneIdValue.put(SubscriptionManagerProxy.SIM_SLOT_INDEX, SubscriptionManagerProxy.INVALID_SIM_SLOT_INDEX);
                    mContext.getContentResolver().update(SubscriptionManagerProxy.CONTENT_URI, phoneIdValue, null,
                            null);

                    setSingeDualSIMModeSwitch(Integer
                            .parseInt(singleDualSIMModeStr));
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {

                            }
                            rebootDevice("setSingleDualMode");
                        }
                    });
                    break;
                case GET_WPREFER_PLUS_STATUS:
                    try {
                        mStrTmp = teleApi.wcdmaPrefer().getWcdmaPrefer();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (mStrTmp != null && mStrTmp.contains("1")) {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mWpreferplus.setChecked(true);
                            }
                        });
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mWpreferplus.setChecked(false);
                            }
                        });
                    }
                    break;

                case SET_CFU: {
                    String valueStr = (String) msg.obj;
                    try {
                        teleApi.callFunc().setCfu(valueStr);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case GET_NR_CAP: {
                    Log.d(TAG, "GET_NR_CAP");
                    String nrCapState = "";
                    try {
                        nrCapState = teleApi.nrCap().getState();
                        isSuccess = true;
                    } catch (Exception e) {
                        isSuccess = false;
                        e.printStackTrace();
                    }
                    final String state = nrCapState;
                    if (isSuccess) {
                        mUiThread.post(() -> {
                            mNrCap.setValueIndex(Integer.parseInt(state));
                            mNrCap.setSummary(mNrCap.getEntry());
                        });
                    } else {
                        mUiThread.post(() -> {
                            mNrCap.setSummary(R.string.feature_not_support);
                            mNrCap.setEnabled(false);
                        });
                    }
                    break;
                }
                case SET_NR_CAP: {
                    String nrCapValue = (String) msg.obj;
                    Log.d(TAG, "SET_NR_CAP: " + nrCapValue);
                    if (nrCapValue.equals(NSA_ONLY)
                            && SystemPropertiesProxy.get(NETWORK_NR_ENABLE, NORMAL_MODE).equals(NR_ONLY_MODE)) {
                        mUiThread.post(() -> {
                            Toast.makeText(mContext, getResources().getString(R.string.unselect_nsa_only_prompt), Toast.LENGTH_LONG).show();
                            mNrCap.setValueIndex(mNrCapPreviousValue);
                        });
                        break;
                    }
                    try {
                        teleApi.nrCap().setState(nrCapValue);
                        isSuccess = true;
                        String dialogMsg = mContext.getResources().getString(R.string.alertdialog_reboot_message);
                        if (dialogMsg != null && dialogMsg.length() > 2) dialogMsg = dialogMsg.substring(2);
                        final AlertDialog alertDialog = new AlertDialog.Builder(
                                mContext)
                                .setTitle("NR Cap Select")
                                .setCancelable(false)
                                .setMessage(dialogMsg)
                                .setPositiveButton(getString(R.string.alertdialog_reboot),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                            }
                                        })
                                .create();
                        alertDialog.show();
                        positiveBtn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                        positiveBtn.setEnabled(false);

                        new Thread(() -> {
                            for (int i = 5; i > -2; i--) {
                                rebootDelay = i;
                                mTELHandler.sendEmptyMessage(KEY_SET_REBOOTBUTTON_TEXT);
                                try {
                                    if (rebootDelay > -1) Thread.sleep(1000);
                                } catch (Exception ex) {
                                    Log.e(TAG, ex.toString(), ex);
                                }
                            }
                        }).start();
                    } catch (Exception e) {
                        isSuccess = false;
                        e.printStackTrace();
                    }
                    if (isSuccess) {
                        Log.d(TAG, "SET_NR_CAP isSuccess");
                        if (nrCapValue.equals(NSA_ONLY)) {
                            SystemPropertiesProxy.set(NETWORK_NR_ENABLE, NSA_ONLY_MODE);
                        } else if (SystemPropertiesProxy.get(NETWORK_NR_ENABLE, NORMAL_MODE).equals(NSA_ONLY_MODE)) {
                            SystemPropertiesProxy.set(NETWORK_NR_ENABLE, NORMAL_MODE);
                        }
                        mUiThread.post(() -> {
                            mNrCap.setSummary(mNrCap.getEntry());
                        });
                    }
                    break;
                }
                case SET_VIDEOTYPE: {
                    String value = (String) msg.obj;
                    try {
                        teleApi.videoType().set(Integer.parseInt(value));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case OPEN_AUTO_ANSWER: {
                    Log.d(TAG, "OPEN_AUTO_ANSWER");
                    try {
                        teleApi.callFunc().openAutoAnswer();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "open auto answer, set vowifi prop, value is: "
                            + SystemPropertiesProxy.getBoolean(AUTO_ANSWER, false));
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mAutoAnswer.setChecked(true);
                            Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
                    break;
                }
                case CLOSE_AUTO_ANSWER: {
                    Log.d(TAG, "CLOSE_AUTO_ANSWER");
                    try {
                        teleApi.callFunc().closeAutoAnswer();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "close auto answer, set vowifi prop, value is: "
                            + SystemPropertiesProxy.getBoolean(AUTO_ANSWER, false));
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mAutoAnswer.setChecked(false);
                            Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
                    break;
                }
                case SET_FAST_DORMANCY:
                    final int arg1 = msg.arg1;
                    int user_value = 0;
                    try {
                        if (arg1 == FAST_DORMANCY_CLOSE) {
                            teleApi.fastDormancy().close();
                        } else {
                            if (msg.obj != null) {
                                user_value = Integer.parseInt((String) msg.obj);
                            }
                            teleApi.fastDormancy().open(user_value);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });
                    }

                    final String setNum = String.valueOf(user_value);
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            /* SPRD Bug 849570:Fast Dormancy has problem for setting default value. @{ */
                            if (mFirstDormancySet.equals("true")) {
                                editor.putString("first_dormancy", "false");
                            } else {
                                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                                String defaultValue = preferences.getString("default", "");
                                if (defaultValue.equals("true")) {
                                    mFastdormancy.setSummary(mContext.getString(R.string.Default_value));
                                    editor.putString("default", "");
                                    editor.commit();
                                } else {
                                    if (arg1 == 1) {
                                        mFastdormancy.setSummary(setNum + "s");
                                    } else {
                                        mFastdormancy.setSummary(setNum);
                                    }
                                    // mDromTime = setNum;
                                    editor.putString(DORM_TIME, setNum);
                                    editor.commit();
                                }
                            }
                            /* @} */
                        }
                    });
                    break;
                case OPEN_DATA_SERVICES_PRE:
                    try {
                        teleApi.dataServicePrefer().open();
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mDataServicePreferred.setChecked(false);
                                Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });
                        return;
                    }
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mDataServicePreferred.setChecked(true);
                            Toast.makeText(mContext, "Success",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case CLOSE_DATA_SERVICES_PRE:
                    try {
                        teleApi.dataServicePrefer().close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mDataServicePreferred.setChecked(true);
                                Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });
                        return;
                    }

                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mDataServicePreferred.setChecked(false);
                            Toast.makeText(mContext, "Success",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case LTE_GET_DATA_IMPEDE:
                    try {
                        mATCmd = engconstents.ENG_AT_GET_LTE_DATA_IMPEDE;
                        Log.d(TAG, "LTE_GET_DATA_IMPEDE" + "  " + "mATCmd: " + mATCmd);
                        mATResponse = IATUtils.sendATCmd(mATCmd, "atchannel0");
                        Log.d(TAG, "LTE_GET_DATA_IMPEDE" + "  " + "mATResponse: "
                                + mATResponse);
                        if (mATResponse != null && mATResponse.contains(IATUtils.AT_OK)) {
                            String[] result = mATResponse.split("\n");
                            String[] result1 = result[0].split(":");
                            String atResponse = result1[1].trim().substring(2);
                            int value = Integer.parseInt(atResponse, 16);
                            Log.d(TAG, "atResponse: " + atResponse + "value: " + value);
                            if ((value & 0x01) > 0) {
                                mUiThread.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mLTEDateImpede.setChecked(true);
                                    }
                                });
                            } else {
                                mUiThread.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mLTEDateImpede.setChecked(false);
                                    }
                                });
                            }
                        } else {
                            mUiThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    mLTEDateImpede.setEnabled(false);
                                }
                            });
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                    break;
                case LTE_SET_DATA_IMPEDE:
                    int set_flag = msg.arg1;
                    mATCmd = engconstents.ENG_AT_SET_LTE_DATA_IMPEDE;
                    Log.d(TAG, "LTE_SET_DATA_IMPEDE" + "  " + "mATCmd: " + mATCmd);
                    mATResponse = IATUtils.sendATCmd(mATCmd + set_flag + ",1",
                            "atchannel0");
                    Log.d(TAG, "LTE_SET_DATA_IMPEDE" + "  " + "mATResponse: "
                            + mATResponse);
                    if (mATResponse != null && mATResponse.contains(IATUtils.AT_OK)) {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "Success",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "Failed",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
                case GET_NXP_STATUS:
                    mATCmd = engconstents.ENG_AT_GET_NXP;
                    Log.d(TAG, "GET_NXP_STATUS" + " " + "mATCmd: " + mATCmd);
                    mATResponse = IATUtils.sendATCmd(mATCmd, "atchannel1");
                    Log.d(TAG, "GET_NXP_STATUS" + " " + "mATResponse: "
                            + mATResponse);
                    if(mATResponse == null) mATResponse = "";

                    String unsupport = "255";
                    int unsupportCount = (mATResponse.length() - mATResponse
                            .replaceAll(unsupport, "").length())
                            / unsupport.length();
                    if (unsupportCount == NXP_COUNT) {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mNxp.setEnabled(false);
                                mNxp.setSummary(R.string.feature_not_support);
                            }
                        });
                        break;
                    }
                    if (mATResponse.contains(IATUtils.AT_OK)) {

                        status = analysisResult(mATResponse);
                        Log.d(TAG, "status size is" + status.length);
                        if (status.length < NXP_COUNT + 1) {
                            mUiThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    mNxp.setEnabled(false);
                                    mNxp.setSummary(R.string.feature_not_support);
                                }
                            });
                            break;
                        }
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < NXP_COUNT; i++) {
                                    if (status[i + 1].contains("1")) {
                                        mNxpList[i].setChecked(true);
                                    } else if (status[i + 1].contains("0")) {
                                        mNxpList[i].setChecked(false);
                                    } else {
                                        mNxpList[i].setEnabled(false);
                                    }
                                }
                            }
                        });
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                if (getNxpStatus) {
                                    Toast.makeText(mContext,
                                            "get nxp status failed",
                                            Toast.LENGTH_SHORT).show();
                                }
                                mNxp.setEnabled(false);
                                mNxp.setSummary(R.string.feature_not_support);
                            }
                        });
                    }
                    break;
                case GET_MEID_SWITCH:
                    try {
                        mATResponse = teleApi.controlMeidSwitch().getMeidStatus();
                        if(mATResponse == null) mATResponse = "";
                        String str[] = mATResponse.split("\n");
                        String ss = "";
                        if(str[0].length() > 1) {
                            ss = str[0].substring(str[0].length() - 2);
                        }
                        final String s = ss;
                        Log.d(TAG, " GET_MEID_SWITCH s = " + s);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                if (s.contains("1")) {
                                    mMeidSwitch.setChecked(true);
                                    mMeidSwitch.setSummary(getString(R.string.status_opened));
                                } else {
                                    mMeidSwitch.setChecked(false);
                                    mMeidSwitch.setSummary(getString(R.string.status_closed));
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mMeidSwitch.setChecked(true);
                                mMeidSwitch.setSummary(getString(R.string.status_opened));
                                //Unisoc: Not support modify for Bug 1406918
                                mMeidSwitch.setEnabled(false);
                            }
                        });
                    }
                    break;
                case SET_MEID_SWITCH:
                    int meid_flag = (int)msg.obj;
                    Log.d(TAG, "SET_MEID_SWITCH" + " set_flag: " + meid_flag);
                    try {
                        if (0 == meid_flag) {
                            teleApi.controlMeidSwitch().closeMeidSwitch();
                        } else {
                            teleApi.controlMeidSwitch().openMeidSwitch();
                        }
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                if (0 == meid_flag) {
                                    mMeidSwitch.setChecked(false);
                                    mMeidSwitch.setSummary(getString(R.string.status_closed));
                                } else if (1 == meid_flag) {
                                    mMeidSwitch.setChecked(true);
                                    mMeidSwitch.setSummary(getString(R.string.status_opened));
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "set failed", Toast.LENGTH_SHORT).show();
                                mMeidSwitch.setChecked(mMeidSwitch.isChecked() ? false : true);
                                if (0==meid_flag) {
                                    mMeidSwitch.setSummary(getString(R.string.status_opened));
                                } else {
                                    mMeidSwitch.setSummary(getString(R.string.status_closed));
                                }

                            }
                        });
                    }
                    break;
                case GET_MOS_STATUS:
                    mATCmd = engconstents.ENG_AT_GET_MOS_STATUS;
                    Log.d(TAG, "GET_MOS_STATUS" + " " + "mATCmd: " + mATCmd);
                    mATResponse = IATUtils.sendATCmd(mATCmd, "atchannel1");
                    Log.d(TAG, "GET_MOS_STATUS" + " " + "mATResponse: "
                            + mATResponse);
                    if (mATResponse != null && mATResponse.contains(IATUtils.AT_OK)) {
                        String str[] = mATResponse.split("\n");
                        final String s = str[0].substring(str[0].length() - 2);
                        Log.d(TAG, s);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                if (s.contains("1")) {
                                    mMosSwitch.setChecked(true);
                                    mMosSwitch.setSummary(R.string.mos_test_mode);
                                } else {
                                    mMosSwitch.setChecked(false);
                                    mMosSwitch
                                            .setSummary(R.string.not_mos_test_mode);
                                }
                            }
                        });
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "GET_MOS_STATUS Failed");
                                mMosSwitch.setSummary(mContext
                                        .getString(R.string.feature_not_support));
                                mMosSwitch.setEnabled(false);
                            }
                        });
                    }
                    break;
                case SET_MOS_STATUS:
                    set_flag = msg.arg1;
                    mATCmd = engconstents.ENG_AT_SET_MOS_STATUS;
                    Log.d(TAG, "SET_MOS_STATUS" + " " + "mATCmd: " + mATCmd);
                    mATResponse = IATUtils.sendATCmd(mATCmd + "," + set_flag,
                            "atchannel1");
                    Log.d(TAG, "SET_MOS_STATUS" + " " + "mATResponse: "
                            + mATResponse);
                    final int mode = set_flag;
                    if (mATResponse != null && mATResponse.contains(IATUtils.AT_OK)) {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "set mos status success",
                                        Toast.LENGTH_SHORT).show();
                                if (0 == mode) {
                                    mMosSwitch
                                            .setSummary(getString(R.string.not_mos_test_mode));
                                } else if (1 == mode) {
                                    mMosSwitch
                                            .setSummary(getString(R.string.mos_test_mode));
                                }
                            }
                        });
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "set mos status failed",
                                        Toast.LENGTH_SHORT).show();
                                mMosSwitch.setChecked(mMosSwitch.isChecked() ? false
                                        : true);
                            }
                        });
                    }
                    break;
                case SET_NXP_STATUS:
                    nxp_index = msg.arg1;
                    mATCmd = engconstents.ENG_AT_SET_NXP_STATUS;
                    Log.d(TAG, "SET_NXP_STATUS" + " " + "mATCmd: " + mATCmd);
                    int setValue = mNxpList[nxp_index - 1].isChecked() ? 1 : 0;
                    mATResponse = IATUtils.sendATCmd(mATCmd + "," + nxp_index + ","
                            + setValue, "atchannel1");
                    Log.d(TAG, "SET_NXP_STATUS" + " " + "mATResponse: "
                            + mATResponse);
                    if (mATResponse != null && mATResponse.contains(IATUtils.AT_OK)) {
                        if (setValue == 1) {
                            mUiThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    mNxpList[nxp_index - 1].setChecked(true);
                                }
                            });
                        } else {
                            mUiThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    mNxpList[nxp_index - 1].setChecked(false);
                                }
                            });
                        }
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "set mos status failed",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
                case GET_EVDO_STATUS:
                    try {
                        mATResponse = teleApi.controlEvdo().getEvdoStatus();
                        if(mATResponse == null) mATResponse="";
                        String str[] = mATResponse.split("\n");
                        final String s = str[0].trim();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                if (s.contains("1")) {
                                    mDisableEvdo.setChecked(true);
                                    mDisableEvdo.setSummary(R.string.status_opened);
                                } else {
                                    mDisableEvdo.setChecked(false);
                                    mDisableEvdo.setSummary(R.string.status_closed);
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mDisableEvdo.setSummary(mContext.getString(R.string.feature_not_support));
                                mDisableEvdo.setEnabled(false);
                            }
                        });
                    }
                    break;
                case SET_EVDO_STATUS:
                    int mEvdoValue = (int)msg.obj;
                    try {
                        if (mEvdoValue == 0) {
                            teleApi.controlEvdo().closeEvdo();
                        } else {
                            teleApi.controlEvdo().openEvdo();
                        }
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                if (0 == mEvdoValue) {
                                    mDisableEvdo.setSummary(getString(R.string.status_closed));
                                } else if (1 == mEvdoValue) {
                                    mDisableEvdo.setSummary(getString(R.string.status_opened));
                                }
                                mTELHandler.postDelayed(new Runnable() {
                                    public void run () {
                                        rebootDevice("evdoDisableSwitch");
                                    }
                                }, 2000);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "set failed", Toast.LENGTH_SHORT).show();
                                mDisableEvdo.setChecked(mDisableEvdo.isChecked() ? false : true);
                            }
                        });
                    }
                    break;
                /* SPRD Bug 1048637: C2K Configuration. @{ */
                case GET_C2K_CONFIG:
                    String c2kConfigReturn;
                    try {
                        c2kConfigReturn = teleApi.c2kConfig().getC2kConfig();
                        c2kConfigReturn = c2kConfigReturn.split(":")[1];
                        String getValue = c2kConfigReturn.contains(",") ?
                                c2kConfigReturn.split(",")[0].trim() : c2kConfigReturn.split("\n")[0].trim();
                        Log.d(TAG, "GET_C2K_CONFIG getValue: " + getValue);
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mC2kConfig.setValueIndex(c2kConfigToIndex(getValue));
                                mC2kConfig.setSummary(mC2kConfig.getEntry());
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case SET_C2K_CONFIG:
                    int c2kConfigVal = Integer.parseInt((String) msg.obj);
                    Log.d(TAG, "SET_C2K_CONFIG c2kConfigValue: " + c2kConfigVal);
                    try {
                        teleApi.c2kConfig().setC2kConfig(indexToC2kConfig(c2kConfigVal));
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                               mC2kConfig.setValueIndex(c2kConfigVal);
                               mC2kConfig.setSummary(mC2kConfig.getEntry());
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
            case GET_USAGE_SETTING:
                try {
                    mATResponse = teleApi.usageSettingApi().get();
                    mUiThread.post(() -> {
                        mUsageSetting.setValueIndex(Integer.parseInt(mATResponse));
                        mUsageSetting.setSummary(mUsageSetting.getEntry());
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    mUiThread.post(() -> {
                        mUsageSetting.setSummary(mContext.getString(R.string.feature_not_support));
                        mUsageSetting.setEnabled(false);
                    });
                }
                break;
            case SET_USAGE_SETTING:
                String usageStr = (String) msg.obj;
                teleApi.usageSettingApi().set(usageStr);
                mUiThread.post(() -> {
                    mUsageSetting.setValueIndex(Integer.parseInt(usageStr));
                    mUsageSetting.setSummary(mUsageSetting.getEntry());
                });
                break;
            default:
                break;
            }
        }
    }

    private String indexToC2kConfig(int value) {
        switch (value) {
            case 0:
                return C2K_CONFIG_DEFAULT;
            case 1:
                return C2K_CONFIG_CTCC;
            case 2:
                return C2K_CONFIG_ROAMING;
            case 3:
                return C2K_CONFIG_CHAP;
            case 4:
                return C2K_CONFIG_EXTENDED2;
            default:
                return C2K_CONFIG_DEFAULT;
        }
    }

    private int c2kConfigToIndex(String str) {
        if (str.contains(C2K_CONFIG_DEFAULT)) {
            return 0;
        } else if (str.contains(C2K_CONFIG_CTCC)) {
            return 1;
        } else if (str.contains(C2K_CONFIG_ROAMING)) {
            return 2;
        } else if (str.contains(C2K_CONFIG_CHAP)) {
            return 3;
        } else if (str.contains(C2K_CONFIG_EXTENDED2)) {
            return 4;
        } else {
            return 0;
        }
    }
    /*
     * SPRD: modify 20160707 Spreadtrum of 578851 EngineerMode, add
     * analysisResult function to analysis the return value of "AT+SPTEST=30,0"
     *
     * @{
     */
    private String[] analysisResult(String result) {
        String[] str = result.split("\n");
        return str[0].split(",");
    }

    /* @} */

    private OnPreferenceClickListener clickListenser = new OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            Log.d(TAG, "clickListenser");
            for (int i = 0; i < NXP_COUNT; i++) {
                if (preference == mNxpList[i]) {
                    Message msg = mTELHandler.obtainMessage(SET_NXP_STATUS,
                            i + 1, 0);
                    mTELHandler.sendMessage(msg);
                    break;
                }
            }
            return false;
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void AlertDialogShow(final String key) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setMessage(getString(R.string.volte_switch_waring))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.alertdialog_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                // begin 550429 modify by suyan.yang 2016.05.10
                                if (key.equals(KEY_VOWIFI_LAB_SIM_ALLOWED_SWITCH)) {
                                    if (mVowifiLabSimAllowedChecked) {
                                        SystemPropertiesProxy.set("persist.sys.vowifi.lab.sim", "true");
                                        SystemPropertiesProxy.set("persist.sys.vowifi.whitelist", "false");
                                    } else {
                                        SystemPropertiesProxy.set("persist.sys.vowifi.lab.sim", "false");
                                    }
                                    rebootDevice("setVowifiLabSimAllowedSwitch");
                                } else if (key
                                        .equals(KEY_VOWIFI_WHITE_LIST_ALLOWED_SWITCH)) {
                                    if (mVowifiWhiteListAllowedChecked) {
                                        SystemPropertiesProxy.set("persist.sys.vowifi.whitelist", "true");
                                        SystemPropertiesProxy.set("persist.sys.vowifi.lab.sim", "false");
                                    } else {
                                        SystemPropertiesProxy.set("persist.sys.vowifi.whitelist", "false");
                                    }
                                    rebootDevice("setVowifiWhiteListAllowedSwitch");
                                } else if (key.equals(KEY_WHITELIST_INGORE_SWITCH)) {
                                    if (mDualVolteChecked) {
                                        SystemPropertiesProxy.set(DSDA_CONTROL, "true");
                                    } else {
                                        SystemPropertiesProxy.set(DSDA_CONTROL, "false");
                                    }
                                    mWhiteListIgnoreSwitch.setChecked(mDualVolteChecked);
                                    rebootDevice("switch_orange");
                                } else if (key.equals(KEY_DISABLE_EVDO)) {
                                    rebootDevice("evdoDisableSwitch");
                                }
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
                                        if (key.equals(KEY_VOWIFI_LAB_SIM_ALLOWED_SWITCH)) {
                                            if (mVowifiLabSimAllowedChecked) {
                                                mVowifiLabSimAllowedSwitch.setChecked(false);
                                                mVowifiLabSimAllowedChecked = false;
                                                mVowifiLabSimAllowedSwitch.setSummary("Close");
                                            } else {
                                                mVowifiLabSimAllowedSwitch.setChecked(true);
                                                mVowifiLabSimAllowedChecked = true;
                                                mVowifiLabSimAllowedSwitch.setSummary("Open");
                                            }
                                        } else if (key.equals(KEY_VOWIFI_WHITE_LIST_ALLOWED_SWITCH)) {
                                            if (mVowifiWhiteListAllowedChecked) {
                                                mVowifiWhiteListAllowedSwitch.setChecked(false);
                                                mVowifiWhiteListAllowedChecked = false;
                                                mVowifiWhiteListAllowedSwitch.setSummary("Close");
                                            } else {
                                                mVowifiWhiteListAllowedSwitch.setChecked(true);
                                                mVowifiWhiteListAllowedChecked = true;
                                                mVowifiWhiteListAllowedSwitch.setSummary("Open");
                                            }
                                        } else if (key.equals(KEY_WHITELIST_INGORE_SWITCH)) {
                                            if (!mDualVolteChecked) {
                                                SystemPropertiesProxy.set(DSDA_CONTROL, "true");
                                                mWhiteListIgnoreSwitch.setChecked(true);
                                                mDualVolteChecked = true;
                                                mWhiteListIgnoreSwitch.setSummary("Open");
                                            } else {
                                                SystemPropertiesProxy.set(DSDA_CONTROL, "false");
                                                mWhiteListIgnoreSwitch.setChecked(false);
                                                mDualVolteChecked = false;
                                                mWhiteListIgnoreSwitch.setSummary("close");
                                            }
                                        }
                                    }
                                });
                            }
                        }).create();
        alertDialog.show();
    }

    private void rebootDevice(String reasonStr) {
        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        pm.reboot(reasonStr);
    }

    private boolean isAppInstalled(Context context, String packagename) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(
                    packagename, 0);
        } catch (NameNotFoundException e) {
            packageInfo = null;
            Log.w(TAG, "package not found: " + packagename);
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }
}
