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

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * 方块染色台GUI界面
 * 布局与原版工作台一致，左侧添加颜色选择列表（仅七彩粉末模式）
 * 颜色面板固定放在主界面左侧，避免与右侧JEI物品管理器冲突
 */
public class MardCraftingScreen extends AbstractContainerScreen<MardCraftingScreenHandler> {

    private static final int BASE_WIDTH = 176;
    private static final int BASE_HEIGHT = 166;

    // 颜色选择面板
    private static final int COLOR_PANEL_DEFAULT_WIDTH = 130;
    private static final int COLOR_PANEL_MIN_WIDTH = 80;
    private static final int COLOR_PANEL_HEIGHT = 166;
    private static final int COLOR_PANEL_GAP = 4;
    private static final int COLOR_PANEL_SCREEN_LEFT_MARGIN = 2;
    private static final int COLOR_ITEM_HEIGHT = 22;
    private static final int COLOR_SWATCH_SIZE = 16;
    private static final int COLOR_TEXT_PADDING = 4;

    private int panelWidth = COLOR_PANEL_DEFAULT_WIDTH;
    private int maxVisibleColors = (COLOR_PANEL_HEIGHT - 20) / COLOR_ITEM_HEIGHT;
    private int scrollOffset = 0;
    private String selectedColor = "";

    public MardCraftingScreen(MardCraftingScreenHandler handler, Inventory playerInventory, Component title) {
        super(handler, playerInventory, title);
        this.imageWidth = BASE_WIDTH;
        this.imageHeight = BASE_HEIGHT;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = this.imageHeight - 94;
        updatePanelWidth();
    }

    private void updatePanelWidth() {
        int availableWidth = this.leftPos - COLOR_PANEL_SCREEN_LEFT_MARGIN - COLOR_PANEL_GAP;
        panelWidth = Math.max(COLOR_PANEL_MIN_WIDTH, Math.min(COLOR_PANEL_DEFAULT_WIDTH, availableWidth));
        maxVisibleColors = Math.max(3, (COLOR_PANEL_HEIGHT - 20) / COLOR_ITEM_HEIGHT);
        int totalColors = ColorRegistry.getAllColors().size();
        int maxOffset = Math.max(0, totalColors - maxVisibleColors);
        scrollOffset = Math.min(scrollOffset, maxOffset);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);

