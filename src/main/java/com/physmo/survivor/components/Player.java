package com.physmo.survivor.components;

import com.physmo.garnet.Garnet;
import com.physmo.garnet.graphics.Graphics;
import com.physmo.garnet.input.InputAction;
import com.physmo.garnet.toolkit.Component;
import com.physmo.garnet.toolkit.scene.SceneManager;
import com.physmo.garnet.toolkit.simplecollision.ColliderComponent;
import com.physmo.garnet.toolkit.simplecollision.CollisionSystem;
import com.physmo.garnet.toolkit.simplecollision.RelativeObject;
import com.physmo.survivor.Constants;

import java.util.ArrayList;
import java.util.List;

public class Player extends Component {

    Garnet garnet;

    List<RelativeObject> nearestEnemies = new ArrayList<>();
    List<RelativeObject> nearestCrystals = new ArrayList<>();
    SpriteHelper spriteHelper;
    CollisionSystem collisionSystem;
    ColliderComponent collider;
    PlayerCapabilities playerCapabilities;
    GameLogic gameLogic;

    public List<RelativeObject> getNearestEnemies() {
        return nearestEnemies;
    }

    public void setNearestEnemies(List<RelativeObject> list) {
        this.nearestEnemies = list;
    }

    public void setNearestCrystals(List<RelativeObject> list) {
        this.nearestCrystals = list;
    }

    @Override
    public void tick(double t) {
        double speed = 40;

        if (garnet.getInput().isActionKeyPressed(InputAction.RIGHT)) {
            parent.getTransform().x += speed * t;
        }
        if (garnet.getInput().isActionKeyPressed(InputAction.LEFT)) {
            parent.getTransform().x -= speed * t;
        }
        if (garnet.getInput().isActionKeyPressed(InputAction.UP)) {
            parent.getTransform().y -= speed * t;
        }
        if (garnet.getInput().isActionKeyPressed(InputAction.DOWN)) {
            parent.getTransform().y += speed * t;
        }

        if (nearestCrystals != null) {
//            for (RelativeObject nearestCrystal : nearestCrystals) {
//                Vector3 transform = nearestCrystal.otherObject.collisionGetGameObject().getTransform();
//                transform.x -= nearestCrystal.dx * 70.1 * t;
//                transform.y -= nearestCrystal.dy * 70.1 * t;
//            }
            for (RelativeObject nearestCrystal : nearestCrystals) {
                Crystal component = nearestCrystal.getOtherObject().collisionGetGameObject().getComponent(Crystal.class);
                component.setHoming(true);
            }
        }

        garnet.getDebugDrawer().setUserString("d1 ", "");
        garnet.getDebugDrawer().setUserString("d2 ", "");
        garnet.getDebugDrawer().setUserString("d3 ", "");
        garnet.getDebugDrawer().setUserString("Player ", (int) parent.getTransform().x + ", " + (int) parent.getTransform().x);

        collider.setCollisionRegion(-8, -8, 14, 14);
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
                gameLogic.increaseXp(1);
            }
        });




    }

    @Override
    public void draw(Graphics g) {
        int x = (int) parent.getTransform().x;
        int y = (int) parent.getTransform().y;

        spriteHelper.drawSpriteInMap(x - 8, y - 8, 2, 0);
    }


}
