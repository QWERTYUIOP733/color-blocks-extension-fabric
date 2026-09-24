package com.mard.pixel.fabric;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 颜色注册表
 * 加载并管理所有221个颜色定义
 */
public class ColorRegistry {
    private static final Map<String, ColorDefinition> COLORS_BY_CODE = new HashMap<>();
    private static final List<ColorDefinition> ALL_COLORS = new ArrayList<>();
    private static final Map<String, List<ColorDefinition>> COLORS_BY_SERIES = new HashMap<>();

    public static void init() {
        try {
            // 从资源文件加载颜色配置
            InputStream inputStream = ColorRegistry.class.getResourceAsStream("/assets/mard_pixel/colors.json");
            if (inputStream != null) {
                InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                JsonObject jsonObject = new Gson().fromJson(reader, JsonObject.class);
                JsonArray colorsArray = jsonObject.getAsJsonArray("colors");

                for (int i = 0; i < colorsArray.size(); i++) {
                    JsonObject colorObj = colorsArray.get(i).getAsJsonObject();
                    String code = colorObj.get("code").getAsString();
                    String hex = colorObj.get("hex").getAsString();
                    String series = colorObj.get("series").getAsString();

                    ColorDefinition color = new ColorDefinition(code, hex, series);
                    registerColor(color);
                }

                MardPixelMod.LOGGER.info("Loaded {} colors from colors.json", ALL_COLORS.size());
            } else {
                MardPixelMod.LOGGER.error("Could not find colors.json resource file!");
            }
        } catch (Exception e) {
            MardPixelMod.LOGGER.error("Failed to load colors.json", e);
        }
    }

    private static void registerColor(ColorDefinition color) {
        COLORS_BY_CODE.put(color.getCode(), color);
        ALL_COLORS.add(color);
        COLORS_BY_SERIES.computeIfAbsent(color.getSeries(), k -> new ArrayList<>()).add(color);
    }

    public static ColorDefinition getColorByCode(String code) {
        return COLORS_BY_CODE.get(code);
    }

    public static List<ColorDefinition> getAllColors() {
        return Collections.unmodifiableList(ALL_COLORS);
    }

    public static List<ColorDefinition> getColorsBySeries(String series) {
        return Collections.unmodifiableList(COLORS_BY_SERIES.getOrDefault(series, new ArrayList<>()));
    }

    public static Set<String> getAllSeries() {
        return Collections.unmodifiableSet(COLORS_BY_SERIES.keySet());
    }

    public static int getTotalColors() {
        return ALL_COLORS.size();
    }
}
