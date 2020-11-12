package com.grantranda.retorn.engine.graphics;

import com.grantranda.retorn.engine.input.KeyboardInput;
import com.grantranda.retorn.engine.input.MouseInput;
import com.grantranda.retorn.engine.util.FPSCounter;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {

    private final String title;
    private final FPSCounter fpsCounter = new FPSCounter();

    private long windowID;

    private int width;
    private int height;

    private float contentScaleX;
    private float contentScaleY;

    private boolean vSync;
    private boolean resized = false;

    public Window(String title, int width, int height, boolean vSync) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.vSync = vSync;
    }

    public String getTitle() {
        return title;
    }

    public FPSCounter getFpsCounter() {
        return fpsCounter;
    }

    public long getWindowID() {
        return windowID;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getContentScaleX() {
        return contentScaleX;
    }

    public float getContentScaleY() {
        return contentScaleY;
    }

    public boolean isvSync() {
        return vSync;
    }

    public void setvSync(boolean vSync) {
        this.vSync = vSync;
        glfwSwapInterval(vSync ? 1 : 0);
    }

    public boolean isResized() {
        return resized;
    }

    public void setResized(boolean resized) {
        this.resized = resized;
    }

    public void resize(int width, int height) {
        setWidth(width);
        setHeight(height);
        setResized(true);
        glfwSetWindowSize(windowID, width, height);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(windowID);
    }

    public double getAspectRatio() {
        return (double) width / height;
    }

    public void initialize() {
        initializeGlfw();
    }

    public void terminate() {
        glfwFreeCallbacks(windowID);
        glfwDestroyWindow(windowID);
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    public void update() {
        glfwSwapBuffers(windowID);
        glfwPollEvents();
        fpsCounter.update();
    }

    public void render() {

    }

    public void restoreRenderState() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_STENCIL_TEST);
    }

    private void initializeGlfw() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) throw new IllegalStateException("Error initializing GLFW");

        // Configure GLFW
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        // Create the window
        windowID = glfwCreateWindow(width, height, title, NULL, NULL);
        if (windowID == NULL) throw new RuntimeException("Failed to create the GLFW window");

        KeyboardInput.initialize(windowID);
        MouseInput.initialize(windowID);

        // Set window resize callback
        glfwSetFramebufferSizeCallback(windowID, (window, width, height) -> {
            resize(width, height);
        });
        glfwSetWindowContentScaleCallback(windowID, (window, contentScaleX, contentScaleY) -> {
            this.contentScaleX = contentScaleX;
            this.contentScaleY = contentScaleY;
        });

        try (MemoryStack stack = stackPush()) {
            IntBuffer   w = stack.mallocInt(1);
            IntBuffer   h = stack.mallocInt(1);
            FloatBuffer x = stack.mallocFloat(1);
            FloatBuffer y = stack.mallocFloat(1);

            glfwGetFramebufferSize(windowID, w, h);
            width = w.get(0);
            height = h.get(0);

            glfwGetWindowContentScale(windowID, x, y);
            contentScaleX = x.get(0);
            contentScaleY = y.get(0);
        }

        // Set the window position
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(
                windowID,
                (vidMode.width() - width) / 2,
                (vidMode.height() - height) / 2
        );

        glfwMakeContextCurrent(windowID);
        glfwShowWindow(windowID);
        GL.createCapabilities();
        if (isvSync()) glfwSwapInterval(1);

        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_STENCIL_TEST); // TODO: Possibly add window options class and check before enabling
    }
}
