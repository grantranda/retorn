package com.grantranda.retorn.engine.util;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class FPSCounter {

    private int nbFrames;
    private int fps;
    private double mpf;
    private double lastTime;

    public FPSCounter() {

    }

    public int getNbFrames() {
        return nbFrames;
    }

    public int getFps() {
        return fps;
    }

    public double getMpf() {
        return mpf;
    }

    public double getLastTime() {
        return lastTime;
    }

    public void update() {
        nbFrames++;
        if (glfwGetTime() - lastTime >= 1.0f) {
            mpf = 1000.0 / (double) nbFrames;
            fps = nbFrames;
            nbFrames = 0;
            lastTime += 1.0f;
        }
    }
}
