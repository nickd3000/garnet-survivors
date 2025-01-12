package com.physmo.survivor.components.projectile;

import com.physmo.garnet.Garnet;
import com.physmo.garnet.graphics.Graphics;
import com.physmo.garnet.toolkit.Component;
import com.physmo.garnet.toolkit.GameObject;
import com.physmo.garnet.toolkit.particle.ParticleManager;
import com.physmo.garnet.toolkit.particle.ParticleTemplate;
import com.physmo.garnet.toolkit.scene.SceneManager;
import com.physmo.garnet.toolkit.simplecollision.Collidable;
import com.physmo.garnet.toolkit.simplecollision.ColliderComponent;
import com.physmo.garnet.toolkit.simplecollision.CollisionSystem;
import com.physmo.survivor.components.ParticleFactory;
import com.physmo.survivor.components.PlayerCapabilities;
import com.physmo.survivor.components.ProjectileType;
import com.physmo.survivor.components.SpriteHelper;
import com.physmo.survivor.components.weapons.Affliction;
import com.physmo.survivor.components.weapons.AfflictionPacket;
import com.physmo.survivor.components.weapons.DamageSupplier;

public class OrbitingBullet extends Component implements DamageSupplier {

    double speed = 50;
    ProjectileType projectileType = ProjectileType.ARROW;

    boolean killMe = false;
    double age = 0;
    SpriteHelper spriteHelper;
    ColliderComponent colliderComponent;
    PlayerCapabilities playerCapabilities;

    GameObject orbitObject;
    ParticleFactory particleFactory;

    double radius;
    int bulletNumber;
    int bulletGroupSize;
    double lifeTime;
    double rotationAngle = 0;
    double spinAngle = 0;
    ParticleTemplate glaveParticleTemplate;
    ParticleManager particleManager;
    double particleTimer = 0;
    double damage;

    public OrbitingBullet(GameObject orbitObject, double radius, double speed, int bulletNumber, int bulletGroupSize, ProjectileType type, double lifeTime, double damage) {
        this.orbitObject = orbitObject;
        this.radius = radius;
        this.speed = speed;
        this.bulletNumber = bulletNumber;
        this.bulletGroupSize = bulletGroupSize;
        this.projectileType = type;
        this.lifeTime = lifeTime;
        this.damage = damage;

        rotationAngle = ((Math.PI * 2) / bulletGroupSize) * bulletNumber;

    }


    @Override
    public void init() {
        playerCapabilities = getComponentFromParentContext(PlayerCapabilities.class);
        particleManager = getObjectByTypeFromParentContext(ParticleManager.class);
        Garnet garnet = SceneManager.getSharedContext().getObjectByType(Garnet.class);
        spriteHelper = getComponentFromParentContext(SpriteHelper.class);
        particleFactory = getComponentFromParentContext(ParticleFactory.class);
        colliderComponent = parent.getComponent(ColliderComponent.class);

//        colliderComponent.setCallbackEnter(target -> {
//            if (target.hasTag(Constants.TAG_ENEMY)) {
//                killMe = true;
//            }
//        });

//        ColorSupplierLinear glaveColor = new ColorSupplierLinear(
//                new int[]{
//                        ColorUtils.asRGBA(1, 1, 1, 0.2f),
//                        ColorUtils.asRGBA(1, 1, 0, 0)});
//
//        glaveParticleTemplate = new ParticleTemplate();
//        glaveParticleTemplate.setLifeTime(0.2, 0.5);
//        glaveParticleTemplate.setSpeed(0, 5);
//        glaveParticleTemplate.setPositionJitter(1.1);
//        glaveParticleTemplate.setColorSupplier(glaveColor);
//        glaveParticleTemplate.setSpeedCurve(new StandardCurve(CurveType.LINE_DOWN));
//        glaveParticleTemplate.setParticleDrawer(p -> {
//                    int col = p.colorSupplier.getColor(p.getTime());
//                    spriteHelper.drawSpriteInMap((int) p.position.x, (int) p.position.y, 4, 2, 0, col);
//                }
//        );
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    @Override
    public void tick(double t) {
        age += t;
        rotationAngle += t * speed;
        spinAngle += t * 300;
        particleTimer -= t;

        double dx = Math.sin(rotationAngle) * radius;
        double dy = Math.cos(rotationAngle) * radius;

        parent.getTransform().x = orbitObject.getTransform().x + dx;
        parent.getTransform().y = orbitObject.getTransform().y + dy;

//        if (bulletNumber == 0) {
//            System.out.println(" " + orbitObject.getPosition().x + " " + orbitObject.getPosition().y);
//        }

        if (age > lifeTime) killMe = true;

        if (killMe) {
            //System.out.println("kill orbiter");
            CollisionSystem collisionSystem = getObjectByTypeFromParentContext(CollisionSystem.class);
            Collidable collidable = parent.getComponent(ColliderComponent.class);
            collisionSystem.removeCollidable(collidable);
            parent.destroy();
        }

        if (particleTimer < 0) {
            particleTimer += 0.02;
            //glaveParticleTemplate.initParticle(particleManager.getFreeParticle(), parent.getTransform());
            particleFactory.createParticle(particleFactory.glaveTrail, parent.getTransform());
        }
        colliderComponent.setCollisionRegion(-2, -2, 4, 4);

    }

    @Override
    public void draw(Graphics g) {
        int x = (int) parent.getTransform().x;
        int y = (int) parent.getTransform().y;

        if (projectileType == ProjectileType.GLAVE) {
            spriteHelper.drawSpriteInMap(x, y, 4, 2, spinAngle);
        }
        //else {
        //   spriteHelper.drawSpriteInMap(x - 8, y - 8, 2, 2);
        // }
    }

    public ProjectileType getProjectileType() {
        return projectileType;
    }

    public void setProjectileType(ProjectileType type) {
        this.projectileType = type;
    }

    @Override
    public double getDamage() {
        return damage;
    }

    @Override
    public AfflictionPacket[] getAfflictionPackets() {
        return new AfflictionPacket[]{new AfflictionPacket(Affliction.BLEED, 3)};
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }
}
