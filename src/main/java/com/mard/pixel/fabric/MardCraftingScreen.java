package com.mard.pixel.fabric;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/**
 * 方块染色台GUI界面
 * 左侧颜色选择面板(100px) + 中间3x3合成网格 + 右侧输出槽
 * 总宽度276px，高度166px
 */
public class MardCraftingScreen extends AbstractContainerScreen<MardCraftingScreenHandler> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(MardPixelMod.MOD_ID, "textures/gui/mard_crafting_table.png");

    // 颜色选择面板
    private static final int COLOR_PANEL_WIDTH = MardCraftingScreenHandler.COLOR_PANEL_WIDTH;
    private static final int COLOR_PANEL_HEIGHT = 166;
    private static final int COLOR_SLOT_SIZE = 14;
    private static final int COLOR_SLOTS_PER_ROW = 5;
    private static final int COLOR_PANEL_PADDING = 6;
    private static final int MAX_VISIBLE_COLORS = 30;

    // 当前显示的色系
    private String currentSeries = "A";
    // 颜色面板滚动偏移
    private int colorScrollOffset = 0;

    public MardCraftingScreen(MardCraftingScreenHandler handler, Inventory playerInventory, Component title) {
        super(handler, playerInventory, title);
        this.imageWidth = 176 + COLOR_PANEL_WIDTH; // 276
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        // 标题和背包标签位置（在合成区域上方）
        this.titleLabelX = COLOR_PANEL_WIDTH + 8;
        this.inventoryLabelX = COLOR_PANEL_WIDTH + 8;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, delta);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // 绘制颜色选择面板背景（左侧）
        int panelX = this.leftPos;
        int panelY = this.topPos;
        graphics.fill(panelX, panelY, panelX + COLOR_PANEL_WIDTH, panelY + COLOR_PANEL_HEIGHT, 0xFFC6C6C6);
        graphics.fill(panelX + 1, panelY + 1, panelX + COLOR_PANEL_WIDTH - 1, panelY + COLOR_PANEL_HEIGHT - 1, 0xFF8B8B8B);

        // 绘制色系标签
        graphics.drawString(this.font, currentSeries,
                panelX + COLOR_PANEL_PADDING, panelY + 4, 0xFFFFFF, false);
        graphics.drawString(this.font, "(1-9切换)",
                panelX + COLOR_PANEL_PADDING, panelY + 14, 0xCCCCCC, false);

        // 绘制主GUI背景（合成网格部分，右侧176px）
        RenderSystem.setShaderTexture(0, TEXTURE);
        int mainX = this.leftPos + COLOR_PANEL_WIDTH;
        graphics.blit(TEXTURE, mainX, this.topPos, 0, 0, 176, this.imageHeight);

        // 绘制颜色选择槽位
        renderColorSlots(graphics, mouseX, mouseY);
    }

    /**
     * 绘制颜色选择槽位
     */
    private void renderColorSlots(GuiGraphics graphics, int mouseX, int mouseY) {
        List<ColorDefinition> colors = ColorRegistry.getColorsBySeries(currentSeries);
        int startX = this.leftPos + COLOR_PANEL_PADDING;
        int startY = this.topPos + 28;

        int endIndex = Math.min(colorScrollOffset + MAX_VISIBLE_COLORS, colors.size());
        for (int i = colorScrollOffset; i < endIndex; i++) {
            ColorDefinition color = colors.get(i);
            int slotIndex = i - colorScrollOffset;
            int row = slotIndex / COLOR_SLOTS_PER_ROW;
            int col = slotIndex % COLOR_SLOTS_PER_ROW;
            int x = startX + col * (COLOR_SLOT_SIZE + 3);
            int y = startY + row * (COLOR_SLOT_SIZE + 3);

            // 绘制颜色方块（带边框）
            graphics.fill(x, y, x + COLOR_SLOT_SIZE, y + COLOR_SLOT_SIZE, 0xFF000000);
            graphics.fill(x + 1, y + 1, x + COLOR_SLOT_SIZE - 1, y + COLOR_SLOT_SIZE - 1,
                    0xFF000000 | color.getColorValue());

            // 高亮选中的颜色
            if (color.getCode().equals(menu.getSelectedColor())) {
                graphics.fill(x - 1, y - 1, x + COLOR_SLOT_SIZE + 1, y, 0xFFFFFFFF);
                graphics.fill(x - 1, y + COLOR_SLOT_SIZE, x + COLOR_SLOT_SIZE + 1, y + COLOR_SLOT_SIZE + 1, 0xFFFFFFFF);
                graphics.fill(x - 1, y, x, y + COLOR_SLOT_SIZE, 0xFFFFFFFF);
                graphics.fill(x + COLOR_SLOT_SIZE, y, x + COLOR_SLOT_SIZE + 1, y + COLOR_SLOT_SIZE, 0xFFFFFFFF);
            }

            // 鼠标悬停提示
            if (mouseX >= x && mouseX < x + COLOR_SLOT_SIZE &&
                    mouseY >= y && mouseY < y + COLOR_SLOT_SIZE) {
                graphics.renderTooltip(this.font,
                        Component.literal(color.getCode() + " " + color.getHex()),
                        mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 检查颜色选择槽位点击
        List<ColorDefinition> colors = ColorRegistry.getColorsBySeries(currentSeries);
        int startX = this.leftPos + COLOR_PANEL_PADDING;
        int startY = this.topPos + 28;

        int endIndex = Math.min(colorScrollOffset + MAX_VISIBLE_COLORS, colors.size());
        for (int i = colorScrollOffset; i < endIndex; i++) {
            ColorDefinition color = colors.get(i);
            int slotIndex = i - colorScrollOffset;
            int row = slotIndex / COLOR_SLOTS_PER_ROW;
            int col = slotIndex % COLOR_SLOTS_PER_ROW;
            int x = startX + col * (COLOR_SLOT_SIZE + 3);
            int y = startY + row * (COLOR_SLOT_SIZE + 3);

            if (mouseX >= x && mouseX < x + COLOR_SLOT_SIZE &&
                    mouseY >= y && mouseY < y + COLOR_SLOT_SIZE) {
                // 发送网络包选择颜色
                MardPixelClient.sendSelectColor(color.getCode());
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        // 颜色面板滚动
        if (mouseX >= this.leftPos && mouseX < this.leftPos + COLOR_PANEL_WIDTH &&
                mouseY >= this.topPos && mouseY < this.topPos + COLOR_PANEL_HEIGHT) {
            List<ColorDefinition> colors = ColorRegistry.getColorsBySeries(currentSeries);
            int maxOffset = Math.max(0, colors.size() - MAX_VISIBLE_COLORS);
            colorScrollOffset = Math.max(0, Math.min(maxOffset,
                    colorScrollOffset - (int) Math.signum(amount) * COLOR_SLOTS_PER_ROW));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // 数字键切换色系
        if (keyCode >= GLFW.GLFW_KEY_1 && keyCode <= GLFW.GLFW_KEY_9) {
            String[] series = {"A", "B", "C", "D", "E", "F", "G", "H", "M"};
            int index = keyCode - GLFW.GLFW_KEY_1;
            if (index < series.length) {
                currentSeries = series[index];
                colorScrollOffset = 0;
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
