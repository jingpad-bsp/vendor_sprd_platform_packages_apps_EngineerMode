package com.unisoc.engineermode.core.utils;

public class HidlUtils {
    private static String HIDL_SOCKET_NAME = "hidl_common_socket";


    public static synchronized String sendCmd(String cmd) {
        return SocketUtils.sendCmd(HIDL_SOCKET_NAME, cmd);
    }
}
