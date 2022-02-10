package com.unisoc.engineermode.core.impl.debuglog

import com.unisoc.engineermode.core.annotation.Implementation
import com.unisoc.engineermode.core.intf.ICpuDebug
import com.unisoc.engineermode.core.exception.OperationFailedException
import com.unisoc.engineermode.core.impl.hardware.PhaseCheckParse;

@Implementation(
    interfaceClass = ICpuDebug::class
)

object CpuDebugImpl : ICpuDebug {
    private const val CPU_Debug_PATH = "/proc/sprd_hang_debug/wdt_disable"

    @Throws(Exception::class)
    override fun set(isOn: Boolean) {
        val success: Boolean
        if (isOn) {
            success = PhaseCheckParse().setCpuDebug(0)
        } else {
            success = PhaseCheckParse().setCpuDebug(1)
        }

        if (!success) {
            throw OperationFailedException("set cpu debug failed")
        }
    }

    override fun get(): Boolean {
        val cpuDebugState = PhaseCheckParse().getCpudebug()
        return "0" == cpuDebugState.trim()
    }
}