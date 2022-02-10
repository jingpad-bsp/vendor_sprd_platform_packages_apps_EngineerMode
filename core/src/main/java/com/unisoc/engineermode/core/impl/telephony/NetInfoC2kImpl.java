package com.unisoc.engineermode.core.impl.telephony;

import android.util.Log;
import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.utils.IATUtils;


import java.util.ArrayList;
import java.util.List;

public class NetInfoC2kImpl implements ITelephonyApi.INetInfoC2k {
    private static final String TAG = "NetInfoC2kImpl";

    private static final String[][] Description_name = {
            { "Not Support", "Support" },
            { "Not Support", "Support" },
            { "Unknown", "GEA1", "GEA2", "GEA3" },
            { "A51", "A52", "A53", "A54", "A55", "A56", "A57" },
            { "Unknown", "UEA0", "UEA1" },
            { "Not support Hsdpa and Hsupa", "Support Hsdpa", "Support Hsupa",
                    "Support Hsdpa and Hsupa" }, { "Not Support", "Support" },
            { "Not Support", "VAMOS1", "VAMOS2" },
            { "Not Support", "Support" }, { "Not Support", "Support" },
            { "other", "R8", "R9" }, { "Not Support", "Support" },
            { "Unknown", "eea0", "eea1", "eea2" },
            { "Unknown", "eia0 ", "eia1", "eia2" } };

