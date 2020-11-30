package com.grantranda.retorn.app.graphics;

import com.grantranda.retorn.app.state.RenderState;
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

        setProjectionType(window, projectionType);
    }

    public void terminate() {

    }

    public void render(Window window, RenderState renderState, Model[] models) {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        shader.bind();

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

        // TODO
        if (!test) {

            // TODO: Use different resolution
            saveImage("test.png", "PNG", new Resolution(windowWidth, windowHeight), GL_FRAMEBUFFER);
            test = true;
        }
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
