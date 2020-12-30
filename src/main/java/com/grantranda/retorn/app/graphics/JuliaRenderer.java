package com.grantranda.retorn.app.graphics;

public class JuliaRenderer extends AbstractFractalRenderer {

    public JuliaRenderer(String vertexShaderPath, String fragmentShaderPath) {
        super(vertexShaderPath, fragmentShaderPath);
    }

    @Override
    protected void setUniforms() {
        super.shader.setUniform2d("seed", -0.8, 0.156);
    }
}
