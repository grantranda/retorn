package com.grantranda.retorn.engine.graphics;

import com.grantranda.retorn.app.Main;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

    private final int ID;
    private int width, height;
    private int type;

    public Texture(int type, String path) {
        setType(type);
        ID = load(path);
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

    public void setType(int type) {
        if (type == GL_TEXTURE_1D || type == GL_TEXTURE_2D) {
            this.type = type;
        } else {
            this.type = GL_TEXTURE_2D;
        }
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

    private int load(String path) {
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

        int id = glGenTextures();
        glBindTexture(type, id);
        glTexParameteri(type, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(type, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(type, GL_TEXTURE_BASE_LEVEL, 0);
        glTexParameteri(type, GL_TEXTURE_MAX_LEVEL, 0);

        if (type == GL_TEXTURE_1D) {
            glTexImage1D(type, 0, GL_RGBA, 256, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        } else {
            glTexImage2D(type, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        }
        unbind();

        if (buffer != null) {
            stbi_image_free(buffer);
        }
        return id;
    }
}
