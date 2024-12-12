package com.physmo.survivor.components.items;


import com.physmo.survivor.components.weapons.ValueChange;
import com.physmo.survivor.components.weapons.WeaponStatType;

public interface Item {
    String getName();

    int getLevel();

    int getMaxLevel();

    void increaseLevel();

    ValueChange getWeaponModifierValueChange(WeaponStatType weaponStatType);
}
