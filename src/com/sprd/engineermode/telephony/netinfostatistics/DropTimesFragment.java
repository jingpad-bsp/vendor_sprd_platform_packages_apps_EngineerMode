package com.sprd.engineermode.telephony.netinfostatistics;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.app.Fragment;
import android.view.ViewGroup;
import android.telephony.TelephonyManager;

import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.intf.ITelephonyApi.INetInfoStat.DataItem;
import com.unisoc.engineermode.core.impl.nonpublic.TelephonyManagerProxy;
import com.unisoc.engineermode.core.impl.telephony.TelephonyManagerSprd;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.sprd.engineermode.R;
import com.sprd.engineermode.utils.ThreadUtils;

public class DropTimesFragment extends Fragment {

    private String TAG = "DropTimesFragment";
    private ITelephonyApi teleApi = CoreApi.getTelephonyApi();
    private Context mContext;
    private Handler mUiThread = new Handler();
    private HandlerThread mHT;
    private NetinfoHandler mNetinfoHandler;

    private boolean mFragmentDestroyed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.droptimes, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        Log.d(TAG, "onCreate");
        if (mHT == null) {
            mHT = new HandlerThread(TAG);
            mHT.start();
        }
        mNetinfoHandler = new NetinfoHandler(mHT.getLooper());
        mFragmentDestroyed = false;
    }

    class NetinfoHandler extends Handler {

        public NetinfoHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            int simid = msg.what;
            if (isSimExist(simid)) {
                parseNetStatistic(simid);
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /* SPRD 1034435: Fragment content repeated. {@ */
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        Intent intent = getActivity().getIntent();
        int simindex = intent.getIntExtra("simindex", -1);
        Log.d(TAG, "onResume simindex=" + simindex);
        Message m = mNetinfoHandler.obtainMessage(simindex);
        mNetinfoHandler.sendMessage(m);
    }
    /* @} */

    @Override
    public void onStop() {
        super.onStop();
        /* SPRD 1016025: DropTimes Fragment content repeated. {@ */
        mFragmentDestroyed = true;
        /* @} */
        Log.d(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        /* SPRD 1016025: DropTimes Fragment content repeated. {@ */
        mFragmentDestroyed = false;
        /* @} */
        Log.d(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mNetinfoHandler != null) {
            Log.d(TAG, "HandlerThread has quit");
            mNetinfoHandler.getLooper().quit();
        }
        if (mHT != null) {
            ThreadUtils.stopThread(mHT);
        }
        mFragmentDestroyed = true;
        Log.d(TAG, "onDestroy");
    }

    private TextView createTextView(String text) {
        TextView view = new TextView(mContext);
        view.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        view.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        view.setText(text);
        return view;
    }

    private TableRow createRow(DataItem item) {
        TableRow tr = new TableRow(mContext);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT);

        float paddingDp = 5f;
        // Convert to pixels
        int paddingPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, paddingDp,
                mContext.getResources().getDisplayMetrics());

        Log.d(TAG, "padding: " + paddingPx);
        tr.setPadding(paddingPx, paddingPx,0,0);
        tr.setLayoutParams(lp);

        tr.addView(createTextView(item.desc));
        tr.addView(createTextView(item.data));

        return tr;
    }

    private void updateUi(DataItem[] items) {
        TableLayout tl = getActivity().findViewById(R.id.dropcount_tablelayout);
        for (DataItem item : items) {
            Log.d(TAG, String.format("%s: %s ", item.desc, item.data));
            tl.addView(createRow(item));
        }
    }
    private void parseNetStatistic(int simindex) {

        if (mFragmentDestroyed) {
            Log.d(TAG, "mFragmentDestroyed ==" + mFragmentDestroyed);
            return;
        }

        try {
            DataItem[] data = teleApi.netInfoStat().getDropCount(simindex);
            mUiThread.post(new Runnable() {
                @Override
                public void run() {
                    updateUi(data);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isSimExist(int simIndex) {
        if (TelephonyManagerProxy.getSimState(simIndex) == TelephonyManager.SIM_STATE_READY) {
            return true;
        }
        return false;
    }

}
