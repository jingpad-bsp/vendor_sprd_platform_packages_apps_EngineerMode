package com.unisoc.engineermode.core.intf

import com.unisoc.engineermode.core.factory.BaseInterface

interface IDebugLogApi {
    fun smsCenterNumberApi(): ISMSCenterNumber
    fun aiIpApi(): IAiIp
    fun gcfTestApi(): IGcfTest
    fun phoneInfoApi(): IPhoneInfo
    fun jeitaControlApi(): IJeitaControl
    fun atDiagApi(): IAtDiag
    fun cabcApi(): ICabc
    fun sfpTestApi(): ISfpTest
    fun mipiLogApi(): IMIPILog
    fun cpuDebugApi(): ICpuDebug
}

interface ISMSCenterNumber: BaseInterface {
    fun getSmsCenterNumber(subId: Int): String
    fun setSmsCenterNumber(subId: Int, smsAddress: String): Boolean
}


interface IAiIp: BaseInterface {
    val aiIpStatus: String
    var aiDebugVLog: String
    fun setAICambriconNPU()
    fun setAIImaginationNNA()
    fun setAINone()
}

interface IGcfTest: BaseInterface {
    fun isGcfTestOpened(): Boolean
    fun openGcfTest()
    fun closeGcfTest()
}

interface ISfpTest : BaseInterface {
    @Throws(Exception::class)
    fun setSfp(type: Int)
}

const val INVALID_CELL_ID: Int = -1
interface IPhoneInfo: BaseInterface {
    data class Operator(var mcc: String, var mnc: String)
    data class CdmaInfo(var prl: String, var systemId: Int, var networkId: Int, var baseStationId: Int)

    fun getMemorySize(): String
    fun getAllOpers(): List<Operator>
    fun getAllImsi(): List<String>
    fun getCellId(): List<Int>
    fun getCdmaInfo(): List<CdmaInfo>
    fun getEsn(): String
    fun getMeid(): String
    fun getAllImei(): List<String>
    fun getInternalStorageSize(): String
    fun getSoftwareVersion(): String
    fun getHardwareVersion(): String
    fun getOsVersion(): String
    fun getCdmaImsi(): List<String>
    fun getIccId(): List<String>
}

interface IJeitaControl : BaseInterface {
    fun getJeita() : String
    fun setJeita(value: String)
}

interface IAtDiag : BaseInterface {
    fun isAtDiagEnabled(): Boolean
    fun openAtDiag()
    fun closeAtDiag()
}

interface ICabc : BaseInterface {
    @Throws(Exception::class)
    fun set(isOn: Boolean)
}

interface IMIPILog : BaseInterface {
    val isSupportNr: Boolean
    @Throws(Exception::class)
    fun setMIPILogSerdes(cmd: String)
    fun getMIPILogSerdes(cmd: String): String
    var mIPILogChannel: String
}

interface ICpuDebug : BaseInterface {
    @Throws(Exception::class)
    fun set(isOn: Boolean)
    fun get(): Boolean
}
