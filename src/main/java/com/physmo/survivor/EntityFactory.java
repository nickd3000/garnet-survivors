package com.physmo.survivor;

import com.physmo.garnet.toolkit.Context;
import com.physmo.garnet.toolkit.GameObject;
import com.physmo.garnet.toolkit.scene.SceneManager;
import com.physmo.garnet.toolkit.simplecollision.ColliderComponent;
import com.physmo.garnet.toolkit.simplecollision.CollisionSystem;
import com.physmo.survivor.components.Crystal;
import com.physmo.survivor.components.Enemy;
import com.physmo.survivor.components.GameLogic;
import com.physmo.survivor.components.ProjectileType;
import com.physmo.survivor.components.projectile.Bullet;
import com.physmo.survivor.components.projectile.OrbitingBullet;
import com.physmo.survivor.gamedata.GDEnemy;

import java.util.List;

public class EntityFactory {

    public static double baseEnemySpeed = 10;

    public static void addEnemy(Context context, CollisionSystem collisionSystem, int x, int y) {
        Enemy enemyComponent = new Enemy();
        GameObject enemy = new GameObject("enemy");
        enemy.addComponent(enemyComponent);
        enemy.getTransform().set(x, y, 0);
        enemy.addTag(Constants.TAG_ENEMY);

        addColliderToGameObject(collisionSystem, enemy);

        GameLogic gameLogic = context.getComponent(GameLogic.class);


        configureEnemy(context, gameLogic, enemyComponent, 1);


        context.add(enemy);
    }

    public static void addColliderToGameObject(CollisionSystem collisionSystem, GameObject gameObject) {
        ColliderComponent collider = new ColliderComponent();
        gameObject.addComponent(collider);
        collisionSystem.addCollidable(collider);
    }

    public static void addCrystal(Context context, CollisionSystem collisionSystem, int x, int y) {
        GameObject entity = new GameObject("crystal").addComponent(new Crystal());
        entity.getTransform().set(x, y, 0);
        entity.addTag(Constants.TAG_CRYSTAL);

        addColliderToGameObject(collisionSystem, entity);

        context.add(entity);
    }

    public static void configureEnemy(Context context, GameLogic gameLogic, Enemy enemy, int type) {

        Resources resources = SceneManager.getSharedContext().getObjectByType(Resources.class);
        int wave = gameLogic.getCurrentWave();

        int numEnemyTypes = resources.getGameData().getEnemies().size();

        type = wave;
        if (Math.random() > 0.8) type += 1;
        type = type % numEnemyTypes;

        List<GDEnemy> enemies = resources.getGameData().getEnemies();
        GDEnemy enemyData = null;
        for (GDEnemy e : enemies) {
            if (e.getId() == type) {
                enemyData = e;
            }
        }

        assert enemyData != null;
        int[] coords = convertCoords(enemyData.getSprite());

        enemy.setDetails(enemyData.getSpeed() * baseEnemySpeed, enemyData.getHealth(), coords[0], coords[1]);

    }

    public static void createSimpleBullet(Context context, CollisionSystem collisionSystem, double x, double y, double dx, double dy, double speed, ProjectileType type, int pierce, double damage) {
        Bullet bullet = new Bullet();
        GameObject obj = new GameObject("bullet").addComponent(bullet);
        ColliderComponent collider = new ColliderComponent();
        obj.addComponent(collider);
        obj.getTransform().set(x, y, 0);
        bullet.setDirection(dx, dy);
        bullet.setProjectileType(type);
        bullet.setSpeed(speed);
        bullet.setPierce(pierce);
        bullet.setDamage(damage);

        obj.addTag(Constants.TAG_BULLET);
        context.add(obj);

        collisionSystem.addCollidable(collider);
    }

    public static void createOrbitingBullet(Context context, CollisionSystem collisionSystem, GameObject parent, double radius, double speed, int bulletNumber, int bulletGroupSize, ProjectileType type, double lifeTime, double damage) {

        GameObject player = parent.getContext().getObjectByTag("player");

        OrbitingBullet orbitingBullet = new OrbitingBullet(player, radius, speed, bulletNumber, bulletGroupSize, type, lifeTime, damage);
        GameObject obj = new GameObject("bullet").addComponent(orbitingBullet);
        ColliderComponent collider = new ColliderComponent();
        obj.addComponent(collider);

        obj.addTag(Constants.TAG_BULLET);
        context.add(obj);
        collisionSystem.addCollidable(collider);
    }

    public static int[] convertCoords(String coords) {
        String[] strArray = coords.split(",");
        int[] intArray = new int[strArray.length];
        for (int i = 0; i < strArray.length; i++) {
            intArray[i] = Integer.parseInt(strArray[i]);
        }
        return intArray;
    }
}
