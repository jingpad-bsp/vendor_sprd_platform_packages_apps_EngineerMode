package com.unisoc.engineermode.core.impl.telephony;

import android.util.Log;

import com.unisoc.engineermode.core.data.telephony.VamosCpcData;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.utils.IATUtils;

class VamosCpcImpl implements ITelephonyApi.IVamosCpc {
    private static final String TAG = "VamosCpc";

    @Override
    public VamosCpcData getVamosCpc() {
        String atCmd = engconstents.ENG_CET_VAMOS_CPC + "0,0,7";
        String result = IATUtils.sendATCmd(atCmd, 0);
        Log.d(TAG, atCmd + ": " + result);

        int vamosValue = -1;
        int cpcValue = -1;

        if (result.contains(IATUtils.AT_OK)) {
            result = result.replaceAll("--", "-+");
            String[] str1 = result.split("\n");
            String[] str2 = str1[0].split("-");
            /*
             * SPRD: FixBug451913 ,EngineerMode crash beacause
             * java.lang.ArrayIndexOutOfBoundsException {@
             */
            if (str2.length > 7) {
                /*BEGIN BUG567691 ansel.li 2016/05/30 EngineerMode has stopped when click vamos&cpc*/
                if (str2[7].contains(",")) {
                    String[] str3 = str2[7].split(",");
                    vamosValue = Integer.valueOf(str3[0].replace("+", "-").trim());
                } else {
                    vamosValue = Integer.valueOf(str2[7].replace("+", "-").trim());
                }
                /*END BUG567691 ansel.li 2016/05/30 EngineerMode has stopped when click vamos&cpc*/
            }
            if (str2.length > 14) {
                cpcValue = Integer.valueOf(str2[14].replace("+", "-").trim().substring(0, 1));
            }

        }

        return new VamosCpcData(vamosValue, cpcValue);
    }
}
