package com.physmo.survivor.scenes;

import com.physmo.garnet.Garnet;
import com.physmo.garnet.graphics.Graphics;
import com.physmo.garnet.toolkit.scene.Scene;
import com.physmo.garnet.toolkit.scene.SceneManager;
import com.physmo.garnet.toolkit.tick.TimedEvent;

public class LoadingScene extends Scene {

    TimedEvent loadingDelay = new TimedEvent();

    public LoadingScene(String name) {
        super(name);
    }

    @Override
    public void init() {
        Garnet garnet = SceneManager.getSharedContext().getObjectByType(Garnet.class);

        garnet.getDisplay().setWindowScale(3, false);
        //garnet.getDisplay().setFullScreen(true);

        loadingDelay.startAndOnEnd(0.3, () -> SceneManager.setActiveScene("game"));
    }

    @Override
    public void tick(double delta) {
        loadingDelay.tick(delta);
    }

    @Override
    public void draw(Graphics g) {

    }

    @Override
    public void onMakeActive() {

    }

    @Override
    public void onMakeInactive() {

    }
}
