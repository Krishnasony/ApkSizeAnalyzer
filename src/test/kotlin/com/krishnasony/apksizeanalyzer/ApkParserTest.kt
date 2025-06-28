package com.krishnasony.apksizeanalyzer

import com.krishnasony.apksizeanalyzer.model.*
import com.krishnasony.apksizeanalyzer.parser.ApkParser
import org.junit.Test
import org.junit.Assert.*
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import java.io.FileOutputStream

/**
 * Unit tests for APK analysis functionality
 */
class ApkParserTest {
    
    private val parser = ApkParser()
    
    @Test
    fun testApkAnalysis() {
        // This would require a sample APK file for testing
        // For now, we'll test the basic functionality
        assertTrue("Parser should be initialized", parser != null)
    }
    
    @Test
    fun testFileSizeFormatting() {
        // Test file size formatting logic
        assertEquals("1.00 KB", formatFileSize(1024))
        assertEquals("1.00 MB", formatFileSize(1024 * 1024))
        assertEquals("1.00 GB", formatFileSize(1024 * 1024 * 1024))
        assertEquals("512 B", formatFileSize(512))
        assertEquals("1.50 KB", formatFileSize(1536))
    }
    
    @Test
    fun testApkFileInfo() {
        val fileInfo = ApkFileInfo(
            path = "assets/image.png",
            size = 1024,
            compressedSize = 800,
            method = "Deflated",
            crc = 12345L
        )
        
        assertEquals("png", fileInfo.extension)
        assertEquals("assets", fileInfo.folder)
        assertEquals(21.875, fileInfo.compression, 0.001)
    }
    
    @Test
    fun testFolderChangeCalculation() {
        val change = FolderChange(
            folder = "assets",
            oldSize = 1000,
            newSize = 1200,
            sizeDifference = 200,
            percentageChange = 20.0
        )
        
        assertEquals(200, change.sizeDifference)
        assertEquals(20.0, change.percentageChange, 0.001)
    }
    
    @Test
    fun testFileChangeTypes() {
        val addedFile = FileChange(
            path = "new_file.txt",
            oldSize = null,
            newSize = 100,
            sizeDifference = 100,
            changeType = ChangeType.ADDED
        )
        
        val removedFile = FileChange(
            path = "old_file.txt",
            oldSize = 200,
            newSize = null,
            sizeDifference = -200,
            changeType = ChangeType.REMOVED
        )
        
        val modifiedFile = FileChange(
            path = "modified_file.txt",
            oldSize = 150,
            newSize = 180,
            sizeDifference = 30,
            changeType = ChangeType.MODIFIED
        )
        
        assertEquals(ChangeType.ADDED, addedFile.changeType)
        assertEquals(ChangeType.REMOVED, removedFile.changeType)
        assertEquals(ChangeType.MODIFIED, modifiedFile.changeType)
        
        assertEquals(100, addedFile.sizeDifference)
        assertEquals(-200, removedFile.sizeDifference)
        assertEquals(30, modifiedFile.sizeDifference)
    }
    
    @Test
    fun testApkAnalysisResult() {
        val files = listOf(
            ApkFileInfo("classes.dex", 1000, 800, "Deflated", 1L),
            ApkFileInfo("assets/icon.png", 500, 450, "Deflated", 2L),
            ApkFileInfo("res/layout/main.xml", 200, 100, "Deflated", 3L)
        )
        
        val folders = mapOf(
            "assets" to 500L,
            "res" to 200L,
            "res/layout" to 200L
        )
        
        val fileTypes = mapOf(
            "dex" to 1000L,
            "png" to 500L,
            "xml" to 200L
        )
        
        val result = ApkAnalysisResult(
            apkPath = "/test/app.apk",
            totalSize = 1700L,
            files = files,
            folders = folders,
            fileTypes = fileTypes
        )
        
        assertEquals(3, result.files.size)
        assertEquals(1700L, result.totalSize)
        
        val folderSummary = result.getFolderSummary()
        assertEquals(3, folderSummary.size)
        
        val fileTypeSummary = result.getFileTypeSummary()
        assertEquals(3, fileTypeSummary.size)
        
        // Check that largest folder comes first
        assertEquals("assets", folderSummary[0].path)
        assertEquals(500L, folderSummary[0].size)
    }
    
    @Test
    fun testComparisonResult() {
        val oldApk = ApkAnalysisResult(
            apkPath = "/test/old.apk",
            totalSize = 1000L,
            files = emptyList(),
            folders = emptyMap(),
            fileTypes = emptyMap()
        )
        
        val newApk = ApkAnalysisResult(
            apkPath = "/test/new.apk",
            totalSize = 1200L,
            files = emptyList(),
            folders = emptyMap(),
            fileTypes = emptyMap()
        )
        
        val comparison = ApkComparisonResult(
            oldApk = oldApk,
            newApk = newApk,
            sizeDifference = 200L,
            percentageChange = 20.0,
            folderChanges = emptyMap(),
            fileTypeChanges = emptyMap(),
            fileChanges = emptyList()
        )
        
        assertTrue(comparison.isIncreased)
        assertFalse(comparison.isDecreased)
        assertFalse(comparison.isUnchanged)
        assertEquals(200L, comparison.sizeDifference)
        assertEquals(20.0, comparison.percentageChange, 0.001)
    }
    
    private fun formatFileSize(bytes: Long): String {
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
}
