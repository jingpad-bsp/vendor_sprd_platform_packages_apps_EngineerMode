package com.unisoc.engineermode.core.factory

import android.os.Build
import android.util.Log
import com.unisoc.engineermode.core.factory.R
import com.unisoc.engineermode.core.factory.PropertyKeys.ANDROID_VERSION
import com.unisoc.engineermode.core.factory.PropertyKeys.BOARD
import com.unisoc.engineermode.core.factory.PropertyKeys.HARDWARE
import com.unisoc.engineermode.core.factory.PropertyKeys.PRODUCT
import org.json.JSONObject

object Config {
    private const val TAG = "Config"
    private const val CONFIG_FILENAME = "config.json"

    data class ConfigData(
//        var androidVersion: String = "",
//        var product: String = "",
//        var board: String = "",
//        var hardware: String = "",
        var properties: HashMap<String, String>
    ) {
        override fun toString(): String {
            return "ConfigData(properties=$properties)"
        }
    }

    lateinit var data: ConfigData

    fun init(json: String? = null): ConfigData {
        data = load(json)
        return data
    }

//   config.json example:
//    {
//        "global": {
//            "wcn": "sprdroid9_trunck",
//            "board": "sp9832e",
//            "feature": "zte"
//        }
//    }

    private fun load(json: String? = null): ConfigData {
//        val jsonObj = JSONObject(json?: getTextResource(CONFIG_FILENAME))
        val jsonObj = JSONObject(json?: getRawTextResource(R.raw.config))
        val data = ConfigData(hashMapOf())
        data.properties[ANDROID_VERSION] = Build.VERSION.RELEASE
        data.properties[PRODUCT] = Build.PRODUCT
        data.properties[BOARD] = Build.BOARD
        data.properties[HARDWARE] = Build.HARDWARE

        val keys = jsonObj.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = jsonObj.get(key)
            data.properties[key] = value.toString()
        }

        Log.d(TAG, "config: $data")
        return data
    }

    private fun getJSONResource(jsonFileName: String): JSONObject {
        ImplementationFactory.context!!.assets.open(jsonFileName).bufferedReader().use {
            val json: String = it.readText()
            return JSONObject(json)
        }
    }

    private fun getTextResource(fileName: String): String {
        return ImplementationFactory.context!!.assets.open(fileName).bufferedReader().use {
             it.readText()
        }
    }

    private fun getRawTextResource(resId: Int): String {
        return ImplementationFactory.context!!.resources.openRawResource(resId).bufferedReader().use {
            it.readText()
        }
    }
}