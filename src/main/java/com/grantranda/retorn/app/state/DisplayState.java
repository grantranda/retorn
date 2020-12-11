package com.grantranda.retorn.app.state;

import com.grantranda.retorn.engine.graphics.display.Resolution;
import com.grantranda.retorn.engine.state.State;

public class DisplayState implements State {

    private final Resolution windowResolution = new Resolution(1280, 720);
    private boolean customResolution = false;
    private boolean fullscreen = false;
    private boolean vSync = false;

    public DisplayState() {

    }

    public Resolution getWindowResolution() {
        return windowResolution;
    }

    public void setWindowResolution(int width, int height) {
        windowResolution.set(width, height);
    }

    public boolean isCustomResolution() {
        return customResolution;
    }

    public void setCustomResolution(boolean customResolution) {
        this.customResolution = customResolution;
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }

    public boolean isVSync() {
        return vSync;
    }

    public void setVSync(boolean vSync) {
        this.vSync = vSync;
    }

    @Override
    public void reset() {
        setWindowResolution(1280, 720);
        setCustomResolution(false);
        setFullscreen(false);
        setVSync(false);
    }
}
