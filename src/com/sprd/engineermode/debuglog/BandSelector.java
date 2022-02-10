
package com.sprd.engineermode.debuglog;

import android.content.Context;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;

import com.unisoc.engineermode.core.impl.nonpublic.TelephonyManagerProxy;
import com.unisoc.engineermode.core.impl.telephony.TelephonyManagerSprd.RadioCapbility;
import android.util.Log;
import com.unisoc.engineermode.core.impl.telephony.TelephonyManagerSprd;
import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.intf.ITelephonyApi;

/**
 * collect radio capbility and bands in every mode
 */
public class BandSelector {
    private static final String TAG = "BandSelector";

    public static final int RADIO_MODE_MASK_NONE = 0x00;

    private static final int RADIO_MODE_MASK_TD_LTE = 0x02;
    private static final int RADIO_MODE_MASK_LTE_FDD = 0x04;
    private static final int RADIO_MODE_MASK_GSM = 0x08;
    private static final int RADIO_MODE_MASK_TD_SCDMA = 0x10;
    private static final int RADIO_MODE_MASK_WCDMA = 0x20;
    private static final int RADIO_MODE_MASK_NR_TDD = 0x40;
    private static final int RADIO_MODE_MASK_NR_FDD = 0x80;


    public static final int RADIO_MODE_NONE = 0; // 0x00
    public static final int RADIO_MODE_TD_LTE = 2; // 0x02
    public static final int RADIO_MODE_LTE_FDD = 3; // 0x04
    public static final int RADIO_MODE_GSM = 4; // 0x08
    public static final int RADIO_MODE_TD_SCDMA = 5; // 0x10
    public static final int RADIO_MODE_WCDMA = 6; // 0x20
    /* sprd 1002440  : 5G netInfo mode @{ */
    public static final int RADIO_MODE_NR_TDD = 7; // 0x40
    public static final int RADIO_MODE_NR_FDD = 8; // 0x80
    /* @} */
    private int[] mSupportRadioModes;

    private int mPhoneCount;
    private int mPhoneID;
    private int mPrimeryCard;
    private RadioCapbility mRadioCapbility;
    private int mRadioFeature;
    private Context mContext;
    private BandData[] mBandDatas;
    private Handler mUiThread = new Handler();
    private boolean PrimeCardFlag; // true primary card, false not primary card

    private ITelephonyApi teleApi = CoreApi.getTelephonyApi();

    public BandSelector(int phoneID, Context context, Handler threadUi) {
//        final TelephonyManager tm = TelephonyManager.from(context);
        final TelephonyManagerSprd tmSprd = TelephonyManagerSprd.getInstance();
        mContext = context;
        mUiThread = threadUi;
        mPhoneID = phoneID;
        mPhoneCount = TelephonyManagerProxy.getPhoneCount();
        mPrimeryCard = tmSprd.getPrimaryCard();
        mRadioFeature = tmSprd.getPreferredNetworkType(mPhoneID);
        // mPrimeryCard = Settings.Secure.getInt(mContext.getContentResolver(),
        // Settings.Secure.SERVICE_PRIMARY_CARD,-1); // 4.4

        // NONE, TDD_SVLTE, FDD_CSFB, TDD_CSFB,
        if (mPhoneID == mPrimeryCard) {
            PrimeCardFlag = true;
            mRadioCapbility = TelephonyManagerSprd.getRadioCapbility();
        } else {
            PrimeCardFlag = false;
            if (!teleApi.EngTestStatus().isEngTest()) {
                teleApi.EngTestStatus().set(1);
                mRadioFeature = tmSprd.getPreferredNetworkType(Math.abs(mPrimeryCard - 1));
                teleApi.EngTestStatus().set(0);
            } else {
                mRadioFeature = tmSprd.getPreferredNetworkType(Math.abs(mPrimeryCard - 1));
            }
        }

        getSupportRadioModes();
        Log.d(TAG, "Init OK:" + this.toString());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("mPhoneID:").append(mPhoneID);
        sb.append(" mPhoneCount:").append(mPhoneCount);
        sb.append(" mPrimeryCard:").append(mPrimeryCard);
        sb.append(" mRadioCapbility:").append(mRadioCapbility);
        sb.append(" mRadioFeature:").append(mRadioFeature);
        sb.append(" support modes:");
        if (mSupportRadioModes == null) {
            sb.append("null");
        } else {
            for (int n : mSupportRadioModes) {
                sb.append(getModeName(n) + " ");
            }
        }
        return sb.toString();
    }

