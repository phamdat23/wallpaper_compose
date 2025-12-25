import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.RECEIVER_EXPORTED
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.coroutines.resume

object DownloadImageAndSaveStorage {

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    suspend fun downloadImageAndSaveToStorage(
        context: Context,
        imageUrl: String
    ): String? = withContext(Dispatchers.IO) {
        Log.e("AAAAAAAAAAAAAA", "downloadImageAndSaveToStorage: ${imageUrl}", )
        try {
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val uri = Uri.parse(imageUrl.replace(" ", "%20"))

            // Create a request for DownloadManager
            val request = DownloadManager.Request(uri).apply {
                setTitle("Downloading Image")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                setDestinationInExternalFilesDir(
                    context,
                    "",
                    "Wallpaper_video_${imageUrl.substringAfterLast("/")}"
                )
            }

            // Enqueue the download request and get the download ID
            val downloadId = downloadManager.enqueue(request)

            // Wait for the download to complete
            return@withContext suspendCancellableCoroutine { continuation ->
                val onCompleteReceiver = object : BroadcastReceiver() {
                    @SuppressLint("Range")
                    override fun onReceive(ctxt: Context?, intent: Intent?) {
                        val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                        if (downloadId == id) {
                            val query = DownloadManager.Query().setFilterById(downloadId)
                            val cursor = downloadManager.query(query)
                            if (cursor.moveToFirst()) {
                                val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                                if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(columnIndex)) {
                                    val uriString = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                                    Log.e("AAAAAAAAAA", "onReceive: uriString:${uriString}", )
                                    continuation.resume(uriString) // Notify completion with saved image URI
                                } else {
                                    Log.e("AAAAAAAAAA", "onReceive: uriString null", )
                                    continuation.resume(null)
                                }
                            }
                            cursor.close()
                            context.unregisterReceiver(this)
                        }
                    }
                }

                // Register the receiver and resume coroutine on completion
                if(Build.VERSION.SDK_INT>Build.VERSION_CODES.TIRAMISU){
                    context.registerReceiver(
                        onCompleteReceiver,
                        IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                        RECEIVER_EXPORTED
                    )
                }else{
                    context.registerReceiver(
                        onCompleteReceiver,
                        IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
                    )
                }

                // Handle cancellation to unregister the receiver if needed
                continuation.invokeOnCancellation {
                    context.unregisterReceiver(onCompleteReceiver)
                }
            }
        } catch (e: Exception) {
            Log.e("ERROR", "downloadImageAndSaveToStorage:${e.message} ", )
            null
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    suspend fun downloadImageAndUnZipToStorage(
        context: Context,
        imageUrl: String): String? = withContext(Dispatchers.IO) {
        Log.e("AAAAAAAAAAAAAAAA", "downloadImageAndUnZipToStorage: ${imageUrl}", )
        try {
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val uri = Uri.parse(imageUrl.replace(" ", "%20"))

            // Create a request for DownloadManager
            val request = DownloadManager.Request(uri).apply {
                setTitle("Downloading Image")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                setDestinationInExternalFilesDir(
                    context,
                   "",
                    imageUrl.substringAfterLast("/")
                )
            }

            // Enqueue the download request and get the download ID
            val downloadId = downloadManager.enqueue(request)

            // Wait for the download to complete
            return@withContext suspendCancellableCoroutine { continuation ->
                val onCompleteReceiver = object : BroadcastReceiver() {
                    @SuppressLint("Range")
                    override fun onReceive(ctxt: Context?, intent: Intent?) {
                        val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                        if (downloadId == id) {
                            val query = DownloadManager.Query().setFilterById(downloadId)
                            val cursor = downloadManager.query(query)
                            if (cursor.moveToFirst()) {
                                val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                                if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(columnIndex)) {
                                    val uriString = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                                    Log.e("AAAAAAAAAAAA", "onReceive:file zip: ${uriString} ", )
                                    unzip(uriString, context){
                                        continuation.resume(it)
                                    }

                                } else {
                                    Log.e("AAAAAAAAAA", "onReceive: uriString null", )
                                    continuation.resume(null)
                                }
                            }
                            cursor.close()
                            context.unregisterReceiver(this)
                        }
                    }
                }

                // Register the receiver and resume coroutine on completion
                if(Build.VERSION.SDK_INT>Build.VERSION_CODES.TIRAMISU){
                    context.registerReceiver(
                        onCompleteReceiver,
                        IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                        RECEIVER_EXPORTED
                    )
                }else{
                    context.registerReceiver(
                        onCompleteReceiver,
                        IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
                    )
                }


                // Handle cancellation to unregister the receiver if needed
                continuation.invokeOnCancellation {
                    context.unregisterReceiver(onCompleteReceiver)
                }
            }
        } catch (e: Exception) {
            Log.e("ERROR", "downloadImageAndSaveToStorage:${e.message} ", )
            null
        }
    }
    // Unzip it
    fun unzip(zipFileName: String, context: Context, onChangePathFile: (String) -> Unit) {
        // Tạo đường dẫn đến file ZIP trong thư mục riêng của ứng dụng
        val zipFilePath = File(context.getExternalFilesDir(null), zipFileName.substringAfterLast("/"))

        // Kiểm tra file ZIP có tồn tại không
        if (!zipFilePath.exists()) {
            Log.e("UnzipError", "File not found at: ${zipFilePath.absolutePath}")
            return
        }

        // Đường dẫn đến thư mục đích để giải nén
        val destDirectory = File(context.getExternalFilesDir(null), zipFileName.substringAfterLast("/").substringBeforeLast("."))
        if (!destDirectory.exists()) {
            destDirectory.mkdirs()
        }

        try {
            ZipInputStream(FileInputStream(zipFilePath)).use { zipInputStream ->
                var entry: ZipEntry? = zipInputStream.nextEntry
                while (entry != null) {
                    val filePath = File(destDirectory, entry.name)

                    if (entry.isDirectory) {
                        // Tạo thư mục nếu entry là thư mục
                        filePath.mkdirs()
                    } else {
                        // Giải nén các file
                        FileOutputStream(filePath).use { outputStream ->
                            val buffer = ByteArray(1024)
                            var length: Int
                            while (zipInputStream.read(buffer).also { length = it } > 0) {
                                outputStream.write(buffer, 0, length)
                            }
                        }
                    }
                    zipInputStream.closeEntry()
                    entry = zipInputStream.nextEntry
                }
                onChangePathFile.invoke(destDirectory.absolutePath)
                zipFilePath.delete()
            }
        } catch (e: FileNotFoundException) {
            Log.e("UnzipError", "File not found: ${e.message}")
        } catch (e: IOException) {
            Log.e("UnzipError", "IO Exception: ${e.message}")
        }
    }
}