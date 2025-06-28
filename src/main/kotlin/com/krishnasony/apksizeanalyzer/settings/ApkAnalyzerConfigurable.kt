package com.krishnasony.apksizeanalyzer.settings

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import javax.swing.*

/**
 * Settings UI for APK Size Analyzer plugin
 */
class ApkAnalyzerConfigurable(private val project: Project) : Configurable {
    
    private var settingsPanel: JPanel? = null
    private var autoAnalyzeCheckBox: JCheckBox? = null
    private var showNotificationsCheckBox: JCheckBox? = null
    private var sizeThresholdSpinner: JSpinner? = null
    private var maxFilesSpinner: JSpinner? = null
    private var exportFormatCombo: JComboBox<String>? = null
    private var baselineApkField: JTextField? = null
    
    override fun getDisplayName(): String = "APK Size Analyzer"
    
    override fun createComponent(): JComponent {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        
        // Auto-analyze after build
        autoAnalyzeCheckBox = JCheckBox("Automatically analyze APK after build")
        panel.add(autoAnalyzeCheckBox)
        
        // Show notifications
        showNotificationsCheckBox = JCheckBox("Show notifications for analysis results")
        panel.add(showNotificationsCheckBox)
        
        // Size threshold
        val thresholdPanel = JPanel()
        thresholdPanel.layout = BoxLayout(thresholdPanel, BoxLayout.X_AXIS)
        thresholdPanel.add(JLabel("Size increase threshold (MB): "))
        sizeThresholdSpinner = JSpinner(SpinnerNumberModel(10, 1, 1000, 1))
        thresholdPanel.add(sizeThresholdSpinner)
        panel.add(thresholdPanel)
        
        // Max files to show
        val maxFilesPanel = JPanel()
        maxFilesPanel.layout = BoxLayout(maxFilesPanel, BoxLayout.X_AXIS)
        maxFilesPanel.add(JLabel("Maximum files to display: "))
        maxFilesSpinner = JSpinner(SpinnerNumberModel(100, 10, 10000, 10))
        maxFilesPanel.add(maxFilesSpinner)
        panel.add(maxFilesPanel)
        
        // Default export format
        val exportPanel = JPanel()
        exportPanel.layout = BoxLayout(exportPanel, BoxLayout.X_AXIS)
        exportPanel.add(JLabel("Default export format: "))
        exportFormatCombo = JComboBox(arrayOf("HTML", "CSV", "JSON"))
        exportPanel.add(exportFormatCombo)
        panel.add(exportPanel)
        
        // Baseline APK path
        val baselinePanel = JPanel()
        baselinePanel.layout = BoxLayout(baselinePanel, BoxLayout.X_AXIS)
        baselinePanel.add(JLabel("Baseline APK path: "))
        baselineApkField = JTextField()
        baselinePanel.add(baselineApkField)
        val browseButton = JButton("Browse...")
        browseButton.addActionListener {
            val fileChooser = JFileChooser()
            fileChooser.fileFilter = javax.swing.filechooser.FileNameExtensionFilter("APK files", "apk")
            if (fileChooser.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION) {
                baselineApkField?.text = fileChooser.selectedFile.absolutePath
            }
        }
        baselinePanel.add(browseButton)
        panel.add(baselinePanel)
        
        settingsPanel = panel
        reset()
        
        return panel
    }
    
    override fun isModified(): Boolean {
        val settings = ApkAnalyzerSettings.getInstance(project)
        return autoAnalyzeCheckBox?.isSelected != settings.autoAnalyzeAfterBuild ||
               showNotificationsCheckBox?.isSelected != settings.showNotifications ||
               (sizeThresholdSpinner?.value as? Int) != settings.sizeThresholdMB ||
               (maxFilesSpinner?.value as? Int) != settings.maxFilesToShow ||
               exportFormatCombo?.selectedItem != settings.defaultExportFormat ||
               baselineApkField?.text != settings.baselineApkPath
    }
    
    override fun apply() {
        val settings = ApkAnalyzerSettings.getInstance(project)
        settings.autoAnalyzeAfterBuild = autoAnalyzeCheckBox?.isSelected ?: true
        settings.showNotifications = showNotificationsCheckBox?.isSelected ?: true
        settings.sizeThresholdMB = sizeThresholdSpinner?.value as? Int ?: 10
        settings.maxFilesToShow = maxFilesSpinner?.value as? Int ?: 100
        settings.defaultExportFormat = exportFormatCombo?.selectedItem as? String ?: "HTML"
        settings.baselineApkPath = baselineApkField?.text ?: ""
    }
    
    override fun reset() {
        val settings = ApkAnalyzerSettings.getInstance(project)
        autoAnalyzeCheckBox?.isSelected = settings.autoAnalyzeAfterBuild
        showNotificationsCheckBox?.isSelected = settings.showNotifications
        sizeThresholdSpinner?.value = settings.sizeThresholdMB
        maxFilesSpinner?.value = settings.maxFilesToShow
        exportFormatCombo?.selectedItem = settings.defaultExportFormat
        baselineApkField?.text = settings.baselineApkPath
    }
}
