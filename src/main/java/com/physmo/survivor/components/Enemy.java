package com.physmo.survivor.components;

import com.physmo.garnet.graphics.Graphics;
import com.physmo.garnet.structure.Vector3;
import com.physmo.garnet.toolkit.Component;
import com.physmo.garnet.toolkit.GameObject;
import com.physmo.garnet.toolkit.scene.SceneManager;
import com.physmo.garnet.toolkit.simplecollision.ColliderComponent;
import com.physmo.garnet.toolkit.simplecollision.CollisionSystem;
import com.physmo.garnet.toolkit.simplecollision.RelativeObject;
import com.physmo.survivor.Constants;
import com.physmo.survivor.EntityFactory;
import com.physmo.survivor.Resources;
import com.physmo.survivor.TimedEvent;
import com.physmo.survivor.components.weapons.Affliction;
import com.physmo.survivor.components.weapons.AfflictionPacket;
import com.physmo.survivor.components.weapons.DamageSupplier;

import java.util.ArrayList;
import java.util.List;

public class Enemy extends Component {
    Vector3 moveDir = new Vector3(1, 0, 0);
    GameObject player;

    double moveDirTimeout = 0;

    double health = 100;
    List<RelativeObject> closeObjects = new ArrayList<>();

    SpriteHelper spriteHelper;
    double rollAngle = 0;
    Resources resources;
    ColliderComponent collider;
    double speed = 4;
    int[] sprite = new int[2];
    PlayerCapabilities playerCapabilities;
    GameLogic gameLogic;
    ParticleFactory particleFactory;

    TimedEvent hitFlashEvent = new TimedEvent();

    TimedEvent frozenEvent = new TimedEvent();
    TimedEvent burnEvent = new TimedEvent();
    TimedEvent poisonEvent = new TimedEvent();
    TimedEvent acidEvent = new TimedEvent();
    TimedEvent bleedEvent = new TimedEvent();

    TimedEvent pushBackEvent = new TimedEvent();



    @Override
    public void init() {

        playerCapabilities = getComponentFromParentContext(PlayerCapabilities.class);
        particleFactory = getComponentFromParentContext(ParticleFactory.class);

        spriteHelper = getComponentFromParentContext(SpriteHelper.class);

        player = getObjectByTagFromParentContext(Constants.TAG_PLAYER);

        collider = parent.getComponent(ColliderComponent.class);
        collider.setCallbackProximity(relativeObject -> {
            closeObjects.add(relativeObject); // Just store for now and process the event in the tick function.
        });
        collider.setCallbackEnter(target -> {
            //if (target.hasTag(Constants.TAG_BULLET)) {
                for (Component component : target.getComponents()) {
                    if (component instanceof DamageSupplier damageSupplier) {

                        health -= damageSupplier.getDamage();

                        hitFlashEvent.start(0.2);

                        processAfflictionPackets(damageSupplier.getAfflictionPackets());


                        pushBackEvent.start(0.5);

                    }
                }

            //}
        });

        resources = SceneManager.getSharedContext().getObjectByType(Resources.class);
        gameLogic = getComponentFromParentContext(GameLogic.class);

        rollAngle = Math.random() * 360;

    }

    public void setDetails(double speed, double health, int spriteX, int spriteY) {
        this.speed = speed;
        this.health = health;
        this.sprite[0] = spriteX;
        this.sprite[1] = spriteY;
    }

    public void processAfflictionPackets(AfflictionPacket[] afflictionPackets) {
        for (AfflictionPacket afflictionPacket : afflictionPackets) {
            if (afflictionPacket.affliction == Affliction.FREEZE) {
                frozenEvent.start(afflictionPacket.duration);
            }
            if (afflictionPacket.affliction == Affliction.BURN) {
                burnEvent.start(afflictionPacket.duration);
            }
            if (afflictionPacket.affliction == Affliction.ACID) {
                acidEvent.start(afflictionPacket.duration);
            }
            if (afflictionPacket.affliction == Affliction.BLEED) {
                bleedEvent.start(afflictionPacket.duration);
            }
            if (afflictionPacket.affliction == Affliction.POISON) {
                poisonEvent.start(afflictionPacket.duration);
            }
        }
    }

