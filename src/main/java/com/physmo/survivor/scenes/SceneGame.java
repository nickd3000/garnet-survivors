package com.physmo.survivor.scenes;

import com.physmo.garnet.ColorUtils;
import com.physmo.garnet.Garnet;
import com.physmo.garnet.drawablebatch.BlendMode;
import com.physmo.garnet.graphics.Graphics;
import com.physmo.garnet.graphics.Viewport;
import com.physmo.garnet.input.InputKeys;
import com.physmo.garnet.renderer.BatchRenderStats;
import com.physmo.garnet.structure.Array;
import com.physmo.garnet.structure.Rect;
import com.physmo.garnet.text.RegularFont;
import com.physmo.garnet.toolkit.GameObject;
import com.physmo.garnet.toolkit.particle.ParticleManager;
import com.physmo.garnet.toolkit.scene.Scene;
import com.physmo.garnet.toolkit.scene.SceneManager;
import com.physmo.garnet.toolkit.simplecollision.ColliderComponent;
import com.physmo.garnet.toolkit.simplecollision.CollisionSystem;
import com.physmo.garnet.toolkit.simplecollision.RelativeObject;
import com.physmo.survivor.Constants;
import com.physmo.survivor.EntityFactory;
import com.physmo.survivor.Message;
import com.physmo.survivor.Resources;
import com.physmo.survivor.components.EnemySpawner;
import com.physmo.survivor.components.GameLogic;
import com.physmo.survivor.components.Hud;
import com.physmo.survivor.components.LevelMap;
import com.physmo.survivor.components.Player;
import com.physmo.survivor.components.PlayerCapabilities;
import com.physmo.survivor.components.ParticleFactory;
import com.physmo.survivor.components.SpriteHelper;
import com.physmo.survivor.components.items.CombinedItemStats;
import com.physmo.survivor.components.weapons.Bow;
import com.physmo.survivor.components.weapons.IceStorm;
import com.physmo.survivor.components.weapons.ValueChange;
import com.physmo.survivor.components.weapons.WeaponStatType;

import java.util.Random;

public class SceneGame extends Scene {

    Random random = new Random();
    GameObject player;
    CollisionSystem collisionSystem;
    Garnet garnet;

    PlayerCapabilities componentPlayerCapabilities;
    CombinedItemStats combinedItemStats;

    boolean showCollision = false;
    boolean showRenderStatsOverlay = false;

    public SceneGame(String name) {
        super(name);
    }

