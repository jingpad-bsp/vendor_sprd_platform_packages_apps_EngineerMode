package com.unisoc.engineermode.core.utils;

import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class Watchdog {
    public static final String TAG = "WATCHDOG";
    private volatile int food = 0;
    private String server;
    private String cmd;
    private Future<?> future;
    private Consumer<Object> consumer;
    private Object consumerObj;
    private static final int WATCHDOG_TIME = 180;

    public Watchdog(String s, String c) {
        server = s;
        cmd = c;
    }

    void wantEat() {
        if(null==cmd){
            Log.d(TAG,   "invalid watching  " +server+" ["+cmd+"]");
            return;
        }
        food = 0;
        ExecutorService es = Executors.newSingleThreadExecutor();
        future = es.submit(this::run);
        es.shutdown();
    }

    public void feedFood() {
        food = 1;
        try{
            if(null!=future){
                future.cancel(true);
            }
        } catch (Exception e) {
            Log.w(TAG, "feedFood error" + e);

        }
    }

    public void setTimeoutCallback(Consumer<Object> callback, Object obj) {
        consumer = callback;
        consumerObj = obj;
    }

    private void run() {
        try {
            int count = 0;
            while (count < WATCHDOG_TIME) {
                if (1 == food) {
                    return;
                }
                Thread.sleep(1000);
                count++;
            }
        } catch (InterruptedException e) {
            return;
        }

        if (0 == food) {
            if (consumer != null) {
                consumer.accept(consumerObj);
            }
        }
    }
}
