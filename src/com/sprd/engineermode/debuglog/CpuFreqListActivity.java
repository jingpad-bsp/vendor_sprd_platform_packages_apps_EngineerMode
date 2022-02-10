
package com.sprd.engineermode.debuglog;

import android.widget.ListView;
import android.app.ListActivity;
import android.widget.ArrayAdapter;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.view.View;
import android.content.Intent;

import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;
import com.unisoc.engineermode.core.utils.ShellUtils;

public class CpuFreqListActivity extends ListActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "CpuFreqListActivity";
    private static final String KEY_SET_FREQ = "set_freq";
    private ListView mCpuFreqList;
    private ArrayAdapter<String> mCpuFreqAdapter;
    private String[] mCpuFreq;
    private String[] mCpuFreqSet;
    private String mAvaliableFreq;

    private static final String CPU_POLICY = "cpu_policy";
    private static final String CPU_CLUSTER_PATH = "/sys/devices/system/cpu/cpufreq/";
    private String mPolicy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPolicy = this.getIntent().getStringExtra(CPU_POLICY);
        Log.d(TAG, "mPolicy: " + mPolicy);
        if (mPolicy != null) {
            mAvaliableFreq = getAvaliableFreq();
            mCpuFreqSet = mAvaliableFreq.trim().split(" ");
            mCpuFreq = changeUnit();
            mCpuFreqAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_single_choice,
                    mCpuFreq);
            mCpuFreqList = this.getListView();
            mCpuFreqList.setAdapter(mCpuFreqAdapter);
            mCpuFreqList.setOnItemClickListener(this);
            mCpuFreqList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            initCpuFreq();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String curFreq = mCpuFreqSet[position];
        /* SPRD:811186 one key to set CPU Frequencey @{ */
        SystemPropertiesProxy.set("persist.sys.thermal.ipa", "0");
        /* }@ */
        setFreq(curFreq);
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_SET_FREQ, Integer.valueOf(curFreq) / 1000 + "mHz");
        bundle.putString(CPU_POLICY, mPolicy);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    private String getAvaliableFreq() {
        return ShellUtils.execShellCmd("cat " + CPU_CLUSTER_PATH + mPolicy + "/scaling_available_frequencies");
    }

    private String[] changeUnit() {
        mCpuFreq = new String[mCpuFreqSet.length];
        for (int i = 0; i < mCpuFreqSet.length; i++) {
            Log.d(TAG, "mCpuFreqSet[i]: " + mCpuFreqSet[i]);
            mCpuFreq[i] = Integer.valueOf(mCpuFreqSet[i]) / 1000 + "mHz";
        }
        return mCpuFreq;
    }

    private void initCpuFreq() {
        String curFreq = ShellUtils.execShellCmd("cat " + CPU_CLUSTER_PATH + mPolicy + "/scaling_cur_freq");
        Log.d(TAG, "initCpuFreq000: " + curFreq);
        if (!"".equals(curFreq)) {
            for (int i = 0; i < mCpuFreqSet.length; i++) {
                if (curFreq.trim().equals(mCpuFreqSet[i])) {
                    mCpuFreqList.setItemChecked(i, true);
                }
            }
        }
    }

    private void setFreq(String freq) {
        //ShellUtils.execShellCmd("echo " + freq + " > " + CPU_CLUSTER_PATH + mPolicy + "/scaling_setspeed");
        Log.d(TAG, "setFreq=" + freq);
        ShellUtils.writeToFile(CPU_CLUSTER_PATH + mPolicy + "/scaling_setspeed",freq);
    }
}
