package com.grantranda.retorn.app.util;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.grantranda.retorn.app.state.ApplicationState;
import com.grantranda.retorn.app.state.DisplayState;
import com.grantranda.retorn.app.state.RenderState;
import com.grantranda.retorn.engine.state.State;
import com.grantranda.retorn.engine.util.JSONUtils;
import lwjgui.LWJGUIDialog;
import lwjgui.LWJGUIDialog.DialogIcon;

import java.io.File;
import java.io.IOException;

public class StateUtils {

    private StateUtils() {

    }

    public static void saveState(State state, String defaultFilename) {
        File defaultPath = new File(System.getProperty("user.home") + "/" + defaultFilename);
        File selectedFile = LWJGUIDialog.showSaveFileDialog("Save Parameters", defaultPath, "JSON Files (*.json)", "json", false);

        if (selectedFile == null) return;

        try {
            selectedFile.createNewFile();
            JSONUtils.toJson(state, selectedFile.getAbsolutePath());
        } catch (IOException | JsonIOException e) {
            LWJGUIDialog.showMessageDialog("Error", "Error saving parameters.", DialogIcon.ERROR);
        }
    }

    public static <T extends State> void loadState(ApplicationState state, Class<T> type) {
        File defaultPath = new File(System.getProperty("user.home"));
        File selectedFile = LWJGUIDialog.showOpenFileDialog("Load Parameters", defaultPath, "JSON Files (*.json)", "json");

        if (selectedFile == null) return;

        try {
            T loadedState = JSONUtils.readJson(type, selectedFile.getAbsolutePath());

            if (loadedState instanceof DisplayState) {
                state.setDisplayState((DisplayState) loadedState);
            } else if (loadedState instanceof RenderState) {
                state.setRenderState((RenderState) loadedState);
            } else {
                throw new IOException();
            }
        } catch (IOException | JsonSyntaxException e) {
            LWJGUIDialog.showMessageDialog("Error", "Error loading parameters.", DialogIcon.ERROR);
        }
    }
}
