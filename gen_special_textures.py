from PIL import Image
import os

base = r'D:\文档\GitHub\游戏mod开发\fabric\src\main\resources\assets\mard_pixel\textures'

# ==================== 七彩粉末 (彩虹色粉末) ====================
img = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
pixels = img.load()
# 彩虹色粉末图案（类似红石粉但彩色）
rainbow_colors = [
    (255, 0, 0, 255),    # 红
    (255, 128, 0, 255),  # 橙
    (255, 255, 0, 255),  # 黄
    (0, 255, 0, 255),    # 绿
    (0, 200, 255, 255),  # 青
    (0, 0, 255, 255),    # 蓝
    (128, 0, 255, 255),  # 紫
    (255, 0, 255, 255),  # 品红
]
# 粉末散布图案
powder_pattern = [
    (3,3),(5,2),(7,3),(9,2),(11,3),(13,2),
    (2,5),(4,6),(6,5),(8,6),(10,5),(12,6),(14,5),
    (3,8),(5,9),(7,8),(9,9),(11,8),(13,9),
    (2,11),(4,12),(6,11),(8,12),(10,11),(12,12),(14,11),
    (3,14),(5,13),(7,14),(9,13),(11,14),(13,13),
]
for idx, (x, y) in enumerate(powder_pattern):
    if 0 <= x < 16 and 0 <= y < 16:
        color = rainbow_colors[idx % len(rainbow_colors)]
        pixels[x, y] = color
        # 周围加一点光晕
        for dx, dy in [(-1,0),(1,0),(0,-1),(0,1)]:
            nx, ny = x+dx, y+dy
            if 0 <= nx < 16 and 0 <= ny < 16 and pixels[nx, ny][3] == 0:
                c = rainbow_colors[idx % len(rainbow_colors)]
                pixels[nx, ny] = (c[0], c[1], c[2], 100)

path = os.path.join(base, 'item', 'mard_pigment.png')
img.save(path, 'PNG')
print(f'七彩粉末: {path} ({os.path.getsize(path)} bytes)')

# ==================== 染色台顶部 (调色板图案) ====================
img = Image.new('RGBA', (16, 16), (0, 0, 0, 255))
pixels = img.load()
# 橡木木板底色
for y in range(16):
    for x in range(16):
        pixels[x, y] = (150, 100, 50, 255)
# 木板纹理线
for y in range(0, 16, 4):
    for x in range(16):
        pixels[x, y] = (120, 80, 40, 255)
# 4个彩色颜料槽
slot_colors = [(200, 30, 30), (30, 150, 30), (30, 60, 200), (220, 200, 30)]
slot_positions = [(3, 3), (9, 3), (3, 9), (9, 9)]
for (sx, sy), color in zip(slot_positions, slot_colors):
    for dy in range(4):
        for dx in range(4):
            x, y = sx+dx, sy+dy
            if 0 <= x < 16 and 0 <= y < 16:
                # 槽边框
                if dx == 0 or dx == 3 or dy == 0 or dy == 3:
                    pixels[x, y] = (60, 40, 20, 255)
                else:
                    pixels[x, y] = (color[0], color[1], color[2], 255)

path = os.path.join(base, 'block', 'mard_crafting_table_top.png')
img.save(path, 'PNG')
print(f'染色台顶部: {path} ({os.path.getsize(path)} bytes)')

# ==================== 染色台侧面 (工作台侧面+抽屉) ====================
img = Image.new('RGBA', (16, 16), (0, 0, 0, 255))
pixels = img.load()
# 橡木木板底色
for y in range(16):
    for x in range(16):
        pixels[x, y] = (150, 100, 50, 255)
# 木板纹理
for y in range(0, 16, 4):
    for x in range(16):
        pixels[x, y] = (120, 80, 40, 255)
# 中间抽屉/面板
for y in range(5, 11):
    for x in range(3, 13):
        pixels[x, y] = (100, 70, 35, 255)
# 抽屉边框
for x in range(3, 13):
    pixels[x, 5] = (70, 50, 25, 255)
    pixels[x, 10] = (70, 50, 25, 255)
for y in range(5, 11):
    pixels[3, y] = (70, 50, 25, 255)
    pixels[12, y] = (70, 50, 25, 255)
# 把手
for x in range(6, 10):
    pixels[x, 7] = (50, 50, 50, 255)
    pixels[x, 8] = (80, 80, 80, 255)

path = os.path.join(base, 'block', 'mard_crafting_table_side.png')
img.save(path, 'PNG')
print(f'染色台侧面: {path} ({os.path.getsize(path)} bytes)')

# ==================== 染色台底部 (橡木木板) ====================
img = Image.new('RGBA', (16, 16), (0, 0, 0, 255))
pixels = img.load()
for y in range(16):
    for x in range(16):
        pixels[x, y] = (150, 100, 50, 255)
for y in range(0, 16, 4):
    for x in range(16):
        pixels[x, y] = (120, 80, 40, 255)
# 木板接缝
for x in range(0, 16, 8):
    for y in range(16):
        pixels[x, y] = (110, 75, 38, 255)

path = os.path.join(base, 'block', 'mard_crafting_table_bottom.png')
img.save(path, 'PNG')
print(f'染色台底部: {path} ({os.path.getsize(path)} bytes)')

print('\n全部材质重新生成完成!')
