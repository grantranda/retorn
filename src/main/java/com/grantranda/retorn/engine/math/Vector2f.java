package com.grantranda.retorn.engine.math;

public class Vector2f {

    public float x;
    public float y;

    public Vector2f() {
        this(0.0f, 0.0f);
    }

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(Vector2f vector) {
        this.x = vector.x;
        this.y = vector.y;
    }
}
