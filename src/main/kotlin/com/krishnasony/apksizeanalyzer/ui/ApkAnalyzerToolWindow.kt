package com.krishnasony.apksizeanalyzer.ui

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.*
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.JBUI
import com.krishnasony.apksizeanalyzer.model.ApkAnalysisResult
import com.krishnasony.apksizeanalyzer.model.ApkComparisonResult
import com.krishnasony.apksizeanalyzer.services.ApkAnalyzerService
import java.awt.*
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.io.File
import javax.swing.*
import javax.swing.border.TitledBorder
import javax.swing.table.DefaultTableModel

/**
 * Main UI component for the APK Size Analyzer tool window
 */
class ApkAnalyzerToolWindow(private val project: Project) {
    
    private val mainPanel = JPanel(BorderLayout())
    private val service = project.service<ApkAnalyzerService>()
    
    // UI Components
    private val tabbedPane = JBTabbedPane()
    private val statusLabel = JBLabel("Ready to analyze APK files")
    private val progressBar = JProgressBar()
    
    // Component references for updating
    private lateinit var filePathLabel: JLabel
    private lateinit var fileSizeLabel: JLabel
    private lateinit var fileCountLabel: JLabel
    private lateinit var fileTable: JBTable
    private lateinit var folderTable: JBTable
    private lateinit var typeTable: JBTable
    
    private lateinit var oldApkLabel: JLabel
    private lateinit var newApkLabel: JLabel
    private lateinit var sizeDiffLabel: JLabel
    private lateinit var comparisonTable: JBTable
    private lateinit var fileChangesTable: JBTable
    private lateinit var folderChangesTable: JBTable
    private lateinit var typeChangesTable: JBTable
    
    // Analysis tabs
    private val singleAnalysisPanel = createSingleAnalysisPanel()
    private val comparisonPanel = createComparisonPanel()
    
    init {
        initializeUI()
        setupDragAndDrop()
    }
    
    private fun initializeUI() {
        // Create toolbar
        val toolbar = createToolbar()
        mainPanel.add(toolbar, BorderLayout.NORTH)
        
        // Create tabbed pane
        tabbedPane.addTab("Single Analysis", singleAnalysisPanel)
        tabbedPane.addTab("Compare APKs", comparisonPanel)
        mainPanel.add(tabbedPane, BorderLayout.CENTER)
        
        // Create status bar
        val statusPanel = JPanel(BorderLayout())
        statusPanel.border = JBUI.Borders.empty(5)
        statusPanel.add(statusLabel, BorderLayout.WEST)
        progressBar.isVisible = false
        statusPanel.add(progressBar, BorderLayout.EAST)
        mainPanel.add(statusPanel, BorderLayout.SOUTH)
        
        mainPanel.border = JBUI.Borders.empty(10)
    }
    
    private fun createToolbar(): JComponent {
        val toolbar = JPanel(FlowLayout(FlowLayout.LEFT))
        toolbar.border = JBUI.Borders.empty(5, 0)
        
        val analyzeButton = JButton("🔍 Analyze APK")
        analyzeButton.toolTipText = "Select and analyze a single APK file"
        analyzeButton.addActionListener { selectAndAnalyzeApk() }
        
        val compareButton = JButton("⚖️ Compare APKs")
        compareButton.toolTipText = "Compare two APK files"
        compareButton.addActionListener { selectAndCompareApks() }
        
        val clearButton = JButton("🗑️ Clear Results")
        clearButton.toolTipText = "Clear all analysis results"
        clearButton.addActionListener { clearResults() }
        
        toolbar.add(analyzeButton)
        toolbar.add(Box.createHorizontalStrut(10))
        toolbar.add(compareButton)
        toolbar.add(Box.createHorizontalStrut(10))
        toolbar.add(clearButton)
        
        // Add instruction label
        val instructionLabel = JLabel("   💡 Tip: Drag & drop APK files directly onto the window")
        instructionLabel.foreground = Color.GRAY
        instructionLabel.font = instructionLabel.font.deriveFont(Font.ITALIC, 10f)
        toolbar.add(Box.createHorizontalStrut(20))
        toolbar.add(instructionLabel)
        
        return toolbar
    }
    
