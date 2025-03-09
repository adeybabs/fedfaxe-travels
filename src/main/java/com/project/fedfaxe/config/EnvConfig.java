package com.project.fedfaxe.config;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvConfig {

    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    public static String get(String key) {
        String value = dotenv.get(key);
        return (value != null) ? value : System.getenv(key); // Fallback to system env
    }
}

