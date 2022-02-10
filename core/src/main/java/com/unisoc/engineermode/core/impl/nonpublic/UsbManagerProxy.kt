package com.unisoc.engineermode.core.impl.nonpublic

import android.content.Context
import android.hardware.usb.UsbManager;
import com.unisoc.engineermode.core.common.appCtx

object UsbManagerProxy {

    private var usbManagerObject: UsbManager =
        appCtx.getSystemService(Context.USB_SERVICE) as UsbManager


    @JvmField
    val FUNCTION_NONE  =
        UsbManager::class.java.getField("FUNCTION_NONE").get(null) as Long

    @JvmStatic
    fun setCurrentFunctions(value: Long) {
        UsbManager::class.java.getMethod("setCurrentFunctions", Long::class.java)
                .invoke(usbManagerObject, value)
    }
}