
package com.sprd.engineermode.telephony;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.unisoc.engineermode.core.CoreApi;
import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.impl.nonpublic.SubscriptionManagerProxy;
import com.unisoc.engineermode.core.impl.telephony.TelephonyManagerSprd;
import com.unisoc.engineermode.core.utils.IATUtils;

public class GPRSSetActivity extends Activity implements OnClickListener {

    private static final String TAG = "GPRSSetActivity";
    private static final String KEY_SIM_INDEX = "simindex";
    private static ITelephonyApi teleApi = CoreApi.getTelephonyApi();

    private static final int MAX_PDP_CONTEXT = 3;
    private static final String[] Traffic_class = {"0","1","2","3","4"};

    private static final int GPRS_ATTACHED = 1;
    private static final int GPRS_DETACHED = 2;
    private static final int GPRS_ALWAYS_ATTACH = 3;
    private static final int GPRS_WHEN_NEEDED_ATTACH = 4;
    private static final int GPRS_ACTIVATE_PDP = 5;
    private static final int GPRS_DEACTIVATE_PDP = 6;
    private static final int GPRS_SEND_DATA = 7;
    private static final int GET_AUTOATT_STATUS = 8;
    /* SPRD: modify 20140424 Spreadtrum of 302980 telephonyt-gprs and telephony-ps related-gprs data send function repeat @{*/
    private static final int GPRS_CLEAR_DATA = 9;
    /* @} */

    /* For UIhandler */
    private static final int GPRS_STATE_UPDATE = 0;

    private static final String SET_SEC_PDP = ",1,1,\"2.2.2.2.255.255.0.0\",48,\"65435.65535\",\"65235.65335\"";

