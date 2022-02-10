package com.unisoc.engineermode.core.impl.telephony;

import android.text.TextUtils;
import android.util.Log;

import com.unisoc.engineermode.core.exception.ErrorCode;
import com.unisoc.engineermode.core.exception.OperationFailedException;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.utils.IATUtils;

class GprsSettingImpl implements ITelephonyApi.IGprsSetting {
    private static final String TAG = "GPRSOPER";

    @Override
    public void attach(int simIdx) throws Exception {

        String atCmd = engconstents.ENG_AT_GPRS + "1";
        String values = IATUtils.sendATCmd(atCmd, simIdx);
        Log.d(TAG, "<0> ATCMD is " + atCmd + " GPRS_ATTACHED Result is " + values);
        if (!values.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, values);
        }
    }

    @Override
    public void detach(int simIdx) throws Exception {
        String atCmd = engconstents.ENG_AT_GPRS + "0";
        String values = IATUtils.sendATCmd(atCmd, simIdx);
        Log.d(TAG, "<0> ATCMD is " + atCmd + " GPRS_DETACHED Result is " + values);
        if (!values.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, values);
        }
    }

    @Override
    public void setAlwaysAttach(int simIdx) throws Exception {
        String atCmd = engconstents.ENG_AT_SETAUTOATT + "1";
        String values = IATUtils.sendATCmd(atCmd, simIdx);
        Log.d(TAG, "<0> ATCMD is " + atCmd + " GPRS_ALWAYS_ATTACH Result is " + values);
        if (!values.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, values);
        }
    }

    @Override
    public void setNeededAttach(int simIdx) throws Exception {
        String atCmd = engconstents.ENG_AT_SETAUTOATT + "0";
        String values = IATUtils.sendATCmd(atCmd, simIdx);
        Log.d(TAG, "<0> ATCMD is " + atCmd + " GPRS_WHEN_NEEDED_ATTACH Result is " + values);
        if (!values.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, values);
        }
    }

    @Override
    public boolean[] getPdpContextState(int simIdx) throws Exception {
        String result = IATUtils.sendATCmd(engconstents.ENG_AT_GETPDPACTIVE1, simIdx);
        if (!result.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, result);
        }

        final int pdpCtxCount = (TelephonyManagerSprd.getModemType() == TelephonyManagerSprd.MODEM_TYPE_WCDMA) ? 3 : 6;
        boolean[] phaseResult = new boolean[pdpCtxCount];

        String[] line = result.split("\n");
        for (int i = 0; i < phaseResult.length; i++) {
            Log.d(TAG, "line" + i + "=" + line[i]);
            String[] pdpContextInfo = line[i].split(",");

            if (pdpContextInfo[1] != null
                    && pdpContextInfo[1].contains("1")) {
                phaseResult[i] = true;
            } else {
                phaseResult[i] = false;
            }
        }

        return phaseResult;
    }

    @Override
    public boolean activateFirstPdp(int simIdx, int trafficClass, int pdpType) {
        String values, tvalues, apnValues;
        String atCmd;
        // active one PDP,need to make sure no same cid PDP
        String apn = getApn(simIdx, pdpType);
        Log.d(TAG, "apn = " + apn);
        if (apn.equals("")) {
            return false;
        }
        atCmd = "AT+CGDCONT=" + pdpType + "," + "\"IPV4V6\"," + "\"" + apn + "\"" + ",\"\",0,0";
        apnValues = IATUtils.sendATCmd(atCmd, simIdx);
        Log.d(TAG, "<0> ATCMD is " + atCmd
                + " AT+CGDCONT Result is " + apnValues);

        atCmd = "AT+CGEQREQ=" + pdpType + "," + trafficClass + ",0,0,0,0,2,0,\"1e4\",\"0e0\",3,0,0";
        tvalues = IATUtils.sendATCmd(atCmd, simIdx);
        Log.d(TAG, "<0> ATCMD is " + atCmd
                + " AT+CGEQREQ Result is " + tvalues);

        // atCmd = engconstents.ENG_AT_SETPDPACTIVE1 + "1," +
        // mPDPType;
        atCmd = "AT+CGDATA=" + "\"M-ETHER\"" + "," + pdpType;
        values = IATUtils.sendATCmd(atCmd, "atchannel" + simIdx);
        Log.d(TAG, "<0> ATCMD is " + atCmd
                + " GPRS_ACTIVATE_PDP Result is " + values);
        if (values.contains(IATUtils.AT_CONNECT) && tvalues.contains(IATUtils.AT_OK)
                && apnValues.contains(IATUtils.AT_OK)) {
            return true;
        } else {
            return false;
        }

    }

    public boolean activateSecondPdp(int simIdx, int pdpType) {
        String resp;
        resp = IATUtils.sendATCmd(engconstents.ENG_AT_SETPDPACTIVE1 + "1," + pdpType, simIdx);
        if (resp.contains(IATUtils.AT_OK)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean deactivatePdp(int simIdx, int pdpType) {
        String atCmd = engconstents.ENG_AT_SETPDPACTIVE1 + "0," + pdpType;
        String values = IATUtils.sendATCmd(atCmd, simIdx);
        Log.d(TAG, "<0> ATCMD is " + atCmd + " GPRS_DEACTIVATE_PDP Result is " + values);
        return values.contains(IATUtils.AT_OK);
    }

    @Override
    public boolean sendData(int simIdx, int pdpType, String dataLength, String data) {
        String atCmd;
        if (data.equals("")) {
            atCmd = engconstents.ENG_AT_SGPRSDATA1 + dataLength + "," + pdpType;
            // + ",\"GprsTestData\"";
        } else {
            atCmd = engconstents.ENG_AT_SGPRSDATA1 + dataLength + "," + pdpType
                    + ","+"\""+data+"\"";
        }
        /* @} */
        String values = IATUtils.sendATCmd(atCmd, simIdx);
        Log.d(TAG, "<0> ATCMD is " + atCmd + " GPRS_SEND_DATA Result is "
                + values);

        return values.contains(IATUtils.AT_OK);
    }

    @Override
    public boolean getAutoAttachState(int simIdx) throws Exception {

        String atCmd = engconstents.ENG_AT_GETAUTOATT;
        String values = IATUtils.sendATCmd(atCmd, simIdx);
        Log.d(TAG, "<0> ATCMD is " + atCmd + " GET_AUTOATT_STATUS Result is " + values);
        if (values.contains(IATUtils.AT_OK)) {
            if (values.contains("1")) {
               return true;
            } else if (values.contains("0")) {
               return false;
            } else {
                throw new OperationFailedException(ErrorCode.AT_RETURN_PARSE_ERROR, values);
            }
        }
        throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, values);
    }

    private String getApn(int simIdx, int pdpType) {
        final String ATCmd = "AT+CGDCONT?";
        String responValue = IATUtils.sendATCmd(ATCmd, "atchannel" + simIdx);
        Log.d(TAG, "AT+CGDCONT? " + responValue);
        //+CGDCONT:1,IPV4V6,3gnet,0.0.0.0,0,0,0,0
        //+CGDCONT:1,IP,wonet.mnc001.mcc460.gprs,0.0.0.0,0,0,0,0
        //+CGDCONT:2,IPV4V6,3gnet,0.0.0.0,0,0,0,0
        //+CGDCONT:2,IP,cmnet,0.0.0.0,0,0,0,1
        if (!TextUtils.isEmpty(responValue)
                && responValue.startsWith("+CGDCONT") == true) {
            //String dstPrefix = "CGDCONT:"+mPDPType+",\"";
            String dstPrefix = "CGDCONT:" + pdpType + ",";
            int prefixStart = responValue.indexOf(dstPrefix);
            int prefixLen = dstPrefix.length();
            if (prefixStart == -1) {
                return "";
            }
            String tem = responValue.substring(prefixStart + prefixLen);
            //String dstPrefix2 = "\",\"";
            String dstPrefix2 = ",";
            int prefixLen2 = dstPrefix2.length();
            int prefixStart2 = tem.indexOf(dstPrefix2);
            if (prefixStart2 == -1) {
                return "";
            }
            String tem2 = tem.substring(prefixStart2 + prefixLen2);
            //int dstEnd = tem2.indexOf("\"");
            int dstEnd = tem2.indexOf(",");
            if (dstEnd == 0) {
                return "";
            }
            String dst = tem2.substring(0, dstEnd);
            Log.d("TAG", "default bearer net access dst=" + dst);
            return dst;
        } else {
            return "";
        }
    }
}
