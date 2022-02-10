package com.sprd.engineermode.telephony.volte;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.exception.EmException;
import com.unisoc.engineermode.core.intf.ITelephonyApi;

import java.util.function.IntSupplier;

public class DevelopMentSettingsActivity extends PreferenceActivity implements
    Preference.OnPreferenceChangeListener  {

    private static final String TAG = "DevelopMentSettingsActivity";
    private static final String KEY_DEV_FLAG1 = "dev_flag_1";
    private static final String KEY_DEV_FLAG2 = "dev_flag_2";
    private static final String KEY_DEV_FLAG3 = "dev_flag_3";

    private static final int MSG_GET_DEV_FLAG1 = 0;
    private static final int MSG_SET_DEV_FLAG1 = 1;
    private static final int MSG_GET_DEV_FLAG2 = 2;
    private static final int MSG_SET_DEV_FLAG2 = 3;
    private static final int MSG_GET_DEV_FLAG3 = 4;
    private static final int MSG_SET_DEV_FLAG3 = 5;

    private Context mContext = null;
    private Handler mUiThread = new Handler();
    private VolteHandler mVolteHandler;
    private EditTextPreference mDevFlag1;
    private EditTextPreference mDevFlag2;
    private EditTextPreference mDevFlag3;

    private static ITelephonyApi teleApi = CoreApi.getTelephonyApi();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_volte_develop_settings);

        mContext = this;
        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mVolteHandler = new VolteHandler(ht.getLooper());

        mDevFlag1 = (EditTextPreference) findPreference(KEY_DEV_FLAG1);
        mDevFlag1.setOnPreferenceChangeListener(this);
        mDevFlag2 = (EditTextPreference) findPreference(KEY_DEV_FLAG2);
        mDevFlag2.setOnPreferenceChangeListener(this);
        mDevFlag3 = (EditTextPreference) findPreference(KEY_DEV_FLAG3);
        mDevFlag3.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onStart() {
        if (mDevFlag1 != null && mDevFlag1.isEnabled()) {
            Message getDevFlag1 = mVolteHandler
                    .obtainMessage(MSG_GET_DEV_FLAG1);
            mVolteHandler.sendMessage(getDevFlag1);
        }
        if (mDevFlag2 != null && mDevFlag2.isEnabled()) {
            Message getDevFlag2 = mVolteHandler
                    .obtainMessage(MSG_GET_DEV_FLAG2);
            mVolteHandler.sendMessage(getDevFlag2);
        }
        if (mDevFlag3 != null && mDevFlag3.isEnabled()) {
            Message getDevFlag3 = mVolteHandler
                    .obtainMessage(MSG_GET_DEV_FLAG3);
            mVolteHandler.sendMessage(getDevFlag3);
        }
        super.onStart();
    }

    @Override
    public boolean onPreferenceChange(Preference pref, Object newValue) {
        String prefkey = pref.getKey();
        Log.d(TAG, "prefkey: " + prefkey);
        if (prefkey.equals(KEY_DEV_FLAG1)) {
            Message setDevFlag1 = mVolteHandler.obtainMessage(MSG_SET_DEV_FLAG1, newValue);
            mVolteHandler.sendMessage(setDevFlag1);
        } else if (prefkey.equals(KEY_DEV_FLAG2)) {
            Message setDevFlag2 = mVolteHandler.obtainMessage(MSG_SET_DEV_FLAG2, newValue);
            mVolteHandler.sendMessage(setDevFlag2);
        } else if (prefkey.equals(KEY_DEV_FLAG3)) {
            Message setDevFlag3 = mVolteHandler.obtainMessage(MSG_SET_DEV_FLAG3, newValue);
            mVolteHandler.sendMessage(setDevFlag3);
        }
        return true;
    }

    @Override
    public void onDestroy() {
        if (mVolteHandler != null) {
            mVolteHandler.getLooper().quit();
            Log.d(TAG, "HandlerThread has quit");
        }
        super.onDestroy();
    }

    private void getDevFlag(int index) {
        final EditTextPreference devFlagText;
        IntSupplier supplier;

        switch (index) {
            case 1:
                devFlagText = mDevFlag1;
                supplier = () -> teleApi.volteDevelopSettings().getDevFlag1();
                break;
            case 2:
                devFlagText = mDevFlag2;
                supplier = () -> teleApi.volteDevelopSettings().getDevFlag2();
                break;
            case 3:
                devFlagText = mDevFlag3;
                supplier = () -> teleApi.volteDevelopSettings().getDevFlag3();
                break;
            default:
                throw new RuntimeException("invalid index");
        }

        try {
            int devFlag = supplier.getAsInt();

            mUiThread.post(() -> {
                devFlagText.setEnabled(true);
                devFlagText.setText(String.valueOf(devFlag));
                devFlagText.setSummary(String.valueOf(devFlag));
            });
        } catch (EmException e) {
            e.printStackTrace();
            mUiThread.post(() -> {
                devFlagText.setEnabled(false);
                devFlagText.setSummary(R.string.feature_abnormal);
            });
        }
    }

    private void setDevFlag(int index, String value) {
        if (value.trim().equals("")) {
            return;
        }

        int flag;
        try {
            flag = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            Toast.makeText(mContext, "the number is invalid, should not greater than 2147483647(32 bits)", Toast.LENGTH_SHORT).show();
            return;
        }

        final EditTextPreference devFlagText;
        try {
            switch (index) {
                case 1:
                    devFlagText = mDevFlag1;
                    teleApi.volteDevelopSettings().setDevFlag1(flag);
                    break;
                case 2:
                    devFlagText = mDevFlag2;
                    teleApi.volteDevelopSettings().setDevFlag2(flag);
                    break;
                case 3:
                    devFlagText = mDevFlag3;
                    teleApi.volteDevelopSettings().setDevFlag3(flag);
                    break;
                default:
                    throw new RuntimeException("invalid index");
            }
            mUiThread.post(() -> {
                devFlagText.setText(value);
                devFlagText.setSummary(value);
                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
            });
        } catch (EmException e) {
            e.printStackTrace();
            showFailToast();
        }
    }

    class VolteHandler extends Handler {

        VolteHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_GET_DEV_FLAG1:
                    getDevFlag(1);
                    break;
                case MSG_SET_DEV_FLAG1:
                    setDevFlag(1, (String) msg.obj);
                    break;
                case MSG_GET_DEV_FLAG2:
                    getDevFlag(2);
                    break;
                case MSG_SET_DEV_FLAG2:
                    setDevFlag(2, (String) msg.obj);
                    break;
                case MSG_GET_DEV_FLAG3:
                    getDevFlag(3);
                    break;
                case MSG_SET_DEV_FLAG3:
                    setDevFlag(3, (String) msg.obj);
                    break;
                default:
                    break;
            }
        }
    }

    private void showFailToast() {
        mUiThread.post(() -> Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show());
    }
}
