package com.grantranda.retorn.app.graphics.gui;

import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.WindowHandle;
import lwjgui.scene.WindowManager;
import lwjgui.scene.WindowThread;
import lwjgui.scene.layout.Pane;

public class Popup {

    private final int width;
    private final int height;
    private final String title;
    private final Pane root;

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

    public void show() {
        WindowManager.runLater(() -> {
            new WindowThread(width, height, title) {
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
                }
            }.start();
        });
    }
}
