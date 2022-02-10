package com.sprd.engineermode.telephony;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;
import android.widget.Toast;

import com.unisoc.engineermode.core.CoreApi;
import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.common.Const;

public class UeCapLteActivity extends PreferenceActivity implements OnPreferenceChangeListener {

    private static final String TAG = "UeCapLteActivity";
    private static final String KEY_ANR = "anr";
    private static final String KEY_DLCA = "dlca";
    private static final String KEY_ULCA = "ulca";
    private static final String KEY_TM9 = "tm9";
    private static final String KEY_EICIC = "eicic";
    private static final String KEY_EMBMS = "embms";
    private static final String KEY_MDT = "mdt";
    private static final String KEY_MTA = "mta";
    private static final String KEY_EMFBI = "emfbi";
    private static final String KEY_FBI = "fbi";
    private static final String KEY_UL_64QAM = "ul_64qam";

    private static final String OPEN = "1";
    private static final String CLOSE = "0";

    private static final int OPEN_ANR = 1;
    private static final int CLOSE_ANR = 2;
    private static final int OPEN_DLCA = 3;
    private static final int CLOSE_DLCA = 4;
    private static final int OPEN_ULCA = 5;
    private static final int CLOSE_ULCA = 6;
    private static final int OPEN_TM9 = 7;
    private static final int CLOSE_TM9 = 8;
    private static final int OPEN_EICIC = 9;
    private static final int CLOSE_EICIC = 10;
    private static final int OPEN_EMBMS = 11;
    private static final int CLOSE_EMBMS = 12;
    private static final int OPEN_MDT = 13;
    private static final int CLOSE_MDT = 14;
    private static final int OPEN_MTA = 15;
    private static final int CLOSE_MTA = 16;
    private static final int OPEN_EMFBI = 17;
    private static final int CLOSE_EMFBI = 18;
    private static final int OPEN_FBI = 19;
    private static final int CLOSE_FBI = 20;
    private static final int OPEN_UL_64QAM = 21;
    private static final int CLOSE_UL_64QAM = 22;
    private static final int GET_ANR = 23;
    private static final int GET_DLCA = 24;
    private static final int GET_ULCA = 25;
    private static final int GET_TM9 = 26;
    private static final int GET_EICIC = 27;
    private static final int GET_EMBMS = 28;
    private static final int GET_MDT = 29;
    private static final int GET_MTA = 30;
    private static final int GET_EMFBI = 31;
    private static final int GET_FBI = 32;
    private static final int GET_UL_64QAM = 33;

    private ITelephonyApi teleApi = CoreApi.getTelephonyApi();
    private MyHandler mHandler;
    private Handler mUiThread = new Handler();

    private String mATCmd;
    private String mStrTmp;
    private Context mContext;
    private int simIdx = UeNwCapActivity.mSimIndex;
    //private String[] values = null;

