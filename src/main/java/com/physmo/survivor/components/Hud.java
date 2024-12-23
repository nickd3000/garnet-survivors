package com.physmo.survivor.components;

import com.physmo.garnet.ColorUtils;
import com.physmo.garnet.Garnet;
import com.physmo.garnet.graphics.Graphics;
import com.physmo.garnet.text.RegularFont;
import com.physmo.garnet.toolkit.Component;
import com.physmo.garnet.toolkit.scene.SceneManager;
import com.physmo.survivor.Constants;
import com.physmo.survivor.Resources;

public class Hud extends Component {
    Resources resources;
    Graphics g;
    Garnet garnet;
    Player player;
    GameLogic gameLogic;

    double hpBarSpeed = 5;

    int trackedScore = 0;
    double trackedXp = 0;

    @Override
    public void init() {
        resources = SceneManager.getSharedContext().getObjectByType(Resources.class);
        garnet = SceneManager.getSharedContext().getObjectByType(Garnet.class);
        g = garnet.getGraphics();
        player = getComponentFromParentContext(Player.class);
        gameLogic = getComponentFromParentContext(GameLogic.class);
    }

    @Override
    public void tick(double t) {
        int actualScore = gameLogic.getCurrentScore();
        if (trackedScore < actualScore) trackedScore += 1;

        int xp = gameLogic.xp;
        trackedXp = xp;

    }

    @Override
    public void draw(Graphics g) {
        g.setActiveViewport(Constants.scorePanelViewportId);
        g.setDrawOrder(Constants.DRAW_ORDER_HUD);
        RegularFont regularFont = resources.getRegularFont();
        regularFont.setScale(1);
        g.setColor(ColorUtils.YELLOW);
        regularFont.drawText(g, "0" + trackedScore, 3, 3);

        int playerLevel = gameLogic.getPlayerLevel();
        regularFont.drawText(g, "Level " + playerLevel, 3 + 100, 3);

        //int xp = gameLogic.xp;
        int targetXp = gameLogic.getXpToLevelUp();
//        regularFont.drawText(g, "xp " + xp + "/" + targetXp, 3 + 200, 3);

        drawXpBar(g,10,20,trackedXp,targetXp);
        drawClock(g);
    }

    public void drawClock(Graphics g) {
        double gameTime = gameLogic.getGameTime();
        int seconds = ((int) gameTime )%60;
        int minutes = ((int) gameTime ) / 60;
        String timeString = String.format("%02d:%02d", minutes, seconds);
        resources.getRegularFont().drawText(g, timeString, 320, 3);

    }

    public void drawXpBar(Graphics g, int x, int y, double xp, double targetXp) {

        int barWidth = 340;
        int h = 2;
        int n = (int)((xp/targetXp)*barWidth);

        g.setColor(ColorUtils.DARK_GREY);
        g.filledRect(x-1,y-1,barWidth+2,5+2);
        g.setColor(ColorUtils.SUNSET_YELLOW);
        g.filledRect(x,y,n,5);
    }
}
