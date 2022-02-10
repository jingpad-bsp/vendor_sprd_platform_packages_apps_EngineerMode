package com.unisoc.engineermode.core.impl.telephony;

import android.util.Log;

import com.unisoc.engineermode.core.exception.OperationFailedException;
import com.unisoc.engineermode.core.intf.ITelephonyApi.INetInfoWcdmaUeCap.UeCap;
import com.unisoc.engineermode.core.utils.IATUtils;

import java.util.ArrayList;
import java.util.List;

public class NetInfoWcdma {

    private static final String TAG = "NetInfoWcdma";

//    public static List<String> getUeNwCap(int simIdx, int type) {
//        ArrayList<String> ueValueList = new ArrayList<>();
//        ArrayList<String> nwValueList = new ArrayList<>();
//        String result007 = IATUtils.sendATCmd("AT+SPENGMD=0,0,7", simIdx);
//        //1-0-0-5-1-3,1,1-0-1,0-0,1-1-2-0-0-0-1,0-1,0,1-1-1,1-0-0-0-240-0-0-1-0-0,0
//        String result008 = IATUtils.sendATCmd("AT+SPENGMD=0,0,8", simIdx);
//        //AT response 6-0-1,0-1,0-1-1-1-1-5-1-7-5-0-0,1-0-1,1,1,0,0,0,0-7-1-0,0-0,1-0,1
//        String[] str007 = null;
//        String[] str008 = null;
//        Log.d(TAG, "AT+SPENGMD=0,0,7: " + result007);
//        Log.d(TAG, "AT+SPENGMD=0,0,8: " + result008);
//        if (!result007.contains(IATUtils.AT_OK) || !result008.contains(IATUtils.AT_OK)) {
//            return null;
//        }
//        if (result007.contains(IATUtils.AT_OK)) {
//            result007 = result007.replace("--", "-+");
//            String[] str1 = result007.split("\n");
//            str007 = str1[0].split("-");
//            if (str007.length < 27) {
//                Log.d(TAG, "wrong data");
//                return null;
//            }
//        }
//        if (result008.contains(IATUtils.AT_OK)) {
//            result008 = result008.replace("--", "-+");
//            String[] str1 = result008.split("\n");
//            str008 = str1[0].split("-");
//            if (str008.length < 21) {
//                Log.d(TAG, "wrong data");
//                return null;
//            }
//        }
//
//        ueValueList.add(str007[14].substring(0, 1));//cpc-ue
//        nwValueList.add(str007[14].substring(str007[14].length()-1));//cpc-nw
//        ueValueList.add(str007[26].substring(str007[26].length()-1));//16qam-ue
//        nwValueList.add(str007[26].substring(0,1));//16qam-nw
//        ueValueList.add(str008[18].substring(str008[18].length()-1));//db-hsdpa-ue
//        nwValueList.add(str008[18].substring(0,1));//db-hsdpa-nw
//        ueValueList.add(str008[13].substring(str008[13].length()-1));//dc-hsdpa-ue
//        nwValueList.add(str008[13].substring(0,1));//dc-hsdpa-nw
//        ueValueList.add(str007[8].substring(str007[8].length()-1));//efach-ue
//        nwValueList.add(str008[3].substring(str008[3].length()-1));//edrx-nw
//        ueValueList.add(str008[2].substring(0,1));//erach-ue
//        nwValueList.add(str008[2].substring(str008[2].length()-1));//erach-nw
//        ueValueList.add(str008[20].substring(str008[20].length()-1));//snow3G-ue
//        nwValueList.add(str008[20].substring(0,1));//snow3G-nw
//        ueValueList.add(str008[9]);//Rx Diversity-ue
//        ueValueList.add(str008[12]);//type3i-nw
//        nwValueList.add(str008[8].substring(str008[8].length()-1));//fast dormancy-nw
//
//        if (str007[5].length() > 1) {
//            ueValueList.add(str007[5].substring(2, 3));//hsdpa-ue
//            ueValueList.add(str007[5].substring(str007[5].length()-1));//hsupa-ue
//        } else {
//            ueValueList.add("-1");//hsdpa-ue
//            ueValueList.add("-1");//hsupa-ue
//        }
//        Log.d(TAG, "ueValueList = " + ueValueList.toString());
//        Log.d(TAG, "nwValueList = " + nwValueList.toString());
//
//        return type == 0 ? ueValueList :nwValueList;
//    }

    public static UeCap getUeCap(int simIdx) {
        String[] caps = getCapability(simIdx);
        String[] nv = getNvInfo(simIdx);

        UeCap uecap = new UeCap();
        uecap.ul16Qam = caps[26].startsWith("1");
        uecap.snow3g = nv[20].endsWith("1");
        uecap.wDiversity = nv[9].equals("1");
        uecap.dbHsdpa = nv[18].endsWith("1");
        if (caps[5].length() > 2) {
            uecap.hsdpa = caps[5].substring(2,3).equals("1");
        } else {
            uecap.hsdpa = false;
        }

        Log.d(TAG, String.format("ueCap = %b, %b, %b, %b, %b",
            uecap.dbHsdpa, uecap.hsdpa, uecap.snow3g, uecap.ul16Qam, uecap.wDiversity));
        return uecap;
    }

    public static List<String> getNwCap(int simIdx) {
        String[] caps = getCapability(simIdx);
        String[] nv = getNvInfo(simIdx);

        ArrayList<String> nwValueList = new ArrayList<>();

        nwValueList.add(caps[14].substring(caps[14].length()-1));//cpc-nw
        nwValueList.add(caps[26].substring(0,1));//16qam-nw
        nwValueList.add(nv[18].substring(0,1));//db-hsdpa-nw
        nwValueList.add(nv[13].substring(0,1));//dc-hsdpa-nw
        nwValueList.add(nv[3].substring(nv[3].length()-1));//edrx-nw
        nwValueList.add(nv[2].substring(nv[2].length()-1));//erach-nw
        nwValueList.add(nv[20].substring(0,1));//snow3G-nw
        nwValueList.add(nv[8].substring(nv[8].length()-1));//fast dormancy-nw

        return nwValueList;
    }

    private static String[] getCapability(int simIdx) {
        String result = IATUtils.sendATCmd("AT+SPENGMD=0,0,7", simIdx);
        if (!result.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException("get capability failed. " + result);
        }

        result = result.replace("--", "-+");
        String[] params = result.split("\n")[0].split("-");
        if (params.length < 27) {
            Log.d(TAG, "wrong data");
            throw new OperationFailedException("get capability failed. " + result);
        }
        return params;
    }

    private static String[] getNvInfo(int simIdx) {
        String result = IATUtils.sendATCmd("AT+SPENGMD=0,0,8", simIdx);
        if (!result.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException("get capability failed. " + result);
        }

        result = result.replace("--", "-+");
        String[] params = result.split("\n")[0].split("-");
        if (params.length < 21) {
            Log.d(TAG, "wrong data");
            throw new OperationFailedException("get capability failed. " + result);
        }
        return params;
    }
}
