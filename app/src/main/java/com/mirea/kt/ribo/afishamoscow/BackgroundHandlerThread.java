package com.mirea.kt.ribo.afishamoscow;

import android.os.Handler;
import android.os.HandlerThread;

public class BackgroundHandlerThread extends HandlerThread {
    private Handler handler;

    public BackgroundHandlerThread(String name) {
        super(name);
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        handler = new Handler(getLooper());
    }

    public void postTask(Runnable task) {
        handler.post(task);
    }

    public void quitThread() {
        handler.removeCallbacksAndMessages(null);
        quit();
    }
}
