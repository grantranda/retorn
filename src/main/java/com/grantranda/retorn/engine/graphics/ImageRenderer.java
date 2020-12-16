package com.grantranda.retorn.engine.graphics;

import com.grantranda.retorn.engine.graphics.display.Resolution;
import com.grantranda.retorn.engine.state.State;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL30.*;

public class ImageRenderer {

    // Renderer data
    private Renderer renderer;
    private State state;
    private Model[] models;

    // Framebuffer data
    private Framebuffer framebuffer;
    private int source; // The source color buffer

    // Image data
    private final Resolution resolution;
    private String path;
    private String format;

    public ImageRenderer(Renderer renderer, Framebuffer framebuffer, Resolution resolution, String path, String format) {
        this(renderer, framebuffer, GL_COLOR_ATTACHMENT0, resolution, path, format);
    }

    public ImageRenderer(Renderer renderer, Framebuffer framebuffer, int source, Resolution resolution, String path, String format) {
        this.renderer = renderer;
        this.framebuffer = framebuffer;
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

    public Framebuffer getFramebuffer() {
        return framebuffer;
    }

    public void setFramebuffer(Framebuffer framebuffer) {
        this.framebuffer = framebuffer;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public Resolution getResolution() {
        return resolution;
    }

    public void setResolution(Resolution resolution) {
        if (this.resolution.compareTo(resolution) != 0) {
            this.resolution.set(resolution.getWidth(), resolution.getHeight());
            framebuffer.resize(GL_COLOR_ATTACHMENT0, resolution);
        }
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

    public void update(State state, Model[] models) {
        this.state = state;
        this.models = models;
    }

    public void render() {
        int renderWidth = resolution.getWidth();
        int renderHeight = resolution.getHeight();
        int bytesPerPixel = 4;

        framebuffer.bind();
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

        framebuffer.unbind();
    }

    public void terminate() {
        framebuffer.delete();
    }
}
