package com.grantranda.retorn.engine.graphics.display;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Resolution implements Comparable<Resolution> {

    private int width;
    private int height;

    public Resolution() {
        this(0, 0);
    }

    public Resolution(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void set(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    public void set(Resolution resolution) {
        set(resolution.getWidth(), resolution.getHeight());
    }

    public int getArea() {
        return width * height;
    }

    public double getAspectRatio() {
        return (double) width / height;
    }

    @Override
    public int compareTo(@NotNull Resolution o) {
        return Double.compare(getArea() * getAspectRatio(), o.getArea() * o.getAspectRatio());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resolution that = (Resolution) o;
        return width == that.width && height == that.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height);
    }

    @Override
    public String toString() {
        return width + " x " + height;
    }
}
