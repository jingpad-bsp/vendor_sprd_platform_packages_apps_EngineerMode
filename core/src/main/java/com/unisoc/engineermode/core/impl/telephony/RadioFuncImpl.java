package com.unisoc.engineermode.core.impl.telephony;

import android.util.Log;

import com.unisoc.engineermode.core.exception.OperationFailedException;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.utils.IATUtils;

class RadioFuncImpl implements ITelephonyApi.IRadioFunc {
    private static final String TAG = "RFPOWER";
    private static final int WCDMA_PRIMARY_ONLY = 0;
    private static final int WCDMA_PRIMARY_AND_DIVERSITY = 1;
    private static final int WCDMA_DIVERSITY_ONLY = 17;

    @Override
    public void setMaxPower(Band band, int dbm) throws Exception{
        int freq = 0;
        int bandWidth = 0;

        Log.d(TAG, "setMaxPower: " + band.getValue() + ", dbm: " + dbm);

        switch (band) {
            case GSM850:
                break;
            case GSM900:
                break;
            case DCS1800:
                break;
            case DCS1900:
                break;
            case TD19:
                freq = 10054;
                break;
            case TD21:
                freq = 9404;
                break;
            case WBAND1:
                freq = 10693;
                break;
            case WBAND2:
                freq = 9875;
                break;
            case WBAND5:
                freq = 4450;
                break;
            case WBAND8:
                freq = 3012;
                break;
        }

        setMaxPowerImpl(band.getValue(), true, dbm, freq, bandWidth, false);

    }

    @Override
    public void clearMaxPower(Band band) throws Exception {
        Log.d(TAG, "clearMaxPower: " + band.getValue());
        setMaxPowerImpl(band.getValue(), false, 0, 0, 0, false);
    }

    @Override
    public DualRfState getDualRfState() throws Exception {
        String atCmd = "AT+SPDUALRFSEL?";
        String atResponse = IATUtils.sendATCmd(atCmd, "atchannel0");

        if(!atResponse.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException("at return: " + atResponse);
        }

        String[] tempArray = atResponse.split(" ");
        String[] paraArray = tempArray[1].split(",");
        String[] paraArray2 = paraArray[1].trim().split("\n");
        int lteState = Integer.valueOf(paraArray[0].trim());
        int wcdmaState = Integer.valueOf(paraArray2[0].trim());
        Log.d(TAG, "wcdmaState=" + wcdmaState);
        Log.d(TAG, "lteState=" + lteState);

        DualRfState state = new DualRfState();
        switch(wcdmaState) {
            case WCDMA_PRIMARY_ONLY:
                state.wcdmaState = WcdmaDualRfState.PRIMARY_ONLY;
                break;
            case WCDMA_PRIMARY_AND_DIVERSITY:
                state.wcdmaState = WcdmaDualRfState.PRIMARY_AND_DIVERSITY;
                break;
            case WCDMA_DIVERSITY_ONLY:
                state.wcdmaState = WcdmaDualRfState.DIVERSITY_ONLY;
                break;
            default:
                throw new OperationFailedException("wcdma state is wrong.");
        }

        if ((lteState & 1 << 3) != 0) {
            state.isLteSccTxOpened = true;
        }

        if ((lteState & 1 << 4) != 0) {
            state.isLteDiversityRxOpened = true;
        }

        if ((lteState & 1 << 5) != 0) {
            state.isLtePrimaryTxOpened = true;
        }

        if ((lteState & 1 << 6) != 0) {
            state.isLtePrimaryRxOpened = true;
        }
        return state;
    }

    @Override
    public void setDualRfState(DualRfState state) throws Exception {
        int wcdma = WCDMA_PRIMARY_ONLY;
        switch(state.wcdmaState) {
            case PRIMARY_ONLY:
                wcdma = WCDMA_PRIMARY_ONLY;
                break;
            case PRIMARY_AND_DIVERSITY:
                wcdma = WCDMA_PRIMARY_AND_DIVERSITY;
                break;
            case DIVERSITY_ONLY:
                wcdma = WCDMA_DIVERSITY_ONLY;
                break;
        }

        int lte = 0;
        if (state.isLteSccTxOpened) {
            lte = lte | 1 << 3;
        }
        if (state.isLteDiversityRxOpened) {
            lte = lte | 1 << 4;
        }
        if (state.isLtePrimaryTxOpened) {
            lte = lte | 1 << 5;
        }
        if (state.isLtePrimaryRxOpened) {
            lte = lte | 1 << 6;
        }

        String atCmd = "AT+SPDUALRFSEL=" + lte + "," + wcdma;
        Log.d(TAG, "mLTESCCTX mATCmd=" + atCmd);
        String atResponse = IATUtils.sendATCmd(atCmd, "atchannel0");

        if(!atResponse.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException("at return: " + atResponse);
        }


    }

    @Override
    public int getUeCategory() throws Exception {

        //String cat3Value = "Cat: ";
        String at = engconstents.ENG_AT_CATEGARY;
        String result = IATUtils.sendATCmd(at, "atchannel0");
        Log.d(TAG, "GET_CATEGARY_VALUE >>> " + engconstents.ENG_AT_CATEGARY + ": " + result);
        try {
            if (result.contains(IATUtils.AT_OK)) {
                String[] str1 = result.split("\n");
                String[] str2 = str1[0].split(":");
                return Integer.parseInt(str2[1].trim());
            } else {
                throw new OperationFailedException("AT failed: " + result);
            }
        } catch(ArrayIndexOutOfBoundsException | NumberFormatException e) {
            e.printStackTrace();
            throw new OperationFailedException("AT failed: " + result);
        }
    }

    private void setMaxPowerImpl(int band, boolean isOn, int dbm, int freq, int bandWidth, boolean isNegDbm) throws OperationFailedException {
        String cmd = String.format("%s%d,%d,%d,%d,%d,%d",
                engconstents.ENG_SEND_POWER, band, isOn? 1:0, dbm, freq, bandWidth, isNegDbm? 1:0);

        String result = IATUtils.sendATCmd(cmd, "atchannel0");
        if (!result.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException("result: " + result);
        }
    }

}
