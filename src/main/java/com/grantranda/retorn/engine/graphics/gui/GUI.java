package com.grantranda.retorn.engine.graphics.gui;

import com.grantranda.retorn.engine.graphics.display.Window;
import com.grantranda.retorn.engine.state.State;

public interface GUI {

    void init(Window window, State state);

    void terminate();

    void update(Window window, State state);

    void render(Window window);
}
