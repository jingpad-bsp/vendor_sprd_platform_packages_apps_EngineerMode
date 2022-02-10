package com.unisoc.engineermode.core.utils;

import android.net.LocalSocket;
import android.net.LocalSocketAddress;

import java.io.OutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.Arrays;

import android.net.LocalSocketAddress.Namespace;
import android.util.Log;
import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;

import kotlin.BuilderInference;

public class SocketUtils {

    public static final String TAG = "SocketUtils";
    private static final int AT_BUFFER_SIZE = 2048;
    private static final String SOCKET_NAME = "hidl_common_socket";

    private static void timeout(Object obj) {
        Log.w(TAG, "socket timeout");
        LocalSocket socket = (LocalSocket) obj;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String sendCmd(String socketName, String cmd) {

        Thread thread = Thread.currentThread();
        String caller = Thread.currentThread().getStackTrace()[2].getClassName();
        String strcmd =
                String.format("[%s][%d]<%s>", caller, thread.getId(), cmd.replace('\n', '\\'));
        Log.d(TAG, socketName + " send cmd: " + strcmd);

        LocalSocket socketClient = new LocalSocket();
        LocalSocketAddress mSocketAddress =
                new LocalSocketAddress(socketName, Namespace.ABSTRACT);

        String result = null;
        try {
            socketClient.connect(mSocketAddress);
        } catch (IOException e) {
            Log.e(TAG, "connect to " + socketName + " failed", e);
            try {
                socketClient.close();
            } catch (IOException ignored) {
            }
            return result;
        }

        OutputStream ops = null;
        InputStream ins = null;
        byte[] buf = new byte[AT_BUFFER_SIZE];
        Watchdog wd = null;
        try {
            Log.i(TAG, strcmd + "connect " + socketName + " success");
            ops = socketClient.getOutputStream();
            ins = socketClient.getInputStream();

            ops.write(cmd.getBytes(StandardCharsets.UTF_8));
            ops.flush();
            Log.d(TAG, strcmd + " write cmd and flush done");

            wd = new Watchdog(socketName, cmd);
            wd.setTimeoutCallback(SocketUtils::timeout, socketClient);
            wd.wantEat();
            int count = ins.read(buf, 0, AT_BUFFER_SIZE);
            wd.feedFood();
            Log.d(TAG, strcmd + " result read done");
            result = "";
            if (count != -1) {
                byte[] temp = new byte[count];
                System.arraycopy(buf, 0, temp, 0, count);
                result = new String(temp, StandardCharsets.UTF_8);
            } else {
                Log.e(TAG, strcmd + " read failed");
            }

            Log.d(TAG, strcmd + "count = " + count + ", result is: " + result);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (wd != null) {
                wd.feedFood();
            }

            try {
                if (ops != null) {
                    ops.close();
                }
                if (ins != null) {
                    ins.close();
                }
                socketClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, strcmd + "handle over and result is: " + result);
        return result;
    }

    public static boolean sendCmd(String socketName, byte[] cmd, byte[] result ) {
        Log.d(TAG, socketName + " send byte cmd");

        LocalSocket socketClient = new LocalSocket();
        LocalSocketAddress mSocketAddress =
            new LocalSocketAddress(socketName, Namespace.ABSTRACT);

        try {
            socketClient.connect(mSocketAddress);
        } catch (IOException e) {
            Log.e(TAG, "connect to " + socketName + " failed", e);
            try {
                socketClient.close();
            } catch (IOException ignored) {
            }
            return false;
        }

        Watchdog wd = null;
        try (OutputStream ops = socketClient.getOutputStream();
            InputStream ins = socketClient.getInputStream()){
            Log.i(TAG, "connect " + socketName + " success");

            ops.write(cmd);
            ops.flush();
            Log.d(TAG, "write cmd and flush done");

            wd = new Watchdog(socketName, "byte command");
            wd.setTimeoutCallback(SocketUtils::timeout, socketClient);
            wd.wantEat();
            int count = ins.read(result);
            wd.feedFood();

            Log.d(TAG, "result read done, count=" + count);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (wd != null) {
                wd.feedFood();
            }
            try {
                socketClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG,  "cmd over and result len: " + result.length);
        return true;
    }

    public static final String OK = "OK";
//    public static final String FAIL = "FAIL";
//    private static final String SOCKET_NAME = "hidl_common_socket";

    /**
     *
     *  Deprecated, use sendCmd instead
     */
    @Deprecated
    public static synchronized String sendCmdAndRecResult(String socketName, Namespace namespace,
            String strcmd) {
        Log.d(TAG, socketName + " send cmd: " + strcmd);
        byte[] buf = new byte[255];
        String result = null;
        Log.d(TAG, "set cmd: " + strcmd);

        LocalSocket socketClient = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        LocalSocketAddress socketAddress = null;
        try {
            socketClient = new LocalSocket();
            socketAddress = new LocalSocketAddress(socketName, namespace);
            if (!socketClient.isConnected()) {
                Log.d(TAG, "isConnected...");
                socketClient.connect(socketAddress);
            }
            // socketClient.connect(socketAddress);
            Log.d(TAG, "socketClient connect is " + socketClient.isConnected());
            outputStream = socketClient.getOutputStream();
            if (outputStream != null) {
                final StringBuilder cmdBuilder = new StringBuilder(strcmd).append('\0');
                final String cmd = cmdBuilder.toString();
                outputStream.write(cmd.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            }
            inputStream = socketClient.getInputStream();
            int count = inputStream.read(buf, 0, 255);
            result = "";
            result = new String(buf, "utf-8");
            Log.d(TAG, "count = " + count + ", result is: " + result);
        } catch (IOException e) {
            Log.e(TAG, "Failed get output stream: " + e.toString());
            return null;
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (socketClient != null) {
                    socketClient.close();
                }
            } catch (IOException e) {
                Log.d(TAG, "catch exception is " + e);
                return null;
            }
        }
        return result;
    }

    public static synchronized String sendCmdNoCloseSocket(String socketName, Namespace namespace, String strcmd) {
        byte[] buf = new byte[255];
        String result = null;
        LocalSocket noCloseSocketClient = null;
        OutputStream noCloseOutputStream = null;
        InputStream noCloseInputStream = null;
        LocalSocketAddress socketAddress = null;
        try {
            if (noCloseSocketClient == null) {
                Log.d(TAG, "noCloseSocketClient is null");
                noCloseSocketClient = new LocalSocket();

                socketAddress = new LocalSocketAddress(socketName, namespace);
                noCloseSocketClient.connect(socketAddress);
            }
            Log.d(TAG, "noCloseSocketClient connect is " + noCloseSocketClient.isConnected());
            noCloseOutputStream = noCloseSocketClient.getOutputStream();
            if (noCloseOutputStream != null) {
                final StringBuilder cmdBuilder = new StringBuilder(strcmd).append('\0');
                final String cmd = cmdBuilder.toString();
                noCloseOutputStream.write(cmd.getBytes(StandardCharsets.UTF_8));
                noCloseOutputStream.flush();
            }
            noCloseInputStream = noCloseSocketClient.getInputStream();
            int count = noCloseInputStream.read(buf, 0, 255);
            result = new String(buf, "utf-8");
            Log.d(TAG, "count = " + count + ", result is: " + result);
        } catch (IOException e) {
            Log.e(TAG, "Failed get output stream: " + e);
        }
        return result;
    }

    public static String SendSlogModemAt(String cmd) {
        Log.d(TAG, "SendSlogModemAt " + cmd);
        String strTmp = sendCmdAndRecResult(SOCKET_NAME, LocalSocketAddress.Namespace.ABSTRACT, "slogmodem " + cmd);
        return strTmp;
    }
}