    private fun createSingleAnalysisPanel(): JComponent {
        val panel = JPanel(BorderLayout())
        
        // Create file info panel
        val fileInfoPanel = JPanel(GridBagLayout())
        fileInfoPanel.border = BorderFactory.createTitledBorder("APK Information")
        
        val gbc = GridBagConstraints()
        gbc.insets = JBUI.insets(5)
        gbc.anchor = GridBagConstraints.WEST
        
        // File path
        gbc.gridx = 0; gbc.gridy = 0
        fileInfoPanel.add(JLabel("File Path:"), gbc)
        gbc.gridx = 1
        filePathLabel = JLabel("No file selected")
        fileInfoPanel.add(filePathLabel, gbc)
        
        // File size
        gbc.gridx = 0; gbc.gridy = 1
        fileInfoPanel.add(JLabel("Total Size:"), gbc)
        gbc.gridx = 1
        fileSizeLabel = JLabel("-")
        fileInfoPanel.add(fileSizeLabel, gbc)
        
        // File count
        gbc.gridx = 0; gbc.gridy = 2
        fileInfoPanel.add(JLabel("Files Count:"), gbc)
        gbc.gridx = 1
        fileCountLabel = JLabel("-")
        fileInfoPanel.add(fileCountLabel, gbc)
        
        panel.add(fileInfoPanel, BorderLayout.NORTH)
        
        // Create results tabbed pane
        val resultsPane = JBTabbedPane()
        
        // File breakdown table
        fileTable = createFileBreakdownTable()
        resultsPane.addTab("Files", JBScrollPane(fileTable))
        
        // Folder breakdown table
        folderTable = createFolderBreakdownTable()
        resultsPane.addTab("Folders", JBScrollPane(folderTable))
        
        // File types breakdown
        typeTable = createFileTypeTable()
        resultsPane.addTab("File Types", JBScrollPane(typeTable))
        
        panel.add(resultsPane, BorderLayout.CENTER)
        
        return panel
    }
    
    private fun createComparisonPanel(): JComponent {
        val panel = JPanel(BorderLayout())
        
        // Comparison info panel
        val infoPanel = JPanel(GridBagLayout())
        infoPanel.border = BorderFactory.createTitledBorder("Comparison Information")
        
        val gbc = GridBagConstraints()
        gbc.insets = JBUI.insets(5)
        gbc.anchor = GridBagConstraints.WEST
        
        // Old APK info
        gbc.gridx = 0; gbc.gridy = 0
        infoPanel.add(JLabel("Old APK:"), gbc)
        gbc.gridx = 1
        oldApkLabel = JLabel("No file selected")
        infoPanel.add(oldApkLabel, gbc)
        
        // New APK info
        gbc.gridx = 0; gbc.gridy = 1
        infoPanel.add(JLabel("New APK:"), gbc)
        gbc.gridx = 1
        newApkLabel = JLabel("No file selected")
        infoPanel.add(newApkLabel, gbc)
        
        // Size difference
        gbc.gridx = 0; gbc.gridy = 2
        infoPanel.add(JLabel("Size Difference:"), gbc)
        gbc.gridx = 1
        sizeDiffLabel = JLabel("-")
        infoPanel.add(sizeDiffLabel, gbc)
        
        panel.add(infoPanel, BorderLayout.NORTH)
        
        // Create comparison results tabbed pane
        val comparisonPane = JBTabbedPane()
        
        // Overall comparison table
        comparisonTable = createComparisonTable()
        comparisonPane.addTab("Overview", JBScrollPane(comparisonTable))
        
        // File changes table
        fileChangesTable = createFileChangesTable()
        comparisonPane.addTab("File Changes", JBScrollPane(fileChangesTable))
        
        // Folder changes table
        folderChangesTable = createFolderChangesTable()
        comparisonPane.addTab("Folder Changes", JBScrollPane(folderChangesTable))
        
        // File type changes table
        typeChangesTable = createTypeChangesTable()
        comparisonPane.addTab("Type Changes", JBScrollPane(typeChangesTable))
        
        panel.add(comparisonPane, BorderLayout.CENTER)
        
        return panel
    }
    
