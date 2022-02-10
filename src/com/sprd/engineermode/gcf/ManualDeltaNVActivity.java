package com.sprd.engineermode.gcf;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.content.Context;

import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.utils.IATUtils;
import com.unisoc.engineermode.core.common.engconstents;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;

import android.os.AsyncTask;
import android.os.PowerManager;
import com.unisoc.engineermode.core.impl.hardware.PhaseCheckParse;

public class ManualDeltaNVActivity extends ListActivity{

    private static final String TAG = "ManualDeltaNVActivity";

    public static String[] keys = new String[] {"",""};
    private List<String> keyLists = new ArrayList<String>();
    private OperatorHandler mOperatorHandler;

    private String mATCmd, mAtCmd1;
    private String mStrTmp, mStrTmp1;
    private Context mContext;

    private RandomAccessFile file = null;
    private String s_FilePath = "/data/local/tmp/aaa";
    private char[] fileNameByte = new char[20];

    private int lengthInt1;
    private int lengthInt2;
    private int lengthInt3;
    private int lengthInt4;

    private String lengthHex1;
    private String lengthHex2;
    private String lengthHex3;
    private String lengthHex4;

    private int mcc1;
    private int mcc2;
    private int mnc1;
    private int mnc2;

    private String mccHex1;
    private String mccHex2;
    private String mncHex1;
    private String mncHex2;

    private int fileNameOffset1;
    private int fileNameOffset2;
    private int fileNameOffset3;
    private int fileNameOffset4;

    private String fileNameOffsetHex1;
    private String fileNameOffsetHex2;
    private String fileNameOffsetHex3;
    private String fileNameOffsetHex4;

    private String combinentMCCHex;
    private String combinentMNCHex;
    private String combinentFileOffsetHex;
    private String combinentFileNameHex = "";
    private String combinentLengthHex;
    private String combinentMNCStr;

    private int combinentLengthInt;
    private int combinentMCCInt;
    private int combinentMNCInt;
    private int combinentFileOffsetInt;
    private int combinentFileNameInt;

    private ArrayList<String> FileNameList = new ArrayList<String>();
    private ArrayList<String> MCCMNCList = new ArrayList<String>();
    private ArrayList<String> MCCList = new ArrayList<String>();
    private ArrayList<String> MNCList = new ArrayList<String>();
    private boolean isRepeat ;
    private int MCCStartPosition;
    private int highestBit = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        mOperatorHandler = new OperatorHandler(ht.getLooper());
        for (int i=0; i<keys.length; i++) {
            keyLists.add(keys[i]);
        }
        Log.d(TAG, "keyLists.size = " + keyLists.size());

