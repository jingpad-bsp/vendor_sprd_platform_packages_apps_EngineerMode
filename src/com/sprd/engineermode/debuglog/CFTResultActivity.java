
package com.sprd.engineermode.debuglog;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.File;

import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;
import com.unisoc.engineermode.core.impl.telephony.TelephonyManagerSprd;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.intf.ITelephonyApi;

public class CFTResultActivity extends Activity {
    private static final String TAG = "CFTResultActivity";
    //This is only for 9620
    private static final String ADC_PATH = "/productinfo/adc.bin";
    private String str = null;
    private TextView txtViewlabel01;
    private Handler mUiHandler = new Handler();

    private DataInputStream mInputStream=null;
    private static final int ADCBYTES = 56;
    byte[] buffer = new byte[ADCBYTES];

    private ITelephonyApi teleApi = CoreApi.getTelephonyApi();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ctf_result);
        txtViewlabel01 = (TextView) findViewById(R.id.ctf_result);

        new Thread(new Runnable() {
            @Override
            public void run() {
                /* SPRD Bug 825230:Some CFT calibration information disappear. @{ */
                if (teleApi.telephonyInfo().isSupportGsm() || teleApi.telephonyInfo().isSupportTdscdma()) {
                /* @} */
                    str = "GSM/TD ";
                }
                //modify 319936 by sprd
                //read /proc/cmdline, if contains "adc_cal",ADC Calibration pass
                //this is only for 9620
                try {
                    if (SystemPropertiesProxy.get("ro.product.board").contains("9620")) {
                        Log.d(TAG,"This product is 9620");
                        String str1 = teleApi.cftResult().getGsmTdScdmaCFTResult();
                        boolean isAdc = readFile();
                        String[] str3 = str1.split("\n");
                        for (int i = 0;i < str3.length;i++) {
                            if (str3[i].contains("ADC")) {
                                Log.d(TAG,"9620 has calibration bit");
                                String[] str4 = str3[i].split("\\:");
                                if (isAdc) {
                                    str3[i] = str4[0]+":ADC calibrated Pass";
                                }
                            }
                            str += str3[i]+"\n";
                        }
                    } else {
                        Log.d(TAG,"This product is not 9620");
                        str += teleApi.cftResult().getGsmTdScdmaCFTResult();
                    }
                    /* SPRD Bug 825230:Some CFT calibration information disappear. @{ */
                    if (teleApi.telephonyInfo().isSupportWcdma()) {
                        str += "\nWCDMA ";
                        str += teleApi.cftResult().getWcdmaCFTResult();
                    }
                    /* SPRD 958254: C2K feature @{ */
                    if (teleApi.telephonyInfo().isSupportC2k()) {
                            str += "\nC2K ";
                            str += teleApi.cftResult().getC2kCFTResult();
                    }
                     /* @} */
                    if (teleApi.telephonyInfo().isSupportLte()) {
                            str += "\nLTE ";
                            str += teleApi.cftResult().getLteCFTResult();
                    }
                    /* SPRD 1165494: Nr feature @{ */
                    if (teleApi.telephonyInfo().isSupportNr()) {
                            str += "\nNR ";
                            str += teleApi.cftResult().getNrCFTResult();
                    }
                    mUiHandler.post(new Runnable() {
                        public void run() {
                            txtViewlabel01.setText(str);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public boolean readFile() {
        try {
            File adcFile = new File(ADC_PATH);
            int count = 0;
            if (!adcFile.exists()) {
                Log.d(TAG, "adcFile do not exists");
                return false;
            }
            mInputStream = new DataInputStream(new FileInputStream(adcFile));
            count = mInputStream.read(buffer, 0, ADCBYTES);
            if (buffer == null || buffer.length <= 0) {
                Log.d(TAG, "buffer == null or buffer.length <= 0");
                return false;
            }
            Log.d(TAG, "count = " + count + " size = " + buffer.length);
            int adcBit = buffer.length - 4;
            int adcResult = buffer[adcBit] | 0xFFFFFFFC;
            Log.d(TAG, "adcBit = " + adcBit + " buffer[" + adcBit + "] = 0x"
                    + Integer.toHexString(buffer[adcBit]) + " adcResult = 0x"
                    + Integer.toHexString(adcResult));
            if (adcResult == 0xFFFFFFFF) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed get outputStream: " + e);
            e.printStackTrace();
        }
        return false;
    }
}
