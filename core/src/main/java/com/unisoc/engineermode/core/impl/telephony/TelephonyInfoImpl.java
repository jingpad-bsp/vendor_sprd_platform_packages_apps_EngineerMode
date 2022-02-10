package com.unisoc.engineermode.core.impl.telephony;

import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.utils.IATUtils;


class TelephonyInfoImpl implements ITelephonyApi.ITelephonyInfo {

    private static String PROP_SSDA_MODE = "persist.vendor.radio.modem.config";

    private static String CDMA_FLAG = "C";
    private static String WCDMA_FLAG = "W";
    private static String TDSCDMA_FLAG = "TD";
    private static String TDLTE_FLAG = "TL";
    private static String FDDLTE_FLAG = "LF";
    private static String GSM_FLAG = "G";
    private static String NR_FLAG = "NR";

    @Override
    public boolean isSupportWcdma() {
        try {
            String ssdaMode = SystemPropertiesProxy.get(PROP_SSDA_MODE);
            boolean support = ssdaMode != null && ssdaMode.contains(WCDMA_FLAG);
            return support;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isSupportLte() {
        try {
            String ssdaMode = SystemPropertiesProxy.get(PROP_SSDA_MODE);
            boolean support = ssdaMode != null && (ssdaMode.contains(TDLTE_FLAG) || ssdaMode.contains(FDDLTE_FLAG));
            return support;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isSupportC2k() {
        try {
            String ssdaMode = SystemPropertiesProxy.get(PROP_SSDA_MODE);
            boolean support = ssdaMode != null && ssdaMode.contains(CDMA_FLAG);
            return support;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    @Override
    public boolean isSupportNr() {
        try {
            String ssdaMode = SystemPropertiesProxy.get(PROP_SSDA_MODE);
            boolean support = ssdaMode != null && (ssdaMode.contains(NR_FLAG));
            return support;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isSupportCsfb() {
        return TelephonyManagerSprd.RadioCapbility.TDD_CSFB
                .equals(TelephonyManagerSprd.getRadioCapbility())
                || TelephonyManagerSprd.RadioCapbility.FDD_CSFB
                .equals(TelephonyManagerSprd.getRadioCapbility())
                || TelephonyManagerSprd.RadioCapbility.CSFB
                .equals(TelephonyManagerSprd.getRadioCapbility())
                || TelephonyManagerSprd.RadioCapbility.LW
                .equals(TelephonyManagerSprd.getRadioCapbility());
    }

    @Override
    public boolean isSupportLplusG() {
        return TelephonyManagerSprd.RadioCapbility.FDD_CSFB.equals(TelephonyManagerSprd.getRadioCapbility())
                || TelephonyManagerSprd.RadioCapbility.CSFB.equals(TelephonyManagerSprd.getRadioCapbility());
    }

    @Override
    public boolean isSupportLplusW() {
        return TelephonyManagerSprd.RadioCapbility.CSFB.equals(TelephonyManagerSprd.getRadioCapbility())
                || TelephonyManagerSprd.RadioCapbility.FLW.equals(TelephonyManagerSprd.getRadioCapbility());
    }

    @Override
    public boolean isSupportLplusL() {
        return TelephonyManagerSprd.RadioCapbility.TDD_TDD.equals(TelephonyManagerSprd.getRadioCapbility())
                || TelephonyManagerSprd.RadioCapbility.TD_TD.equals(TelephonyManagerSprd.getRadioCapbility());
    }

    @Override
    public boolean isSupportWplusG() {
        return TelephonyManagerSprd.RadioCapbility.WG.equals(TelephonyManagerSprd.getRadioCapbility());
    }

    @Override
    public boolean isSupportWplusW() {
        return TelephonyManagerSprd.RadioCapbility.WW.equals(TelephonyManagerSprd.getRadioCapbility());
    }

    @Override
    public boolean isSupportNetwork() {
        return SystemPropertiesProxy.get("persist.radio.network.unable").equals("true");
    }   

    @Override
    public boolean isSupportTdscdma() {
        try {
            return SystemPropertiesProxy.get(PROP_SSDA_MODE).contains(TDSCDMA_FLAG);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isSupportGsm() {
        try {
            return SystemPropertiesProxy.get(PROP_SSDA_MODE).contains(GSM_FLAG);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isSupportAPCFunc() {
        try {
            String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_GET_SPAPC, "atchannel0");
            return (mStrTmp != null && !mStrTmp.trim().equalsIgnoreCase("+CME ERROR: 4"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
