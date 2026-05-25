# 🛞 LuckyWheel（幸运大转盘）

基于 **Jetpack Compose** 和 **Material 3** 的 Android 幸运大转盘应用。

专治选择困难症——午饭吃啥？喝哪杯奶茶？让转盘帮你决定。

## ✨ 功能

- **转盘抽选** — Canvas 自绘转盘，带缓出动画和指针指示器
- **权重系统** — 每个选项可设 1-10 权重，控制中选概率
- **自定义选项** — 自由增删改选项，支持名称、颜色、权重编辑
- **9 套预设** — 内置蜜雪冰城、瑞幸咖啡、霸王茶姬、茶百道等饮品菜单
- **配置管理** — 保存/加载/重命名/删除转盘配置，一键切换
- **调色板** — 5 种色系共 45 色可选，每个扇区独立配色

## 🛠 技术栈

| 类别 | 技术 |
|------|------|
| 语言 | Kotlin |
| UI | Jetpack Compose + Material 3 |
| 构建 | Gradle (Kotlin DSL) |
| 最低 SDK | Android 13 (API 33) |
| 目标 SDK | Android 14 (API 34) |

## 🚀 构建

用 Android Studio 打开项目，Gradle 同步后：

**IDE 方式：** `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`

**命令行：**
```bash
./gradlew assembleDebug
```

APK 输出路径：`app/build/outputs/apk/debug/app-debug.apk`

## 📱 使用

1. 打开 App，点击 **🎯 开始选择** 转动转盘
2. 点击右上角齿轮进入**设置页面**
3. 在设置中增删选项、调整权重和颜色
4. 点击**保存配置**存储当前方案
5. 点击**已存配置**加载或切换预设

## 📄 许可

MIT License
