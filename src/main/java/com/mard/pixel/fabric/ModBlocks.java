package com.mard.pixel.fabric;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 方块注册类
 * 注册221个颜色方块和方块染色台
 */
public class ModBlocks {
    // 所有颜色方块列表
    public static final List<Block> COLOR_BLOCKS = new ArrayList<>();
    // 按颜色编号索引的方块映射
    public static final Map<String, Block> COLOR_BLOCKS_BY_CODE = new HashMap<>();
    // 方块染色台
    public static Block MARD_CRAFTING_TABLE;

    public static void init() {
        // 注册所有颜色方块
        for (ColorDefinition color : ColorRegistry.getAllColors()) {
            Block block = registerColorBlock(color);
            COLOR_BLOCKS.add(block);
            COLOR_BLOCKS_BY_CODE.put(color.getCode(), block);
        }

        // 注册方块染色台
        MARD_CRAFTING_TABLE = registerBlock("mard_crafting_table",
                new MardCraftingTableBlock(BlockBehaviour.Properties.of()
                        .strength(2.5f)
                        .requiresCorrectToolForDrops()));

        MardPixelMod.LOGGER.info("Registered {} color blocks and 1 crafting table", COLOR_BLOCKS.size());
    }

    private static Block registerColorBlock(ColorDefinition color) {
        String blockId = "color_block_" + color.getCode().toLowerCase();
        Block block = new Block(BlockBehaviour.Properties.of()
                .strength(2.0f)
                .requiresCorrectToolForDrops());
        return registerBlock(blockId, block);
    }

    private static Block registerBlock(String id, Block block) {
        return Registry.register(BuiltInRegistries.BLOCK, MardPixelMod.id(id), block);
    }

    /**
     * 根据颜色编号获取方块
     */
    public static Block getBlockByColorCode(String code) {
        return COLOR_BLOCKS_BY_CODE.get(code);
    }
}
