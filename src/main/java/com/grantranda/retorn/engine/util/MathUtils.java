package com.grantranda.retorn.engine.util;

public class MathUtils {

    private MathUtils() {

    }

    public static float clamp(float value, float min, float max) {
        return value < min ? min : Math.min(value, max);
    }

    public static float lerp(float start, float end, float position) {
        return start + (end - start) * clamp(position, 0.0f, 1.0f);
    }

    public static float inverseLerp(float start, float end, float position) {
        return start != end ? clamp((position - start) / (end - start), 0.0f, 1.0f) : 0.0f;
    }
}
