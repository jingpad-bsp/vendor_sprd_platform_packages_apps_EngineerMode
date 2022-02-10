package com.unisoc.engineermode.core.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.unisoc.engineermode.core.common.CommonKt.appCtx;

public class IATUtils {
    private static final String TAG = "IATUtils";
    public static final String AT_FAIL = "AT FAILED";
    public static final String AT_OK = "OK";
    public static final String AT_CONNECT = "CONNECT";
    private static final String AT_SOCKET_NAME = "miscserver";

    public static String sendAt(final String cmd, final String serverName) {
        if(!isModemATAvaliable()) {
            Log.d(TAG, "modem at not Aviable, return");
            return IATUtils.AT_FAIL;
        }

        //here we fix AT blocking time, we return AT fail after 2 seconds later.
        final ExecutorService exec = Executors.newFixedThreadPool(1);
        String futureObj;
        try {
            Future<String> future = exec.submit(() -> sendATCmd(cmd, serverName));
            futureObj = future.get(2000, TimeUnit.MILLISECONDS);
        } catch (TimeoutException ex) {
            Log.d(TAG, "modem at timeout", ex);
            return IATUtils.AT_FAIL;
        } catch (Exception e) {
            Log.d(TAG, "modem at exception", e);
            return IATUtils.AT_FAIL;
        }
        exec.shutdown();
        return futureObj != null ? futureObj : IATUtils.AT_FAIL;
    }

    private static int getPhoneIdByChannelName(String channelName) {
        if (channelName.contains("atchannel0")) {
            return 0;
        } else if (channelName.contains("atchannel1")) {
            return 1;
        } else {
            return 0;
        }
    }

    @SuppressLint("DefaultLocale")
    private static String getAtCmd(String cmd, int phoneId) {
        return String.format("%s sendAt %d %s", AT_SOCKET_NAME, phoneId, cmd);
    }

    public static synchronized String sendATCmd(String cmd, int phoneId) {
        Log.d(TAG, "send AT: " + cmd);
        return HidlUtils.sendCmd(getAtCmd(cmd, phoneId));
    }

    public static synchronized String sendATCmd(String cmd, String channelName) {
        return sendATCmd(cmd, getPhoneIdByChannelName(channelName));
    }


    private static boolean isModemATAvaliable() {
        String modemAssert = SystemPropertiesProxy.get("vendor.ril.modem.assert", "0");
        if(TextUtils.isEmpty(modemAssert) || modemAssert.equals("0")) {
            Log.d(TAG, "check modem at ok");
            return true;
        }

        SharedPreferences sp = appCtx.getSharedPreferences("com.sprd.engineermode_preferences", Context.MODE_PRIVATE);
        if(modemAssert.contains("1") && sp.getBoolean("key_manualassert", true)) {
            Log.d(TAG, "checked modem at ok");
            return true;
        }
        Log.d(TAG, "check modem at fail");
        return false;
    }
}
