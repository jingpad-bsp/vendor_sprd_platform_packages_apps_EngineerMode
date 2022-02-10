package com.sprd.engineermode.connectivity.BT;
import java.util.ArrayList;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;
import android.view.LayoutInflater;
import android.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.util.Log;
import android.widget.Toast;

import com.unisoc.engineermode.core.common.Const;
import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.impl.connectivity.BTHelper;
import android.widget.TableRow;
import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.intf.IConnectivityApi;

public class BTBLERXActivity extends Activity implements OnClickListener,OnItemSelectedListener {

    private static final String TAG = "BTBLERXActivity";

    private static final int MSG_BT_BLE_RX_START = 0;
    private static final int MSG_BT_BLE_RX_READ = 1;
    private static final int MSG_BT_BLE_RX_STOP = 2;
    private static final int MSG_BT_BLE_OFF = 3;

    //uimessage
    private static final int RESULT_REFRESH = 0;
    private Spinner mPattern;
    private TableRow mLePhyTableRow;
    private TableRow mLeModTableRow;
    private TableRow mPacTypeTableRow;
    private Spinner mModIndex;
    private Spinner mLePhy;
    private EditText mChannel;
    private EditText mGain;
    private EditText mAddr;
    private ListView mResult;
    private Button mStart;
    private Button mRead;
    private Button mAuto;
    private Button mClear;
    private Button mStop;

    private BTHelper.BTRX mBTBLERx;
    private BTBLERXHandler mBTBLERXHandler;
    private String mRxResult = null;
    private int mRefreshTime;

    private String mCurRssi;
    private String mCurPer;
    private String mCurBer;
    private String mCurPktCnt;
    private String mCurPktErrCnt;
    private String mCurBitCnt;
    private String mCurBitErrCnt;
    private boolean isNewPer = false;
    private boolean isNewBer = false;

    private ArrayList<BTBLERXResult> mBTResult;
    private BTBLERXResultAdapter mResultAdapter;
    private BTBLERXResult mSingleRes;
    private IConnectivityApi.IBtEut btEut = CoreApi.getConnectivityApi().btEut();

