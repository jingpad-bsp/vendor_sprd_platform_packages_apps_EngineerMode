package com.unisoc.engineermode.core.impl.debuglog

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.CellInfoLte
import android.telephony.ServiceState
import android.telephony.TelephonyManager
import android.telephony.cdma.CdmaCellLocation
import android.telephony.gsm.GsmCellLocation
import android.text.TextUtils
import com.unisoc.engineermode.core.annotation.Implementation
import com.unisoc.engineermode.core.intf.IPhoneInfo
import android.util.Log
import com.unisoc.engineermode.core.annotation.Property
import com.unisoc.engineermode.core.common.appCtx
import com.unisoc.engineermode.core.factory.PropertyKeys
import com.unisoc.engineermode.core.impl.nonpublic.*
import com.unisoc.engineermode.core.intf.INVALID_CELL_ID
import com.unisoc.engineermode.core.intf.IPhoneInfo.CdmaInfo
import com.unisoc.engineermode.core.intf.IPhoneInfo.Operator

@Implementation(
    interfaceClass = IPhoneInfo::class,
    properties = [
        Property(key = PropertyKeys.ANDROID_VERSION, value = "10")
    ]
)
object PhoneInfoImpl : IPhoneInfo {
    private const val TAG = "PHONEINFO"
    private const val SPRD_RAM_SIZE = "ro.boot.ddrsize"
    private const val SOFTWARE_VERSION_PROPERTY_NAME = "ro.version.software"
    private const val HARDWARE_VERSION_PROPERTY_NAME = "ro.boot.hardware.revision"
    private const val OPERATION_SYSTEM_VERSION_PROPERTY_NAME = "ro.build.version.release"

    private val telephoneMgr by lazy {
        appCtx.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }

    private val phoneCount by lazy {
        telephoneMgr.phoneCount
    }

    override fun getMemorySize(): String {
        var size: String = SystemPropertiesProxy.get(SPRD_RAM_SIZE, "")
        if ("" == size) return "unknown"

        Log.d(TAG, "ram property value is:$size")
        size = size.replace("[^0-9]".toRegex(), "")
        return humanReadableByteCount(size.toLong() * 1024 * 1024, false)
    }

    override fun getAllOpers(): List<Operator> {
        Log.d(TAG, "get mcc and mnc")
        val operList = List(phoneCount){ Operator("0", "0")}

        operList.forEachIndexed { i, oper ->
            if (!cardExists(i)) {
                Log.w(TAG, "not insert sim card i=:$i")
                return@forEachIndexed
            }

            val operatorNumberic = TelephonyManagerProxy.getSimOperatorNumericForPhone(i)
            if (TextUtils.isEmpty(operatorNumberic)) {
                Log.e(TAG, "get mcc and mnc failed")
                return@forEachIndexed
            }

            oper. mcc = operatorNumberic.substring(0, 3)
            oper.mnc = operatorNumberic.substring(3)
        }

        Log.d(TAG, operList.joinToString())
        return operList
    }

    override fun getAllImsi(): List<String> {
        Log.d(TAG, "get all IMSI")
        val imsiList = MutableList(phoneCount){ "" }

        for (i in 0 until phoneCount) {
            if (!cardExists(i)) {
                Log.d(TAG, "not insert sim card i=$i")
                continue
            }

            val subId = SubscriptionManagerProxy.getSubId(i)
            if (subId == null) {
                Log.e(TAG, "get sub id error, subId==null")
                continue
            }

            imsiList[i] = TelephonyManagerProxy.getSubscriberId(subId[0])
        }

        Log.d(TAG, "get all IMSI ${imsiList.joinToString()}")
        return imsiList
    }

    @SuppressLint("MissingPermission")
    override fun getCellId(): List<Int> {
        Log.d(TAG, "get cell id")
        if (phoneCount == 0) {
            Log.d(TAG, "phone Count=: 0, return!")
            return emptyList()
        }

        val cellIdList = MutableList(phoneCount) { INVALID_CELL_ID }

        for (i in 0 until phoneCount) {
            if (!cardExists(i)) {
                Log.d(TAG, "slot $i empty")
                continue
            }

            val subId = SubscriptionManagerProxy.getSubId(i)
            if (subId == null) {
                Log.e(TAG, "get sub id error, subId==null")
                continue
            }

            val phoneType = TelephonyManagerProxy.getCurrentPhoneType(subId[0])
            val serviceState = TelephonyManagerProxy.getServiceStateForSubscriber(subId[0])
            val csInService = ServiceStateProxy.getVoiceRegState(serviceState) == ServiceState.STATE_IN_SERVICE
            val psIn4g = ServiceStateProxy.getDataNetworkType(serviceState) == TelephonyManager.NETWORK_TYPE_LTE
            Log.d(TAG, "phoneType=$phoneType,i=$i")
            Log.d(TAG, "csInService=$csInService,psIn4g=$psIn4g")

            if (psIn4g && ((phoneType == TelephonyManager.PHONE_TYPE_GSM && !csInService)
                    || (phoneType == TelephonyManager.PHONE_TYPE_CDMA))) {
                val allCellList = telephoneMgr.allCellInfo

                if (allCellList == null || allCellList.size == 0) {
                    Log.e(TAG, "getAllCellInfo return zero")
                    continue
                }

                Log.d(TAG, "cell list.size=:" + allCellList.size)
                allCellList.filter { c -> c.isRegistered }
                if (allCellList.size <= i) {
                    Log.e(TAG, "registered cell not found, $i")
                    continue
                }
                val regCell = allCellList[i]
                if (regCell is CellInfoLte) {
                    cellIdList[i] = regCell.cellIdentity.ci
                } else {
                    Log.e(TAG, "registered cell not lte, $i")
                    continue
                }
            } else {
                cellIdList[i] = when (val cellLocation = TelephonyManagerExProxy.getCellLocationForPhone(i)) {
                    is GsmCellLocation -> cellLocation.cid
                    is CdmaCellLocation -> cellLocation.baseStationId
                    else -> INVALID_CELL_ID
                }
            }
        }
        Log.d(TAG, "cell id = ${cellIdList.joinToString()}")
        return cellIdList
    }

