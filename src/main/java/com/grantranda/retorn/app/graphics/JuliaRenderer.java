package com.grantranda.retorn.app.graphics;

import com.grantranda.retorn.engine.math.Vector2d;

public class JuliaRenderer extends AbstractFractalRenderer {

    public static final double DEFAULT_SEED_X = -0.8;
    public static final double DEFAULT_SEED_Y = 0.156;

    protected final Vector2d seed = new Vector2d(DEFAULT_SEED_X, DEFAULT_SEED_Y);

    public JuliaRenderer(String vertexShaderPath, String fragmentShaderPath) {
        super(vertexShaderPath, fragmentShaderPath);
    }

    public Vector2d getSeed() {
        return seed;
    }

    public void setSeedX(double x) {
        seed.x = x;
    }

    public void setSeedY(double y) {
        seed.y = y;
    }

    @Override
    protected void setUniforms() {
        super.shader.setUniform2d("seed", seed.x, seed.y);
        super.shader.setUniform2d("trappingPointOffset", trappingPointOffset.x, trappingPointOffset.y);
    }
}
