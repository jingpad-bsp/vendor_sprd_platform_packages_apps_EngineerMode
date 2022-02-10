package com.unisoc.engineermode.core.impl.telephony;

import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.utils.IATUtils;

class VolteEnableImpl implements ITelephonyApi.IVolteEnable {

    private static final String KEY_VOLTE_ENABLE = "persist.vendor.sys.volte.enable";

    public static class VolteEnableImplHolder {
        static final ITelephonyApi.IVolteEnable INSTANCE = new VolteEnableImpl();
    }

    @Override
    public void set(boolean value) {
        SystemPropertiesProxy.set(KEY_VOLTE_ENABLE, String.valueOf(value));
        //UNISOC: add for bug1412063, send AT Command when close volte
        if (!value) {
            IATUtils.sendATCmd("AT+CAVIMS=0", 0);
            IATUtils.sendATCmd("AT+CAVIMS=0", 1);
        }
    }

    @Override
    public boolean get() {
        return SystemPropertiesProxy.getBoolean(KEY_VOLTE_ENABLE, false);
    }
}
