package com.grantranda.retorn.app.input;

import com.grantranda.retorn.app.state.ApplicationState;
import com.grantranda.retorn.app.state.RenderState;
import com.grantranda.retorn.engine.graphics.Shader;
import com.grantranda.retorn.engine.graphics.Window;
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
        updateState((ApplicationState) state);

        if (stateChanged) {
            gui.updateParametersFromState((ApplicationState) state);
            stateChanged = false;
        }
    }

    private void handleKeyboardInput(Window window) {
        if (KeyboardInput.isKeyPressed(GLFW_KEY_ESCAPE)) {
            glfwSetWindowShouldClose(window.getWindowID(), true);
        } else if (KeyboardInput.isKeyPressed(GLFW_KEY_SPACE)) {
            if (!menuToggled) {
                gui.toggleMenu();
                menuToggled = true;
            }
        } else {
            menuToggled = false;
        }
    }

    private void handleMouseInput(Window window) {
        if (MouseInput.isButtonPressed(GLFW_MOUSE_BUTTON_1)) {
            if (gui.isMouseOver()) {
                gui.setMousePressed(true);
            }
            if (MouseInput.isMouseInWindow() && !gui.isMousePressed()) {
                draggable = true;
            }
        } else {
            gui.setMousePressed(false);
            draggable = false;
        }

        if (draggable || (MouseInput.isMouseInWindow() && !gui.isMouseOver())) {
            scalable = MouseInput.isScrolling();
        }
    }

    private void updateState(ApplicationState state) {
        RenderState renderState = state.getRenderState();
        double offsetX = renderState.getOffset().x;
        double offsetY = renderState.getOffset().y;
        double scale = renderState.getScale();

        if (draggable) {
            double previousX = offsetX;
            double previousY = offsetY;

            Vector3d mouseDelta = MouseInput.getDelta();
            offsetX += mouseDelta.x * scale;
            offsetY += mouseDelta.y * scale;

            if (previousX != offsetX || previousY != offsetY) {
                renderState.setOffset(offsetX, offsetY, 0.0f);
                stateChanged = true;
            }
        }

        if (scalable) {
            double previousScale = scale;
            scale *= 1 + MouseInput.getScrollDirection().y * SCALE_FACTOR;

            if (previousScale != scale) {
                renderState.setScale(scale);
                stateChanged = true;
            }
        }
    }
}
