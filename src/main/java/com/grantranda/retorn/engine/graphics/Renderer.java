package com.grantranda.retorn.engine.graphics;

import com.grantranda.retorn.engine.graphics.display.Resolution;
import com.grantranda.retorn.engine.graphics.display.Window;
import com.grantranda.retorn.engine.state.State;

public interface Renderer {

    void init(Window window);

    void terminate();

    void render(Resolution resolution, State state, Model[] models);
}