    private fun createFileBreakdownTable(): JBTable {
        val columnNames = arrayOf("File Path", "Size", "Compressed", "Compression %", "Type")
        val model = DefaultTableModel(columnNames, 0)
        val table = JBTable(model)
        
        // Set column widths
        table.columnModel.getColumn(0).preferredWidth = 300
        table.columnModel.getColumn(1).preferredWidth = 100
        table.columnModel.getColumn(2).preferredWidth = 100
        table.columnModel.getColumn(3).preferredWidth = 100
        table.columnModel.getColumn(4).preferredWidth = 80
        
        return table
    }
    
    private fun createFolderBreakdownTable(): JBTable {
        val columnNames = arrayOf("Folder Path", "Size", "Percentage", "Files Count")
        val model = DefaultTableModel(columnNames, 0)
        val table = JBTable(model)
        
        table.columnModel.getColumn(0).preferredWidth = 300
        table.columnModel.getColumn(1).preferredWidth = 100
        table.columnModel.getColumn(2).preferredWidth = 100
        table.columnModel.getColumn(3).preferredWidth = 100
        
        return table
    }
    
    private fun createFileTypeTable(): JBTable {
        val columnNames = arrayOf("File Type", "Size", "Percentage", "Files Count")
        val model = DefaultTableModel(columnNames, 0)
        val table = JBTable(model)
        
        table.columnModel.getColumn(0).preferredWidth = 150
        table.columnModel.getColumn(1).preferredWidth = 100
        table.columnModel.getColumn(2).preferredWidth = 100
        table.columnModel.getColumn(3).preferredWidth = 100
        
        return table
    }
    
    private fun createComparisonTable(): JBTable {
        val columnNames = arrayOf("Item", "Old Size", "New Size", "Difference", "Change %")
        val model = DefaultTableModel(columnNames, 0)
        val table = JBTable(model)
        
        table.columnModel.getColumn(0).preferredWidth = 200
        table.columnModel.getColumn(1).preferredWidth = 100
        table.columnModel.getColumn(2).preferredWidth = 100
        table.columnModel.getColumn(3).preferredWidth = 100
        table.columnModel.getColumn(4).preferredWidth = 100
        
        return table
    }
    
    private fun createFileChangesTable(): JBTable {
        val columnNames = arrayOf("File Path", "Status", "Old Size", "New Size", "Difference", "Change %")
        val model = DefaultTableModel(columnNames, 0)
        val table = JBTable(model)
        
        table.columnModel.getColumn(0).preferredWidth = 300
        table.columnModel.getColumn(1).preferredWidth = 80
        table.columnModel.getColumn(2).preferredWidth = 100
        table.columnModel.getColumn(3).preferredWidth = 100
        table.columnModel.getColumn(4).preferredWidth = 100
        table.columnModel.getColumn(5).preferredWidth = 100
        
        return table
    }
    
    private fun createFolderChangesTable(): JBTable {
        val columnNames = arrayOf("Folder Path", "Old Size", "New Size", "Difference", "Change %")
        val model = DefaultTableModel(columnNames, 0)
        val table = JBTable(model)
        
        table.columnModel.getColumn(0).preferredWidth = 300
        table.columnModel.getColumn(1).preferredWidth = 100
        table.columnModel.getColumn(2).preferredWidth = 100
        table.columnModel.getColumn(3).preferredWidth = 100
        table.columnModel.getColumn(4).preferredWidth = 100
        
        return table
    }
    
    private fun createTypeChangesTable(): JBTable {
        val columnNames = arrayOf("File Type", "Old Size", "New Size", "Difference", "Change %")
        val model = DefaultTableModel(columnNames, 0)
        val table = JBTable(model)
        
        table.columnModel.getColumn(0).preferredWidth = 150
        table.columnModel.getColumn(1).preferredWidth = 100
        table.columnModel.getColumn(2).preferredWidth = 100
        table.columnModel.getColumn(3).preferredWidth = 100
        table.columnModel.getColumn(4).preferredWidth = 100
        
        return table
    }
    
