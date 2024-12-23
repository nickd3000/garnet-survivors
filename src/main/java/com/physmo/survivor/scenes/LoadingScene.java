package com.physmo.survivor.scenes;

import com.physmo.garnet.Garnet;
import com.physmo.garnet.graphics.Graphics;
import com.physmo.garnet.toolkit.scene.Scene;
import com.physmo.garnet.toolkit.scene.SceneManager;

public class LoadingScene extends Scene {

    double clock = 0;

    public LoadingScene(String name) {
        super(name);
    }

    @Override
    public void init() {
        Garnet garnet = SceneManager.getSharedContext().getObjectByType(Garnet.class);

        garnet.getDisplay().setWindowScale(3, false);
        //garnet.getDisplay().setFullScreen(true);
    }

    @Override
    public void tick(double delta) {
        clock += delta;
        if (clock>0.2) {
            SceneManager.setActiveScene("game");
        }
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
