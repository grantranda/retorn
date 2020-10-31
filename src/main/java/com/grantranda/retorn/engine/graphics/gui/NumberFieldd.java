package com.grantranda.retorn.engine.graphics.gui;

import java.util.regex.Pattern;

public class NumberFieldd extends NumberField<Double> {

    private static final Pattern DOUBLE_PATTERN = Pattern.compile("[+-]?((?=[.]?[0-9])[0-9]*[.]?[0-9]*)");

    public NumberFieldd() {
        this(0.0);
    }

    public NumberFieldd(Double number) {
        this(number, -Double.MAX_VALUE, Double.MAX_VALUE);
    }

    public NumberFieldd(Double number, Double min, Double max) {
        super(number, min, max);
    }

    @Override
    public boolean isValidNumber() {
        return DOUBLE_PATTERN.matcher(getText()).matches();
    }

    @Override
    public void validate() {
        if (isValidNumber()) {
            setNumber(Double.parseDouble(getText()));
        } else {
            setNumber(number);
        }
    }
}
