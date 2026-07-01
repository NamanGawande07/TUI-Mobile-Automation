package com.tui.qa.config;

import com.tui.qa.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigReader {

    private static final Logger logger =
            LoggerUtil.getLogger(ConfigReader.class);

    private static final Properties properties = new Properties();

    static {
        loadProperties();
    }

    private ConfigReader() {
    }

    private static void loadProperties() {

        try (InputStream inputStream = ConfigReader.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (inputStream == null) {

                logger.error("config.properties file not found.");

                throw new RuntimeException("Unable to locate config.properties");
            }

            properties.load(inputStream);

            logger.info("Configuration loaded successfully.");

        } catch (IOException e) {

            logger.error("Failed to load config.properties.", e);

            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    public static String getProperty(String key) {

        String value = resolveValue(key);

        if (value == null || value.trim().isEmpty()) {

            throw new RuntimeException(
                    "Property '" + key + "' not found in config/system/env."
            );
        }

        return value.trim();
    }

    public static String getOptionalProperty(String key) {

        String value = resolveValue(key);

        return value == null ? "" : value.trim();
    }

    private static String resolveValue(String key) {

        String systemValue = System.getProperty(key);

        if (systemValue != null && !systemValue.trim().isEmpty()) {
            return systemValue;
        }

        String envKey = toEnvKey(key);
        String envValue = System.getenv(envKey);

        if (envValue != null && !envValue.trim().isEmpty()) {
            return envValue;
        }

        return properties.getProperty(key);
    }

    private static String toEnvKey(String key) {

        return key
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .replace('.', '_')
                .toUpperCase();
    }
}