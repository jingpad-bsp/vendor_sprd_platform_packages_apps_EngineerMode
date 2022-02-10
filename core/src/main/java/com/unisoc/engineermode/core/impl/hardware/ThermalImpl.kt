package com.unisoc.engineermode.core.impl.hardware

import android.util.Log
import com.unisoc.engineermode.core.annotation.Implementation
import com.unisoc.engineermode.core.exception.OperationFailedException
import com.unisoc.engineermode.core.exception.UnsupportedFeatureException
import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy
import com.unisoc.engineermode.core.intf.IThermal


import vendor.sprd.hardware.thermal.V1_0.Constants
import vendor.sprd.hardware.thermal.V1_0.IExtThermal

@Implementation(
    interfaceClass = IThermal::class
)
object ThermalImpl : IThermal {
    private const val TAG = "EM-THERMAL"
    private const val PROP_NAME = "persist.sys.thermal.ipa"

    private fun getThermalService(): IExtThermal {
        return IExtThermal.getService()
            ?: throw UnsupportedFeatureException("get thermal service failed")
    }

    override fun switchThermalOn() {
        try {
            getThermalService().setExtThermal(Constants.ExtThermalCmd.THMCMD_SET_EN)
        } catch (e: Exception) {
            e.printStackTrace()
            throw OperationFailedException()
        }
    }

    override fun switchThermalOff() {
        try {
            getThermalService().setExtThermal(Constants.ExtThermalCmd.THMCMD_SET_DIS)
        } catch (e: Exception) {
            e.printStackTrace()
            throw OperationFailedException()
        }
    }

    override fun switchThermalPaOn() {
        try {
            getThermalService().setExtThermal(Constants.ExtThermalCmd.THMCMD_SET_PA_EN)
        } catch (e: Exception) {
            e.printStackTrace()
            throw OperationFailedException()
        }
    }

    override fun switchThermalPaOff() {
        try {
            getThermalService().setExtThermal(Constants.ExtThermalCmd.THMCMD_SET_PA_DIS)
        } catch (e: Exception) {
            e.printStackTrace()
            throw OperationFailedException()
        }
    }

    override fun switchThermalChargeOn() {
        try {
            getThermalService().setExtThermal(Constants.ExtThermalCmd.THMCMD_SET_CHG_EN)
        } catch (e: Exception) {
            e.printStackTrace()
            throw OperationFailedException()
        }
    }

    override fun switchThermalChargeOff() {
        try {
            getThermalService().setExtThermal(Constants.ExtThermalCmd.THMCMD_SET_CHG_DIS)
        } catch (e: Exception) {
            e.printStackTrace()
            throw OperationFailedException()
        }
    }

    override fun switchThermalIpaOn() {
        try {
            SystemPropertiesProxy.set(PROP_NAME, "1")
        } catch (e: Exception) {
            e.printStackTrace()
            throw OperationFailedException()
        }
    }

    override fun switchThermalIpaOff() {
        try {
            SystemPropertiesProxy.set(PROP_NAME, "0")
        } catch (e: Exception) {
            e.printStackTrace()
            throw OperationFailedException()
        }
    }

    override fun getThermalState(): Boolean {
        try {
            return getThermalService().getExtThermal(Constants.ExtThermalCmd.THMCMD_GET_STAT)
        } catch (e: Exception) {
            e.printStackTrace()
            throw OperationFailedException()
        }
    }

    override fun getThermalPaState(): Boolean {
        try {
            return getThermalService().getExtThermal(Constants.ExtThermalCmd.THMCMD_GET_PA_STAT)
        } catch (e: Exception) {
            e.printStackTrace()
            throw OperationFailedException()
        }
    }

    override fun getThermalChargeState(): Boolean {
        try {
            return getThermalService().getExtThermal(Constants.ExtThermalCmd.THMCMD_GET_CHG_STAT)
        } catch (e: Exception) {
            e.printStackTrace()
            throw OperationFailedException()
        }
    }

    override fun getThermalIpaState(): Boolean {
        try {
            return SystemPropertiesProxy.get("persist.sys.thermal.ipa", "1").equals("1")
        } catch (e: Exception) {
            e.printStackTrace()
            throw OperationFailedException()
        }
    }
}

