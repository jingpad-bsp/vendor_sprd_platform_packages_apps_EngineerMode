
package com.sprd.engineermode.connectivity.wifi;

import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.intf.IConnectivityApi.IWifiEut.WifiRX;

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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;

import java.text.NumberFormat;
import com.unisoc.engineermode.core.common.Const;
import android.widget.TableRow;

public class WifiRXActivity extends Activity implements OnClickListener, OnItemSelectedListener {

    private static final String TAG = "WifiRXActivity";
    private static final String INSMODE_RES = "insmode_result";

    // cmd Message
    private static final int INIT_TEST_STATUS = 0;
    private static final int DEINIT_TEST_STATUS = 1;
    private static final int WIFI_RX_GO = 2;
    private static final int WIFI_RX_STOP = 3;
    private static final int REFRESH_VIEW = 4;

    // UI Message
    private static final int INIT_VIEW = 0;
    private static final int DEINIT_VIEW = 1;
    private static final int GO_ALERT = 2;
    private static final int STOP_ALERT = 3;

    // setting pram key
    private static final String CHANNEL = "rx_channel";
    private static final String TEST_NUM = "rx_test_num";
    private static final String BAND_CBW = "rx_band_cbw";
    private static final String BAND_SBW = "rx_band_sbw";
    private static final String STANDRARD = "rf_standrard";
    private static final String OFFSET = "cbw_channel_offset";

    private boolean mInsmodeSuccessByEUT = false;
    private boolean mInsmodeSuccessByRX = false;

    private WifiRXHandler mWifiRXHandler;

    private Spinner mBand;
    private Spinner mChannel;
    private Spinner mOffset;
    private Spinner mBandCBW;
    private Spinner mBandSBW;
    private Spinner mStandrard;
    private EditText mTestRXNum;
    private Spinner mBandWidth;
    private TextView mRXOkResult;
    private TextView mPERResult;
    private Spinner mPath;
    private Button mGo;
    private Button mStop;

    private TableRow mPathTableRow;
    private TableRow mCbwTableRow;
    private TableRow mSbwTableRow;
    private TableRow mOffsetTableRow;

    private TextView mBandTitle;
    private TextView mBandWidthTitle;
    private TextView mPathText;

    ArrayAdapter<String> bandAdapter = null;
    ArrayAdapter<String> channelAdapter = null;
    ArrayAdapter<String> bandWidthAdapter = null;
    ArrayAdapter<String> bandCBWAdapter = null;
    ArrayAdapter<String> bandSBWAdapter = null;
    ArrayAdapter<String> standrardAdapter = null;
    ArrayAdapter<String> pathAdapter = null;
    ArrayAdapter<String> offsetAdapter = null;

    private AlertDialog mAlertDialog;
    private AlertDialog mCmdAlertDialog;
    private SharedPreferences mPref;

    private WifiRX mWifiRX;

    private int mBandPosition;
    private int mChannelPosition;
    private int mOffsetPosition;
    private int mBandWidthPosition;
    private int mBandSBWPosition;
    private int mBandCBWPosition;
    private int mStandrardPosition;
    private int mPathPosition;
    private static final String PATH = "rf_rx_path";

