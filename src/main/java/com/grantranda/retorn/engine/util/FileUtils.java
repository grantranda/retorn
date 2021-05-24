package com.grantranda.retorn.engine.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

public class FileUtils {

    private FileUtils() {

    }

    public static String fileToString(String path) {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader in = new BufferedReader(new FileReader(FileUtils.getResource(path)))) {
            while (in.ready()) {
                sb.append(in.readLine());
                sb.append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
            Throwable[] s = e.getSuppressed();
            for (Throwable t : s) {
                t.printStackTrace();
            }
        }

        return sb.toString();
    }

    public static File getResource(String path) {
        URL jarUrl = FileUtils.class.getProtectionDomain().getCodeSource().getLocation();
        File jarDirectory = new File(jarUrl.getPath()).getParentFile();
        return new File(jarDirectory.getAbsolutePath() + "/resources/" + path);
    }
}
