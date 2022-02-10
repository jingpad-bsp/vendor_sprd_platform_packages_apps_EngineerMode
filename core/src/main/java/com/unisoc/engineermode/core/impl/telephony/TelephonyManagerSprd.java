
package com.unisoc.engineermode.core.impl.telephony;

import android.util.Log;
import android.content.Context;
import android.provider.Settings;

import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;
import com.unisoc.engineermode.core.impl.nonpublic.RadioInteractorProxy;
import com.unisoc.engineermode.core.impl.nonpublic.SubscriptionManagerProxy;
import com.unisoc.engineermode.core.impl.nonpublic.TelephonyManagerProxy;

import java.util.Arrays;

public class TelephonyManagerSprd {

    static final String TAG = "TelephonyManagerSprd";
    static final String MODEM_TYPE = "ro.vendor.radio.modemtype";
    private static String PROP_SSDA_MODE = "persist.vendor.radio.modem.config";

    // ssda mode
    private static String MODE_SVLTE = "svlte";
    private static String MODE_TDD_CSFB = "TL_TD_G,G";
    private static String MODE_FDD_CSFB = "TL_LF_W_G,G";
    private static String MODE_CSFB = "TL_LF_TD_W_G,G";
    private static String MODE_LW = "TL_LF_TD_W_G,W_G";
    private static String MODE_FLW = "TL_LF_W_G,W_G";
    private static String MODE_WG = "W_G,G";
    private static String MODE_WG_WG = "W_G,W_G";
    private static String MODE_TD_TD = "TL_LF_W_G,TL_LF_W_G";
    private static String MODE_TDD_TDD = "TL_LF_TD_W_G,TL_LF_TD_W_G";
    private static String MODE_TLTWCG_TLTWCG = "TL_LF_TD_W_C_G,TL_LF_TD_W_C_G";
    private static String MODE_NRTLWG_TLWG = "NR_TL_LF_W_G,TL_LF_W_G";

    //modem type
    public static final int MODEM_TYPE_GSM = 0;
    public static final int MODEM_TYPE_TDSCDMA = 1;
    public static final int MODEM_TYPE_WCDMA = 2;
    public static final int MODEM_TYPE_LTE = 3;
    public static final int MODEM_TYPE_C2K = 4;
    /* SPRD 1168136: 5G compatible @{ */
    public static final int MODEM_TYPE_NR = 5;
    /* @} */

    //private Context mContext;

//    private SubscriptionManager mSubscriptionManager;
//    private TelephonyManager mTelephonyManager;
//    private IRadioInteractorProxy mRadioInteractor = RadioInteractorProxy.getInstance();

    private static class TelephonyManagerSprdHolder {
        static final TelephonyManagerSprd INSTANCE = new TelephonyManagerSprd();
    }

    public static TelephonyManagerSprd getInstance() {
        return TelephonyManagerSprdHolder.INSTANCE;
    }

    private TelephonyManagerSprd() {
        //mContext = context;
//        mSubscriptionManager = SubscriptionManager.from(CoreApi.getInstance().getContext());
//        mTelephonyManager = TelephonyManager.from(CoreApi.getInstance().getContext());
    }

    /**
     * Returns the type of modem
     * for 0:GSM;1:TDSCDMA;2:WCDMA;3:LTE
     */
    public static int getModemType() {
        String modeType = SystemPropertiesProxy.get(MODEM_TYPE, "");
        Log.d(TAG,"getModemType: modemType=" + modeType);
        if ("t".equals(modeType)) {
            return MODEM_TYPE_TDSCDMA;
        } else if ("w".equals(modeType)) {
            return MODEM_TYPE_WCDMA;
        } else if ("tl".equals(modeType) || "lf".equals(modeType) || "l".equals(modeType)) {
            /* SPRD Bug 786489:Only GSM in network mode for engineermode. @{ */
            String ssdaMode = SystemPropertiesProxy.get(PROP_SSDA_MODE);
            Log.d(TAG, "getModemType: ssdaMode=" + ssdaMode);
            if (ssdaMode.equals(MODE_WG_WG) || ssdaMode.equals(MODE_WG)) {
                return MODEM_TYPE_WCDMA;
            } else if (ssdaMode.equals(MODE_TLTWCG_TLTWCG)) {
                return MODEM_TYPE_C2K;
            }
            /* @} */
            return MODEM_TYPE_LTE;
            /* SPRD 1168136: 5G compatible @{ */
        } else if ("nr".equals(modeType)) {
            return MODEM_TYPE_NR;
        } else {
            return MODEM_TYPE_GSM;
        }
    }