    override fun getCdmaInfo(): List<CdmaInfo> {
        Log.d(TAG, "get CDMA Info")
        val cdmaList = List(phoneCount) { CdmaInfo("", INVALID_CELL_ID, INVALID_CELL_ID, INVALID_CELL_ID)}

        cdmaList.forEachIndexed { i, cdmaInfo ->
            val phoneType = TelephonyManagerProxy.getCurrentPhoneTypeForSlot(i)
            if (phoneType != TelephonyManager.PHONE_TYPE_CDMA) {
                Log.w(TAG, "not cdma, phoneType=$phoneType,i=$i")
                return@forEachIndexed
            }
            Log.d(TAG, "PHONE_TYPE_CDMA...")

            val subId = SubscriptionManagerProxy.getSubId(i)
            if (subId == null) {
                Log.d(TAG, "error, subId==null...")
                return@forEachIndexed
            }

            cdmaInfo.prl = TelephonyManagerProxy.getCdmaPrlVersion(subId[0])

            val location = TelephonyManagerExProxy.getCellLocationForPhone(i) as CdmaCellLocation
            cdmaInfo.systemId = location.systemId
            cdmaInfo.networkId = location.networkId
            cdmaInfo.baseStationId = location.baseStationId
        }
        Log.d(TAG, "CDMA info: ${cdmaList.joinToString()}")
        return cdmaList
    }

    override fun getEsn(): String {
        return TelephonyManagerProxy.getEsn().orEmpty()
    }

    @SuppressLint("MissingPermission")
    override fun getMeid(): String {
        return telephoneMgr.meid.orEmpty()
    }

    @SuppressLint("MissingPermission")
    override fun getAllImei(): List<String> {
        Log.d(TAG, "get all IMEI")
        val imeiList = MutableList(phoneCount){ "" }

        for (i in 0 until phoneCount) {
            imeiList[i] = telephoneMgr.getImei(i)
        }

        Log.d(TAG, "get all IMEI ${imeiList.joinToString()}")
        return imeiList
    }

    override fun getInternalStorageSize(): String {
        return humanReadableByteCount(StorageManagerProxy.getPrimaryStorageSize(), true)
    }

    override fun getSoftwareVersion(): String {
        return SystemPropertiesProxy.get(SOFTWARE_VERSION_PROPERTY_NAME, "")
    }

    override fun getHardwareVersion(): String {
        return SystemPropertiesProxy.get(HARDWARE_VERSION_PROPERTY_NAME, "")
    }

    override fun getOsVersion(): String {
        return SystemPropertiesProxy.get(OPERATION_SYSTEM_VERSION_PROPERTY_NAME, "")
    }

    private fun humanReadableByteCount(bytes: Long, si: Boolean): String {
        val unit = if (si) 1000 else 1024
        if (bytes < unit) return "$bytes B"
        val exp = (Math.log(bytes.toDouble()) / Math.log(unit.toDouble())).toInt()
        val pre = (if (si) "kMGTPE" else "KMGTPE")[exp - 1] + if (si) "" else "i"
        return String.format("%.1f %sB", bytes / Math.pow(unit.toDouble(), exp.toDouble()), pre)
    }

    override fun getCdmaImsi(): List<String> {
        Log.d(TAG, "get all CDMAIMSI")
        val cdmaImsiList = MutableList(phoneCount){ "" }

        for (i in 0 until phoneCount) {
            val cdmaImsi = DmykAbsTelephonyManagerProxy.getCdmaImsi(i)
            if (cdmaImsi == null) {
                cdmaImsiList[i] = ""
            } else {
                cdmaImsiList[i] = cdmaImsi
            }
        }

        Log.d(TAG, "get all CDMA IMSI ${cdmaImsiList.joinToString()}")
        return cdmaImsiList
    }

    override fun getIccId(): List<String> {
        Log.d(TAG, "get ICCID")
        val iccIdList = MutableList(phoneCount){ "" }

        for (i in 0 until phoneCount) {
            val iccId = DmykAbsTelephonyManagerProxy.getIccId(i)
            if (iccId == null) {
                iccIdList[i] = ""
            } else {
                iccIdList[i] = iccId
            }
        }

        Log.d(TAG, "get all ICCID ${iccIdList.joinToString()}")
        return iccIdList
    }

    private fun cardExists(slot: Int): Boolean {
        return telephoneMgr.getSimState(slot) == TelephonyManager.SIM_STATE_READY
    }
}
