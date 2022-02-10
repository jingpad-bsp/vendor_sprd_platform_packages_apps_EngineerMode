package com.sprd.engineermode.debuglog;

import android.preference.PreferenceActivity;
import android.preference.Preference;
import android.os.Bundle;
import android.util.Log;
import android.os.Build;
import com.sprd.engineermode.R;

import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.intf.IPhoneInfo;
import com.unisoc.engineermode.core.intf.IPhoneInfo.CdmaInfo;
import com.unisoc.engineermode.core.intf.IPhoneInfo.Operator;
import static com.unisoc.engineermode.core.intf.IDebugLogApiKt.INVALID_CELL_ID;


public class PhoneInfoActivity extends PreferenceActivity {
    private static final String TAG = "PhoneInfoActivity";

    private static final String TERMINATOR_VERSION_KEY = "terminator_version";
    private static final String SOFTWARE_VERSION_KEY = "software_number";
    private static final String HARDWARE_VERSION_KEY = "hardware_number";
    private static final String PRL_VERSION_KEY = "prl_version";
    private static final String SYSTEM_VERSION_KEY = "system_version";
    private static final String STORAGE_VOLUME_KEY = "storage_volume";
    private static final String MEMORY_VOLUME_KEY = "memory_volume";
    private static final String SID_NUMBER_KEY = "sid_number";
    private static final String NID_NUMBER_KEY = "nid_number";
    private static final String MEID_NUMBER_KEY = "meid_number";
    private static final String ESN_NUMBER_KEY = "esn_number";
    private static final String BASEID_NUMBER_KEY = "baseid_number";
    private static final String IMEI_NUMBER_KEY = "imei_number";
    private static final String CELLID_NUMBER_KEY = "cellid_sim";
    private static final String MCC_NUMBER_KEY = "mcc_sim";
    private static final String MNC_NUMBER_KEY = "mnc_sim";
    private static final String IMSI_NUMBER_KEY = "imsi_sim";
    private static final String CDMA_IMSI_KEY = "cdma_imsi";
    private static final String ICC_ID_KEY = "icc_id";

    private IPhoneInfo phoneInfoApi = CoreApi.getDebugLogApi().phoneInfoApi();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        addPreferencesFromResource(R.xml.pref_phone_information_show);
    }

    private void setMccMnc() {
        int index = 1;
        Preference pref;

        for (Operator mccMnc : phoneInfoApi.getAllOpers()) {
            pref = findPreference(MCC_NUMBER_KEY + index);
            pref.setSummary(mccMnc.getMcc());

            pref = findPreference(MNC_NUMBER_KEY + index);
            pref.setSummary(mccMnc.getMnc());
            index++;
        }
    }

    private void setImsi() {
        int index = 1;
        Preference pref;

        for (String imsi : phoneInfoApi.getAllImsi()) {
            pref = findPreference(IMSI_NUMBER_KEY + index);
            pref.setSummary(imsi);
            index++;
        }
    }

    private void setImei() {
        int index = 1;
        Preference pref;

        for (String imei : phoneInfoApi.getAllImei()) {
            pref = findPreference(IMEI_NUMBER_KEY + index);
            pref.setSummary(imei);
            index++;
        }
    }

    private void setCdmaImsi() {
        int index = 1;
        Preference pref;

        for (String imei : phoneInfoApi.getCdmaImsi()) {
            pref = findPreference(CDMA_IMSI_KEY + index);
            pref.setSummary(imei);
            index++;
        }
    }

    private void setIccId() {
        int index = 1;
        Preference pref;

        for (String imei : phoneInfoApi.getIccId()) {
            pref = findPreference(ICC_ID_KEY + index);
            pref.setSummary(imei);
            index++;
        }
    }

    private void setCellId() {
        int index = 1;
        Preference pref;

        for (int id : phoneInfoApi.getCellId()) {
            if (id != INVALID_CELL_ID) {
                pref = findPreference(CELLID_NUMBER_KEY + index);
                pref.setSummary(String.valueOf(id));
            }
            index++;
        }
    }

    private void setCdmaInfo() {
        Preference pref;
        for (CdmaInfo ci : phoneInfoApi.getCdmaInfo()) {
            if (ci.getSystemId() != INVALID_CELL_ID) {
                pref = findPreference(PRL_VERSION_KEY);
                pref.setSummary(ci.getPrl());
                pref = findPreference(SID_NUMBER_KEY);
                pref.setSummary(String.valueOf(ci.getSystemId()));
                pref = findPreference(NID_NUMBER_KEY);
                pref.setSummary(String.valueOf(ci.getNetworkId()));
                pref = findPreference(BASEID_NUMBER_KEY);
                pref.setSummary(String.valueOf(ci.getBaseStationId()));
            }
        }
    }

    private void setEsn() {
        Preference pref = findPreference(ESN_NUMBER_KEY);
        pref.setSummary(phoneInfoApi.getEsn());
    }

    private void setMeid() {
        Preference pref = findPreference(MEID_NUMBER_KEY);
        pref.setSummary(phoneInfoApi.getMeid());
    }

    private void setMemory() {
        Preference pref = findPreference(MEMORY_VOLUME_KEY);
        pref.setSummary(phoneInfoApi.getMemorySize());
    }

    private void setStorage() {
        Preference pref = findPreference(STORAGE_VOLUME_KEY);
        pref.setSummary(phoneInfoApi.getInternalStorageSize());
    }

    private void setVersion() {
        Preference pref = findPreference(SOFTWARE_VERSION_KEY);
        pref.setSummary(phoneInfoApi.getSoftwareVersion());
        pref = findPreference(HARDWARE_VERSION_KEY);
        pref.setSummary(phoneInfoApi.getHardwareVersion());
        pref = findPreference(SYSTEM_VERSION_KEY);
        pref.setSummary(phoneInfoApi.getOsVersion());
        pref = findPreference(TERMINATOR_VERSION_KEY);
        pref.setSummary(Build.MODEL);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        setMccMnc();
        setImsi();
        setCellId();
        setCdmaInfo();
        setEsn();
        setMeid();
        setImei();
        setMemory();
        setStorage();
        setVersion();
        setCdmaImsi();
        setIccId();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }
}
