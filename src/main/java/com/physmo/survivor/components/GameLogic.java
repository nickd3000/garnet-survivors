package com.physmo.survivor.components;

import com.physmo.garnet.Garnet;
import com.physmo.garnet.graphics.Graphics;
import com.physmo.garnet.toolkit.Component;
import com.physmo.garnet.toolkit.scene.SceneManager;
import com.physmo.survivor.scenes.SceneLevelUp;

public class GameLogic extends Component {

    public int playerLevel = 1;
    public int xp = 0;
    int currentScore = 0;
    PlayerCapabilities playerCapabilities;
    double waveDuration = 30; //60 * 3;
    double waveTimer = waveDuration;
    int currentWave = 0;

    Garnet garnet;
    double gameTime = 0;

    public double getGameTime() {
        return gameTime;
    }

    @Override
    public void init() {
        currentScore = 0;
        playerCapabilities = getComponentFromParentContext(PlayerCapabilities.class);
        if (playerCapabilities == null) throw new RuntimeException("No player capabilities");

        garnet = SceneManager.getSharedContext().getObjectByType(Garnet.class);
    }

    @Override
    public void tick(double t) {
        gameTime += t;
        waveTimer -= t;
        if (waveTimer < 0) {
            waveTimer = waveDuration;
            currentWave++;
            System.out.println("Wave " + currentWave);
        }


    }

    @Override
    public void draw(Graphics g) {

    }

    public int getCurrentWave() {
        return currentWave;
    }

    public int getPlayerLevel() {
        return playerLevel;
    }

    public void increaseXp(int amount) {
        xp += amount;
        int target = getXpToLevelUp();
        if (xp >= getXpToLevelUp()) {
            xp -= target;
            increaseLevel();
        }
    }

    public int getXpToLevelUp() {
        return 5 + ((playerLevel - 1) * 10);
    }

    public void increaseLevel() {
        playerLevel++;
        setCurrentScore(playerLevel);
        showLevelUpScreen();
    }

    public void showLevelUpScreen() {

        SceneManager.getSceneByName("levelUp").ifPresent(scene -> {
            ((SceneLevelUp) scene).setPlayerCapabilities(playerCapabilities);
            ((SceneLevelUp) scene).setPlayer(getObjectByTagFromParentContext("player"));
        });

        SceneManager.pushSubScene("levelUp");

    }

    public void addToScore(int i) {
        setCurrentScore(getCurrentScore() + i);
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
    }

    public int getEnemyCountForCurrentWave() {
        if (currentWave == 0) {
            return 15;
        } else if (currentWave == 1) {
            return 30;
        } else if (currentWave == 2) {
            return 40;
        } else if (currentWave == 3) {
            return 40;
        }
        return 60;


    }
}
