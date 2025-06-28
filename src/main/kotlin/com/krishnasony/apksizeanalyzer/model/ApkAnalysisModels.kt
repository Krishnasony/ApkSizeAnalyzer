package com.krishnasony.apksizeanalyzer.model

/**
 * Represents the analysis result of an APK file
 */
data class ApkAnalysisResult(
    val apkPath: String,
    val totalSize: Long,
    val files: List<ApkFileInfo>,
    val folders: Map<String, Long>,
    val fileTypes: Map<String, Long>,
    val analysisTimestamp: Long = System.currentTimeMillis()
) {
    fun getFolderSummary(): List<FolderInfo> {
        return folders.map { (path, size) ->
            FolderInfo(path, size, (size.toDouble() / totalSize * 100))
        }.sortedByDescending { it.size }
    }
    
    fun getFileTypeSummary(): List<FileTypeInfo> {
        return fileTypes.map { (extension, size) ->
            FileTypeInfo(extension, size, (size.toDouble() / totalSize * 100))
        }.sortedByDescending { it.size }
    }
}

/**
 * Represents a single file within an APK
 */
data class ApkFileInfo(
    val path: String,
    val size: Long,
    val compressedSize: Long,
    val method: String,
    val crc: Long
) {
    val compression: Double
        get() = if (size > 0) ((size - compressedSize).toDouble() / size * 100) else 0.0
        
    val extension: String
        get() = path.substringAfterLast('.', "")
        
    val folder: String
        get() = path.substringBeforeLast('/', "")
}

/**
 * Represents folder-level analysis
 */
data class FolderInfo(
    val path: String,
    val size: Long,
    val percentage: Double
)

/**
 * Represents file type analysis
 */
data class FileTypeInfo(
    val extension: String,
    val size: Long,
    val percentage: Double
)

/**
 * Represents comparison between two APKs
 */
data class ApkComparisonResult(
    val oldApk: ApkAnalysisResult,
    val newApk: ApkAnalysisResult,
    val sizeDifference: Long,
    val percentageChange: Double,
    val folderChanges: Map<String, FolderChange>,
    val fileTypeChanges: Map<String, FileTypeChange>,
    val fileChanges: List<FileChange>
) {
    val isIncreased: Boolean get() = sizeDifference > 0
    val isDecreased: Boolean get() = sizeDifference < 0
    val isUnchanged: Boolean get() = sizeDifference == 0L
}

/**
 * Represents change in a folder between two APKs
 */
data class FolderChange(
    val folder: String,
    val oldSize: Long,
    val newSize: Long,
    val sizeDifference: Long,
    val percentageChange: Double
)

/**
 * Represents change in a file type between two APKs
 */
data class FileTypeChange(
    val fileType: String,
    val oldSize: Long,
    val newSize: Long,
    val sizeDifference: Long,
    val percentageChange: Double
)

/**
 * Represents change in an individual file between two APKs
 */
data class FileChange(
    val path: String,
    val oldSize: Long?,
    val newSize: Long?,
    val sizeDifference: Long,
    val changeType: ChangeType
)

enum class ChangeType {
    ADDED, REMOVED, MODIFIED, UNCHANGED
}
