package com.krishnasony.apksizeanalyzer.gradle

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import com.krishnasony.apksizeanalyzer.services.ApkAnalyzerService
import java.io.File

/**
 * Gradle task listener for automatic APK analysis after builds
 * Note: Temporarily disabled due to API compatibility issues
 */
class GradleBuildListener {
    
    // This class is currently disabled to avoid compatibility issues
    // Can be re-enabled when a stable replacement for ExternalSystemTaskNotificationListenerAdapter is available
    
    fun analyzeBuiltApk(project: Project) {
        val service = project.service<ApkAnalyzerService>()
        val apkFile = service.getCurrentProjectApk()
        
        if (apkFile != null) {
            try {
                val result = service.analyzeApk(apkFile)
                
                // Show results in tool window
                val toolWindowManager = ToolWindowManager.getInstance(project)
                val toolWindow = toolWindowManager.getToolWindow("APK Size Analyzer")
                toolWindow?.show()
                
                // Optionally show a notification
                showBuildAnalysisNotification(project, apkFile, result.totalSize)
                
            } catch (e: Exception) {
                // Log error but don't interrupt the build process
                println("APK analysis failed: ${e.message}")
            }
        }
    }
    
    private fun showBuildAnalysisNotification(project: Project, apkFile: File, totalSize: Long) {
        val service = project.service<ApkAnalyzerService>()
        val sizeFormatted = service.formatFileSize(totalSize)
        
        com.intellij.notification.NotificationGroupManager.getInstance()
            .getNotificationGroup("APK Size Analyzer")
            ?.createNotification(
                "APK Analysis Complete",
                "Built APK ${apkFile.name} analyzed: $sizeFormatted",
                com.intellij.notification.NotificationType.INFORMATION
            )
            ?.notify(project)
    }
}
