file = r"D:\文档\GitHub\游戏mod开发\fabric\src\main\java\com\mard\pixel\fabric\ColorPaletteScreen.java"
with open(file, 'r', encoding='utf-8') as f:
    content = f.read()

# 完全重写 initMainPage 和 renderMainPage
old_init = '''    private void initMainPage() {
        // 大面板布局：左右分栏，左侧按钮，右侧说明
        int panelX = 50;
        int panelY = 80;
        int panelW = width - 100;
        int panelH = height - 160;

        // 左侧按钮区
        int leftAreaW = (int) (panelW * 0.45);
        int btnW = Math.min(240, leftAreaW - 60);
        int btnH = 32;
        int btnX = panelX + (leftAreaW - btnW) / 2;
        int btnCenterY = panelY + panelH / 2;
        int gapY = 70;

        addRenderableWidget(Button.builder(Component.literal("颜色选取"), btn -> {
            currentPage = Page.SWATCHES;
            scrollOffset = 0;
            init();
        }).bounds(btnX, btnCenterY - gapY / 2 - btnH / 2, btnW, btnH).build());

        addRenderableWidget(Button.builder(Component.literal("输入想用的色号"), btn -> {
            currentPage = Page.INPUT;
            init();
        }).bounds(btnX, btnCenterY + gapY / 2 - btnH / 2, btnW, btnH).build());
    }'''

new_init = '''    private void initMainPage() {
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
    }'''

content = content.replace(old_init, new_init)

# 完全重写 renderMainPage
old_render = '''    private void renderMainPage(GuiGraphics g) {
        // 大面板布局参数（与initMainPage一致）
        int panelX = 50;
        int panelY = 80;
        int panelW = width - 100;
        int panelH = height - 160;

        // 标题（面板上方）
        String title = "彩色方块扩展";
        g.drawString(font, title, (width - font.width(title)) / 2, panelY - 35, 0xFFFFFF);

        // 当前模式（标题下方）
        String modeText = "当前模式：" + getGameModeName();
        int modeColor = isSurvivalMode() ? 0xFF5555 : 0x55FF55;
        g.drawString(font, modeText, (width - font.width(modeText)) / 2, panelY - 18, modeColor);

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
            g.fill(warnX, warnY, warnX + warnWidth, warnY + 20, 0x99CC0000);
            g.fill(warnX + 1, warnY + 1, warnX + warnWidth - 1, warnY + 19, 0xFFFF4444);
            g.drawString(font, warnText, warnX + (warnWidth - font.width(warnText)) / 2, warnY + 6, 0xFFFFFF);
        }

        // 右侧说明面板
        int infoPanelX = panelX + (int) (panelW * 0.50);
        int infoPanelW = (int) (panelW * 0.45);
        int infoPanelY = panelY + 20;
        int infoPanelH = panelH - 40;

        // 说明面板背景
        g.fill(infoPanelX, infoPanelY, infoPanelX + infoPanelW, infoPanelY + infoPanelH, 0xEE2a2a2a);
        // 说明面板边框
        g.fill(infoPanelX, infoPanelY, infoPanelX + infoPanelW, infoPanelY + 1, 0xFF666666);
        g.fill(infoPanelX, infoPanelY + infoPanelH - 1, infoPanelX + infoPanelW, infoPanelY + infoPanelH, 0xFF444444);
        g.fill(infoPanelX, infoPanelY, infoPanelX + 1, infoPanelY + infoPanelH, 0xFF666666);
        g.fill(infoPanelX + infoPanelW - 1, infoPanelY, infoPanelX + infoPanelW, infoPanelY + infoPanelH, 0xFF444444);

        g.drawString(font, "mod 使用说明", infoPanelX + 12, infoPanelY + 10, 0xFFFFAA);

        String[] lines = isSurvivalMode() ? new String[]{
            "", "221 色像素画模组（生存模式）", "",
            "按钮一：浏览全部色号", "  生存模式：仅查看颜色", "  合成请使用方块染色台", "",
            "按钮二：输入色号（仅创造模式）", "  生存模式下此功能禁用", "",
            "合成表：任意染料→七彩粉末→色块", "方块染色台：放入粉末后选择颜色", "",
            "按 G 键打开/关闭本界面"
        } : new String[]{
            "", "221 色像素画模组（创造模式）", "",
            "按钮一：浏览全部色号", "  点击色块直接获取一组方块", "  支持连续选择", "",
            "按钮二：输入色号快速获取", "  输入色号后放入快捷栏", "  支持批量输入（空格/逗号分隔）", "",
            "合成表：任意染料→七彩粉末→色块", "方块染色台：放入粉末后选择颜色", "",
            "按 G 键打开/关闭本界面"
        };

        int y = infoPanelY + 28;
        int lineH = 12;
        for (String line : lines) {
            if (y + 10 < infoPanelY + infoPanelH - 5) {
                g.drawString(font, line, infoPanelX + 12, y, 0xCCCCCC);
            }
            y += lineH;
        }

        // 版本号（面板下方）
        String bottomText = "彩色方块扩展 v1.3.0";
        g.drawString(font, bottomText, (width - font.width(bottomText)) / 2, panelY + panelH + 15, 0x888888);

        if (!statusMsg.isEmpty()) {
            g.drawString(font, statusMsg, 10, height - 14, 0xFFFFAA);
        }
    }'''

