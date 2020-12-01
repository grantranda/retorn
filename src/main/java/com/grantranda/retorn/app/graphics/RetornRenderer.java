package com.grantranda.retorn.app.graphics;

import com.grantranda.retorn.app.state.RenderState;
import com.grantranda.retorn.engine.graphics.Model;
import com.grantranda.retorn.engine.graphics.display.Resolution;
import com.grantranda.retorn.engine.math.Matrix4f;
import com.grantranda.retorn.engine.graphics.display.Window;
import com.grantranda.retorn.engine.graphics.Shader;
import com.grantranda.retorn.engine.math.Vector3f;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class RetornRenderer {

    private Shader shader;

    private boolean test; // TODO

    public RetornRenderer() {

    }

    public Shader getShader() {
        return shader;
    }

    public void init(Window window) {
        Matrix4f projection_matrix = Matrix4f.orthographic(-2.5f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f);

        shader = new Shader("shaders/vertex.vert", "shaders/fragment.frag");
        shader.setUniformMatrix4f("projection_matrix", projection_matrix);
        shader.setUniform1i("palette_texture", 0);
    }

    public void terminate() {

    }

    public void render(Window window, RenderState renderState, Model[] models) {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        glDisable(GL_CULL_FACE);

        shader.bind();

        Vector3f viewportPos = updateViewport(window);

        int windowWidth = window.getResolution().getWidth();
        int windowHeight = window.getResolution().getHeight();
        double pixelWidth = 3.5f / (windowWidth - viewportPos.x * 2);
        double pixelHeight = 2.0f / (windowHeight - viewportPos.y * 2);
        double translatedOffsetX = renderState.getOffset().x * pixelWidth;
        double translatedOffsetY = renderState.getOffset().y * pixelHeight;

        shader.setUniform1i("max_iterations", renderState.getMaxIterations());
        shader.setUniform1d("scale", renderState.getScale());
        shader.setUniform2d("offset", translatedOffsetX, translatedOffsetY);
        shader.setUniform2f("window_size", windowWidth, windowHeight);

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

    private Vector3f updateViewport(Window window) {
        int renderWidth = 1920; // TODO: Read from state
        int renderHeight = 1080;
        float renderAspectRatio = (float) renderWidth / renderHeight;

        int windowWidth = window.getResolution().getWidth();
        int windowHeight = window.getResolution().getHeight();
        int width = windowWidth;
        int height = (int) (width / renderAspectRatio);

        if (height > windowHeight) {
            height = windowHeight;
            width = (int) (height * renderAspectRatio);
        }

        int viewportX = (windowWidth - width) / 2;
        int viewportY = (windowHeight - height) / 2;

        glViewport(viewportX, viewportY, width, height);

        return new Vector3f(viewportX, viewportY, 0.0f);
    }
}
