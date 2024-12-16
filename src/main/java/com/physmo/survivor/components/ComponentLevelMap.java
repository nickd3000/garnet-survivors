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

public class ComponentLevelMap extends Component {

    TileGridData tileGridData;
    TileGridDrawer tileGridDrawer;
    int mapWidth = 100;
    int mapHeight = 100;
    Resources resources;
    Graphics graphics;
    Garnet garnet;
    GameObject player;

    int windowWidth = 384 * 2;
    int windowHeight = 216 * 2;

    Viewport viewport;

    public ComponentLevelMap() {


    }

    public TileGridDrawer getTileGridDrawer() {
        return tileGridDrawer;
    }

    @Override
    public void init() {


        garnet = SceneManager.getSharedContext().getObjectByType(Garnet.class);
        graphics = garnet.getGraphics();

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
        double dx = playerPos.x - (scrollX + ((windowWidth / zoom) / 2)) + 8;
        double dy = playerPos.y - (scrollY + ((windowHeight / zoom) / 2)) + 8;
        double speed = 5.0 * t;
        viewport.scroll(dx * speed, dy * speed);
//        scrollX += dx * speed;
//        scrollY += dy * speed;

    }

    @Override
    public void draw(Graphics g) {
        //tileGridDrawer.setScale(2);
        //tileGridDrawer.setScroll(scrollX, scrollY);
        g.setColor(0xffffff);
        g.setDrawOrder(Constants.DRAW_ORDER_GROUND);
        tileGridDrawer.draw(graphics, 20, 20);

//        Rect visibleMapExtents = getVisibleMapExtents();
//        graphics.drawRect((float) visibleMapExtents.x, (float) visibleMapExtents.y,400,400);
    }

    public Rect getVisibleMapExtents() {
        double[] r = viewport.getVisibleRect();
        Rect rect = new Rect(r[0], r[1], r[2], r[3]);

//        double[] scrollPosition = tileGridDrawer.getScrollPosition();
//        rect.x = scrollPosition[0];
//        rect.y = scrollPosition[1];
//
//        int[] windowSizeInTiles = tileGridDrawer.getWindowSizeInTiles();
//        rect.w = (windowSizeInTiles[0] * 16);
//        rect.h = (windowSizeInTiles[1] * 16);
//

        return rect;
    }
}
