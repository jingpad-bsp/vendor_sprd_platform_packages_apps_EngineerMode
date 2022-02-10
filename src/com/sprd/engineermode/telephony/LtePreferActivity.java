package com.sprd.engineermode.telephony;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;
import com.unisoc.engineermode.core.intf.ITelephonyApi;

public class LtePreferActivity extends Activity {

    private static final String TAG = "LtePreferActivity";

    private Button mControlButton;

    private EditText mUpRateEdit;
    private EditText mDownRateEdit;
    private EditText mDurationEdit;
    private EditText mSpeedDurationEdit;
    private EditText mRatePercentEdit;
    private EditText mSpeedTrigerEdit;
    private EditText mEmergencyEdit;
    private EditText mNrWeakRsrpEdit;
    private EditText mLteWeakRsrpEdit;
    private EditText mSpeedThresholdEdit;
    private TextView mRatePercentText;
    private TextView mSpeedTrigerText;
    private TextView mEmergencyText;
    private TextView mNrWeakRsrpText;
    private TextView mLteWeakRsrpText;
    private TextView mSpeedThresholdText;

    private String mResults;
    private static final int MIN_RATE = 0;
    private static final int MAX_RATE = 10000;
    private static final int MAX_LEN = 12;
    private int atResLength = 0;

    private static final String ENGTEST_NR_ENABLE = "persist.radio.engtest.nr.enable";

