package com.ggs.parkuzpp.location

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility object for handling map-related file operations.
 */
object MapUtils {

    /**
     * Saves a [Bitmap] to the internal storage "images" directory.
     * * @param context Application context.
     * @param bitmap The bitmap to save (e.g., map snapshot).
     * @return The [Uri] of the saved image file, or null if an error occurred.
     */
    fun saveBitmapToFile(context: Context, bitmap: Bitmap): Uri? {
        val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
            .format(Date())

        val dir = File(context.filesDir, "images").apply { mkdirs() }
        val file = File(dir, "map_snapshot_$timestamp.jpg")

        return try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
