package com.grantranda.retorn.app.graphics.gui.control;

import com.grantranda.retorn.app.Retorn;
import com.grantranda.retorn.engine.graphics.Texture;
import com.grantranda.retorn.engine.graphics.paint.ColorGradient;
import com.grantranda.retorn.engine.graphics.paint.ColorStop;
import com.grantranda.retorn.engine.input.MouseInput;
import com.grantranda.retorn.engine.util.MathUtils;
import lwjgui.LWJGUIDialog;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.paint.Color;
import lwjgui.scene.Context;
import lwjgui.scene.control.Button;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.HBox;
import lwjgui.scene.layout.VBox;
import org.lwjgl.BufferUtils;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.system.MemoryStack;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.Random;

import static org.lwjgl.nanovg.NanoVG.nvgBeginPath;
import static org.lwjgl.nanovg.NanoVG.nvgClosePath;
import static org.lwjgl.nanovg.NanoVG.nvgFill;
import static org.lwjgl.nanovg.NanoVG.nvgFillColor;
import static org.lwjgl.nanovg.NanoVG.nvgFillPaint;
import static org.lwjgl.nanovg.NanoVG.nvgLinearGradient;
import static org.lwjgl.nanovg.NanoVG.nvgRGBf;
import static org.lwjgl.nanovg.NanoVG.nvgRect;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_1D;

public class GradientEditor extends VBox {

    public static final int MAX_STOPS = 15;
    public static final int STOP_WIDTH = 10;
    public static final int STOP_HEIGHT = 10;
    public static final float HALF_STOP_WIDTH = STOP_WIDTH / 2.0f;

    private int gradientImageWidth = 256;
    private int gradientWidth;
    private int gradientHeight;
    private ColorGradient gradient;
    private ColorStop selectedStop;

    private final Random random = new Random();

    private final ColorSelector colorSelector = new ColorSelector();
    private final Button deleteStopButton = new Button("Delete");
    private final Button saveGradientButton = new Button("Save");
    private final Button loadGradientButton = new Button("Load");
    private final Button randomGradientButton = new Button("Random");
    private final Button applyGradientButton = new Button("Apply");
    private final HBox gradientHBox = new HBox();
    private final HBox colorStopsHBox = new HBox();
    private final HBox stopColorHBox = new HBox();
    private final HBox buttonHBox = new HBox();

    public GradientEditor(Context context, MouseInput mouseInput, int gradientWidth, int gradientHeight) {
        this(context, mouseInput, gradientWidth, gradientHeight, Color.BLACK, Color.WHITE);
    }

