package com.sprd.engineermode.telephony;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.widget.TextView;
import java.lang.String;

import android.util.Log;

import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.sprd.engineermode.R;

public class CategaryActivity extends Activity {

    private static final int GET_CATEGARY_VALUE = 0;
    private static final String TAG = "CategaryActivity";

    private ITelephonyApi teleApi = CoreApi.getTelephonyApi();
    private String cat3Value = "Cat: ";
    private CategaryHandler mCategaryHandler;
    private Handler mUiThread = new Handler();
    private TextView txtViewlabel01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categary_result);
        txtViewlabel01 = (TextView) findViewById(R.id.categray_result);
        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mCategaryHandler = new CategaryHandler(ht.getLooper());
        Message categaryValue = mCategaryHandler
                .obtainMessage(GET_CATEGARY_VALUE);
        mCategaryHandler.sendMessage(categaryValue);
    }

    @Override
    protected void onDestroy() {
        if (mCategaryHandler != null) {
            Log.d(TAG, "HandlerThread has quit");
            mCategaryHandler.getLooper().quit();
        }
        super.onDestroy();
    }

    class CategaryHandler extends Handler {
        public CategaryHandler(Looper looper) {
            super(looper);
        }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case GET_CATEGARY_VALUE:
                int cat;
                try {
                    cat = teleApi.radioFunc().getUeCategory();
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }

                final String catText = "Cat: " + cat;
                mUiThread.post(new Runnable() {
                    @Override
                    public void run() {
                        txtViewlabel01.setText(catText);
                    }
                });
                break;
            default:
                break;
            }
        }
    }
}
