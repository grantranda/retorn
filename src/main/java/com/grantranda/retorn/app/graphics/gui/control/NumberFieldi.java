package com.grantranda.retorn.app.graphics.gui.control;

import java.util.regex.Pattern;

public class NumberFieldi extends NumberField<Integer> {

    private static final Pattern INTEGER_PATTERN = Pattern.compile("[+-]?[0-9]+");

    public NumberFieldi() {
        this(0);
    }

    public NumberFieldi(Integer number) {
        this(number, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public NumberFieldi(Integer number, Integer min, Integer max) {
        super(number, min, max);
    }

    @Override
    public boolean isValidNumber() {
        return INTEGER_PATTERN.matcher(getText()).matches();
    }

    @Override
    public boolean validate() {
        if (isValidNumber()) {
            setNumber(Integer.parseInt(getText()));
            return true;
        } else {
            setNumber(number);
            return false;
        }
    }
}
