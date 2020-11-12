package com.grantranda.retorn.app.graphics.gui.control;

import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.control.Control;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.HBox;

public class Parameter<T extends Control> extends HBox {

    private final Label label;
    private final T control;
    private double width;

    public Parameter(double width, String label, T control) {
        this.label = new Label(label);
        this.label.setAlignment(Pos.CENTER_LEFT);
        this.label.setPadding(new Insets(10, 0, 10, 10));
        this.control = control;

        setWidth(width);
        setFillToParentWidth(true);
        setAlignment(Pos.CENTER);

        getChildren().addAll(this.label, this.control);
    }

    @Override
    public double getWidth() {
        return this.width;
    }

    public void setWidth(double width) {
        this.width = width;
        setPrefWidth(width);
        this.label.setPrefWidth(width / 2.0f);
        this.control.setPrefWidth(width / 2.0f - 20);
    }

    public String getLabelText() {
        return label.getText();
    }

    public void setLabelText(String label) {
        this.label.setText(label);
    }

    public T getControl() {
        return control;
    }
}
