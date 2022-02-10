
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
import com.android.fmradio.FmNative.FmSeekCriteriaParms;
import com.sprd.engineermode.R;

public class FmTuneActivity extends Activity implements OnClickListener{
    private static final String TAG = "FmTuneActivity";

    private EditText mTuneRssiTh,mTuneSnrTh,mTuneFreqOffsetTh,mTunePilotPowerTh,mTuneNoisePowerTh;
    private Button mGetTuneBtn,mSetTuneBtn;
    private Context mContext;

    private static final int TUNE_RSSI_TH_EDIT=R.id.tune_rssi_th_edit;
    private static final int TUNE_SNR_TH_EDIT=R.id.tune_snr_th_edit;
    private static final int TUNE_FREQ_OFFSET_TH_EDIT=R.id.tune_freq_offset_th_edit;
    private static final int TUNE_PILOT_POWER_TH_EDIT=R.id.tune_pilot_power_th_edit;
    private static final int TUNE_PILOT_NOISE_TH_EDIT=R.id.tune_noise_power_th_edit;
    private static final int GET_TUNE_PARA_BTN=R.id.get_tune_btn;
    private static final int SET_TUNE_PARA_BTN=R.id.set_tune_btn;

    private static final String RSSIT_HINT="0~150";
    private static final String SNR_HINT="0~50";
    private static final String FREQ_OFFSET_HINT="0000~FFFF";
    private static final String POWER_HINT="0000~1FFF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fm_tune_mode);
        mContext=this;

        mTuneRssiTh=(EditText)findViewById(TUNE_RSSI_TH_EDIT);
        mTuneRssiTh.setHint(RSSIT_HINT);
        mTuneSnrTh=(EditText)findViewById(TUNE_SNR_TH_EDIT);
        mTuneSnrTh.setHint(SNR_HINT);
        mTuneFreqOffsetTh=(EditText)findViewById(TUNE_FREQ_OFFSET_TH_EDIT);
        mTuneFreqOffsetTh.setHint(FREQ_OFFSET_HINT);
        mTunePilotPowerTh=(EditText)findViewById(TUNE_PILOT_POWER_TH_EDIT);
        mTunePilotPowerTh.setHint(POWER_HINT);
        mTuneNoisePowerTh=(EditText)findViewById(R.id.tune_noise_power_th_edit);
        mTuneNoisePowerTh.setHint(POWER_HINT);
        mGetTuneBtn=(Button)findViewById(R.id.get_tune_btn);
        mGetTuneBtn.setOnClickListener(this);
        mSetTuneBtn=(Button)findViewById(R.id.set_tune_btn);
        mSetTuneBtn.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case GET_TUNE_PARA_BTN:
                FmSeekCriteriaParms read_para=new FmSeekCriteriaParms();
                if(-1 == FmNative.getTuneParm(read_para)){
                    Toast.makeText(mContext,"get tune fail!",Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"get tune fail!");
                    return;
                }else{
                    Log.d(TAG,"read parameter:rssi="+read_para.rssi_th+";snr="+read_para.snr_th+";freq_offset="+read_para.freq_offset_th+
                            ";pilot_power="+read_para.pilot_power_th+";noise_power="+read_para.noise_power_th);
                    mTuneRssiTh.setText(String.valueOf(read_para.rssi_th));
                    mTuneSnrTh.setText(String.valueOf((int)read_para.snr_th));
                    mTuneFreqOffsetTh.setText(Integer.toHexString(read_para.freq_offset_th));
                    mTunePilotPowerTh.setText(Integer.toHexString(read_para.pilot_power_th));
                    mTuneNoisePowerTh.setText(Integer.toHexString(read_para.noise_power_th));
                }
                break;
            case SET_TUNE_PARA_BTN:
                if(null == mTuneRssiTh || "".equals(mTuneRssiTh.getText().toString())){
                    Toast.makeText(mContext,"empty rssi threshold!",Toast.LENGTH_SHORT).show();
                    return;
                }
                int rssi=Integer.valueOf(mTuneRssiTh.getText().toString());
                if(rssi<0 || rssi>150){
                    Toast.makeText(mContext,"please input rssi 0~150 !",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(null == mTuneSnrTh || "".equals(mTuneSnrTh.getText().toString())){
                    Toast.makeText(mContext,"empty snr threshold!",Toast.LENGTH_SHORT).show();
                    return;
                }
                int snr=Integer.valueOf(mTuneSnrTh.getText().toString());
                if(snr<0 || snr>50){
                    Toast.makeText(mContext,"please input snr 0~50 !",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(null == mTuneFreqOffsetTh || "".equals(mTuneFreqOffsetTh.getText().toString())){
                    Toast.makeText(mContext,"empty freq offset threshold!",Toast.LENGTH_SHORT).show();
                    return;
                }
                int freqOffset=Integer.parseInt(mTuneFreqOffsetTh.getText().toString(),16);
                if(freqOffset<0 || freqOffset>Integer.parseInt("FFFF",16)){
                    Toast.makeText(mContext,"please input freq  offset 0000~FFFF !",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(null == mTunePilotPowerTh || "".equals(mTunePilotPowerTh.getText().toString())){
                    Toast.makeText(mContext,"empty pilot power threshold!",Toast.LENGTH_SHORT).show();
                    return;
                }
                int pilotPower=Integer.parseInt(mTunePilotPowerTh.getText().toString(),16);
                if(pilotPower<0 || pilotPower>Integer.parseInt("1FFF",16)){
                    Toast.makeText(mContext,"please input pilot power 0000~1FFF !",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(null == mTuneNoisePowerTh || "".equals(mTuneNoisePowerTh.getText().toString())){
                    Toast.makeText(mContext,"empty noise power threshold!",Toast.LENGTH_SHORT).show();
                    return;
                }
                int noisePower=Integer.parseInt(mTuneNoisePowerTh.getText().toString(),16);
                if(noisePower<0 || noisePower>Integer.parseInt("1FFF",16)){
                    Toast.makeText(mContext,"please input noise power 0000~1FFF !",Toast.LENGTH_SHORT).show();
                    return;
                }

                FmSeekCriteriaParms write_para=new FmSeekCriteriaParms();
                write_para.rssi_th=rssi;
                write_para.snr_th=(byte)snr;
                write_para.freq_offset_th=freqOffset;
                write_para.pilot_power_th=pilotPower;
                write_para.noise_power_th=noisePower;
                Log.d(TAG,"write parameter:rssi="+write_para.rssi_th+";snr="+write_para.snr_th+";freq_offset="+write_para.freq_offset_th+
                        ";pilot_power="+write_para.pilot_power_th+";noise_power="+write_para.noise_power_th);
                FmNative.setTuneParm(write_para);
                break;
            default:
                break;
        }
    }
}
