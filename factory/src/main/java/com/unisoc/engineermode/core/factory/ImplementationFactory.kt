package com.unisoc.engineermode.core.factory

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import kotlin.reflect.KClass
import kotlin.reflect.full.staticFunctions
import kotlin.reflect.full.staticProperties

@SuppressLint("StaticFieldLeak")
object ImplementationFactory {
    private const val TAG = "IMPLEMENTATIONFACTORY"
    private val cachedInstance = hashMapOf<Class<out BaseInterface>, BaseInterface>()
    var context: Context? = null
    private val impls: List<ImplementationInfo>?

    init {
//        AllImplementations.init()
//        val cls = Class.forName("com.unisoc.engineermode.core.factory.AllImplementations")
//        cls.getMethod("init").invoke(null)
        val clsName = "com.unisoc.engineermode.core.AllImplementations"
        val cls = Class.forName(clsName).kotlin
        cls.staticFunctions.find {it.name == "init"}!!.call()
        @Suppress("UNCHECKED_CAST")
        impls = cls.staticProperties.find { it.name == "impls"}?.get() as List<ImplementationInfo>
    }

    fun init(context: Context) {
        this.context = context
        Config.init()
    }

    fun reset() {
        cachedInstance.clear()
    }

    @JvmStatic
    fun create(interfaceClass: KClass<out BaseInterface>): BaseInterface {
        return create(interfaceClass.java)
    }

    @JvmStatic
    @Synchronized
    fun create(interfaceClass: Class<out BaseInterface>): BaseInterface {
        if (cachedInstance.containsKey(interfaceClass)) {
            return cachedInstance[interfaceClass]!!
        }

        val target = findClassNameBy(interfaceClass, Config.data.properties)
        val cls = Class.forName(target)
        val instance = if (cls.kotlin.objectInstance != null) {
            cls.kotlin.objectInstance as BaseInterface
        } else {
            try {
                cls.getMethod("newInstance").invoke(null) as BaseInterface
            } catch (e: NoSuchMethodException) {
                cls.newInstance() as BaseInterface
            }
        }

        cachedInstance[interfaceClass] = instance
        return instance
    }

    private fun findClassNameBy(interfaceClass: Class<out BaseInterface>, properties: HashMap<String, String>): String {
        var target: ImplementationInfo? = null
        val findResults = impls!!.asSequence()
            .filter { matchInterface(it.interfaceClass, interfaceClass) }
            .onEach { Log.d(TAG, "interfaced matched: ${it.implementClassName}") }
            .filter { matchProperties(it.properties, properties) }
            .onEach { Log.d(TAG, "properties matched: ${it.implementClassName}") }
            .filter {
                // if default exists, filter it out, but we need save it for later using
                if (it.properties.isEmpty()) {
                    target = it
                    false
                } else {
                    true
                }
            }
            .toList()

        when {
            findResults.size > 1 -> {
                Log.e(TAG, "find interface $interfaceClass, get more than one result:")
                assert(false)
            }
            findResults.isEmpty() -> {
                assert(target != null)
                Log.e(TAG, "find interface $interfaceClass, get none result, use default: ${target!!.implementClassName}")
            }
            else -> target = findResults[0]
        }

        return target!!.implementClassName
    }

    private fun getProperty(prop: String): Pair<String, String> {
        val arr = prop.split(":")
        assert(arr.size == 2)
        return Pair(arr[0].trim(), arr[1].trim())
    }

    private fun getProperties(props: Array<String>): HashMap<String, String> {
        val properties = hashMapOf<String, String>()
        props.forEach {
            val (k, v) = getProperty(it)
            properties[k] = v
        }
        return properties
    }

    private fun matchInterface(classInterface: Class<out BaseInterface>, configInterface: Class<out BaseInterface>): Boolean {
//        Log.d(TAG, "compare interface: $classInterface , $configInterface")
        return classInterface == configInterface
    }

    private fun matchProperties(classProperties: HashMap<String, List<String>>, configProperties: HashMap<String, String>): Boolean {
        return classProperties.filterNot { (k, v) ->
            val value: String? = configProperties[k]
            if (value == null) {
                true
            } else {
                v.contains(value)
            }
        }.isEmpty()
    }
}