    public GradientEditor(Context context, MouseInput mouseInput, int gradientWidth, int gradientHeight, Color startColor, Color endColor) {
        this.gradientWidth = gradientWidth;
        this.gradientHeight = gradientHeight;
        this.gradient = new ColorGradient(startColor, endColor);

        setAlignment(Pos.CENTER);

        colorSelector.setSupportsAlpha(false);
        deleteStopButton.setMinWidth(50);
        saveGradientButton.setMinWidth(gradientWidth / 3.0f - 5);
        loadGradientButton.setMinWidth(gradientWidth / 3.0f - 5);
        randomGradientButton.setMinWidth(gradientWidth / 3.0f - 5);
        applyGradientButton.setMinWidth(gradientWidth / 3.0f - 5);

        Label stopColorLabel = new Label("Color");
        stopColorLabel.setMinWidth(50);
        Heading selectedStopHeading = new Heading("Selected Stop");
        selectedStopHeading.setPadding(new Insets(10, 0, 5, 0));
        Heading gradientHeading = new Heading("Gradient");
        gradientHeading.setPadding(new Insets(10, 0, 5, 0));

        VBox applyVBox = new VBox();
        applyVBox.setFillToParentWidth(true);
        applyVBox.setPadding(new Insets(10, 0, 0, 0));
        applyVBox.setAlignment(Pos.CENTER);
        applyVBox.getChildren().add(applyGradientButton);

        gradientHBox.setMinWidth(gradientWidth);
        gradientHBox.setMinHeight(gradientHeight);
        gradientHBox.setAlignment(Pos.CENTER);
        gradientHBox.setPadding(new Insets(0, 0, 40, 0));
        colorStopsHBox.setMinWidth(gradientWidth + STOP_WIDTH);
        colorStopsHBox.setMinHeight(20);
        colorStopsHBox.setBackgroundLegacy(Color.DARK_GRAY);
        stopColorHBox.setAlignment(Pos.CENTER);
        stopColorHBox.setSpacing(40);
        stopColorHBox.setPadding(new Insets(5, 0, 0, 0));
        stopColorHBox.getChildren().addAll(stopColorLabel, colorSelector, deleteStopButton);
        buttonHBox.setSpacing(10);
        buttonHBox.setPadding(new Insets(10, 0, 10, 0));
        buttonHBox.getChildren().addAll(saveGradientButton, loadGradientButton, randomGradientButton);

        getChildren().addAll(
                gradientHBox, colorStopsHBox, selectedStopHeading,
                new Separator(),
                stopColorHBox, gradientHeading,
                new Separator(),
                buttonHBox,
                new Separator(),
                applyVBox
        );

        selectedStop = gradient.getStops().get(0);
        colorSelector.setColor(selectedStop.getColor());

        // Set event handlers
        colorStopsHBox.setOnMouseEntered(event -> context.setSelected(colorStopsHBox));
        colorStopsHBox.setOnMousePressed(event -> {
            selectedStop = getStop((float) mouseInput.getCurrentPosition().x);

            if (selectedStop != null) colorSelector.setColor(selectedStop.getColor());
        });
        colorStopsHBox.setOnMouseReleased(event -> {
            if (gradient.getStops().size() < MAX_STOPS) {
                Color stopColor = new Color(random.nextInt(254) + 1, random.nextInt(254) + 1, random.nextInt(254) + 1);
                addStop((float) mouseInput.getCurrentPosition().x, stopColor);
            }
        });
        colorStopsHBox.setOnMouseDragged(event -> {
            float minX = (float) gradientHBox.getX();
            float maxX = minX + (float) gradientHBox.getWidth();

            if (selectedStop != null) {
                float mouseX = (float) mouseInput.getCurrentPosition().x;

                if (mouseX >= minX - HALF_STOP_WIDTH && mouseX <= maxX + HALF_STOP_WIDTH) {
                    float newPosition = MathUtils.inverseLerp(minX, maxX, mouseX);
                    int index = gradient.getStops().indexOf(selectedStop);
                    int newIndex = gradient.setStopPosition(index, newPosition);
                    selectedStop = gradient.getStops().get(newIndex);
                }
            }
        });
        colorSelector.setOnColorUpdate(event -> {
            if (selectedStop != null) selectedStop.setColor(colorSelector.getColor());
        });
        deleteStopButton.setOnAction(event -> deleteSelectedStop());
        saveGradientButton.setOnAction(event -> saveGradient(gradientImageWidth, "pal.png"));
        loadGradientButton.setOnAction(event -> loadGradient());
        randomGradientButton.setOnAction(event -> {
            gradient.randomize();
            if (selectedStop != null) colorSelector.setColor(selectedStop.getColor());
        });
        applyGradientButton.setOnAction(event -> {
            applyGradient();
        });
    }

    public int getGradientImageWidth() {
        return gradientImageWidth;
    }

    public void setGradientImageWidth(int gradientImageWidth) {
        this.gradientImageWidth = gradientImageWidth;
    }

    public int getGradientWidth() {
        return gradientWidth;
    }

    public void setGradientWidth(int gradientWidth) {
        this.gradientWidth = gradientWidth;
    }

