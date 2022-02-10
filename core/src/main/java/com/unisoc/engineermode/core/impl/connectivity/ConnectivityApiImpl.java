package com.unisoc.engineermode.core.impl.connectivity;

import com.unisoc.engineermode.core.common.Const;
import com.unisoc.engineermode.core.intf.IConnectivityApi;

public class ConnectivityApiImpl implements IConnectivityApi {

    private static class ConnectivityApiImplHolder {
        static final IConnectivityApi INSTANCE = new ConnectivityApiImpl();
    }

    public static IConnectivityApi getInstance() {
        return ConnectivityApiImplHolder.INSTANCE;
    }

    @Override
    public IWifiEut wifiEut() {
        return WifiEUTHelperMarlin.WifiEutMarlinImplHolder.INSTANCE;
    }

    @Override
    public IBtEut btEut() {
        return BTHelper.BtHelperHolder.INSTANCE;
    }

    @Override
    public IWcndEngControl wcndEngControl() {
        return WcndEngControlImpl.WcndEngControlImplHolder.INSTANCE;
    }
}
