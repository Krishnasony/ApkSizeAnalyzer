package com.krishnasony.apksizeanalyzer.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
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
    
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val service = project.service<ApkAnalyzerService>()
        
        // Choose first APK file
        val descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor("apk")
        descriptor.title = "Select Old APK File"
        descriptor.description = "Choose the baseline APK file for comparison"
        
        val oldApkVirtualFile = FileChooser.chooseFile(descriptor, project, null)
        if (oldApkVirtualFile == null) {
            return
        }
        
        // Choose second APK file
        descriptor.title = "Select New APK File"
        descriptor.description = "Choose the new APK file to compare against the baseline"
        
        val newApkVirtualFile = FileChooser.chooseFile(descriptor, project, null)
        if (newApkVirtualFile == null) {
            return
        }
        
        val oldApkFile = File(oldApkVirtualFile.path)
        val newApkFile = File(newApkVirtualFile.path)
        
        // Validate files
        if (!oldApkFile.exists() || !newApkFile.exists()) {
            Messages.showErrorDialog(project, "One or both APK files do not exist.", "Error")
            return
        }
        
        // Perform comparison in background
        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Comparing APK Files", true) {
            override fun run(indicator: ProgressIndicator) {
                indicator.text = "Analyzing APK files..."
                indicator.fraction = 0.1
                
                try {
                    indicator.text = "Parsing old APK..."
                    indicator.fraction = 0.3
                    
                    indicator.text = "Parsing new APK..."
                    indicator.fraction = 0.6
                    
                    indicator.text = "Computing differences..."
                    indicator.fraction = 0.8
                    
                    val result = service.compareApks(oldApkFile, newApkFile)
                    
                    indicator.text = "Displaying results..."
                    indicator.fraction = 1.0
                    
                    // Show results in tool window
                    showResults(project, result.newApk.apkPath)
                    
                } catch (e: Exception) {
                    Messages.showErrorDialog(project, "Failed to compare APK files: ${e.message}", "Error")
                }
            }
        })
    }
    
    private fun showResults(project: Project, title: String) {
        val toolWindowManager = ToolWindowManager.getInstance(project)
        val toolWindow = toolWindowManager.getToolWindow("APK Size Analyzer")
        toolWindow?.show()
    }
}
