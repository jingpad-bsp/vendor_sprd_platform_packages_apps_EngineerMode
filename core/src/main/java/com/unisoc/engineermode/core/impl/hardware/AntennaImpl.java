package com.unisoc.engineermode.core.impl.hardware;


import android.util.Log;

import com.unisoc.engineermode.core.annotation.Implementation;
import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.exception.EmException;
import com.unisoc.engineermode.core.exception.ErrorCode;
import com.unisoc.engineermode.core.exception.OperationFailedException;
import com.unisoc.engineermode.core.intf.IAntenna;
import com.unisoc.engineermode.core.utils.IATUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Implementation(
    interfaceClass = IAntenna.class,
    properties =  {
    })
public class AntennaImpl implements IAntenna {

    private static final String TAG = "ANTENNAIMPL";
    private static final int SIM0 = 0;

    private Map<AntennaState, Integer> gsmStateMap = new HashMap<>();
    private Map<AntennaState, Integer> wcdmaStateMap = new HashMap<>();
    private Map<AntennaState, Integer> lteStateMap = new HashMap<>();
    private Map<AntennaState, Integer> c2kStateMap = new HashMap<>();
    private Map<AntennaState, Integer> nrStateMap = new HashMap<>();

    public AntennaImpl() {
        gsmStateMap.put(AntennaState.PRIMARY_DIVERSITY, 1);
        gsmStateMap.put(AntennaState.PRIMARY, 0);
        gsmStateMap.put(AntennaState.DIVERSITY, 3);

        wcdmaStateMap.put(AntennaState.PRIMARY_DIVERSITY, 1);
        wcdmaStateMap.put(AntennaState.PRIMARY, 0);
        wcdmaStateMap.put(AntennaState.DIVERSITY, 17);

        lteStateMap.put(AntennaState.PRIMARY_DIVERSITY, 0);
        lteStateMap.put(AntennaState.PRIMARY, 1);
        lteStateMap.put(AntennaState.DIVERSITY, 2);

        c2kStateMap.put(AntennaState.PRIMARY_DIVERSITY, 4);
        c2kStateMap.put(AntennaState.PRIMARY, 2);
        c2kStateMap.put(AntennaState.DIVERSITY, 3);
        c2kStateMap.put(AntennaState.PRIMARY_DIVERSITY_DYNAMIC, 5);

        nrStateMap.put(AntennaState.DEFAULT, 0);
        nrStateMap.put(AntennaState.ANT3_RX, 1);
        nrStateMap.put(AntennaState.ANT6_RX, 2);
        nrStateMap.put(AntennaState.ANT5_RX, 3);
        nrStateMap.put(AntennaState.ANT4_RX, 4);
        nrStateMap.put(AntennaState.ANT3_TX, 5);
        nrStateMap.put(AntennaState.ANT6_TX, 6);
        nrStateMap.put(AntennaState.ANT4_TX, 7);
        nrStateMap.put(AntennaState.ANT5_TX, 8);
    }


    private Pattern pattern = Pattern.compile("\\+SPDUALRFSEL: ([0-9,]+)\r\nOK\r\n");

    @Override
    public AllState getAllStates() {
        String result = IATUtils.sendATCmd(engconstents.ENG_GET_ANTENNA_STATE, SIM0);
        if (result == null || !result.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, result);
        }

        Matcher matcher = pattern.matcher(result);
        if (!matcher.find() || matcher.group(1) == null) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, result);
        }

        String[] statusArr = matcher.group(1).split(",");
        AllState status = new AllState();
        if (statusArr.length == 2) {
            status.setLte(getStateByValue(lteStateMap,Integer.parseInt(statusArr[0].trim())));
            status.setWcdma(getStateByValue(wcdmaStateMap, Integer.parseInt(statusArr[1].trim())));
        } else if (statusArr.length == 3) {
            status.setLte(getStateByValue(lteStateMap,Integer.parseInt(statusArr[0].trim())));
            status.setWcdma(getStateByValue(wcdmaStateMap, Integer.parseInt(statusArr[1].trim())));
            status.setGsm(getStateByValue(gsmStateMap, Integer.parseInt(statusArr[2].trim())));
        } else if (statusArr.length == 4) {
            status.setLte(getStateByValue(lteStateMap,Integer.parseInt(statusArr[0].trim())));
            status.setWcdma(getStateByValue(wcdmaStateMap, Integer.parseInt(statusArr[1].trim())));
            status.setGsm(getStateByValue(gsmStateMap, Integer.parseInt(statusArr[2].trim())));
            status.setNr(getStateByValue(nrStateMap, Integer.parseInt(statusArr[3].trim())));
        } else {
            status.setWcdma(getStateByValue(wcdmaStateMap, Integer.parseInt(statusArr[0].trim())));
        }
        try {
            status.setC2k(getC2kStatus());
        } catch (EmException e) {
            Log.e(TAG, "get c2k state failed, may not supported");
            status.setC2k(AntennaState.INVALID);
        }
        Log.d(TAG, "status: " + status);
        return status;
    }

    private AntennaState getC2kStatus() {
        String result = IATUtils.sendATCmd(engconstents.ENG_AT_SET_WASDUMMY_PARAM
            + "\"get C2K antenna\"", SIM0);
        if (result == null || !result.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, result);
        }

        String[] c2kStr = result.split(",");
        if (c2kStr.length > 3) {
            return getStateByValue(c2kStateMap,Integer.parseInt(c2kStr[3]));
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, result);
        }
    }

    @Override
    public void setLte(AntennaState status) {
        String atCmd = engconstents.ENG_SET_ANTENNA + lteStateMap.get(status);
        String atResult = IATUtils.sendATCmd(atCmd, SIM0);
        judgeAtResponse(atResult);
    }

    @Override
    public void setWcdma(AntennaState status) {
        String atCmd = String.format("%s,%d",engconstents.ENG_SET_ANTENNA, wcdmaStateMap.get(status));
        String atResult = IATUtils.sendATCmd(atCmd, SIM0);
        judgeAtResponse(atResult);
    }

    @Override
    public void setGsm(AntennaState status) {
        String atCmd = String.format("%s,,%d",engconstents.ENG_SET_ANTENNA, gsmStateMap.get(status));
        String atResult = IATUtils.sendATCmd(atCmd, SIM0);
        judgeAtResponse(atResult);
    }

    @Override
    public void setC2k(AntennaState status) {
        String atCmd = String.format("%s\"set C2K antenna\",1,%d,0,0,0,0",
            engconstents.ENG_AT_SET_WASDUMMY_PARAM, c2kStateMap.get(status));
        String atResult = IATUtils.sendATCmd(atCmd, SIM0);
        judgeAtResponse(atResult);
    }

    @Override
    public void setNr(AntennaState status) {
        String atCmd = String.format("%s,,,%d",engconstents.ENG_SET_ANTENNA, nrStateMap.get(status));
        String atResult = IATUtils.sendATCmd(atCmd, SIM0);
        judgeAtResponse(atResult);
    }

    private AntennaState getStateByValue(Map<AntennaState, Integer> statusMap, int value) {
        for (Map.Entry<AntennaState,Integer> e : statusMap.entrySet()) {
            if (e.getValue() == value) {
                return e.getKey();
            }
        }

        JSONObject json=new JSONObject();
        try {
            json.put("statusMap",statusMap);
        } catch (Exception e) {
            Log.e(TAG,Log.getStackTraceString(e));
        }
        throw new RuntimeException(String.format("can't find value %d in %s",value, json.toString()));
    }

    private void judgeAtResponse(String atRsp) {
        if (!atRsp.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRsp);
        }
    }
}
