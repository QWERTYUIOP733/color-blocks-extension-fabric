package com.mard.pixel.fabric;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * 彩色方块扩展 G键主UI界面。
 * 三页面结构：主菜单 / 颜色选取 / 输入色号
 */
public class ColorPaletteScreen extends Screen {

    private enum Page { MAIN, SWATCHES, INPUT }

    private record Entry(String code, int rgb) {}
    private record Rect(int x, int y, int w, int h) {
        boolean hit(double mx, double my) { return mx >= x && mx < x + w && my >= y && my < y + h; }
    }

    private static final int SW = 20, GAP = 4, CELL_W = 44, CELL_H = 40;

    private Page currentPage = Page.MAIN;
    private final List<Entry> swatches = new ArrayList<>();
    private final List<Rect> swatchRects = new ArrayList<>();
    private int scrollOffset = 0;
    private int lastScrollOffset = -1;
    private int lastWindowWidth = -1;
    private int lastWindowHeight = -1;

    private EditBox inputBox;
    private String statusMsg = "";

    public ColorPaletteScreen() {
        super(Component.literal("彩色方块扩展"));
        rebuildSwatches();
    }

    private void rebuildSwatches() {
        swatches.clear();
        for (ColorDefinition c : ColorRegistry.getAllColors()) {
            swatches.add(new Entry(c.getCode(), c.getColorValue()));
        }
    }

    @Override
    protected void init() {
        this.clearWidgets();
        if (currentPage != Page.INPUT) inputBox = null;
        lastScrollOffset = -1;
        lastWindowWidth = -1;
        lastWindowHeight = -1;

        switch (currentPage) {
            case MAIN -> initMainPage();
            case SWATCHES -> initSwatchesPage();
            case INPUT -> initInputPage();
        }
    }

    private void initMainPage() {
        // 大面板布局参数
        int panelX = 40;
        int panelY = 70;
        int panelW = width - 80;
        int panelH = height - 130;

        // 左侧按钮区：占面板左侧45%
        int leftW = (int) (panelW * 0.45);
        int btnW = Math.min(220, leftW - 50);
        int btnH = 30;
        int btnX = panelX + (leftW - btnW) / 2;

        // 按钮垂直居中，间距60
        int btnCenterY = panelY + panelH / 2;
        int btn1Y = btnCenterY - 45;
        int btn2Y = btnCenterY + 15;

        addRenderableWidget(Button.builder(Component.literal("颜色选取"), btn -> {
            currentPage = Page.SWATCHES;
            scrollOffset = 0;
            init();
        }).bounds(btnX, btn1Y, btnW, btnH).build());

        addRenderableWidget(Button.builder(Component.literal("输入想用的色号"), btn -> {
            currentPage = Page.INPUT;
            init();
        }).bounds(btnX, btn2Y, btnW, btnH).build());
    }

    private void initSwatchesPage() {
        scrollOffset = 0;
        addRenderableWidget(Button.builder(Component.literal("← 返回"), btn -> {
            currentPage = Page.MAIN;
            statusMsg = "";
            init();
        }).bounds(10, 6, 60, 20).build());

        if (isSurvivalMode()) {
            statusMsg = "生存模式下仅可查看颜色，合成请使用方块染色台（消耗七彩粉末）";
        }
    }

    private void initInputPage() {
        addRenderableWidget(Button.builder(Component.literal("← 返回"), btn -> {
            currentPage = Page.MAIN;
            statusMsg = "";
            init();
        }).bounds(10, 6, 60, 20).build());

        int boxW = Math.min(240, width / 3);
        int boxH = 22;
        int boxX = (width - boxW) / 2;
        int boxY = height / 2 - 30;

        if (isSurvivalMode()) {
            statusMsg = "生存模式下输入色号功能已禁用，请使用方块染色台或七彩粉末合成";
        } else {
            inputBox = new EditBox(font, boxX, boxY, boxW, boxH, Component.literal(""));
            inputBox.setMaxLength(64);
            inputBox.setFocused(true);
            addRenderableWidget(inputBox);

            addRenderableWidget(Button.builder(Component.literal("确认放入快捷栏"), btn -> {
                submitCode();
            }).bounds(boxX, boxY + 32, boxW, 22).build());
        }
    }

