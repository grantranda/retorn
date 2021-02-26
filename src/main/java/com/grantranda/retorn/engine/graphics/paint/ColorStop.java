package com.grantranda.retorn.engine.graphics.paint;

import lwjgui.paint.Color;

public class ColorStop {

    private float position;
    private Color color;

    public ColorStop(float position, Color color) {
        this.position = position;
        this.color = color;
    }

    public float getPosition() {
        return position;
    }

    public void setPosition(float position) {
        this.position = position;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
