package com.grantranda.retorn.engine.input;

import com.grantranda.retorn.engine.graphics.Shader;
import com.grantranda.retorn.engine.graphics.Window;
import com.grantranda.retorn.engine.state.State;

public interface InputHandler {

    void handle(Window window, Shader shader, State state);
}