        if (menu.hasPigment()) {
            renderColorPanel(graphics, mouseX, mouseY);
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        // 主背景
        graphics.fill(x, y, x + BASE_WIDTH, y + BASE_HEIGHT, 0xFFC6C6C6);
        // 标题栏
        graphics.fill(x, y, x + BASE_WIDTH, y + 16, 0xFF8B8B8B);

        // 3x3合成网格槽位背景
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int slotX = x + MardCraftingScreenHandler.GRID_START_X + col * MardCraftingScreenHandler.SLOT_SIZE;
                int slotY = y + MardCraftingScreenHandler.GRID_START_Y + row * MardCraftingScreenHandler.SLOT_SIZE;
                drawSlotBackground(graphics, slotX, slotY);
            }
        }

        // 结果槽
        int resultX = x + MardCraftingScreenHandler.RESULT_X;
        int resultY = y + MardCraftingScreenHandler.RESULT_Y;
        drawSlotBackground(graphics, resultX, resultY);

        // 箭头
        int arrowX = x + 90;
        int arrowY = y + 38;
        graphics.fill(arrowX, arrowY + 3, arrowX + 22, arrowY + 7, 0xFF555555);
        graphics.fill(arrowX + 18, arrowY, arrowX + 22, arrowY + 10, 0xFF555555);

        // 玩家背包
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int slotX = x + MardCraftingScreenHandler.PLAYER_INV_X + col * MardCraftingScreenHandler.SLOT_SIZE;
                int slotY = y + MardCraftingScreenHandler.PLAYER_INV_Y + row * MardCraftingScreenHandler.SLOT_SIZE;
                drawSlotBackground(graphics, slotX, slotY);
            }
        }

        // 快捷栏
        for (int col = 0; col < 9; col++) {
            int slotX = x + MardCraftingScreenHandler.PLAYER_INV_X + col * MardCraftingScreenHandler.SLOT_SIZE;
            int slotY = y + MardCraftingScreenHandler.HOTBAR_Y;
            drawSlotBackground(graphics, slotX, slotY);
        }
    }

    private void drawSlotBackground(GuiGraphics graphics, int x, int y) {
        int size = MardCraftingScreenHandler.SLOT_SIZE;
        graphics.fill(x + 1, y + 1, x + size - 1, y + size - 1, 0xFF8B8B8B);
        graphics.fill(x, y, x + size, y + 1, 0xFFFFFFFF);
        graphics.fill(x, y, x + 1, y + size, 0xFFFFFFFF);
        graphics.fill(x, y + size - 1, x + size, y + size, 0xFF373737);
        graphics.fill(x + size - 1, y, x + size, y + size, 0xFF373737);
    }

    private void renderColorPanel(GuiGraphics graphics, int mouseX, int mouseY) {
        updatePanelWidth();

        int panelX = this.leftPos - panelWidth - COLOR_PANEL_GAP;
        panelX = Math.max(COLOR_PANEL_SCREEN_LEFT_MARGIN, panelX);
        int panelY = this.topPos;

        // 面板背景
        graphics.fill(panelX, panelY, panelX + panelWidth, panelY + COLOR_PANEL_HEIGHT, 0xFFC6C6C6);
        graphics.fill(panelX, panelY, panelX + panelWidth, panelY + 1, 0xFFFFFFFF);
        graphics.fill(panelX, panelY, panelX + 1, panelY + COLOR_PANEL_HEIGHT, 0xFFFFFFFF);
        graphics.fill(panelX, panelY + COLOR_PANEL_HEIGHT - 1, panelX + panelWidth, panelY + COLOR_PANEL_HEIGHT, 0xFF373737);
        graphics.fill(panelX + panelWidth - 1, panelY, panelX + panelWidth, panelY + COLOR_PANEL_HEIGHT, 0xFF373737);

        // 标题栏
        graphics.fill(panelX + 1, panelY + 1, panelX + panelWidth - 1, panelY + 14, 0xFF8B8B8B);
        graphics.drawString(this.font, "选择颜色", panelX + 4, panelY + 4, 0xFFFFFFFF, false);

        List<ColorDefinition> allColors = ColorRegistry.getAllColors();
        int totalColors = allColors.size();
        int startIndex = Math.max(0, Math.min(scrollOffset, totalColors - 1));
        int endIndex = Math.min(startIndex + maxVisibleColors, totalColors);
        int listStartY = panelY + 18;

        for (int i = startIndex; i < endIndex; i++) {
            ColorDefinition color = allColors.get(i);
            int itemY = listStartY + (i - startIndex) * COLOR_ITEM_HEIGHT;

            // 选中高亮
            if (color.getCode().equals(selectedColor)) {
                graphics.fill(panelX + 2, itemY, panelX + panelWidth - 2, itemY + COLOR_ITEM_HEIGHT - 1, 0xFF90EE90);
            }

            // 颜色方块
            int swatchX = panelX + 4;
            int swatchY = itemY + 3;
            graphics.fill(swatchX, swatchY, swatchX + COLOR_SWATCH_SIZE, swatchY + COLOR_SWATCH_SIZE, 0xFF000000);
            graphics.fill(swatchX + 1, swatchY + 1, swatchX + COLOR_SWATCH_SIZE - 1, swatchY + COLOR_SWATCH_SIZE - 1,
                    0xFF000000 | color.getColorValue());

            // 文字
            int textX = swatchX + COLOR_SWATCH_SIZE + COLOR_TEXT_PADDING;
            graphics.drawString(this.font, color.getCode(), textX, itemY + 3, 0x404040, false);
            String rgbText = String.format("RGB:%d,%d,%d", color.getRed(), color.getGreen(), color.getBlue());
            graphics.drawString(this.font, rgbText, textX, itemY + 13, 0x606060, false);
        }

        // 滚动条
        if (totalColors > maxVisibleColors) {
            int scrollbarX = panelX + panelWidth - 6;
            int scrollbarY = listStartY;
            int scrollbarHeight = COLOR_PANEL_HEIGHT - 22;
            int thumbHeight = Math.max(20, (int) ((float) maxVisibleColors / totalColors * scrollbarHeight));
            int maxOffset = Math.max(0, totalColors - maxVisibleColors);
            int thumbY = scrollbarY + (maxOffset > 0 ? (int) ((float) scrollOffset / maxOffset * (scrollbarHeight - thumbHeight)) : 0);
            graphics.fill(scrollbarX, scrollbarY, scrollbarX + 4, scrollbarY + scrollbarHeight, 0xFF8B8B8B);
            graphics.fill(scrollbarX, thumbY, scrollbarX + 4, thumbY + thumbHeight, 0xFF555555);
        }
    }

    private int[] getColorPanelBounds() {
        updatePanelWidth();
        int panelX = this.leftPos - panelWidth - COLOR_PANEL_GAP;
        panelX = Math.max(COLOR_PANEL_SCREEN_LEFT_MARGIN, panelX);
        return new int[]{panelX, this.topPos};
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (menu.hasPigment()) {
            int[] bounds = getColorPanelBounds();
            int panelX = bounds[0];
            int panelY = bounds[1];
            int listStartY = panelY + 18;

            if (mouseX >= panelX && mouseX <= panelX + panelWidth &&
                mouseY >= listStartY && mouseY <= panelY + COLOR_PANEL_HEIGHT) {
                int totalColors = ColorRegistry.getAllColors().size();
                int maxOffset = Math.max(0, totalColors - maxVisibleColors);
                scrollOffset = Math.max(0, Math.min(scrollOffset - (int) delta, maxOffset));
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (menu.hasPigment() && button == 0) {
            int[] bounds = getColorPanelBounds();
            int panelX = bounds[0];
            int panelY = bounds[1];
            int listStartY = panelY + 18;

            if (mouseX >= panelX && mouseX <= panelX + panelWidth &&
                mouseY >= listStartY && mouseY <= panelY + COLOR_PANEL_HEIGHT) {

                int itemIndex = (int) ((mouseY - listStartY) / COLOR_ITEM_HEIGHT);
                int colorIndex = scrollOffset + itemIndex;
                List<ColorDefinition> allColors = ColorRegistry.getAllColors();

                if (colorIndex >= 0 && colorIndex < allColors.size()) {
                    ColorDefinition color = allColors.get(colorIndex);
                    selectedColor = color.getCode();
                    MardPixelClient.sendSelectColor(color.getCode());
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);

        if (menu.hasPigment()) {
            int[] bounds = getColorPanelBounds();
            int panelX = bounds[0];
            int panelY = bounds[1];
            int listStartY = panelY + 18;

            if (mouseX >= panelX && mouseX <= panelX + panelWidth &&
                mouseY >= listStartY && mouseY <= panelY + COLOR_PANEL_HEIGHT) {

                int itemIndex = (int) ((mouseY - listStartY) / COLOR_ITEM_HEIGHT);
                int colorIndex = scrollOffset + itemIndex;
                List<ColorDefinition> allColors = ColorRegistry.getAllColors();

                if (colorIndex >= 0 && colorIndex < allColors.size()) {
                    ColorDefinition color = allColors.get(colorIndex);
                    ItemStack stack = new ItemStack(ModItems.getItemByColorCode(color.getCode()));
                    if (!stack.isEmpty()) {
                        graphics.renderTooltip(this.font, stack, mouseX, mouseY);
                    }
                }
            }
        }
    }
}
