package com.unisoc.engineermode.core.impl.nonpublic

import android.annotation.SuppressLint
import com.unisoc.engineermode.core.common.appCtx
import android.util.Log
import android.content.Context;

object DmykAbsTelephonyManagerProxy {

    private const val TAG = "DmykAbsTelephonyManagerProxy"
    private const val DMYKABSTELMANAGER_CLASS_NAME = "com.dmyk.android.telephony.DmykAbsTelephonyManager"

    private lateinit var dmykAbsTelephonyManagerClass: Class<*>
    private lateinit var dmykAbsTelephonyManagerObject: Any

    init {
        init()
    }

    fun init() {
        try {
            dmykAbsTelephonyManagerClass = Class.forName(DMYKABSTELMANAGER_CLASS_NAME)
        } catch (e: ClassNotFoundException) {
            Log.e(TAG, "no such class, $dmykAbsTelephonyManagerClass")
            throw e
        }

        dmykAbsTelephonyManagerObject = dmykAbsTelephonyManagerClass.getMethod("getDefault", Context::class.java).invoke(null, appCtx)!!
    }

    fun getCdmaImsi(subId: Int): String? {
        return dmykAbsTelephonyManagerClass.getMethod("getCdmaImsi", Int::class.java)
            .invoke(dmykAbsTelephonyManagerObject, subId) as String?
    }

    fun getIccId(subId: Int): String? {
        return dmykAbsTelephonyManagerClass.getMethod("getIccId", Int::class.java)
            .invoke(dmykAbsTelephonyManagerObject, subId) as String?
    }
}