    @Override
    public void tick(double t) {

        hitFlashEvent.tick(t);
        tickAfflictions(t);


        moveDirTimeout -= t;
        if (moveDirTimeout < 0) {
            moveDirTimeout = 0.3;
            calculateMoveDir();
        }

        boolean canMove = true;
        if (frozenEvent.isActive()) canMove = false;

        double backOrForward  = 1;

        if (pushBackEvent.isActive()) backOrForward = -1;

        if (canMove) {
            parent.getTransform().x += moveDir.x * speed * t* backOrForward;
            parent.getTransform().y += moveDir.y * speed * t* backOrForward;
        }

        double minDist = 15;
        double pushForce = 250;
        for (RelativeObject closeObject : closeObjects) {
            if (closeObject.distance > minDist) continue;
            Vector3 transform = parent.getTransform();
            double dx = closeObject.dx / closeObject.distance;
            double dy = closeObject.dy / closeObject.distance;
            transform.x += dx * t * pushForce;
            transform.y += dy * t * pushForce;
        }
        closeObjects.clear();

        if (health < 0) {
            CollisionSystem collisionSystem = getObjectByTypeFromParentContext(CollisionSystem.class);
            collisionSystem.removeColliderFromGameObject(parent);
            parent.destroy();


            gameLogic.addToScore(100);

            if (gameLogic.addEnemyXpAndShouldDropCrystal(10)) // * playerCapabilities.getLuckMultiplier()) {
            {
                EntityFactory.addCrystal(parent.getContext(), collisionSystem, (int) parent.getTransform().x, (int) parent.getTransform().y);
            }

            for (int i = 0; i < 5; i++) {
                particleFactory.createParticle(particleFactory.lightSmoke, parent.getTransform());
            }
        }

        rollAngle += t * speed;

        collider.setCollisionRegion(-6, -8, 12, 16);

    }

    public void tickAfflictions(double t) {
        frozenEvent.tick(t);
        pushBackEvent.tick(t);
        burnEvent.tick(t);
        poisonEvent.tick(t);
        acidEvent.tick(t);
        bleedEvent.tick(t);

        boolean drawParticle = false;
        if (Math.random() < 0.2) drawParticle = true;

        if (frozenEvent.isActive()) {
            health -= t * 2;
            if (drawParticle) particleFactory.createParticle(particleFactory.wandTrail, parent.getTransform());
        }
        if (burnEvent.isActive()) {
            health -= t * 2;
            if (drawParticle) particleFactory.createParticle(particleFactory.flame, parent.getTransform());
        }
        if (poisonEvent.isActive()) {
            health -= t * 2;
            if (drawParticle) particleFactory.createParticle(particleFactory.wandTrail, parent.getTransform());
        }
        if (acidEvent.isActive()) {
            health -= t * 2;
            if (drawParticle) particleFactory.createParticle(particleFactory.acid, parent.getTransform());
        }
        if (bleedEvent.isActive()) {
            health -= t * 2;
            if (drawParticle) particleFactory.createParticle(particleFactory.blood, parent.getTransform());
        }

    }

    private void calculateMoveDir() {
        moveDir = player.getTransform().getDirectionTo(parent.getTransform());
    }


    @Override
    public void draw(Graphics g) {
        if (spriteHelper == null) return;

        int x = (int) parent.getTransform().x;
        int y = (int) parent.getTransform().y;

        double rotation = Math.sin(rollAngle) * 10;

        int color = 0xffffffff;
        if (hitFlashEvent.isActive()) color = 0xff0000ff;
        if (frozenEvent.isActive()) color = 0x0000ffff;

        spriteHelper.drawSpriteInMap(x, y, sprite[0], sprite[1], rotation, color);
    }
}
