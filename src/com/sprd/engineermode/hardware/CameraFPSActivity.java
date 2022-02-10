package com.sprd.engineermode.hardware;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;

public class CameraFPSActivity extends PreferenceActivity implements
        Preference.OnPreferenceChangeListener,Preference.OnPreferenceClickListener{

    private static final String TAG = "CameraFPSActivity";

    private static final String KEY_PREVIEW = "preview";
    private static final String KEY_RECORDING = "recording";
    private static final String KEY_RESTORE_DEFAULTS = "restore_defaults";

    private Preference mpreview,mrecording;
    private Preference mRestoreDefaults;
    private AlertDialog dialog;
    public static SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_camera_fps);

        mpreview = (Preference) findPreference(KEY_PREVIEW);
        mpreview.setOnPreferenceClickListener(this);

        mrecording = (Preference) findPreference(KEY_RECORDING);
        mrecording.setOnPreferenceClickListener(this);

        mRestoreDefaults = (Preference) findPreference(KEY_RESTORE_DEFAULTS);
        mRestoreDefaults.setOnPreferenceClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mpreview.setSummary(SystemPropertiesProxy.get("persist.vendor.cam.preview.fps","0"));
        mrecording.setSummary(SystemPropertiesProxy.get("persist.vendor.cam.record.fps","0"));
    }

    @Override
    protected void onDestroy() {
        if (dialog !=null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = null;
        super.onDestroy();
    }

    @Override
    public boolean onPreferenceChange(Preference pref, Object objValue) {
        Log.d(TAG, "pref ="+pref.getKey());
        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference pref) {
        Log.d(TAG, "key="+pref.getKey());
        if (pref.getKey().equals(KEY_PREVIEW)) {
             showSetDialog("Preview");
        } else if (pref.getKey().equals(KEY_RECORDING)) {
             showSetDialog("Recording");
        } else if (pref.getKey().equals(KEY_RESTORE_DEFAULTS)) {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle("Warning !")
                        .setMessage("All operations will restore default values.")
                        .setCancelable(false)
                        .setPositiveButton(
                                getString(R.string.alertdialog_ok),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        restoreAllDefault();
                                    }
                                })
                        .setNegativeButton(
                                getString(R.string.alertdialog_cancel),
                                new DialogInterface.OnClickListener() {
                                @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).create();
                   alertDialog.show();
        }
        return true;
    }

    private void restoreAllDefault() {
        SystemPropertiesProxy.set("persist.vendor.cam.preview.fps", "0");
        SystemPropertiesProxy.set("persist.vendor.cam.record.fps", "0");
        mpreview.setEnabled(true);
        mpreview.setSummary("0");
        mrecording.setEnabled(true);
        mrecording.setSummary("0");
        clearReservedProps();
    }

     private void clearReservedProps() {
        SharedPreferences sp = getSharedPreferences("prop_vaule", Context.MODE_PRIVATE);
        int propCount = sp.getInt("prop_count", 0);
        Log.d(TAG,"propCount="+propCount);
        if (propCount == 0) {
            return;
        }
        mEditor = sp.edit();
        for (int i=1;i<= propCount; i++) {
            String key = "info_" + Integer.toString(i);
            String keyProp = key + "_prop";
            if (sp.getString(keyProp,null) != null) {
                SystemPropertiesProxy.set(sp.getString(keyProp,null),null);
            }
        }
        propCount =0;
        mEditor.clear();
        mEditor.apply();
     }
    private void showSetDialog(String type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_fps, null);
        dialog.setView(view, 0, 0, 0, 0);

        EditText setMax = (EditText) view.findViewById(R.id.max_vaule);
        EditText setMin = (EditText) view.findViewById(R.id.min_vaule);
        TextView title = (TextView) view.findViewById(R.id.textView1);
        title.setText(type);
        Button btnOK = (Button) view.findViewById(R.id.btn_ok);
        btnOK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String maxVaule = setMax.getText().toString();
                String minVaule = setMin.getText().toString();
                if (TextUtils.isEmpty(maxVaule) ||TextUtils.isEmpty(minVaule)) {
                    Toast.makeText(CameraFPSActivity.this, "empty input!", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                     if (Integer.parseInt(maxVaule) < Integer.parseInt(minVaule)) {
                         Toast.makeText(CameraFPSActivity.this, "the minvaule is bigger than the maxvaule!", Toast.LENGTH_SHORT).show();
                         return;
                     }else if (Integer.parseInt(maxVaule)>30 ||Integer.parseInt(maxVaule)<5
                                 ||Integer.parseInt(minVaule)>30 ||Integer.parseInt(minVaule)<5) {
                          Toast.makeText(CameraFPSActivity.this, "please input vaule 5~30", Toast.LENGTH_SHORT).show();
                          return;
                     }else {
                          if (maxVaule.length() ==1) {
                              maxVaule ="0"+maxVaule;
                          }
                          if (minVaule.length() ==1) {
                              minVaule ="0"+minVaule;
                          }
                          String range = maxVaule+minVaule;
                          if (type.equals("Preview")) {
                              SystemPropertiesProxy.set("persist.vendor.cam.preview.fps", range);
                              mpreview.setSummary(range);
                              Log.d(TAG, "Preview="+SystemPropertiesProxy.get("persist.vendor.cam.preview.fps"));
                          }else {
                              SystemPropertiesProxy.set("persist.vendor.cam.record.fps", range);
                              mrecording.setSummary(range);
                              Log.d(TAG, "record="+SystemPropertiesProxy.get("persist.vendor.cam.record.fps"));
                          }
                     }
                }
                dialog.dismiss();
            }
        });

        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}