    @Override
    public void getServingCell(int simIdx, String[] names, String[] values) {
        int temp;
        String result = IATUtils.sendATCmd(engconstents.ENG_CET_VAMOS_CPC + "0,13,0", simIdx);
        Log.d(TAG, "AT+SPENGMD=0,13,0: " + result);
        if (result.contains(IATUtils.AT_OK)) {
            try {
                result = result.replaceAll("--", "-+");
                result = result.replaceAll(",-", ",\\+");
                String[] str1 = result.split("\n");
                if (str1[0].startsWith("-")) {
                    str1[0] = str1[0].replaceFirst("-", "+");
                    Log.d(TAG, "after convert  AT+SPENGMD=0,12,2: " + str1[0]);
                }
                String[] str2 = str1[0].split("-");
                for (int i = 0; i < str2.length; i++) {
                    if (str2[i].contains("+")) {
                        str2[i] = str2[i].replace("+", "-");
                    }
                    if (i == 1) {
                        values[0] = names[0] + ": " + str2[i];
                    } else if (i == 4) {
                        values[1] = names[1] + ": " + str2[i];
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "" + e);
            }
        }

        result = IATUtils.sendATCmd(engconstents.ENG_CET_VAMOS_CPC + "0,13,2", simIdx);
        Log.d(TAG, "AT+SPENGMD=0,13,2: " + result);
        if (result.contains(IATUtils.AT_OK)) {
            try {
                result = result.replaceAll("--", "-+");
                result = result.replaceAll(",-", ",\\+");
                String[] str1 = result.split("\n");
                if (str1[0].startsWith("-")) {
                    str1[0] = str1[0].replaceFirst("-", "+");
                    Log.d(TAG, "after convert  AT+SPENGMD=0,13,2: " + str1[0]);
                }
                String[] str2 = str1[0].split("-");

                for (int i = 0; i < str2.length; i++) {
                    if (str2[i].contains("+")) {
                        str2[i] = str2[i].replace("+", "-");
                    }
                    if (i == 7) {
                        values[2] = names[2] + ": " + str2[i];
                    } else if (i == 8) {
                        values[3] = names[3] + ": " + str2[i];
                    } else if (i == 10) {
                        values[4] = names[4] + ": " + str2[i];
                    } else if (i == 11) {
                        values[5] = names[5] + ": " + str2[i];
                    } else if (i == 12) {
                        values[6] = names[6] + ": " + str2[i];
                    } else if (i == 14) {
                        values[7] = names[7] + ": " + str2[i];
                    } else if (i == 15) {
                        values[8] = names[8] + ": " + str2[i];
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "" + e);
            }
        }
    }

    @Override
    public void getAdjacentCell(int simIdx, String[][] values) {
        int temp;
        int ROW = values.length;
        int COL = values[0].length;

        String result = IATUtils.sendATCmd("AT+SPENGMD=0,13,3", simIdx);
        Log.d(TAG, "AT+SPENGMD=0,13,3: " + result);
        if (result.contains(IATUtils.AT_OK)) {
            try {
                result = result.replaceAll("--", "-+");
                result = result.replaceAll(",-", ",\\+");
                String[] str1 = result.split("\n");
                if (str1[0].startsWith("-")) {
                    str1[0] = str1[0].replaceFirst("-", "+");
                    Log.d(TAG, "after convert  AT+SPENGMD=0,13,2: " + str1[0]);
                }
                String[] str2 = str1[0].split("-");
                Log.d(TAG, "str2.length: " + str2.length);
                int row = 0;
                for (int i = 12; i < str2.length; i ++) {
                    if (str2[i].contains("+")) {
                        str2[i] = str2[i].replace("+", "-");
                    }
                    Log.d(TAG, "str2[i]: " + str2[i]);
                    String[] str3 = str2[i].split(",");
                    for (int j = 0; j < str3.length; j++) {
                        temp = Integer.parseInt(str3[j].trim());
                        values[row][j] = String.valueOf(temp);
                    }
                    row ++;
                }
            } catch (Exception e) {
                Log.d(TAG, "" + e);
            }
        } else {
            for (int i = 0; i < ROW; i++) {
                for (int j = 0; j < COL; j++) {
                    values[i][j] = "NA";
                }
            }
        }
    }

    @Override
    public void getBetweenAdjacentCell2G(int simIdx, String[][] values) {
        int ROW = values.length;
        int COL = values[0].length;

        String result = IATUtils.sendATCmd("AT+SPENGMD=0,12,4", simIdx);
        Log.d(TAG, "AT+SPENGMD=0,12,4: " + result);
        if (result.contains(IATUtils.AT_OK)) {
            result = result.replaceAll(",-", ",+");
            result = result.replaceAll("--", "-+");
            String[] str1 = result.split("\n");
            String[] str2 = str1[0].split("-");
            for (int i = 0; i < 5; i++) {
                if (i < (str2.length - 3)) {
                    if (str2[i + 3].contains(",")) {
                        String[] str3 = str2[i + 3].split(",");
                        for (int j = 1; j < 4; j++) {
                            if (j == 3) {
                                values[i][j - 1] = str3[j].replace("+",
                                        "-") + "dBm";
                            } else {
                                values[i][j - 1] = str3[j].replace("+",
                                        "-");
                            }
                        }
                    }
                } else {
                    for (int j = 0; j < 3; j++) {
                        values[i][j] = "NA";
                    }
                }
            }
        } else {
            for (int i = 0; i < ROW; i++) {
                for (int j = 0; j < COL; j++) {
                    values[i][j] = "NA";
                }
            }
        }
    }

    @Override
    public void getBetweenAdjacentCell4G(int simIdx, String[][] values) {
        int temp;
        int ROW = values.length;
        int COL = values[0].length;

        String result = IATUtils.sendATCmd("AT+SPENGMD=0,13,4", simIdx);
        Log.d(TAG, "AT+SPENGMD=0,13,4: " + result);
        if (result.contains(IATUtils.AT_OK)) {
            try {
                result = result.replaceAll("--", "-+");
                result = result.replaceAll(",-", ",\\+");
                String[] str1 = result.split("\n");
                if (str1[0].startsWith("-")) {
                    str1[0] = str1[0].replaceFirst("-", "+");
                    Log.d(TAG, "after convert  AT+SPENGMD=0,13,4: " + str1[0]);
                }
                String[] str2 = str1[0].split("-");
                Log.d(TAG, "str2.length: " + str2.length);
                int row = 0;
                for (int i = 12; i < str2.length; i ++) {
                    if (str2[i].contains("+")) {
                        str2[i] = str2[i].replace("+", "-");
                    }
                    Log.d(TAG, "str2[i]: " + str2[i]);
                    String[] str3 = str2[i].split(",");
                    for (int j = 0; j < str3.length; j++) {
                        temp = Integer.parseInt(str3[j].trim());
                        values[row][j] = String.valueOf(temp);
                    }
                    row ++;
                }
            } catch (Exception e) {
                Log.d(TAG, "" + e);
            }
        } else {
            for (int i = 0; i < ROW; i++) {
                for (int j = 0; j < COL; j++) {
                    values[i][j] = "NA";
                }
            }
        }
    }

    @Override
    public List<String> getOutfieldNetworkInfo(int simIdx, String[] names) {
        int num = 0;
        String result = IATUtils.sendATCmd("AT+SPENGMD=0,0,7", simIdx);
        Log.d(TAG, "AT+SPENGMD=0,0,7: " + result);

        if (result.contains(IATUtils.AT_OK)) {
            result = result.replaceAll("--", "-+");
            String[] str1 = result.split("\n");
            String[] str2 = str1[0].split("-");
            for (int i = 0; i < str2.length; i++) {
                if (i == 4 || i == 5 || i == 6 || i == 8 || i == 10
                        || i == 11) {
                    if (num < names.length) {
                        names[num] = names[num]
                                + ": "
                                + Description_name[i][Integer
                                .valueOf(str2[i].substring(0, 1))];
                        num++;
                    }
                }
            }
        } else {
            for (int i = 0; i < names.length; i++) {
                names[i] = names[i] + ": NA";
            }
        }
        num = 6;
        List<String> nwCapValues = getNwCap(simIdx);
        if (nwCapValues != null) {
            for (int i=0; i<nwCapValues.size(); i++) {
                if (i == nwCapValues.size() - 1) {
                    names[num] = names[num] + ": " + nwCapValues.get(i);
                    num++;
                    continue;
                }
                String isSupport = nwCapValues.get(i).equals("1") ? "support" : "not support";
                names[num] = names[num] + ": " + isSupport;
                num++;
            }
        } else {
            for (int i=num; i<names.length; i++) {
                names[i] = names[i] + ": NA";
            }
        }
        List<String> list = new ArrayList<String>();
        for (int i=0; i<names.length; i++) {
            if (!names[i].contains("R9")) {
                list.add(names[i]);
            }
        }
        return list;
    }


    private List<String> getNwCap(int simIdx) {
        return NetInfoWcdma.getNwCap(simIdx);
    }
}
