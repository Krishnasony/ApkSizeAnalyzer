package com.krishnasony.apksizeanalyzer.toolwindow

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.table.JBTable
import com.krishnasony.apksizeanalyzer.model.*
import com.krishnasony.apksizeanalyzer.services.ApkAnalyzerService
import com.krishnasony.apksizeanalyzer.ui.AnalysisResultsTable
import com.krishnasony.apksizeanalyzer.ui.ApkSizeChart
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*
import javax.swing.table.DefaultTableModel

/**
 * Main tool window for displaying APK analysis results
 */
class ApkAnalyzerToolWindow(private val project: Project) {
    
    private val service = project.service<ApkAnalyzerService>()
    private val panel = JPanel(BorderLayout())
    private val tabbedPane = JBTabbedPane()
    private val analysisTable = AnalysisResultsTable(project)
    private val chartComponent = ApkSizeChart(project)
    
    init {
        setupUI()
        refreshContent()
    }
    
    private fun setupUI() {
        panel.add(tabbedPane, BorderLayout.CENTER)
        
        // Add toolbar with refresh and export buttons
        val toolbar = JPanel()
        val refreshButton = JButton("Refresh")
        refreshButton.addActionListener { refreshContent() }
        val exportButton = JButton("Export Results")
        exportButton.addActionListener { exportResults() }
        toolbar.add(refreshButton)
        toolbar.add(exportButton)
        panel.add(toolbar, BorderLayout.NORTH)
        
        panel.preferredSize = Dimension(900, 700)
    }
    
    fun getContent(): JComponent = panel
    
    private fun refreshContent() {
        tabbedPane.removeAll()
        
        val analysisResult = service.getLastAnalysisResult()
        val comparisonResult = service.getLastComparisonResult()
        
        when {
            comparisonResult != null -> showComparisonResults(comparisonResult)
            analysisResult != null -> showAnalysisResults(analysisResult)
            else -> showWelcomeMessage()
        }
        
        tabbedPane.revalidate()
        tabbedPane.repaint()
    }
    
    private fun showWelcomeMessage() {
        val welcomePanel = JPanel(BorderLayout())
        val message = JTextArea(
            "Welcome to APK Size Analyzer!\n\n" +
            "To get started:\n" +
            "• Use Tools → APK Size Analyzer → Compare APK Files to compare two APKs\n" +
            "• Use Tools → APK Size Analyzer → Analyze Current APK to analyze your project's APK\n" +
            "• Right-click any APK file in the project view and select 'Analyze APK Size'"
        )
        message.isEditable = false
        message.background = welcomePanel.background
        welcomePanel.add(message, BorderLayout.CENTER)
        
        tabbedPane.addTab("Welcome", welcomePanel)
    }
    
    private fun showAnalysisResults(result: ApkAnalysisResult) {
        // Overview with charts
        val overviewPanel = JPanel(BorderLayout())
        val textOverview = createOverviewPanel(result)
        chartComponent.showSizeDistribution(result)
        
        val splitPane = JSplitPane(JSplitPane.VERTICAL_SPLIT, textOverview, chartComponent)
        splitPane.dividerLocation = 150
        overviewPanel.add(splitPane, BorderLayout.CENTER)
        tabbedPane.addTab("Overview", overviewPanel)
        
        // Enhanced Folders tab
        analysisTable.showFolderAnalysis(result.getFolderSummary())
        val foldersPanel = JPanel(BorderLayout())
        foldersPanel.add(analysisTable, BorderLayout.CENTER)
        tabbedPane.addTab("Folders", foldersPanel)
        
        // File Types tab
        val fileTypesPanel = createFileTypesPanel(result.getFileTypeSummary())
        tabbedPane.addTab("File Types", fileTypesPanel)
        
        // Files tab
        val filesPanel = createFilesPanel(result.files)
        tabbedPane.addTab("Files", filesPanel)
    }
    
    private fun showComparisonResults(result: ApkComparisonResult) {
        // Comparison Overview with Charts
        val overviewPanel = JPanel(BorderLayout())
        val textOverview = createComparisonOverviewPanel(result)
        chartComponent.showComparisonChart(result)
        
        val splitPane = JSplitPane(JSplitPane.VERTICAL_SPLIT, textOverview, chartComponent)
        splitPane.dividerLocation = 200
        overviewPanel.add(splitPane, BorderLayout.CENTER)
        tabbedPane.addTab("Overview", overviewPanel)
        
        // Enhanced File Changes tab
        analysisTable.showFileChanges(result.fileChanges)
        tabbedPane.addTab("File Changes", analysisTable)
        
        // Folder Changes tab
        val folderChangesPanel = createFolderChangesPanel(result.folderChanges.values.toList())
        tabbedPane.addTab("Folder Changes", folderChangesPanel)
        
        // File Type Changes tab
        val fileTypeChangesPanel = createFileTypeChangesPanel(result.fileTypeChanges.values.toList())
        tabbedPane.addTab("File Type Changes", fileTypeChangesPanel)
    }
    
    private fun createOverviewPanel(result: ApkAnalysisResult): JPanel {
        val panel = JPanel(BorderLayout())
        val infoArea = JTextArea()
        infoArea.isEditable = false
        infoArea.text = """
            APK: ${result.apkPath}
            Total Size: ${service.formatFileSize(result.totalSize)}
            Total Files: ${result.files.size}
            Total Folders: ${result.folders.size}
            File Types: ${result.fileTypes.size}
        """.trimIndent()
        
        panel.add(JBScrollPane(infoArea), BorderLayout.CENTER)
        return panel
    }
    