    private Handler mUiThread = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg == null) {
                Log.d(TAG,"UI Message is null");
                return;
            }
            switch (msg.what) {
                case RESULT_REFRESH:
                    mResultAdapter.result.add((BTBLERXResult)msg.obj);
                    mResultAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bt_ble_rx);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        initUI();
        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mBTBLERx = new BTHelper.BTRX();
        mBTBLERXHandler = new BTBLERXHandler(ht.getLooper());
        mBTResult = new ArrayList<BTBLERXResult>();
        mResultAdapter = new BTBLERXResultAdapter(this,mBTResult);
        mResult.setAdapter(mResultAdapter);
    }

    @Override
    public void onBackPressed() {
        Message doMessage = null;
        //if BT RX is not stop, app shoule stop when click back button
        if (mStop.isEnabled()) {
            Log.d(TAG,"Stop is Enabled when click back");
            doMessage = mBTBLERXHandler.obtainMessage(MSG_BT_BLE_RX_STOP);
            mBTBLERXHandler.sendMessage(doMessage);
        }

        //close BT when click back button
        if (BTHelper.isBTOn) {
            Log.d(TAG,"BT is On when click back");
            doMessage = mBTBLERXHandler.obtainMessage(MSG_BT_BLE_OFF);
            mBTBLERXHandler.sendMessage(doMessage);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        Message doMessage = null;
        if (v == mStart) {
            if (!getSettingParam()) {
                return;
            }
            doMessage = mBTBLERXHandler.obtainMessage(MSG_BT_BLE_RX_START);
            mBTBLERXHandler.sendMessage(doMessage);
        } else if (v == mRead) {
            disableSettingPara();
            mUiThread.removeCallbacks(mRefreshHandler);
            doMessage = mBTBLERXHandler.obtainMessage(MSG_BT_BLE_RX_READ);
            mBTBLERXHandler.sendMessage(doMessage);
        } else if (v == mAuto) {
            disableSettingPara();
            LayoutInflater inflater = (LayoutInflater) BTBLERXActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
            final View view = inflater.inflate(R.layout.alert_edittext, null);
            new AlertDialog.Builder(BTBLERXActivity.this).setTitle(getString(R.string.rx_refresh_time))
            .setView(view)
            .setPositiveButton(getString(R.string.alertdialog_ok),new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditText refreshTime = (EditText)view.findViewById(R.id.bt_rx_refresh_time);
                    if (refreshTime.getText().length() != 0) {
                        mRefreshTime = Integer.valueOf(refreshTime.getText().toString());
                        Log.d(TAG,"Refresh time is "+mRefreshTime);
                        mBTBLERXHandler.removeMessages(MSG_BT_BLE_RX_READ);
                        mUiThread.post(mRefreshHandler);
                    } else {
                        Toast.makeText(BTBLERXActivity.this, "no refresh time, do nothing",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }).create().show();
        } else if (v == mStop) {
            mUiThread.removeCallbacks(mRefreshHandler);
            mBTBLERXHandler.removeMessages(MSG_BT_BLE_RX_READ);
            if (!getSettingParam()) {
                return;
            }
            enableSettingPara();
            doMessage = mBTBLERXHandler.obtainMessage(MSG_BT_BLE_RX_STOP);
            mBTBLERXHandler.sendMessage(doMessage);
        } else if (v == mClear) {
            mUiThread.removeCallbacks(mRefreshHandler);
            mBTBLERXHandler.removeMessages(MSG_BT_BLE_RX_READ);
            mResultAdapter.result.clear();
            mResultAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
            long id) {
        if (mBTBLERx != null) {
            /*if (parent == mPacType) {
                mBTBLERx.pactype = this.getResources().getStringArray(R.array.bt_ble_pac_type_int)[position];
            }*/
            if (parent == mPattern) {
                mBTBLERx.pattern = this.getResources().getStringArray(R.array.bt_ble_rx_pattern_int)[position];
            }
            if (Const.isMarlin3() || Const.isMarlin3Lite() || Const.isMarlin3E()) {
                if (parent == mModIndex) {
                    mBTBLERx.modindex = this.getResources().getStringArray(R.array.bt_ble_rx_mod_index_int)[position];
                } else if (parent == mLePhy) {
                    mBTBLERx.lephy = this.getResources().getStringArray(R.array.bt_ble_rx_le_phy_int)[position];
                }
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void initUI(){
        mChannel = (EditText)findViewById(R.id.bt_ble_rx_channel);
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
                    if (Integer.parseInt(mChannel.getText().toString()) <0
                            || Integer.parseInt(mChannel.getText().toString()) > 39) {
                        Toast.makeText(BTBLERXActivity.this, "number between 0 and 39",
                                Toast.LENGTH_SHORT).show();
                          return;
                      }
                }
            }
        });


        mPattern = (Spinner) findViewById(R.id.bt_ble_rx_pattern);
        ArrayAdapter<String> patternAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.bt_ble_rx_pattern));
        patternAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPattern.setAdapter(patternAdapter);
        mPattern.setOnItemSelectedListener(this);

        mModIndex = (Spinner) findViewById(R.id.bt_ble_rx_mod_index);
        ArrayAdapter<String> mModIndexAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.bt_ble_rx_mod_index));
        mModIndexAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mModIndex.setAdapter(mModIndexAdapter);
        mModIndex.setOnItemSelectedListener(this);

        mLePhy = (Spinner) findViewById(R.id.bt_ble_rx_le_phy);
        ArrayAdapter<String> mLePhyAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.bt_ble_rx_le_phy));
        mLePhyAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLePhy.setAdapter(mLePhyAdapter);
        mLePhy.setOnItemSelectedListener(this);
        mLeModTableRow = (TableRow) findViewById(R.id.bt_ble_rx_mod_tablerow);
        mLePhyTableRow = (TableRow) findViewById(R.id.bt_ble_rx_phy_tablerow);
        mPacTypeTableRow = (TableRow) findViewById(R.id.bt_ble_rx_pactype_tablerow);
        if (Const.isMarlin2()) {
            mLeModTableRow.setVisibility(View.GONE);
            mLePhyTableRow.setVisibility(View.GONE);
        }
        mPacTypeTableRow.setVisibility(View.GONE);
        mGain = (EditText)findViewById(R.id.bt_ble_rx_gain);
        mGain.addTextChangedListener(new TextWatcher() {
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
                if (mGain.getText().length() != 0) {
                    if (Integer.parseInt(mGain.getText().toString()) <0
                            || Integer.parseInt(mGain.getText().toString()) > 5) {
                       Toast.makeText(BTBLERXActivity.this, "number between 0 and 5",
                                Toast.LENGTH_SHORT).show();
                          return;
                      }
                }
            }
        });

        mAddr = (EditText)findViewById(R.id.bt_ble_rx_addr);
        mResult = (ListView)findViewById(R.id.ble_rx_result_text);

        mStart = (Button)findViewById(R.id.bt_ble_rx_start);
        mStart.setOnClickListener(this);
        mRead = (Button)findViewById(R.id.bt_ble_rx_read);
        mRead.setOnClickListener(this);
        //mRead.setEnabled(false);
        mAuto = (Button)findViewById(R.id.bt_ble_rx_auto);
        mAuto.setOnClickListener(this);
        //mAuto.setEnabled(false);
        mClear = (Button)findViewById(R.id.bt_ble_rx_clear);
        mClear.setOnClickListener(this);
        mStop = (Button)findViewById(R.id.bt_ble_rx_stop);
        mStop.setOnClickListener(this);
        mStop.setEnabled(false);
    }

    private boolean getSettingParam(){
        if (mChannel.getText().length() == 0) {
            Toast.makeText(BTBLERXActivity.this, "please input BLE TX Channel",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            mBTBLERx.channel = mChannel.getText().toString();
        }
        if (mGain.getText().length() == 0) {
            Toast.makeText(BTBLERXActivity.this, "please input BLE RX Gain",
                Toast.LENGTH_SHORT).show();
            return false;
        } else {
            mBTBLERx.gain = mGain.getText().toString();
        }
        if (mAddr.getText().length() == 0) {
            Toast.makeText(BTBLERXActivity.this, "please input BLE RX Addr",
                Toast.LENGTH_SHORT).show();
            return false;
        } else if (mAddr.getText().length() < 12) {
            Toast.makeText(BTBLERXActivity.this, "please input 12 bits BLE RX Addr",
                Toast.LENGTH_SHORT).show();
            return false;
        } else {
            //add : to addr
            mBTBLERx.addr = String.format("%s:%s:%s:%s:%s:%s",
                            mAddr.getText().toString().substring(0, 2),
                            mAddr.getText().toString().substring(2, 4),
                            mAddr.getText().toString().substring(4, 6),
                            mAddr.getText().toString().substring(6, 8),
                            mAddr.getText().toString().substring(8, 10),
                            mAddr.getText().toString().substring(10, 12));
        }
        Log.d(TAG,"Now BT BLE RX Test"+"\n"+"BLE RX modindex is "+mBTBLERx.modindex+"\n"
                +"BLE RX lephy is "+mBTBLERx.lephy+"\n"+"BLE RX Channel is "+mBTBLERx.channel+"\n"
                +"BLE RX Pac Type is "+mBTBLERx.pactype+"\n"
                +"BLE RX Gain is "+mBTBLERx.gain+"\n"+"BLE RX Addr is "+mBTBLERx.addr);
        return true;
    }

    private void disableSettingPara() {
        mChannel.setEnabled(false);
    }

    private void enableSettingPara() {
        mChannel.setEnabled(true);
        if (Const.isMarlin2()) {
            //mPacType.setEnabled(true);
            mGain.setEnabled(true);
            mAddr.setEnabled(true);
        }
        if (Const.isMarlin3() || Const.isMarlin3Lite() || Const.isMarlin3E()) {
            mModIndex.setEnabled(true);
            mLePhy.setEnabled(true);
        }
    }

    class BTBLERXHandler extends Handler {
        public BTBLERXHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case MSG_BT_BLE_RX_START:
                    if (btEut.BTBLERxStart(mBTBLERx)) {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG,"BLE RX Start Success");
                                mStart.setEnabled(false);
                                Toast.makeText(BTBLERXActivity.this, "BLE RX Start Success",
                                        Toast.LENGTH_SHORT).show();
                                if (Const.isMarlin2()) {
                                    mRead.setEnabled(true);
                                    mAuto.setEnabled(true);
                                }
                                mStop.setEnabled(true);
                            }
                        });
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BTBLERXActivity.this, "BLE RX Start Fail",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
                case MSG_BT_BLE_RX_READ:
                    isNewPer = false;
                    isNewBer = false;
                    mRxResult = btEut.BTBLERxRead();
                    Log.d(TAG,"RX Read Result is "+mRxResult);
                    if (mRxResult != null && mRxResult.contains("OK")) {
                        String res = mRxResult.substring(3);
                        Log.d(TAG,"res is "+res);
                        String[] res1= res.split("\\,");
                        for (int i = 0; i < res1.length; i++){
                            String[] res2 = res1[i].split("\\:");
                            if (res2[0].contains("rssi")) {
                                mCurRssi = "-"+res2[1];
                            } else if (res2[0].contains("per")) {
                               mCurPer = res2[1];
                            } else if (res2[0].contains("ber")) {
                               mCurBer = res2[1];
                            } else if (res2[0].contains("pkt_cnt")) {
                                mCurPktCnt = res2[1];
                            } else if (res2[0].contains("pkt_err_cnt")) {
                                mCurPktErrCnt = res2[1];
                                if (mCurPktErrCnt.equals("0")) {
                                    mCurPer = "0/"+mCurPktCnt+"=0%";
                                } else {
                                    isNewPer = true;
                                }
                            } else if (res2[0].contains("bit_cnt")) {
                                mCurBitCnt = res2[1];
                            } else if (res2[0].contains("bit_err_cnt")) {
                                mCurBitErrCnt = res2[1];
                                if(mCurBitErrCnt.contains("\0")){
                                    Log.d(TAG,"mCurBitErrCnt.contains \0");
                                    String [] value = mCurBitErrCnt.split("\0");
                                    Log.d(TAG,"value = "+value[0]);
                                    mCurBitErrCnt = value[0];
                                }
                                if (mCurBitErrCnt.equals("0")) {
                                    mCurBer = "0/"+mCurBitCnt+"=0%";
                                } else {
                                    isNewBer = true;
                                }
                            }
                            if (isNewPer) {
                                mCurPer = mCurPktErrCnt+"/"+mCurPktCnt+"=";
                                mCurPer += Math.floor((float) Integer.valueOf(mCurPktErrCnt) /(float) Integer.valueOf(mCurPktCnt) * 1000000)/10000 +"%";
                            }
                            if (isNewBer) {
                                mCurBer = mCurBitErrCnt+"/"+mCurBitCnt+"=";
                                mCurBer += Math.floor((float) Integer.valueOf(mCurBitErrCnt) /(float) Integer.valueOf(mCurBitCnt) * 1000000)/10000 +"%";
                            }
                        }
                        if (mResultAdapter.result.size()%2  == 0) {
                            mSingleRes = new BTBLERXResult(mCurRssi,mCurPer,/*mCurBer,*/true);
                        } else {
                            mSingleRes = new BTBLERXResult(mCurRssi,mCurPer,/*mCurBer,*/false);
                        }
                        //use message to transfer BTRXResult can avoid data cross because of MultiThread
                        Message uiMessage = mUiThread.obtainMessage(RESULT_REFRESH, mSingleRes);
                        mUiThread.sendMessage(uiMessage);
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BTBLERXActivity.this, "BLE RX Rec Fail",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
                case MSG_BT_BLE_RX_STOP:
                    if (btEut.BTBLERxStop(mBTBLERx)) {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BTBLERXActivity.this, "BLE RX Stop Success",
                                        Toast.LENGTH_SHORT).show();
                                mStart.setEnabled(true);
                                //mRead.setEnabled(false);
                                //mAuto.setEnabled(false);
                                mStop.setEnabled(false);
                            }
                        });
                    } else {
                        mUiThread.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BTBLERXActivity.this, "BLE RX Stop Fail",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
                case MSG_BT_BLE_OFF:
                    if (btEut.BTOff()) {
                        Toast.makeText(BTBLERXActivity.this, "BT Off Success",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(BTBLERXActivity.this, "BT Off Fail",
                                Toast.LENGTH_SHORT).show();
                    }
                    finish();
                    break;
                default:
                    break;
            }
        }
    }

    private final Runnable mRefreshHandler = new Runnable() {
        @Override
        public void run() {
            Message autoMessage = mBTBLERXHandler.obtainMessage(MSG_BT_BLE_RX_READ);
            mBTBLERXHandler.sendMessage(autoMessage);
            mUiThread.postDelayed(mRefreshHandler, mRefreshTime);
        }
    };

}