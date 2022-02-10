package com.sprd.engineermode.gcf;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.Context;
import android.content.SharedPreferences.Editor;

public class SharepreferenceUtils {
    private final static String TAG = "SharepreferenceUtils";
    public static void saveInfo(Context context,String keyrName, String value) {
        SharedPreferences sp = context.getSharedPreferences("gcf_info",Context.MODE_PRIVATE);
        if(sp == null){
            return;
        }
        Editor editor = sp.edit();
        editor.putString(keyrName, value);
        editor.commit();
    }
    public static String getInfo(Context context,String keyrName){
        SharedPreferences sp = context.getSharedPreferences("gcf_info",Context.MODE_PRIVATE);
        String content=sp.getString(keyrName,"");
        return content;
    }
}
