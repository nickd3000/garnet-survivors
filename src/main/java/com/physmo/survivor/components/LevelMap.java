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

        double zoom = viewport.getZoom();
        Vector3 playerPos = player.getTransform();
        double scrollX = viewport.getX();
        double scrollY = viewport.getY();
        double dx = playerPos.x - (scrollX + ((double) (canvasWidth) / 2)) + 8;
        double dy = playerPos.y - (scrollY + ((double) (canvasHeight) / 2)) + 8;

        double speed = 5.0 * t;
        viewport.scroll(dx * speed, dy * speed);

    }

    @Override
    public void draw(Graphics g) {

        g.setColor(0xffffff);
        g.setDrawOrder(Constants.DRAW_ORDER_GROUND);
        tileGridDrawer.draw(graphics, 20, 20);

    }

    public Rect getVisibleMapExtents() {
        double[] r = viewport.getVisibleRect();
        Rect rect = new Rect(r[0], r[1], r[2], r[3]);


        return rect;
    }
}
