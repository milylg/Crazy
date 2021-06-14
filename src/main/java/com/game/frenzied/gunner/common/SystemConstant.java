package com.game.frenzied.gunner.common;

import com.sun.istack.internal.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Properties;


public class SystemConstant {

    private static final Properties properties;
    private static final Logger logger = LoggerFactory.getLogger(SystemConstant.class);

    private static boolean successLoad = true;

    static {
        properties = new Properties();

        try (InputStream inputStream = SystemConstant.class
                .getClassLoader()
                .getResourceAsStream("system.properties")) {
            properties.load(inputStream);
        } catch (IOException e) {
            logger.error(e.getMessage());
            successLoad = false;
        }
    }

    public static boolean put(@NotNull String key, String value) {
        if (successLoad && Optional.ofNullable(key).isPresent()) {
            Object object = properties.setProperty(key, value);
            return object != null;
        }
        return false;
    }

    /**
     * dynamic get value of direct number type from properties file
     * <p>
     * use default value if load properties fail or without key in properties or key is null
     *
     * @param key
     * @param clazz        : value of type
     * @param defaultValue
     * @param <T>
     * @return
     */
    public static <T extends Number> T valueOf(@NotNull String key, Class<T> clazz, T defaultValue) {

        String value = valueOf(key);

        if (value == null) {
            return defaultValue;
        }

        Method method;
        try {
            method = clazz.getMethod("valueOf", String.class);
            method.setAccessible(true);
            return (T) method.invoke(null, value);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return defaultValue;
    }

    /**
     * get value of String type by key in properties
     *
     * @param key
     * @return value (String)
     */
    public static String valueOf(@NotNull String key) {
        if (!(successLoad && Optional.ofNullable(key).isPresent())) {
            return key;
        }
        return properties.getProperty(key);
    }
}