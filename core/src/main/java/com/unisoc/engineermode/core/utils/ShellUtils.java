package com.unisoc.engineermode.core.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import android.util.Log;


public class ShellUtils {
    private static final String TAG = "ShellUtils";

    public static String execShellCmd(String cmd) {
        StringBuilder ret = new StringBuilder();
        Process process;
        try {
            process = Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

        try (
            BufferedReader stdout = new BufferedReader(new InputStreamReader(
                    process.getInputStream(), StandardCharsets.UTF_8));
            BufferedReader stderr = new BufferedReader(new InputStreamReader(
                    process.getErrorStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((null != (line = stdout.readLine()))
                    || (null != (line = stderr.readLine()))) {
                if (!"".equals(line)) {
                    ret = ret.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, cmd + ":" + ret.toString());
        return ret.toString();
    }

    public static boolean writeToFile(String fileName,String str) {
        File file = new File(fileName);
        if(!file.exists()) {
            Log.d(TAG,"the file is not exists");
            return false;
        }

        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
            byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
            bos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG,"Exception: " + e.toString());
            return false;
        }
        return true;
    }

    public static String execShellStr(String cmd) {
        StringBuilder ret = new StringBuilder();
        Process process;
        try {
            process = Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

        try (
                BufferedReader stdout = new BufferedReader(new InputStreamReader(
                        process.getInputStream(), StandardCharsets.UTF_8));
                BufferedReader stderr = new BufferedReader(new InputStreamReader(
                        process.getErrorStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((null != (line = stdout.readLine()))
                    || (null != (line = stderr.readLine()))) {
                if (!"".equals(line)) {
                    ret = ret.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, cmd + ":" + ret.toString());
        return ret.toString();
    }
}