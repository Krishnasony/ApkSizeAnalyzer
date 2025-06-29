package com.krishnasony.apksizeanalyzer.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.ToolWindowManager
import com.krishnasony.apksizeanalyzer.services.ApkAnalyzerService
import java.io.File

/**
 * Action to analyze an APK file from the context menu
 */
class AnalyzeApkFileAction : AnAction() {
    
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        val service = project.service<ApkAnalyzerService>()
        
        val apkFile = File(virtualFile.path)
        
        // Validate file
        if (!apkFile.exists() || !apkFile.name.endsWith(".apk", ignoreCase = true)) {
            Messages.showErrorDialog(project, "Selected file is not a valid APK file.", "Error")
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
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        val isApkFile = virtualFile?.name?.endsWith(".apk", ignoreCase = true) == true
        e.presentation.isVisible = isApkFile
        e.presentation.isEnabled = isApkFile
    }
    
    private fun showResults(project: Project) {
        val toolWindowManager = ToolWindowManager.getInstance(project)
        val toolWindow = toolWindowManager.getToolWindow("APK Size Analyzer")
        toolWindow?.show()
    }
}
