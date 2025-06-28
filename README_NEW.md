# APK Size Analyzer - Android Studio Plugin

🚀 **A comprehensive IntelliJ IDEA/Android Studio plugin for analyzing and optimizing APK file sizes.**

![Plugin Version](https://img.shields.io/badge/version-1.0.0-blue)
![IntelliJ Platform](https://img.shields.io/badge/platform-IntelliJ%20IDEA%20%7C%20Android%20Studio-orange)
![License](https://img.shields.io/badge/license-MIT-green)

## ✨ Features

### 📊 APK Analysis
- **Detailed size breakdown** by file types (DEX, resources, assets, libraries, etc.)
- **File-by-file analysis** with compression ratios and actual sizes
- **Folder hierarchy visualization** to understand structure
- **Interactive charts** and graphs for visual analysis

### 🔄 APK Comparison
- **Side-by-side comparison** of two APK versions
- **Color-coded change indicators** (added, removed, modified files)
- **Impact analysis** with size difference calculations
- **Detailed change reports** for better optimization insights

### 📤 Export & Reporting
- **Multiple export formats**: HTML, CSV, JSON
- **Professional reports** with charts and tables
- **Share analysis results** with your team
- **Baseline tracking** for size regression monitoring

### 🔧 IDE Integration
- **Tool window** integrated into Android Studio/IntelliJ IDEA
- **Context menu actions** for APK files
- **Gradle build integration** for automatic analysis
- **Project-specific settings** and preferences

### 🎯 Developer Tools
- **Sample APK generator** for testing and demos
- **Automated analysis** after builds
- **Notification system** for build insights
- **Configurable thresholds** and alerts

## 🛠️ Installation

### From JetBrains Marketplace (Coming Soon)
1. Open Android Studio/IntelliJ IDEA
2. Go to **File** → **Settings** → **Plugins**
3. Search for "APK Size Analyzer"
4. Click **Install** and restart the IDE

### Manual Installation
1. Download the latest plugin ZIP from [Releases](../../releases)
2. Go to **File** → **Settings** → **Plugins**
3. Click **⚙️** → **Install Plugin from Disk...**
4. Select the downloaded ZIP file
5. Restart the IDE

### Development Setup
```bash
git clone https://github.com/krishnasony/ApkSizeAnalyzer.git
cd ApkSizeAnalyzer
./gradlew runIde
```

## 🚀 Quick Start

### 1. Analyze Current Project APK
1. Build your Android project
2. Go to **Tools** → **APK Size Analyzer** → **Analyze Current APK**
3. View detailed analysis in the tool window

### 2. Compare APK Versions
1. Go to **Tools** → **APK Size Analyzer** → **Compare APK Files**
2. Select the old and new APK files
3. Review the comparison results with color-coded changes

### 3. Generate Sample APKs (For Testing)
1. Go to **Tools** → **APK Size Analyzer** → **Generate Sample APK Files**
2. Choose a directory to save sample APKs
3. Use these files to explore the plugin features

### 4. Context Menu Analysis
1. Right-click any APK file in the project explorer
2. Select **Analyze APK Size**
3. View instant analysis results

## 📋 Usage Guide

### Tool Window Features
- **Analysis Tab**: Shows file breakdown and size details
- **Comparison Tab**: Displays side-by-side APK comparisons
- **Charts Tab**: Visual representations of size data
- **Export Options**: Save results in various formats

### Analysis Details
The plugin provides comprehensive analysis including:

- **DEX Files**: Code size and method count estimates
- **Resources**: Drawable, layout, and compiled resources
- **Assets**: Raw files bundled with the app
- **Native Libraries**: Architecture-specific .so files
- **META-INF**: Signing and manifest information

### Comparison Features
When comparing APKs, you'll see:

- 🟢 **Added files** (new in the target APK)
- 🔴 **Removed files** (missing from target APK)
- 🟡 **Modified files** (size changed between versions)
- 📊 **Impact levels** (High/Medium/Low based on size change)

## ⚙️ Configuration

### Settings Location
**File** → **Settings** → **Tools** → **APK Size Analyzer**

### Available Options
- **Auto-analyze after builds**: Automatically analyze APKs after Gradle builds
- **Show notifications**: Display build analysis notifications
- **Default export format**: Choose preferred export format
- **Size thresholds**: Configure impact level thresholds

## 🔧 Development

### Building the Plugin
```bash
./gradlew build
```

### Running Tests
```bash
./gradlew test
```

### Development IDE
```bash
./gradlew runIde
```

### Building Distribution
```bash
./gradlew buildPlugin
```

### Plugin Structure
```
src/main/kotlin/com/krishnasony/apksizeanalyzer/
├── actions/              # Menu actions and commands
├── file/                 # APK file type definitions
├── gradle/               # Gradle integration
├── model/                # Data models
├── parser/               # APK parsing logic
├── services/             # Core plugin services
├── settings/             # Configuration management
├── toolwindow/           # UI components
├── ui/                   # Charts and tables
└── utils/                # Utility classes
```

## 🧪 Testing

The plugin includes comprehensive tests for:
- **APK parsing accuracy**
- **Size calculation correctness**
- **Comparison algorithm reliability**
- **Export functionality**

Run tests with: `./gradlew test`

## 🐛 Troubleshooting

### Common Issues

#### Plugin Not Loading
- Ensure Android plugin is enabled
- Check IntelliJ IDEA version compatibility (2023.2+)
- Restart the IDE after installation

#### APK Analysis Fails
- Verify the file is a valid APK
- Check file permissions
- Ensure sufficient memory for large APKs

#### Tool Window Not Visible
- Go to **View** → **Tool Windows** → **APK Size Analyzer**
- Or use **Tools** → **APK Size Analyzer** menu

### Debug Logging
Enable debug logging by adding to IDE's custom VM options:
```
-Didea.log.debug.categories=com.krishnasony.apksizeanalyzer
```

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

### Development Setup
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

### Code Style
- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add documentation for public APIs
- Include unit tests for new features

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- IntelliJ Platform SDK documentation
- Android Developer documentation
- Apache Commons Compress library
- JetBrains UI guidelines

## 📞 Support

- **GitHub Issues**: [Report bugs or request features](../../issues)
- **Documentation**: [Development Guide](DEVELOPMENT.md)
- **Email**: [your-email@example.com]

---

**Made with ❤️ for Android developers**

*Help us improve! ⭐ Star this repo if you find it useful.*
