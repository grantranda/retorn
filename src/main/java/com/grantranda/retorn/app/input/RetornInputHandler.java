package com.grantranda.retorn.app.input;

import com.grantranda.retorn.app.graphics.gui.RetornGUI;
import com.grantranda.retorn.app.state.ApplicationState;
import com.grantranda.retorn.app.state.RenderState;
import com.grantranda.retorn.engine.graphics.display.Window;
import com.grantranda.retorn.engine.input.InputHandler;
import com.grantranda.retorn.engine.input.KeyboardInput;
import com.grantranda.retorn.engine.input.MouseInput;
import com.grantranda.retorn.engine.math.Vector2d;
import com.grantranda.retorn.engine.math.Vector3d;
import com.grantranda.retorn.engine.state.State;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

public class RetornInputHandler implements InputHandler {

    public static final float VELOCITY_FACTOR = 0.008f;
    public static final float SCALE_FACTOR_BUTTON = -0.005f;
    public static final float SCALE_FACTOR_SCROLL = -0.025f;

    private final RetornGUI gui;
    private final Vector2d dragOrigin = new Vector2d();

    private final long cursorID;

    private float scaleFactor = 0.0f;
    private float scaleDirection = 0.0f;

    private boolean stateChanged = false;
    private boolean menuToggled = false;
    private boolean draggable = false;
    private boolean scalable = false;

    public RetornInputHandler(RetornGUI gui, long cursorID) {
        this.gui = gui;
        this.cursorID = cursorID;
    }

    @Override
    public void handle(Window window, State state) {
        handleKeyboardInput(window);
        handleMouseInput(window);
        updateState(window, (ApplicationState) state);

        if (stateChanged) {
            gui.updateRenderParameters(((ApplicationState) state).getRenderState());
            stateChanged = false;
        }
    }

    private void handleKeyboardInput(Window window) {
        KeyboardInput keyboardInput = window.getKeyboardInput();

        if (keyboardInput.isKeyPressed(GLFW_KEY_ESCAPE)) {
            glfwSetWindowShouldClose(window.getWindowID(), true);
        }
        if (keyboardInput.isKeyPressed(GLFW_KEY_SPACE)) {
            if (!menuToggled) {
                gui.toggleMenu();
                menuToggled = true;
            }
        } else {
            menuToggled = false;
        }
        if (keyboardInput.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            scaleDirection = -1.0f;
        } else {
            scaleDirection = 1.0f;
        }
    }

    private void handleMouseInput(Window window) {
        MouseInput mouseInput = window.getMouseInput();

        if (mouseInput.isButtonPressed(GLFW_MOUSE_BUTTON_1)) {
            if (!draggable) {
                Vector3d mousePos = mouseInput.getCurrentPosition();
                dragOrigin.set(mousePos.x, mousePos.y);
            }
            if (gui.isMouseOver()) {
                gui.setMousePressed(true);
            }
            if (mouseInput.isMouseInWindow() && !gui.isMousePressed()) {
                draggable = true;
            }
        } else {
            gui.setMousePressed(false);
            draggable = false;
        }
        if (mouseInput.isButtonPressed(GLFW_MOUSE_BUTTON_2)) {
            scalable = true;
            scaleFactor = SCALE_FACTOR_BUTTON;
        } else if (draggable || (mouseInput.isMouseInWindow() && !gui.isMouseOver())) {
            scalable = mouseInput.isScrolling();
            scaleFactor = SCALE_FACTOR_SCROLL;
            scaleDirection = mouseInput.getScrollDirection().y;
        }
    }

    private void updateState(Window window, ApplicationState state) {
        MouseInput mouseInput = window.getMouseInput();
        RenderState renderState = state.getRenderState();
        double offsetX = renderState.getOffset().x;
        double offsetY = renderState.getOffset().y;
        double scale = renderState.getScale();

        if (draggable) {
            Vector3d mousePos = mouseInput.getCurrentPosition();

            double velocityX = (mousePos.x - dragOrigin.x) * VELOCITY_FACTOR;
            double velocityY = (mousePos.y - dragOrigin.y) * VELOCITY_FACTOR;
            double previousOffsetX = offsetX;
            double previousOffsetY = offsetY;

            offsetX += velocityX * scale;
            offsetY -= velocityY * scale;

            if (previousOffsetX != offsetX || previousOffsetY != offsetY) {
                renderState.setOffset(offsetX, offsetY, 0.0f);
                stateChanged = true;
            }
        }

        if (scalable) {
            double previousScale = scale;
            scale *= 1 + scaleDirection * scaleFactor;

            if (previousScale != scale) {
                renderState.setScale(scale);
                stateChanged = true;
            }
        }
    }
}
