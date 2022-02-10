package com.sprd.engineermode.utils;
import android.content.Context;
import android.telephony.TelephonyManager;
import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;
import com.unisoc.engineermode.core.impl.nonpublic.TelephonyPropertiesProxy;

import android.util.Log;

public class TeleManagerUtil {

    private static final String TAG = "TeleManagerUtil";
    private TelephonyManager mTelephonyManager;
    private static TeleManagerUtil mRingtoneManagerUtil;
    private static String PROP_TEST_MODE = "persist.radio.ssda.testmode";

    public static final int MODEM_TYPE_GSM = 0;
    public static final int MODEM_TYPE_TDSCDMA = 1;
    public static final int MODEM_TYPE_WCDMA = 2;
    public static final int MODEM_TYPE_LTE = 3;

    private TeleManagerUtil(Context context) {
        mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    public static TeleManagerUtil getInstance(Context context) {
        if (mRingtoneManagerUtil == null) {
            mRingtoneManagerUtil = new TeleManagerUtil(context);
        }
        return mRingtoneManagerUtil;
    }

    public int getPhoneCount() {
        int phoneCount = 1;
        switch (getMultiSimConfiguration()) {
            case UNKNOWN:
                phoneCount = 1;
                break;
            case DSDS:
            case DSDA:
                phoneCount = 2;
                break;
            case TSTS:
                phoneCount = 3;
                break;
        }
        return phoneCount;
    }

    public boolean hasIccCard(int phoneId) {
        //return mTelephonyManager.hasIccCard(phoneId);
        return true;
    }

    public MultiSimVariants getMultiSimConfiguration() {
        String mSimConfig =
            SystemPropertiesProxy.get("persist.radio.multisim.config");
        if (mSimConfig.equals("dsds")) {
            return MultiSimVariants.DSDS;
        } else if (mSimConfig.equals("dsda")) {
            return MultiSimVariants.DSDA;
        } else if (mSimConfig.equals("tsts")) {
            return MultiSimVariants.TSTS;
        } else {
            return MultiSimVariants.UNKNOWN;
        }
    }

    public enum MultiSimVariants {
        DSDS,
        DSDA,
        TSTS,
        UNKNOWN
    };

    public static int getModemType() {
        String baseBand = SystemPropertiesProxy.get(TelephonyPropertiesProxy.PROPERTY_BASEBAND_VERSION, "");
        String modemValue = null;

        if (baseBand != null && !baseBand.equals("")) {
            if ((baseBand.contains("sc8810_modem"))
                    || (baseBand.contains("sc8825_modem"))
                    || (baseBand.contains("sc8830_modem"))
                    || (baseBand.contains("sc8830g_CP0_modem"))
                    || (baseBand.contains("sc8815_sc8815_modem"))) {
                return MODEM_TYPE_TDSCDMA;
            } else if ((baseBand.contains("sc7702_modem"))
                    || (baseBand.contains("sc7710g2_modem"))
                    || (baseBand.contains("sc8830_CP0_modem"))
                    || (baseBand.contains("sc7715_sc7715_modem"))
                    || (baseBand.contains("sc7730g_CP0_modem"))
                    || (baseBand.contains("sc7731g_CP0_modem"))) {
                return MODEM_TYPE_WCDMA;
            } else if (baseBand.contains("sc9620_parmv0")
                    || baseBand.contains("sc9620")
                    || baseBand.contains("sc9630")
                    || baseBand.contains("sc9830")) {
                return MODEM_TYPE_LTE;
            }
        }
        return MODEM_TYPE_GSM;
    }

    public static RadioFeatures getRadioFeatures(int phoneId) {
        Log.d(TAG, "getRadioFeatures: phoneId=" + phoneId);
        final String radioTestMode = phoneId > 0 ? PROP_TEST_MODE + phoneId : PROP_TEST_MODE;
        int testMode = SystemPropertiesProxy.getInt(radioTestMode, -1);
        Log.d(TAG, "getRadioFeatures: testMode=" + testMode);
        if (testMode < 15) {
            for (RadioFeatures feature : RadioFeatures.values()) {
                if (feature.ordinal() == testMode) {
                    return feature;
                }
            }
        }

        return RadioFeatures.NONE;
    }

    public static enum RadioFeatures {
        SVLET, TD_LTE, LTE_FDD, TD_LTE_AND_LTE_FDD, // TD-LTE/LTE-FDD
        LTE_FDD_AND_W_AND_GSM_CSFB, // LTE-FDD/W/GSM-CSFB
        TD_LTE_AND_W_AND_GSM_CSFB, // TD-LTE/W/GSM-CSFB
        TD_LTE_AND_LTE_FDD_AND_W_AND_GSM_CSFB, // TD-LTE/LTE-FDD/W/GSM-CSFB
        TD_LTE_AND_TD_AND_GSM_CSFB, // TD-LTE/TD/GSM-CSFB
        TD_LTE_AND_LTE_FDD_AND_TD_AND_GSM_CSFB, // TD-LTE/LTE-FDD/TD/GSM-CSFB
        TD_LTE_AND_LTE_FDD_AND_W_AND_TD_AND_GSM_CSFB, // TD-LTE/LTE-FDD/W/TD/GSM-CSFB
        GSM_ONLY, // GSM
        WCDMA_ONLY, // WCDMA
        TD_ONLY, // TD
        TD_AND_GSM, // T/G
        WCDMA_AND_GSM, // W/G
        NONE
    };


}
