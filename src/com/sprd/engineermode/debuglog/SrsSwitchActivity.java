
package com.sprd.engineermode.debuglog;

import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import com.unisoc.engineermode.core.utils.IATUtils;
import android.util.Log;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.sprd.engineermode.R;

public class SrsSwitchActivity extends PreferenceActivity implements OnPreferenceChangeListener{

    private static final String TAG = "SrsSwitchActivity";
    private static final String KEY_N41 = "key_srs_n41";
    private static final String KEY_N77 = "key_srs_n77";
    private static final String KEY_N78 = "key_srs_n78";
    private static final String KEY_N79 = "key_srs_n79";
    private CheckBoxPreference mSrsN41 = null;
    private CheckBoxPreference mSrsN77 = null;
    private CheckBoxPreference mSrsN78 = null;
    private CheckBoxPreference mSrsN79 = null;
    private String [] mInitState = {"0","0","0","0"};
    private Handler mUiThread = new Handler();
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_srs_switch);

        mSrsN41 = (CheckBoxPreference) findPreference(KEY_N41);
        mSrsN41.setOnPreferenceChangeListener(this);
        mSrsN77 = (CheckBoxPreference) findPreference(KEY_N77);
        mSrsN77.setOnPreferenceChangeListener(this);
        mSrsN78 = (CheckBoxPreference) findPreference(KEY_N78);
        mSrsN78.setOnPreferenceChangeListener(this);
        mSrsN79 = (CheckBoxPreference) findPreference(KEY_N79);
        mSrsN79.setOnPreferenceChangeListener(this);
        mContext = this;
    }

    @Override
    public void onStart() {
        getSrsMode();
        super.onStart();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d(TAG, "key:" + preference.getKey() + "," + "value:" + newValue);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.srs_set, menu);
        MenuItem item = menu.findItem(R.id.srs_set);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.srs_set: {
                AlertDialogShow();
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setSrsMode() {
        boolean isSuccess = false;
        if (mSrsN41.isChecked()) {
            isSuccess = sendSrsCmd("41","1");
        } else {
            isSuccess = sendSrsCmd("41","7");
        }
        if (mSrsN77.isChecked()) {
            isSuccess = sendSrsCmd("77","1");
        } else {
            isSuccess = sendSrsCmd("77","7");
        }
        if (mSrsN78.isChecked()) {
            isSuccess = sendSrsCmd("78","1");
        } else {
            isSuccess = sendSrsCmd("78","7");
        }
        if (mSrsN79.isChecked()) {
            isSuccess = sendSrsCmd("79","1");
        } else {
            isSuccess = sendSrsCmd("79","7");
        }
        if (isSuccess) {
            rebootDevice("srs_switch");
        } else {
            mSrsN41.setChecked("1".equals(mInitState[0]));
            mSrsN77.setChecked("1".equals(mInitState[1]));
            mSrsN78.setChecked("1".equals(mInitState[2]));
            mSrsN79.setChecked("1".equals(mInitState[3]));
            Toast.makeText(getApplicationContext(),
                    "srs set failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void getSrsMode() {
        String atCmd = "AT+SP5GCMDS=\"get nr synch_param\",41";
        String result = IATUtils.sendATCmd(atCmd, "atchannel0");
        Log.d(TAG,"reslut:" + result);
        if (result != null && result.contains(IATUtils.AT_OK)) {
            String[] str = result.split("\n");
            String[] str1 = str[0].split(",");
            for (int i = 0;i < 4;i++){
               mInitState[i] = str1[i+2].trim();
            }
            mSrsN41.setChecked("1".equals(mInitState[0]));
            mSrsN77.setChecked("1".equals(mInitState[1]));
            mSrsN78.setChecked("1".equals(mInitState[2]));
            mSrsN79.setChecked("1".equals(mInitState[3]));
        } else {
            Toast.makeText(getApplicationContext(),
                    "get srs failed",Toast.LENGTH_SHORT).show();
        }

    }

    public void AlertDialogShow() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage(getString(R.string.qrm_switch_waring))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.alertdialog_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setSrsMode();
                            }
                        })
                .setNegativeButton(R.string.alertdialog_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                mUiThread.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mSrsN41.setChecked("1".equals(mInitState[0]));
                                        mSrsN77.setChecked("1".equals(mInitState[1]));
                                        mSrsN78.setChecked("1".equals(mInitState[2]));
                                        mSrsN79.setChecked("1".equals(mInitState[3]));
                                    }
                                });
                            }
                        }).create();
        alertDialog.show();
    }

    private boolean sendSrsCmd(String band , String value){
        String atCmd = "AT+SP5GCMDS=\"set nr param\",23,0," + band + "," + value;
        String result = IATUtils.sendATCmd(atCmd, "atchannel0");
        return result != null && result.contains(IATUtils.AT_OK);
    }

    private void rebootDevice(String reasonStr) {
        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        pm.reboot(reasonStr);
    }
}
