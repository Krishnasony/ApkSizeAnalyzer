package com.krishnasony.apksizeanalyzer.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.ToolWindowManager
import com.krishnasony.apksizeanalyzer.services.ApkAnalyzerService
import java.io.File

/**
 * Action to compare two APK files
 */
class CompareApksAction : AnAction() {
    
    override fun update(e: AnActionEvent) {
        // Always enable this action - it doesn't depend on project state
        e.presentation.isEnabledAndVisible = true
    }
    
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        
        try {
            // Open the APK Size Analyzer tool window
            val toolWindowManager = ToolWindowManager.getInstance(project)
            val toolWindow = toolWindowManager.getToolWindow("APK Size Analyzer")
            
            if (toolWindow != null) {
                toolWindow.show()
                // The tool window will handle the file selection and comparison
            } else {
                Messages.showErrorDialog(project, "APK Size Analyzer tool window is not available.", "Error")
            }
            
        } catch (e: Exception) {
            Messages.showErrorDialog(project, "Failed to open APK Size Analyzer: ${e.message}", "Error")
        }
    }
}
