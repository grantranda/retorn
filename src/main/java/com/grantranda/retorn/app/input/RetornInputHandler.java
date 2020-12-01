package com.grantranda.retorn.app.input;

import com.grantranda.retorn.app.state.ApplicationState;
import com.grantranda.retorn.app.state.RenderState;
import com.grantranda.retorn.engine.graphics.display.Window;
import com.grantranda.retorn.app.graphics.gui.RetornGUI;
import com.grantranda.retorn.engine.input.InputHandler;
import com.grantranda.retorn.engine.input.KeyboardInput;
import com.grantranda.retorn.engine.input.MouseInput;
import com.grantranda.retorn.engine.math.Vector3d;
import com.grantranda.retorn.engine.state.State;

import static org.lwjgl.glfw.GLFW.*;

public class RetornInputHandler implements InputHandler {

    public static final float SCALE_FACTOR = -0.025f;

    private final RetornGUI gui;

    private boolean stateChanged = false;
    private boolean menuToggled = false;
    private boolean draggable = false;
    private boolean scalable = false;

    public RetornInputHandler(RetornGUI gui) {
        this.gui = gui;
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
        } else if (keyboardInput.isKeyPressed(GLFW_KEY_SPACE)) {
            if (!menuToggled) {
                gui.toggleMenu();
                menuToggled = true;
            }
        } else {
            menuToggled = false;
        }
    }

    private void handleMouseInput(Window window) {
        MouseInput mouseInput = window.getMouseInput();

        if (mouseInput.isButtonPressed(GLFW_MOUSE_BUTTON_1)) {
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

        if (draggable || (mouseInput.isMouseInWindow() && !gui.isMouseOver())) {
            scalable = mouseInput.isScrolling();
        }
    }

    private void updateState(Window window, ApplicationState state) {
        MouseInput mouseInput = window.getMouseInput();
        RenderState renderState = state.getRenderState();
        double offsetX = renderState.getOffset().x;
        double offsetY = renderState.getOffset().y;
        double scale = renderState.getScale();

        if (draggable) {
            double previousX = offsetX;
            double previousY = offsetY;

            Vector3d mouseDelta = mouseInput.getDelta();
            offsetX -= mouseDelta.x * scale;
            offsetY += mouseDelta.y * scale;

            if (previousX != offsetX || previousY != offsetY) {
                renderState.setOffset(offsetX, offsetY, 0.0f);
                stateChanged = true;
            }
        }

        if (scalable) {
            double previousScale = scale;
            scale *= 1 + mouseInput.getScrollDirection().y * SCALE_FACTOR;

            if (previousScale != scale) {
                renderState.setScale(scale);
                stateChanged = true;
            }
        }
    }
}
