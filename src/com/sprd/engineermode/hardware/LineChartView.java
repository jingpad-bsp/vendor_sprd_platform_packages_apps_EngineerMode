package com.sprd.engineermode.hardware;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;

public class LineChartView extends View {
    public int XPoint = 40;
    public int YPoint = 560;
    public int XScale = 115;
    public int YScale = 90;
    public int XLength = 1080;
    public int YLength = 530;
    public ArrayList<String> XLabel = new ArrayList<String>();
    public String[] YLabel;
    public ArrayList<String> Data = new ArrayList<String>();
    public String Title;
    public String TitleVol;
    public String mShowChart;

    public LineChartView(Context context) {
        super(context);
    }

    public LineChartView(Context context,AttributeSet attributeSet) {
        super(context,attributeSet);
    }

    public void SetInfo(ArrayList<String> XLabels, String[] YLabels, ArrayList<String> AllData, String strTitle, String strTitleVol, int XWith, int XHeight, String showChart) {
        XLabel = XLabels;
        YLabel = YLabels;
        Data = AllData;
        Title = strTitle;
        TitleVol = strTitleVol;
        XLength = XWith - 100;
        YLength = XHeight - 130;
        mShowChart = showChart;
        if (CoulometerPowerTestActivity.testResults1.size() != 0) {
            XScale = XLength / CoulometerPowerTestActivity.testResults1.size();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // canvas.drawColor(Color.WHITE);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        Paint paint1 = new Paint();
        paint1.setStyle(Paint.Style.STROKE);
        paint1.setAntiAlias(true);
        paint1.setColor(Color.RED);
        paint.setTextSize(20);
        if (mShowChart != null && mShowChart.equals("yes")) {
            canvas.drawLine(XPoint, YPoint - YLength, XPoint, YPoint, paint); // Y axes
            for (int i = 0; i * YScale < YLength; i++) {
                canvas.drawLine(XPoint, YPoint - i * YScale, XPoint + 5, YPoint - i * YScale, paint); // rule
                try {
                    canvas.drawText(YLabel[i], XPoint - 22, YPoint - i * YScale + 5, paint); // text
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            canvas.drawLine(XPoint, YPoint - YLength, XPoint - 3, YPoint - YLength + 6, paint); // x arrow
            canvas.drawLine(XPoint, YPoint - YLength, XPoint + 3, YPoint - YLength + 6, paint);
            canvas.drawLine(XPoint, YPoint, XPoint + XLength, YPoint, paint); // X axes

            for (int i = 0; i * XScale < XLength; i++) {
                canvas.drawLine(XPoint + i * XScale, YPoint, XPoint + i * XScale,
                        YPoint - 5, paint); //rule
                try {
                    canvas.drawText(XLabel.get(i), XPoint + i * XScale - 10, YPoint + 20, paint); //time
                    // data line
                    if (i > 0 && YCoord(Data.get(i - 1)) != -999
                            && YCoord(Data.get(i)) != -999) // promise that the data format is right
                        canvas.drawLine(XPoint + (i - 1) * XScale,
                                YCoord(Data.get(i - 1)), XPoint + i * XScale,
                                YCoord(Data.get(i)), paint);
                    canvas.drawCircle(XPoint + i * XScale, YCoord(Data.get(i)), 2,
                            paint);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            canvas.drawLine(XPoint + XLength, YPoint, XPoint + XLength - 6, YPoint - 3, paint); // Y arrow
            canvas.drawLine(XPoint + XLength, YPoint, XPoint + XLength - 6, YPoint + 3, paint);
        }
        paint.setTextSize(36);
        paint1.setTextSize(36);
        if (Title != null) {
            canvas.drawText(Title, 70, 50, paint1);
        }
        if (TitleVol != null) {
            canvas.drawText(TitleVol, 70, 100, paint1);
        }
        invalidate();
    }

    private int YCoord(String y0) // compute Y point，format error return -999
    {
        int y;
        try {
            y = Integer.parseInt(y0);
        } catch (Exception e) {
            return - 999; // error return :-999
        }
        try {
            if (!YLabel[1].equals("0")) {
                return YPoint - y * YScale / Integer.parseInt(YLabel[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return y;
    }
}