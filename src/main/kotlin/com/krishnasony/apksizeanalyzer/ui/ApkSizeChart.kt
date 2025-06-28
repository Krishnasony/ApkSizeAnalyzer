package com.krishnasony.apksizeanalyzer.ui

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.krishnasony.apksizeanalyzer.model.*
import com.krishnasony.apksizeanalyzer.services.ApkAnalyzerService
import java.awt.*
import javax.swing.*

/**
 * Chart component for visualizing APK size data
 */
class ApkSizeChart(private val project: Project) : JPanel() {
    
    private val service = project.service<ApkAnalyzerService>()
    
    fun showComparisonChart(comparison: ApkComparisonResult) {
        removeAll()
        layout = BorderLayout()
        
        val chartPanel = createComparisonChart(comparison)
        add(chartPanel, BorderLayout.CENTER)
        
        revalidate()
        repaint()
    }
    
    fun showSizeDistribution(analysis: ApkAnalysisResult) {
        removeAll()
        layout = BorderLayout()
        
        val chartPanel = createDistributionChart(analysis)
        add(chartPanel, BorderLayout.CENTER)
        
        revalidate()
        repaint()
    }
    
    private fun createComparisonChart(comparison: ApkComparisonResult): JPanel {
        val panel = JPanel(GridLayout(2, 1))
        
        // Size comparison bar chart
        val sizeComparisonPanel = createSizeComparisonPanel(comparison)
        panel.add(sizeComparisonPanel)
        
        // Top folder changes
        val folderChangesPanel = createFolderChangesPanel(comparison)
        panel.add(folderChangesPanel)
        
        return panel
    }
    
    private fun createSizeComparisonPanel(comparison: ApkComparisonResult): JPanel {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.border = BorderFactory.createTitledBorder("Size Comparison")
        
        val oldSize = comparison.oldApk.totalSize
        val newSize = comparison.newApk.totalSize
        val maxSize = maxOf(oldSize, newSize)
        
        // Old APK bar
        val oldBar = createProgressBar("Old APK", oldSize, maxSize, Color.BLUE)
        panel.add(oldBar)
        
        // New APK bar
        val newBar = createProgressBar("New APK", newSize, maxSize, 
            if (newSize > oldSize) Color.RED else Color.GREEN)
        panel.add(newBar)
        
        // Size difference indicator
        val diffText = JLabel(
            "Difference: ${formatSizeChange(comparison.sizeDifference)} " +
            "(${String.format("%.2f", comparison.percentageChange)}%)"
        )
        diffText.horizontalAlignment = SwingConstants.CENTER
        diffText.foreground = if (comparison.sizeDifference > 0) Color.RED else Color.GREEN
        panel.add(diffText)
        
        return panel
    }
    
    private fun createFolderChangesPanel(comparison: ApkComparisonResult): JPanel {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.border = BorderFactory.createTitledBorder("Top Folder Changes")
        
        val topChanges = comparison.folderChanges.values
            .sortedByDescending { kotlin.math.abs(it.sizeDifference) }
            .take(5)
        
        for (change in topChanges) {
            val changePanel = createFolderChangeBar(change)
            panel.add(changePanel)
        }
        
        return panel
    }
    
    private fun createFolderChangeBar(change: FolderChange): JPanel {
        val panel = JPanel(BorderLayout())
        panel.maximumSize = Dimension(Int.MAX_VALUE, 30)
        
        val label = JLabel(change.folder)
        label.preferredSize = Dimension(150, 25)
        panel.add(label, BorderLayout.WEST)
        
        val progressBar = JProgressBar(-100, 100)
        progressBar.value = change.percentageChange.toInt().coerceIn(-100, 100)
        progressBar.string = formatSizeChange(change.sizeDifference)
        progressBar.isStringPainted = true
        progressBar.foreground = if (change.sizeDifference > 0) Color.RED else Color.GREEN
        
        panel.add(progressBar, BorderLayout.CENTER)
        
        return panel
    }
    
    private fun createDistributionChart(analysis: ApkAnalysisResult): JPanel {
        val panel = JPanel(GridLayout(1, 2))
        
        // File type distribution
        val fileTypePanel = createFileTypeDistribution(analysis)
        panel.add(fileTypePanel)
        
        // Folder distribution
        val folderPanel = createFolderDistribution(analysis)
        panel.add(folderPanel)
        
        return panel
    }
    
    private fun createFileTypeDistribution(analysis: ApkAnalysisResult): JPanel {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.border = BorderFactory.createTitledBorder("File Types")
        
        val fileTypes = analysis.getFileTypeSummary().take(8)
        val colors = arrayOf(
            Color.BLUE, Color.RED, Color.GREEN, Color.ORANGE,
            Color.MAGENTA, Color.CYAN, Color.PINK, Color.YELLOW
        )
        
        fileTypes.forEachIndexed { index, fileType ->
            val bar = createProgressBar(
                fileType.extension,
                fileType.size,
                analysis.totalSize,
                colors[index % colors.size]
            )
            panel.add(bar)
        }
        
        return panel
    }
    
    private fun createFolderDistribution(analysis: ApkAnalysisResult): JPanel {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.border = BorderFactory.createTitledBorder("Folders")
        
        val folders = analysis.getFolderSummary().take(8)
        val colors = arrayOf(
            Color.DARK_GRAY, Color.BLUE, Color.RED, Color.GREEN,
            Color.ORANGE, Color.MAGENTA, Color.CYAN, Color.PINK
        )
        
        folders.forEachIndexed { index, folder ->
            val bar = createProgressBar(
                folder.path.ifEmpty { "(root)" },
                folder.size,
                analysis.totalSize,
                colors[index % colors.size]
            )
            panel.add(bar)
        }
        
        return panel
    }
    
    private fun createProgressBar(label: String, value: Long, maxValue: Long, color: Color): JPanel {
        val panel = JPanel(BorderLayout())
        panel.maximumSize = Dimension(Int.MAX_VALUE, 25)
        
        val labelComponent = JLabel(label)
        labelComponent.preferredSize = Dimension(120, 20)
        panel.add(labelComponent, BorderLayout.WEST)
        
        val progressBar = JProgressBar(0, 100)
        val percentage = if (maxValue > 0) (value * 100 / maxValue).toInt() else 0
        progressBar.value = percentage
        progressBar.string = "${service.formatFileSize(value)} ($percentage%)"
        progressBar.isStringPainted = true
        progressBar.foreground = color
        
        panel.add(progressBar, BorderLayout.CENTER)
        
        return panel
    }
    
    private fun formatSizeChange(sizeDifference: Long): String {
        return when {
            sizeDifference > 0 -> "+${service.formatFileSize(sizeDifference)}"
            sizeDifference < 0 -> service.formatFileSize(sizeDifference)
            else -> "0 B"
        }
    }
}
