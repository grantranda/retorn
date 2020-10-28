package com.grantranda.retorn.app.state;

public class ApplicationState {

    private DisplayState displayState;
    private RenderState renderState;

    public ApplicationState() {

    }

    public DisplayState getDisplayState() {
        return displayState;
    }

    public void setDisplayState(DisplayState displayState) {
        this.displayState = displayState;
    }

    public RenderState getRenderState() {
        return renderState;
    }

    public void setRenderState(RenderState renderState) {
        this.renderState = renderState;
    }
}
