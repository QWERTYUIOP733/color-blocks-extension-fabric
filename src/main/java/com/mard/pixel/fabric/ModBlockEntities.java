package com.mard.pixel.fabric;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * 方块实体注册类
 */
public class ModBlockEntities {
    public static BlockEntityType<MardCraftingTableBlockEntity> MARD_CRAFTING_TABLE;

    public static void init() {
        MARD_CRAFTING_TABLE = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                MardPixelMod.id("mard_crafting_table"),
                BlockEntityType.Builder.of(MardCraftingTableBlockEntity::new,
                        ModBlocks.MARD_CRAFTING_TABLE).build(null));

        MardPixelMod.LOGGER.info("Registered block entities");
    }
}