    public String getSetInfo() {
        String info = "";
        if (mSupportRadioModes == null) {
            info = "error.";
            return info;
        }

        /* SPRD Bug 867203:Change the prompt of BandSelect set. @{ */
        //info = mContext.getString(R.string.band_select_setting_hint) + "\n\n";
        /* @} */
        for (int i = 0; i < mSupportRadioModes.length; i++) {
            info += getModeName(mSupportRadioModes[i]);
            if (mBandDatas[i] != null && mBandDatas[i].isChanged()) {
                if (mBandDatas[i].successful()) {
                    info += ":Success\n";
                } else {
                    info += ":Fail\n";
                }
            } else {
                info += ":Unchange\n";
            }
        }
        return info;
    }

    private String getModeName(int mode) {
        switch (mode) {
            case RADIO_MODE_GSM:
                return "GSM MODE";
            case RADIO_MODE_TD_SCDMA:
                return "TD_SCDMA MODE";
            case RADIO_MODE_WCDMA:
                return "WCDMA MODE";
            case RADIO_MODE_TD_LTE:
                return "LTE_TDD MODE";
            case RADIO_MODE_LTE_FDD:
                return "LTE_FDD MODE";
            case RADIO_MODE_NR_FDD:
                return "NR_FDD MODE";
            case RADIO_MODE_NR_TDD:
                return "NR_TDD MODE";
            default:
                return "NONE";
        }
    }

