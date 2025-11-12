package com.buzzheavier.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.buzzheavier.app.data.model.BuzzHeavierItem
import com.buzzheavier.app.data.model.DirectoryContent
import com.buzzheavier.app.data.repository.BuzzHeavierRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class FileManagerViewModel(private val repository: BuzzHeavierRepository) : ViewModel() {
    
    private val _uiState = MutableStateFlow<FileManagerUiState>(FileManagerUiState.Loading)
    val uiState: StateFlow<FileManagerUiState> = _uiState.asStateFlow()
    
    private val _currentDirectory = MutableStateFlow<DirectoryContent?>(null)
    val currentDirectory: StateFlow<DirectoryContent?> = _currentDirectory.asStateFlow()
    
    fun loadDirectory(directoryId: String?) {
        viewModelScope.launch {
            _uiState.value = FileManagerUiState.Loading
            try {
                val response = if (directoryId == null) {
                    repository.getRootDirectory()
                } else {
                    repository.getDirectory(directoryId)
                }
                
                if (response.isSuccessful && response.body() != null) {
                    val content = response.body()!!
                    _currentDirectory.value = content
                    
                    val items = mutableListOf<BuzzHeavierItem>()
                    
                    val directories = content.directories ?: emptyList()
                    directories.forEach { dir ->
                        items.add(
                            BuzzHeavierItem.Dir(
                                id = dir.id,
                                name = dir.name,
                                createdAt = dir.createdAt,
                                parentId = dir.parentId
                            )
                        )
                    }
                    
                    val files = content.files ?: emptyList()
                    files.forEach { file ->
                        items.add(
                            BuzzHeavierItem.File(
                                id = file.id,
                                name = file.name,
                                createdAt = file.createdAt,
                                size = file.size,
                                url = file.url,
                                note = file.note,
                                parentId = file.parentId
                            )
                        )
                    }
                    
                    _uiState.value = FileManagerUiState.Success(items)
                } else {
                    _uiState.value = FileManagerUiState.Error("Erro ao carregar diretório")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = FileManagerUiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }
    
    fun createDirectory(parentDirectoryId: String, name: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.createDirectory(parentDirectoryId, name)
                if (response.isSuccessful) {
                    onSuccess()
                    loadDirectory(parentDirectoryId)
                } else {
                    _uiState.value = FileManagerUiState.Error("Erro ao criar pasta: ${response.code()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = FileManagerUiState.Error(e.message ?: "Erro ao criar pasta")
            }
        }
    }
    
    fun renameItem(itemId: String, newName: String, isDirectory: Boolean, currentDirectoryId: String?) {
        viewModelScope.launch {
            try {
                val response = if (isDirectory) {
                    repository.renameDirectory(itemId, newName)
                } else {
                    repository.renameFile(itemId, newName)
                }
                
                if (response.isSuccessful) {
                    loadDirectory(currentDirectoryId)
                }
            } catch (e: Exception) {
                _uiState.value = FileManagerUiState.Error(e.message ?: "Erro ao renomear")
            }
        }
    }
    
    fun deleteItem(itemId: String, isDirectory: Boolean, currentDirectoryId: String?) {
        viewModelScope.launch {
            try {
                val response = if (isDirectory) {
                    repository.deleteDirectory(itemId)
                } else {
                    repository.deleteFile(itemId)
                }
                
                if (response.isSuccessful) {
                    loadDirectory(currentDirectoryId)
                }
            } catch (e: Exception) {
                _uiState.value = FileManagerUiState.Error(e.message ?: "Erro ao deletar")
            }
        }
    }
    
    fun uploadFile(parentDirectoryId: String, file: File, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.uploadFile(parentDirectoryId, file)
                if (response.isSuccessful) {
                    loadDirectory(parentDirectoryId)
                    onSuccess()
                } else {
                    onError("Erro ao enviar arquivo")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Erro ao enviar arquivo")
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            repository.clearAuthToken()
        }
    }
    
    sealed class FileManagerUiState {
        object Loading : FileManagerUiState()
        data class Success(val items: List<BuzzHeavierItem>) : FileManagerUiState()
        data class Error(val message: String) : FileManagerUiState()
    }
    
    class Factory(private val repository: BuzzHeavierRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FileManagerViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FileManagerViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
