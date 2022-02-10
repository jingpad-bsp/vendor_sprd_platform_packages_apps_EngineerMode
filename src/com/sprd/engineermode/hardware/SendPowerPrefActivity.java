
package com.sprd.engineermode.hardware;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Button;
import android.view.View;
import android.os.HandlerThread;
import android.os.Looper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.intf.ITelephonyApi.IBand.WcdmaBand;
import com.unisoc.engineermode.core.intf.ITelephonyApi.IRadioFunc.Band;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SendPowerPrefActivity extends Activity {
    private static final String TAG = "SendPowerPrefActivity";

    /*
    private static final int SET_GSM850_MAX_POWER = 0;
    private static final int CLEAR_GSM850_MAX_POWER = 1;
    private static final int SET_EGSM900_MAX_POWER = 2;
    private static final int CLEAR_EGSM900_MAX_POWER = 3;
    private static final int SET_DCS1800_MAX_POWER = 4;
    private static final int CLEAR_DCS1800_MAX_POWER = 5;
    private static final int SET_PCS1900_MAX_POWER = 6;
    private static final int CLEAR_PCS1900_MAX_POWER = 7;
    private static final int SET_TD19_MAX_POWER = 8;
    private static final int CLEAR_TD19_MAX_POWER = 9;
    private static final int SET_TD21_MAX_POWER = 10;
    private static final int CLEAR_TD21_MAX_POWER = 11;
    private static final int SET_WBand1_MAX_POWER = 12;
    private static final int CLEAR_WBand1_MAX_POWER = 13;
    private static final int SET_WBand2_MAX_POWER = 14;
    private static final int CLEAR_WBand2_MAX_POWER = 15;
    private static final int SET_WBand5_MAX_POWER = 16;
    private static final int CLEAR_WBand5_MAX_POWER = 17;
    private static final int SET_WBand8_MAX_POWER = 18;
    private static final int CLEAR_WBand8_MAX_POWER = 19;
    */
    private static final int SET_MAX_POWER = 0;
    private static final int CLEAR_MAX_POWER = 1;



    private EditText mGsm850 = null;
    private EditText mEgsm900 = null;
    private EditText mDcs1800 = null;
    private EditText mPcs1900 = null;
    private EditText mTD19 = null;
    private EditText mTD21 = null;
    private EditText mWBand1 = null;
    private EditText mWBand2 = null;
    private EditText mWBand5 = null;
    private EditText mWBand8 = null;
    private Handler mSetMaxPowerHandler = null;
    private Handler mUiHandler = new Handler();

    private ITelephonyApi teleApi = CoreApi.getTelephonyApi();
    private boolean isSupportTD = false;
    private boolean isSupportWCD = false;

    private final static String ORI_WCDMA_BAND = "ORI_WCDMA_BAND";
    private static final int[] mWBandMap = {
            1, 2, 5, 8
    };
    private static final int[] mWBandET = {
            R.id.et_wband1, R.id.et_wband2,
            R.id.et_wband5, R.id.et_wband8
    };
    private static final int[] mWBandOK = {
            R.id.ok_wband1, R.id.ok_wband2,
            R.id.ok_wband5, R.id.ok_wband8
    };
    private static final int[] mWBandClear = {
            R.id.clear_wband1, R.id.clear_wband2,
            R.id.clear_wband5, R.id.clear_wband8
    };
    private static final int[] mWBandText = {
            R.id.text_wband1, R.id.text_wband2,
            R.id.text_wband5, R.id.text_wband8
    };

    //private int mWCDMASupportBand = -1;
    private List<WcdmaBand> supportedWCDMABands = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.send_power_set);

        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mSetMaxPowerHandler = new SetMaxPowerHandler(ht.getLooper());

        mGsm850 = (EditText) findViewById(R.id.et_gsm850);
        mEgsm900 = (EditText) findViewById(R.id.et_egsm900);
        mDcs1800 = (EditText) findViewById(R.id.et_dcs1800);
        mPcs1900 = (EditText) findViewById(R.id.et_pcs1900);
        mTD19 = (EditText) findViewById(R.id.et_td19);
        mTD21 = (EditText) findViewById(R.id.et_td21);
        mWBand1 = (EditText) findViewById(R.id.et_wband1);
        mWBand2 = (EditText) findViewById(R.id.et_wband2);
        mWBand5 = (EditText) findViewById(R.id.et_wband5);
        mWBand8 = (EditText) findViewById(R.id.et_wband8);
        isSupportTD = teleApi.telephonyInfo().isSupportTdscdma();
        isSupportWCD = teleApi.telephonyInfo().isSupportWcdma();
        supportedWCDMABands = loadBands();
        Button buttonOk = null;
        buttonOk = (Button) findViewById(R.id.ok_gsm850);
        buttonOk.setText("Set");
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGsm850.getText().toString().equals("")) {
                    Toast.makeText(SendPowerPrefActivity.this, "please input data",
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Message msg = mSetMaxPowerHandler.obtainMessage(SET_MAX_POWER,
                            Band.GSM850.getValue(), Integer.valueOf(mGsm850.getText().toString()));
                    mSetMaxPowerHandler.sendMessage(msg);
                }
            }
        });
        buttonOk = (Button) findViewById(R.id.ok_egsm900);
        buttonOk.setText("Set");
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEgsm900.getText().toString().equals("")) {
                    Toast.makeText(SendPowerPrefActivity.this, "please input data",
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Message msg = mSetMaxPowerHandler.obtainMessage(SET_MAX_POWER, Band.GSM900.getValue(),
                            Integer.valueOf(mEgsm900.getText().toString()));
                    mSetMaxPowerHandler.sendMessage(msg);
                }
            }
        });
        buttonOk = (Button) findViewById(R.id.ok_dcs1800);
        buttonOk.setText("Set");
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDcs1800.getText().toString().equals("")) {
                    Toast.makeText(SendPowerPrefActivity.this, "please input data",
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Message msg = mSetMaxPowerHandler.obtainMessage(SET_MAX_POWER, Band.DCS1800.getValue(),
                            Integer.valueOf(mDcs1800.getText().toString()));
                    mSetMaxPowerHandler.sendMessage(msg);
                }
            }
        });
        buttonOk = (Button) findViewById(R.id.ok_pcs1900);
        buttonOk.setText("Set");
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPcs1900.getText().toString().equals("")) {
                    Toast.makeText(SendPowerPrefActivity.this, "please input data",
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Message msg = mSetMaxPowerHandler.obtainMessage(SET_MAX_POWER, Band.DCS1900.getValue(),
                            Integer.valueOf(mPcs1900.getText().toString()));
                    mSetMaxPowerHandler.sendMessage(msg);
                }
            }
        });
        buttonOk = (Button) findViewById(R.id.ok_td19);
        buttonOk.setText("Set");
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTD19.getText().toString().equals("")) {
                    Toast.makeText(SendPowerPrefActivity.this, "please input data",
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Message msg = mSetMaxPowerHandler.obtainMessage(SET_MAX_POWER, Band.TD19.getValue(),
                            Integer.valueOf(mTD19.getText().toString()));
                    mSetMaxPowerHandler.sendMessage(msg);
                }
            }
        });
        buttonOk = (Button) findViewById(R.id.ok_td21);
        buttonOk.setText("Set");
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTD21.getText().toString().equals("")) {
                    Toast.makeText(SendPowerPrefActivity.this, "please input data",
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Message msg = mSetMaxPowerHandler.obtainMessage(SET_MAX_POWER, Band.TD21.getValue(),
                            Integer.valueOf(mTD21.getText().toString()));
                    mSetMaxPowerHandler.sendMessage(msg);
                }
            }
        });
        buttonOk = (Button) findViewById(R.id.ok_wband1);
        buttonOk.setText("Set");
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWBand1.getText().toString().equals("")) {
                    Toast.makeText(SendPowerPrefActivity.this,
                            "please input data", Toast.LENGTH_SHORT).show();
                } else {
                    Message msg = mSetMaxPowerHandler.obtainMessage(
                            SET_MAX_POWER,
                            Band.WBAND1.getValue(),
                            Integer.valueOf(mWBand1.getText().toString()));
                    mSetMaxPowerHandler.sendMessage(msg);
                }
            }
        });
        buttonOk = (Button) findViewById(R.id.ok_wband2);
        buttonOk.setText("Set");
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWBand2.getText().toString().equals("")) {
                    Toast.makeText(SendPowerPrefActivity.this,
                            "please input data", Toast.LENGTH_SHORT).show();
                } else {
                    Message msg = mSetMaxPowerHandler.obtainMessage(
                            SET_MAX_POWER,
                            Band.WBAND2.getValue(),
                            Integer.valueOf(mWBand2.getText().toString()));
                    mSetMaxPowerHandler.sendMessage(msg);
                }
            }
        });
        buttonOk = (Button) findViewById(R.id.ok_wband5);
        buttonOk.setText("Set");
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWBand5.getText().toString().equals("")) {
                    Toast.makeText(SendPowerPrefActivity.this,
                            "please input data", Toast.LENGTH_SHORT).show();
                } else {
                    Message msg = mSetMaxPowerHandler.obtainMessage(
                            SET_MAX_POWER,
                            Band.WBAND5.getValue(),
                            Integer.valueOf(mWBand5.getText().toString()));
                    mSetMaxPowerHandler.sendMessage(msg);
                }
            }
        });
        buttonOk = (Button) findViewById(R.id.ok_wband8);
        buttonOk.setText("Set");
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWBand8.getText().toString().equals("")) {
                    Toast.makeText(SendPowerPrefActivity.this,
                            "please input data", Toast.LENGTH_SHORT).show();
                } else {
                    Message msg = mSetMaxPowerHandler.obtainMessage(
                            SET_MAX_POWER,
                            Band.WBAND8.getValue(),
                            Integer.valueOf(mWBand8.getText().toString()));
                    mSetMaxPowerHandler.sendMessage(msg);
                }
            }
        });

        Button buttonClear = null;
        buttonClear = (Button) findViewById(R.id.clear_gsm850);
        buttonClear.setText("Clear");
        buttonClear.setOnClickListener(new ClearButtonOnClickListener(Band.GSM850, R.id.et_gsm850));

        buttonClear = (Button) findViewById(R.id.clear_egsm900);
        buttonClear.setText("Clear");
        buttonClear.setOnClickListener(new ClearButtonOnClickListener(Band.GSM900, R.id.et_egsm900));

        buttonClear = (Button) findViewById(R.id.clear_dcs1800);
        buttonClear.setText("Clear");
        buttonClear.setOnClickListener(new ClearButtonOnClickListener(Band.DCS1800, R.id.et_dcs1800));

        buttonClear = (Button) findViewById(R.id.clear_pcs1900);
        buttonClear.setText("Clear");
        buttonClear.setOnClickListener(new ClearButtonOnClickListener(Band.DCS1900, R.id.et_pcs1900));

        buttonClear = (Button) findViewById(R.id.clear_td19);
        buttonClear.setText("Clear");
        buttonClear.setOnClickListener(new ClearButtonOnClickListener(Band.TD19, R.id.et_td19));

        buttonClear = (Button) findViewById(R.id.clear_td21);
        buttonClear.setText("Clear");
        buttonClear.setOnClickListener(new ClearButtonOnClickListener(Band.TD21, R.id.et_td21));

        buttonClear = (Button) findViewById(R.id.clear_wband1);
        buttonClear.setText("Clear");
        buttonClear.setOnClickListener(new ClearButtonOnClickListener(Band.WBAND1, R.id.et_wband1));

        buttonClear = (Button) findViewById(R.id.clear_wband2);
        buttonClear.setText("Clear");
        buttonClear.setOnClickListener(new ClearButtonOnClickListener(Band.WBAND2, R.id.et_wband2));

        buttonClear = (Button) findViewById(R.id.clear_wband5);
        buttonClear.setText("Clear");
        buttonClear.setOnClickListener(new ClearButtonOnClickListener(Band.WBAND5, R.id.et_wband5));

        buttonClear = (Button) findViewById(R.id.clear_wband8);
        buttonClear.setText("Clear");
        buttonClear.setOnClickListener(new ClearButtonOnClickListener(Band.WBAND8, R.id.et_wband8));

        if (!isSupportTD) {
            findViewById(R.id.et_td19).setVisibility(View.GONE);
            findViewById(R.id.et_td21).setVisibility(View.GONE);
            findViewById(R.id.ok_td19).setVisibility(View.GONE);
            findViewById(R.id.ok_td21).setVisibility(View.GONE);
            findViewById(R.id.clear_td19).setVisibility(View.GONE);
            findViewById(R.id.clear_td21).setVisibility(View.GONE);
            findViewById(R.id.text_td19).setVisibility(View.GONE);
            findViewById(R.id.text_td21).setVisibility(View.GONE);
        }
        if (!isSupportWCD) {
            findViewById(R.id.et_wband1).setVisibility(View.GONE);
            findViewById(R.id.et_wband2).setVisibility(View.GONE);
            findViewById(R.id.et_wband5).setVisibility(View.GONE);
            findViewById(R.id.et_wband8).setVisibility(View.GONE);
            findViewById(R.id.ok_wband1).setVisibility(View.GONE);
            findViewById(R.id.ok_wband2).setVisibility(View.GONE);
            findViewById(R.id.ok_wband5).setVisibility(View.GONE);
            findViewById(R.id.ok_wband8).setVisibility(View.GONE);
            findViewById(R.id.clear_wband1).setVisibility(View.GONE);
            findViewById(R.id.clear_wband2).setVisibility(View.GONE);
            findViewById(R.id.clear_wband5).setVisibility(View.GONE);
            findViewById(R.id.clear_wband8).setVisibility(View.GONE);
            findViewById(R.id.text_wband1).setVisibility(View.GONE);
            findViewById(R.id.text_wband2).setVisibility(View.GONE);
            findViewById(R.id.text_wband5).setVisibility(View.GONE);
            findViewById(R.id.text_wband8).setVisibility(View.GONE);
        }

        if (isSupportWCD) {
            if (supportedWCDMABands == null || supportedWCDMABands.size() == 0) {
                for (int i = 0; i < mWBandMap.length; i++) {
                    boolean isOpened = true;
                    try {
                        isOpened = teleApi.band().isWcdmaBandOpen(0, WcdmaBand.of(mWBandMap[i]));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (!isOpened) {
                        findViewById(mWBandET[i]).setVisibility(View.GONE);
                        findViewById(mWBandOK[i]).setVisibility(View.GONE);
                        findViewById(mWBandClear[i]).setVisibility(View.GONE);
                        findViewById(mWBandText[i]).setVisibility(View.GONE);
                    }
                }
            } else {
                for (int i = 0; i < mWBandMap.length; i++) {
                    int band = mWBandMap[i];
                    boolean existedFlag = existSupportedWCDMABands(band);
                    if (!existedFlag) {
                        findViewById(mWBandET[i]).setVisibility(View.GONE);
                        findViewById(mWBandOK[i]).setVisibility(View.GONE);
                        findViewById(mWBandClear[i]).setVisibility(View.GONE);
                        findViewById(mWBandText[i]).setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    private boolean existSupportedWCDMABands(int band) {
        if (supportedWCDMABands == null || supportedWCDMABands.size() == 0) {
            return false;
        }
        for (WcdmaBand wBand : supportedWCDMABands) {
            if (wBand.getValue() == band) {
                return true;
            }
        }
        return false;
    }

    private List<WcdmaBand> loadBands() {
        List<WcdmaBand> bandList = new ArrayList<>();
        Log.d(TAG, "loadBands...");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SendPowerPrefActivity.this);
        String saveValue = sp.getString(ORI_WCDMA_BAND, "");
        Log.d(TAG, "saveValue=:" + saveValue);
        if (saveValue.equals("")) {
            return bandList;
        }

        String[] bands = saveValue.split(",");
        for (String band : bands) {
            Log.d(TAG, "load band: " + band);
            bandList.add(WcdmaBand.valueOf(band));
        }
        return bandList;
    }

    private class ClearButtonOnClickListener implements View.OnClickListener {
        private Band band;
        private int editorId;

        public ClearButtonOnClickListener(Band band, int editorId) {
            this.band = band;
            this.editorId = editorId;
        }


        @Override
        public void onClick(View view) {
            Message msg = mSetMaxPowerHandler.obtainMessage(
                    CLEAR_MAX_POWER,
                    band.getValue(), editorId);
            mSetMaxPowerHandler.sendMessage(msg);
        }
    }

    private void clickClearButton(Band band, int editorId) {
        Message msg = mSetMaxPowerHandler.obtainMessage(
                CLEAR_MAX_POWER,
                band.getValue(), editorId);
        mSetMaxPowerHandler.sendMessage(msg);
    }

    private int parseWCDMAResp(String resp) {
        String str[] = resp.split(":|\n|,");
        Log.d(TAG, "parseWCDMAResp() str:" + Arrays.toString(str));
        if (str.length >= 3 && str[3].trim().equals("1")) {
            return 1;
        }
        return 0;
    }

    class SetMaxPowerHandler extends Handler {
        public SetMaxPowerHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Band band = Band.of(msg.arg1);
            boolean isSuccess = false;
            switch (msg.what) {
                case SET_MAX_POWER:
                    try {
                        teleApi.radioFunc().setMaxPower(band, msg.arg2);
                        isSuccess = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
                    break;
                case CLEAR_MAX_POWER:
                    try {
                        teleApi.radioFunc().clearMaxPower(band);
                        isSuccess = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }

                    mUiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            EditText editor = (EditText) findViewById(msg.arg2);
                            if (editor == null) {
                                Log.d(TAG, "editor is null...");
                                return;
                            }
                            editor.setText("");
                        }

                    });
                    break;
                default:
                    return;
            }

            if (isSuccess) {
                Toast.makeText(SendPowerPrefActivity.this, "Success", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SendPowerPrefActivity.this, "Fail", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
