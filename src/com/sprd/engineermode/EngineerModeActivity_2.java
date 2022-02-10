package com.sprd.engineermode;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;
import android.content.Context;
import android.os.PowerManager;

import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.common.engconstents;
import com.sprd.engineermode.gcf.OperatorActivity;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.utils.IATUtils;

public class EngineerModeActivity_2 extends PreferenceActivity{
    private static final String TAG = "EngineerModeActivity_2";
    private static final String KEY_CURRENT_MODE = "current_mode";
    private static final String KEY_GCF = "gcf";
    private static final String KEY_IOT = "iot";
    private static final String KEY_OPERATOR = "operator";
    private static final String KEY_APC = "apc_mode";
    private static final String CURRENT_MODE = "current_mode";
    public static final String ERROR = "ERROR";
    public static final String ACTION_DISABLE_SELECTION = "action.disable.selection";
    public static final String ONE = "1,";
    public static final String TWO = "2,";
    private Context mContext;
    private EngMode_two_Handler mEngMode_two_Handler;
    private Preference mCurrentMode;
    private PreferenceScreen mAPC;
    private Preference mGcf, mIot, mOperator;
    private int testValue1, testValue2;
    private String testMode = "+SPGCFIOTOPER: 0,0";
    private String value1, value2;
    private static final String[] mItemNamesList = new String[] {
        "gcf_protocol", "gcf_rf", "gcf_sim", "gcf_volte",
        "iot_common", "iot_huawei", "iot_ericsson", "iot_nsn",
        "operator_cmcc", "operator_cucc", "operator_orange", "operator_telstra", "operator_vodafone", "operator_telcel",
        "operator_movistar", "operator_reliance", "operator_dt", "operator_mtn", "operator_claro", "operator_docomo","operator_telefonica"
    };
    private ITelephonyApi teleApi = CoreApi.getTelephonyApi();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mEngMode_two_Handler = new EngMode_two_Handler(ht.getLooper());
        mContext = this;
        addPreferencesFromResource(R.xml.pref_engineermode_activity_two);
        mCurrentMode = (Preference) findPreference(KEY_CURRENT_MODE);
        /* bug 1242269, unknow feature @ { */
        /*
        mGcf = (Preference) findPreference(KEY_GCF);
        mIot = (Preference) findPreference(KEY_IOT);
        mOperator = (Preference) findPreference(KEY_OPERATOR);
        */
        /* }@ */
        mAPC = (PreferenceScreen) findPreference(KEY_APC);
        /* SPRD 1113117: APC function @{ */
        if (teleApi.telephonyInfo().isSupportAPCFunc()) {
            //getPreferenceScreen().removePreference(mOperator);
        } else {
            getPreferenceScreen().removePreference(mAPC);
        }
        /* }@ */
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_DISABLE_SELECTION);
        this.registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    public void onStart() {
        //String currentMode =SharepreferenceUtils.getInfo(EngineerModeActivity_2.this, CURRENT_MODE);
        String currentMode = IATUtils.sendATCmd(engconstents.ENG_AT_GET_GCF, "atchannel0");
        String mode = analyseMode(currentMode);
        Log.d(TAG, "currentMode is " + mode);
        mCurrentMode.setSummary(mode);
        if (!mode.equals("none")) disableSelection();
        super.onStart();
    }

    private void enableSelection() {
        /* bug 1242269, unknow feature @ { */
        /*
        mGcf.setEnabled(true);
        mIot.setEnabled(true);
        mOperator.setEnabled(true);
        */
        /* }@ */
    }

    private void disableSelection() {
        /* bug 1242269, unknow feature @ { */
        /*
        mGcf.setEnabled(false);
        mIot.setEnabled(false);
        mOperator.setEnabled(false);
        */
        /* }@ */
    }

    private String analyseMode(String currentMode) {
        String[] result = currentMode.split("\n|:");
        String mode = null;
        String[] values;
        if (result[1].contains(",")) {
            values = result[1].trim().split(",");
        } else {
            Log.d(TAG,"please check the format of return value! ");
            return ERROR;
        }
        //Log.d(TAG, "return result = " + result[1]);
        if (values[0].equals(values[1])) {
            if (values[0].equals("0")) {
                //enableSelection();
                return "none";
            } else {
                //disableSelection();
                return getMode(values[0]);
            }
        } else if (values[0].equals("0")) {
            String setValue0 = IATUtils.sendATCmd(engconstents.ENG_AT_SET_GCF + EngineerModeActivity_2.ONE + values[1], "atchannel0");
            if (setValue0.contains(IATUtils.AT_OK)) {
                Log.d(TAG, "setValue0 success!");
            }
            return getMode(values[1]);
        } else if (values[1].equals("0")) {
            String setValue1 = IATUtils.sendATCmd(engconstents.ENG_AT_SET_GCF + EngineerModeActivity_2.TWO + values[0], "atchannel0");
            if (setValue1.contains(IATUtils.AT_OK)) {
                Log.d(TAG, "setValue1 success!");
            }
            return getMode(values[0]);
        } else {
            Toast.makeText(mContext, "The mode in this version is not correct", Toast.LENGTH_LONG).show();
            return ERROR;
        }


    }
    private String getMode(String values) {
        String mode = null;
        int i = Integer.parseInt(values.trim());
        if (i >= 30) {
            mode = OperatorActivity.keys[i-30];
            return mode;
        }
        switch (i) {
        case 1:
            mode = "gcf_protocol";
            break;
        case 2:
            mode = "gcf_rf";
            break;
        case 3:
            mode = "gcf_sim";
            break;
        case 4:
            mode = "gcf_volte";
            break;
        case 10:
            mode = "iot_common";
            break;
        case 11:
            mode = "iot_huawei";
            break;
        case 12:
            mode = "iot_ericsson";
            break;
        case 13:
            mode = "iot_nsn";
            break;
        /*case 30:
            mode = "operator_cmcc";
            break;
        case 31:
            mode = "operator_cucc";
            break;
        case 32:
            mode = "operator_orange";
            break;
        case 33:
            mode = "operator_telstra";
            break;
        case 34:
            mode = "operator_vodafone";
            break;
        case 35:
            mode = "operator_telcel";
            break;
        case 36:
            mode = "operator_movistar";
            break;
        case 37:
            mode = "operator_reliance";
            break;
        case 38:
            mode = "operator_dt";
            break;
        case 39:
            mode = "operator_mtn";
            break;
        case 40:
            mode = "operator_claro";
            break;
        case 41:
            mode = "operator_docomo";
            break;
        case 42:
            mode = "operator_telefonica";
            break;
        case 43:
            mode = "operator_ukee";
            break;
        case 44:
            mode = "operator_megafon";
            break;
        case 45:
            mode = "operator_tmobile";
            break;
        case 46:
            mode = "operator_true";
            break;
        case 47:
            mode = "operator_ytlc";
            break;
        case 48:
            mode = "operator_at&t";
            break;
        case 49:
            mode = "operator_oysters";
            break;
        case 50:
            mode = "operator_beeline";
            break;
        case 51:
            mode = "operator_tim";
            break;
        case 52:
            mode = "operator_ais";
            break;
        case 53:
            mode = "operator_smarfren";
            break;
        case 54:
            mode = "operator_dtag";
            break;
        case 55:
            mode = "operator_etisalat";
            break;
        case 56:
            mode = "operator_mts";
            break;
        case 57:
            mode = "operator_prestigio";
            break;
        case 58:
            mode = "operator_seatel";
            break;
        case 59:
            mode = "operator_unitel";
            break;
        case 60:
            mode = "operator_altice";
            break;
        case 61:
            mode = "operator_peru_claro";
            break;
        case 62:
            mode = "operator_dialog";
            break;
        case 63:
            mode = "operator_dtac";
            break;
        case 64:
            mode = "operator_inwi";
            break;
        case 65:
            mode = "operator_irancell";
            break;
        case 66:
            mode = "operator_mascom";
            break;
        case 67:
            mode = "operator_ntel";
            break;
        case 68:
            mode = "operator_smile";
            break;
        case 69:
            mode = "operator_veon";
            break;
        case 70:
            mode = "operator_vodacom";
            break;
        case 71:
            mode = "operator_fastlink";
            break;
        case 72:
            mode = "operator_zone";
            break;
        case 73:
            mode = "operator_ctcc";
            break;*/
        default:
            mode = "none";
            break;
        }
        return mode;
    }
    /*  60 OPER Altice;61 OPER Peru_Claro;62 OPER Dialog;63 OPER DTAC;64 OPER INWI;65 OPER Irancell*/
    /*  66 OPER Mascom;67 OPER Ntel;68 OPER Smile;69 OPER Veon;70 OPER Vodacom;71 OPER Fastlink*/
    /*  72 OPER Zone;73 OPER CTCC*/


    public static void checkAtReturnResult(String result1, String result2, Context context) {
        Log.d(TAG, "checkAtReturnResult" + "result1: " + result1 + "result2: " + result2);

        if (result1.contains(IATUtils.AT_OK) && result2.contains(IATUtils.AT_OK)) {
            Toast.makeText(context, "success", Toast.LENGTH_LONG).show();
            if (!(context instanceof OperatorActivity)) return;
            //reset the cp and disable selections
            String result = IATUtils.sendATCmd(engconstents.ENG_AT_RESET, "atchannel0");
            if (result.contains(IATUtils.AT_OK)) {
                Toast.makeText(context, "modem resetting, please wait!", Toast.LENGTH_LONG).show();
            }
            sendDisableSelectionMessage(context);
            confirmToReboot(context);
        } else if (result1.contains(EngineerModeActivity_2.ERROR) ||
                result2.contains(EngineerModeActivity_2.ERROR)){
         Toast.makeText(context, "not support command!", Toast.LENGTH_LONG).show();
        } else{
            Toast.makeText(context, "fail", Toast.LENGTH_LONG).show();
        }
    }

    /* SPRD Bug 868741. */
    public static void checkAtReturnResultForGcf(String result1, Context context) {
        Log.d(TAG, "checkAtReturnResultForGcf" + "\t" + "result1: " + result1);
        if (result1.contains(IATUtils.AT_OK)) {
            Toast.makeText(context, "success", Toast.LENGTH_LONG).show();
            if (!(context instanceof OperatorActivity)) return;
            String result = IATUtils.sendATCmd(engconstents.ENG_AT_RESET, "atchannel0");
            if (result.contains(IATUtils.AT_OK)) {
                Toast.makeText(context, "modem resetting, please wait!", Toast.LENGTH_LONG).show();
            }
            sendDisableSelectionMessage(context);
            confirmToReboot(context);
        } else if (result1.contains(EngineerModeActivity_2.ERROR)) {
            Toast.makeText(context, "not support command!", Toast.LENGTH_LONG).show();
        } else{
            Toast.makeText(context, "fail", Toast.LENGTH_LONG).show();
        }
    }

    private static void confirmToReboot(final Context context) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
        .setMessage(R.string.orange_remaining)
        .setCancelable(false)
        .setPositiveButton(R.string.orange_remaining_ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PowerManager pm = (PowerManager) context
                              .getSystemService(Context.POWER_SERVICE);
                        pm.reboot("switch_orange");
                    }
                }).create();
        alertDialog.show();
    }

    public static void sendDisableSelectionMessage(Context context) {
        Intent intent = new Intent(ACTION_DISABLE_SELECTION);
        context.sendBroadcast(intent);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        String key = preference.getKey();
        Log.d(TAG, "key is " + key);
        if (key == null) {
            return false;
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onDestroy() {
        if (mBroadcastReceiver != null) {
            this.unregisterReceiver(mBroadcastReceiver);
        }
        super.onDestroy();
    }

    private static class EngMode_two_Handler extends Handler{
        public EngMode_two_Handler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
            }
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive:action = " + action);
            if (action.equals(ACTION_DISABLE_SELECTION)) {
                disableSelection();
            }
        }

    };
}
