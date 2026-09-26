import re

file = r"D:\文档\GitHub\游戏mod开发\fabric\src\main\java\com\mard\pixel\fabric\ColorPaletteScreen.java"
with open(file, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. 增加单元格宽度常量
content = content.replace(
    'private static final int SW = 20, GAP = 3;',
    'private static final int SW = 20, GAP = 4, CELL_W = 44, CELL_H = 40;'
)

# 2. 在initSwatchesPage中重置scrollOffset
content = content.replace(
    'private void initSwatchesPage() {\n        addRenderableWidget(Button.builder(Component.literal("\u2190 \u8fd4\u56de")',
    'private void initSwatchesPage() {\n        scrollOffset = 0;\n        addRenderableWidget(Button.builder(Component.literal("\u2190 \u8fd4\u56de")'
)

# 3. 修复renderSwatchesPage中的cellW/cellH/cols
old = '''        int cellW = SW + GAP + 8;
        int cellH = SW + GAP + 10;
        int cols = Math.max(8, Math.min(24, (width - 20) / cellW));'''
new = '''        int cellW = CELL_W;
        int cellH = CELL_H;
        int cols = Math.max(6, Math.min(20, (width - 30) / cellW));'''
content = content.replace(old, new)

# 4. 修复警告条
old = '''            int warnWidth = font.width(warnText) + 20;
            g.fill(5, 5, Math.min(width - 10, warnWidth), 28, 0x88FF3333);
            g.fill(6, 6, Math.min(width - 11, warnWidth - 1), 27, 0xFFFF5555);
            g.drawString(font, warnText, 15, 12, 0xFFFFFF);'''
new = '''            int warnWidth = Math.min(width - 20, font.width(warnText) + 30);
            g.fill(5, 4, 5 + warnWidth, 32, 0xCCFF3333);
            g.fill(6, 5, 5 + warnWidth - 1, 31, 0xFFFF5555);
            g.drawString(font, warnText, 15, 13, 0xFFFFFF);'''
content = content.replace(old, new)

# 5. 修复颜色选取标题位置
old = '''            String title = "颜色选取 - 点击色块获取一组（64个）";
            g.drawString(font, title, 80, 12, 0xFFFFFF);'''
new = '''            String title = "颜色选取 - 点击色块获取一组（64个）";
            g.drawString(font, title, 10, 38, 0xFFFFFF);'''
content = content.replace(old, new)

# 6. 修复contentY
content = content.replace('int contentY = 40;', 'int contentY = 44;')

# 7. 修复mouseClicked中的cols
content = content.replace(
    'int cols = Math.max(8, Math.min(24, (width - 31) / 31));',
    'int cols = Math.max(6, Math.min(20, (width - 30) / CELL_W));'
)

# 8. 修复drawSwatch中的色号文字居中
old = '''    private void drawSwatch(GuiGraphics g, int x, int y, int rgb, String label) {
        g.fill(x - 1, y - 1, x + SW + 1, y + SW + 1, 0xFF333333);
        g.fill(x, y, x + SW, y + SW, 0xFF000000 | rgb);
        g.drawString(font, label, x, y + SW + 2, 0x999999, false);
    }'''
new = '''    private void drawSwatch(GuiGraphics g, int x, int y, int rgb, String label) {
        g.fill(x - 1, y - 1, x + SW + 1, y + SW + 1, 0xFF333333);
        g.fill(x, y, x + SW, y + SW, 0xFF000000 | rgb);
        int textX = x + (SW - font.width(label)) / 2;
        if (textX < x) textX = x;
        g.drawString(font, label, textX, y + SW + 3, 0xCCCCCC, false);
    }'''
content = content.replace(old, new)

with open(file, 'w', encoding='utf-8') as f:
    f.write(content)

print("ColorPaletteScreen 修复完成!")

# 验证
with open(file, 'r', encoding='utf-8') as f:
    c = f.read()
checks = ['CELL_W = 44', 'scrollOffset = 0;', 'warnWidth = Math.min', 'textX = x +', 'int contentY = 44']
for check in checks:
    print(f"  {'OK' if check in c else 'MISSING'}: {check}")