    @Override
    public void init() {
        garnet = SceneManager.getSharedContext().getObjectByType(Garnet.class);

        initParticleManager();

        collisionSystem = new CollisionSystem("d");
        context.add(collisionSystem);
        collisionSystem.setCollisionDrawingCallback(collidable -> {
            if (showCollision) {
                garnet.getGraphics().setDrawOrder(Constants.DRAW_ORDER_DEBUG_OVERLAY);
                Rect rect = collidable.collisionGetRegion();
                float x = (float) rect.x;
                float y = (float) rect.y;
                float w = (float) rect.w;
                float h = (float) rect.h;

                garnet.getGraphics().setColor(ColorUtils.WHITE);
                garnet.getGraphics().drawRect(x, y, w, h);
            }
        });



        // A single game object can hold multiple unrelated systems as components.
        GameObject gameSystems = new GameObject("gameSystems");
        gameSystems.addComponent(new LevelMap());
        gameSystems.addComponent(new SpriteHelper());
        gameSystems.addComponent(new EnemySpawner());
        gameSystems.addComponent(new Hud());
        gameSystems.addComponent(new ParticleFactory());
        gameSystems.addComponent(new GameLogic());
        context.add(gameSystems);


        player = new GameObject("player").addComponent(new Player());
        player.addComponent(new Bow());
        //player.addComponent(new Wand());
        //player.addComponent(new GlaveGun());
        //player.addComponent(new FireWand());
        //player.addComponent(new DuplicatorCharm());
        //player.addComponent(new StrengthCharm());
        player.addComponent(new IceStorm());
        combinedItemStats = new CombinedItemStats();
        player.addComponent(combinedItemStats);
        componentPlayerCapabilities = new PlayerCapabilities();
        player.addComponent(componentPlayerCapabilities);
        ColliderComponent playerCollider = EntityFactory.addColliderToGameObject(collisionSystem, player);
        playerCollider.setCollisionGroup(Constants.COLLISION_GROUP_PLAYER);
        player.addTag(Constants.TAG_PLAYER);
        player.getTransform().set(150*16, 100*16, 0);
        context.add(player);

        int[] canvasSize = garnet.getDisplay().getCanvasSize();

        // Configure Viewports
        Viewport viewportOverlay = garnet.getGraphics().getViewportManager().getViewport(Constants.overlayViewportId);
        viewportOverlay.setWidth(canvasSize[0])
                .setHeight(canvasSize[1])
                .setWindowY(0)
                .setWindowX(0)
                .setClipActive(true)
                .setDrawDebugInfo(false)
                .setZoom(1.0);

        Viewport viewport1 = garnet.getGraphics().getViewportManager().getViewport(Constants.tileGridViewportId);
        viewport1.setWidth(canvasSize[0])
                .setHeight(canvasSize[1])
                .setWindowY(0)
                .setWindowX(0)
                .setClipActive(true)
                .setDrawDebugInfo(false)
                .setZoom(1.0);

        Viewport viewport2 = garnet.getGraphics().getViewportManager().getViewport(Constants.scorePanelViewportId);
        viewport2.setWidth(canvasSize[0])
                .setHeight(60)
                .setWindowY(0)
                .setWindowX(0)
                .setClipActive(true)
                .setDrawDebugInfo(false)
                .setZoom(1.0);

        garnet.getDebugDrawer().setVisible(false);


        // Setup collision group matrix.
        collisionSystem.setCollisionGroupMatrix(Constants.COLLISION_GROUP_ENEMY,Constants.COLLISION_GROUP_ENEMY,false);
        collisionSystem.setCollisionGroupMatrix(Constants.COLLISION_GROUP_PLAYER_WEAPON,Constants.COLLISION_GROUP_PLAYER_WEAPON,false);
        collisionSystem.setCollisionGroupMatrix(Constants.COLLISION_GROUP_ENEMY,Constants.COLLISION_GROUP_PLAYER_WEAPON,true);
        collisionSystem.setCollisionGroupMatrix(Constants.COLLISION_GROUP_PLAYER_WEAPON,Constants.COLLISION_GROUP_ENEMY,true);
        collisionSystem.setCollisionGroupMatrix(Constants.COLLISION_GROUP_PICKUP,Constants.COLLISION_GROUP_PICKUP,false);

        collisionSystem.setCollisionGroupMatrix(Constants.COLLISION_GROUP_PICKUP,Constants.COLLISION_GROUP_ENEMY,false);
        collisionSystem.setCollisionGroupMatrix(Constants.COLLISION_GROUP_ENEMY,Constants.COLLISION_GROUP_PICKUP,false);

        collisionSystem.setCollisionGroupMatrix(Constants.COLLISION_GROUP_PICKUP,Constants.COLLISION_GROUP_PLAYER,true);
        collisionSystem.setCollisionGroupMatrix(Constants.COLLISION_GROUP_PLAYER,Constants.COLLISION_GROUP_PICKUP,true);



    }