    private String mOkResult;
    private String mRXPerResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_rx);
        getWindow().setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mInsmodeSuccessByEUT = this.getIntent().getBooleanExtra(INSMODE_RES, false);

        initUI();
        if (Const.isMarlin()) {
            initUIMarlin();
        } else {
            Log.d(TAG, "initUIBrcm");
            initUIBrcm();
        }

        mPref = PreferenceManager.getDefaultSharedPreferences(this);
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

        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mWifiRXHandler = new WifiRXHandler(ht.getLooper());

        mWifiRX = new WifiRX();
        refreshUI();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed()");
        if (mStop.isEnabled()) {
            Message doMessage = mWifiRXHandler.obtainMessage(WIFI_RX_STOP);
            mWifiRXHandler.sendMessage(doMessage);
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        if (Const.isMarlin()) {
            if (mWifiRX != null) {
                Editor editor = mPref.edit();
                editor.putInt(CHANNEL, mChannelPosition);
                editor.putInt(OFFSET, mOffsetPosition);
                editor.putString(TEST_NUM, mWifiRX.rxtestnum);
                if (Const.isMarlin3() || Const.isMarlin3Lite() || Const.isMarlin3E()) {
                    editor.putInt(PATH, mPathPosition);
                    editor.putInt(BAND_CBW, mBandCBWPosition);
                    editor.putInt(STANDRARD, mStandrardPosition);
                    editor.putInt(BAND_SBW, mBandSBWPosition);
                }
                editor.commit();
            }
            Log.d(TAG, "mChannelOffsetPosition onPause" + mOffsetPosition);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mWifiRXHandler != null) {
            Log.d(TAG, "HandlerThread has quit");
            mWifiRXHandler.getLooper().quit();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        Message doMessage = null;
        // get the setting param
        getSettingParam();
        if (v == mGo) {
            doMessage = mWifiRXHandler.obtainMessage(WIFI_RX_GO);
        } else if (v == mStop) {
            doMessage = mWifiRXHandler.obtainMessage(WIFI_RX_STOP);
        }
        if (doMessage != null) {
            mWifiRXHandler.sendMessage(doMessage);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        if (Const.isMarlin()) {
            onItemSelectMarlin(parent, view, position, id);
        } else {
            onItemSelectBrcm(parent, view, position, id);
        }
    }

    private void onItemSelectMarlin(AdapterView<?> parent, View view, int position, long id) {
        if (mWifiRX == null) {
            return;
        }
        if (parent == mChannel) {
            if (Const.isMarlin3() || Const.isMarlin3Lite() || Const.isMarlin3E()) {
                if (mStandrardPosition == 0 || mStandrardPosition == 1 || mStandrardPosition == 2) {//11b/11g/11n_2.4G
                    if (mBandCBWPosition == 0) {//cbw == 20M
                        mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_0)[position];
                    } else if (mBandCBWPosition == 1) {//cbw == 40M
                        if (mOffsetPosition == 0) {
                            mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_n10)[position];
                        } else if (mOffsetPosition == 1) {
                            mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_p10)[position];
                        }
                    }
                } else if (mStandrardPosition == 3) {//11n_5.0G
                    if (mBandCBWPosition == 0) {//cbw == 20M
                        mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_1)[position];
                    } else if (mBandCBWPosition == 1) {//cbw == 40M
                        if (mOffsetPosition == 0) {
                            mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_2_n10)[position];
                        } else if (mOffsetPosition == 1) {
                            mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_2_p10)[position];
                        }
                    }
                } else if (mStandrardPosition == 4) {//11ac
                    if (mBandCBWPosition == 0) {//cbw = 20M
                        mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_1)[position];
                    } else if (mBandCBWPosition == 1) {//cbw == 40M
                        if (mOffsetPosition == 0) {
                            mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_2_n10)[position];
                        } else if (mOffsetPosition == 1) {
                            mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_2_p10)[position];
                        }
                    } else if (mBandCBWPosition == 2) {//cbw == 80M
                        if (mOffsetPosition == 0) {
                            mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_3_n30)[position];
                        } else if (mOffsetPosition == 1) {
                            mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_3_n10)[position];
                        } else if (mOffsetPosition == 2) {
                            mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_3_p10)[position];
                        } else if (mOffsetPosition == 3) {
                            mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_3_p30)[position];
                        }
                    }
                } else if (mStandrardPosition == 5) {//11a
                    mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_1)[position];
                }
            } else if (Const.isMarlin2()) {
                mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_0)[position];
            }
            mChannelPosition = position;
        } else if (parent == mOffset) {
            if (Const.isMarlin3() || Const.isMarlin3Lite() || Const.isMarlin3E()) {
                if (mStandrardPosition == 0 || mStandrardPosition == 1 || mStandrardPosition == 2) {//11b/11g/11n_2.4G
                    if (mBandCBWPosition == 0) {//cbw == 20M
                        mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_0)[mChannelPosition];
                    } else if (mBandCBWPosition == 1) {//cbw == 40M
                        if (position == 0) {
                            mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_n10)[mChannelPosition];
                        } else if (position == 1) {
                            mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_p10)[mChannelPosition];
                        }
                    }
                } else if (mStandrardPosition == 3) {//11n_5.0G
                    if (mBandCBWPosition == 0) {//cbw == 20M
                        mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_1)[mChannelPosition];
                    } else if (mBandCBWPosition == 1) {//sbw == 40M
                        if (position == 0) {
                            mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_2_n10)[mChannelPosition];
                        } else if (position == 1) {
                            mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_2_p10)[mChannelPosition];
                        }
                    }
                } else if (mStandrardPosition == 4) {//11ac
                    if (mBandCBWPosition == 0) {//sbw = 20M
                        mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_1)[mChannelPosition];
                    } else if (mBandCBWPosition == 1) {//sbw == 40M
                        if (position == 0) {
                            mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_2_n10)[mChannelPosition];
                        } else if (position == 1) {
                            mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_2_p10)[mChannelPosition];
                        }
                    } else if (mBandCBWPosition == 2) {//sbw == 80M
                        if (position == 0) {
                            mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_3_n30)[mChannelPosition];
                        } else if (position == 1) {
                            mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_3_n10)[mChannelPosition];
                        } else if (position == 2) {
                            mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_3_p10)[mChannelPosition];
                        } else if (position == 3) {
                            mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_3_p30)[mChannelPosition];
                        }
                    }
                }
            }
        } else if (parent == mBandCBW) {
            if (Const.isMarlin3() || Const.isMarlin3Lite() || Const.isMarlin3E()) {
                if (mStandrardPosition == 0 || mStandrardPosition == 1) {
                    mWifiRX.bandcbw = this.getResources().getStringArray(R.array.bandwidth_int_arr_0)[position];
                } else if(mStandrardPosition == 2 || mStandrardPosition == 3) {
                    mWifiRX.bandcbw = this.getResources().getStringArray(R.array.bandwidth_int_arr_1)[position];
                } else if(mStandrardPosition == 4) {
                    mWifiRX.bandcbw = this.getResources().getStringArray(R.array.bandwidth_int_arr_2)[position];
                }
                if (position == 0) {
                    Log.d(TAG, " bandsbw_str_arr_0");
                    bandSBWAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.bandsbw_str_arr_0));
                    mBandSBWPosition = 0;
                    offsetAdapter = new ArrayAdapter<String>(this,
                         android.R.layout.simple_spinner_item, getResources()
                         .getStringArray(R.array.offset_freqnote_str_arr_0));
                } else if (position == 1) {
                    Log.d(TAG, " bandsbw_str_arr_1");
                    bandSBWAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.bandsbw_str_arr_1));
                    mBandSBWPosition = 1;
                    offsetAdapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.offset_freqnote_str_arr_1));
                } else if (position == 2) {
                    Log.d(TAG, " bandsbw_str_arr_2");
                    bandSBWAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.bandsbw_str_arr_2));
                    mBandSBWPosition = 2;
                    offsetAdapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.offset_freqnote_str_arr_2));
                } else if (position == 3) {
                    Log.d(TAG, "bandsbw_str_arr_3");
                    bandSBWAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.bandsbw_str_arr_3));
                    mBandSBWPosition = 3;
                    offsetAdapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.offset_freqnote_str_arr_2));
                }
                bandSBWAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mBandSBW.setAdapter(bandSBWAdapter);
                bandSBWAdapter.notifyDataSetChanged();
                mBandSBW.setSelection(mBandSBWPosition);

                offsetAdapter
                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mOffset.setAdapter(offsetAdapter);
                offsetAdapter.notifyDataSetChanged();

                mChannelPosition = 0;
                if (position == 0) {//cbw == 20M
                    if (mStandrardPosition == 0 || mStandrardPosition == 1 || mStandrardPosition == 2) {
                        channelAdapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item, getResources()
                            .getStringArray(R.array.channel_freqnote_str_arr_0));
                        mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_0)[mChannelPosition];
                    } else if (mStandrardPosition == 3 || mStandrardPosition == 4) {
                        channelAdapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item, getResources()
                            .getStringArray(R.array.channel_freqnote_str_arr_1));
                        mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_1)[mChannelPosition];
                    }
                } else if (position == 1) {//cbw == 40M
                    if (mStandrardPosition == 2) {
                        channelAdapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_spinner_item, getResources()
                            .getStringArray(R.array.channel_freqnote_str_arr));
                        if (mOffsetPosition == 0) {
                            mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_n10)[mChannelPosition];
                        } else if (mOffsetPosition == 1) {
                            mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_p10)[mChannelPosition];
                        }
                    } else if (mStandrardPosition == 3 || mStandrardPosition == 4) {
                        channelAdapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item, getResources()
                            .getStringArray(R.array.channel_freqnote_str_arr_2));
                        if (mOffsetPosition == 0) {
                            mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_2_n10)[mChannelPosition];
                        } else if (mOffsetPosition == 1) {
                            mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_2_p10)[mChannelPosition];
                        }
                    }
                } else if (position == 2) {//cbw == 80M
                    if (mStandrardPosition == 4) {
                        channelAdapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_spinner_item, getResources()
                            .getStringArray(R.array.channel_freqnote_str_arr_3));
                        if (mOffsetPosition == 0) {
                            mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_3_n30)[mChannelPosition];
                        } else if (mOffsetPosition == 1) {
                            mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_3_n10)[mChannelPosition];
                        } else if (mOffsetPosition == 2) {
                                    mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_3_p10)[mChannelPosition];
                        } else if (mOffsetPosition == 3) {
                            mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_3_p30)[mChannelPosition];
                        }
                    }
                }
                channelAdapter
                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mChannel.setAdapter(channelAdapter);
                channelAdapter.notifyDataSetChanged();
            }
            mBandCBWPosition = position;
        } else if (parent == mBandSBW) {
            if (Const.isMarlin3() || Const.isMarlin3Lite() || Const.isMarlin3E()) {
                if (mBandCBWPosition == 0) {
                    mWifiRX.bandsbw = this.getResources().getStringArray(R.array.bandsbw_int_arr_0)[position];
                } else if (mBandCBWPosition == 1) {
                    mWifiRX.bandsbw = this.getResources().getStringArray(R.array.bandsbw_int_arr_1)[position];
                } else if (mBandCBWPosition == 2) {
                            mWifiRX.bandsbw = this.getResources().getStringArray(R.array.bandsbw_int_arr_2)[position];
                } else if (mBandCBWPosition == 3) {
                    mWifiRX.bandsbw = this.getResources().getStringArray(R.array.bandsbw_int_arr_3)[position];
                } else {
                    mWifiRX.bandsbw = this.getResources().getStringArray(R.array.bandsbw_int_arr_0)[position];
                }
                mBandSBWPosition = position;
            }
        } else if (parent == mPath) {
            if (Const.isMarlin3()) {
                mWifiRX.path = this.getResources().getStringArray(R.array.rf_path_str_arr)[position];
                mPathPosition = position;
            }
        } else if (parent == mStandrard) {
            if (Const.isMarlin3() || Const.isMarlin3Lite() || Const.isMarlin3E()) {
                if (position == 0 || position == 1 || position == 2) {//2.4G 11b/11g/11n
                    if (mBandCBWPosition == 0) {//cbw == 20M
                        channelAdapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item, getResources()
                            .getStringArray(R.array.channel_freqnote_str_arr_0));
                    } else if (mBandCBWPosition == 1) {//cbw == 40M
                        channelAdapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_spinner_item, getResources()
                            .getStringArray(R.array.channel_freqnote_str_arr));
                    }
                } else if (position == 3) {//5G 11n
                    if (mBandCBWPosition == 0) {//cbw == 20M
                        channelAdapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_spinner_item, getResources()
                            .getStringArray(R.array.channel_freqnote_str_arr_1));
                    } else if (mBandCBWPosition == 1) {//cbw == 40M
                        channelAdapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_spinner_item, getResources()
                            .getStringArray(R.array.channel_freqnote_str_arr_2));
                    }
                } else if (position == 4) {//5G 11ac
                    if (mBandCBWPosition == 0) {//cbw == 20M
                        channelAdapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_spinner_item, getResources()
                            .getStringArray(R.array.channel_freqnote_str_arr_1));
                    } else if (mBandCBWPosition == 1) {//cbw == 40M
                        channelAdapter = new ArrayAdapter<String>(this,
                             android.R.layout.simple_spinner_item, getResources()
                            .getStringArray(R.array.channel_freqnote_str_arr_2));
                    } else if (mBandCBWPosition == 2) {//cbw == 80M
                        channelAdapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_spinner_item, getResources()
                            .getStringArray(R.array.channel_freqnote_str_arr_3));
                    }
                } else if (position == 5) {//5G 11a
                    channelAdapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.channel_freqnote_str_arr_1));
                }
                channelAdapter
                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mChannel.setAdapter(channelAdapter);
                channelAdapter.notifyDataSetChanged();

                if (position == 0) {
                    bandCBWAdapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.bandwidth_str_arr_0));
                } else if (position == 1) {
                    bandCBWAdapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.bandwidth_str_arr_0));
                } else if (position == 2 || position == 3) {
                    bandCBWAdapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.bandwidth_str_arr_1));
                } else if (position == 4) {
                    bandCBWAdapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.bandwidth_str_arr_2));
                } else if (position == 5) {
                    bandCBWAdapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.bandwidth_str_arr_0));
                }
                bandCBWAdapter
                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                 mBandCBW.setAdapter(bandCBWAdapter);
                bandCBWAdapter.notifyDataSetChanged();
            }
            mStandrardPosition = position;
        }
    }

    private void onItemSelectBrcm(AdapterView<?> parent, View view, int position, long id) {
        if (mWifiRX == null) {
            return;
        }
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

                bandWidthAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, getResources()
                    .getStringArray(R.array.bandwidth_str_arr));
                bandWidthAdapter
                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mBandWidth.setAdapter(bandWidthAdapter);
                bandWidthAdapter.notifyDataSetChanged();
            }
            mWifiRX.band = this.getResources().getStringArray(R.array.band_str_arr)[position];
            mBandPosition = position;
        } else if (parent == mBandWidth) {
            if (mBandPosition == 0) {
                Log.d(TAG, "-----band:2.4G BandWidth selection-------");
                mWifiRX.bandwidth = this.getResources().getStringArray(
                    R.array.bandwidth_int_arr_0)[position];
                mBandWidthPosition = position;
            } else {
                Log.d(TAG, "-----band:5G BandWidth selection-------");
                if (position == 2) {
                    channelAdapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.channel_arr_2));
                    channelAdapter
                        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mChannel.setAdapter(channelAdapter);
                    channelAdapter.notifyDataSetChanged();
                } else {
                    channelAdapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.channel_arr_1));
                    channelAdapter
                        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mChannel.setAdapter(channelAdapter);
                    channelAdapter.notifyDataSetChanged();
                }
                mWifiRX.bandwidth = this.getResources().getStringArray(
                    R.array.bandwidth_int_arr)[position];
                mBandWidthPosition = position;
            }
        } else if (parent == mChannel) {
            if (mBandPosition == 0) {
                mWifiRX.channel = this.getResources().getStringArray(R.array.channel_arr_0)[position];
                mChannelPosition = position;
            } else {
                if (mBandWidthPosition == 2) {
                    mWifiRX.channel = this.getResources().getStringArray(R.array.channel_arr_2)[position];
                    mChannelPosition = position;
                }
                mWifiRX.channel = this.getResources().getStringArray(R.array.channel_arr_1)[position];
                mChannelPosition = position;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    class WifiRXHandler extends Handler {
        public WifiRXHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Message uiMessage = null;
            switch (msg.what) {
                case INIT_TEST_STATUS:
                    if (!mInsmodeSuccessByEUT) {
                        if (WifiEUTHelper.getHelper().insMod()) {
                            mInsmodeSuccessByRX = true;
                            uiMessage = mHandler.obtainMessage(INIT_VIEW, 1, 0);
                            mHandler.sendMessage(uiMessage);
                            // initWifi();
                        } else {
                            uiMessage = mHandler.obtainMessage(INIT_VIEW, 0, 0);
                            mHandler.sendMessage(uiMessage);
                        }
                    } else {
                        uiMessage = mHandler.obtainMessage(INIT_VIEW, 1, 0);
                        mHandler.sendMessage(uiMessage);
                        // initWifi();
                    }
                    break;
                case DEINIT_TEST_STATUS:
                    if (mInsmodeSuccessByRX && !WifiEUTHelper.getHelper().removeMod()) {
                        uiMessage = mHandler.obtainMessage(DEINIT_VIEW);
                        mHandler.sendMessage(uiMessage);
                    } else {
                        mInsmodeSuccessByRX = false;
                        finish();
                    }
                    break;
                case WIFI_RX_GO:
                    if (WifiEUTHelper.getHelper().rxStart(mWifiRX)) {
                        uiMessage = mHandler.obtainMessage(GO_ALERT, 1, 0);
                    } else {
                        uiMessage = mHandler.obtainMessage(GO_ALERT, 0, 0);
                    }
                    mHandler.sendMessage(uiMessage);
                    break;
                case WIFI_RX_STOP:
                    //analysisRXResult(WifiEUTHelper.getHelper().wifiRXResult());
                    if (WifiEUTHelper.getHelper().rxStop()) {
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
     * refresh UI Handler
     */
    public Handler mHandler = new Handler() {

        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg == null) {
                Log.d(TAG, "UI Message is null");
                return;
            }

            /*
             * if(mProgress != null) { mProgress.dismiss(); mProgress = null; }
             */

            switch (msg.what) {
                case INIT_VIEW:
                    if (msg.arg1 == 1) {
                        mWifiRX = new WifiRX();
                        enabledView();
                        refreshUI();
                    } else {
                        mAlertDialog.setMessage("Wifi Init Fail");
                        if (!isFinishing()) {
                            mAlertDialog.show();
                        }
                    }
                    break;
                case DEINIT_VIEW:
                    mAlertDialog.setMessage("Wifi Deinit Fail");
                    if (!isFinishing()) {
                        mAlertDialog.show();
                    }
                    break;
                case GO_ALERT:
                    if (msg.arg1 == 1) {
                        mCmdAlertDialog.setMessage("Wifi EUT RX Go Success");
                        mGo.setEnabled(false);
                        mStop.setEnabled(true);
                    } else {
                        mCmdAlertDialog.setMessage("Wifi EUT RX Go Fail");
                    }
                    if (!isFinishing()) {
                        mCmdAlertDialog.show();
                    }
                    break;
                case STOP_ALERT:
                    if (msg.arg1 == 1) {
                        mCmdAlertDialog.setMessage("Wifi EUT RX Stop Success");
                        mGo.setEnabled(true);
                        mStop.setEnabled(false);
                    } else {
                        mCmdAlertDialog.setMessage("Wifi EUT RX Stop Fail");
                    }
                    Log.d(TAG, "STOP_ALERT");
                    if (!isFinishing()) {
                        mCmdAlertDialog.show();
                    }
                    analysisRXResult(WifiEUTHelper.getHelper().rxResult());
                    break;
                case REFRESH_VIEW:
                    mRXOkResult.setText(mOkResult);
                    mPERResult.setText(mRXPerResult);
                    break;
                default:
                    break;
            }
        }
    };

    private void initUI() {
        mTestRXNum = (EditText) findViewById(R.id.wifi_eut_text_rx_num);
        mTestRXNum.addTextChangedListener(new TextWatcher() {
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
                if (mWifiRX != null) {
                    mWifiRX.rxtestnum = mTestRXNum.getText().toString();
                }
            }
        });

        mRXOkResult = (TextView) findViewById(R.id.wifi_eut_rx_ok_result);
        mPERResult = (TextView) findViewById(R.id.wifi_eut_per_result);

        mBand = (Spinner) findViewById(R.id.wifi_eut_band);
        mBandTitle = (TextView) findViewById(R.id.wifi_eut_bandTitle);

        mBandWidth = (Spinner) findViewById(R.id.wifi_eut_band_width);
        mBandWidthTitle = (TextView) findViewById(R.id.wifi_eut_bandWidthTitle);
        mPathText = (TextView) findViewById(R.id.wifi_rx_path_text);

        mGo = (Button) findViewById(R.id.wifi_rx_go);
        mStop = (Button) findViewById(R.id.wifi_rx_stop);

        mGo.setOnClickListener(this);
        mStop.setOnClickListener(this);
        mStop.setEnabled(false);
    }

    /**
     * init WifiTX View(marlin)
     */
    private void initUIMarlin() {
        mBand.setVisibility(View.GONE);
        mBandTitle.setVisibility(View.GONE);
        Log.d(TAG, " initUIMarlin");
        mChannel = (Spinner) findViewById(R.id.wifi_eut_channel);
        if (Const.isMarlin3() || Const.isMarlin3Lite() || Const.isMarlin3E()) {
            channelAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, getResources()
                            .getStringArray(R.array.channel_freqnote_str_arr_3));
        } else {
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

        mBandCBW= (Spinner) findViewById(R.id.wifi_eut_band_cbw);
        bandCBWAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.bandwidth_str_arr));
        bandCBWAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBandCBW.setAdapter(bandCBWAdapter);
        mBandCBW.setOnItemSelectedListener(this);

        mBandSBW= (Spinner) findViewById(R.id.wifi_eut_band_sbw);
        bandSBWAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.bandsbw_str_arr));
        bandSBWAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBandSBW.setAdapter(bandSBWAdapter);
        mBandSBW.setOnItemSelectedListener(this);
        mPath = (Spinner) findViewById(R.id.wifi_rx_path);
        pathAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                        .getStringArray(R.array.rf_path_str_arr));
        pathAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPath.setAdapter(pathAdapter);
        mPath.setOnItemSelectedListener(this);

        mStandrard = (Spinner) findViewById(R.id.wifi_eut_standard);

        mBandWidth.setVisibility(View.GONE);
        mBandWidthTitle.setVisibility(View.GONE);

        mPathTableRow = (TableRow) findViewById(R.id.wifi_eut_path_tr);
        mCbwTableRow = (TableRow) findViewById(R.id.wifi_eut_cbw_tr);
        mSbwTableRow = (TableRow) findViewById(R.id.wifi_eut_sbw_tr);
        mOffsetTableRow = (TableRow) findViewById(R.id.wifi_eut_offset_tr);

        standrardAdapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_item, getResources()
                .getStringArray(R.array.rf_standrard_str_arr));

        if (Const.isMarlin2()) {
            standrardAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                .getStringArray(R.array.rf_standrard_str_arr_marlin2));
            mPathTableRow.setVisibility(View.GONE);
            mCbwTableRow.setVisibility(View.GONE);
            mSbwTableRow.setVisibility(View.GONE);
            mOffsetTableRow.setVisibility(View.GONE);
        } else if (Const.isMarlin3Lite()) {
            mPathTableRow.setVisibility(View.GONE);
        }
        standrardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mStandrard.setAdapter(standrardAdapter);
        mStandrard.setOnItemSelectedListener(this);
    }

    /**
     * init WifiTX View(brcm)
     */
    private void initUIBrcm() {
        Log.d(TAG, "initUIBrcm start");
        Log.d(TAG, "initUIBrcm band");
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

        Log.d(TAG, "initUIBrcm bandwidth");
        mBandWidth = (Spinner) findViewById(R.id.wifi_eut_band_width);
        bandWidthAdapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_item, getResources()
            .getStringArray(R.array.bandwidth_str_arr_0));
        bandWidthAdapter
            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBandWidth.setAdapter(bandWidthAdapter);
        mBandWidth.setOnItemSelectedListener(this);
        Log.d(TAG, "initUIBrcm end");
    }


    /**
     * when insmodeWifi success or others,all view shoule enable
     */
    private void enabledView() {
        mChannel.setEnabled(true);
        mOffset.setEnabled(true);
        if (Const.isMarlin3()) {
            mPath.setEnabled(true);
            mBandCBW.setEnabled(true);
            mBandSBW.setEnabled(true);
            mStandrard.setEnabled(true);
        } else if (Const.isMarlin3Lite() || Const.isMarlin3E()) {
            mBandCBW.setEnabled(true);
            mBandSBW.setEnabled(true);
            mStandrard.setEnabled(true);
        }
        mTestRXNum.setEnabled(true);
        mRXOkResult.setEnabled(true);
        mPERResult.setEnabled(true);
        mGo.setEnabled(true);
        mStop.setEnabled(true);
    }

    /**
     * display the setting param which setting in the last time
     */
    private void refreshUI() {
        try {
            if (Const.isMarlin()) {
                mChannelPosition = mPref.getInt(CHANNEL, 0);
                mOffsetPosition = mPref.getInt(OFFSET, 0);
                mTestRXNum.setText(mPref.getString(TEST_NUM, "0"));
            }
            if (Const.isMarlin3() || Const.isMarlin3Lite() || Const.isMarlin3E()) {
                mBandSBWPosition = mPref.getInt(BAND_SBW, 0);
                mBandCBWPosition = mPref.getInt(BAND_CBW, 0);
                mStandrardPosition = mPref.getInt(STANDRARD, 0);
                mBandCBW.setSelection(mBandCBWPosition);
                mBandSBW.setSelection(mBandSBWPosition);
                mStandrard.setSelection(mStandrardPosition);
                mPathPosition = mPref.getInt(PATH, 0);
                mPath.setSelection(mPathPosition);
            }
            mChannel.setSelection(0);
            mOffset.setSelection(0);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        Log.d(TAG, " mBandSBWPosition: " + mBandSBWPosition);
        Log.d(TAG, " mBandCBWPosition: " + mBandCBWPosition);
        Log.d(TAG, " mStandrardPosition: " + mStandrardPosition);
        Log.d(TAG, " mChannelPosition: " + mChannelPosition);
        Log.d(TAG, " mOffsetPosition: " + mOffsetPosition);
    }

    /**
     * get setting param when click button start/go
     */
    private void getSettingParam() {
        if (Const.isMarlin()) {
            if (mWifiRX.channel == null || mWifiRX.channel.trim().equals("")) {
                mWifiRX.channel = this.getResources().getStringArray(R.array.channel_freqnote_int_arr_0)[mChannelPosition];
            }
        } else {
            getSettingParamBrcm();
        }
        if (Const.isMarlin3() || Const.isMarlin3Lite() || Const.isMarlin3E()) {
            mWifiRX.path = this.getResources().getStringArray(R.array.rf_path_str_arr)[mPathPosition];
        }
        mWifiRX.rxtestnum = mTestRXNum.getText().toString();
        Log.d(TAG, "Now testing RX in WifiEUT, the setting param is: \n channel " + mWifiRX.channel
            + "\n test_num is " + mWifiRX.rxtestnum);
    }

    private void getSettingParamBrcm() {
        mWifiRX.band = this.getResources().getStringArray(R.array.band_int_arr)[mBandPosition];
        if (mBandPosition == 0) {
            mWifiRX.bandwidth = this.getResources().getStringArray(R.array.bandwidth_int_arr_0)[mBandWidthPosition];
            mWifiRX.channel = this.getResources().getStringArray(R.array.channel_arr_0)[mChannelPosition];
        } else {
            mWifiRX.bandwidth = this.getResources().getStringArray(R.array.bandwidth_int_arr)[mBandWidthPosition];
            mWifiRX.channel = this.getResources().getStringArray(R.array.channel_arr_1)[mChannelPosition];
            if (mBandWidthPosition == 2) {
                mWifiRX.channel = this.getResources().getStringArray(R.array.channel_arr_2)[mChannelPosition];
            }
        }
    }

    private void analysisRXResult(String result) {
        Message uiMessage;
        Log.d(TAG, "analysisRXResult" + result);
        if (result != null) {
            String[] str = result.split("\\:");
            if (str.length < 3) {
                Log.d(TAG, "AT Result is error");
                mOkResult = "error";
                mRXPerResult = "error";
            } else {
                String[] str1 = str[2].split("\\ ");
                for (int i = 0; i < str1.length; i++) {
                    Log.d(TAG, "str is " + str1[i]);
                    if (str1[i].contains("rx_end_count")) {
                        String[] str2 = str1[i].split("\\=");
                        mOkResult = str2[1];
                        String val1 = mTestRXNum.getText().toString();
                        if (TextUtils.isEmpty(val1)) {
                            val1 = "0";
                        }
                        if (TextUtils.isEmpty(mOkResult)) {
                            Log.d(TAG, "mOkResult is null");
                            return;
                        }
                        int a = Integer.valueOf(val1) - Integer.valueOf(mOkResult);
                        int b = Integer.valueOf(val1);
                        NumberFormat numberFormat = NumberFormat.getInstance();
                        numberFormat.setMaximumFractionDigits(2);
                        mRXPerResult = numberFormat.format((float) a / (float) b * 100) + "%";
                    }
                }
            }
        } else {
            Log.d(TAG, "AT Result is null");
            mOkResult = "null";
            mRXPerResult = "null";
        }
        uiMessage = mHandler.obtainMessage(REFRESH_VIEW);
        mHandler.sendMessage(uiMessage);
    }
}
