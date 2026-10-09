/*
 * Copyright (c) 2026 Color Blocks Extension
 * SPDX-License-Identifier: MIT
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.mard.pixel.fabric;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 物品注册类
 * 注册七彩粉末(mard_pigment)、方块染色台和所有颜色方块的物品形式（MardBlockItem两行显示）
 */
public class ModItems {
    // 七彩粉末（ID统一为mard_pigment，与Forge版一致）
    public static Item MARD_PIGMENT;
    // 方块染色台物品
    public static Item MARD_CRAFTING_TABLE;
    // 所有颜色方块物品列表
    public static final List<MardBlockItem> COLOR_BLOCK_ITEMS = new ArrayList<>();
    // 按颜色编号索引的物品映射
    public static final Map<String, MardBlockItem> COLOR_BLOCK_ITEMS_BY_CODE = new HashMap<>();

    public static void init() {
        // 注册七彩粉末
        MARD_PIGMENT = registerItem("mard_pigment",
                new Item(new Properties().setId(itemKey("mard_pigment"))));

        // 注册方块染色台物品
        MARD_CRAFTING_TABLE = registerItem("mard_crafting_table",
                new BlockItem(ModBlocks.MARD_CRAFTING_TABLE,
                        new Properties().setId(itemKey("mard_crafting_table")).useBlockDescriptionPrefix()));

        // 注册所有颜色方块的物品形式（使用MardBlockItem两行显示）
        for (ColorDefinition color : ColorRegistry.getAllColors()) {
            MardBlockItem blockItem = registerColorBlockItem(color);
            COLOR_BLOCK_ITEMS.add(blockItem);
            COLOR_BLOCK_ITEMS_BY_CODE.put(color.getCode(), blockItem);
        }

        MardPixelMod.LOGGER.info("Registered {} color block items (MardBlockItem), mard_pigment, and crafting table",
                COLOR_BLOCK_ITEMS.size());
    }

    private static ResourceKey<Item> itemKey(String id) {
        return ResourceKey.create(Registries.ITEM, MardPixelMod.id(id));
    }

    private static MardBlockItem registerColorBlockItem(ColorDefinition color) {
        String itemId = "color_block_" + color.getCode().toLowerCase();
        Block block = ModBlocks.getBlockByColorCode(color.getCode());
        MardBlockItem blockItem = new MardBlockItem(block, color.getCode(), color.getColorValue(),
                new Properties().setId(itemKey(itemId)));
        return registerItem(itemId, blockItem);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Item> T registerItem(String id, T item) {
        return (T) Registry.register(BuiltInRegistries.ITEM, MardPixelMod.id(id), item);
    }

    /**
     * 根据颜色编号获取物品
     */
    public static Item getItemByColorCode(String code) {
        return COLOR_BLOCK_ITEMS_BY_CODE.get(code);
    }
}
