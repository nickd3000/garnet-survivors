package com.physmo.survivor.components;

import com.physmo.garnet.Garnet;
import com.physmo.garnet.graphics.Graphics;
import com.physmo.garnet.input.InputAction;
import com.physmo.garnet.structure.Array;
import com.physmo.garnet.toolkit.Component;
import com.physmo.garnet.toolkit.scene.SceneManager;
import com.physmo.garnet.toolkit.simplecollision.ColliderComponent;
import com.physmo.garnet.toolkit.simplecollision.CollisionSystem;
import com.physmo.garnet.toolkit.simplecollision.RelativeObject;
import com.physmo.garnet.toolkit.tick.TickSequence;
import com.physmo.survivor.Constants;
import com.physmo.survivor.Message;



import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Player extends Component {

    Garnet garnet;

    Array<RelativeObject> nearestEnemies = new Array<>(100);
    Array<RelativeObject> nearestCrystals = new Array<>(100);
    SpriteHelper spriteHelper;
    CollisionSystem collisionSystem;
    ColliderComponent collider;
    PlayerCapabilities playerCapabilities;
    GameLogic gameLogic;
    AtomicInteger moveDir = new AtomicInteger(0);
    TickSequence moveSequence;


    public Array<RelativeObject> getNearestEnemies() {
        return nearestEnemies;
    }

    public void setNearestEnemies(Array<RelativeObject> list) {
        this.nearestEnemies = list;
    }

    public void setNearestCrystals(Array<RelativeObject> list) {
        this.nearestCrystals = list;
    }

    @Override
    public void init() {
        spriteHelper = getComponentFromParentContext(SpriteHelper.class);
        garnet = SceneManager.getSharedContext().getObjectByType(Garnet.class);
        collisionSystem = getObjectByTypeFromParentContext(CollisionSystem.class);
        playerCapabilities = getComponentFromParentContext(PlayerCapabilities.class);
        gameLogic = getComponentFromParentContext(GameLogic.class);

        parent.addTag(Constants.TAG_PLAYER);

        collider = parent.getComponent(ColliderComponent.class);

        collider.setCallbackEnter(target -> {
            if (target.hasTag(Constants.TAG_CRYSTAL)) {
                target.getComponent(Crystal.class).requestKill();
                parent.getContext().broadcastMessage(Message.XP_INCREASE, 1);
            }
        });



        moveSequence = new TickSequence().then(() -> moveDir.set(1))
                .waitFor(3.5).then(() -> moveDir.set(4))
                .waitFor(3).then(() -> moveDir.set(2))
                .waitFor(3).then(() -> moveDir.set(3)).waitFor(3.5).loop();
        moveSequence.start();
    }

    @Override
    public void tick(double t) {
        double speed = 40;
        moveSequence.tick(t);

        if (garnet.getInput().isActionKeyPressed(InputAction.RIGHT) || moveDir.get() == 1) {
            parent.getTransform().x += speed * t;
        }
        if (garnet.getInput().isActionKeyPressed(InputAction.LEFT) || moveDir.get() == 2) {
            parent.getTransform().x -= speed * t;
        }
        if (garnet.getInput().isActionKeyPressed(InputAction.UP) || moveDir.get() == 3) {
            parent.getTransform().y -= speed * t;
        }
        if (garnet.getInput().isActionKeyPressed(InputAction.DOWN) || moveDir.get() == 4) {
            parent.getTransform().y += speed * t;
        }

        if (nearestCrystals != null) {
            for (RelativeObject nearestCrystal : nearestCrystals) {
                nearestCrystal.getOtherObject().collisionGetGameObject().sendMessage(Message.HOMING_REQUEST, null);
            }
        }

        garnet.getDebugDrawer().setUserString("d1 ", "");
        garnet.getDebugDrawer().setUserString("d2 ", "");
        garnet.getDebugDrawer().setUserString("d3 ", "");
        garnet.getDebugDrawer().setUserString("Player ", (int) parent.getTransform().x + ", " + (int) parent.getTransform().x);

        collider.setCollisionRegion(-8, -8, 14, 14);
    }



    @Override
    public void draw(Graphics g) {
        int x = (int) parent.getTransform().x;
        int y = (int) parent.getTransform().y;

        spriteHelper.drawSpriteInMap(x - 8, y - 8, 2, 0);

        nearestEnemies.forEach(e -> {
            spriteHelper.drawSpriteInMap(e.originX, e.originY, 2, 0);

        });
    }


}
