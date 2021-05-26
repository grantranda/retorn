package com.grantranda.retorn.engine.math;

import com.grantranda.retorn.engine.util.MemoryUtils;

import java.nio.FloatBuffer;
import java.util.Arrays;

public class Matrix4f {

    public float[] elements = new float[4 * 4];

    public Matrix4f() {

    }

    public static Matrix4f identity() {
        Matrix4f result = new Matrix4f();

        result.set(0, 0, 1.0f);
        result.set(1, 1, 1.0f);
        result.set(2, 2, 1.0f);
        result.set(3, 3, 1.0f);

        return result;
    }

    public static void identity(Matrix4f matrix) {
        Arrays.fill(matrix.elements, 0.0f);

        matrix.set(0, 0, 1.0f);
        matrix.set(1, 1, 1.0f);
        matrix.set(2, 2, 1.0f);
        matrix.set(3, 3, 1.0f);
    }

    public static Matrix4f orthographic(float left, float right, float bottom, float top, float near, float far) {
        Matrix4f result = identity();

        result.set(0, 0, 2.0f / (right - left));
        result.set(1, 1, 2.0f / (top - bottom));
        result.set(2, 2, -2.0f / (far - near));
        result.set(3, 0, -(right + left) / (right - left));
        result.set(3, 1, -(top + bottom) / (top - bottom));
        result.set(3, 2, -(far + near) / (far - near));

        return result;
    }

    public static Matrix4f perspective(float fov, float aspectRatio, float near, float far) {
        Matrix4f result = new Matrix4f();

        float tanHalfFov = (float) Math.tan(fov / 2.0f);
        float focalLength = 1.0f / tanHalfFov;

        result.set(0, 0, focalLength / aspectRatio);
        result.set(1, 1, focalLength);
        result.set(2, 2, (far + near) / (near - far));
        result.set(2, 3, (2 * far * near) / (near - far));
        result.set(3, 2, -1);

        return result;
    }

    public static Matrix4f translation(Vector3f vector) {
        Matrix4f result = identity();

        result.set(0, 3, vector.x);
        result.set(1, 3, vector.y);
        result.set(2, 3, vector.z);

        return result;
    }

    public static Matrix4f rotationX(float angle) {
        Matrix4f result = identity();

        float radians = (float) Math.toRadians(angle);
        float sin = (float) Math.sin(radians);
        float cos = (float) Math.cos(radians);

        result.set(1, 1, cos);
        result.set(1, 2, -sin);
        result.set(2, 1, sin);
        result.set(2, 2, cos);

        return result;
    }

    public static Matrix4f rotationY(float angle) {
        Matrix4f result = identity();

        float radians = (float) Math.toRadians(angle);
        float sin = (float) Math.sin(radians);
        float cos = (float) Math.cos(radians);

        result.set(0, 0, cos);
        result.set(0, 2, sin);
        result.set(2, 0, -sin);
        result.set(2, 2, cos);

        return result;
    }

    public static Matrix4f rotationZ(float angle) {
        Matrix4f result = identity();

        float radians = (float) Math.toRadians(angle);
        float sin = (float) Math.sin(radians);
        float cos = (float) Math.cos(radians);

        result.set(0, 0, cos);
        result.set(0, 1, -sin);
        result.set(1, 0, sin);
        result.set(1, 1, cos);

        return result;
    }

    public static Matrix4f scaling(float x, float y, float z) {
        Matrix4f result = identity();

        result.set(0, 0, x);
        result.set(1, 1, y);
        result.set(2, 2, z);

        return result;
    }

    public Matrix4f multiply(Matrix4f matrix) {
        Matrix4f result = new Matrix4f();

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                result.set(row, col, get(row, 0) * matrix.get(0, col) +
                                     get(row, 1) * matrix.get(1, col) +
                                     get(row, 2) * matrix.get(2, col) +
                                     get(row, 3) * matrix.get(3, col)
                );
            }
        }

        return result;
    }

    public float get(int row, int column) {
        return elements[4 * row + column];
    }

    public void set(int row, int column, float value) {
        elements[4 * row + column] = value;
    }

    public FloatBuffer toFloatBuffer() {
        return MemoryUtils.allocateFloatBuffer(elements);
    }
}
