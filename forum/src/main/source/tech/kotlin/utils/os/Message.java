package tech.kotlin.utils.os;

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
public final class Message {

    public int what;
    public Object obj;

    /*package*/ Runnable callback;
    /*package*/ Handler target;
    private boolean isRecycle;

    public static Message obtain(int what, Object obj, Handler target) {
        return new Message(what, obj, target);
    }

    public static Message obtain(int what, Handler target) {
        return new Message(what, null, target);
    }

    private Message(int what, Object obj, Handler target) {
        this.what = what;
        this.obj = obj;
        this.target = target;
        this.callback = null;
    }

    /*package*/ Message(Runnable callback, Handler target) {
        this.what = 0;
        this.obj = null;
        this.target = target;
        this.callback = callback;
    }

    /*package*/ void recycleUnchecked() {
        what = 0;
        obj = null;
        callback = null;
        target = null;

        isRecycle = true;
    }

    public void sendToTarget() {
        target.sendMessage(this);
    }
}
