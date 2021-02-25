package com.grantranda.retorn.app.graphics;

import com.grantranda.retorn.engine.graphics.Model;
import com.grantranda.retorn.engine.graphics.Renderer;
import com.grantranda.retorn.engine.graphics.Shader;
import com.grantranda.retorn.engine.graphics.display.Resolution;
import com.grantranda.retorn.engine.graphics.display.Window;
import com.grantranda.retorn.engine.state.State;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glViewport;

public abstract class AbstractRenderer implements Renderer {

    protected static final Resolution VIEWPORT_RESOLUTION = new Resolution();

    public AbstractRenderer() {

    }

    @Override
    public abstract Shader getActiveShader();

    @Override
    public Resolution getViewportResolution() {
        return VIEWPORT_RESOLUTION;
    }

    @Override
    public void setViewport(int x, int y, int width, int height) {
        glViewport(x, y, width, height);
        VIEWPORT_RESOLUTION.set(width, height);
    }

    @Override
    public abstract void init(Window window);

    @Override
    public abstract void terminate();

    @Override
    public abstract void render(Window window, State state, Model[] models, boolean updateViewport);

    public void clear() {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    }

    protected void updateViewport(Resolution maxResolution, double targetAspectRatio) {
        int maxWidth = maxResolution.getWidth();
        int maxHeight = maxResolution.getHeight();
        int viewportWidth = maxWidth;
        int viewportHeight = (int) (viewportWidth / targetAspectRatio);

        if (viewportHeight > maxHeight) {
            viewportHeight = maxHeight;
            viewportWidth = (int) (viewportHeight * targetAspectRatio);
        }

        int viewportX = (maxWidth - viewportWidth) / 2;
        int viewportY = (maxHeight - viewportHeight) / 2;

        setViewport(viewportX, viewportY, viewportWidth, viewportHeight);
    }
}
