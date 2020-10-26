package com.grantranda.retorn.engine.graphics.gui;

import lwjgui.scene.control.TextField;

public class NumberField extends TextField {

    private final double min;
    private final double max;
    private double number;

    public NumberField() {
        this(0);
    }

    public NumberField(double number) {
        this(number, -Double.MAX_VALUE, Double.MAX_VALUE);
    }

    public NumberField(double number, double min, double max) {
        super();

        if (min > max) {
            throw new IllegalArgumentException("Min value " + min + " is greater than max value " + max);
        }
        if (number < min || number > max) {
            throw new IllegalArgumentException("Number " + number + " is not between " + min + " and " + max);
        }

        this.min = min;
        this.max = max;
        setNumber(number);

        setOnTextChange(event -> {
            try {
                setNumber(Double.parseDouble(getText()));
            } catch (NumberFormatException e) {
                setNumber(number);
            }
        });
    }

    public double getNumber() {
        return number;
    }

    public void setNumber(double number) {
        if (number < min || number > max) return;

        this.number = number;
        setText(Double.toString(number));
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }
}
