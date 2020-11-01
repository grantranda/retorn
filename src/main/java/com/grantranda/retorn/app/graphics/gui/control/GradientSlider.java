package com.grantranda.retorn.app.graphics.gui.control;

import lwjgui.paint.Color;
import lwjgui.scene.control.Slider;
import lwjgui.scene.shape.Rectangle;

public class GradientSlider extends Slider {

    private Marker[] markers;

    public GradientSlider() {
        this(0, 100, 50, 0);
    }

    public GradientSlider(double min, double max, double value) {
        this(min, max, value, 0);
    }

    public GradientSlider(double min, double max, double value, double blockIncrement) {
        super(min, max, value, blockIncrement);

    }

    class Marker extends Rectangle {

        private static final int SIZE = 16;
        private float position;
        private boolean selected;

        private Color color;

        public Marker() {
            this(Color.BLUE);
        }

        public Marker(Color color) {
            super(SIZE, SIZE, color);
        }

        public int getSize() {
            return SIZE;
        }

        public float getPosition() {
            return position;
        }

        public void setPosition(float position) {
            this.position = position;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }
    }
}
