package com.mard.pixel.fabric;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * G键色板UI界面
 * 显示所有颜色方块的实际物品图标，点击可获取（创造模式）
 */
public class ColorPaletteScreen extends Screen {
    private static final int SLOT_SIZE = 24;
    private static final int SLOTS_PER_ROW = 10;
    private static final int PADDING = 16;
    private static final int HEADER_HEIGHT = 50;

    private String currentSeries = "A";
    private int scrollOffset = 0;

    public ColorPaletteScreen() {
        super(Component.translatable("screen.mard_pixel.color_palette"));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(graphics);

        // 绘制半透明背景面板
        int panelX = PADDING;
        int panelY = PADDING;
        int panelWidth = this.width - PADDING * 2;
        int panelHeight = this.height - PADDING * 2;
        graphics.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xF0101010);
        graphics.fill(panelX + 1, panelY + 1, panelX + panelWidth - 1, panelY + panelHeight - 1, 0xF0202020);

        // 绘制标题
        graphics.drawCenteredString(this.font, this.title,
                this.width / 2, panelY + 8, 0xFFFFFF);

        // 绘制色系选择提示
        graphics.drawCenteredString(this.font,
                "当前色系: " + currentSeries + "  (按1-9切换 A/B/C/D/E/F/G/H/M, G键关闭)",
                this.width / 2, panelY + 22, 0xAAAAAA);

        // 绘制颜色方块（使用实际物品渲染）
        renderColorBlocks(graphics, mouseX, mouseY);

        super.render(graphics, mouseX, mouseY, delta);
    }

    /**
     * 绘制颜色方块（使用实际物品图标）
     */
    private void renderColorBlocks(GuiGraphics graphics, int mouseX, int mouseY) {
        List<ColorDefinition> colors = ColorRegistry.getColorsBySeries(currentSeries);
        int startX = PADDING + 10;
        int startY = PADDING + HEADER_HEIGHT;
        int usableWidth = this.width - PADDING * 2 - 20;
        int slotsPerRow = Math.max(1, usableWidth / SLOT_SIZE);

        for (int i = scrollOffset; i < colors.size(); i++) {
            ColorDefinition color = colors.get(i);
            int slotIndex = i - scrollOffset;
            int row = slotIndex / slotsPerRow;
            int col = slotIndex % slotsPerRow;
            int x = startX + col * SLOT_SIZE;
            int y = startY + row * SLOT_SIZE;

            // 检查是否超出屏幕底部
            if (y + SLOT_SIZE > this.height - PADDING - 10) {
                break;
            }

            // 绘制槽位背景
            graphics.fill(x, y, x + SLOT_SIZE - 2, y + SLOT_SIZE - 2, 0xFF333333);
            graphics.fill(x + 1, y + 1, x + SLOT_SIZE - 3, y + SLOT_SIZE - 3, 0xFF1A1A1A);

            // 渲染实际物品（ColorProvider会自动染色）
            ItemStack stack = new ItemStack(ModItems.getItemByColorCode(color.getCode()));
            graphics.renderItem(stack, x + 3, y + 3);
            graphics.renderItemDecorations(this.font, stack, x + 3, y + 3);

            // 绘制色号文字（在方块下方）
            graphics.drawString(this.font, color.getCode(),
                    x + 1, y + SLOT_SIZE - 1, 0xFFFFFF, false);

            // 鼠标悬停提示
            if (mouseX >= x && mouseX < x + SLOT_SIZE - 2 &&
                    mouseY >= y && mouseY < y + SLOT_SIZE - 2) {
                graphics.renderTooltip(this.font,
                        Component.literal(color.getCode() + " " + color.getHex() +
                                "  RGB(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")"),
                        mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        List<ColorDefinition> colors = ColorRegistry.getColorsBySeries(currentSeries);
        int startX = PADDING + 10;
        int startY = PADDING + HEADER_HEIGHT;
        int usableWidth = this.width - PADDING * 2 - 20;
        int slotsPerRow = Math.max(1, usableWidth / SLOT_SIZE);

        for (int i = scrollOffset; i < colors.size(); i++) {
            ColorDefinition color = colors.get(i);
            int slotIndex = i - scrollOffset;
            int row = slotIndex / slotsPerRow;
            int col = slotIndex % slotsPerRow;
            int x = startX + col * SLOT_SIZE;
            int y = startY + row * SLOT_SIZE;

            if (y + SLOT_SIZE > this.height - PADDING - 10) {
                break;
            }

            if (mouseX >= x && mouseX < x + SLOT_SIZE - 2 &&
                    mouseY >= y && mouseY < y + SLOT_SIZE - 2) {
                // 发送网络包请求物品（服务端检查游戏模式）
                MardPixelClient.sendRequestItem(color.getCode());
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        List<ColorDefinition> colors = ColorRegistry.getColorsBySeries(currentSeries);
        int usableWidth = this.width - PADDING * 2 - 20;
        int slotsPerRow = Math.max(1, usableWidth / SLOT_SIZE);
        int maxOffset = Math.max(0, colors.size() - slotsPerRow * 3);
        scrollOffset = Math.max(0, Math.min(maxOffset,
                scrollOffset - (int) Math.signum(amount) * slotsPerRow));
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // 数字键切换色系
        if (keyCode >= org.lwjgl.glfw.GLFW.GLFW_KEY_1 && keyCode <= org.lwjgl.glfw.GLFW.GLFW_KEY_9) {
            String[] series = {"A", "B", "C", "D", "E", "F", "G", "H", "M"};
            int index = keyCode - org.lwjgl.glfw.GLFW.GLFW_KEY_1;
            if (index < series.length) {
                currentSeries = series[index];
                scrollOffset = 0;
                return true;
            }
        }

        // G键或ESC关闭界面
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_G || keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
            this.onClose();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
