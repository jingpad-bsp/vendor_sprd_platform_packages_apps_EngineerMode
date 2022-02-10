package com.unisoc.engineermode.core.impl.telephony;


import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.exception.ErrorCode;
import com.unisoc.engineermode.core.exception.OperationFailedException;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.utils.IATUtils;

class C2kConfigImpl implements ITelephonyApi.IC2kConfig {

    @Override
    public String getC2kConfig() throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_GET_SPCAS_PARAM, 0);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return mStrTmp;
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setC2kConfig(String value) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SET_SPCAS_PARAM + value, 0);
        if (!mStrTmp.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }
}
