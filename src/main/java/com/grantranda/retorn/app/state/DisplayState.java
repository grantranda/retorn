package com.grantranda.retorn.app.state;

import com.grantranda.retorn.engine.graphics.display.Resolution;
import com.grantranda.retorn.engine.state.State;

public class DisplayState implements State {

    private final Resolution windowResolution = new Resolution(1280, 720);
    private boolean vSync = false;

    public DisplayState() {

    }

    public Resolution getWindowResolution() {
        return windowResolution;
    }

    public void setWindowResolution(int width, int height) {
        windowResolution.set(width, height);
    }

    public boolean isvSync() {
        return vSync;
    }

    public void setVSync(boolean vSync) {
        this.vSync = vSync;
    }

    @Override
    public void reset() {
        setWindowResolution(1280, 720);
        setvSync(false);
    }
}
