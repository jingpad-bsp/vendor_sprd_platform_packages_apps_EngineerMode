package com.sprd.engineermode.connectivity.fm;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;
import android.content.Context;
import android.util.Log;
import com.android.fmradio.FmNative;
import com.android.fmradio.FmNative.FmRegCtlParms;
import com.sprd.engineermode.R;

public class FMRegModeActivity extends Activity implements OnClickListener{
    private static final String TAG = "FMRegModeActivity";

    private EditText mFmAddress,mFmVal;
    private Button mReadRegBtn,mWriteRegBtn;
    private Context mContext;

    private static final String hintText="00000000~ffffffff";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fm_reg_mode);
        mContext=this;

        mFmAddress=(EditText)findViewById(R.id.fm_address_edit);
        mFmAddress.setHint(hintText);
        mFmVal=(EditText)findViewById(R.id.fm_reg_val_edit);
        mFmVal.setHint(hintText);
        mReadRegBtn=(Button)findViewById(R.id.read_reg_btn);
        mReadRegBtn.setOnClickListener(this);
        mWriteRegBtn=(Button)findViewById(R.id.write_reg_btn);
        mWriteRegBtn.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.read_reg_btn:
                FmRegCtlParms read_para=new FmRegCtlParms();

                String read_address=mFmAddress.getText().toString();
                if(null == read_address || "".equals(read_address)){
                    Toast.makeText(mContext,"empty address!",Toast.LENGTH_SHORT).show();
                    return;
                } else if(read_address.length()==8 && Integer.parseInt(read_address.substring(0,1),16) > 7){
                    Toast.makeText(mContext,"please input address between 0~7fffffff!",Toast.LENGTH_SHORT).show();
                    return;
                }

                read_para.err=(byte)0;
                read_para.addr=Integer.parseInt(read_address,16);
                read_para.val=0;
                read_para.rw_flag=(byte)1;

                Log.d(TAG,"set parameter:address="+read_para.addr+";err="+read_para.err+";value="+read_para.val+";flag="+read_para.rw_flag);
                if(-1 == FmNative.readRegParm(read_para)){
                    Toast.makeText(mContext,"read reg fail!",Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"read reg fail!");
                    return;
                }else{
                    Log.d(TAG,"read parameter:address="+read_para.addr+";err="+read_para.err+";value="+read_para.val+";flag="+read_para.rw_flag);
                    int address=read_para.addr;
                    int value=read_para.val;
                    //mFmAddress.setText(Integer.toHexString(address));
                    mFmVal.setText(Integer.toHexString(value));
                }
                break;
            case R.id.write_reg_btn:
                String address=mFmAddress.getText().toString();
                if(null == address || "".equals(address)){
                    Toast.makeText(mContext,"empty address!",Toast.LENGTH_SHORT).show();
                    return;
                } else if(address.length()==8 && Integer.parseInt(address.substring(0,1),16) > 7){
                    Toast.makeText(mContext,"please input address between 0~7fffffff!",Toast.LENGTH_SHORT).show();
                    return;
                }
                String value=mFmVal.getText().toString();
                if(null == value || "".equals(value)){
                    Toast.makeText(mContext,"empty value!",Toast.LENGTH_SHORT).show();
                    return;
                } else if(value.length()==8 && Integer.parseInt(value.substring(0,1),16) > 7){
                    Toast.makeText(mContext,"please input value between 0~7fffffff!",Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d(TAG,"address is:"+address+";value is:"+value);
                FmRegCtlParms write_para=new FmRegCtlParms();
                write_para.addr=Integer.parseInt(address,16);
                write_para.err=Byte.parseByte("0");
                write_para.val=Integer.parseInt(value,16);
                write_para.rw_flag=Byte.parseByte("0");
                Log.d(TAG,"write parameter:address="+write_para.addr+";err="+write_para.err+";value="+write_para.val+";flag="+write_para.rw_flag);
                FmNative.writeRegParm(write_para);
                break;
            default:
                break;
        }
    }
}