    private int getCapbility() {
        if (!PrimeCardFlag) {
            Log.d(TAG, "getCapbility mRadioFeature: " + mRadioFeature);
            switch (mRadioFeature) {
                case TelephonyManagerSprd.NT_GSM:
                    return RADIO_MODE_MASK_GSM; // be set to gsm only default if not primary card
                case TelephonyManagerSprd.NT_WCDMA_GSM:
                    return RADIO_MODE_MASK_GSM
                            + RADIO_MODE_MASK_WCDMA;
                case TelephonyManagerSprd.NT_WCDMA:
                    return RADIO_MODE_MASK_WCDMA;
                case TelephonyManagerSprd.NT_LTE_FDD_TD_LTE_WCDMA_GSM:
                    return RADIO_MODE_MASK_GSM
                           + RADIO_MODE_MASK_WCDMA
                           + RADIO_MODE_MASK_TD_LTE
                           + RADIO_MODE_MASK_LTE_FDD;
                case TelephonyManagerSprd.NT_WCDMA_TDSCDMA_EVDO_CDMA_GSM:
                    if (!teleApi.telephonyInfo().isSupportTdscdma()) {
                        return RADIO_MODE_MASK_GSM
                           + RADIO_MODE_MASK_WCDMA;
                    } else {
                        return RADIO_MODE_MASK_GSM
                           + RADIO_MODE_MASK_TD_SCDMA
                           + RADIO_MODE_MASK_WCDMA;
                    }
                case TelephonyManagerSprd.NT_LTE_FDD_TD_LTE_WCDMA_TDSCDMA_GSM:
                    return RADIO_MODE_MASK_GSM
                           + RADIO_MODE_MASK_WCDMA
                           + RADIO_MODE_MASK_TD_SCDMA
                           + RADIO_MODE_MASK_TD_LTE
                           + RADIO_MODE_MASK_LTE_FDD;
                case TelephonyManagerSprd.NT_LTE_WCDMA_TDSCDMA_EVDO_CDMA_GSM:
                    return RADIO_MODE_MASK_GSM
                           + RADIO_MODE_MASK_WCDMA
                           + RADIO_MODE_MASK_TD_SCDMA
                           + RADIO_MODE_MASK_TD_LTE
                           + RADIO_MODE_MASK_LTE_FDD;
                case TelephonyManagerSprd.NT_LTE_FDD_TD_LTE_WCDMA:
                    return RADIO_MODE_MASK_WCDMA
                           + RADIO_MODE_MASK_TD_SCDMA
                           + RADIO_MODE_MASK_TD_LTE
                           + RADIO_MODE_MASK_LTE_FDD;
                /* sprd 1002440  : 5G netInfo mode @{ */
                case TelephonyManagerSprd.NT_NR_LTE_FDD_TD_LTE_GSM_WCDMA:
                    return RADIO_MODE_MASK_GSM
                        + RADIO_MODE_MASK_WCDMA
                        + RADIO_MODE_MASK_TD_LTE
                        + RADIO_MODE_MASK_LTE_FDD
                        + RADIO_MODE_MASK_NR_TDD
                        + RADIO_MODE_MASK_NR_FDD;
                        /* @} */
            }
        }
        Log.d(TAG, "getCapbility mRadioCapbility: " + mRadioCapbility);
        if (mRadioCapbility == RadioCapbility.NONE) { // NONE default value is set to gsm +
                                                          // WCDMA
            return RADIO_MODE_MASK_GSM
                    + RADIO_MODE_MASK_WCDMA;
        } else if (mRadioCapbility == RadioCapbility.TDD_CSFB) {
            return RADIO_MODE_MASK_GSM
                    + RADIO_MODE_MASK_TD_SCDMA
                    + RADIO_MODE_MASK_TD_LTE;
        } else if (mRadioCapbility == RadioCapbility.FDD_CSFB) {
            return RADIO_MODE_MASK_GSM
                    + RADIO_MODE_MASK_WCDMA
                    + RADIO_MODE_MASK_TD_LTE
                    + RADIO_MODE_MASK_LTE_FDD;
        } else if (mRadioCapbility == RadioCapbility.CSFB) { // 4.4 not support
            return RADIO_MODE_MASK_GSM
                    + RADIO_MODE_MASK_WCDMA
                    + RADIO_MODE_MASK_TD_SCDMA
                    + RADIO_MODE_MASK_TD_LTE
                    + RADIO_MODE_MASK_LTE_FDD;
        } else if (mRadioCapbility == RadioCapbility.TDD_TDD) {
            return RADIO_MODE_MASK_GSM
                    + RADIO_MODE_MASK_WCDMA
                    + RADIO_MODE_MASK_TD_SCDMA
                    + RADIO_MODE_MASK_TD_LTE
                    + RADIO_MODE_MASK_LTE_FDD;
        } else if (mRadioCapbility == RadioCapbility.TD_TD) {
            return RADIO_MODE_MASK_GSM
                    + RADIO_MODE_MASK_WCDMA
                    + RADIO_MODE_MASK_TD_LTE
                    + RADIO_MODE_MASK_LTE_FDD;
        } else if (mRadioCapbility == RadioCapbility.WG
            || mRadioCapbility == RadioCapbility.WW) {
            return RADIO_MODE_MASK_GSM
                    + RADIO_MODE_MASK_WCDMA;
        } else if (mRadioCapbility == RadioCapbility.FLW) {
            return RADIO_MODE_MASK_GSM
                    + RADIO_MODE_MASK_WCDMA
                    + RADIO_MODE_MASK_TD_LTE
                    + RADIO_MODE_MASK_LTE_FDD;
        } else if (mRadioCapbility == RadioCapbility.TLTWCG_TLTWCG) {
            return RADIO_MODE_MASK_GSM
                    + RADIO_MODE_MASK_WCDMA
                    + RADIO_MODE_MASK_TD_LTE
                    + RADIO_MODE_MASK_LTE_FDD;
        }/* UNISOC: add for bug1002440, 5G netInfo mode @{ */
        //note: Temporary similar to TLTWCG_TLTWCG until tester tell what is 5G' type
        else if (mRadioCapbility == RadioCapbility.NRTLWG_TLWG) {
            return RADIO_MODE_MASK_GSM
                    + RADIO_MODE_MASK_WCDMA
                    + RADIO_MODE_MASK_TD_LTE
                    + RADIO_MODE_MASK_LTE_FDD
                    + RADIO_MODE_MASK_NR_TDD
                    + RADIO_MODE_MASK_NR_FDD;
        }
        /* @} */
        return RADIO_MODE_MASK_NONE;
    }

