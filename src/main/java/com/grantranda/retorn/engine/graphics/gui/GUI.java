package com.grantranda.retorn.engine.graphics.gui;

import com.grantranda.retorn.engine.graphics.Shader;
import com.grantranda.retorn.engine.graphics.Window;

public interface GUI {

    void initialize(Window window);

    void terminate();

    void update(Window window, Shader shader);

    void render(Window window);
}
