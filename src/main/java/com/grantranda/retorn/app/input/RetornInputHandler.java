package com.grantranda.retorn.app.input;

import com.grantranda.retorn.engine.graphics.Shader;
import com.grantranda.retorn.engine.graphics.Window;
import com.grantranda.retorn.app.graphics.gui.RetornGUI;
import com.grantranda.retorn.engine.input.InputHandler;
import com.grantranda.retorn.engine.input.KeyboardInput;
import com.grantranda.retorn.engine.input.MouseInput;
import com.grantranda.retorn.engine.math.Vector3d;

import static org.lwjgl.glfw.GLFW.*;

public class RetornInputHandler implements InputHandler {

    public static final float SCALE_FACTOR = -0.025f;

    private final RetornGUI gui;

    private final Vector3d offset = new Vector3d();
    private double scale = 1.0f;

    private boolean menuToggled = false;
    private boolean draggable = false;

    public RetornInputHandler(RetornGUI gui) {
        this.gui = gui;
    }

    @Override
    public void handle(Window window, Shader shader) {
        handleKeyboardInput(window, shader);
        handleMouseInput(window, shader);
        updateApplication(window, shader);
    }

    private void handleKeyboardInput(Window window, Shader shader) {
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

    private void handleMouseInput(Window window, Shader shader) {
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
    }

    private void updateApplication(Window window, Shader shader) {
        if (draggable) {
            Vector3d mouseDelta = MouseInput.getDelta();
            offset.x += mouseDelta.x * scale;
            offset.y += mouseDelta.y * scale;
        }

        if (draggable || (MouseInput.isMouseInWindow() && !gui.isMouseOver())) {
            if (MouseInput.isScrolling()) {
                scale *= 1 + MouseInput.getScrollDirection().y * SCALE_FACTOR;
            }
        }

        shader.setUniform1d("scale", scale);
        shader.setUniform2d("offset", offset.x, offset.y);
    }
}
