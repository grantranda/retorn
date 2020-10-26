package com.grantranda.retorn.engine.graphics.gui;

import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.control.Label;
import lwjgui.scene.control.TextField;
import lwjgui.scene.layout.HBox;

public class Parameter extends HBox {

    private final Label label;
    private final TextField textField;
    private double width;

    public Parameter() {
        this(0.0f, "", new TextField());
    }

    public Parameter(double width, String label, TextField textField) {
        this.width = width;
        this.label = new Label(label);
        this.label.setPrefWidth(width / 2.0f);
        this.label.setAlignment(Pos.CENTER_LEFT);
        this.label.setPadding(new Insets(10, 0, 10, 10));
        this.textField = textField;
        this.textField.setPrefWidth(width / 2.0f - 20);

        setFillToParentWidth(true);
        setPrefWidth(width);
        setAlignment(Pos.CENTER);

        getChildren().addAll(this.label, this.textField);
    }

    @Override
    public double getWidth() {
        return this.width;
    }

    public void setWidth(double width) {
        this.width = width;
        setPrefWidth(width);
        this.label.setPrefWidth(width / 2.0f);
        this.textField.setPrefWidth(width / 2.0f - 20);
    }

    public String getLabel() {
        return label.getText();
    }

    public void setLabel(String label) {
        this.label.setText(label);
    }

    public TextField getTextField() {
        return textField;
    }

    public String getText() {
        return textField.getText();
    }

    public void setText(String text) {
        textField.setText(text);
    }
}
