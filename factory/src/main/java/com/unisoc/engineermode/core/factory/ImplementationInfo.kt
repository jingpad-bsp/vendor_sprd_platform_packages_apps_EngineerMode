package com.unisoc.engineermode.core.factory

import com.unisoc.engineermode.core.factory.BaseInterface

class ImplementationInfo(val interfaceClass: Class<out BaseInterface>,
                         val implementClassName: String) {
    val properties = hashMapOf<String, List<String>>()
    fun addProperty(key: String, values: Array<String>) {
        properties.put(key, values.toList())
    }
}
