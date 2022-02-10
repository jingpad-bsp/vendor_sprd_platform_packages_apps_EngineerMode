package com.sprd.engineermode;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;

public class yulongversioninfo extends Activity {
    private static final String TAG = "cgversioninfo";
    private TextView txtViewlabel01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.version);
        txtViewlabel01 = (TextView) findViewById(R.id.version_id);
        //*#*#837866#*#*
        String cgversion = SystemPropertiesProxy.INSTANCE.get("ro.board.cg_version", "unknown");
        txtViewlabel01.setText(cgversion);
    }
}
