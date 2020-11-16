package com.grantranda.retorn.app.graphics.gui;

import com.grantranda.retorn.app.graphics.gui.control.ColorSelector;
import com.grantranda.retorn.app.graphics.gui.control.NumberFieldd;
import com.grantranda.retorn.app.graphics.gui.control.NumberFieldi;
import com.grantranda.retorn.app.graphics.gui.control.Parameter;
import com.grantranda.retorn.app.state.ApplicationState;
import com.grantranda.retorn.app.state.RenderState;
import com.grantranda.retorn.app.util.StateUtils;
import com.grantranda.retorn.engine.graphics.Shader;
import com.grantranda.retorn.engine.graphics.display.Resolution;
import com.grantranda.retorn.engine.graphics.display.Window;
import com.grantranda.retorn.engine.graphics.gui.GUI;
import com.grantranda.retorn.engine.input.MouseInput;
import com.grantranda.retorn.engine.math.Vector3d;
import com.grantranda.retorn.engine.state.State;
import com.grantranda.retorn.engine.util.DisplayUtils;
import lwjgui.geometry.Pos;
import lwjgui.paint.Color;
import lwjgui.scene.WindowManager;
import lwjgui.scene.control.*;
import lwjgui.scene.layout.BorderPane;
import lwjgui.scene.layout.StackPane;
import lwjgui.scene.layout.VBox;

