package com.sprd.engineermode.debuglog;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.util.Log;

import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.intf.ITelephonyApi;

import java.util.ArrayList;
import java.util.List;

import static com.unisoc.engineermode.core.intf.ITelephonyApi.IBand.CDMA2000Band;

public class CDMA2000BandData extends BandData {
    private static final String TAG = "CDMA2000BandData";

    private final static String ORI_CDMA_BAND = "ORI_CDMA_BAND";
    private List<CDMA2000Band> supportedBands = null;
    private ITelephonyApi teleApi = CoreApi.getTelephonyApi();

    public CDMA2000BandData(Context context, int phoneID) {
        super(context, phoneID);
    }

    public CDMA2000BandData(Context context, int phoneID, PreferenceCategory pref) {
        super(context, phoneID, pref);
        supportedBands = loadBands();
    }

    public List<CDMA2000Band> getSupportBands() {

        if (supportedBands != null) {
            return supportedBands;
        }

        return getBands();
    }

    private List<CDMA2000Band> getBands() {

        List<CDMA2000Band> selectedBands;
        try {
            selectedBands = teleApi.band().getCDMA2000Band(mPhoneID);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return selectedBands;
    }

    @Override
    protected boolean getSelectedBand() {

        List<CDMA2000Band> selectedBands = getBands();
        if (selectedBands == null) {
            return false;
        }

        if (supportedBands != null) {
            if (supportedBands.isEmpty()) {
                supportedBands.addAll(selectedBands);
                saveBands(selectedBands);
            }
            ArrayList<String> title = new ArrayList<>();
            ArrayList<String> key = new ArrayList<>();
            ArrayList<Integer> checked = new ArrayList<>();

            for (CDMA2000Band band : supportedBands) {
                title.add(band.name());
                key.add("w" + band.name());
                checked.add(selectedBands.contains(band)? 1 : 0);
            }

            if (mBandTitle == null) {
                mBandTitle = title.toArray(new String[0]);
                mBandFrequency = key.toArray(new String[0]);
                mPrefCheckBox = new CheckBoxPreference[mBandTitle.length];
                mSelecteds = new int[checked.size()];
            }
            for (int i = 0; i < checked.size(); i++) {
                mSelecteds[i] = checked.get(i);
            }
        }
        return true;
    }

    private void saveBands(List<CDMA2000Band> bandList) {
        if (bandList.isEmpty()) {
            return;
        }

        String saveValue = "";
        for (CDMA2000Band band : bandList) {
            if (!saveValue.equals("")) {
                saveValue += ",";
            }
            saveValue += band.name();
        }

        Log.d(TAG, "save wcdma band: " + saveValue);


        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(ORI_CDMA_BAND, saveValue);
        editor.apply();
    }

    private List<CDMA2000Band> loadBands() {
        List<CDMA2000Band> bandList = new ArrayList<>();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        String saveValue = sp.getString(ORI_CDMA_BAND, "");

        if (saveValue.equals("")) {
            return bandList;
        }

        String[] bands = saveValue.split(",");
        for (String band : bands) {
            Log.d(TAG, "load band: " + band);
            bandList.add(CDMA2000Band.valueOf(band));
        }

        return bandList;
    }

    @Override
    public boolean setSelectedBandToModem() {
        List<CDMA2000Band> enabledBandList = new ArrayList<>();
        List<CDMA2000Band> disabledBandList = new ArrayList<>();
        for (int i=0; i < supportedBands.size(); i++) {
            if (mSelecteds[i] == 1) {
                enabledBandList.add(supportedBands.get(i));
            } else {
                disabledBandList.add(supportedBands.get(i));
            }
        }

        Log.d(TAG, "setSelectedBandToModem");
        try {
            teleApi.band().setCDMA2000Band(mPhoneID, enabledBandList, disabledBandList);
        } catch (Exception e) {
            e.printStackTrace();
            mSetSuccessful = false;
            return false;
        }

        mSetSuccessful = true;
        return true;
    }
}
