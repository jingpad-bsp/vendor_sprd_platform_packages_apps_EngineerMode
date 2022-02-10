
package com.sprd.engineermode.debuglog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.sprd.engineermode.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;
import com.unisoc.engineermode.core.utils.CmdUtils;

public class SystemSettingActivity extends PreferenceActivity implements
        Preference.OnPreferenceClickListener {

    private static final String TAG = "SystemSettingActivity";
    private static final String KEY_GPU_FREQ = "gpu_freq";
    private static final String GPU_FEEQ_PATH = "/sys/class/devfreq/60000000.gpu/";
    private static final String GPU_FEEQ_CURRENT = "cur_freq";
    private static final String GPU_FEEQ_AVAILABLE = "available_frequencies";
    private static final String GPU_FEEQ_MAX = "max_freq";
    private static final String GPU_FEEQ_MIN = "min_freq";

    private Preference mGPUFreq;
    private String[] mGpuFreq;
    private String mGPUCurrentFreq = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate...");
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_system_settings);
        mGPUFreq = (Preference) findPreference(KEY_GPU_FREQ);
        mGPUFreq.setOnPreferenceClickListener(this);
        mGPUFreq.setEnabled(isSharkL2Support());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceClick(Preference pref) {
        if (pref == mGPUFreq) {
            popDialog();
        }
        return true;
    }

    private void popDialog() {
        mGpuFreq = getmGpuFreq();
        mGPUCurrentFreq = getmGpuCurrentFreq();
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        if(mGpuFreq != null && mGPUCurrentFreq != null) {
            alertBuilder.setTitle("gpu freq").setSingleChoiceItems(mGpuFreq,
                    getCheckedItemPosition(mGPUCurrentFreq), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which >= 0) {
                                setGpuCurrent(mGpuFreq[which]);
                                if(!readFile(GPU_FEEQ_PATH + GPU_FEEQ_MAX).contains(mGpuFreq[which])) {
                                    setGpuMaxFreq(mGpuFreq[which]);
                                }
                                if(!readFile(GPU_FEEQ_PATH + GPU_FEEQ_MIN).contains(mGpuFreq[which])) {
                                    setGpuMinFreq(mGpuFreq[which]);
                                }
                                mGPUFreq.setSummary(getmGpuCurrentFreq());
                            }
                        }
                    });
        } else {
            alertBuilder.setTitle("gpu freq").setMessage("read error");
        }

        alertBuilder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mGPUFreq.setSummary(getmGpuCurrentFreq());
                    }
        });
        AlertDialog mAlertDialog = alertBuilder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.show();
    }

    private int getCheckedItemPosition(String currentFreq) {
        int index = 0;
        for(String str : mGpuFreq) {
            if(currentFreq.contains(str)) {
                break;
            }
            index++;
        }
        return index;
    }

    private void setGpuCurrent(String current) {
        setGpuMaxFreq(current);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setGpuMinFreq(current);
    }

    private String setGpuMaxFreq(String current) {
        return CmdUtils.run("echo " +
            current + ">" + GPU_FEEQ_PATH +
            GPU_FEEQ_MAX);
    }

    private String setGpuMinFreq(String current) {
        return CmdUtils.run("echo " +
            current + ">" + GPU_FEEQ_PATH +
            GPU_FEEQ_MIN);
    }

    private String getmGpuCurrentFreq() {
        return readFile(GPU_FEEQ_PATH + GPU_FEEQ_CURRENT);
    }

    private String[] getmGpuFreq() {
        String available = readFile(GPU_FEEQ_PATH + GPU_FEEQ_AVAILABLE);
        if(available != null) {
            String[] ret =  available.split(" ");
            for(int i = 0; i < ret.length; i++) {
                ret[i] = ret[i].trim();
            }
            return ret;
        }
        return null;
    }

    private String readFile(String path) {
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        String data = null;
        try {
            File file = new File(path);
            fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len = 0;
            bos = new ByteArrayOutputStream();
            while ((len = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            byte[] dataByte = bos.toByteArray();
            data = new String(dataByte, 0, dataByte.length);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }
    public boolean isSharkL2Support() {
        String hardware = SystemPropertiesProxy.get("ro.boot.hardware", "unknown");
        if (hardware.contains("9850")) {
            return true;
        }
        return false;
    }
}
