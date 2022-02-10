package com.unisoc.engineermode.core.impl.telephony;

import android.util.Log;

import com.unisoc.engineermode.core.annotation.Implementation;
import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.intf.ITelephonyApi.INetInfoC2k1x;
import com.unisoc.engineermode.core.utils.IATUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Implementation(
    interfaceClass = INetInfoC2k1x.class,
    properties =  {
    })
public class NetInfoC2k1xImpl implements INetInfoC2k1x {
    private static final String TAG = "NetInfoC2k1xImpl";

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
        String result = IATUtils.sendATCmd(engconstents.ENG_CET_VAMOS_CPC + "0,12,1", simIdx);
        Log.d(TAG, "AT+SPENGMD=0,12,1: " + result);
        if (result.contains(IATUtils.AT_OK)) {
            try {
                result = result.replace("--", "-+");
                //13844-2-11504-15-0-283-24-833490-878490-1228-0-0-1921-60-15,-1926916524-0-0-0-0-0-0-0  in the return 15,-1926916524 is a same field
                result = result.replace(",-", ",\\+");
                Log.d(TAG, "after convert AT+SPENGMD=0,12,1: " + result);
                String[] str1 = result.split("\n");
                if (str1[0].startsWith("-")) {  // -4434--9600-0-1-24,1536,15176-0-0-0-0-0 : index 0 is -4434 index1 is -9600
                    str1[0] = str1[0].replaceFirst("-", "+");
                    Log.d(TAG, "after convert  AT+SPENGMD=0,12,2: " + str1[0]);
                }
                String[] str2 = str1[0].split("-");

                for (int i = 0; i < str2.length; i++) {
                    if (str2[i].contains("+")) {
                        str2[i] = str2[i].replace("+", "-");
                    }
                    if (i == 0) {
                        values[0] = names[0] + ": " + str2[i];
                    } else if (i == 1) {
                        values[1] = names[1] + ": " + str2[i];
                    } else if (i == 2) {
                        values[2] = names[2] + ": " + str2[i];
                    } else if (i == 3) {
                        values[3] = names[3] + ": " + str2[i];
                    } else if (i == 4) {
                        values[4] = names[4] + ": " + str2[i];
                    } else if (i == 5) {
                        values[5] = names[5] + ": " + str2[i];
                    } else if (i == 6) {
                        values[6] = names[6] + ": " + str2[i];
                    } else if (i == 13) {
                        values[7] = names[7] + ": " + str2[i];
                    } else if (i == 14) {
                        values[8] = names[8] + ": " + str2[i].split(",")[0];
                    } else if (i == 15) {
                        values[9] = names[9] + ": " + str2[i];
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "" + e);
            }
        } else {
            for (int i = 0; i < names.length; i++) {
                values[i] = names[i] + ": NA";
            }
        }

        result = IATUtils.sendATCmd(engconstents.ENG_CET_VAMOS_CPC + "0,12,2", simIdx);
        Log.d(TAG, "AT+SPENGMD=0,12,2: " + result);
        if (result.contains(IATUtils.AT_OK)) {
            result = result.replace("--", "-+");
            result = result.replace(",-", ",\\+");
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
                if (i == 0) {
                    values[10] = names[10] + ": " + str2[i];
                } else if (i == 1) {
                    values[11] = names[11] + ": " + str2[i];
                } else if (i == 2) {
                    values[12] = names[12] + ": " + str2[i];
                }
            }
        }
    }

    @Override
    public void getAdjacentCell(int simIdx, String[][] values) {
        int temp;
        int row = values.length;
        int col = values[0].length;

        String result = IATUtils.sendATCmd("AT+SPENGMD=0,12,3", simIdx);
        Log.d(TAG, "AT+SPENGMD=0,12,3: " + result);
        if (result.contains(IATUtils.AT_OK)) {
            result = result.replace("--", "-+");
            result = result.replace(",-", ",\\+");
            String[] str1 = result.split("\n");
            if (str1[0].startsWith("-")) {
                str1[0] = str1[0].replaceFirst("-", "+");
                Log.d(TAG, "after convert  AT+SPENGMD=0,12,2: " + str1[0]);
            }
            String[] str2 = str1[0].split("-");
            Log.d(TAG, "str2.length: " + str2.length);
            int lrow = 0;
            for (int i = 12; i < str2.length; i ++) {
                if (str2[i].contains("+")) {
                    str2[i] = str2[i].replace("+", "-");
                }
                Log.d(TAG, "str2[i]: " + str2[i]);
                String[] str3 = str2[i].split(",");
                for (int j = 0; j < str3.length; j++) {
                    temp = Integer.parseInt(str3[j].trim());
                    values[lrow][j] = String.valueOf(temp);
                }
                lrow ++;
            }
        } else {
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
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
            result = result.replace("--", "-+");
            String[] str1 = result.split("\n");
            String[] str2 = str1[0].split("-");
            for (int i = 0; i < str2.length; i++) {
                if (i == 0 || i == 1 || i == 2 || i == 3 || i == 6
                        || i == 7 || i == 9) {
                    if (i == 3) {
                        int response = Integer.parseInt(str2[i]);
                        int[] bite = new int[7];
                        bite[6] = response % 128 / 64;
                        bite[5] = response % 64 / 32;
                        bite[4] = response % 32 / 16;
                        bite[3] = response % 16 / 8;
                        bite[2] = response % 8 / 4;
                        bite[1] = response % 4 / 2;
                        bite[0] = response % 2;

                        StringBuilder sb = new StringBuilder();
                        for (int j = 0; j < 6; j++) {
                            if (bite[j] == 1) {
                                sb.append(Description_name[3][j]);
                                sb.append(" ");
                            }
                        }
                        names[num] = names[num] + ": " + sb.toString();
                    } else {
                        names[num] = names[num]
                                + ": "
                                + Description_name[i][Integer.valueOf(str2[i].substring(0, 1))];
                    }
                    num++;
                }
            }
        } else {
            for (int i = 0; i < names.length; i++) {
                names[i] = names[i] + ": NA";
            }
        }

        return Arrays.stream(names).filter( n -> !n.contains("R9")).collect(Collectors.toList());
    }
}
