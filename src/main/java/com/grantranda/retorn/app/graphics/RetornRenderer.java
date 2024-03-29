package com.grantranda.retorn.app.graphics;

import com.grantranda.retorn.app.state.RenderState;
import com.grantranda.retorn.engine.graphics.Model;
import com.grantranda.retorn.engine.graphics.Shader;
import com.grantranda.retorn.engine.graphics.display.Resolution;
import com.grantranda.retorn.engine.math.Fraction;
import com.grantranda.retorn.engine.graphics.display.Window;
import com.grantranda.retorn.engine.math.Vector2d;
import com.grantranda.retorn.engine.state.State;

import static org.lwjgl.opengl.GL11.*;

public class RetornRenderer extends AbstractRenderer {

    public static final String MANDELBROT_VERTEX_PATH = "shaders/mandelbrot.vert";
    public static final String MANDELBROT_FRAGMENT_PATH = "shaders/mandelbrot.frag";
    public static final String JULIA_VERTEX_PATH = "shaders/julia.vert";
    public static final String JULIA_FRAGMENT_PATH = "shaders/julia.frag";

    private final MandelbrotRenderer mandelbrotRenderer = new MandelbrotRenderer(MANDELBROT_VERTEX_PATH, MANDELBROT_FRAGMENT_PATH);
    private final JuliaRenderer juliaRenderer = new JuliaRenderer(JULIA_VERTEX_PATH, JULIA_FRAGMENT_PATH);
    private AbstractFractalRenderer activeRenderer;

    private final Resolution targetViewportResolution = new Resolution();
    private final Vector2d viewportPixelSize = new Vector2d();
    private final Fraction fractalAspectRatio = new Fraction();

    public RetornRenderer() {
        super();
    }

    public Resolution getTargetViewportResolution() {
        return targetViewportResolution;
    }

    public void setTargetViewportResolution(int width, int height) {
        this.targetViewportResolution.set(width, height);
    }

    public Fraction getFractalAspectRatio() {
        return fractalAspectRatio;
    }

    public void setFractalAspectRatio(int aspectWidth, int aspectHeight) {
        this.fractalAspectRatio.set(aspectWidth, aspectHeight);
    }

    public MandelbrotRenderer getMandelbrotRenderer() {
        return mandelbrotRenderer;
    }

    public JuliaRenderer getJuliaRenderer() {
        return juliaRenderer;
    }

    public AbstractFractalRenderer getActiveRenderer() {
        return activeRenderer;
    }

    public void setActiveRenderer(AbstractFractalRenderer activeRenderer) {
        this.activeRenderer = activeRenderer;
    }

    @Override
    public Shader getActiveShader() {
        return activeRenderer.getActiveShader();
    }

    @Override
    public void init(Window window) {
        mandelbrotRenderer.init(window);
        juliaRenderer.init(window);
        setTargetViewportResolution(window.getWidth(), window.getHeight());
        setFractalAspectRatio(7, 4);
        setActiveRenderer(mandelbrotRenderer);
    }

    @Override
    public void terminate() {

    }

    @Override
    public void render(Window window, State state, Model[] models, boolean updateViewport) {
        clear();
        glDisable(GL_CULL_FACE);

        RenderState renderState = (RenderState) state;

        // If true, then resize the viewport to fit within the window bounds and maintain the fractal aspect ratio.
        // This is optional to allow for cases in which the viewport size should not be tied to window size, such as
        // when rendering to an image.
        if (updateViewport) {
            updateViewport(targetViewportResolution, fractalAspectRatio.getRatio());

            viewportPixelSize.set(
                    (fractalAspectRatio.getNumerator() / 2.0) / VIEWPORT_RESOLUTION.getWidth(),
                    (fractalAspectRatio.getDenominator() / 2.0) / VIEWPORT_RESOLUTION.getHeight()
            );
        }

        double translatedOffsetX = renderState.getOffset().x * viewportPixelSize.x;
        double translatedOffsetY = renderState.getOffset().y * viewportPixelSize.y;

        Shader activeShader = activeRenderer.getActiveShader();
        activeShader.setUniform1i("use_orbit_trap", renderState.getColoringAlgorithm() == ColoringAlgorithm.ORBIT_TRAP ? 1 : 0);
        activeShader.setUniform1i("max_iterations", renderState.getMaxIterations());
        activeShader.setUniform1i("escape_radius", renderState.getEscapeRadius());
        activeShader.setUniform1d("scale", renderState.getScale());
        activeShader.setUniform2d("offset", translatedOffsetX, translatedOffsetY);

        activeRenderer.render(window, state, models, updateViewport);
    }
}
