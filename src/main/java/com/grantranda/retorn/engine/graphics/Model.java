package com.grantranda.retorn.engine.graphics;

import com.grantranda.retorn.engine.math.Matrix4f;
import com.grantranda.retorn.engine.math.Vector3f;

public class Model {

    private final Mesh mesh;
    private final Texture texture;
    private final Vector3f position;
    private final Vector3f rotation; // Axis rotation angles in degrees
    private final Vector3f scale; // Axis scaling factors
    private final Matrix4f modelMatrix;

    public Model(Mesh mesh) {
        this(mesh, null);
    }

    public Model(Mesh mesh, Texture texture) {
        this(mesh, texture, new Vector3f(), new Vector3f(), new Vector3f());
    }

    public Model(Mesh mesh, Texture texture, Vector3f position, Vector3f rotation, Vector3f scale) {
        this.mesh = mesh;
        this.texture = texture;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.modelMatrix = new Matrix4f();
    }

    public Mesh getMesh() {
        return mesh;
    }

    public Texture getTexture() {
        return texture;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        rotation.set(x, y, z);
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(float x, float y, float z) {
        scale.set(x, y, z);
    }

    public Matrix4f getModelMatrix() {
        Matrix4f.identity(modelMatrix);

        Matrix4f.translation(position)
                .multiply(Matrix4f.rotationX(rotation.x))
                .multiply(Matrix4f.rotationY(rotation.y))
                .multiply(Matrix4f.rotationZ(rotation.z))
                .multiply(Matrix4f.scaling(scale.x, scale.y, scale.z));

        return modelMatrix;
    }

    public void render() {
        // TODO: Refactor without null check
        if (texture != null) {
            texture.bind();
            mesh.render();
            texture.unbind();
        } else {
            mesh.render();
        }
    }

    public void delete() {
        if (texture != null) {
            texture.delete();
        }
    }
}