    private int getRadioFeature() {
        Log.d(TAG, "mRadioFeature: " + mRadioFeature);
         if (mRadioFeature == TelephonyManagerSprd.NT_TD_LTE) {
            return RADIO_MODE_MASK_TD_LTE;
        } else if (mRadioFeature == TelephonyManagerSprd.NT_LTE_FDD) {
            return RADIO_MODE_MASK_LTE_FDD;
        } else if (mRadioFeature == TelephonyManagerSprd.NT_LTE_FDD_TD_LTE) {
            return RADIO_MODE_MASK_TD_LTE
                    + RADIO_MODE_MASK_LTE_FDD;
        } else if (mRadioFeature == TelephonyManagerSprd.NT_LTE_FDD_WCDMA_GSM) {
            return RADIO_MODE_MASK_GSM
                    + RADIO_MODE_MASK_WCDMA
                    + RADIO_MODE_MASK_LTE_FDD;
        } else if (mRadioFeature == TelephonyManagerSprd.NT_TD_LTE_WCDMA_GSM) {
            return RADIO_MODE_MASK_GSM
                    + RADIO_MODE_MASK_WCDMA
                    + RADIO_MODE_MASK_TD_LTE;
        } else if (mRadioFeature == TelephonyManagerSprd.NT_LTE_FDD_TD_LTE_WCDMA_GSM) {
            return RADIO_MODE_MASK_GSM
                    + RADIO_MODE_MASK_WCDMA
                    + RADIO_MODE_MASK_TD_LTE
                    + RADIO_MODE_MASK_LTE_FDD;
        } else if (mRadioFeature == TelephonyManagerSprd.NT_TD_LTE_TDSCDMA_GSM) {
            return RADIO_MODE_MASK_GSM
                    + RADIO_MODE_MASK_TD_SCDMA
                    + RADIO_MODE_MASK_TD_LTE;
        } else if (mRadioFeature == TelephonyManagerSprd.NT_LTE_FDD_TD_LTE_TDSCDMA_GSM) {
            return RADIO_MODE_MASK_GSM
                    + RADIO_MODE_MASK_TD_SCDMA
                    + RADIO_MODE_MASK_TD_LTE
                    + RADIO_MODE_MASK_LTE_FDD;
        } else if (mRadioFeature == TelephonyManagerSprd.NT_LTE_FDD_TD_LTE_WCDMA_TDSCDMA_GSM) {
            return RADIO_MODE_MASK_GSM
                    + RADIO_MODE_MASK_WCDMA
                    + RADIO_MODE_MASK_TD_SCDMA
                    + RADIO_MODE_MASK_TD_LTE
                    + RADIO_MODE_MASK_LTE_FDD;
        } else if (mRadioFeature == TelephonyManagerSprd.NT_GSM) {
            return RADIO_MODE_MASK_GSM;
            /* BEGIN BUG559785 2016/05/05 sylar.wang bandselect show nothing in gsm */
        } else if (mRadioFeature == TelephonyManagerSprd.NT_WCDMA) {
            return RADIO_MODE_MASK_WCDMA;
        } else if (mRadioFeature == TelephonyManagerSprd.NT_TDSCDMA) {
            return RADIO_MODE_MASK_TD_SCDMA;
        } else if (mRadioFeature == TelephonyManagerSprd.NT_TDSCDMA_GSM) {
            return RADIO_MODE_MASK_GSM
                    + RADIO_MODE_MASK_TD_SCDMA;
        } else if (mRadioFeature == TelephonyManagerSprd.NT_WCDMA_GSM) {
            return RADIO_MODE_MASK_GSM
                    + RADIO_MODE_MASK_WCDMA;
        } else if (mRadioFeature == TelephonyManagerSprd.NT_UNKNOWN) {
            return RADIO_MODE_MASK_GSM;
        }
        return RADIO_MODE_MASK_NONE;
    }

