package tech.kotlin.utils.os;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
public final class LooperApp {

    private static final Logger log = LogManager.getLogger(Looper.class.getSimpleName());

    public static void start(AppTask task, Runnable onDestroy, String... args) {
        Looper.prepareMain();
        try {
            if (task.onStart(args)) {
                Looper.loop();
            }
        } catch (Throwable error) {
            log.error(error);
        } finally {
            Looper.quiteMain();
            if (onDestroy != null) onDestroy.run();
        }
    }

    public static void start(AppTask task, String... args) {
        start(task, null, args);
    }

    public interface AppTask {
        boolean onStart(String... args) throws Throwable;
    }
}
