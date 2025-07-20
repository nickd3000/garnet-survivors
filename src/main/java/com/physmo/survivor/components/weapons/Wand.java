package com.physmo.survivor.components.weapons;

import com.physmo.garnet.graphics.Graphics;
import com.physmo.garnet.structure.Array;
import com.physmo.garnet.toolkit.Component;
import com.physmo.garnet.toolkit.scene.SceneManager;
import com.physmo.garnet.toolkit.simplecollision.CollisionSystem;
import com.physmo.garnet.toolkit.simplecollision.RelativeObject;
import com.physmo.survivor.EntityFactory;
import com.physmo.survivor.Resources;
import com.physmo.survivor.Upgradable;
import com.physmo.survivor.components.Player;
import com.physmo.survivor.components.PlayerCapabilities;
import com.physmo.survivor.components.ProjectileType;
import com.physmo.survivor.components.items.CombinedItemStats;
import com.physmo.survivor.gamedata.GDWeapon;

import java.util.Comparator;
import java.util.List;

public class Wand extends Component implements Weapon, Upgradable {

    double cooldownPeriod = 0.7;
    double cooldown = cooldownPeriod;

    CollisionSystem collisionSystem;
    PlayerCapabilities playerCapabilities;
    int maxLevel = 15;
    int level = 0;
    WeaponStats weaponStats = new WeaponStats();
    Resources resources;
    GDWeapon gdWeapon;
    double subShotTimer;
    int pendingShots = 0;
    CombinedItemStats combinedItemStats;

    @Override
    public void init() {

        collisionSystem = getObjectByTypeFromParentContext(CollisionSystem.class);

        playerCapabilities = getComponentFromParentContext(PlayerCapabilities.class);
        resources = SceneManager.getSharedContext().getObjectByType(Resources.class);

        gdWeapon = resources.getGameData().getWeaponByName(getDataName());
        combinedItemStats = parent.getComponent(CombinedItemStats.class);
        weaponStats.refreshStats(gdWeapon, level, combinedItemStats);
    }

    @Override
    public String getDataName() {
        return "wand";
    }

    @Override
    public void tick(double t) {
        cooldown -= t;
        if (cooldown < 0) {
            //cooldown += cooldownPeriod * playerCapabilities.getProjectileRateAdjuster();
            cooldown += weaponStats.getAdjustedStat(WeaponStatType.COOLDOWN).value;
            pendingShots += (int) weaponStats.getAdjustedStat(WeaponStatType.COUNT).value;
        }

        subShotTimer -= t;
        if (subShotTimer < 0) {
            subShotTimer += weaponStats.getAdjustedStat(WeaponStatType.INTERVAL).value;
            if (pendingShots > 0) {

                fire(pendingShots);
                pendingShots = 0;
            }
        }

        weaponStats.refreshStatsOnTimeout(t, gdWeapon, level, combinedItemStats);
    }

    Array<RelativeObject> nearestEnemies = new Array<>(100);

    public void fire(int count) {


        nearestEnemies = parent.getComponent(Player.class).getNearestEnemies();
        if (nearestEnemies.size()<1) return;

        // sort array
        nearestEnemies.sort(Comparator.comparingDouble(RelativeObject::getDistance));

        for (int i = 0; i < count; i++) {
            RelativeObject relativeObject = nearestEnemies.get(i % nearestEnemies.size());

            createBullet(parent.getTransform().x, parent.getTransform().y, relativeObject.dx, relativeObject.dy);
        }
    }

    public void createBullet(double x, double y, double dx, double dy) {
        double bulletSpeed = weaponStats.getAdjustedStat(WeaponStatType.SPEED).value;
        int pierce = (int) weaponStats.getAdjustedStat(WeaponStatType.PIERCE).value;
        double damage = weaponStats.getAdjustedStat(WeaponStatType.DAMAGE).value;

        EntityFactory.createSimpleBullet(parent.getContext(), collisionSystem, x, y, dx, dy, bulletSpeed, ProjectileType.MAGIC, pierce, damage);
    }

    @Override
    public void draw(Graphics g) {

    }

    @Override
    public String getName() {
        return "Magic Wand";
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
    public int getMaxLevel() {
        return maxLevel;
    }

    @Override
    public String getLevelDescription(int level) {
        return gdWeapon.getLevels().get(level).getDescription();
    }


}
