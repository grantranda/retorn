package com.grantranda.retorn.app.state;

import com.grantranda.retorn.engine.graphics.display.Resolution;
import com.grantranda.retorn.engine.math.Vector3d;
import com.grantranda.retorn.engine.state.State;

public class RenderState implements State {

    public static final int DEFAULT_WIDTH = 1280;
    public static final int DEFAULT_HEIGHT = 720;
    public static final int DEFAULT_MAX_ITERATIONS = 100;
    public static final double DEFAULT_OFFSET = 0.0;
    public static final double DEFAULT_SCALE = 1.0;

    private final Resolution renderResolution = new Resolution(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    private final Vector3d offset = new Vector3d(DEFAULT_OFFSET, DEFAULT_OFFSET, DEFAULT_OFFSET);
    private int maxIterations = DEFAULT_MAX_ITERATIONS;
    private double scale = DEFAULT_SCALE;
    private boolean customResolution = false;

    // Palette class?

    public RenderState() {

    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public Vector3d getOffset() {
        return offset;
    }

    public void setOffset(double x, double y, double z) {
        offset.x = x;
        offset.y = y;
        offset.z = z;
    }

    public Resolution getRenderResolution() {
        return renderResolution;
    }

    public void setRenderResolution(int width, int height) {
        renderResolution.set(width, height);
    }

    public boolean isCustomResolution() {
        return customResolution;
    }

    public void setCustomResolution(boolean customResolution) {
        this.customResolution = customResolution;
    }

    @Override
    public void reset() {
        setMaxIterations(DEFAULT_MAX_ITERATIONS);
        setScale(DEFAULT_SCALE);
        setOffset(DEFAULT_OFFSET, DEFAULT_OFFSET, DEFAULT_OFFSET);
        setRenderResolution(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setCustomResolution(false);
    }
}
