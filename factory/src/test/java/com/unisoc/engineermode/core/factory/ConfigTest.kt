package com.unisoc.engineermode.core.factory

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.unisoc.engineermode.core.CoreApi
import org.junit.Test

import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLog
import java.io.File
import com.google.common.truth.Truth.assertThat

@RunWith(RobolectricTestRunner::class)
class ConfigTest {
    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        CoreApi.init(context)
        ShadowLog.stream = System.out;
    }


    @Test
    fun load() {
        val data = Config.init(File("src/test/assets/config.json").readText())
        assertThat(data.properties["variant"]).isEqualTo("google")
    }
}