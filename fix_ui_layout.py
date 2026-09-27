file = r"D:\文档\GitHub\游戏mod开发\fabric\src\main\java\com\mard\pixel\fabric\ColorPaletteScreen.java"
with open(file, 'r', encoding='utf-8') as f:
    content = f.read()

# ========== 1. 修改 initMainPage 按钮位置 ==========
old_init = '''    private void initMainPage() {
        int btnAreaW = (int) (width * 0.30);
        int btnW = Math.min(180, btnAreaW - 20);
        int btnH = 30;
        int btnX = Math.max(25, (btnAreaW - btnW) / 2 + 8);
        int centerY = Math.max(height / 2 + 20, 160);
        int gapY = 50;

        addRenderableWidget(Button.builder(Component.literal("颜色选取"), btn -> {
            currentPage = Page.SWATCHES;
            scrollOffset = 0;
            init();
        }).bounds(btnX, centerY - gapY / 2 - btnH / 2, btnW, btnH).build());

        addRenderableWidget(Button.builder(Component.literal("输入想用的色号"), btn -> {
            currentPage = Page.INPUT;
            init();
        }).bounds(btnX, centerY + gapY / 2 - btnH / 2, btnW, btnH).build());
    }'''

new_init = '''    private void initMainPage() {
        // 居中卡片式布局
        int panelW = Math.min(520, width - 40);
        int panelH = 200;
        int panelX = (width - panelW) / 2;
        int panelY = (height - panelH) / 2 + 10;

        int btnW = 160;
        int btnH = 28;
        int btnX = panelX + 25;
        int btnCenterY = panelY + panelH / 2;
        int gapY = 55;

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

content = content.replace(old_init, new_init)

# ========== 2. 修改 renderMainPage 说明面板位置 ==========
old_render_start = '''        int infoAreaX = (int) (width * 0.55);
        int infoAreaW = (int) (width * 0.35);
        int infoY = Math.max(110, height / 2 - 90);
        int infoH = Math.min(220, height - 180);

        g.fill(infoAreaX - 6, infoY - 6, infoAreaX + infoAreaW + 6, infoY + infoH + 6, 0xFF1a1a1a);
        g.fill(infoAreaX - 5, infoY - 5, infoAreaX + infoAreaW + 5, infoY + infoH + 5, 0xFF2a2a2a);
        g.drawString(font, "mod 使用说明", infoAreaX, infoY, 0xFFFFAA);'''

new_render_start = '''        // 居中卡片式布局（与initMainPage一致）
        int panelW = Math.min(520, width - 40);
        int panelH = 200;
        int panelX = (width - panelW) / 2;
        int panelY = (height - panelH) / 2 + 10;

        // 面板背景
        g.fill(panelX - 4, panelY - 4, panelX + panelW + 4, panelY + panelH + 4, 0xFF1a1a1a);
        g.fill(panelX - 3, panelY - 3, panelX + panelW + 3, panelY + panelH + 3, 0xFF2a2a2a);
        g.fill(panelX - 2, panelY - 2, panelX + panelW + 2, panelY + panelH + 2, 0xFF3a3a3a);

        // 分隔线（左侧按钮区和右侧说明区之间）
        int dividerX = panelX + 200;
        g.fill(dividerX, panelY + 15, dividerX + 1, panelY + panelH - 15, 0xFF555555);

        int infoAreaX = panelX + 215;
        int infoAreaW = panelW - 230;
        int infoY = panelY + 15;
        int infoH = panelH - 25;

        g.drawString(font, "mod 使用说明", infoAreaX, infoY, 0xFFFFAA);'''

content = content.replace(old_render_start, new_render_start)

# ========== 3. 修改行高计算 ==========
old_lineh = '''        int y = infoY + 15;
        int lineH = Math.max(10, (infoH - 20) / lines.length);'''
new_lineh = '''        int y = infoY + 15;
        int lineH = 11;'''
content = content.replace(old_lineh, new_lineh)

with open(file, 'w', encoding='utf-8') as f:
    f.write(content)

print("主菜单UI布局已优化为居中卡片式")
print("  - 面板居中，宽度520，高度200")
print("  - 左侧按钮区（200px），右侧说明区")
print("  - 中间分隔线")
print("  - 更紧凑美观")
