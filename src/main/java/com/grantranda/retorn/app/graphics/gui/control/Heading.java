package com.grantranda.retorn.app.graphics.gui.control;

import lwjgui.font.FontStyle;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.control.Label;

public class Heading extends Label {

    public Heading(String text) {
        super(text);

        setFillToParentWidth(true);
        setAlignment(Pos.CENTER_LEFT);
        setFontStyle(FontStyle.BOLD);
        setPadding(new Insets(10, 0, 0, 0));
    }
}
