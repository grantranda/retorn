package com.grantranda.retorn.app.graphics;

import com.grantranda.retorn.app.state.RenderState;
import com.grantranda.retorn.engine.graphics.Model;
import com.grantranda.retorn.engine.graphics.Renderer;
import com.grantranda.retorn.engine.graphics.display.Resolution;
import com.grantranda.retorn.engine.math.Matrix4f;
import com.grantranda.retorn.engine.graphics.display.Window;
import com.grantranda.retorn.engine.graphics.Shader;
import com.grantranda.retorn.engine.math.Vector3i;
import com.grantranda.retorn.engine.state.State;

import static org.lwjgl.opengl.GL11.*;

public class RetornRenderer implements Renderer {

    private Shader shader;

    public RetornRenderer() {

    }

    public Shader getShader() {
        return shader;
    }

    public void init(Window window) {
        Matrix4f projection_matrix = Matrix4f.orthographic(-2.5f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f);

        shader = new Shader("shaders/vertex.vert", "shaders/fragment.frag");
        shader.setUniformMatrix4f("projection_matrix", projection_matrix);
        shader.setUniform1i("palette_texture", 0);
    }

    public void terminate() {

    }

    public void render(Window window, State state, Model[] models) {
        RenderState renderState = (RenderState) state;

        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        glDisable(GL_CULL_FACE);

        shader.bind();

        Vector3i viewportPos = updateViewport(window, renderState);

        int windowWidth = window.getWidth();
        int windowHeight = window.getHeight();
        double pixelWidth = 3.5f / (windowWidth - viewportPos.x * 2);
        double pixelHeight = 2.0f / (windowHeight - viewportPos.y * 2);
        double translatedOffsetX = renderState.getOffset().x * pixelWidth;
        double translatedOffsetY = renderState.getOffset().y * pixelHeight;

        shader.setUniform1i("max_iterations", renderState.getMaxIterations());
        shader.setUniform1d("scale", renderState.getScale());
        shader.setUniform2d("offset", translatedOffsetX, translatedOffsetY);
        shader.setUniform2f("window_size", windowWidth, windowHeight);

        for (Model model : models) {
            shader.setUniformMatrix4f("model_matrix", model.getModelMatrix());
            model.render();
        }

        shader.unbind();
    }

    private Vector3i updateViewport(Window window, RenderState renderState) {
        Resolution renderResolution = renderState.getRenderResolution();
        double renderAspectRatio = renderResolution.getAspectRatio();

        int windowWidth = window.getWidth();
        int windowHeight = window.getHeight();
        int viewportWidth = windowWidth;
        int viewportHeight = (int) (viewportWidth / renderAspectRatio);

        if (viewportHeight > windowHeight) {
            viewportHeight = windowHeight;
            viewportWidth = (int) (viewportHeight * renderAspectRatio);
        }

        int viewportX = (windowWidth - viewportWidth) / 2;
        int viewportY = (windowHeight - viewportHeight) / 2;

        glViewport(viewportX, viewportY, viewportWidth, viewportHeight);

        return new Vector3i(viewportX, viewportY, 0);
    }
}
