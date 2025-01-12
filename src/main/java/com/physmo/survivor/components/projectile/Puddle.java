package com.physmo.survivor.components.projectile;

import com.physmo.garnet.graphics.Graphics;
import com.physmo.garnet.structure.Vector3;
import com.physmo.garnet.toolkit.Component;
import com.physmo.garnet.toolkit.simplecollision.Collidable;
import com.physmo.garnet.toolkit.simplecollision.ColliderComponent;
import com.physmo.garnet.toolkit.simplecollision.CollisionSystem;
import com.physmo.survivor.Constants;
import com.physmo.survivor.components.ParticleFactory;
import com.physmo.survivor.components.PlayerCapabilities;
import com.physmo.survivor.components.PuddleType;
import com.physmo.survivor.components.SpriteHelper;
import com.physmo.survivor.components.weapons.Affliction;
import com.physmo.survivor.components.weapons.AfflictionPacket;
import com.physmo.survivor.components.weapons.DamageSupplier;

public class Puddle extends Component implements DamageSupplier {


    PuddleType puddleType;
    double dx = 0, dy = 0;
    boolean killMe = false;
    double age = 0;
    SpriteHelper spriteHelper;
    ColliderComponent colliderComponent;
    PlayerCapabilities playerCapabilities;
    ParticleFactory particleFactory;
    int numEnemiesHit = 0;

    double damage = 1;
    double radius = 10;
    double lifeTime;

    public Puddle(PuddleType puddleType, double radius, double damage, double lifeTime) {
        this.puddleType = puddleType;
        this.radius = radius;
        this.damage = damage;
        this.lifeTime = lifeTime;

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
            }
        });

    }


    @Override
    public void tick(double t) {
        age += t;

        if (age > lifeTime) killMe = true;

        if (killMe) {
            CollisionSystem collisionSystem = getObjectByTypeFromParentContext(CollisionSystem.class);
            Collidable collidable = parent.getComponent(ColliderComponent.class);
            collisionSystem.removeCollidable(collidable);
            parent.destroy();
        }

        colliderComponent.setCollisionRegion((int) -radius, (int) -radius, (int) (radius * 2), (int) (radius * 2));


        if (puddleType == PuddleType.ACID && Math.random() < 0.1) {
            Vector3 offset = Vector3.generateRandomRadial2D(radius);
            offset.translate(parent.getTransform());
            particleFactory.createParticle(particleFactory.acidRing, offset);
        }
        if (puddleType == PuddleType.ACID && Math.random() < 0.2) {
            Vector3 offset = Vector3.generateRandomRadial2D(radius);
            offset.scale(Math.random());
            offset.translate(parent.getTransform());
            particleFactory.createParticle(particleFactory.acid, offset);
        }
        if (puddleType == PuddleType.ICE && Math.random() < 0.2) {
            Vector3 offset = Vector3.generateRandomRadial2D(radius);
            offset.scale(Math.random());
            offset.translate(parent.getTransform());
            particleFactory.createParticle(particleFactory.ice, offset);
        }
    }

    @Override
    public void draw(Graphics g) {
        int x = (int) parent.getTransform().x;
        int y = (int) parent.getTransform().y;

        double angle = (getAngle() / (Math.PI * 2)) * 360;

        if (puddleType == PuddleType.ACID) {
            spriteHelper.drawSpriteInMap(x, y, 3, 2, angle);
        }
    }

    public double getAngle() {
        double a = Math.atan2(-dx, -dy);
        a = a > 0 ? (Math.PI * 2) - a : 0 - a;
        return a;
    }

    public PuddleType getPuddleType() {
        return puddleType;
    }

    public void setPuddleType(PuddleType type) {
        this.puddleType = type;
    }


    @Override
    public double getDamage() {
        return damage;
    }

    @Override
    public AfflictionPacket[] getAfflictionPackets() {
        if (puddleType == PuddleType.ACID) {
            return new AfflictionPacket[]{new AfflictionPacket(Affliction.ACID, 8)};
        } else if (puddleType == PuddleType.ICE) {
            return new AfflictionPacket[]{new AfflictionPacket(Affliction.FREEZE, 8)};
        }
        return new AfflictionPacket[0];
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }
}
