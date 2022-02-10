package com.sprd.engineermode.debuglog;

import java.util.ArrayList;
import java.util.List;

import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.intf.ITelephonyApi;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.util.Log;

import static com.unisoc.engineermode.core.intf.ITelephonyApi.IBand.*;

public class WCDMABandData extends BandData {
    private static final String TAG = "WCDMABandData";

    private final static String ORI_WCDMA_BAND = "ORI_WCDMA_BAND";
    //private static final int[] mWBandMap = {1, 2, 5, 8};
    //private int mWCDMASupportBand = -1;
    private List<WcdmaBand> supportedBands = null;
    //private ArrayList<String> mTitle;
    private ITelephonyApi teleApi = CoreApi.getTelephonyApi();

    public WCDMABandData(Context context, int phoneID) {
        super(context, phoneID);
        //mTitle = new ArrayList<String>();
    }

    public WCDMABandData(Context context, int phoneID, PreferenceCategory pref) {
        super(context, phoneID, pref);

        supportedBands = loadBands();
    }

    public List<WcdmaBand> getSupportBands() {

        if (supportedBands != null) {
            return supportedBands;
        }

        return getBands();
    }

    private List<WcdmaBand> getBands() {
        List<WcdmaBand> selectedBands;
        try {
            selectedBands = teleApi.band().getWcdmaBand(mPhoneID);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return selectedBands;
    }

    @Override
    protected boolean getSelectedBand() {

        List<WcdmaBand> selectedBands = getBands();
        if (selectedBands == null) {
            return false;
        }

        if (supportedBands != null) {
            if (supportedBands.isEmpty()) {
                supportedBands.addAll(selectedBands);
                saveBands(selectedBands);
            }
        }

        ArrayList<String> title = new ArrayList<>();
        ArrayList<String> key = new ArrayList<>();
        ArrayList<Integer> checked = new ArrayList<>();

        for (WcdmaBand band : supportedBands) {
            title.add(band.name());
            key.add("w" + band.name());
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

    private void saveBands(List<WcdmaBand> bandList) {
        if (bandList.isEmpty()) {
            return;
        }

        StringBuffer saveValue = new StringBuffer();
        for (WcdmaBand band : bandList) {
            if (!saveValue.toString().equals("")) {
                saveValue.append(",");
            }
            saveValue.append(band.name());
        }

        Log.d(TAG, "save wcdma band: " + saveValue.toString());


        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(ORI_WCDMA_BAND, saveValue.toString());
        editor.apply();
    }

    private List<WcdmaBand> loadBands() {
        List<WcdmaBand> bandList = new ArrayList<>();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        String saveValue = sp.getString(ORI_WCDMA_BAND, "");

        if (saveValue.equals("")) {
            return bandList;
        }

        String[] bands = saveValue.split(",");
        for (String band : bands) {
            Log.d(TAG, "load band: " + band);
            bandList.add(WcdmaBand.valueOf(band));
        }

        return bandList;
    }

    @Override
    public boolean setSelectedBandToModem() {
        List<WcdmaBand> enabledBandList = new ArrayList<>();
        List<WcdmaBand> disabledBandList = new ArrayList<>();
        for (int i=0; i < supportedBands.size(); i++) {
            if (mSelecteds[i] == 1) {
                enabledBandList.add(supportedBands.get(i));
            } else {
                disabledBandList.add(supportedBands.get(i));
            }
        }

        Log.d(TAG, "setSelectedBandToModem");
        try {
            teleApi.band().setWcdmaBand(mPhoneID, enabledBandList, disabledBandList);
        } catch (Exception e) {
            e.printStackTrace();
            mSetSuccessful = false;
            return false;
        }

        mSetSuccessful = true;
        return true;
    }
}
