
package com.sprd.engineermode.connectivity.wifi;

import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.TwoStatePreference;
import android.util.Log;
import android.widget.Toast;

import com.sprd.engineermode.R;

import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.SupplicantState;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;

import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.common.Const;
import com.unisoc.engineermode.core.exception.EmException;
import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;
import com.unisoc.engineermode.core.intf.IConnectivityApi;

public class WifiTestActivity extends PreferenceActivity implements
    Preference.OnPreferenceChangeListener {

    private static final String TAG = "WifiTestActivity";
    private static final String KEY_LNA_BYPASS = "lna_bypass";
    private static final String KEY_WIFI_SCAN_OFF = "wifi_scan_off";
    private static final String KEY_WIFI_ADAPTIVE = "wifi_adaptive";
    private static final String KEY_MAX_POWER = "max_power";

    private static final String KEY_BEAMFORMING = "beamforming_key";
    private static final String KEY_STBC_RX = "stbc_rx_key";

    private static final int INIT_TEST_STATUS = 1;
    private static final int DEINIT_TEST_STATUS = 2;
    private static final int SET_MAX_POWER = 3;
    private static final int UNSET_MAX_POWER = 4;
    private static final int SET_LNA_ON = 5;
    private static final int SET_LNA_OFF = 6;
    private static final int GET_LNA_STATUS = 7;

    private static final int DISABLED_NO_SLEEP = 0;
    private static final int ENABLED_NO_SLEEP = 8;
    private static final int SET_ADAPTIVE_ON = 9;
    private static final int SET_ADAPTIVE_OFF = 10;
    private static final int GET_ADAPTIVE_STATUS = 11;
    private static final int SET_SCAN_ON = 12;
    private static final int SET_SCAN_OFF = 13;
    private static final int GET_SCAN_OFF_STATUS = 14;
    private static final int SET_BEAMFORMING_ON = 15;
    private static final int SET_BEAMFORMING_OFF = 16;
    private static final int GET_BEAMFORMING_STATUS = 17;
    private static final int SET_STBC_RX_ON = 18;
    private static final int SET_STBC_RX_OFF = 19;
    private static final int GET_STBC_RX_STATUS = 20;

    private TwoStatePreference scanOffPref;
    private TwoStatePreference lnaPref;

    private TwoStatePreference maxPower;
    private TwoStatePreference wifiAdaptive;

    /* SPRD 1175930: add Beamforming and STBC RX @{ */
    private TwoStatePreference beamforming;
    private TwoStatePreference stbcRx;
    /* @} */

    private Handler mUiThread = new Handler();
    private WTHandler mWTHandler;

    private IConnectivityApi.IWifiEut wifiEut = CoreApi.getConnectivityApi().wifiEut();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_wifi_certification);
        lnaPref = (TwoStatePreference) findPreference(KEY_LNA_BYPASS);
        lnaPref.setOnPreferenceChangeListener(this);
        scanOffPref = (TwoStatePreference) findPreference(KEY_WIFI_SCAN_OFF);
        scanOffPref.setOnPreferenceChangeListener(this);
        maxPower = (TwoStatePreference) findPreference(KEY_MAX_POWER);
        maxPower.setOnPreferenceChangeListener(this);
        wifiAdaptive = (TwoStatePreference) findPreference(KEY_WIFI_ADAPTIVE);
        wifiAdaptive.setOnPreferenceChangeListener(this);
        if (Const.isMarlin()) {
            scanOffPref.setEnabled(true);
            wifiAdaptive.setEnabled(true);
            maxPower.setEnabled(true);
        } else {
            scanOffPref.setEnabled(false);
            wifiAdaptive.setEnabled(false);
            maxPower.setEnabled(false);
        }
        lnaPref.setEnabled(true);
        /* SPRD 1175930: add Beamforming and STBC RX @{ */
        beamforming = (TwoStatePreference) findPreference(KEY_BEAMFORMING);
        beamforming.setOnPreferenceChangeListener(this);
        stbcRx = (TwoStatePreference) findPreference(KEY_STBC_RX);
        stbcRx.setOnPreferenceChangeListener(this);
        if (!(Const.isMarlin3() || Const.isMarlin3Lite() || Const.isMarlin3E())) {
            getPreferenceScreen().removePreference(beamforming);
            getPreferenceScreen().removePreference(stbcRx);
        }
        /* @} */
        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mWTHandler = new WTHandler(ht.getLooper());
        Message initStatus = mWTHandler.obtainMessage(INIT_TEST_STATUS);
        mWTHandler.sendMessage(initStatus);

        if (!Const.isMarlin()) {
            final IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            registerReceiver(mReceiver, filter);
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                return;
            }
            Log.d(TAG, "wifi changed: " + intent.getAction());
            Message switchMsg = null;
            WifiManager mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
            SupplicantState state = wifiInfo.getSupplicantState();
            if (state == SupplicantState.COMPLETED) {
                switchMsg = mWTHandler.obtainMessage(ENABLED_NO_SLEEP);
                mWTHandler.sendMessage(switchMsg);
            } else {
                switchMsg = mWTHandler.obtainMessage(DISABLED_NO_SLEEP);
                mWTHandler.sendMessage(switchMsg);
            }
        }
    };

    @Override
    protected void onStart() {
        if (Const.isMarlin()) {
            if (lnaPref != null && lnaPref.isEnabled()) {
                Message getLNAStatus = mWTHandler.obtainMessage(GET_LNA_STATUS);
                mWTHandler.sendMessage(getLNAStatus);
            }
        } else {
            if (SystemPropertiesProxy.get("persist.sys.nosleep.enabled", "0").equals("0")) {
                lnaPref.setChecked(false);
            } else {
                lnaPref.setChecked(true);
            }
        }
        if (wifiAdaptive != null && wifiAdaptive.isEnabled()) {
            Message getAdaptiveStatus = mWTHandler.obtainMessage(GET_ADAPTIVE_STATUS);
            mWTHandler.sendMessage(getAdaptiveStatus);
        }
        if (scanOffPref != null && scanOffPref.isEnabled()) {
            Message getScanOffStatus = mWTHandler.obtainMessage(GET_SCAN_OFF_STATUS);
            mWTHandler.sendMessage(getScanOffStatus);
        }
        /* SPRD 1175930: add Beamforming and STBC RX @{ */
        if (beamforming != null && beamforming.isEnabled()) {
            Message getBeamformingStatus = mWTHandler.obtainMessage(GET_BEAMFORMING_STATUS);
            mWTHandler.sendMessage(getBeamformingStatus);
        }
        if (stbcRx != null && stbcRx.isEnabled()) {
            Message getStbcRxStatus = mWTHandler.obtainMessage(GET_STBC_RX_STATUS);
            mWTHandler.sendMessage(getStbcRxStatus);
        }
        /* @} */
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        if (!Const.isMarlin()) {
            unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        Message exitStatus = mWTHandler.obtainMessage(DEINIT_TEST_STATUS);
        mWTHandler.sendMessage(exitStatus);
    }

    private boolean isWifiOn() {
        WifiManager wifiManager = (WifiManager) WifiTestActivity.this.getSystemService(
            Context.WIFI_SERVICE);
        Log.d(TAG, "isWifiOn = " + wifiManager.isWifiEnabled());
        if (wifiManager.isWifiEnabled()) {
            return true;
        }
        return false;
    }

    private void showAlertDialog() {
        Log.d(TAG, " wifi close");
        AlertDialog alertDialog = new AlertDialog.Builder(this)
            .setTitle("Warning !")
            .setMessage("Please open wifi first!")
            .setPositiveButton(
                getString(R.string.alertdialog_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
        alertDialog.show();
        Log.d(TAG, "alertDialog.show");
    }


    @Override
    public boolean onPreferenceChange(Preference pref, Object objValue) {
        Message switchMsg;
        if (pref == maxPower) {
            if (maxPower.isChecked()) {
                Message unSetPower = mWTHandler.obtainMessage(UNSET_MAX_POWER);
                mWTHandler.sendMessage(unSetPower);
            } else {
                Message setPower = mWTHandler.obtainMessage(SET_MAX_POWER);
                mWTHandler.sendMessage(setPower);
            }
            return false;
        } else if (pref == lnaPref) {
            if (Const.isMarlin()) {
                if (lnaPref.isChecked()) {
                    Message setOff = mWTHandler.obtainMessage(SET_LNA_OFF);
                    mWTHandler.sendMessage(setOff);
                } else {
                    Message setOn = mWTHandler.obtainMessage(SET_LNA_ON);
                    mWTHandler.sendMessage(setOn);
                }
                return false;
            } else {
                if (!checkWifiStatus()) {
                    if (lnaPref.isChecked()) {
                        switchMsg = mWTHandler.obtainMessage(DISABLED_NO_SLEEP);
                    } else {
                        switchMsg = mWTHandler.obtainMessage(ENABLED_NO_SLEEP);
                    }
                    mWTHandler.sendMessage(switchMsg);
                } else {
                    Toast.makeText(WifiTestActivity.this, "Please open wifi", Toast.LENGTH_SHORT)
                        .show();
                }
            }
            return false;
        } else if (pref == wifiAdaptive) {
            if (isWifiOn()) {
                if (wifiAdaptive.isChecked()) {
                    Message setOff = mWTHandler.obtainMessage(SET_ADAPTIVE_OFF);
                    mWTHandler.sendMessage(setOff);
                } else {
                    Message setOn = mWTHandler.obtainMessage(SET_ADAPTIVE_ON);
                    mWTHandler.sendMessage(setOn);
                }
            } else {
                showAlertDialog();
            }
            return false;
        } else if (pref == scanOffPref) {
            if (isWifiOn()) {
                if (!scanOffPref.isChecked()) {
                    Message setOn = mWTHandler.obtainMessage(SET_SCAN_ON);
                    mWTHandler.sendMessage(setOn);
                } else {
                    Message setOff = mWTHandler.obtainMessage(SET_SCAN_OFF);
                    mWTHandler.sendMessage(setOff);
                }
            } else {
                showAlertDialog();
            }
            return false;
        } else if (pref == beamforming) {
            if (isWifiOn()) {
                if (beamforming.isChecked()) {
                    Message setBeamformingOff = mWTHandler.obtainMessage(SET_BEAMFORMING_OFF);
                    mWTHandler.sendMessage(setBeamformingOff);
                } else {
                    Message setBeamformingOn = mWTHandler.obtainMessage(SET_BEAMFORMING_ON);
                    mWTHandler.sendMessage(setBeamformingOn);
                }
            } else {
                showAlertDialog();
            }
            return false;
        } else if (pref == stbcRx) {
            if (isWifiOn()) {
                if (stbcRx.isChecked()) {
                    Message setStbcRxOff = mWTHandler.obtainMessage(SET_STBC_RX_OFF);
                    mWTHandler.sendMessage(setStbcRxOff);
                } else {
                    Message setStbcRxOn = mWTHandler.obtainMessage(SET_STBC_RX_ON);
                    mWTHandler.sendMessage(setStbcRxOn);
                }
            } else {
                showAlertDialog();
            }
            return false;
        }
        return false;
    }

    class WTHandler extends Handler {
        WTHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG,"handleMessage "+msg.what);

            switch (msg.what) {
                case INIT_TEST_STATUS:
                    if (isWifiOn()) {
                        Log.d(TAG, "wifi is on, so ignore insmodWifi and wifiStart");
                    } else {
                        if (wifiEut.up()) {
                            Log.d(TAG, "insmodWifi and wifiStart success");
                        } else {
                            Log.d(TAG, "insmodWifi and wifiStart failed");
                        }
                    }
                    break;
                case DEINIT_TEST_STATUS:
                    if (isWifiOn()) {
                        Log.d(TAG, "wifi is on, so ignore wifiStop and rmmodWifi");
                    } else {
                        if (wifiEut.down()) {
                            Log.d(TAG, "wifiStop and rmmodWifi success");
                        } else {
                            Log.d(TAG, "wifiStop and rmmodWifi failed");
                        }
                    }
                    finish();
                    break;
                case SET_MAX_POWER:
                    try {
                        wifiEut.setTxPower(1127);
                        wifiEut.setTxPower(127);
                    } catch (Exception e) {
                        Log.e(TAG, "set power failed");
                        mUiThread.post(() -> maxPower.setChecked(false));
                        break;
                    }
                    mUiThread.post(() -> maxPower.setChecked(true));
                    break;
                case UNSET_MAX_POWER:
                    try {
                        wifiEut.setTxPower(1088);
                        wifiEut.setTxPower(72);
                    } catch (EmException e) {
                        Log.e(TAG, "unset power failed");
                        mUiThread.post(() -> maxPower.setChecked(true));
                        break;
                    }
                    mUiThread.post(() -> maxPower.setChecked(false));
                    break;
                case SET_LNA_ON:
                    try {
                        wifiEut.enableSleepless();
                    } catch (EmException e) {
                        mUiThread.post(() -> lnaPref.setChecked(false));
                        break;
                    }

                    mUiThread.post(() -> lnaPref.setChecked(true));
                    break;
                case SET_LNA_OFF:
                    try {
                        wifiEut.disableSleepless();
                    } catch (EmException e) {
                        mUiThread.post(() -> lnaPref.setChecked(true));
                        break;
                    }

                    mUiThread.post(() -> lnaPref.setChecked(false));
                    break;
                case GET_LNA_STATUS:
                    boolean status;
                    try {
                        status = wifiEut.getSleeplessState();
                    } catch (EmException e) {
                        Log.e(TAG, "GET_LNA_STATUS Fail");
                        mUiThread.post(() -> lnaPref.setEnabled(false));
                        break;
                    }

                    Log.d(TAG, "get lna state:" + status);
                    mUiThread.post(() -> lnaPref.setChecked(status));
                    break;
//                case DISABLED_NO_SLEEP:
//                    wifiEut.disableSleepless();
//                    mUiThread.post(() -> lnaPref.setChecked(false));
//                    break;
//                case ENABLED_NO_SLEEP:
//                    wifiEut.enableSleepless();
//                    mUiThread.post(() -> lnaPref.setChecked(true));
//                    break;
                case SET_ADAPTIVE_ON:
                    try {
                        wifiEut.enableAdaptive();
                    } catch (EmException e) {
                        Log.e(TAG, "enable adaptive failed");
                        mUiThread.post(() -> wifiAdaptive.setChecked(false));
                        break;
                    }
                    mUiThread.post(() -> wifiAdaptive.setChecked(true));
                    break;
                case SET_ADAPTIVE_OFF:
                    try {
                        wifiEut.disableAdaptive();
                    } catch (EmException e) {
                        Log.e(TAG, "disable adaptive failed");
                        mUiThread.post(() -> wifiAdaptive.setChecked(true));
                        break;
                    }
                    mUiThread.post(() -> wifiAdaptive.setChecked(false));
                    break;
                case GET_ADAPTIVE_STATUS:
                    boolean adaptiveStatus;
                    try {
                        adaptiveStatus = wifiEut.getAdaptiveState();
                    } catch (EmException e) {
                        mUiThread.post(() -> wifiAdaptive.setChecked(false));
                        break;
                    }
                    Log.d(TAG, "get adaptive state: " + adaptiveStatus);
                    mUiThread.post(() -> wifiAdaptive.setChecked(adaptiveStatus));
                    break;
                case SET_SCAN_ON:
                    try {
                        wifiEut.enableScan();
                    } catch (EmException e) {
                        Log.e(TAG, "set scan failed");
                        mUiThread.post(() -> scanOffPref.setChecked(false));
                        break;
                    }

                    mUiThread.post(() -> scanOffPref.setChecked(true));
                    break;
                case SET_SCAN_OFF:
                    try {
                        wifiEut.disableScan();
                    } catch (EmException e) {
                        Log.e(TAG, "set scan failed");
                        mUiThread.post(() -> scanOffPref.setChecked(true));
                        break;
                    }

                    mUiThread.post(() -> scanOffPref.setChecked(false));
                    break;
                case GET_SCAN_OFF_STATUS:
                    boolean scanState;
                    try {
                        scanState = wifiEut.getScanState();
                    } catch (EmException e) {
                        Log.e(TAG, "get scan state fail");
                        mUiThread.post(() -> scanOffPref.setEnabled(false));
                        break;
                    }

                    Log.d(TAG, "get scan state: " + scanState);
                    mUiThread.post(() -> scanOffPref.setChecked(scanState));
                    break;
                /* SPRD 1175930: add Beamforming and STBC RX @{ */
                case GET_BEAMFORMING_STATUS:
                    boolean beamformingStatus;
                    try {
                        beamformingStatus = wifiEut.getBeamformingStatus();
                    } catch (EmException e) {
                        Log.e(TAG, "get beamforming status fail");
                        mUiThread.post(() -> beamforming.setEnabled(false));
                        break;
                    }
                    Log.d(TAG, "get beamforming status: " + beamformingStatus);
                    mUiThread.post(() -> beamforming.setChecked(beamformingStatus));
                    break;
                case SET_BEAMFORMING_ON:
                    try {
                        wifiEut.enableBeamforming();
                    } catch (EmException e) {
                        mUiThread.post(() -> beamforming.setChecked(false));
                        mUiThread.post(() -> {
                            beamforming.setChecked(false);
                            Toast.makeText(WifiTestActivity.this, "Set failed", Toast.LENGTH_SHORT).show();
                        });
                        break;
                    }
                    mUiThread.post(() -> beamforming.setChecked(true));
                    break;
                case SET_BEAMFORMING_OFF:
                    try {
                        wifiEut.disableBeamforming();
                    } catch (EmException e) {
                        mUiThread.post(() -> {
                            beamforming.setChecked(true);
                            Toast.makeText(WifiTestActivity.this, "Set failed", Toast.LENGTH_SHORT).show();
                        });
                        break;
                    }
                    mUiThread.post(() -> beamforming.setChecked(false));
                    break;
                case GET_STBC_RX_STATUS:
                    boolean stbcRxStatus;
                    try {
                        stbcRxStatus = wifiEut.getStbcRxStatus();
                    } catch (EmException e) {
                        mUiThread.post(() -> stbcRx.setEnabled(false));
                        break;
                    }
                    Log.d(TAG, "get stbc rx status: " + stbcRxStatus);
                    mUiThread.post(() -> stbcRx.setChecked(stbcRxStatus));
                    break;
                case SET_STBC_RX_ON:
                    try {
                        wifiEut.enableStbcRx();
                    } catch (EmException e) {
                        mUiThread.post(() -> {
                            stbcRx.setChecked(false);
                            Toast.makeText(WifiTestActivity.this, "Set failed", Toast.LENGTH_SHORT).show();
                        });
                        break;
                    }
                    mUiThread.post(() -> stbcRx.setChecked(true));
                    break;
                case SET_STBC_RX_OFF:
                    try {
                        wifiEut.disableStbcRx();
                    } catch (EmException e) {
                        mUiThread.post(() -> {
                            stbcRx.setChecked(true);
                            Toast.makeText(WifiTestActivity.this, "Set failed", Toast.LENGTH_SHORT).show();
                        });
                        break;
                    }
                    mUiThread.post(() -> stbcRx.setChecked(false));
                    break;
                    /* @} */
                default:
                    break;
            }
        }
    }

    private boolean checkWifiStatus() {
        WifiManager wifiManager = (WifiManager) this.getSystemService(WIFI_SERVICE);
        return !wifiManager.isWifiEnabled();
    }
}
