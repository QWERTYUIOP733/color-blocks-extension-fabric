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
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 方块注册类
 * 注册所有221个颜色方块（使用MardBlock自定义类）和方块染色台
 */
public class ModBlocks {
    // 方块染色台
    public static Block MARD_CRAFTING_TABLE;
    // 所有颜色方块列表
    public static final List<MardBlock> COLOR_BLOCKS = new ArrayList<>();
    // 按颜色编号索引的方块映射
    public static final Map<String, MardBlock> COLOR_BLOCKS_BY_CODE = new HashMap<>();

    public static void init() {
        // 注册方块染色台
        MARD_CRAFTING_TABLE = registerBlock("mard_crafting_table",
                new MardCraftingTableBlock());

        // 注册所有颜色方块（使用MardBlock）
        for (ColorDefinition color : ColorRegistry.getAllColors()) {
            MardBlock block = registerColorBlock(color);
            COLOR_BLOCKS.add(block);
            COLOR_BLOCKS_BY_CODE.put(color.getCode(), block);
        }

        MardPixelMod.LOGGER.info("Registered {} color blocks (MardBlock) and crafting table",
                COLOR_BLOCKS.size());
    }

    private static MardBlock registerColorBlock(ColorDefinition color) {
        String blockId = "color_block_" + color.getCode().toLowerCase();
        MardBlock block = new MardBlock(color.getCode(), color.getColorValue());
        return registerBlock(blockId, block);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Block> T registerBlock(String id, T block) {
        return (T) Registry.register(BuiltInRegistries.BLOCK, MardPixelMod.id(id), block);
    }

    /**
     * 根据颜色编号获取方块
     */
    public static Block getBlockByColorCode(String code) {
        return COLOR_BLOCKS_BY_CODE.get(code);
    }
}
