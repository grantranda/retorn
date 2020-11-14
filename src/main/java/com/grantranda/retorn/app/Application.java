package com.grantranda.retorn.app;

import com.grantranda.retorn.engine.graphics.display.Window;
import com.grantranda.retorn.engine.graphics.gui.GUI;

public interface Application {

    GUI getGui();

    void initialize(Window window);

    void update(Window window);

    void render(Window window);

    void terminate();
}
