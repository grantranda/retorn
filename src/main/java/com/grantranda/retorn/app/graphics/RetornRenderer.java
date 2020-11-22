package com.grantranda.retorn.app.graphics;

import com.grantranda.retorn.engine.graphics.Model;
import com.grantranda.retorn.engine.graphics.display.Resolution;
import com.grantranda.retorn.engine.math.Matrix4f;
import com.grantranda.retorn.engine.graphics.display.Window;
import com.grantranda.retorn.engine.graphics.Shader;
import com.grantranda.retorn.engine.math.Matrix4f.Projection;

import static org.lwjgl.opengl.GL11.*;

public class RetornRenderer {

    public static final float FOV = 80.0f;

    private Projection projectionType;
    private Shader shader;

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

        setProjectionType(window, projectionType);
    }

    public void render(Window window, Model[] models) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        Resolution resolution = window.getResolution();

        if (window.isResized()) {
            if (projectionType == Projection.PERSPECTIVE) {
                setProjectionType(window, projectionType);
            }
        }

        shader.bind();

        // Set uniforms
        shader.setUniform2f("window_size", resolution.getWidth(), resolution.getHeight());
        shader.setUniform1i("tex", 0);

        glDisable(GL_CULL_FACE);

        // Render models
        for (Model model : models) {
            shader.setUniformMatrix4f("model_matrix", model.getModelMatrix());
            model.render();
        }

        shader.unbind();
    }
}