new_render = '''    private void renderMainPage(GuiGraphics g) {
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
        int infoPanelY = panelY + 15;
        int infoPanelH = panelH - 30;

        // 说明面板背景
        g.fill(infoPanelX, infoPanelY, infoPanelX + infoPanelW, infoPanelY + infoPanelH, 0xEE2a2a2a);
        // 说明面板边框
        g.fill(infoPanelX, infoPanelY, infoPanelX + infoPanelW, infoPanelY + 1, 0xFF666666);
        g.fill(infoPanelX, infoPanelY + infoPanelH - 1, infoPanelX + infoPanelW, infoPanelY + infoPanelH, 0xFF444444);
        g.fill(infoPanelX, infoPanelY, infoPanelX + 1, infoPanelY + infoPanelH, 0xFF666666);
        g.fill(infoPanelX + infoPanelW - 1, infoPanelY, infoPanelX + infoPanelW, infoPanelY + infoPanelH, 0xFF444444);

        g.drawString(font, "mod 使用说明", infoPanelX + 10, infoPanelY + 8, 0xFFFFAA);

        String[] lines = isSurvivalMode() ? new String[]{
            "", "221 色像素画模组（生存模式）", "",
            "按钮一：浏览全部色号", "  生存模式：仅查看颜色", "  合成请使用方块染色台", "",
            "按钮二：输入色号（仅创造模式）", "  生存模式下此功能禁用", "",
            "合成表：任意染料→七彩粉末→色块", "方块染色台：放入粉末后选择颜色", "",
            "按 G 键打开/关闭本界面"
        } : new String[]{
            "", "221 色像素画模组（创造模式）", "",
            "按钮一：浏览全部色号", "  点击色块直接获取一组方块", "  支持连续选择", "",
            "按钮二：输入色号快速获取", "  输入色号后放入快捷栏", "  支持批量输入（空格/逗号分隔）", "",
            "合成表：任意染料→七彩粉末→色块", "方块染色台：放入粉末后选择颜色", "",
            "按 G 键打开/关闭本界面"
        };

        int y = infoPanelY + 24;
        int lineH = 11;
        for (String line : lines) {
            if (y + 10 < infoPanelY + infoPanelH - 3) {
                g.drawString(font, line, infoPanelX + 10, y, 0xCCCCCC);
            }
            y += lineH;
        }

        // 版本号（面板下方居中）
        String bottomText = "彩色方块扩展 v1.3.0";
        g.drawString(font, bottomText, (width - font.width(bottomText)) / 2, panelY + panelH + 10, 0x888888);

        if (!statusMsg.isEmpty()) {
            g.drawString(font, statusMsg, 10, height - 14, 0xFFFFAA);
        }
    }'''

content = content.replace(old_render, new_render)

with open(file, 'w', encoding='utf-8') as f:
    f.write(content)

print("Fabric版本G键UI已完全重写，修复按钮位置问题")
print("  - panelX=40, panelY=70, panelW=width-80, panelH=height-130")
print("  - 按钮1 Y=btnCenterY-45, 按钮2 Y=btnCenterY+15")
print("  - 确保按钮在面板内")
