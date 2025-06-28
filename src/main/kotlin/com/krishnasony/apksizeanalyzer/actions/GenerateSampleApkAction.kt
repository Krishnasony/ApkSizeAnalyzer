package com.krishnasony.apksizeanalyzer.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFileManager
import com.krishnasony.apksizeanalyzer.utils.ApkTestUtils
import java.io.File

/**
 * Action to generate sample APK files for testing the analyzer
 */
class GenerateSampleApkAction : AnAction("Generate Sample APK", "Generate sample APK files for testing", null) {
    
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        
        // Let user choose where to save the APK files
        val descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
        descriptor.title = "Choose Directory to Save Sample APK Files"
        
        val selectedDir = FileChooser.chooseFile(descriptor, project, null) ?: return
        val outputDir = File(selectedDir.path)
        
        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Generating Sample APK Files", true) {
            override fun run(indicator: ProgressIndicator) {
                try {
                    indicator.text = "Creating sample APK files..."
                    
                    indicator.fraction = 0.3
                    indicator.text2 = "Creating small_sample.apk"
                    val smallApk = File(outputDir, "small_sample.apk")
                    ApkTestUtils.createSampleApk(smallApk)
                    
                    indicator.fraction = 0.7
                    indicator.text2 = "Creating large_sample.apk"
                    val largeApk = File(outputDir, "large_sample.apk")
                    ApkTestUtils.createLargeSampleApk(largeApk)
                    
                    indicator.fraction = 1.0
                    indicator.text2 = "Sample APK files created successfully"
                    
                    // Refresh the virtual file system to show the new files
                    VirtualFileManager.getInstance().refreshWithoutFileWatcher(false)
                    
                    // Show success message
                    Messages.showInfoMessage(
                        project,
                        "Sample APK files created successfully:\n" +
                                "• ${smallApk.name} (${formatFileSize(smallApk.length())})\n" +
                                "• ${largeApk.name} (${formatFileSize(largeApk.length())})",
                        "Sample APK Generation Complete"
                    )
                    
                } catch (e: Exception) {
                    Messages.showErrorDialog(
                        project,
                        "Failed to generate sample APK files: ${e.message}",
                        "Error"
                    )
                }
            }
        })
    }
    
    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes >= 1024 * 1024 -> "%.1f MB".format(bytes / (1024.0 * 1024.0))
            bytes >= 1024 -> "%.1f KB".format(bytes / 1024.0)
            else -> "$bytes bytes"
        }
    }
}
