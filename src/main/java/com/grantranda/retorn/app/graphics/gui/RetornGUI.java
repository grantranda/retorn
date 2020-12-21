package com.grantranda.retorn.app.graphics.gui;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.grantranda.retorn.app.Retorn;
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
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.paint.Color;
import lwjgui.scene.WindowManager;
import lwjgui.scene.control.*;
import lwjgui.scene.layout.BorderPane;
import lwjgui.scene.layout.StackPane;
import lwjgui.scene.layout.VBox;

import java.io.IOException;
import java.util.LinkedList;
import java.util.TreeSet;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class RetornGUI implements GUI {

    public static final int MENU_WIDTH = 350;

    private long nvgContext;
    private boolean mouseOver = false;
    private boolean mousePressed = false;
    private boolean menuShown = true;

    private lwjgui.scene.Window guiWindow;
    private RetornRenderer retornRenderer;
    private ImageRenderer imageRenderer;

    private BorderPane root;
    private BorderPane menu;

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
    private ResolutionSelection windowResolutionSelection;
    private ResolutionSelection renderResolutionSelection;
    private CheckBox fullscreenParam;
    private CheckBox vSyncParam;
    private ColorSelector colorSelector;
    private Label fpsDisplay;

    private LinkedList<Resolution> windowResolutions = new LinkedList<>();
    private LinkedList<Resolution> fractalResolutions = new LinkedList<>();

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

        initMenu(window, state);
        initRoot(window);
        setEventHandlers(window, state);

        guiWindow.getScene().setRoot(root);

        updateRenderParameters(state.getRenderState());
        updateDisplayParameters(state.getDisplayState());
        applyRenderParameters();
        applyDisplayParameters(window);
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
        fpsDisplay.setText("FPS: " + window.getFpsCounter().getFps());

        // TODO: This condition might have unintended consequences
        if (window.isResized() && !fullscreenParam.isChecked()) {
            updateWindowResolutionParameters(window.getResolution(), true);
            updateDisplayState(state.getDisplayState(), window);
        }
    }

    public void updateRenderParameters(RenderState state) {
        maxIterationsParam.getControl().setNumber(state.getMaxIterations());
        scaleParam.getControl().setNumber(state.getScale());
        xParam.getControl().setNumber(state.getOffset().x);
        yParam.getControl().setNumber(state.getOffset().y);
    }

    public void updateDisplayParameters(DisplayState state) {
        updateWindowResolutionParameters(state.getWindowResolution(), state.isCustomResolution());
        fullscreenParam.setChecked(state.isFullscreen());
        vSyncParam.setChecked(state.isVSync());
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
        state.setMaxIterations(maxIterationsParam.getControl().getNumber());
        state.setScale(scaleParam.getControl().getNumber());
        state.setOffset(xParam.getControl().getNumber(), yParam.getControl().getNumber(), 0.0f);
    }

    private void updateDisplayState(DisplayState state, Window window) {
        state.setWindowResolution(window.getWidth(), window.getHeight());
        state.setCustomResolution(windowResolutionSelection.isCustomResolution());
        state.setFullscreen(fullscreenParam.isChecked());
        state.setVSync(vSyncParam.isChecked());
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

        window.resize(windowResolution.getWidth(), windowResolution.getHeight());
        window.setFullscreen(fullscreenParam.isChecked());
        window.setVSync(vSyncParam.isChecked());

        if (!fullscreenParam.isChecked()) {
            window.moveToCenter();
        }
    }

    private void initResolutionSelection(ApplicationState state) {
        DisplayState displayState = state.getDisplayState();
        RenderState renderState = state.getRenderState();

        Resolution monitorResolution = DisplayUtils.getMonitorResolution();
        TreeSet<Resolution> monitorResolutions = DisplayUtils.getMonitorResolutions();
        LinkedList<Resolution> windowResolutions = new LinkedList<>();

        for (Resolution resolution : monitorResolutions) {
            if (resolution.getArea() <= monitorResolution.getArea()) {
                if (resolution.getWidth() >= resolution.getHeight()) {
                    windowResolutions.add(resolution);
                }
            }
        }

        windowResolutionSelection = new ResolutionSelection(MENU_WIDTH, windowResolutions);
        windowResolutionSelection.setResolution(displayState.getWindowResolution(), displayState.isCustomResolution());

        renderResolutionSelection = new ResolutionSelection(MENU_WIDTH, windowResolutions);
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
        initResolutionSelection(state);
        initColorSelector(window);

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
        fullscreenParam = new CheckBox("Fullscreen");
        vSyncParam = new CheckBox("vSync");
        colorSelector = new ColorSelector();
        fpsDisplay = new Label("FPS: " + window.getFpsCounter().getFps());
        fpsDisplay.setAlignment(Pos.BOTTOM_LEFT);
        fpsDisplay.setFillToParentWidth(true);

        VBox top = new VBox();
        top.setAlignment(Pos.TOP_LEFT);
        top.setPadding(new Insets(0, 10, 0, 0));
        top.getChildren().add(hideMenuButton);
        top.getChildren().add(maxIterationsParam);
        top.getChildren().add(scaleParam);
        top.getChildren().addAll(xParam, yParam);
        top.getChildren().add(windowResolutionSelection);
        top.getChildren().add(renderResolutionSelection);
        top.getChildren().add(fullscreenParam);
        top.getChildren().add(vSyncParam);
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
    }

    private void initRoot(Window window) {
        root = new BorderPane();
        root.setPrefSize(window.getWidth(), window.getHeight());
        root.setCenter(new StackPane()); // Set center so BorderPane alignment is correct
        root.setRight(menu);
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
        });
        resetButton.setOnAction(event -> {
            renderState.reset();
            updateRenderParameters(renderState);
        });
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
    }
}
