package com.unisoc.engineermode.core.impl.telephony;


import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.utils.IATUtils;


class NetInfoC2kUeCapImpl implements ITelephonyApi.INetInfoC2kUeCap {

    private static final String TAG = "NetInfoC2kUeCap";

    @Override
    public String getUeCapInfo(int simIdx) {
        String at = engconstents.ENG_CET_VAMOS_CPC + "0,12,0";
        return IATUtils.sendATCmd(at, simIdx);
    }
}
