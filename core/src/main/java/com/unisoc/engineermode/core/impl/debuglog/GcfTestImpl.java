package com.unisoc.engineermode.core.impl.debuglog;

import android.provider.Settings;

import com.unisoc.engineermode.core.annotation.Implementation;
import com.unisoc.engineermode.core.intf.IGcfTest;

import static com.unisoc.engineermode.core.common.CommonKt.appCtx;

@Implementation(
    interfaceClass = IGcfTest.class,
    properties =  {
    })
public class GcfTestImpl implements IGcfTest {

    private static final String SEND_RETRIE_TIME = "message_send_retries";
    private static final String MAX_SEND_RETRIES = "3";
    private static final String MIN_SEND_RETRIES = "0";

    @Override
    public boolean isGcfTestOpened() {
        final String retryKey = Settings.Global.getString(appCtx.getContentResolver(),
                                    SEND_RETRIE_TIME);
        return (retryKey != null && retryKey.equals(MIN_SEND_RETRIES));
    }

    @Override
    public void openGcfTest() {
        Settings.Global.putString(appCtx.getContentResolver(),
                                    SEND_RETRIE_TIME, MIN_SEND_RETRIES);
    }

    @Override
    public void closeGcfTest()  {
        Settings.Global.putString(appCtx.getContentResolver(),
                                    SEND_RETRIE_TIME, MAX_SEND_RETRIES);
    }
}
