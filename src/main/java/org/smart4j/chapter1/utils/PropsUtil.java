package org.smart4j.chapter1.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * 属性文件工具类
 */
@Slf4j
public final class PropsUtil {

    private PropsUtil() {
    }

    /**
     * 加载 classpath 下的属性文件
     */
    public static Properties loadProps(String fileName) {
        Properties props = new Properties();

        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {

            if (inputStream == null) {
                throw new IllegalArgumentException("Properties file not found: " + fileName);
            }

            props.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        } catch (IOException e) {
            log.error("Failed to load properties file: {}", fileName, e);
            throw new IllegalStateException("Failed to load properties file: " + fileName, e);
        }

        return props;
    }

    /**
     * 获取字符串属性
     */
    public static String getString(Properties props, String key) {
        return getString(props, key, "");
    }

    /**
     * 获取字符串属性（指定默认值）
     */
    public static String getString(Properties props, String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    /**
     * 获取 int 属性
     */
    public static int getInt(Properties props, String key) {
        return getInt(props, key, 0);
    }

    /**
     * 获取 int 属性（指定默认值）
     */
    public static int getInt(Properties props, String key, int defaultValue) {
        return CastUtil.castInt(props.getProperty(key), defaultValue);
    }

    /**
     * 获取 boolean 属性
     */
    public static boolean getBoolean(Properties props, String key) {
        return getBoolean(props, key, false);
    }

    /**
     * 获取 boolean 属性（指定默认值）
     */
    public static boolean getBoolean(Properties props, String key, boolean defaultValue) {
        return CastUtil.castBoolean(props.getProperty(key), defaultValue);
    }
}
