package com.unisoc.engineermode.core.impl.telephony;


import com.unisoc.engineermode.core.annotation.Implementation;
import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.exception.ErrorCode;
import com.unisoc.engineermode.core.exception.OperationFailedException;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.intf.ITelephonyApi.IControlEvdo;
import com.unisoc.engineermode.core.utils.IATUtils;

@Implementation(
    interfaceClass = IControlEvdo.class,
    properties =  {
    })
public class ControlEvdoImpl implements IControlEvdo {

    private static final int SIM0 = 0;
    private static final int SIM1 = 1;

    @Override
    public String getEvdoStatus() {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_GET_SPC2K_RAT, SIM0);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return mStrTmp;
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void openEvdo() {
        String atRSP = IATUtils.sendATCmd(engconstents.ENG_AT_SET_SPC2K_RAT + ",1", SIM0);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        } else {
            atRSP = IATUtils.sendATCmd(engconstents.ENG_AT_SET_SPC2K_RAT + ",1", SIM1);
        }
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public void closeEvdo() {
        String atRSP = IATUtils.sendATCmd(engconstents.ENG_AT_SET_SPC2K_RAT + ",0", SIM0);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        } else {
            atRSP = IATUtils.sendATCmd(engconstents.ENG_AT_SET_SPC2K_RAT + ",0", SIM1);
        }
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }
}
