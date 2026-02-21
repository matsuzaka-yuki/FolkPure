# FolkPure

> 🌸 A beautifully designed fork of [AxManager](https://github.com/fahrez182/AxManager) with enhanced translations and improved user experience.

**FolkPure** is a fork of the original **[AxManager](https://github.com/fahrez182/AxManager)** project developed by **fahrez182**.

AxManager is an Android application designed to provide deeper control over apps and the system.  
FolkPure builds upon this foundation, focusing on delivering an **elegant user interface**, **comprehensive multi-language support**, and **smoother interactions** to enhance the overall user experience.

Unlike tools such as *KernelSU* or other root-based "Managers," **FolkPure** (like AxManager) is dedicated to **ADB/Non-Root mode** — while still allowing execution with **Root access** if available.

## ✨ Features

- 🎨 **Beautiful Design**
  - Modern Material Design 3 interface with polished aesthetics
  - Smooth animations and transitions for a premium feel
  - Intuitive navigation and layout

- 🌐 **Comprehensive Translations**
  - Full support for multiple languages
  - Carefully localized UI strings for accurate translations
  - Regular updates to ensure translation quality

- ✨ **Enhanced User Experience**
  - Optimized interactions for smoother operation
  - Improved feedback and visual cues
  - Streamlined workflows for common tasks

- 🖥️ **Shell Executor**
  - Run shell commands directly from the app.
  - Supports **ADB/Non-Root execution**.
  - Optional **Root execution** if the device has root access.

- ⚡ **Plugin (Unrooted Module)**
  - Manage third-party modules with unrooted access.

- 🌐 **WebUI (Unrooted Version)**
  - Execute shell commands with a web-based interactive interface.

## 📱 Key Difference from Root Managers

- 🚫 Does **not** depend on Root access.
- ✅ Focused on **ADB/Non-Root first**, making it usable on a wider range of devices.
- 🔑 Root support is **optional**, not a requirement.
- 🌐 Provides **WebShell UI** as a unique feature.

## 📖 What Makes FolkPure Special

FolkPure is a curated fork of AxManager with the following enhancements:

- **Visual Polish**: Every UI element has been refined for better aesthetics
- **Better UX**: Smoother interactions and more intuitive controls
- **Quality Translations**: Accurate and natural language support
- **Attention to Detail**: Small improvements that make a big difference

## 🔧 Build & Install

Clone the repository and build using Android Studio or Gradle:

```bash
git clone https://github.com/matsuzaka-yuki/FolkPure.git
cd FolkPure
./gradlew assembleDebug
```

Install to your device via ADB:

```bash
adb install manager/build/outputs/apk/debug/FolkPure_v*.apk
```

## 🤝 Contribution

Contributions are welcome!  
Feel free to open **issues**, submit **pull requests**, or start a discussion for new ideas and improvements.

## 🙏 Credits & Acknowledgments

### Original Project
**[AxManager](https://github.com/fahrez182/AxManager)** by **fahrez182**

FolkPure is a fork of AxManager. The original AxManager project is developed and maintained by fahrez182.
- Repository: https://github.com/fahrez182/AxManager
- Author: fahrez182

**Important**: If you redistribute or modify this project, please retain attribution to the original developer, fahrez182.

### Inspiration & References
- **[Magisk](https://github.com/topjohnwu/Magisk)** - Inspiration for BusyBox and Plugin (Unrooted module) ideas
- **[Shizuku](https://github.com/RikkaApps/Shizuku) / [API](https://github.com/RikkaApps/Shizuku-API)** - Starting point and reference for learning Android IPC and ADB-based permission handling
- **[KernelSU](https://github.com/tiann/KernelSU) / [Next](https://github.com/KernelSU-Next/KernelSU-Next)** - Inspiration for the UI and WebUI features

## ⚠️ Notices & Legal Disclaimer

This project includes adapted portions of code from:
- AxManager (© Fahrez182)
  Licensed under the Apache License, Version 2.0
  Repository: https://github.com/fahrez182/AxManager
- Shizuku Manager (© Rikka Apps)
  Licensed under the Apache License, Version 2.0
  Repository: https://github.com/RikkaApps/Shizuku
- Other open-source projects as credited above.

FolkPure does not include or distribute any original visual assets from AxManager, Shizuku Manager or claim to be an official replacement.
All adapted code is used strictly for educational and experimental purposes, with clear attribution and compliance with the Apache License 2.0.

## 📜 License

Licensed under the [Apache License 2.0](LICENSE).
