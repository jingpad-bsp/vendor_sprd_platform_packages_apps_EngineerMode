/*This code functions as follows:
 **1, the display of the WCDMA correlation detection result
 **2, by sending AT commands to implement the query
 **3, real-time updates query results
 */
package com.sprd.engineermode.telephony.netinfo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.widget.ListView;
import android.widget.TextView;
import java.lang.String;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.sprd.engineermode.R;
import android.widget.ArrayAdapter;
import android.content.BroadcastReceiver;
import android.telephony.TelephonyManager;
import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.impl.nonpublic.TelephonyManagerProxy;
import com.unisoc.engineermode.core.intf.ITelephonyApi;

public class CdmaDoShowActivity extends Activity {

    private static final String TAG = "CdmaDoShowActivity";
    private static final String PREF_INDEX = "PrefenceIndex";
    private static final String KEY_SIM_INDEX = "simindex";
    private static final String NETWORK_STAT_CHANGE = "com.sprd.network.NETWORK_STAT_CHANGE";
    private static final String NETWORK_TYPE = "NetWorkType";

    private static final int NETWORK_UNKNOW = 0;
    private static final int NETWORK_GSM = 1;
    private static final int NETWORK_TDSCDMA = 2;
    private static final int NETWORK_WCDMA = 3;
    private static final int NETWORK_LTE = 4;

    private static final int KEY_SERVING = 0;
    private static final int KEY_ADJACENT = 1;
    private static final int KEY_BETWEEN_ADJACENT_2G = 9;
    private static final int KEY_BETWEEN_ADJACENT_4G = 2;
    private static final int KEY_OUTFIELD_NETWORK = 3;

    private static final int GET_SERVING = 0;
    private static final int GET_ADJACENT = 1;
    private static final int GET_BETWEEN_ADJACENT_2G = 2;
    private static final int GET_BETWEEN_ADJACENT_4G = 3;
    private static final int GET_OUTFIELD_NETWORK = 4;

    private ITelephonyApi teleApi = CoreApi.getTelephonyApi();
    private String SaveName = "atchannel";
    private int mCheckId;
    private int mSimIndex;
    private int mSubId;

    private Timer mTimer;
    private WcdmaHandler mWcdmaHandler;
    private Handler mUiThread = new Handler();
    private static final Object mLock = new Object();
    private ListView listView;
    private ListView mlistviewOperators;
    TextView[][] mtextView;
    private String[][] mTextValue;
    private int[][] viewID;
    public int ROW;
    public int COL;

    private String[] values;
    ArrayAdapter<String> adapter = null;

    public boolean isRoaming;
    public String mNetworkOperator;
    public String mNetworkOperatorName;

    private TelephonyManager mTelephonyManager;

    private int[] activityLab = new int[] { R.string.netinfo_server_cell,
            R.string.netinfo_neighbour_cell,
            R.string.netinfo_adjacent_cell_4g};

