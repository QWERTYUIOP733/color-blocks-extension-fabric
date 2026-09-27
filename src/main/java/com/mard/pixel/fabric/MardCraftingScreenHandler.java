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

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * 方块染色台GUI菜单处理器
 * 布局与原版工作台一致：3x3合成网格 + 结果槽 + 玩家背包
 * 颜色选择面板由Screen在左侧外部渲染（仅七彩粉末模式）
 */
public class MardCraftingScreenHandler extends AbstractContainerMenu {
    // 槽位位置（与原版工作台一致）
    public static final int GRID_START_X = 30;
    public static final int GRID_START_Y = 17;
    public static final int SLOT_SIZE = 18;
    public static final int RESULT_X = 124;
    public static final int RESULT_Y = 35;
    public static final int PLAYER_INV_X = 8;
    public static final int PLAYER_INV_Y = 84;
    public static final int HOTBAR_Y = 142;

    // 槽位索引
    private static final int CRAFTING_START = 0;
    private static final int RESULT_SLOT = 9;
    private static final int PLAYER_INVENTORY_START = 10;
    private static final int PLAYER_INVENTORY_END = 37;
    private static final int PLAYER_HOTBAR_START = 37;
    private static final int PLAYER_HOTBAR_END = 46;

    private final MardCraftingTableBlockEntity blockEntity;
    private final ContainerLevelAccess access;
    private final boolean isClient;

    // 服务端构造函数
    public MardCraftingScreenHandler(int syncId, Inventory playerInventory, MardCraftingTableBlockEntity blockEntity) {
        super(ModScreenHandlers.MARD_CRAFTING_TABLE, syncId);
        this.blockEntity = blockEntity;
        this.access = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        this.isClient = false;
        registerSlots(playerInventory, blockEntity);
    }

    // 客户端构造函数
    public MardCraftingScreenHandler(int syncId, Inventory playerInventory) {
        super(ModScreenHandlers.MARD_CRAFTING_TABLE, syncId);
        this.blockEntity = null;
        this.access = ContainerLevelAccess.NULL;
        this.isClient = true;
        net.minecraft.world.SimpleContainer dummy = new net.minecraft.world.SimpleContainer(10);
        registerSlots(playerInventory, dummy);
    }

    private void registerSlots(Inventory playerInventory, net.minecraft.world.Container container) {
        // 3x3合成网格
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new Slot(container, row * 3 + col,
                        GRID_START_X + col * SLOT_SIZE, GRID_START_Y + row * SLOT_SIZE));
            }
        }

        // 结果槽
        this.addSlot(new Slot(container, RESULT_SLOT, RESULT_X, RESULT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) { return false; }

            @Override
            public void onTake(Player player, ItemStack stack) {
                if (blockEntity != null) blockEntity.consumeMaterials();
                super.onTake(player, stack);
            }
        });

        // 玩家背包
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9,
                        PLAYER_INV_X + col * SLOT_SIZE, PLAYER_INV_Y + row * SLOT_SIZE));
            }
        }

        // 快捷栏
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col,
                    PLAYER_INV_X + col * SLOT_SIZE, HOTBAR_Y));
        }
    }

    /**
     * 检测合成网格中是否有七彩粉末
     */
    public boolean hasPigment() {
        if (blockEntity != null) {
            return blockEntity.hasPigment();
        }
        // 客户端：检查前9个槽位
        for (int i = CRAFTING_START; i < RESULT_SLOT; i++) {
            if (i < this.slots.size()) {
                ItemStack stack = this.slots.get(i).getItem();
                if (!stack.isEmpty() && stack.getItem() == ModItems.MARD_PIGMENT) {
                    return true;
                }
            }
        }
        return false;
    }

    public void selectColor(String code) {
        if (blockEntity != null) blockEntity.selectColor(code);
    }

    public String getSelectedColor() {
        if (blockEntity != null) return blockEntity.getSelectedColor();
        return "";
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (blockEntity != null) {
            blockEntity.setChanged();
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index == RESULT_SLOT) {
                if (!this.moveItemStackTo(itemstack1, PLAYER_INVENTORY_START, PLAYER_HOTBAR_END, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index >= PLAYER_INVENTORY_START) {
                if (itemstack1.getItem() == ModItems.MARD_PIGMENT) {
                    if (!this.moveItemStackTo(itemstack1, CRAFTING_START, RESULT_SLOT, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < PLAYER_HOTBAR_START) {
                    if (!this.moveItemStackTo(itemstack1, PLAYER_HOTBAR_START, PLAYER_HOTBAR_END, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemstack1, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, PLAYER_INVENTORY_START, PLAYER_HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        if (isClient || blockEntity == null) return true;
        return stillValid(access, player, ModBlocks.MARD_CRAFTING_TABLE);
    }

    public MardCraftingTableBlockEntity getBlockEntity() { return blockEntity; }
    public boolean isClient() { return isClient; }
}
