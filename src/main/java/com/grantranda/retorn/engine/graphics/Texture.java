package com.grantranda.retorn.engine.graphics;

import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

    private final int ID;
    private final int type;
    private final int pixelFormat;
    private int width, height;

    public Texture(int type, int pixelFormat, int minMagFilter, int width, int height) {
        this(type, pixelFormat, minMagFilter, width, height, null);
    }

    public Texture(int type, int pixelFormat, int minMagFilter, int width, int height, ByteBuffer pixels) {
        this.type = type;
        this.pixelFormat = pixelFormat;
        this.width = width;
        this.height = height;
        this.ID = createTexture(pixels);

        initParameters(minMagFilter);
    }

    public Texture(int type, int pixelFormat, int minMagFilter, String path) {
        this.type = type;
        this.pixelFormat = pixelFormat;
        this.ID = createTexture(path);

        initParameters(minMagFilter);
    }

    public int getID() {
        return ID;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getType() {
        return type;
    }

    public int getPixelFormat() {
        return pixelFormat;
    }

    public void setTexParameteri(int name, int param) {
        bind();
        glTexParameteri(type, name, param);
        unbind();
    }

    public void setTexParameterf(int name, float param) {
        bind();
        glTexParameterf(type, name, param);
        unbind();
    }

    public void bind() {
        glEnable(type);
        glBindTexture(type, ID);
    }

    public void unbind() {
        glDisable(type);
        glBindTexture(type, 0);
    }

    public void delete() {
        glDeleteTextures(ID);
    }

    private void initParameters(int minMagFilter) {
        setTexParameteri(GL_TEXTURE_MIN_FILTER, minMagFilter);
        setTexParameteri(GL_TEXTURE_MAG_FILTER, minMagFilter);
        setTexParameteri(GL_TEXTURE_BASE_LEVEL, 0);
        setTexParameteri(GL_TEXTURE_MAX_LEVEL, 0);
    }

    private int createTexture(ByteBuffer pixels) {
        int id = glGenTextures();
        glBindTexture(type, id);

        if (type == GL_TEXTURE_1D) {
            glTexImage1D(type, 0, pixelFormat, width, 0, pixelFormat, GL_UNSIGNED_BYTE, pixels);
        } else {
            glTexImage2D(type, 0, pixelFormat, width, height, 0, pixelFormat, GL_UNSIGNED_BYTE, pixels);
        }
        return id;
    }

    private int createTexture(String path) {
        ByteBuffer buffer = null;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer x = stack.mallocInt(1);
            IntBuffer y = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            File file = new File(getClass().getClassLoader().getResource(path).getFile());

            buffer = stbi_load(file.getAbsolutePath(), x, y, channels, 4);
            if (buffer == null) {
                throw new RuntimeException("Unable to load texture '" + path + "':\n" + stbi_failure_reason());
            }

            width = x.get();
            height = y.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        int id = createTexture(buffer);

        if (buffer != null) {
            stbi_image_free(buffer);
        }
        return id;
    }
}
