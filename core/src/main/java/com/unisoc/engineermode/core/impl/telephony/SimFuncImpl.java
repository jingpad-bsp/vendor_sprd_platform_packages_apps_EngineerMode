package com.unisoc.engineermode.core.impl.telephony;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.utils.IATUtils;

import static com.unisoc.engineermode.core.common.CommonKt.appCtx;

class SimFuncImpl implements ITelephonyApi.ISimFunc {
    private static final String TAG = "SIMFUNC";

    @Override
    public String readPlmn(int simIdx) throws Exception {
        String cmd;
        if (isSimExist(simIdx)) {
            if (isUsim(simIdx)) {
                cmd = engconstents.ENG_RPLMN_USIM;
            } else {
                cmd = engconstents.ENG_RPLMN_SIM;
            }
            String rsp = IATUtils.sendATCmd(cmd, simIdx);
            Log.d(TAG, rsp);
            return paserFPLMN(rsp);
        } else {
            throw new UnsupportedOperationException("sim not exists");
        }
    }



    private String paserFPLMN(String atRSP) {
        final int FPLMN_MAX = 4;
        String infoPasered = "";
        if (atRSP.contains(IATUtils.AT_OK)) {
            String[] str = atRSP.split(",");
            for (int i = 0; i < FPLMN_MAX; i++) {
                int start = i * 6;
                if (0 != str[2].substring(start + 1, start + 2).compareTo("F")) {
                    infoPasered += "MCC:";
                    infoPasered += str[2].substring(start + 1, start + 2);
                    infoPasered += str[2].substring(start + 0, start + 1);
                    infoPasered += str[2].substring(start + 3, start + 4);
                    infoPasered += " ";
                    infoPasered += "MNC:";
                    if (0 != str[2].substring(start + 2, start + 3).compareTo(
                            "F")) {
                        infoPasered += str[2].substring(start + 2, start + 3);
                    }
                    infoPasered += str[2].substring(start + 5, start + 6);
                    infoPasered += str[2].substring(start + 4, start + 5);
                    infoPasered += "\n";
                }
            }

            return infoPasered;
        } else {
            return "Error";
        }
    }

    private boolean isSimExist(int simIndex) {
        TelephonyManager telephonyManager = (TelephonyManager) appCtx.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager == null) {
            return false;
        }
        return telephonyManager.getSimState(simIndex) == TelephonyManager.SIM_STATE_READY;
    }

    private boolean isUsim(int simIndex) {
        String rsp = IATUtils.sendATCmd(
                "AT+CRSM=192,28539,0,0,15,0,\"3F007FFF\"", "atchannel"
                        + simIndex);

        Log.d(TAG, rsp);
        if (rsp.contains(IATUtils.AT_OK)) {
            String[] str = rsp.split(",");
            if (0 == str[2].substring(0, 1).compareTo("0")
                    && 0 == str[2].substring(1, 2).compareTo("0")
                    && 0 == str[2].substring(2, 3).compareTo("0")) {
                return true;
            }
        }

        return false;
    }
}
