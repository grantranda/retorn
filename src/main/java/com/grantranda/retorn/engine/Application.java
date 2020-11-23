package com.grantranda.retorn.engine;

import com.grantranda.retorn.engine.graphics.display.Window;
import com.grantranda.retorn.engine.graphics.gui.GUI;

public interface Application {

    GUI getGui();

    void init(Window window);

    void update(Window window);

    void render(Window window);

    void terminate();
}
