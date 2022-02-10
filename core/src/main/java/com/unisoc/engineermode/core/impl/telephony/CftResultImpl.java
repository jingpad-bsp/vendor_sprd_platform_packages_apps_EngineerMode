package com.unisoc.engineermode.core.impl.telephony;


import com.unisoc.engineermode.core.annotation.Implementation;
import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.exception.ErrorCode;
import com.unisoc.engineermode.core.exception.OperationFailedException;
import com.unisoc.engineermode.core.intf.ITelephonyApi.ICftResult;
import com.unisoc.engineermode.core.utils.IATUtils;


@Implementation(
    interfaceClass = ICftResult.class,
    properties =  {
    })
public class CftResultImpl implements ICftResult {
    private static final int SIM0 = 0;

    @Override
    public String getGsmTdScdmaCFTResult() {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SGMR + "0,0,3,0", SIM0);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return mStrTmp;
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public String getWcdmaCFTResult() {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SGMR + "0,0,3,1", SIM0);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return mStrTmp;
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public String getC2kCFTResult() {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SGMR + "0,0,3,2", SIM0);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return mStrTmp;
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public String getLteCFTResult() {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SGMR + "1,0,3,3,1", SIM0);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return mStrTmp;
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public String getNrCFTResult() throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SGMR + "1,0,3,4", SIM0);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return mStrTmp;
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }
}