    private SwitchPreference mPreDlca;
    private SwitchPreference mPreUlca;
    private SwitchPreference mPreUl64qam;
    /*
    private SwitchPreference mPreAnr;
    private SwitchPreference mPreEmbms;
    private SwitchPreference mPreTm9;
    private SwitchPreference mPreMdt;
    private SwitchPreference mPreEicic;
    private SwitchPreference mPreMta;
    private SwitchPreference mPreEmfbi;
    private SwitchPreference mPreFbi;
    */
    private SwitchPreference[] LteUeCap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_uecap_lte);
        mContext = this;
        //mPreAnr = (SwitchPreference)findPreference(KEY_ANR);
        //mPreAnr.setOnPreferenceChangeListener(this);
        mPreDlca = (SwitchPreference)findPreference(KEY_DLCA);
        mPreDlca.setOnPreferenceChangeListener(this);
        //mPreDlca.setEnabled(false);
        mPreUlca = (SwitchPreference)findPreference(KEY_ULCA);
        mPreUlca.setOnPreferenceChangeListener(this);

        /*mPreEmbms = (SwitchPreference)findPreference(KEY_EMBMS);
        mPreEmbms.setOnPreferenceChangeListener(this);
        mPreTm9 = (SwitchPreference)findPreference(KEY_TM9);
        mPreTm9.setOnPreferenceChangeListener(this);
        mPreTm9.setEnabled(false);
        mPreMdt = (SwitchPreference)findPreference(KEY_MDT);
        mPreMdt.setOnPreferenceChangeListener(this);
        mPreEicic = (SwitchPreference)findPreference(KEY_EICIC);
        mPreEicic.setOnPreferenceChangeListener(this);
        mPreMta = (SwitchPreference)findPreference(KEY_MTA);
        mPreMta.setOnPreferenceChangeListener(this);
        mPreEmfbi = (SwitchPreference)findPreference(KEY_EMFBI);
        mPreEmfbi.setOnPreferenceChangeListener(this);
        mPreFbi = (SwitchPreference)findPreference(KEY_FBI);
        mPreFbi.setOnPreferenceChangeListener(this);*/
        mPreUl64qam = (SwitchPreference)findPreference(KEY_UL_64QAM);
        mPreUl64qam.setOnPreferenceChangeListener(this);
        //LteUeCap = new SwitchPreference[] {mPreAnr,mPreFbi,mPreDlca,mPreUlca,mPreTm9,mPreEicic,
        //      mPreEmbms,mPreMdt,mPreMta,mPreEmfbi,mPreUl64qam};
        LteUeCap = new SwitchPreference[] {mPreDlca,mPreUlca,mPreUl64qam};

        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mHandler = new MyHandler(ht.getLooper());
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onstart");
        /*
        if (mPreAnr != null) {
            Message getAnr = mHandler.obtainMessage(GET_ANR);
            mHandler.sendMessage(getAnr);
        }
        if (mPreDlca != null) {
            Message getDlca = mHandler.obtainMessage(GET_DLCA);
            mHandler.sendMessage(getDlca);
        }
        if (mPreUlca != null) {
            Message getUlca = mHandler.obtainMessage(GET_ULCA);
            mHandler.sendMessage(getUlca);
        }
        if (mPreEmbms != null) {
            Message getEmbms = mHandler.obtainMessage(GET_EMBMS);
            mHandler.sendMessage(getEmbms);
        }
        if (mPreTm9 != null) {
            Message getTm9 = mHandler.obtainMessage(GET_TM9);
            mHandler.sendMessage(getTm9);
        }
        if (mPreMdt != null) {
            Message getMdt = mHandler.obtainMessage(GET_MDT);
            mHandler.sendMessage(getMdt);
        }
        if (mPreEicic != null) {
            Message getEicic = mHandler.obtainMessage(GET_EICIC);
            mHandler.sendMessage(getEicic);
        }
        if (mPreMta != null) {
            Message getMta = mHandler.obtainMessage(GET_MTA);
            mHandler.sendMessage(getMta);
        }
        if (mPreEmfbi != null) {
            Message getEmfbi = mHandler.obtainMessage(GET_EMFBI);
            mHandler.sendMessage(getEmfbi);
        }
        if (mPreFbi != null) {
            Message getFbi = mHandler.obtainMessage(GET_FBI);
            mHandler.sendMessage(getFbi);
        }
        if (mPreUl64qam != null) {
            Message getUl64qam = mHandler.obtainMessage(GET_UL_64QAM);
            mHandler.sendMessage(getUl64qam);
        }
        */
