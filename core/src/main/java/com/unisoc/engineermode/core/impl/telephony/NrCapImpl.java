package com.unisoc.engineermode.core.impl.telephony;

import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.exception.ErrorCode;
import com.unisoc.engineermode.core.exception.OperationFailedException;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.utils.IATUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class NrCapImpl implements ITelephonyApi.INrCap {

    private static final int SIM0 = 0;
    private static final String TAG = "NrCapImpl";

    private Pattern pattern = Pattern.compile("\\+SPCAPABILITY: ([0-9,]+)\r\nOK\r\n");

    public static class NrCapImplHolder {
        static final ITelephonyApi.INrCap INSTANCE = new NrCapImpl();
    }

    @Override
    public String getState() throws Exception {
        String atResult = IATUtils.sendATCmd(engconstents.ENG_AT_GET_NR_CAP, SIM0);
        if (atResult.contains(IATUtils.AT_OK)) {
            return analyzeResult(atResult);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atResult);
        }
    }

    @Override
    public void setState(String value) throws Exception {
        String atResult = IATUtils.sendATCmd(engconstents.ENG_AT_SET_NR_CAP + valueToParam(value), SIM0);
        if (!atResult.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atResult);
        }
    }

    private String valueToParam(String value) {
        if (value.equals("0")) {
            return "132"; //sa+nsa
        } else if (value.equals("1")) {
            return "516"; //sa
        } else {
            return "260"; //nsa
        }
    }

    private String analyzeResult(String result) throws Exception {
        Matcher matcher = pattern.matcher(result);
        if (!matcher.find() || matcher.group(1) == null) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, result);
        }
        String state = matcher.group(1).split(",")[2];

        if (state.equals("66")) {
            return "0"; //sa+nsa
        } else if (state.equals("258")) {
            return "1"; //sa
        } else {
            return "2"; //nsa
        }
    }
}
