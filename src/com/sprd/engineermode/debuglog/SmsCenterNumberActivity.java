package com.sprd.engineermode.debuglog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.os.Bundle;
import android.util.Log;
import android.content.Context;

import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.intf.IDebugLogApi;

import android.telephony.TelephonyManager;
import android.widget.RelativeLayout;
import android.telephony.SubscriptionManager;
import android.telephony.SubscriptionInfo;
import android.widget.Toast;

import com.sprd.engineermode.R;

public class SmsCenterNumberActivity extends Activity implements OnClickListener {

    private static final String TAG = "SmsCenterNumberActivity";

    private SubscriptionManager mSubscriptionManager;
    private EditText mSMSCentor1, mSMSCentor2;
    private Button mSetBtn1, mSetBtn2;
    private IDebugLogApi debuglogApi = CoreApi.getDebugLogApi();

    private RelativeLayout mLayoutSIM1, mLayoutSIM2;
    private int mPhoneCount;

//    private TelephonyManager[] mTelephonyManager;
    private TelephonyManager telephonyManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_center_number);

        mSubscriptionManager = (SubscriptionManager) SubscriptionManager
                .from(SmsCenterNumberActivity.this);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mPhoneCount = telephonyManager.getPhoneCount();


        mSMSCentor1 = (EditText) findViewById(R.id.sms_center1);
        mSMSCentor2 = (EditText) findViewById(R.id.sms_center2);

        mSetBtn1 = (Button) findViewById(R.id.set_btn1);
        mSetBtn1.setOnClickListener(this);

        mSetBtn2 = (Button) findViewById(R.id.set_btn2);
        mSetBtn2.setOnClickListener(this);

        mLayoutSIM1 = (RelativeLayout) findViewById(R.id.sim1_layout);
        mLayoutSIM2 = (RelativeLayout) findViewById(R.id.sim2_layout);
    }

    @Override
    public void onStart() {
        super.onStart();
        showSMSCentorNumber();
    }

    private void showSMSCentorNumber() {
        Log.d(TAG, "mPhoneCount: " + mPhoneCount);
        for (int i = 0; i < mPhoneCount; i++) {
            if (telephonyManager.getSimState(i) == TelephonyManager.SIM_STATE_READY) {
                Log.d(TAG, "SIM " + i + " SIM_STATE_READY");
                if (i == 0) {
                    mLayoutSIM1.setVisibility(View.VISIBLE);
                    String smsCentorNum1 = debuglogApi.smsCenterNumberApi().getSmsCenterNumber(slotIdToSubId(0));
                    mSMSCentor1.setText(smsCentorNum1);
                } else {
                    mLayoutSIM2.setVisibility(View.VISIBLE);
                    String smsCentorNum2 = debuglogApi.smsCenterNumberApi().getSmsCenterNumber(slotIdToSubId(1));
                    mSMSCentor2.setText(smsCentorNum2);
                }
            } else {
                Log.d(TAG, "SIM " + i + " SIM_STATE_NOT_READY");
                if (i == 0) {
                    mLayoutSIM1.setVisibility(View.GONE);
                } else {
                    mLayoutSIM2.setVisibility(View.GONE);
                }
            }
        }
    }


    public int slotIdToSubId(int phoneId) {
        int subId;
        @SuppressLint("MissingPermission")
        SubscriptionInfo mSubscriptionInfo = mSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(phoneId);
        if (mSubscriptionInfo != null) {
            subId = mSubscriptionInfo.getSubscriptionId();
        } else {
            subId = SubscriptionManager.getDefaultSubscriptionId();
        }
        Log.d(TAG, "slotIdToSubId subId : " + subId);
        return subId;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
        case R.id.set_btn1:
            String mCurSMSCentorNum1 = mSMSCentor1.getText().toString().trim();
            if (debuglogApi.smsCenterNumberApi().setSmsCenterNumber(slotIdToSubId(0), mCurSMSCentorNum1)) {
                Toast.makeText(this, "Set successful !", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Set failed !", Toast.LENGTH_SHORT).show();
            }
            break;
        case R.id.set_btn2:
            String mCurSMSCentorNum2 = mSMSCentor2.getText().toString().trim();
            if (debuglogApi.smsCenterNumberApi().setSmsCenterNumber(slotIdToSubId(1), mCurSMSCentorNum2)) {
                Toast.makeText(this, "Set successful !", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Set failed !", Toast.LENGTH_SHORT).show();
            }
            break;
        default:
            break;
        }
    }
}