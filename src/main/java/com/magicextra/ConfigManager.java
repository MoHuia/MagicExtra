package com.magicextra;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Path configPath;
    private static MyModConfig config;

    // 初始化配置
    public static void init(Path configFolder) {
        configPath = configFolder.resolve("magicextra/config.json");
        loadConfig();
    }

    // 读取配置
    public static void loadConfig() {
        try {
            if (Files.notExists(configPath)) {
                createDefaultConfig();
            }
            String json = new String(Files.readAllBytes(configPath));
            config = GSON.fromJson(json, MyModConfig.class);
        } catch (Exception e) {
            e.printStackTrace();
            // 创建默认配置如果出错
            config = new MyModConfig();
        }
    }

    // 创建默认配置文件
    private static void createDefaultConfig() throws OIException {
        MyModConfig defaultConfig = new MyModConfig();
        defaultConfig.setExampleNumber(42);

        String json = GSON.toJson(defaultConfig);
        Files.createDirectories(configPath.getParent());
        Files.write(configPath, json.getBytes());
    }

    // 获取配置实例
    public static MyModConfig getConfig() {
        return config;
    }
}