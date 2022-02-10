package com.unisoc.engineermode.core.impl.telephony;

import com.unisoc.engineermode.core.exception.ErrorCode;
import com.unisoc.engineermode.core.exception.OperationFailedException;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.utils.IATUtils;
import com.unisoc.engineermode.core.impl.nonpublic.TelephonyManagerProxy;

class FastDormancyImpl implements ITelephonyApi.IFastDormancy {

    private static final int FAST_DORMANCY_OPEN = 1;
    private static final int FAST_DORMANCY_CLOSE = 0;
    private static final int SIM0 = 0;
    private static final int SIM1 = 1;

    private int mPhoneCount;

    @Override
    public void close() throws Exception {
        set(FAST_DORMANCY_CLOSE, 0);
    }

    @Override
    public void open(int value) throws Exception {
        set(FAST_DORMANCY_OPEN, value);
    }

    private void set(int state, int value) throws Exception {
        String cmd = engconstents.ENG_AT_SPSETFDY + state;
        if (value != 0) {
            cmd += "," + value;
        }
        String mStrTmp = IATUtils.sendATCmd(cmd, SIM0);
        judgeAtResponse(mStrTmp);

        mPhoneCount = TelephonyManagerProxy.getPhoneCount();
        if (mPhoneCount > 1) {
            mStrTmp = IATUtils.sendATCmd(cmd, SIM1);
            judgeAtResponse(mStrTmp);
        }
    }

    private void judgeAtResponse(String atRSP) throws Exception {
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }
}
