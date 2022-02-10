// SPRD: add Diversity switch by alisa.li 20160517

package com.sprd.engineermode.telephony;

import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.ListPreference;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.HandlerThread;
import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.intf.ITelephonyApi.IRadioFunc.DualRfState;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import static com.unisoc.engineermode.core.intf.ITelephonyApi.IRadioFunc.WcdmaDualRfState.DIVERSITY_ONLY;
import static com.unisoc.engineermode.core.intf.ITelephonyApi.IRadioFunc.WcdmaDualRfState.PRIMARY_AND_DIVERSITY;
import static com.unisoc.engineermode.core.intf.ITelephonyApi.IRadioFunc.WcdmaDualRfState.PRIMARY_ONLY;

public class DiversityPrefActivity extends PreferenceActivity implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "DiversityPrefActivity";
    private static final String WDIVERSITY = "w_Div";
    private static final String LTESCCTX = "lte_SCC_TX";
    private static final String LTEDIVERSITYRX = "lte_Div_RX";
    private static final String LTEPRIMARYTX = "lte_Pri_TX";
    private static final String LTEPRIMARYRX = "lte_Pri_RX";

    private static final int GET_STATUS = 0;
    private static final int SET_WDiv_STATUS = 1;
    private static final int SET_LTESCCTX_STATUS = 2;
    private static final int SET_LTEDivRX_STATUS = 3;
    private static final int SET_LTEPrimTX_STATUS = 4;
    private static final int SET_LTEPrimRX_STATUS = 5;

    private ITelephonyApi teleApi = CoreApi.getTelephonyApi();

    private ListPreference mWDiversity;
    private ListPreference mLTESCCTX;
    private ListPreference mLTEDiversityRX;
    private ListPreference mLTEPrimTX;
    private ListPreference mLTEPrimRX;

    private String mATCmd;
    private String mATResponse;
    private int PARA1;
    private int PARA2;
    private int type;
    private DualRfState mState;

    private DivHandler mDivHandler;
    private Context mContext;
    private Handler mUiThread = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mDivHandler = new DivHandler(ht.getLooper());

        addPreferencesFromResource(R.xml.pref_diversity);
        mWDiversity = (ListPreference) findPreference(WDIVERSITY);
        mWDiversity.setOnPreferenceChangeListener(this);
        mLTESCCTX = (ListPreference) findPreference(LTESCCTX);
        mLTESCCTX.setOnPreferenceChangeListener(this);
        mLTEDiversityRX = (ListPreference) findPreference(LTEDIVERSITYRX);
        mLTEDiversityRX.setOnPreferenceChangeListener(this);
        mLTEPrimTX = (ListPreference) findPreference(LTEPRIMARYTX);
        mLTEPrimTX.setOnPreferenceChangeListener(this);
        mLTEPrimRX = (ListPreference) findPreference(LTEPRIMARYRX);
        mLTEPrimRX.setOnPreferenceChangeListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Message getStatus = mDivHandler
                .obtainMessage(GET_STATUS);
        mDivHandler.sendMessage(getStatus);
    }

    @Override
    public boolean onPreferenceChange(Preference pref, Object newValue) {
        if (pref == mWDiversity) {
            Message setmDiversitystatus = mDivHandler.obtainMessage(SET_WDiv_STATUS,
                    Integer.parseInt(String.valueOf(newValue)), 0);
            mDivHandler.sendMessage(setmDiversitystatus);
        } else if (pref == mLTESCCTX) {
            Message setmSCCTXstatus = mDivHandler.obtainMessage(SET_LTESCCTX_STATUS,
                    Integer.parseInt(String.valueOf(newValue)), 0);
            mDivHandler.sendMessage(setmSCCTXstatus);
        } else if (pref == mLTEDiversityRX) {
            Message setmDiversityRXstatus = mDivHandler.obtainMessage(SET_LTEDivRX_STATUS,
                    Integer.parseInt(String.valueOf(newValue)), 0);
            mDivHandler.sendMessage(setmDiversityRXstatus);
        } else if (pref == mLTEPrimTX) {
            Message setmPrimaryTXstatus = mDivHandler.obtainMessage(SET_LTEPrimTX_STATUS,
                    Integer.parseInt(String.valueOf(newValue)), 0);
            mDivHandler.sendMessage(setmPrimaryTXstatus);
        } else if (pref == mLTEPrimRX) {
            Log.d(TAG, "start KEY_mLTEPrimRX" + newValue);
            Message setmPrimaryRXstatus = mDivHandler.obtainMessage(SET_LTEPrimRX_STATUS,
                    Integer.parseInt(String.valueOf(newValue)), 0);
            mDivHandler.sendMessage(setmPrimaryRXstatus);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        if (mDivHandler != null) {
            mDivHandler.getLooper().quit();
            Log.d(TAG, "HandlerThread has quit");
        }
        super.onDestroy();
    }

    class DivHandler extends Handler {
        public DivHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_STATUS:
                    try {
                        mState = teleApi.radioFunc().getDualRfState();
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "AT cmd send fail",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    }
                    initUI(mState);
                    break;
                case SET_WDiv_STATUS:
                    type = (int) msg.arg1;
                    Log.d(TAG, "SET_WDiv_STATUS type=" + type);
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "Run Set wDiv Status");
                            mWDiversity.setSummary(mWDiversity.getEntry());
                        }
                    });
                    if (type == 0)
                    {
                        mState.wcdmaState = PRIMARY_ONLY;
                    } else if (type == 1) {
                        mState.wcdmaState = PRIMARY_AND_DIVERSITY;
                    } else {
                        mState.wcdmaState = DIVERSITY_ONLY;
                    }
                    try {
                        teleApi.radioFunc().setDualRfState(mState);
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "AT cmd send fail",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    }

                    break;
                case SET_LTESCCTX_STATUS:
                    type = (int) msg.arg1;
                    Log.d(TAG, "SET_LTESCCTX_STATUS type=" + type);
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "Run Set mLTESCCTX Status");
                            mLTESCCTX.setSummary(mLTESCCTX.getEntry());
                        }
                    });
                    if (type == 0) {
                        mState.isLteSccTxOpened = true;
                    } else if (type == 1) {
                        mState.isLteSccTxOpened = false;
                    }
                    try {
                        teleApi.radioFunc().setDualRfState(mState);
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "AT cmd send fail",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
                case SET_LTEDivRX_STATUS:
                    type = (int) msg.arg1;
                    Log.d(TAG, "SET_LTEDivRX_STATUS type=" + type);
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "Run Set mLTEDiversityRX Status");
                            mLTEDiversityRX.setSummary(mLTEDiversityRX.getEntry());
                        }
                    });
                    if (type == 0) {
                        mState.isLteDiversityRxOpened = true;
                    } else if (type == 1) {
                        mState.isLteDiversityRxOpened = false;
                    }
                    try {
                        teleApi.radioFunc().setDualRfState(mState);
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "AT cmd send fail",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
                case SET_LTEPrimTX_STATUS:
                    type = (int) msg.arg1;
                    Log.d(TAG, "SET_LTEPrimTX_STATUS type=" + type);
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "Run Set mLTEPrimTX Status");
                            mLTEPrimTX.setSummary(mLTEPrimTX.getEntry());
                        }
                    });
                    if (type == 0) {
                        mState.isLtePrimaryTxOpened = true;
                    } else if (type == 1) {
                        mState.isLtePrimaryTxOpened = false;
                    }
                    try {
                        teleApi.radioFunc().setDualRfState(mState);
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "AT cmd send fail",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
                case SET_LTEPrimRX_STATUS:
                    type = (int) msg.arg1;
                    Log.d(TAG, "SET_LTEPrimRX_STATUS type=" + type);
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "Run Set mLTEPrimRX Status");
                            mLTEPrimRX.setSummary(mLTEPrimRX.getEntry());
                        }
                    });
                    if (type == 0) {
                        mState.isLtePrimaryRxOpened = true;
                    } else if (type == 1) {
                        mState.isLtePrimaryRxOpened = false;
                    }
                    try {
                        teleApi.radioFunc().setDualRfState(mState);
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "AT cmd send fail",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
            }
        }
    }

    private void initUI(DualRfState state) {
        PARA1 = PARA1 & ~(1 << 2);
        Log.d(TAG, "initUI() PARA1 = " + PARA1);
        if (mWDiversity != null) {
            mUiThread.post(new Runnable() {
                @Override
                public void run() {
                    switch(state.wcdmaState) {
                        case PRIMARY_ONLY:
                            mWDiversity.setValueIndex(0);
                            mWDiversity.setSummary(mWDiversity.getEntry());
                            Log.d(TAG, "mWDiversity= " + mWDiversity.getEntry());
                            break;
                        case PRIMARY_AND_DIVERSITY:
                            mWDiversity.setValueIndex(1);
                            mWDiversity.setSummary(mWDiversity.getEntry());
                            break;
                        case DIVERSITY_ONLY:
                            mWDiversity.setValueIndex(2);
                            mWDiversity.setSummary(mWDiversity.getEntry());
                            break;
                    }
                }
            });
        }
        if (mLTESCCTX != null) {
            mUiThread.post(new Runnable() {
                @Override
                public void run() {
                    if (state.isLteSccTxOpened) {
                        mLTESCCTX.setValueIndex(0);
                        mLTESCCTX.setSummary(mLTESCCTX.getEntry());
                    } else {
                        mLTESCCTX.setValueIndex(1);
                        mLTESCCTX.setSummary(mLTESCCTX.getEntry());
                    }
                }
            });
        }
        if (mLTEDiversityRX != null) {
            mUiThread.post(new Runnable() {
                @Override
                public void run() {
                    if (state.isLteDiversityRxOpened) {
                        mLTEDiversityRX.setValueIndex(0);
                        mLTEDiversityRX.setSummary(mLTEDiversityRX.getEntry());
                    } else {
                        mLTEDiversityRX.setValueIndex(1);
                        mLTEDiversityRX.setSummary(mLTEDiversityRX.getEntry());
                    }
                }
            });
        }
        if (mLTEPrimTX != null) {
            mUiThread.post(new Runnable() {
                @Override
                public void run() {
                    if (state.isLtePrimaryTxOpened) {
                        mLTEPrimTX.setValueIndex(0);
                        mLTEPrimTX.setSummary(mLTEPrimTX.getEntry());
                    } else {
                        mLTEPrimTX.setValueIndex(1);
                        mLTEPrimTX.setSummary(mLTEPrimTX.getEntry());
                    }
                }
            });
        }
        if (mLTEPrimRX != null) {
            mUiThread.post(new Runnable() {
                @Override
                public void run() {
                    if (state.isLtePrimaryRxOpened) {
                        mLTEPrimRX.setValueIndex(0);
                        mLTEPrimRX.setSummary(mLTEPrimRX.getEntry());
                    } else {
                        mLTEPrimRX.setValueIndex(1);
                        mLTEPrimRX.setSummary(mLTEPrimRX.getEntry());
                    }
                }
            });
        }
    }
}
