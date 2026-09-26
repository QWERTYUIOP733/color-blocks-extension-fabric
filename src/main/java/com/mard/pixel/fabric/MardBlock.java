package com.mard.pixel.fabric;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

/**
 * 自定义颜色方块类
 * 存储色号编号和RGB值，用于程序染色
 */
public class MardBlock extends Block {
    private final String code;
    private final int rgb;

    public MardBlock(String code, int rgb) {
        this(code, rgb, BlockBehaviour.Properties.of()
                .mapColor(MapColor.NONE)
                .strength(0.6f, 1.0f));
    }

    protected MardBlock(String code, int rgb, BlockBehaviour.Properties properties) {
        super(properties);
        this.code = code;
        this.rgb = rgb;
    }

    public String getCode() { return code; }
    public int getRgb() { return rgb; }
}
