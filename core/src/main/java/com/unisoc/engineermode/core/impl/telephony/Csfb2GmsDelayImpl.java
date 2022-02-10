package com.unisoc.engineermode.core.impl.telephony;

import android.util.Log;

import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.utils.IATUtils;

class Csfb2GmsDelayImpl implements ITelephonyApi.ICsfb2GmsDelay {
    private static final String TAG = "Csfb2GsmDelay";

    @Override
    public boolean getStatus(int simIdx, int[] status) {
        String result = IATUtils.sendATCmd(engconstents.ENG_AT_GET_CSFB2GSM, simIdx);
        Log.d(TAG, "<" + simIdx + ">get Csfb2Gsm_delay Result is " + result);

        String[] str = result.split("\n");
        if (str.length < 1) {
            return false;
        }
        String[] str1 = str[0].split("\\:");
        if (str1.length < 2) {
            return false;
        }
        String[] str2 = str1[1].split(",");

        if (!result.contains(IATUtils.AT_OK)) {
            return false;
        }

        if (str2[0].trim().contains("1")) {
            status[0] = 1;
            status[1] = 0;
        } else if (str2[1].trim().contains("1")) {
            status[0] = 0;
            status[1] = 1;
        } else {
            status[0] = 0;
            status[1] = 0;
        }
        return true;
    }

    @Override
    public boolean openGrrcResident(int simIdx) {
        String at = engconstents.ENG_AT_SET_CSFB2GSM + "1,1";
        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
    }

    @Override
    public boolean closeGrrcResident(int simIdx) {
        String at = engconstents.ENG_AT_SET_CSFB2GSM + "0,1";
        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
    }

    @Override
    public boolean openGrrcRandomAccess(int simIdx) {
        String at = engconstents.ENG_AT_SET_CSFB2GSM + "1,2";
        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
    }

    @Override
    public boolean closeGrrcRandomAccess(int simIdx) {
        String at = engconstents.ENG_AT_SET_CSFB2GSM + "0,2";
        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
    }
}
