package com.sprd.engineermode.connectivity.BT;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.WindowManager;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.util.Log;
import android.widget.Toast;
import android.widget.TableRow;
import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.impl.connectivity.BTHelper;
import com.unisoc.engineermode.core.intf.IConnectivityApi;
import com.unisoc.engineermode.core.common.Const;
import com.unisoc.engineermode.core.intf.IConnectivityApi.IBtEut.TxMode;

import com.sprd.engineermode.R;

public class TxModeCwActivity extends Activity implements OnClickListener, OnItemSelectedListener {

    private static final String TAG = "TxModeCwActivity";

    private Spinner mTxMode;
    private EditText mChannel;
    private Button mStart;
    private Button mStop;

    private TxMode mBtTxMode;
    private TxModeHandler mBtTxModeHandler;
    private Handler mUiThread = new Handler();
    private IConnectivityApi.IBtEut btEut = CoreApi.getConnectivityApi().btEut();

    private static final int MSG_TX_MODE_START = 0;
    private static final int MSG_TX_MODE_STOP = 1;
    private static final int MSG_BT_OFF = 3;
    private static final int CHANNEL_MIN_VALUE = 0;
    private static final int CHANNEL_MAX_VALUE = 78;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.bt_tx_mode);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mBtTxMode = new TxMode();
        mBtTxMode.mode = "1";

        initUI();
        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mBtTxModeHandler = new TxModeHandler(ht.getLooper());
    }

    private void initUI() {
        mChannel = (EditText) findViewById(R.id.tx_mode_channel);
        mChannel.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (mChannel.getText().length() != 0
                    && (Integer.parseInt(mChannel.getText().toString()) < CHANNEL_MIN_VALUE
                    || Integer.parseInt(mChannel.getText().toString()) > CHANNEL_MAX_VALUE)) {
                    Toast.makeText(TxModeCwActivity.this, "number between 0 and 78",
                                Toast.LENGTH_SHORT).show();
                }
            }
        });

        mTxMode = (Spinner) findViewById(R.id.bt_tx_mode_cw);
        ArrayAdapter<String> txModeAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.bt_tx_mode_cw));
        txModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTxMode.setAdapter(txModeAdapter);
        mTxMode.setOnItemSelectedListener(this);

        mStart = (Button) findViewById(R.id.tx_mode_start);
        mStart.setOnClickListener(this);
        mStop = (Button) findViewById(R.id.tx_mode_stop);
        mStop.setOnClickListener(this);
        mStop.setEnabled(false);
    }

    @Override
    public void onBackPressed() {
        Message doMessage = null;
        //if BT RX is not stop, app shoule stop when click back button
        if (mStop.isEnabled()) {
            Log.d(TAG,"Stop is Enabled when click back");
            doMessage = mBtTxModeHandler.obtainMessage(MSG_TX_MODE_STOP);
            mBtTxModeHandler.sendMessage(doMessage);
        }
        //close BT when click back button
        if (BTHelper.isBTOn) {
            Log.d(TAG,"BT is On when click back");
            doMessage = mBtTxModeHandler.obtainMessage(MSG_BT_OFF);
            mBtTxModeHandler.sendMessage(doMessage);
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        Message doMessage = null;
        // get the setting param
        if (!getSettingParam()) {
            return;
        }
        if (v == mStart) {
            doMessage = mBtTxModeHandler.obtainMessage(MSG_TX_MODE_START);
        } else if (v == mStop) {
            doMessage = mBtTxModeHandler.obtainMessage(MSG_TX_MODE_STOP);
        }
        mBtTxModeHandler.sendMessage(doMessage);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
            long id) {
        if (parent == mTxMode) {
            mBtTxMode.mode = this.getResources().getStringArray(R.array.bt_tx_mode_cw_int)[position];
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private boolean getSettingParam() {
        if (mChannel.getText().length() == 0) {
            Toast.makeText(TxModeCwActivity.this, "please input Channel",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            mBtTxMode.channel = mChannel.getText().toString();
        }
        return true;
    }

    class TxModeHandler extends Handler {
        public TxModeHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TX_MODE_START:
                    if (btEut.btTxModeStart(mBtTxMode)) {
                        mUiThread.post(() -> {
                            Toast.makeText(TxModeCwActivity.this, "TX Mode Start Success",
                                        Toast.LENGTH_SHORT).show();
                            mStart.setEnabled(false);
                            mStop.setEnabled(true);
                        });
                    } else {
                        mUiThread.post(() -> {
                            Toast.makeText(TxModeCwActivity.this, "TX Mode Start Fail",
                                        Toast.LENGTH_SHORT).show();
                        });
                    }
                    break;
                case MSG_TX_MODE_STOP:
                    if (btEut.btTxModeStop(mBtTxMode)) {
                        mUiThread.post(() -> {
                            Toast.makeText(TxModeCwActivity.this, "TX Mode Stop Success",
                                        Toast.LENGTH_SHORT).show();
                            mStart.setEnabled(true);
                            mStop.setEnabled(false);
                        });
                    } else {
                        mUiThread.post(() -> {
                            Toast.makeText(TxModeCwActivity.this, "TX Mode Stop Fail",
                                        Toast.LENGTH_SHORT).show();
                        });
                    }
                    break;
                case MSG_BT_OFF:
                    if (btEut.BTOff()) {
                        mUiThread.post(() -> {
                            Toast.makeText(TxModeCwActivity.this, "BT Off Success",
                                Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        mUiThread.post(() -> {
                            Toast.makeText(TxModeCwActivity.this, "BT Off Fail",
                                Toast.LENGTH_SHORT).show();
                        });
                    }
                    finish();
                    break;
                default:
                    break;
            }
        }
    }
}