    private fun selectAndAnalyzeApk() {
        val descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor()
        descriptor.title = "Select APK File to Analyze"
        descriptor.description = "Choose an APK file for size analysis"
        
        // Set up file filtering for APK files
        descriptor.withFileFilter { file ->
            file.extension?.lowercase() == "apk" || file.isDirectory
        }
        
        val virtualFile = FileChooser.chooseFile(descriptor, project, null)
        if (virtualFile == null) {
            updateStatus("No APK file selected")
            return
        }
        
        val apkFile = File(virtualFile.path)
        
        // Validate the selected file
        if (!apkFile.exists()) {
            ApplicationManager.getApplication().invokeLater {
                Messages.showErrorDialog(project, "Selected file does not exist: ${apkFile.absolutePath}", "File Error")
            }
            return
        }
        
        if (!apkFile.name.lowercase().endsWith(".apk")) {
            ApplicationManager.getApplication().invokeLater {
                Messages.showErrorDialog(project, "Please select a valid APK file (.apk extension required)", "Invalid File")
            }
            return
        }
        
        updateStatus("Selected APK: ${apkFile.name} (${formatFileSize(apkFile.length())})")
        analyzeApkFile(apkFile)
    }
    
    private fun selectAndCompareApks() {
        // Create descriptor for APK files
        val descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor()
        descriptor.withFileFilter { file ->
            file.extension?.lowercase() == "apk" || file.isDirectory
        }
        
        // Select old APK
        descriptor.title = "Select Old APK File"
        descriptor.description = "Choose the baseline APK file for comparison"
        val oldVirtualFile = FileChooser.chooseFile(descriptor, project, null)
        if (oldVirtualFile == null) {
            updateStatus("No old APK file selected")
            return
        }
        
        val oldApkFile = File(oldVirtualFile.path)
        
        // Validate old APK
        if (!oldApkFile.exists() || !oldApkFile.name.lowercase().endsWith(".apk")) {
            ApplicationManager.getApplication().invokeLater {
                Messages.showErrorDialog(project, "Please select a valid APK file for the old version", "Invalid File")
            }
            return
        }
        
        // Select new APK
        descriptor.title = "Select New APK File"
        descriptor.description = "Choose the new APK file to compare against baseline"
        val newVirtualFile = FileChooser.chooseFile(descriptor, project, null)
        if (newVirtualFile == null) {
            updateStatus("No new APK file selected")
            return
        }
        
        val newApkFile = File(newVirtualFile.path)
        
        // Validate new APK
        if (!newApkFile.exists() || !newApkFile.name.lowercase().endsWith(".apk")) {
            ApplicationManager.getApplication().invokeLater {
                Messages.showErrorDialog(project, "Please select a valid APK file for the new version", "Invalid File")
            }
            return
        }
        
        updateStatus("Selected APKs: ${oldApkFile.name} vs ${newApkFile.name}")
        compareApkFiles(oldApkFile, newApkFile)
    }
    
