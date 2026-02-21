# FolkPure

> 🌸 基于 [AxManager](https://github.com/fahrez182/AxManager) 开发的精美分支，拥有完善的翻译和更好的交互体验。

**FolkPure** 是原始 **[AxManager](https://github.com/fahrez182/AxManager)** 项目的分支，原项目由 **fahrez182** 开发。

AxManager 是一款 Android 应用，旨在提供对应用和系统的深度控制。

FolkPure 在此基础上进行开发，专注于提供**优雅的用户界面**、**完善的多语言支持**和**流畅的交互体验**，为用户带来更好的使用感受。

与 *KernelSU* 或其他基于 root 权限的"管理器"等工具不同，**FolkPure**（同 AxManager）专用于 **ADB/非 root 模式**，同时在设备拥有 **root 权限** 时，也允许执行命令。

## ✨ 功能特性

- 🎨 **精美的设计**
  - 采用 Material Design 3 设计语言，界面现代美观
  - 流畅的动画和过渡效果，带来高级感
  - 直观的导航和布局设计

- 🌐 **完善的翻译**
  - 全面支持多种语言
  - 精心本地化的 UI 字符串，翻译准确自然
  - 定期更新，确保翻译质量

- ✨ **更好的交互体验**
  - 优化的交互操作，更加流畅顺滑
  - 改进的反馈机制和视觉提示
  - 简化常用操作流程，提升效率

- 🖥️ **Shell 执行器**
  - 直接从应用运行 shell 命令
  - 支持 **ADB/非 root 执行**
  - 如果设备拥有 root 权限，则可选择 **root 执行**

- ⚡ **插件（无需root权限的模块）**
  - 无需root权限即可管理第三方模块

- 🌐 **WebUI（无需root权限的版本）**
  - 通过基于Web的交互式界面执行shell命令

## 📱 与root管理器的主要区别

- 🚫 **不**依赖root权限
- ✅ 优先考虑**ADB/非root权限**，使其可在更广泛的设备上使用
- 🔑 Root支持是**可选的**，并非必需
- 🌐 提供**WebShell UI**作为一项独特功能

## 📖 FolkPure 的特色

FolkPure 是经过精心优化的 AxManager 分支，带来以下改进：

- **视觉优化**：每个 UI 元素都经过精心打磨，更加美观
- **更好的 UX**：更流畅的交互和更直观的控件
- **高质量翻译**：准确自然的语言支持
- **注重细节**：微小改进带来更好的体验

## 🔧 构建与安装

克隆仓库并使用 Android Studio 或 Gradle 进行构建：

```bash
git clone https://github.com/matsuzaka-yuki/FolkPure.git
cd FolkPure
./gradlew assembleDebug
```

通过 ADB 安装到您的设备：

```bash
adb install manager/build/outputs/apk/debug/FolkPure_v*.apk
```

## 🤝 贡献

欢迎贡献！

您可以提交 **issues**、**pull request** 或发起讨论，提出新的想法和改进建议。

## 🙏 致谢与鸣谢

### 原始项目
**[AxManager](https://github.com/fahrez182/AxManager)** 作者：**fahrez182**

FolkPure 是 AxManager 的分支。原始 AxManager 项目由 fahrez182 开发和维护。
- 代码仓库：https://github.com/fahrez182/AxManager
- 作者：fahrez182

**重要提示**：如果您重新分发或修改此项目，请保留对原开发者 fahrez182 的署名。

### 灵感来源与参考资料
- **[Magisk](https://github.com/topjohnwu/Magisk)** - BusyBox 和插件（无需root权限的模块）的灵感来源
- **[Shizuku](https://github.com/RikkaApps/Shizuku) / [API](https://github.com/RikkaApps/Shizuku-API)** - 学习 Android 进程间通信 (IPC) 和基于 ADB 的权限处理的起点和参考
- **[KernelSU](https://github.com/tiann/KernelSU) / [Next](https://github.com/KernelSU-Next/KernelSU-Next)** - UI 和 WebUI 功能的灵感来源

## ⚠️ 声明与法律免责声明

本项目包含以下项目的改编代码：
- AxManager (© Fahrez182)
  遵循 Apache License 2.0 许可协议
  代码仓库：https://github.com/fahrez182/AxManager
- Shizuku Manager (© Rikka Apps)
  遵循 Apache License 2.0 许可协议
  代码仓库：https://github.com/RikkaApps/Shizuku
- 上述提及的其他开源项目

FolkPure 不包含或分发任何来自 AxManager、Shizuku Manager 的原始视觉素材，也不声称是其官方替代品。

所有改编代码均严格用于教育和实验目的，并已明确注明出处，且符合 Apache License 2.0 许可协议。

## 📜 许可协议

遵循 [Apache License 2.0](LICENSE) 许可协议。
