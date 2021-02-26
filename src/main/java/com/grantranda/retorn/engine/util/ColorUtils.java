package com.grantranda.retorn.engine.util;

import lwjgui.paint.Color;

public class ColorUtils {

    private ColorUtils() {

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
