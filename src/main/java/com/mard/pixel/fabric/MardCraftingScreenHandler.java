package com.mard.pixel.fabric;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 方块染色台GUI处理器
 * 左侧颜色选择面板 + 中间3x3合成网格 + 右侧输出槽
 * 槽位位置需要加上左侧面板宽度偏移
 */
public class MardCraftingScreenHandler extends AbstractContainerMenu {
    public static final int COLOR_PANEL_WIDTH = 100;

    private final MardCraftingTableBlockEntity blockEntity;
    private final ContainerLevelAccess access;
    private final boolean isClient;

    // 槽位索引
    private static final int CRAFTING_START = 0;
    private static final int RESULT_SLOT = 9;
    private static final int PLAYER_INVENTORY_START = 10;
    private static final int PLAYER_INVENTORY_END = 37;
    private static final int PLAYER_HOTBAR_START = 37;
    private static final int PLAYER_HOTBAR_END = 46;

    // 槽位位置（相对于GUI左上角，已包含左侧颜色面板偏移）
    private static final int GRID_START_X = COLOR_PANEL_WIDTH + 30;
    private static final int GRID_START_Y = 17;
    private static final int SLOT_SIZE = 18;
    private static final int RESULT_X = COLOR_PANEL_WIDTH + 124;
    private static final int RESULT_Y = 35;
    private static final int PLAYER_INV_X = COLOR_PANEL_WIDTH + 8;
    private static final int PLAYER_INV_Y = 84;
    private static final int HOTBAR_Y = 142;

    // 服务端构造函数
    public MardCraftingScreenHandler(int syncId, Inventory playerInventory, MardCraftingTableBlockEntity blockEntity) {
        super(ModScreenHandlers.MARD_CRAFTING_TABLE, syncId);
        this.blockEntity = blockEntity;
        this.access = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        this.isClient = false;
        registerSlots(playerInventory, blockEntity);
    }

    // 客户端构造函数 - 使用同一个虚拟inventory
    public MardCraftingScreenHandler(int syncId, Inventory playerInventory) {
        super(ModScreenHandlers.MARD_CRAFTING_TABLE, syncId);
        this.blockEntity = null;
        this.access = ContainerLevelAccess.NULL;
        this.isClient = true;
        net.minecraft.world.SimpleContainer dummyContainer = new net.minecraft.world.SimpleContainer(10);
        registerSlots(playerInventory, dummyContainer);
    }

    private void registerSlots(Inventory playerInventory, net.minecraft.world.Container container) {
        // 3x3合成网格（槽位0-8）
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new Slot(container,
                        row * 3 + col,
                        GRID_START_X + col * SLOT_SIZE,
                        GRID_START_Y + row * SLOT_SIZE));
            }
        }

        // 结果槽（槽位9）
        this.addSlot(new Slot(container, RESULT_SLOT, RESULT_X, RESULT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                if (blockEntity != null) {
                    blockEntity.consumeMaterials();
                }
                super.onTake(player, stack);
            }
        });

        // 玩家背包（27格）
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory,
                        col + row * 9 + 9,
                        PLAYER_INV_X + col * SLOT_SIZE,
                        PLAYER_INV_Y + row * SLOT_SIZE));
            }
        }

        // 玩家快捷栏（9格）
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col,
                    PLAYER_INV_X + col * SLOT_SIZE,
                    HOTBAR_Y));
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
                // 从输出槽移到玩家背包
                if (!this.moveItemStackTo(itemstack1, PLAYER_INVENTORY_START, PLAYER_HOTBAR_END, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index >= PLAYER_INVENTORY_START) {
                // 从玩家背包移到合成网格
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
        if (isClient || blockEntity == null) {
            return true;
        }
        return stillValid(access, player, ModBlocks.MARD_CRAFTING_TABLE);
    }

    public MardCraftingTableBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public boolean isClient() {
        return isClient;
    }

    public void setSelectedColor(String colorCode) {
        if (blockEntity != null) {
            blockEntity.selectColor(colorCode);
        }
    }

    public String getSelectedColor() {
        if (blockEntity != null) {
            return blockEntity.getSelectedColor();
        }
        return "";
    }
}
