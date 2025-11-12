package com.buzzheavier.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.buzzheavier.app.data.model.Account
import com.buzzheavier.app.data.repository.BuzzHeavierRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: BuzzHeavierRepository) : ViewModel() {
    
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    init {
        checkLoginStatus()
    }
    
    private fun checkLoginStatus() {
        viewModelScope.launch {
            val token = repository.getAuthToken()
            if (!token.isNullOrEmpty()) {
                repository.initializeToken()
                _isLoggedIn.value = true
            }
        }
    }
    
    fun login(accountId: String) {
        if (accountId.isBlank()) {
            _uiState.value = LoginUiState.Error("Por favor, insira o Account ID")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                repository.saveAuthToken(accountId)
                repository.initializeToken()
                
                val response = repository.getAccount()
                if (response.isSuccessful && response.body() != null) {
                    val account = response.body()!!
                    _uiState.value = LoginUiState.Success(account)
                    _isLoggedIn.value = true
                } else {
                    _uiState.value = LoginUiState.Error("Account ID inválido")
                    repository.clearAuthToken()
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.message ?: "Erro ao fazer login")
                repository.clearAuthToken()
            }
        }
    }
    
    fun resetState() {
        _uiState.value = LoginUiState.Initial
    }
    
    sealed class LoginUiState {
        object Initial : LoginUiState()
        object Loading : LoginUiState()
        data class Success(val account: Account) : LoginUiState()
        data class Error(val message: String) : LoginUiState()
    }
    
    class Factory(private val repository: BuzzHeavierRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
