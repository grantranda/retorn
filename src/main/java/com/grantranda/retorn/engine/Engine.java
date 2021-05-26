package com.grantranda.retorn.engine;

import com.grantranda.retorn.engine.graphics.display.Window;
import org.lwjgl.Version;
import org.lwjgl.opengl.GL11;

import static com.grantranda.retorn.app.Main.logger;

public class Engine implements Runnable {

    private final Window window;
    private final Application application;

    public Engine(String title, int width, int height, boolean vSync, Application application) {
        this.application = application;
        window = new Window(title, width, height, vSync);
    }

    @Override
    public void run() {
        init();
        loop();
        terminate();
    }

    private void init() {
        window.init();
        application.init(window);

        logger.info("LWJGL version: " + Version.getVersion());
        logger.info("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
    }

    private void loop() {
        while (!window.shouldClose()) {
            update();
            render();
        }
    }

    private void update() {
        window.update();
        application.update(window);
    }

    private void render() {
        application.render(window);
        window.render();
    }

    private void terminate() {
        application.terminate();
        window.terminate();
    }
}
