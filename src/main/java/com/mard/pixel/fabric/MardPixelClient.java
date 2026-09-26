package com.mard.pixel.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import org.lwjgl.glfw.GLFW;

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

        // 注册GUI
        net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry.register(
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
        FriendlyByteBuf buf = new FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
        MardNetwork.encodeString(buf, target);
        ClientPlayNetworking.send(MardNetwork.REQUEST_ITEM_ID, buf);
    }

    /**
     * 发送快捷栏包（输入色号放入快捷栏）
     */
    public static void sendHotbar(String code) {
        FriendlyByteBuf buf = new FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
        MardNetwork.encodeString(buf, code);
        ClientPlayNetworking.send(MardNetwork.HOTBAR_ID, buf);
    }

    /**
     * 发送七彩粉末合成包
     */
    public static void sendCraftItem(String code) {
        FriendlyByteBuf buf = new FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
        MardNetwork.encodeString(buf, code);
        ClientPlayNetworking.send(MardNetwork.CRAFT_ITEM_ID, buf);
    }

    /**
     * 发送合成台选择颜色包
     */
    public static void sendSelectColor(String code) {
        FriendlyByteBuf buf = new FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
        MardNetwork.encodeString(buf, code);
        ClientPlayNetworking.send(MardNetwork.SELECT_COLOR_ID, buf);
    }
}
