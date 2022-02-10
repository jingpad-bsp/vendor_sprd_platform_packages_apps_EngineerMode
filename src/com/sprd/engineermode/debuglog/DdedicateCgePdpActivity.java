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

public class DdedicateCgePdpActivity extends Activity implements OnClickListener{


    private static final String TAG = "DdedicateCgePdpActivity";
    private static final int SET_CGE_AT = 0;
    private EditText mCid;
    private EditText mQci;
    private EditText mDlGbr;
    private EditText mUlGbr;
    private EditText mDlMbr;
    private EditText mUlMbr;
    private Button mActived;
    public String stringCid = null;
    public String stringQci = null;
    public String stringDlGbr = null;
    public String stringUlGbr = null;
    public String stringDlMbr = null;
    public String stringUlMbr = null;
    public String stringCmd = null;
    private CgePdpHandler mCgePdpHandler;
    private Handler mUiThread = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dedicate_cge_pdp);
        initUI();
        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mCgePdpHandler = new CgePdpHandler(ht.getLooper());
    }
    private void initUI() {
        mCid = (EditText)findViewById(R.id.cge_cid);
        mQci = (EditText)findViewById(R.id.cge_qci);
        mDlGbr = (EditText)findViewById(R.id.cge_dl_gbr);
        mUlGbr = (EditText)findViewById(R.id.cge_ul_gbr);
        mDlMbr = (EditText)findViewById(R.id.cge_dl_mbr);
        mUlMbr = (EditText)findViewById(R.id.cge_ul_mbr);
        mCid.setText("7");
        mQci.setText("1");
        mDlGbr.setText("128");
        mUlGbr.setText("128");
        mDlMbr.setText("384");
        mUlMbr.setText("384");
        mCid.setHint("please input 0~800");
        mActived = (Button)findViewById(R.id.cga_activate);
        mActived.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        getSettingParam();
        if(v==mActived){
            Message cgdMessage = mCgePdpHandler.obtainMessage(SET_CGE_AT,stringCmd);
            mCgePdpHandler.sendMessage(cgdMessage);
        }

    }
    class CgePdpHandler extends Handler{
        public CgePdpHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            String atResponse = null;
            String atCmd = null;
            switch (msg.what) {
            case SET_CGE_AT:
                atCmd = (String) msg.obj;
                Log.d(TAG, "SET_CGE atCmd = " + engconstents.ENG_AT_SET_CGE + atCmd);
                atResponse = IATUtils.sendATCmd(engconstents.ENG_AT_SET_CGE + atCmd, "atchannel0");
                Log.d(TAG, "SET_CGE responValue = " + atResponse);
                if(atResponse != null && atResponse.contains(IATUtils.AT_OK)){
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DdedicateCgePdpActivity.this, "activate cge success",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else{
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DdedicateCgePdpActivity.this, "activate cge fail",
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
        stringQci = mQci.getText().toString();
        stringDlGbr = mDlGbr.getText().toString();
        stringUlGbr = mUlGbr.getText().toString();
        stringDlMbr = mDlMbr.getText().toString();
        stringUlMbr = mUlMbr.getText().toString();
        stringCmd = stringCid + "," + stringQci + "," + stringDlGbr + ","
            + stringUlGbr + "," + stringDlMbr + "," + stringUlMbr;
        Log.d(TAG, "Now testing cge, the setting Cid is: \n cid " + stringCid
                + "\n Qci is " + stringQci + "\n DlGbr is " + stringDlGbr
                + "\n UlGbr is " + stringUlGbr + "\n DlMbr is " + stringDlMbr
                + "\n UlMbr is " + stringUlMbr + "\n stringCmd is " + stringCmd);
    }


}