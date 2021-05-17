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
import com.grantranda.retorn.engine.graphics.Mesh;
import com.grantranda.retorn.engine.graphics.Model;
import com.grantranda.retorn.engine.graphics.Texture;
import com.grantranda.retorn.engine.graphics.display.Resolution;
import com.grantranda.retorn.engine.graphics.display.Window;
import com.grantranda.retorn.engine.graphics.gui.GUI;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.glfwCreateCursor;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_1D;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;

public class Retorn implements Application {

    public static Retorn INSTANCE;

    public static final String DISPLAY_STATE_PATH = "display_parameters.json";
    public static final String RENDER_STATE_PATH = "render_parameters.json";
    public static final String SAVE_PARAMETERS_PATH = "retorn_parameters.json";
    public static final String DEFAULT_RENDER_FILENAME = "fractal.png";
    public static final String CURSOR_PATH = "textures/cursor.png";
    public static final String MANDELBROT_SET = "Mandelbrot";
    public static final String JULIA_SET = "Julia";

    private final ApplicationState state = new ApplicationState();
    private final RetornRenderer renderer = new RetornRenderer();
    private ImageRenderer imageRenderer;
    private RetornGUI gui;
    private RetornInputHandler inputHandler;

    private Mesh mesh;
    private Model[] models;

    public Retorn(String[] args) {
        if (INSTANCE != null) {
            throw new RuntimeException("Instance of Retorn already exists");
        }
        INSTANCE = this;
    }

    public ApplicationState getState() {
        return state;
    }

    @Override
    public GUI getGui() {
        return gui;
    }

    public void setTexture(Texture texture) {
        Model model = new Model(mesh, texture);
        models = new Model[]{model};
    }

    @Override
    public void init(Window window) {
        loadDisplayState(state);
        loadRenderState(state);

        window.setFpsLimit(state.getDisplayState().getFpsLimit());
        window.setCursor(createCursor(CURSOR_PATH));

        Resolution renderResolution = state.getRenderState().getRenderResolution();
        Framebuffer framebuffer = new Framebuffer(renderResolution);
        imageRenderer = new ImageRenderer(renderer, framebuffer, renderResolution, DEFAULT_RENDER_FILENAME, "PNG");
        gui = new RetornGUI(renderer, imageRenderer);
        inputHandler = new RetornInputHandler(gui, window.getCursor());

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

        mesh = new Mesh(vertices, textureCoordinates, indices);

        File defaultPaletteFile = new File(getClass().getClassLoader().getResource("textures/pal.png").getFile());
        setTexture(new Texture(GL_TEXTURE_1D, GL_RGBA, GL_NEAREST, defaultPaletteFile));
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

        if (gui.isMenuShown()) {
            renderer.setTargetViewportResolution(window.getWidth() - RetornGUI.MENU_CONTENT_WIDTH, window.getHeight());
        } else {
            renderer.setTargetViewportResolution(window.getWidth(), window.getHeight());
        }
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

            // Reset nonpersistent fields
            RenderState renderState = state.getRenderState();
            Resolution renderResolution = renderState.getRenderResolution();
            String fractalAlgorithm = renderState.getFractalAlgorithm();
            int renderWidth = renderResolution.getWidth();
            int renderHeight = renderResolution.getHeight();
            boolean customResolution = renderState.isCustomResolution();
            boolean fractalAspectRatioMaintained = renderState.isFractalAspectRatioMaintained();

            renderState.reset();
            renderState.setRenderResolution(renderWidth, renderHeight);
            renderState.setFractalAlgorithm(fractalAlgorithm);
            renderState.setCustomResolution(customResolution);
            renderState.setFractalAspectRatioMaintained(fractalAspectRatioMaintained);
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

    private long createCursor(String path) {
        long cursorID = MemoryUtil.NULL;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer x = stack.mallocInt(1);
            IntBuffer y = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            File file = new File(getClass().getClassLoader().getResource(path).getFile());
            ByteBuffer buffer = stbi_load(file.getAbsolutePath(), x, y, channels, 0);

            if (buffer == null) {
                throw new RuntimeException("Unable to load cursor image '" + path + "':\n" + stbi_failure_reason());
            }

            GLFWImage cursorImage = GLFWImage.create();
            cursorImage.set(x.get(), y.get(), buffer);
            cursorID = glfwCreateCursor(cursorImage, 7, 7);

            stbi_image_free(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cursorID;
    }
}
