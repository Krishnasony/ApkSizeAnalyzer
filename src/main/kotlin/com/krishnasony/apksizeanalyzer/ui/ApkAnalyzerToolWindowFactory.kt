package com.krishnasony.apksizeanalyzer.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

/**
 * Factory for creating the APK Size Analyzer tool window
 */
class ApkAnalyzerToolWindowFactory : ToolWindowFactory {
    
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val apkAnalyzerToolWindow = ApkAnalyzerToolWindow(project)
        val content = ContentFactory.getInstance().createContent(
            apkAnalyzerToolWindow.getContent(), 
            "", 
            false
        )
        toolWindow.contentManager.addContent(content)
    }
}
