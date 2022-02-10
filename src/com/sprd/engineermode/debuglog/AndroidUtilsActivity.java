
package com.sprd.engineermode.debuglog;

import android.os.Bundle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.TwoStatePreference;
import android.util.Log;

import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;

public class AndroidUtilsActivity extends PreferenceActivity implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "AndroidUtilsActivity";
    //public static final String KEY_SLIDE_SETTINGS = "key_slide_settings";
     /* SPRD:modify for Bug 653299 add switch for starting window. @{ */
    private static final String KEY_STARTING_WINDOW = "startingwindow";
    private static final String STARTING_WINDOW__ENABLED = "persist.sys.startingwindow";
    private TwoStatePreference mStartingWindow;
    /* @} */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_android_utils);

    /* SPRD:modify for Bug 653299 add switch for starting window. @{ */
        mStartingWindow = (TwoStatePreference) findPreference(KEY_STARTING_WINDOW);
        mStartingWindow.setOnPreferenceChangeListener(this);
        /* @} */
    }

    @Override
    public void onResume() {
        super.onResume();

        /* SPRD:modify for Bug 653299 add switch for starting window. @{ */
        if (mStartingWindow != null) {
            mStartingWindow.setChecked(SystemPropertiesProxy.getBoolean(STARTING_WINDOW__ENABLED, false));
        }
    }

     @Override
    public boolean onPreferenceChange(Preference preference, Object keyValue) {
         if (preference == mStartingWindow) {
            SystemPropertiesProxy.set(STARTING_WINDOW__ENABLED, Boolean.toString((boolean) keyValue));
            return true;
        }
        return false;
    }

    public String readFile(String path) {
        File file = new File(path);
        StringBuffer sb = new StringBuffer();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            Log.d(TAG, "Read file error!!!");
            sb.append("readError");
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
        Log.d(TAG, "read " + path + " value is " + sb.toString().trim());
        return sb.toString().trim();
    }
}
