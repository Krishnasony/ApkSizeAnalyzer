package com.krishnasony.apksizeanalyzer.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.krishnasony.apksizeanalyzer.model.ApkAnalysisResult
import com.krishnasony.apksizeanalyzer.services.ApkAnalyzerService
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Action to export analysis results to various formats
 */
class ExportAnalysisAction : AnAction() {
    
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val service = project.service<ApkAnalyzerService>()
        
        val analysisResult = service.getLastAnalysisResult()
        val comparisonResult = service.getLastComparisonResult()
        
        if (analysisResult == null && comparisonResult == null) {
            Messages.showInfoMessage(
                project,
                "No analysis results available to export. Please analyze an APK first.",
                "No Data to Export"
            )
            return
        }
        
        // Choose export format
        val options = arrayOf("HTML Report", "CSV Data", "JSON Data")
        val choice = Messages.showChooseDialog(
            project,
            "Choose export format:",
            "Export Analysis Results",
            null,
            options,
            options[0]
        )
        
        if (choice == -1) return
        
        // Choose save location
        val descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
        descriptor.title = "Choose Export Location"
        
        val selectedFolder = FileChooser.chooseFile(descriptor, project, null)
        if (selectedFolder == null) return
        
        val exportDir = File(selectedFolder.path)
        
