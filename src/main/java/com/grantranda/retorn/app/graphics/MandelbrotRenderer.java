package com.grantranda.retorn.app.graphics;

public class MandelbrotRenderer extends AbstractFractalRenderer {

    public MandelbrotRenderer(String vertexShaderPath, String fragmentShaderPath) {
        super(vertexShaderPath, fragmentShaderPath);
    }

    @Override
    protected void setUniforms() {
        super.shader.setUniform2d("trappingPointOffset", trappingPointOffset.x, trappingPointOffset.y);
    }
}
