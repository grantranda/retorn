package com.grantranda.retorn.app;

import com.grantranda.retorn.app.graphics.RetornRenderer;
import com.grantranda.retorn.app.graphics.gui.RetornGUI;
import com.grantranda.retorn.app.input.RetornInputHandler;
import com.grantranda.retorn.app.state.ApplicationState;
import com.grantranda.retorn.engine.graphics.Camera;
import com.grantranda.retorn.engine.graphics.Model;
import com.grantranda.retorn.engine.graphics.Texture;
import com.grantranda.retorn.engine.graphics.display.Window;
import com.grantranda.retorn.engine.graphics.Mesh;
import com.grantranda.retorn.engine.graphics.gui.GUI;
import com.grantranda.retorn.engine.input.MouseInput;
import com.grantranda.retorn.engine.math.Matrix4f.Projection;
import org.lwjgl.opengl.GL11;

public class Retorn implements Application {

    private final ApplicationState state = new ApplicationState();
    private final RetornRenderer renderer = new RetornRenderer(Projection.ORTHOGRAPHIC);
    private final Camera camera = new Camera();
    private final RetornGUI gui = new RetornGUI();
    private final RetornInputHandler inputHandler = new RetornInputHandler(gui);

    private Model[] models;

    public Retorn(String[] args) {

    }

    public ApplicationState getState() {
        return state;
    }

    @Override
    public GUI getGui() {
        return gui;
    }

    @Override
    public void initialize(Window window) {
        renderer.initialize(window);
        gui.initialize(window, state);

        // Create models
        float[] vertices = new float[]{
                -2.5f,  1.0f, 0.0f, // TL
                -2.5f, -1.0f, 0.0f, // BL
                 1.0f, -1.0f, 0.0f, // BR
                 1.0f,  1.0f, 0.0f, // TR
        };
        float[] textureCoordinates = new float[] {
                0, 1,
                0, 0,
                1, 0,
                1, 1
        };
        byte[] indices = new byte[]{
                0, 1, 3,
                3, 1, 2,
        };
        Mesh mesh = new Mesh(vertices, textureCoordinates, indices);
        Texture texture = new Texture(GL11.GL_TEXTURE_1D, "textures/pal.png");
        Model model = new Model(mesh, texture);

        models = new Model[]{model};
    }

    @Override
    public void terminate() {
        for (Model model : models) {
            model.delete();
        }
        gui.terminate();
    }

    @Override
    public void update(Window window) {
        inputHandler.handle(window, state);
        MouseInput.update();
        gui.update(window, renderer.getShader(), state);
    }

    @Override
    public void render(Window window) {
        renderer.render(window, camera, models);
        gui.render(window);
    }
}
