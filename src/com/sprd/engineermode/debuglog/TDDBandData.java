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

public class TDDBandData extends BandData {
    private static final String TAG = "TDDBandData";
    LTEBandData mLTEBandData;
    ArrayList<String> mTitle;
    private ITelephonyApi teleApi = CoreApi.getTelephonyApi();

    public TDDBandData(Context context, int phoneID) {
        super(context, phoneID);
        mLTEBandData = LTEBandData.getInstance(phoneID, context);
        mTitle = new ArrayList<String>();
    }

    public TDDBandData(Context context, int phoneID, PreferenceCategory pref) {
        super(context, phoneID, pref);
        mLTEBandData = LTEBandData.getInstance(phoneID, context);
    }

    /*
    @Override
    protected boolean initSupportBand() {
        mLTEBandData.loadSupportBand();
        //mLTEBandData.loadSelectedBand();
        int supportband = mLTEBandData.getSupportBand_TDD();
        for ( int i = 0; i < 32; i++ ) {
            if (((supportband >> i) & 0x01) == 1) {
                Log.d(TAG, "TDDBand="+(i+33));
                mTitle.add(LTEBandData.KEY_TDD_LTE + (i+33));
            }
        }
        return true;
    }
    */


    public int[] getSupportBands() {
        /*
        StringBuffer bands = new StringBuffer();
        for(int i = 0; i < mTitle.size(); i++) {
            bands.append(mTitle.get(i)+"\n");
        }
        return bands.toString();
        */
        if (mLTEBandData.loadSelectedBand()) {
            return mLTEBandData.getSupportBand_TDD();
        } else {
            return null;
        }
    }


    @Override
    protected boolean getSelectedBand() {
        ArrayList<String> title = new ArrayList<String>();
        ArrayList<String> key = new ArrayList<String>();
        ArrayList<Integer> checked = new ArrayList<Integer>();

        mLTEBandData.loadSelectedBand();
        int[] supportband = mLTEBandData.getSupportBand_TDD();
        int[] checkedband = mLTEBandData.getCheckedBand_TDD();

        for ( int i = 0; i < 32; i++ ) {
            if (supportband[i] == 1) {
                title.add(LTEBandData.KEY_TDD_LTE + (i+33));
                key.add(LTEBandData.KEY_TDD_LTE + (i+33));
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
        return mLTEBandData.getSelectATCmd(BandSelector.RADIO_MODE_TD_LTE, mSelecteds);
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
            if (mLTEBandData.supportedBand.tddBands[i] == 1) {
                if (mSelecteds[n] == 1) {
                    newBand.tddBands[i] = 1;
                } else {
                    newBand.tddBands[i] = 0;
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
