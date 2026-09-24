package com.mard.pixel.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

/**
 * 客户端初始化类
 * 注册GUI界面、颜色提供者和快捷键
 */
public class MardPixelClient implements ClientModInitializer {
    // G键打开色板UI
    public static KeyMapping openColorPaletteKey;

    @Override
    public void onInitializeClient() {
        MardPixelMod.LOGGER.info("Initializing Color Blocks Extension client");

        // 注册屏幕处理器
        ModScreenHandlers.init();

        // 注册GUI界面
        MenuScreens.register(ModScreenHandlers.MARD_CRAFTING_TABLE, MardCraftingScreen::new);

        // 注册方块颜色提供者（用于tintindex染色）
        registerColorProviders();

        // 注册快捷键
        registerKeyBindings();

        // 注册快捷键事件
        registerKeyBindingEvents();

        MardPixelMod.LOGGER.info("Color Blocks Extension client initialized");
    }

    /**
     * 注册方块颜色提供者
     * 为每个颜色方块设置对应的颜色值
     */
    private void registerColorProviders() {
        for (ColorDefinition color : ColorRegistry.getAllColors()) {
            var block = ModBlocks.getBlockByColorCode(color.getCode());
            if (block != null) {
                ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) ->
                        color.getColorValue(), block);

                var item = ModItems.getItemByColorCode(color.getCode());
                if (item != null) {
                    ColorProviderRegistry.ITEM.register((stack, tintIndex) ->
                            color.getColorValue(), item);
                }
            }
        }
    }

    /**
     * 注册快捷键
     */
    private void registerKeyBindings() {
        openColorPaletteKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.mard_pixel.open_color_palette",
                GLFW.GLFW_KEY_G,
                "category.mard_pixel"
        ));
    }

    /**
     * 注册快捷键事件
     */
    private void registerKeyBindingEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openColorPaletteKey.consumeClick()) {
                if (client.player != null && client.screen == null) {
                    // 打开色板UI
                    client.setScreen(new ColorPaletteScreen());
                }
            }
        });
    }
}
