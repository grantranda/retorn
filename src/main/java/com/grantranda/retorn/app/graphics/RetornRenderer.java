package com.grantranda.retorn.app.graphics;

import com.grantranda.retorn.app.state.RenderState;
import com.grantranda.retorn.engine.graphics.Model;
import com.grantranda.retorn.engine.graphics.Renderer;
import com.grantranda.retorn.engine.graphics.display.Resolution;
import com.grantranda.retorn.engine.math.Matrix4f;
import com.grantranda.retorn.engine.graphics.display.Window;
import com.grantranda.retorn.engine.graphics.Shader;
import com.grantranda.retorn.engine.math.Vector2d;
import com.grantranda.retorn.engine.state.State;

import static org.lwjgl.opengl.GL11.*;

public class RetornRenderer implements Renderer {

    private Shader shader;

    private final Resolution viewportResolution = new Resolution();
    private final Vector2d viewportPixelSize = new Vector2d();
    private final Vector2d fractalAspectRatio = new Vector2d();

    public RetornRenderer() {

    }

    public Shader getShader() {
        return shader;
    }

    public Vector2d getFractalAspectRatio() {
        return fractalAspectRatio;
    }

    public void setFractalAspectRatio(double aspectWidth, double aspectHeight) {
        this.fractalAspectRatio.set(aspectWidth, aspectHeight);
    }

    @Override
    public Resolution getViewportResolution() {
        return viewportResolution;
    }

    @Override
    public void setViewport(int x, int y, int width, int height) {
        glViewport(x, y, width, height);
        viewportResolution.set(width, height);
    }

    @Override
    public void init() {
        Matrix4f projection_matrix = Matrix4f.orthographic(-2.5f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f);

        setFractalAspectRatio(3.5, 2.0); // TODO: Remove

        shader = new Shader("shaders/vertex.vert", "shaders/fragment.frag");
        shader.setUniformMatrix4f("projection_matrix", projection_matrix);
        shader.setUniform1i("palette_texture", 0);
    }

    @Override
    public void terminate() {

    }

    @Override
    public void render(Window window, State state, Model[] models, boolean updateViewport) {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        glDisable(GL_CULL_FACE);

        RenderState renderState = (RenderState) state;
        Resolution renderResolution = renderState.getRenderResolution();
        updateViewport(resolution, renderState);

        double pixelWidth = 3.5f / renderResolution.getWidth(); //(windowWidth - viewportPos.x * 2);
        double pixelHeight = 2.0f / renderResolution.getHeight(); //(windowHeight - viewportPos.y * 2);
        double translatedOffsetX = renderState.getOffset().x * pixelWidth;
        double translatedOffsetY = renderState.getOffset().y * pixelHeight;

        shader.bind();

        shader.setUniform1i("max_iterations", renderState.getMaxIterations());
        shader.setUniform1d("scale", renderState.getScale());
        shader.setUniform2d("offset", translatedOffsetX, translatedOffsetY);

        for (Model model : models) {
            shader.setUniformMatrix4f("model_matrix", model.getModelMatrix());
            model.render();
        }

        shader.unbind();
    }

    private void updateViewport(Resolution maxResolution, double targetAspectRatio) {
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
