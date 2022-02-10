package com.unisoc.engineermode.core.impl.telephony;

import android.util.Log;

import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.utils.IATUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class NetInfoLteImpl implements ITelephonyApi.INetInfoLte {
    private static final String TAG = "NetInfoLte";

    private String[][] Description_name = {
            { "Not Support", "Support" },
            { "Not Support", "Support" },
            { "Unknown", "GEA1", "GEA2", "GEA3" },
            { "A51", "A52", "A53", "A54", "A55", "A56", "A57" },
            { "Unknown", "UEA0", "UEA1" , "UEA0,UEA1"},
            { "Not support Hsdpa and Hsupa", "Support Hsdpa", "Support Hsupa",
                    "Support Hsdpa and Hsupa" }, { "Not Support", "Support" },
            { "Not Support", "VAMOS1", "VAMOS2" },
            { "Not Support", "Support" }, { "Not Support", "Support" },
            { "other", "R8", "R9" }, { "Not Support", "Support" },
            { "Unknown", "eea0", "eea1", "eea2" },
            { "Unknown", "eia0 ", "eia1", "eia2" },
            { "Not Support", "Support" }};

    private enum Supportivity {
        NotSupport("not support"), Support("support"), NA("NA");

        private String value;

        Supportivity(String value) {
            this.value=value;
        }

        public String getValue() {
            return value;
        }
    }

    private static final HashMap<String,String> SupportivityMap=new HashMap<String,String>() {
        {
            put("0", Supportivity.NotSupport.getValue());
            put("1", Supportivity.Support.getValue());
        }
    };

    @Override
    public void getServingCell(int simIdx, String[] names, String[] values) {
        int temp;
        int splitPoint=7;
        String result = IATUtils.sendATCmd("AT+SPENGMD=0,6,0", simIdx);
        Log.d(TAG, "AT+SPENGMD=0,6,0: " + result);
        //AT+SPENGMD=0,6,0: 3,0-1650,0-431,0--9077,0--482,0--12800,0-0,0-5,0-0,0--3146050,0-57995,0-12,0
        //AT+SPENGMD=0,6,0: 40,0-38950,0-384,0--10804,0--777,0--12400,0-1,0-5,0-0,0--3146050,0-107148,0-33,0-0,0-0,0
//AT+SPENGMD=0,6,0: 3,0-1650,0-442,0--9542,0--736,0--12800,0-0,0-5,0-0,0--3146050,0-41939,0-21,0-0,0-0,0

        if (result.contains(IATUtils.AT_OK)) {
            result = result.replace(",-", ",+");
            result = result.replace("--", "-+");
            String[] str1 = result.split("\n");
            String[] str2 = str1[0].split("-");
            // 559502 modify by alisa.li 2016.05.05
            for (int i = 0; i+1 < str2.length; i++) {
                if (i == 3 || i == 4) {
                    if (str2[i].contains(",")) {
                        values[i] = names[i] + ": ";
                        String[] str3 = str2[i].split(",");
                        int[] tempArray = new int[str3.length];
                        for (int j = 0; j < str3.length; j++) {
                            tempArray[j] = Integer.parseInt(str3[j].replace("+", "-").trim()) / 100;
                            values[i] = values[i] + tempArray[j] + "dBm";
                            if (j == 0) {
                                values[i] += ",";
                            }
                        }
                    } else {
                        temp = Integer.parseInt(str2[i].replace("+", "-").trim()) / 100;
                        values[i] = names[i] + ": " + temp + "dBm";
                    }
                } else if(i == splitPoint-1 || i == splitPoint-2){
                    values[i] = names[i] + ": "
                            + str2[i+1].replace("+", "-");
                    if (i == splitPoint-1) {
                        int indexOfColon = values[i].indexOf(':');
                        String bandWidth = values[i].substring(indexOfColon + 2);
                        String[] str = bandWidth.split(",");
                        values[i] = values[i].substring(0, indexOfColon + 2);
                        for (int j = 0; j < str.length; j++) {
                            if (j != 0) {
                                values[i] = values[i] + ",";
                            }
                            values[i] += switchToBandWidth(str[j]);
                        }
                        Log.d(TAG, "values[i] = " + values[i]);
                    }
                }else if (i < 14) {
                    values[i] = names[i] + ": "
                            + str2[i].replace("+", "-");
                }
            }
            values[14] = names[14] + ": " + str2[10].replace("+", "-");
            values[15] = names[15] + ": " + str2[11].replace("+", "-");
            Log.d(TAG, "values[14] = " + values[14]);
            Log.d(TAG, "values[15] = " + values[15]);
            //559502 modify by alisa.li 2016.05.05
        } else {
            for (int i = 0; i < splitPoint; i++) {
                values[i] = names[i] + ": NA";
            }
        }
        int num = splitPoint;
        boolean isModem20CV1V2 = false;
        result = IATUtils.sendAt("AT+CGMR", "atchannel0");
        Log.d(TAG, "CP0 Version is " + result);
        isModem20CV1V2 = result.contains("_20C") || result.contains("_V1") || result.contains("_V2");
        result = IATUtils.sendATCmd("AT+SPENGMD=0,0,6", simIdx);
        Log.d(TAG, "AT+SPENGMD=0,0,6: " + result);
        //the return value is: 0,0/-0,0-/29,0/-0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1/-0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0/-0,0,0,0,0,0,0,0,0,0/-0,0,0,0,0,0,0,0,0,0/-0,0
        if (result.contains(IATUtils.AT_OK)) {
            result = result.replace(",-", ",+");
            result = result.replace("--", "-+");
            String[] str1 = result.split("\n");
            String[] str2 = str1[0].split("-");
            for (String s : str2) {
                if (num == 14 || num == 15) {
                    continue;
                }
                if (num < names.length) {
                    //Unisoc Bug 1476934 SINR is need to divide 100 (5G modem)
                    if (num == 9 && isModem20CV1V2) {
                        values[num] = names[num] + ": " + Integer.parseInt(s.replace("+", "-").trim().split(",")[0]) / 100.0;
                        num++;
                    } else {
                        values[num] = names[num] + ": " + s.replace("+", "-");
                        num++;
                    }
                }
            }
            if (str2.length >= 7) {
                values[5] = names[5] + ": " + str2[7];
                Log.d(TAG, values[5] + " = " + values[5]);
            }
            for (int i = num; i < names.length; i++) {
                if (i == 14 || i == 15) {
                    continue;
                }
                values[i] = names[i] + ":NA";
            }
        } else {
            for (int i = splitPoint; i < names.length; i++) {
                values[i] = names[i] + ": NA";
            }
        }
    }

    @Override
    public void getServingCellNR(int simIdx, String[] names, String[] values) {
        String result = IATUtils.sendATCmd("AT+SPENGMD=0,14,1", simIdx);
        Log.d(TAG, "AT+SPENGMD=0,14,1: " + result);
        if (result != null&&result.contains(IATUtils.AT_OK)) {
            result = result.replaceAll(",-", ",+");
            result = result.replaceAll("--", "-+");
            String[] str1 = result.split("\n");
            String[] str2 = str1[0].split("-");
            for (int i = 0; i < 5 && i < str2.length; i++){
                /**UNISOC: Bug1344676 & 1349172 The RSRP/RSRQ value is incorrectly.*/
                if (i == 3 || i== 4 ) {
                    values[i] = names[i] + ": ";
                    String[] str3 = str2[i].split(",");
                    for (int j = 0; j < str3.length; j++) {
                        values[i] = values[i] + Integer.parseInt(str3[j].replace("+", "-").trim()) /100;;
                        if (j != str3.length-1 ) values[i] += ",";
                    }
                } else {
                    values[i] = names[i] + ": " + str2[i].replace("+", "-");
                }
                /**@}*/
            }
        } else {
            for (int i = 0; i < names.length; i++) {
                values[i] = names[i] + ": NA";
            }
        }
    }

    @Override
    public void getAdjacentCell(int simIdx, String[][] values) {
        int temp;
        int row = values.length;
        int col = values[0].length;

        String result = IATUtils.sendATCmd("AT+SPENGMD=0,6,6", simIdx);
        Log.d(TAG, "AT+SPENGMD=0,6,6: " + result);
        if (result.contains(IATUtils.AT_OK)) {
            result = result.replace(",-", ",+");
            result = result.replace("--", "-+");
            String[] str1 = result.split("\n");
            String[] str2 = str1[0].split("-");
            for (int i = 0; i < 5; i++) {
                if (i < str2.length) {
                    if (str2[i].contains(",")) {
                        String[] str3 = str2[i].split(",");
                        for (int j = 0; j < 4; j++) {
                            if (j == 2 || j == 3) {
                                temp = Integer.parseInt(str3[j].replace("+", "-").trim())/100;
                                values[i][j] = temp + "dBm";
                            } else {
                                values[i][j] = str3[j] .replace("+", "-");
                            }
                        }
                    }
                } else {
                    for (int j = 0; j < 4; j++) {
                        values[i][j] = "NA";
                    }
                }
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
    public void getBetweenAdjacentCell2G(int simIdx, String[][] values) {
        int temp;
        int row = values.length;
        int col = values[0].length;

        String result = IATUtils.sendATCmd("AT+SPENGMD=0,6,7", simIdx);
        Log.d(TAG, "AT+SPENGMD=0,6,7: " + result);
        int num = 0;
        if (result.contains(IATUtils.AT_OK)) {
            result = result.replace(",-", ",+");
            result = result.replace("--", "-+");
            String[] str1 = result.split("\n");
            String[] str2 = str1[0].split("-");
            for (int i = 0; i < str2.length; i++) {
                if (num < 5) {
                    if (str2[i].contains(",")) {
                        String[] str3 = str2[i].split(",");
                        for (int j = 0; j < 3; j++) {
                            if (j == 2) {
                                temp = Integer.parseInt(str3[j].replace("+", "-").trim())/100;
                                values[num][j] = temp + "dBm";
                            } else {
                                values[num][j] = str3[j].replace("+", "-");
                            }
                        }
                        num++;
                    }
                } else {
                    break;
                }
            }
            if (num < 5) {
                for (int i = num; i < 5; i++) {
                    for (int j = 0; j < 3; j++) {
                        values[i][j] = "NA";
                    }
                }
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
    public void getBetweenAdjacentCell3G(int simIdx, String[][] values) {
        int temp;
        int row = values.length;
        int col = values[0].length;

        String result = IATUtils.sendATCmd("AT+SPENGMD=0,6,8", simIdx);
        Log.d(TAG, "AT+SPENGMD=0,6,8: " + result);
        int num4G = 0;

        if (result.contains(IATUtils.AT_OK)) {
            result = result.replace(",-", ",+");
            result = result.replace("--", "-+");
            String[] str1 = result.split("\n");
            String[] str2 = str1[0].split("-");
            for (int i = 0; i < str2.length; i++) {
                if (num4G < 5) {
                    if (str2[i].contains(",")) {
                        String[] str3 = str2[i].split(",");
                        for (int j = 0; j < 3; j++) {
                            if (j == 2) {
                                temp = Integer.parseInt(str3[j].replace("+", "-").trim())/100;
                                values[num4G][j] = String.valueOf(temp) + "dBm";
                            } else {
                                values[num4G][j] = str3[j].replace(
                                        "+", "-");
                            }
                        }
                        num4G++;
                    }
                } else {
                    break;
                }
            }
            if (num4G < 5) {
                for (int i = num4G; i < 5; i++) {
                    for (int j = 0; j < 3; j++) {
                        values[i][j] = "NA";
                    }
                }
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
            String[] str2;
            if (str1.length > 3) {
                str2 = str1[2].split("-");
            } else {
                str2 = str1[0].split("-");
            }
            names[num] = names[num] + ": " + Description_name[4][Integer.valueOf(str2[12].substring(0, 1))];
            num++;

            if (num < names.length) {
                names[num] = names[num] + ": "
                    + Description_name[10][Integer.valueOf(str2[10].substring(0, 1))];
                num++;
            }
        } else {
            for (int i = 0; i < names.length; i++) {
                names[i] = names[i] + ": NA";
            }
        }

        String bsrvcc = IATUtils.sendATCmd("AT+SPENGMDVOLTE=24,0", simIdx);
        if (bsrvcc != null && bsrvcc.contains(IATUtils.AT_OK)) {
            String[] bsrvccString = bsrvcc.split("\n");
            String[] bsrvccString1 = bsrvccString[0].split(",");
            if(bsrvccString1.length >=3 && "1".equals(bsrvccString1[2].trim())) {
                names[2] = names[2] + ": " + Description_name[Description_name[0].length-1][1];
            } else {
                names[2] = names[2] + ": " + Description_name[Description_name[0].length-1][0];
            }
        } else {
            names[2] = names[2] + ": NA";
        }

        int n = 3;
        result = IATUtils.sendATCmd("AT+SPENGMD=0,6,0", simIdx);
        Log.d(TAG, "ATSPENGMD=0,6,0: " + result);
//ATSPENGMD=0,6,0: 3,0-1650,0-442,0--4571,0--716,0--12800,0-0,0-5,0-0,0--3146050,0-41939,0-21,0-0,0-0,0,0-0-0-0-0-0-0-0-0-0
        if (result.contains(IATUtils.AT_OK)) {
            result = result.replace("--", "-");
            result = result.replace(",-", ",+");
            String[] str1 = result.split("\n");
            String[] nwCap = str1[0].split("-");

            if (nwCap.length >= 12) {
                String[] uldaca = nwCap[12].split(",");
                for (String s : uldaca) {
                    String isSupport = s.equals("1") ? "support" : "not support";
                    names[n] = names[n] + ": " + isSupport;
                    n++;
                }
            }

            for (int k=21; k<nwCap.length && k<=22; k++) {
                String isSupport = nwCap[k].substring(0, 1).equals("1") ? "support" : "not support";
                names[n] = names[n] + ": " + isSupport;
                n++;
            }

            Log.d(TAG, "nwCap.length = " + nwCap.length);
            if (nwCap.length < 23) {
                for (int i = n; i< names.length; i++) {
                    names[i] = names[i] + ": NA";
                }
            }
            if (nwCap.length > 26) {
                //3CC
                if(nwCap[25] != null) nwCap[25]=nwCap[25].trim();
                String isSupport = SupportivityMap.get(nwCap[25]);
                if(isSupport == null) isSupport = Supportivity.NA.getValue();
                names[10] = names[10] + ": " + isSupport;

                //256QAM
                if(nwCap[26] != null) nwCap[26]=nwCap[26].trim();
                isSupport = SupportivityMap.get(nwCap[26]);
                if(isSupport == null) isSupport = Supportivity.NA.getValue();
                names[11] = names[11] + ": " + isSupport;
            }
        } else {
            for (int i = n; i< names.length; i++) {
                names[i] = names[i] + ": NA";
            }
        }
        //add ipv6
        n = 7;
        result = IATUtils.sendATCmd(engconstents.ENG_AT_APNQUERY, simIdx);
        Log.d(TAG, "ENG_AT_APNQUERY: " + result);
        String[] str1 = result.split("\n");
        String ipv6Support = "not support";
        for (String str : str1) {
            if (str.contains("+CGDCONT:11")) {
                continue;
            }
            if (str.contains("V6") || str.contains("v6")) {
                ipv6Support = "support";
                break;
            }
        }
        names[n] = names[n] + ": " + ipv6Support;
        n++;
        result = IATUtils.sendATCmd(engconstents.ENG_AT_GET_ASRVCC, simIdx);
        Log.d(TAG, "ENG_AT_GET_ASRVCC = " + result);
        String asrvccSupport = "not support";
        if (result.contains(IATUtils.AT_OK)) {
            String str = "25,0,";
            int index = result.indexOf(str);
            if (index >= 0 && result.substring(index+str.length(), index+str.length()+1).equals("1")) {
                asrvccSupport = "support";
            }
        }
        if (n < names.length) {
            names[n] = names[n] + ": " + asrvccSupport;
        }
        n++;
        result = IATUtils.sendATCmd("AT+SPENGMDVOLTE=29,0", simIdx);
        Log.d(TAG, "AT+SPENGMDVOLTE=29,0 : " + result);
        String esrvccSupport = "not support";
        if (result.contains(IATUtils.AT_OK)) {
            String str = "29,0,";
            int index = result.indexOf(str);
            if (index >= 0 && result.substring(index+str.length(), index+str.length()+1).equals("1")) {
                esrvccSupport = "support";
            }
        }
        if (n < names.length) {
            names[n] = names[n] + ": " + esrvccSupport;
        }

        return Arrays.stream(names).filter(name -> !name.contains("R9")).collect(Collectors.toList());
    }

    private String switchToBandWidth(String colon) {
        int c = Integer.parseInt(colon);
        Log.d(TAG, "c = " + c);
        String bandWidth = "";
        switch (c) {
            case 0: bandWidth = "1.4M";
                break;
            case 1: bandWidth = "3M";
                break;
            case 2: bandWidth = "5M";
                break;
            case 3: bandWidth = "10M";
                break;
            case 4: bandWidth = "15M";
                break;
            case 5: bandWidth = "20M";
                break;
            case 255: bandWidth = "NA";
                break;
            default:
                break;
        }
        Log.d(TAG, "bandWidth = " + bandWidth);
        return bandWidth;
    }

}
