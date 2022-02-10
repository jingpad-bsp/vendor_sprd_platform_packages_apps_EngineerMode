package com.sprd.engineermode.debuglog;


import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import android.view.View.OnClickListener;
import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.utils.IATUtils;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Looper;
import android.os.Message;
import android.os.Handler;
import android.os.HandlerThread;
import android.content.SharedPreferences;
import android.content.Context;
import android.content.Intent;

import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.utils.IATUtils;
import java.util.Timer;
import java.util.TimerTask;
import static com.sprd.engineermode.EMApplication.getContext;


public class ModemAssertTestActivity extends Activity implements OnClickListener{

    private static final String TAG = "ModemAssertTestActivity";

    private Toast mToast;
    private EditText mEt, mEt_Number, mEt_Space;
    private Button mModem_Assert_Ok;
    private Button mModem_Assert_Return;
    private Timer mTimer;

    private String mModemAt = "AT+SPTEST=45,1,";
    private int mMinValue = 10;
    private int mMinNumber = 1;
    private int mMinSpace = 20;
    private String mStr, mStrNumber, mStrSpace;
    private int mInt, mIntNumber, mIntSpace;
    private String mResult;
    private String mAt;
    private String mTime;

    private Context mContext;
    public Intent mIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = getContext();
        setContentView(R.layout.activity_modem_assert);
        mModem_Assert_Ok = (Button) findViewById(R.id.modem_assert_ok);
        mModem_Assert_Ok.setOnClickListener(this);
        mModem_Assert_Return = (Button) findViewById(R.id.modem_assert_return);
        mModem_Assert_Return.setOnClickListener(this);
        mEt = (EditText) findViewById(R.id.modem_assert_value);
        mEt_Number = (EditText) findViewById(R.id.modem_assert_number);
        mEt_Space = (EditText) findViewById(R.id.modem_assert_space);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    private void init() {
        mEt.setText("600");
        mEt_Number.setText("1");
        mEt_Space.setText("20");
    }

    private void valuesGet() {
        mStr = mEt.getText().toString();
        mStrNumber = mEt_Number.getText().toString();
        mStrSpace = mEt_Space.getText().toString();
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mModem_Assert_Ok)) {
            valuesGet();
            if (TextUtils.isEmpty(mStr) || TextUtils.isEmpty(mStrNumber) || TextUtils.isEmpty(mStrSpace)) {
                mToast = Toast.makeText(this, "input can't be null", Toast.LENGTH_SHORT);
                mToast.show();
            } else {
                try {
                    mInt = Integer.parseInt(mStr);
                    mIntNumber = Integer.parseInt(mStrNumber);
                    mIntSpace = Integer.parseInt(mStrSpace);
                    if (mInt < mMinValue || mIntNumber < mMinNumber || mIntSpace < mMinSpace) {
                        mToast = Toast.makeText(this, "input value can't be less than " + mMinValue + "s, " + "input number can't be less than " + mMinNumber + ", " + "input space can't be less than " + mMinSpace + "s,", Toast.LENGTH_SHORT);
                        mToast.show();
                    } else {
                        mIntent = new Intent(ModemAssertTestActivity.this, ModemAssertTestService.class);
                        mIntent.putExtra("delaytime", mInt);
                        mIntent.putExtra("number", mIntNumber);
                        mIntent.putExtra("restarttime", mIntSpace);
                        mContext.startService(mIntent);
                    }
                } catch (NumberFormatException e) {
                    mToast = Toast.makeText(this, "input value must be integer！", Toast.LENGTH_SHORT);
                    mToast.show();
                }
            }
        } else if (v.equals(mModem_Assert_Return)) {
            mToast = Toast.makeText(this, "modem assert return", Toast.LENGTH_SHORT);
            mToast.show();
            finish();
        }
    }
}