    private void submitCode() {
        if (inputBox == null) return;
        String input = inputBox.getValue().trim();
        if (input.isEmpty()) {
            statusMsg = "请输入色号";
            return;
        }

        String[] codes = input.split("[\\s,;、/]+");
        int validCount = 0;
        StringBuilder successMsg = new StringBuilder();

        for (String code : codes) {
            String trimmed = code.trim().toUpperCase();
            if (!trimmed.isEmpty()) {
                MardPixelClient.sendHotbar(trimmed);
                if (validCount > 0) successMsg.append(", ");
                successMsg.append(trimmed);
                validCount++;
            }
        }

        if (validCount > 0) {
            if (validCount == 1) {
                statusMsg = "已请求放入快捷栏: " + successMsg;
            } else {
                statusMsg = "已批量请求 " + validCount + " 个色号: " + successMsg;
            }
            inputBox.setValue("");
        } else {
            statusMsg = "未识别到有效色号";
        }
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float partialTick) {
        renderBackground(g);

        switch (currentPage) {
            case MAIN -> renderMainPage(g);
            case SWATCHES -> renderSwatchesPage(g);
            case INPUT -> renderInputPage(g);
        }

        super.render(g, mx, my, partialTick);
    }

    private void renderMainPage(GuiGraphics g) {
        // 大面板布局参数（与initMainPage一致）
        int panelX = 40;
        int panelY = 70;
        int panelW = width - 80;
        int panelH = height - 130;

        // 标题（面板上方居中）
        String title = "彩色方块扩展";
        g.drawString(font, title, (width - font.width(title)) / 2, panelY - 28, 0xFFFFFF);

        // 当前模式（标题下方）
        String modeText = "当前模式：" + getGameModeName();
        int modeColor = isSurvivalMode() ? 0xFF5555 : 0x55FF55;
        g.drawString(font, modeText, (width - font.width(modeText)) / 2, panelY - 14, modeColor);

        // 大面板半透明背景
        g.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xCC1a1a1a);
        // 面板边框
        g.fill(panelX, panelY, panelX + panelW, panelY + 2, 0xFF555555);
        g.fill(panelX, panelY + panelH - 2, panelX + panelW, panelY + panelH, 0xFF333333);
        g.fill(panelX, panelY, panelX + 2, panelY + panelH, 0xFF555555);
        g.fill(panelX + panelW - 2, panelY, panelX + panelW, panelY + panelH, 0xFF333333);

        // 生存模式警告条（面板内顶部）
        if (isSurvivalMode()) {
            String warnText = "生存模式：仅可查看颜色，合成请使用方块染色台";
            int warnWidth = Math.min(font.width(warnText) + 24, panelW - 40);
            int warnX = panelX + (panelW - warnWidth) / 2;
            int warnY = panelY + 10;
            g.fill(warnX, warnY, warnX + warnWidth, warnY + 18, 0x99CC0000);
            g.fill(warnX + 1, warnY + 1, warnX + warnWidth - 1, warnY + 17, 0xFFFF4444);
            g.drawString(font, warnText, warnX + (warnWidth - font.width(warnText)) / 2, warnY + 5, 0xFFFFFF);
        }

        // 右侧说明面板
        int infoPanelX = panelX + (int) (panelW * 0.50);
        int infoPanelW = (int) (panelW * 0.45);
        int infoPanelY = panelY + 10;
        int infoPanelH = panelH - 20;

        // 说明面板背景
        g.fill(infoPanelX, infoPanelY, infoPanelX + infoPanelW, infoPanelY + infoPanelH, 0xEE2a2a2a);
        // 说明面板边框
        g.fill(infoPanelX, infoPanelY, infoPanelX + infoPanelW, infoPanelY + 1, 0xFF666666);
        g.fill(infoPanelX, infoPanelY + infoPanelH - 1, infoPanelX + infoPanelW, infoPanelY + infoPanelH, 0xFF444444);
        g.fill(infoPanelX, infoPanelY, infoPanelX + 1, infoPanelY + infoPanelH, 0xFF666666);
        g.fill(infoPanelX + infoPanelW - 1, infoPanelY, infoPanelX + infoPanelW, infoPanelY + infoPanelH, 0xFF444444);

        g.drawString(font, "mod 使用说明", infoPanelX + 8, infoPanelY + 5, 0xFFFFAA);

