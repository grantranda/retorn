package com.grantranda.retorn.app.state;

import com.grantranda.retorn.engine.state.State;

public class ApplicationState implements State {

    private DisplayState displayState;
    private RenderState renderState;

    public ApplicationState() {
        this(new DisplayState(), new RenderState());
    }

    public ApplicationState(DisplayState displayState, RenderState renderState) {
        setDisplayState(displayState);
        setRenderState(renderState);
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

    @Override
    public void reset() {

    }
}
