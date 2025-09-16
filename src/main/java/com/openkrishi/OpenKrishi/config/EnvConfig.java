package com.openkrishi.OpenKrishi.config;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvConfig {
    public static void loadEnv() {
        System.setProperty("spring.datasource.url", System.getenv("DB_URL"));
        System.setProperty("spring.datasource.username", System.getenv("DB_USERNAME"));
        System.setProperty("spring.datasource.password", System.getenv("DB_PASSWORD"));
        System.setProperty("server.port", System.getenv("PORT"));
        System.setProperty("FRONTEND_URL", System.getenv("FRONTEND_URL"));
    }
} 