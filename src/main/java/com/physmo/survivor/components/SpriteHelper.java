package com.physmo.survivor.components;

import com.physmo.garnet.Garnet;
import com.physmo.garnet.graphics.Graphics;
import com.physmo.garnet.toolkit.Component;
import com.physmo.garnet.toolkit.scene.SceneManager;
import com.physmo.survivor.Constants;
import com.physmo.survivor.Resources;

public class SpriteHelper extends Component {
    Resources resources;
    Graphics g;
    Garnet garnet;

    LevelMap levelMap;

    int spriteColor = 0xffffffff;

    public void drawSpriteInMap(double x, double y, double tileX, double tileY) {
        g.setActiveViewport(Constants.tileGridViewportId);
        g.setColor(spriteColor);
        g.drawImage(resources.getSpritesTilesheet(), x, y, (int) tileX, (int) tileY);
    }

    public void drawSpriteInMap(int x, int y, int tileX, int tileY, double angle) {
        g.setActiveViewport(Constants.tileGridViewportId);
        g.setColor(spriteColor);
        g.drawImage(resources.getSpritesTilesheet(), x, y, tileX, tileY, angle);
    }

    public void drawSpriteInMap(int x, int y, int tileX, int tileY, double angle, int col) {
        g.setActiveViewport(Constants.tileGridViewportId);
        g.setColor(col);
        g.setDrawOrder(Constants.DRAW_ORDER_ABOVE_GROUND);
        g.drawImage(resources.getSpritesTilesheet(), x, y, tileX, tileY, angle);
    }

    @Override
    public void init() {
        resources = SceneManager.getSharedContext().getObjectByType(Resources.class);

        garnet = SceneManager.getSharedContext().getObjectByType(Garnet.class);
        g = garnet.getGraphics();

        levelMap = getComponentFromParentContext(LevelMap.class);
    }

    @Override
    public void tick(double t) {

    }

    @Override
    public void draw(Graphics g) {

    }
}
