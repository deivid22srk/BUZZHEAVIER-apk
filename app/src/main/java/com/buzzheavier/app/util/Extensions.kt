package com.buzzheavier.app.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

fun Long.toReadableFileSize(): String {
    if (this <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(this.toDouble()) / log10(1024.0)).toInt()
    return DecimalFormat("#,##0.#").format(this / 1024.0.pow(digitGroups.toDouble())) + " " + units[digitGroups]
}

fun String.getFileExtension(): String {
    val lastDotIndex = lastIndexOf('.')
    return if (lastDotIndex > 0 && lastDotIndex < length - 1) {
        substring(lastDotIndex + 1).lowercase()
    } else {
        ""
    }
}

fun String.isImageFile(): Boolean {
    val imageExtensions = setOf("jpg", "jpeg", "png", "gif", "bmp", "webp")
    return getFileExtension() in imageExtensions
}

fun String.isVideoFile(): Boolean {
    val videoExtensions = setOf("mp4", "avi", "mkv", "mov", "wmv", "flv", "webm")
    return getFileExtension() in videoExtensions
}

fun String.isAudioFile(): Boolean {
    val audioExtensions = setOf("mp3", "wav", "flac", "aac", "ogg", "wma", "m4a")
    return getFileExtension() in audioExtensions
}

fun Uri.getFileName(context: Context): String {
    var result: String? = null
    if (scheme == "content") {
        val cursor = context.contentResolver.query(this, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (columnIndex != -1) {
                    result = it.getString(columnIndex)
                }
            }
        }
    }
    if (result == null) {
        result = path
        val cut = result?.lastIndexOf('/')
        if (cut != -1 && cut != null) {
            result = result?.substring(cut + 1)
        }
    }
    return result ?: "file_${System.currentTimeMillis()}"
}
