package com.krishnasony.apksizeanalyzer.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.ToolWindowManager
import com.krishnasony.apksizeanalyzer.services.ApkAnalyzerService

/**
 * Action to analyze the current project's APK
 */
class AnalyzeCurrentApkAction : AnAction() {
    
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val service = project.service<ApkAnalyzerService>()
        
        // Try to find the current project's APK
        val apkFile = service.getCurrentProjectApk()
        if (apkFile == null) {
            Messages.showInfoMessage(
                project,
                "No APK file found in the project's build outputs.\n\n" +
                "Please build your project first or use 'Compare APK Files' to select specific APK files.",
                "APK Not Found"
            )
            return
        }
        
        // Analyze APK in background
        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Analyzing APK", true) {
            override fun run(indicator: ProgressIndicator) {
                indicator.text = "Analyzing ${apkFile.name}..."
                indicator.fraction = 0.1
                
                try {
                    indicator.text = "Parsing APK structure..."
                    indicator.fraction = 0.5
                    
                    service.analyzeApk(apkFile)
                    
                    indicator.text = "Displaying results..."
                    indicator.fraction = 1.0
                    
                    // Show results in tool window
                    showResults(project)
                    
                } catch (e: Exception) {
                    Messages.showErrorDialog(project, "Failed to analyze APK: ${e.message}", "Error")
                }
            }
        })
    }
    
    override fun update(e: AnActionEvent) {
        // Enable action only for Android projects
        val project = e.project
        e.presentation.isEnabled = project != null && isAndroidProject(project)
    }
    
    private fun isAndroidProject(project: Project): Boolean {
        // Check if this is an Android project by looking for common Android files
        val basePath = project.basePath ?: return false
        val androidManifest = java.io.File(basePath, "app/src/main/AndroidManifest.xml")
        val buildGradle = java.io.File(basePath, "app/build.gradle")
        val buildGradleKts = java.io.File(basePath, "app/build.gradle.kts")
        
        return androidManifest.exists() && (buildGradle.exists() || buildGradleKts.exists())
    }
    
    private fun showResults(project: Project) {
        val toolWindowManager = ToolWindowManager.getInstance(project)
        val toolWindow = toolWindowManager.getToolWindow("APK Size Analyzer")
        toolWindow?.show()
    }
}
