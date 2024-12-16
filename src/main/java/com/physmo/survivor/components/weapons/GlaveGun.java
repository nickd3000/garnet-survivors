package com.physmo.survivor.components.weapons;

import com.physmo.garnet.graphics.Graphics;
import com.physmo.garnet.toolkit.Component;
import com.physmo.garnet.toolkit.scene.SceneManager;
import com.physmo.garnet.toolkit.simplecollision.CollisionSystem;
import com.physmo.survivor.EntityFactory;
import com.physmo.survivor.Resources;
import com.physmo.survivor.Upgradable;
import com.physmo.survivor.components.ComponentPlayerCapabilities;
import com.physmo.survivor.components.ProjectileType;
import com.physmo.survivor.components.items.CombinedItemStats;
import com.physmo.survivor.gamedata.GDWeapon;

public class GlaveGun extends Component implements Weapon, Upgradable {
    double cooldownPeriod = 8.0;
    double cooldown = 0.1;

    CollisionSystem collisionSystem;
    ComponentPlayerCapabilities playerCapabilities;

    int level = 0;
    WeaponStats weaponStats = new WeaponStats();
    Resources resources;
    GDWeapon gdWeapon;
    int maxLevel = 15;
    CombinedItemStats combinedItemStats;

    @Override
    public void init() {

        collisionSystem = getObjectByTypeFromParentContext(CollisionSystem.class);

        playerCapabilities = getComponentFromParentContext(ComponentPlayerCapabilities.class);

        resources = SceneManager.getSharedContext().getObjectByType(Resources.class);

        gdWeapon = resources.getGameData().getWeaponByName(getDataName());
        combinedItemStats = parent.getComponent(CombinedItemStats.class);

        weaponStats.refreshStats(gdWeapon, level, combinedItemStats);
    }

    public double getEffectiveCooldownPeriod() {
        return cooldownPeriod * playerCapabilities.getProjectileRateAdjuster();
    }

    @Override
    public String getDataName() {
        return "glave";
    }

    @Override
    public void tick(double t) {
        cooldown -= t;
        if (cooldown < 0) {
            cooldown += weaponStats.getAdjustedStat(WeaponStatType.COOLDOWN).value;
            int shotCount = (int) weaponStats.getAdjustedStat(WeaponStatType.COUNT).value;
            for (int i = 0; i < shotCount; i++) {
                fire(i, shotCount);
            }
        }

        weaponStats.refreshStatsOnTimeout(t, gdWeapon, level, combinedItemStats);
    }

    public void fire(int bulletNumber, int bulletTotal) {
        double lifeTime = weaponStats.getAdjustedStat(WeaponStatType.DURATION).value;
        double radius = 40;
        double speed = weaponStats.getAdjustedStat(WeaponStatType.SPEED).value;
        double damage = weaponStats.getAdjustedStat(WeaponStatType.DAMAGE).value;
        EntityFactory.createOrbitingBullet(parent.getContext(), collisionSystem, parent, radius, speed, bulletNumber, bulletTotal, ProjectileType.GLAVE, lifeTime, damage);
    }


    @Override
    public void draw(Graphics g) {

    }

    @Override
    public String getName() {
        return "Glave Gun";
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
