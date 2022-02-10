package com.sprd.engineermode.gcf;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.content.Context;

import com.sprd.engineermode.EngineerModeActivity_2;
import com.sprd.engineermode.R;
import android.telephony.TelephonyManager;

import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;
import com.unisoc.engineermode.core.impl.nonpublic.ImsManagerProxy;
import com.unisoc.engineermode.core.impl.nonpublic.TelephonyManagerProxy;
import com.unisoc.engineermode.core.utils.IATUtils;
import com.unisoc.engineermode.core.common.engconstents;
import com.sprd.engineermode.EMSwitchPreference;

public class GcfActivity extends PreferenceActivity{
    private static final String TAG = "GcfActivity";
    private static final String KEY_GCF_PROTOCAL = "gcf_protocol";
    private static final String KEY_GCF_RF = "gcf_rf";
    private static final String KEY_GCF_SIM = "gcf_sim";
    private static final String KEY_GCF_VOLTE = "gcf_volte";
    private static final String CFU_CONTROL = "persist.sys.callforwarding";
    private static final String CURRENT_MODE = "current_mode";
    /* SPRD:838473 mbsfn swtich @{ */
    private EMSwitchPreference mMbsfnSwitch;
    private static final String KEY_MBSFN_SWITCH = "mbsfn_switch";
    /* }@ */
    private Context mContext;
    private GcfHandler mGcfHandler;
    private Preference mGcfProtocal;
    private Preference mGcfRf;
    private Preference mGcfSim;
    private Preference mGcgVolte;
    private String resultInfo = "";
    private String mATCmd, mAtCmd1;
    private String mStrTmp, mStrTmp1;
    private TelephonyManager mTelephonyManager;

