package com.unisoc.engineermode.core

import android.content.Context
import com.unisoc.engineermode.core.common.appCtx
import com.unisoc.engineermode.core.factory.Config
import com.unisoc.engineermode.core.factory.ImplementationFactory

import com.unisoc.engineermode.core.impl.connectivity.ConnectivityApiImpl
import com.unisoc.engineermode.core.intf.IConnectivityApi
import com.unisoc.engineermode.core.intf.IHardwareApi
import com.unisoc.engineermode.core.intf.ITelephonyApi
import com.unisoc.engineermode.core.impl.telephony.TelephonyApiImpl
import com.unisoc.engineermode.core.intf.IDebugLogApi
import com.unisoc.engineermode.core.impl.debuglog.DebugLogApiImpl
import com.unisoc.engineermode.core.impl.hardware.HardwareApiImpl

object CoreApi {

    @JvmStatic
    val telephonyApi: ITelephonyApi
        get() = TelephonyApiImpl.getInstance()

    @JvmStatic
    val debugLogApi: IDebugLogApi
        get() = DebugLogApiImpl

    @JvmStatic
    val connectivityApi: IConnectivityApi
        get() = ConnectivityApiImpl.getInstance()

    @JvmStatic
    val hardwareApi: IHardwareApi
        get() = HardwareApiImpl

//    @JvmStatic
//    fun setContext(ctx : Context) {
//        appCtx = ctx
//    }

    @JvmStatic
    fun init(ctx : Context) {
        appCtx = ctx
        ImplementationFactory.init(ctx)
    }
}
