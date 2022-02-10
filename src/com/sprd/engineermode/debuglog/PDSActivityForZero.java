
package com.sprd.engineermode.debuglog;

import android.content.Intent;

import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.TwoStatePreference;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;
import android.app.AlertDialog;
import android.os.PowerManager;
import android.content.DialogInterface;
import android.content.Context;
import com.sprd.engineermode.utils.EMFileUtils;
import com.unisoc.engineermode.core.utils.ShellUtils;
import java.util.ArrayList;

public class PDSActivityForZero extends PreferenceActivity implements
        OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private static final String TAG = "PDSActivityForZero";
    private static final String KEY_SET_FREQ = "set_freq";

    private static final String KEY_DVFS = "dvfs";
    private static final String KEY_CPU_FREQUENCE = "cpuFrequence";
    private static final String KEY_MAX_CPU_FREQUENCE = "cpu_frq_max";
    /* SPRD Bug:847281:Power Dissipation setting - CPU Fre can not set. @{ */
    private static final String KEY_CPU_FRE_SWITCH = "cpuFrequenceSwitch";
    private TwoStatePreference mCPUFreSwitch;
    /* @} */

    private PreferenceScreen mCPUPre;

    private static final String DVFS_STATUS_PATH = "/sys/devices/system/cpu/cpufreq/policy0/";
    private static final String USER_SPACE_PATH = "/sys/devices/system/cpu/cpufreq/policy0/scaling_available_governors";
    private static final String CPU_CLUSTER_PATH = "/sys/devices/system/cpu/cpufreq/";
    private static final String CPU_POLICY = "cpu_policy";

    private Preference mDVFSPre;
    private Preference mCpuFreSelect[];
    private ArrayList<String> mOriginalGovernor = new ArrayList<String>();
    private ArrayList<String> mDirs = new ArrayList<String>();
    private ArrayList<String> mPolicyName = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_power_dissipation1);

        mDVFSPre = (Preference) this.findPreference(KEY_DVFS);
        /* SPRD Bug:847281:Power Dissipation setting - CPU Fre can not set. @{ */
        mCPUFreSwitch = (TwoStatePreference) this.findPreference(KEY_CPU_FRE_SWITCH);
        mCPUFreSwitch.setOnPreferenceChangeListener(this);
        /* @} */
        mCPUPre = (PreferenceScreen) this.findPreference(KEY_CPU_FREQUENCE);
        mCPUPre.setOnPreferenceClickListener(this);

        if (isDvfsEnabled()) {
            mDirs = EMFileUtils.getDirectoryName(CPU_CLUSTER_PATH);
            if (mDirs != null) {
                for (String dir : mDirs) {
                    if (dir.contains("policy")) {
                        mPolicyName.add(dir);
                    }
                }
                changeOriginalGovernor();
                initCpuPolicys();
            }
            mDVFSPre.setSummary(R.string.status_opened);
            mCPUPre.setEnabled(true);
            mCPUFreSwitch.setEnabled(true);
        } else {
            mDVFSPre.setSummary(R.string.status_closed);
            mCPUPre.setEnabled(false);
            mCPUFreSwitch.setEnabled(false);
        }
    }

    @Override
    protected void onStart() {
        /* SPRD Bug:847281:Power Dissipation setting - CPU Fre can not set. @{ */
        if (SystemPropertiesProxy.get("persist.vendor.power.hint", "1").equals("1")) {
            mCPUFreSwitch.setChecked(true);
        } else {
            mCPUFreSwitch.setChecked(false);
        }
        /* @} */
        super.onStart();
    }

    private void initCpuPolicys() {
        mCpuFreSelect = new Preference[mPolicyName.size()];
        for (int i = 0; i < mPolicyName.size(); i ++) {
            String curFreq = ShellUtils.execShellCmd("cat " + CPU_CLUSTER_PATH + mPolicyName.get(i) + "/scaling_cur_freq").trim();
            mCpuFreSelect[i] = new Preference(this);
            mCpuFreSelect[i].setTitle(mPolicyName.get(i));
            mCpuFreSelect[i].setKey(mPolicyName.get(i));
            mCpuFreSelect[i].setSummary(Integer.valueOf(curFreq) / 1000 + "mHz");
            mCPUPre.addPreference(mCpuFreSelect[i]);
            mCpuFreSelect[i].setOnPreferenceClickListener(this);
        }
    }

    private void changeOriginalGovernor() {
        for (int i = 0; i < mPolicyName.size(); i ++) {
            String governor = ShellUtils.execShellCmd("cat " + CPU_CLUSTER_PATH + mPolicyName.get(i) + "/scaling_governor").replaceAll("\r|\n", "");
            mOriginalGovernor.add(governor);
            //ShellUtils.execShellCmd("echo userspace > " + CPU_CLUSTER_PATH + mPolicyName.get(i) + "/scaling_governor");
            ShellUtils.writeToFile(CPU_CLUSTER_PATH + mPolicyName.get(i) + "/scaling_governor","userspace");
        }
    }

    private void restoreOriginalGovernor() {
        for (int i = 0; i < mOriginalGovernor.size(); i ++) {
            ShellUtils.execShellCmd("echo " + mOriginalGovernor.get(i) + " > " + CPU_CLUSTER_PATH + mPolicyName.get(i) + "/scaling_governor");
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        /* SPRD Bug:847281:Power Dissipation setting - CPU Fre can not set. @{ */
        if (preference == mCPUFreSwitch) {
            if (mCPUFreSwitch.isChecked()) {
                Log.d(TAG, "mCPUFreSwitch is Checked");
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.cpu_fre_hint_title))
                        .setMessage(getString(R.string.cpu_fre_reboot_hint))
                        /* SPRD Bug:868987. */
                        .setCancelable(false)
                        .setPositiveButton(R.string.alertdialog_ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SystemPropertiesProxy.set("persist.vendor.power.hint", "0");
                                        mCPUFreSwitch.setChecked(false);
                                        Log.d(TAG, "set persist.vendor.power.hint to 0");
                                        PowerManager pm = (PowerManager) PDSActivityForZero.this.getSystemService(Context.POWER_SERVICE);
                                        pm.reboot("switch_powerhint");
                                    }
                                })
                        .setNegativeButton(R.string.alertdialog_cancel,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        return;
                                }
                        }).create();
                alertDialog.show();
            } else {
                Log.d(TAG, "mCPUFreSwitch is not Checked");
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.cpu_fre_hint_title))
                        .setMessage(getString(R.string.cpu_fre_reboot_hint))
                        /* SPRD Bug:868987. */
                        .setCancelable(false)
                        .setPositiveButton(R.string.alertdialog_ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SystemPropertiesProxy.set("persist.vendor.power.hint", "1");
                                        mCPUFreSwitch.setChecked(true);
                                        Log.d(TAG, "set persist.vendor.power.hint to 1");
                                        PowerManager pm = (PowerManager) PDSActivityForZero.this.getSystemService(Context.POWER_SERVICE);
                                        pm.reboot("switch_powerhint");
                                    }
                                })
                        .setNegativeButton(R.string.alertdialog_cancel,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        return;
                                }
                        }).create();
                alertDialog.show();
            }
        }
        /* @} */
        return false;
    }

    @Override
    public void onBackPressed() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
            .setTitle(getString(R.string.cpu_fre_hint_title))
            .setMessage(getString(R.string.restore_cpu_frequency))
            .setPositiveButton(R.string.alertdialog_ok,
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).create();
        alertDialog.show();
    }

    @Override
    public boolean onPreferenceClick(Preference pref) {
        String key = pref.getKey();
        Log.d(TAG, "onPreferenceClick: " + key);
        for (int i = 0; i < mPolicyName.size(); i++) {
            if (key.equals(mCpuFreSelect[i].getKey())) {
                Intent intent = new Intent();
                intent.setClass(this, CpuFreqListActivity.class);
                intent.putExtra(CPU_POLICY, mPolicyName.get(i));
                startActivityForResult(intent, 0);
                break;
            }
        }
        if (pref == mCPUPre) {
            /* SPRD Bug:847281:Power Dissipation setting - CPU Fre can not set. @{ */
            if (SystemPropertiesProxy.get("persist.vendor.power.hint", "1").equals("1")) {
                Log.d(TAG, "persist.vendor.power.hint equals 1");
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.cpu_fre_hint_title))
                        .setMessage(getString(R.string.cpu_fre_setting_hint))
                        .setPositiveButton(R.string.alertdialog_ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        return;
                                    }
                                }).create();
                alertDialog.show();
            }
            /* @} */
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                if (requestCode == 0) {
                    Log.d(TAG,"set Freq is: " + data.getExtras().getString(KEY_SET_FREQ));
                    Log.d(TAG,"set policy is: " + data.getExtras().getString(CPU_POLICY));
                    for (int i = 0; i < mPolicyName.size(); i ++) {
                        if (data.getExtras().getString(CPU_POLICY).equals(mPolicyName.get(i))) {
                            mCpuFreSelect[i].setSummary(data.getExtras().getString(KEY_SET_FREQ));
                        }
                    }
                    Toast.makeText(getApplicationContext(), R.string.dvfs_cpufre_hint, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private boolean isDvfsEnabled() {
        return EMFileUtils.isFileDirExits(DVFS_STATUS_PATH) && ShellUtils.execShellCmd("cat " + USER_SPACE_PATH).contains("userspace");
    }
}
