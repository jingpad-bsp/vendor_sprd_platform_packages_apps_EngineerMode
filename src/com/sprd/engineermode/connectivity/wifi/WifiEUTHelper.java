
package com.sprd.engineermode.connectivity.wifi;

import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.intf.IConnectivityApi.IWifiEut;

public class WifiEUTHelper {

    public static synchronized IWifiEut getHelper() {
        return CoreApi.getConnectivityApi().wifiEut();
    }


}
