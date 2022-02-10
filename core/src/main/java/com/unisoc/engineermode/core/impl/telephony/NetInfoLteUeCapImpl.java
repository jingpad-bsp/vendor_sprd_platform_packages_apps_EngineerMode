package com.unisoc.engineermode.core.impl.telephony;

import android.util.Log;

import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.utils.IATUtils;

class NetInfoLteUeCapImpl implements ITelephonyApi.INetInfoLteUeCap {
    private static final String TAG = "NetInfoUeCap";
    private static final String OPEN = "1";
    private static final String CLOSE = "0";

    public static class NetInfoLteUeCapImplHolder {
        static final ITelephonyApi.INetInfoLteUeCap INSTANCE = new NetInfoLteUeCapImpl();
    }


    @Override
    public boolean getCap(int simIdx, boolean[] isOn) {

        int num = 0;
        String channel = "atchannel" + simIdx;
        String result = IATUtils.sendATCmd("AT+SPENGMD=0,0,7", channel);

        if (result.contains(IATUtils.AT_OK)) {
            result = result.replaceAll("--", "-+");
            String[] str1 = result.split("\n");
            String[] str2 = str1[0].split("-");
            if (str2.length < 27) {
                Log.d(TAG, "wrong data");
                return false;
            }else {
                for (int i=15; i<str2.length; i++) {
                     /*if (i == 15) {
                        String[] str = str2[i].split(",");
                        LteUeCap[num++].setChecked(str[0].equals("1") ? true : false);
                        continue;
                    }*/
                    if (i == 17) {
                        String[] ca = str2[i].split(",");
                        if (ca.length < 2) {
                            Log.d(TAG, "data wrong");
                            continue;
                        }
                        isOn[num++] = ca[0].equals("1");
                        isOn[num++] = ca[1].equals("1");
                        continue;
                    }
                    /*if (i == 19 || i == 26) {
                        Log.d(TAG, "num = " + num);
                        continue;
                    }*/
                    if (i == 25) {
                        isOn[num++] = str2[i].equals("1");
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public boolean openDlca(int simIdx) {
        String at = engconstents.ENG_AT_SET_DLCA + OPEN;
        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
    }

    @Override
    public boolean closeDlca(int simIdx) {
        String at = engconstents.ENG_AT_SET_DLCA + CLOSE;
        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
    }

    @Override
    public boolean openUlca(int simIdx) {
        String at = engconstents.ENG_AT_SET_ULCA + "1,10";
        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
    }

    @Override
    public boolean closeUlca(int simIdx) {
        String at = engconstents.ENG_AT_SET_ULCA + "0,10";
        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
    }

    @Override
    public boolean openUl64Qam(int simIdx) {
        String at = engconstents.ENG_AT_SET_UL_64QAM + "1,0,7,3,1";
        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
    }

    @Override
    public boolean closeUl64Qam(int simIdx) {
        String at = engconstents.ENG_AT_SET_UL_64QAM + "1,0,7,3,0";
        return IATUtils.sendATCmd(at, simIdx).contains(IATUtils.AT_OK);
    }


}

