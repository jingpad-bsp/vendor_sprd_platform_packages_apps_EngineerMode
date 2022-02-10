package com.unisoc.engineermode.core.sample

import android.util.Log
import com.unisoc.engineermode.core.factory.ImplementationFactory

object KotlinTest {
    const val TAG = "KOTLINTEST";

    fun kotlinApi(): KotlinInterface {
        return ImplementationFactory.create(KotlinInterface::class) as KotlinInterface
    }

    fun test() {
        Log.d(TAG, kotlinApi().self());
    }
}
