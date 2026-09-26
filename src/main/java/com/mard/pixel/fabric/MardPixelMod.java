package com.mard.pixel.fabric;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 彩色方块扩展 Mod 主类（Fabric版）
 *
 * 核心功能：
 * 1. 221色基础色块（程序染色，色标准确）
 * 2. 物品两行名称（色号编号 + RGB值）
 * 3. 按系列分类的9个创造模式标签页（A/B/C/D/E/F/G/H/M）
 * 4. 快速物品检索（/mardp give / find）
 * 5. 颜色选取UI（快捷键G）
 * 6. 输入色号快速获取（支持批量输入）
 * 7. 生存模式禁用直接获取方块
 */
public class MardPixelMod implements ModInitializer {
    public static final String MOD_ID = "mard_pixel";
    public static final Logger LOGGER = LoggerFactory.getLogger("Color Blocks Extension");

    // 创造模式标签页列表（按系列分类）
    public static final List<CreativeModeTab> CREATIVE_TABS = new ArrayList<>();

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Color Blocks Extension (Fabric)...");

        // 初始化颜色注册表
        ColorRegistry.init();

        // 注册方块和物品
        ModBlocks.init();
        ModItems.init();
        ModBlockEntities.init();
        ModScreenHandlers.init();

        // 注册创造模式标签页（按系列分类）
        registerCreativeTabs();

        // 注册网络包（服务端接收）
        registerServerNetworking();

        // 注册命令
        registerCommands();

        LOGGER.info("Color Blocks Extension initialized! {} colors, {} creative tabs",
                ColorRegistry.getAllColors().size(), CREATIVE_TABS.size());
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    // ==================== 创造模式标签页 ====================

