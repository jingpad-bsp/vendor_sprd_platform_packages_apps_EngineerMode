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

public class DdedicateCgtPdpActivity extends Activity implements OnClickListener{

    private static final String TAG = "DdedicateCgtPdpActivity";
    private static final int SET_CGT_AT = 0;
    private EditText mEtCid;
    private EditText mEtPacketFilter;
    private EditText mEtEvaluation;
    private EditText mEtSourceAddress;
    private EditText mEtProtocolNumber;
    private EditText mEtDestination;
    private EditText mEtSourcePort;
    private EditText mEtIpsecSecurity;
    private EditText mEtType;
    private EditText mEtFlowLabel;
    private Button mActived;
    public String strCid = null;
    public String strPacket = null;
    public String strEva = null;
    public String strSou = null;
    public String strPro = null;
    public String strDes = null;
    public String strSourcePort = null;
    public String strIpsec = null;
    public String strType = null;
    public String strFlow = null;
    public String strCmd = null;
    private CgtPdpHandler mCgtPdpHandler;
    private Handler mUiThread = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dedicate_cgt_pdp);
        initUI();
        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mCgtPdpHandler = new CgtPdpHandler(ht.getLooper());
    }
    private void initUI() {
        mEtCid = (EditText)findViewById(R.id.cgt_cid);
        mEtPacketFilter = (EditText)findViewById(R.id.cgt_packet_filter);
        mEtEvaluation = (EditText)findViewById(R.id.cgt_evaluation_precedence);
        mEtSourceAddress = (EditText)findViewById(R.id.cgt_source_address);
        mEtProtocolNumber = (EditText)findViewById(R.id.cgt_protocol_number);
        mEtDestination = (EditText)findViewById(R.id.cgt_destination_port_range);
        mEtSourcePort = (EditText)findViewById(R.id.cgt_source_port_range);
        mEtIpsecSecurity = (EditText)findViewById(R.id.cgt_ipsec_security_parameter);
        mEtType = (EditText)findViewById(R.id.cgt_type_of_service);
        mEtFlowLabel = (EditText)findViewById(R.id.cgt_flow_label);
        mEtCid.setText("7");
        mEtPacketFilter.setText("2");
        mEtEvaluation.setText("5");
        mEtSourceAddress.setText("\"" + "10.19.4.1.255.255.255.255" + "\"");
        mEtProtocolNumber.setText("1");
        mActived = (Button)findViewById(R.id.cga_activate);
        mActived.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        getSettingParam();
        if(v==mActived){
            Message cgtMessage = mCgtPdpHandler.obtainMessage(SET_CGT_AT,strCmd);
            mCgtPdpHandler.sendMessage(cgtMessage);
        }

    }
    class CgtPdpHandler extends Handler{
        public CgtPdpHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            String atResponse = null;
            String atCmd = null;
            switch (msg.what) {
            case SET_CGT_AT:
                atCmd = (String) msg.obj;
                Log.d(TAG, "SET_CGT atCmd = " + engconstents.ENG_AT_SET_CGT + atCmd);
                atResponse = IATUtils.sendATCmd(engconstents.ENG_AT_SET_CGT + atCmd, "atchannel0");
                Log.d(TAG, "SET_CGT responValue = " + atResponse);
                if(atResponse != null && atResponse.contains(IATUtils.AT_OK)){
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DdedicateCgtPdpActivity.this, "activate cgt success",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else{
                    mUiThread.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DdedicateCgtPdpActivity.this, "activate cgt fail",
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
        strCid = mEtCid.getText().toString();
        strPacket = mEtPacketFilter.getText().toString();
        strEva = mEtEvaluation.getText().toString();
        strSou = mEtSourceAddress.getText().toString();
        strPro = mEtProtocolNumber.getText().toString();
        strDes = mEtDestination.getText().toString();
        strSourcePort = mEtSourcePort.getText().toString();
        strIpsec = mEtIpsecSecurity.getText().toString();
        strType = mEtType.getText().toString();
        strFlow = mEtFlowLabel.getText().toString();
        strCmd = strCid + "," + strPacket + "," + strEva + ","
            + strSou + "," + strPro + "," + strDes + ","
            + strSourcePort+ "," + strIpsec+ "," + strType+ "," + strFlow;
        Log.d(TAG, "Now testing cgt, the setting Cid is: \n cid " + strCid
                + "\n strPacket is " + strPacket + "\n strEva is " + strEva
                + "\n strSou is " + strSou + "\n strPro is " + strPro
                + "\n strDes is " + strDes + "\n strSourcePort is " + strSourcePort
                + "\n strIpsec is " + strIpsec+ "\n strType is " + strType
                +"\n strFlow is " + strFlow+ "\n stringCmd is " + strCmd);
    }



}