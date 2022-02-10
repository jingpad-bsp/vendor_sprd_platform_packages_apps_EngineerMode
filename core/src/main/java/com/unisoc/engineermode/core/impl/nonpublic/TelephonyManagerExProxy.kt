package com.unisoc.engineermode.core.impl.nonpublic

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.CellLocation
import android.util.Log
import com.unisoc.engineermode.core.common.appCtx

object TelephonyManagerExProxy {
    private const val TAG = "TELEPHONYMANAGEREXPROXY"
    private const val CLASS_NAME = "android.telephony.TelephonyManagerEx"
    private lateinit var tmExClass: Class<*>
    private lateinit var tmExObject: Any

    init {
        init()
    }

    @SuppressLint("PrivateApi")
    fun init() {
        try {
            tmExClass = Class.forName(CLASS_NAME)
        } catch (e: ClassNotFoundException) {
            Log.e(TAG, "no such class, $CLASS_NAME")
            throw e
        }

        tmExObject = tmExClass.getMethod("from", Context::class.java).invoke(null, appCtx)!!
    }

    @JvmStatic
    fun getCellLocationForPhone(phoneId: Int): CellLocation? {
        return tmExClass.getMethod("getCellLocationForPhone", Int::class.java)
            .invoke(tmExObject, phoneId) as CellLocation?
    }
}