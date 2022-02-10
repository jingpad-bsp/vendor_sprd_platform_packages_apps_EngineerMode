package com.unisoc.engineermode.core.impl.telephony;

import android.util.Log;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.utils.IATUtils;
import java.util.ArrayList;
import java.util.List;

class NetInfoTdscdmaImpl implements ITelephonyApi.INetInfoTdscdma {
    private static final String TAG = "NetInfoTdscdma";

    private static String[][] Description_name = {
        {"Not Support", "Support"},
        {"Not Support", "Support"},
        {"Unknown", "GEA1", "GEA2", "GEA3"},
        {"A51", "A52", "A53", "A54", "A55", "A56", "A57"},
        {"Unknown", "UEA0", "UEA1"},
        {
            "Not support Hsdpa and Hsupa",
            "Support Hsdpa",
            "Support Hsupa",
            "Support Hsdpa and Hsupa"
        },
        {"Not Support", "Support"},
        {"Not Support", "VAMOS1", "VAMOS2"},
        {"Not Support", "Support"},
        {"Not Support", "Support"},
        {"other", "R8", "R9"},
        {"Not Support", "Support"},
        {"Unknown", "eea0", "eea1", "eea2"},
        {"Unknown", "eia0 ", "eia1", "eia2"}
    };

    @Override
    public void getServingCell(int simIdx, String[] names, String[] values) {
        int temp;
        String result = IATUtils.sendATCmd("AT+SPENGMD=0,6,1", simIdx);
        Log.d(TAG, "AT+SPENGMD=0,6,1: " + result);
        if (result.contains(IATUtils.AT_OK)) {
            result = result.replaceAll(",-", ",+");
            result = result.replaceAll("--", "-+");
            String[] str1 = result.split("\n");
            String[] str2 = str1[0].split("-");
            for (int i = 0; i < str2.length; i++) {
                if (i == 0) {
                    values[2] = names[2] + ": " + str2[i].replace("+", "-");
                }
                if (i == 1) {
                    values[3] = names[3] + ": " + str2[i].replace("+", "-");
                }
                if (i == 2) {
                    temp = Integer.parseInt(str2[i].replace("+", "-").trim()) - 116;
                    values[5] = names[5] + ": " + String.valueOf(temp) + "dBm";
                }
                if (i == 15) {
                    values[4] = names[4] + ": " + str2[i].replace("+", "-");
                }
                if (i == 16) {
                    values[0] = names[0] + ": " + str2[i].replace("+", "-");
                }
                if (i == 17) {
                    values[1] = names[1] + ": " + str2[i].replace("+", "-");
                }
            }
        } else {
            for (int i = 0; i < 6; i++) {
                values[i] = names[i] + ": NA";
            }
        }
        int num = 6;
        result = IATUtils.sendATCmd("AT+SPENGMD=0,0,5", simIdx);
        Log.d(TAG, "AT+SPENGMD=0,0,5: " + result);
        if (result.contains(IATUtils.AT_OK)) {
            result = result.replaceAll(",-", ",+");
            result = result.replaceAll("--", "-+");
            String[] str1 = result.split("\n");
            String[] str2 = str1[0].split("-");
            if ("".equals(str2[0])) {
                for (int i = 1; i < str2.length; i++) {
                    if (i == 1) {
                        if (str2[1].contains(",")) {
                            String[] str3 = str2[1].split(",");
                            for (int j = 0; j < 3; j++) {
                                if (j == 0) {
                                    values[num] = names[num] + ": " + "-" + str3[0];
                                } else {
                                    values[num] = names[num] + ": " + str3[j].replace("+", "-");
                                }
                                num++;
                            }
                        } else {
                            for (int j = 0; j < 3; j++) {
                                values[num] = names[num] + ": " + str2[1].replace("+", "-");
                                num++;
                            }
                        }
                    } else {
                        if (num < names.length) {
                            values[num] = names[num] + ": " + str2[i].replace("+", "-");
                            num++;
                        }
                    }
                }
            } else {
                for (int i = 0; i < str2.length; i++) {
                    if (i == 0) {
                        if (str2[0].contains(",")) {
                            String[] str3 = str2[0].split(",");
                            for (int j = 0; j < 3; j++) {
                                values[num] = names[num] + ": " + str3[j].replace("+", "-");
                                num++;
                            }
                        } else {
                            for (int j = 0; j < 3; j++) {
                                values[num] = names[num] + ": " + str2[0].replace("+", "-");
                                num++;
                            }
                        }
                    } else {
                        if (num < names.length) {
                            values[num] = names[num] + ": " + str2[i].replace("+", "-");
                            num++;
                        }
                    }
                }
            }
        } else {
            for (int i = 6; i < names.length; i++) {
                values[i] = names[i] + ": NA";
            }
        }
    }

