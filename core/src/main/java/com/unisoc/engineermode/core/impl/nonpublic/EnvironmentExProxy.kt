package com.unisoc.engineermode.core.impl.nonpublic

import android.annotation.SuppressLint
import java.io.File

object EnvironmentExProxy {

    private const val ENVEX_CLASS_NAME = "android.os.EnvironmentEx"

    private lateinit var environmentExClass: Class<*>

    init {
        init()
    }

    @SuppressLint("PrivateApi")
    fun init() {
        environmentExClass = Class.forName(ENVEX_CLASS_NAME)
    }

    @JvmStatic
    fun getInternalStoragePath(): File {
        return environmentExClass.getMethod("getInternalStoragePath").invoke(null) as File
    }
}