package com.unisoc.engineermode.core.impl.telephony;

import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.utils.IATUtils;

import java.util.ArrayList;
import java.util.List;

class NetInfoNrImpl implements ITelephonyApi.INetInfoNr {
    private static final String TAG = "NetInfoNr";

    @Override
    public void getServingCell(int simIdx, String[] names, String[] values) {
        int temp;
        int splitPoint = 7;
        String result = IATUtils.sendATCmd(engconstents.ENG_AT_NR_SERVICE_CELL,
                simIdx);
        if (result.contains(IATUtils.AT_OK)) {
            result = result.replaceAll(",-", ",+");
            result = result.replaceAll("--", "-+");
            String[] str1 = result.split("\n");
            String[] str2 = str1[0].split("-");

            if (values.length <= str2.length) {
                values[0] = names[0] + ": " + str2[0];// band
                values[1] = names[1] + ": " + str2[1];// freq
                values[2] = names[2] + ": " + str2[2];// pci
                values[3] = names[3] + ": " + Integer.parseInt(str2[3].split(",")[0].replace("+", "-").trim()) / 100 + "dBm";// rsrp
                values[4] = names[4] + ": " + Integer.parseInt(str2[4].split(",")[0].replace("+", "-").trim()) / 100 + "dBm";// rsrq
                values[5] = names[5] + ": " + str2[7];// bandWidth
                //Unisoc: modify for Bug 1423836 Serving Cell Crash
                values[6] = names[6] + ": " + Integer.parseInt(str2[15].split(",")[0].replace("+", "-").trim()) / 100 + "dBm";// sinr
                values[7] = names[7] + ": " + str2[16];// UL_MCS
                values[8] = names[8] + ": " + str2[17];// DL_MCS
                values[9] = names[9] + ": " + str2[18];// UL_BLER
                values[10] = names[10] + ": " + str2[19];// DL_BLER
                values[11] = names[11] + ": " + str2[8];// g-NodeB ID
                values[12] = names[12] + ": " + str2[9];// Cell ID
            }
        }
    }

    @Override
    public void getAdjacentCell(int simIdx, String[][] values) {
        int temp;
        int ROW = values.length;
        int COL = values[0].length;

        String result = IATUtils.sendATCmd(
                engconstents.ENG_AT_NR_ADJACENT_CELL, simIdx);
        if (result.contains(IATUtils.AT_OK)) {
            result = result.replaceAll(",-", ",+");
            result = result.replaceAll("--", "-+");
            String[] str1 = result.split("\n");
            String[] str2 = str1[0].split("-");
            //UNISOC: add for bug1411049, ArrayIndexOutOfBoundsException
            for (int i = 0; i < 4; i++) {
                if (i < str2.length - 1) {
                    String[] str3 = str2[i + 1].split(",");
                    for (int j = 0; j < str3.length & j < 5; j++) {
                        if (i == 2 || i == 3) { // RSRP\RSRQ
                            temp = Integer.parseInt(str3[j].replace("+", "-").trim()) / 100;
                            values[j][i] = temp + "dBm";
                        } else {
                            values[j][i] = str3[j].replace("+", "-");
                        }
                    }
                    for (int j = str3.length; j < 5; j++) {
                        values[j][i] = "NA";
                    }
                } else {
                    for (int j = 0; j < 5; j++) {
                        values[j][i] = "NA";
                    }
                }
            }
        }
    }

    //UNISOC: add for bug1415070, ArrayIndexOutOfBoundsException
    @Override
    public void getBetweenAdjacentCell5G(int simIdx, String[][] values) {
        int temp;
        int ROW = values.length;
        int COL = values[0].length;

        String result = IATUtils.sendATCmd(engconstents.ENG_AT_NR_DIFF_ADJACENT_CELL,
                simIdx);
        if (result.contains(IATUtils.AT_OK)) {
            result = result.replaceAll(",-", ",+");
            result = result.replaceAll("--", "-+");
            String[] str1 = result.split("\n");
            String[] str2 = str1[0].split("-");
            for (int i = 0; i < 4; i++) {
                if (i < str2.length - 1) {
                    String[] str3 = str2[i + 1].split(",");
                    for (int j = 0; j < str3.length & j < 5; j++) {
                        if (i == 2 || i == 3) { // RSRP\RSRQ
                            temp = Integer.parseInt(str3[j].replace("+", "-").trim()) / 100;
                            values[j][i] = temp + "dBm";
                        } else {
                            values[j][i] = str3[j].replace("+", "-");
                        }
                    }
                    for (int j = str3.length; j < 5; j++) {
                        values[j][i] = "NA";
                    }
                } else {
                    for (int j = 0; j < 5; j++) {
                        values[j][i] = "NA";
                    }
                }
            }
        }
    }

    @Override
    public List<String> getOutfieldNetworkInfo(int simIdx, String[] names) {
        int num = 0;
        String result = IATUtils.sendATCmd(engconstents.ENG_AT_NR_SERVICE_CELL,
                simIdx);
        if (result.contains(IATUtils.AT_OK)) {
            result = result.replaceAll(",-", ",+");
            result = result.replaceAll("--", "-+");
            String[] str1 = result.split("\n");
            String[] str2 = str1[0].split("-");

            if (names.length <= str2.length) {
                names[0] = names[0] + ": " + str2[10].split(",")[0];// UL_CA
                names[1] = names[1] + ": " + str2[10].split(",")[1];// DL_CA
            }
        }
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < names.length; i++) {
            list.add(names[i]);
        }
        return list;
    }
}
