package com.lq.butterknifelol;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

public abstract class DebouncingOnClickListener implements View.OnClickListener {
    //  private static final Runnable ENABLE_AGAIN = () -> enabled = true;
    private static final Runnable ENABLE_AGAIN = new Runnable() {
        @Override
        public void run() {
            enabled = true;
        }
    };
    private static final Handler MAIN = new Handler(Looper.getMainLooper());

    static boolean enabled = true;

    @Override
    public final void onClick(View v) {
        if (enabled) {
            enabled = false;
            // Post to the main looper directly rather than going through the view.
            // Ensure that ENABLE_AGAIN will be executed, avoid static field {@link #enabled}
            // staying in false state.
            MAIN.post(ENABLE_AGAIN);
            doClick(v);
        }
    }

    public abstract void doClick(View v);
}