    Array<RelativeObject> nearestEnemies = new Array<>(100);
    Array<RelativeObject> nearestCrystals = new Array<>(100);
    @Override
    public void tick(double delta) {

        collisionSystem.processCloseObjects(Constants.COLLISION_GROUP_ENEMY, 20);

        nearestEnemies.clear();
        nearestCrystals.clear();

        collisionSystem.getNearestObjects(Constants.COLLISION_GROUP_ENEMY, (int) player.getTransform().x, (int) player.getTransform().y, 120, nearestEnemies);
        player.getComponent(Player.class).setNearestEnemies(nearestEnemies);

        ValueChange weaponModifierValueChange = combinedItemStats.getWeaponModifierValueChange(WeaponStatType.PICKUP_RADIUS);
        double pickupRadius = weaponModifierValueChange.adjustDoubleByValue(32);
        collisionSystem.getNearestObjects(Constants.COLLISION_GROUP_PICKUP, (int) player.getTransform().x, (int) player.getTransform().y, pickupRadius, nearestCrystals);
        player.getComponent(Player.class).setNearestCrystals(nearestCrystals);

        garnet.getDebugDrawer().setUserString("collisions", String.valueOf(collisionSystem.getTestsPerFrame()));

        // Handle pause
        if (garnet.getInput().getKeyboard().isKeyFirstPress(InputKeys.KEY_P)) {
            SceneManager.getSceneByName("pause").ifPresent(scene -> ((ScenePause) scene).setPlayerCapabilities(componentPlayerCapabilities));
            SceneManager.getSceneByName("pause").ifPresent(scene -> ((ScenePause) scene).setCombinedItemStats(combinedItemStats));
            SceneManager.pushSubScene("pause");
        }

        // Show level up screen
        if (garnet.getInput().getKeyboard().isKeyFirstPress(InputKeys.KEY_L)) {
            GameLogic gameLogic = context.getComponent(GameLogic.class);
            gameLogic.showLevelUpScreen();
        }

        // Skip stage
        if (garnet.getInput().getKeyboard().isKeyFirstPress(InputKeys.KEY_W)) {
            context.broadcastMessage(Message.NEXT_WAVE);
        }

        if (garnet.getInput().getKeyboard().isKeyFirstPress(InputKeys.KEY_F3)) {
            showRenderStatsOverlay = !showRenderStatsOverlay;
        }

    }

    public void initParticleManager() {
        Resources resources = SceneManager.getSharedContext().getObjectByType(Resources.class);

        ParticleManager particleManager = new ParticleManager(5000);
        particleManager.setParticleDrawer(p -> {
            float pAge = (float) (p.age / p.lifeTime);
            garnet.getGraphics().setColor(p.colorSupplier.getColor(pAge));
            garnet.getGraphics().setDrawOrder(Constants.DRAW_ORDER_ABOVE_GROUND);
            garnet.getGraphics().drawImage(resources.getSpritesTilesheet(), (int) (p.position.x) - 8,
                    (int) (p.position.y) - 8, 2, 1)
                    .setBlendMode(BlendMode.ADDITIVE);
        });

        context.add(particleManager);
    }

    @Override
    public void draw(Graphics g) {
        if (!showRenderStatsOverlay) return;

        drawRenderStatsOverlay(g);
    }

    private void drawRenderStatsOverlay(Graphics g) {
        Resources resources = SceneManager.getSharedContext().getObjectByType(Resources.class);
        RegularFont font = resources.getRegularFont();
        BatchRenderStats stats = g.getRenderStats();

        int prevViewport = g.getViewportManager().getActiveViewportId();
        g.setActiveViewport(Constants.overlayViewportId);
        g.setDrawOrder(Constants.DRAW_ORDER_PERFORMANCE_OVERLAY);
        g.setColor(ColorUtils.rgb(0, 0, 0, 190));
        g.filledRect(230, 5, 148, 64);

        font.setScale(1);
        g.setColor(ColorUtils.WINTER_WHITE);
        font.drawText(g, "F3 Render Stats", 236, 10);
        font.drawText(g, "queued " + stats.getQueuedElements(), 236, 20);
        font.drawText(g, "runs   " + stats.getRenderRuns(), 236, 30);
        font.drawText(g, "draws  " + stats.getDrawCalls(), 236, 40);
        font.drawText(g, "binds  " + stats.getTextureBinds(), 236, 50);
        font.drawText(g, "uploads " + stats.getBufferUploads(), 236, 60);

        g.setActiveViewport(prevViewport);
    }

    @Override
    public void onMakeActive() {

    }

    @Override
    public void onMakeInactive() {

    }
}
