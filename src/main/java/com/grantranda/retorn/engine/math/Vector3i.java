package com.grantranda.retorn.engine.math;

public class Vector3i {

    public int x;
    public int y;
    public int z;

    public Vector3i() {
        this(0, 0, 0);
    }

    public Vector3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(Vector3i vector) {
        this.x = vector.x;
        this.y = vector.y;
        this.z = vector.z;
    }
}
