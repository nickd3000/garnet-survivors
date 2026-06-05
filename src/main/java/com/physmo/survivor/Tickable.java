package com.physmo.survivor;

/**
 * Common contract for objects that update once per game loop tick.
 */
public interface Tickable {

    /**
     * Advance this object by the elapsed frame time.
     *
     * @param t elapsed time since the previous tick, in seconds.
     */
    void tick(double t);
}
