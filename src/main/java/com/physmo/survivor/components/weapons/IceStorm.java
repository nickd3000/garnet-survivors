package com.physmo.survivor.components.weapons;

import com.physmo.garnet.graphics.Graphics;
import com.physmo.garnet.toolkit.Component;
import com.physmo.garnet.toolkit.scene.SceneManager;
import com.physmo.garnet.toolkit.simplecollision.CollisionSystem;
import com.physmo.survivor.EntityFactory;
import com.physmo.survivor.Resources;
import com.physmo.survivor.Upgradable;
import com.physmo.survivor.components.PlayerCapabilities;
import com.physmo.survivor.components.PuddleType;
import com.physmo.survivor.components.items.CombinedItemStats;
import com.physmo.survivor.gamedata.GDWeapon;

public class IceStorm extends Component implements Weapon, Upgradable {

    CollisionSystem collisionSystem;
    PlayerCapabilities playerCapabilities;
    WeaponStats weaponStats = new WeaponStats();
    Resources resources;
    GDWeapon gdWeapon;
    CombinedItemStats combinedItemStats;

    double cooldown;
    int maxLevel = 15;
    int level = 0;
    double subShotTimer;
    int pendingShots = 0;
    double spawnDistance = 16 * 5;

    @Override
    public void init() {

        collisionSystem = getObjectByTypeFromParentContext(CollisionSystem.class);

        playerCapabilities = getComponentFromParentContext(PlayerCapabilities.class);
        resources = SceneManager.getSharedContext().getObjectByType(Resources.class);

        gdWeapon = resources.getGameData().getWeaponByName(getDataName());
        combinedItemStats = parent.getComponent(CombinedItemStats.class);
        weaponStats.refreshStats(gdWeapon, level, combinedItemStats);

        cooldown = weaponStats.getAdjustedStat(WeaponStatType.COOLDOWN).value;
    }

    @Override
    public void tick(double t) {
        cooldown -= t;
        if (cooldown < 0) {
            cooldown += weaponStats.getAdjustedStat(WeaponStatType.COOLDOWN).value;
            pendingShots += (int) weaponStats.getAdjustedStat(WeaponStatType.COUNT).value;
        }

        subShotTimer -= t;
        if (subShotTimer < 0) {
            subShotTimer += weaponStats.getAdjustedStat(WeaponStatType.INTERVAL).value;
            if (pendingShots > 0) {
                fire(1);
                pendingShots--;
            }
        }

        weaponStats.refreshStatsOnTimeout(t, gdWeapon, level, combinedItemStats);
    }

    public void fire(int count) {

        double x = parent.getTransform().x;
        double y = parent.getTransform().y;
        for (int i = 0; i < count; i++) {
            double a = Math.random() * 360;
            double d = spawnDistance + (Math.random() * 32);
            double dx = Math.sin(a) * d;
            double dy = Math.cos(a) * d;

            createPuddle(x + dx, y + dy);
        }
    }

    public void createPuddle(double x, double y) {
        double size = weaponStats.getAdjustedStat(WeaponStatType.SIZE).value;
        double damage = weaponStats.getAdjustedStat(WeaponStatType.DAMAGE).value;
        double duration = weaponStats.getAdjustedStat(WeaponStatType.DURATION).value;

        EntityFactory.createPuddle(parent.getContext(), collisionSystem, x, y, parent, size, PuddleType.ICE, duration, damage);

        System.out.println("creating puddle:" + PuddleType.ICE);
    }

    @Override
    public void draw(Graphics g) {

    }

    @Override
    public String getName() {
        return "Ice Storm";
    }

    @Override
    public String getDataName() {
        return "icestorm";
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void increaseLevel() {
        level++;
        weaponStats.refreshStats(gdWeapon, level, combinedItemStats);
    }

    @Override
    public String getLevelDescription(int level) {
        return gdWeapon.getLevels().get(level).getDescription();
    }


}
