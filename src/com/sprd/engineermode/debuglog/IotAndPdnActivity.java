
package com.sprd.engineermode.debuglog;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import android.os.HandlerThread;
import android.content.Context;

import com.sprd.engineermode.EMSwitchPreference;
import com.sprd.engineermode.R;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

public class IotAndPdnActivity extends PreferenceActivity
        implements EMSwitchPreference.OnPreferenceChangeListener {
    private static final String TAG = "IotAndPdnActivity";
    private static final String IOT = "key_iot";
    private static final String PDPONE = "key_pdp_one";
    private static final String PDPTWO = "key_pdp_two";
    //private EMSwitchPreference mIotPreference = null;
    private EMSwitchPreference mPdpOnePreference = null;
    private EMSwitchPreference mPdpTwoPreference = null;
    private TelephonyManager mTm = null;
    private ConnectivityManager mCm = null;
    private ConnectivityManager.NetworkCallback mNetworkCallbackFirst = null;
    private ConnectivityManager.NetworkCallback mNetworkCallbackSecond = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        addPreferencesFromResource(R.xml.pref_iot_pdn);
        //mIotPreference = (EMSwitchPreference) findPreference(IOT);
        //mIotPreference.setOnPreferenceChangeListener(this);
        mPdpOnePreference = (EMSwitchPreference) findPreference(PDPONE);
        mPdpOnePreference.setOnPreferenceChangeListener(this);
        mPdpTwoPreference = (EMSwitchPreference) findPreference(PDPTWO);
        mPdpTwoPreference.setOnPreferenceChangeListener(this);
        mTm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mCm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        init();
    }

    @Override
    public void onStart() {
        super.onStart();
        /*if (mIotPreference != null) {
            boolean isIot = SystemProperties.getBoolean(
                    "persist.sys.volte.iot", false);
            Log.d(TAG, "Iot: " + isIot);
            if (isIot) {
                mIotPreference.setChecked(true);
            } else {
                mIotPreference.setChecked(false);
            }
        }*/
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference instanceof EMSwitchPreference) {
            String key = preference.getKey();
            /*if (key.equals(IOT)) {
                if (!mIotPreference.isChecked()) {
                    Log.d(TAG, "Open Iot Switch");
                    SystemPropertiesProxy.set("persist.sys.volte.iot", "1");
                    mIotPreference.setChecked(true);
                } else {
                    Log.d(TAG, "Close Iot Switch");
                    SystemPropertiesProxy.set("persist.sys.volte.iot", "0");
                    mIotPreference.setChecked(false);
                }
            } else */if (key.equals(PDPTWO)) {
                if (!mPdpTwoPreference.isChecked()) {
                    Log.d(TAG, "activate NET_CAPABILITY_SUPL pdp");
                    NetworkRequest request = buildRequest(NetworkCapabilities.NET_CAPABILITY_SUPL);
                    mCm.registerNetworkCallback(request, mNetworkCallbackFirst);
                    mCm.requestNetwork(request, mNetworkCallbackFirst);
                    mPdpTwoPreference.setChecked(true);
                } else {
                    try {
                        mCm.unregisterNetworkCallback(mNetworkCallbackFirst);
                        mPdpTwoPreference.setChecked(false);
                    } catch (IllegalArgumentException iea) {
                        iea.printStackTrace();
                    }
                }
            } else if (key.equals(PDPONE)) {
                if (!mPdpOnePreference.isChecked()) {
                    Log.d(TAG, "activate NET_CAPABILITY_MMS pdp");
                    NetworkRequest request = buildRequest(NetworkCapabilities.NET_CAPABILITY_MMS);
                    mCm.registerNetworkCallback(request, mNetworkCallbackSecond);
                    mCm.requestNetwork(request, mNetworkCallbackSecond);
                    mPdpOnePreference.setChecked(true);
                } else {
                    try {
                        mCm.unregisterNetworkCallback(mNetworkCallbackSecond);
                        mPdpOnePreference.setChecked(false);
                    } catch (IllegalArgumentException iea) {
                        iea.printStackTrace();
                    }
                }
            }
            return true;
        }
        return false;
    }

    private NetworkRequest buildRequest(int type) {
        NetworkRequest.Builder builder = null;
        builder = new NetworkRequest.Builder();
        builder.addCapability(type);
        builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);
        builder.setNetworkSpecifier("");
        NetworkRequest request = builder.build();
        return request;
    }

    private void init() {
        /*
         * if(mPdpOnePreference!=null){ mPdpOnePreference.setChecked(false); }
         * if(mPdpTwoPreference!=null){ mPdpTwoPreference.setChecked(false); }
         */

        mNetworkCallbackSecond = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);
                Log.d(TAG, "network2 onAvailable");
                Toast.makeText(IotAndPdnActivity.this, "success", Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onLost(Network network) {
                super.onLost(network);
                Log.d(TAG, "network2 onLost");
            }
        };
        mNetworkCallbackFirst = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);
                Log.d(TAG, "network1 onAvailable");
                Toast.makeText(IotAndPdnActivity.this, "success", Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onLost(Network network) {
                super.onLost(network);
                Log.d(TAG, "network1 onLost");
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}