import java.util.TreeSet;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class RetornGUI implements GUI {

    public static final int RIGHT_PANE_WIDTH = 400;

    private long nvgContext;
    private boolean mouseOver;
    private boolean mousePressed;
    private boolean menuShown = true;

    private lwjgui.scene.Window guiWindow;

    private BorderPane root;
    private BorderPane menu;

    private Button hideMenuButton;
    private Button showMenuButton;
    private Button updateButton;
    private Button resetButton;
    private Button saveButton;
    private Button loadButton;
    private Parameter<NumberFieldi> maxIterationsParam;
    private Parameter<NumberFieldd> scaleParam;
    private Parameter<NumberFieldd> xParam;
    private Parameter<NumberFieldd> yParam;
    private ToggleButton vSyncParam;
    private ColorSelector colorSelector;
    private ComboBox<String> resolutionParam;
    private Label fpsDisplay;

    private BorderPane customResolutionRoot;
    private Popup customResolutionPopup;

    public RetornGUI() {

    }

    public boolean isMouseOver() {
        return mouseOver;
    }

    public void setMouseOver(boolean mouseOver) {
        this.mouseOver = mouseOver;
    }

    public boolean isMousePressed() {
        return mousePressed;
    }

    public void setMousePressed(boolean mousePressed) {
        this.mousePressed = mousePressed;
    }

    public boolean isMenuShown() {
        return menuShown;
    }

    public lwjgui.scene.Window getGuiWindow() {
        return guiWindow;
    }

    public void hideMenu() {
        menu.setVisible(false);
        root.setRight(showMenuButton);
        showMenuButton.setVisible(true);
        menuShown = false;
    }

    public void showMenu() {
        showMenuButton.setVisible(false);
        root.setRight(menu);
        menu.setVisible(true);
        menuShown = true;
    }

    public void toggleMenu() {
        if (menuShown) {
            hideMenu();
        } else {
            showMenu();
        }
    }

    @Override
    public void init(Window window, State state) {
        initGui(window, (ApplicationState) state);
        initNvg(window);
    }

    private void initGui(Window window, ApplicationState state) {
        guiWindow = WindowManager.generateWindow(window.getWindowID());
        guiWindow.setWindowAutoClear(false);
        guiWindow.show();

        initResolutionSelection();
        initColorSelector(window);
        initMenu(window);
        initRoot(window);
        setEventHandlers(window, state);

        guiWindow.getScene().setRoot(root);

        updateParameters();
        updateState(state);
    }

    private void initNvg(Window window) {
        nvgContext = nvgCreate(0); // TODO: Change flags?
        if (nvgContext == NULL) throw new RuntimeException("Failed to create a NanoVG context");
    }

    @Override
    public void terminate() {
        WindowManager.dispose();
        nvgDelete(nvgContext);
    }

    @Override
    public void update(Window window, Shader shader, State state) {
        WindowManager.update();
        updateInput(window);
        updateGui(window);
        updateUniforms(shader, (ApplicationState) state);
    }

    private void updateInput(Window window) {
        Vector3d mousePos = MouseInput.getCurrentPosition();
        int width  = window.getResolution().getWidth();
        boolean mouseOverMenu = MouseInput.isMouseInWindow()
                && isMenuShown()
                && (mousePos.x >= width - RIGHT_PANE_WIDTH && mousePos.x <= width);

        setMouseOver(mouseOverMenu);
    }

    private void updateGui(Window window) {
        fpsDisplay.setText("FPS: " + window.getFpsCounter().getFps());
    }

    private void updateParameters() {
        maxIterationsParam.getControl().validate();
        scaleParam.getControl().validate();
        xParam.getControl().validate();
        yParam.getControl().validate();
    }

    public void updateParametersFromState(ApplicationState state) {
        RenderState renderState = state.getRenderState();

        maxIterationsParam.getControl().setNumber(renderState.getMaxIterations());
        scaleParam.getControl().setNumber(renderState.getScale());
        xParam.getControl().setNumber(renderState.getOffset().x);
        yParam.getControl().setNumber(renderState.getOffset().y);
    }

    private void updateState(ApplicationState state) {
        RenderState renderState = state.getRenderState();
        renderState.setMaxIterations(maxIterationsParam.getControl().getNumber());
        renderState.setScale(scaleParam.getControl().getNumber());
        renderState.setOffset(xParam.getControl().getNumber(), yParam.getControl().getNumber(), 0.0f);
    }

    private void updateUniforms(Shader shader, ApplicationState state) {
        RenderState renderState = state.getRenderState();

        shader.setUniform1i("max_iterations", renderState.getMaxIterations());
        shader.setUniform1d("scale", renderState.getScale());
        shader.setUniform2d("offset", renderState.getOffset().x, renderState.getOffset().y);
    }

    @Override
    public void render(Window window) {
        guiWindow.render();
        renderNvg(window);
        window.restoreRenderState();
    }

    private void renderNvg(Window window) {
        int width  = (int) (window.getResolution().getWidth() / window.getContentScaleX());
        int height = (int) (window.getResolution().getHeight() / window.getContentScaleY());
        int midX = width / 2;
        int midY = height / 2;
        int startX = midX - 50;
        int startY = midY - 30;
        int endX = midX + 100;
        int endY = midY + 60;
        int w = 100;
        int h = 60;

        nvgBeginFrame(nvgContext, width, height, Math.max(window.getContentScaleX(), window.getContentScaleY()));

        // TODO: Gradient
//        try (MemoryStack stack = MemoryStack.stackPush()) {
//            NVGColor c1 = nvgRGBf(1.0f, 0.0f, 0.0f, NVGColor.callocStack(stack));
//            NVGColor c2 = nvgRGBf(0.0f, 0.0f, 1.0f, NVGColor.callocStack(stack));
//
//            NVGPaint gradient = nvgLinearGradient(nvgContext, startX, startY, endX, endY, c1, c2, NVGPaint.callocStack(stack));
//
//            nvgBeginPath(nvgContext);
//            NanoVG.nvgRoundedRectVarying(nvgContext, startX, startY+h*0.5f, w, h*0.5f, 0, 0, 4, 4);
//
//            nvgFillPaint(nvgContext, gradient);
//            nvgFill(nvgContext);
//            nvgClosePath(nvgContext);
//        }

        nvgEndFrame(nvgContext);
    }

    private void initResolutionSelection() {
        initCustomResolutionRoot();

        customResolutionPopup = new Popup(300, 100, "Custom Resolution", customResolutionRoot);
        resolutionParam = new ComboBox<>();
        resolutionParam.setPrefWidth(200);

        Resolution monitorResolution = DisplayUtils.getMonitorResolution();
        TreeSet<Resolution> resolutions = DisplayUtils.getMonitorResolutions();

        for (Resolution resolution : resolutions) {
            if (resolution.getArea() > monitorResolution.getArea()) {
                break;
            }
            resolutionParam.getItems().add(resolution.toString());
        }
        resolutionParam.getItems().add("Custom");

//        // Set default option
//        resolutionParam.setValue(resolutions.first().toString());
    }

    // TODO Create custom resolution root pane
    private void initCustomResolutionRoot() {
        customResolutionRoot = new BorderPane();
    }

    private void initColorSelector(Window window) {

        // TODO: Remove following commented color selector code
//        DraggablePane dragPane1 = new DraggablePane();
//        dragPane1.setBackgroundLegacy(Color.DARK_GRAY);
//        dragPane1.setPrefHeight(64);
//        dragPane1.setPrefWidth(50);
//
//        dragPane1.setOnMouseEntered(event -> {
//            setMouseOver(true);
//        });
//        dragPane1.setOnMouseExited(event -> {
//            setMouseOver(false);
//        });
//
//        //Put pane in center of screen
//        dragPane1.setAbsolutePosition(window.getWidth() / 2.0f, window.getHeight() / 2.0f);
//
//        //Add text
//        ColorSelector colorPicker = new ColorSelector();
//        dragPane1.getChildren().add(colorPicker);
////        Label label = new Label("I'm draggable!");
////        label.setMouseTransparent(true);
////        dragPane1.getChildren().add(label);
//
//        //Test that it is sticky!
//        dragPane1.setAbsolutePosition(0, 0);
//
//        //Add it to root
//        root.getChildren().add(dragPane1);
    }

    private void initMenu(Window window) {
        maxIterationsParam = new Parameter<>(RIGHT_PANE_WIDTH, "Max Iterations", new NumberFieldi(100, 0, 100000));
        scaleParam = new Parameter<>(RIGHT_PANE_WIDTH, "Scale", new NumberFieldd(1.0));
        xParam = new Parameter<>(RIGHT_PANE_WIDTH, "X", new NumberFieldd(0.0));
        yParam = new Parameter<>(RIGHT_PANE_WIDTH, "Y", new NumberFieldd(0.0));
        hideMenuButton = new Button("X");
        showMenuButton = new Button("|||");
        updateButton = new Button("Update");
        resetButton = new Button("Reset");
        saveButton = new Button("Save");
        loadButton = new Button("Load");
        vSyncParam = new ToggleButton("vSync");
        colorSelector = new ColorSelector();
        fpsDisplay = new Label("FPS: " + window.getFpsCounter().getFps());
        fpsDisplay.setAlignment(Pos.BOTTOM_LEFT);
        fpsDisplay.setFillToParentWidth(true);

        VBox menuTop = new VBox();
        menuTop.setAlignment(Pos.TOP_LEFT);
        menuTop.setFillToParentWidth(true);
        menuTop.getChildren().add(hideMenuButton);
        menuTop.getChildren().add(maxIterationsParam);
        menuTop.getChildren().add(scaleParam);
        menuTop.getChildren().addAll(xParam, yParam);
        menuTop.getChildren().add(resolutionParam);
        menuTop.getChildren().add(vSyncParam);
        menuTop.getChildren().add(colorSelector);
        menuTop.getChildren().add(updateButton);
        menuTop.getChildren().add(resetButton);
        menuTop.getChildren().add(saveButton);
        menuTop.getChildren().add(loadButton);

        menu = new BorderPane();
        menu.setMinWidth(RIGHT_PANE_WIDTH);
        menu.setMaxWidth(RIGHT_PANE_WIDTH);
        menu.setPrefHeight(window.getResolution().getHeight());
        menu.setAlignment(Pos.TOP_LEFT);
        menu.setFillToParentHeight(true);
        menu.setBackgroundLegacy(new Color(.9, .9, .9, 0.95));
        menu.setBottom(fpsDisplay);
        menu.setTop(menuTop);
    }

    private void initRoot(Window window) {
        root = new BorderPane();
        root.setPrefSize(window.getResolution().getWidth(), window.getResolution().getHeight());
        root.setCenter(new StackPane()); // Set center so BorderPane alignment is correct
        root.setRight(menu);
    }

    private void setEventHandlers(Window window, ApplicationState state) {
        hideMenuButton.setOnAction(event -> hideMenu());
        showMenuButton.setOnAction(event -> showMenu());
        updateButton.setOnAction(event -> {
            updateParameters();
            updateState(state);
        });
        resetButton.setOnAction(event -> {
            state.getRenderState().reset();
            updateParametersFromState(state);
        });
        saveButton.setOnAction(event -> StateUtils.saveState(state.getRenderState(), "retorn_parameters.json"));
        loadButton.setOnAction(event -> {
            StateUtils.loadState(state, RenderState.class);
            updateParametersFromState(state);
        });
        resolutionParam.setOnAction(event -> {
            String value = resolutionParam.getValue();
            if (value.equals("Custom")) {
                customResolutionPopup.show();
            } else {
                int xIndex = value.indexOf('x');
                int w = Integer.parseInt(value.substring(0, xIndex));
                int h = Integer.parseInt(value.substring(xIndex + 1));
                window.resize(w, h);
            }
        });
    }
}
