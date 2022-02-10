package com.unisoc.engineermode.core.impl.telephony;

import com.unisoc.engineermode.core.annotation.Implementation;
import com.unisoc.engineermode.core.exception.ErrorCode;
import com.unisoc.engineermode.core.exception.OperationFailedException;
import com.unisoc.engineermode.core.intf.ITelephonyApi.ILtePrefer;
import com.unisoc.engineermode.core.utils.IATUtils;
import com.unisoc.engineermode.core.common.engconstents;

@Implementation(
        interfaceClass = ILtePrefer.class,
        properties =  {
        })
public class LtePreferImpl implements ILtePrefer {

    private final int SIM0 = 0;

    @Override
    public void openLtePrefer(String val) throws Exception {
        String atCmd = engconstents.ENG_AT_SET_LAS_DUMMY + "\"set power optimization" + "\",1" + val;
        String atResult = IATUtils.sendATCmd(atCmd, SIM0);
        judgeAtResponse(atResult);
    }

    @Override
    public void closeLtePrefer() throws Exception {
        String atCmd = engconstents.ENG_AT_SET_LAS_DUMMY + "\"set power optimization" + "\"" + ",0,0,0,0,0";
        String atResult = IATUtils.sendATCmd(atCmd, SIM0);
        judgeAtResponse(atResult);
    }

    @Override
    public String getLtePrefer() throws Exception {
        String atCmd = engconstents.ENG_AT_SET_LAS_DUMMY + "\"get power optimization params" + "\"";
        String mStrTmp = IATUtils.sendATCmd(atCmd, SIM0);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return mStrTmp;
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    private void judgeAtResponse(String atRsp) throws Exception {
        if (!atRsp.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRsp);
        }
    }
}
