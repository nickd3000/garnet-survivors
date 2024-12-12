package com.physmo.survivor.scenes;

import com.physmo.garnet.Garnet;
import com.physmo.garnet.graphics.Graphics;
import com.physmo.garnet.input.InputAction;
import com.physmo.garnet.input.InputKeys;
import com.physmo.garnet.text.RegularFont;
import com.physmo.garnet.toolkit.Component;
import com.physmo.garnet.toolkit.GameObject;
import com.physmo.garnet.toolkit.scene.Scene;
import com.physmo.garnet.toolkit.scene.SceneManager;
import com.physmo.survivor.Constants;
import com.physmo.survivor.Resources;
import com.physmo.survivor.Upgradable;
import com.physmo.survivor.components.ComponentPlayerCapabilities;
import com.physmo.survivor.components.PlayerCapability;
import com.physmo.survivor.components.items.CooldownCharm;
import com.physmo.survivor.components.items.DuplicatorCharm;
import com.physmo.survivor.components.items.PierceCharm;
import com.physmo.survivor.components.items.SpeedCharm;
import com.physmo.survivor.components.items.StrengthCharm;
import com.physmo.survivor.components.weapons.Bow;
import com.physmo.survivor.components.weapons.FireWand;
import com.physmo.survivor.components.weapons.GlaveGun;
import com.physmo.survivor.components.weapons.Wand;
import com.physmo.survivor.components.weapons.Weapon;
import com.physmo.survivor.gamedata.GDWeapon;
import com.physmo.survivor.gamedata.GameData;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class SceneLevelUp extends Scene {

    Garnet garnet;
    Resources resources;
    RegularFont regularFont;
    ComponentPlayerCapabilities playerCapabilities;
    List<PlayerCapability> threeCapabilities = new ArrayList<>();
    int selectedRow = 0;

    GameObject player;
    List<Upgrade> upgrades = new ArrayList<>();
    List<Integer> upgradeIndexes = new ArrayList<>();

    public SceneLevelUp(String name) {
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

        if (garnet.getInput().getKeyboard().isKeyFirstPress(InputKeys.KEY_L)) {
            SceneManager.popSubScene("levelUp");
        }

        if (garnet.getInput().isActionKeyFirstPress(InputAction.UP)) {
            selectedRow--;
            if (selectedRow < 0) {
                selectedRow = upgrades.size();
            }
        }
        if (garnet.getInput().isActionKeyFirstPress(InputAction.DOWN)) {
            selectedRow++;
            if (selectedRow >= upgrades.size()) {
                selectedRow = 0;
            }
        }

        boolean confirm = garnet.getInput().isActionKeyFirstPress(InputAction.FIRE1);
        if (garnet.getInput().getKeyboard().isKeyFirstPress(InputKeys.KEY_ENTER)) confirm = true;
        if (confirm) {
            int upgradeIndex = upgradeIndexes.get(selectedRow);
            upgrades.get(upgradeIndex).action.run();
            SceneManager.popSubScene("levelUp");
        }
    }

    @Override
    public void onMakeInactive() {

    }

    @Override
    public void draw(Graphics g) {
        g.setDrawOrder(Constants.DRAW_ORDER_PAUSE_BACKGROUND);
        g.setActiveViewport(Constants.overlayViewportId);
        int[] bufferSize = garnet.getDisplay().getBufferSize();
        g.filledRect(10, 10, 200 * 2, 150);
        drawSelections(g);
    }

    public List<PlayerCapability> getThreeCapabilities() {
        List<PlayerCapability> selected = new ArrayList<>();
        while (selected.size() < 3) {
            PlayerCapability pc = getRandomCapability();
            if (!selected.contains(pc)) {
                selected.add(pc);
            }
        }
        return selected;
    }

    public PlayerCapability getRandomCapability() {
        return PlayerCapability.values()[(int) (Math.random() * PlayerCapability.values().length)];
    }

    public void drawSelections(Graphics g) {
        g.setColor(0x005500ff);

        regularFont.setScale(2);
        regularFont.drawText(g, "LEVEL UP", 100, 100);

        regularFont.setScale(1);


        for (int i = 0; i < upgradeIndexes.size(); i++) {
            int upgradeIndex = upgradeIndexes.get(i);
            if (selectedRow == i) {
                g.setColor(0xFF0000ff);
            } else {
                g.setColor(0x000000ff);
            }
            Upgrade upgrade = upgrades.get(upgradeIndex);
            drawCapability(g, upgrade.description, upgrade.subText, 30, 20 + i * 22);

        }

    }

    public void setPlayerCapabilities(ComponentPlayerCapabilities playerCapabilities) {

        this.playerCapabilities = playerCapabilities;

    }

    public void drawCapability(Graphics g, String description, String subText, int x, int y) {
        regularFont.drawText(g, description, x, y);
        regularFont.drawText(g, subText, x, y + 9);
    }

    @Override
    public void onMakeActive() {
        threeCapabilities = getThreeCapabilities();
        findUpgrades();
    }

    public void setPlayer(GameObject player) {
        this.player = player;
    }

    public String getUpgradeDescription(Component component, int level) {
        GameData gameData = resources.getGameData();

        if (component instanceof Weapon weapon) {
            System.out.println(weapon.getName());
            GDWeapon weaponByName = gameData.getWeaponByName(weapon.getDataName());
            return weaponByName.getLevels().get(level).getEffect();

        }

        return "unknown";
    }

    public void test() {
        Map<String, String> nick = new HashMap<>();
        Optional.ofNullable(nick.get("nick")).ifPresent(n -> nick.put("nick", n));
    }

    // Create a list of all available upgrades.
    public void findUpgrades() {
        Random rand = new Random();
        upgradeIndexes = new ArrayList<>();
        upgrades.clear();

        List<Component> playerComponents = player.getComponents();
        for (Component component : playerComponents) {
            if (component instanceof Upgradable upgradable) {
                int currentLevel = upgradable.getLevel();
                int maxLevel = upgradable.getMaxLevel();
                if (currentLevel < maxLevel) {
                    Upgrade upgrade = new Upgrade();
                    upgrade.action = upgradable::increaseLevel;
                    upgrade.description = "Upgrade " + upgradable.getName() + " " + currentLevel + " -> " + (currentLevel + 1);
                    upgrade.subText = getUpgradeDescription(component, upgradable.getLevel());
                    upgrades.add(upgrade);

                }

            }
        }


        try {
            addNewUpgrades(StrengthCharm.class, "Strength Charm");
            addNewUpgrades(DuplicatorCharm.class, "Duplicator Charm");

            addNewUpgrades(PierceCharm.class, "Pierce Charm");
            addNewUpgrades(SpeedCharm.class, "Speed Charm");
            addNewUpgrades(CooldownCharm.class, "Cooldown Charm");

            addNewUpgrades(GlaveGun.class, "Glave Gun");
            addNewUpgrades(Bow.class, "Bow");
            addNewUpgrades(FireWand.class, "Fire Wand");
            addNewUpgrades(Wand.class, "Magic Wand");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }


        // Chose some indexes randomly from the set of upgrades.
        int count = 0;
        while (upgradeIndexes.size() < 3 && count < 100) {
            count++;
            int selection = rand.nextInt(upgrades.size());
            if (!upgradeIndexes.contains(selection)) {
                upgradeIndexes.add(selection);
            }
        }
    }


    public void addNewUpgrades(Class<?> clazz, String name) throws NoSuchMethodException {

        if (!doesPlayerHaveComponent(clazz)) {
            Upgrade upgrade = new Upgrade();

            Constructor<?> cons = clazz.getDeclaredConstructor();
            cons.setAccessible(true);

            upgrade.action = () -> {
                try {
                    Component component = (Component) cons.newInstance();
                    player.addComponent(component);
                    component.init();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            };
            upgrade.description = "*NEW* " + name;
            upgrades.add(upgrade);

        }
    }

    public boolean doesPlayerHaveComponent(Class<?> clazz) {
        List<Component> playerComponents = player.getComponents();

        for (Component playerComponent : playerComponents) {
            if (playerComponent.getClass().equals(clazz)) {
                return true;
            }
        }

        return false;
    }
}


class Upgrade {
    String description;
    String subText;
    Runnable action;
}