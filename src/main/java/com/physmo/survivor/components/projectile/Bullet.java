package com.physmo.survivor.components.projectile;

import com.physmo.garnet.graphics.Graphics;
import com.physmo.garnet.toolkit.Component;
import com.physmo.garnet.toolkit.simplecollision.Collidable;
import com.physmo.garnet.toolkit.simplecollision.ColliderComponent;
import com.physmo.garnet.toolkit.simplecollision.CollisionSystem;
import com.physmo.survivor.Constants;
import com.physmo.survivor.components.PlayerCapabilities;
import com.physmo.survivor.components.ParticleFactory;
import com.physmo.survivor.components.ProjectileType;
import com.physmo.survivor.components.SpriteHelper;
import com.physmo.survivor.components.weapons.Affliction;
import com.physmo.survivor.components.weapons.AfflictionPacket;
import com.physmo.survivor.components.weapons.DamageSupplier;

public class Bullet extends Component implements DamageSupplier {

    double speed = 50;
    ProjectileType projectileType = ProjectileType.ARROW;
    double dx = 0, dy = 0;
    boolean killMe = false;
    double age = 0;
    SpriteHelper spriteHelper;
    ColliderComponent colliderComponent;
    PlayerCapabilities playerCapabilities;
    ParticleFactory particleFactory;
    int numEnemiesHit = 0;
    int pierce = 1;
    double damage = 1;

    public void setDirection(double x, double y) {
        dx = x;
        dy = y;
    }


    @Override
    public void init() {
        playerCapabilities = getComponentFromParentContext(PlayerCapabilities.class);
        particleFactory = getComponentFromParentContext(ParticleFactory.class);

        spriteHelper = getComponentFromParentContext(SpriteHelper.class);

        colliderComponent = parent.getComponent(ColliderComponent.class);

        colliderComponent.setCallbackEnter(target -> {
            if (target.hasTag(Constants.TAG_ENEMY)) {
                numEnemiesHit++;
                if (numEnemiesHit >= pierce) {
                    killMe = true;
                }

            }
        });

    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    @Override
    public void tick(double t) {
        age += t;
        double speedAdjuster = playerCapabilities.getProjectileSpeedAdjuster();
        parent.getTransform().x += dx * t * speed * speedAdjuster;
        parent.getTransform().y += dy * t * speed * speedAdjuster;

        if (age > 3) killMe = true;

        if (killMe) {
            CollisionSystem collisionSystem = getObjectByTypeFromParentContext(CollisionSystem.class);
            Collidable collidable = parent.getComponent(ColliderComponent.class);
            collisionSystem.removeCollidable(collidable);
            parent.destroy();
        }

        colliderComponent.setCollisionRegion(-2, -2, 4, 4);


        if (projectileType == ProjectileType.MAGIC && Math.random() < 0.2) {
            particleFactory.createParticle(particleFactory.wandTrail, parent.getTransform());
        }
        if (projectileType == ProjectileType.FIREBALL && Math.random() < 0.1) {
            particleFactory.createParticle(particleFactory.flame, parent.getTransform());
        }
        if (projectileType == ProjectileType.FIREBALL && Math.random() < 0.05) {
            particleFactory.createParticle(particleFactory.lightSmoke, parent.getTransform());
        }
    }

    @Override
    public void draw(Graphics g) {
        int x = (int) parent.getTransform().x;
        int y = (int) parent.getTransform().y;

        double angle = (getAngle() / (Math.PI * 2)) * 360;

        if (projectileType == ProjectileType.ARROW) {
            spriteHelper.drawSpriteInMap(x, y, 3, 2, angle);
        } else if (projectileType == ProjectileType.ICE_ARROW) {
            spriteHelper.drawSpriteInMap(x, y, 11, 2, angle);
        } else if (projectileType == ProjectileType.MAGIC) {
            spriteHelper.drawSpriteInMap(x - 8, y - 8, 2, 2);
        } else if (projectileType == ProjectileType.FIREBALL) {
            spriteHelper.drawSpriteInMap(x, y, 5, 2, angle+(age*600));
        }
    }

    public double getAngle() {
        double a = Math.atan2(-dx, -dy);
        a = a > 0 ? (Math.PI * 2) - a : 0 - a;
        return a;
    }

    public ProjectileType getProjectileType() {
        return projectileType;
    }

    public void setProjectileType(ProjectileType type) {
        this.projectileType = type;
    }

    public void setPierce(int pierce) {
        this.pierce = pierce;
    }

    @Override
    public double getDamage() {
        return damage;
    }



    @Override
    public AfflictionPacket[] getAfflictionPackets() {

        if (projectileType == ProjectileType.ARROW) {
            return new AfflictionPacket[] {new AfflictionPacket(Affliction.BLEED, 1)};
        } else if (projectileType == ProjectileType.ICE_ARROW) {
            return new AfflictionPacket[] {new AfflictionPacket(Affliction.FREEZE, 3)};
        } else if (projectileType == ProjectileType.MAGIC) {
            return new AfflictionPacket[0];
        } else if (projectileType == ProjectileType.FIREBALL) {
            return new AfflictionPacket[] {new AfflictionPacket(Affliction.BURN, 5)};
        }

        return new AfflictionPacket[0];
    }


    public void setDamage(double damage) {
        this.damage = damage;
    }
}
