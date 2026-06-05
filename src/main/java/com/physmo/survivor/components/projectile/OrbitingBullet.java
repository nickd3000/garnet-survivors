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
import com.physmo.garnet.toolkit.tick.TickGate;
import com.physmo.garnet.toolkit.tick.Timer;
import com.physmo.survivor.components.ParticleFactory;
import com.physmo.survivor.components.PlayerCapabilities;
import com.physmo.survivor.components.ProjectileType;
import com.physmo.survivor.components.SpriteHelper;
import com.physmo.survivor.components.weapons.Affliction;
import com.physmo.survivor.components.weapons.AfflictionPacket;
import com.physmo.survivor.Constants;
import com.physmo.survivor.Message;
import com.physmo.survivor.messages.DamageMessage;
import java.util.Arrays;

public class OrbitingBullet extends Component {

    double speed = 50;
    ProjectileType projectileType = ProjectileType.ARROW;

    boolean killMe = false;
    SpriteHelper spriteHelper;
    ColliderComponent colliderComponent;
    PlayerCapabilities playerCapabilities;

    GameObject orbitObject;
    ParticleFactory particleFactory;
    Timer lifetime;

    double radius;
    int bulletNumber;
    int bulletGroupSize;
    double rotationAngle = 0;
    double spinAngle = 0;
    ParticleTemplate glaveParticleTemplate;
    ParticleManager particleManager;
    TickGate particleGate = new TickGate(0.02);
    boolean firstParticleTick = true;
    double damage;

    public OrbitingBullet(GameObject orbitObject, double radius, double speed, int bulletNumber, int bulletGroupSize, ProjectileType type, double lifeTime, double damage) {
        this.orbitObject = orbitObject;
        this.radius = radius;
        this.speed = speed;
        this.bulletNumber = bulletNumber;
        this.bulletGroupSize = bulletGroupSize;
        this.projectileType = type;
        this.damage = damage;
        lifetime = new Timer(lifeTime);

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
        lifetime.restart();

        colliderComponent.setCallbackEnter(target -> {
            if (target.hasTag(Constants.TAG_ENEMY)) {
                target.sendMessage(Message.DAMAGE, new DamageMessage(damage, Arrays.asList(getAfflictionPackets())));
            }
        });

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
        lifetime.tick(t);
        rotationAngle += t * speed;
        spinAngle += t * 300;

        double dx = Math.sin(rotationAngle) * radius;
        double dy = Math.cos(rotationAngle) * radius;

        parent.getTransform().x = orbitObject.getTransform().x + dx;
        parent.getTransform().y = orbitObject.getTransform().y + dy;

//        if (bulletNumber == 0) {
//            System.out.println(" " + orbitObject.getPosition().x + " " + orbitObject.getPosition().y);
//        }

        if (lifetime.isComplete()) killMe = true;

        if (killMe) {
            //System.out.println("kill orbiter");
            CollisionSystem collisionSystem = getObjectByTypeFromParentContext(CollisionSystem.class);
            Collidable collidable = parent.getComponent(ColliderComponent.class);
            collisionSystem.removeCollidable(collidable);
            parent.destroy();
        }

        boolean emitParticle = particleGate.allow(t);
        if (firstParticleTick || emitParticle) {
            firstParticleTick = false;
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

    public double getDamage() {
        return damage;
    }

    public AfflictionPacket[] getAfflictionPackets() {
        return new AfflictionPacket[]{new AfflictionPacket(Affliction.BLEED, 3)};
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }
}
