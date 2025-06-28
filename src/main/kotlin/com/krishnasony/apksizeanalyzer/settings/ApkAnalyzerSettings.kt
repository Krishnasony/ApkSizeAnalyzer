package com.krishnasony.apksizeanalyzer.settings

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project

/**
 * Settings for APK Size Analyzer plugin
 */
@Service(Service.Level.PROJECT)
@State(
    name = "ApkSizeAnalyzerSettings",
    storages = [Storage("apkSizeAnalyzer.xml")]
)
class ApkAnalyzerSettings : PersistentStateComponent<ApkAnalyzerSettings.State> {
    
    private var myState = State()
    
    data class State(
        var autoAnalyzeAfterBuild: Boolean = true,
        var showNotifications: Boolean = true,
        var sizeThresholdMB: Int = 10,
        var maxFilesToShow: Int = 100,
        var defaultExportFormat: String = "HTML",
        var baselineApkPath: String = ""
    )
    
    override fun getState(): State = myState
    
    override fun loadState(state: State) {
        myState = state
    }
    
    companion object {
        fun getInstance(project: Project): ApkAnalyzerSettings {
            return project.service<ApkAnalyzerSettings>()
        }
    }
    
    // Convenience methods
    var autoAnalyzeAfterBuild: Boolean
        get() = myState.autoAnalyzeAfterBuild
        set(value) { myState.autoAnalyzeAfterBuild = value }
    
    var showNotifications: Boolean
        get() = myState.showNotifications
        set(value) { myState.showNotifications = value }
    
    var sizeThresholdMB: Int
        get() = myState.sizeThresholdMB
        set(value) { myState.sizeThresholdMB = value }
    
    var maxFilesToShow: Int
        get() = myState.maxFilesToShow
        set(value) { myState.maxFilesToShow = value }
    
    var defaultExportFormat: String
        get() = myState.defaultExportFormat
        set(value) { myState.defaultExportFormat = value }
    
    var baselineApkPath: String
        get() = myState.baselineApkPath
        set(value) { myState.baselineApkPath = value }
}
