package com.physmo.survivor.components.items;

import com.physmo.garnet.graphics.Graphics;
import com.physmo.garnet.toolkit.Component;
import com.physmo.survivor.Upgradable;
import com.physmo.survivor.components.weapons.ValueChange;
import com.physmo.survivor.components.weapons.WeaponStatType;

public class VortexCharm extends Component implements Item, Upgradable {

    int level = 1;

    @Override
    public void init() {

    }

    @Override
    public void tick(double t) {

    }

    @Override
    public void draw(Graphics g) {

    }

    @Override
    public String getName() {
        return "Vortex charm";
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public void increaseLevel() {
        level++;
    }

    @Override
    public String getLevelDescription(int level) {
        return "";
    }

    @Override
    public ValueChange getWeaponModifierValueChange(WeaponStatType weaponStatType) {
        if (weaponStatType == WeaponStatType.PICKUP_RADIUS) {
            return ValueChange.createPercentageChange(level * 15);
        }
        return ValueChange.createUnchanged();
    }
}
