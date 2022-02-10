package com.sprd.engineermode.telephony.volte;

import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceCategory;
import android.preference.EditTextPreference;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.SharedPreferences;

import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.impl.nonpublic.TelephonyManagerProxy;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import android.util.Log;
import android.widget.Toast;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.content.Context;
import android.os.Message;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.os.PowerManager;

import com.sprd.engineermode.R;

import java.nio.charset.StandardCharsets;

public class TemporarySettingsActivity extends PreferenceActivity implements
        Preference.OnPreferenceChangeListener,
        OnSharedPreferenceChangeListener {

    private static final String TAG = "TemporarySettingsActivity";
    private static final String KEY_IMPI_SETTING = "impi_setting";
    private static final String KEY_IMPU_SETTING = "impu_setting";
    private static final String KEY_DOMAIN_SETTING = "domain_setting";
    private static final String KEY_PCSCF_SETTING = "pcscf_setting";

    private static final int MSG_SET_IMPI = 0;
    private static final int MSG_SET_IMPU = 1;
    private static final int MSG_SET_DOMAIN = 2;
    private static final int MSG_SET_PCSCF = 3;
    private static final int PCSCF_LENGTH_MAX = 91;
    private static final int IMPI_LENGTH_MAX = 127;

    private PreferenceGroup mIMPISettingScreen;


    private int mPhoneCount = 0;
    private int mPhoneId = 0;
    private Context mContext = null;
    private SharedPreferences mSharePref;
    private Handler mUiThread = new Handler();
    private VolteHandler mVolteHandler;

    private static ITelephonyApi teleApi = CoreApi.getTelephonyApi();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_volte_temporary_settings);

        mPhoneCount = TelephonyManagerProxy.getPhoneCount();
        mContext = this;
        mSharePref = PreferenceManager.getDefaultSharedPreferences(this);
        mSharePref.registerOnSharedPreferenceChangeListener(this);
        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mVolteHandler = new VolteHandler(ht.getLooper());
        Log.d(TAG, "");
        mIMPISettingScreen = getPreferenceScreen();
        Log.d(TAG, "onCreate mPhoneCount: " + mPhoneCount);
        for (int i = 0; i < mPhoneCount; i++) {
            PreferenceCategory prefCategory = new PreferenceCategory(this);
            prefCategory.setTitle("SIM" + i);
            mIMPISettingScreen.addPreference(prefCategory);
            EditTextPreference impisetting = new EditTextPreference(this);
            impisetting.setKey(KEY_IMPI_SETTING + i);
            impisetting.setTitle("IMPI Setting");
            impisetting.setDialogTitle("IMPI Setting");
            impisetting.setText("");
            impisetting.setOnPreferenceChangeListener(this);
            if (mSharePref.getString(KEY_IMPI_SETTING + i, null) == null) {
                impisetting.setSummary(R.string.input);
            } else {
                impisetting.setSummary(mSharePref.getString(KEY_IMPI_SETTING
                        + i, null));
            }
            mIMPISettingScreen.addPreference(impisetting);

            EditTextPreference impusetting = new EditTextPreference(this);
            impusetting.setKey(KEY_IMPU_SETTING + i);
            impusetting.setTitle("IMPU Setting");
            impusetting.setDialogTitle("IMPU Setting");
            impusetting.setOnPreferenceChangeListener(this);
            if (mSharePref.getString(KEY_IMPU_SETTING + i, null) == null) {
                impusetting.setSummary(R.string.input);
            } else {
                impusetting.setSummary(mSharePref.getString(KEY_IMPU_SETTING
                        + i, null));
            }
            mIMPISettingScreen.addPreference(impusetting);

            EditTextPreference domain = new EditTextPreference(this);
            domain.setKey(KEY_DOMAIN_SETTING + i);
            domain.setTitle("Domain Setting");
            domain.setDialogTitle("Domain Setting");
            domain.setOnPreferenceChangeListener(this);
            if (mSharePref.getString(KEY_DOMAIN_SETTING + i, null) == null) {
                domain.setSummary(R.string.input);
            } else {
                domain.setSummary(mSharePref.getString(KEY_DOMAIN_SETTING + i,
                        null));
            }
            mIMPISettingScreen.addPreference(domain);

            EditTextPreference pcscf = new EditTextPreference(this);
            pcscf.setKey(KEY_PCSCF_SETTING + i);
            pcscf.setTitle("P-CSCF");
            pcscf.setDialogTitle("P-CSCF");
            pcscf.setOnPreferenceChangeListener(this);
            if (mSharePref.getString(KEY_PCSCF_SETTING + i, null) == null) {
                pcscf.setSummary(R.string.input);
            } else {
                pcscf.setSummary(mSharePref.getString(KEY_PCSCF_SETTING + i,
                        null));
            }
            mIMPISettingScreen.addPreference(pcscf);
        }
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

    @Override
    public boolean onPreferenceChange(Preference pref, Object newValue) {
        String prefkey = pref.getKey();
        Log.d(TAG, "onPreferenceChange prefkey: " + prefkey);
        for (int i = 0; i < mPhoneCount; i++) {
            if (prefkey.equals(KEY_IMPI_SETTING + i)) {
                Message setIMPI = mVolteHandler.obtainMessage(MSG_SET_IMPI, i,
                        0, newValue);
                mVolteHandler.sendMessage(setIMPI);
                return false;
            } else if (prefkey.equals(KEY_IMPU_SETTING + i)) {
                Message setIMPU = mVolteHandler.obtainMessage(MSG_SET_IMPU, i,
                        0, newValue);
                mVolteHandler.sendMessage(setIMPU);
                return false;
            } else if (prefkey.equals(KEY_DOMAIN_SETTING + i)) {
                Message setDomain = mVolteHandler.obtainMessage(MSG_SET_DOMAIN,
                        i, 0, newValue);
                mVolteHandler.sendMessage(setDomain);
                return false;
            } else if (prefkey.equals(KEY_PCSCF_SETTING + i)) {
                Message setPcscf = mVolteHandler.obtainMessage(MSG_SET_PCSCF,
                        i, 0, newValue);
                mVolteHandler.sendMessage(setPcscf);
                return false;
            }
        }
        return true;
    }
    class VolteHandler extends Handler {

        public VolteHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_SET_IMPI:
                Log.d(TAG, "MSG_SET_IMPI");
                mPhoneId = msg.arg1;
                String impiValue = (String) msg.obj;
                if (impiValue != null && impiValue.length() != 0) {
                    if (impiValue.getBytes(StandardCharsets.UTF_8).length > IMPI_LENGTH_MAX) {
                        Toast.makeText(mContext, "The length of the input value can not exceed 127",
                            Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                try {
                    String mImpiCmd = engconstents.ENG_VOLTE_IMPI + "\"" + impiValue + "\"";
                    teleApi.volteTemporarySettings().setImpi(mImpiCmd, mPhoneId);
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mIMPISettingScreen.findPreference(
                                    KEY_IMPI_SETTING + mPhoneId).setSummary(
                                    impiValue);
                            Editor editor = mSharePref.edit();
                            editor.putString(KEY_IMPI_SETTING + mPhoneId,
                                    impiValue);
                            editor.commit();
                            Toast.makeText(mContext, "Success",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    showFailToast();
                }
                break;
            case MSG_SET_IMPU:
                Log.d(TAG, "MSG_SET_IMPU");
                mPhoneId = msg.arg1;
                String impuValue = (String) msg.obj;
                if (impuValue != null && impuValue.length() != 0) {
                    if (impuValue.length() > IMPI_LENGTH_MAX) {
                        Toast.makeText(mContext, "The length of the input value can not exceed 127",
                            Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                try {
                    String mImpuCmd = engconstents.ENG_VOLTE_IMPU + "\"" + impuValue + "\"";
                    teleApi.volteTemporarySettings().setImpu(mImpuCmd, mPhoneId);
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mIMPISettingScreen.findPreference(
                                    KEY_IMPU_SETTING + mPhoneId).setSummary(
                                    impuValue);
                            Editor editor = mSharePref.edit();
                            editor.putString(KEY_IMPU_SETTING + mPhoneId,
                                    impuValue);
                            editor.commit();
                            Toast.makeText(mContext, "Success",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    showFailToast();
                }
                break;
            case MSG_SET_DOMAIN:
                Log.d(TAG, "MSG_SET_DOMAIN");
                mPhoneId = msg.arg1;
                String domainValue = (String) msg.obj;
                if (domainValue != null && domainValue.length() != 0) {
                    if (domainValue.getBytes().length > IMPI_LENGTH_MAX) {
                        Toast.makeText(mContext, "The length of the input value can not exceed 127",
                            Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                try {
                    String mDomainCmd = engconstents.ENG_VOLTE_DOMAIN + "\"" + domainValue + "\"";
                    teleApi.volteTemporarySettings().setDomain(mDomainCmd, mPhoneId);
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mIMPISettingScreen.findPreference(
                                    KEY_DOMAIN_SETTING + mPhoneId).setSummary(
                                    domainValue);
                            Editor editor = mSharePref.edit();
                            editor.putString(KEY_DOMAIN_SETTING + mPhoneId,
                                    domainValue);
                            editor.commit();
                            Toast.makeText(mContext, "Success",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    showFailToast();
                }
                break;
            case MSG_SET_PCSCF:
                mPhoneId = msg.arg1;
                String pcscfValue = (String) msg.obj;
                Log.d(TAG, "MSG_SET_PCSCF pcscfValue: " + pcscfValue);
                if (pcscfValue != null && pcscfValue.length() != 0) {
                    if (pcscfValue.getBytes().length > PCSCF_LENGTH_MAX) {
                        Toast.makeText(mContext, "The length of the input value can not exceed 91",
                            Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        teleApi.volteTemporarySettings().setPcscf(pcscfValue);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    rebootDialog(TemporarySettingsActivity.this);
                }
                mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mIMPISettingScreen.findPreference(
                                    KEY_PCSCF_SETTING + mPhoneId).setSummary(
                                    pcscfValue);
                            Editor editor = mSharePref.edit();
                            editor.putString(KEY_PCSCF_SETTING + mPhoneId,
                                    pcscfValue);
                            editor.commit();
                            Toast.makeText(mContext, "Success",
                                    Toast.LENGTH_SHORT).show();
                        }
                });
                break;
            default:
                break;
            }
        }
    }

    private void showFailToast() {
        mUiThread.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void rebootDialog(Context context) {
        AlertDialog alertDialog = new AlertDialog.Builder(
                    TemporarySettingsActivity.this)
                    .setTitle("P-CSCF")
                    .setCancelable(false)
                    .setMessage(getString(R.string.mode_switch_waring))
                    .setPositiveButton(getString(R.string.alertdialog_ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                    int which) {
                                    PowerManager pm = (PowerManager) TemporarySettingsActivity.this.getSystemService(Context.POWER_SERVICE);
                                    pm.reboot("pcscfset");
                                }
                            })
                    .setNegativeButton(R.string.alertdialog_cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                }
                            }).create();
        alertDialog.show();
    }
}

