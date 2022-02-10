package com.sprd.engineermode.telephony.volte;

import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.EditTextPreference;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.SharedPreferences;
import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.exception.EmException;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import android.util.Log;
import android.widget.Toast;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.content.Context;
import android.os.Message;

import com.sprd.engineermode.R;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class PlmnSettingsActivity extends PreferenceActivity implements
    Preference.OnPreferenceChangeListener, OnSharedPreferenceChangeListener {

    private static final String TAG = "PlmnSettingsActivity";
    private static final String KEY_PLMN_FLAG1 = "test_plmn_1";
    private static final String KEY_PLMN_FLAG2 = "test_plmn_2";

    private static final int MSG_GET_PLMN_FLAG1 = 0;
    private static final int MSG_SET_PLMN_FLAG1 = 1;
    private static final int MSG_GET_PLMN_FLAG2 = 2;
    private static final int MSG_SET_PLMN_FLAG2 = 3;

    private Context mContext = null;
    private SharedPreferences mSharePref;
    private Handler mUiThread = new Handler();
    private VolteHandler mVolteHandler;
    private EditTextPreference mPlmnFlag1, mPlmnFlag2;

    private static ITelephonyApi teleApi = CoreApi.getTelephonyApi();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_plmn_settings);

        mContext = this;
        mSharePref = PreferenceManager.getDefaultSharedPreferences(this);
        mSharePref.registerOnSharedPreferenceChangeListener(this);
        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mVolteHandler = new VolteHandler(ht.getLooper());

        mPlmnFlag1 = (EditTextPreference) findPreference(KEY_PLMN_FLAG1);
        mPlmnFlag1.setOnPreferenceChangeListener(this);
        mPlmnFlag2 = (EditTextPreference) findPreference(KEY_PLMN_FLAG2);
        mPlmnFlag2.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onStart() {
        if (mPlmnFlag1 != null && mPlmnFlag1.isEnabled()) {
            Message getPlmnFlag1 = mVolteHandler
                    .obtainMessage(MSG_GET_PLMN_FLAG1);
            mVolteHandler.sendMessage(getPlmnFlag1);
        }
        if (mPlmnFlag2 != null && mPlmnFlag2.isEnabled()) {
            Message getPlmnFlag2 = mVolteHandler
                    .obtainMessage(MSG_GET_PLMN_FLAG2);
            mVolteHandler.sendMessage(getPlmnFlag2);
        }
        super.onStart();
    }

    @Override
    public boolean onPreferenceChange(Preference pref, Object newValue) {
        String prefkey = pref.getKey();
        Log.d(TAG, "prefkey: " + prefkey);
        if (prefkey.equals(KEY_PLMN_FLAG1)) {
            Message setPlmnFlag1 = mVolteHandler.obtainMessage(MSG_SET_PLMN_FLAG1, newValue);
            mVolteHandler.sendMessage(setPlmnFlag1);
        } else if (prefkey.equals(KEY_PLMN_FLAG2)) {
            Message setPlmnFlag2 = mVolteHandler.obtainMessage(MSG_SET_PLMN_FLAG2, newValue);
            mVolteHandler.sendMessage(setPlmnFlag2);
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
    }

    private void getPlmn(int index) {
        EditTextPreference plmnText;
        Supplier<String> supplier;

        switch (index) {
            case 1:
                plmnText = mPlmnFlag1;
                supplier = () -> teleApi.voltePlmnSettings().getPlmn1();
                break;
            case 2:
                plmnText = mPlmnFlag2;
                supplier = () -> teleApi.voltePlmnSettings().getPlmn2();
                break;
            default:
                throw new RuntimeException("invalid index");
        }

        try {
            final String plmn = supplier.get();
            mUiThread.post(() -> {
                plmnText.setEnabled(true);
                plmnText.setSummary(plmn);
            });
        } catch (EmException e) {
            e.printStackTrace();
            mUiThread.post(() -> {
                plmnText.setEnabled(false);
                plmnText.setSummary(R.string.feature_abnormal);
            });
        }
    }

    private void setPlmn(int index, String plmn) {
        if (plmn.trim().equals("")) {
            return;
        }
        EditTextPreference plmnText;
        Consumer<String> func;

        switch (index) {
            case 1:
                plmnText = mPlmnFlag1;
                func = p -> teleApi.voltePlmnSettings().setPlmn1(p);
                break;
            case 2:
                plmnText = mPlmnFlag2;
                func = p -> teleApi.voltePlmnSettings().setPlmn2(p);
                break;
            default:
                throw new RuntimeException("invalid index");
        }

        try {
            func.accept(plmn);
            mUiThread.post(() -> {
                Editor editor = mSharePref.edit();
                editor.putString(KEY_PLMN_FLAG1, plmn);
                editor.commit();
                plmnText.setSummary(plmn);
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
                case MSG_GET_PLMN_FLAG1:
                    getPlmn(1);
                    break;
                case MSG_SET_PLMN_FLAG1:
                    setPlmn(1, (String)msg.obj);
                    break;
                case MSG_GET_PLMN_FLAG2:
                    getPlmn(2);
                    break;
                case MSG_SET_PLMN_FLAG2:
                    setPlmn(2, (String)msg.obj);
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
