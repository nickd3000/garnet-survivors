package com.physmo.survivor.components.weapons;

// TODO: rename this if it's not just going to be for weapons
public enum WeaponStatType {
    DAMAGE(false),
    SPEED(false),
    COOLDOWN(false),
    COUNT(true),
    PIERCE(true),
    INTERVAL(false),
    KNOCK_BACK(false),
    DURATION(false),
    SIZE(false),
    PICKUP_RADIUS(false),
    MOVEMENT_SPEED(false);

    final boolean isInteger;

    WeaponStatType(boolean isInteger) {
        this.isInteger = isInteger;
    }

    public boolean isInteger() {
        return isInteger;
    }
}
