package com.grantranda.retorn.engine.graphics.gui;

import lwjgui.geometry.Insets;
import lwjgui.paint.Color;
import lwjgui.scene.control.ColorPicker;

public class ColorSelector extends ColorPicker {

    public ColorSelector() {
        this(Color.BLUE);
    }

    public ColorSelector(Color color) {
        super(color);
        setMinSize(20.0f, 18.0f);
        setBorderRadii(2.0f);
        setGraphicTextGap(0.0f);
        setBorder(Insets.EMPTY);
        setPadding(Insets.EMPTY);
    }

    @Override
    public void setColor(Color color) {
        super.setColor(color);
        setText("");
        // TODO: Change graphic size?
    }
}
