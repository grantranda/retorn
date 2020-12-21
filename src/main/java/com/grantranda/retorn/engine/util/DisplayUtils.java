package com.grantranda.retorn.engine.util;

import com.grantranda.retorn.engine.graphics.display.Resolution;
import com.grantranda.retorn.engine.math.Fraction;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWVidMode.Buffer;

import java.util.TreeSet;

import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetVideoModes;

public class DisplayUtils {

    private DisplayUtils() {

    }

    public static Resolution getMonitorResolution() {
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        return new Resolution(vidMode.width(), vidMode.height());
    }

    public static TreeSet<Resolution> getMonitorResolutions() {
        TreeSet<Resolution> resolutions = new TreeSet<>();
        Buffer vidModes = glfwGetVideoModes(glfwGetPrimaryMonitor());

        if (vidModes != null) {
            for (GLFWVidMode vidMode : vidModes) {
                resolutions.add(new Resolution(vidMode.width(), vidMode.height()));
            }
        }

        return resolutions;
    }

    public static Fraction getMonitorAspectRatio() {
        Resolution monitorResolution = getMonitorResolution();
        return new Fraction(monitorResolution.getWidth(), monitorResolution.getHeight()).simplify();
    }
}
