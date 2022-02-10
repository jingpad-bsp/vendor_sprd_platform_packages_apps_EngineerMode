package com.unisoc.engineermode.core.intf

import com.unisoc.engineermode.core.exception.EmException
import com.unisoc.engineermode.core.factory.BaseInterface

interface IHardwareApi {
    fun chargeApi(): ICharge
    fun sensePll(): ISensePll
    fun coulometerPowerApi(): ICoulometerPower
    fun antennaApi(): IAntenna
    fun asdivApi(): IAsdiv
    fun thermalApi(): IThermal
}

interface ICharge : BaseInterface {
    val chargeState: Boolean
    val chargeLevel: String

    @Throws(Exception::class)
    fun openUsbCharge()

    @Throws(Exception::class)
    fun closeUsbCharge()
}

interface ISensePll : BaseInterface {
    @Throws(Exception::class)
    fun read(address: String, count: Int): String

    @Throws(Exception::class)
    fun write(address: String, data: String)
}

interface ICoulometerPower : BaseInterface {
    val ccTestResult: String
    val ccTestVoltages: String
    val ccResultNewKernel: String
    val ccVoltNewKernel: String

    @Throws(Exception::class)
    fun writeOneToCCTestCmd()

    @Throws(Exception::class)
    fun writeZeroToCCTestCmd()
}

interface IAntenna: BaseInterface {
    enum class AntennaState() {
        PRIMARY_DIVERSITY,
        PRIMARY,
        DIVERSITY,
        PRIMARY_DIVERSITY_DYNAMIC,
        INVALID,
        DEFAULT,
        ANT3_RX,
        ANT6_RX,
        ANT5_RX,
        ANT4_RX,
        ANT3_TX,
        ANT6_TX,
        ANT4_TX,
        ANT5_TX
    }

    data class AllState(
        var gsm: AntennaState = AntennaState.INVALID,
        var wcdma: AntennaState = AntennaState.INVALID,
        var lte: AntennaState = AntennaState.INVALID,
        var c2k: AntennaState = AntennaState.INVALID,
        var nr: AntennaState = AntennaState.INVALID)

    @Throws(EmException::class) fun getAllStates(): AllState
    @Throws(EmException::class) fun setLte(state: AntennaState)
    @Throws(EmException::class) fun setWcdma(state: AntennaState)
    @Throws(EmException::class) fun setC2k(state: AntennaState)
    @Throws(EmException::class) fun setGsm(state: AntennaState)
    @Throws(EmException::class) fun setNr(state: AntennaState)
}

interface IAsdiv: BaseInterface {
    @Throws(Exception::class) fun setLteToAntenna1()
    @Throws(Exception::class) fun setLteToAntenna2()
    @Throws(Exception::class) fun setGsmToAntenna1()
    @Throws(Exception::class) fun setGsmToAntenna2()
    @Throws(Exception::class) fun setWcdmaToAntenna1()
    @Throws(Exception::class) fun setWcdmaToAntenna2()
    @Throws(Exception::class) fun setC2kToAntenna1()
    @Throws(Exception::class) fun setC2kToAntenna2()
    @Throws(Exception::class) fun closeAllAntennas()
}

interface IThermal : BaseInterface {
    fun switchThermalOn()
    fun switchThermalOff()
    fun switchThermalPaOn()
    fun switchThermalPaOff()
    fun switchThermalChargeOn()
    fun switchThermalChargeOff()
    fun switchThermalIpaOn()
    fun switchThermalIpaOff()
    fun getThermalState(): Boolean
    fun getThermalPaState(): Boolean
    fun getThermalChargeState(): Boolean
    fun getThermalIpaState(): Boolean
}
