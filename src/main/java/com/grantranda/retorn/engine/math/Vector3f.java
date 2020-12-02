package com.grantranda.retorn.engine.math;

public class Vector3f {

    public float x;
    public float y;
    public float z;

    public Vector3f() {
        this(0.0f, 0.0f, 0.0f);
    }

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(Vector3f vector) {
        this.x = vector.x;
        this.y = vector.y;
        this.z = vector.z;
    }
}
