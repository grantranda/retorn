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
    private int type;

    public Texture(int type, String path) {
        setType(type);
        ID = load(path);
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
        glBindTexture(type, ID);
    }

    public void unbind() {
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

            width = x.get();
            height = y.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        int id = glGenTextures();
        glBindTexture(type, id);
        glTexParameteri(type, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(type, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(type, GL_TEXTURE_BASE_LEVEL, 0);
        glTexParameteri(type, GL_TEXTURE_MAX_LEVEL, 0);

        if (type == GL_TEXTURE_1D) {
            glTexImage1D(type, 0, pixelFormat, width, 0, pixelFormat, GL_UNSIGNED_BYTE, pixels);
        } else {
            glTexImage2D(type, 0, pixelFormat, width, height, 0, pixelFormat, GL_UNSIGNED_BYTE, pixels);
        }
        unbind();

        if (buffer != null) {
            stbi_image_free(buffer);
        }
        return id;
    }
}
