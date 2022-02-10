package com.unisoc.engineermode.core.impl.telephony;

import com.unisoc.engineermode.core.annotation.Implementation;
import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.exception.ErrorCode;
import com.unisoc.engineermode.core.exception.OperationFailedException;
import com.unisoc.engineermode.core.intf.ITelephonyApi.IVolteDevelopSettings;
import com.unisoc.engineermode.core.utils.IATUtils;

@Implementation(
    interfaceClass = IVolteDevelopSettings.class,
    properties =  {
    })
public class VolteDevelopSettingsImpl implements IVolteDevelopSettings {
    private static final int SIM0 = 0;

    @Override
    public int getDevFlag1() {
        return getDevFlag(1);
    }

    @Override
    public int getDevFlag2() {
        return getDevFlag(2);
    }

    @Override
    public int getDevFlag3() {
        return getDevFlag(3);
    }

    @Override
    public void setDevFlag1(int value) {
        setDevFlag(1, value);
    }

    @Override
    public void setDevFlag2(int value) {
        setDevFlag(2, value);
    }

    @Override
    public void setDevFlag3(int value) {
        setDevFlag(3, value);
    }

    private void setDevFlag(int index, int value) {
        String at = String.format("%s100%d,1,\"%s\"", engconstents.ENG_AT_SPVOLTEENG, index, value);
        String atRsp = IATUtils.sendATCmd(at, SIM0);
        if (!atRsp.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRsp);
        }
    }

    private int getDevFlag(int index) {
        String at = String.format("%s100%d,0", engconstents.ENG_AT_SPVOLTEENG, index);
        String atRsp = IATUtils.sendATCmd(at, SIM0);

        if (atRsp.contains(IATUtils.AT_OK)) {
            return anayResult(atRsp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRsp);
        }
    }

    private int anayResult(String result) {
        try {
            String value = result.split("\\:")[1].split("\n")[0].split(",")[1].trim().replace("\"", "");
            return Integer.parseInt(value);
        } catch (ArrayIndexOutOfBoundsException | NullPointerException | NumberFormatException e) {
            e.printStackTrace();
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, result);
        }
    }
}
