
package com.sprd.engineermode.debuglog;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.sprd.engineermode.R;
import com.sprd.engineermode.telephony.TelephonyFragment;
import com.unisoc.engineermode.core.impl.nonpublic.TelephonyManagerProxy;
import com.unisoc.engineermode.core.utils.IATUtils;
import android.content.Context;
import android.os.PowerManager;
import android.widget.Button;

public class BandModeSetActivity extends PreferenceActivity implements
        Preference.OnPreferenceClickListener {

    private static final String TAG = "BandModeSetActivity";
    private static final int KEY_SAVE_BAND = 1;
    private static final int KEY_SET_REBOOTBUTTON_TEXT = 2;
    private PreferenceGroup mPreGroup = null;
    private ProgressDialog mProgressDlg;
    private BandSelector mBandSelector;
    private FBHandler mFBHandler;
    private int mPhoneID = -1;
    private Handler mUiThread = new Handler();
    public static BandModeSetActivity BandModeSetActivityInstance = null;

    /* SPRD Bug 950776:Modem Assert. @{ */
    private Context mContext;
    private TelephonyManager mTelephonyManager;
    private boolean[] mIsCardExit;
    private int mPhoneCount;
    private Runnable mSuccessRunnable = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(getApplicationContext(), "Modem reset success!", Toast.LENGTH_SHORT).show();
            return;
        }
    };

    private Runnable mFailRunnable = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(getApplicationContext(), "Modem reset failed!", Toast.LENGTH_SHORT).show();
            return;
        }
    };
    /* @} */

    class FBHandler extends Handler {
        private int rebootDelay;
        private Button positiveBtn;

        public FBHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, " handleMessage:" + msg.what);
            switch (msg.what) {
                case KEY_SET_REBOOTBUTTON_TEXT:
                    positiveBtn.setText(mContext.getResources().getString(R.string.alertdialog_reboot_count) + " (" + rebootDelay + ")");
                    if (rebootDelay == 0) {
                        positiveBtn.setEnabled(false);
                    }
                    if (rebootDelay == -1) {
                        rebootDevice("auto reboot after BandModeSetActivity save band");
                    }
                    break;
                case KEY_SAVE_BAND:
                    showProgressDialog("Saving band");
                    mBandSelector.saveBand();
                    dismissProgressDialog();
                    final AlertDialog alertDialog = new AlertDialog.Builder(
                            BandModeSetActivity.this)
                            .setTitle("Band Select")
                            .setCancelable(false)
                            .setMessage(mBandSelector.getSetInfo() + mContext.getResources().getString(R.string.alertdialog_reboot_message))
                            .setPositiveButton(R.string.alertdialog_reboot_count,
                                    (dialog, which) -> {
                                    }).create();
                    alertDialog.show();

                    positiveBtn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    positiveBtn.setEnabled(false);
                    new Thread(() -> {
                        for (int i = 5; i > -2; i--) {
                            rebootDelay = i;
                            mFBHandler.sendEmptyMessage(KEY_SET_REBOOTBUTTON_TEXT);
                            try {
                                if (rebootDelay > -1) Thread.sleep(1000);
                            } catch (Exception ex) {
                                Log.e(TAG, ex.toString(), ex);
                            }
                        }
                    }).start();
                    break;
            }
        }
    }

    private void rebootDevice(String reasonStr) {
        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        pm.reboot(reasonStr);
    }

    /* SPRD Bug 950776:Modem Assert. @{ */
    private void getCardExitState() {
        mTelephonyManager = TelephonyManagerProxy.getService();
        for (int i = 0; i < mPhoneCount; i++) {
            if (mTelephonyManager != null && mTelephonyManager.getSimState(i) == TelephonyManager.SIM_STATE_READY) {
                mIsCardExit[i] = true;
            } else {
                mIsCardExit[i] = false;
            }
            Log.d(TAG, "mIsCardExit[" + i + "] = " + mIsCardExit[i]);
        }
    }
    /* @} */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mFBHandler = new FBHandler(ht.getLooper());

        setPreferenceScreen(getPreferenceManager().createPreferenceScreen(this));
        mPreGroup = getPreferenceScreen();

        mPhoneID = getIntent().getIntExtra(TelephonyFragment.KEY_PHONEID, 0);
        Log.d(TAG, "onCreate mPhoneID:" + mPhoneID);
        mBandSelector = new BandSelector(mPhoneID, this, mUiThread);
        BandModeSetActivityInstance = this;

        /* SPRD Bug 950776:Modem Assert. @{ */
        mContext = getApplicationContext();
        mPhoneCount = TelephonyManagerProxy.getPhoneCount();
//        mTelephonyManager = new TelephonyManager[mPhoneCount];
        mIsCardExit = new boolean[mPhoneCount];
        getCardExitState();
        /* @} */
    }

    @Override
    protected void onStart() {
        mBandSelector.initModes(mPreGroup);
        mBandSelector.loadBands();
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        if (mFBHandler != null) {
            mFBHandler.getLooper().quit();
        }
        BandModeSetActivityInstance = null;
        super.onDestroy();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.frequency_set, menu);
        MenuItem item = menu.findItem(R.id.frequency_set);
        if (item != null) {
            item.setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.frequency_set: {
                if (!mBandSelector.isCheckOneOrMore()) {
                    Toast.makeText(getApplicationContext(),
                            "Please check at least one every mode!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    mFBHandler.sendEmptyMessage(KEY_SAVE_BAND);
                }
            }
                break;
            default:
                Log.i(TAG, "default");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showProgressDialog(final String msg) {
        mUiThread.post(new Runnable() {
            @Override
            public void run() {
                mProgressDlg = ProgressDialog.show(BandModeSetActivity.this,
                        msg, "Please wait...", true, false);
            }
        });
    }

    private void dismissProgressDialog() {
        mUiThread.post(new Runnable() {
            @Override
            public void run() {
                if (mProgressDlg != null) {
                    mProgressDlg.dismiss();
                }
            }
        });
    }

}
