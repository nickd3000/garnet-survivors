package com.physmo.survivor.components.weapons;

public interface DamageSupplier {
    double getDamage();
    //boolean causesFreeze();
    AfflictionPacket[] getAfflictionPackets();
}
