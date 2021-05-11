package com.grantranda.retorn.app.graphics.gui;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.grantranda.retorn.app.Retorn;
import com.grantranda.retorn.app.graphics.AbstractFractalRenderer;
import com.grantranda.retorn.app.graphics.RetornRenderer;
import com.grantranda.retorn.app.graphics.gui.control.GradientEditor;
import com.grantranda.retorn.app.graphics.gui.control.Heading;
import com.grantranda.retorn.app.graphics.gui.control.NumberFieldd;
import com.grantranda.retorn.app.graphics.gui.control.NumberFieldi;
import com.grantranda.retorn.app.graphics.gui.control.Parameter;
import com.grantranda.retorn.app.graphics.gui.control.ResolutionSelection;
import com.grantranda.retorn.app.graphics.gui.control.Separator;
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
import lwjgui.scene.Node;
import lwjgui.scene.WindowManager;
import lwjgui.scene.control.*;
import lwjgui.scene.control.ScrollPane.ScrollBarPolicy;
import lwjgui.scene.layout.BorderPane;
import lwjgui.scene.layout.HBox;
import lwjgui.scene.layout.Pane;
import lwjgui.scene.layout.StackPane;
import lwjgui.scene.layout.VBox;
import lwjgui.style.BorderStyle;
import lwjgui.theme.Theme;
import lwjgui.theme.ThemeDark;

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

    public static final int MENU_CONTENT_WIDTH = 350;
    public static final int MENU_SCROLLBAR_WIDTH = 14;
    public static final int MENU_TOTAL_WIDTH = MENU_CONTENT_WIDTH + MENU_SCROLLBAR_WIDTH;
    public static final int MENU_CONTENT_HEIGHT = 700; // TODO: Determine final menu height
    public static final int MAX_FPS_LIMIT = 260;
    public static final double BUTTON_WIDTH = (MENU_CONTENT_WIDTH - 20) / 3.0f - 5;

    public static final Color MENU_COLOR = new Color(0.9, 0.9, 0.9, 1.0);

    private long nvgContext;
    private boolean mouseOver = false;
    private boolean mousePressed = false;
    private boolean menuShown = true;
    private boolean menuDisabled = false;

    private final RetornRenderer retornRenderer;
    private final ImageRenderer imageRenderer;
    private lwjgui.scene.Window guiWindow;
    private ThemeDark themeDark;

    private BorderPane root;
    private BorderPane fractalBorderPane;
    private StackPane fractalContainer;
    private ScrollPane fractalScrollPane;
    private BorderPane colorBorderPane;
    private StackPane colorContainer;
    private ScrollPane colorScrollPane;
    private BorderPane displayBorderPane;
    private StackPane displayContainer;
    private ScrollPane displayScrollPane;
    private StackPane menuCover;
    private Tab fractalTab;
    private Tab colorTab;
    private Tab displayTab;
    private TabPane menuTabPane;

    private Button hideMenuButton;
    private Button showMenuButton;
    private Button applyFractalButton;
    private Button resetButton;
    private Button saveButton;
    private Button loadButton;
    private Button applyDisplayButton;
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
    private Slider fpsLimitSlider;
    private Label fpsLimitLabel;
    private Label fpsDisplay;
    private GradientEditor gradientEditor;

    private final Map<String, AbstractFractalRenderer> fractalRenderers = new HashMap<>();
    private final LinkedList<Resolution> windowResolutions = new LinkedList<>();
    private final LinkedList<Resolution> monitorRenderResolutions = new LinkedList<>();
    private final LinkedList<Resolution> fractalRenderResolutions = new LinkedList<>();

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
        fractalBorderPane.setVisible(false);
        root.setRight(showMenuButton);
        showMenuButton.setVisible(true);
        menuShown = false;
    }

    public void showMenu() {
        showMenuButton.setVisible(false);
        root.setRight(menuTabPane);
        fractalBorderPane.setVisible(true);
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
            fractalContainer.getChildren().add(menuCover);
        } else {
            fractalContainer.getChildren().remove(menuCover);
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

        themeDark = new ThemeDark();
        Theme.setTheme(themeDark);

        initFractalAlgorithms();
        initWindowResolutions();
        initRenderResolutions();

        initMenu(window, state);
        initRoot();
        setEventHandlers(window, state);

        guiWindow.getScene().setRoot(root);

        updateDisplayParameters(state.getDisplayState());
        updateRenderParameters(state.getRenderState());

        EventHelper.fireEvent(applyDisplayButton.getOnAction(), new ActionEvent());
        EventHelper.fireEvent(applyFractalButton.getOnAction(), new ActionEvent());
    }

    private void initNvg(Window window) {
        nvgContext = nvgCreate(0); // TODO: Change flags?
        if (nvgContext == NULL) throw new RuntimeException("Failed to create a NanoVG context");
    }

    private void initFractalAlgorithms() {
        fractalRenderers.put(Retorn.MANDELBROT_SET, retornRenderer.getMandelbrotRenderer());
        fractalRenderers.put(Retorn.JULIA_SET, retornRenderer.getJuliaRenderer());
    }

    private void initFractalAlgorithmSelection(RenderState state) {
        fractalAlgorithmSelection = new ComboBox<>();
        fractalAlgorithmSelection.setPrefWidth(MENU_CONTENT_WIDTH / 2.0f - 10);

        for (Entry<String, AbstractFractalRenderer> entry : fractalRenderers.entrySet()) {
            fractalAlgorithmSelection.getItems().add(entry.getKey());
        }
        fractalAlgorithmSelection.setValue(state.getFractalAlgorithm());
    }

    private void initWindowResolutions() {
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
        Resolution monitorResolution = DisplayUtils.getMonitorResolution();
        double fractalAspectRatio = retornRenderer.getFractalAspectRatio().getRatio();

        for (Resolution resolution : windowResolutions) {
            int height = (int) (resolution.getWidth() / fractalAspectRatio);
            monitorRenderResolutions.add(new Resolution(resolution.getWidth(), resolution.getHeight()));
            fractalRenderResolutions.add(new Resolution(resolution.getWidth(), height));
        }
        monitorRenderResolutions.add(new Resolution(monitorResolution.getWidth(), monitorResolution.getHeight()));
        fractalRenderResolutions.add(new Resolution(monitorResolution.getWidth(), (int) (monitorResolution.getWidth() / fractalAspectRatio)));
    }

    private void initResolutionSelection(ApplicationState state) {
        DisplayState displayState = state.getDisplayState();
        RenderState renderState = state.getRenderState();

        windowResolutionSelection = new ResolutionSelection(MENU_CONTENT_WIDTH, windowResolutions);
        windowResolutionSelection.setResolution(displayState.getWindowResolution(), displayState.isCustomResolution());

        renderResolutionSelection = new ResolutionSelection(MENU_CONTENT_WIDTH, fractalRenderResolutions);
        renderResolutionSelection.setResolution(renderState.getRenderResolution(), renderState.isCustomResolution());
    }

    private void initMenu(Window window, ApplicationState state) {
        initFractalAlgorithmSelection(state.getRenderState());
        initResolutionSelection(state);

        String monitorAspectRatio = "(" + DisplayUtils.getMonitorAspectRatio().toRatio() + ")";
        String fractalAspectRatio = "(" + retornRenderer.getFractalAspectRatio().toRatio() + ")";

        // Menu Elements
        maxIterationsParam = new Parameter<>(MENU_CONTENT_WIDTH, "Max Iterations", new NumberFieldi(100, 0, 100000));
        scaleParam = new Parameter<>(MENU_CONTENT_WIDTH, "Scale", new NumberFieldd(1.0));
        xParam = new Parameter<>(MENU_CONTENT_WIDTH, "X", new NumberFieldd(0.0));
        yParam = new Parameter<>(MENU_CONTENT_WIDTH, "Y", new NumberFieldd(0.0));
        hideMenuButton = new Button("X");
        showMenuButton = new Button("|||");
        applyFractalButton = new Button("Apply");
        applyFractalButton.setMinWidth(BUTTON_WIDTH);
        resetButton = new Button("Reset");
        resetButton.setMinWidth(BUTTON_WIDTH);
        saveButton = new Button("Save");
        saveButton.setMinWidth(BUTTON_WIDTH);
        loadButton = new Button("Load");
        loadButton.setMinWidth(BUTTON_WIDTH);
        applyDisplayButton = new Button("Apply");
        applyDisplayButton.setMinWidth(BUTTON_WIDTH);
        renderButton = new Button("Render Image");
        renderButton.setMinWidth(BUTTON_WIDTH);
        fullscreenToggle = new CheckBox("Fullscreen");
        vSyncToggle = new CheckBox("vSync");
        aspectRatioToggleLabel = new Label("Aspect Ratio");
        aspectRatioToggleGroup = new ToggleGroup();
        monitorAspectRatioToggle = new RadioButton("Monitor " + monitorAspectRatio, aspectRatioToggleGroup);
        fractalAspectRatioToggle = new RadioButton("Fractal " + fractalAspectRatio, aspectRatioToggleGroup);
        fpsLimitSlider = new Slider(10, MAX_FPS_LIMIT, 10, 10);
        fpsLimitSlider.setFillToParentWidth(true);
        fpsLimitLabel = new Label("10");
        fpsLimitLabel.setPrefWidth(80);
        fpsLimitLabel.setAlignment(Pos.CENTER);
        fpsDisplay = new Label("FPS: " + window.getFpsCounter().getFps());
        fpsDisplay.setAlignment(Pos.CENTER_RIGHT);
        fpsDisplay.setFillToParentWidth(true);
        gradientEditor = new GradientEditor(guiWindow.getContext(), window.getMouseInput(), MENU_CONTENT_WIDTH - 20, 40);

        HBox tabTopHBox = new HBox();
        tabTopHBox.setMinWidth(MENU_CONTENT_WIDTH);
        tabTopHBox.setPadding(new Insets(0, 10, 0, 0));
        tabTopHBox.getChildren().addAll(hideMenuButton, fpsDisplay);

        // Fractal Tab
        {
            Heading fractalParametersHeading = new Heading("Fractal Parameters");
            Heading renderResolutionHeading = new Heading("Render");

            Label fractalAlgorithmLabel = new Label("Algorithm");
            fractalAlgorithmLabel.setPrefWidth(MENU_CONTENT_WIDTH / 2.0f - 10);
            fractalAlgorithmLabel.setAlignment(Pos.CENTER_LEFT);

            HBox fractalAlgorithmSelectionHBox = new HBox();
            fractalAlgorithmSelectionHBox.setMaxWidth(MENU_CONTENT_WIDTH - 20);
            fractalAlgorithmSelectionHBox.setAlignment(Pos.CENTER);
            fractalAlgorithmSelectionHBox.getChildren().addAll(fractalAlgorithmLabel, fractalAlgorithmSelection);

            HBox fractalButtonHBox = new HBox();
            fractalButtonHBox.setFillToParentWidth(true);
            fractalButtonHBox.setAlignment(Pos.CENTER);
            fractalButtonHBox.setSpacing(10);
            fractalButtonHBox.getChildren().addAll(resetButton, saveButton, loadButton);

            VBox renderAspectRatioVBox = new VBox();
            renderAspectRatioVBox.setFillToParentWidth(true);
            renderAspectRatioVBox.setAlignment(Pos.CENTER_LEFT);
            renderAspectRatioVBox.setSpacing(10);
            renderAspectRatioVBox.setPadding(new Insets(0, 0, 0, 5));
            renderAspectRatioVBox.getChildren().addAll(aspectRatioToggleLabel);

            VBox renderAspectRatioToggleVBox = new VBox();
            renderAspectRatioToggleVBox.setFillToParentWidth(true);
            renderAspectRatioToggleVBox.setAlignment(Pos.CENTER_LEFT);
            renderAspectRatioToggleVBox.setSpacing(10);
            renderAspectRatioToggleVBox.setPadding(new Insets(0, 0, 0, 20));
            renderAspectRatioToggleVBox.getChildren().addAll(monitorAspectRatioToggle, fractalAspectRatioToggle);

            VBox renderImageVBox = new VBox();
            renderImageVBox.setFillToParentWidth(true);
            renderImageVBox.setAlignment(Pos.CENTER);
            renderImageVBox.getChildren().add(renderButton);

            VBox fractalApplyVBox = new VBox();
            fractalApplyVBox.setFillToParentWidth(true);
            fractalApplyVBox.setAlignment(Pos.CENTER);
            fractalApplyVBox.getChildren().add(applyFractalButton);

            VBox fractalTopVBox = createMenuBorderPaneTop(
                    tabTopHBox, fractalParametersHeading,
                    new Separator(),
                    fractalAlgorithmSelectionHBox, maxIterationsParam, scaleParam, xParam, yParam, fractalButtonHBox, renderResolutionHeading,
                    new Separator(),
                    renderAspectRatioVBox, renderAspectRatioToggleVBox, renderResolutionSelection, renderImageVBox,
                    new Separator(),
                    fractalApplyVBox
            );
            fractalTopVBox.setAlignment(Pos.CENTER);
            fractalTopVBox.setSpacing(10);

            fractalBorderPane = createMenuBorderPane(fractalTopVBox);
            fractalContainer = createMenuContainer(fractalBorderPane);
            fractalScrollPane = createMenuScrollPane(fractalContainer);
        }

        // Color Tab
        {
            VBox colorTopVBox = createMenuBorderPaneTop(
                    tabTopHBox, gradientEditor
            );
            colorTopVBox.setAlignment(Pos.CENTER);
            colorTopVBox.setSpacing(10);

            colorBorderPane = createMenuBorderPane(colorTopVBox);
            colorContainer = createMenuContainer(colorBorderPane);
            colorScrollPane = createMenuScrollPane(colorContainer);
        }

        // Display Tab
        {
            Heading resolutionHeading = new Heading("Window Resolution");
            Heading framerateLimitHeading = new Heading("Framerate Limit");

            HBox fpsLimitHBox = new HBox();
            fpsLimitHBox.setFillToParentWidth(true);
            fpsLimitHBox.setAlignment(Pos.CENTER);
            fpsLimitHBox.setPadding(new Insets(0, 0, 0, 10));
            fpsLimitHBox.getChildren().addAll(fpsLimitSlider, fpsLimitLabel);

            VBox displayCheckboxesVBox = new VBox();
            displayCheckboxesVBox.setFillToParentWidth(true);
            displayCheckboxesVBox.setAlignment(Pos.CENTER_LEFT);
            displayCheckboxesVBox.setPadding(new Insets(0, 0, 0, 10));
            displayCheckboxesVBox.setSpacing(10);
            displayCheckboxesVBox.getChildren().addAll(fullscreenToggle, vSyncToggle);

            VBox displayApplyVBox = new VBox();
            displayApplyVBox.setFillToParentWidth(true);
            displayApplyVBox.setAlignment(Pos.CENTER);
            displayApplyVBox.getChildren().add(applyDisplayButton);

            VBox displayTopVBox = createMenuBorderPaneTop(
                    tabTopHBox, resolutionHeading,
                    new Separator(),
                    windowResolutionSelection, framerateLimitHeading,
                    new Separator(),
                    fpsLimitHBox,
                    new Separator(),
                    displayCheckboxesVBox,
                    new Separator(),
                    displayApplyVBox
            );
            displayTopVBox.setAlignment(Pos.CENTER);
            displayTopVBox.setSpacing(10);

            displayBorderPane = createMenuBorderPane(displayTopVBox);
            displayContainer = createMenuContainer(displayBorderPane);
            displayScrollPane = createMenuScrollPane(displayContainer);
        }

        // Tab Pane
        menuTabPane = new TabPane();
        menuTabPane.setFillToParentWidth(false);
        menuTabPane.setFillToParentHeight(false);
        menuTabPane.setMinWidth(MENU_TOTAL_WIDTH);
        menuTabPane.setMaxWidth(MENU_TOTAL_WIDTH);
        menuTabPane.setCanDrag(false);
        fractalTab = new Tab("Fractal", false);
        fractalTab.setContent(fractalScrollPane);
        colorTab = new Tab("Color", false);
        colorTab.setContent(colorScrollPane);
        displayTab = new Tab("Display", false);
        displayTab.setContent(displayScrollPane);
        menuTabPane.getTabs().addAll(fractalTab, colorTab, displayTab);

        menuCover = new StackPane();
        menuCover.setMinWidth(MENU_TOTAL_WIDTH);
        menuCover.setMaxWidth(MENU_TOTAL_WIDTH);
    }

    private void initRoot() {
        root = new BorderPane();
        root.setCenter(new StackPane()); // Set center so BorderPane alignment is correct
        root.setRight(menuTabPane);
    }

    private VBox createMenuBorderPaneTop(Node... children) {
        VBox top = new VBox();
        top.setAlignment(Pos.TOP_LEFT);
        top.setPadding(new Insets(0, 10, 0, 0));
        top.getChildren().addAll(children);
        return top;
    }

    private BorderPane createMenuBorderPane(Pane top) {
        BorderPane fractalBorderPane = new BorderPane();
        fractalBorderPane.setMinWidth(MENU_CONTENT_WIDTH);
        fractalBorderPane.setMaxWidth(MENU_CONTENT_WIDTH);
        fractalBorderPane.setAlignment(Pos.TOP_LEFT);
        fractalBorderPane.setTop(top);
        return fractalBorderPane;
    }

    private StackPane createMenuContainer(Pane content) {
        StackPane container = new StackPane();
        container.setMinWidth(MENU_CONTENT_WIDTH);
        container.setMaxWidth(MENU_CONTENT_WIDTH);
        container.setPadding(new Insets(0, 10, 0, 20));
        container.getChildren().add(content);
        return container;
    }

    private ScrollPane createMenuScrollPane(Pane content) {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(content);
        scrollPane.setMinWidth(MENU_TOTAL_WIDTH);
        scrollPane.setMaxWidth(MENU_TOTAL_WIDTH);
        scrollPane.setPrefHeight(windowResolutionSelection.getResolution().getHeight());
        scrollPane.setAlignment(Pos.TOP_CENTER);
        scrollPane.setInternalPadding(new Insets(10, 0, 0, 0));
        scrollPane.setBorderStyle(BorderStyle.NONE);
        scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        return scrollPane;
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
                && (mousePos.x >= width - MENU_TOTAL_WIDTH && mousePos.x <= width);

        setMouseOver(mouseOverMenu);
    }

    private void updateGui(Window window, ApplicationState state) {
        DisplayState displayState = state.getDisplayState();
        fpsDisplay.setText("FPS: " + window.getFpsCounter().getFps());

        // TODO: This condition might have unintended consequences
        if (window.isResized()) {
            updateGuiSize(window);

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

    private void updateGuiSize(Window window) {
        int scrollPaneTopOffset = 45;
        fractalBorderPane.setMinHeight(MENU_CONTENT_HEIGHT);
        fractalBorderPane.setMaxHeight(MENU_CONTENT_HEIGHT);
        fractalContainer.setMinHeight(MENU_CONTENT_HEIGHT);
        menuCover.setMinHeight(MENU_CONTENT_HEIGHT);
        fractalScrollPane.setMinHeight(window.getHeight() - scrollPaneTopOffset);
        fractalScrollPane.setMaxHeight(window.getHeight() - scrollPaneTopOffset);
        colorScrollPane.setMinHeight(window.getHeight() - scrollPaneTopOffset);
        colorScrollPane.setMaxHeight(window.getHeight() - scrollPaneTopOffset);
        displayScrollPane.setMinHeight(window.getHeight() - scrollPaneTopOffset);
        displayScrollPane.setMaxHeight(window.getHeight() - scrollPaneTopOffset);
        menuTabPane.setMinHeight(window.getHeight());
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
            selectAspectRatioToggle(fractalAspectRatioToggle, fractalRenderResolutions, state.isCustomResolution());
        } else {
            selectAspectRatioToggle(monitorAspectRatioToggle, monitorRenderResolutions, state.isCustomResolution());
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

        if (menuShown) {
            renderNvg(window);
        }
    }

    private void renderNvg(Window window) {
        int width  = (int) (window.getWidth() / window.getContentScaleX());
        int height = (int) (window.getHeight() / window.getContentScaleY());
        float minX = (float) menuTabPane.getX();
        float startX = (float) gradientEditor.getX();

        nvgBeginFrame(nvgContext, width, height, Math.max(window.getContentScaleX(), window.getContentScaleY()));

        if (menuTabPane.getSelected() == colorTab && startX >= minX) {
            gradientEditor.render(nvgContext);
        }

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

        updateGuiSize(window);
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
        applyFractalButton.setOnAction(event -> {
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
        applyDisplayButton.setOnAction(event -> {
            applyDisplayParameters(window);
            updateWindowResolutionParameters(windowResolutionSelection.getResolution(), windowResolutionSelection.isCustomResolution());
            updateDisplayState(displayState, window);
        });
        renderButton.setOnAction(event -> imageRenderer.render(window));
        monitorAspectRatioToggle.setOnAction(event -> selectAspectRatioToggle(monitorAspectRatioToggle, monitorRenderResolutions, true));
        fractalAspectRatioToggle.setOnAction(event -> selectAspectRatioToggle(fractalAspectRatioToggle, fractalRenderResolutions, true));
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
