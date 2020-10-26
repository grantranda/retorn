package com.grantranda.retorn.engine.input;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LAST;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.system.MemoryUtil.*;

public class KeyboardInput {

    private static final boolean[] KEYS = new boolean[GLFW_KEY_LAST];
    private static long windowID = NULL;

    private KeyboardInput() {

    }

    public static void initialize(long windowID) {
        if (KeyboardInput.windowID != NULL) throw new ExceptionInInitializerError("KeyboardInput already initialized");

        KeyboardInput.windowID = windowID;

        glfwSetKeyCallback(windowID, (long window, int key, int scancode, int action, int mods) -> {
            KEYS[key] = action != GLFW_RELEASE;
        });
    }

    public static long getWindowID() {
        return windowID;
    }

    public static boolean isKeyPressed(int key) {
        return KEYS[key];
    }
}
