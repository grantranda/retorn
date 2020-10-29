package com.grantranda.retorn.engine.graphics.gui;

import com.grantranda.retorn.engine.graphics.Shader;
import com.grantranda.retorn.engine.graphics.Window;
import com.grantranda.retorn.engine.state.State;

public interface GUI {

    void initialize(Window window, State state);

    void terminate();

    void update(Window window, Shader shader);

    void render(Window window);
}
