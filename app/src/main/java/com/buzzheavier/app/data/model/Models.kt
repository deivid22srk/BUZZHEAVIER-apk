package com.buzzheavier.app.data.model

import com.google.gson.annotations.SerializedName

data class Account(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String?,
    @SerializedName("storage") val storage: Storage?,
    @SerializedName("plan") val plan: String?
)

data class Storage(
    @SerializedName("used") val used: Long,
    @SerializedName("total") val total: Long
)

data class Location(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("country") val country: String?
)

data class Directory(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("parentId") val parentId: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("type") val type: String = "directory"
)

data class FileItem(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("size") val size: Long,
    @SerializedName("url") val url: String?,
    @SerializedName("note") val note: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("parentId") val parentId: String?,
    @SerializedName("type") val type: String = "file"
)

data class DirectoryContent(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("directories") val directories: List<Directory> = emptyList(),
    @SerializedName("files") val files: List<FileItem> = emptyList()
)

data class CreateDirectoryRequest(
    @SerializedName("name") val name: String
)

data class RenameRequest(
    @SerializedName("name") val name: String
)

data class MoveRequest(
    @SerializedName("parentId") val parentId: String
)

data class NoteRequest(
    @SerializedName("note") val note: String
)

sealed class BuzzHeavierItem {
    abstract val id: String
    abstract val name: String
    abstract val createdAt: String?

    data class Dir(
        override val id: String,
        override val name: String,
        override val createdAt: String?,
        val parentId: String?
    ) : BuzzHeavierItem()

    data class File(
        override val id: String,
        override val name: String,
        override val createdAt: String?,
        val size: Long,
        val url: String?,
        val note: String?,
        val parentId: String?
    ) : BuzzHeavierItem()
}
