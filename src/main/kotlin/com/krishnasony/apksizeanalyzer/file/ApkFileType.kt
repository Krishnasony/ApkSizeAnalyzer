package com.krishnasony.apksizeanalyzer.file

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.Icon

/**
 * File type for APK files
 */
class ApkFileType : FileType {
    companion object {
        val INSTANCE = ApkFileType()
    }
    
    override fun getName(): String = "APK"
    
    override fun getDescription(): String = "Android Application Package"
    
    override fun getDefaultExtension(): String = "apk"
    
    override fun getIcon(): Icon? = null // You could add an APK icon here
    
    override fun isBinary(): Boolean = true
    
    override fun isReadOnly(): Boolean = true
    
    override fun getCharset(file: VirtualFile, content: ByteArray): String? = null
}
