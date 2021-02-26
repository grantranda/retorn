package com.grantranda.retorn.engine.graphics.paint;

import com.grantranda.retorn.engine.graphics.Texture;
import com.grantranda.retorn.engine.util.ColorUtils;
import com.grantranda.retorn.engine.util.MathUtils;
import lwjgui.paint.Color;

import java.nio.ByteBuffer;
import java.util.LinkedList;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_1D;

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

    public Texture toTexture(int width) {
        ByteBuffer pixels = null; // TODO
        return new Texture(GL_TEXTURE_1D, GL_RGBA, GL_NEAREST, width, 1, pixels);
    }
}
