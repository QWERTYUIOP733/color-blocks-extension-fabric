from PIL import Image
import os

# 创建纯白色16x16 RGBA图片
img = Image.new('RGBA', (16, 16), (255, 255, 255, 255))
path = r'D:\文档\GitHub\游戏mod开发\fabric\src\main\resources\assets\mard_pixel\textures\block\color_block_template.png'
img.save(path, 'PNG')
print(f'已保存: {path}')
print(f'文件大小: {os.path.getsize(path)} bytes')

# 验证
img2 = Image.open(path)
print(f'尺寸: {img2.size}, 模式: {img2.mode}')
print(f'左上角像素: {img2.getpixel((0, 0))}')
