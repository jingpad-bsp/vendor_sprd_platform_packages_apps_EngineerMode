
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

import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.impl.connectivity.BTHelper;
import com.unisoc.engineermode.core.intf.IConnectivityApi;
import com.unisoc.engineermode.core.common.Const;
import android.widget.TableRow;

import java.util.Locale;

public class BTTXActivity extends Activity implements OnClickListener, OnItemSelectedListener {

    private static final String TAG = "BTTXActivity";

    // handle message
    private static final int MSG_BT_TX_START = 0;
    private static final int MSG_BT_TX_STOP = 1;
    private static final int MSG_BT_OFF = 2;

    private static final int CHANNEL_MIN=0;
    private static final int CHANNEL_MAX=78;
    private static final int CHANNEL_VALUE=255;
    private static final int POWER_MIN=0;
    private static final int POWER_MAX_MARLIN2 =4;
    private static final int POWER_MAX_MARLIN3 =9;
    private static final int PAC_CNT_MIN=0;
    private static final int PAC_CNT_MAX=65535;
    private static final int VALUE_NULL=0;

    private Spinner mPattern;
    private EditText mChannel;
    private Spinner mPacType;
    private EditText mPacLen;
    private TextView mPacLenSummary;
    private EditText mPowerValue;
    private EditText mPacCnt;
    private Spinner mTxMode;
    private Button mStart;
    private Button mStop;
    private String mMaxPacLen = "0";

    private TableRow mPatternLayout;
    private TableRow mPacTypeLayout;
    private TableRow mPacLenLayout;
    private TableRow mPowerValueLayout;
    private TableRow mPacCntLayout;

    private static final String MODE_CW = "1";

