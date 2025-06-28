# Contributing to APK Size Analyzer

🎉 Thank you for your interest in contributing to the APK Size Analyzer plugin! We welcome contributions from the community.

## 🤝 How to Contribute

### Reporting Issues
- **Check existing issues** before creating a new one
- **Use the issue template** to provide necessary details
- **Include steps to reproduce** the problem
- **Attach relevant files** (APKs, logs, screenshots)

### Feature Requests
- **Search for existing requests** to avoid duplicates
- **Provide clear use cases** and examples
- **Explain the benefits** to other users
- **Consider implementation complexity**

### Code Contributions

#### Getting Started
1. **Fork the repository** on GitHub
2. **Clone your fork** locally
3. **Create a feature branch** from `main`
4. **Set up the development environment**

```bash
git clone https://github.com/your-username/ApkSizeAnalyzer.git
cd ApkSizeAnalyzer
./gradlew runIde
```

#### Development Workflow
1. **Make your changes** in the feature branch
2. **Add tests** for new functionality
3. **Update documentation** if needed
4. **Ensure all tests pass**
5. **Follow code style guidelines**

```bash
# Run tests
./gradlew test

# Build the plugin
./gradlew build

# Test in development IDE
./gradlew runIde
```

#### Pull Request Process
1. **Update the CHANGELOG.md** with your changes
2. **Ensure all CI checks pass**
3. **Request review** from maintainers
4. **Address feedback** promptly
5. **Squash commits** if requested

## 🎯 Development Guidelines

### Code Style
- **Follow Kotlin conventions** and best practices
- **Use meaningful names** for classes, methods, and variables
- **Add KDoc comments** for public APIs
- **Keep methods small** and focused
- **Use data classes** for model objects

### Architecture Principles
- **Separation of concerns** between UI, business logic, and data
- **Service-oriented architecture** for core functionality
- **Background tasks** for long-running operations
- **Error handling** with user-friendly messages
- **Thread safety** for concurrent operations

### Testing Standards
- **Unit tests** for core logic and algorithms
- **Integration tests** for file operations
- **UI tests** for critical user flows
- **Test coverage** should be maintained or improved
- **Mock external dependencies** when appropriate

## 🏗️ Project Structure

### Core Components
```
src/main/kotlin/com/krishnasony/apksizeanalyzer/
├── actions/          # IDE actions and commands
├── file/             # File type definitions
├── gradle/           # Gradle build integration
├── model/            # Data models and DTOs
├── parser/           # APK parsing logic
├── services/         # Core business services
├── settings/         # Configuration management
├── toolwindow/       # UI tool window components
├── ui/               # Reusable UI components
└── utils/            # Utility classes
```

### Key Classes
- **ApkParser**: Core APK analysis logic
- **ApkAnalyzerService**: Main service for analysis operations
- **ApkAnalyzerToolWindow**: Primary UI component
- **CompareApksAction**: APK comparison workflow
- **ApkAnalyzerSettings**: Configuration management

## 🧪 Testing

### Running Tests
```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "ApkParserTest"

# Run tests with coverage
./gradlew test jacocoTestReport
```

### Test Categories
- **Unit Tests**: Fast, isolated tests for individual components
- **Integration Tests**: Tests that involve file system or external dependencies
- **UI Tests**: Tests for user interface components and interactions

### Test Data
- Use the `ApkTestUtils` class to generate sample APK files
- Keep test data small and focused
- Clean up temporary files after tests

## 📝 Documentation

### Code Documentation
- **Public APIs** must have KDoc comments
- **Complex algorithms** should be well-documented
- **Configuration options** need clear descriptions
- **Examples** should be provided for public methods

### User Documentation
- Update **README.md** for new features
- Add entries to **CHANGELOG.md**
- Update **DEVELOPMENT.md** for development changes
- Consider adding **screenshots** for UI changes

## 🐛 Debugging

### Common Issues
- **IDE won't start**: Check plugin.xml syntax and dependencies
- **Build failures**: Verify Gradle version and dependencies
- **Runtime errors**: Check logs in the development IDE

### Debug Logging
```kotlin
import com.intellij.openapi.diagnostic.Logger

private val LOG = Logger.getInstance(YourClass::class.java)
LOG.info("Debug message")
LOG.warn("Warning message")
LOG.error("Error message", exception)
```

### IDE Logs
- **Development IDE logs**: `build/idea-sandbox/system/log/`
- **Host IDE logs**: Help → Show Log in Explorer/Finder

## 🚀 Release Process

### Version Numbering
- Follow **Semantic Versioning** (MAJOR.MINOR.PATCH)
- **MAJOR**: Breaking changes
- **MINOR**: New features, backward compatible
- **PATCH**: Bug fixes, backward compatible

### Release Checklist
1. Update version in `build.gradle.kts`
2. Update `CHANGELOG.md` with release notes
3. Ensure all tests pass
4. Create release tag
5. Build and upload plugin ZIP
6. Update marketplace listing

## 📞 Support

### Getting Help
- **GitHub Discussions**: General questions and ideas
- **GitHub Issues**: Bug reports and feature requests
- **Email**: Direct contact for sensitive issues

### Response Times
- **Bug reports**: Within 48 hours
- **Feature requests**: Within 1 week
- **Pull requests**: Within 1 week

## 📄 License

By contributing to this project, you agree that your contributions will be licensed under the MIT License.

---

**Happy coding! 🎉**
