package com.unisoc.engineermode.core.utils

import android.content.Context
import android.hardware.usb.UsbManager
import android.os.Process
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.M
import android.os.UserManager
import com.unisoc.engineermode.core.common.appCtx

object CommonUtils {
    @JvmStatic
    val UserManager.isCurrentUserDeviceOwner: Boolean
        get() = if (SDK_INT >= M) isSystemUser
        else if (SDK_INT >= 17) getSerialNumberForUser(Process.myUserHandle()) == 0L
        else true

    @JvmField
    val USBMANAGER_ACTION_USB_STATE = UsbManager::class.java.getField("ACTION_USB_STATE").get(null) as String

    @JvmStatic
    fun isCurrentUserDeviceOwner(): Boolean {
        val userManager =  appCtx.getSystemService(Context.USER_SERVICE) as UserManager
        return userManager.getSerialNumberForUser(Process.myUserHandle()) == 0L
    }
}