    private BroadcastReceiver mNetWorkReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            int netWorkStat = intent.getIntExtra(NETWORK_TYPE, 0);
            int phoneId = intent.getIntExtra(KEY_SIM_INDEX, -1);
            String mIntentAction;
            Log.d(TAG, "NetWork state is " + netWorkStat);
            synchronized (mLock) {
                if ((netWorkStat != NETWORK_WCDMA) && (phoneId == mSimIndex)) {
                    switch (netWorkStat) {
                    case NETWORK_GSM:
                        mIntentAction = "android.engineermode.action.GSMSHOW";
                        break;
                    case NETWORK_TDSCDMA:
                        mIntentAction = "android.engineermode.action.TDSCDMASHOW";
                        break;
                    case NETWORK_LTE:
                        mIntentAction = "android.engineermode.action.LTESHOW";
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
        mCheckId = data.getInt(PREF_INDEX, -1);
        mSimIndex = data.getInt("simindex", -1);
        mSubId = data.getInt("SubId", -1);
        Log.d(TAG, "mCheckId is: " + mCheckId + "mSimIndex is: " + mSimIndex
                + "SubId: " + mSubId);
        registerReceiver();
        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mWcdmaHandler = new WcdmaHandler(ht.getLooper());
        if (mCheckId != -1 && mSimIndex != -1) {
//            mTelephonyManager = (TelephonyManager) TelephonyManager
//                    .from(CdmaDoShowActivity.this);
            mNetworkOperatorName = TelephonyManagerProxy.getNetworkOperatorName(mSubId);
            mNetworkOperator = TelephonyManagerProxy.getNetworkOperatorForPhone(mSimIndex);
            isRoaming = TelephonyManagerProxy.isNetworkRoaming(mSubId);
            Log.d(TAG, "NetworkOperatorName: " + mNetworkOperatorName
                    + "NetworkOperator: " + mNetworkOperator + "isRoaming: "
                    + isRoaming);
            setTitle(activityLab[mCheckId]);
            SaveName = "atchannel" + mSimIndex;
            switch (mCheckId) {
            case KEY_SERVING:
                setContentView(R.layout.netinfo_listview_show);
                listView = (ListView) findViewById(R.id.netinfo_listview);
                values = this.getResources().getStringArray(R.array.c2k_do_sering_name);
                adapter = new ArrayAdapter<String>(CdmaDoShowActivity.this, R.layout.array_item, values);
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
                        CdmaDoShowActivity.this, R.layout.array_item, values);
                mlistviewOperators.setAdapter(operatorsAdapter);
                Message serving = mWcdmaHandler.obtainMessage(GET_SERVING);
                mWcdmaHandler.sendMessage(serving);
                break;
            case KEY_ADJACENT:
                setContentView(R.layout.netinfo_adjacent_evdo);
                TextView mTextViewPsc = (TextView) (CdmaDoShowActivity.this
                        .findViewById(R.id.netinfo_adjacent_3g_label02));
                mTextViewPsc.setText(R.string.netinfo_psc);
                ROW = 10;
                COL = 3;
                mtextView = new TextView[ROW][COL];
                mTextValue = new String[ROW][COL];
                viewID = new int[ROW][COL];

                viewID[0][0] = R.id.netinfo_adjacent_3g_label11;
                viewID[0][1] = R.id.netinfo_adjacent_3g_label12;
                viewID[0][2] = R.id.netinfo_adjacent_3g_label13;

                viewID[1][0] = R.id.netinfo_adjacent_3g_label21;
                viewID[1][1] = R.id.netinfo_adjacent_3g_label22;
                viewID[1][2] = R.id.netinfo_adjacent_3g_label23;

                viewID[2][0] = R.id.netinfo_adjacent_3g_label31;
                viewID[2][1] = R.id.netinfo_adjacent_3g_label32;
                viewID[2][2] = R.id.netinfo_adjacent_3g_label33;

                viewID[3][0] = R.id.netinfo_adjacent_3g_label41;
                viewID[3][1] = R.id.netinfo_adjacent_3g_label42;
                viewID[3][2] = R.id.netinfo_adjacent_3g_label43;

                viewID[4][0] = R.id.netinfo_adjacent_3g_label51;
                viewID[4][1] = R.id.netinfo_adjacent_3g_label52;
                viewID[4][2] = R.id.netinfo_adjacent_3g_label53;

                viewID[5][0] = R.id.netinfo_adjacent_3g_label61;
                viewID[5][1] = R.id.netinfo_adjacent_3g_label62;
                viewID[5][2] = R.id.netinfo_adjacent_3g_label63;

                viewID[6][0] = R.id.netinfo_adjacent_3g_label71;
                viewID[6][1] = R.id.netinfo_adjacent_3g_label72;
                viewID[6][2] = R.id.netinfo_adjacent_3g_label73;

                viewID[7][0] = R.id.netinfo_adjacent_3g_label81;
                viewID[7][1] = R.id.netinfo_adjacent_3g_label82;
                viewID[7][2] = R.id.netinfo_adjacent_3g_label83;

                viewID[8][0] = R.id.netinfo_adjacent_3g_label91;
                viewID[8][1] = R.id.netinfo_adjacent_3g_label92;
                viewID[8][2] = R.id.netinfo_adjacent_3g_label93;

                viewID[9][0] = R.id.netinfo_adjacent_3g_label101;
                viewID[9][1] = R.id.netinfo_adjacent_3g_label102;
                viewID[9][2] = R.id.netinfo_adjacent_3g_label103;

                for (int i = 0; i < ROW; i++) {
                    for (int j = 0; j < COL; j++) {
                        mtextView[i][j] = (TextView) (CdmaDoShowActivity.this
                                .findViewById(viewID[i][j]));
                    }
                }
                Message adjacent = mWcdmaHandler.obtainMessage(GET_ADJACENT);
                mWcdmaHandler.sendMessage(adjacent);
                break;
            case KEY_BETWEEN_ADJACENT_2G:
                setContentView(R.layout.netinfo_between_adjacent_3g);
                TextView mTextViewPsc_2g = (TextView) (CdmaDoShowActivity.this
                        .findViewById(R.id.netinfo_adjacent_3g_label02));
                mTextViewPsc_2g.setText(R.string.netinfo_psc);
                ROW = 5;
                COL = 3;
                mtextView = new TextView[ROW][COL];
                mTextValue = new String[ROW][COL];
                viewID = new int[ROW][COL];
                viewID[0][0] = R.id.netinfo_adjacent_3g_label11;
                viewID[0][1] = R.id.netinfo_adjacent_3g_label12;
                viewID[0][2] = R.id.netinfo_adjacent_3g_label13;

                viewID[1][0] = R.id.netinfo_adjacent_3g_label21;
                viewID[1][1] = R.id.netinfo_adjacent_3g_label22;
                viewID[1][2] = R.id.netinfo_adjacent_3g_label23;

                viewID[2][0] = R.id.netinfo_adjacent_3g_label31;
                viewID[2][1] = R.id.netinfo_adjacent_3g_label32;
                viewID[2][2] = R.id.netinfo_adjacent_3g_label33;

                viewID[3][0] = R.id.netinfo_adjacent_3g_label41;
                viewID[3][1] = R.id.netinfo_adjacent_3g_label42;
                viewID[3][2] = R.id.netinfo_adjacent_3g_label43;

                viewID[4][0] = R.id.netinfo_adjacent_3g_label51;
                viewID[4][1] = R.id.netinfo_adjacent_3g_label52;
                viewID[4][2] = R.id.netinfo_adjacent_3g_label53;

                for (int i = 0; i < ROW; i++) {
                    for (int j = 0; j < COL; j++) {
                        mtextView[i][j] = (TextView) (CdmaDoShowActivity.this
                                .findViewById(viewID[i][j]));
                    }
                }
                Message adjacent_2g = mWcdmaHandler
                        .obtainMessage(GET_BETWEEN_ADJACENT_2G);
                mWcdmaHandler.sendMessage(adjacent_2g);
                break;
            case KEY_BETWEEN_ADJACENT_4G:
                setContentView(R.layout.netinfo_between_adjacent_4g_evdo);
                ROW = 16;
                COL = 4;
                mtextView = new TextView[ROW][COL];
                mTextValue = new String[ROW][COL];
                viewID = new int[ROW][COL];

                viewID[0][0] = R.id.netinfo_adjacent_4g_label11;
                viewID[0][1] = R.id.netinfo_adjacent_4g_label12;
                viewID[0][2] = R.id.netinfo_adjacent_4g_label13;
                viewID[0][3] = R.id.netinfo_adjacent_4g_label14;

                viewID[1][0] = R.id.netinfo_adjacent_4g_label21;
                viewID[1][1] = R.id.netinfo_adjacent_4g_label22;
                viewID[1][2] = R.id.netinfo_adjacent_4g_label23;
                viewID[1][3] = R.id.netinfo_adjacent_4g_label24;

                viewID[2][0] = R.id.netinfo_adjacent_4g_label31;
                viewID[2][1] = R.id.netinfo_adjacent_4g_label32;
                viewID[2][2] = R.id.netinfo_adjacent_4g_label33;
                viewID[2][3] = R.id.netinfo_adjacent_4g_label34;

                viewID[3][0] = R.id.netinfo_adjacent_4g_label41;
                viewID[3][1] = R.id.netinfo_adjacent_4g_label42;
                viewID[3][2] = R.id.netinfo_adjacent_4g_label43;
                viewID[3][3] = R.id.netinfo_adjacent_4g_label44;

                viewID[4][0] = R.id.netinfo_adjacent_4g_label51;
                viewID[4][1] = R.id.netinfo_adjacent_4g_label52;
                viewID[4][2] = R.id.netinfo_adjacent_4g_label53;
                viewID[4][3] = R.id.netinfo_adjacent_4g_label54;

                viewID[5][0] = R.id.netinfo_adjacent_4g_label61;
                viewID[5][1] = R.id.netinfo_adjacent_4g_label62;
                viewID[5][2] = R.id.netinfo_adjacent_4g_label63;
                viewID[5][3] = R.id.netinfo_adjacent_4g_label64;

                viewID[6][0] = R.id.netinfo_adjacent_4g_label71;
                viewID[6][1] = R.id.netinfo_adjacent_4g_label72;
                viewID[6][2] = R.id.netinfo_adjacent_4g_label73;
                viewID[6][3] = R.id.netinfo_adjacent_4g_label74;

                viewID[7][0] = R.id.netinfo_adjacent_4g_label81;
                viewID[7][1] = R.id.netinfo_adjacent_4g_label82;
                viewID[7][2] = R.id.netinfo_adjacent_4g_label83;
                viewID[7][3] = R.id.netinfo_adjacent_4g_label84;

                viewID[8][0] = R.id.netinfo_adjacent_4g_label91;
                viewID[8][1] = R.id.netinfo_adjacent_4g_label92;
                viewID[8][2] = R.id.netinfo_adjacent_4g_label93;
                viewID[8][3] = R.id.netinfo_adjacent_4g_label94;

                viewID[9][0] = R.id.netinfo_adjacent_4g_label101;
                viewID[9][1] = R.id.netinfo_adjacent_4g_label102;
                viewID[9][2] = R.id.netinfo_adjacent_4g_label103;
                viewID[9][3] = R.id.netinfo_adjacent_4g_label104;

                viewID[10][0] = R.id.netinfo_adjacent_4g_label111;
                viewID[10][1] = R.id.netinfo_adjacent_4g_label112;
                viewID[10][2] = R.id.netinfo_adjacent_4g_label113;
                viewID[10][3] = R.id.netinfo_adjacent_4g_label114;

                viewID[11][0] = R.id.netinfo_adjacent_4g_label121;
                viewID[11][1] = R.id.netinfo_adjacent_4g_label122;
                viewID[11][2] = R.id.netinfo_adjacent_4g_label123;
                viewID[11][3] = R.id.netinfo_adjacent_4g_label124;

                viewID[12][0] = R.id.netinfo_adjacent_4g_label131;
                viewID[12][1] = R.id.netinfo_adjacent_4g_label132;
                viewID[12][2] = R.id.netinfo_adjacent_4g_label133;
                viewID[12][3] = R.id.netinfo_adjacent_4g_label134;

                viewID[13][0] = R.id.netinfo_adjacent_4g_label141;
                viewID[13][1] = R.id.netinfo_adjacent_4g_label142;
                viewID[13][2] = R.id.netinfo_adjacent_4g_label143;
                viewID[13][3] = R.id.netinfo_adjacent_4g_label144;

                viewID[14][0] = R.id.netinfo_adjacent_4g_label151;
                viewID[14][1] = R.id.netinfo_adjacent_4g_label152;
                viewID[14][2] = R.id.netinfo_adjacent_4g_label153;
                viewID[14][3] = R.id.netinfo_adjacent_4g_label154;

                viewID[15][0] = R.id.netinfo_adjacent_4g_label161;
                viewID[15][1] = R.id.netinfo_adjacent_4g_label162;
                viewID[15][2] = R.id.netinfo_adjacent_4g_label163;
                viewID[15][3] = R.id.netinfo_adjacent_4g_label164;

                for (int i = 0; i < ROW; i++) {
                    for (int j = 0; j < COL; j++) {
                        mtextView[i][j] = (TextView) (CdmaDoShowActivity.this
                                .findViewById(viewID[i][j]));
                    }
                }
                Message adjacent_4g = mWcdmaHandler
                        .obtainMessage(GET_BETWEEN_ADJACENT_4G);
                mWcdmaHandler.sendMessage(adjacent_4g);
                break;
            case KEY_OUTFIELD_NETWORK:
                setContentView(R.layout.netinfo_outfield_show);
                listView = (ListView) findViewById(R.id.netinfo_outfield_listview);

                Message outfieldNetwork = mWcdmaHandler
                        .obtainMessage(GET_OUTFIELD_NETWORK);
                mWcdmaHandler.sendMessage(outfieldNetwork);
                break;
            }
        } else {
            if (mWcdmaHandler != null) {
                mWcdmaHandler.getLooper().quit();
            }
            return;
        }
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                switch (mCheckId) {
                case KEY_SERVING:
                    Message serving = mWcdmaHandler.obtainMessage(GET_SERVING);
                    mWcdmaHandler.sendMessage(serving);
                    break;
                case KEY_ADJACENT:
                    Message adjacent = mWcdmaHandler
                            .obtainMessage(GET_ADJACENT);
                    mWcdmaHandler.sendMessage(adjacent);
                    break;
                case KEY_BETWEEN_ADJACENT_2G:
                    Message adjacent_2g = mWcdmaHandler
                            .obtainMessage(GET_BETWEEN_ADJACENT_2G);
                    mWcdmaHandler.sendMessage(adjacent_2g);
                    break;
                case KEY_BETWEEN_ADJACENT_4G:
                    Message adjacent_4g = mWcdmaHandler
                            .obtainMessage(GET_BETWEEN_ADJACENT_4G);
                    mWcdmaHandler.sendMessage(adjacent_4g);
                    break;
                case KEY_OUTFIELD_NETWORK:
                    Message outfieldNetwork = mWcdmaHandler
                            .obtainMessage(GET_OUTFIELD_NETWORK);
                    mWcdmaHandler.sendMessage(outfieldNetwork);
                    break;
                }
            }
        }, 0, 2000);
    }

    @Override
    protected void onDestroy() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        if (mWcdmaHandler != null) {
            mWcdmaHandler.getLooper().quit();
        }
        unregisterReceiver();
        super.onDestroy();
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
            // Ignored.
        }
    }

    public void TextValuesDisplay(int cmdType) {
        String result = null;
        int temp;
        switch (cmdType) {
        case KEY_ADJACENT:
            teleApi.netInfoC2kApi().getAdjacentCell(mSimIndex, mTextValue);
            break;
        case KEY_BETWEEN_ADJACENT_2G:
            teleApi.netInfoC2kApi().getBetweenAdjacentCell2G(mSimIndex, mTextValue);
            break;
        case KEY_BETWEEN_ADJACENT_4G:
            teleApi.netInfoC2kApi().getBetweenAdjacentCell4G(mSimIndex, mTextValue);
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
        int temp;
        final String[] tmpValues;
        if (cmdType == KEY_SERVING) {
            tmpValues = this.getResources().getStringArray(
                    R.array.c2k_do_sering_name);
            teleApi.netInfoC2kApi().getServingCell(mSimIndex, tmpValues, values);
            mUiThread.post(new Runnable() {
                @Override
                public void run() {
//                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//                            CdmaDoShowActivity.this, R.layout.array_item, values);
//                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            });
        } else if (cmdType == KEY_OUTFIELD_NETWORK) {
            values = this.getResources().getStringArray(R.array.Wcdma_Outfield_Network);
            List<String> list = teleApi.netInfoC2kApi().getOutfieldNetworkInfo(mSimIndex, values);
            mUiThread.post(new Runnable() {
                @Override
                public void run() {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            CdmaDoShowActivity.this, R.layout.array_item, list);
                    listView.setAdapter(adapter);
                    //adapter.notifyDataSetChanged();
                }
            });
        }
    }

    class WcdmaHandler extends Handler {
        public WcdmaHandler(Looper looper) {
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
            case GET_BETWEEN_ADJACENT_2G:
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
