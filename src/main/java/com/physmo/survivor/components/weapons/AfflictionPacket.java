package com.physmo.survivor.components.weapons;

public class AfflictionPacket {
    public Affliction affliction;
    public double duration;
    public AfflictionPacket(Affliction affliction, double duration) {
        this.affliction = affliction;
        this.duration = duration;
    }
}