    private int mSimIndex = 0;
    private Button mAttachBt;
    private Button mDetachBt;
    private RadioGroup mAlwaysNeededAttach;
    private RadioGroup mSetPrimSec;
    private RadioGroup mSetPdp;
    private RadioButton mSetFirPdp;
    private RadioButton mSetSecPdp;
    private RadioButton mSetPrim;
    private RadioButton mSetSec;
    private RadioButton mAlwaysAttach;
    private RadioButton mNeedAttach;
    private Button mActiveatePDP;
    private Button mDeactivatePDP;
    private Button mSendData;
    private Button mClearData;
    private EditText mInput1;
    private EditText mInput2;
    private Spinner mPDPTypeSpinner,mTrafficClassSpinner;
    private ArrayAdapter<String> mPDPTypeAdapter,mTrafficClassAdapter;
    private int mPDPType;
    private int mTrafficClass;
    private int mExistPDPType;
    private final int mContextCount = (TelephonyManagerSprd.getModemType()==TelephonyManagerSprd.MODEM_TYPE_WCDMA )? 3:6;
    private String[] PDP_context = new String[mContextCount];
    private Handler mUiThread = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GPRS_STATE_UPDATE:
                    if (mAttached) {
                        mActiveatePDP.setEnabled(true);
                        mDeactivatePDP.setEnabled(true);
                        mSendData.setEnabled(true);
                        mClearData.setEnabled(true);
/* SPRD: modify 20140512 Spreadtrum of 308862  telephonyt-gprs,using different colors to distinguish the Attached's and Detached's current status @{*/
                        mAttachBt.setEnabled(false);
                        mDetachBt.setEnabled(true);
                        /* @} */
                        updateAdapter();
                    } else {
                        mActiveatePDP.setEnabled(false);
                        mDeactivatePDP.setEnabled(false);
                        mSendData.setEnabled(false);
                        mClearData.setEnabled(false);
/* SPRD: modify 20140512 Spreadtrum of 308862  telephonyt-gprs,using different colors to distinguish the Attached's and Detached's current status @{*/
                        mAttachBt.setEnabled(true);
                        mDetachBt.setEnabled(false);
                        /* @} */
                    }
                    break;
            }
        }
    };
    private GPRSHandler mGPRSHandler;
    private String mDialogMessage;
    private String mInputDataLength;
    private String mInputDataContent="";
    private boolean mAttached = false;
    private boolean[] mPdpContextState = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*mSimIndex = getIntent().getIntExtra(KEY_SIM_INDEX,-1);
        if(mSimIndex == -1){
            Toast.makeText(GPRSSetActivity.this, "The Wrong simindex " + mSimIndex, Toast.LENGTH_SHORT)
            .show();
            finish();
        }
        setTitle(getResources().getString(R.string.gprs_sim_title) + mSimIndex);
        Log.d(TAG,"current simindex = " + mSimIndex);*/

        for (int i =0 ; i< mContextCount; i++) {
            PDP_context[i] = "PDP Context " + (i+1);
        }
        setContentView(R.layout.gprs_set);
        mAttachBt = (Button) findViewById(R.id.gprs_attach);
        mAttachBt.setOnClickListener(this);
        mDetachBt = (Button) findViewById(R.id.gprs_deattach);
        mDetachBt.setOnClickListener(this);
        mAlwaysNeededAttach = (RadioGroup) findViewById(R.id.always_needed_attach);
        mAlwaysAttach = (RadioButton) GPRSSetActivity.this.findViewById(R.id.always);
        mAlwaysAttach.setOnClickListener(this);
        mNeedAttach = (RadioButton) GPRSSetActivity.this.findViewById(R.id.needed);
        mNeedAttach.setOnClickListener(this);

        mSetPdp = (RadioGroup) findViewById(R.id.radiogroup_pdp_set);
        mSetFirPdp = (RadioButton) mSetPdp.findViewById(R.id.fir_pdp);
        mSetFirPdp.setOnClickListener(this);
        mSetSecPdp = (RadioButton) mSetPdp.findViewById(R.id.sec_pdp);
        mSetSecPdp.setOnClickListener(this);

        mSetPrimSec = (RadioGroup) findViewById(R.id.radiogroup_pdp);
        mSetPrim = (RadioButton) mSetPrimSec.findViewById(R.id.as_prm);
        mSetSec = (RadioButton) mSetPrimSec.findViewById(R.id.as_sec);

        mPDPTypeSpinner = (Spinner) findViewById(R.id.pdp_spinner);
        mPDPTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                PDP_context);
        mPDPTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPDPTypeSpinner.setAdapter(mPDPTypeAdapter);
        mPDPTypeSpinner.setOnItemSelectedListener(new PDPSelectedListener());
        mPDPTypeSpinner.setVisibility(View.VISIBLE);

        mTrafficClassSpinner=(Spinner) findViewById(R.id.traffic_class_spinner);
        mTrafficClassAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                Traffic_class);
        mTrafficClassAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTrafficClassSpinner.setAdapter(mTrafficClassAdapter);
        mTrafficClassSpinner.setOnItemSelectedListener(new TrafficClassSelectedListener());
        mTrafficClassSpinner.setVisibility(View.VISIBLE);

        mActiveatePDP = (Button) findViewById(R.id.activate_pdp);
        mActiveatePDP.setOnClickListener(this);
        mDeactivatePDP = (Button) findViewById(R.id.deactivate_pap);
        mDeactivatePDP.setOnClickListener(this);
        mInput1 = (EditText) findViewById(R.id.edit_data1);
        mInput2 = (EditText) findViewById(R.id.edit_data2);
        mSendData = (Button) findViewById(R.id.send_data);
        mSendData.setOnClickListener(this);
        /* SPRD: modify 20140424 Spreadtrum of 302980 telephonyt-gprs and telephony-ps related-gprs data send function repeat @{*/
        mClearData = (Button) findViewById(R.id.clear_data);
        mClearData.setOnClickListener(this);
        /* @} */
        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mGPRSHandler = new GPRSHandler(ht.getLooper());
    }

    @Override
    protected void onStart() {
//        final SubscriptionManager subscriptionManager = SubscriptionManager.from(this);
        mSimIndex= SubscriptionManagerProxy.getDefaultDataPhoneId();
        /*if(!subscriptionManager.isValidSlotId(mSimIndex)){
            finish();
        }*/
        if(mSimIndex==-1){
            Toast.makeText(GPRSSetActivity.this, "The Wrong simindex " + mSimIndex, Toast.LENGTH_SHORT)
            .show();
            finish();
        }
        setTitle(getResources().getString(R.string.gprs_sim_title) + mSimIndex);
        Log.d(TAG,"current simindex = " + mSimIndex);
        Message getAutoATTStatus = mGPRSHandler.obtainMessage(GET_AUTOATT_STATUS);
        mGPRSHandler.sendMessage(getAutoATTStatus);
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        if (mGPRSHandler != null) {
            Log.d(TAG, "HandlerThread has quit");
            mGPRSHandler.getLooper().quit();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    private int pdpContextCount() {
        int count = 0;
        if (mPdpContextState != null) {
            for (int i = 0; i < mPdpContextState.length; i++) {
                if (mPdpContextState[i]) {
                    count++;
                }
            }
        }

        return count;
    }

    private boolean[] phasePdpContextState(String atRsp) {
        boolean[] phaseResult = new boolean[PDP_context.length];
        if (atRsp.contains(IATUtils.AT_OK)) {
            String[] line = atRsp.split("\n");
            for (int i = 0; i < phaseResult.length & i < line.length - 1; i++) {
                Log.d(TAG, "line" + i + "=" + line[i]);
                String[] pdpContextInfo = line[i].split(",");

                if (pdpContextInfo[1] != null
                        && pdpContextInfo[1].contains("1")) {
                    phaseResult[i] = true;
                } else {
                    phaseResult[i] = false;
                }
            }
            //Unisoc:Bug #1422499 GPRS Crash
            if (phaseResult.length > line.length - 1) {
                for (int i = line.length - 1; i < phaseResult.length; i++) {
                    phaseResult[i] = false;
                }
            }
        }

        return phaseResult;
    }

    private void updateGprsStatus(boolean isAttached, boolean[] isPdpContextActive) {
        mAttached = isAttached;
        mPdpContextState = isPdpContextActive;

        mUiThread.sendEmptyMessage(GPRS_STATE_UPDATE);
    }

    private void updateGprsStatus(int position, boolean isActive) {
        if (mPdpContextState != null) {
            mPdpContextState[position] = isActive;
        }
        mUiThread.sendEmptyMessage(GPRS_STATE_UPDATE);
    }

    private void updateAdapter() {
        int backup = mPDPTypeSpinner.getSelectedItemPosition();
        if (mPdpContextState == null) {
            mPDPTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                    PDP_context);
            mPDPTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mPDPTypeSpinner.setAdapter(mPDPTypeAdapter);
            mPDPTypeSpinner.setSelection(backup);
            return;
        }

        String[] PdpContextWithState = new String[PDP_context.length];
        for (int i = 0; i < PDP_context.length; i++) {
            if (mPdpContextState[i]) {
                PdpContextWithState[i] = PDP_context[i] + "    Actived";
            } else {
                PdpContextWithState[i] = PDP_context[i];
            }
        }

        mPDPTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                PdpContextWithState);
        mPDPTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPDPTypeSpinner.setAdapter(mPDPTypeAdapter);
        mPDPTypeSpinner.setSelection(backup);
    }

    class PDPSelectedListener implements OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            mPDPType = arg2 + 1;
            Log.d(TAG, "mPDPType is " + mPDPType);
            if (mAttached) {
                if (mPdpContextState[arg2]) {
                    mActiveatePDP.setEnabled(false);
                    mDeactivatePDP.setEnabled(true);
                    mSendData.setEnabled(true);
                    mClearData.setEnabled(true);
                } else {
                    mActiveatePDP.setEnabled(true);
                    mDeactivatePDP.setEnabled(false);
                    mSendData.setEnabled(false);
                    mClearData.setEnabled(false);
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

	class TrafficClassSelectedListener implements OnItemSelectedListener {
		@Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
			mTrafficClass = arg2;
			Log.d(TAG, "mTrafficClass is " + mTrafficClass);
		}

		@Override
        public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

    @Override
    public void onClick(View v) {
        if (v.equals(mAttachBt)) {
            Message setAttach = mGPRSHandler.obtainMessage(GPRS_ATTACHED);
            mGPRSHandler.sendMessage(setAttach);
            return;
        }
        if (v.equals(mAlwaysAttach)) {
            Message setAlways = mGPRSHandler.obtainMessage(GPRS_ALWAYS_ATTACH);
            mGPRSHandler.sendMessage(setAlways);
            return;
        }
        if (v.equals(mNeedAttach)) {
            Message setNeeded = mGPRSHandler.obtainMessage(GPRS_WHEN_NEEDED_ATTACH);
            mGPRSHandler.sendMessage(setNeeded);
            return;
        }
        if (v.equals(mDetachBt)) {
            Message setDetach = mGPRSHandler.obtainMessage(GPRS_DETACHED);
            mGPRSHandler.sendMessage(setDetach);
            return;
        }
        if (v.equals(mSetFirPdp)) {
            mSetPrim.setEnabled(false);
            mSetSec.setEnabled(false);
            return;
        }
        if (v.equals(mSetSecPdp)) {
            mSetPrim.setEnabled(true);
            mSetPrim.setChecked(true);
            mSetSec.setEnabled(true);
            return;
        }
        if (v.equals(mActiveatePDP)) {
            mActiveatePDP.setEnabled(false);
            Message activatePDP = mGPRSHandler.obtainMessage(GPRS_ACTIVATE_PDP);
            mGPRSHandler.sendMessage(activatePDP);
            return;
        }
        if (v.equals(mDeactivatePDP)) {
            mDeactivatePDP.setEnabled(false);
            Message deActivatePDP = mGPRSHandler.obtainMessage(GPRS_DEACTIVATE_PDP);
            mGPRSHandler.sendMessage(deActivatePDP);
            return;
        }
        if (v.equals(mSendData)) {
            if (mInput1.getText().toString().equals("")) {
                Toast.makeText(GPRSSetActivity.this, "please input data length", Toast.LENGTH_SHORT)
                .show();
            } else {
                if (Integer.valueOf(mInput1.getText().toString()) >= 1
                        && Integer.valueOf(mInput1.getText().toString()) <= 16000) {
                    Message sendData = mGPRSHandler.obtainMessage(GPRS_SEND_DATA);
                    mGPRSHandler.sendMessage(sendData);
                } else {
                    Toast.makeText(GPRSSetActivity.this, "please input 1 ~ 16000",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
        /* SPRD: modify 20140424 Spreadtrum of 302980 telephonyt-gprs and telephony-ps related-gprs data send function repeat @{*/
        if(v.equals(mClearData)) {
            Message clearData = mGPRSHandler.obtainMessage(GPRS_CLEAR_DATA);
            mGPRSHandler.sendMessage(clearData);
        }
        /* @} */
    }

    /* SPRD: modify 20140424 Spreadtrum of 302980 telephonyt-gprs and telephony-ps related-gprs data send function repeat @{*/
    private void clearEditText() {
        mInput1.setText("");
        mInput2.setText("");
    }
    /* @} */

    private void showResult(final String message) {
        if (message != null) {
            mUiThread.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(GPRSSetActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean isPDPExist(int i) {
        return mPdpContextState[i];
    }

    private boolean isValidPDP(int setPDP) {
        if (setPDP != mExistPDPType) {
            return true;
        } else {
            return false;
        }
    }

    class GPRSHandler extends Handler {

        public GPRSHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            String values,tvalues,apnValues;
            String atCmd;
            switch (msg.what) {
                case GPRS_ATTACHED: {
                    try {
                        teleApi.gprsOper().attach(mSimIndex);
                        mDialogMessage = "GPRS Attached Success";
                        updateGprsStatus(true, teleApi.gprsOper().getPdpContextState(mSimIndex));
                    } catch (Exception e) {
                        mDialogMessage = "GPRS Attached Failed";
                    }

                    showResult(mDialogMessage);
                    break;
                }
                case GPRS_DETACHED: {
                    try {
                        teleApi.gprsOper().detach(mSimIndex);
                        mDialogMessage = "GPRS Detached Success";
                        updateGprsStatus(false, teleApi.gprsOper().getPdpContextState(mSimIndex));
                    } catch (Exception e) {
                        mDialogMessage = "GPRS Detached Failed";
                    }

                    showResult(mDialogMessage);
                    break;
                }
                case GPRS_ALWAYS_ATTACH: {
                    try {
                        teleApi.gprsOper().setAlwaysAttach(mSimIndex);
                        mDialogMessage = "GPRS Always Attach Success";
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mAttachBt.setEnabled(false);
                                mDetachBt.setEnabled(false);
                            }
                        });
                        updateGprsStatus(true, teleApi.gprsOper().getPdpContextState(mSimIndex));
                    } catch (Exception e) {
                        mDialogMessage = "GPRS Always Attach Failed";
                    }

                    showResult(mDialogMessage);
                    break;
                }
                case GPRS_WHEN_NEEDED_ATTACH: {
                    try {
                        teleApi.gprsOper().setNeededAttach(mSimIndex);
                        mDialogMessage = "GPRS When Needed Attach Success";
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mAttachBt.setEnabled(false);
                                mDetachBt.setEnabled(false);
                            }
                        });
                        updateGprsStatus(true, teleApi.gprsOper().getPdpContextState(mSimIndex));
                    } catch (Exception e) {
                        mDialogMessage = "GPRS When Needed Attach Failed";
                    }
                    showResult(mDialogMessage);
                    break;
                }
                case GPRS_ACTIVATE_PDP: {
                    if (pdpContextCount() >= MAX_PDP_CONTEXT) {
                        mDialogMessage = "Reach the Max! Cannot active!";

                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mActiveatePDP.setEnabled(true);
                            }
                        });
                    } else if (mPdpContextState[mPDPType - 1]) {
                        mDialogMessage = "This same CID PDP exist";
                    } else if (mSetFirPdp.isChecked()
                            || (mSetSecPdp.isChecked() && mSetPrim.isChecked())) {
                        // active one PDP,need to make sure no same cid PDP
                        if (teleApi.gprsOper().activateFirstPdp(mSimIndex, mTrafficClass, mPDPType)) {
                                mDialogMessage = "GPRS Activate PDP Success";
                            updateGprsStatus(mPDPType - 1, true);
                        } else {
                            mDialogMessage = "GPRS Activate PDP Failed";

                            mUiThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    mActiveatePDP.setEnabled(true);
                                }
                            });
                        }
                    } else if (mSetSecPdp.isChecked() && mSetSec.isChecked()) {
                        // when one PDP exists,the secPDP can active
                        if (pdpContextCount() != 0) {
                            if (teleApi.gprsOper().activateSecondPdp(mSimIndex, mPDPType)) {
                                updateGprsStatus(mPDPType - 1, true);
                            }
                        } else {
                            mDialogMessage = "NO PDP Activiated";

                            mUiThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    mActiveatePDP.setEnabled(true);
                                }
                            });
                        }
                    }
                    showResult(mDialogMessage);
                    break;
                }
                case GPRS_DEACTIVATE_PDP: {

                    if (teleApi.gprsOper().deactivatePdp(mSimIndex, mPDPType)) {
                        mDialogMessage = "GPRS DeActivate PDP Success";
                        updateGprsStatus(mPDPType - 1, false);
                    } else {
                        mDialogMessage = "GPRS DeActivate PDP Failed";
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mDeactivatePDP.setEnabled(true);
                            }
                        });
                    }
                    showResult(mDialogMessage);
                    break;
                }
                case GPRS_SEND_DATA: {
                    // send data to make sure pdp is active
                    /* SPRD: modify 20140424 Spreadtrum of 302980 telephonyt-gprs and telephony-ps related-gprs data send function repeat @{*/
                    mInputDataLength = mInput1.getText().toString().trim();
                    mInputDataContent = mInput2.getText().toString();
                    if (teleApi.gprsOper().sendData(mSimIndex, mPDPType, mInputDataLength, mInputDataContent)) {
                        mDialogMessage = "GPRS Send Data Success";
                    } else {
                        mDialogMessage = "GPRS Send Data Failed";
                    }
                    showResult(mDialogMessage);
                    break;
                }
                case GPRS_CLEAR_DATA: {
                    mUiThread.post(new Runnable() {
                         @Override
                         public void run() {
                            clearEditText();
                         }
                     });
                }
                case GET_AUTOATT_STATUS: {
                    boolean isAutoAttach;
                    try {
                        isAutoAttach = teleApi.gprsOper().getAutoAttachState(mSimIndex);
                    } catch (Exception e) {
                        e.printStackTrace();
                        // set default
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mAlwaysAttach.setChecked(true);
                                mNeedAttach.setChecked(false);
                            }
                        });
                        return;
                    }

                    if (isAutoAttach) {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mAlwaysAttach.setChecked(true);
                                mNeedAttach.setChecked(false);
                            }
                        });
                        updateGprsStatus(true, phasePdpContextState(IATUtils.sendATCmd(
                                engconstents.ENG_AT_GETPDPACTIVE1, "atchannel" + mSimIndex)));
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                mAlwaysAttach.setChecked(false);
                                mNeedAttach.setChecked(true);
                            }
                        });
                        updateGprsStatus(true, phasePdpContextState(IATUtils.sendATCmd(
                                engconstents.ENG_AT_GETPDPACTIVE1, "atchannel" + mSimIndex)));
                    }

                    break;
                }
                default:
                    break;
            }

        }
    }
}
