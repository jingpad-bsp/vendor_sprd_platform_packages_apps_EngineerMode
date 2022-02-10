package com.unisoc.engineermode.core.impl.telephony;

import android.util.Log;

import com.unisoc.engineermode.core.exception.OperationFailedException;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.utils.IATUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


class BandImpl implements ITelephonyApi.IBand {
    private static final String TAG = "EN-BAND";

    private static final int[] gsmBandMap = {
            0x01, // 0  - 0000 0001
            0x02, // 1  - 0000 0010
            0x04, // 2  - 0000 0100
            0x08, // 3  - 0000 1000
            0x03, // 4  - 0000 0011
            0x09, // 5  - 0000 1001
            0x0A, // 6  - 0000 1010
            0x0C, // 7  - 0000 1100
            0x05, // 8  - 0000 0101
            0x0B, // 9  - 0000 1011
            0x0D, // 10 - 0000 1101
            0x06, // 11 - 0000 0110
            0x0E, // 12 - 0000 1110
            0x07, // 13 - 0000 0111
            0x0F, // 14 - 0000 1111
    };
    private static final int GSM900_BIT = 0x01;
    private static final int DCS1800_BIT = 0x02;
    private static final int PCS1900_BIT = 0x04;
    private static final int GSM850_BIT = 0x08;

    @Override
    public List<GsmBand> getGsmBand (int simIdx) throws Exception {
        String resp = IATUtils.sendATCmd(engconstents.ENG_AT_CURRENT_GSMBAND, simIdx);

        if (!resp.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException("AT returns: " + resp);
        }
        // resp e.g. +SBAND: 13,14
        String str[] = resp.split(":|\n|,");
        Log.d(TAG, "str:"+ Arrays.toString(str));
        int bandValue = Integer.valueOf(str[2].trim());

        //GsmBand[] bands = new GsmBand[GsmBand.values().length];
        List<GsmBand> bandList = new ArrayList<>();

        for (GsmBand band : GsmBand.values()) {
           switch (band) {
               case GSM900:
                   if ((gsmBandMap[bandValue] & GSM900_BIT) != 0) {
                      bandList.add(band);
                   }
                   break;
               case DCS1800:
                   if ((gsmBandMap[bandValue] & DCS1800_BIT) != 0) {
                       bandList.add(band);
                   }
                   break;
               case PCS1900:
                   if ((gsmBandMap[bandValue] & PCS1900_BIT) != 0) {
                       bandList.add(band);
                   }
                   break;
               case GSM850:
                   if ((gsmBandMap[bandValue] & GSM850_BIT) != 0) {
                       bandList.add(band);
                   }
                   break;
           }
        }

        /*
        for (int i=0; i < GsmBand.values().length; i++) {
            if (((gsmBandMap[bandValue] >> i) & (0x01)) == 1 ) {
                bandList.add(GsmBand.values()[i]);
            }
        }
        */

        return bandList;
    }

    @Override
    public void setGsmBand(int simIdx, List<GsmBand> bands) throws Exception {

        if (bands.isEmpty()) {
            throw new OperationFailedException("at least one band is needed");
        }

        int bandValue = 0;
        for (GsmBand band : bands) {
            switch (band) {
                case GSM900:
                    bandValue |= GSM900_BIT;
                    break;
                case DCS1800:
                    bandValue |= DCS1800_BIT;
                    break;
                case PCS1900:
                    bandValue |= PCS1900_BIT;
                    break;
                case GSM850:
                    bandValue |= GSM850_BIT;
                    break;
            }
        }

        int setValue = 0;

        for ( int i = 0; i < gsmBandMap.length; i++ ) {
            if (gsmBandMap[i] == bandValue) {
                setValue = i;
                break;
            }
        }

        String resp = IATUtils.sendATCmd(engconstents.ENG_AT_SELECT_GSMBAND + setValue, simIdx);

        if (!resp.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException("AT returns: " + resp);
        }
    }

    @Override
    public TdBand getTdBand(int simIdx) throws Exception {
        String resp = IATUtils.sendATCmd(engconstents.ENG_AT_TD_LOCKED_BAND, simIdx);
        if (!resp.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException("AT returns: " + resp);
        }

        TdBand band = new TdBand();
        band.band_a = false;
        band.band_f = false;

        String str[] = resp.split(":|\n|,");
        Log.d(TAG, "str:"+Arrays.toString(str));
        String bandstr = str[1].trim();
        if (bandstr.startsWith("A ") ) {
            band.band_a = true;
        } else if (bandstr.startsWith("F ")) {
            band.band_f = true;
        } else if (bandstr.startsWith("A+F ")) {
            band.band_a = true;
            band.band_f = true;
        }
        return band;
    }

