package com.unisoc.engineermode.core.impl.telephony;

import android.util.Log;

import com.unisoc.engineermode.core.annotation.Implementation;
import com.unisoc.engineermode.core.exception.OperationFailedException;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.intf.ITelephonyApi.IVamos;
import com.unisoc.engineermode.core.utils.IATUtils;


@Implementation(
    interfaceClass = IVamos.class,
    properties =  {
    })
public class VamosImpl implements IVamos {
    private static final String TAG = "ENGVAMOS";

    @Override
    public VamosState getState() {

        String atCmd = "AT+SPENGMD=0,0,7";
        String atResponse = IATUtils.sendATCmd(atCmd, "atchannel0");

        if (!atResponse.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException("AT failed: " + atResponse);
        }
        VamosState state = new VamosState();

        final String[] stateStr = atResponse.split("-");
        if (stateStr.length > 7 && stateStr[7].contains(",")) {
            final String[] temp = stateStr[7].split(",");
            if (temp.length >= 2) {
                if (temp[0].equals("1")) {
                    state.isSupported = true;
                    if (temp[1].equals("1")) {
                        state.isWorking = true;
                    }
                }
            }
        } else {
            throw new OperationFailedException("AT response parse error: " + atResponse);
        }

        return state;
    }

    @Override
    public void openCapability() {
        setCapability(true);
    }

    @Override
    public void closeCapability() {
        setCapability(false);
    }

    private void setCapability(boolean isOpened) {
        String value = isOpened? "1":"0";
        String atCmd = "AT+SPENGMD=1,0,7," + value;
        Log.d(TAG, "SET_STATUS atCmd = " + atCmd);
        String atResponse = IATUtils.sendATCmd(atCmd, "atchannel0");
        Log.d(TAG, "SET_STATUS responValue = " + atResponse);

        if (!atResponse.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException("AT Failed: " + atResponse);
        }


    }
}