    /**
     * Returns the slotId of Primary Card. Returns 0 for the frist slot is Primary Card Returns 1
     * for the second slot is Primary Card
     */
    public int getPrimaryCard() {
        int phoneId = SubscriptionManagerProxy.getDefaultDataPhoneId();
        Log.d(TAG, "getPrimaryCard: " + phoneId);
        return phoneId;
    }

    public static final int NT_UNKNOWN = -1;
    /** {@hide} */
    public static final int NT_TD_LTE = 51; // TD-LTE only
    /** {@hide} */
    public static final int NT_LTE_FDD = 52; // LTE-FDD only
    /** {@hide} */
    public static final int NT_LTE_FDD_TD_LTE = 53; // LTE-FDD/TD-LTE
    /** {@hide} */
    public static final int NT_LTE_FDD_WCDMA_GSM = 54; // LTE-FDD/WCDMA/GSM
    /** {@hide} */
    public static final int NT_TD_LTE_WCDMA_GSM = 55; // TD-LTE/WCDMA/GSM
    /** {@hide} */
    public static final int NT_LTE_FDD_TD_LTE_WCDMA_GSM = 56; // LTE-FDD/TD-LTE/WCDMA/GSM
    /** {@hide} */
    public static final int NT_TD_LTE_TDSCDMA_GSM = 57; // TD-LTE/TD/GSM
    /** {@hide} */
    public static final int NT_LTE_FDD_TD_LTE_TDSCDMA_GSM = 58; // LTE-FDD/TD-LTE/TD/GSM
    /** {@hide} */
    public static final int NT_LTE_FDD_TD_LTE_WCDMA_TDSCDMA_GSM = 59; // LTE-FDD/TD-LTE/WCDMA/TD/GSM
    /** {@hide} */
    public static final int NT_GSM = 60; // GSM only
    /** {@hide} */
    public static final int NT_WCDMA = 61; // WCDMA only
    /** {@hide} */
    public static final int NT_TDSCDMA = 62; // TD only
    /** {@hide} */
    public static final int NT_TDSCDMA_GSM = 63; // TD/GSM
    /** {@hide} */
    public static final int NT_WCDMA_GSM = 64; // WCDMA/GSM
    /* SPRD 958254: C2K feature @{ */
    public static final int NETWORK_MODE_BASE = 50;
    public static final int NETWORK_MODE_CDMA = 4;  /* CDMA and EvDo (auto mode, according to PRL) AVAILABLE Application Settings menu */
    public static final int NETWORK_MODE_TDSCDMA_CDMA_EVDO_GSM_WCDMA  = 21; /*TD-SCDMA,EvDo,CDMA,GSM/WCDMA*/
    public static final int NETWORK_MODE_LTE_TDSCDMA_CDMA_EVDO_GSM_WCDMA = 22; /* TD-SCDMA/LTE/GSM/WCDMA, CDMA, and EvDo */
    public static final int NT_WCDMA_TDSCDMA_EVDO_CDMA_GSM = NETWORK_MODE_BASE + 15;
    public static final int NT_LTE_WCDMA_TDSCDMA_EVDO_CDMA_GSM = NETWORK_MODE_BASE + 17;
    public static final int NT_LTE_FDD_TD_LTE_WCDMA= NETWORK_MODE_BASE + 18;
    public static final int NT_EVDO_CDMA = NETWORK_MODE_BASE + 22;
    public static final int NT_CDMA = NETWORK_MODE_BASE + 23;
    public static final int NT_EVDO = NETWORK_MODE_BASE + 24;
    /* @} */
    /* sprd 1002440  : 5G mode @{ */
    public static final int NT_NR = NETWORK_MODE_BASE + 19;
    public static final int NT_NR_LTE_FDD_TD_LTE = NETWORK_MODE_BASE + 20;
    public static final int NT_NR_LTE_FDD_TD_LTE_GSM_WCDMA =  NETWORK_MODE_BASE + 21;
    /* @} */
    /**
     * Get the preferred network type.
     * Used for device configuration by some CDMA operators.
     * @param slotIdx the id of the subscription to get the preferred network type for
     * @return the preferred network type, defined in RILConstants.java
     */
    public int getPreferredNetworkType(int slotIdx) {
        int networkType = RadioInteractorProxy.getPreferredNetworkType(slotIdx);
        Log.d(TAG, "the NetworkType of the slot[" + slotIdx + "]: " + networkType);
        return networkType;
    }

