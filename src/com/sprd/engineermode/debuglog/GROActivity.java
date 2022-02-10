package com.sprd.engineermode.debuglog;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import com.sprd.engineermode.R;
import com.sprd.engineermode.EMSwitchPreference;
import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;

public class GROActivity extends PreferenceActivity implements
        Preference.OnPreferenceChangeListener,Preference.OnPreferenceClickListener{

    private static final String TAG = "GROActivity";
    private static final String KEY_GRO_SWITCH = "key_gro";

    private EMSwitchPreference mGro;
    private static final int GET_GRO_STATE = 14;
    private Handler mUiThread;
    private DEGHandler mDEGHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_apcp_gro);
        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mDEGHandler = new DEGHandler(ht.getLooper());

        mUiThread = new Handler();
        mGro = (EMSwitchPreference) findPreference(KEY_GRO_SWITCH);
        mGro.setOnPreferenceChangeListener(this);
        Message getGroState = mDEGHandler.obtainMessage(GET_GRO_STATE);
        mDEGHandler.sendMessage(getGroState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGro.setSummary(SystemPropertiesProxy.get("persist.vendor.cam.preview.fps","0"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onPreferenceChange(Preference pref, Object objValue) {
        Log.d(TAG, "pref ="+pref.getKey());
        if (pref == mGro) {
            if (!mGro.isChecked()) {
                SystemPropertiesProxy.set("ctl.start", "vendor.gro_on");
                mGro.setSummary("1");
            } else {
                SystemPropertiesProxy.set("ctl.start", "vendor.gro_off");
                mGro.setSummary("0");
            }
            return true;
        }
        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference pref) {
        Log.d(TAG, "key="+pref.getKey());
        return true;
    }

    public String readFile(String path) {
        File file = new File(path);
        String str = new String("");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                str = str + line;
            }
        } catch (Exception e) {
            Log.d(TAG, "Read file error!!!");
            str = "readError";
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
        Log.d(TAG, "read " + path + " value is " + str.trim());
        return str.trim();
    }

    class DEGHandler extends Handler {

        public DEGHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_GRO_STATE:
                    // /sys/module/seth/parameter/gro_enable
                    String groStatus = readFile("/sys/module/seth/parameters/gro_enable");
                    Log.d(TAG, "GRO status is " + groStatus);
                    if (!"readError".equals(groStatus) && groStatus != null) {
                        if (groStatus.contains("1")) {
                            mUiThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    mGro.setChecked(true);
                                    mGro.setSummary("1");
                                }
                            });
                        } else if (groStatus.contains("0")) {
                            mUiThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    mGro.setChecked(false);
                                    mGro.setSummary("0");
                                }
                            });
                        } else {
                            mUiThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    mGro.setEnabled(false);
                                    mGro.setSummary(R.string.feature_not_support);
                                }
                            });
                        }
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mGro.setEnabled(false);
                                mGro.setSummary(R.string.feature_not_support);
                            }
                        });
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
