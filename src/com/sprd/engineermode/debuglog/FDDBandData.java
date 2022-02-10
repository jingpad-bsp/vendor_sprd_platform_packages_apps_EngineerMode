package com.sprd.engineermode.debuglog;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceCategory;
import android.util.Log;

import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.intf.ITelephonyApi.IBand.LteBand;

public class FDDBandData extends BandData {
    private static final String TAG = "FDDBandData";
    LTEBandData mLTEBandData;
    private ITelephonyApi teleApi = CoreApi.getTelephonyApi();
    ArrayList<String> mTitle;

    public FDDBandData(Context context, int phoneID) {
        super(context, phoneID);
        mLTEBandData = LTEBandData.getInstance(phoneID, context);
        mTitle = new ArrayList<String>();
    }
    public FDDBandData(Context context, int phoneID, PreferenceCategory pref) {
        super(context, phoneID, pref);
        mLTEBandData = LTEBandData.getInstance(phoneID, context);
    }

    /*
    @Override
    protected boolean initSupportBand() {
        mLTEBandData.loadSupportBand();
        int supportband = mLTEBandData.getSupportBand_FDD();
        for ( int i = 0; i < 32; i++ ) {
            if (((supportband >> i) & 0x01) == 1) {
                Log.d(TAG, "FDDBand="+(i+1));
                mTitle.add(LTEBandData.KEY_FDD_LTE + (i+1));
            }
        }
        return true;
    }
    */


    @Override
    protected boolean getSelectedBand() {
        ArrayList<String> title = new ArrayList<String>();
        ArrayList<String> key = new ArrayList<String>();
        ArrayList<Integer> checked = new ArrayList<Integer>();

        mLTEBandData.loadSelectedBand();
        int[] supportband = mLTEBandData.getSupportBand_FDD();
        int[] checkedband = mLTEBandData.getCheckedBand_FDD();

        for ( int i = 0; i < 32; i++ ) {
            if (supportband[i] == 1) {
                title.add(LTEBandData.KEY_FDD_LTE + (i+1));
                key.add(LTEBandData.KEY_FDD_LTE + (i+1));
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

    /*
    protected String[] getSelectATCmd() {
        return mLTEBandData.getSelectATCmd(BandSelector.RADIO_MODE_LTE_FDD, mSelecteds);
    }
    */

    @Override
    public boolean setSelectedBandToModem() {

        mLTEBandData.loadSelectedBand();
        LteBand newBand = new LteBand();
        newBand.tddBands = Arrays.copyOf(mLTEBandData.getCheckedBand_TDD(), 32);
        newBand.fddBands = Arrays.copyOf(mLTEBandData.getCheckedBand_FDD(), 32);

        int n = 0;
        for ( int i = 0; i < 32; i++ ) {
            if (mLTEBandData.supportedBand.fddBands[i] == 1) {
                if (mSelecteds[n] == 1) {
                    newBand.fddBands[i] = 1;
                } else {
                    newBand.fddBands[i] = 0;
                }
                n++;
            }
        }

        Log.d(TAG, "setSelectedBandToModem");
        try {
            teleApi.band().setLteBand(mPhoneID, newBand);
        } catch (Exception e) {
            e.printStackTrace();
            mSetSuccessful = false;
            return false;
        }

        mSetSuccessful = true;
        return true;
    }
}
