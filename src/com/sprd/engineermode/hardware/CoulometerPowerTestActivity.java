package com.sprd.engineermode.hardware;

import android.app.Activity;
import android.view.View;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.content.Context;
import com.sprd.engineermode.R;
import android.os.PowerManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.app.AlarmManager;
import android.app.PendingIntent;
import java.util.Calendar;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import com.sprd.engineermode.utils.EMFileUtils;
import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.intf.IHardwareApi;

public class CoulometerPowerTestActivity extends Activity implements OnClickListener, OnSharedPreferenceChangeListener {

    private static final String TAG = "CoulometerPowerTestActivity";

    private Button timeBeginBtn1;
    private Button timeBeginBtn2;
    private Button timeBeginBtn3;
    private Button timeResultBtn1;
    private Button timeResultBtn2;
    private Button timeResultBtn3;

    private CheckBox mCheckbox1;
    private CheckBox mCheckbox2;
    private CheckBox mCheckbox3;

    private EditText endTimeEditText1;
    private EditText endTimeEditText2;
    private EditText endTimeEditText3;

    private EditText yearEditText1;
    private EditText monthEditText1;
    private EditText dayEditText1;
    private EditText hourEditText1;
    private EditText minuteEditText1;

    private EditText yearEditTextEnd1;
    private EditText monthEditTextEnd1;
    private EditText dayEditTextEnd1;
    private EditText hourEditTextEnd1;
    private EditText minuteEditTextEnd1;

    private EditText yearEditText2;
    private EditText monthEditText2;
    private EditText dayEditText2;
    private EditText hourEditText2;
    private EditText minuteEditText2;

    private EditText yearEditTextEnd2;
    private EditText monthEditTextEnd2;
    private EditText dayEditTextEnd2;
    private EditText hourEditTextEnd2;
    private EditText minuteEditTextEnd2;

    private EditText yearEditText3;
    private EditText monthEditText3;
    private EditText dayEditText3;
    private EditText hourEditText3;
    private EditText minuteEditText3;

    private EditText yearEditTextEnd3;
    private EditText monthEditTextEnd3;
    private EditText dayEditTextEnd3;
    private EditText hourEditTextEnd3;
    private EditText minuteEditTextEnd3;

    private TextView textStatus1;
    private TextView textStatus2;
    private TextView textStatus3;

    private int mYear = 2018;
    private int mMonth = 1;
    private int mDay = 1;
    private int mHour = 1;
    private int mMinute = 0;

    private int mYearEnd = 2018;
    private int mMonthEnd = 1;
    private int mDayEnd = 1;
    private int mHourEnd = 1;
    private int mMinuteEnd = 0;

    private String mY, mM, mD, mH, mMin, during1, during2, during3;
    private String mYEnd, mMEnd, mDEnd, mHEnd, mMinEnd;
    private Calendar c1, c2, c3, c1_end, c2_end, c3_end;
    private PendingIntent pi1, pi2, pi3, pi1_end, pi2_end, pi3_end;
    public static String[] mCCValue = new String[] {"0", "0", "0"};//electric current
    public static String[] mCCValueVol = new String[] {"0", "0", "0"};//voltage

    private String curDate, curDateEnd;
    private String startDateStr;

    private AlarmManager am;
    public static boolean[] timerStop = {true, true, true};
    public static ArrayList<String> testResults1 = new ArrayList<String>();
    public static ArrayList<String> testResults2 = new ArrayList<String>();
    public static ArrayList<String> testResults3 = new ArrayList<String>();

    public static ArrayList<String> testTimes1 = new ArrayList<String>();
    public static ArrayList<String> testTimes2 = new ArrayList<String>();
    public static ArrayList<String> testTimes3 = new ArrayList<String>();

    private static final int UPDATE_UI_END = 1;
    private static final int TWINTY_MINUTE = 20;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Intent intent;

    private int t1, t2, t3;

