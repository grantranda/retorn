package com.grantranda.retorn.engine.graphics;

import com.grantranda.retorn.engine.math.Matrix4f;
import com.grantranda.retorn.engine.math.Vector3f;

public class Camera {

    private final Vector3f position;

    // Axis rotation angles in degrees
    private final Vector3f rotation;

    private final Matrix4f viewMatrix;

    public Camera() {
        this(new Vector3f(), new Vector3f());
    }

    public Camera(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
        this.viewMatrix = new Matrix4f();
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    public Matrix4f getViewMatrix() {
        Matrix4f.identity(viewMatrix);

        Matrix4f.rotationX(rotation.x)
                .multiply(Matrix4f.rotationY(rotation.y))
                .multiply(Matrix4f.rotationZ(rotation.z))
                .multiply(Matrix4f.translation(position));

        return viewMatrix;
    }
}
