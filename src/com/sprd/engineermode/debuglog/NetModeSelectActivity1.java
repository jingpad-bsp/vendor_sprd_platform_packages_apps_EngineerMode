package com.sprd.engineermode.debuglog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.ProgressDialog;
import android.app.NotificationManager;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;

import com.sprd.engineermode.telephony.NotificationBroadcastReceiver;
import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;
import com.unisoc.engineermode.core.impl.nonpublic.SubscriptionManagerProxy;
import com.unisoc.engineermode.core.impl.nonpublic.TelephonyManagerProxy;
import com.unisoc.engineermode.core.impl.telephony.TelephonyManagerSprd;
import com.unisoc.engineermode.core.impl.telephony.TelephonyManagerSprd.RadioCapbility;

import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;
import android.content.Context;

import com.sprd.engineermode.R;

import android.telephony.SubscriptionManager;

import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.intf.ITelephonyApi;

public class NetModeSelectActivity1 extends PreferenceActivity implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "NetModeSelectActivity1";
    static final String PROPERTY_MULTI_SIM_CONFIG = "persist.radio.multisim.config";
    private static final String KEY = "sim";

    private static final int SET_NETMODE = 1;
    private static final int GET_NETMODE = 2;
    private static final int NOTIFICATION_ID = 0x1123;
    private static final String NOTIFICATION_CHANNEL_ID = "mobileServiceMessages";

    private static final String KEY_SVLTE = "svlte_key";
    private static final String KEY_FDD = "fdd_csfb_key";
    private static final String KEY_TDD = "tdd_csfb_key";
    private static final String KEY_CSFB = "csfb_key";
    private static final String KEY_TDD_SVLTE = "TDD_SVLTE";
    private static final String KEY_FDD_CSFB = "FDD_CSFB";
    private static final String KEY_TDD_CSFB = "TDD_CSFB";
    private static final String KEY_LTE_CSFB = "CSFB";
    private static final String CHANGE_NETMODE_BY_EM = "persist.sys.cmccpolicy.disable";
    private static final String NEW_MESSAGE = "Notice From EngineerMode!";
    private static final String CLOSE_NOTICE ="After test,please close this notificaiton";

    /* SPRD Bug 849746: network mode switch. @{ */
    private static final int SHOW_PROGRESS_DIALOG = 1;
    private static final int DISMISS_PROGRESS_DIALOG = 0;
    private RadioCapbility mLastPrimaryRadio = null;
    private int mLastPrimaryValueIndex = 0;
    private int mNewPrimaryRadioFeature = -1;
    private int secondPhoneId = 0;
    private int mLastValueIndex = 0;
    private int mNewValueIndex = 0;
    private boolean isCurrentPrimary = true;
    /* @} */

    private RadioCapbility mCurrentRadioCapbility;
    private int mCurrentRadioFeatures;
    private int mSecondRadioFeatures;

    private int mPhoneCount;
    private int primaryPhoneId;
    private ListPreference[] mListPreference;
    private TelephonyManagerSprd mTelephonyManager;
    private SharedPreferences mSharePref;
    private ProgressDialog mProgressDialog;
    private SubscriptionManager mSubscriptionManager;
    private NotificationManager mNotifiCation;
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
        if (isCurrentPrimary) {
            Log.d(TAG, "current is primary card");
            if (mRetValue < 0) {
                retrievePrefIndex(mLastPrimaryRadio, mLastPrimaryValueIndex);
                String switchFailedHint = getResources().getString(R.string.network_mode_switch_faided_hint);
                Toast.makeText(this, switchFailedHint, Toast.LENGTH_SHORT).show();
                return;
            } else {
                if (mCurrentRadioCapbility == TelephonyManagerSprd.RadioCapbility.CSFB) {
                    if (mNewPrimaryRadioFeature == TelephonyManagerSprd.NT_LTE_FDD_TD_LTE_WCDMA_TDSCDMA_GSM) {
                        SystemPropertiesProxy.set(CHANGE_NETMODE_BY_EM, "false");
                    } else {
                        SystemPropertiesProxy.set(CHANGE_NETMODE_BY_EM, "true");
                    }
                }
                setSummary(mCurrentRadioCapbility);
            }
        } else {
            Log.d(TAG, "current is not primary card");
            if (mRetValue < 0) {
                mListPreference[secondPhoneId].setValueIndex(mLastValueIndex);
                mListPreference[secondPhoneId].setSummary(mListPreference[secondPhoneId].getEntry());
            } else {
                mListPreference[secondPhoneId].setValueIndex(mNewValueIndex);
                mListPreference[secondPhoneId].setSummary(mListPreference[secondPhoneId].getEntry());
            }
        }
    }
    /* @} */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPhoneCount = TelephonyManagerProxy.getPhoneCount();
        Log.d(TAG, "mPhoneCount is " + mPhoneCount);
        setPreferenceScreen(getPreferenceManager().createPreferenceScreen(this));
        PreferenceGroup preGroup = getPreferenceScreen();
        mSubscriptionManager = (SubscriptionManager) SubscriptionManager.from(NetModeSelectActivity1.this);
        mSharePref = PreferenceManager.getDefaultSharedPreferences(this);
        mListPreference = new ListPreference[mPhoneCount];

        if(!teleApi.EngTestStatus().isEngTest()){
            teleApi.EngTestStatus().set(1);
            initNotifiCation();
        }

        mCurrentRadioCapbility = TelephonyManagerSprd.getRadioCapbility();
        mTelephonyManager = TelephonyManagerSprd.getInstance();

        for (int i = 0; i < mPhoneCount; i++) {
            String key = KEY + i;
            mListPreference[i] = new ListPreference(NetModeSelectActivity1.this);
            mListPreference[i].setEnabled(false);
            mListPreference[i].setTitle(key);
            mListPreference[i].setKey(key);
            mListPreference[i]
                    .setOnPreferenceChangeListener(NetModeSelectActivity1.this);
            preGroup.addPreference(mListPreference[i]);
            mListPreference[i].setEntries(R.array.network_mode_choices_gsm);
            mListPreference[i].setEntryValues(R.array.network_mode_gsm_values);
        }
    }

    /* SPRD Bug 853746:Switch off main card, occurs ANR possibly. @{ */
    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        mPhoneCount = TelephonyManagerProxy.getPhoneCount();
        primaryPhoneId = SubscriptionManagerProxy.getDefaultDataPhoneId();

        secondPhoneId = Math.abs(primaryPhoneId-1);
        mCurrentRadioFeatures = mTelephonyManager.getPreferredNetworkType();
        
        if (mSubscriptionManager.getActiveSubscriptionInfoList() != null
            && mSubscriptionManager.getActiveSubscriptionInfoList().size()  > 1) {
            mSecondRadioFeatures = mTelephonyManager.getPreferredNetworkType(Math.abs(primaryPhoneId - 1));
        }

        Log.d(TAG, "mCurrentRadioCapbility is " + mCurrentRadioCapbility.toString()
                + ", mCurrentRadioFeatures is " + mCurrentRadioFeatures + " mSecondRadioFeatures is " + mSecondRadioFeatures);
        TelephonyManager tm;
        for (int i=0; i < mPhoneCount; i++) {
            tm = TelephonyManagerProxy.getService();
            if (tm != null && tm.getSimState(i) == TelephonyManager.SIM_STATE_READY) {
                mListPreference[i].setEnabled(true);
            } else {
                mListPreference[i].setEnabled(false);
                continue;
            }
            try {
                if (primaryPhoneId == i) {
                    mListPreference[i].setEntries(R.array.list_entries_csfb_lw);
                    mListPreference[i].setEntryValues(R.array.list_entriesvalues_csfb_lw);
                    mListPreference[i].setValueIndex(changeValueToIndex("primary card"));
                    mListPreference[i].setSummary(mListPreference[i].getEntry());
                } else {
                    mNewValueIndex = changeValueToIndex("second card");
                    mLastValueIndex = mNewValueIndex;
                    mListPreference[i].setEntries(R.array.network_mode_choices_lplusw);
                    mListPreference[i].setEntryValues(R.array.network_mode_choices_lplusw_values);
                    mListPreference[i].setValueIndex(mNewValueIndex);
                    mListPreference[i].setSummary(mListPreference[i].getEntry());
                }
            } catch(ArrayIndexOutOfBoundsException e) {
                Log.e(TAG, "ArrayIndexOutOfBoundsException: " + e);
            }
        }
        super.onResume();
        /* @} */
    }
    /* @} */

    @Override
    protected void onStart() {
        super.onStart();
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
    protected void onDestroy() {
        super.onDestroy();
    }

    private int changeValueToIndex(String PrefKey) {
        int valueIndex = 0;
        if (PrefKey.equals("primary card")) {
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
        } else if (PrefKey.equals("second card")) {
            valueIndex = 2;
            if (mSecondRadioFeatures == TelephonyManagerSprd.NT_GSM) {
                valueIndex = 2;
            } else if (mSecondRadioFeatures == TelephonyManagerSprd.NT_WCDMA) {
                valueIndex = 1;
            } else if (mSecondRadioFeatures == TelephonyManagerSprd.NT_WCDMA_GSM || mSecondRadioFeatures == TelephonyManagerSprd.NT_WCDMA_TDSCDMA_EVDO_CDMA_GSM) {
                valueIndex = 0;
            }
        }
        return valueIndex;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        Log.d(TAG, "onPreferenceChange and the key is " + key);
        try {
            if (key.contains("" + primaryPhoneId)) {
                int setValueIndex = 0;
                int valueIndex = 0;
                Log.d(TAG, "get vSystemProperties is " + teleApi.EngTestStatus().isEngTest());
                mCurrentRadioCapbility = TelephonyManagerSprd.getRadioCapbility();
                mCurrentRadioFeatures = mTelephonyManager.getPreferredNetworkType();
                Log.d(TAG, "onPreferenceChange" + "\n" + "mCurrentRadioCapbility is "
                        + mCurrentRadioCapbility + ", mCurrentRadioFeatures is "
                        + mCurrentRadioFeatures);
                setValueIndex = Integer.valueOf(newValue.toString());
                valueIndex = changeValueToIndex("primary card");
                if (preference instanceof ListPreference) {
                    RadioCapbility radioCapbility = null;
                    ListPreference listPreference = (ListPreference) preference;
                    key = listPreference.getKey();
                    radioCapbility = TelephonyManagerSprd.RadioCapbility.CSFB;
                    if (setValueIndex != valueIndex) {
                        showAlertDialog(radioCapbility, setValueIndex, valueIndex);
                    }
                    Log.d(TAG, "mCurrentRadioCapbility is " + mCurrentRadioCapbility
                            + ", changeradioCapbility is " + radioCapbility
                            + ", setValueIndex is " + setValueIndex
                            + ", valueIndex is " + valueIndex + ", key is " + key);
                }
            } else {
                String re = newValue.toString();
                final int i = Integer.parseInt(re.trim());

                /* SPRD Bug 849746: network mode switch. @{ */
                mLteMainHandler.sendEmptyMessage(SHOW_PROGRESS_DIALOG);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        isCurrentPrimary = false;
                        mNewValueIndex = i;
                        setSecondCardNetMode(mNewValueIndex, secondPhoneId);
                        Log.d(TAG, "mRetValue is: " + mRetValue);
                        mLteMainHandler.sendEmptyMessage(DISMISS_PROGRESS_DIALOG);
                    }
                }).start();
                /* @} */
            }
        } catch(ArrayIndexOutOfBoundsException e) {
            Log.e(TAG, "ArrayIndexOutOfBoundsException: " + e);
        }
        return true;
    }

    private void setSecondCardNetMode(int i, int phoneId) {
        Log.d(TAG, "i = " + i);
        switch (i) {
        case 2:
            mRetValue = mTelephonyManager.setPreferredNetworkType(phoneId, TelephonyManagerSprd.NT_GSM);
            break;
        case 1:
            mRetValue = mTelephonyManager.setPreferredNetworkType(phoneId, TelephonyManagerSprd.NT_WCDMA);
            break;
        case 0:
            mRetValue = mTelephonyManager.setPreferredNetworkType(phoneId, TelephonyManagerSprd.NT_WCDMA_GSM);
            break;
        default:
            break;
        }
    }

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
                                /* SPRD Bug 849746: network mode switch. @{ */
                                mLastPrimaryRadio = mRadio;
                                mLastPrimaryValueIndex = valueIndex;
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

    private int changeIndexToValue(RadioCapbility radio,
            int setValueIndex) {
        int setRadioFeature = -1;
        if (radio.equals(TelephonyManagerSprd.RadioCapbility.CSFB)) {
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
            return setRadioFeature;
        }
        return setRadioFeature;
    }

    public void setRadioFeatures(int setRadioFeature) {
        Log.d(TAG, "now setRadioFeatures: " + setRadioFeature);
        /* SPRD Bug 849746: network mode switch. @{ */
        mLteMainHandler.sendEmptyMessage(SHOW_PROGRESS_DIALOG);
        new Thread(new Runnable() {
            @Override
            public void run() {
                isCurrentPrimary = true;
                mNewPrimaryRadioFeature = setRadioFeature;
                mRetValue = mTelephonyManager.setPreferredNetworkType(setRadioFeature);
                mLteMainHandler.sendEmptyMessage(DISMISS_PROGRESS_DIALOG);
            }
        }).start();
        /* @} */
    }

    private void retrievePrefIndex(RadioCapbility radio, int lastValueIndex) {
        if (radio == null) {
            return;
        }
        if (radio.equals(TelephonyManagerSprd.RadioCapbility.CSFB)) {
                mListPreference[primaryPhoneId].setValueIndex(lastValueIndex);
                SharedPreferences.Editor edit = mSharePref.edit();
                edit.putString(KEY_CSFB, mListPreference[primaryPhoneId].getValue());
                edit.commit();
        }
    }

    private void setSummary(RadioCapbility radio) {
        if (radio == null) {
            return;
        }
        /* SPRD Bug 837854: Main SIM card mode does not update on time. @{ */
        if (radio.equals(TelephonyManagerSprd.RadioCapbility.CSFB)
                || radio.equals(TelephonyManagerSprd.RadioCapbility.FLW)) {
            Log.d(TAG, "primaryPhoneId = " + primaryPhoneId);
            mListPreference[primaryPhoneId].setSummary(mListPreference[primaryPhoneId].getEntry());
        }
        /* @} */
    }

}

