package com.unisoc.engineermode.core.impl.telephony;

import android.os.INetworkAdapterService;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

import com.unisoc.engineermode.core.intf.ITelephonyApi.IDnsFilter;
import com.unisoc.engineermode.core.annotation.Implementation;

@Implementation(
    interfaceClass = IDnsFilter.class,
    properties =  {
    })
public class DnsFilterImpl implements IDnsFilter {
    private static final String TAG = "DnsFilterImpl";
    private static final String NETWORKADAPTER_SERVICE = "network_adapter";

//    private static class Holder {
//        static final DnsFilterImpl INSTANCE = new DnsFilterImpl();
//    }
//
//    public static DnsFilterImpl newInstance() {
//        return Holder.INSTANCE;
//    }

    @Override
    public void set(int type) {
        INetworkAdapterService networkAdapterObject  = INetworkAdapterService.Stub.asInterface(ServiceManager.getService(NETWORKADAPTER_SERVICE));
        try {
            networkAdapterObject.setDnsFilterEnable(type);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "DnsFilter set ok, " + type);
    }
}
