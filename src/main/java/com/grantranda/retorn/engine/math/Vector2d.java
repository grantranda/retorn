package com.grantranda.retorn.engine.math;

public class Vector2d {

    public double x;
    public double y;

    public Vector2d() {
        this(0.0, 0.0);
    }

    public Vector2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void set(Vector2d vector) {
        this.x = vector.x;
        this.y = vector.y;
    }
}
