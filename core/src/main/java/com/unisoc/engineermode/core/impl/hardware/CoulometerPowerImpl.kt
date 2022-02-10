package com.unisoc.engineermode.core.impl.hardware


import com.unisoc.engineermode.core.annotation.Implementation
import com.unisoc.engineermode.core.exception.OperationFailedException
import com.unisoc.engineermode.core.intf.ICoulometerPower


@Implementation(
    interfaceClass = ICoulometerPower::class
)
object CoulometerPowerImpl : ICoulometerPower {
    private const val TAG = "CoulometerPowerImpl"

    override val ccTestResult: String
        @Throws(Exception::class)
        get() {
            try {
                return PhaseCheckParse().ccTestSwitch
            } catch (e: Exception) {
                throw OperationFailedException("get cc result file failed")
            }
        }

    override val ccTestVoltages: String
        @Throws(Exception::class)
        get() {
            try {
                return PhaseCheckParse().ccTestVoltage
            } catch (e: Exception) {
                throw OperationFailedException("get cc Voltage file failed")
            }
        }

    override val ccResultNewKernel: String
        @Throws(Exception::class)
        get() {
            try {
                return PhaseCheckParse().ccEnergyNewKernel
            } catch (e: Exception) {
                throw OperationFailedException("get cc Voltage file failed")
            }
        }

    override val ccVoltNewKernel: String
        @Throws(Exception::class)
        get() {
            try {
                return PhaseCheckParse().ccVoltagesNewKernel
            } catch (e: Exception) {
                throw OperationFailedException("get cc Voltage file failed")
            }
        }

    @Throws(Exception::class)
    override fun writeOneToCCTestCmd() {
        val success = PhaseCheckParse().writeChargeSwitch(1)
        if (!success) {
            throw OperationFailedException("write 1 To CCTestCmd failed")
        }
    }

    @Throws(Exception::class)
    override fun writeZeroToCCTestCmd() {
        val success = PhaseCheckParse().writeChargeSwitch(0)
        if (!success) {
            throw OperationFailedException("write 0 To CCTestCmd failed")
        }
    }
}
