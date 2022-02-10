package com.sprd.engineermode.connectivity.BT;
//bug567135 add by suyan.yang 2016-05-27
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
import com.unisoc.engineermode.core.intf.IConnectivityApi.IBtEut.BTTX;

public class BTBLETXActivity extends Activity implements OnClickListener, OnItemSelectedListener {

    private static final String TAG = "BTBLETXActivity";

    // handle message
    private static final int MSG_BT_BLE_TX_START = 0;
    private static final int MSG_BT_BLE_TX_STOP = 1;
    private static final int MSG_BT_BLE_OFF = 2;

    private TableRow mLePhyLayout;
    private TableRow mPatternLayout;
    private TableRow mDataLenLayout;
    private TableRow mPacCntLayout;
    private Spinner mPattern;
    private Spinner mLePhy;
    private EditText mChannel;
    private EditText mDataLen;
    private EditText mPowerValue;
    private EditText mPacCnt;
    private Spinner mTxMode;
    private Button mStart;
    private Button mStop;

    private static final String MODE_CW = "1";

    private BTTX mBTBLETx;
    private BTTXHandler mBTBLETxHandler;
    private Handler mUiThread = new Handler();
    private IConnectivityApi.IBtEut btEut = CoreApi.getConnectivityApi().btEut();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bt_ble_tx);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mBTBLETx = new BTTX();
        mBTBLETx.powervalue = "1";
        initUI();
        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mBTBLETxHandler = new BTTXHandler(ht.getLooper());
    }

    @Override
    public void onBackPressed() {
        Message doMessage = null;
        // if BT TX is not stop, app shoule stop when click back button
        if (mStop.isEnabled()) {
            Log.d(TAG, "Stop is Enabled when click back");
            doMessage = mBTBLETxHandler.obtainMessage(MSG_BT_BLE_TX_STOP);
            mBTBLETxHandler.sendMessage(doMessage);
        }

        // close BT when click back button
        if (BTHelper.isBTOn) {
            Log.d(TAG, "BT is On when click back");
            doMessage = mBTBLETxHandler.obtainMessage(MSG_BT_BLE_OFF);
            mBTBLETxHandler.sendMessage(doMessage);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        Message doMessage = null;
        // get the setting param
        if (!getSettingParam()) {
            return;
        }
        if (v == mStart) {
            doMessage = mBTBLETxHandler.obtainMessage(MSG_BT_BLE_TX_START);
        } else if (v == mStop) {
            doMessage = mBTBLETxHandler.obtainMessage(MSG_BT_BLE_TX_STOP);
        }
        mBTBLETxHandler.sendMessage(doMessage);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
            long id) {
        if (mBTBLETx != null) {
            if (parent == mPattern) {
                mBTBLETx.pattern = this.getResources().getStringArray(R.array.bt_ble_tx_pattern_int)[position];
            } else if (parent == mLePhy) {
                if (Const.isMarlin3() || Const.isMarlin3Lite() || Const.isMarlin3E()) {
                    mBTBLETx.lephy= this.getResources().getStringArray(R.array.bt_ble_tx_le_phy_int)[position];
                }
            } else if (parent == mTxMode) {
                mBTBLETx.txMode = this.getResources().getStringArray(R.array.bt_ble_tx_mode_int)[position];
                setTxModeUI();
            }
        }
    }

    private void setTxModeUI() {
        if (mBTBLETx.txMode == null || mBTBLETx.txMode.equals(MODE_CW)) {
            mPatternLayout.setVisibility(View.GONE);
            mDataLenLayout.setVisibility(View.GONE);
            mLePhyLayout.setVisibility(View.GONE);
            mPacCntLayout.setVisibility(View.GONE);
        } else {
            mPatternLayout.setVisibility(View.VISIBLE);
            mDataLenLayout.setVisibility(View.VISIBLE);
            if (!Const.isMarlin2()) {
                mLePhyLayout.setVisibility(View.VISIBLE);
            }
            mPacCntLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void initUI() {
        mPatternLayout = (TableRow) findViewById(R.id.tablerow_pattern);
        mDataLenLayout = (TableRow) findViewById(R.id.tablerow_data_len);
        mPacCntLayout = (TableRow) findViewById(R.id.tablerow_pac_cnt);

        mPattern = (Spinner) findViewById(R.id.bt_ble_tx_pattern);
        ArrayAdapter<String> patternAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.bt_ble_tx_pattern));
        patternAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPattern.setAdapter(patternAdapter);
        mPattern.setOnItemSelectedListener(this);

        mLePhy = (Spinner) findViewById(R.id.bt_ble_tx_le_phy);
        ArrayAdapter<String> mLePhyAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.bt_ble_tx_le_phy));
        mLePhyAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLePhy.setAdapter(mLePhyAdapter);
        mLePhy.setOnItemSelectedListener(this);

        mLePhyLayout = (TableRow) findViewById(R.id.bt_ble_tx_phy_tablerow);
        if (Const.isMarlin2()) {
            mLePhyLayout.setVisibility(View.GONE);
        }

        setTxModeUI();
        mChannel = (EditText) findViewById(R.id.bt_ble_tx_channel);
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
                if (mChannel.getText().length() != 0) {
                    if (Integer.parseInt(mChannel.getText().toString()) >= 0
                            && Integer.parseInt(mChannel.getText().toString()) <= 39) {
                        return;
                    } else {
                        Toast.makeText(BTBLETXActivity.this, "number between 0 and 39",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });

        mDataLen = (EditText) findViewById(R.id.bt_ble_tx_data_length);
        mDataLen.addTextChangedListener(new TextWatcher() {
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
                if (mDataLen.getText().length() != 0) {
                    if (Integer.parseInt(mDataLen.getText().toString()) >= 0
                            && Integer.parseInt(mDataLen.getText().toString()) <= 192) {
                        return;
                    } else {
                        Toast.makeText(BTBLETXActivity.this, "number between 0 and 192",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });

        mPacCnt = (EditText) findViewById(R.id.bt_ble_tx_pac_cnt);
        mPacCnt.addTextChangedListener(new TextWatcher() {
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
                if (mPacCnt.getText().length() != 0) {
                    if (Integer.parseInt(mPacCnt.getText().toString()) < 0
                            || Integer.parseInt(mPacCnt.getText().toString()) > 65535) {
                        Toast.makeText(BTBLETXActivity.this, "number between 0 and 65535",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                /*
                 * if (mWifiTX != null) { mWifiTX.pktlength =
                 * mChannel.getText().toString(); }
                 */
            }
        });

        mTxMode = (Spinner) findViewById(R.id.bt_ble_tx_mode);
        ArrayAdapter<String> txModeAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.bt_ble_tx_mode));
        txModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTxMode.setAdapter(txModeAdapter);
        mTxMode.setOnItemSelectedListener(this);

        mStart = (Button) findViewById(R.id.bt_ble_tx_start);
        mStart.setOnClickListener(this);
        mStop = (Button) findViewById(R.id.bt_ble_tx_stop);
        mStop.setOnClickListener(this);
        mStop.setEnabled(false);
        if (Const.isMarlin2()) {
            mLePhy.setEnabled(false);
        }
    }

    private boolean getSettingParam() {

        if (mChannel.getText().length() == 0) {
            Toast.makeText(BTBLETXActivity.this, "please input BLE TX Channel",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            mBTBLETx.channel = mChannel.getText().toString();
        }
        if (mBTBLETx.txMode != null && !mBTBLETx.txMode.equals(MODE_CW)) {
            if (mDataLen.getText().length() == 0) {
                Toast.makeText(BTBLETXActivity.this, "please input BLE TX Data Length",
                    Toast.LENGTH_SHORT).show();
                return false;
            } else {
                mBTBLETx.datalength = mDataLen.getText().toString();
            }
            if (mPacCnt.getText().length() == 0) {
                Toast.makeText(BTBLETXActivity.this, "please input BLE TX Pac Cnt",
                    Toast.LENGTH_SHORT).show();
                return false;
            } else {
                mBTBLETx.paccnt = mPacCnt.getText().toString();
            }
        }
        Log.d(TAG, "Now BT BLE TX Start" + "\n"
                + "BLE TX Pattern is " + mBTBLETx.pattern + "\n"
                + "BLE TX Channel is " + mBTBLETx.channel + "\n"
                + "BLE TX Pac Type is " + mBTBLETx.pactype + "\n"
                + "BLE TX Pac Len is " + mBTBLETx.paclen + "\n"
                + "BLE TX Pac Cnt is " + mBTBLETx.paccnt +"\n"
                + "BLE TX DataLen is " + mBTBLETx.datalength+"\n"
                + "BLE TX DataLen is " + mBTBLETx.lephy+"\n"
                );
        return true;
    }

    class BTTXHandler extends Handler {
        public BTTXHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_BT_BLE_TX_START:
                    if (btEut.BTBLETxStart(mBTBLETx)) {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BTBLETXActivity.this, "BLE TX Start Success",
                                        Toast.LENGTH_SHORT).show();
                                mStart.setEnabled(false);
                                mStop.setEnabled(true);
                            }
                        });
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BTBLETXActivity.this, "BLE TX Start Fail",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
                case MSG_BT_BLE_TX_STOP:
                    if (btEut.BTBLETxStop(mBTBLETx)) {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BTBLETXActivity.this, "BLE TX Stop Success",
                                        Toast.LENGTH_SHORT).show();
                                mStart.setEnabled(true);
                                mStop.setEnabled(false);
                            }
                        });
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BTBLETXActivity.this, "BLE TX Stop Fail",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
                case MSG_BT_BLE_OFF:
                    if (btEut.BTOff()) {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BTBLETXActivity.this, "BT Off Success",
                                    Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BTBLETXActivity.this, "BT Off Fail",
                                    Toast.LENGTH_SHORT).show();
                            }
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