    /**
     * Get the primary card of network type.
     * @return the network type of the primary card
     */
    public int getPreferredNetworkType() {
        return getPreferredNetworkType(getPrimaryCard());
    }


    /**
     * Set the preferred network type.
     * @param slotIdx the id of the subscription to set the preferred network type for
     * @param networkType the preferred network type.
     * @return true on success; false on any failure.
     */
    /* SPRD Bug 849746: network mode switch. @{ */
    public int setPreferredNetworkType(int slotIdx, int networkType) {
        if (!SubscriptionManagerProxy.isValidPhoneId(slotIdx)) {
            Log.d(TAG, "the slotIdx is not Valid");
            return -1;
        }
        return RadioInteractorProxy.setPreferredNetworkType(slotIdx,networkType);
    }
    /* @} */

    /**
     * Set the network type of the primary card.
     * @return true on success; false on any failure.
     */
    /* SPRD Bug 849746: network mode switch. @{ */
    public int setPreferredNetworkType(int networkType) {
        return setPreferredNetworkType(getPrimaryCard(), networkType);
    }
    /* @} */

    public static enum RadioCapbility {
        NONE, TDD_SVLTE, FDD_CSFB, TDD_CSFB, CSFB, LW, FLW, WG, WW, TD_TD, TDD_TDD, TLTWCG_TLTWCG, NRTLWG_TLWG
    }

    public static RadioCapbility getRadioCapbility() {
        String ssdaMode = SystemPropertiesProxy.get(PROP_SSDA_MODE);
        Log.d(TAG, "getRadioCapbility: ssdaMode=" + ssdaMode);
        if (ssdaMode.equals(MODE_TDD_CSFB)) {
            return RadioCapbility.TDD_CSFB;
        } else if (ssdaMode.equals(MODE_FDD_CSFB)) {
            return RadioCapbility.FDD_CSFB;
        } else if (ssdaMode.equals(MODE_CSFB)) {
            return RadioCapbility.CSFB;
        } else if (ssdaMode.equals(MODE_LW)) {
            return RadioCapbility.CSFB;
        } else if (ssdaMode.equals(MODE_FLW)) {
            return RadioCapbility.FLW;
        } else if (ssdaMode.equals(MODE_WG)) {
            return RadioCapbility.WG;
        } else if (ssdaMode.equals(MODE_WG_WG)) {
            return RadioCapbility.WW;
        } else if (ssdaMode.equals(MODE_TDD_TDD)) {
            return RadioCapbility.TDD_TDD;
        } else if (ssdaMode.equals(MODE_TD_TD)) {
            return RadioCapbility.TD_TD;
        } else if (ssdaMode.equals(MODE_TLTWCG_TLTWCG)) {
            return RadioCapbility.TLTWCG_TLTWCG;
        } else if (ssdaMode.equals(MODE_NRTLWG_TLWG)) {
            return RadioCapbility.NRTLWG_TLWG;
        }
        return RadioCapbility.NONE;
    }

    public static final String SIM_STANDBY = "sim_standby";

    public static boolean isSimStandby(int phoneId, Context context) {
        if (context == null) {
            return true;
        }
        return Settings.Global.getInt(context.getContentResolver(),
                SIM_STANDBY + phoneId, 1) == 1;
         }
}