/*
 * "ANR_LTE,ANR_W,ANR_G",  //the capability to read CGI of lte/w/g in lte
        "mFBI",
        "DL_CA,UL_CA",
        "TM9",
        "UL_MIMO",
        "eICIC",
        "embms",
        "MDT",
        "MTA",
        "eMFBI",
        "uL 64QAM"

 * */
        //values = this.getResources().getStringArray(R.array.Lte_Outfield_Network);
        //send AT cmd:         AT+SPENGMD=0,0,7
        //the return value is: 0-0-0-5-1-3,1,1-0-1,0-0,1-1-2-0-3-3-1,0-/1,0,1-1-1,1-0-0-0-0-0-0-1-0-0,0

        /*
        int num = 0;
        String result = IATUtils.sendATCmd("AT+SPENGMD=0,0,7", UeCapActivity.saveName);
        Log.d(TAG, "AT+SPENGMD=0,0,7: " + result);
        if (result.contains(IATUtils.AT_OK)) {
            result = result.replaceAll("--", "-+");
            String[] str1 = result.split("\n");
            String[] str2 = str1[0].split("-");
            if (str2.length < 27) {
                Log.d(TAG, "wrong data");
                for (int i=0; i<LteUeCap.length; i++) {
                    LteUeCap[i].setEnabled(false);
                }
            }else {
                for (int i=15; i<str2.length; i++) {
                    if (i == 17) {
                        String[] ca = str2[i].split(",");
                        if (ca.length < 2) {
                            Log.d(TAG, "data wrong");
                            continue;
                        }
                        LteUeCap[num++].setChecked(ca[0].equals("1") ? true : false);
                        LteUeCap[num++].setChecked(ca[1].equals("1") ? true : false);
                        continue;
                    }
                    if (i == 25) {
                        LteUeCap[num++].setChecked(str2[i].equals("1") ? true : false);
                    }
                }
            }
        }
        */
        boolean status[] = new boolean[] {false, false, false};
        if (teleApi.lteUeCapApi().getCap(simIdx, status)) {
            for (int i = 0; i < 3; i++) {
                LteUeCap[i].setChecked(status[i]);
            }
        } else {
            for (SwitchPreference aLteUeCap : LteUeCap) {
                aLteUeCap.setEnabled(false);
            }
        }
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class MyHandler extends Handler {
        AlertDialog alertDialog;

        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OPEN_DLCA:
                    Log.d(TAG, "OPEN_DLCA");
                    if (teleApi.lteUeCapApi().openDlca(simIdx)) {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mPreDlca.setChecked(true);
                                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mPreDlca.setChecked(false);
                                Toast.makeText(mContext, "Change status is not supported", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
                case CLOSE_DLCA:
                    Log.d(TAG, "CLOSE_DLCA");
                    if (teleApi.lteUeCapApi().closeDlca(simIdx)) {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mPreDlca.setChecked(false);
                                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mPreDlca.setChecked(true);
                                Toast.makeText(mContext, "Change status is not supported", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
                /* SPRD Bug 909245 UL CA、UL 64QAM is no effect @{ */
                case OPEN_ULCA:
                    Log.d(TAG, "OPEN_ULCA");
                    if (teleApi.lteUeCapApi().openUlca(simIdx) && !Const.isBoardCAT4()) {
                        chooseToReboot(OPEN_ULCA);
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mPreUlca.setChecked(false);
                                Toast.makeText(mContext, "Change status is not supported", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
                case CLOSE_ULCA:
                    Log.d(TAG, "CLOSE_ULCA");
                    if (teleApi.lteUeCapApi().closeUlca(simIdx) && !Const.isBoardCAT4()) {
                        chooseToReboot(CLOSE_ULCA);
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mPreUlca.setChecked(true);
                                Toast.makeText(mContext, "Change status is not supported", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
                case OPEN_UL_64QAM:
                    Log.d(TAG, "OPEN_UL_64QAM");
                    if (teleApi.lteUeCapApi().openUl64Qam(simIdx) && !Const.isBoardCAT4()) {
                        chooseToReboot(OPEN_UL_64QAM);
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mPreUl64qam.setChecked(false);
                                Toast.makeText(mContext, "Change status is not supported", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
                case CLOSE_UL_64QAM:
                    Log.d(TAG, "CLOSE_UL_64QAM");
                    if (teleApi.lteUeCapApi().closeUl64Qam(simIdx) && !Const.isBoardCAT4()) {
                        chooseToReboot(CLOSE_UL_64QAM);
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mPreUl64qam.setChecked(true);
                                Toast.makeText(mContext, "Change status is not supported", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
                default:
                    break;
                /* @} */
            }
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String key = preference.getKey();
        if (KEY_DLCA.equals(key)){
            if (!mPreDlca.isChecked()) {
                Message openDlca = mHandler.obtainMessage(OPEN_DLCA);
                mHandler.sendMessage(openDlca);
            } else {
                Message closeDlca = mHandler.obtainMessage(CLOSE_DLCA);
                mHandler.sendMessage(closeDlca);
            }
        } else if (KEY_ULCA.equals(key)) {
            if (!mPreUlca.isChecked()) {
                Message openUlca = mHandler.obtainMessage(OPEN_ULCA);
                mHandler.sendMessage(openUlca);
            } else {
                Message closeUlca = mHandler.obtainMessage(CLOSE_ULCA);
                mHandler.sendMessage(closeUlca);
            }
        } else if (KEY_UL_64QAM.equals(key)) {
            if (!mPreUl64qam.isChecked()) {
                Message openUl64qam = mHandler.obtainMessage(OPEN_UL_64QAM);
                mHandler.sendMessage(openUl64qam);
            } else {
                Message closeUl64qam = mHandler.obtainMessage(CLOSE_UL_64QAM);
                mHandler.sendMessage(closeUl64qam);
            }
        }
        return false;
    }

    public void chooseToReboot(final int type) {
        AlertDialog alertDialog = new AlertDialog.Builder(mContext)
        .setMessage(getString(R.string.choose_to_reboot))
        .setCancelable(false)
        .setPositiveButton(getString(R.string.alertdialog_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (type) {
                            /* SPRD Bug 909245 UL CA、UL 64QAM is no effect @{ */
                            case OPEN_ULCA:
                                mUiThread.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mPreUlca.setChecked(true);
                                        Toast.makeText(mContext, "Success",Toast.LENGTH_SHORT).show();
                                        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
                                        pm.reboot("openULCA");
                                    }
                                });
                                break;
                            case CLOSE_ULCA:
                                mUiThread.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mPreUlca.setChecked(false);
                                        Toast.makeText(mContext, "Success",Toast.LENGTH_SHORT).show();
                                        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
                                        pm.reboot("closeULCA");
                                    }
                                });
                                break;
                            case OPEN_UL_64QAM:
                                mUiThread.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mPreUl64qam.setChecked(true);
                                        Toast.makeText(mContext, "Success",Toast.LENGTH_SHORT).show();
                                        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
                                        pm.reboot("openUL64QAM");
                                    }
                                });
                                break;
                            case CLOSE_UL_64QAM:
                                mUiThread.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mPreUl64qam.setChecked(false);
                                        Toast.makeText(mContext, "Success",Toast.LENGTH_SHORT).show();
                                        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
                                        pm.reboot("closeUL64QAM");
                                    }
                                });
                                break;
                            default:
                                break;
                            /* @} */
                        }
                    }
                })
        .setNegativeButton(R.string.alertdialog_cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
        alertDialog.show();
    }

}
