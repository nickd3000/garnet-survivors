package com.physmo.survivor;

/**
 * The Upgradable interface defines the contract for objects that have levels and can be upgraded.
 * Implementing classes will typically represent items or components that can be improved or enhanced over time.
 */
public interface Upgradable {

    int getMaxLevel();

    int getLevel();

    void increaseLevel();

    String getLevelDescription(int level);

    String getName();
}
