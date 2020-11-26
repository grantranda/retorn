package com.grantranda.retorn.engine.graphics;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class Framebuffer {

    private final int FBO;

    private final ArrayList<Texture> textures = new ArrayList<>();

    public Framebuffer() {

        // Create framebuffer
        FBO = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, FBO);
    }

    public int getFBO() {
        return FBO;
    }

    public ArrayList<Texture> getTextures() {
        return textures;
    }

    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, FBO);
    }

    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void delete() {
        glDeleteFramebuffers(FBO);
    }

    public boolean isComplete() {
        return glCheckFramebufferStatus(FBO) == GL_FRAMEBUFFER_COMPLETE;
    }

    public void attachColorBuffer(int attachment) {
        bind();

        Texture texture = new Texture(GL_TEXTURE_2D, GL_RGB, GL_LINEAR, width, height);
        glFramebufferTexture2D(GL_FRAMEBUFFER, attachment, GL_TEXTURE_2D, texture.getID(), 0);
        textures.add(texture);

        unbind();
    }

    private void attachRenderBuffer() {
        rbo = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rbo);

        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rbo);

        glBindRenderbuffer(GL_RENDERBUFFER, 0);
    }
}
