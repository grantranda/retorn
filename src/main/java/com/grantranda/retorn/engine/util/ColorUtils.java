package com.grantranda.retorn.engine.util;

import lwjgui.paint.Color;

import java.util.Random;

public class ColorUtils {

    private static final Random random = new Random();

    private ColorUtils() {

    }

    public static Color getRandomColor() {
        return getRandomColor(254);
    }

    public static Color getRandomColor(int bound) {
        bound = Math.min(Math.max(0, bound), 254);
        return new Color(random.nextInt(bound) + 1, random.nextInt(bound) + 1, random.nextInt(bound) + 1);
    }

    public static Color lerp(Color start, Color end, float position) {
        position = MathUtils.clamp(position, 0.0f, 1.0f);
        return new Color(
                start.getRedF() + (end.getRedF() - start.getRedF()) * position,
                start.getGreenF() + (end.getGreenF() - start.getGreenF()) * position,
                start.getBlueF() + (end.getBlueF() - start.getBlueF()) * position,
                start.getAlphaF() + (end.getAlphaF() - start.getAlphaF()) * position
        );
    }
}
