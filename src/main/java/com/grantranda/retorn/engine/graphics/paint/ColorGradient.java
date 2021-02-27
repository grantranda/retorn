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

    public void addStop(float position, Color color) {
        ColorStop stop = new ColorStop(position, color);
        int i = 0;
        for (; i < stops.size(); i++) {
            if (stop.getPosition() < stops.get(i).getPosition()) {
                break;
            }
        }
        stops.add(i, stop);
    }

    public void removeStop(int index) {
        if (stops.size() >= 2) {
            stops.remove(index);
        }
    }

    public void setStopPosition(int index, float position) {
        Color color = stops.get(index).getColor();
        removeStop(index);
        addStop(position, color);
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
}
