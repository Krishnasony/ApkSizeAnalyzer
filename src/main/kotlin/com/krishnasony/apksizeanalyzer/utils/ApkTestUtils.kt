package com.krishnasony.apksizeanalyzer.utils

import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Utility class for creating sample APK files for testing purposes
 */
object ApkTestUtils {
    
    /**
     * Creates a sample APK file with various file types for testing
     */
    fun createSampleApk(outputFile: File): File {
        ZipOutputStream(FileOutputStream(outputFile)).use { zip ->
            // Add manifest
            addZipEntry(zip, "AndroidManifest.xml", createManifestContent())
            
            // Add classes.dex
            addZipEntry(zip, "classes.dex", ByteArray(1024 * 100)) // 100KB
            
            // Add resources
            addZipEntry(zip, "resources.arsc", ByteArray(1024 * 50)) // 50KB
            
            // Add assets
            addZipEntry(zip, "assets/data.json", "{\"sample\": \"data\"}".toByteArray())
            addZipEntry(zip, "assets/config.xml", "<config></config>".toByteArray())
            
            // Add libraries
            addZipEntry(zip, "lib/arm64-v8a/libnative.so", ByteArray(1024 * 200)) // 200KB
            addZipEntry(zip, "lib/armeabi-v7a/libnative.so", ByteArray(1024 * 180)) // 180KB
            
            // Add resources
            addZipEntry(zip, "res/drawable/icon.png", ByteArray(1024 * 10)) // 10KB
            addZipEntry(zip, "res/layout/activity_main.xml", createLayoutContent())
            addZipEntry(zip, "res/values/strings.xml", createStringsContent())
            
            // Add META-INF
            addZipEntry(zip, "META-INF/MANIFEST.MF", "Manifest-Version: 1.0\n".toByteArray())
            addZipEntry(zip, "META-INF/CERT.RSA", ByteArray(1024 * 2)) // 2KB
            addZipEntry(zip, "META-INF/CERT.SF", ByteArray(1024)) // 1KB
        }
        
        return outputFile
    }
    
    /**
     * Creates a larger sample APK for comparison testing
     */
    fun createLargeSampleApk(outputFile: File): File {
        ZipOutputStream(FileOutputStream(outputFile)).use { zip ->
            // Add manifest
            addZipEntry(zip, "AndroidManifest.xml", createManifestContent())
            
            // Add multiple dex files
            addZipEntry(zip, "classes.dex", ByteArray(1024 * 150)) // 150KB
            addZipEntry(zip, "classes2.dex", ByteArray(1024 * 120)) // 120KB
            addZipEntry(zip, "classes3.dex", ByteArray(1024 * 100)) // 100KB
            
            // Add resources
            addZipEntry(zip, "resources.arsc", ByteArray(1024 * 80)) // 80KB
            
            // Add more assets
            addZipEntry(zip, "assets/data.json", "{\"sample\": \"data\", \"large\": true}".toByteArray())
            addZipEntry(zip, "assets/config.xml", "<config><large>true</large></config>".toByteArray())
            addZipEntry(zip, "assets/large_file.dat", ByteArray(1024 * 500)) // 500KB
            
            // Add more libraries
            addZipEntry(zip, "lib/arm64-v8a/libnative.so", ByteArray(1024 * 300)) // 300KB
            addZipEntry(zip, "lib/armeabi-v7a/libnative.so", ByteArray(1024 * 280)) // 280KB
            addZipEntry(zip, "lib/x86/libnative.so", ByteArray(1024 * 250)) // 250KB
            addZipEntry(zip, "lib/x86_64/libnative.so", ByteArray(1024 * 270)) // 270KB
            
            // Add more resources
            addZipEntry(zip, "res/drawable/icon.png", ByteArray(1024 * 15)) // 15KB
            addZipEntry(zip, "res/drawable-hdpi/icon.png", ByteArray(1024 * 20)) // 20KB
            addZipEntry(zip, "res/drawable-xhdpi/icon.png", ByteArray(1024 * 25)) // 25KB
            addZipEntry(zip, "res/layout/activity_main.xml", createLayoutContent())
            addZipEntry(zip, "res/layout/activity_detail.xml", createLayoutContent())
            addZipEntry(zip, "res/values/strings.xml", createStringsContent())
            addZipEntry(zip, "res/values/colors.xml", createColorsContent())
            
            // Add META-INF
            addZipEntry(zip, "META-INF/MANIFEST.MF", "Manifest-Version: 1.0\n".toByteArray())
            addZipEntry(zip, "META-INF/CERT.RSA", ByteArray(1024 * 3)) // 3KB
            addZipEntry(zip, "META-INF/CERT.SF", ByteArray(1024 * 2)) // 2KB
        }
        
        return outputFile
    }
    
    private fun addZipEntry(zip: ZipOutputStream, name: String, content: ByteArray) {
        val entry = ZipEntry(name)
        zip.putNextEntry(entry)
        zip.write(content)
        zip.closeEntry()
    }
    
    private fun createManifestContent(): ByteArray {
        return """
            <?xml version="1.0" encoding="utf-8"?>
            <manifest xmlns:android="http://schemas.android.com/apk/res/android"
                package="com.example.testapp">
                <application android:label="Test App">
                    <activity android:name=".MainActivity">
                        <intent-filter>
                            <action android:name="android.intent.action.MAIN" />
                            <category android:name="android.intent.category.LAUNCHER" />
                        </intent-filter>
                    </activity>
                </application>
            </manifest>
        """.trimIndent().toByteArray()
    }
    
    private fun createLayoutContent(): ByteArray {
        return """
            <?xml version="1.0" encoding="utf-8"?>
            <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:orientation="vertical">
                
                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Hello World!" />
                    
            </LinearLayout>
        """.trimIndent().toByteArray()
    }
    
    private fun createStringsContent(): ByteArray {
        return """
            <?xml version="1.0" encoding="utf-8"?>
            <resources>
                <string name="app_name">Test App</string>
                <string name="hello_world">Hello World!</string>
            </resources>
        """.trimIndent().toByteArray()
    }
    
    private fun createColorsContent(): ByteArray {
        return """
            <?xml version="1.0" encoding="utf-8"?>
            <resources>
                <color name="primary">#6200EE</color>
                <color name="primary_dark">#3700B3</color>
                <color name="accent">#03DAC5</color>
            </resources>
        """.trimIndent().toByteArray()
    }
}
