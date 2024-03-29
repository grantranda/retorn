package com.grantranda.retorn.app.graphics.gui.control;

import com.grantranda.retorn.engine.graphics.display.Resolution;
import lwjgui.geometry.Pos;
import lwjgui.scene.control.ComboBox;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.HBox;
import lwjgui.scene.layout.VBox;

import java.util.Collection;
import java.util.LinkedList;

public class ResolutionSelection extends VBox {

    public static final int MIN_RESOLUTION = 100;
    public static final int MAX_RESOLUTION = 10000;
    public static final String CUSTOM_OPTION = "Custom";

    private double parameterWidth; // the width of the width and height fields
    private boolean customResolution;
    private final ComboBox<String> resolutionComboBox = new ComboBox<>();
    private final Parameter<NumberFieldi> widthParam;
    private final Parameter<NumberFieldi> heightParam;
    private final Resolution resolutionFromSelection = new Resolution(MIN_RESOLUTION, MIN_RESOLUTION);
    private final Resolution resolutionFromFields = new Resolution(MIN_RESOLUTION, MIN_RESOLUTION);
    private final LinkedList<Resolution> resolutions = new LinkedList<>();

    public ResolutionSelection(double parameterWidth, Collection<Resolution> resolutions) {
        Label resolutionLabel = new Label("Resolution");
        resolutionLabel.setPrefWidth(parameterWidth / 2.0f - 10);
        resolutionLabel.setAlignment(Pos.CENTER_LEFT);
        resolutionComboBox.setPrefWidth(parameterWidth / 2.0f - 10);
        widthParam = new Parameter<>(parameterWidth, "Width", new NumberFieldi(MIN_RESOLUTION, MIN_RESOLUTION, MAX_RESOLUTION));
        heightParam = new Parameter<>(parameterWidth, "Height", new NumberFieldi(MIN_RESOLUTION, MIN_RESOLUTION, MAX_RESOLUTION));
        this.parameterWidth = parameterWidth;
        setResolutions(resolutions);

        resolutionComboBox.setOnAction(event -> {
            String value = resolutionComboBox.getValue();
            if (value.equals(CUSTOM_OPTION)) {
                customResolution = true;
                setFieldsDisabled(false);
            } else {
                customResolution = false;
                setFieldsDisabled(true);

                int xIndex = value.indexOf('x');
                resolutionFromSelection.set(Integer.parseInt(value.substring(0, xIndex - 1)), Integer.parseInt(value.substring(xIndex + 2)));
            }
        });

        HBox resolutionComboBoxHBox = new HBox();
        resolutionComboBoxHBox.setMaxWidth(parameterWidth - 20);
        resolutionComboBoxHBox.setAlignment(Pos.CENTER);
        resolutionComboBoxHBox.getChildren().addAll(resolutionLabel, resolutionComboBox);

        setAlignment(Pos.CENTER);
        getChildren().addAll(resolutionComboBoxHBox, widthParam, heightParam);
    }

    public double getParameterWidth() {
        return parameterWidth;
    }

    public void setParameterWidth(double parameterWidth) {
        this.parameterWidth = parameterWidth;
        widthParam.setWidth(parameterWidth);
        heightParam.setWidth(parameterWidth);
    }

    public boolean isCustomResolution() {
        return customResolution;
    }

    public ComboBox<String> getResolutionComboBox() {
        return resolutionComboBox;
    }

    public Parameter<NumberFieldi> getWidthParam() {
        return widthParam;
    }

    public Parameter<NumberFieldi> getHeightParam() {
        return heightParam;
    }

    public LinkedList<Resolution> getResolutions() {
        return resolutions;
    }

    public void setResolutions(Collection<Resolution> resolutions) {
        this.resolutions.clear();
        this.resolutions.addAll(resolutions);

        resolutionComboBox.getItems().clear();
        for (Resolution resolution : resolutions) {
            resolutionComboBox.getItems().add(resolution.toString());
        }
        resolutionComboBox.getItems().add(CUSTOM_OPTION);
    }

    public void setResolutions(Collection<Resolution> resolutions, Resolution selectedResolution, boolean customResolution) {
        setResolutions(resolutions);
        setResolution(selectedResolution, customResolution);
    }

    public Resolution getResolution() {
        Resolution resolution;

        if (isCustomResolution()) {
            resolution = getResolutionFromFields();
        } else {
            resolution = getResolutionFromSelection();
        }
        return resolution;
    }

    public void setResolution(int width, int height, boolean customResolution) {
        this.customResolution = customResolution;
        if (customResolution) {
            setResolutionFields(width, height);
            setFieldsDisabled(false);
            resolutionComboBox.setValue(CUSTOM_OPTION);
        } else {
            setResolutionSelection(width, height);
            setResolutionFields(width, height);
            setFieldsDisabled(true);
        }
    }

    public void setResolution(Resolution resolution, boolean customResolution) {
        setResolution(resolution.getWidth(), resolution.getHeight(), customResolution);
    }

    public Resolution getResolutionFromFields() {
        validateResolution();
        setResolutionFields(widthParam.getControl().getNumber(), heightParam.getControl().getNumber());
        return resolutionFromFields;
    }

    public void setResolutionFields(int width, int height) {
        if (widthParam.getControl().setNumber(width) && heightParam.getControl().setNumber(height)) {
            resolutionFromFields.set(width, height);
        } else {
            widthParam.getControl().setNumber(resolutionFromFields.getWidth());
            heightParam.getControl().setNumber(resolutionFromFields.getHeight());
        }
    }

    public void setResolutionFields(Resolution resolution) {
        setResolutionFields(resolution.getWidth(), resolution.getHeight());
    }

    public Resolution getResolutionFromSelection() {
        return resolutionFromSelection;
    }

    public void setResolutionSelection(int width, int height) {
        resolutionFromSelection.set(width, height);
        resolutionComboBox.setValue(resolutionFromSelection.toString());
    }

    public void setResolutionSelection(Resolution resolution) {
        setResolutionSelection(resolution.getWidth(), resolution.getHeight());
    }

    public void setComboBoxDisabled(boolean comboBoxDisabled) {
        resolutionComboBox.setDisabled(comboBoxDisabled);
    }

    public void setWidthFieldDisabled(boolean widthFieldDisabled) {
        widthParam.getControl().setEditable(!widthFieldDisabled);
        widthParam.getControl().setDisabled(widthFieldDisabled);
    }

    public void setHeightFieldDisabled(boolean heightFieldDisabled) {
        heightParam.getControl().setEditable(!heightFieldDisabled);
        heightParam.getControl().setDisabled(heightFieldDisabled);
    }

    public void setFieldsDisabled(boolean fieldsDisabled) {
        setWidthFieldDisabled(fieldsDisabled);
        setHeightFieldDisabled(fieldsDisabled);
    }

    public boolean validateResolution() {
        return validateWidth() && validateHeight();
    }

    private boolean validateWidth() {
        return widthParam.getControl().validate();
    }

    private boolean validateHeight() {
        return heightParam.getControl().validate();
    }
}
