package com.unisoc.engineermode.core.impl.telephony;

import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;
import com.unisoc.engineermode.core.intf.ITelephonyApi;

class EngTestStatusImpl implements ITelephonyApi.IEngTestStatus {
    private static final String ENGTEST_ENABLE_TYPE = "persist.vendor.radio.engtest.enable";

    public static class EngTestStatusImplHolder {
        static final ITelephonyApi.IEngTestStatus INSTANCE = new EngTestStatusImpl();
    }

    @Override
    public boolean isEngTest() {
        return SystemPropertiesProxy.get(ENGTEST_ENABLE_TYPE).contains("true");
    }

    @Override
    public void set(int type){
        if (type == 1) {
            SystemPropertiesProxy.set(ENGTEST_ENABLE_TYPE, "true");
        } else {
            SystemPropertiesProxy.set(ENGTEST_ENABLE_TYPE, "false");
        }
    }
}
