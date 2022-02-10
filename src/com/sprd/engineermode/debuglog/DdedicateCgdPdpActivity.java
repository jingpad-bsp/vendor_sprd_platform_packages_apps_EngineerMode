package com.sprd.engineermode.debuglog;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.util.Log;
import android.view.View;
import com.unisoc.engineermode.core.utils.IATUtils;
import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.common.engconstents;

public class DdedicateCgdPdpActivity extends Activity implements OnClickListener{
    private static final String TAG = "DedicateCgdPdpActivity";
    private static final int SET_CGD_AT = 0;
    private EditText mCid;
    private EditText mPid;
    private Button mActived;
    public String stringCid = null;
    public String stringPid = null;
    public String stringCmd = null;
    private CgdPdpHandler mCgdPdpHandler;
    private Handler mUiThread = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dedicate_cgd_pdp);
        initUI();
        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mCgdPdpHandler = new CgdPdpHandler(ht.getLooper());
    }
    private void initUI() {
        mCid = (EditText)findViewById(R.id.cgd_cid);
        mPid = (EditText)findViewById(R.id.cgd_pid);
        mCid.setText("7");
        mPid.setText("1");
        mActived = (Button)findViewById(R.id.cgd_activate);
        mActived.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        getSettingParam();
        if(v==mActived){
            Message cgdMessage = mCgdPdpHandler.obtainMessage(SET_CGD_AT,stringCmd);
            mCgdPdpHandler.sendMessage(cgdMessage);
        }

    }
    class CgdPdpHandler extends Handler{
        public CgdPdpHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            String atResponse = null;
            String atCmd = null;
            switch (msg.what) {
            case SET_CGD_AT:
                atCmd = (String) msg.obj;
                Log.d(TAG, "SET_CGD atCmd = " + engconstents.ENG_AT_SET_CGD + atCmd);
                atResponse = IATUtils.sendATCmd(engconstents.ENG_AT_SET_CGD + atCmd, "atchannel0");
                Log.d(TAG, "SET_CGD responValue = " + atResponse);
                if(atResponse != null && atResponse.contains(IATUtils.AT_OK)){
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DdedicateCgdPdpActivity.this, "activate cgd success",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else{
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DdedicateCgdPdpActivity.this, "activate cgd fail",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
             default:
                break;
            }
        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
    }
    private void getSettingParam() {
        stringCid = mCid.getText().toString();
        stringPid = mPid.getText().toString();
        stringCmd = stringCid+","+stringPid;
        Log.d(TAG, "Now testing cgd, the setting param is: \n cid " + stringCid
                + "\n pid is " + stringPid +"\n pid is "+stringCmd);
    }
}