package com.grantranda.retorn.engine.graphics;

import com.grantranda.retorn.engine.graphics.display.Resolution;
import com.grantranda.retorn.engine.graphics.display.Window;
import com.grantranda.retorn.engine.state.State;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.glfwHideWindow;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.opengl.GL30.*;

public class ImageRenderer {

    // Renderer data
    private Renderer renderer;
    private State state;
    private Model[] models;

    // Image data
    private final Resolution resolution;
    private String path;
    private String format;

    // The source color buffer
    private int source;

    public ImageRenderer(Renderer renderer, Resolution resolution, String path, String format) {
        this(renderer, resolution, path, format, GL_COLOR_ATTACHMENT0);
    }

    public ImageRenderer(Renderer renderer, Resolution resolution, String path, String format, int source) {
        this.renderer = renderer;
        this.resolution = new Resolution(resolution.getWidth(), resolution.getHeight());
        this.path = path;
        this.format = format;
        this.source = source;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    public Resolution getResolution() {
        return resolution;
    }

    public void setResolution(int width, int height) {
        resolution.set(width, height);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public void update(State state, Model[] models) {
        this.state = state;
        this.models = models;
    }

    public void render(Window window) {
        int windowWidth = window.getWidth();
        int windowHeight = window.getHeight();
        int renderWidth = resolution.getWidth();
        int renderHeight = resolution.getHeight();
        int bytesPerPixel = 4;

        // Get viewport position
        IntBuffer viewportData = BufferUtils.createIntBuffer(4);
        glGetIntegerv(GL_VIEWPORT, viewportData);
        int viewportX = viewportData.get(0); // TODO: Not needed?
        int viewportY = viewportData.get(1);

        window.resize(renderWidth, renderHeight);
        glfwHideWindow(window.getWindowID());
        glViewport(0, 0, renderWidth, renderHeight);
        renderer.render(resolution, state, models);

        glReadBuffer(source);
        ByteBuffer buffer = BufferUtils.createByteBuffer(resolution.getArea() * bytesPerPixel);

        glReadPixels(0, 0, renderWidth, renderHeight, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        File file = new File(path);
        BufferedImage image = new BufferedImage(renderWidth, renderHeight, BufferedImage.TYPE_INT_ARGB);

        for(int x = 0; x < renderWidth; x++) {
            for(int y = 0; y < renderHeight; y++) {
                int i = (x + (renderWidth * y)) * bytesPerPixel;
                int r = buffer.get(i) & 0xFF;
                int g = buffer.get(i + 1) & 0xFF;
                int b = buffer.get(i + 2) & 0xFF;
                image.setRGB(x, renderHeight - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
            }
        }

        try {
            ImageIO.write(image, format, file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        window.resize(windowWidth, windowHeight);
        glViewport(viewportX, viewportY, windowWidth, windowHeight);
        glfwShowWindow(window.getWindowID());
    }
}
