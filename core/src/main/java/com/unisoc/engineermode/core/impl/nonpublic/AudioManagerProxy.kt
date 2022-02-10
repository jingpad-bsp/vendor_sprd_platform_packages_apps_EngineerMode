package com.unisoc.engineermode.core.impl.nonpublic

import android.content.Context
import android.media.AudioManager
import com.unisoc.engineermode.core.common.appCtx

object AudioManagerProxy {
    @JvmField
    val EXTRA_VOLUME_STREAM_TYPE: String = getField("EXTRA_VOLUME_STREAM_TYPE") as String

    @JvmField
    val EXTRA_VOLUME_STREAM_VALUE: String = getField("EXTRA_VOLUME_STREAM_VALUE") as String

    @JvmField
    val DEVICE_OUT_FM_HEADSET: Int = getField("DEVICE_OUT_FM_HEADSET") as Int

    @JvmField
    val VOLUME_CHANGED_ACTION: String = getField("VOLUME_CHANGED_ACTION") as String

    private var audioManager: AudioManager =
        appCtx.getSystemService(Context.AUDIO_SERVICE) as AudioManager


    private fun getField(fieldName: String): Any {
        return AudioManager::class.java.getField(fieldName).get(null)!!
    }

    @JvmStatic
    fun setParameters(keyValuePairs: String) {
        AudioManager::class.java.getMethod("setParameters", String::class.java).invoke(audioManager, keyValuePairs)
    }

    @JvmStatic
    fun setDeviceConnectionStateForFM(device: Int, state: Int, device_address: String, device_name: String) {
        AudioManager::class.java.getMethod("setDeviceConnectionStateForFM",
            Int::class.java, Int::class.java, String::class.java, String::class.java)
            .invoke(audioManager, device, state, device_address, device_name)
    }
}