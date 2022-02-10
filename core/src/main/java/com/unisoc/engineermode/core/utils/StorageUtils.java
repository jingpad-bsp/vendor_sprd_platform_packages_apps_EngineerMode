package com.unisoc.engineermode.core.utils;

import java.io.File;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;


public class StorageUtils {
    private static final String DEFAULT_INTERNAL_PATH = "/storage/emulated/0";
    private static final String DEFAULT_EXTERNAL_PATH = "/storage/sdcard0/";
    private static final String SDCARD_PATH_PROP = "vold.sdcard0.path";
    private static final String SDCARD_MOUNTED_PROP = "vold.sdcard0.state";

    private static final String MISC_SOCKET_NAME = "miscserver";
    private static final String GET_SDCARD_STATE = "getSdState";
    private static final String GET_SDCARD_PATH = "getSdPath";

    private static final String TAG = "StorageUitls";

    public static String getExternalStorage() {
        String path = SystemPropertiesProxy.get(SDCARD_PATH_PROP);
        if (!TextUtils.isEmpty(path)) {
            String stateString = SystemPropertiesProxy.get(SDCARD_MOUNTED_PROP);
            if (!TextUtils.isEmpty(stateString) && stateString.trim().equals("mounted")) {
                return path;
            }else if(!TextUtils.isEmpty(stateString) && stateString.trim().equals("unmounted")){
           // return false;
            }
            //return path;
        }
        path = getSdPath();
        if (path == null) {
            path =  DEFAULT_EXTERNAL_PATH;
        }

        Log.d(TAG, "external storage path: " + path);
        return path.trim();
    }

    public static String getInternalStorage() {
        return DEFAULT_INTERNAL_PATH;
    }

    public static boolean getExternalStorageState() {
        String stateString = SystemPropertiesProxy.get(SDCARD_MOUNTED_PROP);
        if (!TextUtils.isEmpty(stateString) && stateString.trim().equals("mounted")) {
            return true;
        }else if(!TextUtils.isEmpty(stateString) && stateString.trim().equals("unmounted")){
           // return false;
        }
        String state = getSdState();
        Log.d(TAG, "external storage state: " + state);
        if (state == null) {
            Log.e(TAG, "get external storage state failed");
            return false;
        }

        if (state.contains("0")) {
            return false;
        } else if (state.contains("1")) {
            return true;
        } else {
            Log.e(TAG, "state is unknown");
            return false;
        }
    }

    public static long getFreespace(File storageLocation) {
        if (storageLocation == null) {
            Log.e(TAG, "storageLocation is null, return 0");
            return 0;
        }
        return storageLocation.getUsableSpace();
    }

    public static long getTotalSpace(File storageLocation) {
        if (storageLocation == null) {
            Log.e(TAG, "storageLocation is null, return 0");
            return 0;
        }

        return storageLocation.getTotalSpace();

    }

    public static long getStorageTotalSize(boolean isExternal) {
        try {
            File file = new File(isExternal ? DEFAULT_EXTERNAL_PATH : DEFAULT_INTERNAL_PATH);
            if (!file.exists()) {
                file = Environment.getDataDirectory();
            }
            return file.getTotalSpace();
        } catch (Exception e) {
            Log.d(TAG, "getStorageTotalSize exception", e);
            return 0;
        }
    }

    public static long getStorageFreeSize(boolean isExternal) {
        try {
            File file = new File(isExternal ? DEFAULT_EXTERNAL_PATH : DEFAULT_INTERNAL_PATH);
            if (!file.exists()) {
                file = Environment.getDataDirectory();
            }
            return file.getFreeSpace();
        } catch (Exception e) {
            Log.d(TAG, "getStorageTotalSize exception", e);
            return 0;
        }
    }

    public static long getFreeSpace(String path) {
        Log.i(TAG, "getFreeSpace path is " + path);
        final File file = new File(path);
        if (file.exists()) {
            return file.getFreeSpace();
        }
        Log.w(TAG, "file is not exist :" + path);
        return 0;
    }

    public static long getTotalSpace(String path) {
        Log.i(TAG, "getTotalSpace path is " + path);
        final File file = new File(path);
        if (file.exists()) {
            return file.getTotalSpace();
        }
        Log.i(TAG, "path not exist: " + path);
        return 0;
    }

    private static String getSdState() {
//        SocketUtils socketUtils = new SocketUtils(MISC_SOCKET_NAME);
//        String response = socketUtils.sendCmdAndRecResult(GET_SDCARD_STATE);
//        Log.i(TAG, "use miscserver to getSdState result : " + response);
//        socketUtils.closeSocket();
//        return response;
        return sendCmd(GET_SDCARD_STATE);
    }

    private static String getSdPath() {
//        SocketUtils socketUtils = new SocketUtils(MISC_SOCKET_NAME);
//        String response = socketUtils.sendCmdAndRecResult(GET_SDCARD_PATH);
//        Log.i(TAG, "use miscserver to getSdPath result : " + response);
//        socketUtils.closeSocket();
//        return response;
        return sendCmd(GET_SDCARD_PATH);
    }

    private static String sendCmd(String cmd) {
        return HidlUtils.sendCmd(MISC_SOCKET_NAME + " " + cmd );
    }
}
