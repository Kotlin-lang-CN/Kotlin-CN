package tech.kotlin.utils.os;

import tech.kotlin.utils.log.Log;

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
public final class LooperApp {

    public static void start(AppTask task, String... args) throws InterruptedException {
        Looper.prepareMain();
        try {
            task.onStart(args);
        } catch (Throwable error) {
            Log.e(error);
            return;
        }
        try {
            Looper.loop();
        } finally {
            Looper.quiteMain();
        }
    }

    public interface AppTask {
        void onStart(String... args) throws Throwable;
    }

}