    private fun createComparisonOverviewPanel(result: ApkComparisonResult): JPanel {
        val panel = JPanel(BorderLayout())
        val infoArea = JTextArea()
        infoArea.isEditable = false
        
        val sizeChangeText = when {
            result.isIncreased -> "increased by"
            result.isDecreased -> "decreased by"
            else -> "unchanged"
        }
        
        infoArea.text = """
            Old APK: ${result.oldApk.apkPath}
            New APK: ${result.newApk.apkPath}
            
            Size Comparison:
            Old Size: ${service.formatFileSize(result.oldApk.totalSize)}
            New Size: ${service.formatFileSize(result.newApk.totalSize)}
            
            The APK size has $sizeChangeText ${service.formatFileSize(kotlin.math.abs(result.sizeDifference))}
            Percentage Change: ${"%.2f".format(result.percentageChange)}%
            
            Changes Summary:
            Folders Changed: ${result.folderChanges.size}
            File Types Changed: ${result.fileTypeChanges.size}
            Files Changed: ${result.fileChanges.size}
        """.trimIndent()
        
        panel.add(JBScrollPane(infoArea), BorderLayout.CENTER)
        return panel
    }
    
    private fun createFoldersPanel(folders: List<FolderInfo>): JPanel {
        val panel = JPanel(BorderLayout())
        val columnNames = arrayOf("Folder", "Size", "Percentage")
        val data = folders.map { arrayOf(it.path, service.formatFileSize(it.size), "${"%.2f".format(it.percentage)}%") }.toTypedArray()
        val table = JBTable(DefaultTableModel(data, columnNames))
        panel.add(JBScrollPane(table), BorderLayout.CENTER)
        return panel
    }
    
    private fun createFileTypesPanel(fileTypes: List<FileTypeInfo>): JPanel {
        val panel = JPanel(BorderLayout())
        val columnNames = arrayOf("File Type", "Size", "Percentage")
        val data = fileTypes.map { arrayOf(it.extension, service.formatFileSize(it.size), "${"%.2f".format(it.percentage)}%") }.toTypedArray()
        val table = JBTable(DefaultTableModel(data, columnNames))
        panel.add(JBScrollPane(table), BorderLayout.CENTER)
        return panel
    }
    
    private fun createFilesPanel(files: List<ApkFileInfo>): JPanel {
        val panel = JPanel(BorderLayout())
        val columnNames = arrayOf("File Path", "Size", "Compressed Size", "Compression", "Method")
        val data = files.map { 
            arrayOf(
                it.path, 
                service.formatFileSize(it.size),
                service.formatFileSize(it.compressedSize),
                "${"%.1f".format(it.compression)}%",
                it.method
            ) 
        }.toTypedArray()
        val table = JBTable(DefaultTableModel(data, columnNames))
        panel.add(JBScrollPane(table), BorderLayout.CENTER)
        return panel
    }
    
    private fun createFolderChangesPanel(changes: List<FolderChange>): JPanel {
        val panel = JPanel(BorderLayout())
        val columnNames = arrayOf("Folder", "Old Size", "New Size", "Change", "Percentage")
        val data = changes.map { 
            arrayOf(
                it.folder,
                service.formatFileSize(it.oldSize),
                service.formatFileSize(it.newSize),
                "${if (it.sizeDifference > 0) "+" else ""}${service.formatFileSize(it.sizeDifference)}",
                "${"%.2f".format(it.percentageChange)}%"
            ) 
        }.toTypedArray()
        val table = JBTable(DefaultTableModel(data, columnNames))
        panel.add(JBScrollPane(table), BorderLayout.CENTER)
        return panel
    }
    
    private fun createFileTypeChangesPanel(changes: List<FileTypeChange>): JPanel {
        val panel = JPanel(BorderLayout())
        val columnNames = arrayOf("File Type", "Old Size", "New Size", "Change", "Percentage")
        val data = changes.map { 
            arrayOf(
                it.fileType,
                service.formatFileSize(it.oldSize),
                service.formatFileSize(it.newSize),
                "${if (it.sizeDifference > 0) "+" else ""}${service.formatFileSize(it.sizeDifference)}",
                "${"%.2f".format(it.percentageChange)}%"
            ) 
        }.toTypedArray()
        val table = JBTable(DefaultTableModel(data, columnNames))
        panel.add(JBScrollPane(table), BorderLayout.CENTER)
        return panel
    }
    
    private fun createFileChangesPanel(changes: List<FileChange>): JPanel {
        val panel = JPanel(BorderLayout())
        val columnNames = arrayOf("File Path", "Change Type", "Old Size", "New Size", "Size Change")
        val data = changes.map { 
            arrayOf(
                it.path,
                it.changeType.name,
                it.oldSize?.let { service.formatFileSize(it) } ?: "-",
                it.newSize?.let { service.formatFileSize(it) } ?: "-",
                "${if (it.sizeDifference > 0) "+" else ""}${service.formatFileSize(it.sizeDifference)}"
            ) 
        }.toTypedArray()
        val table = JBTable(DefaultTableModel(data, columnNames))
        panel.add(JBScrollPane(table), BorderLayout.CENTER)
        return panel
    }
    
    private fun exportResults() {
        // Trigger the export action programmatically
        val actionManager = com.intellij.openapi.actionSystem.ActionManager.getInstance()
        val action = actionManager.getAction("ApkSizeAnalyzer.ExportAnalysis")
        if (action != null) {
            val event = com.intellij.openapi.actionSystem.AnActionEvent.createFromAnAction(
                action, null, "", com.intellij.openapi.actionSystem.impl.SimpleDataContext.getProjectContext(project)
            )
            action.actionPerformed(event)
        }
    }
}
