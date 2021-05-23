package com.grantranda.retorn.app.graphics;

public enum ColoringAlgorithm {

    ESCAPE_TIME("Escape Time", 4),
    ORBIT_TRAP("Orbit Trap", 0);

    private final String name;
    private final int escapeRadius;

    ColoringAlgorithm(String name, int escapeRadius) {
        this.name = name;
        this.escapeRadius = escapeRadius;
    }

    public static ColoringAlgorithm fromName(String name) {
        for (ColoringAlgorithm coloringAlgorithm : ColoringAlgorithm.values()) {
            if (coloringAlgorithm.name.equals(name)) return coloringAlgorithm;
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public int getEscapeRadius() {
        return escapeRadius;
    }
}