    private int[] getSupportRadioModes() {
        /*
         * NONE, TDD_SVLTE, FDD_CSFB, TDD_CSFB, CSFB 1、"persist.radio.ssda.mode"
         * （TelephonyManager：static RadioCapbility getRadioCapbility()） “tdd-csfb” means 3mod
         * （RadioCapbility.TDD_CSFB） “fdd-csfb”means 4mod（RadioCapbility.FDD_CSFB） “csfb”means 5mod
         * （RadioCapbility.CSFB） 3mod：GSM、TD-SCDMA、TDD-LTE 4mod：GSM、 WCDMA、TDD-LTE、FDD-LTE
         * 5mod：GSM、TD- SCDMA、WCDMA、TDD-LTE、FDD-LTE
         */
        int modes1 = getCapbility();
        /* SPRD: Bug 595102 Engineering band select mode interface is empty @{ */
        int modes = modes1;
        /* @} */
        int n = 0;
        for (int i = 0; i < 8; i++) {
            if (((modes >> i) & 0x01) == 1) {
                n++;
            }
        }
        Log.d(TAG, "getSupportRadioModes modes1:" + modes1 + " modes:"
                + modes + " n:" + n);
        mSupportRadioModes = new int[n];
        for (int i = 0, j = 0; i < 8; i++) {
            switch (modes & (1 << i)) {
                case RADIO_MODE_MASK_TD_LTE:
                    mSupportRadioModes[j] = RADIO_MODE_TD_LTE;
                    break;
                case RADIO_MODE_MASK_LTE_FDD:
                    mSupportRadioModes[j] = RADIO_MODE_LTE_FDD;
                    break;
                case RADIO_MODE_MASK_GSM:
                    mSupportRadioModes[j] = RADIO_MODE_GSM;
                    break;
                case RADIO_MODE_MASK_TD_SCDMA:
                    mSupportRadioModes[j] = RADIO_MODE_TD_SCDMA;
                    break;
                case RADIO_MODE_MASK_WCDMA:
                    mSupportRadioModes[j] = RADIO_MODE_WCDMA;
                    break;
                case RADIO_MODE_MASK_NR_TDD:
                    mSupportRadioModes[j] = RADIO_MODE_NR_TDD;
                    break;
                case RADIO_MODE_MASK_NR_FDD:
                    mSupportRadioModes[j] = RADIO_MODE_NR_FDD;
                    break;
                default:
                    continue;
            }
            j++;
        }
        return mSupportRadioModes;
    }

    public void initModes(PreferenceGroup preferenceGroup) {
        if (mSupportRadioModes == null) {
            Log.d(TAG, "loadBands() mRadioModes is null");
            return;
        }
        if (mBandDatas != null) {
            return;
        }
        Log.d(TAG, "initModes mSupportRadioModes.length: " + mSupportRadioModes.length);
        mBandDatas = new BandData[mSupportRadioModes.length];
        for (int i = 0; i < mSupportRadioModes.length; i++) {
            BandData data = null;
            final PreferenceCategory pref = new PreferenceCategory(mContext);
            pref.setTitle(getModeName(mSupportRadioModes[i]));
            preferenceGroup.addPreference(pref);
            switch (mSupportRadioModes[i]) {
                case RADIO_MODE_GSM:
                    data = new GSMBandData(mContext, mPhoneID, pref);
                    break;
                case RADIO_MODE_TD_SCDMA:
                    data = new TDBandData(mContext, mPhoneID, pref);
                    break;
                case RADIO_MODE_WCDMA:
                    data = new WCDMABandData(mContext, mPhoneID, pref);
                    break;
                case RADIO_MODE_TD_LTE:
                    data = new TDDBandData(mContext, mPhoneID, pref);
                    break;
                case RADIO_MODE_LTE_FDD:
                    data = new FDDBandData(mContext, mPhoneID, pref);
                    break;
                case RADIO_MODE_NR_TDD:
                    Log.d(TAG, "RADIO_MODE_NR_TDD");
                    data = new NrTDDBandData(mContext, mPhoneID, pref);
                    break;
                case RADIO_MODE_NR_FDD:
                    Log.d(TAG, "RADIO_MODE_NR_FDD");
                    data = new NrFDDBandData(mContext, mPhoneID, pref);
                    break;
                default:
                    break;
            }

            if (data != null && data.init()) {
                final BandData d = data;
                CheckBoxPreference[] cbPrefs = d.addAvailableBandsToPerference();
                for (CheckBoxPreference cb : cbPrefs) {
                    pref.addPreference(cb);
                }
                mBandDatas[i] = data;
            }
        }
    }

    public void loadBands() {
        if (mUiThread == null) {
            return;
        }
        mUiThread.post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < mBandDatas.length; i++) {
                    if (mBandDatas[i] != null) {
                        mBandDatas[i].init();
                    }
                }
            }
        });
    }

    public void saveBand() {
        for (BandData bands : mBandDatas) {
            if (bands != null && bands.isChanged()) {
                bands.setSelectedBandToModem();
            }
        }
    }

    public boolean isCheckOneOrMore() {
        for (int i = 0; i < mBandDatas.length; i++) {
            if (mBandDatas[i] != null && !mBandDatas[i].isCheckOneOrMore()) {
                return false;
            }
        }
        return true;
    }
}
