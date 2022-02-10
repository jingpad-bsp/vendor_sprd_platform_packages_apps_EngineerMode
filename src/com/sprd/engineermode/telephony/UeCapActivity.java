package com.sprd.engineermode.telephony;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;
import android.widget.Toast;

import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.impl.telephony.TelephonyManagerSprd;
import com.unisoc.engineermode.core.utils.IATUtils;

public class UeCapActivity extends PreferenceActivity implements OnPreferenceChangeListener {

    private static final String TAG = "UeCapActivity";
    //private static final String KEY_IPV6 = "ipv6";

    private static final int OPEN_IPV6 = 1;
    private static final int ClOSE_IPV6 = 2;
    private static final int GET_IPV6 = 3;
    static String saveName = "atchannel0";

    private MyHandler mHandler;
    private Handler mUiThread = new Handler();

    private String mATCmd;
    private String mStrTmp;
    private Context mContext;

    private static final String KEY_LTE = "lte";
    private static final String KEY_WCDMA = "wcdma";
    private static final String KEY_GSM = "gsm";
    private static final String OPEN = "1";
    private static final String CLOSE = "0";
    private Preference mLtePreference;
    private Preference mWcdmaPreference;
    private Preference mGsmPreference;
    private SwitchPreference mPreIpv6;
    private static final String KEY_SIM_INDEX = "simindex";
    private int mSimIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (TelephonyManagerSprd.getRadioCapbility() == TelephonyManagerSprd.RadioCapbility.WG
            || TelephonyManagerSprd.getRadioCapbility() == TelephonyManagerSprd.RadioCapbility.WW) {
            addPreferencesFromResource(R.xml.pref_uecap_wg);
        }else {
            addPreferencesFromResource(R.xml.pref_uecap);
        }
        mContext = this;
        //mPreIpv6 = (SwitchPreference)findPreference(KEY_IPV6);
        //mPreIpv6.setOnPreferenceChangeListener(this);
        //mPreIpv6.setEnabled(false);

        saveName = "atchannel" + UeNwCapActivity.mSimIndex;
        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mHandler = new MyHandler(ht.getLooper());

    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onstart");
        if (mPreIpv6 != null) {
            Message openIpv6 = mHandler.obtainMessage(GET_IPV6);
            mHandler.sendMessage(openIpv6);
        }
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String key = preference.getKey();
        Log.d(TAG, "onPreferenceChange key = " + key);

        return false;
    }

    private class MyHandler extends Handler {
        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
				case GET_IPV6:
					Log.d(TAG, "GET_IPV6");
					mATCmd = engconstents.ENG_AT_GET_IPV6;
					mStrTmp = IATUtils.sendATCmd(mATCmd, "atchannel0");
					if (mStrTmp.contains("")) {
						mUiThread.post(new Runnable() {
							@Override
							public void run() {
								mPreIpv6.setChecked(false);
								//Toast.makeText(mContext, "Success",Toast.LENGTH_SHORT).show();
							}
						});
					} else {
						mUiThread.post(new Runnable() {
							@Override
							public void run() {
								mPreIpv6.setChecked(true);
								//Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
							}
						});
					}
					break;
            }
        }
    }
}
