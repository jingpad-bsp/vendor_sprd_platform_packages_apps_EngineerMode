/*This code functions as follows:
 **1, the display of the GSM correlation detection result
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
import java.util.Timer;
import java.util.TimerTask;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.impl.nonpublic.TelephonyManagerProxy;

import android.widget.ArrayAdapter;
import android.content.BroadcastReceiver;

import java.util.List;

public class GsmShowActivity extends Activity {

    private static final String TAG = "GsmShowActivity";
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
    private static final int KEY_BETWEEN_ADJACENT_3G = 2;
    private static final int KEY_BETWEEN_ADJACENT_4G = 3;
    private static final int KEY_OUTFIELD_NETWORK = 4;

    private static final int GET_SERVING = 0;
    private static final int GET_ADJACENT = 1;
    private static final int GET_BETWEEN_ADJACENT_3G = 2;
    private static final int GET_BETWEEN_ADJACENT_4G = 3;
    private static final int GET_OUTFIELD_NETWORK = 4;

    private ITelephonyApi teleApi = CoreApi.getTelephonyApi();

    private String SaveName = "atchannel";
    private int mCheckId;
    private int mSimIndex;
    private int mSubId;

    private Timer mTimer;
    private GsmHandler mGsmHandler;
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


    private int[] activityLab = new int[] { R.string.netinfo_server_cell,
            R.string.netinfo_neighbour_cell, R.string.netinfo_adjacent_cell_3g,
            R.string.netinfo_adjacent_cell_4g,
            R.string.netinfo_outfield_information };

    private BroadcastReceiver mNetWorkReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            int netWorkStat = intent.getIntExtra(NETWORK_TYPE, 0);
            int phoneId = intent.getIntExtra(KEY_SIM_INDEX, -1);
            String mIntentAction;
            Log.d(TAG, "NetWork state is " + netWorkStat);
            synchronized (mLock) {
                if ((netWorkStat != NETWORK_GSM) && (phoneId == mSimIndex)) {
                    switch (netWorkStat) {
                    case NETWORK_TDSCDMA:
                        mIntentAction = "android.engineermode.action.TDSCDMASHOW";
                        break;
                    case NETWORK_WCDMA:
                        mIntentAction = "android.engineermode.action.WCDMASHOW";
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

        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mGsmHandler = new GsmHandler(ht.getLooper());
        if ((mCheckId != -1) && (mSimIndex != -1)) {
//            mTelephonyManager = TelephonyManagerProxy.getService();
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
                values = this.getResources().getStringArray(R.array.Gsm_sering_name);
                adapter = new ArrayAdapter<String>(GsmShowActivity.this, R.layout.array_item, values);
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
                        GsmShowActivity.this, R.layout.array_item, values);
                mlistviewOperators.setAdapter(operatorsAdapter);
                Message serving = mGsmHandler.obtainMessage(GET_SERVING);
                mGsmHandler.sendMessage(serving);
                break;
            case KEY_ADJACENT:
                setContentView(R.layout.netinfo_adjacent_show);
                ROW = 5;
                COL = 5;
                mtextView = new TextView[ROW][COL];
                mTextValue = new String[ROW][COL];
                viewID = new int[ROW][COL];

                viewID[0][0] = R.id.netinfo_adjacent_label11;
                viewID[0][1] = R.id.netinfo_adjacent_label12;
                viewID[0][2] = R.id.netinfo_adjacent_label13;
                viewID[0][3] = R.id.netinfo_adjacent_label14;
                viewID[0][4] = R.id.netinfo_adjacent_label15;

                viewID[1][0] = R.id.netinfo_adjacent_label21;
                viewID[1][1] = R.id.netinfo_adjacent_label22;
                viewID[1][2] = R.id.netinfo_adjacent_label23;
                viewID[1][3] = R.id.netinfo_adjacent_label24;
                viewID[1][4] = R.id.netinfo_adjacent_label25;

                viewID[2][0] = R.id.netinfo_adjacent_label31;
                viewID[2][1] = R.id.netinfo_adjacent_label32;
                viewID[2][2] = R.id.netinfo_adjacent_label33;
                viewID[2][3] = R.id.netinfo_adjacent_label34;
                viewID[2][4] = R.id.netinfo_adjacent_label35;

                viewID[3][0] = R.id.netinfo_adjacent_label41;
                viewID[3][1] = R.id.netinfo_adjacent_label42;
                viewID[3][2] = R.id.netinfo_adjacent_label43;
                viewID[3][3] = R.id.netinfo_adjacent_label44;
                viewID[3][4] = R.id.netinfo_adjacent_label45;

                viewID[4][0] = R.id.netinfo_adjacent_label51;
                viewID[4][1] = R.id.netinfo_adjacent_label52;
                viewID[4][2] = R.id.netinfo_adjacent_label53;
                viewID[4][3] = R.id.netinfo_adjacent_label54;
                viewID[4][4] = R.id.netinfo_adjacent_label55;

                for (int i = 0; i < ROW; i++) {
                    for (int j = 0; j < COL; j++) {
                        mtextView[i][j] = (TextView) (GsmShowActivity.this
                                .findViewById(viewID[i][j]));
                    }
                }
                Message adjacent = mGsmHandler.obtainMessage(GET_ADJACENT);
                mGsmHandler.sendMessage(adjacent);
                break;
            case KEY_BETWEEN_ADJACENT_3G:
                setContentView(R.layout.netinfo_between_adjacent_3g);
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
                        mtextView[i][j] = (TextView) (GsmShowActivity.this
                                .findViewById(viewID[i][j]));
                    }
                }
                Message adjacent_3g = mGsmHandler
                        .obtainMessage(GET_BETWEEN_ADJACENT_3G);
                mGsmHandler.sendMessage(adjacent_3g);
                break;
            case KEY_BETWEEN_ADJACENT_4G:
                setContentView(R.layout.netinfo_between_adjacent_4g);
                ROW = 5;
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

                for (int i = 0; i < ROW; i++) {
                    for (int j = 0; j < COL; j++) {
                        mtextView[i][j] = (TextView) (GsmShowActivity.this
                                .findViewById(viewID[i][j]));
                    }
                }
                Message adjacent_4g = mGsmHandler
                        .obtainMessage(GET_BETWEEN_ADJACENT_4G);
                mGsmHandler.sendMessage(adjacent_4g);
                break;
            case KEY_OUTFIELD_NETWORK:
                setContentView(R.layout.netinfo_outfield_show);
                listView = (ListView) findViewById(R.id.netinfo_outfield_listview);
                Message outfieldNetwork = mGsmHandler
                        .obtainMessage(GET_OUTFIELD_NETWORK);
                mGsmHandler.sendMessage(outfieldNetwork);
                break;
            }
        } else {
            if (mGsmHandler != null) {
                mGsmHandler.getLooper().quit();
            }
            return;
        }
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                switch (mCheckId) {
                case KEY_SERVING:
                    Message serving = mGsmHandler.obtainMessage(GET_SERVING);
                    mGsmHandler.sendMessage(serving);
                    break;
                case KEY_ADJACENT:
                    Message adjacent = mGsmHandler.obtainMessage(GET_ADJACENT);
                    mGsmHandler.sendMessage(adjacent);
                    break;
                case KEY_BETWEEN_ADJACENT_3G:
                    Message adjacent_3g = mGsmHandler
                            .obtainMessage(GET_BETWEEN_ADJACENT_3G);
                    mGsmHandler.sendMessage(adjacent_3g);
                    break;
                case KEY_BETWEEN_ADJACENT_4G:
                    Message adjacent_4g = mGsmHandler
                            .obtainMessage(GET_BETWEEN_ADJACENT_4G);
                    mGsmHandler.sendMessage(adjacent_4g);
                    break;
                case KEY_OUTFIELD_NETWORK:
                    Message outfieldNetwork = mGsmHandler
                            .obtainMessage(GET_OUTFIELD_NETWORK);
                    mGsmHandler.sendMessage(outfieldNetwork);
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
            // Ignored.
        }
    }

    /* BUG 1009917 - when not in network cell Activity, do not startActivity @{ */
    @Override
    public void onStart() {
        super.onStart();
        registerReceiver();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver();
        Log.d(TAG, "onStop");
    }
    /* @} */

    @Override
    protected void onDestroy() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        if (mGsmHandler != null) {
            mGsmHandler.getLooper().quit();
        }
        super.onDestroy();
    }

    public void TextValuesDisplay(int cmdType) {
        String result = null;
        int temp;
        switch (cmdType) {
        case KEY_ADJACENT:
            teleApi.netInfoGsmApi().getAdjacentCell(mSimIndex, mTextValue);
            break;
        case KEY_BETWEEN_ADJACENT_3G:
            teleApi.netInfoGsmApi().getBetweenAdjacentCell3G(mSimIndex, mTextValue);
            break;
        case KEY_BETWEEN_ADJACENT_4G:
            teleApi.netInfoGsmApi().getBetweenAdjacentCell4G(mSimIndex, mTextValue);
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
            tmpValues = this.getResources()
                    .getStringArray(R.array.Gsm_sering_name);
            teleApi.netInfoGsmApi().getServingCell(mSimIndex, tmpValues, values);
            mUiThread.post(new Runnable() {
                @Override
                public void run() {
//                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//                            GsmShowActivity.this, R.layout.array_item, values);
//                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            });
        } else if (cmdType == KEY_OUTFIELD_NETWORK) {
            values = this.getResources().getStringArray(R.array.Gsm_Outfield_Network);
            List<String> list = teleApi.netInfoGsmApi().getOutfieldNetworkInfo(mSimIndex, values);

            mUiThread.post(new Runnable() {
                @Override
                public void run() {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            GsmShowActivity.this, R.layout.array_item, list);
                    listView.setAdapter(adapter);
                    //adapter.notifyDataSetChanged();
                }
            });
        }
    }

    class GsmHandler extends Handler {
        public GsmHandler(Looper looper) {
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
            case GET_BETWEEN_ADJACENT_3G:
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
