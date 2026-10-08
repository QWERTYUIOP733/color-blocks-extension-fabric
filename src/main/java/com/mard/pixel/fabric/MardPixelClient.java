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

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/**
 * 客户端初始化类
 * 注册GUI、颜色提供者、快捷键、网络包发送
 */
public class MardPixelClient implements ClientModInitializer {
    private static net.minecraft.client.KeyMapping keyOpenPalette;

    @Override
    public void onInitializeClient() {
        MardPixelMod.LOGGER.info("Initializing Color Blocks Extension client...");

        // 注册颜色提供者（方块和物品）
        registerColorProviders();

        // 注册GUI - 使用原版MenuScreens注册
        net.minecraft.client.gui.screens.MenuScreens.register(
                ModScreenHandlers.MARD_CRAFTING_TABLE,
                MardCraftingScreen::new);

        // 注册快捷键（G键打开色板）
        keyOpenPalette = KeyBindingHelper.registerKeyBinding(new net.minecraft.client.KeyMapping(
                "key.mard_pixel.open_palette",
                GLFW.GLFW_KEY_G,
                "category.mard_pixel"));

        // 注册快捷键事件
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyOpenPalette.consumeClick()) {
                if (client.player != null && client.screen == null) {
                    client.setScreen(new ColorPaletteScreen());
                }
            }
        });

        // 注册Tooltip清理（移除JEI等模组添加的额外信息，只保留色号+RGB）
        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            if (stack.getItem() instanceof MardBlockItem) {
                // 从后往前移除，只保留第一行（名称）和包含RGB的行
                for (int i = lines.size() - 1; i >= 1; i--) {
                    String text = lines.get(i).getString();
                    if (!text.contains("RGB")) {
                        lines.remove(i);
                    }
                }
            }
        });

        MardPixelMod.LOGGER.info("Color Blocks Extension client initialized!");
    }

    /**
     * 注册方块和物品的颜色提供者
     * 所有颜色方块使用tintindex=0进行程序染色
     */
    private void registerColorProviders() {
        // 方块颜色提供者
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
            if (tintIndex == 0 && state.getBlock() instanceof MardBlock mardBlock) {
                return mardBlock.getRgb();
            }
            return 0xFFFFFF;
        }, ModBlocks.COLOR_BLOCKS.toArray(new net.minecraft.world.level.block.Block[0]));

        // 物品颜色提供者
        for (MardBlockItem item : ModItems.COLOR_BLOCK_ITEMS) {
            ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
                if (tintIndex == 0 && stack.getItem() instanceof MardBlockItem mbi) {
                    return mbi.getRgb();
                }
                return 0xFFFFFF;
            }, item);
        }
    }

    // ==================== 网络包发送方法 ====================

    /**
     * 发送请求物品包（UI点击获取）
     */
    public static void sendRequestItem(String target) {
        ClientPlayNetworking.send(new MardNetwork.RequestItemPayload(target));
    }

    /**
     * 发送快捷栏包（输入色号放入快捷栏）
     */
    public static void sendHotbar(String code) {
        ClientPlayNetworking.send(new MardNetwork.HotbarPayload(code));
    }

    /**
     * 发送七彩粉末合成包
     */
    public static void sendCraftItem(String code) {
        ClientPlayNetworking.send(new MardNetwork.CraftItemPayload(code));
    }

    /**
     * 发送合成台选择颜色包
     */
    public static void sendSelectColor(String code) {
        ClientPlayNetworking.send(new MardNetwork.SelectColorPayload(code));
    }
}
