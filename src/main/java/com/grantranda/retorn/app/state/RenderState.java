package com.grantranda.retorn.app.state;

import com.grantranda.retorn.engine.graphics.display.Resolution;
import com.grantranda.retorn.engine.math.Vector3d;
import com.grantranda.retorn.engine.state.State;

public class RenderState implements State {

    private final Resolution renderResolution = new Resolution(1280, 720);
    private final Vector3d offset = new Vector3d();
    private int maxIterations = 100;
    private double scale = 1.0;
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
        setMaxIterations(100);
        setScale(1.0);
        setOffset(0.0, 0.0, 0.0);
        setRenderResolution(1280, 720);
        setCustomResolution(false);
    }
}
