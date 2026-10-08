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

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * Fabric网络包管理（1.21 CustomPacketPayload版本）
 * 4种网络包：
 * 1. C2S_REQUEST_ITEM - 客户端请求物品（UI点击获取）
 * 2. C2S_HOTBAR - 输入色号放入快捷栏
 * 3. C2S_CRAFT_ITEM - 使用七彩粉末合成
 * 4. C2S_SELECT_COLOR - 合成台选择颜色
 */
public final class MardNetwork {

    // ==================== 网络包定义 ====================

    /**
     * 请求物品包
     */
    public record RequestItemPayload(String target) implements CustomPacketPayload {
        public static final Type<RequestItemPayload> TYPE =
                new Type<>(ResourceLocation.fromNamespaceAndPath(MardPixelMod.MOD_ID, "request_item"));

        public static final StreamCodec<FriendlyByteBuf, RequestItemPayload> CODEC =
                StreamCodec.of(
                        (buf, payload) -> buf.writeUtf(payload.target()),
                        buf -> new RequestItemPayload(buf.readUtf())
                );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /**
     * 快捷栏包
     */
    public record HotbarPayload(String code) implements CustomPacketPayload {
        public static final Type<HotbarPayload> TYPE =
                new Type<>(ResourceLocation.fromNamespaceAndPath(MardPixelMod.MOD_ID, "hotbar"));

        public static final StreamCodec<FriendlyByteBuf, HotbarPayload> CODEC =
                StreamCodec.of(
                        (buf, payload) -> buf.writeUtf(payload.code()),
                        buf -> new HotbarPayload(buf.readUtf())
                );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /**
     * 七彩粉末合成包
     */
    public record CraftItemPayload(String code) implements CustomPacketPayload {
        public static final Type<CraftItemPayload> TYPE =
                new Type<>(ResourceLocation.fromNamespaceAndPath(MardPixelMod.MOD_ID, "craft_item"));

        public static final StreamCodec<FriendlyByteBuf, CraftItemPayload> CODEC =
                StreamCodec.of(
                        (buf, payload) -> buf.writeUtf(payload.code()),
                        buf -> new CraftItemPayload(buf.readUtf())
                );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /**
     * 合成台选择颜色包
     */
    public record SelectColorPayload(String code) implements CustomPacketPayload {
        public static final Type<SelectColorPayload> TYPE =
                new Type<>(ResourceLocation.fromNamespaceAndPath(MardPixelMod.MOD_ID, "select_color"));

        public static final StreamCodec<FriendlyByteBuf, SelectColorPayload> CODEC =
                StreamCodec.of(
                        (buf, payload) -> buf.writeUtf(payload.code()),
                        buf -> new SelectColorPayload(buf.readUtf())
                );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    // ==================== 初始化 ====================

    public static void init() {
        // 注册在MardPixelMod中
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
