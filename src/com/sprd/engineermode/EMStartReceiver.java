package com.sprd.engineermode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import java.lang.String;


import com.sprd.engineermode.debuglog.PhoneInfoActivity;
import com.unisoc.engineermode.core.common.Const;
import com.sprd.engineermode.debuglog.SensorsIDActivity;
import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;

public class  EMStartReceiver extends BroadcastReceiver {

    private static final String TAG = "EMStartReceiver";
    /* SPRD BUG 843409 - SR-P932V40-VODAFONE-TM-0010 for ZTE @{ */
    private static final String SECURITY_CODE_ENGINEERMODE = "33284";
    /* }@ */
    public EMStartReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String host = null;
        Uri uri = intent.getData();
        if (uri != null) {
            host = uri.getHost();
        } else {
            Log.d(TAG,"uri is null");
            return;
        }
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if("83781".equals(host)){
            i.setClass(context, EngineerModeActivity.class);
            context.startActivity(i);
        } else if("83782".equals(host)){
            i.setClass(context, EngineerModeActivity_2.class);
            context.startActivity(i);
        } else if("1688".equals(host)){
            i.setClass(context, SensorsIDActivity.class);
            context.startActivity(i);
        /* SPRD BUG 843409 - SR-P932V40-VODAFONE-TM-0010 for ZTE @{ */
        } else if (Const.isBoardISharkL210c10() && SECURITY_CODE_ENGINEERMODE.equals(host)) {
            i.setClass(context, EngineerModeActivity.class);
            context.startActivity(i);
        } else if("0000".equals(host)){
            if (Const.isSupportPhoneInfo()) {
                i.setClass(context, PhoneInfoActivity.class);
                context.startActivity(i);
            } else {
                Log.d(TAG, "This product is not support!");
                return;
            }
        }
        /* }@ */
        else {
            if(SystemPropertiesProxy.get("ro.product.board.customer", "none").equalsIgnoreCase("cgmobile")){
                //cg add by xuyouqin add cgversioninfo    start
                if("837868".equals(host)) {
                    i.setClass(context, cgversioninfo.class);
                    context.startActivity(i);
                } else if("837866".equals(host)) {
                    i.setClass(context, yulongversioninfo.class);
                    context.startActivity(i);
                }
                //cg add by xuyouqin add cgversioninfo    end
            }
        }
    }
}

