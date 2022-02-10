package com.sprd.engineermode.telephony;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;
import android.widget.Toast;

import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.utils.IATUtils;

public class UeCapVolteActivity extends PreferenceActivity implements OnPreferenceChangeListener {

    private static final String TAG = "UeCapVolteActivity";
    private static final String KEY_ASRVCC = "asrvcc";
    private static final String KEY_BSRVCC = "bsrvcc";
    private static final String KEY_EVS = "evs";

    private static final String OPEN = "1";
    private static final String CLOSE = "0";

    private static final int OPEN_ASRVCC = 1;
    private static final int CLOSE_ASRVCC = 2;
    private static final int OPEN_BSRVCC = 3;
    private static final int CLOSE_BSRVCC = 4;
    private static final int OPEN_EVS = 5;
    private static final int CLOSE_EVS = 6;
    private static final int GET_ASRVCC = 7;
    private static final int GET_BSRVCC = 8;
    private static final int GET_EVS = 9;
    private MyHandler mHandler;
    private Handler mUiThread = new Handler();

    private String mATCmd;
    private String mStrTmp;
    private Context mContext;

    private SwitchPreference mPreAsrvcc;
    private SwitchPreference mPreBsrvcc;
    private SwitchPreference mPreEvs;


@Override
protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.pref_uecap_volte);
    mContext = this;
    mPreAsrvcc = (SwitchPreference)findPreference(KEY_ASRVCC);
    mPreAsrvcc.setOnPreferenceChangeListener(this);
    mPreAsrvcc.setEnabled(false);
    mPreBsrvcc = (SwitchPreference)findPreference(KEY_BSRVCC);
    mPreBsrvcc.setOnPreferenceChangeListener(this);
    mPreBsrvcc.setEnabled(false);
    mPreEvs = (SwitchPreference)findPreference(KEY_EVS);
    mPreEvs.setOnPreferenceChangeListener(this);
    mPreEvs.setEnabled(false);
    HandlerThread ht = new HandlerThread(TAG);
    ht.start();
    mHandler = new MyHandler(ht.getLooper());
}

@Override
protected void onStart() {
    Log.d(TAG, "onstart");
    if (mPreAsrvcc != null) {
        Message getAsrvcc = mHandler.obtainMessage(GET_ASRVCC);
        mHandler.sendMessage(getAsrvcc);
    }
    if (mPreBsrvcc != null) {
        Message getBsrvcc = mHandler.obtainMessage(GET_BSRVCC);
        mHandler.sendMessage(getBsrvcc);
    }
    if (mPreEvs != null) {
        Message getEvs = mHandler.obtainMessage(GET_EVS);
        mHandler.sendMessage(getEvs);
    }
    super.onStart();
}

@Override
protected void onDestroy() {
    // TODO Auto-generated method stub
    super.onDestroy();
}

