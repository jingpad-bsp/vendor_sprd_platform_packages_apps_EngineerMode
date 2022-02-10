package com.unisoc.engineermode.core.sample;

import android.util.Log;
import com.unisoc.engineermode.core.annotation.Implementation;
import com.unisoc.engineermode.core.annotation.Property;
import com.unisoc.engineermode.core.factory.PropertyKeys;


@Implementation(
    interfaceClass = JavaInterface.class,
    properties =  {
        @Property(key= PropertyKeys.ANDROID_VERSION, value="9"),
        @Property(key="variant", value="SampleImpl2")
    })
public class JavaImpl implements JavaInterface {
    private static String TAG = "SampleImpl2";


    private static class Holder {
        static final JavaImpl INSTANCE = new JavaImpl();
    }

    @Override
    public String self() {
        Log.d(TAG, "this is for android10 sp7731e_1h10");
        return getClass().getSimpleName();
    }

    public static JavaImpl newInstance() {
        return Holder.INSTANCE;
    }
}
