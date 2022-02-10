package com.unisoc.engineermode.core.impl.telephony;

import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.exception.ErrorCode;
import com.unisoc.engineermode.core.exception.OperationFailedException;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.utils.IATUtils;

class NetWorkModeImpl implements ITelephonyApi.INetWorkMode {
    private static final String TAG = "EM-NetWorkMode";

    public static class NetWorkModeImplHolder {
        public static ITelephonyApi.INetWorkMode INSTANCE = new NetWorkModeImpl();
    }

    @Override
    public void setNetWorkMode(String atCmd, int mSimIndex) throws Exception {
        String atRsp = IATUtils.sendATCmd(engconstents.ENG_AT_SFUN + "5", mSimIndex);
        judgeAtResponse(atRsp);
        atRsp = IATUtils.sendATCmd(atCmd, mSimIndex);
        judgeAtResponse(atRsp);
        atRsp = IATUtils.sendATCmd(engconstents.ENG_AT_SFUN + "4", mSimIndex);
        judgeAtResponse(atRsp);
    }

    @Override
    public String getNetWorkMode(int mSimIndex) {
        return IATUtils.sendATCmd(engconstents.ENG_AT_GET_WCDMA_MODEM, mSimIndex);
    }

    private void judgeAtResponse(String atRSP) throws Exception {
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }
}
