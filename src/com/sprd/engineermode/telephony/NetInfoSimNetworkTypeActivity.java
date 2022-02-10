/*This code functions as follows:
 * 1, real-time monitoring of changes in the type of network
 * 2, to identify the type of network
 */
package com.sprd.engineermode.telephony;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.impl.nonpublic.ServiceStateProxy;
import com.unisoc.engineermode.core.impl.nonpublic.SubscriptionManagerProxy;
import com.unisoc.engineermode.core.impl.nonpublic.TelephonyManagerProxy;
import com.unisoc.engineermode.core.impl.telephony.TelephonyManagerSprd;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.utils.IATUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetInfoSimNetworkTypeActivity extends PreferenceActivity {

    private static final String TAG = "NetInfoSimNetworkTypeActivity";
    private static final String KEY_SIM_INDEX = "simindex";
    private static final String PREF_INDEX = "PrefenceIndex";
    private static final String LTE_INDEX = "LteIndex";
    private static final String NETWORK_TYPE = "NetWorkType";
    private static final String KEY_TDD_SVLTE = "TDD_SVLTE";
    private static final String KEY_FDD_CSFB = "FDD_CSFB";
    private static final String KEY_TDD_CSFB = "TDD_CSFB";
    private static final String NETWORK_STAT_CHANGE = "com.sprd.network.NETWORK_STAT_CHANGE";

    private static final int NETWORK_UNKNOW = 0;
    private static final int NETWORK_GSM = 1;
    private static final int NETWORK_TDSCDMA = 2;
    private static final int NETWORK_WCDMA = 3;
    private static final int NETWORK_LTE = 4;
    /* SPRD 1020953: C2K feature @{ */
    private static final int NETWORK_CDMADO = 5;
    private static final int NETWORK_CDMA1X = 6;
    private static final int NETWORK_CDMA1X_DO = 200; //in order to diff with TelephonyManager.NETWORK_TYPE..... (1 ~ 20)
    private static final int NETWORK_CDMA1X_LTE = 201; //in order to diff with TelephonyManager.NETWORK_TYPE..... (1 ~ 20)
    /* @} */
    /* SPRD 5G feature. @{ */
    private static final int NETWORK_NR = 7;
    /* @} */
    /**
     * SPRD Add For LTE_CA 5g+ Supports.(Bug 570270)
     * TelephonyManager.NETWORK_TYPE_LTE_CA is added on SPRD_Trunk,
     * but Engineermode app is shared with Trunk and Prime,
     * so needs to be defined locally.
     */
    private static final int NETWORK_TYPE_LTE_CA = 19;

    private static final int LTE_CHANGE = 0;

    static final boolean DEBUG = true;

    /* SPRD Bug 815691:Do not show Outfield Network information in ue cap. @{ */
    //private Preference[] mSIMPref;
    private ArrayList<Preference> mSIMPref;
    /* @} */
    private TelephonyManager mTelephonyManager;
    //    private SubscriptionManager mSubscriptionManager;
    private PhoneStateListener mPhoneStateListener;
    private PreferenceCategory mPreferenceCategory;
    private PreferenceGroup mPreGroup = null;
    private ServiceState mServiceState;
    private boolean mListening = false;

    private int mDataNetType = TelephonyManager.NETWORK_TYPE_UNKNOWN;
    private int mSimIndex;
    private int mNetWorkType = NETWORK_UNKNOW;
    private int mPrefenceIndex;

    private int[] PrefenceName = null;

    /* SPRD Bug 815691:Do not show Outfield Network information in ue cap. @{ */
    private int[] PreferenceNameGSM = new int[]{R.string.netinfo_server_cell,
        R.string.netinfo_neighbour_cell, R.string.netinfo_adjacent_cell_3g,
        R.string.netinfo_outfield_information};

    private int[] PreferenceNameGSMSupportLte = new int[] { R.string.netinfo_server_cell,
            R.string.netinfo_neighbour_cell, R.string.netinfo_adjacent_cell_3g,
            R.string.netinfo_adjacent_cell_5g, R.string.netinfo_outfield_information };

    private int[] PreferenceName1xSupportLte = new int[] { R.string.netinfo_server_cell,
            R.string.netinfo_neighbour_cell };

    private int[] PreferenceNameDoSupportLte = new int[] { R.string.netinfo_server_cell,
            R.string.netinfo_neighbour_cell, R.string.netinfo_adjacent_cell_5g };

    private String[] PreferenceIndexGSM = new String[] { "PrefenceIndex0",
            "PrefenceIndex1", "PrefenceIndex2", "PrefenceIndex4" };

    private String[] PreferenceIndexGSMSupportLte = new String[] { "PrefenceIndex0",
            "PrefenceIndex1", "PrefenceIndex2", "PrefenceIndex3", "PrefenceIndex4" };

    private String[] PreferenceIndex1xSupportLte = new String[] { "PrefenceIndex0",
            "PrefenceIndex1"};

    private String[] PreferenceIndexDoSupportLte = new String[] { "PrefenceIndex0",
            "PrefenceIndex1", "PrefenceIndex2" };

    private int[] PreferenceNameCDMA = new int[]{R.string.netinfo_server_cell,
        R.string.netinfo_neighbour_cell, R.string.netinfo_adjacent_cell_2g,
        R.string.netinfo_outfield_information};

    private int[] PreferenceNameCDMASupportLte = new int[] { R.string.netinfo_server_cell,
            R.string.netinfo_neighbour_cell, R.string.netinfo_adjacent_cell_2g,
            R.string.netinfo_adjacent_cell_5g, R.string.netinfo_outfield_information };

    private String[] PreferenceIndexCDMA = new String[]{"PrefenceIndex0",
        "PrefenceIndex1", "PrefenceIndex2", "PrefenceIndex4"};

    private String[] PreferenceIndexCDMASupportLte = new String[]{"PrefenceIndex0",
        "PrefenceIndex1", "PrefenceIndex2", "PrefenceIndex3", "PrefenceIndex4"};

    private int[] PreferenceNameLTE = new int[]{R.string.netinfo_server_cell,
        R.string.netinfo_neighbour_cell, R.string.netinfo_adjacent_cell_2g,
        R.string.netinfo_adjacent_cell_3g,
        R.string.netinfo_outfield_information};

    private String[] PreferenceIndexLTE = new String[]{"PrefenceIndex0",
        "PrefenceIndex1", "PrefenceIndex2", "PrefenceIndex3", "PrefenceIndex4"};

    private int[] PreferenceNameNR = new int[] { R.string.netinfo_server_cell,
            R.string.netinfo_adjacent_cell_4g,
            //R.string.netinfo_adjacent_cell_3g,
            R.string.netinfo_adjacent_cell_5g,
            R.string.netinfo_outfield_information };
    private String[] PreferenceIndexNR = new String[] {
            "PrefenceIndex0",
            "PrefenceIndex1",
            //"PrefenceIndex2",
            "PrefenceIndex2",
            "PrefenceIndex3"};

    private int[] PreferenceNameLTE1X = new int[] { R.string.netinfo_server_cell_lte,
            R.string.netinfo_neighbour_cell_lte, R.string.netinfo_adjacent_cell_2g_lte,
            R.string.netinfo_adjacent_cell_3g_lte,
            R.string.netinfo_outfield_information_lte,
            R.string.netinfo_server_cell_1x, R.string.netinfo_neighbour_cell_1x};

    private String[] PreferenceIndexLTE1X = new String[] { "PrefenceIndex0",
            "PrefenceIndex1", "PrefenceIndex2", "PrefenceIndex3", "PrefenceIndex4",
            "PrefenceIndex5", "PrefenceIndex6" };

    private int[] PreferenceNameDO1X = new int[] { R.string.netinfo_server_cell_do,
            R.string.netinfo_neighbour_cell_do,
            R.string.netinfo_adjacent_cell_5g,
            //R.string.netinfo_outfield_information,
            R.string.netinfo_server_cell_1x, R.string.netinfo_neighbour_cell_1x};

    private String[] PreferenceIndexDO1X = new String[] { "PrefenceIndex0",
            "PrefenceIndex1", "PrefenceIndex2", "PrefenceIndex3",
            "PrefenceIndex4"};

    private ITelephonyApi teleApi = CoreApi.getTelephonyApi();

    // Detect whether the phone supports 5g network
    private boolean isSupportSVLTE = TelephonyManagerSprd.RadioCapbility.TDD_SVLTE.equals(TelephonyManagerSprd.getRadioCapbility());
    private boolean isSupportLTE = teleApi.telephonyInfo().isSupportLte();
    /* @} */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (TelephonyManagerSprd.getRadioCapbility() == TelephonyManagerSprd.RadioCapbility.WG
            || TelephonyManagerSprd.getRadioCapbility() == TelephonyManagerSprd.RadioCapbility.WW) {
            PrefenceName = new int[] { R.string.netinfo_server_cell,
                    R.string.netinfo_neighbour_cell, R.string.netinfo_adjacent_cell_3g,
                    R.string.netinfo_outfield_information};
        }else {
            PrefenceName = new int[] { R.string.netinfo_server_cell,
                    R.string.netinfo_neighbour_cell, R.string.netinfo_adjacent_cell_3g,
                    R.string.netinfo_adjacent_cell_5g,
                    R.string.netinfo_outfield_information};
        }
        setPreferenceScreen(getPreferenceManager().createPreferenceScreen(this));
        mPreGroup = getPreferenceScreen();
        NetTypeDisplay();
        mServiceState = new ServiceState();
        mTelephonyManager = TelephonyManagerProxy.getService();
        Intent intent = this.getIntent();
        mSimIndex = intent.getIntExtra(KEY_SIM_INDEX, -1);
        mPhoneStateListener = getPhoneStateListener(mSimIndex);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSimIndex != -1) {
            setTitle("SIM" + mSimIndex);
            /* SPRD 1141002: NetInfo show problem. @{ */
            updateDataNetType(mSimIndex);
            /* @} */
        } else {
            finish();
        }
        Log.d(TAG, "onResume SimIndex=" + mSimIndex);
        listenForPhoneState(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        listenForPhoneState(false);
    }

    void listenForPhoneState(boolean start) {
        if (start) {
            if (!mListening) {
                if (mPhoneStateListener != null) {
                    Log.d(TAG, "start listen");
                    mTelephonyManager.listen(mPhoneStateListener,
                            PhoneStateListener.LISTEN_SERVICE_STATE
                                | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
                                | PhoneStateListener.LISTEN_DATA_ACTIVITY);
                    mListening = true;
                }
            }
        } else {
            if (mPhoneStateListener != null) {
                Log.d(TAG, "stop listen");
                mTelephonyManager.listen(mPhoneStateListener,
                    PhoneStateListener.LISTEN_NONE);
                mListening = false;
            }
        }
    }

    // The first interface display function
    // if it has 5g network,it will display "Lte",or it will not display
    private void NetTypeDisplay() {
        mPreferenceCategory = new PreferenceCategory(this);
        mPreGroup.addPreference(mPreferenceCategory);
        /* SPRD Bug 815691:Do not show Outfield Network information in ue cap. @{ */
        mSIMPref = new ArrayList<Preference>();
        for (int i = 0; i < PrefenceName.length; i++) {
            Preference pref = new Preference(this);
            pref.setTitle(PrefenceName[i]);
            pref.setKey(PREF_INDEX + i);
            mSIMPref.add(pref);
            mPreGroup.addPreference(pref);
        }
        /* @} */
    }

    // phoneId get subid
    public int slotIdToSubId(int phoneId) {
        int subId;
        SubscriptionInfo mSubscriptionInfo = SubscriptionManagerProxy.getActiveSubscriptionInfoForSimSlotIndex(phoneId);
        if (mSubscriptionInfo == null) {
            Log.d(TAG, "mSubscriptionInfo is null");
            subId = SubscriptionManager.getDefaultSubscriptionId();
        } else {
            subId = mSubscriptionInfo.getSubscriptionId();
        }
        return subId;
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        MyPhoneStateListener(int subId) {
            setSubId(subId);
        }

        private void setSubId(int subId) {
            try {
                getClass().getSuperclass().getField("mSubId").set(this, subId);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    private PhoneStateListener getPhoneStateListener(final int phoneId) {
        int subId = slotIdToSubId(phoneId);
        return new MyPhoneStateListener(subId) {
            @Override
            public void onServiceStateChanged(ServiceState state) {
                if (DEBUG) {
                    Log.d(TAG, "onServiceStateChanged on phoneId" + phoneId
                        + "state=" + state.getState());
                }
                mServiceState = state;
                mDataNetType = ServiceStateProxy.getDataNetworkType(mServiceState);
                updateDataNetType(phoneId);
            }

            @Override
            public void onDataConnectionStateChanged(int state, int networkType) {
                if (DEBUG) {
                    Log.d(TAG, "onDataConnectionStateChanged: state=" + state
                        + " type=" + networkType + " phoneId=" + phoneId);
                }
                if (!isSupportSVLTE) {
                    if (networkType == TelephonyManager.NETWORK_TYPE_UNKNOWN) {
                        //mDataNetType = mServiceState.getNetworkType();
//                        mDataNetType = mServiceState.getDataNetworkType();
                        mDataNetType = ServiceStateProxy.getDataNetworkType(mServiceState);
                    } else {
                        mDataNetType = networkType;
                    }
                } else {
                    mDataNetType = networkType;
                }
                updateDataNetType(phoneId);
            }

        };
    }

    /* SPRD Bug 815691:Do not show Outfield Network information in ue cap. @{ */
    private void reNetTypeDisplay(String type) {
        mSIMPref.clear();
        mPreGroup.removeAll();

        mPreGroup.addPreference(mPreferenceCategory);
        /* SPRD 1020953: C2K feature @{ */
        if ("GSM".equals(type)) {
            if (isSupportLTE) {
                Log.d(TAG, "NetType SupportLTE, current NetType is GSM!");
                for (int i = 0; i < PreferenceNameGSMSupportLte.length; i++) {
                    Preference preference = new Preference(this);
                    preference.setTitle(PreferenceNameGSMSupportLte[i]);
                    preference.setKey(PreferenceIndexGSMSupportLte[i]);
                    preference.setEnabled(true);

                    mSIMPref.add(preference);
                    mPreGroup.addPreference(preference);
                }
            } else {
                Log.d(TAG, "NetType not SupportLTE, current NetType is GSM!");
                for (int i = 0; i < PreferenceNameGSM.length; i++) {
                    Preference preference = new Preference(this);
                    preference.setTitle(PreferenceNameGSM[i]);
                    preference.setKey(PreferenceIndexGSM[i]);
                    preference.setEnabled(true);

                    mSIMPref.add(preference);
                    mPreGroup.addPreference(preference);
                }
            }
        } else if ("CDMA 1X".equals(type)) {
            Log.d(TAG, "NetType SupportLTE, current NetType is CDMA 1X!");
            for (int i = 0; i < PreferenceName1xSupportLte.length; i++) {
                Preference preference = new Preference(this);
                preference.setTitle(PreferenceName1xSupportLte[i]);
                preference.setKey(PreferenceIndex1xSupportLte[i]);
                preference.setEnabled(true);

                mSIMPref.add(preference);
                mPreGroup.addPreference(preference);
            }

        } else if ("CDMA DO".equals(type)) {
            Log.d(TAG, "NetType SupportLTE, current NetType is CDMA DO!");
            for (int i = 0; i < PreferenceNameDoSupportLte.length; i++) {
                Preference preference = new Preference(this);
                preference.setTitle(PreferenceNameDoSupportLte[i]);
                preference.setKey(PreferenceIndexDoSupportLte[i]);
                preference.setEnabled(true);

                mSIMPref.add(preference);
                mPreGroup.addPreference(preference);
            }

        } else if ("TD-SCDMA".equals(type) || "WCDMA".equals(type)) {
            if (isSupportLTE) {
                Log.d(TAG, "NetType SupportLTE, current NetType is TD-SCDMA/WCDMA!");
                for (int i = 0; i < PreferenceNameCDMASupportLte.length; i++) {
                    Preference preference = new Preference(this);
                    preference.setTitle(PreferenceNameCDMASupportLte[i]);
                    preference.setKey(PreferenceIndexCDMASupportLte[i]);
                    preference.setEnabled(true);

                    mSIMPref.add(preference);
                    mPreGroup.addPreference(preference);
                }
            } else {
                Log.d(TAG, "NetType not SupportLTE, current NetType is TD-SCDMA/WCDMA!");
                for (int i = 0; i < PreferenceNameCDMA.length; i++) {
                    Preference preference = new Preference(this);
                    preference.setTitle(PreferenceNameCDMA[i]);
                    preference.setKey(PreferenceIndexCDMA[i]);
                    preference.setEnabled(true);

                    mSIMPref.add(preference);
                    mPreGroup.addPreference(preference);
                }
            }
        } else if ("LTE".equals(type)) {
            Log.d(TAG, "current NetType is LTE!");
            for (int i = 0; i < PreferenceNameLTE.length; i++) {
                Preference preference = new Preference(this);
                preference.setTitle(PreferenceNameLTE[i]);
                preference.setKey(PreferenceIndexLTE[i]);
                preference.setEnabled(true);

                mSIMPref.add(preference);
                mPreGroup.addPreference(preference);
            }
        } else if ("LTE_1X".equals(type)) {
            Log.d(TAG, "current NetType is LTE_1X!");
            for (int i = 0; i < PreferenceNameLTE1X.length; i++) {
                Preference preference = new Preference(this);
                preference.setTitle(PreferenceNameLTE1X[i]);
                preference.setKey(PreferenceIndexLTE1X[i]);
                preference.setEnabled(true);

                mSIMPref.add(preference);
                mPreGroup.addPreference(preference);
            }
        } else if ("DO_1X".equals(type)) {
            Log.d(TAG, "current NetType is LTE_DO!");
            for (int i = 0; i < PreferenceNameDO1X.length; i++) {
                Preference preference = new Preference(this);
                preference.setTitle(PreferenceNameDO1X[i]);
                preference.setKey(PreferenceIndexDO1X[i]);
                preference.setEnabled(true);

                mSIMPref.add(preference);
                mPreGroup.addPreference(preference);
            }
        /* SPRD 5G feature. @{ */
        } else if ("NR".equals(type)) {
            Log.d(TAG, "current NetType is NR!");
            for (int i = 0; i < PreferenceNameNR.length; i++) {
                Preference preference = new Preference(this);
                preference.setTitle(PreferenceNameNR[i]);
                preference.setKey(PreferenceIndexNR[i]);
                preference.setEnabled(true);

                mSIMPref.add(preference);
                mPreGroup.addPreference(preference);
            }
            /* @} */
        } else if ("UNKNOW".equals(type)) {
            Log.d(TAG, "current NetType is UNKNOW!");
            if (TelephonyManagerSprd.getRadioCapbility() == TelephonyManagerSprd.RadioCapbility.WG
                || TelephonyManagerSprd.getRadioCapbility() == TelephonyManagerSprd.RadioCapbility.WW) {
                Log.d(TAG, "getRadioCapbility result is WG/WW!");
                for (int i = 0; i < PreferenceNameCDMA.length; i++) {
                    Preference preference = new Preference(this);
                    preference.setTitle(PreferenceNameCDMA[i]);
                    preference.setKey(PreferenceIndexCDMA[i]);
                    preference.setEnabled(false);
                    mSIMPref.add(preference);
                    mPreGroup.addPreference(preference);
                }
            } else {
                Log.d(TAG, "getRadioCapbility result is not WG/WW!");
                for (int i = 0; i < PreferenceNameLTE.length; i++) {
                    Preference preference = new Preference(this);
                    preference.setTitle(PreferenceNameLTE[i]);
                    preference.setKey(PreferenceIndexLTE[i]);
                    preference.setEnabled(false);
                    mSIMPref.add(preference);
                    mPreGroup.addPreference(preference);
                }
            }
        } else {
            Log.d(TAG, "NetType is incorrect!");
        }
    }
    /* @} */

    private void showNwCap(int netType, int phoneId) {
        //convert NETWORK_TYPE_LTE_CA to NETWORK_TYPE_LTE becuase the first is not const value
        if (netType == TelephonyManagerProxy.NETWORK_TYPE_LTE_CA) {
            netType = TelephonyManager.NETWORK_TYPE_LTE;
        }
        switch(netType) {
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_IDEN:
            case TelephonyManager.NETWORK_TYPE_GSM:
                /* SPRD Bug 815691:Do not show Outfield Network information in ue cap. @{ */
                mPreferenceCategory.setTitle("GSM");
                mNetWorkType = NETWORK_GSM;
                Log.d(TAG, "Networktype is GSM");

                reNetTypeDisplay("GSM");
                /* @} */
                break;
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                mPreferenceCategory.setTitle("CDMA 1X");
                mNetWorkType = NETWORK_CDMA1X;
                Log.d(TAG, "Networktype is CDMA 1X");

                reNetTypeDisplay("CDMA 1X");
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                /* SPRD Bug 815691:Do not show Outfield Network information in ue cap. @{ */
                mNetWorkType = check3GNetWork(mSimIndex);
                if (mNetWorkType == NETWORK_TDSCDMA) {
                    mPreferenceCategory.setTitle("TD-SCDMA");
                    Log.d(TAG, "Networktype is TDSCDMA");
                    reNetTypeDisplay("TD-SCDMA");
                } else if (mNetWorkType == NETWORK_WCDMA) {
                    mPreferenceCategory.setTitle("WCDMA");
                    Log.d(TAG, "Networktype is WCDMA");
                    reNetTypeDisplay("WCDMA");
                }
                /* SPRD 1020953: C2K feature @{ */
                else if (mNetWorkType == NETWORK_CDMADO) {
                    mPreferenceCategory.setTitle("CDMA DO");
                    Log.d(TAG, "Networktype is CDMA DO");
                    reNetTypeDisplay("CDMA DO");
                }
                /* @} */
                /* @} */
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
//            case TelephonyManagerProxy.NETWORK_TYPE_LTE_CA: // SRPD Add to Support LTE_CA.
                /* SPRD Bug 815691:Do not show Outfield Network information in ue cap. @{ */
                mPreferenceCategory.setTitle("LTE");
                mNetWorkType = NETWORK_LTE;
                Log.d(TAG, "Networktype is LTE");

                reNetTypeDisplay("LTE");
                /* @} */
                break;
            case NETWORK_CDMA1X_LTE:
                mPreferenceCategory.setTitle("LTE and 1X");
                mNetWorkType = NETWORK_CDMA1X_LTE;
                Log.d(TAG, "Networktype is LTE and 1X");

                reNetTypeDisplay("LTE_1X");
                break;
            case NETWORK_CDMA1X_DO:
                mPreferenceCategory.setTitle("DO and 1X");
                mNetWorkType = NETWORK_CDMA1X_DO;
                Log.d(TAG, "Networktype is DO and 1X");

                reNetTypeDisplay("DO_1X");
                break;
            /* SPRD 5G feature. @{ */
            case TelephonyManager.NETWORK_TYPE_NR:
                mPreferenceCategory.setTitle("NR");
                mNetWorkType = NETWORK_NR;
                Log.d(TAG, "Networktype is NR");
                reNetTypeDisplay("NR");
                break;
                /* @} */
            default:
                /* SPRD Bug 815691:Do not show Outfield Network information in ue cap. @{ */
                mPreferenceCategory.setTitle(R.string.unknow_server);
                mNetWorkType = NETWORK_UNKNOW;
                Log.d(TAG, "modem type is UnKnow");

                reNetTypeDisplay("UNKNOW");
                /* @} */
                break;
        }
        sendNetWorkStatBroadcast(mNetWorkType,phoneId);
    }

    private final void updateDataNetType(int phoneId) {
        int netType = TelephonyManager.NETWORK_TYPE_UNKNOWN;

        int[] subId = SubscriptionManagerProxy.getSubId(phoneId);
        if (subId != null) {
            if (DEBUG) {
                Log.d(TAG, "updateDataNetType phoneId = " + phoneId + ", subId = " + subId[0]);
            }
            netType = TelephonyManagerProxy.getNetworkType(subId[0]);
            if (TelephonyManagerProxy.getCurrentPhoneTypeForSlot(phoneId) == TelephonyManager.PHONE_TYPE_CDMA) {
                int csNetType = TelephonyManagerProxy.getVoiceNetworkType(subId[0]);
//                int psNetType = TelephonyManagerProxy.getNetworkType(subId[0]);
                Log.d(TAG, "updateDataNetType csNetType: " + csNetType + " psNetType: " + netType);
                //1x and Lte can exist at same time, 1x and evdo also can exist at same time, so need to show both net infos
                if (netType == TelephonyManager.NETWORK_TYPE_UNKNOWN) {
                    netType = csNetType;
                } else if (csNetType == TelephonyManager.NETWORK_TYPE_1xRTT
                    && (netType == TelephonyManager.NETWORK_TYPE_LTE || netType == TelephonyManagerProxy.NETWORK_TYPE_LTE_CA)) {
                    netType = NETWORK_CDMA1X_LTE;
                } else {
                    netType = NETWORK_CDMA1X_DO;
                }
            } else {
                if (SubscriptionManagerProxy.isValidSubscriptionId(subId[0])) {
//                    netType = TelephonyManagerProxy.getNetworkType(subId[0]);
                    if (netType == TelephonyManager.NETWORK_TYPE_UNKNOWN) {
                        netType = TelephonyManagerProxy.getVoiceNetworkType(subId[0]);
                    }
                }
                Log.d(TAG, "netType = " + netType + ", typeName = " + TelephonyManagerProxy.getNetworkTypeName(netType));
                if(netType == TelephonyManager.NETWORK_TYPE_UNKNOWN){
                    netType = mDataNetType;
                }
            }
            showNwCap(netType, phoneId);
        }
    }

    // Identify the 3G network type (Mobile and China Unicom)
    private int check3GNetWork(int mSimIndex) {
        String mServerName = "atchannel" + mSimIndex;
        String mATcmd = engconstents.ENG_AT_COPS;
        String mStrTmp = IATUtils.sendATCmd(mATcmd, mServerName);
        Log.d(TAG, "check3GNetWork mStrTmp: " + mStrTmp);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            Log.d(TAG, mStrTmp);
            String[] strs = mStrTmp.split("\n");
            Log.d(TAG, strs[0]);
            return get3GNetWork(strs[0]);
        }
        return NETWORK_WCDMA;
    }

    public int get3GNetWork(String atResult) {
        Resources mResources=getResources();
        
        String[] tdsCdmaPlmnList=mResources.getStringArray(R.array.simPlmnTdsCdma);

        StringBuilder strBuilder = new StringBuilder();
        for (String plmn : tdsCdmaPlmnList) {
            strBuilder.append("([\\D]*");
            strBuilder.append(plmn);
            strBuilder.append("[\\D]*)|");
        }
        strBuilder.deleteCharAt(strBuilder.length() - 1);

        Pattern pattern = Pattern.compile(strBuilder.toString());
        Matcher matcher = pattern.matcher(atResult);
        if (matcher.find()) {
            return NETWORK_TDSCDMA;
        } else {
            strBuilder.setLength(0);
            String[] cdmaPlmnList=mResources.getStringArray(R.array.simPlmnCdma);
            for (String plmn : cdmaPlmnList) {
                strBuilder.append("([\\D]*");
                strBuilder.append(plmn);
                strBuilder.append("[\\D]*)|");
            }
            strBuilder.deleteCharAt(strBuilder.length() - 1);

            pattern = Pattern.compile(strBuilder.toString());
            matcher = pattern.matcher(atResult);
            if (matcher.find()) {
                return NETWORK_CDMADO;
            }
        }

        return NETWORK_WCDMA;
    }

    private void sendNetWorkStatBroadcast(int netWorkType, int phoneId) {
        Log.d(TAG, "sendNetWorkStatBroadcast : " + netWorkType);
        Intent intent = new Intent(NETWORK_STAT_CHANGE);
        intent.putExtra(NETWORK_TYPE, netWorkType);
        intent.putExtra(KEY_SIM_INDEX, phoneId);
        sendBroadcast(intent);
    }

    // PreferenceNameLTE1X 's  top 5 belong to lte, the left belong to 1x
    private boolean isShowLteCap() {
        return mPrefenceIndex < 5;
    }

    // PreferenceNameDO1X 's  top 3 belong to EVDO, the left belong to 1x
    private boolean isShowDoCap() {
        return mPrefenceIndex < 3;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        String key = preference.getKey();
        mPrefenceIndex = Integer.valueOf(key.substring(13, 14));
        int mSubId = slotIdToSubId(mSimIndex);
        Log.d(TAG, "PrefIndex: " + mPrefenceIndex + "  " + KEY_SIM_INDEX + ": "+ mSimIndex + "  SubId: " + mSubId);
        Bundle data = new Bundle();
        data.putInt("PrefenceIndex", mPrefenceIndex);
        data.putInt(KEY_SIM_INDEX, mSimIndex);
        data.putInt("SubId", mSubId);
        Intent mIntent = null;
        switch (mNetWorkType) {
        case NETWORK_GSM:
            mIntent = new Intent("android.engineermode.action.GSMSHOW");
            mIntent.putExtras(data);
            startActivity(mIntent);
            break;
        case NETWORK_TDSCDMA:
            mIntent = new Intent("android.engineermode.action.TDSCDMASHOW");
            mIntent.putExtras(data);
            startActivity(mIntent);
            break;
        case NETWORK_WCDMA:
            mIntent = new Intent("android.engineermode.action.WCDMASHOW");
            mIntent.putExtras(data);
            startActivity(mIntent);
            break;
        case NETWORK_LTE:
            mIntent = new Intent("android.engineermode.action.LTESHOW");
            mIntent.putExtras(data);
            startActivity(mIntent);
            break;
        /* SPRD 1020953: C2K feature @{ */
        case NETWORK_CDMADO:
            mIntent = new Intent("android.engineermode.action.CDMADOSHOW");
            mIntent.putExtras(data);
            startActivity(mIntent);
            break;
        case NETWORK_CDMA1X:
            mIntent = new Intent("android.engineermode.action.CDMA1XSHOW");
            mIntent.putExtras(data);
            startActivity(mIntent);
            break;
        case NETWORK_CDMA1X_LTE:
            if (isShowLteCap()) {
                mIntent = new Intent("android.engineermode.action.LTESHOW");
                mIntent.putExtras(data);
                startActivity(mIntent);
            } else {
                mIntent = new Intent("android.engineermode.action.CDMA1XSHOW");
                // PreferenceNameLTE1X 's  top 5 belong to lte, the left belong to 1x
                data.putInt("PrefenceIndex", mPrefenceIndex - 5);
                mIntent.putExtras(data);
                startActivity(mIntent);
            }
            break;
        case NETWORK_CDMA1X_DO:
            if (isShowDoCap()) {
                mIntent = new Intent("android.engineermode.action.CDMADOSHOW");
                mIntent.putExtras(data);
                startActivity(mIntent);
            } else {
                mIntent = new Intent("android.engineermode.action.CDMA1XSHOW");
                // PreferenceNameDO1X 's  top 3 belong to EVDO, the left belong to 1x
                data.putInt("PrefenceIndex", mPrefenceIndex - 3);
                mIntent.putExtras(data);
                startActivity(mIntent);
            }
            break;
            /* @} */
        /* SPRD 5G feature. @{ */
        case NETWORK_NR:
            mIntent = new Intent("android.engineermode.action.NRSHOW");
            mIntent.putExtras(data);
            startActivity(mIntent);
            break;
        /* @} */
        default:
            break;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
