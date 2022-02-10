package com.sprd.engineermode.debuglog;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;

import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;
import com.unisoc.engineermode.core.impl.nonpublic.SettingsProxy;
import com.unisoc.engineermode.core.impl.nonpublic.SubscriptionManagerProxy;
import com.unisoc.engineermode.core.impl.nonpublic.TelephonyManagerProxy;
import com.unisoc.engineermode.core.impl.telephony.TelephonyManagerSprd;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import android.provider.Settings;
import android.content.Context;

import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.utils.IATUtils;
import android.telephony.SubscriptionManager;
import android.telephony.SubscriptionInfo;
import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.intf.ITelephonyApi;

public class NetModeSelectActivity extends PreferenceActivity implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "NetModeSelectActivity";
    static final String PROPERTY_MULTI_SIM_CONFIG = "persist.radio.multisim.config";
    private static final String KEY = "sim";

    private static final int SET_NETMODE = 1;
    private static final int GET_NETMODE = 2;

    private int mPhoneCount;
    private int mLastValue;
    private ListPreference[] mListPreference;
    private SharedPreferences mSharePref;
    private NMODEHandler mNMODEHandler;
    private Context mContext;
    private NetModeSelectHelper[] mSelectHelper = null;
    private SubscriptionManager mSubscriptionManager;
    private Handler mUiThread = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case SET_NETMODE: {
                int sim = msg.arg1;
                int isSuccess = msg.arg2;

                if (isSuccess == 1) {
                    if (mSelectHelper[sim].getModemType() == TelephonyManagerSprd.MODEM_TYPE_TDSCDMA) {
                        mListPreference[sim].setSummary(mListPreference[sim]
                                .getEntry());
                        finish();
                        Toast.makeText(NetModeSelectActivity.this,
                                "success,modem reset...", Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        mListPreference[sim].setSummary(mListPreference[sim]
                                .getEntry());
                        Toast.makeText(NetModeSelectActivity.this, "success",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String backupString = (String) msg.obj;
                    mListPreference[sim].setValue(backupString);
                    mListPreference[sim].setSummary(mListPreference[sim]
                            .getEntry());
                    SharedPreferences.Editor edit = mSharePref.edit();
                    edit.putString(KEY + sim, backupString);
                    edit.commit();
                    Toast.makeText(NetModeSelectActivity.this, "fail",
                            Toast.LENGTH_SHORT).show();
                }

            }
                break;
            case GET_NETMODE: {
                int sim = msg.arg1;
                mLastValue = msg.arg2;
                mListPreference[sim].setValue(String.valueOf(mLastValue));
                mListPreference[sim]
                        .setSummary(mListPreference[sim].getEntry());
            }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mNMODEHandler = new NMODEHandler(ht.getLooper());

        mPhoneCount = TelephonyManagerProxy.getPhoneCount();
        Log.d(TAG, "mPhoneCount is " + mPhoneCount);

        setPreferenceScreen(getPreferenceManager().createPreferenceScreen(this));
        PreferenceGroup preGroup = getPreferenceScreen();
        mSubscriptionManager = (SubscriptionManager) SubscriptionManager.from(NetModeSelectActivity.this);
        mSharePref = PreferenceManager.getDefaultSharedPreferences(this);
        mListPreference = new ListPreference[mPhoneCount];
        mSelectHelper = new NetModeSelectHelper[mPhoneCount];

        for (int i = 0; i < mPhoneCount; i++) {
            String key = KEY + i;
            mListPreference[i] = new ListPreference(NetModeSelectActivity.this);
            mListPreference[i].setEnabled(false);
            mListPreference[i].setTitle(key);
            mListPreference[i].setKey(key);
            mListPreference[i]
                    .setOnPreferenceChangeListener(NetModeSelectActivity.this);
            preGroup.addPreference(mListPreference[i]);
            mListPreference[i].setEntries(R.array.network_mode_choices_gsm);
            mListPreference[i].setEntryValues(R.array.network_mode_gsm_values);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < mPhoneCount; i++) {
                    mSelectHelper[i] = NetModeSelectHelper
                            .getNetModeSelectHelper(i,mContext);
                }

                mUiThread.post(new Runnable() {
                    @Override
                    public void run() {
                        TelephonyManager tm = null;
                        for (int i = 0; i < mPhoneCount; i++) {
                            tm = TelephonyManagerProxy.getService();
                            if (mSelectHelper[i] != null) {
                                if (tm != null
                                        && tm.getSimState(i) == TelephonyManager.SIM_STATE_READY) {
                                    mListPreference[i].setEnabled(true);
                                } else {
                                    mListPreference[i].setEnabled(false);
                                    continue;
                                }
                                Log.d(TAG, "mSelectHelper[" + i + "], getModemType(): " + mSelectHelper[i].getModemType());
                                if (mSelectHelper[i].getModemType() == TelephonyManagerSprd.MODEM_TYPE_TDSCDMA) {
                                    mListPreference[i]
                                            .setEntries(R.array.network_mode_choices_td);
                                    mListPreference[i]
                                            .setEntryValues(R.array.network_mode_td_values);
                                } else if (mSelectHelper[i].getModemType() == TelephonyManagerSprd.MODEM_TYPE_WCDMA) {
                                    mListPreference[i]
                                            .setEntries(R.array.network_mode_choices_wcdma);
                                    mListPreference[i]
                                            .setEntryValues(R.array.network_mode_wcdma_values);
                                } else {
                                    mListPreference[i]
                                            .setEntries(R.array.network_mode_choices_gsm);
                                    mListPreference[i]
                                            .setEntryValues(R.array.network_mode_gsm_values);
                                }

                                if (mListPreference[i].isEnabled()) {
                                    Log.d(TAG, "sim " + i + " is enable");
                                    Message netMode = mNMODEHandler
                                            .obtainMessage(GET_NETMODE, i, 0);
                                    mNMODEHandler.sendMessage(netMode);
                                }
                            }
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        if (mNMODEHandler != null) {
            Log.d(TAG, "HandlerThread has quit");
            mNMODEHandler.getLooper().quit();
        }
        super.onDestroy();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        Log.d(TAG, "onPreferenceChange and the key is " + key);
        for (int i = 0; i < mPhoneCount; i++) {
            if (key.equals(KEY + i)) {
                String re = newValue.toString();
                Message mNetMode = mNMODEHandler.obtainMessage(SET_NETMODE, i,
                        0, re);
                mNMODEHandler.sendMessage(mNetMode);
            }
        }
        return true;
    }

    /*
     * Modify 324468 & 342378 by sprd When Setting Net Mode in TD/W,
     * EngineerMode need to set Setting Provider after writting NV or sending AT
     * Success
     */
    private boolean refreshSettingProvider(int sim, int value) {
        boolean hasRefreshed = false;
        int subId = slotIdToSubId(sim);
        switch (value) {
            case NetModeSelectHelper.NETMODE_GSMONLY:
                Settings.Global
                        .putInt(getContentResolver(), getSetting(SettingsProxy.Global.PREFERRED_NETWORK_MODE,
                                subId), 1);
                try {
                    int newvalue = Settings.Global
                            .getInt(getContentResolver(), getSetting(SettingsProxy.Global.PREFERRED_NETWORK_MODE,
                                    subId));
                    if (newvalue == 1) {
                        hasRefreshed = true;
                    } else {
                        hasRefreshed = false;
                    }
                    Log.d(TAG, "NetMode Set GSM Only and Refresh Setting Provider " + newvalue);
                } catch (Settings.SettingNotFoundException e) {
                    Log.d(TAG, "catch exception");
                    hasRefreshed = false;
                }
                break;
        case NetModeSelectHelper.NETMODE_TDONLY:
            Settings.Global
                    .putInt(getContentResolver(), getSetting(SettingsProxy.Global.PREFERRED_NETWORK_MODE,
                            subId), 2);
            try {
                int newvalue = Settings.Global
                        .getInt(getContentResolver(), getSetting(SettingsProxy.Global.PREFERRED_NETWORK_MODE,
                                subId));
                if (newvalue == 2) {
                    hasRefreshed = true;
                } else {
                    hasRefreshed = false;
                }
                Log.d(TAG, "NetMode Set TD Only and Refresh Setting Provider " + newvalue);
            } catch (Settings.SettingNotFoundException e) {
                Log.d(TAG, "catch exception");
                hasRefreshed = false;
            }
            break;
        case NetModeSelectHelper.NETMODE_TDPREFER:
            Settings.Global
                    .putInt(getContentResolver(), getSetting(SettingsProxy.Global.PREFERRED_NETWORK_MODE,
                            subId), 0);
            try {
                int newvalue = Settings.Global
                        .getInt(getContentResolver(), getSetting(SettingsProxy.Global.PREFERRED_NETWORK_MODE,
                                subId));
                if (newvalue == 0) {
                    hasRefreshed = true;
                } else {
                    hasRefreshed = false;
                }
                Log.d(TAG, "NetMode Set TD Prefered and Refresh Setting Provider " + newvalue);
            } catch (Settings.SettingNotFoundException e) {
                Log.d(TAG, "catch exception");
                hasRefreshed = false;
            }
            break;
        case NetModeSelectHelper.NETMODE_WCDMAONLY:
            Settings.Global
                    .putInt(getContentResolver(), getSetting(SettingsProxy.Global.PREFERRED_NETWORK_MODE,
                            subId), 2);
            try {
                int newvalue = Settings.Global
                        .getInt(getContentResolver(), getSetting(SettingsProxy.Global.PREFERRED_NETWORK_MODE,
                                subId));
                if (newvalue == 2) {
                    hasRefreshed = true;
                } else {
                    hasRefreshed = false;
                }
                Log.d(TAG, "NetMode Set WCDMA Only and Refresh Setting Provider " + newvalue);
            } catch (Settings.SettingNotFoundException e) {
                Log.d(TAG, "catch exception");
                hasRefreshed = false;
            }
            break;
        case NetModeSelectHelper.NETMODE_WCDMAPREFER:
            Settings.Global
                    .putInt(getContentResolver(), getSetting(SettingsProxy.Global.PREFERRED_NETWORK_MODE,
                            subId), 0);
            try {
                int newvalue = Settings.Global
                        .getInt(getContentResolver(), getSetting(SettingsProxy.Global.PREFERRED_NETWORK_MODE,
                                subId));
                if (newvalue == 0) {
                    hasRefreshed = true;
                } else {
                    hasRefreshed = false;
                }
                Log.d(TAG, "NetMode Set WCDMA Prefered and Refresh Setting Provider " + newvalue);
            } catch (Settings.SettingNotFoundException e) {
                Log.d(TAG, "catch exception");
                hasRefreshed = false;
            }
            break;
        case NetModeSelectHelper.NETMODE_AUTO:
            Settings.Global
                    .putInt(getContentResolver(), getSetting(SettingsProxy.Global.PREFERRED_NETWORK_MODE,
                            subId), 3);
            try {
                int newvalue = Settings.Global
                        .getInt(getContentResolver(), getSetting(SettingsProxy.Global.PREFERRED_NETWORK_MODE,
                                subId));
                if (newvalue == 3) {
                    hasRefreshed = true;
                } else {
                    hasRefreshed = false;
                }
                Log.d(TAG, "NetMode Set Auto and Refresh Setting Provider " + newvalue);
            } catch (Settings.SettingNotFoundException e) {
                Log.d(TAG, "catch exception");
                hasRefreshed = false;
            }
            break;
        default:
            break;
        }
        return hasRefreshed;
    }

    public int slotIdToSubId(int phoneId) {
        int subId;
        SubscriptionInfo mSubscriptionInfo = SubscriptionManagerProxy.getActiveSubscriptionInfoForSimSlotIndex(phoneId);
        if (mSubscriptionInfo != null) {
            subId = mSubscriptionInfo.getSubscriptionId();
        } else {
            subId = SubscriptionManager.getDefaultSubscriptionId();
        }
        return subId;
     }

    public String getSetting(String defaultSetting, int phoneId) {
        if (isMultiSimEnabledEx()) {
            return defaultSetting + phoneId;
        } else {
            return defaultSetting;
        }
    }

    private  boolean isMultiSimEnabledEx() {
        String multiSimConfig = SystemPropertiesProxy.get(PROPERTY_MULTI_SIM_CONFIG);
        return (multiSimConfig.equals("dsds") || multiSimConfig.equals("dsda") || multiSimConfig
                .equals("tsts"));
    }

    class NMODEHandler extends Handler {

        public NMODEHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case SET_NETMODE: {
                    int sim = (int) msg.arg1;
                    String valueStr = (String) msg.obj;
                    int value = Integer.valueOf(valueStr.trim());

                    if (mSelectHelper[sim].setNetMode(value)) {
                        // Modify 324468 by sprd
                        if (refreshSettingProvider(sim, value)) {
                            if (value == NetModeSelectHelper.NETMODE_GSMONLY && (mLastValue == NetModeSelectHelper.NETMODE_TDONLY || mLastValue == NetModeSelectHelper.NETMODE_TDPREFER)
                                    || value == NetModeSelectHelper.NETMODE_TDONLY
                                    || value == NetModeSelectHelper.NETMODE_TDPREFER) {
                                IATUtils.sendATCmd(engconstents.ENG_AT_RESET, sim);
                            }
                            mLastValue = value;
                            mUiThread.sendMessage(mUiThread.obtainMessage(SET_NETMODE,
                                    sim, 1, null));
                        } else {
                            mUiThread.sendMessage(mUiThread.obtainMessage(SET_NETMODE,
                                    sim, 0, String.valueOf(mLastValue)));
                        }
                    } else {
                        mUiThread.sendMessage(mUiThread.obtainMessage(SET_NETMODE,
                                sim, 0, String.valueOf(mLastValue)));
                    }
                    break;
                }
            case GET_NETMODE: {
                int sim = (int) msg.arg1;
                if (mSelectHelper[sim] != null) {
                    int netMode = mSelectHelper[sim].getNetMode();
                    if (netMode != NetModeSelectHelper.NETMODE_INVALID) {

                        mUiThread.sendMessage(mUiThread.obtainMessage(
                                GET_NETMODE, sim, netMode));
                    }
                    Log.d(TAG, "GET_NETMODE  sim = " + sim + " netMode ="
                            + netMode);
                }
                break;
            }
            default:
                break;
            }
        }
    }
}

class NetModeSelectHelper {
    private int mSimIndex = -1;
    private int mModemType = -1;
    private Context mContext = null;

    public final static int NETMODE_INVALID = -1;
    public final static int NETMODE_AUTO = 0;
    public final static int NETMODE_GSMONLY = 1;
    public final static int NETMODE_WCDMAONLY = 2;
    public final static int NETMODE_WCDMAPREFER = 3;
    public final static int NETMODE_TDONLY = 4;
    public final static int NETMODE_TDPREFER = 5;
    // public final static int NETMODE_GSMPREFER = 6;

    /* SPRD BUG 1037573 - change one card ,another card disabled @{ */
    private static final String GSM_ONLY = "10";
    private static final String WCDMA_ONLY = "11";
    private static final String WCDMA_PREFER = "14";
    private static final String TD_ONLY = "12";
    private static final String TD_PREFER = "13";

    private static String netModeSim0;
    private static String netModeSim1;
    /* }@ */

    public static ITelephonyApi teleApi = CoreApi.getTelephonyApi();

    public static NetModeSelectHelper getNetModeSelectHelper(int simIndex,Context context) {
        NetModeSelectHelper helper = new NetModeSelectHelper(simIndex,context);
        if (helper.mSimIndex == -1 || helper.mModemType == -1) {
            return null;
        }

        return helper;
    }

    public int getModemType() {
        return mModemType;
    }

    /* SPRD BUG 1037573 - change one card ,another card disabled @{ */
    public int getNetMode() {
        int netMode = NETMODE_INVALID;
        String atRsp = teleApi.netWorkMode().getNetWorkMode(mSimIndex);
        /* sprd 958489  : W_G network mode @{ */
        if (atRsp.contains(IATUtils.AT_OK)) {
            String[] str = atRsp.split("\\:");
            String[] str1 = str[1].split("\\,");
            String result = str1[0].trim();
            if (mSimIndex == 0) {
                result = str1[0].trim();
                netModeSim0 = result;
                netModeSim1 = str1[1].trim();
            } else {
                result = str1[1].trim();
                netModeSim0 = str1[0].trim();
                netModeSim1 = result;
            }
            if (result.contains("11")) {
                netMode = NETMODE_WCDMAONLY;
            } else if (result.contains("10")) {
                netMode = NETMODE_GSMONLY;
            } else if (result.contains("14")) {
                netMode = NETMODE_WCDMAPREFER;
            } else if (result.contains("18")) {
                netMode = NETMODE_WCDMAONLY;
            } else if (result.contains("13")) {
                netMode = NETMODE_TDPREFER;
            } else if (result.contains("12")) {
                netMode = NETMODE_WCDMAONLY;
            } else if (result.contains("15")) {
                netMode = NETMODE_GSMONLY;
            } else {
                netMode = NETMODE_WCDMAPREFER;
            }
        }
        /* @} */
        return netMode;
    }

    public boolean setNetMode(int netMode) {
        if (checkNetMode(netMode)) {
            String atCmd = null;
            switch (netMode) {
                case NETMODE_AUTO:
                    atCmd = String.format("%s2,0,2,4", engconstents.ENG_AT_NETMODE);
                    break;
                /* sprd 958489  : W_G network mode @{ */
                case NETMODE_GSMONLY:
                    if (mSimIndex == 0) {
                        atCmd = String.format("%s%s,%s", engconstents.ENG_AT_SET_WCDMA_MODEM, GSM_ONLY, netModeSim1);
                        netModeSim0 = GSM_ONLY;
                    } else {
                        atCmd = String.format("%s%s,%s", engconstents.ENG_AT_SET_WCDMA_MODEM, netModeSim0, GSM_ONLY);
                        netModeSim1 = GSM_ONLY;
                    }
                    break;
                case NETMODE_WCDMAONLY:
                    if (mSimIndex == 0) {
                        atCmd = String.format("%s%s,%s", engconstents.ENG_AT_SET_WCDMA_MODEM, WCDMA_ONLY, netModeSim1);
                        netModeSim0 = WCDMA_ONLY;
                    } else {
                        atCmd = String.format("%s%s,%s", engconstents.ENG_AT_SET_WCDMA_MODEM, netModeSim0, WCDMA_ONLY);
                        netModeSim1 = WCDMA_ONLY;
                    }
                    break;
                case NETMODE_WCDMAPREFER:
                    if (mSimIndex == 0) {
                        atCmd = String.format("%s%s,%s", engconstents.ENG_AT_SET_WCDMA_MODEM, WCDMA_PREFER, netModeSim1);
                        netModeSim0 = WCDMA_PREFER;
                    } else {
                        atCmd = String.format("%s%s,%s", engconstents.ENG_AT_SET_WCDMA_MODEM, netModeSim0, WCDMA_PREFER);
                        netModeSim1 = WCDMA_PREFER;
                    }
                    break;
                case NETMODE_TDONLY:
                    if (mSimIndex == 0) {
                        atCmd = String.format("%s%s,%s", engconstents.ENG_AT_SET_WCDMA_MODEM, TD_ONLY, netModeSim1);
                        netModeSim0 = TD_ONLY;
                    } else {
                        atCmd = String.format("%s%s,%s", engconstents.ENG_AT_SET_WCDMA_MODEM, netModeSim0, TD_ONLY);
                        netModeSim1 = TD_ONLY;
                    }
                    break;
                case NETMODE_TDPREFER:
                    if (mSimIndex == 0) {
                        atCmd = String.format("%s%s,%s", engconstents.ENG_AT_SET_WCDMA_MODEM, TD_PREFER, netModeSim1);
                        netModeSim0 = TD_PREFER;
                    } else {
                        atCmd = String.format("%s%s,%s", engconstents.ENG_AT_SET_WCDMA_MODEM, netModeSim0, TD_PREFER);
                        netModeSim1 = TD_PREFER;
                    }
                    break;
            }
            /* @} */
            try {
                teleApi.netWorkMode().setNetWorkMode(atCmd, mSimIndex);
                return true;
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }
    /* }@ */

    private NetModeSelectHelper(int simIndex, Context context) {
        mSimIndex = simIndex;
        mContext = context;
        TelephonyManagerSprd tm = TelephonyManagerSprd.getInstance();
        int previousSetPhoneId = tm.getPrimaryCard();
        if (TelephonyManagerSprd.getModemType() == TelephonyManagerSprd.MODEM_TYPE_TDSCDMA) {
            if (mSimIndex == previousSetPhoneId) {
                mModemType = TelephonyManagerSprd.MODEM_TYPE_TDSCDMA;
            } else {
                mModemType = TelephonyManagerSprd.MODEM_TYPE_GSM;
            }
        } else if (TelephonyManagerSprd.getModemType() == TelephonyManagerSprd.MODEM_TYPE_WCDMA) {
            if (mSimIndex == previousSetPhoneId) {
                mModemType = TelephonyManagerSprd.MODEM_TYPE_WCDMA;
            } else {
                if (TelephonyManagerSprd.getRadioCapbility() == TelephonyManagerSprd.RadioCapbility.WW) {
                    mModemType = TelephonyManagerSprd.MODEM_TYPE_WCDMA;
                } else {
                    mModemType = TelephonyManagerSprd.MODEM_TYPE_GSM;
                }
            }
        } else {
            mModemType = TelephonyManagerSprd.MODEM_TYPE_GSM;
        }
    }

    private boolean checkNetMode(int netMode) {
        if ((mModemType == TelephonyManagerSprd.MODEM_TYPE_GSM)
                && ((netMode == NETMODE_AUTO) || (netMode == NETMODE_GSMONLY))) {
            return true;
        } else if ((mModemType == TelephonyManagerSprd.MODEM_TYPE_WCDMA)
                && ((netMode == NETMODE_AUTO) || (netMode == NETMODE_GSMONLY)
                        || (netMode == NETMODE_WCDMAONLY) || (netMode == NETMODE_WCDMAPREFER))) {
            return true;
        } else if ((mModemType == TelephonyManagerSprd.MODEM_TYPE_TDSCDMA)
                && ((netMode == NETMODE_AUTO) || (netMode == NETMODE_GSMONLY)
                        || (netMode == NETMODE_TDONLY) || (netMode == NETMODE_TDPREFER))) {
            return true;
        } else {
            return false;
        }
    }

}