private class MyHandler extends Handler {
    public MyHandler(Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
        case OPEN_ASRVCC:
            Log.d(TAG, "OPEN_ASRVCC");
            mATCmd = engconstents.ENG_AT_SET_ASRVCC + OPEN;
            mStrTmp = IATUtils.sendATCmd(mATCmd, UeCapActivity.saveName);
            if (mStrTmp.contains(IATUtils.AT_OK)) {
                mUiThread.post(new Runnable() {
                    @Override
                    public void run() {
                        mPreAsrvcc.setChecked(true);
                        Toast.makeText(mContext, "Success",Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                mUiThread.post(new Runnable() {
                    @Override
                    public void run() {
                        mPreAsrvcc.setChecked(false);
                        Toast.makeText(mContext, "Change status is not supported", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            break;
        case CLOSE_ASRVCC:
            Log.d(TAG, "CLOSE_ASRVCC");
            mATCmd = engconstents.ENG_AT_SET_ASRVCC + CLOSE;
            mStrTmp = IATUtils.sendATCmd(mATCmd, UeCapActivity.saveName);
            if (mStrTmp.contains(IATUtils.AT_OK)) {
                mUiThread.post(new Runnable() {
                    @Override
                    public void run() {
                        mPreAsrvcc.setChecked(false);
                        Toast.makeText(mContext, "Success",Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                mUiThread.post(new Runnable() {
                    @Override
                    public void run() {
                        mPreAsrvcc.setChecked(true);
                        Toast.makeText(mContext, "Change status is not supported", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            break;
        case GET_ASRVCC:
            Log.d(TAG, "GET_ASRVCC");
            mATCmd = engconstents.ENG_AT_GET_ASRVCC;
            mStrTmp = IATUtils.sendATCmd(mATCmd, UeCapActivity.saveName);
            if (mStrTmp.contains(IATUtils.AT_OK)) {
                String str = "25,0,";
                int index = mStrTmp.indexOf(str);
                if (index >= 0 && mStrTmp.substring(index+str.length(), index+str.length()+1).equals("1")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mPreAsrvcc.setChecked(true);
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mPreAsrvcc.setChecked(false);
                        }
                    });
                }
            } else {
                mUiThread.post(new Runnable() {
                    @Override
                    public void run() {
                        mPreAsrvcc.setChecked(false);
                        //Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            break;
        case OPEN_BSRVCC:
            Log.d(TAG, "OPEN_BSRVCC");
            mATCmd = engconstents.ENG_AT_SET_BSRVCC + OPEN;
            mStrTmp = IATUtils.sendATCmd(mATCmd, UeCapActivity.saveName);
            if (mStrTmp.contains(IATUtils.AT_OK)) {
                mUiThread.post(new Runnable() {
                    @Override
                    public void run() {
                        mPreBsrvcc.setChecked(true);
                        Toast.makeText(mContext, "Success",Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                mUiThread.post(new Runnable() {
                    @Override
                    public void run() {
                        mPreBsrvcc.setChecked(false);
                        Toast.makeText(mContext, "Change status is not supported", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            break;
        case CLOSE_BSRVCC:
            Log.d(TAG, "CLOSE_BSRVCC");
            mATCmd = engconstents.ENG_AT_SET_BSRVCC + CLOSE;
            mStrTmp = IATUtils.sendATCmd(mATCmd, UeCapActivity.saveName);
            if (mStrTmp.contains(IATUtils.AT_OK)) {
                mUiThread.post(new Runnable() {
                    @Override
                    public void run() {
                        mPreBsrvcc.setChecked(false);
                        Toast.makeText(mContext, "Success",Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                mUiThread.post(new Runnable() {
                    @Override
                    public void run() {
                        mPreBsrvcc.setChecked(true);
                        Toast.makeText(mContext, "Change status is not supported", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            break;
        case GET_BSRVCC:
            Log.d(TAG, "GET_BSRVCC");
            mATCmd = engconstents.ENG_AT_GET_BSRVCC;
            mStrTmp = IATUtils.sendATCmd(mATCmd, UeCapActivity.saveName);
            if (mStrTmp.contains(IATUtils.AT_OK)) {
                String str = "24,0,";
                int index = mStrTmp.indexOf(str);
                if (index >= 0 && mStrTmp.substring(index+str.length(), index+str.length()+1).equals("1")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mPreBsrvcc.setChecked(true);
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mPreBsrvcc.setChecked(false);
                        }
                    });
                }
            } else {
                mUiThread.post(new Runnable() {
                    @Override
                    public void run() {
                        mPreBsrvcc.setChecked(false);
                        //Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            break;
        case OPEN_EVS:
            Log.d(TAG, "OPEN_EVS");
            mATCmd = engconstents.ENG_AT_SET_EVS + OPEN;
            mStrTmp = IATUtils.sendATCmd(mATCmd, UeCapActivity.saveName);
            if (mStrTmp.contains(IATUtils.AT_OK)) {
                mUiThread.post(new Runnable() {
                    @Override
                    public void run() {
                        mPreEvs.setChecked(true);
                        Toast.makeText(mContext, "Success",Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                mUiThread.post(new Runnable() {
                    @Override
                    public void run() {
                        mPreEvs.setChecked(false);
                        Toast.makeText(mContext, "Change status is not supported", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            break;
        case CLOSE_EVS:
            Log.d(TAG, "CLOSE_EVS");
            mATCmd = engconstents.ENG_AT_SET_EVS + CLOSE;
            mStrTmp = IATUtils.sendATCmd(mATCmd, UeCapActivity.saveName);
            if (mStrTmp.contains(IATUtils.AT_OK)) {
                mUiThread.post(new Runnable() {
                    @Override
                    public void run() {
                        mPreEvs.setChecked(false);
                        Toast.makeText(mContext, "Success",Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                mUiThread.post(new Runnable() {
                    @Override
                    public void run() {
                        mPreEvs.setChecked(true);
                        Toast.makeText(mContext, "Change status is not supported", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            break;
        case GET_EVS:
            Log.d(TAG, "GET_EVS");
            mATCmd = engconstents.ENG_AT_GET_EVS;
            mStrTmp = IATUtils.sendATCmd(mATCmd, UeCapActivity.saveName);
            if (mStrTmp.contains(IATUtils.AT_OK)) {
                String str = "12,0,1,";
                int index = mStrTmp.indexOf(str);
                if (index >= 0 && mStrTmp.substring(index+str.length(), index+str.length()+1).equals("1")) {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mPreEvs.setChecked(true);
                        }
                    });
                } else {
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            mPreEvs.setChecked(false);
                        }
                    });
                }

            } else {
                mUiThread.post(new Runnable() {
                    @Override
                    public void run() {
                        mPreEvs.setChecked(false);
                        //Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            break;
        }
    }
}

@Override
public boolean onPreferenceChange(Preference preference, Object newValue) {
    final String key = preference.getKey();
    if (KEY_ASRVCC.equals(key)) {
        if (!mPreAsrvcc.isChecked()) {
            Message openAsrvcc = mHandler.obtainMessage(OPEN_ASRVCC);
            mHandler.sendMessage(openAsrvcc);
        } else {
            Message closeAsrvcc = mHandler.obtainMessage(CLOSE_ASRVCC);
            mHandler.sendMessage(closeAsrvcc);
        }
    } else if (KEY_BSRVCC.equals(key)){
        if (!mPreBsrvcc.isChecked()) {
            Message openBsrvcc = mHandler.obtainMessage(OPEN_BSRVCC);
            mHandler.sendMessage(openBsrvcc);
        } else {
            Message closeBsrvcc = mHandler.obtainMessage(CLOSE_BSRVCC);
            mHandler.sendMessage(closeBsrvcc);
        }
    } else if (KEY_EVS.equals(key)){
        if (!mPreEvs.isChecked()) {
            Message openEvs = mHandler.obtainMessage(OPEN_EVS);
            mHandler.sendMessage(openEvs);
        } else {
            Message closeEvs = mHandler.obtainMessage(CLOSE_EVS);
            mHandler.sendMessage(closeEvs);
        }
    }
    return false;
}

}
