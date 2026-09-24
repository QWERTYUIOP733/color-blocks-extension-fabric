package com.mard.pixel.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 彩色方块扩展 - 主类
 * 适用于Minecraft 26.3 + Fabric 0.19.5
 */
public class MardPixelMod implements ModInitializer {
    public static final String MOD_ID = "mard_pixel";
    public static final Logger LOGGER = LoggerFactory.getLogger("Color Blocks Extension");

    // 创造模式物品栏标签页
    public static CreativeModeTab COLOR_BLOCKS_TAB;

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Color Blocks Extension for Minecraft 26.3 + Fabric");

        // 初始化颜色注册表
        ColorRegistry.init();

        // 注册方块和物品
        ModBlocks.init();
        ModItems.init();

        // 注册方块实体
        ModBlockEntities.init();

        // 注册创造模式物品栏
        registerCreativeTabs();

        LOGGER.info("Color Blocks Extension initialized with {} colors", ColorRegistry.getTotalColors());
    }

    private void registerCreativeTabs() {
        COLOR_BLOCKS_TAB = FabricItemGroup.builder()
                .title(Component.translatable("itemGroup.mard_pixel.color_blocks"))
                .icon(() -> new ItemStack(ModBlocks.COLOR_BLOCKS.get(0)))
                .displayItems((parameters, output) -> {
                    // 添加所有颜色方块
                    ModBlocks.COLOR_BLOCKS.forEach(output::accept);
                    // 添加七彩粉末
                    output.accept(ModItems.RAINBOW_POWDER);
                    // 添加方块染色台
                    output.accept(ModItems.MARD_CRAFTING_TABLE);
                })
                .build();

        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,
                new ResourceLocation(MOD_ID, "color_blocks"),
                COLOR_BLOCKS_TAB);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
