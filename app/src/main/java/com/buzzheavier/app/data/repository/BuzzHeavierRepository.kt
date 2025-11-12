package com.buzzheavier.app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.buzzheavier.app.data.api.BuzzHeavierApi
import com.buzzheavier.app.data.api.BuzzHeavierUploadApi
import com.buzzheavier.app.data.api.RetrofitClient
import com.buzzheavier.app.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class BuzzHeavierRepository(private val context: Context) {
    
    private val api: BuzzHeavierApi = RetrofitClient.api
    private val uploadApi: BuzzHeavierUploadApi = RetrofitClient.uploadApi
    
    private val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
    
    val authToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[AUTH_TOKEN_KEY]
    }
    
    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[AUTH_TOKEN_KEY] = token
        }
        RetrofitClient.setAuthToken(token)
    }
    
    suspend fun clearAuthToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(AUTH_TOKEN_KEY)
        }
        RetrofitClient.setAuthToken(null)
    }
    
    suspend fun getAuthToken(): String? {
        return authToken.first()
    }
    
    suspend fun initializeToken() {
        val token = getAuthToken()
        RetrofitClient.setAuthToken(token)
    }
    
    suspend fun getAccount(): Response<Account> {
        return api.getAccount()
    }
    
    suspend fun getLocations(): Response<List<Location>> {
        return api.getLocations()
    }
    
    suspend fun getRootDirectory(): Response<DirectoryContent> {
        return api.getRootDirectory()
    }
    
    suspend fun getDirectory(directoryId: String): Response<DirectoryContent> {
        return api.getDirectory(directoryId)
    }
    
    suspend fun createDirectory(parentDirectoryId: String, name: String): Response<Directory> {
        return api.createDirectory(parentDirectoryId, CreateDirectoryRequest(name))
    }
    
    suspend fun renameDirectory(directoryId: String, newName: String): Response<Unit> {
        return api.renameDirectory(directoryId, RenameRequest(newName))
    }
    
    suspend fun moveDirectory(directoryId: String, newParentId: String): Response<Unit> {
        return api.moveDirectory(directoryId, MoveRequest(newParentId))
    }
    
    suspend fun deleteDirectory(directoryId: String): Response<Unit> {
        return api.deleteDirectory(directoryId)
    }
    
    suspend fun renameFile(fileId: String, newName: String): Response<Unit> {
        return api.renameFile(fileId, RenameRequest(newName))
    }
    
    suspend fun moveFile(fileId: String, newParentId: String): Response<Unit> {
        return api.moveFile(fileId, MoveRequest(newParentId))
    }
    
    suspend fun addNoteToFile(fileId: String, note: String): Response<Unit> {
        return api.addNoteToFile(fileId, NoteRequest(note))
    }
    
    suspend fun deleteFile(fileId: String): Response<Unit> {
        return api.deleteFile(fileId)
    }
    
    suspend fun uploadFile(parentId: String, file: File): Response<String> {
        val mediaType = when {
            file.name.endsWith(".jpg", true) || file.name.endsWith(".jpeg", true) -> "image/jpeg"
            file.name.endsWith(".png", true) -> "image/png"
            file.name.endsWith(".pdf", true) -> "application/pdf"
            file.name.endsWith(".mp4", true) -> "video/mp4"
            file.name.endsWith(".mp3", true) -> "audio/mpeg"
            else -> "application/octet-stream"
        }
        val requestBody = file.asRequestBody(mediaType.toMediaTypeOrNull())
        return uploadApi.uploadFile(parentId, file.name, requestBody)
    }
}
