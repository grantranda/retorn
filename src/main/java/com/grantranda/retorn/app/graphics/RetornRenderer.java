package com.grantranda.retorn.app.graphics;

import com.grantranda.retorn.app.state.RenderState;
import com.grantranda.retorn.engine.graphics.Model;
import com.grantranda.retorn.engine.graphics.Renderer;
import com.grantranda.retorn.engine.graphics.display.Resolution;
import com.grantranda.retorn.engine.math.Fraction;
import com.grantranda.retorn.engine.math.Matrix4f;
import com.grantranda.retorn.engine.graphics.display.Window;
import com.grantranda.retorn.engine.graphics.Shader;
import com.grantranda.retorn.engine.math.Vector2d;
import com.grantranda.retorn.engine.state.State;

import static org.lwjgl.opengl.GL11.*;

public class RetornRenderer implements Renderer {

    public static final String MANDELBROT_VERTEX_PATH = "shaders/mandelbrot.vert";
    public static final String MANDELBROT_FRAGMENT_PATH = "shaders/mandelbrot.frag";
    public static final String JULIA_VERTEX_PATH = "shaders/julia.vert";
    public static final String JULIA_FRAGMENT_PATH = "shaders/julia.frag";

    private final Resolution viewportResolution = new Resolution();
    private final Vector2d viewportPixelSize = new Vector2d();
    private final Fraction fractalAspectRatio = new Fraction();

    public RetornRenderer() {

    }

    public Fraction getFractalAspectRatio() {
        return fractalAspectRatio;
    }

    public void setFractalAspectRatio(int aspectWidth, int aspectHeight) {
        this.fractalAspectRatio.set(aspectWidth, aspectHeight);
    }

    @Override
    public Shader getShader() {
        return shader;
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
    public void init(String vertexShaderPath, String fragmentShaderPath) {
        Matrix4f projection_matrix = Matrix4f.orthographic(-2.5f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f);

        setFractalAspectRatio(7, 4); // TODO: Remove

        shader = new Shader(vertexShaderPath, fragmentShaderPath);
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

        // If true, then resize the viewport to fit within the window bounds and maintain the fractal aspect ratio.
        // This is optional to allow for cases in which the viewport size should not be tied to window size, such as
        // when rendering to an image.
        if (updateViewport) {
            updateViewport(window.getResolution(), fractalAspectRatio.getRatio());

            // TODO: Potentially remove division by 2.0 and find alternate solution
            viewportPixelSize.set(
                    (fractalAspectRatio.getNumerator() / 2.0) / viewportResolution.getWidth(),
                    (fractalAspectRatio.getDenominator() / 2.0) / viewportResolution.getHeight()
            );
        }

        double translatedOffsetX = renderState.getOffset().x * viewportPixelSize.x;
        double translatedOffsetY = renderState.getOffset().y * viewportPixelSize.y;

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
