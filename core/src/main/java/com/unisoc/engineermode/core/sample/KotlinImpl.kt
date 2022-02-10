package com.unisoc.engineermode.core.sample

import android.util.Log
import com.unisoc.engineermode.core.annotation.Implementation
import com.unisoc.engineermode.core.annotation.Property
import com.unisoc.engineermode.core.factory.PropertyKeys.ANDROID_VERSION
import com.unisoc.engineermode.core.factory.PropertyKeys.PRODUCT


@Implementation(
    interfaceClass = KotlinInterface::class,
    properties =  [
        Property(key= ANDROID_VERSION, value="9"),
        Property(key= PRODUCT, value="robolectric"),
        Property(key= PRODUCT, value="xxxxxx"),
        Property(key="variant", value="SampleImpl")
    ]
)
object KotlinImpl: KotlinInterface {
    const val TAG = "SampleImpl"

    override fun self(): String {
        Log.d(TAG, "this is SmapleImple for android9")
        return this::class.simpleName!!
    }
}