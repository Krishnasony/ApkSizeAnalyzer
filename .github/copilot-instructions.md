<!-- Use this file to provide workspace-specific custom instructions to Copilot. For more details, visit https://code.visualstudio.com/docs/copilot/copilot-customization#_use-a-githubcopilotinstructionsmd-file -->

# APK Size Analyzer - Android Studio Plugin

This is an IntelliJ IDEA plugin project for Android Studio that provides APK size analysis capabilities. The plugin allows developers to analyze and compare APK files directly within the IDE.

## Project Structure

- **Language**: Kotlin
- **Build System**: Gradle with IntelliJ Platform Plugin
- **Target Platform**: IntelliJ IDEA Community Edition / Android Studio
- **Dependencies**: Apache Commons Compress for ZIP/APK parsing

## Key Components

### Core Classes
- `ApkParser`: Main logic for parsing APK files and extracting size information
- `ApkAnalyzerService`: Project-level service for managing analysis operations
- `ApkAnalysisResult` & `ApkComparisonResult`: Data models for analysis results

### UI Components
- `ApkAnalyzerToolWindow`: Main UI for displaying analysis results
- `ApkAnalyzerToolWindowFactory`: Factory for creating tool windows

### Actions
- `CompareApksAction`: Compare two APK files
- `AnalyzeCurrentApkAction`: Analyze current project's APK
- `AnalyzeApkFileAction`: Context menu action for APK files

## Development Guidelines

### IntelliJ Platform APIs
- Use `@Service` annotation for project services
- Implement `AnAction` for menu actions
- Use `ToolWindowFactory` for custom tool windows
- Follow IntelliJ Platform SDK guidelines

### Code Style
- Use Kotlin data classes for models
- Implement proper error handling with try-catch blocks
- Use background tasks for long-running operations
- Follow IntelliJ Platform threading guidelines

### UI Development
- Use Swing components with IntelliJ UI library (JBTable, JBScrollPane, etc.)
- Implement tabbed interfaces for different views
- Provide progress indicators for background operations
- Use appropriate layout managers (BorderLayout, etc.)

### APK Analysis Features
- Parse APK files as ZIP archives
- Extract file sizes, compression ratios, and folder structures
- Calculate size differences between APK versions
- Group files by type and folder hierarchy
- Generate detailed comparison reports

## Plugin Configuration
- `plugin.xml`: Defines plugin metadata, extensions, and actions
- `build.gradle.kts`: Gradle build configuration with IntelliJ Platform Plugin
- Target IntelliJ IDEA 2023.2+ for compatibility

When implementing new features:
1. Follow the existing service-action-UI pattern
2. Use proper IntelliJ Platform APIs for file operations
3. Implement background tasks for analysis operations
4. Add appropriate error handling and user feedback
5. Update plugin.xml for new extensions or actions
