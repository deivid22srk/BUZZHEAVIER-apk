package com.buzzheavier.app.util

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