    /**
     * 注册按系列分类的创造模式标签页
     * 每个系列（A/B/C/.../M）一个独立标签页，第一个标签页包含七彩粉末和染色台
     */
    private void registerCreativeTabs() {
        // 收集所有系列（去重并保持顺序）
        Set<String> seriesSet = new LinkedHashSet<>();
        for (ColorDefinition color : ColorRegistry.getAllColors()) {
            seriesSet.add(color.getSeries());
        }
        List<String> seriesList = new ArrayList<>(seriesSet);

        // 按系列创建标签页
        for (int i = 0; i < seriesList.size(); i++) {
            final String series = seriesList.get(i);
            final boolean isFirst = (i == 0);

            CreativeModeTab tab = FabricItemGroup.builder()
                    .title(Component.literal(series))
                    .icon(() -> {
                        // 用该系列第一个色块作为图标
                        for (ColorDefinition c : ColorRegistry.getAllColors()) {
                            if (c.getSeries().equals(series)) {
                                return new ItemStack(ModItems.getItemByColorCode(c.getCode()));
                            }
                        }
                        return ItemStack.EMPTY;
                    })
                    .displayItems((params, output) -> {
                        // 第一个标签页添加七彩粉末和染色台
                        if (isFirst) {
                            output.accept(new ItemStack(ModItems.MARD_PIGMENT));
                            output.accept(new ItemStack(ModItems.MARD_CRAFTING_TABLE));
                        }
                        // 添加该系列的所有色块
                        for (ColorDefinition c : ColorRegistry.getAllColors()) {
                            if (c.getSeries().equals(series)) {
                                output.accept(new ItemStack(ModItems.getItemByColorCode(c.getCode())));
                            }
                        }
                    })
                    .build();

            Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,
                    id("mard_pixel_" + series.toLowerCase()), tab);
            CREATIVE_TABS.add(tab);
        }
    }

    // ==================== 网络包注册 ====================

    private void registerServerNetworking() {
        // 客户端请求物品（UI点击获取）
        ServerPlayNetworking.registerGlobalReceiver(MardNetwork.REQUEST_ITEM_ID,
                (server, player, handler, buf, responseSender) -> {
                    String target = MardNetwork.decodeString(buf);
                    server.execute(() -> MardNetwork.handleRequestItem(player, target));
                });

        // 输入色号放入快捷栏
        ServerPlayNetworking.registerGlobalReceiver(MardNetwork.HOTBAR_ID,
                (server, player, handler, buf, responseSender) -> {
                    String code = MardNetwork.decodeString(buf);
                    server.execute(() -> MardNetwork.handleHotbar(player, code));
                });

        // 使用七彩粉末合成
        ServerPlayNetworking.registerGlobalReceiver(MardNetwork.CRAFT_ITEM_ID,
                (server, player, handler, buf, responseSender) -> {
                    String code = MardNetwork.decodeString(buf);
                    server.execute(() -> MardNetwork.handleCraftItem(player, code));
                });

        // 合成台选择颜色
        ServerPlayNetworking.registerGlobalReceiver(MardNetwork.SELECT_COLOR_ID,
                (server, player, handler, buf, responseSender) -> {
                    String code = MardNetwork.decodeString(buf);
                    server.execute(() -> MardNetwork.handleSelectColor(player, code));
                });
    }

    // ==================== 命令注册 ====================

    private void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    Commands.literal("mardp")
                            // 查找最近颜色
                            .then(Commands.literal("find")
                                    .then(Commands.argument("hex", StringArgumentType.string())
                                            .executes(ctx -> {
                                                ServerPlayer p = ctx.getSource().getPlayerOrException();
                                                String hex = StringArgumentType.getString(ctx, "hex");
                                                int rgb = parseHexColor(hex);
                                                ColorDefinition nearest = ColorRegistry.findNearest(rgb);
                                                if (nearest != null) {
                                                    p.sendSystemMessage(Component.literal("最近颜色："
                                                            + nearest.getCode() + " " + nearest.getHex()));
                                                } else {
                                                    p.sendSystemMessage(Component.literal("未找到颜色").withStyle(ChatFormatting.RED));
                                                }
                                                return 1;
                                            })))
                            // 快速给予物品
                            .then(Commands.literal("give")
                                    .then(Commands.argument("target", StringArgumentType.greedyString())
                                            .executes(ctx -> {
                                                ServerPlayer p = ctx.getSource().getPlayerOrException();
                                                giveRequestedItem(p, StringArgumentType.getString(ctx, "target"));
                                                return 1;
                                            }))));
        });
    }

    private int parseHexColor(String hex) {
        try {
            String clean = hex.replace("#", "").replace("0x", "");
            return Integer.parseInt(clean, 16);
        } catch (Exception e) {
            return 0xFFFFFF;
        }
    }

    // ==================== 物品生成 ====================

    /**
     * 根据目标字符串生成物品栈
     */
    public static ItemStack buildStack(String target) {
        if (target == null) return ItemStack.EMPTY;
        String t = target.trim();
        if (t.isEmpty()) return ItemStack.EMPTY;

        if (t.startsWith("色块:")) {
            t = t.substring(5).trim();
        }

        // 按色号查找
        ColorDefinition color = ColorRegistry.getByCode(t);
        if (color != null) {
            return new ItemStack(ModItems.getItemByColorCode(color.getCode()));
        }
        return ItemStack.EMPTY;
    }

    /**
     * 快速给予指定物品（/mardp give命令），给予1个
     */
    public static void giveRequestedItem(ServerPlayer player, String target) {
        if (target == null || target.isBlank()) {
            player.sendSystemMessage(Component.literal("用法：<色号> 或直接输入色号").withStyle(ChatFormatting.GRAY));
            return;
        }
        ItemStack stack = buildStack(target);
        if (stack == null || stack.isEmpty()) {
            player.sendSystemMessage(Component.literal("无法生成物品：" + target).withStyle(ChatFormatting.RED));
            return;
        }
        Component itemName = stack.getHoverName();
        int count = stack.getCount();
        player.getInventory().add(stack);
        player.sendSystemMessage(Component.literal("已给予 ").append(itemName).append(" x" + count));
    }

    /**
     * UI点击色块时给予一组（64个）对应颜色的方块
     */
    public static void giveRequestedStack(ServerPlayer player, String target) {
        if (target == null || target.isBlank()) return;
        ItemStack stack = buildStack(target);
        if (stack == null || stack.isEmpty()) return;
        stack.setCount(64);
        Component itemName = stack.getHoverName();
        player.getInventory().add(stack);
        player.sendSystemMessage(Component.literal("已给予一组 ").append(itemName));
    }

    /**
     * 输入色号后将一组（64个）对应颜色的方块放入快捷栏
     */
    public static void giveToHotbar(ServerPlayer player, String code) {
        if (code == null || code.isBlank()) {
            player.sendSystemMessage(Component.literal("请输入色号").withStyle(ChatFormatting.RED));
            return;
        }
        String target = code.toUpperCase().trim();
        ItemStack stack = buildStack(target);
        if (stack == null || stack.isEmpty()) {
            player.sendSystemMessage(Component.literal("色号不存在: " + code).withStyle(ChatFormatting.RED));
            return;
        }
        stack.setCount(64);

        Inventory inv = player.getInventory();
        boolean placed = false;
        for (int i = 0; i < 9; i++) {
            if (inv.getItem(i).isEmpty()) {
                inv.setItem(i, stack);
                placed = true;
                break;
            }
        }
        if (!placed) {
            placed = inv.add(stack);
        }
        Component hotbarName = stack.getHoverName();
        if (!placed) {
            player.drop(stack, false);
        }
        player.sendSystemMessage(Component.literal("已放入快捷栏一组 ").append(hotbarName));
    }

    /**
     * 使用七彩粉末合成色块
     * 检查玩家背包中是否有七彩粉末，有则消耗1个，给予64个对应色块
     */
    public static void craftWithPigment(ServerPlayer player, String code) {
        if (code == null || code.isBlank()) {
            player.sendSystemMessage(Component.literal("色号无效").withStyle(ChatFormatting.RED));
            return;
        }

        Inventory inv = player.getInventory();
        boolean hasPigment = false;
        int pigmentSlot = -1;

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack slotStack = inv.getItem(i);
            if (!slotStack.isEmpty() && slotStack.getItem() == ModItems.MARD_PIGMENT) {
                hasPigment = true;
                pigmentSlot = i;
                break;
            }
        }

        if (!hasPigment) {
            player.sendSystemMessage(Component.literal("背包中没有七彩粉末，无法合成").withStyle(ChatFormatting.RED));
            return;
        }

        String target = code.toUpperCase().trim();
        ItemStack stack = buildStack(target);
        if (stack == null || stack.isEmpty()) {
            player.sendSystemMessage(Component.literal("色号不存在: " + code).withStyle(ChatFormatting.RED));
            return;
        }
        stack.setCount(64);

        // 消耗1个七彩粉末
        ItemStack pigmentStack = inv.getItem(pigmentSlot);
        pigmentStack.shrink(1);
        if (pigmentStack.isEmpty()) {
            inv.setItem(pigmentSlot, ItemStack.EMPTY);
        }

        // 给予色块
        Component craftName = stack.getHoverName();
        boolean placed = inv.add(stack);
        if (!placed) {
            player.drop(stack, false);
        }

        player.sendSystemMessage(Component.literal("消耗1个七彩粉末，合成一组 ")
                .append(craftName));
    }
}
