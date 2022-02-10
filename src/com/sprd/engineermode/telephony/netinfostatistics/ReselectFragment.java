
package com.sprd.engineermode.telephony.netinfostatistics;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.app.Fragment;
import android.view.ViewGroup;

import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.data.telephony.netinfostat.NetInfoStatData;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.impl.nonpublic.TelephonyManagerProxy;
import com.unisoc.engineermode.core.impl.telephony.TelephonyManagerSprd;
import android.content.Context;
import android.app.Activity;
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
import com.unisoc.engineermode.core.utils.IATUtils;
import com.sprd.engineermode.utils.ThreadUtils;

import java.text.NumberFormat;

public class ReselectFragment extends Fragment {

    private String TAG = "ReselectFragment";
    private Handler mUiThread = new Handler();

    private ITelephonyApi teleApi = CoreApi.getTelephonyApi();
    private HandlerThread mht;
    private NetinfoHandler netinfoHandler;
    public int ROW;
    public int COL;
    private Context mContext;

    private boolean mFragmentDestroyed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView");

        View view =  inflater.inflate(R.layout.cell_reselect_lte, container,
                false);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        mContext = getActivity();
        if (mht == null) {
            mht = new HandlerThread(TAG);
            mht.start();
        }
        netinfoHandler = new NetinfoHandler(mht.getLooper());
        mFragmentDestroyed = false;
    }

    class NetinfoHandler extends Handler {

        public NetinfoHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            int simid = msg.what;
            if (TelephonyManagerProxy.isSimExist(simid)) {
                parseNetStatisticlte(simid);
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
    }

    private int getResIdByRowCol(String prefix, int row, int col) {
        return getResources().getIdentifier(prefix + row + col, "id", mContext.getPackageName());
    }

    /* SPRD 1034435: Fragment content repeated. {@ */
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        Intent intent = getActivity().getIntent();
        int simindex = intent.getIntExtra("simindex", -1);
        Log.d(TAG, "onResume simindex=" + simindex);
        Message m = netinfoHandler.obtainMessage(simindex);
        netinfoHandler.sendMessage(m);
    }
    /* @} */

    @Override
    public void onStop() {
        super.onStop();
        /* SPRD 1016025: Reselect Fragment content repeated. {@ */
        mFragmentDestroyed = true;
        /* @} */
        Log.d(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        /* SPRD 1016025: Reselect Fragment content repeated. {@ */
        mFragmentDestroyed = false;
        /* @} */
        Log.d(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (netinfoHandler != null) {
            Log.d(TAG, "HandlerThread has quit");
            netinfoHandler.getLooper().quit();
        }
        if (mht != null) {
            ThreadUtils.stopThread(mht);
        }
        mFragmentDestroyed = true;
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
    }

    private TextView createTextView(String text) {
        TextView view = new TextView(mContext);
        view.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        view.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        view.setText(text);
        return view;
    }

    private TableRow createRow(NetInfoStatData.Item item) {
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

        tr.addView(createTextView(item.getDesc()));
        tr.addView(createTextView(item.getSuccess()));
        tr.addView(createTextView(item.getFail()));
        tr.addView(createTextView(item.getRatio()));
        tr.addView(createTextView(item.getDelay()));

        return tr;
    }

    private void updateUi(NetInfoStatData.Item[] items) {
        TableLayout tl = getActivity().findViewById(R.id.reselect_lte_tablelayout);
        for (NetInfoStatData.Item item : items) {
            Log.d(TAG, String.format("%s: %s  %s  %s %s", item.getDesc(), item.getSuccess(), item.getFail(), item.getRatio(), item.getDelay()));
            tl.addView(createRow(item));
        }
    }

    private void parseNetStatisticlte(int simindex) {
        if (mFragmentDestroyed) {
            Log.d(TAG, "mFragmentDestroyed ==" + mFragmentDestroyed);
            return;
        }

        try {
            NetInfoStatData data = teleApi.netInfoStat().getReselectInfo(simindex);
            mUiThread.post(new Runnable() {
                @Override
                public void run() {
                    updateUi(data.getData());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
