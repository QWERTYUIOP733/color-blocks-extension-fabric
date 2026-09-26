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
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

/**
 * 方块染色台方块实体
 * 存储3x3合成网格（9格）和结果槽（1格）
 * 支持七彩粉末颜色选择模式：放入七彩粉末后，可从右侧列表选择颜色合成
 */
public class MardCraftingTableBlockEntity extends BlockEntity implements Container, MenuProvider {

    public static final int GRID_SIZE = 9;
    public static final int RESULT_SLOT = 9;
    public static final int TOTAL_SLOTS = 10;

    private String selectedColor = ""; // 当前选择的颜色色号，如"A1"
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(TOTAL_SLOTS, ItemStack.EMPTY);

    public MardCraftingTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MARD_CRAFTING_TABLE, pos, state);
    }

    public String getSelectedColor() {
        return selectedColor;
    }

    /**
     * 选择颜色（由客户端网络包调用）
     */
    public void selectColor(String code) {
        this.selectedColor = code != null ? code : "";
        if (level != null && !level.isClientSide) {
            updateCraftingResult();
            setChanged();
        }
    }

    /**
     * 检查合成网格中是否有七彩粉末
     */
    private boolean hasPigment() {
        for (int i = 0; i < GRID_SIZE; i++) {
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty() && isPigmentItem(stack)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断物品是否为七彩粉末
     */
    private boolean isPigmentItem(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (stack.getItem() == ModItems.MARD_PIGMENT) return true;
        String registryName = stack.getItem().getDescriptionId();
        return "item.mard_pixel.mard_pigment".equals(registryName);
    }

    // ==================== Container接口 ====================

    @Override
    public int getContainerSize() {
        return TOTAL_SLOTS;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : inventory) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot >= 0 && slot < TOTAL_SLOTS ? inventory.get(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (slot >= 0 && slot < TOTAL_SLOTS) {
            ItemStack stack = inventory.get(slot);
            ItemStack result = stack.split(amount);
            if (slot < GRID_SIZE && level != null && !level.isClientSide) {
                updateCraftingResult();
            }
            setChanged();
            return result;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot >= 0 && slot < TOTAL_SLOTS) {
            ItemStack stack = inventory.get(slot);
            inventory.set(slot, ItemStack.EMPTY);
            setChanged();
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot >= 0 && slot < TOTAL_SLOTS) {
            inventory.set(slot, stack);
            if (slot < GRID_SIZE && level != null && !level.isClientSide) {
                updateCraftingResult();
            }
            setChanged();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return !this.isRemoved() &&
                Vec3.atCenterOf(this.worldPosition).distanceToSqr(player.position()) <= 64.0;
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            inventory.set(i, ItemStack.EMPTY);
        }
        setChanged();
    }

    // ==================== 合成逻辑 ====================

    /**
     * 更新合成结果
     * 如果有七彩粉末且选择了颜色，则结果为对应颜色的64个方块
     * 否则使用原版配方系统，只允许模组内物品
     */
    private void updateCraftingResult() {
        if (level == null || level.isClientSide) return;

        // 七彩粉末颜色选择模式
        if (hasPigment() && !selectedColor.isEmpty()) {
            ItemStack result = MardPixelMod.buildStack(selectedColor);
            if (!result.isEmpty()) {
                result.setCount(64);
                inventory.set(RESULT_SLOT, result);
                return;
            }
        }

        // 原版配方系统（只允许模组内物品）
        net.minecraft.world.inventory.TransientCraftingContainer craftingContainer =
                new net.minecraft.world.inventory.TransientCraftingContainer(new AbstractContainerMenu(null, -1) {
                    @Override
                    public ItemStack quickMoveStack(Player player, int index) { return ItemStack.EMPTY; }
                    @Override
                    public boolean stillValid(Player player) { return true; }
                }, 3, 3);
        for (int i = 0; i < GRID_SIZE; i++) {
            craftingContainer.setItem(i, inventory.get(i).copy());
        }

        Optional<CraftingRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(RecipeType.CRAFTING, craftingContainer, level);

        if (recipe.isPresent()) {
            ItemStack result = recipe.get().assemble(craftingContainer, level.registryAccess());
            if (isMardPixelItem(result)) {
                inventory.set(RESULT_SLOT, result);
            } else {
                inventory.set(RESULT_SLOT, ItemStack.EMPTY);
            }
        } else {
            inventory.set(RESULT_SLOT, ItemStack.EMPTY);
        }
    }

    /**
     * 检查物品是否属于模组内物品
     */
    private boolean isMardPixelItem(ItemStack stack) {
        if (stack.isEmpty()) return false;
        String registryName = stack.getItem().getDescriptionId();
        return registryName.startsWith("item.mard_pixel.") || registryName.startsWith("block.mard_pixel.");
    }

    /**
     * 消耗合成材料（玩家取走结果时调用）
     */
    public void consumeMaterials() {
        if (hasPigment() && !selectedColor.isEmpty()) {
            // 七彩粉末模式：只消耗七彩粉末
            for (int i = 0; i < GRID_SIZE; i++) {
                ItemStack stack = inventory.get(i);
                if (!stack.isEmpty() && isPigmentItem(stack)) {
                    stack.shrink(1);
                }
            }
        } else {
            // 普通模式：消耗所有材料
            for (int i = 0; i < GRID_SIZE; i++) {
                ItemStack stack = inventory.get(i);
                if (!stack.isEmpty()) {
                    stack.shrink(1);
                }
            }
        }
        inventory.set(RESULT_SLOT, ItemStack.EMPTY);
        if (level != null && !level.isClientSide) {
            updateCraftingResult();
        }
    }

    // ==================== 序列化 ====================

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, inventory);
        tag.putString("SelectedColor", selectedColor);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ContainerHelper.loadAllItems(tag, inventory);
        selectedColor = tag.getString("SelectedColor");
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.mard_pixel.mard_crafting_table");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new MardCraftingScreenHandler(containerId, playerInventory, this);
    }

    /**
     * 掉落所有物品（方块被破坏时调用）
     */
    public void dropContents() {
        if (level == null || level.isClientSide) return;
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty()) {
                net.minecraft.world.level.block.Block.popResource(level, worldPosition, stack);
                inventory.set(i, ItemStack.EMPTY);
            }
        }
    }
}
