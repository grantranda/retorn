package com.grantranda.retorn.engine.graphics;

import com.grantranda.retorn.engine.graphics.display.Resolution;
import com.grantranda.retorn.engine.graphics.display.Window;
import com.grantranda.retorn.engine.state.State;

public interface Renderer {

    Resolution getViewportResolution();

    void setViewport(int x, int y, int width, int height);

    void init();

    void terminate();

    void render(Resolution resolution, State state, Model[] models);
}
