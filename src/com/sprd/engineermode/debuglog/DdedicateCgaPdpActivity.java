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

public class DdedicateCgaPdpActivity extends Activity implements OnClickListener{

    private static final String TAG = "DdedicateCgaPdpActivity";
    private static final int SET_CGA_AT = 0;
    private EditText mState;
    private EditText mCid;
    private Button mActived;
    public String stringState = null;
    public String stringCid = null;
    public String stringCmd = null;
    private CgaPdpHandler mCgaPdpHandler;
    private Handler mUiThread = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dedicate_cga_pdp);
        initUI();
        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mCgaPdpHandler = new CgaPdpHandler(ht.getLooper());
    }
    private void initUI() {
        mState = (EditText)findViewById(R.id.cga_state);
        mCid = (EditText)findViewById(R.id.cga_cid);
        mState.setText("1");
        mCid.setText("7");
        mActived = (Button)findViewById(R.id.cga_activate);
        mActived.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        getSettingParam();
        if(v==mActived){
            Message cgdMessage = mCgaPdpHandler.obtainMessage(SET_CGA_AT,stringCmd);
            mCgaPdpHandler.sendMessage(cgdMessage);
        }

    }
    class CgaPdpHandler extends Handler{
        public CgaPdpHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            String atResponse = null;
            String atCmd = null;
            switch (msg.what) {
            case SET_CGA_AT:
                atCmd = (String) msg.obj;
                Log.d(TAG, "SET_CGA atCmd = " + engconstents.ENG_AT_SET_CGA + atCmd);
                atResponse = IATUtils.sendATCmd(engconstents.ENG_AT_SET_CGA + atCmd, "atchannel0");
                Log.d(TAG, "SET_CGA responValue = " + atResponse);
                if(atResponse != null && atResponse.contains(IATUtils.AT_OK)){
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DdedicateCgaPdpActivity.this, "activate cga success",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else{
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DdedicateCgaPdpActivity.this, "activate cga fail",
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
        stringState = mState.getText().toString();
        stringCid = mCid.getText().toString();
        stringCmd = stringState+","+stringCid;
        Log.d(TAG, "Now testing cga, the setting State is: \n cid " + stringState
                + "\n cid is " + stringCid +"\n stringCmd is "+stringCmd);
    }

}