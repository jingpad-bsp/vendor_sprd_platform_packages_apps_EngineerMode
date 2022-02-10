package com.unisoc.engineermode.core.impl.telephony;


import com.unisoc.engineermode.core.annotation.Implementation;
import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.exception.ErrorCode;
import com.unisoc.engineermode.core.exception.OperationFailedException;
import com.unisoc.engineermode.core.intf.ITelephonyApi.IVoltePlmnSettings;
import com.unisoc.engineermode.core.utils.IATUtils;

import java.util.Locale;

@Implementation(
    interfaceClass = IVoltePlmnSettings.class,
    properties =  {
    })
public class VoltePlmnSettingsImpl implements IVoltePlmnSettings {
    private static final int SIM0 = 0;

    @Override
    public String getPlmn1() {
        return getPlmn(1);
    }

    @Override
    public void setPlmn1(String plmn) {
        setPlmn(1, plmn);
    }

    @Override
    public String getPlmn2() {
        return getPlmn(2);
    }

    @Override
    public void setPlmn2(String plmn) {
        setPlmn(2, plmn);
    }

    private String getPlmn(int index) {
        int id;
        if (index == 1) {
            id = 107;
        } else {
            id = 108;
        }
        String at = String.format(Locale.US,"%s%d,0", engconstents.ENG_AT_SPVOLTEENG, id);
        String mStrTmp = IATUtils.sendATCmd(at, SIM0);

        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    private void setPlmn(int index, String plmn) {
        int id;
        if (index == 1) {
            id = 107;
        } else {
            id = 108;
        }

        String at = String.format(Locale.US,"%s%d,1,\"%s\"", engconstents.ENG_AT_SPVOLTEENG, id, plmn);

        String atRsp = IATUtils.sendATCmd(at, SIM0);
        if (!atRsp.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRsp);
        }
    }

    private String anayResult(String result) {
        try {
            return result.split("\\:")[1].split("\n")[0].split(",")[1].trim().replace("\"", "");
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            e.printStackTrace();
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, result);
        }
    }
}
