# VFX Tracker - 专业级影视拍摄手机屏幕跟踪工具

![VFX Tracker Logo](https://via.placeholder.com/150/00FF00/FFFFFF?text=VFX+Tracker)

**VFX Tracker** 是一款专为影视后期制作（VFX）设计的 Android 应用。它将智能手机转变为标准的色度键（Chroma Key）绿幕，并带有高精度的追踪标记（Tracking Markers），帮助后期制作人员轻松完成屏幕替换和透视匹配。

## ✨ 核心特性

- 🖥️ **全屏沉浸模式**：完全隐藏系统状态栏和导航栏，提供最大化的发光区域。
- 🎨 **智能对比度算法 (Luminance Offset)**：Markers 颜色基于背景色实时计算。通过微小的亮度偏移（默认 -20%），在保证追踪软件精准识别的同时，最小化对演员皮肤的溢色（Spill）影响。
- 🔮 **Glassmorphism 毛玻璃 UI**：基于拟态设计语言的控制面板，提供极致的视觉体验，且不干扰背景曝光参考。
- 🛡️ **绝对防误触锁定**：
  - 点击“完成设定”后 UI 彻底消失。
  - 屏蔽所有点击、双击手势。
  - **唯一解锁方式**：在屏幕任意位置持续长按 3 秒，防止拍摄现场意外触发。
- 📱 **硬件接管**：
  - **屏幕常亮**：防止拍摄过程中手机进入休眠。
  - **亮度控制**：直接在 App 内调节硬件背光，适应不同影棚光效。
  - **方向锁定**：通过 Manifest 强制锁定竖屏/横屏，防止重力感应导致画面翻转。

## 🚀 快速开始

### 开发环境要求
- **Android Studio** Ladybug 或更新版本。
- **Kotlin** 1.9+。
- **Jetpack Compose** 现代 UI 框架。
- **Minimum SDK**: API 24 (Android 7.0)。

### 安装步骤
1. 克隆或下载本项目代码。
2. 在 Android Studio 中打开项目。
3. 确保 `AndroidManifest.xml` 中已配置 `android:screenOrientation="portrait"` 以锁定方向。
4. 点击 **Run** 部署到测试机。

## 🛠️ 技术栈

- **UI 框架**：Jetpack Compose (声明式 UI)。
- **绘图引擎**：Compose `Canvas` (高性能原生绘制)。
- **手势系统**：底层 `pointerInput` 自定义计时器监听。
- **窗口管理**：`WindowInsetsControllerCompat` 实现全屏沉浸。

## 📖 拍摄使用指南

1. **设置背景**：根据合成需求选择 Chroma Green（绿幕）或 Chroma Blue（蓝幕）。
2. **调节可见度**：通过 `Luminance Offset` 滑块调整 Markers。建议调至“肉眼刚好看清”的状态，这对后期软件最为友好。
3. **调整大小/粗细**：若镜头为远景或广角，建议调大十字标记；若是特写，可适当调小以减少擦除工作量。
4. **锁定**：确认所有参数后点击“完成设定并锁定”。
5. **解锁**：若需重新调节，请寻找屏幕空位持续按住 3 秒钟。

## ⚖️ 许可证

本项目采用 MIT 许可证。您可以自由用于商业拍摄或二次开发。
