package com.sprd.engineermode.telephony;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;

import com.unisoc.engineermode.core.CoreApi;
import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.impl.nonpublic.TelephonyManagerProxy;

import android.preference.TwoStatePreference;
import android.preference.SwitchPreference;

public class Csfb2GsmDelayActivity extends PreferenceActivity implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "Csfb2GsmDelayActivity";
    private static final String KEY_GRRC_RESIDENT_STATE = "GrrcResidentState";
    private static final String KEY_GRRC_RANDOM_STATE = "GrrcRandomAccessState";
    private static final int KEY_GET_CSFB2GSM_DELAY = 0;
    private static final int KEY_GRRC_RESIDENT_OPEN = 1;
    private static final int KEY_GRRC_RESIDENT_CLOSE = 2;
    private static final int KEY_GRRC_RANDOM_OPEN = 3;
    private static final int KEY_GRRC_RANDOM_CLOSE = 4;

    private ITelephonyApi teleApi = CoreApi.getTelephonyApi();
    PreferenceGroup mPreGroup = null;
    private int mPhoneCount;
    private PreferenceCategory mPreferenceCategory;
    private TwoStatePreference[] mResidentState;
    private TwoStatePreference[] mRandomState;
    private int mSIM = 0;
    private String mResult = null;
    private String mServerName = "atchannel";

    private Csfb2GsmDelayHandler mCsfb2GsmDelayHandler;
    private Handler mUiThread = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhoneCount = TelephonyManagerProxy.getPhoneCount();
        mResidentState = new TwoStatePreference[mPhoneCount];
        mRandomState = new TwoStatePreference[mPhoneCount];
        setPreferenceScreen(getPreferenceManager().createPreferenceScreen(this));
        mPreGroup = getPreferenceScreen();
        for (int i = 0; i < mPhoneCount; i++) {
            mPreferenceCategory = new PreferenceCategory(this);
            mPreferenceCategory.setTitle("SIM" + i);
            mPreGroup.addPreference(mPreferenceCategory);
            mResidentState[i] = new SwitchPreference(this);
            mResidentState[i].setKey(KEY_GRRC_RESIDENT_STATE + i);
            mResidentState[i].setTitle(R.string.grrc_resident_state);
            mResidentState[i].setChecked(false);
            mResidentState[i].setOnPreferenceChangeListener(this);
            mRandomState[i] = new SwitchPreference(this);
            mRandomState[i].setKey(KEY_GRRC_RANDOM_STATE + i);
            mRandomState[i].setTitle(R.string.grrc_random_access_state);
            mRandomState[i].setChecked(false);
            mRandomState[i].setOnPreferenceChangeListener(this);
            mPreGroup.addPreference(mResidentState[i]);
            mPreGroup.addPreference(mRandomState[i]);
        }
        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mCsfb2GsmDelayHandler = new Csfb2GsmDelayHandler(ht.getLooper());

    }

    @Override
    public void onStart() {
        super.onStart();
        Message getCsfb2gsm_delay = mCsfb2GsmDelayHandler
                .obtainMessage(KEY_GET_CSFB2GSM_DELAY);
        mCsfb2GsmDelayHandler.sendMessage(getCsfb2gsm_delay);
    }

    @Override
    protected void onDestroy() {
        if (mCsfb2GsmDelayHandler != null) {
            Log.d(TAG, "HandlerThread has quit");
            mCsfb2GsmDelayHandler.getLooper().quit();
        }
        super.onDestroy();
    }

    @Override
    public boolean onPreferenceChange(Preference pref, Object newValue) {
        for (int i = 0; i < mPhoneCount; i++) {
            if (pref == mResidentState[i]) {
                if (!mResidentState[i].isChecked()) {
                    Message GrrcResidentOpen = mCsfb2GsmDelayHandler
                            .obtainMessage(KEY_GRRC_RESIDENT_OPEN, i);
                    mCsfb2GsmDelayHandler.sendMessage(GrrcResidentOpen);
                } else {
                    Message GrrcResidentClose = mCsfb2GsmDelayHandler
                            .obtainMessage(KEY_GRRC_RESIDENT_CLOSE, i);
                    mCsfb2GsmDelayHandler.sendMessage(GrrcResidentClose);
                }

            } else if (pref == mRandomState[i]) {
                if (!mRandomState[i].isChecked()) {
                    Message GrrcRandomOpen = mCsfb2GsmDelayHandler
                            .obtainMessage(KEY_GRRC_RANDOM_OPEN, i);
                    mCsfb2GsmDelayHandler.sendMessage(GrrcRandomOpen);
                } else {
                    Message GrrcRandomClose = mCsfb2GsmDelayHandler
                            .obtainMessage(KEY_GRRC_RANDOM_CLOSE, i);
                    mCsfb2GsmDelayHandler.sendMessage(GrrcRandomClose);
                }
            }
        }
        return true;
    }
    private void getStatus() {
        for (int i = 0; i < mPhoneCount; i++) {
            mSIM = i;
            final int simIdx = i;
            int[] status = new int[]{0, 0};
            if (teleApi.csfb2GsmDealyApi().getStatus(i, status)) {
                if (status[0] == 1) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mResidentState[simIdx].setChecked(true);
                            mResidentState[simIdx].setEnabled(true);
                            mRandomState[simIdx].setEnabled(false);
                            mRandomState[simIdx].setChecked(false);
                        }
                    });
                } else if (status[1] == 1) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mResidentState[simIdx].setChecked(false);
                            mResidentState[simIdx].setEnabled(false);
                            mRandomState[simIdx].setEnabled(true);
                            mRandomState[simIdx].setChecked(true);
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mResidentState[simIdx].setChecked(false);
                            mResidentState[simIdx].setEnabled(true);
                            mRandomState[simIdx].setChecked(false);
                            mRandomState[simIdx].setEnabled(true);
                        }
                    });
                }
            } else {
                mUiThread.post(new Runnable() {
                    @Override
                    public void run() {
                        mResidentState[simIdx].setChecked(false);
                        mResidentState[simIdx].setEnabled(false);
                        mRandomState[simIdx].setEnabled(false);
                        mRandomState[simIdx].setChecked(false);
                        mResidentState[simIdx].setSummary(R.string.feature_abnormal);
                        mRandomState[simIdx].setSummary(R.string.feature_abnormal);
                    }
                });
            }
        }
    }

    private void openGrrcResident(final int simIdx) {
        if (teleApi.csfb2GsmDealyApi().openGrrcResident(simIdx))
        {
            mUiThread.post(new Runnable() {
                @Override
                public void run() {
                    mResidentState[simIdx].setChecked(true);
                    mRandomState[simIdx].setEnabled(false);
                    Toast.makeText(Csfb2GsmDelayActivity.this, "Success",
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            mUiThread.post(new Runnable() {
                @Override
                public void run() {
                    mResidentState[simIdx].setChecked(false);
                    Toast.makeText(Csfb2GsmDelayActivity.this, "Fail",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void closeGrrcResident(final int simIdx) {

        if (teleApi.csfb2GsmDealyApi().closeGrrcResident(simIdx))
        {
            mUiThread.post(new Runnable() {
                @Override
                public void run() {
                    mResidentState[simIdx].setChecked(false);
                    mRandomState[simIdx].setEnabled(true);
                    Toast.makeText(Csfb2GsmDelayActivity.this, "Success",
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            mUiThread.post(new Runnable() {
                @Override
                public void run() {
                    mResidentState[simIdx].setChecked(true);
                    Toast.makeText(Csfb2GsmDelayActivity.this, "Fail",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void openGrrcRandomAccess(final int simIdx) {
        if (teleApi.csfb2GsmDealyApi().openGrrcRandomAccess(simIdx))
        {
            mUiThread.post(new Runnable() {
                @Override
                public void run() {
                    mRandomState[simIdx].setChecked(true);
                    mResidentState[simIdx].setEnabled(false);
                    Toast.makeText(Csfb2GsmDelayActivity.this, "Success",
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            mUiThread.post(new Runnable() {
                @Override
                public void run() {
                    mRandomState[simIdx].setChecked(false);
                    Toast.makeText(Csfb2GsmDelayActivity.this, "Fail",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void closeGrrcRandomAccess(final int simIdx) {
        if (teleApi.csfb2GsmDealyApi().closeGrrcRandomAccess(simIdx))
        {
            mUiThread.post(new Runnable() {
                @Override
                public void run() {
                    mRandomState[simIdx].setChecked(false);
                    mResidentState[simIdx].setEnabled(true);
                    Toast.makeText(Csfb2GsmDelayActivity.this, "Success",
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            mUiThread.post(new Runnable() {
                @Override
                public void run() {
                    mRandomState[simIdx].setChecked(true);
                    Toast.makeText(Csfb2GsmDelayActivity.this, "Fail",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    class Csfb2GsmDelayHandler extends Handler {

        Csfb2GsmDelayHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case KEY_GET_CSFB2GSM_DELAY:
                getStatus();
                break;
            case KEY_GRRC_RESIDENT_OPEN:
                openGrrcResident((Integer) msg.obj);
                break;
            case KEY_GRRC_RESIDENT_CLOSE:
                closeGrrcResident((Integer) msg.obj);
                break;
            case KEY_GRRC_RANDOM_OPEN:
                openGrrcRandomAccess((Integer) msg.obj);
                break;
            case KEY_GRRC_RANDOM_CLOSE:
                closeGrrcRandomAccess((Integer) msg.obj);
                break;
            default:
                break;
            }
        }
    }

}
