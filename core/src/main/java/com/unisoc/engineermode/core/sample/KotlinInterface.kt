package com.unisoc.engineermode.core.sample

import com.unisoc.engineermode.core.factory.BaseInterface


interface KotlinInterface : BaseInterface {
    fun self(): String {
       return this::class.simpleName!!
    }
}

