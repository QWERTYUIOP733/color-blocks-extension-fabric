import json
import os

# 读取颜色配置
with open(r"D:\文档\GitHub\游戏mod开发\colors\mard_295.json", "r", encoding="utf-8-sig") as f:
    data = json.load(f)

assets_dir = r"D:\文档\GitHub\游戏mod开发\fabric\src\main\resources\assets\mard_pixel"

# 确保目录存在
os.makedirs(os.path.join(assets_dir, "blockstates"), exist_ok=True)
os.makedirs(os.path.join(assets_dir, "models", "item"), exist_ok=True)

blockstate_template = '{"variants":{"":{"model":"mard_pixel:block/color_block"}}}'
item_model_template = '{"parent":"mard_pixel:block/color_block"}'

count = 0
for color in data["colors"]:
    code = color["code"].lower()
    block_id = f"color_block_{code}"

    # 方块状态文件
    with open(os.path.join(assets_dir, "blockstates", f"{block_id}.json"), "w", encoding="utf-8") as f:
        f.write(blockstate_template)

    # 物品模型文件
    with open(os.path.join(assets_dir, "models", "item", f"{block_id}.json"), "w", encoding="utf-8") as f:
        f.write(item_model_template)

    count += 1

print(f"Created blockstates and item models for {count} color blocks")