    private fun analyzeApkFile(apkFile: File) {
        showProgress("Analyzing APK file...")
        
        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Analyzing APK", true) {
            override fun run(indicator: ProgressIndicator) {
                try {
                    indicator.text = "Reading APK file..."
                    indicator.fraction = 0.3
                    
                    val result = service.analyzeApk(apkFile)
                    
                    indicator.text = "Processing results..."
                    indicator.fraction = 0.8
                    
                    ApplicationManager.getApplication().invokeLater {
                        displaySingleAnalysisResults(result)
                        hideProgress()
                        tabbedPane.selectedIndex = 0 // Switch to single analysis tab
                    }
                    
                } catch (e: Exception) {
                    ApplicationManager.getApplication().invokeLater {
                        hideProgress()
                        Messages.showErrorDialog(project, "Failed to analyze APK: ${e.message}", "Analysis Error")
                    }
                }
            }
        })
    }
    
    private fun compareApkFiles(oldApk: File, newApk: File) {
        showProgress("Comparing APK files...")
        
        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Comparing APKs", true) {
            override fun run(indicator: ProgressIndicator) {
                try {
                    indicator.text = "Analyzing old APK..."
                    indicator.fraction = 0.2
                    
                    indicator.text = "Analyzing new APK..."
                    indicator.fraction = 0.6
                    
                    val result = service.compareApks(oldApk, newApk)
                    
                    indicator.text = "Computing differences..."
                    indicator.fraction = 0.9
                    
                    ApplicationManager.getApplication().invokeLater {
                        displayComparisonResults(result)
                        hideProgress()
                        tabbedPane.selectedIndex = 1 // Switch to comparison tab
                    }
                    
                } catch (e: Exception) {
                    ApplicationManager.getApplication().invokeLater {
                        hideProgress()
                        Messages.showErrorDialog(project, "Failed to compare APKs: ${e.message}", "Comparison Error")
                    }
                }
            }
        })
    }
    
    private fun displaySingleAnalysisResults(result: ApkAnalysisResult) {
        updateStatus("Analysis complete - ${result.files.size} files analyzed")
        
        // Update file info labels
        filePathLabel.text = result.apkPath
        fileSizeLabel.text = formatFileSize(result.totalSize)
        fileCountLabel.text = result.files.size.toString()
        
        // Update tables
        updateFileBreakdownTable(result)
        updateFolderBreakdownTable(result)
        updateFileTypeTable(result)
    }
    
    private fun displayComparisonResults(result: ApkComparisonResult) {
        updateStatus("Comparison complete")
        
        // Update comparison info labels
        oldApkLabel.text = "${result.oldApk.apkPath} (${formatFileSize(result.oldApk.totalSize)})"
        newApkLabel.text = "${result.newApk.apkPath} (${formatFileSize(result.newApk.totalSize)})"
        
        val sizeDiff = result.newApk.totalSize - result.oldApk.totalSize
        val changeText = if (sizeDiff > 0) "+${formatFileSize(sizeDiff)}" else formatFileSize(sizeDiff)
        sizeDiffLabel.text = changeText
        
        // Update all comparison tables
        updateComparisonTable(result)
        updateFileChangesTable(result)
        updateFolderChangesTable(result)
        updateTypeChangesTable(result)
    }
    
    private fun updateFileBreakdownTable(result: ApkAnalysisResult) {
        val model = fileTable.model as DefaultTableModel
        model.rowCount = 0
        
        result.files.take(100).forEach { file -> // Limit to top 100 files
            model.addRow(arrayOf(
                file.path,
                formatFileSize(file.size),
                formatFileSize(file.compressedSize),
                String.format("%.1f%%", file.compression),
                file.extension.ifEmpty { "N/A" }
            ))
        }
    }
    
    private fun updateFolderBreakdownTable(result: ApkAnalysisResult) {
        val model = folderTable.model as DefaultTableModel
        model.rowCount = 0
        
        result.getFolderSummary().forEach { folder ->
            model.addRow(arrayOf(
                folder.path.ifEmpty { "(root)" },
                formatFileSize(folder.size),
                String.format("%.1f%%", folder.percentage),
                "N/A" // File count would need to be calculated
            ))
        }
    }
    
    private fun updateFileTypeTable(result: ApkAnalysisResult) {
        val model = typeTable.model as DefaultTableModel
        model.rowCount = 0
        
        result.getFileTypeSummary().forEach { type ->
            model.addRow(arrayOf(
                type.extension.ifEmpty { "(no extension)" },
                formatFileSize(type.size),
                String.format("%.1f%%", type.percentage),
                "N/A" // File count would need to be calculated
            ))
        }
    }
    
    private fun updateComparisonTable(result: ApkComparisonResult) {
        val model = comparisonTable.model as DefaultTableModel
        model.rowCount = 0
        
        // Add overall comparison
        val sizeDiff = result.newApk.totalSize - result.oldApk.totalSize
        val changePercent = if (result.oldApk.totalSize > 0) {
            (sizeDiff.toDouble() / result.oldApk.totalSize * 100)
        } else 0.0
        
        model.addRow(arrayOf(
            "Total APK Size",
            formatFileSize(result.oldApk.totalSize),
            formatFileSize(result.newApk.totalSize),
            formatFileSize(sizeDiff),
            String.format("%.1f%%", changePercent)
        ))
        
        // Add file count comparison
        val fileCountDiff = result.newApk.files.size - result.oldApk.files.size
        val fileCountPercent = if (result.oldApk.files.size > 0) {
            (fileCountDiff.toDouble() / result.oldApk.files.size * 100)
        } else 0.0
        
        model.addRow(arrayOf(
            "Total Files",
            result.oldApk.files.size.toString(),
            result.newApk.files.size.toString(),
            if (fileCountDiff > 0) "+$fileCountDiff" else fileCountDiff.toString(),
            String.format("%.1f%%", fileCountPercent)
        ))
        
        // Add summary of changes
        val addedFiles = result.fileChanges.count { it.changeType == com.krishnasony.apksizeanalyzer.model.ChangeType.ADDED }
        val removedFiles = result.fileChanges.count { it.changeType == com.krishnasony.apksizeanalyzer.model.ChangeType.REMOVED }
        val modifiedFiles = result.fileChanges.count { it.changeType == com.krishnasony.apksizeanalyzer.model.ChangeType.MODIFIED }
        
        model.addRow(arrayOf(
            "Files Added",
            "0",
            addedFiles.toString(),
            "+$addedFiles",
            "-"
        ))
        
        model.addRow(arrayOf(
            "Files Removed",
            removedFiles.toString(),
            "0",
            "-$removedFiles",
            "-"
        ))
        
        model.addRow(arrayOf(
            "Files Modified",
            modifiedFiles.toString(),
            modifiedFiles.toString(),
            "0",
            "-"
        ))
    }
    
    private fun updateFileChangesTable(result: ApkComparisonResult) {
        val model = fileChangesTable.model as DefaultTableModel
        model.rowCount = 0
        
        // Show top 100 most significant changes
        result.fileChanges.take(100).forEach { change ->
            val oldSizeStr = change.oldSize?.let { formatFileSize(it) } ?: "-"
            val newSizeStr = change.newSize?.let { formatFileSize(it) } ?: "-"
            val changePercent = if (change.oldSize != null && change.oldSize > 0) {
                (change.sizeDifference.toDouble() / change.oldSize * 100)
            } else 0.0
            
            val status = when (change.changeType) {
                com.krishnasony.apksizeanalyzer.model.ChangeType.ADDED -> "➕ Added"
                com.krishnasony.apksizeanalyzer.model.ChangeType.REMOVED -> "➖ Removed"
                com.krishnasony.apksizeanalyzer.model.ChangeType.MODIFIED -> "📝 Modified"
                com.krishnasony.apksizeanalyzer.model.ChangeType.UNCHANGED -> "✓ Unchanged"
            }
            
            model.addRow(arrayOf(
                change.path,
                status,
                oldSizeStr,
                newSizeStr,
                formatFileSize(change.sizeDifference),
                String.format("%.1f%%", changePercent)
            ))
        }
    }
    
    private fun updateFolderChangesTable(result: ApkComparisonResult) {
        val model = folderChangesTable.model as DefaultTableModel
        model.rowCount = 0
        
        result.folderChanges.values.sortedByDescending { kotlin.math.abs(it.sizeDifference) }.forEach { change ->
            model.addRow(arrayOf(
                change.folder.ifEmpty { "(root)" },
                formatFileSize(change.oldSize),
                formatFileSize(change.newSize),
                formatFileSize(change.sizeDifference),
                String.format("%.1f%%", change.percentageChange)
            ))
        }
    }
    
    private fun updateTypeChangesTable(result: ApkComparisonResult) {
        val model = typeChangesTable.model as DefaultTableModel
        model.rowCount = 0
        
        result.fileTypeChanges.values.sortedByDescending { kotlin.math.abs(it.sizeDifference) }.forEach { change ->
            model.addRow(arrayOf(
                change.fileType.ifEmpty { "(no extension)" },
                formatFileSize(change.oldSize),
                formatFileSize(change.newSize),
                formatFileSize(change.sizeDifference),
                String.format("%.1f%%", change.percentageChange)
            ))
        }
    }
    
    private fun clearResults() {
        // Clear file info labels
        filePathLabel.text = "No file selected"
        fileSizeLabel.text = "-"
        fileCountLabel.text = "-"
        
        // Clear comparison labels
        oldApkLabel.text = "No file selected"
        newApkLabel.text = "No file selected"
        sizeDiffLabel.text = "-"
        
        // Clear all tables
        (fileTable.model as DefaultTableModel).rowCount = 0
        (folderTable.model as DefaultTableModel).rowCount = 0
        (typeTable.model as DefaultTableModel).rowCount = 0
        (comparisonTable.model as DefaultTableModel).rowCount = 0
        (fileChangesTable.model as DefaultTableModel).rowCount = 0
        (folderChangesTable.model as DefaultTableModel).rowCount = 0
        (typeChangesTable.model as DefaultTableModel).rowCount = 0
        
        updateStatus("Ready to analyze APK files")
    }
    
    private fun showProgress(message: String) {
        updateStatus(message)
        progressBar.isVisible = true
        progressBar.isIndeterminate = true
    }
    
    private fun hideProgress() {
        progressBar.isVisible = false
        progressBar.isIndeterminate = false
    }
    
    private fun updateStatus(message: String) {
        statusLabel.text = message
    }
    
    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes >= 1024 * 1024 * 1024 -> String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0))
            bytes >= 1024 * 1024 -> String.format("%.2f MB", bytes / (1024.0 * 1024.0))
            bytes >= 1024 -> String.format("%.2f KB", bytes / 1024.0)
            else -> "$bytes B"
        }
    }
    
    private fun setupDragAndDrop() {
        // Setup drag and drop for the main panel
        val dropTarget = object : DropTarget() {
            override fun drop(dtde: DropTargetDropEvent) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY)
                    val droppedFiles = dtde.transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<File>
                    
                    val apkFiles = droppedFiles.filter { it.extension.lowercase() == "apk" }
                    
                    when (apkFiles.size) {
                        0 -> {
                            updateStatus("No APK files found in dropped items")
                            Messages.showWarningDialog(project, "Please drop APK files (.apk extension)", "No APK Files")
                        }
                        1 -> {
                            updateStatus("Dropped APK: ${apkFiles[0].name}")
                            analyzeApkFile(apkFiles[0])
                            tabbedPane.selectedIndex = 0 // Switch to single analysis tab
                        }
                        2 -> {
                            updateStatus("Dropped 2 APKs for comparison: ${apkFiles[0].name} vs ${apkFiles[1].name}")
                            compareApkFiles(apkFiles[0], apkFiles[1])
                            tabbedPane.selectedIndex = 1 // Switch to comparison tab
                        }
                        else -> {
                            updateStatus("Multiple APK files dropped - using first two for comparison")
                            compareApkFiles(apkFiles[0], apkFiles[1])
                            tabbedPane.selectedIndex = 1 // Switch to comparison tab
                        }
                    }
                    
                    dtde.dropComplete(true)
                } catch (e: Exception) {
                    updateStatus("Error processing dropped files: ${e.message}")
                    dtde.dropComplete(false)
                }
            }
            
            override fun dragOver(dtde: DropTargetDragEvent) {
                if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    dtde.acceptDrag(DnDConstants.ACTION_COPY)
                } else {
                    dtde.rejectDrag()
                }
            }
        }
        
        mainPanel.dropTarget = dropTarget
        
        // Add visual feedback for drag and drop
        val dragLabel = JLabel("<html><center>📱 Drop APK files here<br/>or use the buttons above</center></html>")
        dragLabel.horizontalAlignment = SwingConstants.CENTER
        dragLabel.foreground = Color.GRAY
        dragLabel.font = dragLabel.font.deriveFont(Font.ITALIC, 12f)
        
        // Add the drag label to the center when no content is shown
        if (mainPanel.componentCount < 3) {
            val centerPanel = JPanel(BorderLayout())
            centerPanel.add(dragLabel, BorderLayout.CENTER)
            mainPanel.add(centerPanel, BorderLayout.CENTER)
        }
    }
    
    fun getContent(): JComponent {
        return mainPanel
    }
}
