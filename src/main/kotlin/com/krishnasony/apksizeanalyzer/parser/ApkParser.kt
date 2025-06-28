package com.krishnasony.apksizeanalyzer.parser

import com.krishnasony.apksizeanalyzer.model.*
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream
import java.io.File
import java.io.FileInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * APK parser for analyzing APK file contents and sizes
 */
class ApkParser {
    
    /**
     * Analyzes an APK file and returns detailed analysis results
     */
    fun analyzeApk(apkFile: File): ApkAnalysisResult {
        val files = mutableListOf<ApkFileInfo>()
        val folders = mutableMapOf<String, Long>()
        val fileTypes = mutableMapOf<String, Long>()
        var totalSize = 0L
        
        ZipFile(apkFile).use { zipFile ->
            zipFile.entries().asSequence().forEach { entry ->
                if (!entry.isDirectory) {
                    val fileInfo = ApkFileInfo(
                        path = entry.name,
                        size = entry.size,
                        compressedSize = entry.compressedSize,
                        method = getCompressionMethod(entry.method),
                        crc = entry.crc
                    )
                    
                    files.add(fileInfo)
                    totalSize += entry.size
                    
                    // Accumulate folder sizes
                    val folder = fileInfo.folder
                    if (folder.isNotEmpty()) {
                        folders[folder] = folders.getOrDefault(folder, 0L) + entry.size
                        
                        // Also count parent folders
                        var parentFolder = folder
                        while (parentFolder.contains('/')) {
                            parentFolder = parentFolder.substringBeforeLast('/')
                            folders[parentFolder] = folders.getOrDefault(parentFolder, 0L) + entry.size
                        }
                    }
                    
                    // Accumulate file type sizes
                    val extension = fileInfo.extension.lowercase()
                    if (extension.isNotEmpty()) {
                        fileTypes[extension] = fileTypes.getOrDefault(extension, 0L) + entry.size
                    } else {
                        fileTypes["(no extension)"] = fileTypes.getOrDefault("(no extension)", 0L) + entry.size
                    }
                }
            }
        }
        
        return ApkAnalysisResult(
            apkPath = apkFile.absolutePath,
            totalSize = totalSize,
            files = files.sortedByDescending { it.size },
            folders = folders,
            fileTypes = fileTypes
        )
    }
    
    /**
     * Compares two APK files and returns comparison results
     */
    fun compareApks(oldApk: File, newApk: File): ApkComparisonResult {
        val oldAnalysis = analyzeApk(oldApk)
        val newAnalysis = analyzeApk(newApk)
        
        val sizeDifference = newAnalysis.totalSize - oldAnalysis.totalSize
        val percentageChange = if (oldAnalysis.totalSize > 0) {
            (sizeDifference.toDouble() / oldAnalysis.totalSize * 100)
        } else 0.0
        
        val folderChanges = calculateFolderChanges(oldAnalysis, newAnalysis)
        val fileTypeChanges = calculateFileTypeChanges(oldAnalysis, newAnalysis)
        val fileChanges = calculateFileChanges(oldAnalysis, newAnalysis)
        
        return ApkComparisonResult(
            oldApk = oldAnalysis,
            newApk = newAnalysis,
            sizeDifference = sizeDifference,
            percentageChange = percentageChange,
            folderChanges = folderChanges,
            fileTypeChanges = fileTypeChanges,
            fileChanges = fileChanges
        )
    }
    
    private fun calculateFolderChanges(
        oldAnalysis: ApkAnalysisResult, 
        newAnalysis: ApkAnalysisResult
    ): Map<String, FolderChange> {
        val changes = mutableMapOf<String, FolderChange>()
        val allFolders = (oldAnalysis.folders.keys + newAnalysis.folders.keys).toSet()
        
        allFolders.forEach { folder ->
            val oldSize = oldAnalysis.folders[folder] ?: 0L
            val newSize = newAnalysis.folders[folder] ?: 0L
            val difference = newSize - oldSize
            val percentageChange = if (oldSize > 0) {
                (difference.toDouble() / oldSize * 100)
            } else if (newSize > 0) 100.0 else 0.0
            
            if (difference != 0L) {
                changes[folder] = FolderChange(
                    folder = folder,
                    oldSize = oldSize,
                    newSize = newSize,
                    sizeDifference = difference,
                    percentageChange = percentageChange
                )
            }
        }
        
        return changes
    }
    
    private fun calculateFileTypeChanges(
        oldAnalysis: ApkAnalysisResult, 
        newAnalysis: ApkAnalysisResult
    ): Map<String, FileTypeChange> {
        val changes = mutableMapOf<String, FileTypeChange>()
        val allTypes = (oldAnalysis.fileTypes.keys + newAnalysis.fileTypes.keys).toSet()
        
        allTypes.forEach { type ->
            val oldSize = oldAnalysis.fileTypes[type] ?: 0L
            val newSize = newAnalysis.fileTypes[type] ?: 0L
            val difference = newSize - oldSize
            val percentageChange = if (oldSize > 0) {
                (difference.toDouble() / oldSize * 100)
            } else if (newSize > 0) 100.0 else 0.0
            
            if (difference != 0L) {
                changes[type] = FileTypeChange(
                    fileType = type,
                    oldSize = oldSize,
                    newSize = newSize,
                    sizeDifference = difference,
                    percentageChange = percentageChange
                )
            }
        }
        
        return changes
    }
    
    private fun calculateFileChanges(
        oldAnalysis: ApkAnalysisResult, 
        newAnalysis: ApkAnalysisResult
    ): List<FileChange> {
        val changes = mutableListOf<FileChange>()
        val oldFileMap = oldAnalysis.files.associateBy { it.path }
        val newFileMap = newAnalysis.files.associateBy { it.path }
        val allFiles = (oldFileMap.keys + newFileMap.keys).toSet()
        
        allFiles.forEach { path ->
            val oldFile = oldFileMap[path]
            val newFile = newFileMap[path]
            
            when {
                oldFile == null && newFile != null -> {
                    // File added
                    changes.add(FileChange(
                        path = path,
                        oldSize = null,
                        newSize = newFile.size,
                        sizeDifference = newFile.size,
                        changeType = ChangeType.ADDED
                    ))
                }
                oldFile != null && newFile == null -> {
                    // File removed
                    changes.add(FileChange(
                        path = path,
                        oldSize = oldFile.size,
                        newSize = null,
                        sizeDifference = -oldFile.size,
                        changeType = ChangeType.REMOVED
                    ))
                }
                oldFile != null && newFile != null -> {
                    val difference = newFile.size - oldFile.size
                    if (difference != 0L) {
                        // File modified
                        changes.add(FileChange(
                            path = path,
                            oldSize = oldFile.size,
                            newSize = newFile.size,
                            sizeDifference = difference,
                            changeType = ChangeType.MODIFIED
                        ))
                    }
                }
            }
        }
        
        return changes.sortedByDescending { kotlin.math.abs(it.sizeDifference) }
    }
    
    private fun getCompressionMethod(method: Int): String {
        return when (method) {
            ZipEntry.STORED -> "Stored"
            ZipEntry.DEFLATED -> "Deflated"
            else -> "Unknown ($method)"
        }
    }
}
