package com.physmo.survivor.scenes;

import com.physmo.garnet.Garnet;
import com.physmo.garnet.graphics.Graphics;
import com.physmo.garnet.input.InputAction;
import com.physmo.garnet.input.InputKeys;
import com.physmo.garnet.text.RegularFont;
import com.physmo.garnet.toolkit.scene.Scene;
import com.physmo.garnet.toolkit.scene.SceneManager;
import com.physmo.survivor.Constants;
import com.physmo.survivor.Resources;
import com.physmo.survivor.components.PlayerCapabilities;
import com.physmo.survivor.components.items.CombinedItemStats;
import com.physmo.survivor.components.weapons.ValueChange;
import com.physmo.survivor.components.weapons.WeaponStatType;


public class ScenePause extends Scene {

    Garnet garnet;
    Resources resources;
    RegularFont regularFont;
    PlayerCapabilities playerCapabilities;
    CombinedItemStats combinedItemStats;

    public ScenePause(String name) {
        super(name);
    }

    @Override
    public void init() {
        garnet = SceneManager.getSharedContext().getObjectByType(Garnet.class);
        resources = SceneManager.getSharedContext().getObjectByType(Resources.class);

        regularFont = resources.getRegularFont();
    }

    @Override
    public void tick(double delta) {
        // Handle pause
        if (garnet.getInput().isActionKeyPressed(InputAction.MENU)) {
            //ScenePause scenePause = SceneManager.getSharedContext().getObjectByType(ScenePause.class);
            SceneManager.popSubScene("pause");

        }

        if (garnet.getInput().getKeyboard().isKeyFirstPress(InputKeys.KEY_P)) {
            SceneManager.popSubScene("pause");
        }
    }

    @Override
    public void draw(Graphics g) {
        g.setDrawOrder(Constants.DRAW_ORDER_PAUSE_BACKGROUND);
        g.setActiveViewport(Constants.overlayViewportId);
        int[] bufferSize = garnet.getDisplay().getBufferSize();
        g.filledRect(10, 10, 350, 200);

        drawPlayerDetails(g);
    }

    @Override
    public void onMakeActive() {

    }

    @Override
    public void onMakeInactive() {

    }

    public void drawPlayerDetails(Graphics g) {
        g.setColor(0x005500ff);

        drawCapability(g, WeaponStatType.PICKUP_RADIUS, "Pickup Radius", 30, 50);
        drawCapability(g, WeaponStatType.COUNT, "Projectile count", 30, 65);

        drawCapability(g, WeaponStatType.DAMAGE, "Projectile Damage", 30, 80);
        drawCapability(g, WeaponStatType.COOLDOWN, "Cooldown", 30, 95);
        drawCapability(g, WeaponStatType.DURATION, "Duration", 30, 110);
    }

    public void drawCapability(Graphics g, WeaponStatType id, String description, int x, int y) {
        ValueChange weaponModifierValueChange = combinedItemStats.getWeaponModifierValueChange(id);
        String text = "";

        if (weaponModifierValueChange.type == ValueChange.TYPE_PERCENTAGE) {
            text += description + (weaponModifierValueChange.value > 0 ? " +" : " ") + weaponModifierValueChange.value + "%";
        } else if (weaponModifierValueChange.type == ValueChange.TYPE_WHOLE_NUMBER) {
            text += description + " +" + (int) weaponModifierValueChange.value;
        }
        regularFont.drawText(g, text, x, y);

    }

    public void setPlayerCapabilities(PlayerCapabilities playerCapabilities) {

        this.playerCapabilities = playerCapabilities;

    }

    public void setCombinedItemStats(CombinedItemStats combinedItemStats) {
        this.combinedItemStats = combinedItemStats;
    }

}
