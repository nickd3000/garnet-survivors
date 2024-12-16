package com.physmo.survivor.components;

import com.physmo.garnet.ColorUtils;
import com.physmo.garnet.Garnet;
import com.physmo.garnet.graphics.Graphics;
import com.physmo.garnet.text.RegularFont;
import com.physmo.garnet.toolkit.Component;
import com.physmo.garnet.toolkit.scene.SceneManager;
import com.physmo.survivor.Constants;
import com.physmo.survivor.Resources;

public class ComponentHud extends Component {
    Resources resources;
    Graphics g;
    Garnet garnet;
    ComponentPlayer player;
    ComponentGameLogic gameLogic;

    double hpBarSpeed = 5;

    int trackedScore = 0;
    double trackedXp = 0;

    @Override
    public void init() {
        resources = SceneManager.getSharedContext().getObjectByType(Resources.class);
        garnet = SceneManager.getSharedContext().getObjectByType(Garnet.class);
        g = garnet.getGraphics();
        player = getComponentFromParentContext(ComponentPlayer.class);
        gameLogic = getComponentFromParentContext(ComponentGameLogic.class);
    }

    @Override
    public void tick(double t) {
        int actualScore = gameLogic.getCurrentScore();
        if (trackedScore < actualScore) trackedScore += 1;

        int xp = gameLogic.xp;
        if (trackedXp>xp) trackedXp = xp;
        else if (trackedXp!=xp) {
            trackedXp+=t*hpBarSpeed;
        }
    }

    @Override
    public void draw(Graphics g) {
        g.setActiveViewport(Constants.scorePanelViewportId);
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
