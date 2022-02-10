package com.unisoc.engineermode.core.impl.nonpublic

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.unisoc.engineermode.core.common.appCtx

/**
 *  RadioInteractor is NOT part of AOSP,
 *  so we use reflection to invoke it's method in order to build this on Android Studio
 */
object RadioInteractorProxy {
    private const val TAG = "RADIOINTERACTORPROXY"
    private const val CLASS_NAME = "com.android.sprd.telephony.RadioInteractor"
    private lateinit var radioInteractorClass: Class<*>
    private lateinit var radioInteractorObject: Any

    init {
        init()
    }

    @SuppressLint("PrivateApi")
    fun init() {
        try {
            radioInteractorClass = Class.forName(CLASS_NAME)
        } catch (e: ClassNotFoundException) {
            Log.e(TAG, "no such class, $CLASS_NAME")
            throw e
        }

        radioInteractorObject = radioInteractorClass.getConstructor(Context::class.java)
            .newInstance(appCtx)
    }

    @JvmStatic
    fun setPreferredNetworkType(slot: Int, networkType: Int): Int {
        return radioInteractorClass.getMethod("setPreferredNetworkType", Int::class.java, Int::class.java)
            .invoke(radioInteractorObject, slot, networkType) as Int
    }

    @JvmStatic
    fun getPreferredNetworkType(slot: Int): Int {
        return radioInteractorClass.getMethod("getPreferredNetworkType", Int::class.java)
            .invoke(radioInteractorObject, slot) as Int
    }
}
