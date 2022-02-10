package com.unisoc.engineermode.core.impl.telephony;

import com.unisoc.engineermode.core.exception.ErrorCode;
import com.unisoc.engineermode.core.exception.OperationFailedException;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.utils.IATUtils;

class SimTraceImpl implements ITelephonyApi.ISimTrace {
    @Override
    public boolean get() throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_GET_SIM_TRACE,
                "atchannel0");
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return mStrTmp.contains("1");
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void open() throws Exception {

        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SET_SIM_TRACE
                + "1", "atchannel0");
        if (!mStrTmp.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void close() throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SET_SIM_TRACE
                + "0", "atchannel0");
        if (!mStrTmp.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }
}
