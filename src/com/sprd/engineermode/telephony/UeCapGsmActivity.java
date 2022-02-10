package com.sprd.engineermode.telephony;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.util.Log;
import android.widget.Toast;

import com.unisoc.engineermode.core.CoreApi;
import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.intf.ITelephonyApi;

public class UeCapGsmActivity extends PreferenceActivity implements OnPreferenceChangeListener {

    private static final String TAG = "UeCapGsmActivity";
    private static final String KEY_DIVERSITY = "GSM diversity";

    private static final String OPEN = "1";
    private static final String CLOSE = "0";

    private static final int OPEN_VAMOS = 1;
    private static final int CLOSE_VAMOS = 2;
    private static final int OPEN_DIVERSITY = 3;
    private static final int CLOSE_DIVERSITY = 4;
    private static final int GET_VAMOS = 5;
    private static final int GET_DIVERSITY = 6;
    private MyHandler mHandler;
    private Handler mUiThread = new Handler();

    private ITelephonyApi teleApi = CoreApi.getTelephonyApi();
    private String mATCmd;
    private String mStrTmp;
    private Context mContext;
    private int simIdx = UeNwCapActivity.mSimIndex;

    private SwitchPreference mPreDiversity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_uecap_gsm);
        mContext = this;
        mPreDiversity = (SwitchPreference)findPreference(KEY_DIVERSITY);
        mPreDiversity.setOnPreferenceChangeListener(this);
        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mHandler = new MyHandler(ht.getLooper());
    }

    @Override
    protected void onStart() {
        if (mPreDiversity != null) {
            Message openIpv6 = mHandler.obtainMessage(GET_DIVERSITY);
            mHandler.sendMessage(openIpv6);
        }
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class MyHandler extends Handler {
        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case OPEN_DIVERSITY:
                Log.d(TAG, "OPEN_DIVERSITY");
                if (teleApi.gsmUeCapApi().openDiversity(simIdx)) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mPreDiversity.setChecked(true);
                            Toast.makeText(mContext, "Success",Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mPreDiversity.setChecked(false);
                            Toast.makeText(mContext, "Change status is not supported", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            case CLOSE_DIVERSITY:
                Log.d(TAG, "CLOSE_DIVERSITY");
                if (teleApi.gsmUeCapApi().closeDiversity(simIdx)) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mPreDiversity.setChecked(false);
                            Toast.makeText(mContext, "Success",Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mPreDiversity.setChecked(true);
                            Toast.makeText(mContext, "Change status is not supported", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            case GET_DIVERSITY:
                Log.d(TAG, "GET_DIVERSITY");
                if (teleApi.gsmUeCapApi().getDiversity(simIdx)) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mPreDiversity.setChecked(false);
                            //Toast.makeText(mContext, "Success",Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mPreDiversity.setChecked(true);
                            //Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            }
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String key = preference.getKey();
        if (KEY_DIVERSITY.equals(key)){
            if (!mPreDiversity.isChecked()) {
                Message openDiversity = mHandler.obtainMessage(OPEN_DIVERSITY);
                mHandler.sendMessage(openDiversity);
            } else {
                Message closeDiversity = mHandler.obtainMessage(CLOSE_DIVERSITY);
                mHandler.sendMessage(closeDiversity);
            }
        }
        return false;
    }

}
