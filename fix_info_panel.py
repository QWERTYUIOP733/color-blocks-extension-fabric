# 修复Fabric版本说明面板显示问题
file = r"D:\文档\GitHub\游戏mod开发\fabric\src\main\java\com\mard\pixel\fabric\ColorPaletteScreen.java"
with open(file, 'r', encoding='utf-8') as f:
    content = f.read()

# 修改说明面板参数，增加高度，减小行高
old_info = '''        // 右侧说明面板
        int infoPanelX = panelX + (int) (panelW * 0.50);
        int infoPanelW = (int) (panelW * 0.45);
        int infoPanelY = panelY + 15;
        int infoPanelH = panelH - 30;'''

new_info = '''        // 右侧说明面板
        int infoPanelX = panelX + (int) (panelW * 0.50);
        int infoPanelW = (int) (panelW * 0.45);
        int infoPanelY = panelY + 10;
        int infoPanelH = panelH - 20;'''

content = content.replace(old_info, new_info)

# 修改说明文字渲染，减小行高和起始位置
old_text = '''        g.drawString(font, "mod 使用说明", infoPanelX + 10, infoPanelY + 8, 0xFFFFAA);'''
new_text = '''        g.drawString(font, "mod 使用说明", infoPanelX + 8, infoPanelY + 6, 0xFFFFAA);'''
content = content.replace(old_text, new_text)

old_lines = '''        int y = infoPanelY + 24;
        int lineH = 11;
        for (String line : lines) {
            if (y + 10 < infoPanelY + infoPanelH - 3) {
                g.drawString(font, line, infoPanelX + 10, y, 0xCCCCCC);
            }
            y += lineH;
        }'''
new_lines = '''        int y = infoPanelY + 20;
        int lineH = 9;
        for (String line : lines) {
            if (y + 9 < infoPanelY + infoPanelH - 2) {
                g.drawString(font, line, infoPanelX + 8, y, 0xCCCCCC);
            }
            y += lineH;
        }'''
content = content.replace(old_lines, new_lines)

with open(file, 'w', encoding='utf-8') as f:
    f.write(content)

print("Fabric版本说明面板已修复")
print("  - 说明面板高度增加（panelH-20）")
print("  - 行高减小（11→9）")
print("  - 起始位置上移（24→20）")
print("  - 确保按钮二等全部内容可见")
