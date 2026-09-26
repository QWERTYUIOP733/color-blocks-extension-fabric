package com.mard.pixel.fabric;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * G键色板UI界面
 * 显示所有颜色方块，点击可直接获取（创造模式）
 */
public class ColorPaletteScreen extends Screen {
    private static final int COLOR_SLOT_SIZE = 20;
    private static final int COLOR_SLOTS_PER_ROW = 12;
    private static final int PADDING = 20;

    private String currentSeries = "A";
    private int scrollOffset = 0;

    public ColorPaletteScreen() {
        super(Component.translatable("screen.mard_pixel.color_palette"));
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(graphics);

        // 绘制标题
        graphics.drawCenteredString(this.font, this.title,
                this.width / 2, 15, 0xFFFFFF);

        // 绘制色系标签
        graphics.drawString(this.font, "Series: " + currentSeries +
                        " (Press 1-9 to switch)",
                PADDING, 30, 0xFFFFFF);

        // 绘制颜色方块
        renderColorBlocks(graphics, mouseX, mouseY);

        super.render(graphics, mouseX, mouseY, delta);
    }

    /**
     * 绘制颜色方块
     */
    private void renderColorBlocks(GuiGraphics graphics, int mouseX, int mouseY) {
        List<ColorDefinition> colors = ColorRegistry.getColorsBySeries(currentSeries);
        int startX = PADDING;
        int startY = 50;

        for (int i = scrollOffset; i < colors.size(); i++) {
            ColorDefinition color = colors.get(i);
            int slotIndex = i - scrollOffset;
            int row = slotIndex / COLOR_SLOTS_PER_ROW;
            int col = slotIndex % COLOR_SLOTS_PER_ROW;
            int x = startX + col * (COLOR_SLOT_SIZE + 4);
            int y = startY + row * (COLOR_SLOT_SIZE + 4);

            // 检查是否超出屏幕
            if (y + COLOR_SLOT_SIZE > this.height - PADDING) {
                break;
            }

            // 绘制颜色方块背景
            graphics.fill(x - 1, y - 1, x + COLOR_SLOT_SIZE + 1, y + COLOR_SLOT_SIZE + 1, 0xFF555555);
            graphics.fill(x, y, x + COLOR_SLOT_SIZE, y + COLOR_SLOT_SIZE, 0xFF000000 | color.getColorValue());

            // 绘制颜色编号
            graphics.drawString(this.font, color.getCode(),
                    x + 2, y + COLOR_SLOT_SIZE + 2, 0xFFFFFF, false);

            // 鼠标悬停提示
            if (mouseX >= x && mouseX < x + COLOR_SLOT_SIZE &&
                    mouseY >= y && mouseY < y + COLOR_SLOT_SIZE) {
                graphics.renderTooltip(this.font,
                        Component.literal(color.getCode() + " " + color.getHex() +
                                " RGB(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")"),
                        mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 检查颜色方块点击
        List<ColorDefinition> colors = ColorRegistry.getColorsBySeries(currentSeries);
        int startX = PADDING;
        int startY = 50;

        for (int i = scrollOffset; i < colors.size(); i++) {
            ColorDefinition color = colors.get(i);
            int slotIndex = i - scrollOffset;
            int row = slotIndex / COLOR_SLOTS_PER_ROW;
            int col = slotIndex % COLOR_SLOTS_PER_ROW;
            int x = startX + col * (COLOR_SLOT_SIZE + 4);
            int y = startY + row * (COLOR_SLOT_SIZE + 4);

            if (y + COLOR_SLOT_SIZE > this.height - PADDING) {
                break;
            }

            if (mouseX >= x && mouseX < x + COLOR_SLOT_SIZE &&
                    mouseY >= y && mouseY < y + COLOR_SLOT_SIZE) {
                // 发送网络包请求物品（服务端会检查游戏模式）
                MardPixelClient.sendRequestItem(color.getCode());
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        List<ColorDefinition> colors = ColorRegistry.getColorsBySeries(currentSeries);
        int maxOffset = Math.max(0, colors.size() - COLOR_SLOTS_PER_ROW * 5);
        scrollOffset = Math.max(0, Math.min(maxOffset,
                scrollOffset - (int) Math.signum(amount) * COLOR_SLOTS_PER_ROW));
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
