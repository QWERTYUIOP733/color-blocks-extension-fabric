package com.mard.pixel.fabric;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 物品注册类
 * 注册七彩粉末、方块染色台和所有颜色方块的物品形式
 */
public class ModItems {
    // 七彩粉末
    public static Item RAINBOW_POWDER;
    // 方块染色台物品
    public static Item MARD_CRAFTING_TABLE;
    // 所有颜色方块物品列表
    public static final List<Item> COLOR_BLOCK_ITEMS = new ArrayList<>();
    // 按颜色编号索引的物品映射
    public static final Map<String, Item> COLOR_BLOCK_ITEMS_BY_CODE = new HashMap<>();

    public static void init() {
        // 注册七彩粉末
        RAINBOW_POWDER = registerItem("rainbow_powder",
                new Item(new Properties()));

        // 注册方块染色台物品
        MARD_CRAFTING_TABLE = registerItem("mard_crafting_table",
                new BlockItem(ModBlocks.MARD_CRAFTING_TABLE, new Properties()));

        // 注册所有颜色方块的物品形式
        for (ColorDefinition color : ColorRegistry.getAllColors()) {
            BlockItem blockItem = registerColorBlockItem(color);
            COLOR_BLOCK_ITEMS.add(blockItem);
            COLOR_BLOCK_ITEMS_BY_CODE.put(color.getCode(), blockItem);
        }

        MardPixelMod.LOGGER.info("Registered {} color block items, rainbow powder, and crafting table",
                COLOR_BLOCK_ITEMS.size());
    }

    private static BlockItem registerColorBlockItem(ColorDefinition color) {
        String itemId = "color_block_" + color.getCode().toLowerCase();
        Block block = ModBlocks.getBlockByColorCode(color.getCode());
        BlockItem blockItem = new BlockItem(block, new Properties());
        return registerItem(itemId, blockItem);
    }

    private static <T extends Item> T registerItem(String id, T item) {
        return Registry.register(BuiltInRegistries.ITEM, MardPixelMod.id(id), item);
    }

    /**
     * 根据颜色编号获取物品
     */
    public static Item getItemByColorCode(String code) {
        return COLOR_BLOCK_ITEMS_BY_CODE.get(code);
    }
}