        String[] lines = isSurvivalMode() ? new String[]{
            "", "221 色像素画模组（生存模式）", "",
            "按钮一：浏览全部色号", "  生存模式仅查看颜色", "",
            "按钮二：输入色号（仅创造模式）", "  生存模式下此功能禁用", "",
            "合成表：任意染料→七彩粉末→色块", "方块染色台：放入粉末后选择颜色", "",
            "按 G 键打开/关闭本界面"
        } : new String[]{
            "", "221 色像素画模组（创造模式）", "",
            "按钮一：浏览全部色号", "  点击色块获取一组方块", "",
            "按钮二：输入色号快速获取", "  输入色号后放入快捷栏", "",
            "合成表：任意染料→七彩粉末→色块", "方块染色台：放入粉末后选择颜色", "",
            "按 G 键打开/关闭本界面"
        };

        int y = infoPanelY + 18;
        int lineH = 8;
        for (String line : lines) {
            if (y + 8 < infoPanelY + infoPanelH - 2) {
                g.drawString(font, line, infoPanelX + 8, y, 0xCCCCCC);
            }
            y += lineH;
        }

        // 版本号（面板下方居中）
        String bottomText = "彩色方块扩展 v1.3.0";
        g.drawString(font, bottomText, (width - font.width(bottomText)) / 2, panelY + panelH + 10, 0x888888);

