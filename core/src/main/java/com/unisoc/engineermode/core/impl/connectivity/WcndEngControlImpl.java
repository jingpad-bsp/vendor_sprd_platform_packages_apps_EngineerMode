package com.unisoc.engineermode.core.impl.connectivity;


import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;
import com.unisoc.engineermode.core.intf.IConnectivityApi;

class WcndEngControlImpl implements IConnectivityApi.IWcndEngControl {
    private static final String TAG = "EM-WcndEngControlImpl";
    private static final String WCND_ENG_ENABLE = "persist.vendor.wcnd.eng.enable";

    public static class WcndEngControlImplHolder {
        public static IConnectivityApi.IWcndEngControl INSTANCE = new WcndEngControlImpl();
    }

    @Override
    public boolean isWcndEngRunning() throws Exception {
        return SystemPropertiesProxy.get(WCND_ENG_ENABLE).equals("1");
    }

    @Override
    public void startWcndEng() throws Exception {
        SystemPropertiesProxy.set(WCND_ENG_ENABLE, "1");
    }

    @Override
    public void stopWcndEng() throws Exception {
        SystemPropertiesProxy.set(WCND_ENG_ENABLE, "0");
    }
}
