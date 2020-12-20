package com.grantranda.retorn.app;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.grantranda.retorn.app.graphics.RetornRenderer;
import com.grantranda.retorn.app.graphics.gui.RetornGUI;
import com.grantranda.retorn.app.input.RetornInputHandler;
import com.grantranda.retorn.app.state.ApplicationState;
import com.grantranda.retorn.app.state.DisplayState;
import com.grantranda.retorn.app.state.RenderState;
import com.grantranda.retorn.app.util.StateUtils;
import com.grantranda.retorn.engine.Application;
import com.grantranda.retorn.engine.graphics.Framebuffer;
import com.grantranda.retorn.engine.graphics.ImageRenderer;
import com.grantranda.retorn.engine.graphics.Model;
import com.grantranda.retorn.engine.graphics.Texture;
import com.grantranda.retorn.engine.graphics.display.Resolution;
import com.grantranda.retorn.engine.graphics.display.Window;
import com.grantranda.retorn.engine.graphics.Mesh;
import com.grantranda.retorn.engine.graphics.gui.GUI;

import java.io.File;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;

public class Retorn implements Application {

    public static final String VERTEX_SHADER_PATH = "shaders/vertex.vert";
    public static final String FRAGMENT_SHADER_PATH = "shaders/fragment.frag";
    public static final String DISPLAY_STATE_PATH = "display_parameters.json";
    public static final String RENDER_STATE_PATH = "render_parameters.json";
    public static final String SAVE_PARAMETERS_PATH = "retorn_parameters.json";

    private final ApplicationState state = new ApplicationState();
    private final RetornRenderer renderer = new RetornRenderer();
    private ImageRenderer imageRenderer;
    private RetornGUI gui;
    private RetornInputHandler inputHandler;

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
        loadRenderState(state);

        Resolution renderResolution = state.getRenderState().getRenderResolution();
        Framebuffer framebuffer = new Framebuffer(renderResolution);
        imageRenderer = new ImageRenderer(renderer, framebuffer, renderResolution, "test.png", "PNG");
        gui = new RetornGUI(imageRenderer);
        inputHandler = new RetornInputHandler(gui);

        renderer.init(VERTEX_SHADER_PATH, FRAGMENT_SHADER_PATH);
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
        saveRenderState(state);
        for (Model model : models) {
            model.delete();
        }
        renderer.terminate();
        imageRenderer.terminate();
        gui.terminate();
    }

    @Override
    public void update(Window window) {
        RenderState renderState = state.getRenderState();

        inputHandler.handle(window, state);
        imageRenderer.setResolution(renderState.getRenderResolution());
        imageRenderer.update(renderState, models);
        gui.update(window, state);
    }

    @Override
    public void render(Window window) {
        renderer.render(window, state.getRenderState(), models, true);
        gui.render(window);
    }

    public void loadDisplayState(ApplicationState state) {
        try {
            StateUtils.loadState(state, DisplayState.class, new File(DISPLAY_STATE_PATH));
        } catch (IOException | JsonSyntaxException e) {
            Main.logger.error("Error loading display state");
        }
    }

    public void saveDisplayState(ApplicationState state) {
        try {
            StateUtils.saveState(state.getDisplayState(), new File(DISPLAY_STATE_PATH));
        } catch (IOException | JsonIOException e) {
            Main.logger.error("Error saving display state");
        }
    }

    public void loadRenderState(ApplicationState state) {
        try {
            StateUtils.loadState(state, RenderState.class, new File(RENDER_STATE_PATH));

            // Reset everything but render resolution
            RenderState renderState = state.getRenderState();
            Resolution renderResolution = renderState.getRenderResolution();
            int renderWidth = renderResolution.getWidth();
            int renderHeight = renderResolution.getHeight();
            boolean customResolution = renderState.isCustomResolution();

            renderState.reset();
            renderState.setRenderResolution(renderWidth, renderHeight);
            renderState.setCustomResolution(customResolution);
        } catch (IOException | JsonSyntaxException e) {
            Main.logger.error("Error loading render state");
        }
    }

    public void saveRenderState(ApplicationState state) {
        try {
            StateUtils.saveState(state.getRenderState(), new File(RENDER_STATE_PATH));
        } catch (IOException | JsonIOException e) {
            Main.logger.error("Error saving render state");
        }
    }
}
