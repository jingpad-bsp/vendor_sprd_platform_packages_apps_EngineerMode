package com.unisoc.engineermode.core.impl.hardware;

import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.exception.ErrorCode;
import com.unisoc.engineermode.core.exception.OperationFailedException;
import com.unisoc.engineermode.core.intf.IAsdiv;
import com.unisoc.engineermode.core.utils.IATUtils;


class AsdivImpl implements IAsdiv {
    private static final String TAG = "EM-Asdiv";

    private int SIM0 = 0;

    public static class AsdivImplHolder {
        public static IAsdiv INSTANCE = new AsdivImpl();
    }

    @Override
    public void setLteToAntenna1() throws Exception {
        String atRSP = IATUtils.sendATCmd(engconstents.ENG_AT_SET_DPDT_PARAM + "16,1,1,0", SIM0);
        judgeAtResponse(atRSP);
    }

    @Override
    public void setLteToAntenna2() throws Exception {
        String atRSP = IATUtils.sendATCmd(engconstents.ENG_AT_SET_DPDT_PARAM + "16,1,1,1", SIM0);
        judgeAtResponse(atRSP);
    }

    @Override
    public void setGsmToAntenna1() throws Exception {
        String atRSP = IATUtils.sendATCmd(engconstents.ENG_AT_SET_DPDT_PARAM + "0,1,1,0", SIM0);
        judgeAtResponse(atRSP);
    }

    @Override
    public void setGsmToAntenna2() throws Exception {
        String atRSP = IATUtils.sendATCmd(engconstents.ENG_AT_SET_DPDT_PARAM + "0,1,1,1", SIM0);
        judgeAtResponse(atRSP);
    }

    @Override
    public void setWcdmaToAntenna1() throws Exception {
        String atRSP = IATUtils.sendATCmd(engconstents.ENG_AT_SET_DPDT_PARAM + "1,1,1,0", SIM0);
        judgeAtResponse(atRSP);
    }

    @Override
    public void setWcdmaToAntenna2() throws Exception {
        String atRSP = IATUtils.sendATCmd(engconstents.ENG_AT_SET_DPDT_PARAM + "1,1,1,1", SIM0);
        judgeAtResponse(atRSP);
    }

    @Override
    public void setC2kToAntenna1() throws Exception {
        String atRSP = IATUtils.sendATCmd(engconstents.ENG_AT_SET_WASDUMMY_PARAM + "\"set C2K antenna" + "\"" + ",1,0,0,0,0,0", SIM0);
        judgeAtResponse(atRSP);
    }

    @Override
    public void setC2kToAntenna2() throws Exception {
        String atRSP = IATUtils.sendATCmd(engconstents.ENG_AT_SET_WASDUMMY_PARAM + "\"set C2K antenna" + "\"" + ",1,1,0,0,0,0", SIM0);
        judgeAtResponse(atRSP);
    }

    @Override
    public void closeAllAntennas() throws Exception {
        String atRSP = IATUtils.sendATCmd(engconstents.ENG_AT_SET_DPDT_PARAM + "16,1,0,0", SIM0);
        judgeAtResponse(atRSP);
        atRSP = IATUtils.sendATCmd(engconstents.ENG_AT_SET_DPDT_PARAM + "16,1,0,1", SIM0);
        judgeAtResponse(atRSP);
        atRSP = IATUtils.sendATCmd(engconstents.ENG_AT_SET_DPDT_PARAM + "0,1,0,0", SIM0);
        judgeAtResponse(atRSP);
        atRSP = IATUtils.sendATCmd(engconstents.ENG_AT_SET_DPDT_PARAM + "0,1,0,1", SIM0);
        judgeAtResponse(atRSP);
        atRSP = IATUtils.sendATCmd(engconstents.ENG_AT_SET_DPDT_PARAM + "1,1,0,0", SIM0);
        judgeAtResponse(atRSP);
        atRSP = IATUtils.sendATCmd(engconstents.ENG_AT_SET_DPDT_PARAM + "1,1,0,1", SIM0);
        judgeAtResponse(atRSP);
    }

    private void judgeAtResponse(String atRSP) throws Exception {
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }
}
