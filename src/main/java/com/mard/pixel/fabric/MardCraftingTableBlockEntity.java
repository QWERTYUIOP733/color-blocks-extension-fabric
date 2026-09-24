package com.mard.pixel.fabric;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.Containers;
import org.jetbrains.annotations.Nullable;

/**
 * 方块染色台方块实体
 * 管理3x3合成网格和输出槽
 */
public class MardCraftingTableBlockEntity extends BlockEntity implements MenuProvider, Container {
    // 3x3合成网格 (9个槽位) + 1个输出槽
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(10, ItemStack.EMPTY);
    // 当前选中的颜色编号
    private String selectedColorCode = "A1";

    public MardCraftingTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MARD_CRAFTING_TABLE, pos, state);
    }

    public void tick() {
        if (level != null && !level.isClientSide) {
            // 更新合成结果
            updateCraftingResult();
        }
    }

    /**
     * 更新合成结果
     * 检查合成网格中是否有七彩粉末，如果有则输出选中颜色的方块x64
     */
    private void updateCraftingResult() {
        ItemStack result = ItemStack.EMPTY;

        // 检查是否有七彩粉末
        boolean hasRainbowPowder = false;
        for (int i = 0; i < 9; i++) {
            if (inventory.get(i).getItem() == ModItems.RAINBOW_POWDER) {
                hasRainbowPowder = true;
                break;
            }
        }

        if (hasRainbowPowder) {
            // 输出选中颜色的方块x64
            ItemStack colorBlock = new ItemStack(ModItems.getItemByColorCode(selectedColorCode));
            colorBlock.setCount(64);
            result = colorBlock;
        }

        inventory.set(9, result);
    }

    /**
     * 取出合成结果
     */
    public ItemStack takeResult(Player player) {
        ItemStack result = inventory.get(9).copy();
        if (!result.isEmpty()) {
            // 消耗一个七彩粉末
            for (int i = 0; i < 9; i++) {
                if (inventory.get(i).getItem() == ModItems.RAINBOW_POWDER) {
                    inventory.get(i).shrink(1);
                    break;
                }
            }
            inventory.set(9, ItemStack.EMPTY);
            setChanged();
        }
        return result;
    }

    /**
     * 设置选中的颜色
     */
    public void setSelectedColor(String colorCode) {
        this.selectedColorCode = colorCode;
        setChanged();
    }

    /**
     * 获取选中的颜色
     */
    public String getSelectedColor() {
        return selectedColorCode;
    }

    /**
     * 获取物品栏
     */
    public NonNullList<ItemStack> getInventory() {
        return inventory;
    }

    /**
     * 掉落所有物品
     */
    public void dropContents() {
        if (level != null) {
            Containers.dropContents(level, worldPosition, inventory);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ContainerHelper.loadAllItems(tag, inventory);
        selectedColorCode = tag.getString("SelectedColor");
        if (selectedColorCode.isEmpty()) {
            selectedColorCode = "A1";
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, inventory);
        tag.putString("SelectedColor", selectedColorCode);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.mard_pixel.mard_crafting_table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new MardCraftingScreenHandler(syncId, playerInventory, this);
    }

    // Container接口实现
    @Override
    public int getContainerSize() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot >= 0 && slot < inventory.size() ? inventory.get(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (slot >= 0 && slot < inventory.size()) {
            ItemStack stack = inventory.get(slot);
            ItemStack result = stack.split(amount);
            if (stack.isEmpty()) {
                inventory.set(slot, ItemStack.EMPTY);
            }
            setChanged();
            return result;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot >= 0 && slot < inventory.size()) {
            ItemStack stack = inventory.get(slot);
            inventory.set(slot, ItemStack.EMPTY);
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot >= 0 && slot < inventory.size()) {
            inventory.set(slot, stack);
            setChanged();
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        if (level == null || level.getBlockEntity(worldPosition) != this) {
            return false;
        }
        return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clearContent() {
        inventory.clear();
    }
}
