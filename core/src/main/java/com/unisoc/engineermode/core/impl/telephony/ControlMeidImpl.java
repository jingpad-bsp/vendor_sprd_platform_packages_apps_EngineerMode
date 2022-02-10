package com.unisoc.engineermode.core.impl.telephony;


import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.exception.ErrorCode;
import com.unisoc.engineermode.core.exception.OperationFailedException;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.utils.IATUtils;
import com.unisoc.engineermode.core.intf.ITelephonyApi.IControlMeid;
import com.unisoc.engineermode.core.annotation.Implementation;

@Implementation(
    interfaceClass = IControlMeid.class,
    properties =  {
    })
public class ControlMeidImpl implements IControlMeid{
    private static final String mAtChannel = "atchannel1" ;

    @Override
    public String getMeidStatus() throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_GET_MEID_SWITCH,mAtChannel);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return mStrTmp;
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void openMeidSwitch() throws Exception {
        String atRSP = IATUtils.sendATCmd(engconstents.ENG_AT_OPEN_MEID_SWITCH,mAtChannel);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public void closeMeidSwitch() throws Exception {
        String atRSP = IATUtils.sendATCmd(engconstents.ENG_AT_CLOSE_MEID_SWITCH ,mAtChannel);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }
}
