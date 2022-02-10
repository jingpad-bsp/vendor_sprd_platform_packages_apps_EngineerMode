/*This code functions as follows:
 **1, the display of the LTE correlation detection result
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
import android.content.BroadcastReceiver;
import android.util.Log;

import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.impl.nonpublic.TelephonyManagerProxy;

import android.widget.ArrayAdapter;

import java.util.List;
import java.util.ArrayList;

public class LteShowActivity extends Activity {

    private static final String TAG = "LteShowActivity";
    private static final String LTE_INDEX = "PrefenceIndex";
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
    private static final int KEY_BETWEEN_ADJACENT_2G = 2;
    private static final int KEY_BETWEEN_ADJACENT_3G = 3;
    private static final int KEY_OUTFIELD_NETWORK = 4;

    private static final int GET_SERVING = 0;
    private static final int GET_ADJACENT = 1;
    private static final int GET_BETWEEN_ADJACENT_2G = 2;
    private static final int GET_BETWEEN_ADJACENT_3G = 3;
    private static final int GET_OUTFIELD_NETWORK = 4;

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
    private ListView mNrListView;
    private ListView mlistviewOperators;
    TextView[][] mtextView;
    private String[][] mTextValue;
    private int[][] viewID;
    public int ROW;
    public int COL;
    private String[] values;
    private String[] mSimValues;
    private String[] mNrValues;
    private List<String> mList;
    ArrayAdapter<String> mNrAdapter = null;
    ArrayAdapter<String> adapter = null;

    public boolean isRoaming;
    public String mNetworkOperator;
    public String mNetworkOperatorName;

    private int[] activityLab = new int[] { R.string.netinfo_server_cell,
            R.string.netinfo_neighbour_cell, R.string.netinfo_adjacent_cell_2g,
            R.string.netinfo_adjacent_cell_3g,
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
        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mLteHandler = new LteHandler(ht.getLooper());
        if (mCheckId != -1 && mSimIndex != -1) {
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
                setContentView(R.layout.netinfo_listview_lte_show);
                listView = (ListView) findViewById(R.id.netinfo_listview);
                listView.setItemsCanFocus(true);
                mNrListView = (ListView)findViewById(R.id.nr_listview);
                values = this.getResources().getStringArray(R.array.Lte_sering_name);
                mNrValues = this.getResources().getStringArray(R.array.Lte_sering_nr_name);
                mSimValues = this.getResources().getStringArray(R.array.operators_sering_name);
                mList = new ArrayList<>();
                mSimValues[0] = mSimValues[0] + ": " + (mNetworkOperator != null ? mNetworkOperator : "NA");
                mSimValues[1] = mSimValues[1] + ": " + (mNetworkOperatorName != null ? mNetworkOperatorName : "NA");
                mSimValues[2] = mSimValues[2] + ": " + (isRoaming ? getResources().getString(R.string.is_roaming) : getResources().getString(R.string.non_roaming));
                mList.add(mSimValues[0]);
                mList.add(mSimValues[1]);
                mList.add(mSimValues[2]);
                adapter = new ArrayAdapter<String>(LteShowActivity.this, R.layout.array_item, mList);
                mNrAdapter = new ArrayAdapter<String>(LteShowActivity.this, R.layout.array_item, mNrValues);
                listView.setAdapter(adapter);
                mNrListView.setAdapter(mNrAdapter);
                Message serving = mLteHandler.obtainMessage(GET_SERVING);
                mLteHandler.sendMessage(serving);
                break;
            case KEY_ADJACENT:
                setContentView(R.layout.netinfo_between_adjacent_4g);
                TextView mTextViewPsc = (TextView) (LteShowActivity.this
                        .findViewById(R.id.netinfo_adjacent_4g_label02));
                mTextViewPsc.setText(R.string.netinfo_pci);
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
                        mtextView[i][j] = (TextView) (LteShowActivity.this
                                .findViewById(viewID[i][j]));
                    }
                }
                Message adjacent = mLteHandler.obtainMessage(GET_ADJACENT);
                mLteHandler.sendMessage(adjacent);
                break;
            case KEY_BETWEEN_ADJACENT_2G:
                setContentView(R.layout.netinfo_between_adjacent_3g);
                TextView mTextViewPsc_2g = (TextView) (LteShowActivity.this
                        .findViewById(R.id.netinfo_adjacent_3g_label02));
                mTextViewPsc_2g.setText(R.string.netinfo_pci);
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
                        mtextView[i][j] = (TextView) (LteShowActivity.this
                                .findViewById(viewID[i][j]));
                    }
                }
                Message adjacent_2g = mLteHandler
                        .obtainMessage(GET_BETWEEN_ADJACENT_2G);
                mLteHandler.sendMessage(adjacent_2g);
                break;
            case KEY_BETWEEN_ADJACENT_3G:
                setContentView(R.layout.netinfo_between_adjacent_3g);
                TextView mTextViewPsc_3g = (TextView) (LteShowActivity.this
                        .findViewById(R.id.netinfo_adjacent_3g_label02));
                mTextViewPsc_3g.setText(R.string.netinfo_pci);
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
                        mtextView[i][j] = (TextView) (LteShowActivity.this
                                .findViewById(viewID[i][j]));
                    }
                }
                Message adjacent_3g = mLteHandler
                        .obtainMessage(GET_BETWEEN_ADJACENT_3G);
                mLteHandler.sendMessage(adjacent_3g);
                break;
            case KEY_OUTFIELD_NETWORK:
                setContentView(R.layout.netinfo_outfield_show);
                listView = (ListView) findViewById(R.id.netinfo_outfield_listview);
//                values = this.getResources().getStringArray(R.array.Lte_Outfield_Network);
//                adapter = new ArrayAdapter<String>(LteShowActivity.this, R.layout.array_item, values);
//                listView.setAdapter(adapter);
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
                case KEY_BETWEEN_ADJACENT_2G:
                    Message adjacent_2g = mLteHandler
                            .obtainMessage(GET_BETWEEN_ADJACENT_2G);
                    mLteHandler.sendMessage(adjacent_2g);
                    break;
                case KEY_BETWEEN_ADJACENT_3G:
                    Message adjacent_3g = mLteHandler
                            .obtainMessage(GET_BETWEEN_ADJACENT_3G);
                    mLteHandler.sendMessage(adjacent_3g);
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

    @Override
    protected void onDestroy() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        if (mLteHandler != null) {
            mLteHandler.getLooper().quit();
        }

        super.onDestroy();
    }

    public void TextValuesDisplay(int cmdType) {
        String result = null;
        int temp;
        switch (cmdType) {
        case KEY_ADJACENT:
            teleApi.netInfoLteApi().getAdjacentCell(mSimIndex, mTextValue);
            break;
        case KEY_BETWEEN_ADJACENT_2G:
            teleApi.netInfoLteApi().getBetweenAdjacentCell2G(mSimIndex, mTextValue);
            break;
        case KEY_BETWEEN_ADJACENT_3G:
            teleApi.netInfoLteApi().getBetweenAdjacentCell3G(mSimIndex, mTextValue);
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
        final String[] tmpLteValues;
        final String[] tmpNrValues;
        int temp;
        String strTemp = null;
        int splitPoint=7;
        if (cmdType == KEY_SERVING) {
            tmpLteValues = this.getResources()
                    .getStringArray(R.array.Lte_sering_name);
            tmpNrValues = this.getResources()
                    .getStringArray(R.array.Lte_sering_nr_name);

            teleApi.netInfoLteApi().getServingCell(mSimIndex, tmpLteValues, values);
            mList.clear();
            mList.add(mSimValues[0]);
            mList.add(mSimValues[1]);
            mList.add(mSimValues[2]);
            for (int i = 0; i < values.length; i++){
                mList.add(values[i]);
            }
            teleApi.netInfoLteApi().getServingCellNR(mSimIndex, tmpNrValues, mNrValues);

            mUiThread.post(new Runnable() {
                @Override
                public void run() {
//                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//                            LteShowActivity.this, R.layout.array_item, values);
//                    listView.setAdapter(adapter);
                    //UNISOC: add for bug1368940, java.lang.IndexOutOfBoundsException
                    if (isResumed()) {
                        adapter.notifyDataSetChanged();
                        mNrAdapter.notifyDataSetChanged();
                    }
                }
            });
        } else if (cmdType == KEY_OUTFIELD_NETWORK) {
            values = this.getResources().getStringArray(R.array.Lte_Outfield_Network);
            List<String> list = teleApi.netInfoLteApi().getOutfieldNetworkInfo(mSimIndex, values);
            mUiThread.post(new Runnable() {
                @Override
                public void run() {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            LteShowActivity.this, R.layout.array_item, list);
                    listView.setAdapter(adapter);
                    //adapter.notifyDataSetChanged();
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
            case GET_BETWEEN_ADJACENT_2G:
                TextValuesDisplay(mCheckId);
                break;
            case GET_BETWEEN_ADJACENT_3G:
                TextValuesDisplay(mCheckId);
                break;
            case GET_OUTFIELD_NETWORK:
                valuesDisplay(mCheckId);
                break;
            }
        }
    }
}
