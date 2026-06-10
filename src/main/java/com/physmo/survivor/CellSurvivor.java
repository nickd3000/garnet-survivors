package com.physmo.survivor;

import com.physmo.garnet.Garnet;
import com.physmo.garnet.GarnetApp;
import com.physmo.garnet.graphics.Graphics;
import com.physmo.garnet.graphics.ShaderProgram;
import com.physmo.garnet.toolkit.scene.SceneManager;
import com.physmo.survivor.scenes.LoadingScene;
import com.physmo.survivor.scenes.SceneGame;
import com.physmo.survivor.scenes.SceneLevelUp;
import com.physmo.survivor.scenes.ScenePause;

public class CellSurvivor extends GarnetApp {

    private ShaderProgram crtShader;
    private Garnet garnet;

    public CellSurvivor() {
        super("");
    }

    public static void main(String[] args) {
        Garnet.launch(384, 216, CellSurvivor::new, garnet -> garnet.setInternalBufferMode(true));
    }

    @Override
    public void init() {
        this.garnet = getGarnet();
        Resources resources = new Resources();
        resources.init(this.garnet.getGraphics());
        SceneManager.getSharedContext().add(resources);

        SceneManager.getSharedContext().add(this.garnet);
        SceneManager.addScene(new SceneGame("game"));
        SceneManager.addScene(new ScenePause("pause"));
        SceneManager.addScene(new SceneLevelUp("levelUp"));
        SceneManager.addScene(new LoadingScene("loadingScene"));
        SceneManager.setActiveScene("loadingScene");

        crtShader = ShaderProgram.fromFiles("shaders/passthrough.vert", "shaders/crt.frag");
        this.garnet.setInternalBufferShader(crtShader);
    }

    @Override
    public void tick(double delta) {
        SceneManager.tick(delta);
    }

    @Override
    public void draw(Graphics g) {
        SceneManager.draw(g);

        crtShader.bind();
        int[] canvas = garnet.getDisplay().getCanvasSize();
        crtShader.setUniform2f("resolution", canvas[0], canvas[1]);
        crtShader.unbind();
    }
}
