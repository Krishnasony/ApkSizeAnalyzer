package com.krishnasony.apksizeanalyzer.toolwindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

/**
 * Factory for creating the APK Analyzer tool window
 */
class ApkAnalyzerToolWindowFactory : ToolWindowFactory {
    
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val apkAnalyzerWindow = ApkAnalyzerToolWindow(project)
        val content = ContentFactory.getInstance().createContent(
            apkAnalyzerWindow.getContent(),
            "",
            false
        )
        toolWindow.contentManager.addContent(content)
    }
}
