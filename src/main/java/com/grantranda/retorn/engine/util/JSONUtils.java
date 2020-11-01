package com.grantranda.retorn.engine.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JSONUtils {

    private JSONUtils() {

    }

    public static <T> T readJson(Class<T> type, String path) throws IOException, JsonSyntaxException {
        BufferedReader in = new BufferedReader(new FileReader(path));
        T object = new Gson().fromJson(in, type);
        in.close();
        return object;
    }

    public static String toJson(Object object) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create();
        return gson.toJson(object);
    }

    public static void toJson(Object object, String path) throws IOException, JsonIOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create();
        BufferedWriter out = new BufferedWriter(new FileWriter(path));
        gson.toJson(object, out);
        out.close();
    }
}
