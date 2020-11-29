package com.grantranda.retorn.app.graphics;

import com.grantranda.retorn.engine.graphics.Model;
import com.grantranda.retorn.engine.graphics.display.Resolution;
import com.grantranda.retorn.engine.math.Matrix4f;
import com.grantranda.retorn.engine.graphics.display.Window;
import com.grantranda.retorn.engine.graphics.Shader;
import com.grantranda.retorn.engine.math.Matrix4f.Projection;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class RetornRenderer {

    public static final float FOV = 80.0f;

    private Projection projectionType;
    private Shader shader;

    // TODO: Remove
//    private Shader quadShader;
//    private Framebuffer framebuffer;
//    private Model quad;

    public RetornRenderer(Projection projectionType) {
        this.projectionType = projectionType;
    }

    public Projection getProjectionType() {
        return projectionType;
    }

    public void setProjectionType(Window window, Projection projectionType) {
        this.projectionType = projectionType;

        if (projectionType == Projection.ORTHOGRAPHIC) {
            Matrix4f projection_matrix = Matrix4f.orthographic(-2.5f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f);
            shader.setUniformMatrix4f("projection_matrix", projection_matrix);
        } else if (projectionType == Projection.PERSPECTIVE) {
            Matrix4f projection_matrix = Matrix4f.perspective(FOV, (float) window.getResolution().getAspectRatio(), 1.0f, 1000.0f);
            shader.setUniformMatrix4f("projection_matrix", projection_matrix);
        }
    }

    public Shader getShader() {
        return shader;
    }

    public void init(Window window) {
        shader = new Shader("shaders/vertex.vert", "shaders/fragment.frag");
        shader.setUniform1i("palette_texture", 0);

        // TODO: Remove
//        quadShader = new Shader("shaders/v.vert", "shaders/f.frag");
//        quadShader.setUniform1i("scene_texture", 0);
//        framebuffer = new Framebuffer(window.getResolution().getWidth(), window.getResolution().getHeight());

        setProjectionType(window, projectionType);

        // TODO: Remove
//        float[] quadVertices = {
//                -1.0f,  1.0f, 0.0f, // TL
//                -1.0f, -1.0f, 0.0f, // BL
//                 1.0f, -1.0f, 0.0f, // BR
//                 1.0f,  1.0f, 0.0f, // TR
//        };
//
////        float[] quadVertices = new float[]{
////                -2.5f,  1.0f, 0.0f, // TL
////                -2.5f, -1.0f, 0.0f, // BL
////                1.0f, -1.0f, 0.0f, // BR
////                1.0f,  1.0f, 0.0f, // TR
////        };
//
////        float[] textureCoordinates = new float[] {
////                0.0f, 1.0f, // TL
////                0.0f, 0.0f, // BL
////                1.0f, 0.0f, // BR
////                1.0f, 1.0f, // TR
////        };
//
//        float[] textureCoordinates = new float[] {
//                -1.0f,  1.0f, // TL
//                -1.0f, -1.0f, // BL
//                 1.0f, -1.0f, // BR
//                 1.0f,  1.0f, // TR
//        };
//
//        byte[] quadIndices = new byte[]{
//                0, 1, 3, // First triangle
//                3, 1, 2, // Second triangle
//        };
//
//        Mesh quadMesh = new Mesh(quadVertices, null, quadIndices);
//        quad = new Model(quadMesh, framebuffer.getTextures().get(GL_COLOR_ATTACHMENT0));
    }

    public void terminate() {
        // TODO: Remove
//        quad.delete();
//        framebuffer.delete();
    }

    public void render(Window window, Model[] models) {
        // TODO: Remove
        // Bind custom framebuffer
        //framebuffer.bind();
        //glViewport(0, 0, framebuffer.getTexture(GL_COLOR_ATTACHMENT0).getWidth(), framebuffer.getTexture(GL_COLOR_ATTACHMENT0).getHeight());
        //glEnable(GL_DEPTH_TEST);

        glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        shader.bind();

        Resolution resolution = window.getResolution();
        shader.setUniform2f("window_size", resolution.getWidth(), resolution.getHeight());

        if (window.isResized()) {
            if (projectionType == Projection.PERSPECTIVE) {
                setProjectionType(window, projectionType);
            }
        }

        glDisable(GL_CULL_FACE);

        // Render models
        for (Model model : models) {
            shader.setUniformMatrix4f("model_matrix", model.getModelMatrix());
            model.render();
        }

        shader.unbind();

        // TODO: Remove
        // Bind default framebuffer
//        framebuffer.unbind();
//        glViewport(0, 0, window.getResolution().getWidth(), window.getResolution().getHeight());
//        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
//        glClear(GL_COLOR_BUFFER_BIT);
//
//        quadShader.bind();
//        quadShader.setUniformMatrix4f("model_matrix", quad.getModelMatrix());
//        glDisable(GL_DEPTH_TEST);
//        glEnable(GL_TEXTURE_2D);
//        glBindTexture(GL_TEXTURE_2D, framebuffer.getTexture(GL_COLOR_ATTACHMENT0).getID());
//        quad.getMesh().render();
//        glDisable(GL_TEXTURE_2D);
//        glBindTexture(GL_TEXTURE_2D, 0);
//        quadShader.unbind();
    }

    public void saveImage(String path, String format, Resolution resolution, int source) {
        int width = resolution.getWidth();
        int height = resolution.getHeight();
        int bpp = 4;

        glReadBuffer(GL_FRAMEBUFFER);
        ByteBuffer buffer = BufferUtils.createByteBuffer(resolution.getArea() * bpp);
        glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        File file = new File(path);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for(int x = 0; x < width; x++)
        {
            for(int y = 0; y < height; y++)
            {
                int i = (x + (width * y)) * bpp;
                int r = buffer.get(i) & 0xFF;
                int g = buffer.get(i + 1) & 0xFF;
                int b = buffer.get(i + 2) & 0xFF;
                image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
            }
        }

        try {
            ImageIO.write(image, format, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
