package com.krishnasony.apksizeanalyzer.ui

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import com.krishnasony.apksizeanalyzer.model.*
import com.krishnasony.apksizeanalyzer.services.ApkAnalyzerService
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import javax.swing.*
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel

/**
 * Enhanced table component for displaying APK analysis results with color coding
 */
class AnalysisResultsTable(private val project: Project) : JPanel(BorderLayout()) {
    
    private val service = project.service<ApkAnalyzerService>()
    private val table = JBTable()
    
    init {
        setupTable()
        add(JBScrollPane(table), BorderLayout.CENTER)
    }
    
    private fun setupTable() {
        table.autoResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS
        table.setDefaultRenderer(Object::class.java, ColorCodedCellRenderer())
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
    }
    
    fun showFileChanges(changes: List<FileChange>) {
        val columnNames = arrayOf("File Path", "Change Type", "Old Size", "New Size", "Size Change", "Impact")
        val data = changes.map { change ->
            arrayOf(
                change.path,
                change.changeType.name,
                change.oldSize?.let { service.formatFileSize(it) } ?: "-",
                change.newSize?.let { service.formatFileSize(it) } ?: "-",
                formatSizeChange(change.sizeDifference),
                getImpactLevel(change.sizeDifference)
            )
        }.toTypedArray()
        
        table.model = DefaultTableModel(data, columnNames)
        configureColumnWidths()
    }
    
    fun showFolderAnalysis(folders: List<FolderInfo>) {
        val columnNames = arrayOf("Folder", "Size", "Percentage", "File Count", "Avg File Size")
        val data = folders.map { folder ->
            arrayOf(
                folder.path,
                service.formatFileSize(folder.size),
                "${"%.2f".format(folder.percentage)}%",
                "N/A", // Would need to calculate from files
                "N/A"  // Would need to calculate from files
            )
        }.toTypedArray()
        
        table.model = DefaultTableModel(data, columnNames)
        configureColumnWidths()
    }
    
    private fun configureColumnWidths() {
        val columnModel = table.columnModel
        if (columnModel.columnCount >= 6) {
            columnModel.getColumn(0).preferredWidth = 300 // File Path
            columnModel.getColumn(1).preferredWidth = 100 // Change Type
            columnModel.getColumn(2).preferredWidth = 80  // Old Size
            columnModel.getColumn(3).preferredWidth = 80  // New Size
            columnModel.getColumn(4).preferredWidth = 100 // Size Change
            columnModel.getColumn(5).preferredWidth = 80  // Impact
        }
    }
    
    private fun formatSizeChange(sizeDifference: Long): String {
        return when {
            sizeDifference > 0 -> "+${service.formatFileSize(sizeDifference)}"
            sizeDifference < 0 -> service.formatFileSize(sizeDifference)
            else -> "0 B"
        }
    }
    
    private fun getImpactLevel(sizeDifference: Long): String {
        return when {
            sizeDifference > 1024 * 1024 -> "HIGH"
            sizeDifference > 1024 * 100 -> "MEDIUM"
            sizeDifference > 0 -> "LOW"
            sizeDifference < -1024 * 1024 -> "HIGH"
            sizeDifference < -1024 * 100 -> "MEDIUM"
            sizeDifference < 0 -> "LOW"
            else -> "NONE"
        }
    }
    
    private inner class ColorCodedCellRenderer : DefaultTableCellRenderer() {
        override fun getTableCellRendererComponent(
            table: JTable,
            value: Any?,
            isSelected: Boolean,
            hasFocus: Boolean,
            row: Int,
            column: Int
        ): Component {
            val component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
            
            if (!isSelected && column == 4) { // Size Change column
                val sizeChangeText = value?.toString() ?: ""
                when {
                    sizeChangeText.startsWith("+") -> {
                        component.foreground = Color.RED
                        component.background = Color(255, 240, 240)
                    }
                    sizeChangeText.startsWith("-") -> {
                        component.foreground = Color(0, 120, 0)
                        component.background = Color(240, 255, 240)
                    }
                    else -> {
                        component.foreground = Color.BLACK
                        component.background = Color.WHITE
                    }
                }
            } else if (!isSelected && column == 5) { // Impact column
                val impact = value?.toString() ?: ""
                when (impact) {
                    "HIGH" -> {
                        component.foreground = Color.WHITE
                        component.background = Color.RED
                    }
                    "MEDIUM" -> {
                        component.foreground = Color.BLACK
                        component.background = Color.ORANGE
                    }
                    "LOW" -> {
                        component.foreground = Color.BLACK
                        component.background = Color.YELLOW
                    }
                    else -> {
                        component.foreground = Color.BLACK
                        component.background = Color.WHITE
                    }
                }
            } else if (!isSelected) {
                component.foreground = Color.BLACK
                component.background = Color.WHITE
            }
            
            return component
        }
    }
}
