package com.sprd.engineermode.debuglog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.util.Log;
import android.text.TextUtils;

import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.intf.IDebugLogApi;

public class MIPILogSerdesActivity extends Activity implements OnClickListener, OnItemSelectedListener {
    private static final String TAG = "MIPILogSerdesActivity";
    private static final String KEY_MIPI_SERDES = "mipi_log_serdes";
    private static final String SET_MIPI_LOG_SOCKET_NAME = "SET_MIPI_LOG_INFO";
    private static final String GET_MIPI_LOG_SOCKET_NAME = "GET_MIPI_LOG_INFO";
    private static final String SPLIT = " ";
    private static final String ENDFLAG = "\n";

    private String mCurrentSerdesId = "";
    private String mChannel = "";
    private String mFreq = "";
    private Spinner mSpinnerChannel = null;
    private Spinner mSpinnerFreq = null;
    private Button mButton = null;
    private TextView mResult = null;
    private String[] mChannelArray = null;
    private String[] mFreqArray = null;
    private ArrayAdapter<String> mChannelAdapter = null;
    private IDebugLogApi debuglogApi = CoreApi.getDebugLogApi();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_mipi_log_serdes);

        Intent intent = this.getIntent();
        String serdesId = intent.getStringExtra(KEY_MIPI_SERDES);
        TextView serdesLabel = findViewById(R.id.serdes_label);
        String index = serdesId != null ? serdesId.substring(serdesId.length() - 1) : "0";
        mCurrentSerdesId = "serdes" + index;
        serdesLabel.setText(mCurrentSerdesId);

        mSpinnerChannel = findViewById(R.id.spinner_channel);
        getChannelArray(index);
        mChannelAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, mChannelArray);
        mChannelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerChannel.setAdapter(mChannelAdapter);
        mSpinnerChannel.setOnItemSelectedListener(this);

        mSpinnerFreq = findViewById(R.id.spinner_freq);
        mSpinnerFreq.setOnItemSelectedListener(this);
        mFreqArray = getResources().getStringArray(R.array.mipi_log_serdes_freq);

        mButton = (Button) findViewById(R.id.btn_ok);
        mButton.setOnClickListener(this);

        mResult = findViewById(R.id.reslut);
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshUI();
    }

    private void refreshUI() {
        String currentResult = debuglogApi.mipiLogApi().getMIPILogSerdes(GET_MIPI_LOG_SOCKET_NAME + SPLIT + mCurrentSerdesId + ENDFLAG);
        if (currentResult != null && currentResult.contains(ENDFLAG)) {
            currentResult = currentResult.substring(0, currentResult.indexOf(ENDFLAG));
        }
        Log.d(TAG, "getMIPILogInfo Result = " + currentResult);

        String[] mipiInfo = TextUtils.isEmpty(currentResult) ? null : currentResult.split(SPLIT); //type channel freq
        if(mipiInfo != null && mipiInfo.length == 3) {
            if (mCurrentSerdesId.equals(mipiInfo[0])) {
                mSpinnerChannel.setSelection(getIndex(mChannelArray, mipiInfo[1]));
                mSpinnerFreq.setSelection(getIndex(mFreqArray, mipiInfo[2]));
                return;
            }
        }
        mSpinnerChannel.setSelection(0);
        mSpinnerFreq.setSelection(0);
    }

    private int getIndex(String[] arr, String value) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(value)) {
                return i;
            }
        }
        return 0;
    }


    private void getChannelArray(String index) {
        if (index.equals("0")) {
            mChannelArray = getResources().getStringArray(R.array.mipi_log_serdes0_entries);
        } else if(index.equals("1")) {
            mChannelArray = getResources().getStringArray(R.array.mipi_log_serdes1_entries);
        } else {
            mChannelArray = getResources().getStringArray(R.array.mipi_log_serdes2_entries);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == mSpinnerChannel) {
            mChannel = mChannelArray[position];
        } else if (parent == mSpinnerFreq) {
            mFreq = mFreqArray[position];
        }
    }

    @Override
    public void onClick(View v) {
        try {
            String cmd = SET_MIPI_LOG_SOCKET_NAME + SPLIT + mCurrentSerdesId + SPLIT + mChannel + SPLIT + mFreq + ENDFLAG;
            Log.d(TAG, "setMIPILogInfo cmd = " + cmd);
            debuglogApi.mipiLogApi().setMIPILogSerdes(cmd);
            mResult.setText("Success!");
        } catch(Exception e) {
            mResult.setText("Fail!");
            e.printStackTrace();
        }
    }
}