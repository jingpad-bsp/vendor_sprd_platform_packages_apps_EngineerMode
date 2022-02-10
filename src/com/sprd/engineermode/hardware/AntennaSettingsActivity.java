package com.sprd.engineermode.hardware;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.intf.IAntenna;
import com.unisoc.engineermode.core.intf.IAntenna.AntennaState;
import com.sprd.engineermode.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AntennaSettingsActivity extends PreferenceActivity implements
        OnPreferenceChangeListener {
    private static final String TAG = "AntennaSettingsActivity";
    private static final String KEY_LTE_SET = "lte_set";
    private static final String KEY_WCDMA_SET = "wcdma_set";
    private static final String KEY_GSM_SET = "gsm_set";
    private static final String KEY_C2K_SET = "c2k_set";
    private static final String KEY_NR_SET = "nr_set";

    private static final int GET_ANTENNA_STATE = 0;
    private static final int SET_ANTENNA = 1;

    private AnSetHandler mAnSetHandler;
    private Handler mUiThread = new Handler();

    private IAntenna antenna = CoreApi.getHardwareApi().antennaApi();
    private static final int DELAY_TIME_POWEROFF = 3000;
    private static final int DELAY_TIME_UPDATE = 2000;

    /**
     * all these arrays represent the ListPreferences' value to AntennaState
     * we set ListPreference's value to numbers based on 0, so it's corresponding to array's index
     */
    private AntennaState[] gsmListPrefValues = {
        AntennaState.PRIMARY_DIVERSITY,
        AntennaState.PRIMARY,
        AntennaState.DIVERSITY
    };

    private AntennaState[] wcdmaListPrefValues = {
        AntennaState.PRIMARY_DIVERSITY,
        AntennaState.PRIMARY,
        AntennaState.DIVERSITY
    };

    private AntennaState[] lteListPrefValues = {
        AntennaState.PRIMARY_DIVERSITY,
        AntennaState.PRIMARY,
        AntennaState.DIVERSITY
    };

    private AntennaState[] c2kListPrefValues = {
            AntennaState.PRIMARY_DIVERSITY,
            AntennaState.PRIMARY,
            AntennaState.DIVERSITY,
            AntennaState.PRIMARY_DIVERSITY_DYNAMIC
    };

    private AntennaState[] nrListPrefValues = {
        AntennaState.DEFAULT,
        AntennaState.ANT3_RX,
        AntennaState.ANT6_RX,
        AntennaState.ANT5_RX,
        AntennaState.ANT4_RX,
        AntennaState.ANT3_TX,
        AntennaState.ANT6_TX,
        AntennaState.ANT4_TX,
        AntennaState.ANT5_TX
    };

    private enum NetworkType {
        GSM(0),
        WCDMA(1),
        LTE(2),
        //C2K(3);
        NR(3);

        private int value;
        NetworkType(int value) {
            this.value = value;
        }

        int getValue() {
            return this.value;
        }

        static NetworkType of(String key) {
            switch(key) {
                case KEY_GSM_SET:
                    return GSM;
                case KEY_WCDMA_SET:
                    return WCDMA;
                case KEY_LTE_SET:
                    return LTE;
/*                case KEY_C2K_SET:
                    return C2K;*/
                case KEY_NR_SET:
                    return NR;
                default:
                    throw new RuntimeException("invalid key");
            }
        }

        static NetworkType of(int value) {
            for (NetworkType t : NetworkType.values()) {
               if (t.getValue() == value) {
                   return t;
               }
            }
            throw new RuntimeException("invalid value");
        }
    }

    private static class StatusData {
        NetworkType type;
        ListPreference pref;
        AntennaState[] listPrefValues;

        StatusData(NetworkType type, ListPreference pref, AntennaState[] statusValues) {
            this.type = type;
            this.pref = pref;
            this.listPrefValues = statusValues;
        }
    }

    private List<StatusData> statusData = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_antenna_set);
        ListPreference lteList = (ListPreference) findPreference(KEY_LTE_SET);
        ListPreference wcdmaList = (ListPreference) findPreference(KEY_WCDMA_SET);
        ListPreference gsmList = (ListPreference) findPreference(KEY_GSM_SET);
        //ListPreference c2kList = (ListPreference) findPreference(KEY_C2K_SET);
        ListPreference nrList = (ListPreference) findPreference(KEY_NR_SET);

        lteList.setOnPreferenceChangeListener(this);
        wcdmaList.setOnPreferenceChangeListener(this);
        gsmList.setOnPreferenceChangeListener(this);
        //c2kList.setOnPreferenceChangeListener(this); //bug 1196033, hide CDMA 2000 due to no owner
        nrList.setOnPreferenceChangeListener(this);

        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mAnSetHandler = new AnSetHandler(ht.getLooper());

        statusData.add(new StatusData(NetworkType.GSM, gsmList, gsmListPrefValues));
        statusData.add(new StatusData(NetworkType.WCDMA, wcdmaList, wcdmaListPrefValues));
        statusData.add(new StatusData(NetworkType.LTE, lteList, lteListPrefValues));
        //statusData.add(new StatusData(NetworkType.C2K, c2kList, c2kListPrefValues)); //bug 1196033, hide CDMA 2000 due to no owner
        statusData.add(new StatusData(NetworkType.NR, nrList, nrListPrefValues));
    }

    @Override
    public void onStart() {
        super.onStart();
        Message mGetAntennaState = mAnSetHandler.obtainMessage(GET_ANTENNA_STATE);
        mAnSetHandler.sendMessage(mGetAntennaState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAnSetHandler != null) {
            mAnSetHandler.getLooper().quit();
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final int value = Integer.parseInt(newValue.toString());

        Log.d(TAG, "onPreferenceChange = " + value);
        if (!(preference instanceof ListPreference)) {
            return false;
        }

        ListPreference listPreference = (ListPreference) preference;
        NetworkType type = NetworkType.of(listPreference.getKey());

        if (type == NetworkType.LTE) {
            createDialog(type, value);
            return false;
        } else if (type == NetworkType.NR) {
            createDialog(type, value);
            return false;
        }

        Message mSetAntenna = mAnSetHandler.obtainMessage(SET_ANTENNA, type.getValue(), value);
        mAnSetHandler.sendMessage(mSetAntenna);
        return false;
    }

    void createDialog(final NetworkType type, final int value) {
        AlertDialog alertDialog = new AlertDialog.Builder(AntennaSettingsActivity.this)
            .setTitle(getString(R.string.antenna_set))
            .setMessage(getString(R.string.mode_switch_waring))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.alertdialog_ok),
                (dialog, which) -> mAnSetHandler.sendMessage(
                    mAnSetHandler.obtainMessage(SET_ANTENNA,type.getValue(), value)))
            .setNegativeButton(R.string.alertdialog_cancel,
                (dialog, which) -> {})
            .create();
        alertDialog.show();
    }

    private static int indexOf(AntennaState state, AntennaState[] arr) {
        return Arrays.asList(arr).indexOf(state);
    }

    private void setPref(ListPreference pref, int index) {
        if (index == -1) {
            mUiThread.post(() -> {
                pref.setEnabled(false);
                pref.setSummary(R.string.feature_not_support);
            });
            return;
        }

        mUiThread.post(() -> {
            pref.setValueIndex(index);
            pref.setSummary(pref.getEntry());
        });
    }

    private AntennaState[] getStatesArr() {
        IAntenna.AllState currentStatus = new IAntenna.AllState();
        try {
            currentStatus = antenna.getAllStates();
        } catch (Exception e) {
            e.printStackTrace();
        }

        AntennaState[] states = new AntennaState[NetworkType.values().length];
        states[NetworkType.GSM.getValue()] = currentStatus.getGsm();
        states[NetworkType.WCDMA.getValue()] = currentStatus.getWcdma();
        states[NetworkType.LTE.getValue()] = currentStatus.getLte();
        //states[NetworkType.C2K.getValue()] = currentStatus.getC2k(); //bug 1196033, hide CDMA 2000 due to no owner
        states[NetworkType.NR.getValue()] = currentStatus.getNr();
        return states;
    }


    private void update() {
        AntennaState[] stateArr = getStatesArr();

        for (StatusData data : statusData) {
            setPref(data.pref, indexOf(stateArr[data.type.getValue()], data.listPrefValues));
        }
    }


    private StatusData getStatusData(NetworkType type) {
        StatusData data = statusData.stream().filter( d -> d.type == type ).findAny().orElse(null);
        if (data == null) {
            throw new RuntimeException("type is wrong");
        }
        return data;
    }

    private void set(NetworkType type, int value) {
        StatusData data = getStatusData(type);
        AntennaState state = data.listPrefValues[value];

        try {
            switch (type) {
                case GSM:
                    antenna.setGsm(state);
                    break;
                case WCDMA:
                    antenna.setWcdma(state);
                    break;
                case LTE:
                    antenna.setLte(state);
                    break;
                //case C2K:
                    //antenna.setC2k(state); //bug 1196033, hide CDMA 2000 due to no owner
                case NR:
                    antenna.setNr(state);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showFailToast();
            return;
        }

        mAnSetHandler.postDelayed(() -> {
            update();
        }, DELAY_TIME_UPDATE);

        if (type == NetworkType.LTE || type == NetworkType.NR) {
            powerOff();
        }
    }

    public void powerOff() {
        //wait 3S to take effect
        mAnSetHandler.postDelayed(() -> {
            PowerManager pm = (PowerManager) AntennaSettingsActivity.this
                .getSystemService(Context.POWER_SERVICE);
            pm.reboot(TAG);
        }, DELAY_TIME_POWEROFF);
    }


    class AnSetHandler extends Handler {
        AnSetHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_ANTENNA_STATE:
                    update();
                    break;
                case SET_ANTENNA:
                    set(NetworkType.of(msg.arg1), msg.arg2);
                    break;
                default:
                    break;
            }
        }
    }

    private void showFailToast() {
        mUiThread.post(() -> Toast.makeText(AntennaSettingsActivity.this, "Fail",
            Toast.LENGTH_SHORT).show());
    }
}
