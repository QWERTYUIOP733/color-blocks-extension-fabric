file = r"D:\文档\GitHub\游戏mod开发\fabric\src\main\java\com\mard\pixel\fabric\MardCraftingScreenHandler.java"
with open(file, 'r', encoding='utf-8') as f:
    content = f.read()

# 在quickMoveStack之前添加broadcastChanges方法
old = '''    @Override
    public ItemStack quickMoveStack(Player player, int index) {'''
new = '''    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (blockEntity != null) {
            blockEntity.setChanged();
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {'''
content = content.replace(old, new)

with open(file, 'w', encoding='utf-8') as f:
    f.write(content)

print("已添加broadcastChanges方法")
with open(file, 'r', encoding='utf-8') as f:
    c = f.read()
print("broadcastChanges存在:", "broadcastChanges()" in c)
