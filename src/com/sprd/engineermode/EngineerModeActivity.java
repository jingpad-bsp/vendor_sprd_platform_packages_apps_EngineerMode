package com.sprd.engineermode;

import java.util.ArrayList;
import java.util.List;

import com.sprd.engineermode.connectivity.ConnectivityFragment;
import com.unisoc.engineermode.core.common.Const;
import com.sprd.engineermode.debuglog.DebugLogFragment;
import com.sprd.engineermode.hardware.HardWareFragment;
import com.sprd.engineermode.telephony.TelephonyFragment;
import com.sprd.engineermode.location.LocationFragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.Intent;
import java.text.SimpleDateFormat;

public class EngineerModeActivity extends AppCompatActivity {

    private static final String TAG = "EngineerModeActivity";

    //message list
    private static final int CLOSE_CP2 = 0;
    private static final int OPEN_CP2 = 1;

    //AT Command list
    private static final String AT_CLOSE_CP2 = "poweroff";
    private static final String AT_OPEN_CP2 = "poweron";

    //scoket name
    private static final String SOCKET_NAME = "wcnd";
    private SharedPreferences mPrefs;
    private ArrayList<Fragment> mFragmentsList;
    private List<String> mTitleList = new ArrayList<String>();
    private ViewPager mViewPager;
    private Context mContext;

    private Handler mUiThread = new Handler();
    /* SPRD 815541 : Coulometer Power Test Mode @{ */
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor, mEditor;
    /* }@ */

    public interface TabState {
        public static int TAB_TELE_INDEX = 0;
        public static int TAB_DEBUG_INDEX = 1;
        public static int TAB_CONNECT_INDEX = 2;
        public static int TAB_HARDWARE_INDEX = 3;
         public static int TAB_LOCATION_INDEX = 4;
    }

    private int[] mTabTitle = new int[] { R.string.tab_telephony,
            R.string.tab_debug, R.string.tab_connectivity,
            R.string.tab_hardwaretest,R.string.tab_location};

    private int mCurrentTab = TabState.TAB_TELE_INDEX;
    public static String CURRENT_TAB = "android.app.engmode.currenttab";
    public static boolean mIsFirst = false;
    /*
    private TextView teleTextView;
    private TextView debugTextView;
    private TextView connectivityTextView;
    private TextView hardwareTextView;
    private ImageView cursor;

    private int offset = 0;
    private int position_one;
    private int position_two;
    private int position_three;

    private int bmpW;
    private int currIndex = 0;

    private FragmentManager fragmentManager;
    */
    /* Sprd 910010: androidp Shutdown broadcast can just only registered by dynamic @{ */
    private SharedPreferences mPowerOffPref, mBatteryLifeTime;
    private ShutDownReceiver mReceiver = null;
    public static final String POWER_ON_PREF_NAME = "power_on";
    public static final String POWER_OFF_PREF_NAME = "power_off";
    public static final String MODEM_ASSERT_PREF_NAME = "modem_assert";
    public static final String BATTERY_LIFE_TIME = "battery_life_time";
    public static final String INFO_COUNT = "info_count";
    public static long mPowerOffCount = 0;

    public static final String PREF_INFO_NUM = "info_";
    public static final String PREF_INFO_TIME = "_time";
    public static final String PREF_INFO_MODE = "_mode";
    public static final String PREF_MODEM_INFO = "_info";

