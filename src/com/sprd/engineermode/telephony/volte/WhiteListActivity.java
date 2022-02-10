package com.sprd.engineermode.telephony.volte;

import java.util.ArrayList;
import java.util.Collections;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.unisoc.engineermode.core.utils.IATUtils;
import com.unisoc.engineermode.core.common.engconstents;

public class WhiteListActivity extends ListActivity{

    private static final String TAG = "WhiteListActivity";

    private ArrayList<String> fileNameList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showWhiteList();
    }

    private void showWhiteList() {
        String atCmd = engconstents.ENG_AT_GET_WHITE_LIST;
        String atRsp = IATUtils.sendATCmd(atCmd, 0);
        Log.d(TAG, "sendATCmd mStrTmp= " + atRsp);
        try {
            if (atRsp.contains("OK")) {
                Toast.makeText(WhiteListActivity.this, "Successful!", Toast.LENGTH_SHORT).show();
                atRsp = atRsp.replaceAll("\"", "");
                if (atRsp.contains(":")) {
                    String[] str = atRsp.split(":");
                    Log.d(TAG, "ShowDeltanvFileList str.length: " + str.length);
                    if (str.length > 1) {
                        String[] str1 = str[1].split("\n")[0].split(",");
                        Collections.addAll(fileNameList, str1);
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(WhiteListActivity.this, android.R.layout.simple_list_item_1, fileNameList);
                        setListAdapter(adapter);
                    }
                }
            } else {
                Toast.makeText(WhiteListActivity.this, atRsp, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}