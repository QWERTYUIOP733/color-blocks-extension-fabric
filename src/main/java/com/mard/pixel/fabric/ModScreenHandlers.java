package com.mard.pixel.fabric;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

/**
 * 屏幕处理器注册类
 */
public class ModScreenHandlers {
    public static MenuType<MardCraftingScreenHandler> MARD_CRAFTING_TABLE;

    public static void init() {
        MARD_CRAFTING_TABLE = Registry.register(
                BuiltInRegistries.MENU,
                MardPixelMod.id("mard_crafting_table"),
                new MenuType<>((syncId, playerInventory) -> new MardCraftingScreenHandler(syncId, playerInventory), FeatureFlags.DEFAULT_FLAGS));
    }
}
