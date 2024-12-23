package com.physmo.survivor;

import java.util.function.DoubleConsumer;

public class TimedEvent {

    boolean active;
    double time;
    Runnable onEndRunnable;
    DoubleConsumer tickRunnable;

    public void tick(double t) {
        if (!active) return;

        time -= t;
        if (time < 0) {
            active = false;
            if (onEndRunnable != null) onEndRunnable.run();
        } else {
            if (tickRunnable != null) tickRunnable.accept(t);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void start(double time) {
        active = true;
        this.time = time;
        this.tickRunnable = null;
        this.onEndRunnable = null;
    }

    public void startAndWhileRunning(double time, DoubleConsumer r) {
        active = true;
        this.time = time;
        this.tickRunnable = r;
        this.onEndRunnable = null;
    }

    public void startAndOnEnd(double time, Runnable r) {
        active = true;
        this.time = time;
        this.tickRunnable = null;
        this.onEndRunnable = r;
    }

    public void startAndWhileRunningAndOnEnd(double time, DoubleConsumer r, Runnable r2) {
        active = true;
        this.time = time;
        this.tickRunnable = r;
        this.onEndRunnable = r2;
    }
}
