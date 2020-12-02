package com.grantranda.retorn.engine.graphics;

import com.grantranda.retorn.engine.graphics.display.Window;
import com.grantranda.retorn.engine.state.State;

public interface Renderer {

    void init(Window window);

    void terminate();

    void render(Window window, State state, Model[] models);
}
