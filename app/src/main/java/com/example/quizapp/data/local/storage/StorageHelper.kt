package com.example.quizapp.data.local.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object StorageHelper {
    private const val IMAGE_DIR = "profile_images"

    fun saveImageToInternalStorage(context: Context, bitmap: Bitmap, userId: Int): Boolean {
        val dir = File(context.filesDir, IMAGE_DIR)
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "user_$userId.png")
        return try {
            FileOutputStream(file).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun loadImageFromInternalStorage(context: Context, userId: Int): Bitmap? {
        val file = File(context.filesDir, "$IMAGE_DIR/user_$userId.png")
        return if (file.exists()) {
            try {
                BitmapFactory.decodeFile(file.absolutePath)
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }

    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    fun byteArrayToBitmap(byteArray: ByteArray): Bitmap? {
        return try {
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}