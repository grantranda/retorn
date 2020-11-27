package com.grantranda.retorn.engine.graphics;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class Framebuffer {

    private final int width;
    private final int height;
    private int fbo;
    private int rbo;

    private final ArrayList<Texture> textures = new ArrayList<>();

    public Framebuffer(int width, int height) {
        this.width = width;
        this.height = height;
        fbo = glGenFramebuffers();

        attachColorBuffer(GL_COLOR_ATTACHMENT0);
        attachRenderBuffer();

        validateCompleteness();

        unbind();
    }

    public int getFbo() {
        return fbo;
    }

    public int getRbo() {
        return rbo;
    }

    public ArrayList<Texture> getTextures() {
        return textures;
    }

    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
    }

    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void delete() {
        glDeleteFramebuffers(fbo);
    }

    public void validateCompleteness() {
        String statusMessage;
        int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);

        switch (status) {
            case GL_FRAMEBUFFER_COMPLETE:
                return;
            case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
                statusMessage = "GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT";
                break;
            case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
                statusMessage = "GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT";
                break;
            case GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
                statusMessage = "GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER";
                break;
            case GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:
                statusMessage = "GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER";
                break;
            case GL_FRAMEBUFFER_UNSUPPORTED:
                statusMessage = "GL_FRAMEBUFFER_UNSUPPORTED";
                break;
            case GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE:
                statusMessage = "GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE";
                break;
            case GL_FRAMEBUFFER_UNDEFINED:
                statusMessage = "GL_FRAMEBUFFER_UNDEFINED";
                break;
            default:
                statusMessage = "" + status;
        }

        throw new RuntimeException("Framebuffer not complete. Status: " + statusMessage);
    }

    public void attachColorBuffer(int attachment) {
        bind();

        Texture texture = new Texture(GL_TEXTURE_2D, GL_RGB, GL_LINEAR, width, height);
        glFramebufferTexture2D(GL_FRAMEBUFFER, attachment, GL_TEXTURE_2D, texture.getID(), 0);
        textures.add(texture);

        unbind();
    }

    private void attachRenderBuffer() {
        bind();

        rbo = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rbo);

        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rbo);

        glBindRenderbuffer(GL_RENDERBUFFER, 0);
    }
}
