package com.sprd.engineermode.telephony.netinfostatistics;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Build;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.design.widget.TabLayout;
import android.util.Log;
import com.sprd.engineermode.R;
import com.sprd.engineermode.TabFragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;

public class NetinfoStatisticsActivity extends AppCompatActivity {

    private static final String TAG = "NetinfoStatisticsActivity";
    private ArrayList<Fragment> mFragmentsList;
    private List<String> mTitleList = new ArrayList<String>();
    private ViewPager mViewPager;
    private Context mContext;

    /*
    public interface TabState {
        public static int TAB_RESELECT_INDEX = 0;  //Reselect
        public static int TAB_HANDOVER_INDEX = 1; //handOver
        public static int TAB_ATTACH_INDEX = 2; //Attachtime
        public static int TAB_DROP_INDEX = 3; //droptimes
        public static int TAB_CARRIER_HANDOVER_INDEX = 4; //carrier handover times
    }
    */

    private int[] mTabTitle = new int[] {
            R.string.tab_reselect,
            R.string.tab_handover,
            R.string.tab_attachTime,
            R.string.tab_dropTimes,
            //R.string.tab_carrierHandoverTimes
    };

    //private int mCurrentTab = TabState.TAB_RESELECT_INDEX;
    public static String CURRENT_TAB = "android.app.engmode.currenttab";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setToolbarTabLayout();
        initCoordinatorAndTabLayout();
    }

    private void setToolbarTabLayout() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
    }

    private void initCoordinatorAndTabLayout() {
        setContentView(R.layout.activity_netinfostatistics);
        mFragmentsList = new ArrayList<Fragment>();
        mViewPager = (ViewPager)findViewById(R.id.view_pager);
        FragmentManager fragmentManager = getFragmentManager();
        Fragment reselectFragment = new ReselectFragment();
        Fragment handOverFragment = new HandOverFragment();
        Fragment attachTimeFragment = new AttachTimeFragment();
        Fragment dropTimesFragment = new DropTimesFragment();
        //Fragment carrierHandoverTimesFragment = new CarrierHandoverTimesFragment();

        mFragmentsList.add(reselectFragment);
        mFragmentsList.add(handOverFragment);
        mFragmentsList.add(attachTimeFragment);
        mFragmentsList.add(dropTimesFragment);
        //mFragmentsList.add(carrierHandoverTimesFragment);
        mViewPager.setAdapter(new TabFragmentPagerAdapter(fragmentManager,
                mFragmentsList, mTabTitle, mContext));

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(this.getResources().getString(R.string.tab_reselect)));
        tabLayout.addTab(tabLayout.newTab().setText(this.getResources().getString(R.string.tab_handover)));
        tabLayout.addTab(tabLayout.newTab().setText(this.getResources().getString(R.string.tab_attachTime)));
        tabLayout.addTab(tabLayout.newTab().setText(this.getResources().getString(R.string.tab_dropTimes)));
        //tabLayout.addTab(tabLayout.newTab().setText(this.getResources().getString(R.string.tab_carrierHandoverTimes)));
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = this.getIntent();
        int simindex = intent.getIntExtra("simindex", -1);
        setTitle("SIM" + simindex);
        Log.d(TAG, "onResume simindex=" + simindex);
    }
}

