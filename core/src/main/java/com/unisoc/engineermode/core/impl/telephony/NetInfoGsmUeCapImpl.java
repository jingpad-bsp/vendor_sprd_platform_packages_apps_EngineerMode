package com.unisoc.engineermode.core.impl.telephony;

import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.utils.IATUtils;

class NetInfoGsmUeCapImpl implements ITelephonyApi.INetInfoGsmUeCap {
    private static final String OPEN = "1";
    private static final String CLOSE = "0";

    @Override
    public boolean getVamos(int simIdx) {
        String at = engconstents.ENG_AT_GET_VAMOS;
        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
    }

    @Override
    public boolean openVamos(int simIdx) {
        String at = engconstents.ENG_AT_SET_VAMOS + OPEN;
        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
    }

    @Override
    public boolean closeVamos(int simIdx) {
        String at = engconstents.ENG_AT_SET_VAMOS + CLOSE;
        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
    }

    @Override
    public boolean getDiversity(int simIdx) {
        String at = engconstents.ENG_AT_GET_DIVERSITY;
        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
    }

    @Override
    public boolean openDiversity(int simIdx) {
        String at = engconstents.ENG_AT_SET_DIVERSITY + "1,0,1,70,1,70";
        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
    }

    @Override
    public boolean closeDiversity(int simIdx) {
        String at = engconstents.ENG_AT_SET_DIVERSITY + "1,0,0,70,0,70";
        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
    }
}
