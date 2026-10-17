# 彩色方块扩展（Color Blocks Extension）· Fabric 版

一套将官方 **MARD 221 色色卡**做成 Minecraft 实心可放置方块的像素画模组，支持 **Forge 与 Fabric 双加载器**，覆盖多个 Minecraft 版本。通用材料「七彩粉末」与专属「方块染色台」让生存模式也能完整体验。

> 本仓库是 **Fabric 版**；Forge 版在独立仓库 [-mod-](https://github.com/QWERTYUIOP733/-mod-)（monorepo，内含双加载器共享的色板数据与通用代码）。两个加载器的功能、界面保持一致。

- 模组 ID：`mard_pixel`
- 许可证：MIT
- 作者：Pixel Team
- 前置：**Fabric API**（需与游戏版本匹配）
- 色卡来源：官方 MARD 221 色（共 9 个色系）

---

## 支持版本

每个 Minecraft 版本 × 加载器都提供独立的构建，请按你的游戏版本下载对应文件。

| Minecraft | Fabric 模组版本 | Fabric Loader / API | Java |
|---|---|---|---|
| 1.20.1 | 1.3.2 | Loader 0.19.5+ / 对应 Fabric API | 17 |
| 1.21.1 | 2.0.0 | Loader 0.19.5+ / API 0.116.x | 21 |
| 1.21.4 | 2.1.0 | Loader 0.19.5+ / API 0.119.4+1.21.4 | 21 |
| 26.3 | 3.0.0 | Loader 0.19.5+ / 对应 Fabric API | 25 |

| Minecraft | Forge 模组版本 | Forge | Java |
|---|---|---|---|
| 1.20.1 | 1.2.1 | 47.x | 17 |
| 1.21.1 | 2.0.0 | 52.1.x | 21 |
| 1.21.4 | 2.1.0 | 54.1.x | 21 |
| 26.3 | 3.0.0 | 66.0.x | 25 |

> Fabric 版需安装与游戏版本匹配的 **Fabric API**；Forge 版无需任何前置模组。

---

## 功能特性

- **221 个色块**：基于官方 MARD 色卡的 221 种颜色（A1–A26、B1–B32、C1–C29、D1–D26、E1–E24、F1–F25、G1–G21、H1–H23、M1–M15），程序化染色，色值精确。
- **G 键色板 UI**：按 G 打开色板；按钮一浏览全部色号点击获取，按钮二输入色号批量放入快捷栏（创造模式可直接获取，生存模式仅查看）。
- **9 个创造模式标签页**：按色系划分，首个标签页含七彩粉末与方块染色台。
- **七彩粉末**：通用合成材料，任意 1 个原版染料即可合成。
- **方块染色台**：专用合成台，3×3 网格 + 侧边选色面板，1 个七彩粉末可合成所选颜色方块 64 个。
- **物品提示**：第一行色号，第二行 `RGB #RRGGBB`。
- **命令**：`/mardp give <色号>`、`/mardp find <#RRGGBB>`。

### 221 色分布

| 色系 | A | B | C | D | E | F | G | H | M | 合计 |
|---|---|---|---|---|---|---|---|---|---|---|
| 数量 | 26 | 32 | 29 | 26 | 24 | 25 | 21 | 23 | 15 | **221** |

---

## 使用方法

1. 游戏中按 **G** 键打开色板。
2. **按钮一 · 颜色选取**：浏览全部 221 色，创造模式点击色块获得一组（64 个）；生存模式仅可查看。
3. **按钮二 · 输入色号**：输入一个或多个色号（如 `A1 B2 C3`，可用空格、逗号、分号、顿号、斜杠分隔），自动按「快捷栏 → 背包 → 掉落」顺序放入。
4. **生存模式**无法直接获取方块，请使用方块染色台配合七彩粉末合成。

---

## 合成配方

- 任意 1 个原版染料 → 七彩粉末 × 1（无序合成，16 种染料均可）
- 七彩粉末 × 1 + 在方块染色台选择颜色 → 对应色块 × 64（仅消耗 1 个粉末）
- 任意木板 × 2 + 羽毛 × 2 → 方块染色台 × 1（无序合成）

---

## 安装

1. 安装对应 Minecraft 版本的 [Fabric Loader](https://fabricmc.net/use/)（0.19.5 及以上）。
2. 安装与游戏版本匹配的 [Fabric API](https://modrinth.com/mod/fabric-api)。
3. 在 [Releases](../../releases) 或 [Modrinth](https://modrinth.com/mod/color-blocks-extension/versions) 下载与游戏版本匹配的 `color_blocks_extension-<版本>.jar`。
4. 将 jar 放入 `.minecraft/mods/` 目录后启动游戏。

可选：安装 JEI 浏览合成配方。

---

## 下载

- **Fabric Releases**：https://github.com/QWERTYUIOP733/color-blocks-extension-fabric/releases
- **Forge / 主仓库 Releases**：https://github.com/QWERTYUIOP733/-mod-/releases
- **Modrinth 项目页**：https://modrinth.com/mod/color-blocks-extension

---

## 从源码构建

各 Minecraft 版本对应一个分支：`mc/1.20.1`、`mc/1.21.1`、`mc/1.21.4`、`mc/26.3`（`main` 为最新主线）。检出对应分支后构建：

```bash
git checkout mc/26.3
gradle build
```

构建产物位于 `build/libs/`。工具链要求：

| 分支（Minecraft） | JDK | Gradle |
|---|---|---|
| mc/1.20.1 | JDK 17 | 8.8 |
| mc/1.21.1 | JDK 21 | 8.8 |
| mc/1.21.4 | JDK 25（release 21），Loom 1.18.3 | 9.7.1 |
| mc/26.3 | JDK 25 | 9.7.1 |

---

## 更新日志

### v3.0.0 — Minecraft 26.3
- 支持 Minecraft 26.3，适配 Java 25 与新版 Fabric Loader / Fabric API。
- 适配 26.3 输入与渲染变更（SDL3 鼠标输入、GuiGraphics 文字渲染与界面背景）；G 键色板界面在各窗口尺寸下与既有版本保持一致，介绍框文字按清晰整数档缩放并整块居中，无黑边、无大黑框。
- 修复按 G 键时可能自动向物品栏投放一组方块的问题；高频获取提示改为动作条显示，不再占用聊天记录。
- 221 色块、七彩粉末、方块染色台、9 个创造标签页、G 键色板、批量色号输入、`/mardp` 命令等功能完整。

### v2.1.0 — Minecraft 1.21.4
- 修复 1.21.2 及以上版本合成书中 16 种染料配方未归组（每种染料各占一格）的问题。
- 适配 1.21.4 物品颜色数据驱动（常量染色）与配方格式变更。

### v2.0.0 — Minecraft 1.21.1
- 适配 1.21.1 自定义网络包（CustomPacketPayload）机制，规范载荷类型与接收端注册顺序，修复进入游戏时网络通道相关崩溃。
- 配方更新为 1.21.1 格式；G 键色板「mod 使用说明」面板随窗口大小自适应缩放并整块垂直居中。

### v1.3.2 — Minecraft 1.20.1（Fabric）
- 1.20.1 稳定版，G 键色板界面的视觉基准版本：221 色块、七彩粉末、方块染色台、9 个创造标签页、G 键色板与 `/mardp` 命令。

---

## 技术信息

- 所有色块使用程序化染色（ColorProviderRegistry），无需 221 张独立贴图。
- 色块 ID 格式：`mard_pixel:mard_<色号>`；七彩粉末：`mard_pixel:mard_pigment`；方块染色台：`mard_pixel:mard_crafting_table`。
- 客户端与服务端均需安装。不修改原版内容。

---

## 许可证

MIT © Pixel Team，可自由使用、修改与分发。
