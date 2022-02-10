package com.sprd.engineermode.debuglog;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.intf.ITelephonyApi.IBand.NrBand;

public class NrBandData{
    private static final String TAG = "NrBandData";
    private final static String ORI_NR_BAND_TDD = "ORI_NR_BAND_TDD";
    private final static String ORI_NR_BAND_FDD = "ORI_NR_BAND_FDD";
    public static final String KEY_FDD_NR = "NR_FDD_BAND";
    public static final String KEY_TDD_NR = "NR_TDD_BAND";

    public NrBand supportedBand;
    private NrBand checkedBand = null;
    private Context mContext;
    private int mPhoneID;
    private String mChannel;
    private ITelephonyApi teleApi = CoreApi.getTelephonyApi();

    private static NrBandData NrBandData;
    public synchronized static NrBandData getInstance(int phoneID, Context context) {
        if ( NrBandData == null ) {
            NrBandData = new NrBandData(phoneID, context);
        }
        return NrBandData;
    }

    /** UNISOC: Bug1381637 Adapt and modify according to the underlying NR support. @{*/
    public NrBandData(int phoneID, Context context) {
        mContext = context;
        mPhoneID = phoneID;
        mChannel = "atchannel" + mPhoneID;
        supportedBand = getNrBands();

    }

    public NrBand getSupportBands() {
        if (supportedBand != null) {
            return supportedBand;
        }

        return getNrBands();
    }

    private NrBand getNrBands() {
        NrBand band;
        try {
            band = teleApi.band().getSupportNrBand(mPhoneID);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return band;
    }
    /** @}*/

    private NrBand getBands() {
        NrBand band;
        try {
            band = teleApi.band().getNrBand(mPhoneID, getSupportBands());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return band;
    }

    public boolean loadSelectedBand() {

        checkedBand = getBands();
        if (checkedBand == null) {
            return false;
        }

//        if (supportedBand == null) {
        saveBands(checkedBand);
//            supportedBand = loadBands();
//        }
        return true;
    }

    public int[] getSupportBand_TDD() {
        return supportedBand == null ? null : supportedBand.tddBands;
    }


    public int[] getSupportBand_FDD() {
        return supportedBand == null ? null : supportedBand.fddBands;
    }

    public int[] getCheckedBand_TDD() {
        return checkedBand == null ? null : checkedBand.tddBands;
    }

    public int[] getCheckedBand_FDD() {
        return checkedBand == null ? null : checkedBand.fddBands;
    }


    private void saveBands(NrBand nrBand) {

        String tddSaveValue = "";
        String fddSaveValue = "";

        for (int i = 0; i < 16; i++) {
            if (nrBand.tddBands[i] == 1) {
                if (!tddSaveValue.equals("")) {
                    tddSaveValue += ",";
                }
                tddSaveValue += i;
            }
            if (nrBand.fddBands[i] == 1) {
                if (!fddSaveValue.equals("")) {
                    fddSaveValue += ",";
                }
                fddSaveValue += i;
            }
        }

        Log.d(TAG, "save tdd band: " + tddSaveValue);
        Log.d(TAG, "save fdd band: " + fddSaveValue);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(ORI_NR_BAND_TDD, tddSaveValue);
        editor.putString(ORI_NR_BAND_FDD, fddSaveValue);
        editor.apply();
    }

    private NrBand loadBands() {
        NrBand nrBand = new NrBand();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        String tddSaveValue = sp.getString(ORI_NR_BAND_TDD, "");
        String fddSaveValue = sp.getString(ORI_NR_BAND_FDD, "");
        Log.d(TAG, "tddSaveValue: "+tddSaveValue+"  fddSaveValue:"+fddSaveValue);

        if (tddSaveValue.equals("") && fddSaveValue.equals("")) {
            return null;
        }

        if (!tddSaveValue.equals("")) {
            String[] bands = tddSaveValue.split(",");
            for (String band : bands) {
                Log.d(TAG, "load tdd band: " + band);
                nrBand.tddBands[Integer.valueOf(band)] = 1;
            }
        }

        if (!fddSaveValue.equals("")) {
            String[] bands = fddSaveValue.split(",");
            for (String band : bands) {
                Log.d(TAG, "load fdd band: " + band);
                nrBand.fddBands[Integer.valueOf(band)] = 1;
            }
        }
        return nrBand;
    }


}
