package com.sprd.engineermode.telephony;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.utils.IATUtils;

public class LocalReleaseActivity extends Activity {
    private EditText mTimerEdit;
    private boolean mSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_release);

        mTimerEdit = (EditText)findViewById(R.id.local_release_timer);
        Button setBtn = (Button) findViewById(R.id.set_button);
        setBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSuccess = setTimer(mTimerEdit.getText().toString());
                if (mSuccess) {
                    Toast.makeText(LocalReleaseActivity.this, getResources().getString(R.string.set_success), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LocalReleaseActivity.this, getResources().getString(R.string.set_fail), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        String timer = getTimer();
        if (timer.equals("0")) {
            Toast.makeText(this, getResources().getString(R.string.get_fail), Toast.LENGTH_SHORT).show();
        } else {
            mTimerEdit.setText(timer);
        }
    }

    private boolean setTimer(String value) {
        String timer = mTimerEdit.getText().toString();
        String atCmd = engconstents.ENG_AT_SET_NAS_DUMMY + "\"set nas local release timer\"," + timer + ",1";
        String result = IATUtils.sendATCmd(atCmd, 0);
        if (result.contains(IATUtils.AT_OK)) {
            return true;
        } else {
            return false;
        }
    }

    private String getTimer() {
        String atCmd = engconstents.ENG_AT_SET_NAS_DUMMY + "\"get nas local release timer\"";
        String result = IATUtils.sendATCmd(atCmd, 0);
        if (result.contains(IATUtils.AT_OK)){
            String[] str = result.split("\n");
            if (str.length < 1) {
                return "0";
            }
            String[] str1 = str[0].split(",");
            return str1[str1.length - 1];
        } else {
            return "0";
        }
    }
}
