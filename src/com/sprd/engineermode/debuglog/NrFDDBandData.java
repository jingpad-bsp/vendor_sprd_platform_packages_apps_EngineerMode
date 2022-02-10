package com.sprd.engineermode.debuglog;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceCategory;
import android.util.Log;

import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.intf.ITelephonyApi.IBand.NrBand;

public class NrFDDBandData extends BandData {
    private static final String TAG = "NrFDDBandData";
    NrBandData mNRBandData;
    private ITelephonyApi teleApi = CoreApi.getTelephonyApi();
    ArrayList<String> mTitle;

    public NrFDDBandData(Context context, int phoneID) {
        super(context, phoneID);
        mNRBandData = NrBandData.getInstance(phoneID, context);
        mTitle = new ArrayList<String>();
    }
    public NrFDDBandData(Context context, int phoneID, PreferenceCategory pref) {
        super(context, phoneID, pref);
        mNRBandData = NrBandData.getInstance(phoneID, context);
    }


    @Override
    protected boolean getSelectedBand() {
        ArrayList<String> title = new ArrayList<String>();
        ArrayList<String> key = new ArrayList<String>();
        ArrayList<Integer> checked = new ArrayList<Integer>();

        mNRBandData.loadSelectedBand();
        int[] supportband = mNRBandData.getSupportBand_FDD();
        int[] checkedband = mNRBandData.getCheckedBand_FDD();

        if (supportband == null) {
            return false;
        }

        /** UNISOC: Bug1381637 Adapt and modify according to the underlying NR support. @{*/
        NrBand nrBand = new NrBand();
        for ( int i = 0; i < nrBand.nrFddBandName.length; i++ ) {
            if (supportband[i] == 1) {
                title.add(nrBand.nrFddBandName[i]);
                key.add("nr" + nrBand.nrFddBandName[i]);
                checked.add(checkedband[i]);
            }
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

    @Override
    public boolean setSelectedBandToModem() {

        mNRBandData.loadSelectedBand();
        NrBand newBand = new NrBand();
        newBand.tddBands = Arrays.copyOf(mNRBandData.getCheckedBand_TDD(), 16);
        newBand.fddBands = Arrays.copyOf(mNRBandData.getCheckedBand_FDD(), 16);

        int n = 0;
        for ( int i = 0; i < 16; i++ ) {
            if (mNRBandData.supportedBand.fddBands[i] == 1) {
                if (mSelecteds[n] == 1) {
                    newBand.fddBands[i] = 1;
                } else {
                    newBand.fddBands[i] = 0;
                }
                n++;
            } else {
                newBand.fddBands[i] = 0;
            }
        }
        /** @}*/

        Log.d(TAG, "setSelectedBandToModem");
        try {
            teleApi.band().setNrBand(mPhoneID, newBand);
        } catch (Exception e) {
            e.printStackTrace();
            mSetSuccessful = false;
            return false;
        }

        mSetSuccessful = true;
        return true;
    }
}
