package com.sprd.engineermode.hardware;

import com.sprd.engineermode.hardware.LineChartView;
import android.app.Activity;
import android.os.Bundle;
import java.util.ArrayList;
import android.view.WindowManager;
import android.content.Context;
import java.text.SimpleDateFormat;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.util.Log;
import android.view.View;

import com.sprd.engineermode.R;

public class CoulometerPowerResultActivity extends Activity implements OnClickListener {

    private static final String TAG = "CoulometerPowerResultActivity";

    private LineChartView lineChartView;
    private WindowManager wm;
    private Button clearBtn;
    private String time;
    private String[] yLabels = new String[] { "", "100", "200", "300", "400", "500" };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.coulometer_power_chart);
        wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);

        clearBtn = (Button) findViewById(R.id.clear_btn);
        clearBtn.setOnClickListener(this);

        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();

        lineChartView = (LineChartView) findViewById(R.id.chart_line);

        String title = getString(R.string.cc_test_chart);
        String titleVol = getString(R.string.cc_test_chart);
        time = getIntent().getStringExtra("time");
        String showChart = getIntent().getStringExtra("showChart");
        try {
            if (time.equals("time1")) {
                title = CoulometerPowerTestActivity.mCCValue[0];
                titleVol = CoulometerPowerTestActivity.mCCValueVol[0];
                lineChartView.SetInfo(CoulometerPowerTestActivity.testTimes1, yLabels, CoulometerPowerTestActivity.testResults1, title, titleVol, width, height, showChart);
            } else if (time.equals("time2")) {
                title = CoulometerPowerTestActivity.mCCValue[1];
                titleVol = CoulometerPowerTestActivity.mCCValueVol[1];
                lineChartView.SetInfo(CoulometerPowerTestActivity.testTimes2, yLabels, CoulometerPowerTestActivity.testResults2, title, titleVol, width, height, showChart);
            } else if (time.equals("time3")) {
                title = CoulometerPowerTestActivity.mCCValue[2];
                titleVol = CoulometerPowerTestActivity.mCCValueVol[2];
                lineChartView.SetInfo(CoulometerPowerTestActivity.testTimes3, yLabels, CoulometerPowerTestActivity.testResults3, title, titleVol, width, height, showChart);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.d(TAG, "ArrayIndexOutOfBoundsException" + e);
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException" + e);
        }
        //lineChartView.SetInfo(CoulometerPowerTestActivity.testResults1, yLabels, allData, title, width, height);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.clear_btn:
                Log.d(TAG, "onClick clear_btn");
                try {
                    if (time.equals("time1")) {
                        lineChartView.SetInfo(CoulometerPowerTestActivity.testTimes1, yLabels, CoulometerPowerTestActivity.testResults1, "", "", 0, 0, "no");
                        CoulometerPowerTestActivity.testTimes1.clear();
                        CoulometerPowerTestActivity.testResults1.clear();
                    } else if (time.equals("time2")) {
                        lineChartView.SetInfo(CoulometerPowerTestActivity.testTimes2, yLabels, CoulometerPowerTestActivity.testResults2, "", "", 0, 0, "no");
                        CoulometerPowerTestActivity.testTimes2.clear();
                        CoulometerPowerTestActivity.testResults2.clear();
                    } else {
                        lineChartView.SetInfo(CoulometerPowerTestActivity.testTimes3, yLabels, CoulometerPowerTestActivity.testResults3, "", "", 0, 0, "no");
                        CoulometerPowerTestActivity.testTimes3.clear();
                        CoulometerPowerTestActivity.testResults3.clear();
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    Log.d(TAG, "onClick ArrayIndexOutOfBoundsException" + e);
                } catch (NullPointerException e) {
                    Log.d(TAG, "onClick NullPointerException" + e);
                } finally {
                    finish();
                }
                break;
            default:
                break;
        }
    }
}