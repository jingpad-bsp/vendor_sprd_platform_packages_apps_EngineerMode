package com.sprd.engineermode.connectivity;

import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.sprd.engineermode.R;
import com.sprd.engineermode.connectivity.wifi.WifiEUTHelper;
import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.common.Const;
import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;

import android.bluetooth.BluetoothAdapter;
import android.view.View.OnClickListener;
import com.unisoc.engineermode.core.intf.IConnectivityApi;

public class ConnectivityFragment extends PreferenceFragment implements
        Preference.OnPreferenceClickListener {

    private static final String TAG = "ConnectivityFragment";
    private static final String KEY_WIFI_EUT = "wifi eut";
    private static final String KEY_BT = "bt";
    private static final String KEY_FM = "fm";
    private static final String KEY_CERTIFICATION = "wifi certification";
    private Context mContext = null;
    private ProgressDialog mProgress;
    private Preference mWifiTest;
    private Preference mBtTest;
    private Preference mFmTest;
    private Button mStartService;
    private Button mStopService;

    private IConnectivityApi.IWcndEngControl wcndEngCtl = CoreApi.getConnectivityApi().wcndEngControl();

    private Handler UIHandler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            boolean mWcndRunning = true;
            try {
                mWcndRunning = wcndEngCtl.isWcndEngRunning();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!mWcndRunning) {
                UIHandler.postDelayed(this, 100);
            } else {
                if (mProgress != null) {
                    mProgress.dismiss();
                }
                if (!Const.isUser()) {
                    mFmTest.setEnabled(true);
                }

                mStopService.setEnabled(true);
                mStartService.setEnabled(false);
                UIHandler.postDelayed(this, 2000);
            }
        }
    };

    @Override
    public void onResume() {
        UIHandler.postDelayed(runnable, 100);
        super.onResume();
    }

    @Override
    public void onPause() {
        UIHandler.removeCallbacks(runnable);
        super.onPause();
    }

    private void checkServiceStatus() {
        UIHandler.postDelayed(runnable, 100);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.connectivity, container, false);
        // getListView().setItemsCanFocus(true);
        mStartService = (Button) view.findViewById(R.id.start_service);
        mStartService.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // start service
                try {
                    wcndEngCtl.startWcndEng();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "start wcnd_eng service");
                mProgress = ProgressDialog.show(mContext,
                        "Start wcnd_eng service", "Please wait...", true, true);
                checkServiceStatus();
            }
        });
        mStopService = (Button) view.findViewById(R.id.stop_service);
        mStopService.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                try {
                    wcndEngCtl.stopWcndEng();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d(TAG, "stop wcnd_eng service");
                // when wcnd_eng service has stopped, we can not test wifi/bt/fm
                mWifiTest.setEnabled(false);
                mBtTest.setEnabled(false);
                mFmTest.setEnabled(false);
                mStopService.setEnabled(false);
                mStartService.setEnabled(true);
                UIHandler.removeCallbacks(runnable);
            }
        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_connectivitytab);
        mContext = this.getActivity();
        mWifiTest = (Preference) findPreference(KEY_WIFI_EUT);
        mWifiTest.setOnPreferenceClickListener(this);
        mBtTest = (Preference) findPreference(KEY_BT);
        mBtTest.setOnPreferenceClickListener(this);
        mFmTest = (Preference) findPreference(KEY_FM);
        mFmTest.setOnPreferenceClickListener(this);
        findPreference(KEY_CERTIFICATION).setOnPreferenceClickListener(this);

        if (Const.isUser()) {
            mFmTest.setEnabled(false);
            mFmTest.setSummary(R.string.feature_not_support_by_user_version);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        boolean mWcndRunning = true;
        try {
            mWcndRunning = wcndEngCtl.isWcndEngRunning();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mWcndRunning) {
            mStartService.setEnabled(false);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference pref) {
        if (pref.getKey().equals(KEY_WIFI_EUT)) {
            // Tester should close wifi when wifi eut testing in order to
            // getting correct result
            if (!isWifiOn()) {
                AlertDialog alertDialog = new AlertDialog.Builder(
                        this.getActivity())
                        .setTitle(getString(R.string.alert_wifi_test))
                        .setMessage(getString(R.string.alert_wifi))
                        .setCancelable(true)
                        .setPositiveButton(getString(R.string.alertdialog_ok),
                            (dialog, which) -> {
                                Intent intent = new Intent();
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                                intent.setClass(
                                        mContext,
                                        com.sprd.engineermode.connectivity.wifi.WifiEUTActivity.class);
                                startActivity(intent);
                            }).create();
                alertDialog.show();
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(
                        this.getActivity())
                        .setTitle(getString(R.string.alert_wifi_test))
                        .setMessage(getString(R.string.alert_close_wifi))
                        .setPositiveButton(getString(R.string.alertdialog_ok),
                            (dialog, which) -> {
                            }).create();
                alertDialog.show();
            }
        } else if (pref.getKey().equals(KEY_BT)) {
            if (!isBTOn()) {
                AlertDialog alertDialog = new AlertDialog.Builder(
                        this.getActivity())
                        .setTitle(getString(R.string.alert_bt_test))
                        .setMessage(getString(R.string.alert_bt))
                        .setCancelable(true)
                        .setPositiveButton(getString(R.string.alertdialog_ok),
                            (dialog, which) -> {
                                Intent intent = new Intent();
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                                intent.setClass(
                                        mContext,
                                        com.sprd.engineermode.connectivity.BT.BTActivity.class);
                                startActivity(intent);
                            }).create();
                alertDialog.show();
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(
                        this.getActivity())
                        .setTitle(getString(R.string.alert_bt_test))
                        .setMessage(getString(R.string.alert_close_bt))
                        .setPositiveButton(getString(R.string.alertdialog_ok),
                            (dialog, which) -> {
                            }).create();
                alertDialog.show();
            }
        } else if (pref.getKey().equals(KEY_FM)) {
            AlertDialog alertDialog = new AlertDialog.Builder(
                        this.getActivity())
                        .setTitle(getString(R.string.alert_fm_test))
                        .setMessage(getString(R.string.alert_fm))
                        .setCancelable(true)
                        .setPositiveButton(getString(R.string.alertdialog_ok),
                            (dialog, which) -> {
                                Intent intent = new Intent();
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                                intent.setClass( mContext,
                                        com.sprd.engineermode.connectivity.fm.FMActivity.class);
                                startActivity(intent);
                            }).create();
                alertDialog.show();
        } else if (pref.getKey().equals(KEY_CERTIFICATION)) {
            if (!isWifiOn()) {
                showWarningDialog(getString(R.string.alert_open_wifi_first));
                return true;
            }

            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
            intent.setClass(mContext,com.sprd.engineermode.connectivity.wifi.WifiTestActivity.class);
            startActivity(intent);
        }
        return true;
    }

    private boolean isWifiOn() {
        WifiManager wifiManager = (WifiManager) this.getActivity()
                .getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            return true;
        }
        return false;
    }

    private boolean isBTOn() {
        boolean isBtOn = false;
        int btState = BluetoothAdapter.getDefaultAdapter().getState();
        Log.d(TAG, "Connectivity BT Status is " + btState);
        if (btState == BluetoothAdapter.STATE_ON) {
            isBtOn = true;
        }
        return isBtOn;
    }

    private void showWarningDialog(String msg) {
        new AlertDialog.Builder(
            this.getActivity())
            .setTitle(getString(R.string.alert_wifi_test))
            .setMessage(msg)
            .setPositiveButton(getString(R.string.alertdialog_ok),
                (dialog, which) -> {
                })
            .create()
            .show();
    }
}
