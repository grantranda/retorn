package com.grantranda.retorn.engine.math;

public class Vector3d {

    public double x;
    public double y;
    public double z;

    public Vector3d() {
        this(0.0, 0.0, 0.0);
    }

    public Vector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(Vector3d vector) {
        this.x = vector.x;
        this.y = vector.y;
        this.z = vector.z;
    }
}
