package com.buzzheavier.app.data.api

import com.buzzheavier.app.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface BuzzHeavierApi {
    
    @GET("api/account")
    suspend fun getAccount(): Response<Account>
    
    @GET("api/locations")
    suspend fun getLocations(): Response<List<Location>>
    
    @GET("api/fs")
    suspend fun getRootDirectory(): Response<DirectoryContent>
    
    @GET("api/fs/{directoryId}")
    suspend fun getDirectory(@Path("directoryId") directoryId: String): Response<DirectoryContent>
    
    @POST("api/fs/{parentDirectoryId}")
    suspend fun createDirectory(
        @Path("parentDirectoryId") parentDirectoryId: String,
        @Body request: CreateDirectoryRequest
    ): Response<Directory>
    
    @PATCH("api/fs/{directoryId}")
    suspend fun renameDirectory(
        @Path("directoryId") directoryId: String,
        @Body request: RenameRequest
    ): Response<Unit>
    
    @PUT("api/fs/{directoryId}")
    suspend fun moveDirectory(
        @Path("directoryId") directoryId: String,
        @Body request: MoveRequest
    ): Response<Unit>
    
    @DELETE("api/fs/{directoryId}")
    suspend fun deleteDirectory(@Path("directoryId") directoryId: String): Response<Unit>
    
    @PATCH("api/fs/{fileId}")
    suspend fun renameFile(
        @Path("fileId") fileId: String,
        @Body request: RenameRequest
    ): Response<Unit>
    
    @PUT("api/fs/{fileId}")
    suspend fun moveFile(
        @Path("fileId") fileId: String,
        @Body request: MoveRequest
    ): Response<Unit>
    
    @PUT("api/fs/{fileId}")
    suspend fun addNoteToFile(
        @Path("fileId") fileId: String,
        @Body request: NoteRequest
    ): Response<Unit>
    
    @DELETE("api/fs/{fileId}")
    suspend fun deleteFile(@Path("fileId") fileId: String): Response<Unit>
}

interface BuzzHeavierUploadApi {
    
    @PUT("{parentId}/{filename}")
    suspend fun uploadFile(
        @Path("parentId") parentId: String,
        @Path("filename") filename: String,
        @Body file: RequestBody
    ): Response<String>
    
    @PUT("{filename}")
    suspend fun uploadFileAnonymous(
        @Path("filename") filename: String,
        @Body file: RequestBody
    ): Response<String>
}
