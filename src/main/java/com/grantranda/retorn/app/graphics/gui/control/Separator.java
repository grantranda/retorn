package com.grantranda.retorn.app.graphics.gui.control;

import lwjgui.scene.layout.StackPane;
import lwjgui.style.BackgroundSolid;
import lwjgui.theme.Theme;

public class Separator extends StackPane {

    public Separator() {
        setMinHeight(1);
        setMaxHeight(1);
        setFillToParentWidth(true);
        setBackground(new BackgroundSolid(Theme.current().getControlAlt()));
    }
}
