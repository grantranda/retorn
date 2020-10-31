package com.grantranda.retorn.app.state;

import com.grantranda.retorn.engine.math.Vector3d;

public class RenderState {

    private final Vector3d offset = new Vector3d();
    private int maxIterations;
    private double scale;

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
}
