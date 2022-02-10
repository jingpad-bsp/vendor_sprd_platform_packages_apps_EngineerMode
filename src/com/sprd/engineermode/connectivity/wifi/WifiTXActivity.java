
package com.sprd.engineermode.connectivity.wifi;

import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.intf.IConnectivityApi.IWifiEut.WifiTX;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Toast;
import android.content.Context;
import android.widget.TableRow;
import com.unisoc.engineermode.core.common.Const;

public class WifiTXActivity extends Activity implements OnClickListener, OnItemSelectedListener {

    private static final String TAG = "WifiTXActivity";

    // Cmd Message
    private static final int WIFI_TX_GO = 3;
    private static final int WIFI_TX_STOP = 4;

    // UI Message
    private static final int GO_ALERT = 3;
    private static final int STOP_ALERT = 4;

    // setting pram key
    private static final String BAND = "tx_band";
    private static final String CHANNEL = "tx_channel";
    private static final String OFFSET = "cbw_channel_offset";
    private static final String PKT_LENGTH = "tx_pktlength";
    private static final String PKT_CNT = "tx_pktcnt";
    private static final String POWER_LEVEL = "tx_powerlevel";
    private static final String RATE = "tx_rate";
    private static final String MODE = "tx_mode";
    private static final String PREAMBLE = "tx_preamble";
    private static final String BAND_WIDTH = "tx_bandwidth";
    private static final String BAND_SBW = "tx_sbw";
    private static final String GUARD_INTERVAL = "tx_guardinterval";
    private static final String PATH = "rf_tx_path";
    private static final String STANDRARD = "rf_standrard";
    private static final String INSMODE_RES = "insmode_result";

    private int mBandPosition;
    private int mChannelPosition;
    private int mOffsetPosition;
    private int mRatePosition;
    private int mModePosition;
    private int mPreamblePosition;
    private int mBandWidthPosition;
    private int mBandSBWPosition;
    private int mGuardIntervalPosition;
    private int mPathPosition;
    private int mStandrardPosition;
    private boolean mInsmodeSuccessByEUT = false;
    private boolean mInsmodeSuccessByTX = false;

    private WifiTXHandler mWifiTXHandler;

    private Spinner mBand;
    private Spinner mChannel;
    private Spinner mOffset;
    private Spinner mRate;
    private Spinner mMode;
    private Spinner mPreamble;
    private Spinner mBandWidth;
    private Spinner mBandSBW;
    private Spinner mGuardInterval;
    private Spinner mPath;
    private Spinner mStandrard;

    private TableRow mPathTableRow;
    private TableRow mCbwTableRow;
    private TableRow mSbwTableRow;
    private TableRow mOffsetTableRow;

    private EditText mPktlength;
    private EditText mPktcnt;
    private EditText mPowerLevel;

    private Button mGo;
    private Button mStop;
    private TextView mBandTitle;
    private AlertDialog mAlertDialog;
    private AlertDialog mCmdAlertDialog;
    private SharedPreferences mPref;
    private WifiTX mWifiTX;

