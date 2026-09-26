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
 * 管理3x3合成网格、输出槽和玩家物品栏
 */
public class MardCraftingScreenHandler extends AbstractContainerMenu {
    private final MardCraftingTableBlockEntity blockEntity;
    private final ContainerLevelAccess access;
    private final NonNullList<ItemStack> clientInventory;
    private final boolean isClient;

    // 槽位索引
    private static final int CRAFTING_START = 0;
    private static final int CRAFTING_END = 9;
    private static final int RESULT_SLOT = 9;
    private static final int PLAYER_INVENTORY_START = 10;
    private static final int PLAYER_INVENTORY_END = 37;
    private static final int PLAYER_HOTBAR_START = 37;
    private static final int PLAYER_HOTBAR_END = 46;

    public MardCraftingScreenHandler(int syncId, Inventory playerInventory, MardCraftingTableBlockEntity blockEntity) {
        super(ModScreenHandlers.MARD_CRAFTING_TABLE, syncId);
        this.blockEntity = blockEntity;
        this.access = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        this.clientInventory = null;
        this.isClient = false;

        // 添加3x3合成网格槽位
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new Slot(blockEntity,
                        row * 3 + col, 30 + col * 18, 17 + row * 18));
            }
        }

        // 添加输出槽位
        this.addSlot(new Slot(blockEntity, RESULT_SLOT, 124, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                blockEntity.takeResult(player);
                super.onTake(player, stack);
            }
        });

        // 添加玩家物品栏
        addPlayerInventory(playerInventory);
    }

    // 客户端构造函数 - 使用虚拟inventory避免NPE
    public MardCraftingScreenHandler(int syncId, Inventory playerInventory) {
        super(ModScreenHandlers.MARD_CRAFTING_TABLE, syncId);
        this.blockEntity = null;
        this.access = ContainerLevelAccess.NULL;
        this.clientInventory = NonNullList.withSize(10, ItemStack.EMPTY);
        this.isClient = true;

        // 添加3x3合成网格槽位（使用虚拟inventory）
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                final int slotIndex = row * 3 + col;
                this.addSlot(new Slot(new net.minecraft.world.SimpleContainer(10),
                        slotIndex, 30 + col * 18, 17 + row * 18));
            }
        }

        // 添加输出槽位
        this.addSlot(new Slot(new net.minecraft.world.SimpleContainer(10), RESULT_SLOT, 124, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        // 添加玩家物品栏
        addPlayerInventory(playerInventory);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory,
                        col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
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
                // 从输出槽移动物品
                if (!this.moveItemStackTo(itemstack1, PLAYER_INVENTORY_START, PLAYER_HOTBAR_END, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index >= PLAYER_INVENTORY_START) {
                // 从玩家物品栏移动物品到合成网格
                if (itemstack1.getItem() == ModItems.RAINBOW_POWDER) {
                    if (!this.moveItemStackTo(itemstack1, CRAFTING_START, CRAFTING_END, false)) {
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

    /**
     * 设置选中的颜色（从客户端发送）
     */
    public void setSelectedColor(String colorCode) {
        if (blockEntity != null) {
            blockEntity.setSelectedColor(colorCode);
        }
    }

    /**
     * 获取选中的颜色
     */
    public String getSelectedColor() {
        if (blockEntity != null) {
            return blockEntity.getSelectedColor();
        }
        return "A1";
    }
}
