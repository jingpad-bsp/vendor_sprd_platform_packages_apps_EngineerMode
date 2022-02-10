package com.unisoc.engineermode.core.impl.debuglog;

import com.unisoc.engineermode.core.annotation.Implementation;
import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;
import com.unisoc.engineermode.core.intf.ISfpTest;

@Implementation(
        interfaceClass = ISfpTest.class,
        properties =  {
        })
class SfpTestImpl implements ISfpTest {

    @Override
    public void setSfp(int type) throws Exception {
        if (type == 1) {
            SystemPropertiesProxy.set("ctl.start", "vendor.sfp_on");
        } else {
            SystemPropertiesProxy.set("ctl.start", "vendor.sfp_off");
        }
    }
}