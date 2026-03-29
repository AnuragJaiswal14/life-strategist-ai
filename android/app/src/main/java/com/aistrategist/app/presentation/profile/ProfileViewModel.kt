package com.aistrategist.app.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistrategist.app.domain.repository.AuthRepository
import com.aistrategist.app.domain.usecase.CalendarSyncUseCase
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val calendarSyncUseCase: CalendarSyncUseCase
) : ViewModel() {

    private val _user = MutableStateFlow<FirebaseUser?>(authRepository.getCurrentUser())
    val user = _user.asStateFlow()
    
    private val _syncStatus = MutableStateFlow("Sync Google Calendar")
    val syncStatus = _syncStatus.asStateFlow()

    fun syncCalendar() {
        _syncStatus.value = "Syncing..."
        viewModelScope.launch {
            val events = calendarSyncUseCase.getUpcomingEvents()
            if (events.isNotEmpty()) {
                _syncStatus.value = "Synced ${events.size} upcoming events! Next: ${events.first().title}"
            } else {
                _syncStatus.value = "Synced 0 events. (Calendar Empty)"
            }
        }
    }

    fun logout() {
        authRepository.signOut()
        _user.value = null
    }
}
