package com.grantranda.retorn.engine.math;

public class Vector2i {

    public int x;
    public int y;

    public Vector2i() {
        this(0, 0);
    }

    public Vector2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void set(Vector2i vector) {
        this.x = vector.x;
        this.y = vector.y;
    }
}
