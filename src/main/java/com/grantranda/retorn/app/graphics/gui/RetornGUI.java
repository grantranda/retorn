package com.grantranda.retorn.app.graphics.gui;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.grantranda.retorn.app.Retorn;
import com.grantranda.retorn.app.graphics.AbstractFractalRenderer;
import com.grantranda.retorn.app.graphics.RetornRenderer;
import com.grantranda.retorn.app.graphics.gui.control.ColorSelector;
import com.grantranda.retorn.app.graphics.gui.control.NumberFieldd;
import com.grantranda.retorn.app.graphics.gui.control.NumberFieldi;
import com.grantranda.retorn.app.graphics.gui.control.Parameter;
import com.grantranda.retorn.app.graphics.gui.layout.ResolutionSelection;
import com.grantranda.retorn.app.state.ApplicationState;
import com.grantranda.retorn.app.state.DisplayState;
import com.grantranda.retorn.app.state.RenderState;
import com.grantranda.retorn.app.util.StateUtils;
import com.grantranda.retorn.engine.graphics.ImageRenderer;
import com.grantranda.retorn.engine.graphics.display.Resolution;
import com.grantranda.retorn.engine.graphics.display.Window;
import com.grantranda.retorn.engine.graphics.gui.GUI;
import com.grantranda.retorn.engine.input.MouseInput;
import com.grantranda.retorn.engine.math.Vector3d;
import com.grantranda.retorn.engine.state.State;
import com.grantranda.retorn.engine.util.DisplayUtils;
import lwjgui.LWJGUIDialog;
import lwjgui.LWJGUIDialog.DialogIcon;
import lwjgui.event.ActionEvent;
import lwjgui.event.EventHelper;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.paint.Color;
import lwjgui.scene.WindowManager;
import lwjgui.scene.control.*;
import lwjgui.scene.layout.BorderPane;
import lwjgui.scene.layout.HBox;
import lwjgui.scene.layout.StackPane;
import lwjgui.scene.layout.VBox;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class RetornGUI implements GUI {

    public static final int MENU_WIDTH = 350;
    public static final int MAX_FPS_LIMIT = 260;

    private long nvgContext;
    private boolean mouseOver = false;
    private boolean mousePressed = false;
    private boolean menuShown = true;
    private boolean menuDisabled = false;

    private final RetornRenderer retornRenderer;
    private final ImageRenderer imageRenderer;
    private lwjgui.scene.Window guiWindow;

    private BorderPane root;
    private BorderPane menu;
    private StackPane menuContainer;
    private StackPane menuCover;

    private Button hideMenuButton;
    private Button showMenuButton;
    private Button updateButton;
    private Button resetButton;
    private Button saveButton;
    private Button loadButton;
    private Button applyButton;
    private Button renderButton;
    private Parameter<NumberFieldi> maxIterationsParam;
    private Parameter<NumberFieldd> scaleParam;
    private Parameter<NumberFieldd> xParam;
    private Parameter<NumberFieldd> yParam;
    private ComboBox<String> fractalAlgorithmSelection;
    private ResolutionSelection windowResolutionSelection;
    private ResolutionSelection renderResolutionSelection;
    private CheckBox fullscreenToggle;
    private CheckBox vSyncToggle;
    private Label aspectRatioToggleLabel;
    private ToggleGroup aspectRatioToggleGroup;
    private RadioButton monitorAspectRatioToggle;
    private RadioButton fractalAspectRatioToggle;
    private ColorSelector colorSelector;
    private Slider fpsLimitSlider;
    private Label fpsLimitLabel;
    private Label fpsDisplay;

    private final Map<String, AbstractFractalRenderer> fractalRenderers = new HashMap<>();
    private final LinkedList<Resolution> windowResolutions = new LinkedList<>();
    private final LinkedList<Resolution> fractalResolutions = new LinkedList<>();

    public RetornGUI(RetornRenderer retornRenderer, ImageRenderer imageRenderer) {
        this.retornRenderer = retornRenderer;
        this.imageRenderer = imageRenderer;
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

    public lwjgui.scene.Window getGuiWindow() {
        return guiWindow;
    }

    public boolean isMenuShown() {
        return menuShown;
    }

    public void hideMenu() {
        menu.setVisible(false);
        root.setRight(showMenuButton);
        showMenuButton.setVisible(true);
        menuShown = false;
    }

    public void showMenu() {
        showMenuButton.setVisible(false);
        root.setRight(menuContainer);
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

    public boolean isMenuDisabled() {
        return menuDisabled;
    }

    public void setMenuDisabled(boolean menuDisabled) {
        if (menuDisabled == this.menuDisabled) return;

        this.menuDisabled = menuDisabled;
        if (menuDisabled) {
            menuContainer.getChildren().add(menuCover);
        } else {
            menuContainer.getChildren().remove(menuCover);
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

        initFractalAlgorithms();
        initWindowResolutions();
        initRenderResolutions();

        initMenu(window, state);
        initRoot(window);
        setEventHandlers(window, state);

        guiWindow.getScene().setRoot(root);

        updateDisplayParameters(state.getDisplayState());
        updateRenderParameters(state.getRenderState());

        EventHelper.fireEvent(applyButton.getOnAction(), new ActionEvent());
        EventHelper.fireEvent(updateButton.getOnAction(), new ActionEvent());
    }

    private void initNvg(Window window) {
        nvgContext = nvgCreate(0); // TODO: Change flags?
        if (nvgContext == NULL) throw new RuntimeException("Failed to create a NanoVG context");
    }

    private void initFractalAlgorithms() {
        fractalRenderers.clear();

        fractalRenderers.put(Retorn.MANDELBROT_SET, retornRenderer.getMandelbrotRenderer());
        fractalRenderers.put(Retorn.JULIA_SET, retornRenderer.getJuliaRenderer());
    }

    private void initFractalAlgorithmSelection(RenderState state) {
        fractalAlgorithmSelection = new ComboBox<>();
        fractalAlgorithmSelection.setPrefWidth(200); // TODO: Remove constant

        for (Entry<String, AbstractFractalRenderer> entry : fractalRenderers.entrySet()) {
            fractalAlgorithmSelection.getItems().add(entry.getKey());
        }
        fractalAlgorithmSelection.setValue(state.getFractalAlgorithm());
    }

    private void initWindowResolutions() {
        windowResolutions.clear();

        Resolution monitorResolution = DisplayUtils.getMonitorResolution();
        TreeSet<Resolution> monitorResolutions = DisplayUtils.getMonitorResolutions();

        for (Resolution resolution : monitorResolutions) {
            if (resolution.getArea() < monitorResolution.getArea()) {
                if (resolution.getWidth() >= resolution.getHeight()) {
                    windowResolutions.add(resolution);
                }
            }
        }
    }

    private void initRenderResolutions() {
        fractalResolutions.clear();

        Resolution monitorResolution = DisplayUtils.getMonitorResolution();
        double fractalAspectRatio = retornRenderer.getFractalAspectRatio().getRatio();

        for (Resolution resolution : windowResolutions) {
            int height = (int) (resolution.getWidth() / fractalAspectRatio);
            fractalResolutions.add(new Resolution(resolution.getWidth(), height));
        }
        fractalResolutions.add(new Resolution(monitorResolution.getWidth(), (int) (monitorResolution.getWidth() / fractalAspectRatio)));
    }

    private void initResolutionSelection(ApplicationState state) {
        DisplayState displayState = state.getDisplayState();
        RenderState renderState = state.getRenderState();

        windowResolutionSelection = new ResolutionSelection(MENU_WIDTH, windowResolutions);
        windowResolutionSelection.setResolution(displayState.getWindowResolution(), displayState.isCustomResolution());

        renderResolutionSelection = new ResolutionSelection(MENU_WIDTH, fractalResolutions);
        renderResolutionSelection.setResolution(renderState.getRenderResolution(), renderState.isCustomResolution());
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

    private void initMenu(Window window, ApplicationState state) {
        initFractalAlgorithmSelection(state.getRenderState());
        initResolutionSelection(state);
        initColorSelector(window);

        String monitorAspectRatio = "(" + DisplayUtils.getMonitorAspectRatio().toRatio() + ")";
        String fractalAspectRatio = "(" + retornRenderer.getFractalAspectRatio().toRatio() + ")";

        maxIterationsParam = new Parameter<>(MENU_WIDTH, "Max Iterations", new NumberFieldi(100, 0, 100000));
        scaleParam = new Parameter<>(MENU_WIDTH, "Scale", new NumberFieldd(1.0));
        xParam = new Parameter<>(MENU_WIDTH, "X", new NumberFieldd(0.0));
        yParam = new Parameter<>(MENU_WIDTH, "Y", new NumberFieldd(0.0));
        hideMenuButton = new Button("X");
        showMenuButton = new Button("|||");
        updateButton = new Button("Update");
        resetButton = new Button("Reset");
        saveButton = new Button("Save");
        loadButton = new Button("Load");
        applyButton = new Button("Apply");
        renderButton = new Button("Render");
        fullscreenToggle = new CheckBox("Fullscreen");
        vSyncToggle = new CheckBox("vSync");
        aspectRatioToggleLabel = new Label("Render Aspect Ratio:");
        aspectRatioToggleGroup = new ToggleGroup();
        monitorAspectRatioToggle = new RadioButton("Monitor " + monitorAspectRatio, aspectRatioToggleGroup);
        fractalAspectRatioToggle = new RadioButton("Fractal " + fractalAspectRatio, aspectRatioToggleGroup);
        colorSelector = new ColorSelector();
        fpsLimitSlider = new Slider(10, MAX_FPS_LIMIT, 10, 10);
        fpsLimitSlider.setFillToParentWidth(true);
        fpsLimitLabel = new Label("10");
        fpsLimitLabel.setPrefWidth(80);
        fpsLimitLabel.setAlignment(Pos.CENTER);
        fpsDisplay = new Label("FPS: " + window.getFpsCounter().getFps());
        fpsDisplay.setAlignment(Pos.BOTTOM_LEFT);
        fpsDisplay.setFillToParentWidth(true);

        HBox fpsLimitHBox = new HBox();
        fpsLimitHBox.setFillToParentWidth(true);
        fpsLimitHBox.setAlignment(Pos.CENTER);
        fpsLimitHBox.setPadding(new Insets(0, 0, 0, 10));
        fpsLimitHBox.getChildren().addAll(fpsLimitSlider, fpsLimitLabel);

        VBox top = new VBox();
        top.setAlignment(Pos.TOP_LEFT);
        top.setPadding(new Insets(0, 10, 0, 0));
        top.getChildren().add(hideMenuButton);
        top.getChildren().add(fractalAlgorithmSelection);
        top.getChildren().add(maxIterationsParam);
        top.getChildren().add(scaleParam);
        top.getChildren().addAll(xParam, yParam);
        top.getChildren().add(windowResolutionSelection);
        top.getChildren().add(aspectRatioToggleLabel);
        top.getChildren().addAll(monitorAspectRatioToggle, fractalAspectRatioToggle);
        top.getChildren().add(renderResolutionSelection);
        top.getChildren().add(fullscreenToggle);
        top.getChildren().add(vSyncToggle);
        top.getChildren().add(fpsLimitHBox);
        top.getChildren().add(applyButton);
        top.getChildren().add(renderButton);
        top.getChildren().add(colorSelector);
        top.getChildren().add(updateButton);
        top.getChildren().add(resetButton);
        top.getChildren().add(saveButton);
        top.getChildren().add(loadButton);

        menu = new BorderPane();
        menu.setMinWidth(MENU_WIDTH);
        menu.setMaxWidth(MENU_WIDTH);
        menu.setPrefHeight(window.getHeight());
        menu.setAlignment(Pos.TOP_LEFT);
        menu.setFillToParentHeight(true);
        menu.setBackgroundLegacy(new Color(.9, .9, .9, 0.95));
        menu.setBottom(fpsDisplay);
        menu.setTop(top);

        menuContainer = new StackPane();
        menuContainer.setMinWidth(MENU_WIDTH);
        menuContainer.setMaxWidth(MENU_WIDTH);
        menuContainer.setPrefHeight(window.getHeight());
        menuContainer.setFillToParentHeight(true);
        menuContainer.getChildren().add(menu);

        menuCover = new StackPane();
        menuCover.setMinWidth(MENU_WIDTH);
        menuCover.setMaxWidth(MENU_WIDTH);
        menuCover.setPrefHeight(window.getHeight());
        menuCover.setFillToParentHeight(true);
    }

    private void initRoot(Window window) {
        root = new BorderPane();
        root.setPrefSize(window.getWidth(), window.getHeight());
        root.setCenter(new StackPane()); // Set center so BorderPane alignment is correct
        root.setRight(menuContainer);
    }

    @Override
    public void terminate() {
        WindowManager.dispose();
        nvgDelete(nvgContext);
    }

    @Override
    public void update(Window window, State state) {
        WindowManager.update();
        updateInput(window);
        updateGui(window, (ApplicationState) state);
    }

    private void updateInput(Window window) {
        MouseInput mouseInput = window.getMouseInput();
        Vector3d mousePos = mouseInput.getCurrentPosition();
        int width = window.getWidth();
        boolean mouseOverMenu = mouseInput.isMouseInWindow()
                && isMenuShown()
                && (mousePos.x >= width - MENU_WIDTH && mousePos.x <= width);

        setMouseOver(mouseOverMenu);
    }

    private void updateGui(Window window, ApplicationState state) {
        DisplayState displayState = state.getDisplayState();
        fpsDisplay.setText("FPS: " + window.getFpsCounter().getFps());

        // TODO: This condition might have unintended consequences
        if (window.isResized()) {
            if (window.isFullscreen()) {
                Resolution monitorResolution = DisplayUtils.getMonitorResolution();
                displayState.setWindowResolution(monitorResolution.getWidth(), monitorResolution.getHeight());
                displayState.setCustomResolution(false);
            } else {
                updateWindowResolutionParameters(window.getResolution(), true);
                updateDisplayState(displayState, window);
            }
        }
    }

    public void updateDisplayParameters(DisplayState state) {
        updateWindowResolutionParameters(state.getWindowResolution(), state.isCustomResolution());
        fullscreenToggle.setChecked(state.isFullscreen());
        vSyncToggle.setChecked(state.isVSync());
        fpsLimitSlider.setValue(state.getFpsLimit());
    }

    private void updateDisplayState(DisplayState state, Window window) {
        state.setWindowResolution(window.getWidth(), window.getHeight());
        state.setCustomResolution(windowResolutionSelection.isCustomResolution());
        state.setFullscreen(fullscreenToggle.isChecked());
        state.setVSync(vSyncToggle.isChecked());
        state.setFpsLimit((int) fpsLimitSlider.getValue());
    }

    public void updateRenderParameters(RenderState state) {
        updateRenderResolutionParameters(state.getRenderResolution(), state.isCustomResolution());
        fractalAlgorithmSelection.setValue(state.getFractalAlgorithm());
        maxIterationsParam.getControl().setNumber(state.getMaxIterations());
        xParam.getControl().setNumber(state.getOffset().x);
        yParam.getControl().setNumber(state.getOffset().y);
        scaleParam.getControl().setNumber(state.getScale());

        if (state.isFractalAspectRatioMaintained()) {
            selectAspectRatioToggle(fractalAspectRatioToggle, fractalResolutions, state.isCustomResolution());
        } else {
            selectAspectRatioToggle(monitorAspectRatioToggle, windowResolutions, state.isCustomResolution());
        }
    }

    public void updateRenderResolutionParameters(Resolution resolution, boolean customResolution) {
        renderResolutionSelection.setResolution(resolution, customResolution);
    }

    public void updateWindowResolutionParameters(Resolution resolution, boolean customResolution) {
        windowResolutionSelection.setResolution(resolution, customResolution);
    }

    private void updateRenderState(RenderState state) {
        Resolution renderResolution = renderResolutionSelection.getResolution();

        state.setRenderResolution(renderResolution.getWidth(), renderResolution.getHeight());
        state.setCustomResolution(renderResolutionSelection.isCustomResolution());
        state.setFractalAspectRatioMaintained(fractalAspectRatioToggle.isSelected());
        state.setFractalAlgorithm(fractalAlgorithmSelection.getValue());
        state.setMaxIterations(maxIterationsParam.getControl().getNumber());
        state.setScale(scaleParam.getControl().getNumber());
        state.setOffset(xParam.getControl().getNumber(), yParam.getControl().getNumber(), 0.0f);
    }

    @Override
    public void render(Window window) {
        guiWindow.render();
        renderNvg(window);
    }

    private void renderNvg(Window window) {
        int width  = (int) (window.getWidth() / window.getContentScaleX());
        int height = (int) (window.getHeight() / window.getContentScaleY());
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

    private void applyRenderParameters() {
        maxIterationsParam.getControl().validate();
        scaleParam.getControl().validate();
        xParam.getControl().validate();
        yParam.getControl().validate();
    }

    private void applyDisplayParameters(Window window) {
        Resolution windowResolution = windowResolutionSelection.getResolution();
        Resolution monitorResolution = DisplayUtils.getMonitorResolution();

        int fpsLimit = (int) fpsLimitSlider.getValue();
        window.setVSync(vSyncToggle.isChecked());
        window.setFpsLimit(fpsLimit < MAX_FPS_LIMIT ? fpsLimit : 0);

        if (fullscreenToggle.isChecked()) {
            windowResolutionSelection.setComboBoxDisabled(true);
            windowResolutionSelection.setResolution(monitorResolution, false);
            window.resize(monitorResolution.getWidth(), monitorResolution.getHeight());
            window.setFullscreen(true);
        } else {
            windowResolutionSelection.setComboBoxDisabled(false);

            if (windowResolution.compareTo(monitorResolution) >= 0) {
                windowResolution.set(windowResolutions.getLast());
            }
            window.setFullscreen(false);
            window.resize(windowResolution.getWidth(), windowResolution.getHeight());
            window.moveToCenter();
        }
    }

    private void resetPosition(RenderState state) {
        state.setOffset(RenderState.DEFAULT_OFFSET, RenderState.DEFAULT_OFFSET, RenderState.DEFAULT_OFFSET);
        state.setScale(RenderState.DEFAULT_SCALE);

        xParam.getControl().setNumber(state.getOffset().x);
        yParam.getControl().setNumber(state.getOffset().y);
        scaleParam.getControl().setNumber(state.getScale());
    }

    private void selectAspectRatioToggle(RadioButton aspectRatioToggle, LinkedList<Resolution> renderResolutions, boolean customResolution) {
        if (!aspectRatioToggle.isSelected()) {
            aspectRatioToggle.setSelected(true);
            renderResolutionSelection.setResolutions(renderResolutions, renderResolutionSelection.getResolutionFromFields(), customResolution);
        }
    }

    private void setEventHandlers(Window window, ApplicationState state) {
        DisplayState displayState = state.getDisplayState();
        RenderState renderState = state.getRenderState();

        hideMenuButton.setOnAction(event -> hideMenu());
        showMenuButton.setOnAction(event -> showMenu());
        updateButton.setOnAction(event -> {
            applyRenderParameters();
            updateRenderResolutionParameters(renderResolutionSelection.getResolution(), renderResolutionSelection.isCustomResolution());
            updateRenderState(renderState);
            retornRenderer.setActiveRenderer(fractalRenderers.get(fractalAlgorithmSelection.getValue()));
        });
        resetButton.setOnAction(event -> resetPosition(renderState));
        saveButton.setOnAction(event -> {
            try {
                StateUtils.saveStateDialog(renderState, Retorn.SAVE_PARAMETERS_PATH, "Save Parameters");
            } catch (IOException | JsonIOException e) {
                LWJGUIDialog.showMessageDialog("Error", "Error saving parameters.", DialogIcon.ERROR);
            }
        });
        loadButton.setOnAction(event -> {
            try {
                StateUtils.loadStateDialog(state, RenderState.class, "Load Parameters");
                updateRenderParameters(renderState);
            } catch (IOException | JsonSyntaxException e) {
                LWJGUIDialog.showMessageDialog("Error", "Error loading parameters.", DialogIcon.ERROR);
            }
        });
        applyButton.setOnAction(event -> {
            applyDisplayParameters(window);
            updateWindowResolutionParameters(windowResolutionSelection.getResolution(), windowResolutionSelection.isCustomResolution());
            updateDisplayState(displayState, window);
        });
        renderButton.setOnAction(event -> imageRenderer.render(window));
        monitorAspectRatioToggle.setOnAction(event -> selectAspectRatioToggle(monitorAspectRatioToggle, windowResolutions, true));
        fractalAspectRatioToggle.setOnAction(event -> selectAspectRatioToggle(fractalAspectRatioToggle, fractalResolutions, true));
        fpsLimitSlider.setOnValueChangedEvent(event -> {
            int fpsLimit = (int) fpsLimitSlider.getValue();
            if (fpsLimit >= MAX_FPS_LIMIT) {
                fpsLimitLabel.setText("Unlimited");
            } else {
                fpsLimitLabel.setText(String.valueOf(fpsLimit));
            }
        });
    }
}
