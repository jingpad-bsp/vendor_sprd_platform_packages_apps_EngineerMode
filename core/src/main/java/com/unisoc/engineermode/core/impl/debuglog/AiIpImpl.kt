package com.unisoc.engineermode.core.impl.debuglog

import com.unisoc.engineermode.core.annotation.Implementation
import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy
import com.unisoc.engineermode.core.intf.IAiIp


@Implementation(
    interfaceClass = IAiIp::class
)
object AiIpImpl : IAiIp {
    private const val AI_IP_SWITCH = "persist.vendor.npu.version"
    private const val GALLERY_MODULE_SWITCH = "persist.sys.gallery.discover.module"
    private const val GALLERY_MODULE_ENABLE = "persist.sys.gallery.all.modules.enable"
    private const val AI_DEBUG_VLOG = "debug.nn.vlog"

    override val aiIpStatus: String
        get() = SystemPropertiesProxy.get(AI_IP_SWITCH, "")

    override var aiDebugVLog: String
        get() = SystemPropertiesProxy.get(AI_DEBUG_VLOG, "")
        set(vLogName) = SystemPropertiesProxy.set(AI_DEBUG_VLOG, vLogName)

    override fun setAICambriconNPU() {
        SystemPropertiesProxy.set(AI_IP_SWITCH, "2")
        if (SystemPropertiesProxy.getBoolean(GALLERY_MODULE_ENABLE, false)) {
            SystemPropertiesProxy.set(GALLERY_MODULE_SWITCH, "2")
        }
    }

    override fun setAIImaginationNNA() {
        SystemPropertiesProxy.set(AI_IP_SWITCH, "1")
        if (SystemPropertiesProxy.getBoolean(GALLERY_MODULE_ENABLE, false)) {
            SystemPropertiesProxy.set(GALLERY_MODULE_SWITCH, "1")
        }
    }

    override fun setAINone() {
        SystemPropertiesProxy.set(AI_IP_SWITCH, "0")
    }
}