        if (!statusMsg.isEmpty()) {
            g.drawString(font, statusMsg, 10, height - 14, 0xFFFFAA);
        }
    }

    private void renderSwatchesPage(GuiGraphics g) {
        boolean isSurvivalRender = isSurvivalMode();

        if (isSurvivalRender) {
            String warnText = "生存模式：仅可查看颜色，点击不会获取方块，请使用方块染色台合成";
            int warnWidth = Math.min(width - 20, font.width(warnText) + 24);
            int warnX = 6, warnY = 4, warnH = 26;
            g.fill(warnX, warnY, warnX + warnWidth, warnY + warnH, 0xFF8B0000);
            g.fill(warnX + 1, warnY + 1, warnX + warnWidth - 1, warnY + warnH - 1, 0xFFB22222);
            g.drawString(font, warnText, warnX + 10, warnY + (warnH - 8) / 2 + 1, 0xFFFFE4B5);
        } else {
            String title = "颜色选取 - 点击色块获取一组（64个）";
            g.drawString(font, title, 10, 12, 0xFFFFFF);
        }

        int contentY = 36;
        int contentH = height - contentY - 20;
        int cellW = CELL_W;
        int cellH = CELL_H;
        int cols = Math.max(6, Math.min(20, (width - 30) / cellW));
        int visibleRows = Math.max(1, contentH / cellH);
        int totalRows = (int) Math.ceil((double) swatches.size() / cols);
        int maxScroll = Math.max(0, totalRows - visibleRows);
        if (scrollOffset > maxScroll) scrollOffset = maxScroll;

        if (lastScrollOffset != scrollOffset || lastWindowWidth != width || lastWindowHeight != height) {
            swatchRects.clear();
            int startIdx = scrollOffset * cols;
            for (int i = startIdx; i < swatches.size(); i++) {
                int col = (i - startIdx) % cols;
                int row = (i - startIdx) / cols;
                if (row >= visibleRows) break;
                int x = 10 + col * cellW;
                int y = contentY + row * cellH;
                swatchRects.add(new Rect(x, y, SW, SW + 10));
            }
            lastScrollOffset = scrollOffset;
            lastWindowWidth = width;
            lastWindowHeight = height;
        }

        int startIdx = scrollOffset * cols;
        for (int i = 0; i < swatchRects.size() && startIdx + i < swatches.size(); i++) {
            Rect r = swatchRects.get(i);
            Entry e = swatches.get(startIdx + i);
            drawSwatch(g, r.x, r.y, e.rgb(), e.code());
        }

        if (totalRows > visibleRows) {
            int barX = width - 8;
            int barY = contentY;
            int barH = contentH;
            int thumbH = Math.max(20, barH * visibleRows / totalRows);
            int thumbY = barY + (barH - thumbH) * scrollOffset / Math.max(1, maxScroll);
            g.fill(barX, barY, barX + 4, barY + barH, 0xFF333333);
            g.fill(barX, thumbY, barX + 4, thumbY + thumbH, 0xFF888888);
        }

        if (!statusMsg.isEmpty()) {
            g.drawString(font, statusMsg, 10, height - 14, 0xFFFFAA);
        }
    }

    private void renderInputPage(GuiGraphics g) {
        if (isSurvivalMode()) {
            String title = "输入色号功能已禁用";
            g.drawString(font, title, (width - font.width(title)) / 2, height / 2 - 50, 0xFF5555);

            String warn1 = "生存模式下无法通过输入色号直接获取方块";
            String warn2 = "请使用方块染色台，通过七彩粉末合成对应颜色";
            int boxWidth = Math.max(font.width(warn1), font.width(warn2)) + 40;
            int boxX = (width - boxWidth) / 2;
            int boxY = height / 2 - 20;
            g.fill(boxX, boxY, boxX + boxWidth, boxY + 70, 0x88FF3333);
            g.fill(boxX + 2, boxY + 2, boxX + boxWidth - 2, boxY + 68, 0xFFFF5555);
            g.drawString(font, warn1, (width - font.width(warn1)) / 2, boxY + 15, 0xFFFFFF);
            g.drawString(font, warn2, (width - font.width(warn2)) / 2, boxY + 35, 0xFFFFEE);

            if (!statusMsg.isEmpty()) {
                g.drawString(font, statusMsg, (width - font.width(statusMsg)) / 2, height / 2 + 70, 0xFFFFAA);
            }
        } else {
            String title = "输入想用的色号（支持批量输入）";
            g.drawString(font, title, (width - font.width(title)) / 2, height / 2 - 70, 0xFFFFFF);

            String hint = "输入色号（如 A1、B5、M3），支持批量输入多个色号";
            g.drawString(font, hint, (width - font.width(hint)) / 2, height / 2 + 45, 0xAAAAAA);

            String hint2 = "用空格/逗号/分号分隔，例如：A1 B2 C3 或 A1,B2,C3";
            g.drawString(font, hint2, (width - font.width(hint2)) / 2, height / 2 + 60, 0x888888);

            if (!statusMsg.isEmpty()) {
                g.drawString(font, statusMsg, (width - font.width(statusMsg)) / 2, height / 2 + 85, 0xFFFFAA);
            }
        }
    }

    private void drawSwatch(GuiGraphics g, int x, int y, int rgb, String label) {
        g.fill(x - 1, y - 1, x + SW + 1, y + SW + 1, 0xFF333333);
        g.fill(x, y, x + SW, y + SW, 0xFF000000 | rgb);
        int textX = x + (SW - font.width(label)) / 2;
        if (textX < x) textX = x;
        g.drawString(font, label, textX, y + SW + 3, 0xCCCCCC, false);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0 && currentPage == Page.SWATCHES) {
            boolean isSurvivalClick = isSurvivalMode();
            int cols = Math.max(6, Math.min(20, (width - 30) / CELL_W));
            int startIdx = scrollOffset * cols;
            for (int i = 0; i < swatchRects.size() && startIdx + i < swatches.size(); i++) {
                Rect r = swatchRects.get(i);
                if (r.hit(mx, my)) {
                    Entry e = swatches.get(startIdx + i);
                    if (isSurvivalClick) {
                        statusMsg = e.code() + " - 生存模式下请使用方块染色台合成（消耗七彩粉末）";
                    } else {
                        MardPixelClient.sendRequestItem(e.code());
                        statusMsg = "已给予一组 " + e.code();
                    }
                    return true;
                }
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double delta) {
        if (currentPage == Page.SWATCHES) {
            scrollOffset -= (int) Math.signum(delta);
            if (scrollOffset < 0) scrollOffset = 0;
            return true;
        }
        return super.mouseScrolled(mx, my, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.onClose();
            return true;
        }

        if (inputBox != null && inputBox.isFocused() && currentPage == Page.INPUT) {
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                submitCode();
                return true;
            }
            return inputBox.keyPressed(keyCode, scanCode, modifiers);
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private boolean isSurvivalMode() {
        if (Minecraft.getInstance().player == null) return false;
        var player = Minecraft.getInstance().player;
        return !player.isCreative() && !player.isSpectator();
    }

    private String getGameModeName() {
        if (isSurvivalMode()) return "生存模式";
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.isCreative()) return "创造模式";
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.isSpectator()) return "旁观模式";
        return "未知模式";
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
