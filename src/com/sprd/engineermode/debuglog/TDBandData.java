package com.sprd.engineermode.debuglog;

import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.intf.ITelephonyApi.IBand.TdBand;

import android.content.Context;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceCategory;
import android.util.Log;

public class TDBandData extends BandData {
    private static final String TAG = "TDBandData";
    private ITelephonyApi teleApi = CoreApi.getTelephonyApi();
    private TdBand band = null;

    public TDBandData(Context context, int phoneID, PreferenceCategory pref) {
        super(context, phoneID, pref);
        mBandTitle = new String[] {"TD_SCDMA A Band", "TD_SCDMA F Band"};
        mBandFrequency = new String[] {"td_a_frequency", "td_f_frequency"};
        mSelecteds = new int[] {0, 0};
        mPrefCheckBox = new CheckBoxPreference[mBandTitle.length];
    }
    public TDBandData(Context context, int phoneID) {
        super(context, phoneID);
        mBandTitle = new String[] {"TD_SCDMA A Band", "TD_SCDMA F Band"};
        mBandFrequency = new String[] {"td_a_frequency", "td_f_frequency"};
        mSelecteds = new int[] {0, 0};
    }


    @Override
    protected boolean getSelectedBand() {
        /*
        String resp = IATUtils.sendATCmd(engconstents.ENG_AT_TD_LOCKED_BAND, mChannel);
        if (resp == null) {
            return false;
        }
        if (!resp.contains(IATUtils.AT_OK)) {
            return false;
        }
        String str[] = resp.split(":|\n|,");
        Log.d(TAG, "str:"+Arrays.toString(str));
        String bandstr = str[1].trim();
        if ( bandstr.startsWith("A ") ) {
            mSelecteds[0] = 1;
            mSelecteds[1] = 0;
        } else if (bandstr.startsWith("F ")) {
            mSelecteds[0] = 0;
            mSelecteds[1] = 1;
        } else if (bandstr.startsWith("A+F ")) {
            mSelecteds[0] = 1;
            mSelecteds[1] = 1;
        }
        return true;
        */
        TdBand selectedBands = getBands();
        /*
        try {
            selectedBands = teleApi.band().getTdBand(mPhoneID);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        */

        if (selectedBands == null) {
            return false;
        }

        if (selectedBands.band_a) {
            mSelecteds[0] = 1;
        }
        if (selectedBands.band_f) {
            mSelecteds[1] = 1;
        }
        band = selectedBands;
        return true;
    }

    private TdBand getBands() {
        TdBand band;
        try {
            band = teleApi.band().getTdBand(mPhoneID);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return band;
    }

    /*
    protected String[] getSelectATCmd() {
        // e.g.
        // 01-05 07:12:00.206 13443 13541 D IATUtils: <0> mAtChannel = com.sprd.internal.telephony.IAtChannel$Stub$Proxy@41e1ea78 , and cmd = AT+SPLOCKBAND="A"
        // 01-05 07:12:00.236 13443 13541 D IATUtils: <0> AT response OK
        String para = "";
        if ( mSelecteds[0] == 1 && mSelecteds[1] == 1) {
            para = "\"A+F\"";
        } else if ( mSelecteds[0] == 1 ) {
            para = "\"A\"";
        } else if ( mSelecteds[1] == 1 ) {
            para = "\"F\"";
        }
        return new String[] {engconstents.ENG_AT_TD_SET_BAND+para};
    }
    */

    public TdBand getSupportBands() {
        /*
        StringBuffer bands = new StringBuffer();
        for(int i = 0; i < mSelecteds.length; i++) {
            if(mSelecteds[i] == 1) {
                bands.append(mBandTitle[i]+"\n");
            }
        }
        return bands.toString();
        */
        if (band != null) {
            return band;
        }

        if (getSelectedBand()) {
            return band;
        } else {
            return null;
        }

    }

    @Override
    public boolean setSelectedBandToModem() {
        TdBand band = new TdBand();
        if (mSelecteds[0] == 1) {
            band.band_a = true;
        }

        if (mSelecteds[1] == 1) {
            band.band_f = true;
        }

        Log.d(TAG, "setSelectedBandToModem");
        try {
            teleApi.band().setTdBand(mPhoneID, band);
        } catch (Exception e) {
            e.printStackTrace();
            mSetSuccessful = false;
            return false;
        }

        mSetSuccessful = true;
        return true;
    }

    /*
    @Override
    protected boolean initSupportBand() {
        String resp = IATUtils.sendATCmd("AT+SPLOCKBAND?", mChannel);
        if (resp == null) {
            return false;
        }
        if (!resp.contains(IATUtils.AT_OK)) {
            return false;
        }
        String str[] = resp.split(":|\n|,");
        Log.d(TAG, "str:"+Arrays.toString(str));
        String bandstr = str[1].trim();
        if ( bandstr.startsWith("A ") ) {
            mSelecteds[0] = 1;
            mSelecteds[1] = 0;
        } else if (bandstr.startsWith("F ")) {
            mSelecteds[0] = 0;
            mSelecteds[1] = 1;
        } else if (bandstr.startsWith("A+F ")) {
            mSelecteds[0] = 1;
            mSelecteds[1] = 1;
        }
        return true;
    }
    */
}
