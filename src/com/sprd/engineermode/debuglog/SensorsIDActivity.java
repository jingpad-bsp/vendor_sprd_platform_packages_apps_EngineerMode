
package com.sprd.engineermode.debuglog;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import android.text.TextUtils;

import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;

import android.os.Handler;
import android.os.Message;
import java.io.File;

import android.hardware.Camera;

public class SensorsIDActivity extends Activity {

    private static final String TAG = "SensorsIDActivity";

    /* SPRD Bug 841799 @{ */
    private static final String LCD_ID_OLD = "/sys/devices/platform/soc/soc:ap-ahb/20800000.dispc/lcd_name";
    private static final String LCD_ID_NEW = "/sys/class/display/panel0/name";
    /* @} */
    private static final String TP_CHIP_ID = "/sys/touchscreen/chip_id";
    private static final String TP_FIRMWARE_VERSION= "/sys/touchscreen/firmware_version";
    private static final String GSENSOR = "sys/module/sprd_phinfo/parameters/SPRD_GsensorInfo";
    private static final String LSENSOR = "sys/module/sprd_phinfo/parameters/SPRD_LsensorInfo";
    private static final String CAMERA_SENSOR = "/sys/devices/virtual/misc/sprd_sensor/camera_sensor_name";
    /* SPRD 1005285: add Sensor ID @{ */
    private static final String SENSOR_VERSION = "/sys/class/sprd_sensorhub/sensor_hub/sensor_info";
    /* @} */
    private int mCameraId = 0;
    private static final int GET_CAMERA_ID = 0;
    private boolean mIsCameraOpen;

    private TextView mCameraID, mLcdID, mTpVersion, mSensorVersion;
    private Camera mCamera = null;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_CAMERA_ID:
                    String cameraId = getCameraID();
                    Log.e(TAG, "getCameraID:" + cameraId);
                    if(cameraId != null) {
                        cameraId = cameraId.replaceAll("\n","");
                    }
                    if(!TextUtils.isEmpty(cameraId)) {
                        mCameraID.setText(cameraId);
                        SystemPropertiesProxy.set("persist.vendor.cam.sensor.id", "");
                    } else {
                        SystemPropertiesProxy.set("persist.vendor.cam.sensor.id", "trigger_srid");
                        if(!mIsCameraOpen) {
                            startCamera();
                        }
                        mHandler.sendEmptyMessageDelayed(GET_CAMERA_ID, 200);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors_id);

        mCameraID = (TextView) findViewById(R.id.camera_id);
        mLcdID = (TextView) findViewById(R.id.lcd_id);
        mTpVersion = (TextView) findViewById(R.id.tp_version);

        mHandler.sendEmptyMessage(GET_CAMERA_ID);
        mLcdID.setText(getLcdID());
        mTpVersion.setText(getTpID() + getTpVersion());
        /* SPRD 1005285: add Sensor ID @{ */
        mSensorVersion = (TextView) findViewById(R.id.sensor_version);
        mSensorVersion.setText(getSensorID());
        /* @} */
    }

    private void startCamera() {
        Log.e(TAG, "startCamera");
        if (mCamera != null) {
            mCamera.release();
        }
        try {
            mCamera = Camera.open(mCameraId);
            mIsCameraOpen = true;
        } catch (RuntimeException e) {
            Log.e(TAG, "fail to open camera", e);
            e.printStackTrace();
            mCamera = null;
            mIsCameraOpen = false;
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (mCamera != null) {
                mCamera.release();
                mCamera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(mHandler != null) {
            mHandler.removeMessages(GET_CAMERA_ID);
        }
    }
    private String getCameraID() {
        String result = getFileValue(CAMERA_SENSOR);
        if(TextUtils.isEmpty(result)) {
            result = SystemPropertiesProxy.get("vendor.cam.sensor.info");
            return result;
        }
        return result;
    }

    private String getLsensorID() {
        return getFileValue(LSENSOR);
    }

    private String getGsensorID() {
        return getFileValue(GSENSOR);
    }
    private String getTpID() {
        return getFileValue(TP_CHIP_ID);
    }

    private String getTpVersion() {
        return getFileValue(TP_FIRMWARE_VERSION);
    }

    /* SPRD Bug 841799 @{ */
    private String getLcdID() {
        String lcdId = isLCDIDExist(LCD_ID_OLD) ? LCD_ID_OLD : LCD_ID_NEW;
        return getFileValue(lcdId);
    }
    /* @} */
    /* SPRD 1005285: add Sensor ID @{ */
    private String getSensorID() {
        return getFileValue(SENSOR_VERSION);
    }
    /* @} */
    private String getFileValue(String path) {
        BufferedReader bReader = null;
        StringBuffer sBuffer = new StringBuffer();

        try {
            FileInputStream fi = new FileInputStream(path);
            bReader = new BufferedReader(new InputStreamReader(fi));
            String str = bReader.readLine();

            while (str != null) {
                /* SPRD 1005285: add Sensor ID @{ */
                if (path.equals(SENSOR_VERSION)) {
                    if (str.startsWith("1")) {
                        str = str.replaceFirst("1", "");
                    }
                    if (!str.startsWith("0")) {
                        sBuffer.append(str + "\n");
                    }
                } else {
                    sBuffer.append(str + "\n");
                }
                str = bReader.readLine();
                /* @} */
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bReader != null) {
                try {
                    bReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d(TAG, "getFileValue sBuffer.toString(): " + sBuffer.toString());
        return sBuffer.toString();
    }

    /* SPRD Bug 841799 @{ */
    public boolean isLCDIDExist(String path) {
        File file = new File(path);
        if (file.exists()) {
            return true;
        }
        return false;
    }
    /* @} */
}
