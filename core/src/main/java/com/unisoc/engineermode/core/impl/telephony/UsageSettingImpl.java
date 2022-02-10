package com.unisoc.engineermode.core.impl.telephony;

import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.exception.ErrorCode;
import com.unisoc.engineermode.core.exception.OperationFailedException;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.utils.IATUtils;

class UsageSettingImpl implements ITelephonyApi.IUsageSetting {

    private final int SIM0 = 0;

    public static class UsageSettingImplHolder {
        static final ITelephonyApi.IUsageSetting INSTANCE = new UsageSettingImpl();
    }

    @Override
    public String get() throws Exception {
        String atRSP = IATUtils.sendATCmd(engconstents.ENG_AT_GET_EUS, SIM0);
        if (atRSP.contains(IATUtils.AT_OK)) {
            return analyseResult(atRSP);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public void set(String val) {
        if (val.equals("0")) {
            IATUtils.sendATCmd(engconstents.ENG_AT_SET_EMODE + "1", SIM0);
            IATUtils.sendATCmd(engconstents.ENG_AT_SET_EUS + "0", SIM0);
        } else {
            IATUtils.sendATCmd(engconstents.ENG_AT_SET_EMODE + "2", SIM0);
            IATUtils.sendATCmd(engconstents.ENG_AT_SET_EUS + "1", SIM0);
        }
    }

    private String analyseResult(String atResponse) {
        String str[] = atResponse.split("\n");
        return str[0].trim().contains("0") ? "0" : "1";
    }
}
