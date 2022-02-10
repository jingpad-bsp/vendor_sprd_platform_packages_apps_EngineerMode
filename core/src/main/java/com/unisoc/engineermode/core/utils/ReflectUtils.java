package com.unisoc.engineermode.core.utils;

import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class ReflectUtils {
    private static final String TAG = "REFLECTUTILS";

    @SuppressWarnings("unchecked")
    public static <T> T getProxyByClassName(String clsName, Class<T> type, Object... args) {
        Class<?> cls;
        try {
            cls =  Class.forName(clsName);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "no such class, " + clsName);
            return null;
        }

        Object target;
        //Class<?>[] constructorParamTypeArray = new Class<?>[args.length];
        Class<?>[] constructorParamTypeArray = Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new);

        try {
            target = cls.getConstructor(constructorParamTypeArray).newInstance(args);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            Log.e(TAG, String.format("exception when get %s's constructor", clsName ));
            return null;
        }

        return (T) Proxy.newProxyInstance(type.getClassLoader(),
            new Class[]{cls},
            (proxy, method, params) -> method.invoke(target, params));
    }
}