    ArrayAdapter<String> bandAdapter = null;
    ArrayAdapter<String> channelAdapter = null;
    ArrayAdapter<String> rateAdapter = null;
    ArrayAdapter<String> preadmbleAdapter = null;
    ArrayAdapter<String> bandWidthAdapter = null;
    ArrayAdapter<String> bandSBWAdapter = null;
    ArrayAdapter<String> pathAdapter = null;
    ArrayAdapter<String> standrardAdapter = null;
    ArrayAdapter<String> offsetAdapter = null;

//    UNISOC：1430096  Modify the value range of power level
    private int mPowerLevelMax = 24;
    private boolean mRefreshUI = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_tx);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mInsmodeSuccessByEUT = this.getIntent().getBooleanExtra(INSMODE_RES, false);
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        initUI();
        if (Const.isMarlin()) {
            initUIMarlin();
        } else {
            initUIBrcm();
        }

        HandlerThread ht = new HandlerThread(TAG);
        ht.start();

        if (mWifiTXHandler == null) {
            mWifiTXHandler = new WifiTXHandler(ht.getLooper());
        }
        if (mWifiTX == null) {
            mWifiTX = new WifiTX();
        }

        // mPref = PreferenceManager.getDefaultSharedPreferences(this);
        mAlertDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.alert_wifi))
                .setMessage(null)
                .setPositiveButton(
                        getString(R.string.alertdialog_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).create();

        mCmdAlertDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.alert_wifi))
                .setMessage(null)
                .setPositiveButton(
                        getString(R.string.alertdialog_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).create();
    }

    @Override
    protected void onResume() {
        refreshUI();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (mStop.isEnabled()) {
            Message doMessage = mWifiTXHandler.obtainMessage(WIFI_TX_STOP);
            mWifiTXHandler.sendMessage(doMessage);
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause start");
        mRefreshUI = true;
        if (Const.isMarlin()) {
            if (mWifiTX != null) {
                Editor editor = mPref.edit();
                editor.putInt(BAND, mBandPosition);
                editor.putInt(CHANNEL, mChannelPosition);
                editor.putInt(OFFSET, mOffsetPosition);
                editor.putString(PKT_LENGTH, mWifiTX.pktlength);
                editor.putString(PKT_CNT, mWifiTX.pktcnt);
                editor.putString(POWER_LEVEL, mWifiTX.powerlevel);
                editor.putInt(RATE, mRatePosition);
                editor.putInt(MODE, mModePosition);
                editor.putInt(PREAMBLE, mPreamblePosition);
                editor.putInt(BAND_WIDTH, mBandWidthPosition);
                editor.putInt(GUARD_INTERVAL, mGuardIntervalPosition);
                if (Const.isMarlin3() || Const.isMarlin3Lite() || Const.isMarlin3E()) {
                    editor.putInt(PATH, mPathPosition);
                    editor.putInt(STANDRARD, mStandrardPosition);
                    editor.putInt(BAND_SBW, mBandSBWPosition);
                }
                editor.commit();
            }
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWifiTXHandler != null) {
            Log.d(TAG, "HandlerThread has quit");
            mWifiTXHandler.getLooper().quit();
        }
    }

    @Override
    public void onClick(View v) {
        Message doMessage = null;
        // get the setting param
        if (v == mGo) {
            if (checkParams()) {
                doMessage = mWifiTXHandler.obtainMessage(WIFI_TX_GO);
            }
        } else if (v == mStop) {
            doMessage = mWifiTXHandler.obtainMessage(WIFI_TX_STOP);
        }
        if (doMessage != null) {
            mWifiTXHandler.sendMessage(doMessage);
        }
    }

    /* SPRD Bug 1065861: upgrade wifi test. @{ */
    private boolean checkParams() {
        if (mPktlength.getText().toString().length() == 0
                    || Integer.parseInt(mPktlength.getText().toString()) < 64
                    || Integer.parseInt(mPktlength.getText().toString()) > 4095) {
            Toast.makeText(WifiTXActivity.this, "Pkt length: number between 64 and 4095",
                        Toast.LENGTH_SHORT).show();
            return false;
        } else if (mPktcnt.getText().toString().length() == 0
                    || Integer.parseInt(mPktcnt.getText().toString()) < 0
                    || Integer.parseInt(mPktcnt.getText().toString()) > 65535) {
            Toast.makeText(WifiTXActivity.this, "Pkt cnt: number between 0 and 65535",
                        Toast.LENGTH_SHORT).show();
            return false;
        } else if (mPowerLevel.getText().toString().length() == 0
                    || Integer.parseInt(mPowerLevel.getText().toString()) < 0
                    || Integer.parseInt(mPowerLevel.getText().toString()) > mPowerLevelMax) {
            Toast.makeText(WifiTXActivity.this, "Power Level: number between 0 and " + mPowerLevelMax,
                        Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    /* @} */

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
            long id) {
        if (Const.isMarlin()) {
            onItemSelectMarlin(parent, view, position, id);
        } else {
            onItemSelectBrcm(parent, view, position, id);
        }
    }

    private void onItemSelectMarlin(AdapterView<?> parent, View view, int position,
            long id) {
        if (mWifiTX != null) {
            try {
                if (parent == mBand) {
                    Log.d(TAG, "mBand");
                    mWifiTX.band = this.getResources().getStringArray(R.array.band_str_arr)[position];
                    mBandPosition = position;
                } else if (parent == mChannel) {
                    if (Const.isMarlin3() || Const.isMarlin3Lite() || Const.isMarlin3E()) {
                        if (mStandrardPosition == 0 || mStandrardPosition == 1 || mStandrardPosition == 2) {//11b/11g/11n_2.4G
                            if (mBandWidthPosition == 0) {//cbw == 20M
                                mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_0)[position];
                            } else if (mBandWidthPosition == 1) {//cbw == 40M
                                if (mOffsetPosition == 0) {
                                    mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_n10)[position];
                                } else if (mOffsetPosition == 1) {
                                    mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_p10)[position];
                                }
                            }
                        } else if (mStandrardPosition == 3) {//11n_5.0G
                            if (mBandWidthPosition == 0) {//cbw == 20M
                                mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_1)[position];
                            } else if (mBandWidthPosition == 1) {//cbw == 40M
                                if (mOffsetPosition == 0) {
                                    mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_2_n10)[position];
                                } else if (mOffsetPosition == 1) {
                                    mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_2_p10)[position];
                                }
                            }
                        } else if (mStandrardPosition == 4) {//11ac
                            if (mBandWidthPosition == 0) {//cbw = 20M
                                mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_1)[position];
                            } else if (mBandWidthPosition == 1) {//cbw == 40M
                                if (mOffsetPosition == 0) {
                                    mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_2_n10)[position];
                                } else if (mOffsetPosition == 1) {
                                    mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_2_p10)[position];
                                }
                            } else if (mBandWidthPosition == 2) {//cbw == 80M
                                if (mOffsetPosition == 0) {
                                    mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_3_n30)[position];
                                } else if (mOffsetPosition == 1) {
                                    mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_3_n10)[position];
                                } else if (mOffsetPosition == 2) {
                                    mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_3_p10)[position];
                                } else if (mOffsetPosition == 3) {
                                    mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_3_p30)[position];
                                }
                            }
                        } else if (mStandrardPosition == 5) {//11a
                            if (mBandWidthPosition == 0) {//cbw = 20M
                                mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_1)[position];
                            }
                        }
                    }else if(Const.isMarlin2()) {
                        mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_0)[position];
                    }
                    mChannelPosition = position;
                } else if (parent == mOffset) {
                    if (Const.isMarlin3() || Const.isMarlin3Lite() || Const.isMarlin3E()) {
                        if (mStandrardPosition == 0 || mStandrardPosition == 1 || mStandrardPosition == 2) {//11b/11g/11n_2.4G
                            if (mBandWidthPosition == 0) {//sbw == 20M
                                mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_0)[mChannelPosition];
                            } else if (mBandWidthPosition == 1) {//sbw == 40M
                                if (position == 0) {
                                    mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_n10)[mChannelPosition];
                                } else if (position == 1) {
                                    mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_p10)[mChannelPosition];
                                }
                            }
                        } else if (mStandrardPosition == 3) {//11n_5.0G
                            if (mBandWidthPosition == 0) {//sbw == 20M
                                mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_1)[mChannelPosition];
                            } else if (mBandWidthPosition == 1) {//sbw == 40M
                                if (position == 0) {
                                    mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_2_n10)[mChannelPosition];
                                } else if (position == 1) {
                                    mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_2_p10)[mChannelPosition];
                                }
                            }
                        } else if (mStandrardPosition == 4 ||mStandrardPosition == 5) {//11ac and 11a
                            if (mBandWidthPosition == 0) {//sbw = 20M
                                mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_1)[mChannelPosition];
                            } else if (mBandWidthPosition == 1) {//sbw == 40M
                                if (position == 0) {
                                    mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_2_n10)[mChannelPosition];
                                } else if (position == 1) {
                                    mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_2_p10)[mChannelPosition];
                                }
                            } else if (mBandWidthPosition == 2) {//sbw == 80M
                                if (position == 0) {
                                    mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_3_n30)[mChannelPosition];
                                } else if (position == 1) {
                                    mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_3_n10)[mChannelPosition];
                                } else if (position == 2) {
                                    mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_3_p10)[mChannelPosition];
                                } else if (position == 3) {
                                    mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_3_p30)[mChannelPosition];
                                }
                            }
                        }
                    } else if(Const.isMarlin2()) {
                        mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_0)[mChannelPosition];
                    }
                    mOffsetPosition = position;
                } else if (parent == mRate) {
                    if (Const.isMarlin3() || Const.isMarlin3Lite() || Const.isMarlin3E()) {
                        if (mStandrardPosition == 0) {
                            mWifiTX.rate = this.getResources().getStringArray(R.array.tx_rate_int_arr_0)[position];
                        } else if (mStandrardPosition == 1) {
                            mWifiTX.rate = this.getResources().getStringArray(R.array.tx_rate_int_arr_1)[position];
                        } else if (mStandrardPosition == 2 || mStandrardPosition == 3) {
                            if (mPathPosition == 0 || mPathPosition == 1) {
                                mWifiTX.rate = this.getResources().getStringArray(R.array.tx_rate_int_arr_2_primary)[position];
                            } else {
                                mWifiTX.rate = this.getResources().getStringArray(R.array.tx_rate_int_arr_2_mimo)[position];
                            }
                        } else if (mStandrardPosition == 4) {
                            if (mPathPosition == 0 || mPathPosition == 1) {
                                mWifiTX.rate = this.getResources().getStringArray(R.array.tx_rate_int_arr_3_primary)[position];
                            } else {
                                mWifiTX.rate = this.getResources().getStringArray(R.array.tx_rate_int_arr_3_mimo)[position];
                            }
                        } else if (mStandrardPosition == 5) {
                            mWifiTX.rate = this.getResources().getStringArray(R.array.tx_rate_int_arr_802_11a)[position];
                        } else {
                            mWifiTX.rate = this.getResources().getStringArray(R.array.tx_rate_int_arr_0)[position];
                        }
                    } else if (Const.isMarlin2()) {
                        if (mStandrardPosition == 0) {
                            mWifiTX.rate = this.getResources().getStringArray(R.array.tx_rate_int_arr_0_marlin2)[position];
                        } else if (mStandrardPosition == 1) {
                            mWifiTX.rate = this.getResources().getStringArray(R.array.tx_rate_int_arr_1_marlin2)[position];
                        } else if (mStandrardPosition == 2) {
                            mWifiTX.rate = this.getResources().getStringArray(R.array.tx_rate_int_arr_2_marlin2)[position];
                        } else {
                            mWifiTX.rate = this.getResources().getStringArray(R.array.tx_rate_int_arr_0_marlin2)[position];
                        }
                    } else {
                        mWifiTX.rate = this.getResources().getStringArray(R.array.rate_int_arr)[position];
                    }
                    mRatePosition = position;
                } else if (parent == mMode) {
                    mWifiTX.mode = this.getResources().getStringArray(R.array.mode_str_arr)[position];
                    mModePosition = position;
                } else if (parent == mPreamble) {
                    mWifiTX.preamble = this.getResources().getStringArray(R.array.preamble_int_arr_0)[position];
                    mPreamblePosition = position;
                } else if (parent == mBandWidth) {
                    if (Const.isMarlin3() || Const.isMarlin3Lite() || Const.isMarlin3E()) {
                        if (mStandrardPosition == 0 || mStandrardPosition == 1) {
                            mWifiTX.bandwidth = this.getResources().getStringArray(R.array.bandwidth_int_arr_0)[position];
                        } else if(mStandrardPosition == 2 || mStandrardPosition == 3) {
                            mWifiTX.bandwidth = this.getResources().getStringArray(R.array.bandwidth_int_arr_1)[position];
                        } else if(mStandrardPosition == 4) {
                            mWifiTX.bandwidth = this.getResources().getStringArray(R.array.bandwidth_int_arr_2)[position];
                        }
                        if (position == 0) {
                            Log.d(TAG, " bandcbw_str_arr_0");
                            bandSBWAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, getResources()
                                .getStringArray(R.array.bandsbw_str_arr_0));
                            mBandSBWPosition = 0;
                            offsetAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources()
                                            .getStringArray(R.array.offset_freqnote_str_arr_0));
                        } else if (position == 1){
                            Log.d(TAG, " bandcbw_str_arr_1");
                            bandSBWAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, getResources()
                                .getStringArray(R.array.bandsbw_str_arr_1));
                            mBandSBWPosition = 1;
                            offsetAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources()
                                            .getStringArray(R.array.offset_freqnote_str_arr_1));
                        } else if (position == 2){
                            Log.d(TAG, " bandcbw_str_arr_2");
                            bandSBWAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources()
                                .getStringArray(R.array.bandsbw_str_arr_2));
                            mBandSBWPosition = 2;
                            offsetAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources()
                                            .getStringArray(R.array.offset_freqnote_str_arr_2));
                        } else if (position == 3){
                            Log.d(TAG, " bandsbw_ctr_arr_3");
                            bandSBWAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources()
                                .getStringArray(R.array.bandsbw_str_arr_3));
                            mBandSBWPosition = 3;
                            offsetAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources()
                                            .getStringArray(R.array.offset_freqnote_str_arr_2));
                        }
                        bandSBWAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mBandSBW.setAdapter(bandSBWAdapter);
                        bandSBWAdapter.notifyDataSetChanged();

                        offsetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mOffset.setAdapter(offsetAdapter);
                        offsetAdapter.notifyDataSetChanged();

                        mBandSBW.setSelection(mBandSBWPosition);

                        mChannelPosition = 0;
                        if (position == 0) {//cbw == 20M
                            if (mStandrardPosition == 0 || mStandrardPosition == 1 || mStandrardPosition == 2) {
                                channelAdapter = new ArrayAdapter<String>(this,
                                android.R.layout.simple_spinner_item, getResources()
                                        .getStringArray(R.array.channel_freqnote_str_arr_0));
                                mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_0)[mChannelPosition];
                            } else if (mStandrardPosition == 3 || mStandrardPosition == 4) {
                                channelAdapter = new ArrayAdapter<String>(this,
                                android.R.layout.simple_spinner_item, getResources()
                                        .getStringArray(R.array.channel_freqnote_str_arr_1));
                                mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_1)[mChannelPosition];
                            }
                        } else if (position == 1) {//cbw == 40M
                            if (mStandrardPosition == 2) {
                                channelAdapter = new ArrayAdapter<String>(this,
                                android.R.layout.simple_spinner_item, getResources()
                                        .getStringArray(R.array.channel_freqnote_str_arr));
                                if (mOffsetPosition == 0) {
                                    mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_n10)[mChannelPosition];
                                } else if (mOffsetPosition == 1) {
                                    mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_p10)[mChannelPosition];
                                }
                            } else if (mStandrardPosition == 3 || mStandrardPosition == 4) {
                                channelAdapter = new ArrayAdapter<String>(this,
                                android.R.layout.simple_spinner_item, getResources()
                                        .getStringArray(R.array.channel_freqnote_str_arr_2));
                                if (mOffsetPosition == 0) {
                                    mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_2_n10)[mChannelPosition];
                                } else if (mOffsetPosition == 1) {
                                    mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_2_p10)[mChannelPosition];
                                }
                            }
                        } else if (position == 2) {//cbw == 80M
                            if (mStandrardPosition == 4) {
                                channelAdapter = new ArrayAdapter<String>(this,
                                android.R.layout.simple_spinner_item, getResources()
                                        .getStringArray(R.array.channel_freqnote_str_arr_3));
                                if (mOffsetPosition == 0) {
                                    mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_3_n30)[mChannelPosition];
                                } else if (mOffsetPosition == 1) {
                                    mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_3_n10)[mChannelPosition];
                                } else if (mOffsetPosition == 2) {
                                    mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_3_p10)[mChannelPosition];
                                } else if (mOffsetPosition == 3) {
                                    mWifiTX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_3_p30)[mChannelPosition];
                                }
                            }
                        }
                        channelAdapter
                            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mChannel.setAdapter(channelAdapter);
                        channelAdapter.notifyDataSetChanged();
                    }
                    mBandWidthPosition = position;
                } else if (parent == mBandSBW) {
                    if (Const.isMarlin3() || Const.isMarlin3Lite() || Const.isMarlin3E()) {
                         if (mBandWidthPosition == 0) {
                            mWifiTX.bandsbw = this.getResources().getStringArray(R.array.bandsbw_int_arr_0)[position];
                        } else if (mBandWidthPosition == 1) {
                            mWifiTX.bandsbw = this.getResources().getStringArray(R.array.bandsbw_int_arr_1)[position];
                        } else if (mBandWidthPosition == 2) {
                            mWifiTX.bandsbw = this.getResources().getStringArray(R.array.bandsbw_int_arr_2)[position];
                        } else if (mBandWidthPosition == 3) {
                            mWifiTX.bandsbw = this.getResources().getStringArray(R.array.bandsbw_int_arr_3)[position];
                        } else {
                            mWifiTX.bandsbw = this.getResources().getStringArray(R.array.bandsbw_int_arr_0)[position];
                        }
                        mBandSBWPosition = position;
                    }
                } else if (parent == mGuardInterval) {
                    mWifiTX.guardinterval = this.getResources().getStringArray(
                            R.array.guardinterval_int_arr)[position];
                    mGuardIntervalPosition = position;
                } else if (parent == mPath) {
                    if (Const.isMarlin3() || Const.isMarlin3Lite() || Const.isMarlin3E()) {
                        mWifiTX.path = this.getResources().getStringArray(R.array.rf_path_str_arr)[position];
                        mPathPosition = position;
                        if (position == 0 || position == 1) {
                            if (mStandrardPosition == 2 || mStandrardPosition == 3) {
                                rateAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources()
                                    .getStringArray(R.array.tx_rate_str_arr_2_primary));
                            } else if  (mStandrardPosition == 4) {
                                rateAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources()
                                    .getStringArray(R.array.tx_rate_str_arr_3_primary));
                            }
                        } else if (position == 2) {
                            if (mStandrardPosition == 2 || mStandrardPosition == 3) {
                                rateAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources()
                                    .getStringArray(R.array.tx_rate_str_arr_2_mimo));
                            } else if  (mStandrardPosition == 4) {
                                rateAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources()
                                    .getStringArray(R.array.tx_rate_str_arr_3_mimo));
                            }
                            rateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            mRate.setAdapter(rateAdapter);
                            rateAdapter.notifyDataSetChanged();
                        }
                    }
                } else if (parent == mStandrard) {
                    if (Const.isMarlin3() || Const.isMarlin3Lite() || Const.isMarlin3E()) {
                        if (position == 0 || position == 1 || position == 2) {//11b/11g/2.4G_11n
                            if (mBandWidthPosition == 0) {//cbw == 20M
                                channelAdapter = new ArrayAdapter<String>(this,
                                android.R.layout.simple_spinner_item, getResources()
                                        .getStringArray(R.array.channel_freqnote_str_arr_0));
                            } else if (mBandWidthPosition == 1) {//cbw == 40M
                                channelAdapter = new ArrayAdapter<String>(this,
                                android.R.layout.simple_spinner_item, getResources()
                                        .getStringArray(R.array.channel_freqnote_str_arr));
                            }
                        } else if (position == 3) {//5G 11n
                            if (mBandWidthPosition == 0) {//cbw == 20M
                                channelAdapter = new ArrayAdapter<String>(this,
                                android.R.layout.simple_spinner_item, getResources()
                                        .getStringArray(R.array.channel_freqnote_str_arr_1));
                            } else if (mBandWidthPosition == 1) {//cbw == 40M
                                channelAdapter = new ArrayAdapter<String>(this,
                                android.R.layout.simple_spinner_item, getResources()
                                        .getStringArray(R.array.channel_freqnote_str_arr_2));
                            }
                        } else if (position == 4) {//5G 11ac
                            if (mBandWidthPosition == 0) {//cbw == 20M
                                channelAdapter = new ArrayAdapter<String>(this,
                                android.R.layout.simple_spinner_item, getResources()
                                        .getStringArray(R.array.channel_freqnote_str_arr_1));
                            } else if (mBandWidthPosition == 1) {//cbw == 40M
                                channelAdapter = new ArrayAdapter<String>(this,
                                android.R.layout.simple_spinner_item, getResources()
                                        .getStringArray(R.array.channel_freqnote_str_arr_2));
                            } else if (mBandWidthPosition == 2) {//cbw == 80M
                                channelAdapter = new ArrayAdapter<String>(this,
                                android.R.layout.simple_spinner_item, getResources()
                                        .getStringArray(R.array.channel_freqnote_str_arr_3));
                            }
                        } else if (position == 5) {//cbw == 20M
                            channelAdapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_spinner_item, getResources()
                                    .getStringArray(R.array.channel_freqnote_str_arr_1));
                        }
                        channelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mChannel.setAdapter(channelAdapter);
                        channelAdapter.notifyDataSetChanged();

                        if (position == 0) {
                            rateAdapter = new ArrayAdapter<String>(this,
                                android.R.layout.simple_spinner_item, getResources()
                                        .getStringArray(R.array.tx_rate_str_arr_0));
                            mWifiTX.rate = this.getResources().getStringArray(R.array.tx_rate_int_arr_0)[0];
                            bandWidthAdapter = new ArrayAdapter<String>(this,
                                android.R.layout.simple_spinner_item, getResources()
                                        .getStringArray(R.array.bandwidth_str_arr_0));
                        } else if (position == 1) {
                            rateAdapter = new ArrayAdapter<String>(this,
                                android.R.layout.simple_spinner_item, getResources()
                                        .getStringArray(R.array.tx_rate_str_arr_1));
                            mWifiTX.rate = this.getResources().getStringArray(R.array.tx_rate_int_arr_1)[0];
                            bandWidthAdapter = new ArrayAdapter<String>(this,
                                android.R.layout.simple_spinner_item, getResources()
                                        .getStringArray(R.array.bandwidth_str_arr_0));
                        } else if (position == 2 || position == 3) {
                            Log.e(TAG, "mPathPosition: " + mPathPosition);
                            if (mPathPosition == 0 || mPathPosition == 1) {
                                rateAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources()
                                    .getStringArray(R.array.tx_rate_str_arr_2_primary));
                                mWifiTX.rate = this.getResources().getStringArray(R.array.tx_rate_int_arr_2_primary)[0];
                            } else {
                                rateAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources()
                                    .getStringArray(R.array.tx_rate_str_arr_2_mimo));
                                mWifiTX.rate = this.getResources().getStringArray(R.array.tx_rate_int_arr_2_mimo)[0];
                            }
                            bandWidthAdapter = new ArrayAdapter<String>(this,
                                android.R.layout.simple_spinner_item, getResources()
                                        .getStringArray(R.array.bandwidth_str_arr_1));
                        } else if (position == 4) {
                            Log.e(TAG, "mPathPosition: " + mPathPosition);
                            if (mPathPosition == 0 || mPathPosition == 1) {
                                rateAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources()
                                    .getStringArray(R.array.tx_rate_str_arr_3_primary));
                                mWifiTX.rate = this.getResources().getStringArray(R.array.tx_rate_int_arr_3_primary)[0];
                            } else {
                                rateAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources()
                                    .getStringArray(R.array.tx_rate_str_arr_3_mimo));
                                mWifiTX.rate = this.getResources().getStringArray(R.array.tx_rate_int_arr_3_mimo)[0];
                            }
                            bandWidthAdapter = new ArrayAdapter<String>(this,
                                android.R.layout.simple_spinner_item, getResources()
                                        .getStringArray(R.array.bandwidth_str_arr_2));
                        } else if (position == 5) {
                            rateAdapter = new ArrayAdapter<String>(this,
                                android.R.layout.simple_spinner_item, getResources()
                                        .getStringArray(R.array.tx_rate_str_arr_802_11a));
                            mWifiTX.rate = this.getResources().getStringArray(R.array.tx_rate_int_arr_802_11a)[0];
                            bandWidthAdapter = new ArrayAdapter<String>(this,
                                android.R.layout.simple_spinner_item, getResources()
                                        .getStringArray(R.array.bandwidth_str_arr_0));
                        }
                        rateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mRate.setAdapter(rateAdapter);
                        rateAdapter.notifyDataSetChanged();

                        bandWidthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mBandWidth.setAdapter(bandWidthAdapter);
                        bandWidthAdapter.notifyDataSetChanged();
                    } else if (Const.isMarlin2()) {
                        if (position == 0) {
                            rateAdapter = new ArrayAdapter<String>(this,
                                android.R.layout.simple_spinner_item, getResources()
                                        .getStringArray(R.array.tx_rate_str_arr_0_marlin2));
                            mWifiTX.rate = this.getResources().getStringArray(R.array.tx_rate_int_arr_0_marlin2)[0];
                        } else if (position == 1) {
                            rateAdapter = new ArrayAdapter<String>(this,
                                android.R.layout.simple_spinner_item, getResources()
                                        .getStringArray(R.array.tx_rate_str_arr_1_marlin2));
                            mWifiTX.rate = this.getResources().getStringArray(R.array.tx_rate_int_arr_1_marlin2)[0];
                        } else if (position == 2) {
                            rateAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources()
                                    .getStringArray(R.array.tx_rate_str_arr_2_marlin2));
                            mWifiTX.rate = this.getResources().getStringArray(R.array.tx_rate_int_arr_2_marlin2)[0];
                        }
                        rateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mRate.setAdapter(rateAdapter);
                        rateAdapter.notifyDataSetChanged();
                    }
                    if (position == 0) {
                        Log.d(TAG, "standard 0");
                        preadmbleAdapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_spinner_item, getResources()
                            .getStringArray(R.array.preamble_str_arr_0));
                        mWifiTX.preamble = this.getResources().getStringArray(R.array.preamble_int_arr_0)[0];
                        mPowerLevel.setHint(getString(R.string.power_level_range2));
                    } else if (position == 1 || position == 5) {
                        Log.d(TAG, "standard 1");
                        preadmbleAdapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_spinner_item, getResources()
                            .getStringArray(R.array.preamble_str_arr_3));
                        mWifiTX.preamble = this.getResources().getStringArray(R.array.preamble_int_arr_3)[0];
                        mPowerLevel.setHint(getString(R.string.power_level_range1));
                    } else if (position == 2 || position == 3) {
                        Log.d(TAG, "standard 2");
                        preadmbleAdapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_spinner_item, getResources()
                            .getStringArray(R.array.preamble_str_arr_1));
                        mWifiTX.preamble = this.getResources().getStringArray(R.array.preamble_int_arr_1)[0];
                        mPowerLevel.setHint(getString(R.string.power_level_range1));
                    } else if (position == 4) {
                        Log.d(TAG, "standard 3");
                        preadmbleAdapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_spinner_item, getResources()
                            .getStringArray(R.array.preamble_str_arr_2));
                        mWifiTX.preamble = this.getResources().getStringArray(R.array.preamble_int_arr_2)[0];
                        mPowerLevel.setHint(getString(R.string.power_level_range1));
                    }
                    preadmbleAdapter
                        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mPreamble.setAdapter(preadmbleAdapter);
                    preadmbleAdapter.notifyDataSetChanged();
                    mStandrardPosition = position;
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    private void onItemSelectBrcm(AdapterView<?> parent, View view, int position,
            long id) {
        if (mWifiTX != null) {
            if (parent == mBand) {
                if (position == 0) {
                    Log.d(TAG, "-----band:2.4G-------");
                    channelAdapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_spinner_item, getResources()
                                    .getStringArray(R.array.channel_arr_0));
                    channelAdapter
                            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mChannel.setAdapter(channelAdapter);
                    channelAdapter.notifyDataSetChanged();

                    rateAdapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_spinner_item, getResources()
                                    .getStringArray(R.array.rate_str_arr_0));
                    rateAdapter
                            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mRate.setAdapter(rateAdapter);
                    rateAdapter.notifyDataSetChanged();

                    preadmbleAdapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_spinner_item, getResources()
                                    .getStringArray(R.array.preamble_str_arr_0));
                    preadmbleAdapter
                            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mPreamble.setAdapter(preadmbleAdapter);
                    preadmbleAdapter.notifyDataSetChanged();

                    bandWidthAdapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_spinner_item, getResources()
                                    .getStringArray(R.array.bandwidth_str_arr_0));
                    bandWidthAdapter
                            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mBandWidth.setAdapter(bandWidthAdapter);
                    bandWidthAdapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "-----band:5G-------");
                    channelAdapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_spinner_item, getResources()
                                    .getStringArray(R.array.channel_arr_1));
                    channelAdapter
                            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mChannel.setAdapter(channelAdapter);
                    channelAdapter.notifyDataSetChanged();

                    rateAdapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_spinner_item, getResources()
                                    .getStringArray(R.array.rate_str_arr_1));
                    rateAdapter
                            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mRate.setAdapter(rateAdapter);
                    rateAdapter.notifyDataSetChanged();

                    preadmbleAdapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_spinner_item, getResources()
                                    .getStringArray(R.array.preamble_str_arr_1));
                    preadmbleAdapter
                            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mPreamble.setAdapter(preadmbleAdapter);
                    preadmbleAdapter.notifyDataSetChanged();

                    bandWidthAdapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_spinner_item, getResources()
                                    .getStringArray(R.array.bandwidth_str_arr));
                    bandWidthAdapter
                            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mBandWidth.setAdapter(bandWidthAdapter);
                    bandWidthAdapter.notifyDataSetChanged();
                }
                mWifiTX.band = this.getResources().getStringArray(R.array.band_str_arr)[position];
                mBandPosition = position;
                Log.d(TAG, "mBandPosition=" + mBandPosition);
            } else if (parent == mPreamble) {
                if (mBandPosition == 0) {
                    Log.d(TAG, "-----band:2.4G preample selection-------");
                    channelAdapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_spinner_item, getResources()
                                    .getStringArray(R.array.channel_arr_0));
                    channelAdapter
                            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mChannel.setAdapter(channelAdapter);
                    channelAdapter.notifyDataSetChanged();

                    rateAdapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_spinner_item, getResources()
                                    .getStringArray(R.array.rate_str_arr_0));
                    rateAdapter
                            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mRate.setAdapter(rateAdapter);
                    rateAdapter.notifyDataSetChanged();

                    bandWidthAdapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_spinner_item, getResources()
                                    .getStringArray(R.array.bandwidth_str_arr_0));
                    bandWidthAdapter
                            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mBandWidth.setAdapter(bandWidthAdapter);
                    bandWidthAdapter.notifyDataSetChanged();

                    mWifiTX.preamble = this.getResources().getStringArray(
                            R.array.preamble_int_arr_0)[position];
                    mPreamblePosition = position;
                } else {
                    Log.d(TAG, "-----band:5G preample selection-------");
                    if (position == 2) {
                        Log.d(TAG, "-----band:5G preample:802.11ac-------");
                        rateAdapter = new ArrayAdapter<String>(this,
                                android.R.layout.simple_spinner_item, getResources()
                                        .getStringArray(R.array.rate_str_arr_1));
                        rateAdapter
                                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mRate.setAdapter(rateAdapter);
                        rateAdapter.notifyDataSetChanged();

                        bandWidthAdapter = new ArrayAdapter<String>(this,
                                android.R.layout.simple_spinner_item, getResources()
                                        .getStringArray(R.array.bandwidth_str_arr));
                        bandWidthAdapter
                                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mBandWidth.setAdapter(bandWidthAdapter);
                        bandWidthAdapter.notifyDataSetChanged();

                        mWifiTX.preamble = this.getResources().getStringArray(
                                R.array.preamble_int_arr_1)[position];
                        mPreamblePosition = position;
                    } else {
                        Log.d(TAG,
                                "-----band:5G preample:802.11n Mixed Mode || 802.11n Green Field-------");
                        rateAdapter = new ArrayAdapter<String>(this,
                                android.R.layout.simple_spinner_item, getResources()
                                        .getStringArray(R.array.rate_str_arr_2));
                        rateAdapter
                                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mRate.setAdapter(rateAdapter);
                        rateAdapter.notifyDataSetChanged();

                        bandWidthAdapter = new ArrayAdapter<String>(this,
                                android.R.layout.simple_spinner_item, getResources()
                                        .getStringArray(R.array.bandwidth_str_arr_1));
                        bandWidthAdapter
                                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mBandWidth.setAdapter(bandWidthAdapter);
                        bandWidthAdapter.notifyDataSetChanged();
                        mWifiTX.preamble = this.getResources().getStringArray(
                                R.array.preamble_int_arr_1)[position];
                        mPreamblePosition = position;
                    }
                    Log.d(TAG, "mPreamblePosition=" + mPreamblePosition);
                }
            } else if (parent == mBandWidth) {
                if (mBandPosition == 0) {
                    Log.d(TAG, "-----band:2.4G BandWidth selection-------");
                    rateAdapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_spinner_item, getResources()
                                    .getStringArray(R.array.rate_str_arr_0));
                    rateAdapter
                            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mRate.setAdapter(rateAdapter);
                    rateAdapter.notifyDataSetChanged();

                    mWifiTX.bandwidth = this.getResources().getStringArray(
                            R.array.bandwidth_int_arr_0)[position];
                    mBandWidthPosition = position;
                } else {
                    Log.d(TAG, "-----band:5G BandWidth selection-------");
                    if (mPreamblePosition == 0 || mPreamblePosition == 1) {
                        Log.d(TAG,
                                "-----band:5G preample:802.11n Mixed Mode || 802.11n Green Field BandWidth selection-------");
                        rateAdapter = new ArrayAdapter<String>(this,
                                android.R.layout.simple_spinner_item, getResources()
                                        .getStringArray(R.array.rate_str_arr_2));
                        rateAdapter
                                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mRate.setAdapter(rateAdapter);
                        rateAdapter.notifyDataSetChanged();

                        mWifiTX.bandwidth = this.getResources().getStringArray(
                                R.array.bandwidth_int_arr)[position];
                        mBandWidthPosition = position;
                    } else {
                        Log.d(TAG, "-----band:5G preample:802.11ac BandWidth selection-------");
                        if (position == 0) {
                            Log.d(TAG, "-----band:5G preample:802.11ac BandWidth:20M-------");
                            rateAdapter = new ArrayAdapter<String>(this,
                                    android.R.layout.simple_spinner_item, getResources()
                                            .getStringArray(R.array.rate_str_arr_3));
                            rateAdapter
                                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            mRate.setAdapter(rateAdapter);
                            rateAdapter.notifyDataSetChanged();

                            channelAdapter = new ArrayAdapter<String>(this,
                                    android.R.layout.simple_spinner_item, getResources()
                                            .getStringArray(R.array.channel_arr_1));
                            channelAdapter
                                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            mChannel.setAdapter(channelAdapter);
                            channelAdapter.notifyDataSetChanged();
                        } else if (position == 1) {
                            Log.d(TAG, "-----band:5G preample:802.11ac BandWidth:40M-------");
                            rateAdapter = new ArrayAdapter<String>(this,
                                    android.R.layout.simple_spinner_item, getResources()
                                            .getStringArray(R.array.rate_str_arr_1));
                            rateAdapter
                                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            mRate.setAdapter(rateAdapter);
                            rateAdapter.notifyDataSetChanged();

                            channelAdapter = new ArrayAdapter<String>(this,
                                    android.R.layout.simple_spinner_item, getResources()
                                            .getStringArray(R.array.channel_arr_1));
                            channelAdapter
                                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            mChannel.setAdapter(channelAdapter);
                            channelAdapter.notifyDataSetChanged();
                        } else if (position == 2) {
                            Log.d(TAG, "-----band:5G preample:802.11ac BandWidth:80M-------");
                            rateAdapter = new ArrayAdapter<String>(this,
                                    android.R.layout.simple_spinner_item, getResources()
                                            .getStringArray(R.array.rate_str_arr_1));
                            rateAdapter
                                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            mRate.setAdapter(rateAdapter);
                            rateAdapter.notifyDataSetChanged();

                            channelAdapter = new ArrayAdapter<String>(this,
                                    android.R.layout.simple_spinner_item, getResources()
                                            .getStringArray(R.array.channel_arr_2));
                            channelAdapter
                                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            mChannel.setAdapter(channelAdapter);
                            channelAdapter.notifyDataSetChanged();
                        }
                        mWifiTX.bandwidth = this.getResources().getStringArray(
                                R.array.bandwidth_int_arr)[position];
                        mBandWidthPosition = position;
                    }
                }
            } else if (parent == mChannel) {
                if (mBandPosition == 0) {
                    mWifiTX.channel = this.getResources().getStringArray(R.array.channel_arr_0)[position];
                    mChannelPosition = position;
                } else {
                    if (mPreamblePosition == 2 && mBandWidthPosition == 2) {
                        mWifiTX.channel = this.getResources().getStringArray(R.array.channel_arr_2)[position];
                        mChannelPosition = position;
                    }
                    mWifiTX.channel = this.getResources().getStringArray(R.array.channel_arr_1)[position];
                    mChannelPosition = position;
                }

            } else if (parent == mRate) {
                if (mBandPosition == 0) {
                    mWifiTX.rate = this.getResources().getStringArray(R.array.rate_str_arr_0)[position];
                    mRatePosition = position;
                } else {
                    if (mPreamblePosition == 0 || mPreamblePosition == 1) {
                        mWifiTX.rate = this.getResources().getStringArray(R.array.rate_str_arr_2)[position];
                        mRatePosition = position;
                    } else if (mPreamblePosition == 2) {
                        if (mBandWidthPosition == 0) {
                            mWifiTX.rate = this.getResources().getStringArray(
                                    R.array.rate_str_arr_0)[position];
                            mRatePosition = position;
                        }
                    }
                    mWifiTX.rate = this.getResources().getStringArray(R.array.rate_str_arr_1)[position];
                    mRatePosition = position;
                }
            } else if (parent == mMode) {
                mWifiTX.mode = this.getResources().getStringArray(R.array.mode_str_arr)[position];
                mModePosition = position;
            } else if (parent == mGuardInterval) {
                mWifiTX.guardinterval = this.getResources().getStringArray(
                        R.array.guardinterval_int_arr)[position];
                mGuardIntervalPosition = position;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    /**
     * refresh UI Handler
     */
    public Handler mHandler = new Handler() {

        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg == null) {
                Log.d(TAG, "UI Message is null");
                return;
            }

            switch (msg.what) {
                case GO_ALERT:
                    if (msg.arg1 == 1) {
                        mCmdAlertDialog.setMessage("Wifi EUT TX Start Success");
                        /* SPRD Bug 1065861: upgrade wifi test. @{ */
                        mGo.setEnabled(false);
                        mStop.setEnabled(true);
                        /* @} */
                    } else {
                        mCmdAlertDialog.setMessage("Wifi EUT TX Start Fail");
                    }
                    if (!isFinishing()) {
                        mCmdAlertDialog.show();
                    }
                    break;
                case STOP_ALERT:
                    if (msg.arg1 == 1) {
                        mCmdAlertDialog.setMessage("Wifi EUT TX Stop Success");
                        mGo.setEnabled(true);
                        /* @} */
                        mStop.setEnabled(false);
                    } else {
                        mCmdAlertDialog.setMessage("Wifi EUT TX Stop Fail");
                    }
                    Log.d(TAG, "STOP_ALERT");
                    if (!isFinishing()) {
                        mCmdAlertDialog.show();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * send cmd Handler
     *
     * @author SPREADTRUM\qianqian.tian
     */
    class WifiTXHandler extends Handler {
        public WifiTXHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Message uiMessage = null;
            switch (msg.what) {
                case WIFI_TX_GO:
                    if (mModePosition == 1) { //cw
                        if (WifiEUTHelper.getHelper().txCw(mWifiTX)) {
                            uiMessage = mHandler.obtainMessage(GO_ALERT, 1, 0);
                            Log.d(TAG, "WIFI_TX_CW uiMessage=" + uiMessage);
                        } else {
                            uiMessage = mHandler.obtainMessage(GO_ALERT, 0, 0);
                            Log.d(TAG, "WIFI_TX_CW uiMessage=" + uiMessage);
                        }
                    } else { //classic
                        if (WifiEUTHelper.getHelper().txGo(mWifiTX)) {
                            uiMessage = mHandler.obtainMessage(GO_ALERT, 1, 0);
                        } else {
                            uiMessage = mHandler.obtainMessage(GO_ALERT, 0, 0);
                        }
                    }
                    mHandler.sendMessage(uiMessage);
                    break;
                case WIFI_TX_STOP:
                    if (WifiEUTHelper.getHelper().txStop()) {
                        uiMessage = mHandler.obtainMessage(STOP_ALERT, 1, 0);
                    } else {
                        uiMessage = mHandler.obtainMessage(STOP_ALERT, 0, 0);
                    }
                    mHandler.sendMessage(uiMessage);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * init WifiTX View
     */
    private void initUI() {
        mPktlength = (EditText) findViewById(R.id.wifi_eut_pkt_length);
        mPktlength.addTextChangedListener(new TextWatcher() {
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
                if (mPktlength.getText().toString().length() == 0
                        || Integer.parseInt(mPktlength.getText().toString()) < 64
                        || Integer.parseInt(mPktlength.getText().toString()) > 4095) {
                    Toast.makeText(WifiTXActivity.this, "number between 64 and 4095",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mWifiTX != null) {
                    mWifiTX.pktlength = mPktlength.getText().toString();
                }
            }
        });

        mPktcnt = (EditText) findViewById(R.id.wifi_eut_pkt_cnt);
        mPktcnt.addTextChangedListener(new TextWatcher() {
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
                if (mPktcnt.getText().toString().length() == 0
                        || Integer.parseInt(mPktcnt.getText().toString()) < 0
                        || Integer.parseInt(mPktcnt.getText().toString()) > 65535) {
                    Toast.makeText(WifiTXActivity.this, "number between 0 and 65535",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mWifiTX != null) {
                    mWifiTX.pktcnt = mPktcnt.getText().toString();
                }
            }
        });

        mPowerLevel = (EditText) findViewById(R.id.wifi_eut_powerLevel);
        mPowerLevel.addTextChangedListener(new TextWatcher() {
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
                if (mPowerLevel.getText().toString().length() == 0
                        || Integer.parseInt(mPowerLevel.getText().toString()) < 0
                        || Integer.parseInt(mPowerLevel.getText().toString()) > mPowerLevelMax) {
                    Toast.makeText(WifiTXActivity.this, "number between 0 and " + mPowerLevelMax,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mWifiTX != null) {
                    mWifiTX.powerlevel = mPowerLevel.getText().toString();
                }
            }
        });

        mMode = (Spinner) findViewById(R.id.wifi_eut_mode);
        ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.mode_str_arr));
        modeAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMode.setAdapter(modeAdapter);
        mMode.setOnItemSelectedListener(this);

        mGuardInterval = (Spinner) findViewById(R.id.wifi_eut_guard_interval);
        ArrayAdapter<String> guardintervalAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.guardinterval_str_arr));
        guardintervalAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGuardInterval.setAdapter(guardintervalAdapter);
        mGuardInterval.setOnItemSelectedListener(this);

        mGo = (Button) findViewById(R.id.wifi_tx_go);
        mStop = (Button) findViewById(R.id.wifi_tx_stop);

        mBand = (Spinner) findViewById(R.id.wifi_eut_band);
        mBandTitle = (TextView) findViewById(R.id.wifi_eut_bandTitle);
        mGo.setOnClickListener(this);
        mStop.setOnClickListener(this);
        mStop.setEnabled(false);
    }

    /**
     * init WifiTX View(marlin)
     */
        private void initUIMarlin() {
        Log.d(TAG, "initUIMarlin start");
        mBand.setVisibility(View.GONE);
        mBandTitle.setVisibility(View.GONE);

        mChannel = (Spinner) findViewById(R.id.wifi_eut_channel);
        if (Const.isMarlin3() || Const.isMarlin3Lite() || Const.isMarlin3E()) {
            channelAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.channel_freqnote_str_arr_3));
        }else {
            channelAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.channel_freqnote_str_arr_0));
        }
        channelAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mChannel.setAdapter(channelAdapter);
        mChannel.setOnItemSelectedListener(this);

        mOffset = (Spinner) findViewById(R.id.wifi_eut_offset);
        offsetAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.offset_freqnote_str_arr_0));
        offsetAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mOffset.setAdapter(offsetAdapter);
        mOffset.setOnItemSelectedListener(this);

        mRate = (Spinner) findViewById(R.id.wifi_eut_rate);
        if (Const.isMarlin3() || Const.isMarlin3Lite() || Const.isMarlin3E()) {
            rateAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.tx_rate_str_arr));
        } else {
            rateAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.rate_str_arr));
        }

        rateAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRate.setAdapter(rateAdapter);
        mRate.setOnItemSelectedListener(this);

        mPreamble = (Spinner) findViewById(R.id.wifi_eut_preamble);
        preadmbleAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.preamble_str_arr_0));
        preadmbleAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPreamble.setAdapter(preadmbleAdapter);
        mPreamble.setOnItemSelectedListener(this);

        mBandWidth = (Spinner) findViewById(R.id.wifi_eut_band_width);
        bandWidthAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.bandwidth_str_arr));
        bandWidthAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBandWidth.setAdapter(bandWidthAdapter);
        mBandWidth.setOnItemSelectedListener(this);

        mBandSBW = (Spinner) findViewById(R.id.wifi_eut_band_sbw);
        bandSBWAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.bandsbw_str_arr));
        bandSBWAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBandSBW.setAdapter(bandSBWAdapter);
        mBandSBW.setOnItemSelectedListener(this);

        mPath = (Spinner) findViewById(R.id.wifi_eut_path);
        pathAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.rf_path_str_arr));
        pathAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPath.setAdapter(pathAdapter);
        mPath.setOnItemSelectedListener(this);

        mPathTableRow = (TableRow) findViewById(R.id.wifi_eut_path_tr);
        mCbwTableRow = (TableRow) findViewById(R.id.wifi_eut_cbw_tr);
        mSbwTableRow = (TableRow) findViewById(R.id.wifi_eut_sbw_tr);
        mOffsetTableRow = (TableRow) findViewById(R.id.wifi_eut_offset_tr);

        mStandrard = (Spinner) findViewById(R.id.wifi_eut_standard);
        standrardAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.rf_standrard_str_arr));

        if (Const.isMarlin2()){
            standrardAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                .getStringArray(R.array.rf_standrard_str_arr_marlin2));
            mPathTableRow.setVisibility(View.GONE);
            mCbwTableRow.setVisibility(View.GONE);
            mSbwTableRow.setVisibility(View.GONE);
            mOffsetTableRow.setVisibility(View.GONE);
        } else if (Const.isMarlin3Lite()){
            mPathTableRow.setVisibility(View.GONE);
        }

        standrardAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mStandrard.setAdapter(standrardAdapter);
        mStandrard.setOnItemSelectedListener(this);
    }

    /**
     * init WifiTX View(brcm)
     */
    private void initUIBrcm() {
        // mBand = (Spinner) findViewById(R.id.wifi_eut_band);
        bandAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.band_str_arr));
        bandAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBand.setAdapter(bandAdapter);
        mBand.setOnItemSelectedListener(this);
        /* mBandPosition=mPref.getInt(BAND,0); */

        Log.d(TAG, "initUIBrcm Channel");
        mChannel = (Spinner) findViewById(R.id.wifi_eut_channel);
        channelAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.channel_arr_0));
        channelAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mChannel.setAdapter(channelAdapter);
        mChannel.setOnItemSelectedListener(this);

        Log.d(TAG, "initUIBrcm Rate");
        mRate = (Spinner) findViewById(R.id.wifi_eut_rate);
        rateAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.rate_str_arr_0));
        rateAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRate.setAdapter(rateAdapter);
        mRate.setOnItemSelectedListener(this);

        Log.d(TAG, "initUIBrcm Preamble");
        mPreamble = (Spinner) findViewById(R.id.wifi_eut_preamble);
        /* if (mBandPosition == 0) { */
        Log.d(TAG, "onCreate mBandPosition" + mBandPosition);
        preadmbleAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.preamble_str_arr_0));
        preadmbleAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPreamble.setAdapter(preadmbleAdapter);
        mPreamble.setOnItemSelectedListener(this);

        Log.d(TAG, "initUIBrcm bandwidth");
        mBandWidth = (Spinner) findViewById(R.id.wifi_eut_band_width);
        /* if (mBandPosition == 0) { */
        Log.d(TAG, "onCreate mBandPosition" + mBandPosition);
        bandWidthAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.bandwidth_str_arr_0));
        bandWidthAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBandWidth.setAdapter(bandWidthAdapter);
        mBandWidth.setOnItemSelectedListener(this);
    }

    private boolean checkPosition(Spinner spinner, int position) {
        Log.d(TAG, "checkPosition spinner:"+spinner.getClass().getName()+",position="+position);
        if (spinner != null) {
            if (spinner == mChannel) {
                return channelAdapter != null && position < channelAdapter.getCount();
            } else if (spinner == mRate) {
                return rateAdapter != null && position < rateAdapter.getCount();
            } else if (spinner == mBandWidth) {
                return bandWidthAdapter != null && position < bandWidthAdapter.getCount();
            } else if (spinner == mBandSBW) {
                return bandSBWAdapter != null && position < bandSBWAdapter.getCount();
            } else if (spinner == mPath) {
                return pathAdapter != null && position < pathAdapter.getCount();
            } else if (spinner == mStandrard) {
                return standrardAdapter != null && position < standrardAdapter.getCount();
            } else if (spinner == mOffset) {
                return offsetAdapter != null && position < offsetAdapter.getCount();
            }
        }
        return false;
    }
    /* @} */

    /**
     * display the setting param which setting in the last time
     */
    private void refreshUI() {
        if (mRefreshUI) {
            try {
                mBandPosition = mPref.getInt(BAND, 0);
                mChannelPosition = mPref.getInt(CHANNEL, 0);
                mOffsetPosition = mPref.getInt(OFFSET, 0);
                mRatePosition = mPref.getInt(RATE, 0);
                mModePosition = mPref.getInt(MODE, 0);
                mPreamblePosition = mPref.getInt(PREAMBLE, 0);
                mBandWidthPosition = mPref.getInt(BAND_WIDTH, 0);
                mBandSBWPosition = mPref.getInt(BAND_SBW, 0);
                mGuardIntervalPosition = mPref.getInt(GUARD_INTERVAL, 0);
                if (Const.isMarlin3Lite()) {
                    mPathPosition = mPref.getInt(PATH, 1);
                } else {
                    mPathPosition = mPref.getInt(PATH, 0);
                }
                mStandrardPosition = mPref.getInt(STANDRARD, 0);
                mPktlength.setText(mPref.getString(PKT_LENGTH, ""));
                mPktcnt.setText(mPref.getString(PKT_CNT, ""));

                mBandSBW.setSelection(checkPosition(mBandSBW, mBandSBWPosition) ? mBandSBWPosition : 0);
                mPath.setSelection(checkPosition(mPath, mPathPosition) ? mPathPosition : 0);
                mStandrard.setSelection(checkPosition(mStandrard, mStandrardPosition) ? mStandrardPosition : 0);
                mBand.setSelection(checkPosition(mBand, mBandPosition) ? mBandPosition : 0);
                mChannel.setSelection(checkPosition(mChannel, mChannelPosition) ? mChannelPosition : 0);
                mOffset.setSelection(checkPosition(mOffset, mOffsetPosition) ? mOffsetPosition : 0);
                mRate.setSelection(checkPosition(mRate, mRatePosition) ? mRatePosition : 0);
                mBandWidth.setSelection(checkPosition(mBandWidth, mBandWidthPosition) ? mBandWidthPosition : 0);
                mGuardInterval.setSelection(mGuardIntervalPosition);
                mMode.setSelection(mModePosition);
                mPreamble.setSelection(mPreamblePosition);

                Log.d(TAG, "refreshUI mWifiTX.path: " + mWifiTX.path);
                Log.d(TAG, "refreshUI mWifiTX.rate: " + mWifiTX.rate);
                Log.d(TAG, "refreshUI mWifiTX.pktlength: " + mWifiTX.pktlength);
                Log.d(TAG, "refreshUI mWifiTX.pktcnt: " + mWifiTX.pktcnt);
                Log.d(TAG, "refreshUI mWifiTX.preamble: " + mWifiTX.preamble);
                Log.d(TAG, "refreshUI mWifiTX.bandwidth: " + mWifiTX.bandwidth);
                Log.d(TAG, "refreshUI mWifiTX.bandsbw: " + mWifiTX.bandsbw);
                Log.d(TAG, "refreshUI mWifiTX.channel: " + mWifiTX.channel);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}