    private BTHelper.BTTX mBTTx;
    private BTTXHandler mBTTXHandler;
    private Handler mUiThread = new Handler();
    private IConnectivityApi.IBtEut btEut = CoreApi.getConnectivityApi().btEut();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bt_tx);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mBTTx = new BTHelper.BTTX();
        initUI();
        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mBTTXHandler = new BTTXHandler(ht.getLooper());
    }

    @Override
    public void onBackPressed() {
        Message doMessage = null;
        // if BT TX is not stop, app shoule stop when click back button
        if (mStop.isEnabled()) {
            Log.d(TAG, "Stop is Enabled when click back");
            doMessage = mBTTXHandler.obtainMessage(MSG_BT_TX_STOP);
            mBTTXHandler.sendMessage(doMessage);
        }

        // close BT when click back button
        if (BTHelper.isBTOn) {
            Log.d(TAG, "BT is On when click back");
            doMessage = mBTTXHandler.obtainMessage(MSG_BT_OFF);
            mBTTXHandler.sendMessage(doMessage);
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
            doMessage = mBTTXHandler.obtainMessage(MSG_BT_TX_START);
        } else if (v == mStop) {
            doMessage = mBTTXHandler.obtainMessage(MSG_BT_TX_STOP);
        }
        mBTTXHandler.sendMessage(doMessage);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
            long id) {
        if (mBTTx != null) {
            if (parent == mPacType) {
                mBTTx.pactype = this.getResources().getStringArray(R.array.bt_pac_type_int)[position];
                mMaxPacLen = this.getResources().getStringArray(R.array.bt_pac_max_len)[position];
                mPacLenSummary.setText("MaxLen is " + mMaxPacLen);
            } else if (parent == mPattern) {
                mBTTx.pattern = this.getResources().getStringArray(R.array.bt_tx_pattern_int)[position];
            } else if (parent == mTxMode) {
                mBTTx.txMode = this.getResources().getStringArray(R.array.bt_tx_mode_int)[position];
                setTxModeUI();
            }
        }
    }

    private void setTxModeUI() {
        if (mBTTx.txMode == null || mBTTx.txMode.equals(MODE_CW)) {
            mPatternLayout.setVisibility(View.GONE);
            mPacTypeLayout.setVisibility(View.GONE);
            mPacLenLayout.setVisibility(View.GONE);
            mPowerValueLayout.setVisibility(View.GONE);
            mPacCntLayout.setVisibility(View.GONE);
        } else {
            mPatternLayout.setVisibility(View.VISIBLE);
            mPacTypeLayout.setVisibility(View.VISIBLE);
            mPacLenLayout.setVisibility(View.VISIBLE);
            mPowerValueLayout.setVisibility(View.VISIBLE);
            mPacCntLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void initUI() {
        mPatternLayout = (TableRow) findViewById(R.id.tablerow_pattern);
        mPacTypeLayout = (TableRow) findViewById(R.id.tablerow_pac_type);
        mPacLenLayout = (TableRow) findViewById(R.id.tablerow_pac_len);
        mPowerValueLayout = (TableRow) findViewById(R.id.tablerow_power_value);
        mPacCntLayout = (TableRow) findViewById(R.id.tablerow_pac_cnt);
        setTxModeUI();
        mPattern = (Spinner) findViewById(R.id.bt_tx_pattern);
        ArrayAdapter<String> patternAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.bt_tx_pattern));
        patternAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPattern.setAdapter(patternAdapter);
        mPattern.setOnItemSelectedListener(this);

        mChannel = (EditText) findViewById(R.id.bt_tx_channel);
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
                if (mChannel.getText().length() != VALUE_NULL) {
                    if (Integer.parseInt(mChannel.getText().toString()) >= CHANNEL_MIN
                            && Integer.parseInt(mChannel.getText().toString()) <= CHANNEL_MAX
                            || Integer.parseInt(mChannel.getText().toString()) == CHANNEL_VALUE) {
                        return;
                    } else {
                        Toast.makeText(BTTXActivity.this, "number between 0 and 78 or 255",
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

        mPacType = (Spinner) findViewById(R.id.bt_tx_pac_type);
        ArrayAdapter<String> pactype = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.bt_pac_type));
        pactype
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPacType.setAdapter(pactype);
        mPacType.setOnItemSelectedListener(this);

        mPacLen = (EditText) findViewById(R.id.bt_tx_pac_len);
        mPacLenSummary = (TextView) findViewById(R.id.bt_tx_pac_len_summary);

        mPowerValue = (EditText) findViewById(R.id.bt_tx_power_value);
        if (Const.isMarlin2()) {
            mPowerValue.setHint(getString(R.string.power_value_marlin2));
        } else {
            mPowerValue.setHint(getString(R.string.power_value_marlin3));
        }

        mPowerValue.addTextChangedListener(new TextWatcher() {
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
                if (mPowerValue.getText().length() != VALUE_NULL) {
                    int max=0;
                    if (Const.isMarlin2()) {
                        max=POWER_MAX_MARLIN2;
                    } else {
                        max= POWER_MAX_MARLIN3;
                    }

                    if (Integer.parseInt(mPowerValue.getText().toString()) < POWER_MIN
                            || Integer.parseInt(mPowerValue.getText().toString()) > max) {
                        Toast.makeText(BTTXActivity.this, String.format(Locale.US, "number between %d and %d", POWER_MIN, max), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                /*
                 * if (mWifiTX != null) { mWifiTX.pktlength =
                 * mChannel.getText().toString(); }
                 */
            }
        });

        mPacCnt = (EditText) findViewById(R.id.bt_tx_pac_cnt);
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
                if (mPacCnt.getText().length() != VALUE_NULL) {
                    if (Integer.parseInt(mPacCnt.getText().toString()) < PAC_CNT_MIN
                            || Integer.parseInt(mPacCnt.getText().toString()) > PAC_CNT_MAX) {
                        Toast.makeText(BTTXActivity.this, "number between 0 and 65535",
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

        mTxMode = (Spinner) findViewById(R.id.bt_tx_mode);
        ArrayAdapter<String> txModeAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.bt_tx_mode));
        txModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTxMode.setAdapter(txModeAdapter);
        mTxMode.setOnItemSelectedListener(this);

        mStart = (Button) findViewById(R.id.bt_tx_start);
        mStart.setOnClickListener(this);
        mStop = (Button) findViewById(R.id.bt_tx_stop);
        mStop.setOnClickListener(this);
        mStop.setEnabled(false);
    }

    private boolean getSettingParam() {
        if (mChannel.getText().length() == 0) {
            Toast.makeText(BTTXActivity.this, "please input TX Channel",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            mBTTx.channel = mChannel.getText().toString();
        }
        if (mBTTx.txMode != null && !mBTTx.txMode.equals(MODE_CW)) {
            if (mPacLen.getText().length() == 0) {
                Toast.makeText(BTTXActivity.this, "please input TX Pac Len",
                        Toast.LENGTH_SHORT).show();
                return false;
            } else {
                if (Integer.parseInt(mPacLen.getText().toString()) > Integer.parseInt(mMaxPacLen)) {
                    mBTTx.paclen = mMaxPacLen;
                } else {
                    mBTTx.paclen = mPacLen.getText().toString();
                }
            }

            if (mPowerValue.getText().length() == 0) {
                Toast.makeText(BTTXActivity.this, "please input TX Power Value",
                        Toast.LENGTH_SHORT).show();
                return false;
            } else {
                mBTTx.powervalue = mPowerValue.getText().toString();
            }

            if (mPacCnt.getText().length() == 0) {
                Toast.makeText(BTTXActivity.this, "please input TX Pac Cnt",
                        Toast.LENGTH_SHORT).show();
                return false;
            } else {
                mBTTx.paccnt = mPacCnt.getText().toString();
            }
        }
        Log.d(TAG, "Now BT TX Start" + "\n" + "TX Pattern is " + mBTTx.pattern + "\n"
                + "TX Channel is " + mBTTx.channel + "\n"
                + "TX Pac Type is " + mBTTx.pactype + "\n" + "TX Pac Len is " + mBTTx.paclen + "\n"
                + "TX Power Value is "
                + mBTTx.powervalue + "\n"
                + "TX Pac Cnt is " + mBTTx.paccnt);
        return true;
    }

    class BTTXHandler extends Handler {
        public BTTXHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_BT_TX_START:
                    if (btEut.BTTxStart(mBTTx)) {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BTTXActivity.this, "TX Start Success",
                                        Toast.LENGTH_SHORT).show();
                                mStart.setEnabled(false);
                                mStop.setEnabled(true);
                            }
                        });
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BTTXActivity.this, "TX Start Fail",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
                case MSG_BT_TX_STOP:
                    if (btEut.BTTxStop(mBTTx)) {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BTTXActivity.this, "TX Stop Success",
                                        Toast.LENGTH_SHORT).show();
                                mStart.setEnabled(true);
                                mStop.setEnabled(false);
                            }
                        });
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BTTXActivity.this, "TX Stop Fail",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
                case MSG_BT_OFF:
                    if (btEut.BTOff()) {
                        Toast.makeText(BTTXActivity.this, "BT Off Success",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(BTTXActivity.this, "BT Off Fail",
                                Toast.LENGTH_SHORT).show();
                    }
                    finish();
                    break;
                default:
                    break;
            }
        }
    }
}
