package com.sprd.engineermode.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class EMFileUtils {
    private static final String TAG = "FileUtils";

    public static void write(String path, String content, boolean append) {
        File file = new File(path);
        write(file, content, append);
    }

    public static void write(File file, String content, boolean append) {
        FileOutputStream fos=null;
        try {
            if (file.exists()) {
                Log.d(TAG, "File is exists");
            } else {
                Log.d(TAG, "file is not exist, creating...");
                if (file.createNewFile()) {
                    Log.d(TAG, "creat success！");
                } else {
                    Log.d(TAG, "creat fail！");
                    return;
                }
            }

            fos=new FileOutputStream(file, append);
            fos.write(content.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        } finally {
            try {
                if (fos != null) fos.close();
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
            }
        }
    }

    public static String read(String filePath) {
        File file = new File(filePath);
        return read(file);
    }

    public static String read(File file) {
        String lineStr = null;
        StringBuilder strBuilder = new StringBuilder();
        if (file.exists()) {
            BufferedReader buffReader = null;
            FileInputStream fis = null;
            InputStreamReader isReader = null;
            try {
                final String newLine=System.lineSeparator();
                fis = new FileInputStream(file);
                isReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
                buffReader = new BufferedReader(isReader);
                while ((lineStr = buffReader.readLine()) != null) {
                    strBuilder.append(lineStr);
                    strBuilder.append(newLine);
                }
                if(strBuilder.length()>=newLine.length()) strBuilder.setLength(strBuilder.length()-newLine.length());

                return strBuilder.toString();
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
            } finally {
                try {
                    if (fis != null) fis.close();
                    if (isReader != null) isReader.close();
                    if (buffReader != null) buffReader.close();
                } catch (Exception e) {
                    Log.e(TAG, e.toString(), e);
                }
            }
        } else {
            Log.e(TAG, "File not found: " + file.getPath());
        }

        return null;
    }

    public static void newFolder(String folderPath) {
        File myFilePath = new File(folderPath);
        try {
            if (myFilePath.isDirectory()) {
                Log.d(TAG, "the directory is exists!");
            } else {
                if(myFilePath.mkdirs()) {
                    Log.d(TAG, "create directory success");
                } else {
                    Log.d(TAG, "create directory fail");
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "create directory fail");
            Log.e(TAG, e.toString(), e);
        }
    }

    public static boolean deleteFolder(String folder) {
        return deleteFolder(new File(folder));
    }

    public static boolean deleteFolder(File folder) {
        boolean result = false;
        try {
            if (folder.isDirectory()) {
                File childs[] = folder.listFiles();
                if (childs != null && childs.length > 0) {
                    for (File child : childs) {
                        if (child.isFile()) {
                            result = child.delete();
                            if (!result) return false;
                        } else if (child.isDirectory()) {
                            result = deleteFolder(child);
                            if (!result) return false;
                        }
                    }
                }

                result = folder.delete();
            } else {
                Log.d(TAG, "it is not a directory: " + folder);
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
            result = false;
        }

        return result;
    }

    public static boolean isFileDirExits(String folderPath) {
        File myFilePath = new File(folderPath);

        return myFilePath.isDirectory();
    }

    public static ArrayList<String> getDirectoryName(String path) {
        File f = new File(path);
        if (!f.exists() || !f.isDirectory()) {
            return null;
        }

        File[] files = f.listFiles();

        if (files == null) {
            return null;
        }

        ArrayList<String> dirList = new ArrayList<String>();
        for (File file : files) {
            if (file.isDirectory()) {
                dirList.add(file.getName());
            }
        }
        return dirList;
    }

}
