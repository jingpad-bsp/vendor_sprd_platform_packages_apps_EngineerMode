package com.sprd.engineermode.utils;

import android.util.Log;
import android.content.Context;

public class UiUtils {
    static final String TAG = "UiUtils";


    public static int getResIdByRowCol(Context ctx, String prefix, int row, int col) {
        String resId = prefix + row + col;
        Log.d(TAG, String.format("getResIdByRowCol, ResId=%s, row=%d, col=%d", resId, row, col));
        return ctx.getResources().getIdentifier(resId, "id", ctx.getPackageName());
    }

}
