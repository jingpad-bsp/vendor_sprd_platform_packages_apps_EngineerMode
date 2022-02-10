package com.unisoc.engineermode.core.impl.nonpublic

import android.app.ActivityManager
import android.content.Context
import com.unisoc.engineermode.core.common.appCtx

fun ActivityManager_forceStopPackage(packageName: String) {
    val am = appCtx.getSystemService(Context.ACTIVITY_SERVICE);
    ActivityManager::class.java.getMethod("forceStopPackage", String::class.java).invoke(am, packageName)
}
