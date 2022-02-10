package com.sprd.engineermode.telephony;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;
import com.unisoc.engineermode.core.impl.telephony.TelephonyManagerSprd;
import com.unisoc.engineermode.core.impl.telephony.TelephonyManagerSprd.RadioCapbility;
import com.unisoc.engineermode.core.intf.ITelephonyApi;

public class NetworkModeActivity extends PreferenceActivity implements
        OnPreferenceChangeListener {

    private static final String TAG = "NetworkModeActivity";
    private static final String KEY_SVLTE = "svlte_key";
    private static final String KEY_FDD = "fdd_csfb_key";
    private static final String KEY_TDD = "tdd_csfb_key";
    private static final String KEY_CSFB = "csfb_key";
    private static final String KEY_TLTWCG = "tltwcg_key";
    private static final String KEY_NRTLWG = "nrtlwg_key";
    private static final String KEY_TDD_SVLTE = "TDD_SVLTE";
    private static final String KEY_FDD_CSFB = "FDD_CSFB";
    private static final String KEY_TDD_CSFB = "TDD_CSFB";
    private static final String KEY_TD_TD = "TD_TD";
    private static final String KEY_TDD_TDD = "TDD_TDD";
    private static final String KEY_LTE_CSFB = "CSFB";
    private static final String KEY_TLTWCG_TLTWCG = "TLTWCG_TLTWCG";
    private static final String KEY_NRTLWG_TLWG = "NRTLWG_TLWG";

    private static final String KEY_SIM_INDEX = "simindex";
    private static final String CHANGE_NETMODE_BY_EM = "persist.sys.cmccpolicy.disable";
    private static final String NEW_MESSAGE = "Notice From EngineerMode!";
    private static final String CLOSE_NOTICE ="After test,please close this notificaiton";
    private static final String NETWORK_NR_ENABLE = "persist.sys.eng.nr.enable";
    private static final String NSA_ONLY_MODE = "0";
    private static final String NR_ONLY_MODE = "1";
    private static final String NORMAL_MODE = "2";

    /* SPRD Bug 849746: network mode switch. @{ */
    private static final int SHOW_PROGRESS_DIALOG = 1;
    private static final int DISMISS_PROGRESS_DIALOG = 0;
    private RadioCapbility mLastRadio = null;
    private int mLastValueIndex = 0;
    /* @} */

    private static final int NOTIFICATION_ID = 0x1123;
    private boolean isSupportTDD = TelephonyManagerSprd.RadioCapbility.TDD_CSFB
            .equals(TelephonyManagerSprd.getRadioCapbility());
    private ListPreference mListPreferenceSVLTE;
    private ListPreference mListPreferenceFDD;
    private ListPreference mListPreferenceTDD;
    private ListPreference mListPreferenceCSFB;
    private ListPreference mListPreferenceTLTWCG;
    private ListPreference mListPreferenceNRTLWG;

    private RadioCapbility mCurrentRadioCapbility;
    private int mCurrentRadioFeatures;
    private SharedPreferences mSharePref;
    private TelephonyManagerSprd mTelephonyManager;
    private ProgressDialog mProgressDialog;

    private NotificationManager mNotifiCation;
    private static final String NOTIFICATION_CHANNEL_ID = "mobileServiceMessages";
    private String simId;
    private ITelephonyApi teleApi = CoreApi.getTelephonyApi();

    /* SPRD Bug 849746: network mode switch. @{ */
    private int mRetValue = 0;
    private LteMainHandler mLteMainHandler = new LteMainHandler();
    public class LteMainHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case SHOW_PROGRESS_DIALOG:
                    showProgressDialog();
                    break;
                case DISMISS_PROGRESS_DIALOG:
                    dismissProgressDialog();
                    updateListPreferenceUI();
                    break;
                default:
                    Log.d(TAG, "LteMainHandler message is wrong!");
            }
        }
    }

    private void showProgressDialog() {
        String progressDialogHint = getResources().getString(R.string.network_mode_switch_hint);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(progressDialogHint);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }

    private void dismissProgressDialog() {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void updateListPreferenceUI () {
        if (mRetValue < 0) {
            retrievePrefIndex(mLastRadio, mLastValueIndex);
            String switchFailedHint = getResources().getString(R.string.network_mode_switch_faided_hint);
            Toast.makeText(this, switchFailedHint, Toast.LENGTH_SHORT).show();
            return;
        }

        if (mCurrentRadioCapbility == TelephonyManagerSprd.RadioCapbility.TDD_CSFB) {
            if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_TD_LTE_TDSCDMA_GSM) {
                SystemPropertiesProxy.set(CHANGE_NETMODE_BY_EM, "false");
            } else {
                SystemPropertiesProxy.set(CHANGE_NETMODE_BY_EM, "true");
            }
            mListPreferenceTDD.setValueIndex(changeValueToIndex(KEY_TDD));
            mListPreferenceTDD.setSummary(mListPreferenceTDD.getEntry());
        /*SPRD Bug 854796*/
        } else if (mCurrentRadioCapbility == TelephonyManagerSprd.RadioCapbility.FDD_CSFB
                || SystemPropertiesProxy.get("ro.product.name", "").contains("oversea")
                || SystemPropertiesProxy.get("ro.build.display.id", "").contains("oversea")) {
            if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_LTE_FDD_TD_LTE_WCDMA_GSM) {
                SystemPropertiesProxy.set(CHANGE_NETMODE_BY_EM, "false");
            } else {
                SystemPropertiesProxy.set(CHANGE_NETMODE_BY_EM, "true");
            }
            mListPreferenceFDD.setValueIndex(changeValueToIndex(KEY_FDD));
            mListPreferenceFDD.setSummary(mListPreferenceFDD.getEntry());
        } else if (mCurrentRadioCapbility == TelephonyManagerSprd.RadioCapbility.CSFB
            || mCurrentRadioCapbility.toString().equals(KEY_TD_TD)
            || mCurrentRadioCapbility.toString().equals(KEY_TDD_TDD)) {
            if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_LTE_FDD_TD_LTE_WCDMA_TDSCDMA_GSM) {
                SystemPropertiesProxy.set(CHANGE_NETMODE_BY_EM, "false");
            } else {
                SystemPropertiesProxy.set(CHANGE_NETMODE_BY_EM, "true");
            }
            mListPreferenceCSFB.setValueIndex(changeValueToIndex(KEY_CSFB));
            mListPreferenceCSFB.setSummary(mListPreferenceCSFB.getEntry());
        } else if (mCurrentRadioCapbility == TelephonyManagerSprd.RadioCapbility.TLTWCG_TLTWCG) {
            mListPreferenceTLTWCG.setValueIndex(changeValueToIndex(KEY_TLTWCG));
            mListPreferenceTLTWCG.setSummary(mListPreferenceTLTWCG.getEntry());
        } else if (mCurrentRadioCapbility == TelephonyManagerSprd.RadioCapbility.NRTLWG_TLWG) {
            mListPreferenceNRTLWG.setValueIndex(changeValueToIndex(KEY_NRTLWG));
            mListPreferenceNRTLWG.setSummary(mListPreferenceNRTLWG.getEntry());
        } else {
            mListPreferenceCSFB.setValueIndex(changeValueToIndex(KEY_CSFB));
            mListPreferenceCSFB.setSummary(mListPreferenceCSFB.getEntry());
        }
    }
    /* @} */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        simId = intent.getStringExtra(KEY_SIM_INDEX);
        Log.d(TAG, "onCreate getIntExtra simId = " + simId);
        if (simId == null || simId.trim().equals("")) {
            simId = "0";
        }

        addPreferencesFromResource(R.xml.pref_network_mode);

        mListPreferenceSVLTE = (ListPreference) findPreference(KEY_SVLTE);
        mListPreferenceFDD = (ListPreference) findPreference(KEY_FDD);
        mListPreferenceTDD = (ListPreference) findPreference(KEY_TDD);
        mListPreferenceCSFB = (ListPreference) findPreference(KEY_CSFB);
        mListPreferenceTLTWCG = (ListPreference) findPreference(KEY_TLTWCG);
        mListPreferenceNRTLWG = (ListPreference) findPreference(KEY_NRTLWG);

        mListPreferenceSVLTE.setOnPreferenceChangeListener(this);
        mListPreferenceFDD.setOnPreferenceChangeListener(this);
        mListPreferenceTDD.setOnPreferenceChangeListener(this);
        mListPreferenceCSFB.setOnPreferenceChangeListener(this);
        mListPreferenceTLTWCG.setOnPreferenceChangeListener(this);
        mListPreferenceNRTLWG.setOnPreferenceChangeListener(this);

        mSharePref = PreferenceManager.getDefaultSharedPreferences(this);

        mTelephonyManager = TelephonyManagerSprd.getInstance();
        new NetworkTypeGainAsyncTask().execute();
        /* @} */
    }

    private void initNotifiCation() {
        mNotifiCation = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mNotifiCation.createNotificationChannel(new NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                getResources().getString(R.string.app_name),
                NotificationManager.IMPORTANCE_LOW));
        RemoteViews mRemoteViews = new RemoteViews(this.getPackageName(), R.layout.notification);
        mRemoteViews.setTextViewText(R.id.mt_notification,NEW_MESSAGE+"\n"+"    "+CLOSE_NOTICE);
        Intent clickIntent = new Intent(this, NotificationBroadcastReceiver.class);
        clickIntent.putExtra("notificationId", NOTIFICATION_ID);
        PendingIntent pendingIntent= PendingIntent.getBroadcast(this, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.bt_notification, pendingIntent);
        Notification notify = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setTicker(NEW_MESSAGE)
            .setContent(mRemoteViews)
            .setAutoCancel(true)
            .setContentTitle(NEW_MESSAGE)
            .setPriority(Notification.PRIORITY_DEFAULT)
            .setOngoing(true)
            .setSmallIcon(R.drawable.cg)
            .build();
        mNotifiCation.notify(NOTIFICATION_ID,notify);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /** BEGIN BUG:543427 zhijie.yang 2016/04/06 modify the method of the switch network **/
    private int changeValueToIndex(String PrefKey) {
        int valueIndex = 0;
        if (PrefKey.equals(KEY_FDD)) {
            valueIndex = 5;
            if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_LTE_FDD_WCDMA_GSM) {
                valueIndex = 0;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_TD_LTE_WCDMA_GSM) {
                valueIndex = 1;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_TD_LTE) {
                valueIndex = 2;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_LTE_FDD) {
                valueIndex = 3;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_LTE_FDD_TD_LTE) {
                valueIndex = 4;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_LTE_FDD_TD_LTE_WCDMA_GSM) {
                valueIndex = 5;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_GSM) {
                valueIndex = 6;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_WCDMA) {
                valueIndex = 7;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_WCDMA_GSM) {
                valueIndex = 8;
            }
        } else if (PrefKey.equals(KEY_TDD)) {
            if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_TD_LTE_TDSCDMA_GSM) {
                valueIndex = 0;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_TD_LTE) {
                valueIndex = 1;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_LTE_FDD) {
                valueIndex = 2;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_LTE_FDD_TD_LTE) {
                valueIndex = 3;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_LTE_FDD_TD_LTE_TDSCDMA_GSM) {
                valueIndex = 4;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_GSM) {
                if (isSupportTDD) {
                    valueIndex = 2;
                } else {
                    valueIndex = 5;
                }
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_TDSCDMA) {
                if (isSupportTDD) {
                    valueIndex = 3;
                } else {
                    valueIndex = 6;
                }
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_TDSCDMA_GSM) {
                if (isSupportTDD) {
                    valueIndex = 4;
                } else {
                    valueIndex = 7;
                }
            }
        } else if (PrefKey.equals(KEY_CSFB)) {
            /* SPRD Bug 865089. */
            if (SystemPropertiesProxy.get("ro.build.product", "null").contains("sp9853i")) {
                valueIndex = 1;
            }
            /* @} */
            if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_LTE_FDD_TD_LTE_WCDMA_TDSCDMA_GSM) {
                valueIndex = 0;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_LTE_FDD_TD_LTE_WCDMA_GSM) {
                valueIndex = 1;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_LTE_FDD_WCDMA_GSM) {
                valueIndex = 2;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_TD_LTE_WCDMA_GSM) {
                valueIndex = 3;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_TD_LTE) {
                valueIndex = 4;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_LTE_FDD) {
                valueIndex = 5;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_LTE_FDD_TD_LTE) {
                valueIndex = 6;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_TD_LTE_TDSCDMA_GSM) {
                valueIndex = 7;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_LTE_FDD_TD_LTE_TDSCDMA_GSM) {
                valueIndex = 8;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_GSM) {
                valueIndex = 9;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_WCDMA) {
                valueIndex = 10;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_WCDMA_GSM) {
                valueIndex = 11;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_TDSCDMA) {
                valueIndex = 12;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_TDSCDMA_GSM) {
                valueIndex = 13;
            }
        } else if (PrefKey.equals(KEY_TLTWCG)) {
            valueIndex = 0;
            if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_GSM) {
                valueIndex = 0;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_WCDMA) {
                valueIndex = 1;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_WCDMA_GSM) {
                valueIndex = 2;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_LTE_FDD_TD_LTE) {
                valueIndex = 3;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_LTE_FDD_TD_LTE_WCDMA_TDSCDMA_GSM) {
                valueIndex = 4;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_EVDO_CDMA) {
                valueIndex = 5;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_CDMA) {
                valueIndex = 6;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_EVDO) {
                valueIndex = 7;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_LTE_WCDMA_TDSCDMA_EVDO_CDMA_GSM) {
                valueIndex = 8;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_WCDMA_TDSCDMA_EVDO_CDMA_GSM) {
                valueIndex = 9;
            }
        } else if (PrefKey.equals(KEY_NRTLWG)) {
            valueIndex = 0;
            if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_LTE_FDD_TD_LTE_WCDMA_GSM) {
                valueIndex = 0;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_LTE_FDD_TD_LTE) {
                valueIndex = 1;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_WCDMA_GSM) {
                valueIndex = 2;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_WCDMA) {
                valueIndex = 3;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_GSM) {
                valueIndex = 4;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_NR_LTE_FDD_TD_LTE_GSM_WCDMA) {
                valueIndex = 5;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_NR_LTE_FDD_TD_LTE) {
                valueIndex = 6;
            } else if (mCurrentRadioFeatures == TelephonyManagerSprd.NT_NR) {
                valueIndex = 7;
            }
        }
        return valueIndex;
    }

    private int changeIndexToValue(RadioCapbility radio,
            int setValueIndex) {
        int setRadioFeature = -1;
        if (radio.equals(TelephonyManagerSprd.RadioCapbility.FDD_CSFB)) {
            switch (setValueIndex) {
                case 0:
                    setRadioFeature = TelephonyManagerSprd.NT_LTE_FDD_WCDMA_GSM;
                    break;
                case 1:
                    setRadioFeature = TelephonyManagerSprd.NT_TD_LTE_WCDMA_GSM;
                    break;
                case 2:
                    setRadioFeature = TelephonyManagerSprd.NT_TD_LTE;
                    break;
                case 3:
                    setRadioFeature = TelephonyManagerSprd.NT_LTE_FDD;
                    break;
                case 4:
                    setRadioFeature = TelephonyManagerSprd.NT_LTE_FDD_TD_LTE;
                    break;
                case 5:
                    setRadioFeature = TelephonyManagerSprd.NT_LTE_FDD_TD_LTE_WCDMA_GSM;
                    break;
                case 6:
                    setRadioFeature = TelephonyManagerSprd.NT_GSM;
                    break;
                case 7:
                    setRadioFeature = TelephonyManagerSprd.NT_WCDMA;
                    break;
                case 8:
                    setRadioFeature = TelephonyManagerSprd.NT_WCDMA_GSM;
                    break;
            }
        } else if (radio.equals(TelephonyManagerSprd.RadioCapbility.TDD_CSFB)) {
            switch (setValueIndex) {
                case 0:
                    setRadioFeature = TelephonyManagerSprd.NT_TD_LTE_TDSCDMA_GSM;
                    break;
                case 1:
                    setRadioFeature = TelephonyManagerSprd.NT_TD_LTE;
                    break;
                case 2:
                    if (isSupportTDD) {
                        setRadioFeature = TelephonyManagerSprd.NT_GSM;
                    } else {
                        setRadioFeature = TelephonyManagerSprd.NT_LTE_FDD;
                    }
                    break;
                case 3:
                    if (isSupportTDD) {
                        setRadioFeature = TelephonyManagerSprd.NT_TDSCDMA;
                    } else {
                        setRadioFeature = TelephonyManagerSprd.NT_LTE_FDD_TD_LTE;
                    }
                    break;
                case 4:
                    if (isSupportTDD) {
                        setRadioFeature = TelephonyManagerSprd.NT_TDSCDMA_GSM;
                    } else {
                        setRadioFeature = TelephonyManagerSprd.NT_LTE_FDD_TD_LTE_TDSCDMA_GSM;
                    }
                    break;
                case 5:
                    setRadioFeature = TelephonyManagerSprd.NT_GSM;
                    break;
                case 6:
                    setRadioFeature = TelephonyManagerSprd.NT_TDSCDMA;
                    break;
                case 7:
                    setRadioFeature = TelephonyManagerSprd.NT_TDSCDMA_GSM;
                    break;
            }
        } else if (radio.equals(TelephonyManagerSprd.RadioCapbility.CSFB)) {
            switch (setValueIndex) {
                case 0:
                    setRadioFeature = TelephonyManagerSprd.NT_LTE_FDD_TD_LTE_WCDMA_TDSCDMA_GSM;
                    break;
                case 1:
                    setRadioFeature = TelephonyManagerSprd.NT_LTE_FDD_TD_LTE_WCDMA_GSM;
                    break;
                case 2:
                    setRadioFeature = TelephonyManagerSprd.NT_LTE_FDD_WCDMA_GSM;
                    break;
                case 3:
                    setRadioFeature = TelephonyManagerSprd.NT_TD_LTE_WCDMA_GSM;
                    break;
                case 4:
                    setRadioFeature = TelephonyManagerSprd.NT_TD_LTE;
                    break;
                case 5:
                    setRadioFeature = TelephonyManagerSprd.NT_LTE_FDD;
                    break;
                case 6:
                    setRadioFeature = TelephonyManagerSprd.NT_LTE_FDD_TD_LTE;
                    break;
                case 7:
                    setRadioFeature = TelephonyManagerSprd.NT_TD_LTE_TDSCDMA_GSM;
                    break;
                case 8:
                    setRadioFeature = TelephonyManagerSprd.NT_LTE_FDD_TD_LTE_TDSCDMA_GSM;
                    break;
                case 9:
                    setRadioFeature = TelephonyManagerSprd.NT_GSM;
                    break;
                case 10:
                    setRadioFeature = TelephonyManagerSprd.NT_WCDMA;
                    break;
                case 11:
                    setRadioFeature = TelephonyManagerSprd.NT_WCDMA_GSM;
                    break;
                case 12:
                    setRadioFeature = TelephonyManagerSprd.NT_TDSCDMA;
                    break;
                case 13:
                    setRadioFeature = TelephonyManagerSprd.NT_TDSCDMA_GSM;
                    break;
            }
        } else if (radio.equals(TelephonyManagerSprd.RadioCapbility.TLTWCG_TLTWCG)) {
            switch (setValueIndex) {
                case 0:
                    setRadioFeature = TelephonyManagerSprd.NT_GSM;
                    break;
                case 1:
                    setRadioFeature = TelephonyManagerSprd.NT_WCDMA;
                    break;
                case 2:
                    setRadioFeature = TelephonyManagerSprd.NT_WCDMA_GSM;
                    break;
                case 3:
                    setRadioFeature = TelephonyManagerSprd.NT_LTE_FDD_TD_LTE;
                    break;
                case 4:
                    setRadioFeature = TelephonyManagerSprd.NT_LTE_FDD_TD_LTE_WCDMA_TDSCDMA_GSM;
                    break;
                case 5:
                    setRadioFeature = TelephonyManagerSprd.NT_EVDO_CDMA;
                    break;
                case 6:
                    setRadioFeature = TelephonyManagerSprd.NT_CDMA;
                    break;
                case 7:
                    setRadioFeature = TelephonyManagerSprd.NT_EVDO;
                    break;
                case 8:
                    setRadioFeature = TelephonyManagerSprd.NT_LTE_WCDMA_TDSCDMA_EVDO_CDMA_GSM;
                    break;
                case 9:
                    setRadioFeature = TelephonyManagerSprd.NT_WCDMA_TDSCDMA_EVDO_CDMA_GSM;
                    break;
            }
        } else if (radio.equals(TelephonyManagerSprd.RadioCapbility.NRTLWG_TLWG)) {
            switch (setValueIndex) {
                case 0:
                    setRadioFeature = TelephonyManagerSprd.NT_LTE_FDD_TD_LTE_WCDMA_GSM;
                    break;
                case 1:
                    setRadioFeature = TelephonyManagerSprd.NT_LTE_FDD_TD_LTE;
                    break;
                case 2:
                    setRadioFeature = TelephonyManagerSprd.NT_WCDMA_GSM;
                    break;
                case 3:
                    setRadioFeature = TelephonyManagerSprd.NT_WCDMA;
                    break;
                case 4:
                    setRadioFeature = TelephonyManagerSprd.NT_GSM;
                    break;
                case 5:
                    setRadioFeature = TelephonyManagerSprd.NT_NR_LTE_FDD_TD_LTE_GSM_WCDMA;
                    break;
                case 6:
                    setRadioFeature = TelephonyManagerSprd.NT_NR_LTE_FDD_TD_LTE;
                    break;
                case 7:
                    setRadioFeature = TelephonyManagerSprd.NT_NR;
                    break;
            }
        }
        return setRadioFeature;
    }

    /** END BUG:543427 zhijie.yang 2016/04/06 modify the method of the switch network **/

    private void setSummary(RadioCapbility radio) {
        if (radio == null) {
            return;
        } else {
            if (radio.equals(TelephonyManagerSprd.RadioCapbility.TDD_SVLTE)) {
                mListPreferenceSVLTE
                        .setSummary(mListPreferenceSVLTE.getEntry());
            } else if (radio
                    .equals(TelephonyManagerSprd.RadioCapbility.FDD_CSFB)) {
                mListPreferenceFDD.setSummary(mListPreferenceFDD.getEntry());
            } else if (radio
                    .equals(TelephonyManagerSprd.RadioCapbility.TDD_CSFB)) {
                mListPreferenceTDD.setSummary(mListPreferenceTDD.getEntry());
            } else if (radio.equals(TelephonyManagerSprd.RadioCapbility.CSFB)) {
                mListPreferenceCSFB.setSummary(mListPreferenceCSFB.getEntry());
            } else if (radio.equals(TelephonyManagerSprd.RadioCapbility.TLTWCG_TLTWCG)) {
                mListPreferenceTLTWCG.setSummary(mListPreferenceTLTWCG.getEntry());
            } else if (radio.equals(TelephonyManagerSprd.RadioCapbility.TLTWCG_TLTWCG)) {
                mListPreferenceNRTLWG.setSummary(mListPreferenceNRTLWG.getEntry());
            }
        }
    }

    private void retrievePrefIndex(RadioCapbility radio, int lastValueIndex) {
        if (radio == null) {
            return;
        } else {
            if (radio.equals(TelephonyManagerSprd.RadioCapbility.FDD_CSFB)) {
                mListPreferenceFDD.setValueIndex(lastValueIndex);
                SharedPreferences.Editor edit = mSharePref.edit();
                edit.putString(KEY_FDD, mListPreferenceFDD.getValue());
                edit.commit();
            } else if (radio.equals(TelephonyManagerSprd.RadioCapbility.TDD_CSFB)) {
                mListPreferenceTDD.setValueIndex(lastValueIndex);
                SharedPreferences.Editor edit = mSharePref.edit();
                edit.putString(KEY_TDD, mListPreferenceTDD.getValue());
                edit.commit();
            } else if (radio.equals(TelephonyManagerSprd.RadioCapbility.CSFB)) {
                mListPreferenceCSFB.setValueIndex(lastValueIndex);
                SharedPreferences.Editor edit = mSharePref.edit();
                edit.putString(KEY_CSFB, mListPreferenceCSFB.getValue());
                edit.commit();
            }
        }
    }

    private void retrieveCapbility(RadioCapbility radio) {
        if (radio == null) {
            return;
        } else {
            if (radio.equals(TelephonyManagerSprd.RadioCapbility.TDD_SVLTE)) {
                mListPreferenceSVLTE.setValue(null);
            } else if (radio
                    .equals(TelephonyManagerSprd.RadioCapbility.FDD_CSFB)) {
                mListPreferenceFDD.setValue(null);
            } else if (radio
                    .equals(TelephonyManagerSprd.RadioCapbility.TDD_CSFB)) {
                mListPreferenceTDD.setValue(null);
            } else if (radio.equals(TelephonyManagerSprd.RadioCapbility.CSFB)) {
                mListPreferenceCSFB.setValue(null);
            }
        }
    }

    /** BEGIN BUG:543427 zhijie.yang 2016/04/06 modify the method of the switch network **/
    public AlertDialog showAlertDialog(final RadioCapbility mRadio,
            final int setValueIndex, final int valueIndex) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final int setRadioFeature = changeIndexToValue(mRadio,
                setValueIndex);
        Log.d(TAG, "setRadioFeature is " + setRadioFeature);
        builder.setMessage(getString(R.string.setting_dialog_con1))
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                if (!teleApi.EngTestStatus().isEngTest()) {
                                    teleApi.EngTestStatus().set(1);
                                    Log.d(TAG, "get vSystemProperties is " + teleApi.EngTestStatus().isEngTest());
                                    initNotifiCation();
                                }
                                /* SPRD Bug 849746: network mode switch. @{ */
                                mLastRadio = mRadio;
                                mLastValueIndex = valueIndex;
                                /* @} */
                                setRadioFeatures(setRadioFeature);
                            }
                        })
                .setNegativeButton(android.R.string.no,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                retrievePrefIndex(mRadio, valueIndex);
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
        return alert;
    }

    public void setRadioFeatures(int setRadioFeature) {
        Log.d(TAG, "now setRadioFeatures: " + setRadioFeature);
        int simIndex = Integer.parseInt(simId);
        final int radioFeature = setRadioFeature;
        if (radioFeature == TelephonyManagerSprd.NT_NR
                && SystemPropertiesProxy.get(NETWORK_NR_ENABLE, NORMAL_MODE).equals(NSA_ONLY_MODE)) {
            Toast.makeText(NetworkModeActivity.this, getResources().getString(R.string.unselect_nr_only_prompt), Toast.LENGTH_LONG).show();
            mListPreferenceNRTLWG.setValueIndex(mLastValueIndex);
            return ;
        }
        /* SPRD Bug 849746: network mode switch. @{ */
        mLteMainHandler.sendEmptyMessage(SHOW_PROGRESS_DIALOG);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (teleApi.telephonyInfo().isSupportLplusG()) {
                    mRetValue = mTelephonyManager.setPreferredNetworkType(radioFeature);
                } else {
                    mRetValue = mTelephonyManager.setPreferredNetworkType(simIndex, radioFeature);
                }
                Log.d(TAG, "mRetValue is: " + mRetValue);
                if (radioFeature == TelephonyManagerSprd.NT_NR) {
                    SystemPropertiesProxy.set(NETWORK_NR_ENABLE, NR_ONLY_MODE);
                } else if (SystemPropertiesProxy.get(NETWORK_NR_ENABLE, NORMAL_MODE).equals(NR_ONLY_MODE)) {
                    SystemPropertiesProxy.set(NETWORK_NR_ENABLE, NORMAL_MODE);
                }
                mCurrentRadioFeatures = radioFeature;
                Log.d(TAG, "mCurrentRadioFeatures is: " + mCurrentRadioFeatures);
                mLteMainHandler.sendEmptyMessage(DISMISS_PROGRESS_DIALOG);
            }
        }).start();
        //setSummary(mCurrentRadioCapbility);
        /* @} */
    }

    /** END BUG:543427 zhijie.yang 2016/04/06 modify the method of the switch network **/

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int setValueIndex = 0;
        int valueIndex = 0;
        Log.d(TAG, "get vSystemProperties is " + teleApi.EngTestStatus().isEngTest());
        mCurrentRadioCapbility = TelephonyManagerSprd.getRadioCapbility();

        if(!teleApi.EngTestStatus().isEngTest()){
            teleApi.EngTestStatus().set(1);
            if (teleApi.telephonyInfo().isSupportLplusG()) {
                mCurrentRadioFeatures = mTelephonyManager.getPreferredNetworkType();
            } else {
                mCurrentRadioFeatures = mTelephonyManager.getPreferredNetworkType(Integer.parseInt(simId));
            }
            teleApi.EngTestStatus().set(0);
        } else {
            if (teleApi.telephonyInfo().isSupportLplusG()) {
                mCurrentRadioFeatures = mTelephonyManager.getPreferredNetworkType();
            } else {
                mCurrentRadioFeatures = mTelephonyManager.getPreferredNetworkType(Integer.parseInt(simId));
            }
        }
        Log.d(TAG, " onPreferenceChange" + "\n" + "mCurrentRadioCapbility is "
                + mCurrentRadioCapbility + ", mCurrentRadioFeatures is "
                + mCurrentRadioFeatures);
        setValueIndex = Integer.valueOf(newValue.toString());

        if (mCurrentRadioCapbility.toString().equals(KEY_FDD_CSFB)
                || SystemPropertiesProxy.get("ro.product.name", "").contains("oversea")
                || SystemPropertiesProxy.get("ro.build.display.id", "").contains("oversea")) {
            valueIndex = changeValueToIndex(KEY_FDD);
        } else if (mCurrentRadioCapbility.toString().equals(KEY_TDD_CSFB)) {
            valueIndex = changeValueToIndex(KEY_TDD);
        } else if (mCurrentRadioCapbility.toString().equals(KEY_LTE_CSFB)
            || mCurrentRadioCapbility.toString().equals(KEY_TD_TD)
            || mCurrentRadioCapbility.toString().equals(KEY_TDD_TDD)) {
            /* SPRD 917357: if not support TDWCDMA, change to TDWCDMA due to assert {@ */
            if (mCurrentRadioCapbility.toString().equals(KEY_TD_TD) && isChangeValueToTDSCDMA(mCurrentRadioCapbility.toString(), setValueIndex)) {
                Toast.makeText(this, getResources().getString(R.string.network_mode_switch_faided_hint), Toast.LENGTH_SHORT).show();
                return false;
            } else {
                valueIndex = changeValueToIndex(KEY_CSFB);
            }
            /* @} */
        } else if (mCurrentRadioCapbility.toString().equals(KEY_TLTWCG_TLTWCG)) {
            valueIndex = changeValueToIndex(KEY_TLTWCG);
        } else if (mCurrentRadioCapbility.toString().equals(KEY_NRTLWG_TLWG)) {
            valueIndex = changeValueToIndex(KEY_NRTLWG);
        }
        if (preference instanceof ListPreference) {
            RadioCapbility radioCapbility = null;
            ListPreference listPreference = (ListPreference) preference;
            String key = listPreference.getKey();
            if (key.equals(KEY_FDD)) {
                radioCapbility = TelephonyManagerSprd.RadioCapbility.FDD_CSFB;
            } else if (key.equals(KEY_CSFB)) {
                radioCapbility = TelephonyManagerSprd.RadioCapbility.CSFB;
            } else if (key.equals(KEY_TLTWCG)) {
                radioCapbility = TelephonyManagerSprd.RadioCapbility.TLTWCG_TLTWCG;
            } else if (key.equals(KEY_NRTLWG)) {
                radioCapbility = TelephonyManagerSprd.RadioCapbility.NRTLWG_TLWG;
            } else {
                radioCapbility = TelephonyManagerSprd.RadioCapbility.TDD_CSFB;
            }
            if (setValueIndex != valueIndex) {
                showAlertDialog(radioCapbility, setValueIndex, valueIndex);
            }
            Log.d(TAG, "mCurrentRadioCapbility is " + mCurrentRadioCapbility
                    + ", changeradioCapbility is " +  radioCapbility
                    + ", setValueIndex is " + setValueIndex
                    + ", valueIndex is " + valueIndex + ", key is " + key);
        }
        return true;
    }

    /* SPRD 917357: if not support TDWCDMA, change to TDWCDMA due to assert {@ */
    private boolean isChangeValueToTDSCDMA(String mCurRadioCapbility, int mCurValueIndex) {
        if (mCurRadioCapbility.equals(KEY_TD_TD)) {
            if (mCurValueIndex == 7 || mCurValueIndex == 8 || mCurValueIndex == 12 || mCurValueIndex == 13) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    /* @} */

    /* SPRD Bug 851599:EngineerMode calls the interface TelephonyManager.getPreferredNetworkType,
        occurs engineer mode isn't responding. @{ */
    private class NetworkTypeGainAsyncTask extends AsyncTask<String, Void, Boolean> {
        public NetworkTypeGainAsyncTask() {
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Log.d(TAG, "NetworkTypeGainAsyncTask doInBackground");
            mCurrentRadioCapbility = TelephonyManagerSprd.getRadioCapbility();

            if (!teleApi.EngTestStatus().isEngTest()){
                teleApi.EngTestStatus().set(1);
                if (teleApi.telephonyInfo().isSupportLplusG()) {
                    mCurrentRadioFeatures = mTelephonyManager.getPreferredNetworkType();
                } else {
                    mCurrentRadioFeatures = mTelephonyManager.getPreferredNetworkType(Integer.parseInt(simId));
                }
                teleApi.EngTestStatus().set(0);
            } else {
                if (teleApi.telephonyInfo().isSupportLplusG()) {
                    mCurrentRadioFeatures = mTelephonyManager.getPreferredNetworkType();
                } else {
                    mCurrentRadioFeatures = mTelephonyManager.getPreferredNetworkType(Integer.parseInt(simId));
                }
            }
            Log.d(TAG, "onCreate mCurrentRadioCapbility is " + mCurrentRadioCapbility.toString()
                + ", mCurrentRadioFeatures is " + mCurrentRadioFeatures);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.d(TAG, "NetworkTypeGainAsyncTask onPostExecute");
            PreferenceScreen prefSet = getPreferenceScreen();
            if (mCurrentRadioCapbility.toString().equals(KEY_TDD_SVLTE)) {
                prefSet.removePreference(mListPreferenceFDD);
                prefSet.removePreference(mListPreferenceTDD);
                prefSet.removePreference(mListPreferenceCSFB);
                prefSet.removePreference(mListPreferenceTLTWCG);
                prefSet.removePreference(mListPreferenceNRTLWG);
                mListPreferenceSVLTE.setValueIndex(changeValueToIndex(KEY_SVLTE));
                mListPreferenceSVLTE.setSummary(mListPreferenceSVLTE.getEntry());
                mListPreferenceFDD.setValue(null);
                mListPreferenceTDD.setValue(null);
                mListPreferenceCSFB.setValue(null);
                mListPreferenceTLTWCG.setValue(null);
                mListPreferenceNRTLWG.setValue(null);
            } else if (mCurrentRadioCapbility.toString().equals(KEY_FDD_CSFB)
                || SystemPropertiesProxy.get("ro.product.name", "").contains("oversea")
                || SystemPropertiesProxy.get("ro.build.display.id", "").contains("oversea")) {
                prefSet.removePreference(mListPreferenceSVLTE);
                prefSet.removePreference(mListPreferenceTDD);
                prefSet.removePreference(mListPreferenceCSFB);
                prefSet.removePreference(mListPreferenceTLTWCG);
                prefSet.removePreference(mListPreferenceNRTLWG);
                mListPreferenceFDD.setValueIndex(changeValueToIndex(KEY_FDD));
                mListPreferenceFDD.setSummary(mListPreferenceFDD.getEntry());
                mListPreferenceSVLTE.setValue(null);
                mListPreferenceTDD.setValue(null);
                mListPreferenceCSFB.setValue(null);
                mListPreferenceTLTWCG.setValue(null);
                mListPreferenceNRTLWG.setValue(null);
            } else if (mCurrentRadioCapbility.toString().equals(KEY_TDD_CSFB)) {
                prefSet.removePreference(mListPreferenceSVLTE);
                prefSet.removePreference(mListPreferenceFDD);
                prefSet.removePreference(mListPreferenceCSFB);
                prefSet.removePreference(mListPreferenceTLTWCG);
                prefSet.removePreference(mListPreferenceNRTLWG);
                mListPreferenceTDD.setEntries(R.array.list_entries_tdd_csfb_change);
                mListPreferenceTDD
                        .setEntryValues(R.array.list_entriesvalues_tdd_csfb_change);
                mListPreferenceTDD.setValueIndex(changeValueToIndex(KEY_TDD));
                mListPreferenceTDD.setSummary(mListPreferenceTDD.getEntry());
                mListPreferenceSVLTE.setValue(null);
                mListPreferenceFDD.setValue(null);
                mListPreferenceCSFB.setValue(null);
                mListPreferenceTLTWCG.setValue(null);
                mListPreferenceNRTLWG.setValue(null);
            } else if (mCurrentRadioCapbility.toString().equals(KEY_LTE_CSFB)
                || mCurrentRadioCapbility.toString().equals(KEY_TD_TD)
                || mCurrentRadioCapbility.toString().equals(KEY_TDD_TDD)) {
                prefSet.removePreference(mListPreferenceSVLTE);
                prefSet.removePreference(mListPreferenceTDD);
                prefSet.removePreference(mListPreferenceFDD);
                prefSet.removePreference(mListPreferenceTLTWCG);
                prefSet.removePreference(mListPreferenceNRTLWG);
                mListPreferenceCSFB.setValueIndex(changeValueToIndex(KEY_CSFB));
                mListPreferenceCSFB.setSummary(mListPreferenceCSFB.getEntry());
                mListPreferenceSVLTE.setValue(null);
                mListPreferenceTDD.setValue(null);
                mListPreferenceFDD.setValue(null);
                mListPreferenceTLTWCG.setValue(null);
                mListPreferenceNRTLWG.setValue(null);
            } else if (mCurrentRadioCapbility.toString().equals(KEY_TLTWCG_TLTWCG)) {
                prefSet.removePreference(mListPreferenceSVLTE);
                prefSet.removePreference(mListPreferenceTDD);
                prefSet.removePreference(mListPreferenceFDD);
                prefSet.removePreference(mListPreferenceCSFB);
                prefSet.removePreference(mListPreferenceNRTLWG);
                mListPreferenceTLTWCG.setValueIndex(changeValueToIndex(KEY_TLTWCG));
                mListPreferenceTLTWCG.setSummary(mListPreferenceTLTWCG.getEntry());
                mListPreferenceSVLTE.setValue(null);
                mListPreferenceTDD.setValue(null);
                mListPreferenceFDD.setValue(null);
                mListPreferenceCSFB.setValue(null);
                mListPreferenceNRTLWG.setValue(null);
                /* sprd 1002440  : 5G mode @{ */
            } else if (mCurrentRadioCapbility.toString().equals(KEY_NRTLWG_TLWG)) {
                prefSet.removePreference(mListPreferenceSVLTE);
                prefSet.removePreference(mListPreferenceTDD);
                prefSet.removePreference(mListPreferenceFDD);
                prefSet.removePreference(mListPreferenceCSFB);
                prefSet.removePreference(mListPreferenceTLTWCG);
                mListPreferenceNRTLWG.setValueIndex(changeValueToIndex(KEY_NRTLWG));
                mListPreferenceNRTLWG.setSummary(mListPreferenceNRTLWG.getEntry());
                mListPreferenceSVLTE.setValue(null);
                mListPreferenceTDD.setValue(null);
                mListPreferenceFDD.setValue(null);
                mListPreferenceCSFB.setValue(null);
                mListPreferenceTLTWCG.setValue(null);
                /* @} */
            } else {
                mListPreferenceSVLTE.setValue(null);
                mListPreferenceFDD.setValue(null);
                mListPreferenceTDD.setValue(null);
				mListPreferenceTLTWCG.setValue(null);
            }
        }
    }
    /* @} */
}
