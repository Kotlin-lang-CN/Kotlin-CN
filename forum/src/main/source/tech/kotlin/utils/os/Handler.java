package tech.kotlin.utils.os;


import tech.kotlin.utils.exceptions.Objs;

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
public class Handler implements MsgDispatcher<Message> {

    private final Looper mLooper;

    public Handler() {
        mLooper = Looper.myLooper();
    }

    public Handler(Looper looper) {
        this.mLooper = looper;
    }

    public final Looper getLooper() {
        return mLooper;
    }

    public final void sendMessage(Message msg) {
        dispatchMsg(msg);
    }

    public final void post(Runnable task) {
        dispatchMsg(new Message(task, this));
    }

    public final void postDelay(Runnable task, long delay) {
        Timer.getInstance().schedule(this, new Message(task, this), delay);
    }

    public final void cancel(Runnable task) {
        Timer.getInstance().cancel(this, new Message(task, this), (o1, o2) ->
                Objs.isEqual(o1.callback, o2.callback));
    }

    public final void cancelAll() {
        Timer.getInstance().cancelAll(this);
    }

    public void handleMessage(Message task) {
    }

    /**
     * Handle system messages here.
     */
    @Override
    public void dispatchMsg(Message msg) {
        mLooper.mQueue.add(msg);
    }

    final void onMessage(Message msg) {
        if (msg.callback != null) {
            msg.callback.run();
        } else {
            handleMessage(msg);
        }
    }
}
