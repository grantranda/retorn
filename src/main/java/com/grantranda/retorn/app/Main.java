package com.grantranda.retorn.app;

import com.grantranda.retorn.engine.Engine;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public class Main {

    public static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            Configurator.setRootLevel(Level.INFO);
            new Engine("Retorn", 1280, 720, false, new Retorn(args)).run();

        } catch (Exception e) {
            e.printStackTrace();
            Throwable[] s = e.getSuppressed();
            for (Throwable t : s) {
                t.printStackTrace();
            }
            System.exit(-1);
        }
    }
}