        showDeltanvFileList();//read through AT cmd
        //LoadAsyncTask task = new LoadAsyncTask();
        //task.execute((Void[])null);//read through C++
        //new Thread(runnable).start();//read through java
    }

    private void showDeltanvFileList() {
        String ret = IATUtils.sendATCmd(engconstents.ENG_AT_GET_DELTANV_NAME, "atchannel0");
        if (ret.contains("\"")) {
            String[] str = ret.split("\"");
            Log.d(TAG, "ShowDeltanvFileList str.length: " + str.length);
            if (str.length > 1) {
                String[] str1 = str[1].split(",");
                for (int i = 0; i < str1.length; i ++) {
                    FileNameList.add(str1[i]);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ManualDeltaNVActivity.this, android.R.layout.simple_list_item_1, FileNameList);
                setListAdapter(adapter);
            }
        }
        Log.d(TAG, "ShowDeltanvFileList: " + ret);
    }

    private class LoadAsyncTask extends AsyncTask<Void, Integer, Integer>{

        @Override
        protected void onPostExecute(Integer result) {
            Log.d(TAG,"onPostExecute result =" + result);
            if (result == 0) {
                Log.d(TAG,"onPostExecute result = null");
            } else {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ManualDeltaNVActivity.this, android.R.layout.simple_list_item_1, FileNameList);
                setListAdapter(adapter);
            }
        }

        @Override
        protected Integer doInBackground(Void... params) {
            // TODO Auto-generated method stub
            Log.d(TAG,"doInBackground ");
            String result = new PhaseCheckParse().getDeltaNVInfo();
            //int ret = readFromBinFile();
            int ret = parseRelult(result);
            if (ret <= 0) return 0;
            else return 1;
        }
    }

    /*private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            readFromBinFile();
        }
    };*/

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG, "position = " + position);
        Message mOperator = mOperatorHandler.obtainMessage(position);
        mOperatorHandler.sendMessage(mOperator);
    }

    class OperatorHandler extends Handler{
        public OperatorHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg){
            int setPosition = msg.what;

            //mATCmd = engconstents.ENG_AT_SET_DELTANV_NAME + "\"" + MCCList.get(setPosition) + MNCList.get(setPosition) + "\"";
            mATCmd = engconstents.ENG_AT_SET_DELTANV_NAME + "\"" + FileNameList.get(setPosition) + "\"";
            Log.d(TAG, "setPosition = " + setPosition + " mATCmd: " + mATCmd);
            mStrTmp = IATUtils.sendATCmd(mATCmd, "atchannel0");
            Log.d(TAG, "sendATCmd mStrTmp= " + mStrTmp);
            if (mStrTmp.contains("OK")) {
                Toast.makeText(ManualDeltaNVActivity.this, "Successful!", Toast.LENGTH_SHORT).show();
                confirmToReboot();
            } else {
                Toast.makeText(ManualDeltaNVActivity.this, mStrTmp, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void confirmToReboot() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
        .setMessage(R.string.orange_remaining)
        .setCancelable(false)
        .setPositiveButton(R.string.orange_remaining_ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PowerManager pm = (PowerManager) ManualDeltaNVActivity.this.getSystemService(Context.POWER_SERVICE);
                        pm.reboot("test mode");
                    }
                }).create();
        alertDialog.show();
    }

    private int parseRelult(String results) {
        String[] operators = results.trim().split("\\|");
        Log.d(TAG, "parseRelult operators.length = " + operators.length);
        if (operators.length < 1) {
            return 0;
        }
        for (int i = 0; i < operators.length; i++) {
            if (!operators[i].trim().equals("")) {
                String[] strSplit = operators[i].split("\\:");
                if (strSplit.length > 1) {
                    FileNameList.add(strSplit[0].trim());
                    MCCMNCList.add(strSplit[1].trim());
                }
            }
        }
        return FileNameList.size();
    }

    //Delta_nv file consistant: index(20) + version(2) + length(4) + MNC(2) + MCC(2) + cfg_offset(4) + sim_offset(4) + plmn_offset(4) + MCC........
    private int readFromBinFile() {
        int a;
        Log.d(TAG, "FileNameList.size() = " + FileNameList.size());
        try {
            if (!new File(s_FilePath).exists()) {
                Log.d(TAG, "init file not exist!");
            }
            file = new RandomAccessFile(s_FilePath, "r");

            file.seek(22);//little-endian, Header length hold 4 bytes
            lengthInt1 = file.readUnsignedByte();
            lengthHex1 = Integer.toHexString(lengthInt1);
            lengthInt2 = file.readUnsignedByte();
            lengthHex2 = Integer.toHexString(lengthInt2);
            lengthInt3 = file.readUnsignedByte();
            lengthHex3 = Integer.toHexString(lengthInt3);
            lengthInt4 = file.readUnsignedByte();
            lengthHex4 = Integer.toHexString(lengthInt4);

            combinentLengthHex = String.valueOf(lengthHex4) + String.valueOf(lengthHex3)
                               + String.valueOf(lengthHex2) + String.valueOf(lengthHex1);//little-endian, FileName offset hold 4 bytes
            combinentLengthInt = Integer.parseInt(combinentLengthHex, 16);
            Log.d(TAG, "readFromBin readByte combinentLengthHex:" + combinentLengthHex + " combinentLengthInt:" + combinentLengthInt);
            for (int n = 0; ; n ++) {
                MCCStartPosition = 26 + 16 * n;//26 is firt MCC start position, 16 = MNC(2) + MCC(2) + cfg_offset(4) + sim_offset(4) + plmn_offset(4)
                if (MCCStartPosition > combinentLengthInt - 22) {
                    break;
                }
                file.seek(MCCStartPosition);//seek 16bytes to next mnc begin

                mnc1 = file.readUnsignedByte();
                mncHex1 = Integer.toHexString(mnc1);
                mnc2 = file.readUnsignedByte();
                mncHex2 = Integer.toHexString(mnc2);
                Log.d(TAG, "readFromBin readByte mncHex1:" + mncHex1 + " mncHex2:" + mncHex2);
                combinentMNCHex = String.valueOf(mncHex2) + String.valueOf(mncHex1);//little-endian, MNC 2 bytes
                combinentMNCHex = combinentMNCHex.substring(1, combinentMNCHex.length()); //delete flag bit(15 bit)
                combinentMNCInt = Integer.parseInt(combinentMNCHex, 16);
                combinentMNCStr = String.valueOf(combinentMNCInt);
                //Log.d(TAG, "readFromBin readByte combinentMNCInt:" + combinentMNCInt + " combinentMNCStr:" + combinentMNCStr);
                //mnc bit 15 = 1, three ; bit 15 = 0, two
                if (mncHex2.toLowerCase().startsWith("8")
                    || mncHex2.toLowerCase().startsWith("c")
                    || mncHex2.toLowerCase().startsWith("e")
                    || mncHex2.toLowerCase().startsWith("f")) {
                    Log.d(TAG, " mncHex2:" + mncHex2 + " High bit is 1");
                    if (combinentMNCStr.length() == 1) {
                        combinentMNCStr = "00" + combinentMNCStr;
                    } else if (combinentMNCStr.length() == 2) {
                        combinentMNCStr = "0" + combinentMNCStr;
                    }
                } else {
                    if (combinentMNCStr.length() == 1) {
                        combinentMNCStr = "0" + combinentMNCStr;
                    }
                }
                Log.d(TAG, "readFromBin readByte combinentMNCHex:" + combinentMNCHex + " MNC:" + combinentMNCStr);

                mcc1 = file.readUnsignedByte();
                mccHex1 = Integer.toHexString(mcc1);
                mcc2 = file.readUnsignedByte();
                mccHex2 = Integer.toHexString(mcc2);
                //Log.d(TAG, "readFromBin readByte mccHex1:" + mccHex1 + " mccHex2:" + mccHex2);
                combinentMCCHex = String.valueOf(mccHex2) + String.valueOf(mccHex1);//little-endian, MCC 2 bytes
                combinentMCCInt = Integer.parseInt(combinentMCCHex, 16);
                Log.d(TAG, "readFromBin readByte combinentMCCHex:" + combinentMCCHex + " MCC:" + combinentMCCInt);

                fileNameOffset1 = file.readUnsignedByte();
                fileNameOffsetHex1 = Integer.toHexString(fileNameOffset1);
                fileNameOffset2 = file.readUnsignedByte();
                fileNameOffsetHex2 = Integer.toHexString(fileNameOffset2);
                fileNameOffset3 = file.readUnsignedByte();
                fileNameOffsetHex3 = Integer.toHexString(fileNameOffset3);
                fileNameOffset4 = file.readUnsignedByte();
                fileNameOffsetHex4 = Integer.toHexString(fileNameOffset4);

                combinentFileOffsetHex = String.valueOf(fileNameOffsetHex4)
                                       + String.valueOf(fileNameOffsetHex3)
                                       + String.valueOf(fileNameOffsetHex2)
                                       + String.valueOf(fileNameOffsetHex1);//little-endian, FileName offset hold 4 bytes
                combinentFileOffsetInt = Integer.parseInt(combinentFileOffsetHex, 16);

                file.seek(combinentFileOffsetInt);//big-endian, seek to fileName offset, then read 20 bytes fileName
                for (int i = 0; i < 20; i ++) {
                    a = file.readUnsignedByte();
                    fileNameByte[i] = (char)(a);
                }
                combinentFileNameHex = String.valueOf(fileNameByte).trim();
                isRepeat = false;
                for (int i = 0; i < FileNameList.size(); i ++) {
                    if (combinentFileNameHex.equals(FileNameList.get(i))) {
                        isRepeat = true;
                        break;
                    }
                }
                if (!isRepeat) {
                    MCCList.add(String.valueOf(combinentMCCInt));
                    MNCList.add(combinentMNCStr);
                    FileNameList.add(combinentFileNameHex);
                }
                combinentFileNameHex = "";
            }

            return FileNameList.size();
        } catch(IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
