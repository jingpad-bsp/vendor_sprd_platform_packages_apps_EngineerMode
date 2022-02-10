package com.sprd.engineermode.gcf;

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
import com.unisoc.engineermode.core.utils.IATUtils;
import com.unisoc.engineermode.core.common.engconstents;

public class IotActivity extends PreferenceActivity{

     private static final String TAG = "IotActivity";
     private static final String KEY_COMMON = "iot_common";
     private static final String KEY_HUAWEI = "iot_huawei";
     private static final String KEY_ERICSSON = "iot_ericsson";
     private static final String KEY_NSN = "iot_nsn";
     private IotHandler mIotHandler;

     private static final int SET_IOT_COMMON = 1;
     private static final int SET_IOT_HUAWEI = 2;
     private static final int SET_IOT_ERICSSON = 3;
     private static final int SET_IOT_NSN = 4;

     private String mATCmd, mAtCmd1;
     private String mStrTmp, mStrTmp1;
     private Context mContext;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         HandlerThread ht = new HandlerThread(TAG);
         ht.start();
         mIotHandler = new IotHandler(ht.getLooper());
         addPreferencesFromResource(R.xml.pref_iot_activity);
         mContext = this;

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

         if (key.equals(KEY_COMMON)) {
             Message mIotCommon = mIotHandler.obtainMessage(SET_IOT_COMMON,key);
             mIotHandler.sendMessage(mIotCommon);
         } else if (key.equals(KEY_HUAWEI)){
             Message mIotHuawei = mIotHandler.obtainMessage(SET_IOT_HUAWEI,key);
             mIotHandler.sendMessage(mIotHuawei);
         } else if (key.equals(KEY_ERICSSON)){
             Message mIotEricsson = mIotHandler.obtainMessage(SET_IOT_ERICSSON,key);
             mIotHandler.sendMessage(mIotEricsson);
         } else if (key.equals(KEY_NSN)){
             Message mIotNsn = mIotHandler.obtainMessage(SET_IOT_NSN,key);
             mIotHandler.sendMessage(mIotNsn);
         }

         return super.onPreferenceTreeClick(preferenceScreen, preference);
     }
     @Override
     public void onDestroy() {

         super.onDestroy();
     }
     class IotHandler extends Handler{
         public IotHandler(Looper looper) {
             super(looper);
         }
         @Override
         public void handleMessage(Message msg){
             String keyName = (String) msg.obj;
             Log.d(TAG, "keyName is " + keyName);
             switch (msg.what){
             case SET_IOT_COMMON:
                 mATCmd = engconstents.ENG_AT_SET_GCF + EngineerModeActivity_2.ONE + 10;
                 mAtCmd1 = engconstents.ENG_AT_SET_GCF + EngineerModeActivity_2.TWO + 10;
                 Log.d(TAG, "SET_IOT_COMMON");
                 mStrTmp = IATUtils.sendATCmd(mATCmd, "atchannel0");
                 mStrTmp1 = IATUtils.sendATCmd(mAtCmd1, "atchannel0");
                 EngineerModeActivity_2.checkAtReturnResult(mStrTmp, mStrTmp1, mContext);
                 break;
             case SET_IOT_HUAWEI:
            	 mATCmd = engconstents.ENG_AT_SET_GCF + EngineerModeActivity_2.ONE + 11;
                 mAtCmd1 = engconstents.ENG_AT_SET_GCF + EngineerModeActivity_2.TWO + 11;
                 Log.d(TAG, "SET_IOT_HUAWEI");
                 mStrTmp = IATUtils.sendATCmd(mATCmd, "atchannel0");
                 mStrTmp1 = IATUtils.sendATCmd(mAtCmd1, "atchannel0");
                 EngineerModeActivity_2.checkAtReturnResult(mStrTmp, mStrTmp1, mContext);
                 break;
             case SET_IOT_ERICSSON:
            	 mATCmd = engconstents.ENG_AT_SET_GCF + EngineerModeActivity_2.ONE + 12;
                 mAtCmd1 = engconstents.ENG_AT_SET_GCF + EngineerModeActivity_2.TWO + 12;
                 Log.d(TAG, "SET_IOT_ERICSSON");
                 mStrTmp = IATUtils.sendATCmd(mATCmd, "atchannel0");
                 mStrTmp1 = IATUtils.sendATCmd(mAtCmd1, "atchannel0");
                 EngineerModeActivity_2.checkAtReturnResult(mStrTmp, mStrTmp1, mContext);
                 break;
             case SET_IOT_NSN:
            	 mATCmd = engconstents.ENG_AT_SET_GCF + EngineerModeActivity_2.ONE + 13;
                 mAtCmd1 = engconstents.ENG_AT_SET_GCF + EngineerModeActivity_2.TWO + 13;
                 Log.d(TAG, "SET_IOT_NSN");
                 mStrTmp = IATUtils.sendATCmd(mATCmd, "atchannel0");
                 mStrTmp1 = IATUtils.sendATCmd(mAtCmd1, "atchannel0");
                 EngineerModeActivity_2.checkAtReturnResult(mStrTmp, mStrTmp1, mContext);
                 break;
             default:
                 break;
             }
         }
     }
}
