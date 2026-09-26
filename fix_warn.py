import re

file = r"D:\文档\GitHub\游戏mod开发\fabric\src\main\java\com\mard\pixel\fabric\ColorPaletteScreen.java"
with open(file, 'r', encoding='utf-8') as f:
    content = f.read()

# 修复警告条：简化背景，增加高度，调整文字位置
old = '''        if (isSurvivalRender) {
            String warnText = "生存模式：仅可查看颜色，点击不会获取方块，请使用方块染色台合成";
            int warnWidth = Math.min(width - 20, font.width(warnText) + 30);
            g.fill(5, 4, 5 + warnWidth, 32, 0xCCFF3333);
            g.fill(6, 5, 5 + warnWidth - 1, 31, 0xFFFF5555);
            g.drawString(font, warnText, 15, 13, 0xFFFFFF);
        } else {'''
new = '''        if (isSurvivalRender) {
            String warnText = "生存模式：仅可查看颜色，点击不会获取方块，请使用方块染色台合成";
            int warnWidth = Math.min(width - 20, font.width(warnText) + 24);
            int warnX = 6, warnY = 4, warnH = 26;
            g.fill(warnX, warnY, warnX + warnWidth, warnY + warnH, 0xFF8B0000);
            g.fill(warnX + 1, warnY + 1, warnX + warnWidth - 1, warnY + warnH - 1, 0xFFB22222);
            g.drawString(font, warnText, warnX + 10, warnY + (warnH - 8) / 2 + 1, 0xFFFFE4B5);
        } else {'''
content = content.replace(old, new)

# 修复contentY（警告条高度变了）
content = content.replace('int contentY = 44;', 'int contentY = 36;')

# 修复标题位置（创造模式）
content = content.replace(
    'g.drawString(font, title, 10, 38, 0xFFFFFF);',
    'g.drawString(font, title, 10, 12, 0xFFFFFF);'
)

with open(file, 'w', encoding='utf-8') as f:
    f.write(content)

print("警告条修复完成!")
