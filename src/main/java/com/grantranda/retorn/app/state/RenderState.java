package com.grantranda.retorn.app.state;

import com.grantranda.retorn.engine.math.Vector3d;

public class RenderState {

    private int maxIterations;
    private double scale;
    private Vector3d position;

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

    public Vector3d getPosition() {
        return position;
    }

    public void setPosition(Vector3d position) {
        this.position = position;
    }
}
