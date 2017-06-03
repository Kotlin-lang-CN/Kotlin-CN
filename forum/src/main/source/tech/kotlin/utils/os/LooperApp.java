package tech.kotlin.utils.os;

import tech.kotlin.utils.log.Log;

import java.io.IOException;

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
public final class LooperApp {

    public static void start(AppTask task, String... args) {
        Looper.prepareMain();
        try {
            task.onStart(args);
            Looper.loop();
        } catch (Throwable err) {
            Log.e(err);
        } finally {
            Looper.quiteMain();
        }
    }

    public interface AppTask {
        void onStart(String... args) throws Throwable;
    }

}
