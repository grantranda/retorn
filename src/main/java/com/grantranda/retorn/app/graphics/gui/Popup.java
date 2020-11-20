package com.grantranda.retorn.app.graphics.gui;

import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.WindowHandle;
import lwjgui.scene.WindowManager;
import lwjgui.scene.WindowThread;
import lwjgui.scene.layout.Pane;

import static org.lwjgl.glfw.GLFW.*;

public class Popup {

    private final int width;
    private final int height;
    private final String title;
    private final Pane root;
    private Thread thread;
    private WindowHandle windowHandle;

    public Popup(int width, int height, String title, Pane root) {
        this.width = width;
        this.height = height;
        this.title = title;
        this.root = root;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Pane getRoot() {
        return root;
    }

    public void init() {
        thread = new Thread(width, height, title);
        WindowManager.runLater(() -> thread.start());
        glfwHideWindow(thread.getWindow().getID());
    }

    public void terminate() {
        glfwSetWindowShouldClose(thread.getWindow().getID(), true);
    }

    public void show() {
        windowHandle.isVisible(true);
    }

    class Thread extends WindowThread {

        public Thread(int width, int height, String title) {
            super(width, height, title);
        }

        @Override
        protected void init(Window window) {
            super.init(window);
            window.setScene(new Scene(root, width, height));
            window.show();
        }

        @Override
        protected void setupHandle(WindowHandle handle) {
            super.setupHandle(handle);
            handle.canResize(false);
            windowHandle = handle;
        }
    }
}