        try {
            when (choice) {
                0 -> exportAsHtml(exportDir, analysisResult, comparisonResult)
                1 -> exportAsCsv(exportDir, analysisResult, comparisonResult)
                2 -> exportAsJson(exportDir, analysisResult, comparisonResult)
            }
            
            Messages.showInfoMessage(
                project,
                "Analysis results exported successfully to ${exportDir.absolutePath}",
                "Export Complete"
            )
        } catch (e: Exception) {
            Messages.showErrorDialog(
                project,
                "Failed to export analysis results: ${e.message}",
                "Export Failed"
            )
        }
    }
    
    override fun update(e: AnActionEvent) {
        val project = e.project
        if (project != null) {
            val service = project.service<ApkAnalyzerService>()
            val hasData = service.getLastAnalysisResult() != null || service.getLastComparisonResult() != null
            e.presentation.isEnabled = hasData
        } else {
            e.presentation.isEnabled = false
        }
    }
    
    private fun exportAsHtml(
        exportDir: File,
        analysisResult: ApkAnalysisResult?,
        comparisonResult: com.krishnasony.apksizeanalyzer.model.ApkComparisonResult?
    ) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(Date())
        val fileName = "apk_analysis_report_$timestamp.html"
        val file = File(exportDir, fileName)
        
        FileWriter(file).use { writer ->
            writer.write(generateHtmlReport(analysisResult, comparisonResult))
        }
    }
    
    private fun exportAsCsv(
        exportDir: File,
        analysisResult: ApkAnalysisResult?,
        comparisonResult: com.krishnasony.apksizeanalyzer.model.ApkComparisonResult?
    ) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(Date())
        
        if (comparisonResult != null) {
            // Export file changes
            val fileName = "apk_file_changes_$timestamp.csv"
            val file = File(exportDir, fileName)
            
            FileWriter(file).use { writer ->
                writer.write("File Path,Change Type,Old Size,New Size,Size Difference\n")
                comparisonResult.fileChanges.forEach { change ->
                    writer.write("\"${change.path}\",${change.changeType},${change.oldSize ?: 0},${change.newSize ?: 0},${change.sizeDifference}\n")
                }
            }
        } else if (analysisResult != null) {
            // Export file list
            val fileName = "apk_file_list_$timestamp.csv"
            val file = File(exportDir, fileName)
            
            FileWriter(file).use { writer ->
                writer.write("File Path,Size,Compressed Size,Compression Method\n")
                analysisResult.files.forEach { fileInfo ->
                    writer.write("\"${fileInfo.path}\",${fileInfo.size},${fileInfo.compressedSize},\"${fileInfo.method}\"\n")
                }
            }
        }
    }
    
    private fun exportAsJson(
        exportDir: File,
        analysisResult: ApkAnalysisResult?,
        comparisonResult: com.krishnasony.apksizeanalyzer.model.ApkComparisonResult?
    ) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(Date())
        val fileName = "apk_analysis_$timestamp.json"
        val file = File(exportDir, fileName)
        
        FileWriter(file).use { writer ->
            writer.write(generateJsonReport(analysisResult, comparisonResult))
        }
    }
    
    private fun generateHtmlReport(
        analysisResult: ApkAnalysisResult?,
        comparisonResult: com.krishnasony.apksizeanalyzer.model.ApkComparisonResult?
    ): String {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        
        return buildString {
            append("""
                <!DOCTYPE html>
                <html>
                <head>
                    <title>APK Size Analysis Report</title>
                    <style>
                        body { font-family: Arial, sans-serif; margin: 20px; }
                        table { border-collapse: collapse; width: 100%; margin: 20px 0; }
                        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                        th { background-color: #f2f2f2; }
                        .increase { color: red; }
                        .decrease { color: green; }
                        .summary { background-color: #f9f9f9; padding: 15px; margin: 10px 0; }
                    </style>
                </head>
                <body>
                    <h1>APK Size Analysis Report</h1>
                    <p>Generated on: $timestamp</p>
            """.trimIndent())
            
            if (comparisonResult != null) {
                append(generateComparisonHtml(comparisonResult))
            } else if (analysisResult != null) {
                append(generateAnalysisHtml(analysisResult))
            }
            
            append("""
                </body>
                </html>
            """.trimIndent())
        }
    }
    
    private fun generateComparisonHtml(comparison: com.krishnasony.apksizeanalyzer.model.ApkComparisonResult): String {
        return buildString {
            append("""
                <div class="summary">
                    <h2>Comparison Summary</h2>
                    <p><strong>Old APK:</strong> ${comparison.oldApk.apkPath}</p>
                    <p><strong>New APK:</strong> ${comparison.newApk.apkPath}</p>
                    <p><strong>Size Change:</strong> 
                        <span class="${if (comparison.sizeDifference > 0) "increase" else "decrease"}">
                            ${formatSizeChange(comparison.sizeDifference)} (${String.format("%.2f", comparison.percentageChange)}%)
                        </span>
                    </p>
                </div>
                
                <h3>File Changes</h3>
                <table>
                    <tr>
                        <th>File Path</th>
                        <th>Change Type</th>
                        <th>Old Size</th>
                        <th>New Size</th>
                        <th>Size Change</th>
                    </tr>
            """.trimIndent())
            
            comparison.fileChanges.take(100).forEach { change ->
                append("""
                    <tr>
                        <td>${change.path}</td>
                        <td>${change.changeType}</td>
                        <td>${change.oldSize?.let { formatBytes(it) } ?: "-"}</td>
                        <td>${change.newSize?.let { formatBytes(it) } ?: "-"}</td>
                        <td class="${if (change.sizeDifference > 0) "increase" else "decrease"}">
                            ${formatSizeChange(change.sizeDifference)}
                        </td>
                    </tr>
                """.trimIndent())
            }
            
            append("</table>")
        }
    }
    
    private fun generateAnalysisHtml(analysis: ApkAnalysisResult): String {
        return buildString {
            append("""
                <div class="summary">
                    <h2>Analysis Summary</h2>
                    <p><strong>APK:</strong> ${analysis.apkPath}</p>
                    <p><strong>Total Size:</strong> ${formatBytes(analysis.totalSize)}</p>
                    <p><strong>File Count:</strong> ${analysis.files.size}</p>
                </div>
                
                <h3>Top Files by Size</h3>
                <table>
                    <tr>
                        <th>File Path</th>
                        <th>Size</th>
                        <th>Compressed Size</th>
                        <th>Compression</th>
                    </tr>
            """.trimIndent())
            
            analysis.files.take(50).forEach { file ->
                append("""
                    <tr>
                        <td>${file.path}</td>
                        <td>${formatBytes(file.size)}</td>
                        <td>${formatBytes(file.compressedSize)}</td>
                        <td>${String.format("%.1f", file.compression)}%</td>
                    </tr>
                """.trimIndent())
            }
            
            append("</table>")
        }
    }
    
    private fun generateJsonReport(
        analysisResult: ApkAnalysisResult?,
        comparisonResult: com.krishnasony.apksizeanalyzer.model.ApkComparisonResult?
    ): String {
        // Simple JSON generation - in a real implementation, you'd use a JSON library
        return buildString {
            append("{\n")
            append("  \"timestamp\": \"${SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(Date())}\",\n")
            
            if (comparisonResult != null) {
                append("  \"type\": \"comparison\",\n")
                append("  \"data\": {\n")
                append("    \"oldApk\": \"${comparisonResult.oldApk.apkPath}\",\n")
                append("    \"newApk\": \"${comparisonResult.newApk.apkPath}\",\n")
                append("    \"sizeDifference\": ${comparisonResult.sizeDifference},\n")
                append("    \"percentageChange\": ${comparisonResult.percentageChange},\n")
                append("    \"fileChangesCount\": ${comparisonResult.fileChanges.size}\n")
                append("  }\n")
            } else if (analysisResult != null) {
                append("  \"type\": \"analysis\",\n")
                append("  \"data\": {\n")
                append("    \"apkPath\": \"${analysisResult.apkPath}\",\n")
                append("    \"totalSize\": ${analysisResult.totalSize},\n")
                append("    \"fileCount\": ${analysisResult.files.size},\n")
                append("    \"folderCount\": ${analysisResult.folders.size}\n")
                append("  }\n")
            }
            
            append("}\n")
        }
    }
    
    private fun formatBytes(bytes: Long): String {
        val units = arrayOf("B", "KB", "MB", "GB")
        var size = bytes.toDouble()
        var unitIndex = 0
        
        while (size >= 1024 && unitIndex < units.size - 1) {
            size /= 1024
            unitIndex++
        }
        
        return if (unitIndex == 0) {
            "$bytes ${units[unitIndex]}"
        } else {
            "%.2f ${units[unitIndex]}".format(size)
        }
    }
    
    private fun formatSizeChange(sizeDifference: Long): String {
        return when {
            sizeDifference > 0 -> "+${formatBytes(sizeDifference)}"
            sizeDifference < 0 -> formatBytes(sizeDifference)
            else -> "0 B"
        }
    }
}
