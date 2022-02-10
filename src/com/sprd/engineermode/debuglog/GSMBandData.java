package com.sprd.engineermode.debuglog;

import java.util.ArrayList;
import java.util.List;

import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.intf.ITelephonyApi.IBand.GsmBand;

import android.content.SharedPreferences;
import android.content.Context;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceManager;
import android.preference.PreferenceCategory;
import android.util.Log;

public class GSMBandData extends BandData {
    private static final String TAG = "GSMBandData";
    private final static String ORI_GSM_BAND = "ORI_GSM_BAND";
    //private static final String[] mGBandMap = {"GSM850", "PCS1900", "DCS1800", "GSM900"};
    //private int mGSMSupportBand = -1;
    private List<GsmBand> supportedBands = null;
    //private ArrayList<String> mTitle;
    private ITelephonyApi teleApi = CoreApi.getTelephonyApi();

    private static final int[] mSBANDMap = {
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
    public GSMBandData(Context context, int phoneID) {
        super(context, phoneID);
    }

    public GSMBandData(Context context, int phoneID, PreferenceCategory pref) {
        super(context, phoneID, pref);
        supportedBands = loadBands();
    }

    protected boolean getSelectedBand() {
        List<GsmBand> selectedBands = null;
        try {
            selectedBands = teleApi.band().getGsmBand(mPhoneID);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        if (supportedBands.isEmpty()) {
            supportedBands.addAll(selectedBands);
            saveBands(selectedBands);
        }

        ArrayList<String> title = new ArrayList<String>();
        ArrayList<String> key = new ArrayList<String>();
        ArrayList<Integer> checked = new ArrayList<Integer>();

        for (GsmBand band : supportedBands) {
            title.add(band.name());
            key.add("g" + band.name());
            checked.add(selectedBands.contains(band)? 1 : 0);
        }

        if ( mBandTitle == null ) {
            mBandTitle = title.toArray(new String[0]);
            mBandFrequency = key.toArray(new String[0]);
            mPrefCheckBox = new CheckBoxPreference[mBandTitle.length];
            mSelecteds = new int[checked.size()];
        }
        for (int i = 0; i < checked.size(); i++) {
            mSelecteds[i] = checked.get(i);
        }
        return true;
    }

    protected String[] getSelectATCmd() {
        return new String[0];
    }

    private void saveBands(List<GsmBand> bandList) {
        if (bandList.isEmpty()) {
            return;
        }

        String saveValue = "";
        for (GsmBand band : bandList) {
            if (!saveValue.equals("")) {
                saveValue += ",";
            }
            saveValue += band.name();
        }

        Log.d(TAG, "save gsm band: " + saveValue);


        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(ORI_GSM_BAND, saveValue);
        editor.apply();
    }

    private List<GsmBand> loadBands() {
        List<GsmBand> bandList = new ArrayList<>();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        String saveValue = sp.getString(ORI_GSM_BAND, "");
        if (saveValue.equals("")) {
            return bandList;
        }

        String[] bands = saveValue.split(",");
        for (String band : bands) {
            Log.d(TAG, "load band: " + band);
            bandList.add(GsmBand.valueOf(band));
        }

        return bandList;
    }

    @Override
    public boolean setSelectedBandToModem() {
        List<GsmBand> bandList = new ArrayList<>();
        for (int i=0; i < supportedBands.size(); i++) {
            if (mSelecteds[i] == 1) {
               bandList.add(supportedBands.get(i));
            }
        }
        Log.d(TAG, "setSelectedBandToModem");
        try {
            teleApi.band().setGsmBand(mPhoneID, bandList);
        } catch (Exception e) {
            e.printStackTrace();
            mSetSuccessful = false;
            return false;
        }

        mSetSuccessful = true;
        return true;
    }
}
