package com.mard.pixel.fabric;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * Fabric网络包管理
 * 4种网络包：
 * 1. C2S_REQUEST_ITEM - 客户端请求物品（UI点击获取）
 * 2. C2S_HOTBAR - 输入色号放入快捷栏
 * 3. C2S_CRAFT_ITEM - 使用七彩粉末合成
 * 4. C2S_SELECT_COLOR - 合成台选择颜色
 */
public final class MardNetwork {
    public static final ResourceLocation REQUEST_ITEM_ID = new ResourceLocation(MardPixelMod.MOD_ID, "request_item");
    public static final ResourceLocation HOTBAR_ID = new ResourceLocation(MardPixelMod.MOD_ID, "hotbar");
    public static final ResourceLocation CRAFT_ITEM_ID = new ResourceLocation(MardPixelMod.MOD_ID, "craft_item");
    public static final ResourceLocation SELECT_COLOR_ID = new ResourceLocation(MardPixelMod.MOD_ID, "select_color");

    public static void init() {
        // 服务端接收包注册在MardPixelMod中
    }

    // ==================== 编码/解码 ====================

    public static void encodeString(FriendlyByteBuf buf, String value) {
        buf.writeUtf(value != null ? value : "");
    }

    public static String decodeString(FriendlyByteBuf buf) {
        try {
            return buf.readUtf();
        } catch (Exception e) {
            return "";
        }
    }

    // ==================== 服务端处理 ====================

    /**
     * 处理客户端请求物品（UI点击获取）
     * 生存模式禁用，必须通过合成台或七彩粉末合成
     */
    public static void handleRequestItem(ServerPlayer player, String target) {
        if (player == null) return;
        if (!player.isCreative() && !player.isSpectator()) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                "生存模式下无法直接获取方块，请使用方块染色台或七彩粉末合成").withStyle(net.minecraft.ChatFormatting.RED));
            return;
        }
        MardPixelMod.giveRequestedStack(player, target);
    }

    /**
     * 处理输入色号放入快捷栏
     * 生存模式禁用
     */
    public static void handleHotbar(ServerPlayer player, String code) {
        if (player == null) return;
        if (!player.isCreative() && !player.isSpectator()) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                "生存模式下无法直接获取方块，请使用方块染色台或七彩粉末合成").withStyle(net.minecraft.ChatFormatting.RED));
            return;
        }
        MardPixelMod.giveToHotbar(player, code);
    }

    /**
     * 处理使用七彩粉末合成
     */
    public static void handleCraftItem(ServerPlayer player, String code) {
        if (player != null) {
            MardPixelMod.craftWithPigment(player, code);
        }
    }

    /**
     * 处理合成台选择颜色
     */
    public static void handleSelectColor(ServerPlayer player, String code) {
        if (player != null && player.containerMenu instanceof MardCraftingScreenHandler menu) {
            if (menu.getBlockEntity() != null) {
                menu.getBlockEntity().selectColor(code);
            }
        }
    }

    private MardNetwork() {}
}