    @Override
    public void setTdBand(int simIdx, TdBand bands) throws Exception {

        String para = "";
        if (bands.band_a && bands.band_f) {
            para = "A+F";
        } else if (bands.band_a) {
            para = "A";
        } else if (bands.band_f) {
            para = "F";
        }

        String atCmd = String.format("%s\"%s\"", engconstents.ENG_AT_TD_SET_BAND, para);
        String resp = IATUtils.sendATCmd(atCmd, simIdx);

        if (!resp.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException("AT failed: " + resp);
        }
    }

    @Override
    public List<WcdmaBand> getWcdmaBand(int simIdx) throws Exception {

        List<WcdmaBand> bandList = new ArrayList<>();
        for (WcdmaBand band : WcdmaBand.values()) {
            if (isWcdmaBandOpen(simIdx, band)) {
               bandList.add(band);
            }
        }

        return bandList;

    }

    @Override
    public void setWcdmaBand(int simIdx, List<WcdmaBand> enabledBands, List<WcdmaBand> disabledBands) throws Exception {

        if (enabledBands.isEmpty()) {
            throw new OperationFailedException("at least one enabled band is needed");
        }

        for (WcdmaBand band : enabledBands) {
            setWcdmaBandState(simIdx, band, true);
        }

        for (WcdmaBand band : disabledBands) {
            setWcdmaBandState(simIdx, band, false);
        }

    }

    @Override
    public boolean isWcdmaBandOpen(int simIdx, WcdmaBand band) throws Exception {
        String resp = IATUtils.sendATCmd(engconstents.ENG_AT_W_LOCKED_BAND + band.getValue(), simIdx);
        Log.d(TAG, "isWcdmaBandOpen() resp:" + resp);
        if (resp.contains(IATUtils.AT_OK)) {
            String str[] = resp.split(":|\n|,");
            return str.length >= 3 && str[3].trim().equals("1");

        } else {
            throw new OperationFailedException("AT failed: " + resp);
        }
    }

    /* SPRD 958254: C2K feature @{ */
    @Override
    public List<CDMA2000Band> getCDMA2000Band(int simIdx) throws Exception {

        List<CDMA2000Band> bandList = new ArrayList<>();
        for (CDMA2000Band band : CDMA2000Band.values()) {
            if (isCDMA2000BandOpen(simIdx, band)) {
               bandList.add(band);
            }
        }

        return bandList;

    }

    @Override
    public void setCDMA2000Band(int simIdx, List<CDMA2000Band> enabledBands, List<CDMA2000Band> disabledBands) throws Exception {

        if (enabledBands.isEmpty()) {
            throw new OperationFailedException("at least one enabled band is needed");
        }

        for (CDMA2000Band band : enabledBands) {
            setCDMA2000BandState(simIdx, band, true);
        }

        for (CDMA2000Band band : disabledBands) {
            setCDMA2000BandState(simIdx, band, false);
        }

    }

    @Override
    public boolean isCDMA2000BandOpen(int simIdx, CDMA2000Band band) throws Exception {
        String resp = IATUtils.sendATCmd(engconstents.ENG_AT_W_LOCKED_BAND + band.getValue(), simIdx);
        Log.d(TAG, "isCDMA2000BandOpen() resp:" + resp);
        if (resp.contains(IATUtils.AT_OK)) {
            String str[] = resp.split(":|\n|,");
            return str.length >= 3 && str[3].trim().equals("1");

        } else {
            throw new OperationFailedException("AT failed: " + resp);
        }
    }

    private void setCDMA2000BandState(int simIdx, CDMA2000Band band, boolean enable) throws Exception {
        String atCmd = String.format("%s%d,%d", engconstents.ENG_AT_W_LOCK_BAND, band.getValue(), enable? 1 : 0);
        String resp = IATUtils.sendATCmd(atCmd, simIdx);

        if (!resp.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException("AT failed: " + resp);
        }
    }
    /* @} */

    /** sprd 1002440  : 5G netInfo mode
     * UNISOC: Bug1381637 Adapt and modify according to the underlying NR support. @{ */
    @Override
    public NrBand getNrBand(int simIdx, NrBand supportedBand) throws Exception {
        // get lock or not for NR bands
        String resp = IATUtils.sendATCmd(engconstents.ENG_AT_GET_NR_BAND, simIdx);
        Log.d(TAG, "getNrBand resp:" + resp);

        if (!resp.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException("AT returns: " + resp);
        }

        String[] result = resp.split("\n");
        String[] result2 = result[0].split("\\:");
        String[] result3 = result2[1].split(",");

        NrBand band = new NrBand();
        int fdd = Integer.parseInt(result3[0].trim());
        int fddBackup = Integer.parseInt(result3[1].trim());
        int tdd = Integer.parseInt(result3[2].trim());
        for (int i = 0; i < 16; i++) {
            //set all FDD and TDD band's checkbox as checked when result is "0, 0, 0"
            if (tdd == 0 && fdd == 0 && fddBackup == 0) {
                band.tddBands[i] = supportedBand.tddBands[i]; // if support band, then set checked
                band.fddBands[i] = supportedBand.fddBands[i];
            } else {
                if (((tdd >> i) & 0x01) == 1 && supportedBand.tddBands[i] == 1) {
                    band.tddBands[i] = 1;
                }
                if (((fdd >> i) & 0x01) == 1 && supportedBand.fddBands[i] == 1) {
                    band.fddBands[i] = 1;
                }
            }
        }
        return band;
    }

