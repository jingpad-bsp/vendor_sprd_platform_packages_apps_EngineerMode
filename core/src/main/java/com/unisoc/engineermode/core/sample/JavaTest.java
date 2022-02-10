package com.unisoc.engineermode.core.sample;

import android.util.Log;

import com.unisoc.engineermode.core.factory.ImplementationFactory;

import java.util.List;

public class JavaTest {
    private static String TAG = "JAVATEST";

    public static JavaInterface javaApi() {
        return (JavaInterface) ImplementationFactory.create(JavaInterface.class);
    }

    public static void test() {
       Log.d(TAG, javaApi().self());
    }

}
