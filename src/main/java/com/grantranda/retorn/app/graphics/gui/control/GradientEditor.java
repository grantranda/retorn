package com.grantranda.retorn.app.graphics.gui.control;

import com.grantranda.retorn.engine.graphics.paint.ColorGradient;
import lwjgui.paint.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GradientEditor {

    private final ColorGradient gradient;

    public GradientEditor() {
        this(Color.BLACK, Color.WHITE);
    }

    public GradientEditor(Color startColor, Color endColor) {
        this.gradient = new ColorGradient(startColor, endColor);
    }

    public void saveGradient(int width, String path) {
        BufferedImage image = new BufferedImage(width, 1, BufferedImage.TYPE_INT_ARGB);
        Color[] colors = gradient.toArray(width);

        for (int i = 0; i < width; i++) {
            image.setRGB(i, 0, colors[i].getRGBA());
        }

        try {
            ImageIO.write(image, "PNG", new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
