package tech.kotlin.common.os;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.concurrent.ConcurrentHashMap;

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *
 * 提供了统一的异步调度服务，所有 postDelay 服务全部由该线程执行调度
 *********************************************************************/
public final class Timer extends Thread {

    private volatile static Timer sInstance;
    private final ConcurrentHashMap<Long, ScheduleTask> taskQueue = new ConcurrentHashMap<>();
    private boolean isClose;

    static void init() {
        synchronized (Timer.class) {
            if (sInstance == null) {
                sInstance = new Timer();
                sInstance.start();
            }
        }
    }

    public static Timer getInstance() {
        synchronized (Timer.class) {
            init();
            return sInstance;
        }
    }

    private Timer() {
        super("Timer");
    }

    public final <T> boolean schedule(MsgDispatcher<T> dispatcher, T msg, long delay) {
        synchronized (this) {
            long scheduleTime = System.nanoTime() + delay * 1000_000L;
            if (taskQueue.containsKey(scheduleTime)) {
                return false;
            } else {
                taskQueue.put(scheduleTime, new ScheduleTask<>(dispatcher, msg));
                return true;
            }
        }
    }

    public final <T> void cancel(MsgDispatcher<T> dispatcher, T task, ObjEq<T> eq) {
        synchronized (this) {
            //noinspection unchecked
            taskQueue.entrySet().removeIf(entry ->
                    Objs.isEqual(entry.getValue().dispatcher, dispatcher)
                            && eq.isEq(task, (T) entry.getValue().task));

        }
    }

    public final <T> void cancelAll(MsgDispatcher<T> dispatcher) {
        synchronized (this) {
            //noinspection unchecked
            taskQueue.entrySet().removeIf(entry ->
                    Objs.isEqual(entry.getValue().dispatcher, dispatcher));
        }
    }

    @Override
    public final void run() {
        try (Selector selector = Selector.open()) {
            while (!isClose) {
                selector.select(1);
                synchronized (this) {
                    long nano = System.nanoTime();
                    taskQueue.entrySet().removeIf(entry -> {
                        if (entry.getKey() < nano) {
                            //noinspection unchecked
                            entry.getValue().dispatcher.dispatchMsg(entry.getValue().task);
                            return true;
                        }
                        return false;
                    });
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);//should not be here
        }
    }

    public void close() {
        isClose = true;
    }

    interface ObjEq<T> {
        boolean isEq(T o1, T o2);
    }

    private final class ScheduleTask<T> {

        final MsgDispatcher<T> dispatcher;
        final T task;

        ScheduleTask(MsgDispatcher<T> dispatcher, T task) {
            this.dispatcher = dispatcher;
            this.task = task;
        }
    }
}
