package com.itsol.vn.wallpaper.live.parallax.parallaxWallpaper

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.NumberFormat


class Utils private constructor() {


    val REQUEST_CHANGE_SURFACE = 112
    private val SPACE_KB = 1024.0
    private val SPACE_MB = 1024 * SPACE_KB
    private val SPACE_GB = 1024 * SPACE_MB
    private val SPACE_TB = 1024 * SPACE_GB

    companion object {
        private const val PREF_FILE: String = "file_pref"
        private var INSTANCE: Utils? = null
        fun getInstance(): Utils {
            if (INSTANCE == null) {
                INSTANCE = Utils()
            }
            return INSTANCE!!
        }
    }

    fun getBetweenStrings(text: String, textFrom: String, textTo: String?): String? {
        var result = text.substring(
            text.indexOf(textFrom) + textFrom.length
        )

        // Cut the excessive ending of the text:
        result = result.substring(0, result.indexOf(textTo!!))
        return result
    }
    fun uriToFile(context: Context, uri: Uri?, fileName: String?): File {
        val file = File(context.cacheDir, fileName)
        try {
            context.contentResolver.openInputStream(uri!!).use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    val buffer = ByteArray(1024)
                    var length: Int
                    while ((inputStream!!.read(buffer).also { length = it }) > 0) {
                        outputStream.write(buffer, 0, length)
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return file
    }
    fun bytes2String(sizeInBytes: Long): String? {
        val nf: NumberFormat = DecimalFormat()
        nf.maximumFractionDigits = 1
        nf.minimumFractionDigits = 1
        return try {
            if (sizeInBytes < SPACE_KB) {
                nf.format(sizeInBytes) + " Byte(s)"
            } else if (sizeInBytes < SPACE_MB) {
                nf.format(sizeInBytes / SPACE_KB) + " KB"
            } else if (sizeInBytes < SPACE_GB) {
                nf.format(sizeInBytes / SPACE_MB) + " MB"
            } else if (sizeInBytes < SPACE_TB) {
                nf.format(sizeInBytes / SPACE_GB) + " GB"
            } else {
                nf.format(sizeInBytes / SPACE_TB) + " TB"
            }
        } catch (e: Exception) {
            "$sizeInBytes Byte(s)"
        }
    }

    fun getFileUrlFromExternalDownload(context: Context, name: String): Uri? {
        var uriFile: Uri? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
            val storageVolumes = storageManager.storageVolumes
            val storageVolume = storageVolumes[0] // Lấy ổ đĩa mong muốn
            val file = File((storageVolume.directory?.path ?: "") + "/Download/" + name + ".mp4")

            if (file.exists()) {
                uriFile =
                    FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            } else {
                null
            }
        } else {
            val downloadDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadDir, "$name.mp4")
            if (file.exists()) {
                uriFile =
                    FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            } else {
                null
            }
        }
        return uriFile
    }

    fun checkFileIsExitExternalDownload(context: Context, name: String): Boolean {
        val isExits: Boolean
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val storaManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
            val storageVolums = storaManager.storageVolumes
            val storageVolume = storageVolums[0]
            val file = File(
                (storageVolume.directory?.path
                    ?: "") + "/Download/" + name + ".mp4"
            )
            isExits = file.exists()
        } else {
            // For Android 9 and below, use Environment.getExternalStoragePublicDirectory()
            val downloadDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadDir, "$name.mp4")
            isExits = file.exists()
        }
        return isExits
    }

    fun checkFileSize(context: Context, name: String) {
        val isExits: Int
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val storaManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
            val storageVolums = storaManager.storageVolumes
            val storageVolume = storageVolums[0]
            val file = File(
                (storageVolume.directory?.path
                    ?: "") + "/Download/" + name + ".mp4"
            )
            isExits = file.length().toInt()
            if (isExits == 0) {
                file.delete()
            }
        } else {
            // For Android 9 and below, use Environment.getExternalStoragePublicDirectory()
            val downloadDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadDir, "$name.mp4")
            isExits = file.length().toInt()
            if (isExits == 0) {
                file.delete()
            }
        }

    }




}