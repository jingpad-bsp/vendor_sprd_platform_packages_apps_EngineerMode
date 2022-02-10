package com.unisoc.engineermode.core.impl.telephony;

import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;

class VideoTypeImpl implements ITelephonyApi.IVideoType {
    private static final String VALUEKEY_VIDEO_TYPE = "debug.videophone.videotype";

    @Override
    public int get() throws Exception {

        if (true) return 1;
        else return 0;
    }

    @Override
    public void set(int type) throws Exception {
        SystemPropertiesProxy.set(VALUEKEY_VIDEO_TYPE, String.valueOf(type));
    }
}
