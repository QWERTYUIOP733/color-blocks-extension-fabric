from PIL import Image
import os

assets_dir = r"D:\文档\GitHub\游戏mod开发\fabric\src\main\resources\assets\mard_pixel"
textures_dir = os.path.join(assets_dir, "textures")
block_dir = os.path.join(textures_dir, "block")
item_dir = os.path.join(textures_dir, "item")
gui_dir = os.path.join(textures_dir, "gui")

os.makedirs(block_dir, exist_ok=True)
os.makedirs(item_dir, exist_ok=True)
os.makedirs(gui_dir, exist_ok=True)

# 1. 颜色方块模板（白色，用于tintindex染色）
color_block = Image.new("RGBA", (16, 16), (255, 255, 255, 255))
# 添加简单的边框效果
for x in range(16):
    for y in range(16):
        if x == 0 or x == 15 or y == 0 or y == 15:
            color_block.putpixel((x, y), (200, 200, 200, 255))
color_block.save(os.path.join(block_dir, "color_block_template.png"))

# 2. 方块染色台材质（6面）
# 顶面
top = Image.new("RGBA", (16, 16), (139, 90, 43, 255))  # 棕色
for x in range(16):
    for y in range(16):
        if x < 8 and y < 8:
            top.putpixel((x, y), (160, 100, 50, 255))
        elif x >= 8 and y >= 8:
            top.putpixel((x, y), (120, 80, 40, 255))
top.save(os.path.join(block_dir, "mard_crafting_table_top.png"))

# 底面（橡木木板颜色）
bottom = Image.new("RGBA", (16, 16), (160, 120, 70, 255))
for x in range(16):
    for y in range(16):
        if y % 4 == 0:
            bottom.putpixel((x, y), (140, 100, 60, 255))
bottom.save(os.path.join(block_dir, "mard_crafting_table_bottom.png"))

# 侧面
side = Image.new("RGBA", (16, 16), (150, 100, 50, 255))
# 添加调色板图案
for x in range(2, 14):
    for y in range(2, 6):
        colors = [(255, 0, 0), (255, 165, 0), (255, 255, 0), (0, 128, 0), (0, 0, 255), (75, 0, 130), (238, 130, 238)]
        idx = (x - 2) % 7
        side.putpixel((x, y), colors[idx])
side.save(os.path.join(block_dir, "mard_crafting_table_side.png"))

# 3. 七彩粉末材质
powder = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
# 彩虹色粉末效果
rainbow_colors = [
    (255, 0, 0), (255, 127, 0), (255, 255, 0),
    (0, 255, 0), (0, 0, 255), (75, 0, 130), (148, 0, 211)
]
import random
random.seed(42)
for _ in range(80):
    x = random.randint(2, 13)
    y = random.randint(2, 13)
    color = random.choice(rainbow_colors)
    powder.putpixel((x, y), color)
    if x + 1 < 16:
        powder.putpixel((x + 1, y), color)
    if y + 1 < 16:
        powder.putpixel((x, y + 1), color)
powder.save(os.path.join(item_dir, "rainbow_powder.png"))

# 4. GUI背景
gui_bg = Image.new("RGBA", (256, 256), (0, 0, 0, 0))
# 简单的GUI背景
for x in range(256):
    for y in range(256):
        if x < 176 and y < 166:
            gui_bg.putpixel((x, y), (198, 198, 198, 255))
gui_bg.save(os.path.join(gui_dir, "mard_crafting_table.png"))

print("All textures generated successfully!")
print(f"- color_block_template.png")
print(f"- mard_crafting_table_top/bottom/side.png")
print(f"- rainbow_powder.png")
print(f"- mard_crafting_table GUI background")
