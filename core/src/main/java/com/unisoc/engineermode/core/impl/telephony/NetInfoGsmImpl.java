package com.unisoc.engineermode.core.impl.telephony;

import android.util.Log;

import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.utils.IATUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NetInfoGsmImpl implements ITelephonyApi.INetInfoGsm {
    private static final String TAG = "NetInfoGsm" ;
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
        String result = IATUtils.sendATCmd("AT+SPENGMD=0,0,0", simIdx);
        Log.d(TAG, "AT+SPENGMD=0,0,0: " + result);
        if (result.contains(IATUtils.AT_OK)) {
            result = result.replace("--", "-+");
            String[] str1 = result.split("\n");
            String[] str2 = str1[0].split("-");
            for (int i = 0; i < str2.length; i++) {
                if (i == 0) {
                    values[1] = names[1] + ": " + str2[i].replace("+", "-");
                }
                if (i == 1) {
                    values[2] = names[2] + ": " + str2[i].replace("+", "-");
                }
                if (i == 2) {
                    values[0] = names[0] + ": " + str2[i].replace("+", "-");
                }
                if (i == 3) {
                    values[3] = names[3] + ": " + str2[i].replace("+", "-");
                }
                if (i == 4) {
                    temp = Integer.parseInt(str2[i].replace("+", "-").trim()) - 110;
                    values[4] = names[4] + ": " + temp + "dBm";
                }
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

        String result = IATUtils.sendATCmd("AT+SPENGMD=0,0,1", simIdx);
        Log.d(TAG, "AT+SPENGMD=0,0,1: " + result);
        if (result.contains(IATUtils.AT_OK)) {
            result = result.replace(",-", ",+");
            result = result.replace("--", "-+");
            String[] str1 = result.split("\n");
            String[] str2 = str1[0].split("-");
            for (int i = 0; i < str2.length; i++) {
                if (str2[i].contains(",")) {
                    String[] str3 = str2[i].split(",");
                    for (int j = 0; j < 5; j++) {
                        if (i == 0) {
                            if (j >= str3.length) {
                                values[j][1] = "NA";
                            } else {
                                values[j][1] = str3[j].replace("+", "-");
                            }
                        }
                        if (i == 1) {
                            if (j >= str3.length) {
                                values[j][2] = "NA";
                            } else {
                                values[j][2] = str3[j].replace("+", "-");
                            }
                        }
                        if (i == 2) {
                            if (j >= str3.length) {
                                values[j][0] = "NA";
                            } else {
                                values[j][0] = str3[j].replace("+", "-");
                            }
                        }
                        if (i == 3) {
                            if (j >= str3.length) {
                                values[j][3] = "NA";
                            } else {
                                values[j][3] = str3[j].replace("+", "-");
                            }
                        }
                        if (i == 4) {
                            if (j >= str3.length) {
                                values[j][4] = "NA";
                            } else {
                                temp = Integer.parseInt(str3[j].replace("+", "-").trim()) - 110;
                                values[j][4] = temp + "dBm";
                            }
                        }
                    }
                } else {
                    for (int j = 0; j < 5; j++) {
                        if (i == 0) {
                            if (j > 0) {
                                values[j][1] = "NA";
                            } else {
                                values[j][1] = str2[i].replace("+", "-");
                            }
                        }
                        if (i == 1) {
                            if (j > 0) {
                                values[j][2] = "NA";
                            } else {
                                values[j][2] = str2[i].replace("+", "-");
                            }
                        }
                        if (i == 2) {
                            if (j > 0) {
                                values[j][0] = "NA";
                            } else {
                                values[j][0] = str2[i].replace("+", "-");
                            }
                        }
                        if (i == 3) {
                            if (j > 0) {
                                values[j][3] = "NA";
                            } else {
                                values[j][3] = str2[i].replace("+", "-");
                            }
                        }
                        if (i == 4) {
                            if (j > 0) {
                                values[j][4] = "NA";
                            } else {
                                temp = Integer.parseInt(str2[i].replace("+", "-").trim()) - 110;
                                values[j][4] = temp + "dBm";
                            }
                        }
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

        String result = IATUtils.sendATCmd("AT+SPENGMD=0,0,2", simIdx);
        Log.d(TAG, "AT+SPENGMD=0,0,2: " + result);
        if (result.contains(IATUtils.AT_OK)) {
            result = result.replace(",-", ",+");
            result = result.replace("--", "-+");
            String[] str1 = result.split("\n");
            String[] str2 = str1[0].split("-");
            for (int i = 0; i < str2.length; i++) {
                if (str2[i].contains(",")) {
                    String[] str3 = str2[i].split(",");
                    for (int j = 0; j < 5; j++) {
                        if (i < 3) {
                            if (j >= str3.length) {
                                values[j][i] = "NA";
                            } else {
                                if (i == 2) {
                                    temp = Integer.parseInt(str3[j].replace("+","-").trim()) - 116;
                                    values[j][i] = temp + "dBm";
                                } else {
                                    values[j][i] = str3[j].replace("+","-");
                                }
                            }
                        }
                    }
                } else {
                    for (int j = 0; j < 5; j++) {
                        if (i < 3) {
                            if (j > 0) {
                                values[j][i] = "NA";
                            } else {
                                if (i == 2) {
                                    temp = Integer.parseInt(str2[i].replace("+","-").trim()) - 116;
                                    values[j][i] = temp + "dBm";
                                } else {
                                    values[j][i] = str2[i].replace("+","-");
                                }
                            }
                        }
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
    public void getBetweenAdjacentCell4G(int simIdx, String[][] values) {
        int temp;
        int row = values.length;
        int col = values[0].length;

        String result = IATUtils.sendATCmd("AT+SPENGMD=0,0,3", simIdx);
        Log.d(TAG, "AT+SPENGMD=0,0,3: " + result);
        if (result.contains(IATUtils.AT_OK)) {
            result = result.replace(",-", ",+");
            result = result.replace("--", "-+");
            String[] str1 = result.split("\n");
            String[] str2 = str1[0].split("-");
            for (int i = 0; i < 4; i++) {
                if (str2[i].contains(",")) {
                    String[] str3 = str2[i].split(",");
                    for (int j = 0; j < 5; j++) {
                        if (j >= str3.length) {
                            values[j][i] = "NA";
                        } else {
                            if (i == 2 || i == 3) {
                                temp = Integer.parseInt(str3[j].replace("+","-").trim())/100;
                                values[j][i] = temp + "dBm";
                            } else {
                                values[j][i] = str3[j].replace("+","-");
                            }
                        }
                    }
                } else {
                    for (int j = 0; j < 5; j++) {
                        if (j > 0) {
                            values[j][i] = "NA";
                        } else {
                            if (i == 2 || i == 3) {
                                temp = Integer.parseInt(str2[i].replace("+","-").trim())/100;
                                values[j][i] = temp + "dBm";
                            } else {
                                values[j][i] = str2[i].replace("+","-");
                            }
                        }
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

    /** UNISOC: add for bug1629409, change index for AMR_WB
     AT+SPENGMD=0,0,7
     0：1,0-  ( <AMR_WB_FOR_3G>,<AMR_WB_FOR_2G> 1 support;0 not support)
     **/
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
                if (i == 0) {
                    String[] str3 = str2[i].split(",");
                    if (str3.length < 2) {
                        names[num] = names[num] + ": NA";
                    } else {
                        names[num] = names[num]
                                + ": "
                                + Description_name[i][Integer.parseInt(str3[1].trim())];
                    }
                    num++;
                } else if (i == 3) {
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
                    num++;
                } else if (i == 1 || i == 2 || i == 6 || i == 7 || i == 9) {
                    names[num] = names[num]
                            + ": "
                            + Description_name[i][Integer.parseInt(str2[i].substring(0, 1))];
                    num++;
                }
            }
        } else {
            for (int i = 0; i < names.length; i++) {
                names[i] = names[i] + ": NA";
            }
        }
        return Arrays.stream(names).filter(n -> !n.contains("R9")).collect(Collectors.toList());
    }

}
