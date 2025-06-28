# APK Size Analyzer - Android Studio Plugin

A powerful Android Studio plugin for analyzing and comparing APK file sizes. This plugin helps Android developers optimize their app sizes by providing detailed insights into size differences between APK versions.

## 🚀 Features

- **APK Comparison**: Compare two APK files side by side
- **Size Analysis**: Detailed breakdown by folder structure and file types
- **Visual Charts**: Interactive charts showing size differences
- **Android Studio Integration**: Seamless integration with your development workflow
- **Gradle Task Support**: Automated analysis during build process
- **Export Reports**: Save analysis results for documentation
- **Context Menu Actions**: Right-click analysis on APK files in project view

## 📱 How to Use

### 1. Install the Plugin
- Open Android Studio
- Go to `File` → `Settings` → `Plugins`
- Search for "APK Size Analyzer"
- Install and restart Android Studio

### 2. Analyze APK Files
- **Compare APKs**: `Tools` → `APK Size Analyzer` → `Compare APK Files`
- **Analyze Current APK**: `Tools` → `APK Size Analyzer` → `Analyze Current APK`
- **Context Menu**: Right-click any APK file → `Analyze APK Size`

### 3. View Results
- Results appear in the "APK Size Analyzer" tool window at the bottom
- Browse through different views: Overview, Folders, File Types, Details
- Export results as reports for documentation

## 🛠️ Development Setup

### Prerequisites
- IntelliJ IDEA or Android Studio
- JDK 17 or higher
- Gradle 8.0+

### Building the Plugin

```bash
# Clone the repository
git clone https://github.com/Krishnasony/ApkSizeAnalyzer.git
cd ApkSizeAnalyzer

# Build the plugin
./gradlew buildPlugin

# Run in development mode
./gradlew runIde
```

### Plugin Development Commands

```bash
# Build the plugin
./gradlew buildPlugin

# Run plugin in IntelliJ IDEA
./gradlew runIde

# Run tests
./gradlew test

# Verify plugin
./gradlew verifyPlugin

# Publish to marketplace
./gradlew publishPlugin
```

## 🏗️ Architecture

### Core Components

- **ApkAnalyzerService**: Main service for APK analysis
- **ApkAnalyzerToolWindow**: UI component for displaying results
- **CompareApksAction**: Action for comparing two APK files
- **AnalyzeCurrentApkAction**: Action for analyzing current project's APK
- **ApkParser**: Core logic for parsing and analyzing APK files

### File Structure

```
src/main/kotlin/com/krishnasony/apksizeanalyzer/
├── actions/           # Action classes for menu items
├── model/            # Data models for analysis results
├── parser/           # APK parsing logic
├── services/         # Plugin services
├── toolwindow/       # Tool window implementation
└── ui/               # UI components and panels
```

## 🔧 Configuration

### Gradle Integration
Add to your app's `build.gradle`:

```gradle
apply plugin: 'apk-size-analyzer'

apkSizeAnalyzer {
    enabled = true
    baselineApk = file('baseline/app-release.apk')
    threshold = '10%' // Alert if size increases by more than 10%
}
```

## 📊 Analysis Features

### Size Comparison
- Total APK size differences
- Per-folder size analysis
- File type breakdown (DEX, resources, assets, etc.)
- Individual file size changes

### Visual Reports
- Interactive charts using Charts library
- Size distribution pie charts
- Growth trend analysis
- Detailed file explorer

### Export Options
- HTML reports
- CSV data export
- JSON analysis results
- PDF summary reports

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Built on top of the Chrome Extension version
- Uses Apache Commons Compress for APK parsing
- Inspired by Android Studio's built-in APK analyzer

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/Krishnasony/ApkSizeAnalyzer/issues)
- **Documentation**: [Wiki](https://github.com/Krishnasony/ApkSizeAnalyzer/wiki)
- **Email**: your.email@example.com

---

**Made with ❤️ for the Android development community**
