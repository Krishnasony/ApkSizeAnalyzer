# Changelog

All notable changes to the APK Size Analyzer plugin will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.3] - 2025-06-29

### Fixed
- Cleaned up code warnings and unused parameters
- Removed conflicting Kotlin stdlib dependency per IntelliJ Platform guidelines
- Fixed unchecked cast warnings with proper suppression
- Improved test assertions for better reliability
- Final polish for marketplace compatibility

## [1.0.2] - 2025-06-29

### Fixed
- Fixed deprecated API usage for marketplace compliance
- Replaced deprecated Messages.showChooseDialog with Messages.showDialog
- Replaced deprecated FileChooserDescriptorFactory with FileChooserDescriptor
- Temporarily disabled GradleBuildListener to avoid compatibility issues

### Changed
- Updated change notes to match actual version

## [1.0.1] - 2025-06-29

### Added
- **Enhanced APK Comparison**: Complete detailed comparison with 4 comprehensive tabs
  - Overview: Total size, file count changes, and summary statistics
  - File Changes: Individual file-level changes with visual status indicators
  - Folder Changes: Directory-level size analysis
  - Type Changes: File extension-based breakdown
- **Improved UI**: Professional tabbed interface for better data organization
- **Better Status Indicators**: Visual markers for added (➕), removed (➖), and modified (📝) files

### Fixed
- **UI Data Binding**: Fixed critical issue where analysis results weren't displayed in tables
- **Duplicate Functionality**: Removed redundant "Browse APK" button, keeping only "Analyze APK"
- **Component References**: Direct table updates instead of failed component searches

### Changed
- **Extended IDE compatibility**: Now supports Android Studio/IntelliJ IDEA up to build 251.*
- **Enhanced Toolbar**: Cleaner interface with single analyze button and clear instructions
- **Detailed Comparison**: Shows top 100 most significant changes sorted by impact

### Technical
- Updated `untilBuild` range from `241.*` to `251.*`
- Fixed table model updates using direct component references
- Enhanced comparison algorithms with comprehensive change tracking

## [1.0.0] - 2025-06-28

### 🎉 Release Status
- ✅ **Submitted to JetBrains Marketplace** for official distribution
- ✅ **Released on GitHub** as an open-source project  
- ✅ **Production ready** for Android developers
- 🔄 **Community feedback welcome** for future enhancements

### Added
- 🚀 **Initial plugin release**
- 📊 **APK size analysis** with detailed file breakdown
- 🔄 **APK comparison** with color-coded change indicators
- 📤 **Export functionality** (HTML, CSV, JSON formats)
- 🔧 **IDE integration** with tool window and context menus
- ⚙️ **Project settings** and configuration options
- 🎯 **Sample APK generator** for testing and demos
- 🔔 **Gradle build integration** with automatic analysis
- 📈 **Interactive charts** and visual representations
- 🎨 **Modern UI** with IntelliJ Look and Feel
- 🧪 **Comprehensive testing** suite
- 📚 **Documentation** and development guides

### Core Features
- **APK Parser**: Robust ZIP-based APK parsing
- **Size Calculator**: Accurate file and folder size calculations
- **Comparison Engine**: Intelligent diff algorithm for APK changes
- **Export System**: Multi-format report generation
- **Settings Management**: Persistent user preferences
- **Tool Window**: Integrated UI with tabbed interface
- **Action System**: Menu items and context actions
- **Notification System**: Build completion alerts

### Technical Highlights
- **IntelliJ Platform 2023.2+** compatibility
- **Kotlin** implementation with modern language features
- **Gradle** build system with IntelliJ Platform Plugin
- **Apache Commons Compress** for ZIP/APK handling
- **Swing UI** with JetBrains components
- **Background tasks** for non-blocking operations
- **VFS integration** for file system operations
- **Service architecture** for modularity

### Supported Platforms
- **IntelliJ IDEA Community/Ultimate 2023.2+**
- **Android Studio 2023.2+**
- **macOS, Windows, Linux**

### Dependencies
- **com.intellij.modules.platform**
- **com.intellij.modules.java**
- **org.jetbrains.android**
- **org.jetbrains.plugins.gradle**
- **org.apache.commons:commons-compress:1.24.0**

## [Unreleased]

### Planned Features
- 📊 **Advanced analytics** with size trend tracking
- 🎯 **Size optimization suggestions** and recommendations
- 🔍 **Deep APK inspection** with method count analysis
- 📱 **APK comparison history** and baseline management
- 🚀 **Performance improvements** for large APK files
- 🎨 **Enhanced visualizations** with more chart types
- 📤 **PDF export** support
- 🔔 **Size threshold alerts** and warnings
- 🧩 **Plugin API** for extensibility
- 🌐 **Multi-language support**

### Known Issues
- Large APK files (>100MB) may take longer to analyze
- Charts may not display correctly on some high-DPI displays
- Export functionality requires write permissions to target directory

### Compatibility Notes
- Requires IntelliJ IDEA 2023.2 or later
- Android plugin must be enabled for full functionality
- Gradle plugin integration requires Gradle 7.0+

---

For more details about specific changes, see the [commit history](../../commits/main).
