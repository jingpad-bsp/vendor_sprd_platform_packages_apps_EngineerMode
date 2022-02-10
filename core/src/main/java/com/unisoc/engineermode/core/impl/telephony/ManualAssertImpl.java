package com.unisoc.engineermode.core.impl.telephony;

import android.util.Log;

import com.unisoc.engineermode.core.annotation.Implementation;
import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;
import com.unisoc.engineermode.core.exception.OperationFailedException;
import com.unisoc.engineermode.core.exception.UnsupportedFeatureException;
import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.intf.ITelephonyApi.IManualAssert;
import com.unisoc.engineermode.core.utils.HidlUtils;
import com.unisoc.engineermode.core.utils.IATUtils;
import com.unisoc.engineermode.core.utils.SocketUtils;
import com.unisoc.engineermode.core.common.Const;

@Implementation(
    interfaceClass = IManualAssert.class,
    properties =  {
    })
public class ManualAssertImpl implements IManualAssert {
    private static final String TAG = "MANUALASSERT";
    private static final String AT_OPEN_CP2 = "poweron";
    private static final String PROP_CP2_RESET = "persist.sys.sprd.wcnreset";

    @Override
    public void assertModem() {
        IATUtils.sendATCmd(engconstents.ENG_AT_SET_MANUAL_ASSERT, "atchannel0");
    }

    @Override
    public boolean isModemResetting() {
        return SystemPropertiesProxy.getBoolean("persist.vendor.sys.sprd.modemreset", false);
    }

    @Override
    public boolean isCp2Reset() {
        return SystemPropertiesProxy.getBoolean(PROP_CP2_RESET, false);
    }

    @Override
    public void enableCp2Reset() {
        SystemPropertiesProxy.set(PROP_CP2_RESET, "1");
    }

    @Override
    public void disableCp2Reset() {
        SystemPropertiesProxy.set(PROP_CP2_RESET, "0");
    }

    @Override
    public void powerOnCp2() {
        if(!canAssertCp2()){
            throw new UnsupportedFeatureException("not support in user");
        }else{
            String atResponse = HidlUtils.sendCmd("wcnd " + "wcn " + AT_OPEN_CP2);
            if (atResponse != null && atResponse.contains(SocketUtils.OK)) {
                Log.d(TAG, "Power on OK");
            } else {
                Log.d(TAG, "Power on fail");
                throw new OperationFailedException("power on cp2 failed");
            }
        }
    }

    @Override
    public boolean canResetCp2() {
        return Const.isMarlin() && Const.isUser();
    }

    @Override
    public boolean canAssertCp2() {
        return Const.isMarlin() || !Const.isUser();
    }
}