    public static final String PREF_OPEN_TIME = "open_time";
    public static final String PREF_OPEN_BATTERY = "open_battery";
    public static final String PREF_SHUT_DOWN_INFO = "shut_down_info";
    public static final String PREF_TOTAL_TIME = "total_time";
    public static final String PREF_IS_FIRST_SHUTDOWN = "first_shutdown";
    /* }@ */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        if (Const.isGSIVersion()) {
            finish();
            return;
        }
        setToolbarTabLayout();
        initCoordinatorAndTabLayout();
        /* SPRD 815541 : Coulometer Power Test Mode @{ */
        preferences = this.getSharedPreferences("cc_status", this.MODE_PRIVATE);
        Log.d(TAG, "onCreate");
        editor = preferences.edit();
        editor.putBoolean("time1_stop", true);
        editor.putBoolean("time2_stop", true);
        editor.putBoolean("time3_stop", true);
        editor.commit();
        /* }@ */
        /* Sprd 910010: androidp Shutdown broadcast can just only registered by dynamic @{ */
        if (mReceiver == null) {
            mReceiver = new ShutDownReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SHUTDOWN);
            registerReceiver(mReceiver, filter);
        }
        /* }@ */
    }

    private void setToolbarTabLayout() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
    }

    /* Sprd 910010: androidp Shutdown broadcast can just only registered by dynamic @{ */
    private class ShutDownReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mPowerOffPref = context.getSharedPreferences(POWER_OFF_PREF_NAME, Context.MODE_PRIVATE);

            mPowerOffCount = mPowerOffPref.getLong(INFO_COUNT, 0);

            mPowerOffCount++;
            mEditor = mPowerOffPref.edit();

            SimpleDateFormat sDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
            String date = sDateFormat.format(new java.util.Date());
            String shutDownMode = intent.getStringExtra("shutdown_mode");
            Log.d(TAG, "date = " + date + " shutDownMode = " + shutDownMode + " mPowerOffCount = "
                    + mPowerOffCount);
            mEditor.putLong(INFO_COUNT, mPowerOffCount);

            String key = PREF_INFO_NUM + Long.toString(mPowerOffCount);
            String keyTime = key + PREF_INFO_TIME;
            String keyMode = key + PREF_INFO_MODE;

            mEditor.putString(keyTime, date);
            mEditor.putString(keyMode, shutDownMode);
            mEditor.apply();

            if (shutDownMode != null && shutDownMode.contains("no_power")) {
                mBatteryLifeTime = context.getSharedPreferences(BATTERY_LIFE_TIME,
                        Context.MODE_PRIVATE);
                boolean isFirst = mBatteryLifeTime.getBoolean(PREF_IS_FIRST_SHUTDOWN, true);
                Log.d(TAG, "isFirst = " + isFirst);
                if (isFirst) {
                    SharedPreferences.Editor editor = mBatteryLifeTime.edit();
                    editor.putString(PREF_SHUT_DOWN_INFO, date);
                    editor.putBoolean(PREF_IS_FIRST_SHUTDOWN, false);
                    editor.apply();
                }

            }

        }
    }
    /* }@ */

    private void initCoordinatorAndTabLayout() {
        setContentView(R.layout.activity_main);

        /*
        InitTextView();
        InitImageView();
        InitFragment();
        InitViewPager();
        */

        mFragmentsList = new ArrayList<Fragment>();
        mViewPager = (ViewPager)findViewById(R.id.view_pager);
        FragmentManager fragmentManager = getFragmentManager();
        Fragment telephonyFragment = new TelephonyFragment();
        Fragment debugFragment = new DebugLogFragment();
        Fragment connectivityFragment = new ConnectivityFragment();
        Fragment hardwareFragment = new HardWareFragment();
        Fragment locationFragment = new LocationFragment();
        mFragmentsList.add(telephonyFragment);
        mFragmentsList.add(debugFragment);
        mFragmentsList.add(connectivityFragment);
        mFragmentsList.add(hardwareFragment);
        mFragmentsList.add(locationFragment);
        mViewPager.setAdapter(new TabFragmentPagerAdapter(fragmentManager,
                mFragmentsList, mTabTitle, mContext));
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(this.getResources().getString(R.string.tab_telephony)));
        tabLayout.addTab(tabLayout.newTab().setText(this.getResources().getString(R.string.tab_debug)));
        tabLayout.addTab(tabLayout.newTab().setText(this.getResources().getString(R.string.tab_connectivity)));
        tabLayout.addTab(tabLayout.newTab().setText(this.getResources().getString(R.string.tab_hardwaretest)));
        tabLayout.addTab(tabLayout.newTab().setText(this.getResources().getString(R.string.tab_location)));
        tabLayout.setupWithViewPager(mViewPager);
    }

    /*
    private void InitTextView(){
        teleTextView = (TextView)findViewById(R.id.tele_text);
        debugTextView = (TextView) findViewById(R.id.debug_text);
        connectivityTextView = (TextView)findViewById(R.id.connectivity_text);
        hardwareTextView = (TextView)findViewById(R.id.hardware_text);

        teleTextView.setOnClickListener(new MyOnClickListener(0));
        debugTextView.setOnClickListener(new MyOnClickListener(1));
        connectivityTextView.setOnClickListener(new MyOnClickListener(2));
        hardwareTextView.setOnClickListener(new MyOnClickListener(3));
    }

    private void InitImageView() {
        cursor = (ImageView) findViewById(R.id.cursor);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        bmpW = (screenW/4);
        setBmpW(cursor, bmpW);
        offset = 0;
        position_one = (int) (screenW / 4.0);
        position_two = position_one * 2;
        position_three = position_one * 3;
    }

    private void InitFragment(){
        mFragmentsList = new ArrayList<Fragment>();
        fragmentManager = getFragmentManager();
        Fragment telephonyFragment = new TelephonyFragment();
        Fragment debugFragment = new DebugLogFragment();
        Fragment connectivityFragment = new ConnectivityFragment();
        Fragment hardwareFragment = new HardWareFragment();

        mFragmentsList.add(telephonyFragment);
        mFragmentsList.add(debugFragment);
        mFragmentsList.add(connectivityFragment);
        mFragmentsList.add(hardwareFragment);
    }

    private void InitViewPager() {

        mViewPager = (ViewPager)findViewById(R.id.view_pager);
        mViewPager.setAdapter(new TabFragmentPagerAdapter(fragmentManager,
                mFragmentsList, mTabTitle, mContext));

        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setCurrentItem(0);
        resetTextViewTextColor();
        teleTextView.setTextColor(getResources().getColor(R.color.main_top_tab_color_2));

        mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    public class MyOnClickListener implements View.OnClickListener{
        private int index = 0 ;
        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            mViewPager.setCurrentItem(index);
        }
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageSelected(int position) {
            Animation animation = null ;
            Log.d(TAG, "MyOnPageChangeListener position:" + position + " currIndex: " + currIndex);
            switch (position){
                case 0:
                    if(currIndex == 1){
                        animation = new TranslateAnimation(position_one, 0, 0, 0);
                        resetTextViewTextColor();
                        teleTextView.setTextColor(getResources().getColor(R.color.main_top_tab_color_2));
                    }else if(currIndex == 2){
                        animation = new TranslateAnimation(position_two, 0, 0, 0);
                        resetTextViewTextColor();
                        teleTextView.setTextColor(getResources().getColor(R.color.main_top_tab_color_2));
                    }
                    break;
                case 1:
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(offset, position_one, 0, 0);
                        resetTextViewTextColor();
                        debugTextView.setTextColor(getResources().getColor(R.color.main_top_tab_color_2));
                    } else if (currIndex == 2) {
                        animation = new TranslateAnimation(position_two, position_one, 0, 0);
                        resetTextViewTextColor();
                        debugTextView.setTextColor(getResources().getColor(R.color.main_top_tab_color_2));
                    }
                    break;
                case 2:
                    if (currIndex == 3) {
                        animation = new TranslateAnimation(position_three, position_two, 0, 0);
                        resetTextViewTextColor();
                        connectivityTextView.setTextColor(getResources().getColor(R.color.main_top_tab_color_2));
                    } else if (currIndex == 1) {
                        animation = new TranslateAnimation(position_one, position_two, 0, 0);
                        resetTextViewTextColor();
                        connectivityTextView.setTextColor(getResources().getColor(R.color.main_top_tab_color_2));
                    }
                    break;
                case 3:
                    if (currIndex == 2) {
                        animation = new TranslateAnimation(position_two, position_three, 0, 0);
                        resetTextViewTextColor();
                        hardwareTextView.setTextColor(getResources().getColor(R.color.main_top_tab_color_2));
                    } else if (currIndex == 0) {
                        animation = new TranslateAnimation(position_one, position_two, 0, 0);
                        resetTextViewTextColor();
                        hardwareTextView.setTextColor(getResources().getColor(R.color.main_top_tab_color_2));
                    }
                    break;
            }
            currIndex = position;
            animation.setFillAfter(true);
            animation.setDuration(300);
            cursor.startAnimation(animation);

        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void setBmpW(ImageView imageView,int mWidth){
        ViewGroup.LayoutParams para;
        para = imageView.getLayoutParams();
        para.width = mWidth;
        imageView.setLayoutParams(para);
    }

    private void resetTextViewTextColor(){

        teleTextView.setTextColor(getResources().getColor(R.color.main_top_tab_color));
        debugTextView.setTextColor(getResources().getColor(R.color.main_top_tab_color));
        connectivityTextView.setTextColor(getResources().getColor(R.color.main_top_tab_color));
        hardwareTextView.setTextColor(getResources().getColor(R.color.main_top_tab_color));
    }
    */

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.version_info, menu);
        MenuItem item =menu.findItem(R.id.action_version_info);
        if (item != null) {
            item.setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_version_info:
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.version_info))
                .setMessage(getString(R.string.version_info_detail))
                .setPositiveButton(R.string.alertdialog_ok,
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }) .create();
                alertDialog.show();
                return true;
            default:
                Log.i(TAG, "default");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        mIsFirst = false;
        /* Sprd 910010: androidp Shutdown broadcast can just only registered by dynamic @{ */
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        /* }@ */
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