    //get support NR bands
    @Override
    public NrBand getSupportNrBand(int simIdx) throws Exception {

        String resp = IATUtils.sendATCmd(engconstents.ENG_AT_GET_SUPPORT_NR_BAND, simIdx);
        Log.d(TAG, "getSupportNrBand resp:" + resp);

        if (!resp.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException("AT returns: " + resp);
        }

        String[] result = resp.split("\n");
        String[] result2 = result[0].split("\\:");
        String[] result3 = result2[1].split(",");

        NrBand band = new NrBand();
        for (int j = 0; j < Integer.valueOf(result3[1].trim()); j++) {
            for ( int n = 0; n < band.nrFddBandName.length; n++ ) {
                if (band.nrFddBandName[n].substring(9).equals(result3[j+2].trim())) {
                    band.fddBands[n] = 1;
                }

            }
            for ( int n = 0; n < band.nrTddBandName.length; n++ ) {
                if (band.nrTddBandName[n].substring(9).equals(result3[j+2].trim())) {
                    band.tddBands[n] = 1;
                }
            }
            Log.d(TAG, "band.fddBands = " + Arrays.toString(band.fddBands) + " band.tddBands = " + Arrays.toString(band.tddBands));
        }
        return band;
    }

    @Override
    public void setNrBand(int simIdx, NrBand bands) throws Exception {

        int tdd = 0;
        int fdd = 0;
        for ( int i = 0; i < 16; i++ ) {
            if (bands.tddBands[i] == 1 && i != 7/*NR_BAND_N77 not support*/) {
                tdd |= 0x1 << i;
            }
            if (bands.fddBands[i] == 1) {
                fdd |= 0x1 << i;
            }
        }

        Log.d(TAG, "tdd = " + tdd + " fdd = " + fdd);
        int standby = 0;

        String atCmd = String.format("%s%d,%d,%d", engconstents.ENG_AT_SET_NR_BAND, fdd, standby, tdd);
        String resp = IATUtils.sendATCmd(atCmd, simIdx);

        if (!resp.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException("AT failed: " + resp);
        }
    }
    /** @} */

    @Override
    public LteBand getLteBand(int simIdx) throws Exception {

        String resp = IATUtils.sendATCmd(engconstents.ENG_AT_GET_LTE_BAND, simIdx);
        Log.d(TAG, "getLteBand resp:" + resp);

        if (!resp.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException("AT returns: " + resp);
        }

        String[] result = resp.split("\n");
        String[] result2 = result[0].split("\\:");
        String[] result3 = result2[1].split(",");
        int tdd = ((Integer.valueOf(result3[0].trim()) << 16) & 0xffff0000) + Integer.valueOf(result3[1].trim());
        int fdd = ((Integer.valueOf(result3[2].trim()) << 16) & 0xffff0000) + Integer.valueOf(result3[3].trim());

        LteBand band = new LteBand();
        for (int i = 0; i < 32; i++) {
            if (((tdd >> i) & 0x01) == 1) {
                band.tddBands[i] = 1;
            }

            if (((fdd >> i) & 0x01) == 1) {
                band.fddBands[i] = 1;
            }
        }

        return band;
    }
    @Override
    public void setLteBand(int simIdx, LteBand bands) throws Exception {

        int tdd = 0;
        int fdd = 0;
        for ( int i = 0; i < 32; i++ ) {
            if (bands.tddBands[i] == 1) {
                tdd |= 0x1 << i;
            }
            if (bands.fddBands[i] == 1) {
                fdd |= 0x1 << i;
            }
        }

        int tdd_h = (tdd>>16) & 0xFFFF;
        int tdd_l = tdd & 0xFFFF;
        int fdd_h = (fdd>>16) & 0xFFFF;
        int fdd_l = fdd & 0xFFFF;

        String atCmd = String.format("%s%d,%d,%d,%d", engconstents.ENG_AT_SET_LTE_BAND, tdd_h, tdd_l, fdd_h, fdd_l);
        String resp = IATUtils.sendATCmd(atCmd, simIdx);

        if (!resp.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException("AT failed: " + resp);
        }
    }

    private void setWcdmaBandState(int simIdx, WcdmaBand band, boolean enable) throws Exception {
        String atCmd = String.format("%s%d,%d", engconstents.ENG_AT_W_LOCK_BAND, band.getValue(), enable? 1 : 0);
        String resp = IATUtils.sendATCmd(atCmd, simIdx);

        if (!resp.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException("AT failed: " + resp);
        }
    }
}
