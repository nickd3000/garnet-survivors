package com.physmo.survivor.components.weapons;

import com.physmo.garnet.graphics.Graphics;
import com.physmo.garnet.toolkit.Component;
import com.physmo.garnet.toolkit.scene.SceneManager;
import com.physmo.garnet.toolkit.simplecollision.CollisionSystem;
import com.physmo.garnet.toolkit.simplecollision.RelativeObject;
import com.physmo.survivor.EntityFactory;
import com.physmo.survivor.Resources;
import com.physmo.survivor.Upgradable;
import com.physmo.survivor.components.ComponentPlayer;
import com.physmo.survivor.components.ComponentPlayerCapabilities;
import com.physmo.survivor.components.ProjectileType;
import com.physmo.survivor.components.items.CombinedItemStats;
import com.physmo.survivor.gamedata.GDWeapon;

import java.util.List;
import java.util.Random;

public class Bow extends Component implements Weapon, Upgradable {

    double cooldown = 1.0;
    Random random = new Random();
    CollisionSystem collisionSystem;
    ComponentPlayerCapabilities playerCapabilities;
    int level = 0;
    WeaponStats weaponStats = new WeaponStats();
    Resources resources;
    GDWeapon gdWeapon;
    double subShotTimer;
    int pendingShots = 0;
    int maxLevel = 15;
    CombinedItemStats combinedItemStats;

    @Override
    public void init() {

        collisionSystem = parent.getContext().getObjectByType(CollisionSystem.class);

        playerCapabilities = parent.getContext().getComponent(ComponentPlayerCapabilities.class);

        resources = SceneManager.getSharedContext().getObjectByType(Resources.class);

        gdWeapon = resources.getGameData().getWeaponByName(getDataName());

        combinedItemStats = parent.getComponent(CombinedItemStats.class);

        weaponStats.refreshStats(gdWeapon, level, combinedItemStats);
    }

    @Override
    public String getDataName() {
        return "bow";
    }

    @Override
    public void tick(double t) {
        cooldown -= t;
        if (cooldown < 0) {
            //cooldown += baseCooldownTime * playerCapabilities.getProjectileRateAdjuster();
            cooldown += weaponStats.get(WeaponStatType.COOLDOWN).value;
            pendingShots += (int) weaponStats.get(WeaponStatType.COUNT).value;
        }

        subShotTimer -= t;
        if (subShotTimer < 0) {
            subShotTimer += weaponStats.get(WeaponStatType.INTERVAL).value;
            if (pendingShots > 0) {
                pendingShots--;
                fire();
            }
        }

        weaponStats.refreshStatsOnTimeout(t, gdWeapon, level, combinedItemStats);
    }

    public void fire() {
        List<RelativeObject> nearestObjects = parent.getComponent(ComponentPlayer.class).getNearestEnemies();
        if (nearestObjects.isEmpty()) return;
        RelativeObject relativeObject = nearestObjects.get(random.nextInt(nearestObjects.size()));
        createBullet(parent.getTransform().x, parent.getTransform().y, relativeObject.dx, relativeObject.dy);
    }

    public void createBullet(double x, double y, double dx, double dy) {
        double bulletSpeed = weaponStats.get(WeaponStatType.SPEED).value;
        int pierce = (int) weaponStats.get(WeaponStatType.PIERCE).value;
        double damage = weaponStats.get(WeaponStatType.DAMAGE).value;
        EntityFactory.createSimpleBullet(parent.getContext(), collisionSystem, x, y, dx, dy, bulletSpeed, ProjectileType.BULLET, pierce, damage);
    }

    @Override
    public void draw(Graphics g) {

    }

    @Override
    public String getName() {
        return "Short Bow";
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
