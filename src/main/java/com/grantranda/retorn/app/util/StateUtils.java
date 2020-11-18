package com.grantranda.retorn.app.util;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.grantranda.retorn.app.state.ApplicationState;
import com.grantranda.retorn.app.state.DisplayState;
import com.grantranda.retorn.app.state.RenderState;
import com.grantranda.retorn.engine.state.State;
import com.grantranda.retorn.engine.util.JSONUtils;
import lwjgui.LWJGUIDialog;

import java.io.File;
import java.io.IOException;

public class StateUtils {

    private StateUtils() {

    }

    public static void saveState(State state, File file) throws IOException, JsonIOException {
        if (file == null) return;

        file.createNewFile();
        JSONUtils.toJson(state, file.getAbsolutePath());
    }

    public static void saveStateDialog(State state, String defaultFilename, String title)
            throws IOException, JsonIOException {

        File defaultPath = new File(System.getProperty("user.home") + "/" + defaultFilename);
        File selectedFile = LWJGUIDialog.showSaveFileDialog(title, defaultPath, "JSON Files (*.json)", "json", false);
        saveState(state, selectedFile);
    }

    public static <T extends State> void loadState(ApplicationState state, Class<T> type, File file)
            throws IOException, JsonSyntaxException {

        if (file == null) return;

        T loadedState = JSONUtils.readJson(type, file.getAbsolutePath());

        if (loadedState instanceof DisplayState) {
            state.setDisplayState((DisplayState) loadedState);
        } else if (loadedState instanceof RenderState) {
            state.setRenderState((RenderState) loadedState);
        } else {
            throw new IOException();
        }
    }

    public static <T extends State> void loadStateDialog(ApplicationState state, Class<T> type)
            throws IOException, JsonSyntaxException {

        File defaultPath = new File(System.getProperty("user.home"));
        File selectedFile = LWJGUIDialog.showOpenFileDialog("Load Parameters", defaultPath, "JSON Files (*.json)", "json");
        loadState(state, type, selectedFile);
    }
}
