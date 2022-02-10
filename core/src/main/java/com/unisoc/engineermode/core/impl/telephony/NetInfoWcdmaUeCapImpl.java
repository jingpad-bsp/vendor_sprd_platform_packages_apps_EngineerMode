package com.unisoc.engineermode.core.impl.telephony;

import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.utils.IATUtils;

public class NetInfoWcdmaUeCapImpl implements ITelephonyApi.INetInfoWcdmaUeCap {
    private static final String TAG = "NetInfoWcdmaUeNwCap";

    private static final String OPEN = "1";
    private static final String CLOSE = "0";


    @Override
    public UeCap getUeCap(int simIdx) {
        return NetInfoWcdma.getUeCap(simIdx);
    }


//    @Override
//    public boolean openCpc(int simIdx) {
//        String at = engconstents.ENG_AT_SET_CPC + OPEN;
//        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
//    }
//
//    @Override
//    public boolean closeCpc(int simIdx) {
//        String at = engconstents.ENG_AT_SET_CPC + CLOSE;
//        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
//    }

    @Override
    public boolean openUl16Qam(int simIdx) {
        String at = engconstents.ENG_AT_SET_16QAM + OPEN;
        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
    }

    @Override
    public boolean closeUl16Qam(int simIdx) {
        String at = engconstents.ENG_AT_SET_16QAM + CLOSE;
        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
    }

    @Override
    public boolean openDbHsdpa(int simIdx) {
        String at = engconstents.ENG_AT_SET_DBHSDPA + OPEN;
        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
    }

    @Override
    public boolean closeDbHsdpa(int simIdx) {
        String at = engconstents.ENG_AT_SET_DBHSDPA + CLOSE;
        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
    }

//    @Override
//    public boolean openDcHsdpa(int simIdx) {
//        String at = engconstents.ENG_AT_SET_DCHSDPA + OPEN;
//        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
//    }
//
//    @Override
//    public boolean closeDcHsdpa(int simIdx) {
//        String at = engconstents.ENG_AT_SET_DCHSDPA + CLOSE;
//        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
//    }
//
//    @Override
//    public boolean openEFach(int simIdx) {
//        String at = engconstents.ENG_AT_SET_EFACH + OPEN;
//        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
//    }
//
//    @Override
//    public boolean closeEFach(int simIdx) {
//        String at = engconstents.ENG_AT_SET_EFACH + CLOSE;
//        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
//    }
//
//    @Override
//    public boolean openERach(int simIdx) {
//        String at = engconstents.ENG_AT_SET_ERACH + OPEN;
//        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
//    }
//
//    @Override
//    public boolean closeERach(int simIdx) {
//        String at = engconstents.ENG_AT_SET_ERACH + CLOSE;
//        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
//    }

    @Override
    public boolean openSnow3G(int simIdx) {
        String at = engconstents.ENG_AT_SET_SNOW3G + OPEN;
        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
    }

    @Override
    public boolean closeSnow3G(int simIdx) {
        String at = engconstents.ENG_AT_SET_SNOW3G + CLOSE;
        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
    }

    @Override
    public boolean openWDiversity(int simIdx) {
        String at = engconstents.ENG_AT_SET_DIVERSITY + OPEN;
        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
    }

    @Override
    public boolean closeWDiversity(int simIdx) {
        String at = engconstents.ENG_AT_SET_DIVERSITY + CLOSE;
        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
    }

//    @Override
//    public boolean openType3i(int simIdx) {
//        String at = engconstents.ENG_AT_SET_TYPE3I + OPEN;
//        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
//    }
//
//    @Override
//    public boolean closeType3i(int simIdx) {
//        String at = engconstents.ENG_AT_SET_TYPE3I + CLOSE;
//        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
//    }

    @Override
    public boolean openUlHsdpa(int simIdx) {
        String at = engconstents.ENG_AT_SET_UL_HSDPA + OPEN;
        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
    }

    @Override
    public boolean closeUlHsdpa(int simIdx) {
        String at = engconstents.ENG_AT_SET_UL_HSDPA + CLOSE;
        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
    }

//    @Override
//    public boolean openUlHsupa(int simIdx) {
//        String at = engconstents.ENG_AT_SET_UL_HSUPA + OPEN;
//        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
//    }
//
//    @Override
//    public boolean closeUlHsupa(int simIdx) {
//        String at = engconstents.ENG_AT_SET_UL_HSUPA + CLOSE;
//        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
//    }
}
