package com.grantranda.retorn.app.graphics.gui.control;

import lwjgui.scene.control.TextField;
import lwjgui.style.Background;
import lwjgui.style.BackgroundSolid;
import lwjgui.theme.Theme;

public abstract class NumberField<T extends Number & Comparable<T>> extends TextField {

    private static final Background BACKGROUND_ENABLED = new BackgroundSolid(Theme.current().getBackground());
    private static final Background BACKGROUND_DISABLED = new BackgroundSolid(Theme.current().getBackgroundAlt());

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

    @Override
    public void setDisabled(boolean disabled) {
        super.setDisabled(disabled);
        setBackground(disabled ? BACKGROUND_DISABLED : BACKGROUND_ENABLED);
    }

    public T getNumber() {
        return number;
    }

    public boolean setNumber(T number) {
        if (number.compareTo(getMin()) < 0 || number.compareTo(getMax()) > 0) return false;

        this.number = number;
        setText(number.toString());
        return true;
    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }

    public abstract boolean isValidNumber();

    public abstract boolean validate();
}
