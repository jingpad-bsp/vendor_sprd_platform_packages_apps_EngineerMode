package com.unisoc.engineermode.core.impl.telephony;

import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.utils.IATUtils;

class NetInfoImpl implements ITelephonyApi.INetInfo {

    @Override
    public String getPlmn() throws Exception {
        return IATUtils.sendATCmd(
                engconstents.ENG_AT_NETINFO_STATI, "atchannel0");
    }
}
