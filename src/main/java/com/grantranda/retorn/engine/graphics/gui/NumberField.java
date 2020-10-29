package com.grantranda.retorn.engine.graphics.gui;

import lwjgui.scene.control.TextField;

public abstract class NumberField<T extends Number & Comparable<T>> extends TextField {

    protected final T min;
    protected final T max;
    protected T number;

    public NumberField(T number, T min, T max) {
        super();

        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("Min value " + min + " is greater than max value " + max);
        }
        if (number.compareTo(min) < 0 || number.compareTo(max) > 0) {
            throw new IllegalArgumentException("Number " + number + " is not between " + min + " and " + max);
        }

        this.min = min;
        this.max = max;
        setNumber(number);
    }

    public T getNumber() {
        return number;
    }

    public void setNumber(T number) {
        if (number.compareTo(getMin()) < 0 || number.compareTo(getMax()) > 0) return;

        this.number = number;
    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }

    public abstract void updateNumber();
}
