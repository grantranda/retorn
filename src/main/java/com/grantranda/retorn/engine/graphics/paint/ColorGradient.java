package com.grantranda.retorn.engine.graphics.paint;

import com.grantranda.retorn.engine.util.ColorUtils;
import com.grantranda.retorn.engine.util.MathUtils;
import lwjgui.paint.Color;

import java.util.LinkedList;

public class ColorGradient {

    private final LinkedList<ColorStop> stops = new LinkedList<>();

    public ColorGradient(Color startColor, Color endColor) {
        addStop(0.0f, startColor);
        addStop(1.0f, endColor);
    }

    public LinkedList<ColorStop> getStops() {
        return stops;
    }

    public Color lerp(float position) {
        ColorStop startStop = stops.getFirst();
        ColorStop endStop = stops.getLast();

        for (ColorStop currentStop : stops) {
            if (position > currentStop.getPosition()) {
                startStop = currentStop;
            } else if (position < currentStop.getPosition()) {
                endStop = currentStop;
                break;
            }
        }
        float relativePosition = MathUtils.inverseLerp(startStop.getPosition(), endStop.getPosition(), position);
        return ColorUtils.lerp(startStop.getColor(), endStop.getColor(), relativePosition);
    }

    public int addStop(float position, Color color) {
        return addStop(new ColorStop(position, color));
    }

    public int addStop(ColorStop stop) {
        int i = 0;
        for (; i < stops.size(); i++) {
            if (stop.getPosition() < stops.get(i).getPosition()) {
                break;
            }
        }

        float increment = -0.001f;
        while (!isValidPosition(stop.getPosition())) {
            float position = stop.getPosition();
            if (position == 0.0f || position == 1.0f) {
                increment *= -1;
            }
            stop.setPosition(MathUtils.clamp(position + increment, 0.0f, 1.0f));
        }

        stops.add(i, stop);
        return i;
    }

    public void removeStop(int index) {
        stops.remove(index);
    }

    public int setStopPosition(int index, float position) {
        ColorStop stop = stops.get(index);
        removeStop(index);
        stop.setPosition(position);
        return addStop(stop);
    }

    public void setStopColor(int index, Color color) {
        stops.get(index).setColor(color);
    }

    public Color[] toArray(int width) {
        Color[] colors = new Color[width];
        for (int i = 0; i < width; i++) {
            colors[i] = lerp(i * (1.0f / width));
        }
        return colors;
    }

    private boolean isValidPosition(float position) {
        for (ColorStop stop : stops) {
            if (stop.getPosition() == position) {
                return false;
            }
        }
        return true;
    }
}
