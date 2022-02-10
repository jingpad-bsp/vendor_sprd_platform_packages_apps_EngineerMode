
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
import com.android.fmradio.FmNative.FmAudioThresholdParms;
import com.android.fmradio.FmNative.FmSeekCriteriaParms;
import com.sprd.engineermode.R;

public class FMAudioActivity extends Activity implements OnClickListener{
    private static final String TAG = "FMAudioActivity";

    private EditText mAudioHbound,mAudioLbound,mAudioPowerTh,mAudioPhyt,mAudioSnrTh;
    private Button mGetAudioBtn,mSetAudioBtn;
    private Context mContext;

    private static final int AUDIO_HBOUND_EDIT=R.id.audio_hbound_edit;
    private static final int AUDIO_LBOUND_EDIT=R.id.audio_lbound_edit;
    private static final int AUDIO_POWER_TH_EDIT=R.id.audio_power_th_edit;
    private static final int AUDIO_PHYT_EDIT=R.id.audio_phyt_edit;
    private static final int AUDIO_SNR_TH_EDIT=R.id.audio_snr_th_edit;
    private static final int GET_AUDIO_PARA_BTN=R.id.get_audio_btn;
    private static final int SET_AUDIO_PARA_BTN=R.id.set_audio_btn;

    private static final String HINT="0~512";
    private static final String PHYT_HINT="0~31";
    private static final String SNR_HINT="0~50";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fm_audio_mode);
        mContext=this;

        mAudioHbound=(EditText)findViewById(AUDIO_HBOUND_EDIT);
        mAudioHbound.setHint(HINT);
        mAudioLbound=(EditText)findViewById(AUDIO_LBOUND_EDIT);
        mAudioLbound.setHint(HINT);
        mAudioPowerTh=(EditText)findViewById(AUDIO_POWER_TH_EDIT);
        mAudioPowerTh.setHint(HINT);
        mAudioPhyt=(EditText)findViewById(AUDIO_PHYT_EDIT);
        mAudioPhyt.setHint(PHYT_HINT);
        mAudioSnrTh=(EditText)findViewById(AUDIO_SNR_TH_EDIT);
        mAudioSnrTh.setHint(SNR_HINT);
        mGetAudioBtn=(Button)findViewById(GET_AUDIO_PARA_BTN);
        mGetAudioBtn.setOnClickListener(this);
        mSetAudioBtn=(Button)findViewById(SET_AUDIO_PARA_BTN);
        mSetAudioBtn.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case GET_AUDIO_PARA_BTN:
                FmAudioThresholdParms read_para=new FmAudioThresholdParms();
                if(-1 == FmNative.getAudioParm(read_para)){
                    Toast.makeText(mContext,"get audio fail!",Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"get audio fail!");
                    return;
                }else{
                    Log.d(TAG,"read parameter:hbound="+read_para.hbound+";lhound="+read_para.lbound+";power="+read_para.power_th+
                            ";phyt="+read_para.phyt+";snr="+read_para.snr_th);
                    mAudioHbound.setText(String.valueOf(read_para.hbound));
                    mAudioLbound.setText(String.valueOf(read_para.lbound));
                    mAudioPowerTh.setText(String.valueOf(read_para.power_th));
                    mAudioPhyt.setText(String.valueOf((int)read_para.phyt));
                    mAudioSnrTh.setText(String.valueOf((int)read_para.snr_th));
                }
                break;
            case SET_AUDIO_PARA_BTN:
                if(null == mAudioHbound || "".equals(String.valueOf(mAudioHbound.getText()))){
                    Toast.makeText(mContext,"empty softmute hbound threshold!",Toast.LENGTH_SHORT).show();
                    return;
                }
                int hbound=Integer.valueOf(mAudioHbound.getText().toString());
                if(hbound<0 || hbound>512){
                    Toast.makeText(mContext,"please input softmute hbound 0~512 !",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(null == mAudioLbound || "".equals(String.valueOf(mAudioLbound.getText()))){
                    Toast.makeText(mContext,"empty softmute lbound threshold!",Toast.LENGTH_SHORT).show();
                    return;
                }
                int lbound=Integer.valueOf(mAudioLbound.getText().toString());
                if(lbound<0 || lbound>512){
                    Toast.makeText(mContext,"please input softmute lbound 0~512 !",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(null == mAudioPowerTh || "".equals(String.valueOf(mAudioPowerTh.getText()))){
                    Toast.makeText(mContext,"empty power threshold!",Toast.LENGTH_SHORT).show();
                    return;
                }
                int power=Integer.valueOf(mAudioPowerTh.getText().toString());
                if(power<0 || power>512){
                    Toast.makeText(mContext,"please input power 0~512 !",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(null == mAudioPhyt || "".equals(String.valueOf(mAudioPhyt.getText()))){
                    Toast.makeText(mContext,"empty Retardation coefficient threshold!",Toast.LENGTH_SHORT).show();
                    return;
                }
                int phyt=Integer.valueOf(mAudioPhyt.getText().toString());
                if(phyt<0 || phyt>31){
                    Toast.makeText(mContext,"please input Retardation coefficient 0~31 !",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(null == mAudioSnrTh || "".equals(String.valueOf(mAudioSnrTh.getText()))){
                    Toast.makeText(mContext,"empty snr threshold!",Toast.LENGTH_SHORT).show();
                    return;
                }
                int snr=Integer.valueOf(mAudioSnrTh.getText().toString());
                if(snr<0 || snr>50){
                    Toast.makeText(mContext,"please input snr 0~50 !",Toast.LENGTH_SHORT).show();
                    return;
                }

                FmAudioThresholdParms write_para=new FmAudioThresholdParms();
                write_para.hbound=hbound;
                write_para.lbound=lbound;
                write_para.power_th=power;
                write_para.phyt=(byte)phyt;
                write_para.snr_th=(byte)snr;
                Log.d(TAG,"write parameter:hbound="+write_para.hbound+";lhound="+write_para.lbound+";power="+write_para.power_th+
                        ";phyt="+write_para.phyt+";snr="+write_para.snr_th);
                FmNative.setAudioParm(write_para);
                break;
            default:
                break;
        }
    }
}
