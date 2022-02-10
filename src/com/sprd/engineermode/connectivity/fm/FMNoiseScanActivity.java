package com.sprd.engineermode.connectivity.fm;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ListView;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.util.ArrayList;
import java.util.Date;
import android.content.Context;
import android.view.View;
import android.widget.Toast;
import android.view.View.OnClickListener;
import com.android.fmradio.FmNative;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.utils.StorageUtils;

public class FMNoiseScanActivity extends Activity implements OnClickListener{

    private static final String TAG = "FMNoiseScanActivity";
    private Context mContext;
    private static final int LOOP=1;
    private static int LOOP_TIME=1000;
    private EditText mStartFreqEdit,mEndFreqEdit,mDelayEdit;
    private Button mStart,mStop;
    private ListView mResultList;
    private Handler UiHandler=new Handler();
    String mCurFreq,mEndFreq;
    private FMNoiseScanAdapter mResultAdapter;
    private ArrayList<NoiseScanResult> mResult;
    static DecimalFormat decimalFormat;
    private static String FILE_NAME="FMNoiseTest_";
    String LogPath="";
    private Process process = null;
    private DataInputStream is = null;
    private DataOutputStream os = null;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what==LOOP){
                final int rssi=FmNative.getRssi();
                final int snr=FmNative.getSnr();
                Log.d(TAG,"fm rssi is:"+rssi);
                Log.d(TAG,"fm snr is:"+snr);
                SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd-hh-mm-ss");
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String cmd="echo "+"\""+formatter.format(curDate)+"   "+mCurFreq+"   "+String.valueOf(-rssi)+"   "+String.valueOf(snr)+"\" >> "+LogPath + "\n";
                Log.d(TAG,"cmd is:"+cmd);
                executeCmd(cmd);

                NoiseScanResult mSingleRes=new NoiseScanResult(mCurFreq,String.valueOf(-rssi),String.valueOf(snr));
                mResultAdapter.result.add(mSingleRes);
                mResultAdapter.notifyDataSetChanged();
                mResultList.setAdapter(mResultAdapter);

                mCurFreq=decimalFormat.format(Float.valueOf(mCurFreq)+0.1);
                Log.d(TAG,"current freq is:"+mCurFreq);
                if(Float.valueOf(mEndFreq) >= Float.valueOf(mCurFreq)){
                    EarphoneFragment.setFMPlayerRoute(EarphoneFragment.FM_MODE, mCurFreq);
                    Message m = obtainMessage(LOOP);
                    sendMessageDelayed(m, LOOP_TIME);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fm_noise_scan);
        mContext=this;
        mStartFreqEdit=(EditText)findViewById(R.id.start_freq_edit);
        mEndFreqEdit=(EditText)findViewById(R.id.end_freq_edit);
        mDelayEdit=(EditText)findViewById(R.id.fm_delay_edit);
        mStart=(Button)findViewById(R.id.fm_noise_start);
        mStart.setOnClickListener(this);
        mStop=(Button)findViewById(R.id.fm_noise_stop);
        mStop.setOnClickListener(this);
        mStart.setEnabled(true);
        mStop.setEnabled(false);
        mResultList=(ListView)findViewById(R.id.noise_result_text);

        decimalFormat=new DecimalFormat(".0");
        mResult=new ArrayList<NoiseScanResult>();
        mResultAdapter=new FMNoiseScanAdapter(this,mResult);
        mResultList.setAdapter(mResultAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.fm_noise_start:
                String delay=mDelayEdit.getText().toString().trim();
                if(delay == null || "".equals(delay)){
                    Toast.makeText(mContext,"empty delay time!",Toast.LENGTH_SHORT).show();
                    return;
                }
                LOOP_TIME=Integer.valueOf(delay);
                mCurFreq=mStartFreqEdit.getText().toString().trim();
                mEndFreq=mEndFreqEdit.getText().toString().trim();

                if(mCurFreq == null || "".equals(mCurFreq)){
                    Toast.makeText(mContext,"empty start freqency!",Toast.LENGTH_SHORT).show();
                    return;
                }
                Pattern pattern = Pattern.compile("[0-9]{1,3}.{0,1}[0-9]{0,1}");
                Matcher matcher = pattern.matcher(mCurFreq);
                Log.d(TAG,"the match start freqency result is : "+matcher.matches());
                if(!matcher.matches()){
                    Toast.makeText(mContext,"input start freqency error!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mEndFreq == null || "".equals(mEndFreq)){
                    Toast.makeText(mContext,"empty end freqency!",Toast.LENGTH_SHORT).show();
                    return;
                }
                pattern = Pattern.compile("[0-9]{1,3}.{0,1}[0-9]{0,1}");
                matcher = pattern.matcher(mEndFreq);
                Log.d(TAG,"the match end freqency result is : "+matcher.matches());
                if(!matcher.matches()){
                    Toast.makeText(mContext,"input end freqency error!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(Float.valueOf(mEndFreq) < Float.valueOf(mCurFreq)){
                    Toast.makeText(mContext,"end freqency should be greater than start freqency!",Toast.LENGTH_SHORT).show();
                    return;
                }
                EarphoneFragment.setFMPlayerRoute(EarphoneFragment.FM_MODE, mCurFreq);
                getLogPath();
                Log.d(TAG,"the log path is:"+LogPath);

                initProcess();
                String cmd="echo "+"\""+"        TIME          FREQ  RSSI  SNR"+"\" >> "+LogPath + "\n";
                executeCmd(cmd);
                mResultAdapter.result.clear();
                Message m = handler.obtainMessage(LOOP);
                handler.sendMessage(m);
                mStart.setEnabled(false);
                mStop.setEnabled(true);
                break;
            case R.id.fm_noise_stop:
                handler.removeMessages(LOOP);
                mStart.setEnabled(true);
                mStop.setEnabled(false);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy...");
        handler.removeMessages(LOOP);
        EarphoneFragment.setFMPlayerRoute(EarphoneFragment.FM_MODE, EarphoneFragment.mChannelValue);
    }

    private void getLogPath(){
        SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMddhhmmss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String fileName = FILE_NAME+formatter.format(curDate);
        String dir_path="";
        if(checkSDCard()){
            dir_path = StorageUtils.getExternalStorage()+"/"+"FMNoiseTest";
        } else {
            dir_path = StorageUtils.getInternalStorage().toString()+"/"+"FMNoiseTest";
        }
        try {
            File destDir = new File(dir_path);
            if (!destDir.exists()) {
                destDir.mkdirs();
                String command = "chmod 777 " + dir_path;
                Log.d(TAG, command);
                Log.d(TAG, dir_path + " create success");
            }
        } catch (Exception e) {
            Log.d(TAG, dir_path+" create fail");
            Log.d(TAG,e.getMessage());
        }
        LogPath=dir_path+"/"+fileName;
    }

    protected boolean checkSDCard() {
        if (StorageUtils.getExternalStorageState()) {
            return true;
        }else {
            return false;
        }
    }

    private void initProcess() {
        DataInputStream stream;
        try {
            stream = suCmd("echo start");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private DataInputStream suCmd(String cmd) {
        try {
            process = Runtime.getRuntime().exec("/system/bin/sh");
            os = new DataOutputStream(process.getOutputStream());
            is = new DataInputStream(process.getInputStream());
            os.writeBytes(cmd + "\n");
            os.flush();
            return is;
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error -:" + e.getMessage());
            return is;
        }
    }

    private void executeCmd(String cmd){
        Log.d(TAG,"cmd is:"+cmd);
        try {
            os.writeBytes(cmd);
            os.flush();
        } catch (IOException e) {
            Log.d(TAG, e.toString());
            e.printStackTrace();
        }
    }
}
