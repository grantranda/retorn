package com.grantranda.retorn.engine.graphics.gui;

import java.util.regex.Matcher;
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
    public void updateNumber() {
        String text = getText();
        Matcher matcher = INTEGER_PATTERN.matcher(text);

        if (matcher.matches()) {
            setNumber(Integer.parseInt(getText()));
            setText(text);
        } else {
            setNumber(number);
        }

        setText(number.toString());
    }
}
