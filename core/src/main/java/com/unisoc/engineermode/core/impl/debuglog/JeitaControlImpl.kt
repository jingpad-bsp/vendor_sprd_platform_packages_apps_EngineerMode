package com.unisoc.engineermode.core.impl.debuglog

import com.unisoc.engineermode.core.annotation.Implementation
import com.unisoc.engineermode.core.intf.IJeitaControl
import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy

@Implementation(
    interfaceClass = IJeitaControl::class
)

object JeitaControlImpl : IJeitaControl {
    private const val TAG = "JeitaControlImpl"
    private const val JEITA_CONTROL_SWITCH = "persist.vendor.eng.jeita"

    override fun getJeita(): String {
        return SystemPropertiesProxy.get(JEITA_CONTROL_SWITCH, "1")
    }

    override fun setJeita(value: String) {
        SystemPropertiesProxy.set(JEITA_CONTROL_SWITCH, value)
    }
}