    public int getGradientHeight() {
        return gradientHeight;
    }

    public void setGradientHeight(int gradientHeight) {
        this.gradientHeight = gradientHeight;
    }

    public ColorGradient getGradient() {
        return gradient;
    }

    public void setGradient(ColorGradient gradient) {
        this.gradient = gradient;
    }

    public void saveGradient(int width, String defaultFilename) {
        File defaultPath = new File(System.getProperty("user.home") + "/" + defaultFilename);
        File selectedFile = LWJGUIDialog.showSaveFileDialog("Save Gradient", defaultPath, "Image Files (*.png)", "png", true);

        if (selectedFile == null) return;

        BufferedImage image = new BufferedImage(width, 1, BufferedImage.TYPE_INT_ARGB);
        Color[] colors = gradient.toArray(width);

        for (int i = 0; i < width; i++) {
            image.setRGB(i, 0, colors[i].getRGBA());
        }

        try {
            ImageIO.write(image, "PNG", selectedFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadGradient() {
        File defaultPath = new File(System.getProperty("user.home"));
        File selectedFile = LWJGUIDialog.showOpenFileDialog("Load Gradient", defaultPath, "Image Files (*.png, *.jpg)", "png", "jpg");

        if (selectedFile == null || !selectedFile.exists()) return;

        try {
            BufferedImage image = ImageIO.read(selectedFile);

            int bytesPerPixel = 4;
            int[] rgbArray = new int[image.getWidth()];
            ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * bytesPerPixel);
            buffer.order(ByteOrder.nativeOrder());

            for (int i = 0; i < image.getWidth(); i++) {
                rgbArray[i] = new Color(image.getRGB(i, 0)).getRGBA();
            }

            buffer.asIntBuffer().put(rgbArray);
            Retorn.INSTANCE.setTexture(new Texture(GL_TEXTURE_1D, GL_RGBA, GL_NEAREST, image.getWidth(), 1, buffer));

            gradient.getStops().clear();
            gradient.addStop(0.0f, Color.BLACK);
            gradient.addStop(1.0f, Color.WHITE);

            selectedStop = gradient.getStops().get(0);
            colorSelector.setColor(selectedStop.getColor());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void applyGradient() {
        int bytesPerPixel = 4;
        int[] rgbArray = new int[gradientImageWidth];
        Color[] colors = gradient.toArray(gradientImageWidth);
        ByteBuffer buffer = BufferUtils.createByteBuffer(gradientImageWidth * bytesPerPixel);
        buffer.order(ByteOrder.nativeOrder());

        for (int i = 0; i < colors.length; i++) {
            rgbArray[i] = colors[i].getRGBA();
        }

        buffer.asIntBuffer().put(rgbArray);
        Retorn.INSTANCE.setTexture(new Texture(GL_TEXTURE_1D, GL_RGBA, GL_NEAREST, gradientImageWidth, 1, buffer));
    }

    public void addStop(float x, Color color) {
        float minX = (float) gradientHBox.getX();
        float maxX = minX + (float) gradientHBox.getWidth();

        if (x < minX || x > maxX || getStop(x) != null) return;

        float relativeX = MathUtils.inverseLerp(minX, maxX, x);
        selectedStop = gradient.getStops().get(gradient.addStop(relativeX, color));
        colorSelector.setColor(selectedStop.getColor());
    }

    public void deleteSelectedStop() {
        if (selectedStop != null && gradient.getStops().size() > 2) {
            int index = gradient.getStops().indexOf(selectedStop);
            gradient.removeStop(index);
            selectedStop = gradient.getStops().get(index - 1);
            colorSelector.setColor(selectedStop.getColor());
        }
    }

    public ColorStop getStop(float x) {
        float minX = (float) gradientHBox.getX();
        float maxX = minX + (float) gradientHBox.getWidth();

        for (ColorStop stop : gradient.getStops()) {
            float stopX = MathUtils.lerp(minX, maxX, stop.getPosition());
            if (x >= stopX - HALF_STOP_WIDTH && x <= stopX + HALF_STOP_WIDTH) return stop;
        }
        return null;
    }

    public void render(long nvgContext) {
        float minX = (float) gradientHBox.getX();
        float maxX = minX + (float) gradientHBox.getWidth();
        float gradientY = (float) gradientHBox.getY();
        LinkedList<ColorStop> stops = gradient.getStops();

        // Draw solid color before first stop
        ColorStop firstStop = stops.getFirst();
        float firstStopX = MathUtils.lerp(minX, maxX, firstStop.getPosition());
        if (firstStopX > minX) {
            drawRect(nvgContext, minX, gradientY, firstStopX - minX, gradientHeight, firstStop.getColor());
        }

        // Draw solid color after last stop
        ColorStop lastStop = stops.getLast();
        float lastStopX = MathUtils.lerp(minX, maxX, lastStop.getPosition());
        if (lastStopX < maxX) {
            drawRect(nvgContext, lastStopX, gradientY, maxX - lastStopX, gradientHeight, lastStop.getColor());
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {

            // Draw gradient
            for (int i = 0; i < stops.size() - 1; i++) {
                ColorStop currentStop = stops.get(i);
                ColorStop nextStop = stops.get(i + 1);

                float startX = MathUtils.lerp(minX, maxX, currentStop.getPosition());
                float endX = MathUtils.lerp(minX, maxX, nextStop.getPosition());

                Color startColor = currentStop.getColor();
                Color endColor = nextStop.getColor();

                NVGColor gradientStartColor = nvgRGBf(startColor.getRedF(), startColor.getGreenF(), startColor.getBlueF(), NVGColor.callocStack(stack));
                NVGColor gradientEndColor = nvgRGBf(endColor.getRedF(), endColor.getGreenF(), endColor.getBlueF(), NVGColor.callocStack(stack));
                NVGPaint gradientFill = nvgLinearGradient(
                        nvgContext, startX, gradientY, endX, gradientY,
                        gradientStartColor, gradientEndColor,
                        NVGPaint.callocStack(stack)
                );

                nvgBeginPath(nvgContext);
                nvgRect(nvgContext, startX, gradientY, endX - startX, gradientHeight);
                nvgFillPaint(nvgContext, gradientFill);
                nvgFill(nvgContext);
                nvgClosePath(nvgContext);
            }
        }

        // Draw stops
        for (ColorStop stop : gradient.getStops()) {
            float stopX = MathUtils.lerp(minX, maxX, stop.getPosition());
            float stopY = (float) (colorStopsHBox.getY() + colorStopsHBox.getHeight() / 2.0f - STOP_HEIGHT / 2.0f - 1.0f);

            // Outline selected stop
            if (stop == selectedStop) {
                drawRect(nvgContext, stopX - HALF_STOP_WIDTH - 3, stopY - 3, STOP_WIDTH + 6, STOP_HEIGHT + 6, Color.BLACK);
                drawRect(nvgContext, stopX - HALF_STOP_WIDTH - 1, stopY - 1, STOP_WIDTH + 2, STOP_HEIGHT + 2, Color.WHITE);
            }

            drawRect(nvgContext, stopX - HALF_STOP_WIDTH, stopY, STOP_WIDTH, STOP_HEIGHT, stop.getColor());
        }
    }

    private void drawRect(long nvgContext, float x, float y, float width, float height, Color color) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            NVGColor nvgColor = nvgRGBf(color.getRedF(), color.getGreenF(), color.getBlueF(), NVGColor.callocStack(stack));

            nvgBeginPath(nvgContext);
            nvgRect(nvgContext, x, y, width, height);
            nvgFillColor(nvgContext, nvgColor);
            nvgFill(nvgContext);
            nvgClosePath(nvgContext);
        }
    }
}
