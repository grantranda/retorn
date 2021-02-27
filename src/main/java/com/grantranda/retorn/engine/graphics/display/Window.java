package com.grantranda.retorn.engine.graphics.display;

import com.grantranda.retorn.engine.input.KeyboardInput;
import com.grantranda.retorn.engine.input.MouseInput;
import com.grantranda.retorn.engine.math.Vector3i;
import com.grantranda.retorn.engine.util.DisplayUtils;
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
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_STENCIL_TEST;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    public static final int MIN_WIDTH = 400;
    public static final int MIN_HEIGHT = 400;
    public static final long DEFAULT_CURSOR = NULL;

    private final String title;
    private final Resolution resolution;
    private final Vector3i position = new Vector3i();
    private final FPSCounter fpsCounter = new FPSCounter();

    private KeyboardInput keyboardInput;
    private MouseInput mouseInput;

    private long windowID;
    private long cursorID = DEFAULT_CURSOR;

    private int fpsLimit = 0;

    private float contentScaleX;
    private float contentScaleY;

    private boolean fullscreen;
    private boolean vSync;
    private boolean resized = false;

    public Window(String title, int width, int height, boolean vSync) {
        this.title = title;
        this.resolution = new Resolution(width, height);
        this.vSync = vSync;
    }

    public String getTitle() {
        return title;
    }

    public Resolution getResolution() {
        return resolution;
    }

    public int getWidth() {
        return resolution.getWidth();
    }

    public int getHeight() {
        return resolution.getHeight();
    }

    public Vector3i getPosition() {
        return position;
    }

    public void setPosition(int x, int y) {
        position.set(x, y, 0);
        glfwSetWindowPos(windowID, x, y);
    }

    public FPSCounter getFpsCounter() {
        return fpsCounter;
    }

    public int getFpsLimit() {
        return fpsLimit;
    }

    public void setFpsLimit(int fpsLimit) {
        this.fpsLimit = fpsLimit;
    }

    public KeyboardInput getKeyboardInput() {
        return keyboardInput;
    }

    public MouseInput getMouseInput() {
        return mouseInput;
    }

    public long getWindowID() {
        return windowID;
    }

    public long getCursor() {
        return cursorID;
    }

    public void setCursor(long cursorID) {
        this.cursorID = cursorID;
        glfwSetCursor(windowID, cursorID);
    }

    public float getContentScaleX() {
        return contentScaleX;
    }

    public float getContentScaleY() {
        return contentScaleY;
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public void setFullscreen(boolean fullscreen) {
        setFullscreen(fullscreen, glfwGetPrimaryMonitor());
    }

    public void setFullscreen(boolean fullscreen, long monitor) {
        this.fullscreen = fullscreen;
        GLFWVidMode vidMode = glfwGetVideoMode(monitor);

        if (fullscreen) {
            glfwSetWindowMonitor(windowID, monitor, 0, 0, getWidth(), getHeight(), vidMode.refreshRate());
        } else {
            glfwSetWindowMonitor(windowID, NULL, 0, 0, getWidth(), getHeight(), vidMode.refreshRate());
            setPosition(position.x, position.y);
        }
        setVSync(isVSync()); // vSync needs to be set again after switching between windowed and fullscreen
    }

    public boolean isVSync() {
        return vSync;
    }

    public void setVSync(boolean vSync) {
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
        resolution.set(width, height);
        setResized(true);
        glfwSetWindowSize(windowID, width, height);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(windowID);
    }

    public void moveToCenter() {
        moveToCenter(glfwGetPrimaryMonitor());
    }

    public void moveToCenter(long monitor) {
        GLFWVidMode vidMode = glfwGetVideoMode(monitor);
        setPosition(
                (vidMode.width() - getWidth()) / 2,
                (vidMode.height() - getHeight()) / 2
        );
    }

    public void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) throw new IllegalStateException("Error initializing GLFW");

        // Configure GLFW
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_SCALE_TO_MONITOR, GLFW_TRUE);
        glfwWindowHint(GLFW_AUTO_ICONIFY, GLFW_TRUE);

        // Create the window
        windowID = glfwCreateWindow(getWidth(), getHeight(), title, NULL, NULL);
        if (windowID == NULL) {
            terminate();
            throw new RuntimeException("Failed to create the GLFW window");
        }
        glfwMakeContextCurrent(windowID);
        GL.createCapabilities();

        glfwSetWindowSizeLimits(windowID, MIN_WIDTH, MIN_HEIGHT, GLFW_DONT_CARE, GLFW_DONT_CARE);

        keyboardInput = new KeyboardInput(windowID);
        mouseInput = new MouseInput(windowID);

        // Set callbacks
        glfwSetWindowPosCallback(windowID, (window, xpos, ypos) -> {
            position.set(xpos, ypos, 0);
        });
        glfwSetFramebufferSizeCallback(windowID, (window, width, height) -> {
            resize(width, height);
        });
        glfwSetWindowContentScaleCallback(windowID, (window, contentScaleX, contentScaleY) -> {
            this.contentScaleX = contentScaleX;
            this.contentScaleY = contentScaleY;
        });

        // Set initial size and content scale
        try (MemoryStack stack = stackPush()) {
            IntBuffer   w = stack.mallocInt(1);
            IntBuffer   h = stack.mallocInt(1);
            FloatBuffer x = stack.mallocFloat(1);
            FloatBuffer y = stack.mallocFloat(1);

            glfwGetFramebufferSize(windowID, w, h);
            resolution.set(w.get(0), h.get(0));

            glfwGetWindowContentScale(windowID, x, y);
            contentScaleX = x.get(0);
            contentScaleY = y.get(0);
        }

        moveToCenter();

        glfwShowWindow(windowID);
        if (isVSync()) glfwSwapInterval(1);

        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_STENCIL_TEST); // TODO: Possibly add window options class and check before enabling
    }

    public void terminate() {
        glfwFreeCallbacks(windowID);
        if (cursorID != DEFAULT_CURSOR) {
            glfwDestroyCursor(cursorID);
        }
        glfwDestroyWindow(windowID);
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    public void update() {
        mouseInput.update();
        fpsCounter.update();
        setResized(false);
        glfwSwapBuffers(windowID);
        glfwPollEvents();
        DisplayUtils.sync(fpsLimit);
    }

    public void render() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_STENCIL_TEST);
    }
}
