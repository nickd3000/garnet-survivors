package com.physmo.survivor.components;

import com.physmo.garnet.Garnet;
import com.physmo.garnet.graphics.Graphics;
import com.physmo.garnet.graphics.Viewport;
import com.physmo.garnet.structure.Rect;
import com.physmo.garnet.structure.Vector3;
import com.physmo.garnet.tilegrid.TileGridData;
import com.physmo.garnet.tilegrid.TileGridDrawer;
import com.physmo.garnet.toolkit.Component;
import com.physmo.garnet.toolkit.GameObject;
import com.physmo.garnet.toolkit.scene.SceneManager;
import com.physmo.survivor.Constants;
import com.physmo.survivor.Resources;

public class LevelMap extends Component {

    TileGridData tileGridData;
    TileGridDrawer tileGridDrawer;
    int mapWidth = 300;
    int mapHeight = 200;
    Resources resources;
    Graphics graphics;
    Garnet garnet;
    GameObject player;

    int canvasWidth = 384;
    int canvasHeight = 216;

    Viewport viewport;
    double scrollX = 0;
    double scrollY = 0;

    public LevelMap() {


    }

    public TileGridDrawer getTileGridDrawer() {
        return tileGridDrawer;
    }

    @Override
    public void init() {



        garnet = SceneManager.getSharedContext().getObjectByType(Garnet.class);
        graphics = garnet.getGraphics();

        canvasWidth = garnet.getDisplay().getCanvasSize()[0];
        canvasHeight = garnet.getDisplay().getCanvasSize()[1];

        resources = SceneManager.getSharedContext().getObjectByType(Resources.class);

        tileGridData = new TileGridData(mapWidth, mapHeight);
        tileGridDrawer = new TileGridDrawer().setData(tileGridData)
                //.setWindowSize(windowWidth, windowHeight)
                .setTileSize(16, 16).setTileSheet(resources.getSpritesTilesheet())
                //.setScale((int) scale)
                .setViewportId(Constants.tileGridViewportId);

        int grass = resources.getSpritesTilesheet().getTileIndexFromCoords(0, 1);
        int flower = resources.getSpritesTilesheet().getTileIndexFromCoords(1, 1);
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                if (Math.random() < 0.98) tileGridData.setTileId(x, y, grass);
                else tileGridData.setTileId(x, y, flower);
            }
        }

        viewport = garnet.getGraphics().getViewportManager().getViewport(Constants.tileGridViewportId);

        player = getObjectByTagFromParentContext(Constants.TAG_PLAYER);
    }

    @Override
    public void tick(double t) {

        Vector3 playerPosition = player.getTransform();
        double dx = playerPosition.x - (scrollX + ((double) (canvasWidth) / 2)) + 8;
        double dy = playerPosition.y - (scrollY + ((double) (canvasHeight) / 2)) + 8;

        // Snap scroll to player pos if too far to scroll.
        if (Math.abs(dx) > 50 || Math.abs(dy) > 50) {
            scrollX = playerPosition.x - (((double) (canvasWidth) / 2) + 8);
            scrollY = playerPosition.y - (((double) (canvasHeight) / 2) + 8);
        }

        double speed = 5.0 * t;
        scrollX += dx * speed;
        scrollY += dy * speed;

        viewport.setX((int) scrollX);
        viewport.setY((int) scrollY);

    }

    @Override
    public void draw(Graphics g) {
        // Use opaque white. Color literals in this codebase are RRGGBBAA; 0xffffff would be fully transparent.
        g.setColor(0xffffffff);
        g.setDrawOrder(Constants.DRAW_ORDER_GROUND);
        // IMPORTANT: use the Graphics instance passed into draw(), which is bound
        // to the current render target (e.g., the CRT RenderTexture in CellSurvivor).
        // Using a cached Graphics field here can lead to drawing to the wrong target
        // or no visible output when rendering via FBOs.
        tileGridDrawer.draw(g, 20, 20);

    }

    public Rect getVisibleMapExtents() {
        double[] r = viewport.getVisibleRect();
        Rect rect = new Rect(r[0], r[1], r[2], r[3]);


        return rect;
    }
}
