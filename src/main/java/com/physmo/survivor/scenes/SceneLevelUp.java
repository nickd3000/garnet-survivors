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
import com.physmo.survivor.components.PlayerCapabilities;
import com.physmo.survivor.components.PlayerCapability;
import com.physmo.survivor.components.items.CooldownCharm;
import com.physmo.survivor.components.items.DuplicatorCharm;
import com.physmo.survivor.components.items.PierceCharm;
import com.physmo.survivor.components.items.SizeCharm;
import com.physmo.survivor.components.items.SpeedCharm;
import com.physmo.survivor.components.items.StrengthCharm;
import com.physmo.survivor.components.items.VortexCharm;
import com.physmo.survivor.components.weapons.AcidStorm;
import com.physmo.survivor.components.weapons.Bow;
import com.physmo.survivor.components.weapons.FireWand;
import com.physmo.survivor.components.weapons.GlaveGun;
import com.physmo.survivor.components.weapons.IceBow;
import com.physmo.survivor.components.weapons.IceStorm;
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
    PlayerCapabilities playerCapabilities;
    List<PlayerCapability> threeCapabilities = new ArrayList<>();
    int selectedRow = 0;

    GameObject player;
    List<Upgrade> upgrades = new ArrayList<>();
    List<Integer> upgradeIndexes = new ArrayList<>();

    int[] windowSize = new int[2];
    int[] windowMargin = new int[2];

    public SceneLevelUp(String name) {
        super(name);
    }
    double timer=0;

    @Override
    public void init() {
        garnet = SceneManager.getSharedContext().getObjectByType(Garnet.class);
        resources = SceneManager.getSharedContext().getObjectByType(Resources.class);

        regularFont = resources.getRegularFont();

        windowSize[0] = (int) (garnet.getDisplay().getCanvasSize()[0] * 0.9);
        windowSize[1] = (int) (garnet.getDisplay().getCanvasSize()[1] * 0.9);
        windowMargin[0] = (garnet.getDisplay().getCanvasSize()[0] - windowSize[0]) / 2;
        windowMargin[1] = (garnet.getDisplay().getCanvasSize()[1] - windowSize[1]) / 2;

        System.out.println(windowSize[0] + " " + windowSize[1] + windowMargin[0] + " " + windowMargin[1]);
    }

    @Override
    public void tick(double delta) {
        timer+=delta;

        if (garnet.getInput().getKeyboard().isKeyFirstPress(InputKeys.KEY_L)) {
            SceneManager.popSubScene("levelUp");
        }

        if (garnet.getInput().isActionKeyFirstPress(InputAction.UP)) {
            selectedRow--;
            if (selectedRow < 0) {
                selectedRow = upgradeIndexes.size()-1;
            }
        }
        if (garnet.getInput().isActionKeyFirstPress(InputAction.DOWN)) {
            selectedRow++;
            if (selectedRow >= upgradeIndexes.size()) {
                selectedRow = 0;
            }
        }

        boolean confirm = garnet.getInput().isActionKeyFirstPress(InputAction.FIRE1);
        if (garnet.getInput().getKeyboard().isKeyFirstPress(InputKeys.KEY_ENTER)) confirm = true;
        if (confirm || timer>10) {
            timer=0;
            int upgradeIndex = upgradeIndexes.get(selectedRow);
            upgrades.get(upgradeIndex).action.run();
            SceneManager.popSubScene("levelUp");
        }
    }

    @Override
    public void draw(Graphics g) {
        g.setDrawOrder(Constants.DRAW_ORDER_PAUSE_BACKGROUND);
        g.setActiveViewport(Constants.overlayViewportId);
        g.setColor(0xffffffaa);
        g.filledRect(windowMargin[0], windowMargin[1], windowSize[0], windowSize[1]);
        g.setDrawOrder(Constants.DRAW_ORDER_PAUSE_FOREGROUND);
        drawSelections(g);
    }

    public void drawSelections(Graphics g) {
        g.setColor(0x005500ff);

        regularFont.setScale(2);
        regularFont.drawText(g, "LEVEL UP", 100, windowMargin[1] + 15);

        regularFont.setScale(1);


        for (int i = 0; i < upgradeIndexes.size(); i++) {
            int upgradeIndex = upgradeIndexes.get(i);
            if (selectedRow == i) {
                g.setColor(0xFF0000ff);
            } else {
                g.setColor(0x000000ff);
            }
            Upgrade upgrade = upgrades.get(upgradeIndex);
            drawCapability(g, upgrade.description, upgrade.subText, 30, windowMargin[1] + 20 + 30 + i * 22);

        }

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

    @Override
    public void onMakeInactive() {

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
            addNewUpgrades(VortexCharm.class, "Vortex Charm");
            addNewUpgrades(SizeCharm.class, "Size Charm");

            addNewUpgrades(GlaveGun.class, "Glave Gun");
            addNewUpgrades(Bow.class, "Bow");
            addNewUpgrades(FireWand.class, "Fire Wand");
            addNewUpgrades(Wand.class, "Magic Wand");
            addNewUpgrades(IceBow.class, "Ice Bow");
            addNewUpgrades(AcidStorm.class, "Acid Storm");
            addNewUpgrades(IceStorm.class, "Ice Storm");
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

    public PlayerCapability getRandomCapability() {
        return PlayerCapability.values()[(int) (Math.random() * PlayerCapability.values().length)];
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

    public void setPlayerCapabilities(PlayerCapabilities playerCapabilities) {

        this.playerCapabilities = playerCapabilities;

    }

    public void setPlayer(GameObject player) {
        this.player = player;
    }

    public void test() {
        Map<String, String> nick = new HashMap<>();
        Optional.ofNullable(nick.get("nick")).ifPresent(n -> nick.put("nick", n));
    }
}


class Upgrade {
    String description;
    String subText;
    Runnable action;
}