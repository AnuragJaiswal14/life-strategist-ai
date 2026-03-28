package com.aistrategist.app.presentation.profile

import androidx.lifecycle.ViewModel
import com.aistrategist.app.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _user = MutableStateFlow<FirebaseUser?>(authRepository.getCurrentUser())
    val user = _user.asStateFlow()

    fun logout() {
        authRepository.signOut()
        _user.value = null
    }
}
