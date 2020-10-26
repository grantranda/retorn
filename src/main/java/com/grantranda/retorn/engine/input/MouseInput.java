package com.grantranda.retorn.engine.input;

import com.grantranda.retorn.engine.math.Vector3d;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

public class MouseInput {

    private static final boolean[] BUTTONS = new boolean[GLFW_MOUSE_BUTTON_LAST];
    private static final Vector3d PREVIOUS_POSITION = new Vector3d();
    private static final Vector3d CURRENT_POSITION = new Vector3d();
    private static final Vector3d DELTA = new Vector3d();
    private static final Vector3d SCROLL_DIRECTION = new Vector3d();
    private static long windowID = NULL;
    private static boolean mouseInWindow = false;
    private static boolean scrolling = false;

    private MouseInput() {

    }

    public static void initialize(long windowID) {
        if (MouseInput.windowID != NULL) throw new ExceptionInInitializerError("MouseInput already initialized");

        MouseInput.windowID = windowID;

        glfwSetMouseButtonCallback(windowID, (long window, int button, int action, int mods) -> {
            BUTTONS[button] = action == GLFW_PRESS;
        });
        glfwSetCursorPosCallback(windowID, (long window, double xpos, double ypos) -> {
            CURRENT_POSITION.x = xpos;
            CURRENT_POSITION.y = ypos;
        });
        glfwSetScrollCallback(windowID, (long window, double xoffset, double yoffset) -> {
            SCROLL_DIRECTION.x = xoffset;
            SCROLL_DIRECTION.y = yoffset;
            scrolling = true;
        });
        glfwSetCursorEnterCallback(windowID, (long window, boolean entered) -> {
            mouseInWindow = entered;
        });
    }

    public static Vector3d getPreviousPosition() {
        return PREVIOUS_POSITION;
    }

    public static Vector3d getCurrentPosition() {
        return CURRENT_POSITION;
    }

    public static Vector3d getDelta() {
        return DELTA;
    }

    public static Vector3d getScrollDirection() {
        return SCROLL_DIRECTION;
    }

    public static long getWindowID() {
        return windowID;
    }

    public static boolean isButtonPressed(int button) {
        return BUTTONS[button];
    }

    public static boolean isMouseInWindow() {
        return mouseInWindow;
    }

    public static boolean isScrolling() {
        return scrolling;
    }

    public static void setScrolling(boolean scrolling) {
        MouseInput.scrolling = scrolling;
    }

    public static boolean isMouseHovered() {
        return glfwGetWindowAttrib(windowID, GLFW_HOVERED) == GLFW_TRUE;
    }

    public static void update() {
        DELTA.x = CURRENT_POSITION.x - PREVIOUS_POSITION.x;
        DELTA.y = CURRENT_POSITION.y - PREVIOUS_POSITION.y;

        PREVIOUS_POSITION.x = CURRENT_POSITION.x;
        PREVIOUS_POSITION.y = CURRENT_POSITION.y;

        setScrolling(false);
    }
}
