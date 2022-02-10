
package com.sprd.engineermode.connectivity.BT;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.LocalSocketAddress;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.Preference;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.TwoStatePreference;
import android.util.Log;
import android.widget.Toast;
import android.provider.Settings;
import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.common.Const;
import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;
import com.unisoc.engineermode.core.impl.nonpublic.SettingsProxy;
import com.unisoc.engineermode.core.intf.IConnectivityApi;
import com.unisoc.engineermode.core.utils.SocketUtils;

public class BTActivity extends PreferenceActivity implements OnPreferenceChangeListener,
        Preference.OnPreferenceClickListener {

    private static final String TAG = "BTActivity";

    private static final String KEY_BT_RF_PATH = "bt_rf_path";
    private static final String KEY_BT_EUT = "bt_eut";
    private static final String KEY_BT_NOSIGNAL_TEST = "bt_nosignal_test";
    private static final String KEY_BQB_MODE = "bqb_mode";
    private static final String KEY_NO_SSP = "no_ssp";
    private static final String KEY_CONTROLLER_BQB = "controller_bqb_mode";
    // private static final String CMD_BT_ON = "eng bt bt_on";
    // private static final String CMD_BT_OFF = "eng bt bt_off";
    private static final String CMD_BT_EUT_OPEN = "eng bt dut_mode_configure 1";
    private static final String CMD_BT_EUT_CLOSE = "eng bt dut_mode_configure 0";
    private static final String CMD_BT_EUT_STATUS = "eng bt eut_status";
    private static final String CMD_BT_SINGLE_PATH= "eng bt set_rf_path 1";
    private static final String CMD_BT_SHARED_PATH = "eng bt set_rf_path 2";

    private static final String PERSIST_BT_NO_SSP = "persist.sys.bt.non.ssp";
    private static final String PERSIST_BT_RF_PATH = "persist.sys.bt.rf.path";
    private static final String SOCKET_NAME = "hidl_common_socket";
    private static final String PROCESS_NAME = "wcnd_eng ";

    private static final int MSG_BT_OFF = 0;
    private static final int MSG_BT_ON = 1;
    private static final int MSG_GET_EUT_STATUS = 2;
    private static final int MSG_SET_EUT_ON = 3;
    private static final int MSG_SET_EUT_OFF = 4;
    private static final int MSG_SET_BQB_ON = 5;
    private static final int MSG_SET_BQB_OFF = 6;
    private static final int MSG_GET_RF_PATH = 7;
    private static final int MSG_SET_SINGLE_PATH = 8;
    private static final int MSG_SET_SHARED_PATH = 9;
    private static final int MESSAGE_CONTROLLER_BQB_ONOFF = 10;
    private static final int MESSAGE_CONTROLLER_BQB_TRIGGER = 11;

    private static final String CONTROLLER_BQB_ENABLED = "controller bqb enabled";
    private static final String CONTROLLER_BQB_DISABLED = "controller bqb disabled";

    private static final boolean isSprdBoard = SystemPropertiesProxy.getBoolean("ro.modem.wcn.enable",false)
                                            || SystemPropertiesProxy.getBoolean("ro.vendor.modem.wcn.enable",false);

    private Preference mBTNosignalTest;
    private TwoStatePreference mBtRfPath;
    private TwoStatePreference mBtEUT;
    private TwoStatePreference mSsp;
    private Preference mControllerBqb;

    private BluetoothAdapter mAdapter;

    private static final String PROPERTY_HCITOOLS_SOCKET = "bluetooth.hcitools.socket";
    private static final String PROPERTY_HCITOOLS_SERVER = "bluetooth.hcitools.server";

    public static SharedPreferences.Editor mRfPathEditor;
    public static SharedPreferences mRfPathPref;

    private Handler mUIThread = new Handler();
    private BTHandler mBtHandler;

    private String mCmdRes;
    private boolean mControllerBqbState = false;

    private IConnectivityApi.IBtEut btEut = CoreApi.getConnectivityApi().btEut();
    private IConnectivityApi.IWcndEngControl wcndEngCtl = CoreApi.getConnectivityApi().wcndEngControl();
    /**
     * this adapted to bcm and sprd ro.modem.wcn.enable:1 sprd
     * ro.modem.wcn.enable:0 bcm and other not sprd CR:383499
     */
    // private boolean mIsSprd = SystemPropertiesProxy.get("ro.modem.wcn.enable",
    // "0").equals("1");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.pref_bt);

        mRfPathPref= getSharedPreferences("rf_path_pref", Context.MODE_PRIVATE);
        mRfPathEditor = mRfPathPref.edit();
        mBtRfPath = (TwoStatePreference) this.findPreference(KEY_BT_RF_PATH);
        mBtRfPath.setOnPreferenceChangeListener(this);
        if (!(Const.isMarlin3() || Const.isMarlin3E())) {
            getPreferenceScreen().removePreference(mBtRfPath);
        }
        mBtEUT = (TwoStatePreference) this.findPreference(KEY_BT_EUT);
        mBtEUT.setOnPreferenceChangeListener(this);
        if (SystemPropertiesProxy.get("ro.build.display.id", "unknown").contains("9630")) {
            mBtEUT.setEnabled(false);
            mBtEUT.setSummary(R.string.feature_not_support);
        }

        mControllerBqb = (Preference) this.findPreference(KEY_CONTROLLER_BQB);
        mControllerBqb.setOnPreferenceClickListener(this);

        mSsp = (TwoStatePreference) this.findPreference(KEY_NO_SSP);
        mSsp.setOnPreferenceChangeListener(this);
        mBTNosignalTest = (Preference) this.findPreference(KEY_BT_NOSIGNAL_TEST);
        mBTNosignalTest.setOnPreferenceClickListener(this);

        if (!isSprdBoard) {
            mSsp.setEnabled(false);
            mBTNosignalTest.setEnabled(false);
        }

        mAdapter = BluetoothAdapter.getDefaultAdapter();

        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mBtHandler = new BTHandler(ht.getLooper());
    }

    @Override
    protected void onStart() {
        if (mSsp != null && mSsp.isEnabled()) {
            String sspStatus = SystemPropertiesProxy.get(PERSIST_BT_NO_SSP, "");
            Log.d(TAG, "sspStatus is " + sspStatus);
            if ("open".equals(sspStatus)) {
                mSsp.setChecked(true);
            }
            if ("close".equals(sspStatus)) {
                mSsp.setChecked(false);
            }
        }

        if (mBtRfPath != null && mBtRfPath.isEnabled()) {
            Message get_rf_path = mBtHandler.obtainMessage(MSG_GET_RF_PATH);
            mBtHandler.sendMessage(get_rf_path);
        }
        if (mAdapter.isEnabled()) {
            mControllerBqb.setEnabled(false);
            if (SystemPropertiesProxy.get(PROPERTY_HCITOOLS_SERVER, "stopped").equals("stopped")) {
                Log.e(TAG, "HCI SERVER did not start");
            } else {
                int socket_port =  Integer.parseInt(SystemPropertiesProxy.get(PROPERTY_HCITOOLS_SOCKET, "0"));
                Log.d(TAG, PROPERTY_HCITOOLS_SOCKET + ": " + socket_port);
                if (socket_port == 0) {
                    Log.e(TAG, "unknow socket");
                }
            }
        } else {
            mControllerBqb.setEnabled(true);
            Message msg = mBtHandler.obtainMessage(MESSAGE_CONTROLLER_BQB_TRIGGER);
            mBtHandler.sendMessage(msg);
        }
        try {
            if (!wcndEngCtl.isWcndEngRunning()) {
                Log.d(TAG, "startWcndEng");
                wcndEngCtl.startWcndEng();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mBtHandler.postDelayed(runnable, 500);
        super.onStart();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (wcndEngCtl.isWcndEngRunning()) {
                    Log.d(TAG, "isWcndEngRunning INIT_TEST_STATUS");
                    if (mBtEUT != null && mBtEUT.isEnabled()) {
                        Message get_bt_eut_status = mBtHandler.obtainMessage(MSG_GET_EUT_STATUS);
                        mBtHandler.sendMessage(get_bt_eut_status);
                    }
                } else {
                    mBtHandler.postDelayed(this, 500);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onDestroy() {
        mBtHandler.removeCallbacks(runnable);
        try {
            if (wcndEngCtl.isWcndEngRunning()) {
                Log.d(TAG, "onDestroy isWcndEngRunning");
                wcndEngCtl.stopWcndEng();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public boolean onPreferenceChange(Preference pref, Object value) {
        if (pref.getKey().equals(KEY_NO_SSP)) {
            if (mSsp.isChecked()) {
                SystemPropertiesProxy.set(PERSIST_BT_NO_SSP, "close");
                mSsp.setChecked(false);
            } else {
                SystemPropertiesProxy.set(PERSIST_BT_NO_SSP, "open");
                mSsp.setChecked(true);
            }
        } else if (pref.getKey().equals(KEY_BT_EUT)) {
            /* SPRD Bug 921791: Switch off bt location scan before Opening bt eut. @{ */
            boolean isBleScanOpen = Settings.Global.getInt(getContentResolver(), SettingsProxy.Global.BLE_SCAN_ALWAYS_AVAILABLE, 0) == 1;
            if (isBleScanOpen) {
                Toast.makeText(this, getString(R.string.operate_bt_eut_hint), Toast.LENGTH_LONG).show();
                return false;
            }
            /* @} */
            mBtEUT.setEnabled(false);
            if (mBtEUT.isChecked()) {
                Message close_eut = mBtHandler.obtainMessage(MSG_SET_EUT_OFF);
                mBtHandler.sendMessage(close_eut);
            } else {
                Message close_eut = mBtHandler.obtainMessage(MSG_SET_EUT_ON);
                mBtHandler.sendMessage(close_eut);
            }
        }  else if (pref.getKey().equals(KEY_BT_RF_PATH)) {
            mBtRfPath.setEnabled(false);
            if (mBtRfPath.isChecked()) {
                Message single_path = mBtHandler.obtainMessage(MSG_SET_SINGLE_PATH);
                mBtHandler.sendMessage(single_path);
            } else {
                Message shared_path = mBtHandler.obtainMessage(MSG_SET_SHARED_PATH);
                mBtHandler.sendMessage(shared_path);
            }
        }
        return false;
    }

    @Override
    public boolean onPreferenceClick(Preference pref) {
        if (pref.getKey().equals(KEY_BT_NOSIGNAL_TEST)) {
            if (mBtEUT.isChecked()) {
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.alert_bt_test))
                        .setMessage(getString(R.string.alert_close_bt_eut))
                        .setPositiveButton(getString(R.string.alertdialog_ok),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).create();
                alertDialog.show();
            } else {
                Intent intent = new Intent(this,
                        com.sprd.engineermode.connectivity.BT.BTNOSIGNALActivity.class);
                startActivity(intent);
            }
        } else if (pref.getKey().equals(KEY_CONTROLLER_BQB)) {
            Log.d(TAG, "Controller BQB State Changed " + mControllerBqbState + " -> " + !mControllerBqbState);
            Message msg = mBtHandler.obtainMessage(MESSAGE_CONTROLLER_BQB_ONOFF);
            mBtHandler.sendMessage(msg);
        }
        return false;
    }

    class BTHandler extends Handler {

        public BTHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_GET_EUT_STATUS:
                    if (sendCmd(SOCKET_NAME, CMD_BT_EUT_STATUS, MSG_GET_EUT_STATUS)) {
                        String[] str = mCmdRes.split("\\:");
                        String status = str[1].trim();
                        if (status.equals("1")) {
                            mUIThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    mBtEUT.setChecked(true);
                                }
                            });
                        } else {
                            mUIThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    mBtEUT.setChecked(false);
                                }
                            });
                        }
                    } else {
                        Log.d(TAG, "GET_BT_EUT_STATUS Fail");
                        Toast.makeText(BTActivity.this, "GET_BT_EUT_STATUS Fail",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case MSG_SET_EUT_ON:
                    /* SPRD Bug 850928: BT EUT and RF Path. @{ */
                    // SPRD Bug 859986: Only for Marlin3 product.
                    if (Const.isMarlin3() || Const.isMarlin3E()) {
                        int pathState = mRfPathPref.getInt("rf_path_state", 1);
                        boolean isSuccess = false;
                        if (sendCmd(SOCKET_NAME, CMD_BT_EUT_OPEN, MSG_SET_EUT_ON)) {
                            if (pathState == 2) {
                                if (sendCmd(SOCKET_NAME, CMD_BT_SHARED_PATH, MSG_SET_SHARED_PATH)) {
                                    isSuccess = true;
                                } else {
                                    isSuccess = false;
                                }
                            } else {
                                if (sendCmd(SOCKET_NAME, CMD_BT_SINGLE_PATH, MSG_SET_SINGLE_PATH)) {
                                    isSuccess = true;
                                } else {
                                    isSuccess = false;
                                }
                            }
                        } else {
                            isSuccess = false;
                        }
                        if (isSuccess) {
                            mUIThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    mBtEUT.setEnabled(true);
                                    mBtEUT.setChecked(true);
                                }
                            });
                        } else {
                            mUIThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    mBtEUT.setEnabled(true);
                                    mBtEUT.setChecked(false);
                                }
                            });
                        }
                    } else {
                        if (sendCmd(SOCKET_NAME, CMD_BT_EUT_OPEN, MSG_SET_EUT_ON)) {
                            mUIThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    mBtEUT.setEnabled(true);
                                    mBtEUT.setChecked(true);
                                }
                            });
                        } else {
                            mUIThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    mBtEUT.setEnabled(true);
                                    mBtEUT.setChecked(false);
                                }
                            });
                        }
                    }
                    /* @} */
                    break;
                case MSG_SET_EUT_OFF:
                    if (sendCmd(SOCKET_NAME, CMD_BT_EUT_CLOSE, MSG_SET_EUT_OFF)) {
                        mUIThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mBtEUT.setEnabled(true);
                                mBtEUT.setChecked(false);
                            }
                        });
                    } else {
                        mUIThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mBtEUT.setEnabled(true);
                                mBtEUT.setChecked(true);
                            }
                        });
                    }
                    break;
                case MSG_SET_BQB_ON:
                    break;
                case MSG_SET_BQB_OFF:
                    break;
                case MSG_GET_RF_PATH:
                    int mRfPathState = mRfPathPref.getInt("rf_path_state", 1);
                        if (mRfPathState == 2) {
                            mUIThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    mBtRfPath.setChecked(true);
                                    if (Const.isMarlin3() || Const.isMarlin3E()) {
                                        SystemPropertiesProxy.set(PERSIST_BT_RF_PATH, "2");
                                    }
                                }
                            });
                        } else {
                            mUIThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    mBtRfPath.setChecked(false);
                                    if (Const.isMarlin3() || Const.isMarlin3E()) {
                                        SystemPropertiesProxy.set(PERSIST_BT_RF_PATH, "1");
                                    }
                                }
                            });
                        }
                    break;
                /* SPRD Bug 850928: BT EUT and RF Path. @{ */
                case MSG_SET_SINGLE_PATH:
                    if (Const.isMarlin3() || Const.isMarlin3E()) {
                        mBtRfPath.setEnabled(true);
                        mBtRfPath.setChecked(false);
                        mRfPathEditor.putInt("rf_path_state", 1);
                        mRfPathEditor.apply();
                        SystemPropertiesProxy.set(PERSIST_BT_RF_PATH, "1");
                        Log.d(TAG, "**mBtRfPath enabled true checked false 1**");
                    } else {
                        if (sendCmd(SOCKET_NAME, CMD_BT_SINGLE_PATH, MSG_SET_SINGLE_PATH)) {
                            mUIThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    mBtRfPath.setEnabled(true);
                                    mBtRfPath.setChecked(false);
                                }
                            });
                            mRfPathEditor.putInt("rf_path_state", 1);
                            mRfPathEditor.apply();
                        } else {
                            mUIThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    mBtRfPath.setEnabled(true);
                                    mBtRfPath.setChecked(true);
                                }
                            });
                        }
                    }
                    break;
                case MSG_SET_SHARED_PATH:
                    if (Const.isMarlin3() || Const.isMarlin3E()) {
                        mBtRfPath.setEnabled(true);
                        mBtRfPath.setChecked(true);
                        mRfPathEditor.putInt("rf_path_state", 2);
                        mRfPathEditor.apply();
                        SystemPropertiesProxy.set(PERSIST_BT_RF_PATH, "2");
                        Log.d(TAG, "**mBtRfPath enabled true checked true 2**");
                    } else {
                        if (sendCmd(SOCKET_NAME, CMD_BT_SHARED_PATH, MSG_SET_SHARED_PATH)) {
                            mUIThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    mBtRfPath.setEnabled(true);
                                    mBtRfPath.setChecked(true);
                                }
                            });
                            mRfPathEditor.putInt("rf_path_state", 2);
                            mRfPathEditor.apply();
                        } else {
                            mUIThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    mBtRfPath.setEnabled(true);
                                    mBtRfPath.setChecked(false);
                                }
                            });
                        }
                    }
                    break;
                /* @} */
                case MESSAGE_CONTROLLER_BQB_ONOFF:
                    try {
                        mControllerBqbState = btEut.controllerBqbEnable(!mControllerBqbState);
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
                    if (mControllerBqbState) {
                        mUIThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "CONTROLLER BQB ENABLE");
                                mControllerBqb.setSummary(CONTROLLER_BQB_ENABLED);
                            }
                        });
                    } else {
                        mUIThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "CONTROLLER BQB DISABLE");
                                mControllerBqb.setSummary(CONTROLLER_BQB_DISABLED);
                            }
                        });
                    }
                    break;
                case MESSAGE_CONTROLLER_BQB_TRIGGER:
                    try {
                        mControllerBqbState = btEut.getControllerBqbState();
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
                    if (mControllerBqbState) {
                        mUIThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "CONTROLLER BQB ENABLE");
                                mControllerBqb.setSummary(CONTROLLER_BQB_ENABLED);
                            }
                        });
                    } else {
                        mUIThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "CONTROLLER BQB DISABLE");
                                mControllerBqb.setSummary(CONTROLLER_BQB_DISABLED);
                            }
                        });
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private boolean sendCmd(String socketName, String cmd, int msg) {
        String res = null;
        /**
         * bcm need to connect "BDTD" server
         */
        /*
         * if (!mIsSprd) { // sprd cmd need to change //CMD_BT_EUT_OPEN =
         * "eng bt dut_mode_configure 1"; //CMD_BT_EUT_CLOSE =
         * "eng bt dut_mode_configure 0"; //CMD_BT_EUT_STATUS =
         * "eng bt eut_status"; cmd = "eng bt "+cmd; }
         */
        Log.d(TAG, "connect socket is " + socketName + ", cmd is " + cmd);
        res = SocketUtils.sendCmdNoCloseSocket(socketName, LocalSocketAddress.Namespace.ABSTRACT,
                PROCESS_NAME + cmd);
        mCmdRes = res;
        return analyRes(msg, res);
    }

    private boolean analyRes(int msg, String result) {
        boolean isSuccess = false;
        Log.d(TAG, "analyRes msg is " + msg + ", result is " + result);
        switch (msg) {
            case MSG_BT_ON:
                if (result != null && result.contains("bt_status=1")) {
                    isSuccess = true;
                }
                break;
            case MSG_BT_OFF:
                if (result != null && result.contains("bt_status=0")) {
                    isSuccess = true;
                }
                break;
            case MSG_GET_EUT_STATUS:
            case MSG_SET_EUT_ON:
            case MSG_SET_EUT_OFF:
                if (result != null && result.contains(SocketUtils.OK)) {
                    isSuccess = true;
                }
                break;
            case MSG_SET_SINGLE_PATH:
            case MSG_SET_SHARED_PATH:
                if (result != null && result.contains(SocketUtils.OK)) {
                    isSuccess = true;
                }
                break;
            default:
                break;
        }
        return isSuccess;
    }
}
