package com.unisoc.engineermode.core.impl.telephony;


import com.unisoc.engineermode.core.annotation.Implementation;
import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.exception.ErrorCode;
import com.unisoc.engineermode.core.exception.OperationFailedException;
import com.unisoc.engineermode.core.utils.IATUtils;

import static com.unisoc.engineermode.core.intf.ITelephonyApi.*;


@Implementation(
    interfaceClass = IWcdmaPrefer.class,
    properties =  {
    })
public class WcdmaPreferImpl implements IWcdmaPrefer {
    private static final int SIM0 = 0;

    @Override
    public String getWcdmaPrefer() {
        String atRsp = IATUtils.sendATCmd(engconstents.ENG_GET_WPREFER_SWITCH, SIM0);
        if (atRsp.contains(IATUtils.AT_OK)) {
            return atRsp;
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRsp);
        }
    }

    @Override
    public void openWcdmaPrefer() {
        String atRsp = IATUtils.sendATCmd(engconstents.ENG_SET_WPREFER_SWITCH + "1", SIM0);
        if (!atRsp.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRsp);
        }
    }

    @Override
    public void closeWcdmaPrefer() {
        String atRsp = IATUtils.sendATCmd(engconstents.ENG_SET_WPREFER_SWITCH + "0", SIM0);
        if (!atRsp.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRsp);
        }
    }
}