    private static final String CC_TEST_CMD_PATH = "/sys/class/power_supply/sprdfgu/";
    private IHardwareApi hwApi = CoreApi.getHardwareApi();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coulometer_power);

        c1 = Calendar.getInstance();
        c2 = Calendar.getInstance();
        c3 = Calendar.getInstance();
        c1_end = Calendar.getInstance();
        c2_end = Calendar.getInstance();
        c3_end = Calendar.getInstance();

        am = (AlarmManager)getSystemService(ALARM_SERVICE);

        initUI();

        preferences = this.getSharedPreferences("cc_status", MODE_PRIVATE);
        preferences.registerOnSharedPreferenceChangeListener(this);
        editor = preferences.edit();

        refreshStatus();
    }

    private void initUI() {
        timeBeginBtn1 = (Button) findViewById(R.id.time1_begin_btn);
        timeBeginBtn1.setOnClickListener(this);

        timeBeginBtn2 = (Button) findViewById(R.id.time2_begin_btn);
        timeBeginBtn2.setOnClickListener(this);

        timeBeginBtn3 = (Button) findViewById(R.id.time3_begin_btn);
        timeBeginBtn3.setOnClickListener(this);

        timeResultBtn1 = (Button) findViewById(R.id.time1_result_btn);
        timeResultBtn1.setOnClickListener(this);

        timeResultBtn2 = (Button) findViewById(R.id.time2_result_btn);
        timeResultBtn2.setOnClickListener(this);

        timeResultBtn3 = (Button) findViewById(R.id.time3_result_btn);
        timeResultBtn3.setOnClickListener(this);

        mCheckbox1 = (CheckBox) findViewById(R.id.checkbox1);
        mCheckbox1.setOnClickListener(this);

        mCheckbox2 = (CheckBox) findViewById(R.id.checkbox2);
        mCheckbox2.setOnClickListener(this);

        mCheckbox3 = (CheckBox) findViewById(R.id.checkbox3);
        mCheckbox3.setOnClickListener(this);

        yearEditText1 = (EditText) findViewById(R.id.time1_year_edit);
        yearEditText2 = (EditText) findViewById(R.id.time2_year_edit);
        yearEditText3 = (EditText) findViewById(R.id.time3_year_edit);

        monthEditText1 = (EditText) findViewById(R.id.time1_month_edit);
        monthEditText2 = (EditText) findViewById(R.id.time2_month_edit);
        monthEditText3 = (EditText) findViewById(R.id.time3_month_edit);

        dayEditText1 = (EditText) findViewById(R.id.time1_day_edit);
        dayEditText2 = (EditText) findViewById(R.id.time2_day_edit);
        dayEditText3 = (EditText) findViewById(R.id.time3_day_edit);

        hourEditText1 = (EditText) findViewById(R.id.time1_hour_edit);
        hourEditText2 = (EditText) findViewById(R.id.time2_hour_edit);
        hourEditText3 = (EditText) findViewById(R.id.time3_hour_edit);

        minuteEditText1 = (EditText) findViewById(R.id.time1_minute_edit);
        minuteEditText2 = (EditText) findViewById(R.id.time2_minute_edit);
        minuteEditText3 = (EditText) findViewById(R.id.time3_minute_edit);

        yearEditTextEnd1 = (EditText) findViewById(R.id.time1_year_edit_end);
        yearEditTextEnd2 = (EditText) findViewById(R.id.time2_year_edit_end);
        yearEditTextEnd3 = (EditText) findViewById(R.id.time3_year_edit_end);

        monthEditTextEnd1 = (EditText) findViewById(R.id.time1_month_edit_end);
        monthEditTextEnd2 = (EditText) findViewById(R.id.time2_month_edit_end);
        monthEditTextEnd3 = (EditText) findViewById(R.id.time3_month_edit_end);

        dayEditTextEnd1 = (EditText) findViewById(R.id.time1_day_edit_end);
        dayEditTextEnd2 = (EditText) findViewById(R.id.time2_day_edit_end);
        dayEditTextEnd3 = (EditText) findViewById(R.id.time3_day_edit_end);

        hourEditTextEnd1 = (EditText) findViewById(R.id.time1_hour_edit_end);
        hourEditTextEnd2 = (EditText) findViewById(R.id.time2_hour_edit_end);
        hourEditTextEnd3 = (EditText) findViewById(R.id.time3_hour_edit_end);

        minuteEditTextEnd1 = (EditText) findViewById(R.id.time1_minute_edit_end);
        minuteEditTextEnd2 = (EditText) findViewById(R.id.time2_minute_edit_end);
        minuteEditTextEnd3 = (EditText) findViewById(R.id.time3_minute_edit_end);

        endTimeEditText1 = (EditText) findViewById(R.id.time1_end_edit);
        endTimeEditText2 = (EditText) findViewById(R.id.time2_end_edit);
        endTimeEditText3 = (EditText) findViewById(R.id.time3_end_edit);

        textStatus1 = (TextView) findViewById(R.id.textStatus1);
        textStatus2 = (TextView) findViewById(R.id.textStatus2);
        textStatus3 = (TextView) findViewById(R.id.textStatus3);

        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm");
        curDate = sDateFormat.format(new java.util.Date());

        String [] strings = curDate.split(":");
        if (strings.length == 5) {
            yearEditText1.setText(strings[0]);
            yearEditText2.setText(strings[0]);
            yearEditText3.setText(strings[0]);

            monthEditText1.setText(strings[1]);
            monthEditText2.setText(strings[1]);
            monthEditText3.setText(strings[1]);

            dayEditText1.setText(strings[2]);
            dayEditText2.setText(strings[2]);
            dayEditText3.setText(strings[2]);

            hourEditText1.setText(strings[3]);
            hourEditText2.setText(strings[3]);
            hourEditText3.setText(strings[3]);

            minuteEditText1.setText(strings[4]);
            minuteEditText2.setText(strings[4]);
            minuteEditText3.setText(strings[4]);

            yearEditTextEnd1.setText(strings[0]);
            yearEditTextEnd2.setText(strings[0]);
            yearEditTextEnd3.setText(strings[0]);

            monthEditTextEnd1.setText(strings[1]);
            monthEditTextEnd2.setText(strings[1]);
            monthEditTextEnd3.setText(strings[1]);

            dayEditTextEnd1.setText(strings[2]);
            dayEditTextEnd2.setText(strings[2]);
            dayEditTextEnd3.setText(strings[2]);

            hourEditTextEnd1.setText(strings[3]);
            hourEditTextEnd2.setText(strings[3]);
            hourEditTextEnd3.setText(strings[3]);

            minuteEditTextEnd1.setText(strings[4]);
            minuteEditTextEnd2.setText(strings[4]);
            minuteEditTextEnd3.setText(strings[4]);
        }
    }

    private void refreshStatus() {
        boolean timeStop1 = preferences.getBoolean("time1_stop", true);
        if (!timeStop1) {
            yearEditText1.setText(preferences.getString("time1_year", "2018"));
            monthEditText1.setText(preferences.getString("time1_month", "1"));
            dayEditText1.setText(preferences.getString("time1_day", "1"));
            hourEditText1.setText(preferences.getString("time1_hour", "1"));
            minuteEditText1.setText(preferences.getString("time1_minute", "1"));

            yearEditTextEnd1.setText(preferences.getString("timeEnd1_year", "2018"));
            monthEditTextEnd1.setText(preferences.getString("timeEnd1_month", "1"));
            dayEditTextEnd1.setText(preferences.getString("timeEnd1_day", "1"));
            hourEditTextEnd1.setText(preferences.getString("timeEnd1_hour", "1"));
            minuteEditTextEnd1.setText(preferences.getString("timeEnd1_minute", "1"));

            endTimeEditText1.setText(preferences.getString("time1_during", "1"));
            textStatus1.setText(getString(R.string.coulometer_testing));
            timeBeginBtn1.setEnabled(false);
        } else {
            textStatus1.setText(getString(R.string.coulometer_test_stop));
            timeBeginBtn1.setEnabled(true);
        }
        boolean timeStop2 = preferences.getBoolean("time2_stop", true);
        if (!timeStop2) {
            yearEditText2.setText(preferences.getString("time2_year", "2018"));
            monthEditText2.setText(preferences.getString("time2_month", "1"));
            dayEditText2.setText(preferences.getString("time2_day", "1"));
            hourEditText2.setText(preferences.getString("time2_hour", "1"));
            minuteEditText2.setText(preferences.getString("time2_minute", "1"));

            yearEditTextEnd2.setText(preferences.getString("timeEnd2_year", "2018"));
            monthEditTextEnd2.setText(preferences.getString("timeEnd2_month", "1"));
            dayEditTextEnd2.setText(preferences.getString("timeEnd2_day", "1"));
            hourEditTextEnd2.setText(preferences.getString("timeEnd2_hour", "1"));
            minuteEditTextEnd2.setText(preferences.getString("timeEnd2_minute", "1"));

            endTimeEditText2.setText(preferences.getString("time2_during", "1"));
            textStatus2.setText(getString(R.string.coulometer_testing));
            timeBeginBtn2.setEnabled(false);
        } else {
            textStatus2.setText(getString(R.string.coulometer_test_stop));
            timeBeginBtn2.setEnabled(true);
        }
        boolean timeStop3 = preferences.getBoolean("time3_stop", true);
        if (!timeStop3) {
            yearEditText3.setText(preferences.getString("time3_year", "2018"));
            monthEditText3.setText(preferences.getString("time3_month", "1"));
            dayEditText3.setText(preferences.getString("time3_day", "1"));
            hourEditText3.setText(preferences.getString("time3_hour", "1"));
            minuteEditText3.setText(preferences.getString("time3_minute", "1"));

            yearEditTextEnd3.setText(preferences.getString("timeEnd3_year", "2018"));
            monthEditTextEnd3.setText(preferences.getString("timeEnd3_month", "1"));
            dayEditTextEnd3.setText(preferences.getString("timeEnd3_day", "1"));
            hourEditTextEnd3.setText(preferences.getString("timeEnd3_hour", "1"));
            minuteEditTextEnd3.setText(preferences.getString("timeEnd3_minute", "1"));

            endTimeEditText3.setText(preferences.getString("time3_during", "1"));
            textStatus3.setText(getString(R.string.coulometer_testing));
            timeBeginBtn3.setEnabled(false);
        } else {
            textStatus3.setText(getString(R.string.coulometer_test_stop));
            timeBeginBtn3.setEnabled(true);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.i(TAG, "instance variable key=" + key);
        if (key.equals("time1_stop") && preferences.getBoolean("time1_stop", true)) {
            timeBeginBtn1.setEnabled(true);
            timeResultBtn1.setEnabled(true);
            textStatus1.setText(getString(R.string.coulometer_test_stop));
        } else if (key.equals("time2_stop") && preferences.getBoolean("time2_stop", true)) {
            timeBeginBtn2.setEnabled(true);
            timeResultBtn2.setEnabled(true);
            textStatus2.setText(getString(R.string.coulometer_test_stop));
        } else if (key.equals("time3_stop") && preferences.getBoolean("time3_stop", true)) {
            timeBeginBtn3.setEnabled(true);
            timeResultBtn3.setEnabled(true);
            textStatus3.setText(getString(R.string.coulometer_test_stop));
        }
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        if (preferences.getBoolean("time1_stop", true)) {
            timeBeginBtn1.setEnabled(true);
            timeResultBtn1.setEnabled(true);
            textStatus1.setText(getString(R.string.coulometer_test_stop));
        }
        if (preferences.getBoolean("time2_stop", true)) {
            timeBeginBtn2.setEnabled(true);
            timeResultBtn2.setEnabled(true);
            textStatus2.setText(getString(R.string.coulometer_test_stop));
        }
        if (preferences.getBoolean("time3_stop", true)) {
            timeBeginBtn3.setEnabled(true);
            timeResultBtn3.setEnabled(true);
            textStatus3.setText(getString(R.string.coulometer_test_stop));
        }
        super.onResume();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (!timerStop[0]) {
            editor.putBoolean("time1_stop", timerStop[0]);
            editor.putString("time1_year", yearEditText1.getText().toString());
            editor.putString("time1_month", monthEditText1.getText().toString());
            editor.putString("time1_day", dayEditText1.getText().toString());
            editor.putString("time1_hour", hourEditText1.getText().toString());
            editor.putString("time1_minute", minuteEditText1.getText().toString());
            editor.putString("time1_during", endTimeEditText1.getText().toString());

            editor.putString("timeEnd1_year", yearEditTextEnd1.getText().toString());
            editor.putString("timeEnd1_month", monthEditTextEnd1.getText().toString());
            editor.putString("timeEnd1_day", dayEditTextEnd1.getText().toString());
            editor.putString("timeEnd1_hour", hourEditTextEnd1.getText().toString());
            editor.putString("timeEnd1_minute", minuteEditTextEnd1.getText().toString());
            editor.commit();
        }
        if (!timerStop[1]) {
            editor.putBoolean("time2_stop", timerStop[1]);
            editor.putString("time2_year", yearEditText2.getText().toString());
            editor.putString("time2_month", monthEditText2.getText().toString());
            editor.putString("time2_day", dayEditText2.getText().toString());
            editor.putString("time2_hour", hourEditText2.getText().toString());
            editor.putString("time2_minute", minuteEditText2.getText().toString());
            editor.putString("time2_during", endTimeEditText2.getText().toString());

            editor.putString("timeEnd2_year", yearEditTextEnd2.getText().toString());
            editor.putString("timeEnd2_month", monthEditTextEnd2.getText().toString());
            editor.putString("timeEnd2_day", dayEditTextEnd2.getText().toString());
            editor.putString("timeEnd2_hour", hourEditTextEnd2.getText().toString());
            editor.putString("timeEnd2_minute", minuteEditTextEnd2.getText().toString());
            editor.commit();
        }
        if (!timerStop[2]) {
            editor.putBoolean("time3_stop", timerStop[2]);
            editor.putString("time3_year", yearEditText3.getText().toString());
            editor.putString("time3_month", monthEditText3.getText().toString());
            editor.putString("time3_day", dayEditText3.getText().toString());
            editor.putString("time3_hour", hourEditText3.getText().toString());
            editor.putString("time3_minute", minuteEditText3.getText().toString());
            editor.putString("time3_during", endTimeEditText3.getText().toString());

            editor.putString("timeEnd3_year", yearEditTextEnd3.getText().toString());
            editor.putString("timeEnd3_month", monthEditTextEnd3.getText().toString());
            editor.putString("timeEnd3_day", dayEditTextEnd3.getText().toString());
            editor.putString("timeEnd3_hour", hourEditTextEnd3.getText().toString());
            editor.putString("timeEnd3_minute", minuteEditTextEnd3.getText().toString());
            editor.commit();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
        case R.id.time1_begin_btn:
            Log.d(TAG, "onClick time1_begin_btn");

            mY = yearEditText1.getText().toString();
            mM = monthEditText1.getText().toString();
            mD = dayEditText1.getText().toString();
            mH = hourEditText1.getText().toString();
            mMin = minuteEditText1.getText().toString();
            during1 = endTimeEditText1.getText().toString();

            if (mY.trim().equals("")) mY = "2018";
            if (mM.trim().equals("")) mM = "1";
            if (mD.trim().equals("")) mD = "1";
            if (mH.trim().equals("")) mH = "1";
            if (mMin.trim().equals("")) mMin = "0";
            if (during1.trim().equals("")) during1 = "1.5";
            if (!checkNumberFormatOk(mY, mM, mD, mH, mMin, during1)) {
                break;
            }
            mYear = Integer.parseInt(mY);
            mMonth = Integer.parseInt(mM) - 1;
            mDay = Integer.parseInt(mD);
            mHour = Integer.parseInt(mH);
            mMinute = Integer.parseInt(mMin);
            c1.set(mYear, mMonth, mDay, mHour, mMinute, 0);

            curDate = mY + ":" + mM + ":" + mD + ":" + mH + ":" + mMin;
            SimpleDateFormat sDateFormat1 = new SimpleDateFormat("yyyy:MM:dd:HH:mm");
            startDateStr = sDateFormat1.format(new java.util.Date());
            if (TimeCompare(curDate, startDateStr)) {
            //    createAlarm(c1, during1, "time1");
            //    editor.putBoolean("time1_stop", false);
            //    editor.commit();
            } else {
                createDialog(getString(R.string.start_time_error));
                break;
            }
            mYEnd = yearEditTextEnd1.getText().toString();
            mMEnd = monthEditTextEnd1.getText().toString();
            mDEnd = dayEditTextEnd1.getText().toString();
            mHEnd = hourEditTextEnd1.getText().toString();
            mMinEnd = minuteEditTextEnd1.getText().toString();

            if (mYEnd.trim().equals("")) mYEnd = "2018";
            if (mMEnd.trim().equals("")) mMEnd = "1";
            if (mDEnd.trim().equals("")) mDEnd = "1";
            if (mHEnd.trim().equals("")) mHEnd = "1";
            if (mMinEnd.trim().equals("")) mMinEnd = "0";
            if (!checkNumberFormatOk(mYEnd, mMEnd, mDEnd, mHEnd, mMinEnd, during1)) {
                break;
            }
            mYearEnd = Integer.parseInt(mYEnd);
            mMonthEnd = Integer.parseInt(mMEnd) - 1;
            mDayEnd = Integer.parseInt(mDEnd);
            mHourEnd = Integer.parseInt(mHEnd);
            mMinuteEnd = Integer.parseInt(mMinEnd);
            c1_end.set(mYearEnd, mMonthEnd, mDayEnd, mHourEnd, mMinuteEnd, 10);

            curDateEnd = mYEnd + ":" + mMEnd + ":" + mDEnd + ":" + mHEnd + ":" + mMinEnd;
            if (TimeCompare(curDateEnd, curDate)) {
                t1 = DurationTimeMinutes(curDateEnd, curDate);
                createAlarm(c1, String.valueOf(t1), "time1");
                createAlarm(c1_end, String.valueOf(t1), "time1_end");
                editor.putBoolean("time1_stop", false);
                editor.commit();
            } else {
                createDialog(getString(R.string.end_time_error));
            }
            break;
        case R.id.time1_result_btn:
            Log.d(TAG, "onClick time1_result_btn");
            if (!timerStop[0]) {
                canStopDialog(getString(R.string.notice_testing_show), "time1");
            } else {
                showTestResult("time1");
            }
            break;
        case R.id.time2_begin_btn:
            Log.d(TAG, "onClick time2_begin_btn");

            mY = yearEditText2.getText().toString();
            mM = monthEditText2.getText().toString();
            mD = dayEditText2.getText().toString();
            mH = hourEditText2.getText().toString();
            mMin = minuteEditText2.getText().toString();
            during2 = endTimeEditText2.getText().toString();

            if (mY.trim().equals("")) mY = "2018";
            if (mM.trim().equals("")) mM = "1";
            if (mD.trim().equals("")) mD = "1";
            if (mH.trim().equals("")) mH = "1";
            if (mMin.trim().equals("")) mMin = "0";
            if (during2.trim().equals("")) during2 = "1.5";
            if (!checkNumberFormatOk(mY, mM, mD, mH, mMin, during2)) {
                break;
            }
            mYear = Integer.parseInt(mY);
            mMonth = Integer.parseInt(mM) - 1;
            mDay = Integer.parseInt(mD);
            mHour = Integer.parseInt(mH);
            mMinute = Integer.parseInt(mMin);
            c2.set(mYear, mMonth, mDay, mHour, mMinute, 0);
            curDate = mY + ":" + mM + ":" + mD + ":" + mH + ":" + mMin;
            SimpleDateFormat sDateFormat2 = new SimpleDateFormat("yyyy:MM:dd:HH:mm");
            startDateStr = sDateFormat2.format(new java.util.Date());
            if (TimeCompare(curDate, startDateStr)) {
                //createAlarm(c2, during2, "time2");
                //editor.putBoolean("time2_stop", false);
                //editor.commit();
            } else {
                createDialog(getString(R.string.start_time_error));
                break;
            }
            mYEnd = yearEditTextEnd2.getText().toString();
            mMEnd = monthEditTextEnd2.getText().toString();
            mDEnd = dayEditTextEnd2.getText().toString();
            mHEnd = hourEditTextEnd2.getText().toString();
            mMinEnd = minuteEditTextEnd2.getText().toString();

            if (mYEnd.trim().equals("")) mYEnd = "2018";
            if (mMEnd.trim().equals("")) mMEnd = "1";
            if (mDEnd.trim().equals("")) mDEnd = "1";
            if (mHEnd.trim().equals("")) mHEnd = "1";
            if (mMinEnd.trim().equals("")) mMinEnd = "0";
            if (!checkNumberFormatOk(mYEnd, mMEnd, mDEnd, mHEnd, mMinEnd, during2)) {
                break;
            }
            mYearEnd = Integer.parseInt(mYEnd);
            mMonthEnd = Integer.parseInt(mMEnd) - 1;
            mDayEnd = Integer.parseInt(mDEnd);
            mHourEnd = Integer.parseInt(mHEnd);
            mMinuteEnd = Integer.parseInt(mMinEnd);
            c2_end.set(mYearEnd, mMonthEnd, mDayEnd, mHourEnd, mMinuteEnd, 10);

            curDateEnd = mYEnd + ":" + mMEnd + ":" + mDEnd + ":" + mHEnd + ":" + mMinEnd;
            if (TimeCompare(curDateEnd, curDate)) {
                t2 = DurationTimeMinutes(curDateEnd, curDate);
                createAlarm(c2, String.valueOf(t2), "time2");
                createAlarm(c2_end, String.valueOf(t2), "time2_end");
                editor.putBoolean("time2_stop", false);
                editor.commit();
            } else {
                createDialog(getString(R.string.end_time_error));
            }
            break;
        case R.id.time2_result_btn:
            Log.d(TAG, "onClick time2_result_btn");
            if (!timerStop[1]) {
                canStopDialog(getString(R.string.notice_testing_show), "time2");
            } else {
                showTestResult("time2");
            }
            break;
        case R.id.time3_begin_btn:
            Log.d(TAG, "onClick time3_begin_btn");

            mY = yearEditText3.getText().toString();
            mM = monthEditText3.getText().toString();
            mD = dayEditText3.getText().toString();
            mH = hourEditText3.getText().toString();
            mMin = minuteEditText3.getText().toString();
            during3 = endTimeEditText3.getText().toString();

            if (mY.trim().equals("")) mY = "2018";
            if (mM.trim().equals("")) mM = "1";
            if (mD.trim().equals("")) mD = "1";
            if (mH.trim().equals("")) mH = "1";
            if (mMin.trim().equals("")) mMin = "0";
            if (during3.trim().equals("")) during3 = "1.5";
            if (!checkNumberFormatOk(mY, mM, mD, mH, mMin, during3)) {
                break;
            }
            mYear = Integer.parseInt(mY);
            mMonth = Integer.parseInt(mM) - 1;
            mDay = Integer.parseInt(mD);
            mHour = Integer.parseInt(mH);
            mMinute = Integer.parseInt(mMin);
            c3.set(mYear, mMonth, mDay, mHour, mMinute, 0);
            curDate = mY + ":" + mM + ":" + mD + ":" + mH + ":" + mMin;
            SimpleDateFormat sDateFormat3 = new SimpleDateFormat("yyyy:MM:dd:HH:mm");
            startDateStr = sDateFormat3.format(new java.util.Date());
            if (TimeCompare(curDate, startDateStr)) {
                //createAlarm(c3, during3, "time3");
                //editor.putBoolean("time3_stop", false);
                //editor.commit();
            } else {
                createDialog(getString(R.string.start_time_error));
                break;
            }
            mYEnd = yearEditTextEnd3.getText().toString();
            mMEnd = monthEditTextEnd3.getText().toString();
            mDEnd = dayEditTextEnd3.getText().toString();
            mHEnd = hourEditTextEnd3.getText().toString();
            mMinEnd = minuteEditTextEnd3.getText().toString();

            if (mYEnd.trim().equals("")) mYEnd = "2018";
            if (mMEnd.trim().equals("")) mMEnd = "1";
            if (mDEnd.trim().equals("")) mDEnd = "1";
            if (mHEnd.trim().equals("")) mHEnd = "1";
            if (mMinEnd.trim().equals("")) mMinEnd = "0";
            if (!checkNumberFormatOk(mYEnd, mMEnd, mDEnd, mHEnd, mMinEnd, during3)) {
                break;
            }
            mYearEnd = Integer.parseInt(mYEnd);
            mMonthEnd = Integer.parseInt(mMEnd) - 1;
            mDayEnd = Integer.parseInt(mDEnd);
            mHourEnd = Integer.parseInt(mHEnd);
            mMinuteEnd = Integer.parseInt(mMinEnd);
            c3_end.set(mYearEnd, mMonthEnd, mDayEnd, mHourEnd, mMinuteEnd, 10);

            curDateEnd = mYEnd + ":" + mMEnd + ":" + mDEnd + ":" + mHEnd + ":" + mMinEnd;
            if (TimeCompare(curDateEnd, curDate)) {
                t3 = DurationTimeMinutes(curDateEnd, curDate);
                createAlarm(c3, String.valueOf(t3), "time3");
                createAlarm(c3_end, String.valueOf(t3), "time3_end");
                editor.putBoolean("time3_stop", false);
                editor.commit();
            } else {
                createDialog(getString(R.string.end_time_error));
            }
            break;
        case R.id.time3_result_btn:
            Log.d(TAG, "onClick time3_result_btn");
            if (!timerStop[2]) {
                canStopDialog(getString(R.string.notice_testing_show), "time3");
            } else {
                showTestResult("time3");
            }
            break;
        case R.id.checkbox1:
            Log.d(TAG, "onClick checkbox1");
            during1 = endTimeEditText1.getText().toString();
            if (during1.trim().equals("")) during1 = "1.5";
            try {
                if (t1 <= TWINTY_MINUTE) {
                    Toast.makeText(CoulometerPowerTestActivity.this, this.getString(R.string.less_than_twinty_toast), Toast.LENGTH_LONG).show();
                    mCheckbox1.setChecked(false);
                }
            } catch (java.lang.NumberFormatException e) {
                e.printStackTrace();
            }
            break;
        case R.id.checkbox2:
            Log.d(TAG, "onClick checkbox2");
            during2 = endTimeEditText2.getText().toString();
            if (during2.trim().equals("")) during2 = "1.5";
            try {
                if (t2 <= TWINTY_MINUTE) {
                    Toast.makeText(CoulometerPowerTestActivity.this, getString(R.string.less_than_twinty_toast), Toast.LENGTH_LONG).show();
                    mCheckbox2.setChecked(false);
                }
            } catch (java.lang.NumberFormatException e) {
                e.printStackTrace();
            }
            break;
        case R.id.checkbox3:
            Log.d(TAG, "onClick checkbox3");
            during3 = endTimeEditText3.getText().toString();
            if (during3.trim().equals("")) during3 = "1.5";
            try {
                if (t3 <= TWINTY_MINUTE) {
                    Toast.makeText(CoulometerPowerTestActivity.this, getString(R.string.less_than_twinty_toast), Toast.LENGTH_LONG).show();
                    mCheckbox3.setChecked(false);
                }
            } catch (java.lang.NumberFormatException e) {
                e.printStackTrace();
            }
            break;
        default:
            break;
        }
    }

    private void createAlarm(Calendar calendar, String dur, String timePeriod) {
        Log.d(TAG, "createAlarm timePeriod: " + timePeriod + " dur: " + dur);
        boolean isCCTestCmdExist = EMFileUtils.isFileDirExits(CC_TEST_CMD_PATH);
        intent = new Intent(this, CoulometerPowerService.class);
        intent.putExtra("duration", dur);
        intent.putExtra("time", timePeriod);
        try {
            if (timePeriod.equals("time1")) {
                pi1 = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi1);
                timeBeginBtn1.setEnabled(false);
                textStatus1.setText(getString(R.string.coulometer_testing));
                timerStop[0] = false;
                /* SPRD 1016593 : Coulometer Power Test Mode for kernel 4.14 @{ */
                if (!isCCTestCmdExist) {
                    mCCValue[0] = hwApi.coulometerPowerApi().getCcResultNewKernel();
                    Log.d(TAG, "time1 startEnergy : " + mCCValue[0]);
                }
                /* }@ */
            } else if (timePeriod.equals("time1_end")) {
                pi1_end = PendingIntent.getService(this, 4, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi1_end);
            } else if (timePeriod.equals("time2")) {
                pi2 = PendingIntent.getService(this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi2);
                timeBeginBtn2.setEnabled(false);
                textStatus2.setText(getString(R.string.coulometer_testing));
                timerStop[1] = false;
                /* SPRD 1016593 : Coulometer Power Test Mode for kernel 4.14 @{ */
                if (!isCCTestCmdExist) {
                    mCCValue[1] = hwApi.coulometerPowerApi().getCcResultNewKernel();
                    Log.d(TAG, "time2 startEnergy : " + mCCValue[1]);
                }/* }@ */
            } else if (timePeriod.equals("time2_end")) {
                pi2_end = PendingIntent.getService(this, 5, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi2_end);
            } else if (timePeriod.equals("time3")) {
                pi3 = PendingIntent.getService(this, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi3);
                timeBeginBtn3.setEnabled(false);
                textStatus3.setText(getString(R.string.coulometer_testing));
                timerStop[2] = false;
                /* SPRD 1016593 : Coulometer Power Test Mode for kernel 4.14 @{ */
                if (!isCCTestCmdExist) {
                    mCCValue[2] = hwApi.coulometerPowerApi().getCcResultNewKernel();
                    Log.d(TAG, "time3 startEnergy : " + mCCValue[2]);
                }
                /* }@ */
            } else if (timePeriod.equals("time3_end")) {
                pi3_end = PendingIntent.getService(this, 6, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi3_end);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int DurationTimeMinutes(String curTime, String startTime) {
        SimpleDateFormat CurrentTime = new SimpleDateFormat("yyyy:MM:dd:HH:mm");
        int mDuration = 0;
        try {
            long endTime = CurrentTime.parse(curTime).getTime();
            long beginTime = CurrentTime.parse(startTime).getTime();
            mDuration = (int)((endTime - beginTime) / 1000 / 60);
            Log.d(TAG, "DurationTimeMinutes mDuration " + mDuration);
        } catch (Exception e) {
            Log.d(TAG, "DurationTimeMinutes Exception " + e);
            return 0;
        }
        return mDuration;
 }

    private boolean TimeCompare(String curTime, String startTime) {
        SimpleDateFormat CurrentTime = new SimpleDateFormat("yyyy:MM:dd:HH:mm");
        try {
            Date beginTime = CurrentTime.parse(curTime);
            Date endTime = CurrentTime.parse(startTime);

            if(endTime.getTime() < beginTime.getTime()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.d(TAG, "TimeCompare Exception " + e);
        }
        return true;
    }

    private void cancelAlarm(String timePeriod) {
        if (timePeriod.equals("time1") && pi1 != null) {
            am.cancel(pi1);
            am.cancel(pi1_end);
        } else if (timePeriod.equals("time2") && pi2 != null) {
            am.cancel(pi2);
            am.cancel(pi2_end);
        } else if (timePeriod.equals("time3") && pi3 != null) {
            am.cancel(pi3);
            am.cancel(pi3_end);
        }
    }
    private boolean checkNumberFormatOk(String y, String m, String d, String h, String min, String during) {
        if (y.length() != 4) {
            createDialog(getString(R.string.year_format_err));
            return false;
        }
        if (m.length() > 2) {
            createDialog(getString(R.string.month_format_err));
            return false;
        }
        if (d.length() > 2) {
            createDialog(getString(R.string.day_format_err));
            return false;
        }
        if (h.length() > 2) {
            createDialog(getString(R.string.hour_format_err));
            return false;
        }
        if (min.length() > 2) {
            createDialog(getString(R.string.minute_format_err));
            return false;
        }
        if (during.length() == 0) {
            createDialog(getString(R.string.during_empty_err));
            return false;
        }
        return true;
    }

    private void createDialog(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(CoulometerPowerTestActivity.this)
                .setTitle(getString(R.string.misc_dialog_warn))
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.alertdialog_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                            }
                        }).create();
        alertDialog.show();
    }

    public void stopTest(String whichTime) {
        if (whichTime.equals("time1")) {
            Log.d(TAG, "stopTest time1");
            timerStop[0] = true;
            cancelAlarm("time1");
            timeBeginBtn1.setEnabled(true);
            textStatus1.setText(getString(R.string.coulometer_not_test));
        } else if (whichTime.equals("time2")) {
            Log.d(TAG, "stopTest time2");
            timerStop[1] = true;
            cancelAlarm("time2");
            timeBeginBtn2.setEnabled(true);
            textStatus2.setText(getString(R.string.coulometer_not_test));
        } else if (whichTime.equals("time3")) {
            Log.d(TAG, "stopTest time3");
            timerStop[2] = true;
            cancelAlarm("time3");
            timeBeginBtn3.setEnabled(true);
            textStatus3.setText(getString(R.string.coulometer_not_test));
        }
    }

    public void showTestResult(String whichTime) {
        if (whichTime.equals("time1")) {
            if (testResults1.size() < 1) {
                createDialog(getString(R.string.notice_data_empty));
            } else {
                Intent mIntent1 = new Intent(CoulometerPowerTestActivity.this,
                        CoulometerPowerResultActivity.class);
                mIntent1.putExtra("time", "time1");
                if (mCheckbox1.isChecked()) {
                    mIntent1.putExtra("showChart", "yes");
                } else {
                    mIntent1.putExtra("showChart", "no");
                }
                startActivity(mIntent1);
            }
        } else if (whichTime.equals("time2")) {
            if (testResults2.size() < 1) {
                createDialog(getString(R.string.notice_data_empty));
            } else {
                Intent mIntent2 = new Intent(CoulometerPowerTestActivity.this,
                        CoulometerPowerResultActivity.class);
                mIntent2.putExtra("time", "time2");
                if (mCheckbox2.isChecked()) {
                    mIntent2.putExtra("showChart", "yes");
                } else {
                    mIntent2.putExtra("showChart", "no");
                }
                startActivity(mIntent2);
            }
        } else if (whichTime.equals("time3")) {
            if (testResults3.size() < 1) {
                createDialog(getString(R.string.notice_data_empty));
            } else {
                Intent mIntent3 = new Intent(CoulometerPowerTestActivity.this,
                        CoulometerPowerResultActivity.class);
                mIntent3.putExtra("time", "time3");
                if (mCheckbox3.isChecked()) {
                    mIntent3.putExtra("showChart", "yes");
                } else {
                    mIntent3.putExtra("showChart", "no");
                }
                startActivity(mIntent3);
            }
        }
    }

    private void canStopDialog(String message, String item) {
        AlertDialog alertDialog = new AlertDialog.Builder(CoulometerPowerTestActivity.this)
                .setTitle(getString(R.string.misc_dialog_warn))
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.alertdialog_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                showTestResult(item);
                            }
                        }).setNegativeButton(R.string.alertdialog_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                            }
                        }).create();
        alertDialog.show();
    }
}