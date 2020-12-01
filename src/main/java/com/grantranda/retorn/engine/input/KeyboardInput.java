package com.grantranda.retorn.engine.input;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LAST;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;

public class KeyboardInput {

    private final boolean[] KEYS = new boolean[GLFW_KEY_LAST];
    private final long WINDOW_ID;

    public KeyboardInput(long windowID) {
        this.WINDOW_ID = windowID;

        glfwSetKeyCallback(windowID, (long window, int key, int scancode, int action, int mods) -> {
            KEYS[key] = action != GLFW_RELEASE;
        });
    }

    public long getWindowID() {
        return WINDOW_ID;
    }

    public boolean isKeyPressed(int key) {
        return KEYS[key];
    }
}
