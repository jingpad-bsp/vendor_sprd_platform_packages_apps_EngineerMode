package com.unisoc.engineermode.core.impl.telephony;

import android.util.Log;

import com.unisoc.engineermode.core.exception.ErrorCode;
import com.unisoc.engineermode.core.exception.OperationFailedException;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.utils.IATUtils;

class DataServicePreferImpl implements ITelephonyApi.IDataServicePrefer {
    private static final String TAG = "DATASERVICEPREFER";

    @Override
    public void open() throws Exception {
        String mATCmd = engconstents.ENG_DATA_SERVICES_PRE + 1;
        Log.d(TAG, "OPEN_DATA_SERVICES_PRE");
        String result = IATUtils.sendATCmd(mATCmd, "atchannel0");
        if (!result.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, result);
        }
    }

    @Override
    public void close() throws Exception {
        String mATCmd = engconstents.ENG_DATA_SERVICES_PRE + 0;
        Log.d(TAG, "CLOSE_DATA_SERVICES_PRE");
        String result = IATUtils.sendATCmd(mATCmd, "atchannel0");
        if (!result.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, result);
        }
    }
}
