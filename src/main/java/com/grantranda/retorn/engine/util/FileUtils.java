package com.grantranda.retorn.engine.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileUtils {

    private FileUtils() {

    }

    public static String fileToString(String path) {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader in = new BufferedReader(new FileReader(FileUtils.class.getClassLoader().getResource(path).getFile()))) {
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
}
