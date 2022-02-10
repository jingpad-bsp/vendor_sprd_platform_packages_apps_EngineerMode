package com.sprd.engineermode.debuglog;

import android.app.Activity;
import android.view.View;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.content.Context;
import com.sprd.engineermode.R;
import android.os.PowerManager;

import java.io.File;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class AdbShellCMDActivity extends Activity implements OnClickListener {

    private static final String TAG = "AdbShellCMDActivity";
    private static final String IPERF_PATH = "/data/data/com.sprd.engineermode";
    private static final String IPERF_BIN = "/data/data/com.sprd.engineermode/bin/iperf";
    private Button mOnOffBtn1, mClearBtn1, mOnOffBtn2, mClearBtn2;

    private TextView mTextResult1;
    private TextView mTextResult2;

    private static final int UPDATE_CMD1 = 1;
    private static final int UPDATE_CMD2 = 2;
    private boolean paused1 = true;
    private boolean paused2 = true;

    Message message1 = null;
    Message message2 = null;

    private EditText mAdbShellEditText1;
    private EditText mAdbShellEditText2;

    private String CMD = null;
    private String[] CMD1 = {"ls", "-l"};
    private String mAdbShellCMD1 = null;
    private String mAdbShellCMD2 = null;
    private Process proc1 = null;
    private Process proc2 = null;

    private String mResult1 = "";
    private String mResult2 = "";

    private PowerManager.WakeLock mWakeLock;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case UPDATE_CMD1:
                    if (!paused1) {
                        String result = (String)msg.obj;
                        if (result != null && !result.equals("null")) {
                            mTextResult1.setText(result);
                        } else {
                            mTextResult1.setText(getString(R.string.result_empty));
                        }
                    }
                    break;
                case UPDATE_CMD2:
                    if (!paused2) {
                        String result = (String)msg.obj;
                        if (result != null && !result.equals("null")) {
                            mTextResult2.setText(result);
                        } else {
                            mTextResult2.setText(getString(R.string.result_empty));
                        }
                    }
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adb_shell);

        mAdbShellEditText1 = (EditText) findViewById(R.id.adb_shell_cmd1);
        mAdbShellEditText2 = (EditText) findViewById(R.id.adb_shell_cmd2);

        mOnOffBtn1 = (Button) findViewById(R.id.start_btn1);
        mOnOffBtn1.setOnClickListener(this);
        mClearBtn1 = (Button) findViewById(R.id.clear_btn1);
        mClearBtn1.setOnClickListener(this);

        mOnOffBtn2 = (Button) findViewById(R.id.start_btn2);
        mOnOffBtn2.setOnClickListener(this);
        mClearBtn2 = (Button) findViewById(R.id.clear_btn2);
        mClearBtn2.setOnClickListener(this);

        mTextResult1 = (TextView) findViewById(R.id.result_text1);
        mTextResult2 = (TextView) findViewById(R.id.result_text2);
    }

    private void runCMD(String[] cmd, int index) {
        String line = "";
        String proNum = "";
        String lstproNum = "";
        int topNum = 5;
        int exitVal = -1;
        InputStream is = null;
        try {
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            if (pm != null) {
                mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WIFI:TEST");
                // Acquire the lock
                mWakeLock.acquire(600*1000);
            }

            Runtime runtime = Runtime.getRuntime();
            if (index == 1) {
                proc1 = runtime.exec(cmd);
                is = proc1.getInputStream();
            } else {
                proc2 = runtime.exec(cmd);
                is = proc2.getInputStream();
            }

            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            do {
                line = buf.readLine();
                Log.d(TAG, "index: " + index + " run ShellUtiles line: " + line);
                if (line == null) {
                    break;
                }
                else if (line.equals("adbdec")) {
                    break;
                }

                if (index == 1) {
                    mResult1 = mResult1 + line + "\n\n";
                    message1 = handler.obtainMessage(UPDATE_CMD1, mResult1);
                    handler.sendMessageDelayed(message1, 200);
                } else {
                    mResult2 = mResult2 + line + "\n\n";
                    message2 = handler.obtainMessage(UPDATE_CMD2, mResult2);
                    handler.sendMessageDelayed(message2, 200);
                }
            } while (true);
            if (index == 1) {
                exitVal = proc1.waitFor();
            } else {
                exitVal = proc2.waitFor();
            }

            Log.d(TAG, "run ShellUtiles line END exitVal: " + exitVal);
            if (is != null) {
                buf.close();
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    private Runnable runnable1 = new Runnable() {
        @Override
        public void run() {
            if (!paused1) {
                runCMD(GenerateCMD(mAdbShellCMD1), 1);
            }
        }
    };

    private Runnable runnable2 = new Runnable() {
        @Override
        public void run() {
            if (!paused2) {
                runCMD(GenerateCMD(mAdbShellCMD2), 2);
            }
        }
    };

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private String[] GenerateCMD(String strCMD) {
        strCMD = strCMD.replaceAll("\\\\", "/");
        String[] strings = strCMD.split("\\s+");
        for (int i = 0; i < strings.length; i++) {
            Log.d(TAG, "strCMD:" + strings[i]);
        }
        return strings;
    }

    /*public void unZip(String assetName, String outputDirectory) {
        try {
            File file = new File(outputDirectory);
            if (!file.exists()) {
                Log.d(TAG, "/data/data/com.sprd.engineermode not exist");
                file.mkdirs();
            }
            InputStream inputStream = null;
            inputStream = getAssets().open("iperf.zip");
            ZipInputStream zipInputStream = new ZipInputStream(inputStream);
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            byte[] buffer = new byte[1024 * 1024];
            int count = 0;
            while (zipEntry != null) {
                if (zipEntry.isDirectory()) {
                    file = new File(outputDirectory + File.separator
                            + zipEntry.getName());
                    file.mkdir();
                } else {
                    file = new File(outputDirectory + File.separator + zipEntry.getName());
                    file.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    while ((count = zipInputStream.read(buffer)) > 0) {
                        Log.d(TAG, "unZip count: " + count);
                        fileOutputStream.write(buffer, 0, count);
                    }
                    fileOutputStream.close();
                }
                zipEntry = zipInputStream.getNextEntry();
            }
            zipInputStream.close();
        } catch (IOException e) {
            Log.d(TAG, "unZip IOException");
            e.printStackTrace();
        }
    }*/

    private boolean isFileExist(String childPath) {
        File filePath = new File(childPath);
        if(filePath.exists()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
        case R.id.start_btn1:
            if (paused1) {
                mAdbShellCMD1 = mAdbShellEditText1.getText().toString();
                /*if (mAdbShellCMD1.contains("iperf")) {
                    if (!isFileExist(IPERF_BIN)) {
                        unZip("", IPERF_PATH);
                        runCMD(GenerateCMD("chmod 777 " + IPERF_BIN), 1);
                    }
                    mAdbShellCMD1 = mAdbShellCMD1.replace("iperf", IPERF_BIN);
                }*/
                Log.d(TAG, "mAdbShellCMD1: " + mAdbShellCMD1);
                if (mAdbShellCMD1 == null || mAdbShellCMD1.equals("")) {
                    mAdbShellCMD1 = "getprop";
                }
                paused1 = false;
                new Thread(runnable1).start();
                mOnOffBtn1.setText(R.string.coulometer_finish);
                mOnOffBtn1.setBackground(getResources().getDrawable(R.drawable.btn_pause));
            } else {
                paused1 = true;
                mOnOffBtn1.setText(R.string.coulometer_start);
                mOnOffBtn1.setBackground(getResources().getDrawable(R.drawable.btn_refresh));
                if (proc1 != null) {
                    proc1.destroy();
                    proc1 = null;
                }
            }
            break;
        case R.id.clear_btn1:
            Log.d(TAG, "onClick clear_btn1");
            mTextResult1.setText("");
            mResult1 = "";
            break;
        case R.id.start_btn2:
            if (paused2) {
                mAdbShellCMD2 = mAdbShellEditText2.getText().toString();
                /*if (mAdbShellCMD2.contains("iperf")) {
                    if (!isFileExist(IPERF_BIN)) {
                        unZip("", IPERF_PATH);
                        runCMD(GenerateCMD("chmod 777 " + IPERF_BIN), 1);
                    }
                    mAdbShellCMD2 = mAdbShellCMD2.replace("iperf", IPERF_BIN);
                }*/
                Log.d(TAG, "mAdbShellCMD2: " + mAdbShellCMD2);
                if (mAdbShellCMD2 == null || mAdbShellCMD2.equals("")) {
                    mAdbShellCMD2 = "getprop";
                }
                paused2 = false;
                new Thread(runnable2).start();
                mOnOffBtn2.setText(R.string.coulometer_finish);
                mOnOffBtn2.setBackground(getResources().getDrawable(R.drawable.btn_pause));
            } else {
                paused2 = true;
                mOnOffBtn2.setText(R.string.coulometer_start);
                mOnOffBtn2.setBackground(getResources().getDrawable(R.drawable.btn_refresh));
                if (proc2 != null) {
                    proc2.destroy();
                    proc2 = null;
                }
            }
            break;
        case R.id.clear_btn2:
            Log.d(TAG, "onClick clear_btn2");
            mTextResult2.setText("");
            mResult2 = "";
            break;
        default:
            break;
        }
    }
}