package com.physmo.survivor;

import com.physmo.garnet.Garnet;
import com.physmo.garnet.GarnetApp;
import com.physmo.garnet.graphics.Graphics;
import com.physmo.garnet.graphics.ShaderProgram;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUseProgram;
import com.physmo.garnet.toolkit.scene.SceneManager;
import com.physmo.survivor.scenes.LoadingScene;
import com.physmo.survivor.scenes.SceneGame;
import com.physmo.survivor.scenes.SceneLevelUp;
import com.physmo.survivor.scenes.ScenePause;

public class CellSurvivor extends GarnetApp {

    private ShaderProgram crtShader;
    private Garnet garnet;

    public CellSurvivor(Garnet garnet, String name) {
        super(garnet, name);
    }

    public static void main(String[] args) {
        Garnet garnet = new Garnet(384, 216);
        garnet.setInternalBufferMode(true);
        garnet.setApp(new CellSurvivor(garnet, ""));
        garnet.init();
        garnet.run();
    }

    @Override
    public void init(Garnet garnet) {
        this.garnet = garnet;
        Resources resources = new Resources();
        resources.init(garnet.getGraphics());
        SceneManager.getSharedContext().add(resources);

        SceneManager.getSharedContext().add(garnet);
        SceneManager.addScene(new SceneGame("game"));
        SceneManager.addScene(new ScenePause("pause"));
        SceneManager.addScene(new SceneLevelUp("levelUp"));
        SceneManager.addScene(new LoadingScene("loadingScene"));
        SceneManager.setActiveScene("loadingScene");

        crtShader = ShaderProgram.fromFiles("shaders/passthrough.vert", "shaders/crt.frag");
        garnet.setInternalBufferShader(crtShader);
    }

    @Override
    public void tick(double delta) {
        SceneManager.tick(delta);
    }

    @Override
    public void draw(Graphics g) {
        SceneManager.draw(g);

        crtShader.bind();
        int loc = glGetUniformLocation(crtShader.getProgramId(), "resolution");
        int[] canvas = garnet.getDisplay().getCanvasSize();
        glUniform2f(loc, canvas[0], canvas[1]);
        glUseProgram(0);
    }
}
