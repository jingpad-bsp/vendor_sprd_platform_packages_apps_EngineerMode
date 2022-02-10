package com.unisoc.engineermode.core.impl.hardware

import com.unisoc.engineermode.core.annotation.Implementation
import com.unisoc.engineermode.core.exception.OperationFailedException
import com.unisoc.engineermode.core.intf.ICharge

@Implementation(
    interfaceClass = ICharge::class
)
object ChargeImpl : ICharge {
    private const val TAG = "EM-CHARGE"

    override val chargeState: Boolean
        @Throws(Exception::class)
        get() {
            val chargeStatus: String?
            try {
                chargeStatus = PhaseCheckParse().readChargeState()
            } catch (e: Exception) {
                throw OperationFailedException("read charge state file failed")
            }

            return chargeStatus!!.contains("1")
        }

    override val chargeLevel: String
        @Throws(Exception::class)
        get() {
            val chargeLevel: String?
            try {
                chargeLevel = PhaseCheckParse().readChargeLevel()
            } catch (e: Exception) {
                throw OperationFailedException("read charge level file failed")
            }

            return chargeLevel
        }


    @Throws(Exception::class)
    override fun openUsbCharge() {
        switchCharge(true)
    }

    @Throws(Exception::class)
    override fun closeUsbCharge() {
        switchCharge(false)
    }

    @Throws(Exception::class)
    private fun switchCharge(isOn: Boolean) {
        val success: Boolean
        if (isOn) {
            success = PhaseCheckParse().writeChargeSwitch(0)
        } else {
            success = PhaseCheckParse().writeChargeSwitch(1)
        }

        if (!success) {
            throw OperationFailedException("switch charge failed")
        }
    }
}
