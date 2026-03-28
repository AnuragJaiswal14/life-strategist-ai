package com.aistrategist.app.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistrategist.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun checkUserLoggedIn(): Boolean {
        return authRepository.getCurrentUser() != null
    }

    fun handleGoogleSignIn(idToken: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = authRepository.signInWithGoogleToken(idToken)
            result.onSuccess { user ->
                if (user != null) {
                    onSuccess()
                } else {
                    _error.value = "Authentication failed. User is null."
                }
            }.onFailure { e ->
                _error.value = "Sign in Error: ${e.localizedMessage}"
            }
            
            _isLoading.value = false
        }
    }
}
