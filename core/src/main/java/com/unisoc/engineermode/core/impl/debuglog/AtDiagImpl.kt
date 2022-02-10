package com.unisoc.engineermode.core.impl.debuglog

import com.unisoc.engineermode.core.annotation.Implementation
import com.unisoc.engineermode.core.intf.IAtDiag
import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy
import com.unisoc.engineermode.core.impl.nonpublic.UsbManagerProxy;

@Implementation(
    interfaceClass = IAtDiag::class
)
object AtDiagImpl : IAtDiag {

    private const val SYS_MODEM_DIAG = "persist.vendor.sys.modem.diag"

    override fun isAtDiagEnabled(): Boolean {
        return SystemPropertiesProxy.get(SYS_MODEM_DIAG, "disable").equals(",gser");
    }

    override fun openAtDiag() {
        SystemPropertiesProxy.set(SYS_MODEM_DIAG, ",gser")
        UsbManagerProxy.setCurrentFunctions(UsbManagerProxy.FUNCTION_NONE);
    }

    override fun closeAtDiag() {
        SystemPropertiesProxy.set(SYS_MODEM_DIAG, "disable")
        UsbManagerProxy.setCurrentFunctions(UsbManagerProxy.FUNCTION_NONE);
    }
}