    private static final int SET_GCF_PROTOCAL = 1;
    private static final int SET_GCF_RF = 2;
    private static final int SET_GCF_SIM = 3;
    private static final int SET_GCF_VOLTE = 4;
    private static final int TEST_OVER = 5;
    private static final int DELAY_SECONDS = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mGcfHandler = new GcfHandler(ht.getLooper());
        mContext = this;
        mTelephonyManager = TelephonyManagerProxy.INSTANCE.getTelephonyManager();
        addPreferencesFromResource(R.xml.pref_gcf_activity);
        mGcfProtocal = (Preference) findPreference(KEY_GCF_PROTOCAL);
        mGcfRf = (Preference) findPreference(KEY_GCF_RF);
        mGcfSim = (Preference) findPreference(KEY_GCF_SIM);
        mGcgVolte = (Preference) findPreference(KEY_GCF_VOLTE);
        /* SPRD:838473 mbsfn swtich @{ */
        mMbsfnSwitch = (EMSwitchPreference) findPreference(KEY_MBSFN_SWITCH);
        //mMbsfnSwitch.setOnPreferenceChangeListener(this);
        if (SystemPropertiesProxy.get("persist.sys.mbsfn.open", "false").equals("true")) {
            mMbsfnSwitch.setChecked(true);
        } else {
            mMbsfnSwitch.setChecked(false);
        }
        /* }@ */
    }
    @Override
    public void onStart() {

        super.onStart();
    }
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        String key = preference.getKey();
        Log.d(TAG, "key is " + key);
        if (key == null) {
            return false;
        }
        if(key.equals(KEY_GCF_PROTOCAL)){
            resultInfo = "";
            Message mGcfProtocal = mGcfHandler.obtainMessage(SET_GCF_PROTOCAL,key);
            mGcfHandler.sendMessage(mGcfProtocal);
        } else if(key.equals(KEY_GCF_RF)){
            resultInfo = "";
            Message mGcfRf = mGcfHandler.obtainMessage(SET_GCF_RF,key);
            mGcfHandler.sendMessage(mGcfRf);
        } else if(key.equals(KEY_GCF_SIM)){
            resultInfo = "";
            Message mGcfSim = mGcfHandler.obtainMessage(SET_GCF_SIM,key);
            mGcfHandler.sendMessage(mGcfSim);
        } else if(key.equals(KEY_GCF_VOLTE)){
            resultInfo = "";
            Message mGcfVolte = mGcfHandler.obtainMessage(SET_GCF_VOLTE,key);
            mGcfHandler.sendMessage(mGcfVolte);
        /* SPRD:838473 mbsfn swtich @{ */
        } else if (key.equals(KEY_MBSFN_SWITCH)) {
            String result;
            if (mMbsfnSwitch.isChecked()) {
                result = IATUtils.sendATCmd(engconstents.ENG_AT_MBSFN + "1", "atchannel0");
                Log.d(TAG, "send ENG_AT_MBSFN 1 result: " + result);
                if (result != null && result.contains(IATUtils.AT_OK)) {
                    SystemPropertiesProxy.set("persist.sys.mbsfn.open", "true");
                    mMbsfnSwitch.setChecked(true);
                } else {
                    SystemPropertiesProxy.set("persist.sys.mbsfn.open", "false");
                    mMbsfnSwitch.setChecked(false);
                }
            } else {
                result = IATUtils.sendATCmd(engconstents.ENG_AT_MBSFN + "0", "atchannel0");
                Log.d(TAG, "send ENG_AT_MBSFN 0 result: " + result);
                if (result != null && result.contains(IATUtils.AT_OK)) {
                    SystemPropertiesProxy.set("persist.sys.mbsfn.open", "false");
                    mMbsfnSwitch.setChecked(false);
                } else {
                    SystemPropertiesProxy.set("persist.sys.mbsfn.open", "true");
                    mMbsfnSwitch.setChecked(true);
                }
            }

        }
        /* }@ */
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    class GcfHandler extends Handler{
        public GcfHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg){
            String keyName = (String) msg.obj;
            Log.d(TAG, "keyName is " + keyName);
            switch (msg.what){
                 case SET_GCF_PROTOCAL:
                     SystemPropertiesProxy.set(CFU_CONTROL, "0");
                     resultInfo = "always_not_query is :" +"Success"+"\n";
                     SystemPropertiesProxy.set("persist.vendor.sys.modemreset","1");
                     resultInfo += "modem_reset is:" +"Success"+"\n";
                     mTelephonyManager.setDataEnabled(false);
                     resultInfo += "Close data connect is:" +"Success"+"\n";
                     ImsManagerProxy.INSTANCE.setEnhanced4gLteModeSetting(GcfActivity.this, false);
                     resultInfo += "Close volte is:" +"Success"+"\n";
                     SharepreferenceUtils.saveInfo(GcfActivity.this, CURRENT_MODE, keyName);
                     mATCmd = engconstents.ENG_AT_SET_GCF + EngineerModeActivity_2.ONE + 1;

                     /* SPRD Bug 868741. */
                     //mAtCmd1 = engconstents.ENG_AT_SET_GCF + EngineerModeActivity_2.TWO + 1;
                     Log.d(TAG, "SET_GCF_PROTOCAL");
                     mStrTmp = IATUtils.sendATCmd(mATCmd, "atchannel0");
                     //mStrTmp1 = IATUtils.sendATCmd(mAtCmd1, "atchannel0");
                     //EngineerModeActivity_2.checkAtReturnResult(mStrTmp, mStrTmp1, mContext);
                     EngineerModeActivity_2.checkAtReturnResultForGcf(mStrTmp, mContext);

                     Message mTestOver = mGcfHandler.obtainMessage(TEST_OVER,keyName);
                     mGcfHandler.sendMessageDelayed(mTestOver, DELAY_SECONDS);
                     break;
                 case SET_GCF_RF:
                     SystemPropertiesProxy.set(CFU_CONTROL, "0");
                     resultInfo = "always_not_query is :" +"Success"+"\n";
                     SystemPropertiesProxy.set("persist.vendor.sys.modemreset","1");
                     resultInfo += "modem_reset is:" +"Success"+"\n";
                     SharepreferenceUtils.saveInfo(GcfActivity.this, CURRENT_MODE, keyName);
                     mATCmd = engconstents.ENG_AUTO_ANSWER + 1;
                     Log.d(TAG, "OPEN_AUTO_ANSWER");
                     mStrTmp = IATUtils.sendATCmd(mATCmd, "atchannel0");
                     if (mStrTmp.contains(IATUtils.AT_OK)) {
                         resultInfo += "auto answer is:" +"Success"+"\n";
                     } else {
                         resultInfo += "auto answer is:" +"Fail"+"\n";
                     }
                     mTelephonyManager.setDataEnabled(false);
                     resultInfo += "Close data connect is:" +"Success"+"\n";
                     mATCmd = engconstents.ENG_AT_SET_GCF + EngineerModeActivity_2.ONE + 2;

                     /* SPRD Bug 868741. */
                     //mAtCmd1 = engconstents.ENG_AT_SET_GCF + EngineerModeActivity_2.TWO + 2;
                     Log.d(TAG, "SET_GCF_RF");
                     mStrTmp = IATUtils.sendATCmd(mATCmd, "atchannel0");
                     //mStrTmp1 = IATUtils.sendATCmd(mAtCmd1, "atchannel0");
                     //EngineerModeActivity_2.checkAtReturnResult(mStrTmp, mStrTmp1, mContext);
                     EngineerModeActivity_2.checkAtReturnResultForGcf(mStrTmp, mContext);

                     Message mTestOverRf = mGcfHandler.obtainMessage(TEST_OVER,keyName);
                     mGcfHandler.sendMessageDelayed(mTestOverRf, DELAY_SECONDS);
                     break;
                 case SET_GCF_SIM:
                     SystemPropertiesProxy.set(CFU_CONTROL, "0");
                     resultInfo = "always_not_query is :" +"Success"+"\n";
                     SystemPropertiesProxy.set("persist.vendor.sys.modemreset","1");
                     resultInfo += "modem_reset is:" +"Success"+"\n";
                     ImsManagerProxy.INSTANCE.setEnhanced4gLteModeSetting(GcfActivity.this, false);
                     resultInfo += "Close volte is:" +"Success"+"\n";
                     SharepreferenceUtils.saveInfo(GcfActivity.this, CURRENT_MODE, keyName);
                     mATCmd = engconstents.ENG_AT_SET_GCF + EngineerModeActivity_2.ONE + 3;

                     /* SPRD Bug 868741. */
                     //mAtCmd1 = engconstents.ENG_AT_SET_GCF + EngineerModeActivity_2.TWO + 3;
                     Log.d(TAG, "SET_GCF_SIM");
                     mStrTmp = IATUtils.sendATCmd(mATCmd, "atchannel0");
                     //mStrTmp1 = IATUtils.sendATCmd(mAtCmd1, "atchannel0");
                     //EngineerModeActivity_2.checkAtReturnResult(mStrTmp, mStrTmp1, mContext);
                     EngineerModeActivity_2.checkAtReturnResultForGcf(mStrTmp, mContext);

                     Message mTestOverSim = mGcfHandler.obtainMessage(TEST_OVER,keyName);
                     mGcfHandler.sendMessageDelayed(mTestOverSim, DELAY_SECONDS);
                     break;
                 case SET_GCF_VOLTE:
                     TelephonyManagerProxy.INSTANCE.setNetworkRoaming(false);
                     resultInfo = "Close data roaming is :" +"Success"+"\n";
                     ImsManagerProxy.INSTANCE.setEnhanced4gLteModeSetting(GcfActivity.this, true);
                     resultInfo += "Open volte is:" +"Success"+"\n";
                     SystemPropertiesProxy.set(CFU_CONTROL, "0");
                     resultInfo += "always_not_query is :" +"Success"+"\n";
                     SharepreferenceUtils.saveInfo(GcfActivity.this, CURRENT_MODE, keyName);
                     mATCmd = engconstents.ENG_AT_SET_GCF + EngineerModeActivity_2.ONE + 4;

                     /* SPRD Bug 868741. */
                     //mAtCmd1 = engconstents.ENG_AT_SET_GCF + EngineerModeActivity_2.TWO + 4;
                     Log.d(TAG, "SET_GCF_VOLTE");
                     mStrTmp = IATUtils.sendATCmd(mATCmd, "atchannel0");
                     //mStrTmp1 = IATUtils.sendATCmd(mAtCmd1, "atchannel0");
                     //EngineerModeActivity_2.checkAtReturnResult(mStrTmp, mStrTmp1, mContext);
                     EngineerModeActivity_2.checkAtReturnResultForGcf(mStrTmp, mContext);

                     Message mTestOverVolte = mGcfHandler.obtainMessage(TEST_OVER,keyName);
                     mGcfHandler.sendMessageDelayed(mTestOverVolte, DELAY_SECONDS);
                     break;
                 case TEST_OVER:
                     showDialog(resultInfo,keyName);
                     break;
                 default:
                     break;
            }
        }
     private void showDialog(String resultInfo,String testTitle) {
         Log.d(TAG, "resultInfo is " + resultInfo +"testTitle is"+testTitle);
         AlertDialog alertDialog = new AlertDialog.Builder(
                 GcfActivity.this)
                 .setTitle(testTitle)
                 .setCancelable(false)
                 .setMessage(resultInfo)
                 .setPositiveButton(R.string.alertdialog_ok,
                         new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog,
                                     int which) {
                             }
                         }).create();
         alertDialog.show();
        }
    }
}
