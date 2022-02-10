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

public class NrTDDBandData extends BandData {
    private static final String TAG = "NrTDDBandData";
    NrBandData mNRBandData;
    ArrayList<String> mTitle;
    private ITelephonyApi teleApi = CoreApi.getTelephonyApi();

    public NrTDDBandData(Context context, int phoneID) {
        super(context, phoneID);
        mNRBandData = NrBandData.getInstance(phoneID, context);
        mTitle = new ArrayList<String>();
    }

    public NrTDDBandData(Context context, int phoneID, PreferenceCategory pref) {
        super(context, phoneID, pref);
        mNRBandData = NrBandData.getInstance(phoneID, context);
    }

    public int[] getSupportBands() {

        if (mNRBandData.loadSelectedBand()) {
            return mNRBandData.getSupportBand_TDD();
        } else {
            return null;
        }
    }

    @Override
    protected boolean getSelectedBand() {
        ArrayList<String> title = new ArrayList<String>();
        ArrayList<String> key = new ArrayList<String>();
        ArrayList<Integer> checked = new ArrayList<Integer>();

        mNRBandData.loadSelectedBand();
        int[] supportband = mNRBandData.getSupportBand_TDD();
        int[] checkedband = mNRBandData.getCheckedBand_TDD();

        if (supportband == null) {
            return false;
        }

        /** UNISOC: Bug1381637 Adapt and modify according to the underlying NR support. @{*/
        NrBand nrBand = new NrBand();
        for ( int i = 0; i < nrBand.nrTddBandName.length; i++ ) {
            if (supportband[i] == 1) {
                title.add(nrBand.nrTddBandName[i]);
                key.add("nr"+nrBand.nrTddBandName[i]);
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
            if (mNRBandData.supportedBand.tddBands[i] == 1) {
                if (mSelecteds[n] == 1) {
                    newBand.tddBands[i] = 1;
                } else {
                    newBand.tddBands[i] = 0;
                }
                n++;
            } else {
                newBand.tddBands[i] = 0;
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
