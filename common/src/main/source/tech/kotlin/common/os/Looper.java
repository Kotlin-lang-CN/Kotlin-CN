package tech.kotlin.common.os;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
public final class Looper {

    private static final ThreadLocal<Looper> sPreparedLooper = new ThreadLocal<>();
    private static Looper sMainLooper;

    private final Thread mThread;
    /*package*/ final BlockingQueue<Message> mQueue;

    public static void prepare() {
        if (sPreparedLooper.get() != null) {
            throw new IllegalStateException("Only one Looper may be created per thread");
        }
        sPreparedLooper.set(new Looper(Thread.currentThread()));
    }

    public static void prepareMain() {
        if (sMainLooper != null) {
            throw new IllegalStateException("Only one Main Looper can be create");
        }
        prepare();
        sMainLooper = myLooper();
        Timer.init();
    }

    public static void quiteMain() {
        if (sMainLooper == null) {
            throw new IllegalStateException("Call preparedMain before quiteMain");
        }
        sMainLooper.quitSafely();
    }

    private Looper(Thread mThread) {
        this.mThread = mThread;
        this.mQueue = new LinkedBlockingQueue<>();
    }

    public static Looper getMainLooper() {
        return sMainLooper;
    }

    public static Looper myLooper() {
        return sPreparedLooper.get();
    }

    public static void loop() throws InterruptedException {
        final Looper me = myLooper();
        if (me == null) {
            throw new IllegalStateException("No Looper; Looper.prepare() wasn't called on this thread.");
        }

        //noinspection InfiniteLoopStatement
        for (; ; ) {
            Message msg = me.mQueue.take();
            msg.target.onMessage(msg);
            msg.recycleUnchecked();
        }
    }

    public Thread getThread() {
        return mThread;
    }

    public void quit() {
        mThread.interrupt();
    }

    public void quitSafely() {
        if (mThread instanceof HandlerThread) {
            ((HandlerThread) mThread).isQuit = true;
        }
        mThread.interrupt();
        if (this.equals(sMainLooper)) {
            Timer.getInstance().close();
        }
    }
}
