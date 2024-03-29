package com.grantranda.retorn.engine.input;

import com.grantranda.retorn.engine.math.Vector3d;
import com.grantranda.retorn.engine.math.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class MouseInput {

    private final boolean[] BUTTONS = new boolean[GLFW_MOUSE_BUTTON_LAST];
    private final Vector3d PREVIOUS_POSITION = new Vector3d();
    private final Vector3d CURRENT_POSITION = new Vector3d();
    private final Vector3d DELTA = new Vector3d();
    private final Vector3f SCROLL_DIRECTION = new Vector3f();
    private final long WINDOW_ID;
    private boolean mouseInWindow = false;
    private boolean scrolling = false;

    public MouseInput(long windowID) {
        this.WINDOW_ID = windowID;

        glfwSetMouseButtonCallback(windowID, (long window, int button, int action, int mods) -> {
            BUTTONS[button] = action == GLFW_PRESS;
        });
        glfwSetCursorPosCallback(windowID, (long window, double xpos, double ypos) -> {
            CURRENT_POSITION.set(xpos, ypos, 0.0f);
        });
        glfwSetScrollCallback(windowID, (long window, double xoffset, double yoffset) -> {
            SCROLL_DIRECTION.set((float) xoffset, (float) yoffset, 0.0f);
            scrolling = true;
        });
        glfwSetCursorEnterCallback(windowID, (long window, boolean entered) -> {
            mouseInWindow = entered;
        });
    }

    public Vector3d getPreviousPosition() {
        return PREVIOUS_POSITION;
    }

    public Vector3d getCurrentPosition() {
        return CURRENT_POSITION;
    }

    public Vector3d getDelta() {
        return DELTA;
    }

    public Vector3f getScrollDirection() {
        return SCROLL_DIRECTION;
    }

    public long getWindowID() {
        return WINDOW_ID;
    }

    public boolean isButtonPressed(int button) {
        return BUTTONS[button];
    }

    public boolean isMouseInWindow() {
        return mouseInWindow;
    }

    public boolean isScrolling() {
        return scrolling;
    }

    public void setScrolling(boolean scrolling) {
        this.scrolling = scrolling;
    }

    public boolean isMouseHovered() {
        return glfwGetWindowAttrib(WINDOW_ID, GLFW_HOVERED) == GLFW_TRUE;
    }

    public void update() {
        DELTA.set(CURRENT_POSITION.x - PREVIOUS_POSITION.x, CURRENT_POSITION.y - PREVIOUS_POSITION.y, 0.0f);
        PREVIOUS_POSITION.set(CURRENT_POSITION.x, CURRENT_POSITION.y, 0.0f);
        setScrolling(false);
    }
}
