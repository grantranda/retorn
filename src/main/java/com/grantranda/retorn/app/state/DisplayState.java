package com.grantranda.retorn.app.state;

import com.grantranda.retorn.engine.state.State;

public class DisplayState implements State {

    private int width;
    private int height;
    private boolean vSync;

    public DisplayState() {

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

    public boolean isvSync() {
        return vSync;
    }

    public void setvSync(boolean vSync) {
        this.vSync = vSync;
    }

    @Override
    public void reset() {
        setWidth(1280);
        setHeight(720);
        setvSync(false);
    }
}
