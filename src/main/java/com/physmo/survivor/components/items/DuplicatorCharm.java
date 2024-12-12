package com.physmo.survivor.components.items;

import com.physmo.garnet.graphics.Graphics;
import com.physmo.garnet.toolkit.Component;
import com.physmo.survivor.Upgradable;
import com.physmo.survivor.components.weapons.ValueChange;
import com.physmo.survivor.components.weapons.WeaponStatType;

public class DuplicatorCharm extends Component implements Item, Upgradable {

    int level = 1;
    int maxLevel = 10;

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
        return "Duplicator Charm";
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
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
        if (weaponStatType == WeaponStatType.COUNT) {
            //System.out.println("duplication reporting "+level);
            return ValueChange.createWholeNumberChange(level);
        }

        return ValueChange.createUnchanged();
    }
}