    private ITelephonyApi telephonyApi = CoreApi.getTelephonyApi();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lte_prefer);

        mUpRateEdit = (EditText) findViewById(R.id.up_rate);
        mDownRateEdit = (EditText) findViewById(R.id.down_rate);
        mDurationEdit = (EditText) findViewById(R.id.duration);
        mSpeedDurationEdit = (EditText) findViewById(R.id.high_speed_duration);
        mRatePercentEdit = (EditText) findViewById(R.id.rate_percent);
        mSpeedTrigerEdit = (EditText)findViewById(R.id.speed_triger);
        mEmergencyEdit = (EditText) findViewById(R.id.max_emergency);
        mNrWeakRsrpEdit = (EditText) findViewById(R.id.nr_weak_rsrp);
        mLteWeakRsrpEdit = (EditText) findViewById(R.id.lte_weak_rsrp);
        mSpeedThresholdEdit = (EditText) findViewById(R.id.speed_threshold);
        mRatePercentText = (TextView) findViewById(R.id.tv_rate_percent);
        mSpeedTrigerText = (TextView) findViewById(R.id.tv_speed_triger);
        mEmergencyText = (TextView) findViewById(R.id.tv_emergency);
        mNrWeakRsrpText = (TextView) findViewById(R.id.tv_nr_rsrp);
        mLteWeakRsrpText = (TextView) findViewById(R.id.tv_lte_rsrp);
        mSpeedThresholdText = (TextView) findViewById(R.id.tv_threshold_percent);

        mControlButton = (Button) findViewById(R.id.start_btn);
        mControlButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                controlLtePrefer(!SystemPropertiesProxy.getBoolean(ENGTEST_NR_ENABLE, false),atResLength);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        new GetLtePreferAsyncTask().execute();
    }

    private void controlLtePrefer(boolean enabled,int len) {
        if (enabled) {
            String upRateValue = mUpRateEdit.getText().toString();
            if (TextUtils.isEmpty(upRateValue) || Integer.parseInt(upRateValue) < MIN_RATE || Integer.parseInt(upRateValue) > MAX_RATE) {
                Toast.makeText(this, getResources().getString(R.string.upstream_rate_prompt), Toast.LENGTH_SHORT).show();
                return ;
            }
            String downRateValue = mDownRateEdit.getText().toString();
            if (TextUtils.isEmpty(downRateValue) || Integer.parseInt(downRateValue) < MIN_RATE || Integer.parseInt(downRateValue) > MAX_RATE) {
                Toast.makeText(this, getResources().getString(R.string.downstream_rate_prompt), Toast.LENGTH_SHORT).show();
                return ;
            }
            String durationValue = mDurationEdit.getText().toString();
            if (TextUtils.isEmpty(durationValue)) {
                Toast.makeText(this, getResources().getString(R.string.resident_duration_prompt), Toast.LENGTH_SHORT).show();
                return ;
            }
            String speedDurationValue = mSpeedDurationEdit.getText().toString();
            if (TextUtils.isEmpty(speedDurationValue)) {
                Toast.makeText(this, getResources().getString(R.string.high_speed_duration_prompt), Toast.LENGTH_SHORT).show();
                return ;
            }
            String mParams = "";
            if (len >= MAX_LEN) {
                String ratePercent = mRatePercentEdit.getText().toString();
                if (TextUtils.isEmpty(ratePercent)) {
                    Toast.makeText(this, getResources().getString(R.string.rate_percent_prompt), Toast.LENGTH_SHORT).show();
                    return ;
                }
                String speedTriger = mSpeedTrigerEdit.getText().toString();
                if (TextUtils.isEmpty(speedTriger)) {
                    Toast.makeText(this, getResources().getString(R.string.speed_triger_prompt), Toast.LENGTH_SHORT).show();
                    return ;
                }
                String emergency = mEmergencyEdit.getText().toString();
                if (TextUtils.isEmpty(emergency)) {
                    Toast.makeText(this, getResources().getString(R.string.emergency_prompt), Toast.LENGTH_SHORT).show();
                    return ;
                }
                String nrWeakRsrp = mNrWeakRsrpEdit.getText().toString();
                if (TextUtils.isEmpty(nrWeakRsrp)) {
                    Toast.makeText(this, getResources().getString(R.string.nr_weak_rsrp_prompt), Toast.LENGTH_SHORT).show();
                    return ;
                }
                String LteWeakRsrp = mLteWeakRsrpEdit.getText().toString();
                if (TextUtils.isEmpty(LteWeakRsrp)) {
                    Toast.makeText(this, getResources().getString(R.string.lte_weak_rsrp_prompt), Toast.LENGTH_SHORT).show();
                    return ;
                }
                String speedThreshold = mSpeedThresholdEdit.getText().toString();
                if (TextUtils.isEmpty(speedThreshold)) {
                    Toast.makeText(this, getResources().getString(R.string.speed_threshold_prompt), Toast.LENGTH_SHORT).show();
                    return ;
                }
                mParams = String.format(",%s,%s,%s,%s,%s,%s,%s,%s,%s,%s", upRateValue, downRateValue, durationValue, speedDurationValue,
                        ratePercent,speedTriger,emergency,nrWeakRsrp,LteWeakRsrp,speedThreshold);
            } else {
                mParams = String.format(",%s,%s,%s,%s", upRateValue, downRateValue, durationValue, speedDurationValue);
            }
            new OpenLtePreferAsyncTask().execute(mParams);
        } else {
            new CloseLtePreferAsyncTask().execute();
        }
    }

    private class OpenLtePreferAsyncTask extends AsyncTask<String, Integer, Integer>{
        @Override
        protected void onPostExecute(Integer result) {
            if (result == 0) {
                mControlButton.setText(getResources().getString(R.string.function_start));
                mControlButton.setBackgroundColor(Color.GRAY);
                Toast.makeText(LtePreferActivity.this, getResources().getString(R.string.start_fail_prompt), Toast.LENGTH_SHORT).show();
            } else {
                SystemPropertiesProxy.set(ENGTEST_NR_ENABLE, "true");
                mControlButton.setText(getResources().getString(R.string.function_stop));
                mControlButton.setBackgroundColor(Color.GREEN);
                Toast.makeText(LtePreferActivity.this, getResources().getString(R.string.start_success_prompt), Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        protected Integer doInBackground(String... params) {
            try {
                telephonyApi.ltePrefer().openLtePrefer(params[0]);
                return 1;
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
    }

    private class CloseLtePreferAsyncTask extends AsyncTask<Void, Integer, Integer>{
        @Override
        protected void onPostExecute(Integer result) {
            if (result == 0) {
                mControlButton.setText(getResources().getString(R.string.function_stop));
                mControlButton.setBackgroundColor(Color.GREEN);
                Toast.makeText(LtePreferActivity.this, getResources().getString(R.string.stop_fail_prompt), Toast.LENGTH_SHORT).show();
            } else {
                SystemPropertiesProxy.set(ENGTEST_NR_ENABLE, "false");
                mControlButton.setText(getResources().getString(R.string.function_start));
                mControlButton.setBackgroundColor(Color.GRAY);
                Toast.makeText(LtePreferActivity.this, getResources().getString(R.string.stop_success_prompt), Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        protected Integer doInBackground(Void... params) {
            try {
                telephonyApi.ltePrefer().closeLtePrefer();
                return 1;
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
    }

    private class GetLtePreferAsyncTask extends AsyncTask<Void, Integer, Integer>{
        @Override
        protected void onPostExecute(Integer result) {
            Log.i(TAG, "GetLtePreferAsyncTask onPostExecute result: " + result);
            if (result == 0) {
                Toast.makeText(LtePreferActivity.this, getResources().getString(R.string.get_status_fail_prompt), Toast.LENGTH_SHORT).show();
            } else {
                AnalyzeResults(mResults);
            }
        }
        @Override
        protected Integer doInBackground(Void... params) {
            try {
                mResults = telephonyApi.ltePrefer().getLtePrefer();
                return 1;
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
    }

    private void AnalyzeResults(String result) {
        String[] str = result.split("\n");
        if (str.length < 1) {
            return ;
        }
        //Unisoc: Bug1508977 get params remove invalid characters
        String[] str1 = str[0].trim().split(",");
        atResLength = str1.length;
        if (atResLength < 6) {
            return;
        }

        if (atResLength >= MAX_LEN) {
            mRatePercentEdit.setVisibility(View.VISIBLE);
            mSpeedTrigerEdit.setVisibility(View.VISIBLE);
            mEmergencyEdit.setVisibility(View.VISIBLE);
            mNrWeakRsrpEdit.setVisibility(View.VISIBLE);
            mLteWeakRsrpEdit.setVisibility(View.VISIBLE);
            mSpeedThresholdEdit.setVisibility(View.VISIBLE);
            mRatePercentText.setVisibility(View.VISIBLE);
            mSpeedTrigerText.setVisibility(View.VISIBLE);
            mEmergencyText.setVisibility(View.VISIBLE);
            mNrWeakRsrpText.setVisibility(View.VISIBLE);
            mLteWeakRsrpText.setVisibility(View.VISIBLE);
            mSpeedThresholdText.setVisibility(View.VISIBLE);
            mUpRateEdit.setText(str1[2]);
            mDownRateEdit.setText(str1[3]);
            mDurationEdit.setText(str1[4]);
            mSpeedDurationEdit.setText(str1[5]);
            mRatePercentEdit.setText(str1[6]);
            mSpeedTrigerEdit.setText(str1[7]);
            mEmergencyEdit.setText(str1[8]);
            mNrWeakRsrpEdit.setText(str1[9]);
            mLteWeakRsrpEdit.setText(str1[10]);
            mSpeedThresholdEdit.setText(str1[11]);
        } else {
            mUpRateEdit.setText(str1[2]);
            mDownRateEdit.setText(str1[3]);
            mDurationEdit.setText(str1[4]);
            mSpeedDurationEdit.setText(str1[5]);
        }

        if ("1".equals(str1[1])) {
            SystemPropertiesProxy.set(ENGTEST_NR_ENABLE, "true");
            mControlButton.setText(getResources().getString(R.string.function_stop));
            mControlButton.setBackgroundColor(Color.GREEN);
        } else {
            SystemPropertiesProxy.set(ENGTEST_NR_ENABLE, "false");
            mControlButton.setText(getResources().getString(R.string.function_start));
            mControlButton.setBackgroundColor(Color.GRAY);
        }
    }
}