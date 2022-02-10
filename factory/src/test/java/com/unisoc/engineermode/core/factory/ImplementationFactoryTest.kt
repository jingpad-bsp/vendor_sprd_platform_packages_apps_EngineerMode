package com.unisoc.engineermode.core.factory

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.unisoc.engineermode.core.CoreApi
import com.unisoc.engineermode.core.sample.Sample
import org.junit.Test

import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLog
import com.google.common.truth.Truth.assertThat

@RunWith(RobolectricTestRunner::class)
class ImplementationFactoryTest {
    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        CoreApi.init(context)
        ShadowLog.stream = System.out
        ImplementationFactory.reset()
    }

    @Test
    fun createKotlin() {
        val json = "{\"variant\": \"SampleImpl\"}"
        Config.init(json)
        val sample = ImplementationFactory.create(Sample::class) as Sample
        assertThat(sample.self()).isEqualTo("SampleImpl")
    }

    @Test
    fun createJava() {
        val json = "{\"variant\": \"SampleImpl2\"}"
        Config.init(json)
        val sample = ImplementationFactory.create(Sample::class) as Sample
        assertThat(sample.self()).isEqualTo("SampleImpl2")
    }

    @Test
    fun createDefault() {
        val json = "{\"variant\": \"default\"}"
        Config.init(json)
        val sample = ImplementationFactory.create(Sample::class) as Sample
        assertThat(sample.self()).isEqualTo("SampleImplDef")
    }
}