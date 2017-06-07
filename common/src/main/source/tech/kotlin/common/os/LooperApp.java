package tech.kotlin.common.os;


/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
public final class LooperApp {

    public static void start(AppTask task, String... args) {
        Looper.prepareMain();
        try {
            task.onStart(args);
            Looper.loopInSafe();
        } catch (Throwable err) {
            Log.e(err);
        }
    }

    public interface AppTask {
        void onStart(String... args) throws Throwable;
    }

}
