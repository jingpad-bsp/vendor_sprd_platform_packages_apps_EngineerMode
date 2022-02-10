package com.unisoc.engineermode.core.impl.debuglog

import com.unisoc.engineermode.core.annotation.Implementation
import com.unisoc.engineermode.core.intf.ICabc
import com.unisoc.engineermode.core.exception.OperationFailedException
import com.unisoc.engineermode.core.impl.hardware.PhaseCheckParse;

@Implementation(
    interfaceClass = ICabc::class
)

object CabcImpl : ICabc {

    @Throws(Exception::class)
    override fun set(isOn: Boolean) {
        val success: Boolean
        if (isOn) {
            success = PhaseCheckParse().setCabc(0)
        } else {
            success = PhaseCheckParse().setCabc(1)
        }

        if (!success) {
            throw OperationFailedException("set cabc failed")
        }
    }
}