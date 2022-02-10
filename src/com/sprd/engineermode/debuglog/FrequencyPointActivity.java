package com.sprd.engineermode.debuglog;

import android.app.Activity;
import android.view.View;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.sprd.engineermode.R;

import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.utils.IATUtils;

public class FrequencyPointActivity extends Activity implements OnClickListener {

    private static final String TAG = "FrequencyPointActivity";

    private RadioGroup mPingAddress;
    private RadioButton mFrequencyLte;
    private RadioButton mFrequencyWcdma;
    private static final String PHONE_CHANNEL = "phonae_channel";
    private Button mOnOffBtn;
    private static final int UPDATE = 1;
    private boolean paused = true;

    private EditText mFrequencyEditText;
    private boolean mLte = true;
    private String frequencyNumber;
    /* SPRD BUG 874410 - Frequency Point test @{ */
    private String intentChannel;
    /* }@ */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frequency_point);
        /* SPRD BUG 874410 - Frequency Point test @{ */
        Intent intent = getIntent();
        intentChannel = intent.getStringExtra(PHONE_CHANNEL);
        /* }@ */
        mPingAddress = (RadioGroup) findViewById(R.id.radio_group);
        mFrequencyLte = (RadioButton) mPingAddress.findViewById(R.id.radio_btn_lte);
        mFrequencyLte.setOnClickListener(this);

        mFrequencyWcdma = (RadioButton) mPingAddress.findViewById(R.id.radio_btn_wcdma);
        mFrequencyWcdma.setOnClickListener(this);

        mFrequencyEditText = (EditText) findViewById(R.id.frequency_point_edit);

        mOnOffBtn = (Button) findViewById(R.id.start_btn);
        mOnOffBtn.setOnClickListener(this);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!paused) {
            }
        }
    };

    private String sendAt(String cmd, String servername) {
        String res = IATUtils.sendATCmd(cmd, servername);
        Log.d(TAG, "ATCmd is " + cmd + ", result is " + res);
        if (res != null) {
            return res;
        } else {
            return "FAILED";
        }
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.start_btn:
                frequencyNumber = mFrequencyEditText.getText().toString();
                if (frequencyNumber == null || frequencyNumber.equals("")) {
                    Toast.makeText(FrequencyPointActivity.this, "Frequency point is empty", Toast.LENGTH_SHORT).show();
                    break;
                }
                /* SPRD BUG 874410 - Frequency Point test @{ */
                if (mLte) {
                    sendAt(engconstents.ENG_AT_SFUN + "5", intentChannel);
                    sendAt(engconstents.ENG_AT_SPFRQ + "1", intentChannel);
                    sendAt(engconstents.ENG_AT_SFUN + "4", intentChannel);

                    sendAt(engconstents.ENG_AT_SFUN + "5", intentChannel);
                    sendAt(engconstents.ENG_AT_SPCLEANINFO + "5", intentChannel);
                    sendAt(engconstents.ENG_AT_SPFRQ + "0,4," + frequencyNumber, intentChannel);
                    sendAt(engconstents.ENG_AT_SFUN + "4", intentChannel);
                } else {
                    sendAt(engconstents.ENG_AT_SPFRQ + "0,0," + frequencyNumber, intentChannel);
                }
                /* }@ */
                break;
            case R.id.radio_btn_lte:
                Log.d(TAG, "onClick radio_btn_lte");
                mLte = true;
                break;
            case R.id.radio_btn_wcdma:
                Log.d(TAG, "onClick radio_btn_wcdma");
                mLte = false;
                break;
            default:
                break;
            }
    }
}