    @Override
    public void getAdjacentCell(int simIdx, String[][] values) {
        int temp;
        int ROW = values.length;
        int COL = values[0].length;

        String result = IATUtils.sendATCmd("AT+SPENGMD=0,6,2", simIdx);
        Log.d(TAG, "AT+SPENGMD=0,6,2: " + result);

        if (result.contains(IATUtils.AT_OK)) {
            result = result.replaceAll(",-", ",+");
            result = result.replaceAll("--", "-+");
            String[] str1 = result.split("\n");
            String[] str2 = str1[0].split("-");
            for (int i = 0; i < 5; i++) {
                if (i < str2.length) {
                    if (str2[i].contains(",")) {
                        String[] str3 = str2[i].split(",");
                        for (int j = 0; j < 3; j++) {
                            if (j == 2) {
                                temp = Integer.parseInt(str3[j].replace("+", "-").trim()) - 116;
                                values[i][j] = String.valueOf(temp) + "dBm";
                            } else {
                                if (-1 == Integer.parseInt(str3[j].replace("+", "-").trim())) {
                                    values[i][j] = "NA";
                                } else {
                                    values[i][j] = str3[j].replace("+", "-");
                                }
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
    public void getBetweenAdjacentCell2G(int simIdx, String[][] values) {
        int temp;
        int ROW = values.length;
        int COL = values[0].length;

        String result = IATUtils.sendATCmd("AT+SPENGMD=0,6,4", simIdx);
        Log.d(TAG, "AT+SPENGMD=0,6,4: " + result);
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
                                temp = Integer.parseInt(str3[j].replace("+", "-").trim()) - 111;
                                if (1936 == temp) {
                                    values[i][j - 1] = "NA";
                                }
                                values[i][j - 1] = String.valueOf(temp) + "dBm";
                            } else {
                                values[i][j - 1] = str3[j].replace("+", "-");
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

        String result = IATUtils.sendATCmd("AT+SPENGMD=0,0,4", simIdx);
        Log.d(TAG, "AT+SPENGMD=0,0,4: " + result);
        int num_4g = 0;
        if (result.contains(IATUtils.AT_OK)) {
            result = result.replaceAll(",-", ",+");
            result = result.replaceAll("--", "-+");
            String[] str1 = result.split("\n");
            String[] str2 = str1[0].split("-");
            for (int i = 0; i < str2.length; i++) {
                if (num_4g < 5) {
                    if (str2[i].contains(",")) {
                        String[] str3 = str2[i].split(",");
                        for (int j = 0; j < 4; j++) {
                            if (j == 3 || j == 2) {
                                if (j == 2) {
                                    temp = Integer.parseInt(str3[j].replace("+", "-").trim()) - 141;
                                } else {
                                    temp =
                                            (Integer.parseInt(str3[j].replace("+", "-").trim())
                                                            - 40)
                                                    / 2;
                                }
                                values[num_4g][j] = String.valueOf(temp) + "dBm";
                            } else {
                                values[num_4g][j] = str3[j].replace("+", "-");
                            }
                        }
                        num_4g++;
                    }
                } else {
                    break;
                }
            }
            if (num_4g < 5) {
                for (int i = num_4g; i < 5; i++) {
                    for (int j = 0; j < 4; j++) {
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
    public List<String> getOutfieldNetworkInfo(int simIdx, String[] names) {
        int num = 0;
        String result = IATUtils.sendATCmd("AT+SPENGMD=0,0,7", simIdx);
        Log.d(TAG, "AT+SPENGMD=0,0,7: " + result);
        if (result.contains(IATUtils.AT_OK)) {
            result = result.replaceAll("--", "-+");
            String[] str1 = result.split("\n");
            String[] str2 = str1[0].split("-");
            for (int i = 0; i < str2.length; i++) {
                if (i == 4 || i == 5 || i == 10) {
                    if (num < names.length) {
                        names[num] =
                                names[num]
                                        + ": "
                                        + Description_name[i][
                                                Integer.valueOf(str2[i].substring(0, 1))];
                        num++;
                    }
                }
            }
        } else {
            for (int i = 0; i < names.length; i++) {
                names[i] = names[i] + ": NA";
            }
        }
        List<String> list = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            if (!names[i].contains("R9")) {
                list.add(names[i]);
            }
        }
        return list;
    }
}
