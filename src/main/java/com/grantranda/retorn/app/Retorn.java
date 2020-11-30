package com.grantranda.retorn.app;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.grantranda.retorn.app.graphics.RetornRenderer;
import com.grantranda.retorn.app.graphics.gui.RetornGUI;
import com.grantranda.retorn.app.input.RetornInputHandler;
import com.grantranda.retorn.app.state.ApplicationState;
import com.grantranda.retorn.app.state.DisplayState;
import com.grantranda.retorn.app.util.StateUtils;
import com.grantranda.retorn.engine.Application;
import com.grantranda.retorn.engine.graphics.Model;
import com.grantranda.retorn.engine.graphics.Texture;
import com.grantranda.retorn.engine.graphics.display.Window;
import com.grantranda.retorn.engine.graphics.Mesh;
import com.grantranda.retorn.engine.graphics.gui.GUI;
import com.grantranda.retorn.engine.input.MouseInput;
import com.grantranda.retorn.engine.math.Matrix4f.Projection;

import java.io.File;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;

public class Retorn implements Application {

    private final ApplicationState state = new ApplicationState();
    private final RetornRenderer renderer = new RetornRenderer(Projection.ORTHOGRAPHIC);
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
    public void init(Window window) {
        loadDisplayState(state);
        renderer.init(window);
        gui.init(window, state);

        // Vertices of mesh corners
        float[] vertices = new float[]{
                -2.5f,  1.0f, 0.0f, // TL
                -2.5f, -1.0f, 0.0f, // BL
                 1.0f, -1.0f, 0.0f, // BR
                 1.0f,  1.0f, 0.0f, // TR
        };

        // Vertices of texture corners
        float[] textureCoordinates = new float[] {
                0.0f, 1.0f, // TL
                0.0f, 0.0f, // BL
                1.0f, 0.0f, // BR
                1.0f, 1.0f, // TR
        };

        // Indices of polygons. Each index points to a vertex in the vertices array.
        byte[] indices = new byte[]{
                0, 1, 3, // First triangle
                3, 1, 2, // Second triangle
        };

        Mesh mesh = new Mesh(vertices, textureCoordinates, indices);
        Texture texture = new Texture(GL_TEXTURE_1D, GL_RGBA, GL_NEAREST, "textures/pal.png");
        Model model = new Model(mesh, texture);

        models = new Model[]{model};
    }

    @Override
    public void terminate() {
        saveDisplayState(state);
        for (Model model : models) {
            model.delete();
        }
        renderer.terminate();
        gui.terminate();
    }

    @Override
    public void update(Window window) {
        inputHandler.handle(window, state);
        MouseInput.update();
        gui.update(window, state);
    }

    @Override
    public void render(Window window) {
        renderer.render(window, state.getRenderState(), models);
        gui.render(window);
    }

    public void loadDisplayState(ApplicationState state) {
        try {
            StateUtils.loadState(state, DisplayState.class, new File("display_parameters.json"));
        } catch (IOException | JsonSyntaxException e) {
            Main.logger.error("Error loading display state");
        }
    }

    public void saveDisplayState(ApplicationState state) {
        try {
            StateUtils.saveState(state.getDisplayState(), new File("display_parameters.json"));
        } catch (IOException | JsonIOException e) {
            Main.logger.error("Error saving display state");
        }
    }
}
