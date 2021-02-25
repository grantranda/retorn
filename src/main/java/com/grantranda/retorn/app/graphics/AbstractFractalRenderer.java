package com.grantranda.retorn.app.graphics;

import com.grantranda.retorn.engine.graphics.Model;
import com.grantranda.retorn.engine.graphics.Shader;
import com.grantranda.retorn.engine.graphics.display.Window;
import com.grantranda.retorn.engine.math.Matrix4f;
import com.grantranda.retorn.engine.state.State;

public abstract class AbstractFractalRenderer extends AbstractRenderer {

    protected final String vertexShaderPath;
    protected final String fragmentShaderPath;

    protected Shader shader;
    protected Matrix4f projectionMatrix;

    public AbstractFractalRenderer(String vertexShaderPath, String fragmentShaderPath) {
        super();
        this.vertexShaderPath = vertexShaderPath;
        this.fragmentShaderPath = fragmentShaderPath;
    }

    public String getVertexShaderPath() {
        return vertexShaderPath;
    }

    public String getFragmentShaderPath() {
        return fragmentShaderPath;
    }

    @Override
    public Shader getActiveShader() {
        return shader;
    }

    @Override
    public void init(Window window) {
        projectionMatrix = Matrix4f.orthographic(-2.5f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f);

        shader = new Shader(vertexShaderPath, fragmentShaderPath);
        shader.setUniformMatrix4f("projection_matrix", projectionMatrix);
        shader.setUniform1i("palette_texture", 0);
    }

    @Override
    public void terminate() {

    }

    @Override
    public void render(Window window, State state, Model[] models, boolean updateViewport) {
        clear();
        shader.bind();
        setUniforms();

        for (Model model : models) {
            shader.setUniformMatrix4f("model_matrix", model.getModelMatrix());
            model.render();
        }
        shader.unbind();
    }

    protected abstract void setUniforms();
}
