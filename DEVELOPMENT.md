# APK Size Analyzer - Development Guide

## 🛠️ Development Setup

### Prerequisites
1. **IntelliJ IDEA 2023.2+** or **Android Studio**
2. **JDK 17** or higher
3. **Gradle** (wrapper included)

### Opening the Project
1. Open IntelliJ IDEA/Android Studio
2. Select "Open" and choose this directory
3. Wait for Gradle sync to complete
4. Install IntelliJ Platform Plugin SDK if prompted

## 🏗️ Building the Plugin

### Using Gradle (Recommended)
```bash
# Build the plugin
./gradlew build

# Run plugin in development IntelliJ instance
./gradlew runIde

# Run tests
./gradlew test

# Build plugin distribution
./gradlew buildPlugin

# Verify plugin compatibility
./gradlew verifyPlugin
```

### Using VS Code Tasks
- Open Command Palette (`Cmd+Shift+P`)
- Run "Tasks: Run Task"
- Select appropriate task

## 🧪 Testing the Plugin

### Manual Testing
1. Run `./gradlew runIde` to launch development IntelliJ
2. Open an Android project
3. Test the following features:
   - **Tools → APK Size Analyzer → Compare APK Files**
   - **Tools → APK Size Analyzer → Analyze Current APK**
   - Right-click APK file → **Analyze APK Size**

### Unit Testing
```bash
./gradlew test
```

## 📁 Project Structure

```
src/main/kotlin/com/krishnasony/apksizeanalyzer/
├── actions/              # IDE actions (menu items, context actions)
│   ├── CompareApksAction.kt
│   ├── AnalyzeCurrentApkAction.kt
│   └── AnalyzeApkFileAction.kt
├── file/                 # File type definitions
│   └── ApkFileType.kt
├── model/                # Data models
│   └── ApkAnalysisModels.kt
├── parser/               # APK parsing logic
│   └── ApkParser.kt
├── services/             # Plugin services
│   └── ApkAnalyzerService.kt
└── toolwindow/          # UI components
    ├── ApkAnalyzerToolWindow.kt
    └── ApkAnalyzerToolWindowFactory.kt

src/main/resources/
├── META-INF/
│   └── plugin.xml        # Plugin configuration
└── messages/
    └── ApkSizeAnalyzerBundle.properties
```

## 🔧 Key Components

### 1. ApkParser
Core logic for parsing APK files and extracting size information.

```kotlin
val parser = ApkParser()
val analysis = parser.analyzeApk(apkFile)
val comparison = parser.compareApks(oldApk, newApk)
```

### 2. ApkAnalyzerService
Project-level service for managing analysis operations.

```kotlin
@Service(Service.Level.PROJECT)
class ApkAnalyzerService(private val project: Project)
```

### 3. Actions
Menu items and context actions for triggering analysis.

### 4. Tool Window
UI for displaying analysis results with tabbed interface.

## 🎨 UI Development

### Swing Components
- Use IntelliJ UI library components (JBTable, JBScrollPane)
- Follow IntelliJ Look and Feel guidelines
- Implement proper layout managers

### Background Tasks
```kotlin
ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Analyzing APK", true) {
    override fun run(indicator: ProgressIndicator) {
        // Long-running operation
    }
})
```

## 🔌 Plugin Configuration

### plugin.xml
Defines plugin metadata, extensions, and actions:
- Tool window factory
- Action registrations
- File type associations
- Service declarations

### Dependencies
- IntelliJ Platform APIs
- Android plugin integration
- Apache Commons Compress for ZIP parsing

## 📋 Development Tasks

### Adding New Features
1. Create appropriate model classes
2. Implement logic in service or parser
3. Add UI components if needed
4. Register actions in plugin.xml
5. Add tests
6. Update documentation

### Common Patterns
- Use `@Service` for project services
- Implement `AnAction` for menu actions
- Use `ToolWindowFactory` for UI panels
- Follow IntelliJ threading guidelines

## 🚀 Distribution

### Building Release
```bash
./gradlew buildPlugin
```
Plugin ZIP will be in `build/distributions/`

### Publishing Status 🚧
**Current Status**: Plugin is ready for release and awaiting publication!

🚧 **JetBrains Marketplace**: Ready for submission  
🚧 **GitHub Release**: Ready to be published  
✅ **Documentation**: Complete and comprehensive  
✅ **Code Quality**: Fully tested and verified  

### Next Steps for Publication
1. **Create GitHub Repository**: Initialize and push code to GitHub
2. **Create GitHub Release**: Tag version and upload plugin ZIP
3. **Submit to Marketplace**: Upload to JetBrains plugin repository
4. **Community Setup**: Enable issues and discussions

### 🛠️ How to Publish (Instructions)
```bash
# 1. Initialize Git repository (if not done)
git init
git branch -m main

# 2. Add files and commit
git add .
git commit -m "Initial release: APK Size Analyzer v1.0.0"

# 3. Create GitHub repository and push
# (Create repo on GitHub first, then:)
git remote add origin https://github.com/YOUR_USERNAME/ApkSizeAnalyzer.git
git push -u origin main

# 4. Create release
git tag v1.0.0
git push origin v1.0.0
# Then upload build/distributions/apk-size-analyzer-plugin-1.0.0.zip to GitHub release
```  

### Publishing Process (To Be Completed)
**Ready for Publication - Follow these steps:**

1. 🚧 **Create GitHub repository** and upload source code
2. 🚧 **Create GitHub release** with plugin ZIP file
3. 🚧 **Submit to JetBrains Marketplace** with plugin package
4. 🚧 **Set up community features** (issues, discussions, wiki)
5. 🚧 **Announce to developer community**

## 🐛 Debugging

### Common Issues
1. **ClassNotFoundException**: Check plugin.xml dependencies
2. **UI freezing**: Ensure background tasks for long operations
3. **File access issues**: Use proper VFS APIs

### Debug Logging
```kotlin
import com.intellij.openapi.diagnostic.Logger

private val LOG = Logger.getInstance(ApkAnalyzerService::class.java)
LOG.info("Analysis completed")
```

## 📚 Resources

- [IntelliJ Platform SDK](https://plugins.jetbrains.com/docs/intellij/)
- [Kotlin for Plugin Development](https://plugins.jetbrains.com/docs/intellij/kotlin.html)
- [UI Guidelines](https://jetbrains.design/intellij/)
- [Testing Plugins](https://plugins.jetbrains.com/docs/intellij/testing-plugins.html)
