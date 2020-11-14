package com.grantranda.retorn.engine.input;

import com.grantranda.retorn.engine.graphics.display.Window;
import com.grantranda.retorn.engine.state.State;

public interface InputHandler {

    void handle(Window window, State state);
}
