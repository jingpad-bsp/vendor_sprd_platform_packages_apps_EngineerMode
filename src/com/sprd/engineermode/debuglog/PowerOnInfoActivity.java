
package com.sprd.engineermode.debuglog;

import android.content.Context;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.view.MenuItem;

import com.sprd.engineermode.R;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import java.util.ArrayList;
import java.util.List;

public class PowerOnInfoActivity extends SwitchBaseActivity {
    private static final String TAG = "PowerOnInfoActivity";

    private static List<String> mTimeArray = new ArrayList<String>();
    private static List<String> mBootModeArray = new ArrayList<String>();

    private SwitchMachineAdapter mAdapter;
    private Handler mThirdHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThirdHandler = new Handler();
        mPowerOnPref = getSharedPreferences(POWER_ON_PREF_NAME, Context.MODE_PRIVATE);
        mPowerOnCount = mPowerOnPref.getLong(INFO_COUNT, 0);
        mEditor = mPowerOnPref.edit();
        if (mTimeArray.size() > 0) {
            mTimeArray.clear();
            mBootModeArray.clear();
        }
        for (int i = 1; i <= mPowerOnCount; i++) {
            String key = PREF_INFO_NUM + Integer.toString(i);
            String keyTime = key + PREF_INFO_TIME;
            String keyMode = key + PREF_INFO_MODE;
            Log.d(TAG, "i = " + i + " keyTime = " + keyTime + " keyMode = " + keyMode);
            Log.d(TAG,
                    "mPowerOnPref.getString(keyTime, null) = "
                            + mPowerOnPref.getString(keyTime, null));
            Log.d(TAG,
                    "mPowerOnPref.getString(keyMode, null) = "
                            + mPowerOnPref.getString(keyMode, null));
            mTimeArray.add(mPowerOnPref.getString(keyTime, null));
            mBootModeArray.add(mPowerOnPref.getString(keyMode, null));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "mPowerOnCount = " + mPowerOnCount);

        if (mAdapter == null) {
            Log.d(TAG, "mAdapter == null");
            mAdapter = new SwitchMachineAdapter(this, mTimeArray, mBootModeArray, MODE_POWER_ON);
            mListView.setAdapter(mAdapter);
            mListView.setOnScrollListener(new OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if (scrollState != AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                        // mAdapter.setScrollingBlean(true);
                        // mAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem,
                        int visibleItemCount, int totalItemCount) {
                }
            });
        }
        mAdapter.notifyDataSetChanged();
        mListView.setSelection(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected");
        switch (item.getItemId()) {
            case R.id.dump_info:
                Log.i(TAG, "dump_info");
                checkSDCard();
                if (mPowerOnCount == 0) {
                    Toast.makeText(PowerOnInfoActivity.this, R.string.no_data_toast,
                            Toast.LENGTH_SHORT).show();
                } else if (!mIsMounted) {
                    Toast.makeText(PowerOnInfoActivity.this, R.string.no_sd_toast,
                            Toast.LENGTH_SHORT).show();
                } else {
                    mThirdHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            saveToSd(POWER_ON_PATH, POWER_ON_NAME);
                        }
                    });
                }
                return true;
            case R.id.clear_info:
                Log.i(TAG, "click clear_info");
                if (mPowerOnCount == 0) {
                    Toast.makeText(PowerOnInfoActivity.this, R.string.no_data_toast,
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
                doReset();
                Toast.makeText(PowerOnInfoActivity.this, R.string.clear_success_toast,
                        Toast.LENGTH_SHORT).show();
                return true;
            default:
                Log.i(TAG, "default");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void doReset() {
        mPowerOnCount = 0;
        mTimeArray.clear();
        mBootModeArray.clear();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        mEditor.clear();
        mEditor.apply();
        invalidateOptionsMenu();
    }

    /*public static class BootCompletedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //boolean gsiVersion=  SystemPropertiesProxy.get("ro.board.device").equalsIgnoreCase("generic_arm_a");
            boolean gsiVersion = Const.isGSIVersion();
            if(gsiVersion){
                //DO NOTHING
                return;
            }
            mPowerOnPref = context.getSharedPreferences(POWER_ON_PREF_NAME, Context.MODE_PRIVATE);

            mPowerOnCount = mPowerOnPref.getLong(INFO_COUNT, 0);

            mPowerOnCount++;
            mEditor = mPowerOnPref.edit();

            SimpleDateFormat sDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
            String date = sDateFormat.format(new java.util.Date());
            String bootMode = SystemPropertiesProxy.get("ro.bootmode", "mode");
            Log.d(TAG, "date = " + date + " bootMode = " + bootMode + " mPowerOnCount = "
                    + mPowerOnCount);

            mEditor.putLong(INFO_COUNT, mPowerOnCount);

            String key = PREF_INFO_NUM + Long.toString(mPowerOnCount);
            String keyTime = key + PREF_INFO_TIME;
            String keyMode = key + PREF_INFO_MODE;

            mEditor.putString(keyTime, date);
            mEditor.putString(keyMode, bootMode);

            mEditor.apply();

        }
    }*/

    @Override
    public void finish() {
        super.finish();
    }

}
