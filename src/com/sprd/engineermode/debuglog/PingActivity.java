package com.sprd.engineermode.debuglog;

import android.app.Activity;
import android.view.View;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.sprd.engineermode.R;

import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PingActivity extends Activity implements OnClickListener {

    private static final String TAG = "PingActivity";

    private RadioGroup mPingAddress;
    private RadioButton mPingIPV4;
    private RadioButton mPingIPV6;

    private Button mOnOffBtn, mClearBtn;

    private TextView mText;
    private static final int UPDATE = 1;
    private boolean paused = true;
    Message message = null;

    private EditText mAddressEditText;
    private EditText mSizeEditText;
    private EditText mCountEditText;

    private String CMD = null;
    private String mStrAddress = null;
    private String mStrCount = null;
    private String mStrSize = null;
    private boolean mIPV4 = true;
    private Process proc = null;
    private String mResult = "";

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case UPDATE:
                    if (!paused) {
                        String result = (String)msg.obj;
                        if (result != null && !result.equals("null")) {
                            mText.setText(result);
                        } else {
                            mText.setText(getString(R.string.result_empty));
                        }
                    }
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ping_package);

        mPingAddress = (RadioGroup) findViewById(R.id.radio_group);
        mPingIPV4 = (RadioButton) mPingAddress.findViewById(R.id.radio_btn_ipv4);
        mPingIPV4.setOnClickListener(this);

        mPingIPV6 = (RadioButton) mPingAddress.findViewById(R.id.radio_btn_ipv6);
        mPingIPV6.setOnClickListener(this);

        mAddressEditText = (EditText) findViewById(R.id.ping_addr);
        mSizeEditText = (EditText) findViewById(R.id.ping_size);
        mCountEditText = (EditText) findViewById(R.id.ping_count);

        mOnOffBtn = (Button) findViewById(R.id.start_btn);
        mOnOffBtn.setOnClickListener(this);
        mClearBtn = (Button) findViewById(R.id.clear_btn);
        mClearBtn.setOnClickListener(this);

        mText = (TextView) findViewById(R.id.result_text);
    }

    private String runCMD(String[] cmd) {
        String line = "";
        String proNum = "";
        String lstproNum = "";
        int topNum = 5;
        InputStream is = null;
        try {
            proc = Runtime.getRuntime().exec(cmd);
            is = proc.getInputStream();
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            do {
                line = buf.readLine();
                Log.d(TAG, "run ShellUtiles line: " + line);
                if (line == null) {
                    break;
                }

                mResult = mResult + line + "\n\n";
                message = handler.obtainMessage(UPDATE, mResult);
                handler.sendMessageDelayed(message, 200);
            } while (true);
            Log.d(TAG, "run ShellUtiles line END");
            if (is != null) {
                buf.close();
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return mResult;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!paused) {
                runCMD(GenerateCMD(CMD));
            }
        }
    };

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
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "onBackPressed");
        paused = true;
        if (proc != null) {
            proc.destroy();
        }
    }

    private String[] GenerateCMD(String strCMD) {
        strCMD = strCMD.replaceAll("\\\\", "/");
        String[] strings = strCMD.split("\\s+");
        for (int i = 0; i < strings.length; i++) {
            Log.d(TAG, "strCMD:" + strings[i]);
        }
        return strings;
    }

    private String GetPingCMD() {
        mStrAddress = mAddressEditText.getText().toString();
        mStrCount = mCountEditText.getText().toString();
        if (mIPV4) {
            CMD = "ping ";
        } else {
            CMD = "ping6 ";
        }
        if (mStrCount == null || mStrCount.equals("")) {
            CMD = CMD + " -c 4 ";
        } else {
            CMD = CMD + " -c " + mStrCount + " ";
        }
        mStrSize = mSizeEditText.getText().toString();
        if (mStrSize == null || mStrSize.equals("")) {
            //mStrSize = "32";
        } else {
            CMD = CMD + " -s " + mStrSize + " ";
        }

        if (mStrAddress == null || mStrAddress.equals("")) {
            mStrAddress = " www.baidu.com";
        }
        CMD = CMD + mStrAddress;
        Log.d(TAG, "onClick start_btn CMD: " + CMD);
        return CMD;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
        case R.id.start_btn:
            if (paused) {
                GetPingCMD();
                paused = false;
                new Thread(runnable).start();
                mOnOffBtn.setText(R.string.coulometer_finish);
                mOnOffBtn.setBackground(getResources().getDrawable(R.drawable.btn_pause));
            } else {
                paused = true;
                mOnOffBtn.setText(R.string.coulometer_start);
                mOnOffBtn.setBackground(getResources().getDrawable(R.drawable.btn_refresh));
                if (proc != null) {
                    proc.destroy();
                }
            }
            break;
        case R.id.clear_btn:
            Log.d(TAG, "onClick clear_btn");
            mText.setText("");
            mResult = "";
            break;
        case R.id.radio_btn_ipv4:
            Log.d(TAG, "onClick radio_btn_ipv4");
            mIPV4 = true;
            break;
        case R.id.radio_btn_ipv6:
            Log.d(TAG, "onClick radio_btn_ipv6");
            mIPV4 = false;
            break;
        default:
            break;
        }
    }
}