package com.sprd.engineermode.telephony.netinfo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.intf.ITelephonyApi;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NrShowActivity extends Activity {

    private static final String TAG = "NrShowActivity";

    private static final String LTE_INDEX = "PrefenceIndex";
    private static final String KEY_SIM_INDEX = "simindex";
    private static final String NETWORK_STAT_CHANGE = "com.sprd.network.NETWORK_STAT_CHANGE";
    private static final String NETWORK_TYPE = "NetWorkType";

    private static final int NETWORK_GSM = 1;
    private static final int NETWORK_TDSCDMA = 2;
    private static final int NETWORK_WCDMA = 3;
    private static final int NETWORK_LTE = 4;

    private static final int KEY_SERVING = 0;
    private static final int KEY_ADJACENT = 1;
    private static final int KEY_BETWEEN_ADJACENT_5G = 2;
    private static final int KEY_OUTFIELD_NETWORK = 3;

    private static final int GET_SERVING = 0;
    private static final int GET_ADJACENT = 1;
    private static final int GET_BETWEEN_ADJACENT_4G = 2;
    private static final int GET_OUTFIELD_NETWORK = 3;

    private ITelephonyApi teleApi = CoreApi.getTelephonyApi();
    private String SaveName = "atchannel";
    private int mCheckId;
    private int mSimIndex;
    private int mSubId;

    private Timer mTimer;
    private LteHandler mLteHandler;
    private Handler mUiThread = new Handler();
    private static final Object mLock = new Object();

    private ListView listView;
    private ListView mlistviewOperators;
    TextView[][] mtextView;
    private String[][] mTextValue;
    private int[][] viewID;
    public int ROW;
    public int COL;
    public int mTemp;
    private String[] values;
    ArrayAdapter<String> adapter = null;

    public boolean isRoaming;
    public String mNetworkOperator;
    public String mNetworkOperatorName;

    private TelephonyManager mTelephonyManager;

    private int[] activityLab = new int[] { R.string.netinfo_server_cell,
            R.string.netinfo_adjacent_cell_4g,
            //R.string.netinfo_adjacent_cell_3g,
            R.string.netinfo_adjacent_cell_5g,
            R.string.netinfo_outfield_information };
    private BroadcastReceiver mNetWorkReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String mIntentAction;
            int netWorkStat = intent.getIntExtra(NETWORK_TYPE, 0);
            int phoneId = intent.getIntExtra(KEY_SIM_INDEX, -1);
            Log.d(TAG, "NetWork state is " + netWorkStat);
            synchronized (mLock) {
                if ((netWorkStat != NETWORK_LTE) && (phoneId == mSimIndex) ) {
                    switch (netWorkStat) {
                    case NETWORK_GSM:
                        mIntentAction = "android.engineermode.action.GSMSHOW";
                        break;
                    case NETWORK_TDSCDMA:
                        mIntentAction = "android.engineermode.action.TDSCDMASHOW";
                        break;
                    case NETWORK_WCDMA:
                        mIntentAction = "android.engineermode.action.WCDMASHOW";
                        break;
                    default:
                        return;
                    }
                    unregisterReceiver();
                    if (mTimer != null) {
                        mTimer.cancel();
                    }
                    Bundle data = new Bundle();
                    data.putInt("PrefenceIndex", mCheckId);
                    data.putInt("SubId", mSubId);
                    data.putInt(KEY_SIM_INDEX, mSimIndex);
                    Intent mIntent = new Intent(mIntentAction);
                    mIntent.putExtras(data);
                    startActivity(mIntent);
                    finish();
                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = this.getIntent();
        Bundle data = intent.getExtras();
        if(data == null){
            return;
        }
        mCheckId = data.getInt(LTE_INDEX, -1);
        mSimIndex = data.getInt(KEY_SIM_INDEX, -1);
        mSubId = data.getInt("SubId", -1);
        Log.d(TAG, "mSimIndex is: " + mSimIndex + "mCheckId is: " + mCheckId
                + "SubId: " + mSubId);
        registerReceiver();
        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mLteHandler = new LteHandler(ht.getLooper());
        if (mCheckId != -1 && mSimIndex != -1) {
            mTelephonyManager = (TelephonyManager) TelephonyManager
                    .from(NrShowActivity.this);
            mNetworkOperatorName = mTelephonyManager.getNetworkOperatorName(mSubId);
            mNetworkOperator = mTelephonyManager.getNetworkOperatorForPhone(mSimIndex);
            isRoaming = mTelephonyManager.isNetworkRoaming(mSubId);
            Log.d(TAG, "NetworkOperatorName: " + mNetworkOperatorName
                    + "NetworkOperator: " + mNetworkOperator + "isRoaming: "
                    + isRoaming);
            setTitle(activityLab[mCheckId]);
            SaveName = "atchannel" + mSimIndex;
            switch (mCheckId) {
            case KEY_SERVING:
                setContentView(R.layout.netinfo_listview_show);
                listView = (ListView) findViewById(R.id.netinfo_listview);
                listView.setItemsCanFocus(true);
                values = this.getResources().getStringArray(R.array.nr_sering_name);
                adapter = new ArrayAdapter<String>(NrShowActivity.this, R.layout.array_item, values);
                listView.setAdapter(adapter);
                mlistviewOperators = (ListView) findViewById(R.id.netinfo_operators_listview);
                String[] values = this.getResources().getStringArray(R.array.operators_sering_name);
                if (mNetworkOperator != null) {
                    values[0] = values[0] + ": " + mNetworkOperator;
                } else {
                    values[0] = values[0] + ": NA";
                }
                if (mNetworkOperatorName != null) {
                    values[1] = values[1] + ": " + mNetworkOperatorName;
                } else {
                    values[1] = values[1] + ": NA";
                }
                if (isRoaming) {
                    values[2] = values[2] + ": " + getResources().getString(R.string.is_roaming);
                } else {
                    values[2] = values[2] + ": " + getResources().getString(R.string.non_roaming);
                }
                ArrayAdapter<String> operatorsAdapter = new ArrayAdapter<String>(
                        NrShowActivity.this, R.layout.array_item, values);
                mlistviewOperators.setAdapter(operatorsAdapter);
                Message serving = mLteHandler.obtainMessage(GET_SERVING);
                mLteHandler.sendMessage(serving);
                break;
            case KEY_ADJACENT:
                setContentView(R.layout.netinfo_between_adjacent_5g);
                TextView mTextViewPsc = (TextView) (NrShowActivity.this
                        .findViewById(R.id.netinfo_adjacent_5g_label02));
                mTextViewPsc.setText(R.string.netinfo_pci);
                ROW = 5;
                COL = 4;
                mtextView = new TextView[ROW][COL];
                mTextValue = new String[ROW][COL];
                viewID = new int[ROW][COL];
                mTemp = R.id.netinfo_adjacent_5g_label11;

                viewID[0][0] = R.id.netinfo_adjacent_5g_label11;
                viewID[0][1] = R.id.netinfo_adjacent_5g_label12;
                viewID[0][2] = R.id.netinfo_adjacent_5g_label13;
                viewID[0][3] = R.id.netinfo_adjacent_5g_label14;

                viewID[1][0] = R.id.netinfo_adjacent_5g_label21;
                viewID[1][1] = R.id.netinfo_adjacent_5g_label22;
                viewID[1][2] = R.id.netinfo_adjacent_5g_label23;
                viewID[1][3] = R.id.netinfo_adjacent_5g_label24;

                viewID[2][0] = R.id.netinfo_adjacent_5g_label31;
                viewID[2][1] = R.id.netinfo_adjacent_5g_label32;
                viewID[2][2] = R.id.netinfo_adjacent_5g_label33;
                viewID[2][3] = R.id.netinfo_adjacent_5g_label34;

                viewID[3][0] = R.id.netinfo_adjacent_5g_label41;
                viewID[3][1] = R.id.netinfo_adjacent_5g_label42;
                viewID[3][2] = R.id.netinfo_adjacent_5g_label43;
                viewID[3][3] = R.id.netinfo_adjacent_5g_label44;

                viewID[4][0] = R.id.netinfo_adjacent_5g_label51;
                viewID[4][1] = R.id.netinfo_adjacent_5g_label52;
                viewID[4][2] = R.id.netinfo_adjacent_5g_label53;
                viewID[4][3] = R.id.netinfo_adjacent_5g_label54;

                for (int i = 0; i < ROW; i++) {
                    for (int j = 0; j < COL; j++) {
                        mtextView[i][j] = (TextView) (NrShowActivity.this
                                .findViewById(viewID[i][j]));
                    }
                }
                Message adjacent = mLteHandler.obtainMessage(GET_ADJACENT);
                mLteHandler.sendMessage(adjacent);
                break;
            case KEY_BETWEEN_ADJACENT_5G:
                setContentView(R.layout.netinfo_between_adjacent_5g);
                TextView mTextViewPsc_5g = (TextView) (NrShowActivity.this
                        .findViewById(R.id.netinfo_adjacent_5g_label02));
                mTextViewPsc_5g.setText(R.string.netinfo_pci);
                ROW = 5;
                COL = 4;
                mtextView = new TextView[ROW][COL];
                mTextValue = new String[ROW][COL];
                viewID = new int[ROW][COL];
                mTemp = R.id.netinfo_adjacent_5g_label11;

                viewID[0][0] = R.id.netinfo_adjacent_5g_label11;
                viewID[0][1] = R.id.netinfo_adjacent_5g_label12;
                viewID[0][2] = R.id.netinfo_adjacent_5g_label13;
                viewID[0][3] = R.id.netinfo_adjacent_5g_label14;

                viewID[1][0] = R.id.netinfo_adjacent_5g_label21;
                viewID[1][1] = R.id.netinfo_adjacent_5g_label22;
                viewID[1][2] = R.id.netinfo_adjacent_5g_label23;
                viewID[1][3] = R.id.netinfo_adjacent_5g_label24;

                viewID[2][0] = R.id.netinfo_adjacent_5g_label31;
                viewID[2][1] = R.id.netinfo_adjacent_5g_label32;
                viewID[2][2] = R.id.netinfo_adjacent_5g_label33;
                viewID[2][3] = R.id.netinfo_adjacent_5g_label34;

                viewID[3][0] = R.id.netinfo_adjacent_5g_label41;
                viewID[3][1] = R.id.netinfo_adjacent_5g_label42;
                viewID[3][2] = R.id.netinfo_adjacent_5g_label43;
                viewID[3][3] = R.id.netinfo_adjacent_5g_label44;

                viewID[4][0] = R.id.netinfo_adjacent_5g_label51;
                viewID[4][1] = R.id.netinfo_adjacent_5g_label52;
                viewID[4][2] = R.id.netinfo_adjacent_5g_label53;
                viewID[4][3] = R.id.netinfo_adjacent_5g_label54;

                for (int i = 0; i < ROW; i++) {
                    for (int j = 0; j < COL; j++) {
                        mtextView[i][j] = (TextView) (NrShowActivity.this
                                .findViewById(viewID[i][j]));
                    }
                }
                break;
            case KEY_OUTFIELD_NETWORK:
                setContentView(R.layout.netinfo_outfield_show);
                listView = (ListView) findViewById(R.id.netinfo_outfield_listview);

                Message outfieldNetwork = mLteHandler
                        .obtainMessage(GET_OUTFIELD_NETWORK);
                mLteHandler.sendMessage(outfieldNetwork);
                break;
            }
        } else {
            if (mLteHandler != null) {
                mLteHandler.getLooper().quit();
            }
            return;
        }
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                switch (mCheckId) {
                case KEY_SERVING:
                    Message serving = mLteHandler.obtainMessage(GET_SERVING);
                    mLteHandler.sendMessage(serving);
                    break;
                case KEY_ADJACENT:
                    Message adjacent = mLteHandler.obtainMessage(GET_ADJACENT);
                    mLteHandler.sendMessage(adjacent);
                    break;
                case KEY_BETWEEN_ADJACENT_5G:
                    Message adjacent_5g = mLteHandler
                            .obtainMessage(KEY_BETWEEN_ADJACENT_5G);
                    mLteHandler.sendMessage(adjacent_5g);
                    break;
                case KEY_OUTFIELD_NETWORK:
                    Message outfieldNetwork = mLteHandler
                            .obtainMessage(GET_OUTFIELD_NETWORK);
                    mLteHandler.sendMessage(outfieldNetwork);
                    break;
                }
            }
        }, 0, 200);
    }

    public void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(NETWORK_STAT_CHANGE);
        registerReceiver(mNetWorkReceiver, filter);
    }

    public void unregisterReceiver() {
        try {
            unregisterReceiver(mNetWorkReceiver);
        } catch (IllegalArgumentException iea) {

        }
    }

    @Override
    protected void onDestroy() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        if (mLteHandler != null) {
            mLteHandler.getLooper().quit();
        }
        unregisterReceiver();
        super.onDestroy();
    }

    public void TextValuesDisplay(int cmdType) {
        String result = null;
        int temp;
        switch (cmdType) {
        case KEY_ADJACENT:
            teleApi.netInfoNrApi().getAdjacentCell(mSimIndex, mTextValue);
            break;
        case KEY_BETWEEN_ADJACENT_5G:
            teleApi.netInfoNrApi().getBetweenAdjacentCell5G(mSimIndex, mTextValue);
            break;
        }
        mUiThread.post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < ROW; i++) {
                    for (int j = 0; j < COL; j++) {
                        mtextView[i][j].setText(mTextValue[i][j]);
                    }
                }
            }
        });
    }

    public void valuesDisplay(int cmdType) {
        String result = null;
        final String[] tmpValues;
        int temp;
        String strTemp = null;
        int splitPoint=7;
        if (cmdType == KEY_SERVING) {
            tmpValues = this.getResources()
                    .getStringArray(R.array.nr_sering_name);

            teleApi.netInfoNrApi().getServingCell(mSimIndex, tmpValues, values);

            mUiThread.post(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        } else if (cmdType == KEY_OUTFIELD_NETWORK) {
            values = this.getResources().getStringArray(R.array.nr_outfield_network);
            List<String> list = teleApi.netInfoNrApi().getOutfieldNetworkInfo(mSimIndex, values);
            mUiThread.post(new Runnable() {
                @Override
                public void run() {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            NrShowActivity.this, R.layout.array_item, list);
                    listView.setAdapter(adapter);
                }
            });
        }
    }

    class LteHandler extends Handler {
        public LteHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case GET_SERVING:
                valuesDisplay(mCheckId);
                break;
            case GET_ADJACENT:
                TextValuesDisplay(mCheckId);
                break;
            case GET_BETWEEN_ADJACENT_4G:
                TextValuesDisplay(mCheckId);
                break;
            case GET_OUTFIELD_NETWORK:
                valuesDisplay(mCheckId);
                break;
            }
        }
    }
}
