package com.krishnasony.apksizeanalyzer.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.krishnasony.apksizeanalyzer.model.ApkAnalysisResult
import com.krishnasony.apksizeanalyzer.model.ApkComparisonResult
import com.krishnasony.apksizeanalyzer.parser.ApkParser
import java.io.File

/**
 * Main service for APK analysis operations
 */
@Service(Service.Level.PROJECT)
class ApkAnalyzerService(private val project: Project) {
    
    private val parser = ApkParser()
    private var lastAnalysisResult: ApkAnalysisResult? = null
    private var lastComparisonResult: ApkComparisonResult? = null
    
    /**
     * Analyzes a single APK file
     */
    fun analyzeApk(apkFile: File): ApkAnalysisResult {
        val result = parser.analyzeApk(apkFile)
        lastAnalysisResult = result
        return result
    }
    
    /**
     * Compares two APK files
     */
    fun compareApks(oldApk: File, newApk: File): ApkComparisonResult {
        val result = parser.compareApks(oldApk, newApk)
        lastComparisonResult = result
        return result
    }
    
    /**
     * Gets the current project's APK file if available
     */
    fun getCurrentProjectApk(): File? {
        // Look for APK files in common Android build output directories
        val buildDirs = listOf(
            "app/build/outputs/apk/release",
            "app/build/outputs/apk/debug",
            "build/outputs/apk/release",
            "build/outputs/apk/debug"
        )
        
        for (buildDir in buildDirs) {
            val dir = File(project.basePath, buildDir)
            if (dir.exists()) {
                val apkFiles = dir.listFiles { _, name -> name.endsWith(".apk") }
                if (apkFiles?.isNotEmpty() == true) {
                    // Return the most recently modified APK
                    return apkFiles.maxByOrNull { it.lastModified() }
                }
            }
        }
        
        return null
    }
    
    /**
     * Formats file size in human-readable format
     */
    fun formatFileSize(bytes: Long): String {
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
    
    /**
     * Gets the last analysis result
     */
    fun getLastAnalysisResult(): ApkAnalysisResult? = lastAnalysisResult
    
    /**
     * Gets the last comparison result
     */
    fun getLastComparisonResult(): ApkComparisonResult? = lastComparisonResult
    
    /**
     * Clears cached results
     */
    fun clearResults() {
        lastAnalysisResult = null
        lastComparisonResult = null
    }
}
